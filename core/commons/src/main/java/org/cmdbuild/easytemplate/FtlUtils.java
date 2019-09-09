/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.easytemplate;

import static com.google.common.base.Strings.nullToEmpty;
import java.util.regex.Pattern;

public class FtlUtils {

    public static boolean isFtlTemplate(String template) {
        return Pattern.compile(("(?s).*[<\\[]#?ftl[\\s>\\]]")).matcher(nullToEmpty(template)).find();
    }

    public static String prepareFtlTemplateFixHeaderIfRequired(String template) {
        template = nullToEmpty(template).replaceFirst("(?s)^(.*)([<\\[]#?ftl[^>\\]]+[>\\]])", "$2$1");
        return template;
    }

}
