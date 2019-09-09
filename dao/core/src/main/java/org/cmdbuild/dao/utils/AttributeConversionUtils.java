/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.utils;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Streams.stream;
import static java.lang.String.format;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.apache.commons.lang3.math.NumberUtils.isNumber;
import org.cmdbuild.dao.DaoException;
import org.cmdbuild.dao.beans.IdAndDescription;
import static org.cmdbuild.utils.date.CmDateUtils.toDate;
import static org.cmdbuild.utils.date.CmDateUtils.toDateTime;
import static org.cmdbuild.utils.date.CmDateUtils.toTime;
import org.cmdbuild.dao.beans.IdAndDescriptionImpl;
import org.cmdbuild.dao.beans.LookupValue;
import org.cmdbuild.dao.beans.LookupValueImpl;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.dao.entrytype.attributetype.IpAddressAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.StringAttributeType;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBigDecimalOrNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.toDoubleOrNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.toIntegerOrNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLongOrNull;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmNullableUtils.getClassOfNullable;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import org.cmdbuild.workflow.type.LookupType;
import org.cmdbuild.workflow.type.ReferenceType;
import static org.cmdbuild.workflow.type.utils.WorkflowTypeUtils.emptyToNull;

public class AttributeConversionUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static <T> T rawToSystem(Attribute attribute, @Nullable Object value) {
        return (T) rawToSystem((CardAttributeType) attribute.getType(), value);
    }

    public static <T> T rawToSystem(CardAttributeType<T> attributeType, @Nullable Object value) {
        try {
            return doRawToSystem(attributeType, value);
        } catch (Exception ex) {
            throw runtime(ex, "error converting raw value = '%s' for attribute type = %s", abbreviate(value), attributeType);
        }
    }

    private static <T> T doRawToSystem(CardAttributeType<T> attributeType, @Nullable Object value) {
        if (value == null) {
            return null;
        } else {
            switch (attributeType.getName()) {
                case BOOLEAN:
                    return (T) toBooleanOrNull(value);
                case CHAR:
                    return (T) convertTextValue(value, 1);
                case DATE:
                    return (T) toDate(value);
                case TIME:
                    return (T) toTime(value);
                case TIMESTAMP:
                    return (T) toDateTime(value);
                case DECIMAL:
                    return (T) toBigDecimalOrNull(value);
                case DOUBLE:
                    return (T) toDoubleOrNull(value);
                case REGCLASS:
                    return (T) (value instanceof EntryType ? ((EntryType) value).getName() : unescapeRegclassName(toStringOrNull(value)));
                case REFERENCE:
                case FOREIGNKEY:
                    return (T) convertReference(value);
                case REFERENCEARRAY:
                    return (T) convertReferenceArray(value);
                case INTEGER:
                    return (T) toIntegerOrNull(value);
                case LONG:
                    return (T) toLongOrNull(value);
                case LOOKUP: {
                    if (value instanceof LookupValue) {
                        return (T) value;
                    }
                    String lookupTypeName = ((LookupAttributeType) attributeType).getLookupTypeName();
                    if (value instanceof IdAndDescription) {
                        return (T) new LookupValueImpl(((IdAndDescription) value).getId(), ((IdAndDescription) value).getDescription(), lookupTypeName);
                    }
                    if (value instanceof LookupType) {
                        return (T) new LookupValueImpl(((LookupType) value).getId(), ((LookupType) value).getDescription(), lookupTypeName);
                    }
                    if (value instanceof Number) {
                        if (((Number) value).longValue() == 0) {
                            return null;
                        } else {
                            return (T) new LookupValueImpl( //
                                    Number.class.cast(value).longValue(), StringUtils.EMPTY, ((LookupAttributeType) attributeType).getLookupTypeName());
                        }
                    } else if (value instanceof String) {
                        String s = String.class.cast(value);
                        if (isBlank(s)) {
                            return null;
                        } else {
                            return (T) LookupValueImpl.builder()
                                    .withDescription("")
                                    .withLookupType(((LookupAttributeType) attributeType).getLookupTypeName())
                                    .accept(b -> {
                                        if (isNumber(s)) { //this is not great: a code that looks like a number will be incorrectly processed as id instead of code
                                            b.withId(toLong(s));
                                        } else {
                                            b.withCode(s);
                                        }
                                    }).build();
                        }
                    } else {
                        throw new IllegalArgumentException(format("invalid value = <%s> (%s) for lookup attr type", abbreviate(value), getClassOfNullable(value).getName()));
                    }
                }
                case STRINGARRAY:
                    return (T) convert(value, String[].class);
                case BYTEAARRAY:
                    return (T) convert(value, byte[][].class);
                case STRING:
                    return (T) convertTextValue(value, ((StringAttributeType) attributeType).getLength());
                case TEXT:
                case JSON:
                    return (T) convertTextValue(value, null);
                case BYTEARRAY:
                    return (T) convertByteArrayValue(value);
                case INET:
                    return (T) parseIpAddrAttributeValue((IpAddressAttributeType) attributeType, value);
                case UNKNOWN:
                default:
                    return (T) value;
            }
        }
    }

    private static @Nullable
    IdAndDescription convertReference(@Nullable Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof IdAndDescription) {
            return IdAndDescription.class.cast(value);
        } else if (value instanceof ReferenceType) {
            ReferenceType reference = emptyToNull((ReferenceType) value);
            return reference == null ? null : new IdAndDescriptionImpl(reference.getId(), reference.getDescription());
        } else if (value instanceof Number) {
            if (isNotNullAndGtZero((Number) value)) {
                return new IdAndDescriptionImpl(toLong(value), StringUtils.EMPTY);
            } else {
                return null;
            }
        } else if (value instanceof String) {
            return convertReference(toLongOrNull(value));
        } else if (value instanceof Map) {
            Map map = (Map) value;
            Long id = toLongOrNull(map.get("_id"));
            String description = toStringOrNull(map.get("_description"));
            String code = toStringOrNull(map.get("_code"));
            return isNotNullAndGtZero(id) ? new IdAndDescriptionImpl(id, description, code) : null;
        } else {
            throw new IllegalArgumentException(format("invalid value = '%s' for reference attr type", value));
        }
    }

    private static @Nullable
    IdAndDescription[] convertReferenceArray(@Nullable Object value) {
        if (value == null) {
            return null;
        } else {
            if (value.getClass().isArray()) {
                value = convert(value, List.class);
            }
            checkArgument(value instanceof Iterable, "expect some sort of iterable, found %s (%s)", value, getClassOfNullable(value));
            return ((List<IdAndDescription>) stream((Iterable) value).map(AttributeConversionUtils::convertReference).collect(toList())).toArray(new IdAndDescription[]{});
        }
    }

    private static @Nullable
    String convertTextValue(@Nullable Object value, @Nullable Integer maxLen) {
        if (value == null) {
            return null;
        }
        String stringValue = value.toString();
        if (isBlank(stringValue)) {
            return null;
        }
        if (maxLen != null) {
            checkArgument(stringValue.length() <= maxLen, "string '%s' exceed maximum attr len of %s", abbreviate(stringValue), maxLen);
        }

        return stringValue;
    }

    private static @Nullable
    byte[] convertByteArrayValue(@Nullable Object value) {
        if (value == null) {
            return null;
        }
        checkArgument(value instanceof byte[], "expected argument of type byte[], found instead %s (%s)", value.getClass(), value);
        return (byte[]) value;
    }

    private static final String IPV4SEG = "(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])";
    private static final String IPV4ADDR = "(" + IPV4SEG + "\\.){3,3}" + IPV4SEG;
    private static final String IPV6SEG = "[0-9a-fA-F]{1,4}";
    private static final String IPV6ADDR = "(" //
            + "(" + IPV6SEG + ":){7,7}" + IPV6SEG + "|" //
            + "(" + IPV6SEG + ":){1,7}:|" //
            + "(" + IPV6SEG + ":){1,6}:" + IPV6SEG + "|" //
            + "(" + IPV6SEG + ":){1,5}(:" + IPV6SEG + "){1,2}|" //
            + "(" + IPV6SEG + ":){1,4}(:" + IPV6SEG + "){1,3}|" //
            + "(" + IPV6SEG + ":){1,3}(:" + IPV6SEG + "){1,4}|" //
            + "(" + IPV6SEG + ":){1,2}(:" + IPV6SEG + "){1,5}|" //
            + IPV6SEG + ":((:" + IPV6SEG + "){1,6})|" //
            + ":((:" + IPV6SEG + "){1,7}|:)|" //
            + "fe80:(:" + IPV6SEG + "){0,4}%[0-9a-zA-Z]{1,}|" //
            + "::(ffff(:0{1,4}){0,1}:){0,1}" + IPV4ADDR + "|" //
            + "(" + IPV6SEG + ":){1,4}:" + IPV4ADDR //
            + ")";
    private static final String CLASS_SEPARATOR_REGEX = "/";
    private static final String IPV4_CLASS_REGEX = "(3[0-2]|[1-2][0-9]|[8-9])";
    private static final String IPV4_PATTERN = "^" + IPV4ADDR + "(" + CLASS_SEPARATOR_REGEX + IPV4_CLASS_REGEX + ")*" + "$";
    private static final String IPV6_CLASS_REGEX = "([0-9]|[1-9][0-9]|1[0-1][0-9]|12[0-8])";
    private static final String IPV6_PATTERN = "^" + IPV6ADDR + "(" + CLASS_SEPARATOR_REGEX + IPV6_CLASS_REGEX + ")*" + "$";

    public static @Nullable
    String parseIpAddrAttributeValue(IpAddressAttributeType attributeType, @Nullable Object value) {
        String stringValue = trimToNull(toStringOrNull(value));
        if (isBlank(stringValue)) {
            return null;
        } else {
            switch (attributeType.getType()) {
                case IPV4:
                    checkArgument(stringValue.matches(IPV4_PATTERN), "invalid ipv4 value");
                    break;
                case IPV6:
                    checkArgument(stringValue.matches(IPV6_PATTERN), "invalid ipv6 value");
                    break;
                case ANY:
                    checkArgument(stringValue.matches(IPV4_PATTERN) || stringValue.matches(IPV6_PATTERN), "invalid ipv4/ipv6 value");
                    break;
                default:
                    throw new DaoException("invalid ip type = %s", attributeType.getType());
            }
            return stringValue;
        }
    }

    private static @Nullable
    String unescapeRegclassName(@Nullable String name) {
        if (isBlank(name)) {
            return null;
        } else {
            return name.replaceAll("^\"|\"$", "");
        }
    }

}
