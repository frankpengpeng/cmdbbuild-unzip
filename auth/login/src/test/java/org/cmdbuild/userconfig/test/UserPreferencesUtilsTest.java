/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.userconfig.test;

import java.text.DecimalFormat;
import static org.cmdbuild.userconfig.UserPreferencesUtils.buildDecimalFormat;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class UserPreferencesUtilsTest {

    @Test
    public void testBuildDecimalFormat() {
        DecimalFormat decimalFormat = buildDecimalFormat(".", null);

        assertEquals("0", decimalFormat.format(0));
        assertEquals("0.1", decimalFormat.format(0.1));
        assertEquals("0.01", decimalFormat.format(0.01));
        assertEquals("1234567", decimalFormat.format(1234567));
        assertEquals("1234.56", decimalFormat.format(1234.56));

        decimalFormat = buildDecimalFormat(",", null);

        assertEquals("0", decimalFormat.format(0));
        assertEquals("0,1", decimalFormat.format(0.1));
        assertEquals("0,01", decimalFormat.format(0.01));
        assertEquals("1234567", decimalFormat.format(1234567));
        assertEquals("1234,56", decimalFormat.format(1234.56));

        decimalFormat = buildDecimalFormat(",", ".");

        assertEquals("0", decimalFormat.format(0));
        assertEquals("0,1", decimalFormat.format(0.1));
        assertEquals("0,01", decimalFormat.format(0.01));
        assertEquals("1.234.567", decimalFormat.format(1234567));
        assertEquals("1.234,56", decimalFormat.format(1234.56));
    }
}
