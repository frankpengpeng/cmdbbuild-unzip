/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.service.test;

import java.util.Map;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import static org.cmdbuild.config.utils.LegacyConfigUtils.translateLegacyConfigNames;

public class LegacyConfigNameMapperTest {

    @Test
    public void testLog4jSkip() {
        Map<String, String> map = translateLegacyConfigNames(map("org.cmdbuild.log4j.something", "asd", "org.cmdbuild.log4j.else", "dsa"));
        assertTrue(map.isEmpty());
    }

    @Test
    public void testNoMap() {
        Map<String, String> source = map("org.cmdbuild.something", "asd", "org.cmdbuild.else", "dsa");
        Map<String, String> target = translateLegacyConfigNames(source);
        assertEquals(source, target);
    }

    @Test
    public void testCoreMapping() {
        Map<String, String> source = map("org.cmdbuild.cmdbuild.something", "asd");
        Map<String, String> target = translateLegacyConfigNames(source);
        assertEquals(source.size(), target.size());
        assertEquals(source.get("org.cmdbuild.cmdbuild.something"), target.get("org.cmdbuild.core.something"));
    }

    @Test
    public void testDmsMapping() {
        Map<String, String> source = map("org.cmdbuild.dms.dms.something", "asd");
        Map<String, String> target = translateLegacyConfigNames(source);
        assertEquals(source.size(), target.size());
        assertEquals(source.get("org.cmdbuild.dms.dms.something"), target.get("org.cmdbuild.dms.something"));
    }

}
