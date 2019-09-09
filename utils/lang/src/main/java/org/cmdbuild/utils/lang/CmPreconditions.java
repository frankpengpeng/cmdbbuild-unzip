/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.lang;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Iterables.isEmpty;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.stream;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

public class CmPreconditions {

    public static String checkNotBlank(@Nullable String arg) {
        checkNotNull(trimToNull(arg));
        return arg;
    }

    public static <T extends Number> T checkNotNullAndGtZero(@Nullable T n) {
        checkArgument(isNotNullAndGtZero(n));
        return n;
    }

    public static <T extends Number> T checkNotNullAndGtZero(@Nullable T n, String message, Object... params) {
        checkArgument(isNotNullAndGtZero(n), message, params);
        return n;
    }

    public static <T extends Iterable> T checkNotEmpty(@Nullable T arg) {
        checkNotNull(arg);
        checkArgument(!isEmpty(arg));
        return arg;
    }

    public static <T extends Iterable> T checkNotEmpty(@Nullable T arg, @Nullable String message, @Nullable Object... params) {
        checkNotNull(arg, message, params);
        checkArgument(!isEmpty(arg), message, params);
        return arg;
    }

    public static String checkNotBlank(@Nullable String arg, @Nullable String message) {
        checkNotNull(trimToNull(arg), message);
        return arg;
    }

    public static String checkNotBlank(@Nullable String arg, @Nullable String message, @Nullable Object... params) {
        checkNotNull(trimToNull(arg), message, params);
        return arg;
    }

    public static <T, C extends Iterable<T>> C checkNoneBlank(@Nullable C c, @Nullable String message, @Nullable Object... params) {
        checkNotNull(c, message, params);
        stream(c).forEach(x -> checkNotBlank(toStringOrNull(x), message, params));
        return c;
    }

    public static <T, C extends Iterable<T>> C checkNoneBlank(@Nullable C c) {
        checkNotNull(c);
        stream(c).forEach(x -> checkNotBlank(toStringOrNull(x)));
        return c;
    }

    public static String trimAndCheckNotBlank(@Nullable String arg) {
        return checkNotNull(trimToNull(arg));
    }

    public static String trimAndCheckNotBlank(@Nullable String arg, @Nullable String message) {
        return checkNotNull(trimToNull(arg), message);
    }

    public static String trimAndCheckNotBlank(@Nullable String arg, @Nullable String message, @Nullable Object... params) {
        return checkNotNull(trimToNull(arg), message, params);
    }

    public static String firstNotBlank(@Nullable String one, String two) {
        return checkNotBlank(firstNotBlankOrNull(one, two));
    }

    @Nullable
    public static String firstNotBlankOrNull(@Nullable String one, @Nullable String two) {
        if (isBlank(one)) {
            return blankToNull(two);
        } else {
            return one;
        }
    }

    @Nullable
    public static String firstNotBlankOrNull(String... many) {
        return list(many).stream().filter(StringUtils::isNotBlank).findFirst().orElse(null);
    }

    public static @Nullable
    String firstNotBlankOrEmpty(@Nullable String one, String two) {
        if (isBlank(one)) {
            return nullToEmpty(two);
        } else {
            return one;
        }
    }

    public static @Nullable
    String blankToNull(@Nullable String arg) {
        return isBlank(arg) ? null : arg;
    }

}
