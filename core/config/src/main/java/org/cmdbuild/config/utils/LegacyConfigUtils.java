/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.utils;

import static com.google.common.base.Objects.equal;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.utils.io.CmPropertyUtils.loadProperties;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LegacyConfigUtils {

    private final static String SKIP = "SKIP";
    private final static Map<String, String> LEGACY_CONFIG_PROPERTY_MAPPING = loadProperties(LegacyConfigUtils.class.getResourceAsStream("/org/cmdbuild/config/legacy_config_property_mapping.properties"));

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static Map<String, String> translateLegacyConfigNames(Map<String, String> loadedConfigs) {
        LOGGER.trace("translate legacy config names, input = \n\n{}\n", mapToLoggableStringLazy(loadedConfigs));
        Map<String, String> map = map();
        loadedConfigs.entrySet().forEach((entry) -> {
            String key = entry.getKey();
            key = translateConfigName(key);
            if (!equal(key, SKIP)) {
                map.put(key, entry.getValue());
            }
        });
        LOGGER.trace("translated legacy config names, output = \n\n{}\n", mapToLoggableStringLazy(loadedConfigs));
        return map;
    }

    private static String translateConfigName(String key) {
        for (Entry<String, String> entry : LEGACY_CONFIG_PROPERTY_MAPPING.entrySet()) {
            Matcher matcher = Pattern.compile(entry.getKey()).matcher(key);
            String to = entry.getValue();
            if (matcher.matches()) {
                if (isBlank(to) || to.equalsIgnoreCase(SKIP)) {
                    return SKIP;
                } else {
                    String translated = matcher.replaceFirst(to);
                    translated = translateConfigName(translated);
                    return translated;
                }
            }
        }
        return key;
    }
}
