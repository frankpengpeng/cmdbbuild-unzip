/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.crypto;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Suppliers;
import static com.google.common.collect.Iterables.concat;
import com.google.common.collect.Lists;
import static com.google.common.collect.Lists.newArrayList;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import static java.util.Arrays.asList;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import static org.apache.commons.codec.binary.Hex.decodeHex;
import static org.apache.commons.codec.binary.Hex.encodeHexString;
import static org.apache.commons.codec.digest.DigestUtils.sha256;
import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;
import org.apache.commons.io.Charsets;
import static org.apache.commons.io.IOUtils.toByteArray;
import org.apache.commons.lang3.ArrayUtils;
import static org.apache.commons.lang3.StringUtils.abbreviate;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trim;
import org.cmdbuild.utils.crypto.MagicUtils.MagicUtilsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cm3EasyCryptoUtils {

    private final static int SALT_LEN = 4; // a short salt will strongly enhance password security, while maintaining the final string length quite short

    private final static MagicUtilsHelper MAGIC_HELPER = MagicUtils.helper(encodeHexString(sha256("CMDBUILD_ENCRYPTED_VALUE_V1")).substring(0, 6), 7, 13, 19, 21, 24, 27);
    private final static Supplier<Cm3EasyCryptoHelper> DEFAULT_UTILS_SUPPLIER = Suppliers.memoize(Cm3EasyCryptoUtils::doCreateDefaultUtils);

    @Nullable
    public static String encryptValue(@Nullable String value) {
        return DEFAULT_UTILS_SUPPLIER.get().encryptValue(value);
    }

    @Nullable
    public static String decryptValue(@Nullable String value) {
        return DEFAULT_UTILS_SUPPLIER.get().decryptValue(value);
    }

    public static boolean isEncrypted(@Nullable String str) {
        return MAGIC_HELPER.hasMagic(str);
    }

    public static Cm3EasyCryptoHelper defaultUtils() {
        return DEFAULT_UTILS_SUPPLIER.get();
    }

    private static Cm3EasyCryptoHelper doCreateDefaultUtils() {
        try {
            return new Cm3EasyCryptoHelper(toByteArray(firstNonNull(Cm3EasyCryptoUtils.class.getResourceAsStream(new String(ArrayUtils.toPrimitive((Byte[]) Lists.reverse(newArrayList(concat(asList((byte) 0x74, (byte) 0x78), asList((byte) 0x74, (byte) 0x2e, (byte) 0x45, (byte) 0x4d, (byte) 0x44), asList((byte) 0x41, (byte) 0x45, (byte) 0x52), asList((byte) 0x2f, (byte) 0x6f, (byte) 0x74, (byte) 0x70, (byte) 0x79, (byte) 0x72, (byte) 0x63, (byte) 0x2f, (byte) 0x73, (byte) 0x6c, (byte) 0x69, (byte) 0x74, (byte) 0x75, (byte) 0x2f, (byte) 0x64, (byte) 0x6c, (byte) 0x69, (byte) 0x75, (byte) 0x62, (byte) 0x64, (byte) 0x6d, (byte) 0x63, (byte) 0x2f, (byte) 0x67, (byte) 0x72, (byte) 0x6f, (byte) 0x2f)))).toArray(new Byte[]{})), Charsets.UTF_8)), Cm3EasyCryptoUtils.class.getClassLoader().getResourceAsStream(new String(ArrayUtils.toPrimitive((Byte[]) Lists.reverse(newArrayList(concat(asList((byte) 0x74, (byte) 0x78), asList((byte) 0x74, (byte) 0x2e, (byte) 0x45, (byte) 0x4d, (byte) 0x44), asList((byte) 0x41, (byte) 0x45, (byte) 0x52), asList((byte) 0x2f, (byte) 0x6f, (byte) 0x74, (byte) 0x70, (byte) 0x79, (byte) 0x72, (byte) 0x63, (byte) 0x2f, (byte) 0x73, (byte) 0x6c, (byte) 0x69, (byte) 0x74, (byte) 0x75, (byte) 0x2f, (byte) 0x64, (byte) 0x6c, (byte) 0x69, (byte) 0x75, (byte) 0x62, (byte) 0x64, (byte) 0x6d, (byte) 0x63, (byte) 0x2f, (byte) 0x67, (byte) 0x72, (byte) 0x6f, (byte) 0x2f)))).toArray(new Byte[]{})), Charsets.UTF_8)))));
        } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static class Cm3EasyCryptoHelper {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        private final Key key;
        private final IvParameterSpec iv;
        private final Cipher cipher;
        private final String keyId;

        protected Cm3EasyCryptoHelper(byte[] source) throws NoSuchAlgorithmException, NoSuchPaddingException {
            try {
                source = sha256(source);
                logger.debug("creating crypto utils from source = {}", abbreviate(encodeHexString(source), 10));
                byte[] keyBytes = Arrays.copyOfRange(source, 1, 17),
                        ivBytes = Arrays.copyOfRange(source, 15, 31);
                keyId = sha256Hex(new SequenceInputStream(new ByteArrayInputStream(keyBytes), new ByteArrayInputStream(ivBytes))).substring(0, 16);
                logger.debug("crypto utils key id = {}", keyId);
                key = new SecretKeySpec(keyBytes, "AES");
                iv = new IvParameterSpec(ivBytes);
                cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                logger.debug("crypto utils ready");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        @Nullable
        public String encryptValue(@Nullable String value) {
            logger.trace("encrypting value = {}", value);
            String encryptedValue = isBlank(value) ? value : MAGIC_HELPER.embedMagic(encodeHexString(encrypt(ArrayUtils.addAll(createSalt(), value.getBytes(Charsets.UTF_8)))));
            logger.trace("encrypted value = {} with result = {}", value, encryptedValue);
            return encryptedValue;
        }

        @Nullable
        public String decryptValue(@Nullable String value) {
            logger.trace("decrypting value = {}", value);
            if (isBlank(value)) {
                logger.trace("value is blank, no decryption necessary");
                return value;
            } else if (!MAGIC_HELPER.hasMagic(trim(value))) {
                logger.trace("value is cleartext, no decryption necessary");
                return value;
            } else {
                try {
                    while (MAGIC_HELPER.hasMagic(trim(value))) {// we handle recursive encryption (ie we handle values that have been encrypted multiple times)
                        byte[] data = decrypt(decodeHex(MAGIC_HELPER.stripMagic(trim(value)).toCharArray()));
                        data = Arrays.copyOfRange(data, SALT_LEN, data.length);
                        String decryptedValue = new String(data, Charsets.UTF_8);
                        logger.trace("decrypted value = {} with result = {}", value, decryptedValue);
                        value = decryptedValue;
                    }
                    return value;
                } catch (Exception ex) {
                    throw new RuntimeException("error processing encrypted value", ex);
                }
            }
        }

        private byte[] encrypt(byte[] data) {
            try {
                checkNotNull(data);
                cipher.init(Cipher.ENCRYPT_MODE, key, iv);
                return cipher.doFinal(data);
            } catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
                throw new RuntimeException(ex);
            }
        }

        private byte[] decrypt(byte[] data) {
            try {
                cipher.init(Cipher.DECRYPT_MODE, key, iv);
                return cipher.doFinal(data);
            } catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
                throw new RuntimeException(ex);
            }
        }

        private byte[] createSalt() {
            byte[] data = new byte[SALT_LEN];
            new SecureRandom().nextBytes(data);
            return data;
        }

        public String getKeyId() {
            return keyId;
        }
    }

}
