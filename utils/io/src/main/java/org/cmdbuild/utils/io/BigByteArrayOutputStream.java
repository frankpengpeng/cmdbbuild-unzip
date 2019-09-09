/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

public class BigByteArrayOutputStream extends OutputStream {

    private final BigByteArray byteArray = new BigByteArray();

    @Override
    public void write(int b) throws IOException {
        write(new byte[]{(byte) b});
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        byteArray.append(Arrays.copyOfRange(b, off, off + len));
    }

    public BigByteArray toBigByteArray() {
        return byteArray;
    }

    public byte[] toByteArray() {
        return toBigByteArray().toByteArray();
    }

}
