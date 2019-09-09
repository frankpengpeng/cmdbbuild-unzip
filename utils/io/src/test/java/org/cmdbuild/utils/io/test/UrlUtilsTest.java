/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.io.test;

import static com.google.common.collect.Iterables.getOnlyElement;
import static java.util.Collections.emptyMap;
import java.util.Map;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.url.CmUrlUtils.decodeUrlParams;
import static org.cmdbuild.utils.url.CmUrlUtils.encodeUrlParams;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

public class UrlUtilsTest {

    @Test
    public void testUrlParsing1() {
        Map<String, String> map = decodeUrlParams(null);
        assertNotNull(map);
        assertEquals(0, map.size());
    }

    @Test
    public void testUrlParsing2() {
        Map<String, String> map = decodeUrlParams("");
        assertNotNull(map);
        assertEquals(0, map.size());
    }

    @Test
    public void testUrlParsing3() {
        Map<String, String> map = decodeUrlParams("  ");
        assertNotNull(map);
        assertEquals(0, map.size());
    }

    @Test
    public void testUrlParsing4() {
        Map<String, String> map = decodeUrlParams("test");
        assertNotNull(map);
        assertEquals(1, map.size());
        assertEquals("test", getOnlyElement(map.keySet()));
        assertEquals(null, map.get("test"));
    }

    @Test
    public void testUrlParsing5() {
        Map<String, String> map = decodeUrlParams("test=something&other=else");
        assertNotNull(map);
        assertEquals(2, map.size());
        assertEquals("something", map.get("test"));
        assertEquals("else", map.get("other"));
    }

    @Test
    public void testUrlEncoding1() {
        assertEquals("", encodeUrlParams(emptyMap()));
        assertEquals("one=1&two=true&three=", encodeUrlParams(map("one", 1, "two", true, "three", null)));
    }

    @Test
    public void testUrlEncoding2() {
        String value = "sdf23^%$#@*()+!~`'\"?/",
                enc = encodeUrlParams(map("special", value));
        assertEquals("special=sdf23%5E%25%24%23%40*%28%29%2B%21%7E%60%27%22%3F%2F", enc);
        assertEquals(value, decodeUrlParams(enc).get("special"));
    }
}
