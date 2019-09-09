/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email.test;

import java.util.List;
import org.cmdbuild.email.job.MapperConfigImpl;
import static org.cmdbuild.email.utils.EmailUtils.formatEmailHeaderToken;
import static org.cmdbuild.email.utils.EmailUtils.parseEmailHeaderToken;
import static org.cmdbuild.email.utils.EmailUtils.parseEmailReferencesHeader;
import static org.cmdbuild.email.utils.EmailUtils.processMapperExpr;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class EmailUtilsTest {

    @Test
    public void testEmailTokenParsing1() {
        String token = "<299378267.0.1552566949093@phil>",
                parsed = parseEmailHeaderToken(token);
        assertEquals("299378267.0.1552566949093@phil", parsed);
        assertEquals(token, formatEmailHeaderToken(parsed));
    }

    @Test
    public void testEmailTokenParsing2() {
        String token = "  <299378267.0.1552566949093@phil> ",
                parsed = parseEmailHeaderToken(token);
        assertEquals("299378267.0.1552566949093@phil", parsed);
    }

    @Test
    public void testParseEmailReferencesHeader1() {
        List<String> list = parseEmailReferencesHeader("<299378267.0.1552566949093@phil> <299378267.0.155256694asd3@phil> <299378267.0.fdsd@phil>");
        assertEquals(list("299378267.0.1552566949093@phil", "299378267.0.155256694asd3@phil", "299378267.0.fdsd@phil"), list);
    }

    @Test
    public void testParseEmailReferencesHeader2() {
        List<String> list = parseEmailReferencesHeader("<299378267.0.1552566949093@phil>\n\r<299378267.0.155256694asd3@phil>\n\r<299378267.0.fdsd@phil>");
        assertEquals(list("299378267.0.1552566949093@phil", "299378267.0.155256694asd3@phil", "299378267.0.fdsd@phil"), list);
    }

    @Test
    public void testProcessMapperExpr1() {
        assertEquals("else", processMapperExpr(new MapperConfigImpl(), "dg v45itng4ng5i3gn <key>something</key> <value>else</value> dcawd", "something"));
    }

    @Test
    public void testPrsocessMapperExpr2() {
        assertEquals("", processMapperExpr(new MapperConfigImpl(), "dg v45itng4ng5i3gn <key>something</key> <value>else</value> dcawd", "asd"));
    }

    @Test
    public void testPrsocessMapperExpr3() {
        assertEquals("qwe", processMapperExpr(new MapperConfigImpl(), "dg v45itng4ng5i3gn\n <key>something</key> <value>else</value> <key>asd</key> \n<value>qwe</value> dcawd", "asd"));
    }
}
