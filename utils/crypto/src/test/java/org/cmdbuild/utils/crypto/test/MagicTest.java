/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.crypto.test;

import java.nio.charset.StandardCharsets;
import org.cmdbuild.utils.crypto.MagicUtils;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class MagicTest {

    @Test
    public void magicTestStr() {
        MagicUtils.MagicUtilsHelper helper = MagicUtils.helper("asde", 3, 7, 11, 14);
        String one = "thisissctringone",
                enc = helper.embedMagic(one),
                two = helper.stripMagic(enc);

        assertTrue(helper.hasMagic(enc));
        assertFalse(helper.hasMagic(one));

        assertNotEquals(one, enc);
        assertEquals("thiasisssctdriengone", enc);
        assertEquals(one, two);
    }

    @Test
    public void magicTestBytes() {
        MagicUtils.MagicUtilsHelper helper = MagicUtils.helper(new byte[]{12, 34, -12, -1}, 3, 7, 11, 14);
        byte[] one = "thisissctringone".getBytes(StandardCharsets.US_ASCII),
                enc = helper.embedMagic(one),
                two = helper.stripMagic(enc);

        assertTrue(helper.hasMagic(enc));
        assertFalse(helper.hasMagic(one));

        assertThat(enc, not(equalTo(one)));
        assertThat(enc, equalTo(new byte[]{116, 104, 105, 12, 115, 105, 115, 34, 115, 99, 116, -12, 114, 105, -1, 110, 103, 111, 110, 101}));
        assertThat(two, equalTo(one));
    }

}
