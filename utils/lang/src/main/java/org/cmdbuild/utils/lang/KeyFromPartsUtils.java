/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.lang;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import java.util.List;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class KeyFromPartsUtils {

    public static String key(Iterable parts) {
        return Joiner.on("|").join(parts);
    }

    public static String key(Object... parts) {
        return Joiner.on("|").join(parts);
    }

    public static String key(String... parts) {
        return Joiner.on("|").join(parts);
    }

    public static List<String> unkey(String key) {
        return Splitter.on("|").splitToList(checkNotBlank(key));
    }
}
