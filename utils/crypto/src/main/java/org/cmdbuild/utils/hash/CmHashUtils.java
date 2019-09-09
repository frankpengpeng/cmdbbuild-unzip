/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.hash;

import static java.lang.String.format;
import java.math.BigInteger;
import java.util.Arrays;
import javax.annotation.Nullable;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.Charsets;
import static org.cmdbuild.utils.random.CmRandomUtils.DEFAULT_RANDOM_ID_SIZE;
import static org.cmdbuild.utils.encode.CmEncodeUtils.encodeBytes;
import org.cmdbuild.utils.io.BigByteArray;
import static org.cmdbuild.utils.lang.CmStringUtils.normalize;

public class CmHashUtils {

    public final static int DEFAULT_HASH_SIZE = DEFAULT_RANDOM_ID_SIZE;

    public static String hash(String data) {
        return hash(data, DEFAULT_HASH_SIZE);
    }

    public static String hash(byte[] data) {
        return hash(data, DEFAULT_HASH_SIZE);
    }

    public static String hash(String data, int size) {
        return hash(data.getBytes(Charsets.UTF_8), size);
    }

    public static String hash(byte[] data, int targetSize) {
        byte[] hashBytes = DigestUtils.sha512(data);
        String hashString = encodeBytes(hashBytes);
        if (hashString.length() >= targetSize) {
            return hashString.substring(0, targetSize);
        } else {
            return hashString + hash(hashBytes, targetSize - hashString.length());
        }
    }

    public static byte[] hashToBytes(byte[] data, int targetSize) {
        byte[] hashBytes = DigestUtils.sha512(data);
        if (hashBytes.length >= targetSize) {
            return Arrays.copyOf(hashBytes, targetSize);
        } else {
            return new BigByteArray()
                    .append(hashBytes)
                    .append(hashToBytes(hashBytes, targetSize - hashBytes.length))
                    .toByteArray();
        }
    }

    public static String hashIfLongerThan(String data, int maxSize) {
        return data.length() > maxSize ? hash(data, maxSize) : data;
    }

    public static int toIntHash(String data) {
        byte[] hashBytes = DigestUtils.sha512(data);
        return Math.abs(new BigInteger(hashBytes).intValue());
    }

    @Nullable
    public static String compact(@Nullable String value, int len) {
        if (value == null || value.length() <= len) {
            return value;
        } else {
            value = normalize(value);
            String pref = value.substring(0, len / 3 - 1),
                    suff = value.substring(value.length() - (len / 3 - 1)),
                    hash = hash(value, len / 3 + len % 3);
            return format("%s_%s_%s", pref, hash, suff);
        }
    }

}
