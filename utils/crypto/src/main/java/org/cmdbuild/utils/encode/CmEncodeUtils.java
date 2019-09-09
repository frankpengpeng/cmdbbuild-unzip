/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.encode;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.nullToEmpty;
import java.math.BigInteger;
import java.util.UUID;
import javax.annotation.Nullable;
import org.apache.commons.io.Charsets;
import org.apache.commons.lang3.ArrayUtils;

public class CmEncodeUtils {

    private final static BigInteger RESERVED_VALUES_OFFSET = BigInteger.valueOf(1);//note: this is equal to the number of special values; currently only one: SPECIAL_VALUE_BLANK
    private final static BigInteger SPECIAL_VALUE_BLANK = BigInteger.valueOf(0); // this must be less than RESERVED_VALUES_OFFSET

    private final static int ENCODE_MAX_RADIX = 36;//same as Character.MAX_RADIX

    public static String encodeString(String string) {
        return encodeBytes(string.getBytes(Charsets.UTF_8));
    }

    public static String encodeBytes(byte[] data) {//TODO check handling of blank bytes
        BigInteger num;
        if (data.length == 0) {
            num = SPECIAL_VALUE_BLANK;
        } else {
            BigInteger numOrig = new BigInteger(data);
            num = numOrig.multiply(BigInteger.valueOf(2));
            if (num.compareTo(BigInteger.ZERO) < 0) {
                num = num.abs().add(BigInteger.ONE);
            }
            num = num.add(RESERVED_VALUES_OFFSET);
        }
        String encodedData = num.toString(ENCODE_MAX_RADIX);
        return encodedData;
    }

    public static String decodeString(String data) {
        return new String(decodeBytes(data), Charsets.UTF_8);
    }

    public static byte[] decodeBytes(String data) {//TODO check handling of blank bytes
        BigInteger num = new BigInteger(data, ENCODE_MAX_RADIX);
        if (equal(num, SPECIAL_VALUE_BLANK)) {
            return new byte[]{};
        } else {
            num = num.subtract(RESERVED_VALUES_OFFSET);
            if (num.mod(BigInteger.valueOf(2)).equals(BigInteger.ONE)) {
                num = num.subtract(BigInteger.ONE).negate();
            }
            num = num.divide(BigInteger.valueOf(2));
            byte[] decodedData = num.toByteArray();
            return decodedData;
        }
    }

    public static String encodeUuid(UUID uuid) {
        byte[] b1 = BigInteger.valueOf(uuid.getMostSignificantBits()).toByteArray();//TODO pad array
        checkArgument(b1.length == 8);
//		assertEquals(8, b1.length);
        byte[] b2 = BigInteger.valueOf(uuid.getLeastSignificantBits()).toByteArray();//TODO pad array
        checkArgument(b1.length == 8);
//		assertEquals(8, b2.length);
        byte[] data = new byte[16];
        for (int i = 0; i < 8; i++) {
            data[i] = b1[i];
            data[i + 8] = b2[i];
        }

        return encodeBytes(data);
    }

    public static UUID decodeUuid(String hash) {
//		assertEquals(numOrig, num2);
        byte[] data = decodeBytes(hash);//TODO pad array
        checkArgument(data.length == 16);
//		assertEquals(16, data2.length);
//		Assert.assertArrayEquals(data, data2);

        long l1 = new BigInteger(ArrayUtils.subarray(data, 0, 8)).longValueExact();
        long l2 = new BigInteger(ArrayUtils.subarray(data, 8, 16)).longValueExact();

        UUID uuid = new UUID(l1, l2);
        return uuid;
    }

    @Nullable
    public static String sanitizeStringForId(@Nullable String id) {
        return nullToEmpty(id).replaceAll("[^0-9a-zA-Z]", "");
    }
}
