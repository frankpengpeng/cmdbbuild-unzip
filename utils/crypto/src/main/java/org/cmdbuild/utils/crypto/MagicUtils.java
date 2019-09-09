/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.crypto;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Iterables.getLast;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import static java.util.Arrays.asList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.commons.lang3.ArrayUtils;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.utils.io.CmIoUtils.copy;
import static org.cmdbuild.utils.io.CmIoUtils.readBytes;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;

public class MagicUtils {

    public static MagicUtilsHelper helper(String magic, Integer... magicPos) {
        return helper(magic, asList(magicPos));
    }

    public static MagicUtilsHelper helper(String magic, List<Integer> magicPos) {
        return new MagicUtilsHelper(Splitter.fixedLength(1).splitToList(magic), magicPos);//TODO check charset
    }

    public static MagicUtilsHelper helper(byte[] magic, Integer... magicPos) {
        return helper(magic, asList(magicPos));
    }

    public static MagicUtilsHelper helper(byte[] magic, List<Integer> magicPos) {
        return new MagicUtilsHelper(list(ArrayUtils.toObject(magic)), magicPos);
    }

    public static class MagicUtilsHelper {

        private final List<Character> magicChars;
        private final List<Byte> magicBytes;
        private final List<Integer> magicPos;
        private final int magicSize;

        public MagicUtilsHelper(List<?> magic, List<Integer> magicPos) {
            this.magicChars = magic.stream().map(c -> c instanceof Byte ? Character.valueOf((char) (byte) (Byte) c) : ((String) c).charAt(0)).collect(toImmutableList());
            this.magicBytes = magic.stream().map(c -> c instanceof Byte ? (Byte) c : ((String) c).getBytes(StandardCharsets.US_ASCII)[0]).collect(toImmutableList());
            this.magicPos = ImmutableList.copyOf(magicPos);
            magicSize = magicPos.size();
            checkArgument(magicSize > 0 && magicSize == magicChars.size() && magicSize == magicBytes.size());
            checkArgument(magicPos.stream().allMatch(p -> p != null && p >= 0));
            checkArgument(set(magicPos).size() == magicPos.size());
        }

        public String embedMagic(String str) {
            checkArgument(str.length() > getLast(magicPos));
            StringBuilder sb = new StringBuilder();
            int i, j = 0, k = 0;
            Iterator<Integer> iterator = magicPos.iterator();
            Iterator<Character> chars = magicChars.iterator();
            while (iterator.hasNext()) {
                i = j;
                j = iterator.next() - k;
                k++;
                sb.append(str.substring(i, j));
                sb.append(chars.next());
            }
            sb.append(str.substring(j));
            String res = sb.toString();
            checkArgument(res.length() == str.length() + magicSize);
            checkArgument(hasMagic(res));
            return res;
        }

        public byte[] embedMagic(byte[] data) {
            try {
                checkArgument(data.length > getLast(magicPos));
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ByteArrayInputStream in = new ByteArrayInputStream(data);
                DataOutputStream out = new DataOutputStream(byteArrayOutputStream);
                int cur = -1, next;
                Iterator<Integer> iterator = magicPos.iterator();
                Iterator<Byte> bytes = magicBytes.iterator();
                while (iterator.hasNext()) {
                    next = iterator.next();
                    out.write(readBytes(in, next - cur - 1));
                    out.writeByte(bytes.next());
                    cur = next;
                }
                copy(in, out);
                byte[] res = byteArrayOutputStream.toByteArray();
                checkArgument(res.length == data.length + magicSize);
                checkArgument(hasMagic(res));
                return res;
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        public boolean hasMagic(@Nullable String str) {
            if (isBlank(str) || str.length() <= getLast(magicPos)) {
                return false;
            } else {
                for (int i = 0; i < magicSize; i++) {
                    char c = magicChars.get(i);
                    int pos = magicPos.get(i);
                    if (c != str.charAt(pos)) {
                        return false;
                    }
                }
                return true;
            }
        }

        public boolean hasMagic(@Nullable byte[] data) {
            if (data == null || data.length <= getLast(magicPos)) {
                return false;
            } else {
                for (int i = 0; i < magicSize; i++) {
                    byte c = magicBytes.get(i);
                    int pos = magicPos.get(i);
                    if (c != data[pos]) {
                        return false;
                    }
                }
                return true;
            }
        }

        public String stripMagic(String str) {
            StringBuilder sb = new StringBuilder();
            int i, j = 0;
            Iterator<Integer> iterator = magicPos.iterator();
            while (iterator.hasNext()) {
                i = j;
                j = iterator.next();
                sb.append(str.substring(i, j));
                j++;
            }
            sb.append(str.substring(j));
            String res = sb.toString();
            checkArgument(res.length() == str.length() - magicChars.size());
            return res;
        }

        public byte[] stripMagic(byte[] data) {
            try {
                int cur = -1, next;
                Iterator<Integer> iterator = magicPos.iterator();
                ByteArrayInputStream in = new ByteArrayInputStream(data);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                while (iterator.hasNext()) {
                    next = iterator.next();
                    out.write(readBytes(in, next - cur - 1));
                    checkArgument(in.read() >= 0);
                    cur = next;
                }
                copy(in, out);
                byte[] res = out.toByteArray();
                checkArgument(res.length == data.length - magicChars.size());
                return res;
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

    }
}
