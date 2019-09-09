/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.lang;

import com.google.common.base.Joiner;
import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Objects.equal;
import com.google.common.base.Splitter;
import static com.google.common.base.Strings.nullToEmpty;
import com.google.common.collect.Ordering;
import com.google.common.io.CharStreams;
import java.io.IOException;
import java.io.StringReader;
import static java.lang.Integer.max;
import static java.lang.String.format;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmExceptionUtils.lazyString;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;

public class CmStringUtils {

    private final static int DEFAULT_MAX = 500;

    public static @Nullable
    String toStringOrNull(@Nullable Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Number && ((Number) value).doubleValue() % 1 == 0) {
            return Long.toString(((Number) value).longValue());
        } else {
            return value.toString();
        }
    }

    public static String toStringOrDefault(@Nullable Object value, String def) {
        return firstNonNull(toStringOrNull(value), def);
    }

    public static String toStringNotBlank(Object value) {
        return checkNotBlank(toStringOrNull(value));
    }

    public static String toStringNotBlank(Object value, String message, Object... args) {
        return checkNotBlank(toStringOrNull(value), message, args);
    }

    public static String toStringOrEmpty(@Nullable Object value) {
        return nullToEmpty(toStringOrNull(value));
    }

    public static @Nullable
    String abbreviate(@Nullable Object value) {
        return abbreviate(toStringOrNull(value));
    }

    public static @Nullable
    String abbreviate(@Nullable String value) {
        return abbreviate(value, DEFAULT_MAX);
    }

    public static @Nullable
    String truncate(@Nullable String value, int len) {
        if (value == null || value.length() <= len) {
            return value;
        } else {
            return value.substring(0, len);
        }
    }

    public static @Nullable
    String addLineNumbers(@Nullable String value) {
        if (value == null) {
            return null;
        } else {
            List<String> list = list(Splitter.onPattern("\r?\n\r?").splitToList(value));
            for (int i = 0; i < list.size(); i++) {
                list.set(i, format("%4s: %s", i + 1, list.get(i)));
            }
            return Joiner.on("\n").join(list);
        }
    }

    public static @Nullable
    String normalize(@Nullable String value) {
        if (value == null) {
            return value;
        } else {
            return value.replaceAll("[\n\r]+", " ").replaceAll("[ \t]+", " ");
        }
    }

    public static @Nullable
    String abbreviate(@Nullable String value, int max) {
        if (value == null) {
            return value;
        } else {
            value = normalize(value);
            if (value.length() <= max) {
                return value;
            } else {
                String count = format(" (%s chars)", value.length());
                return StringUtils.abbreviate(value, max - count.length()) + count;
            }
        }
    }

    @Nullable
    public static String multilineWithOffset(@Nullable String value, int max, int offset) {
        if (isBlank(value) || value.length() < max) {
            return value;
        } else {
            List<String> list = list(Splitter.fixedLength(max).splitToList(value));//TODO break on words
            String padding = StringUtils.leftPad("", offset);
            for (int i = 1; i < list.size(); i++) {
                list.set(i, padding + list.get(i));
            }
            return Joiner.on("\n").join(list);
        }
    }

    @Nullable
    public static String multilineWithOffset(@Nullable String value, int offset) {
        List<String> list = readLines(value);
        String padding = StringUtils.leftPad("", offset);
        return list.stream().map(l -> padding + l).collect(joining("\n"));
    }

    public static List<String> readLines(String value) {
        try {
            return CharStreams.readLines(new StringReader(value));
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }

    public static String mapToLoggableString(Properties properties) {
        return mapToLoggableString(map(properties));
    }

    public static String mapToLoggableString(Map<? extends String, ?> map) {
        if (map.isEmpty()) {
            return "<empty map>";
        } else {
            int len = max(20, map.keySet().stream().map(String::length).collect(Collectors.maxBy(Ordering.natural())).get() + 5);
            return Joiner.on("\n").join(map.entrySet().stream()
                    .sorted(Ordering.natural().onResultOf(Map.Entry::getKey))
                    .map((entry) -> format("\t\t%-" + len + "s %50s = %s", entry.getKey(), "(" + classNameOrVoid(entry.getValue()) + ")", abbreviate(entry.getValue())))
                    .collect(toList()));
        }
    }

    public static String mapToLoggableStringInline(Map<? extends String, ?> map) {
        return normalize(mapToLoggableString(map).replaceAll("\n", ", "));
    }

    public static String mapDifferencesToLoggableString(Map<String, ?> one, Map<String, ?> two) {
        Set<String> differences = set(one.keySet()).with(two.keySet()).without(k -> equal(one.get(k), two.get(k)));
        int len = max(20, set(one.keySet()).with(two.keySet()).stream().map(String::length).collect(Collectors.maxBy(Ordering.natural())).get() + 5);
        if (!differences.isEmpty()) {
            return differences.stream().sorted(Ordering.natural()).map(k -> format("\t\t%-" + len + "s %50s = %s -> %50s = %s", k, "(" + classNameOrVoid(one.get(k)) + ")", abbreviate(one.get(k)), "(" + classNameOrVoid(two.get(k)) + ")", abbreviate(two.get(k))))
                    .collect(joining("\n"));
        } else {
            return "<no changes>";
        }
    }

    public static Object mapDifferencesToLoggableStringLazy(Map<String, ?> one, Map<String, ?> two) {
        return lazyString(() -> mapDifferencesToLoggableString(one, two));
    }

    public static Object mapToLoggableStringLazy(Map<? extends String, ?> map) {
        return lazyString(() -> mapToLoggableString(map));
    }

    public static String classNameOrVoid(@Nullable Object value) {
        Class theClass = classOrVoid(value);
        return theClass.getName().startsWith("java.lang") ? theClass.getSimpleName() : theClass.getName();
    }

    public static Class classOrVoid(@Nullable Object value) {
        if (value == null) {
            return Void.class;
        } else {
            return value.getClass();
        }
    }

}
