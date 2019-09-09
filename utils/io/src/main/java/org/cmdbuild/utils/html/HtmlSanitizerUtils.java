/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.html;

import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static com.google.common.collect.Maps.transformValues;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HtmlSanitizerUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final static PolicyFactory POLICY_FACTORY;

    static {
        POLICY_FACTORY = Sanitizers.FORMATTING.and(Sanitizers.STYLES).and(Sanitizers.BLOCKS);
    }

    public static String sanitizeHtmlInString(String str) {
        if (isBlank(str)) {
            return str;
        } else {
            String sanitized = POLICY_FACTORY.sanitize(str);
            if (!equal(sanitized, str)) {
                LOGGER.trace("sanitized html\n=== source html BEGIN ===\n{}\n== source html END; sanitized html BEGIN ===\n{}\n===sanitized html END ===", str, sanitized);
            }
            return sanitized;
        }
    }

    public static <K, V> Map<K, V> sanitizeHtmlInMapValues(Map<K, V> map) {
        return newLinkedHashMap(transformValues(map, (V o) -> o instanceof String ? (V) sanitizeHtmlInString((String) o) : o));
    }

}
