/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.lang;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Splitter;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.MoreCollectors.toOptional;
import com.google.gson.Gson;
import static java.lang.Math.toIntExact;
import static java.lang.String.format;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.time.ZonedDateTime;
import java.util.Arrays;
import static java.util.Arrays.stream;
import java.util.Collection;
import static java.util.Collections.emptyList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.math.NumberUtils.isNumber;
import org.cmdbuild.utils.date.CmDateUtils;
import org.cmdbuild.utils.json.CmJsonUtils;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmNullableUtils.getClassOfNullable;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNullOrBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class CmConvertUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
//	private static final Map<String, Function> CUSTOM_CONVERTERS = map();
//
//	public static <A, B> void registerCustomConverter(Class<A> sourceClass, Class<B> targetClass, Function<A, B> converter) {
//		CUSTOM_CONVERTERS.put(format("%s -> %s", sourceClass.getName(), targetClass.getName()), checkNotNull(converter));
//	}
//
//	private static @Nullable
//	<A, B> Function<A, B> getCustomConverterOrNull(Class<A> sourceClass, Class<B> targetClass) {
//		return CUSTOM_CONVERTERS.get(format("%s -> %s", sourceClass.getName(), targetClass.getName()));
//	}

    @Nullable
    public static Boolean toBooleanOrNull(@Nullable Object value) {
        return convert(value, Boolean.class);
    }

    @Nullable
    public static BigDecimal toBigDecimalOrNull(@Nullable Object value) {
        return convert(value, BigDecimal.class);
    }

    @Nullable
    public static Integer toIntegerOrNull(@Nullable Object value) {
        return convert(value, Integer.class);
    }

    @Nullable
    public static Double toDoubleOrNull(@Nullable Object value) {
        return convert(value, Double.class);
    }

    public static double toDouble(Object value) {
        return checkNotNull(toDoubleOrNull(value));
    }

    @Nullable
    public static Long toLongOrNull(@Nullable Object value) {
        return convert(value, Long.class);
    }

    public static boolean isNotDecimal(Number number) {
        return Math.floor(number.doubleValue()) == number.doubleValue();
    }

    public static long toLong(Object value) {
        return checkNotNull(toLongOrNull(value));
    }

    public static int toInt(Object value) {
        return checkNotNull(toIntegerOrNull(value));
    }

    public static boolean toBoolean(Object value) {
        return convert(value, Boolean.class);
    }

    public static boolean toBooleanOrDefault(@Nullable Object value, boolean defaultValue) {
        if (isNullOrBlank(value)) {
            return defaultValue;
        } else {
            return convert(value, Boolean.class);
        }
    }

    public static @Nullable
    Integer toIntegerOrDefault(@Nullable String value, @Nullable Integer defaultValue) {
        if (isBlank(value)) {
            return defaultValue;
        } else {
            return Integer.valueOf(value);
        }
    }

    public static @Nullable
    Long toLongOrDefault(@Nullable String value, @Nullable Long defaultValue) {
        if (isBlank(value)) {
            return defaultValue;
        } else {
            return Long.valueOf(value);
        }
    }

    public static <T> T convert(@Nullable Object value, Class<T> targetClass, T defaultValue) {
        return firstNonNull(convert(value, targetClass), defaultValue);
    }

    @Nullable
    public static <T> T convert(@Nullable Object value, Class<T> targetClass) {
        try {
            if (targetClass.equals(Iterable.class) || targetClass.equals(Collection.class) || targetClass.equals(List.class)) {
                if (value == null) {
                    return targetClass.cast((T) emptyList());
                } else if (value instanceof Iterable) {
                    return targetClass.cast((T) list((Iterable) value));
                } else if (value.getClass().isArray()) {
                    return targetClass.cast(arrayToList(value));
                } else if (value instanceof java.sql.Array) {
                    try (ResultSet resultSet = ((java.sql.Array) value).getResultSet()) {
                        List list = list();
                        while (resultSet.next()) {
                            list.add(resultSet.getObject(2)); // 1 is index, 2 is value
                        }
                        return (T) list;
                    }
                } else {
                    throw error(value, targetClass);
                }
            } else if (targetClass.equals(Set.class)) {
                return targetClass.cast((Set) set(convert(value, Iterable.class)));
            } else if (value == null) {
                return null;
            } else if (targetClass.isInstance(value)) {
                return targetClass.cast(value);
            } else if (targetClass.isArray()) {
                List list = (List) convert(value, List.class).stream().map((v) -> convert(v, targetClass.getComponentType())).collect(toList());
                return targetClass.cast(list.toArray((Object[]) Array.newInstance(targetClass.getComponentType(), list.size())));
            } else if (isPrimitiveOrWrapper(targetClass) && ToPrimitive.class.isAssignableFrom(value.getClass())) {
                return convert(extractCmPrimitiveIfAvailable(value), targetClass);
            } else {
//				Function customConverter = getCustomConverterOrNull(getClassOfNullable(value), targetClass);//TODO use all hierarchy
//				if (customConverter != null) {
//					return targetClass.cast(customConverter.apply(value));
//				} else {

                T res = convertIfDateTimeOrReturnNull(value, targetClass);
                if (res != null) {
                    return res;
                } else {
                    return convert(value.toString(), targetClass);
                }
//				}
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException(format("error converting value = %s of type = %s to type = %s", abbreviate(value), getClassOfNullable(value).getName(), targetClass), ex);
        }
    }

    @Nullable
    public static Object extractCmPrimitiveIfAvailable(@Nullable Object value) {
        if (value != null && ToPrimitive.class.isAssignableFrom(value.getClass())) {
            value = ToPrimitive.class.cast(value).toPrimitive();
        }
        return value;
    }

    public static boolean isPrimitiveOrWrapper(Object value) {
        return isPrimitiveOrWrapper(value.getClass());
    }

    public static boolean isPrimitiveOrWrapper(Class myClass) {
        return myClass.isPrimitive()
                || myClass == Double.class
                || myClass == Float.class
                || myClass == Long.class
                || myClass == Integer.class
                || myClass == Short.class
                || myClass == Character.class
                || myClass == Byte.class
                || myClass == Boolean.class
                || myClass == String.class;
    }

    public static @Nullable
    <T> T convert(@Nullable String value, Class<T> targetClass) {
        try {
            if (targetClass.equals(String.class)) {
                return targetClass.cast(value);
            } else if (Iterable.class.isAssignableFrom(targetClass)) {
                List<String> list = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(nullToEmpty(value));
                return convert(list, targetClass);
            } else if (StringUtils.isBlank(value)) {
                return null;
            } else if (targetClass.equals(Integer.class) || targetClass.equals(int.class)) {
                return (T) (Integer) toIntExact(toLong(value));
            } else if (targetClass.equals(Long.class) || targetClass.equals(long.class)) {
                return (T) toLong(value);
            } else if (targetClass.equals(BigDecimal.class) || targetClass.equals(Number.class)) {
                return (T) new BigDecimal(value);
            } else if (targetClass.equals(Double.class) || targetClass.equals(double.class)) {
                return (T) Double.valueOf(value);
            } else if (targetClass.equals(Character.class) || targetClass.equals(char.class)) {
                checkArgument(value.length() == 1);
                return (T) (Character) value.charAt(0);
            } else if (targetClass.equals(Boolean.class) || targetClass.equals(boolean.class)) {
                if (isNumber(value)) {
                    return (T) (Boolean) (toInt(value) != 0);
                } else {
                    return (T) Boolean.valueOf(value);
                }
            } else if (targetClass.isEnum()) {
                return (T) parseEnum(value, (Class) targetClass);
            } else if (!isPrimitiveOrWrapper(targetClass) && hasJsonBeanAnnotation(targetClass)) {
                return (T) convertStringToBeanWithModel(value, targetClass);
            } else {
                T res = convertIfDateTimeOrReturnNull(value, targetClass);
                if (res != null) {
                    return res;
                } else {
                    throw new IllegalArgumentException("unsupported conversion");
                }
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException(format("error converting value = %s to type = %s", abbreviate(value), targetClass), ex);
        }
    }

    public static @Nullable
    <T extends Enum<T>> T parseEnumOrNull(@Nullable String value, Class<T> enumClass) {
        return isNullOrBlank(value) ? null : parseEnum(value, enumClass);
    }

    @Deprecated
    public static <T extends Enum<T>> T parseEnumOrDefault(@Nullable String value, Class<T> enumClass, T defaultValue) {
        return parseEnumOrDefault(value, defaultValue);
    }

    public static <T extends Enum<T>> T parseEnumOrDefault(@Nullable String value, T defaultValue) {
        return isNullOrBlank(value) ? defaultValue : (T) parseEnum(value, defaultValue.getClass());
    }

    public static <T extends Enum<T>> T parseEnum(String value, Class<T> enumClass) {
        checkNotBlank(value);
        T enumValue = getEnumCaseInsensitiveOrNull(value, enumClass);
        if (enumValue == null) {
            String sample = EnumUtils.getEnumMap(enumClass).keySet().iterator().next();
            Matcher matcher = Pattern.compile("^([A-Z]+_).*").matcher(sample);
            if (matcher.matches()) {
                String prefix = matcher.group(1);
                enumValue = getEnumCaseInsensitiveOrNull(prefix + value, enumClass);
            }
        }
        return checkNotNull(enumValue, "enum value not found for name = %s, valid values = %s", value, EnumUtils.getEnumList(enumClass));
    }

    @Nullable
    public static <T extends Enum<T>> String serializeEnum(@Nullable T value) {
        return value == null ? null : value.name().toLowerCase().replaceFirst("^[^_]+_", "");
    }

    private static @Nullable
    <T extends Enum<T>> T getEnumCaseInsensitiveOrNull(String value, Class<T> enumClass) {
        T enumValue = EnumUtils.getEnum(enumClass, value);
        if (enumValue == null) {
            enumValue = ((Map<String, T>) EnumUtils.getEnumMap(enumClass)).entrySet().stream().filter((e) -> e.getKey().equalsIgnoreCase(value)).collect(toOptional()).map(Entry::getValue).orElse(null);
        }
        return enumValue;
    }

    public static @Nullable
    <T> T convert(@Nullable Object value, Type type) {
        if (value == null) {
            return null;
        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Class outer = (Class) parameterizedType.getRawType();
            if (List.class.isAssignableFrom(outer) || Collection.class.isAssignableFrom(outer) || Iterable.class.isAssignableFrom(outer)) {
                Class inner = (Class) parameterizedType.getActualTypeArguments()[0];
                return (T) convert(value, List.class).stream().map((e) -> convert(e, inner)).collect(toList());
            } else if (Map.class.isAssignableFrom(outer)) {
                return (T) convertToMap(value, type);
            } else {
                throw new IllegalArgumentException("unsupported conversion of parametrized type = " + type);
            }

        } else {
            return (T) convert(value, (Class) type);
        }
    }

    private static @Nullable
    Map convertToMap(@Nullable Object value, Type type) {
        try {
            checkArgument(Map.class.isAssignableFrom((Class<?>) (((ParameterizedType) type).getRawType())));
            if (value == null) {
                return null;
            } else if (value instanceof Map) {
                return (Map) value;//TODO convert keys
            } else if (value instanceof String && isJsonMap((String) value)) {
                return new Gson().fromJson((String) value, type);
            } else {
                throw new IllegalArgumentException("unsupported conversion");
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException(format("error converting value = %s to type = %s", abbreviate(value), type), ex);
        }
    }

    private static boolean isJsonMap(String value) {
        return isNotBlank(value) && value.startsWith("{") && value.endsWith("}");
    }

    @Nullable
    private static <T> T convertIfDateTimeOrReturnNull(Object value, Class<T> targetClass) {
        if (Date.class.equals(targetClass)) {
            return targetClass.cast(CmDateUtils.toJavaDate(value));
        } else if (ZonedDateTime.class.equals(targetClass)) {
            return targetClass.cast(CmDateUtils.toDateTime(value));
        } else if (java.sql.Timestamp.class.equals(targetClass)) {
            return targetClass.cast(CmDateUtils.toSqlTimestamp(value));
        } else if (java.sql.Time.class.equals(targetClass)) {
            return targetClass.cast(CmDateUtils.toSqlTime(value));
        } else if (java.sql.Date.class.equals(targetClass)) {
            return targetClass.cast(CmDateUtils.toSqlDate(value));
        } else {
            return null;
        }
    }

    private static Long toLong(String value) {
        BigDecimal num = new BigDecimal(value);
        Long res;
        try {
            res = num.longValueExact();
        } catch (ArithmeticException ex) {
            try {
                res = num.longValue();
                LOGGER.warn("error converting value = {} to int/long, unable to execute exact conversion (had to truncate or something)", value);
                LOGGER.debug("error converting numeric value", ex);
            } catch (Exception exx) {
                throw ex;
            }
        }
        return res;
    }

    private static RuntimeException error(Object value, Class targetClass) {
        return new IllegalArgumentException(format("unsupported conversion of value %s to type = %s", abbreviate(value), targetClass));
    }

    private static <T> List<T> arrayToList(Object value) {
        List<T> list = list();
        for (int i = 0; i < Array.getLength(value); i++) {
            list.add((T) Array.get(value, i));
        }
        return list;
    }

    public static <T> T defaultValue(Class<T> targetClass) {
        if (targetClass.equals(String.class)) {
            return targetClass.cast("");
        } else if (targetClass.equals(Integer.class) || targetClass.equals(int.class)) {
            return targetClass.cast(0);
        } else if (targetClass.equals(Long.class) || targetClass.equals(long.class)) {
            return targetClass.cast(0l);
        } else if (targetClass.equals(BigDecimal.class) || targetClass.equals(Number.class)) {
            return targetClass.cast(BigDecimal.ZERO);
        } else if (targetClass.equals(Double.class) || targetClass.equals(double.class)) {
            return targetClass.cast(0d);
        } else if (targetClass.equals(Float.class) || targetClass.equals(float.class)) {
            return targetClass.cast(0f);
        } else if (targetClass.equals(Boolean.class) || targetClass.equals(boolean.class)) {
            return targetClass.cast(false);
        } else {
//			return newInstance(targetClass);
            return null;
        }
    }

//	private static <T> T newInstance(Class<T> targetClass) {
//		try {
//			return targetClass.newInstance();
//		} catch (InstantiationException | IllegalAccessException ex) {
//			throw new RuntimeException("error creating new instance of type = " + targetClass.getName(), ex);
//		}
//	}
    public static <T> boolean hasJsonBeanAnnotation(Class<T> targetClass) {
        return targetClass.getAnnotation(JsonBean.class) != null
                || stream(targetClass.getConstructors()).map(Constructor::getParameterAnnotations).flatMap(Arrays::stream).flatMap(Arrays::stream).anyMatch(JsonProperty.class::isInstance);
    }

    public static boolean hasJsonBeanAnnotation(Method method) {
        return method.getAnnotation(JsonBean.class) != null;
    }

    private static <T> T convertStringToBeanWithModel(String value, Class<T> targetInterfaceOrMode) {
        JsonBean annotation = targetInterfaceOrMode.getAnnotation(JsonBean.class);
        Class targetClass = annotation == null || annotation.value() == null ? targetInterfaceOrMode : annotation.value();
        checkArgument(targetInterfaceOrMode.isAssignableFrom(targetClass));
        return (T) targetInterfaceOrMode.cast(CmJsonUtils.fromJson(value, targetClass));
    }
}
