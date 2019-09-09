/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.encode;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.nullToEmpty;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;
import javax.annotation.Nullable;
import static org.apache.commons.codec.binary.Base64.decodeBase64;
import static org.apache.commons.codec.binary.Base64.isBase64;
import static org.apache.commons.io.IOUtils.toByteArray;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.utils.encode.CmEncodeUtils.decodeBytes;
import static org.cmdbuild.utils.encode.CmEncodeUtils.encodeBytes;
import static org.cmdbuild.utils.hash.CmHashUtils.hashToBytes;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmPackUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final static String PACK_MAGIC = "pack";
    private final static int PACK_HASH_SIZE = 4,
            PACK_VERSION_HASHED = 1,
            PACK_VERSION_HASHED_DEFLATED = 2;

    public static String packString(String value) {
        return packBytes(nullToEmpty(value).getBytes(StandardCharsets.UTF_8));
    }

    public static String packBytes(byte[] data) {
        LOGGER.trace("pack {} bytes of data", data.length);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(byteArrayOutputStream)) {
            int version;
            byte[] hash = hashToBytes(data, PACK_HASH_SIZE),
                    deflated = deflate(data);
            LOGGER.trace("hash = {}", hash);
            if (deflated.length < data.length) {
                LOGGER.trace("using deflate (size reduced to {})", deflated.length);
                version = PACK_VERSION_HASHED_DEFLATED;
                data = deflated;
            } else {
                LOGGER.trace("skip deflate");
                version = PACK_VERSION_HASHED;
            }
            LOGGER.trace("version = {}", version);
            out.writeByte(version);
            out.write(hash);
            out.writeInt(data.length);
            out.write(data);
        } catch (IOException ex) {
            throw runtime(ex);
        }
        String pack = PACK_MAGIC + encodeBytes(byteArrayOutputStream.toByteArray());
        LOGGER.trace("pack string size = {}", pack.length());
        return pack;
    }

    public static String unpackString(String packed) {
        return new String(unpackBytes(packed), StandardCharsets.UTF_8);
    }

    public static byte[] unpackBytes(String packed) {
        try {
            LOGGER.trace("unpack value = {}", abbreviate(packed));
            checkArgument(isPacked(packed), "invalid pack format");
            byte[] packedData = decodeBytes(packed.substring(PACK_MAGIC.length()));
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(packedData));
            int version = in.readByte();
            LOGGER.trace("version = {}", version);
            boolean inflate;
            switch (version) {
                case PACK_VERSION_HASHED:
                    inflate = false;
                    break;
                case PACK_VERSION_HASHED_DEFLATED:
                    inflate = true;
                    break;
                default:
                    throw new UnsupportedOperationException("unsupported pack version = " + version);
            }
            byte[] storedHash = new byte[PACK_HASH_SIZE], data;
            checkArgument(in.read(storedHash) == PACK_HASH_SIZE, "unable to read hash bytes from packed data");
            LOGGER.trace("stored hash = {}", storedHash);
            int size = in.readInt();
            checkArgument(size >= 0, "invalid size value = %s", size);
            LOGGER.trace("stored size = {}", size);
            data = toByteArray(in);
            checkArgument(data.length == size, "invalid data size (expected %s but got %s )", size, data.length);
            if (inflate) {
                data = inflate(data);
                LOGGER.trace("using inflate, size grow to = {}", data.length);
            }
            byte[] newHash = hashToBytes(data, PACK_HASH_SIZE);
            checkArgument(Arrays.equals(newHash, storedHash), "invalid data hash (probable corruption of data)");
            LOGGER.trace("hash match ok");
            return data;
        } catch (Exception ex) {
            throw runtime(ex, "invalid pack format for value = " + abbreviate(packed));
        }
    }

    public static String unpackStringIfPacked(@Nullable String packed) {
        if (isBlank(packed)) {
            LOGGER.debug("blank data found, returning plain data");
            return nullToEmpty(packed);
        } else if (isPacked(packed)) {
            LOGGER.debug("detected packed value, unpacking");
            return unpackString(packed);
        } else {
            LOGGER.debug("no encoding detected, returning plain data");
            return packed;
        }
    }

    public static byte[] unpackBytesIfPacked(@Nullable String packed) {
        if (isBlank(packed)) {
            LOGGER.debug("blank data found, returning plain data");
            return nullToEmpty(packed).getBytes(StandardCharsets.UTF_8);
        } else if (isPacked(packed)) {
            LOGGER.debug("detected packed value, unpacking");
            return unpackBytes(packed);
        } else {
            LOGGER.debug("no encoding detected, returning plain data");
            return packed.getBytes(StandardCharsets.UTF_8);
        }
    }

    public static String unpackStringIfPackedOrBase64(@Nullable String packed) {
        if (isBlank(packed)) {
            LOGGER.debug("blank data found, returning plain data");
            return nullToEmpty(packed);
        } else if (isPacked(packed)) {
            LOGGER.debug("detected packed value, unpacking");
            return unpackString(packed);
        } else if (isBase64(packed)) {
            LOGGER.debug("detected base64 value, decoding");
            return new String(decodeBase64(packed), StandardCharsets.UTF_8);
        } else {
            LOGGER.debug("no encoding detected, returning plain data");
            return packed;
        }
    }

    public static byte[] unpackBytesIfPackedOrBase64(@Nullable String packed) {
        if (isBlank(packed)) {
            LOGGER.debug("blank data found, returning plain data");
            return nullToEmpty(packed).getBytes(StandardCharsets.UTF_8);
        } else if (isPacked(packed)) {
            LOGGER.debug("detected packed value, unpacking");
            return unpackBytes(packed);
        } else if (isBase64(packed)) {
            LOGGER.debug("detected base64 value, decoding");
            return decodeBase64(packed);
        } else {
            LOGGER.debug("no encoding detected, returning plain data");
            return packed.getBytes(StandardCharsets.UTF_8);
        }
    }

    public static boolean isPacked(String value) {
        if (isBlank(value)) {
            return false;
        } else {
            return value.toLowerCase().matches(PACK_MAGIC + "[a-z0-9]+");
        }
    }

    private static byte[] deflate(byte[] data) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (OutputStream out = new DeflaterOutputStream(byteArrayOutputStream)) {
            out.write(data);
        } catch (IOException ex) {
            throw runtime(ex);
        }
        return byteArrayOutputStream.toByteArray();
    }

    private static byte[] inflate(byte[] data) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (OutputStream out = new InflaterOutputStream(byteArrayOutputStream)) {
            out.write(data);
        } catch (IOException ex) {
            throw runtime(ex);
        }
        return byteArrayOutputStream.toByteArray();
    }

}
