/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.io.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import org.cmdbuild.utils.io.BigByteArray;
import org.cmdbuild.utils.io.BigByteArrayInputStream;
import org.cmdbuild.utils.io.BigByteArrayOutputStream;
import static org.cmdbuild.utils.io.CmIoUtils.tempFile;
import static org.cmdbuild.utils.io.CmIoUtils.toBigByteArray;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Ignore;
import org.junit.Test;

public class CmIoTest {

    @Test(expected = IllegalArgumentException.class)
    @Ignore("slow test")
    public void testByteArrayOfThreeGigsThrowError() throws IOException {
        File file = tempFile();
        try {
            try (FileOutputStream out = new FileOutputStream(file)) {
                for (int i = 0; i < 3000; i++) {
                    byte[] data = new byte[1024 * 1024];//1 M
                    new Random().nextBytes(data);
                    out.write(data);
                }
            }
            assertEquals(3000 * 1024 * 1024l, file.length());
            byte[] allData = toByteArray(file);
        } finally {
            deleteQuietly(file);
        }
    }

    @Test
    @Ignore("slow test")
    public void testBigByteArrayOfThreeGigsFromFile() throws IOException {
        File file = tempFile();
        try {
            try (FileOutputStream out = new FileOutputStream(file)) {
                for (int i = 0; i < 3000; i++) {
                    byte[] data = new byte[1024 * 1024];
                    new Random().nextBytes(data);
                    out.write(data);
                }
            }
            assertEquals(3000 * 1024 * 1024l, file.length());
            BigByteArray bigByteArray = toBigByteArray(file);
            assertEquals(3000 * 1024 * 1024l, bigByteArray.length());
        } finally {
            deleteQuietly(file);
        }
    }

    @Test
    @Ignore("slow test")
    public void testBigByteArrayOfThreeGigs() throws IOException {
        BigByteArrayOutputStream out = new BigByteArrayOutputStream();
        for (int i = 0; i < 3000; i++) {
            byte[] data = new byte[1024 * 1024];
            new Random().nextBytes(data);
            out.write(data);
        }
        assertEquals(3000 * 1024 * 1024l, out.toBigByteArray().length());
    }

    @Test
    public void testBigByteArray() throws IOException {
        BigByteArrayOutputStream bbaos = new BigByteArrayOutputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int i = 0; i < 100; i++) {
            byte[] data = new byte[new Random().nextInt(100) + 100];
            new Random().nextBytes(data);
            bbaos.write(data);
            baos.write(data);
        }

        byte[] data = baos.toByteArray(),
                bdata = bbaos.toByteArray();

        assertEquals(data.length, bdata.length);
        assertArrayEquals(data, bdata);

        BigByteArrayInputStream bbais = new BigByteArrayInputStream(bbaos.toBigByteArray());
        byte[] dataFromStream = toByteArray(bbais);

        assertEquals(data.length, dataFromStream.length);
        assertArrayEquals(data, dataFromStream);
    }
}
