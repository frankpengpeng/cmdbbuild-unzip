/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.io;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.InputStream;
import static java.lang.Math.toIntExact;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public final class BigByteArray {

    private final List<byte[]> bytes = list();
    private long length = 0;

    public static BigByteArray copyOf(BigByteArray source) {
        return new BigByteArray().append(source);
    }

    public BigByteArray append(byte[] data) {
        checkNotNull(data);
        if (data.length > 0) {
            data = Arrays.copyOf(data, data.length);
            bytes.add(data);
            length += data.length;
        }
        return this;
    }

    public BigByteArray append(BigByteArray data) {
        data.forEach(this::append);
        return this;
    }

    public void forEach(Consumer<byte[]> action) {
        bytes.forEach(action);
    }

    public byte[] toByteArray() {
        checkArgument(length <= Integer.MAX_VALUE, "unable to convert this BigByteArray to byte[], length = %s (more than maxint)", length);
        ByteBuffer buffer = ByteBuffer.allocate(toIntExact(length));
        forEach(buffer::put);
        return buffer.array();
    }

    public Stream<byte[]> stream() {
        return bytes.stream();
    }

    public long length() {
        return length;
    }

    public void clear() {
        length = 0;
        bytes.clear();
    }

    public InputStream toInputStream() {
        return new BigByteArrayInputStream(this);
    }

}
