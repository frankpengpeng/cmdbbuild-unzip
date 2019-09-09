package org.cmdbuild.dao.postgres.utils;

import com.google.common.base.Joiner;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.not;
import static com.google.common.base.Predicates.notNull;
import com.google.common.base.Splitter;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.ImmutableList.copyOf;
import com.google.common.collect.ImmutableSet;
import static com.google.common.collect.Iterables.getOnlyElement;
import static java.lang.String.format;
import java.sql.SQLException;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.common.Constants.BASE_DOMAIN_NAME;
import org.cmdbuild.dao.DaoException;
import static org.cmdbuild.dao.beans.AttributeMetadataImpl.emptyAttributeMetadata;
import org.cmdbuild.dao.beans.IdAndDescription;
import org.cmdbuild.dao.beans.IdAndDescriptionImpl;
import org.cmdbuild.dao.beans.LookupValue;
import org.cmdbuild.dao.beans.LookupValueImpl;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeMetadata;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.dao.postgres.Const.DOMAIN_PREFIX;

import org.cmdbuild.dao.postgres.Const.SystemAttributes;
import org.cmdbuild.dao.query.clause.alias.Alias;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.dao.entrytype.EntryType.EntryTypeType;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.FOREIGNKEY;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.LOOKUP;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.REFERENCE;
import org.cmdbuild.dao.entrytype.attributetype.BooleanAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ByteArrayAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ByteaArrayAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.CharAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DateAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DateTimeAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DecimalAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DoubleAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ForeignKeyAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.IntegerAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.IpAddressAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.IpType;
import org.cmdbuild.dao.entrytype.attributetype.JsonAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LongAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.RegclassAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.StringArrayAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.StringAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.TextAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.TimeAttributeType;
import org.cmdbuild.dao.function.StoredFunction;
import static org.cmdbuild.dao.postgres.Const.BASE_DOMAIN_TABLE_NAME;
import org.cmdbuild.dao.postgres.PostgreSQLArray;
import org.cmdbuild.dao.postgres.SqlType;
import org.cmdbuild.dao.postgres.SqlTypeName;
import static org.cmdbuild.dao.utils.AttributeConversionUtils.rawToSystem;
import org.cmdbuild.utils.date.CmDateUtils;
import static org.cmdbuild.utils.date.CmDateUtils.isDate;
import static org.cmdbuild.utils.date.CmDateUtils.isDateTime;
import static org.cmdbuild.utils.date.CmDateUtils.isTime;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDate;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoTimeWithNanos;
import static org.cmdbuild.utils.lang.CmCollectionUtils.isNullOrEmpty;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.stream;
import org.cmdbuild.utils.lang.CmConvertUtils;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNullOrBlank;
import static org.cmdbuild.utils.lang.CmNullableUtils.ltEqZeroToNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.utils.lang.CmStringUtils;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.cmdbuild.utils.lang.ToPrimitive;
import org.postgresql.jdbc.PgArray;
import org.postgresql.util.PGobject;

public class SqlQueryUtils {

    public static final String Q3_MASTER = "Q3_MASTER";

    public static final Set<String> Q3_MARKERS = ImmutableSet.of(Q3_MASTER);

    public static boolean exprContainsQ3Markers(@Nullable String expr) {
        if (isBlank(expr)) {
            return false;
        } else {
            return Q3_MARKERS.stream().anyMatch(m -> expr.contains(m));
        }
    }

    public static String entryTypeToSqlExpr(EntryType entryType) {
        switch (entryType.getEtType()) {
            case ET_CLASS:
                return quoteSqlIdentifier(entryType.getName());
            case ET_DOMAIN:
                return quoteSqlIdentifier(domainNameToSqlTable(entryType.getName()));
            case ET_FUNCTION:
                checkArgument(((StoredFunction) entryType).getInputParameters().isEmpty(), "cannot invoke this function like a view, it has parameters; function = %s", entryType);
                return format("%s()", quoteSqlIdentifier(entryType.getName()));
            case ET_OTHER:
            default:
                throw unsupported("unsupported entry type = %s", entryType);
        }
    }

    public static String entryTypeToSqlExpr(String name, EntryTypeType type) {
        switch (type) {
            case ET_CLASS:
                return quoteSqlIdentifier(name);
            case ET_DOMAIN:
                return quoteSqlIdentifier(domainNameToSqlTable(name));
            default:
                throw unsupported("unsupported entry type = %s", type);
        }
    }

    public static String quoteSqlIdentifier(String name) {
        if (name.matches("[a-z_][a-z0-9_]*") || name.matches("^\".*\"$")) {
            return name;
        } else {
            return format("\"%s\"", name.replace("\"", "\"\""));
        }
    }

    public static String functionCallSqlExpr(String functionName, Object... args) {
        return format("%s(%s)", quoteSqlIdentifier(functionName), list(args).stream().map(SqlQueryUtils::systemToSqlExpr).collect(joining(",")));
    }

    public static String sqlTableToDomainName(String sqlDomainTable) {
        return checkNotBlank(sqlDomainTable).replaceAll("\"", "").replaceFirst("^" + Pattern.quote(DOMAIN_PREFIX), "");
    }

    public static String sqlTableToClassName(String sqlDomainTable) {
        return checkNotBlank(sqlDomainTable).replaceAll("\"", "");
    }

    public static String sqlTableToEntryTypeName(String tableName, EntryTypeType type) {
        switch (type) {
            case ET_CLASS:
                return sqlTableToClassName(tableName);
            case ET_DOMAIN:
                return sqlTableToDomainName(tableName);
            default:
                throw unsupported("unsupported entry type = %s", type);
        }
    }

    public static String domainNameToSqlTable(String domainName) {
        if (equal(domainName, BASE_DOMAIN_NAME)) {
            return BASE_DOMAIN_TABLE_NAME;
        } else {
            return DOMAIN_PREFIX + checkNotBlank(domainName);
        }
    }

    public static String buildReferenceDescExpr(Classe targetClass, String classExpr, String attrExpr) {
//		return format("_cm3_card_description_get('%s'::regclass,%s)", entryTypeToQuotedSql(targetClass), attrExpr); ignore-tenant function; too slow, replaced from simple subquery below
        return format("(SELECT \"Description\" FROM %s WHERE \"Id\" = %s.%s%s)", entryTypeToSqlExpr(targetClass), classExpr, attrExpr, targetClass.isSimpleClass() ? "" : " AND \"Status\" = 'A'");
    }

    public static String buildReferenceCodeExpr(Classe targetClass, String classExpr, String attrExpr) {
        return format("(SELECT \"Code\" FROM %s WHERE \"Id\" = %s.%s%s)", entryTypeToSqlExpr(targetClass), classExpr, attrExpr, targetClass.isSimpleClass() ? "" : " AND \"Status\" = 'A'");
    }

    public static String buildReferenceExistsExpr(Classe targetClass, String classExpr, String attrExpr) {
        return format("(EXISTS (SELECT 1 FROM %s WHERE \"Id\" = %s.%s%s) )", entryTypeToSqlExpr(targetClass), classExpr, attrExpr, targetClass.isSimpleClass() ? "" : " AND \"Status\" = 'A'");
    }

    public static String buildLookupDescExpr(String classExpr, String attrExpr) {
        return format("(SELECT \"Description\" FROM \"LookUp\" WHERE \"Id\" = %s.%s AND \"Status\" = 'A')", classExpr, attrExpr);
    }

    public static String buildLookupCodeExpr(String classExpr, String attrExpr) {
        return format("(SELECT \"Code\" FROM \"LookUp\" WHERE \"Id\" = %s.%s AND \"Status\" = 'A')", classExpr, attrExpr);
    }

    public static String buildDescAttrName(String attr) {
        return format("_%s_description", attr);
    }

    public static String buildCodeAttrName(String attr) {
        return format("_%s_code", attr);
    }

    public static <T> Map<String, Object> parseEntryTypeQueryResponseData(EntryType entryType, List<T> attrs, Function<T, String> attrToAttrName, Function<T, Object> attrToValue) {
        Map<String, Object> map = attrs.stream().collect(toMap(attrToAttrName::apply, (attr) -> {
            Object value = attrToValue.apply(attr);
            Attribute attribute = entryType.getAttributeOrNull(attrToAttrName.apply(attr));
            if (attribute != null) {
                value = rawToSystem(attribute.getType(), value);
            }
            return value;
        }));
        list(map.keySet()).stream().map(entryType::getAttributeOrNull).filter(notNull()).forEach((a) -> {
            switch (a.getType().getName()) {
                case LOOKUP:
                    LookupValue lookupValue = (LookupValue) map.get(a.getName());
                    if (lookupValue != null) {
                        String desc = toStringOrNull(map.remove(buildDescAttrName(a.getName())));
                        String code = toStringOrNull(map.remove(buildCodeAttrName(a.getName())));
                        Object value = LookupValueImpl.copyOf(lookupValue).withDescription(desc).withCode(code).build();
                        map.put(a.getName(), value);
                    }
                    break;
                case REFERENCE:
                case FOREIGNKEY:
                    IdAndDescription idAndDescription = (IdAndDescription) map.get(a.getName());
                    if (idAndDescription != null) {
                        String desc = toStringOrNull(map.remove(buildDescAttrName(a.getName())));
                        String code = toStringOrNull(map.remove(buildCodeAttrName(a.getName())));
                        Object value = new IdAndDescriptionImpl(idAndDescription.getId(), desc, code);
                        map.put(a.getName(), value);
                    }
            }
        });
        return map;
    }

    public static String wrapExprWithBrackets(String expr) {
        if (!isSafelyWrappedWithBrakets(expr)) {
            expr = format("(%s)", expr);
        }
        return expr;
    }

    public static boolean isSafelyWrappedWithBrakets(String expr) { //TODO improve this, parse sql and check
        return expr.trim().matches("[(][^()]+[)]");
    }

    public static SqlType parseSqlType(String sqlTypeString) {
        return parseSqlType(sqlTypeString, emptyAttributeMetadata());
    }

    public static CardAttributeType<?> createAttributeType(String sqlTypeString, AttributeMetadata meta) {
        return parseSqlType(sqlTypeString, meta).toAttributeType();
    }

    public static SqlType parseSqlType(String sqlTypeString, AttributeMetadata meta) {
        checkNotBlank(sqlTypeString);
        checkNotNull(meta);
        Matcher typeMatcher = Pattern.compile("(\\w+)(\\((\\d+(,\\d+)*)\\))?").matcher(sqlTypeString);
        checkArgument(typeMatcher.find());
        SqlTypeName type = SqlTypeName.valueOf(typeMatcher.group(1).toLowerCase());
        List<String> params = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(nullToEmpty(typeMatcher.group(3)));
        SqlType sqlType = new SqlTypeImpl(type, params, meta);
        return sqlType;
    }

//	private static final Pattern TYPE_PATTERN = Pattern.compile("(\\w+)(\\((\\d+(,\\d+)*)\\))?");
//	private static SqlType doParseSqlType(String sqlTypeString, @Nullable AttributeMetadata meta) {
//	}
    public static @Nullable
    String getSystemToSqlCastOrNull(CardAttributeType attributeType) {
        return SqlQueryUtils.getSystemToSqlCastOrNull(attributeTypeToSqlTypeName(attributeType));
    }

    public static String addSqlCastIfRequired(CardAttributeType attributeType, String expr) {
        String cast = getSystemToSqlCastOrNull(attributeType);
        if (isNotBlank(cast)) {
            expr += "::" + cast;
        }
        return expr;
    }

    public static String getSqlTypeString(CardAttributeType attributeType) {
        return attributeTypeToSqlType(attributeType).toSqlTypeString();
    }

//	public static SqlValueConverter getConverter(CardAttributeType attributeType) {
//		return getConverter(attributeTypeToSqlTypeName(attributeType));
//	}
    private static Object wrapJsonForJdbc(@Nullable Object value) {
        try {
            PGobject jsonObject = new PGobject();
            jsonObject.setType("json");
            jsonObject.setValue(toStringOrNull(value));
            return jsonObject;
        } catch (SQLException ex) {
            throw new DaoException(ex, "error wrapping to postgres json, source value = %s", value);
        }
    }

    public static boolean isVoidSqlType(String sqlTypeString) {
        return "void".equalsIgnoreCase(sqlTypeString);
    }

    public static CardAttributeType<?> createAttributeType(String sqlTypeString) {
        return parseSqlType(sqlTypeString).toAttributeType();
    }

    private static CardAttributeType<?> sqlTypeToAttributeType(SqlType sqlType) {
        try {
            AttributeMetadata meta = sqlType.getMetadata();
            switch (sqlType.getType()) {
                case bool:
                    return new BooleanAttributeType();
                case date:
                    return new DateAttributeType();
                case float8:
                    return new DoubleAttributeType();
                case inet:
                    if (meta instanceof AttributeMetadata) {
                        String typeValue = ((AttributeMetadata) meta).get(AttributeMetadata.IP_TYPE);
                        IpType type = parseEnumOrDefault(typeValue, IpType.IPV4);
                        return new IpAddressAttributeType(type);
                    }
                    throw new UnsupportedOperationException("unsupported inet type without AttributeMetadata");//TODO is this correct?
                case int4:
                    return new IntegerAttributeType();
                case int8:
                    if (meta.isLookup()) {
                        return new LookupAttributeType(meta.getLookupType());
                    } else if (meta.isReference()) {
                        return new ReferenceAttributeType(meta.getDomain(), meta.getDirection());
                    } else if (meta.isForeignKey()) {
                        return new ForeignKeyAttributeType(meta.getForeignKeyDestinationClassName());
                    } else {
                        return LongAttributeType.INSTANCE;
                    }
                case numeric:
                    if (sqlType.getParams().size() == 2) {
                        return new DecimalAttributeType(Integer.valueOf(sqlType.getParams().get(0)), Integer.valueOf(sqlType.getParams().get(1)));
                    } else {
                        return new DecimalAttributeType();
                    }
                case regclass:
                    return RegclassAttributeType.INSTANCE;
                case text:
                    return new TextAttributeType();
                case time:
                    return new TimeAttributeType();
                case timestamp:
                case timestamptz:
                    return new DateTimeAttributeType();
                case varchar:
                    if (sqlType.hasParams()) {
                        return new StringAttributeType(Integer.valueOf(getOnlyElement(sqlType.getParams())));
                    } else {
                        return new StringAttributeType();
                    }
                case _varchar:
                    return new StringArrayAttributeType();
                case jsonb:
                    return JsonAttributeType.INSTANCE;
                case bpchar:
                    if (sqlType.getParams().size() == 1) {
                        int value = Integer.valueOf(sqlType.getParams().get(0));
                        checkArgument(value > 0);
                        if (value == 1) {
                            return new CharAttributeType();
                        } else {
                            return new StringAttributeType(value);
                        }
                    } else {
                        return new CharAttributeType();
                    }
                case bytea:
                    return new ByteArrayAttributeType();
                case _bytea:
                    return new ByteaArrayAttributeType();
                default:
                    throw new UnsupportedOperationException(format("unsupported conversion of sql type = %s to attribute type", sqlType.getType()));
            }
        } catch (Exception ex) {
            throw new DaoException(ex, "error converting sql type = %s to attribute type", sqlType);
        }
    }

    public static SqlTypeName attributeTypeToSqlTypeName(CardAttributeType attributeType) {
        try {
            switch (attributeType.getName()) {
                case BOOLEAN:
                    return SqlTypeName.bool;
                case BYTEARRAY:
                    return SqlTypeName.bytea;
                case CHAR:
                    return SqlTypeName.bpchar;
                case DATE:
                    return SqlTypeName.date;
                case DECIMAL:
                    return SqlTypeName.numeric;
                case DOUBLE:
                    return SqlTypeName.float8;
                case REGCLASS:
                    return SqlTypeName.regclass;
                case INTEGER:
                    return SqlTypeName.int4;
                case FOREIGNKEY:
                case LOOKUP:
                case REFERENCE:
                case LONG:
                    return SqlTypeName.int8;
                case INET:
                    return SqlTypeName.inet;
                case JSON:
                    return SqlTypeName.jsonb;
                case STRING:
                    return SqlTypeName.varchar;
                case STRINGARRAY:
                    return SqlTypeName._varchar;
                case BYTEAARRAY:
                    return SqlTypeName._bytea;
                case TEXT:
                    return SqlTypeName.text;
                case TIME:
                    return SqlTypeName.time;
                case TIMESTAMP:
                    return SqlTypeName.timestamptz;
                case UNKNOWN:
                    return SqlTypeName.undefined;
                default:
                    throw new UnsupportedOperationException(format("unsupported conversion of attribute type = %s to sql type", attributeType.getName()));
            }
        } catch (Exception ex) {
            throw new DaoException(ex, "error converting attribute type = %s to sql type", attributeType);
        }
    }

    public static String attributeTypeToSqlCast(CardAttributeType attributeType) {
        return attributeTypeToSqlTypeName(attributeType).name();
    }

    public static SqlType attributeTypeToSqlType(CardAttributeType attributeType) {
        try {
            SqlTypeName sqlTypeName = attributeTypeToSqlTypeName(attributeType);
            switch (sqlTypeName) {
                case bpchar:
                    return new SqlTypeImpl(sqlTypeName, singletonList("1"));
                case numeric:
                    DecimalAttributeType decimalAttributeType = (DecimalAttributeType) attributeType;
                    if (decimalAttributeType.hasPrecisionAndScale()) {
                        return new SqlTypeImpl(sqlTypeName, asList(decimalAttributeType.getPrecision().toString(), decimalAttributeType.getScale().toString()));
                    } else {
                        return new SqlTypeImpl(sqlTypeName);
                    }
                case varchar:
                    StringAttributeType stringAttributeType = (StringAttributeType) attributeType;
                    if (stringAttributeType.hasLength()) {
                        return new SqlTypeImpl(sqlTypeName, singletonList(Integer.toString(stringAttributeType.getLength())));
                    } else {
                        return new SqlTypeImpl(sqlTypeName);
                    }
                default:
                    return new SqlTypeImpl(sqlTypeName);
            }
        } catch (Exception ex) {
            throw new DaoException(ex, "error converting attribute type = %s to sql type", attributeType);
        }
    }

    private static class SqlTypeImpl implements SqlType {

        private final SqlTypeName type;
        private final List<String> params;
        private final AttributeMetadata metadata;
        private final String sqlCast;

        public SqlTypeImpl(SqlTypeName type) {
            this(type, emptyList(), emptyAttributeMetadata());
        }

        public SqlTypeImpl(SqlTypeName type, List<String> params) {
            this(type, params, emptyAttributeMetadata());
        }

        public SqlTypeImpl(SqlTypeName type, List<String> params, AttributeMetadata metadata) {
            this.type = checkNotNull(type);
            this.metadata = checkNotNull(metadata);
            this.params = copyOf(checkNotNull(params));
            checkArgument(params.stream().allMatch(not(StringUtils::isBlank)), "found invalid blank param in params = %s", params);
            sqlCast = SqlQueryUtils.getSystemToSqlCastOrNull(type);
        }

        @Override
        public SqlTypeName getType() {
            return type;
        }

        @Override
        public List<String> getParams() {
            return params;
        }

        @Override
        public AttributeMetadata getMetadata() {
            return metadata;
        }

        @Override
        public String toSqlTypeString() {
            if (isNullOrEmpty(params)) {
                return type.name();
            } else {
                return format("%s(%s)", type.name(), Joiner.on(",").join(params));
            }
        }

        @Override
        public String getSqlCast() {
            return checkNotBlank(sqlCast);
        }

        @Override
        public boolean hasSqlCast() {
            return sqlCast != null;
        }

        @Override
        public CardAttributeType<?> toAttributeType() {
            return sqlTypeToAttributeType(this);
        }

        @Override
        public String toString() {
            return "SqlTypeImpl{" + "type=" + type + '}';
        }

    }

    @Nullable
    public static String getSystemToSqlCastOrNull(SqlTypeName sqlType) {
        switch (sqlType) {
            case inet:
                return "inet";
            case _bytea:
                return "bytea[]";
            case regclass:
                return "regclass";
            case time:
                return "varchar";
            default:
                return null;
        }
    }

    @Deprecated //TODO remove this
    public static @Nullable
    Object systemToSql(CardAttributeType attributeType, @Nullable Object value) {
        switch (attributeType.getName()) {
            case REGCLASS:
                return isNullOrBlank(value) ? null : quoteSqlIdentifier(toStringNotBlank(value));
            case STRING:
                return toStringOrNull(value);
            case JSON:
                return wrapJsonForJdbc(value);
            case DATE:
                return CmDateUtils.toSqlDate(value);
            case TIME:
                return CmDateUtils.toSqlTime(value);
            case TIMESTAMP:
                return CmDateUtils.toSqlTimestamp(value);
            case LOOKUP:
            case REFERENCE:
            case FOREIGNKEY:
                if (value instanceof IdAndDescriptionImpl) {
                    return IdAndDescriptionImpl.class.cast(value).getId();
                } else {
                    return value;
                }
            case STRINGARRAY:
                return new PostgreSQLArray(CmConvertUtils.convert(value, String[].class));
            case BYTEAARRAY:
                return new PostgreSQLArray(CmConvertUtils.convert(value, byte[][].class));
            default:
                return value;
        }
    }

    public static String systemToSqlExpr(@Nullable Object value, Attribute attribute) {
        return systemToSqlExpr(value, attribute.getType());
    }

    public static String systemToSqlExpr(@Nullable Object value, CardAttributeType attributeType) {
        if (value == null) {
            return "NULL";
        } else {
            switch (attributeType.getName()) {
                case REGCLASS:
                    if (value instanceof EntryType) {
                        value = entryTypeToSqlExpr((EntryType) value);
                    }
                    return classNameToSqlExpr(toStringNotBlank(value));
                case STRING:
                case CHAR:
                    return systemToSqlExpr(toStringOrNull(value));
                case JSON:
                    return isNullOrBlank(value) ? "NULL" : format("%s::jsonb", systemToSqlExpr(toStringNotBlank(value)));
                case DATE:
                    return systemToSqlExpr(CmDateUtils.toDate(value));
                case TIME:
                    return systemToSqlExpr(CmDateUtils.toTime(value));
                case TIMESTAMP:
                    return systemToSqlExpr(CmDateUtils.toDateTime(value));
                case LOOKUP:
                case REFERENCE:
                case FOREIGNKEY:
                    if (value instanceof IdAndDescription) {
                        value = ltEqZeroToNull(IdAndDescription.class.cast(value).getId());
                    }
                    return systemToSqlExpr(value);
                case STRINGARRAY:
                    return toSqlArrayExpr((List) convert(value, List.class).stream().map(CmStringUtils::toStringOrNull).collect(toList()), "varchar[]");
                case BYTEARRAY:
                    return byteArrayToSqlExpr(convert(value, byte[].class));
                case BYTEAARRAY:
                    return toSqlArrayExpr((List) list(convert(value, byte[][].class)), "bytea[]");
                default:
                    return systemToSqlExpr(value);
            }
        }
    }

    public static String byteArrayToSqlExpr(byte[] data) {
        return format("decode('%s','base64')", Base64.encodeBase64String(data));
    }

    @Nullable
    public static Object systemToSql(SqlTypeName type, @Nullable Object value) {
        switch (type) {
            case regclass:
                return quoteSqlIdentifier(toStringOrNull(value));
//			case STRING:
//				if (((StringAttributeType) attributeType).isJson()) {
//					return wrapJsonForJdbc(value); //TODO
//				} else {
//					return toStringOrNull(value);
//				}
            case date:
                return CmDateUtils.toSqlDate(value);
            case time:
                return CmDateUtils.toSqlTime(value);
            case timestamp:
            case timestamptz:
                return CmDateUtils.toSqlTimestamp(value);
            case int8:
                if (value instanceof IdAndDescriptionImpl) {
                    return IdAndDescriptionImpl.class.cast(value).getId();
                } else {
                    return value;
                }
            case _varchar:
                return new PostgreSQLArray(CmConvertUtils.convert(value, String[].class));
            case _bytea:
                return new PostgreSQLArray(CmConvertUtils.convert(value, byte[][].class));
            default:
                return value;
        }
    }

    public static String classNameToSqlExpr(String className) {
        return format("%s::regclass", systemToSqlExpr(quoteSqlIdentifier(className)));
    }

    public static String systemToSqlExpr(@Nullable Object value) {
        return systemToSqlExpr(value, (SqlTypeName) null);
    }

    public static String systemToSqlExpr(@Nullable Object value, @Nullable SqlTypeName sqlTypeName) {
        if (value == null) {
            return "NULL";
        } else if (value instanceof String) {
            return format("'%s'", ((String) value).replace("'", "''"));
        } else if (value instanceof Number) {
            return value.toString();
        } else if (value instanceof Boolean) {
            return value.toString().toUpperCase();
        } else if (isDate(value)) {
            return format("DATE %s", systemToSqlExpr(toIsoDate(value)));
        } else if (isTime(value)) {
            return format("TIME %s", systemToSqlExpr(toIsoTimeWithNanos(value)));
        } else if (isDateTime(value)) {
            return format("TIMESTAMPTZ %s", systemToSqlExpr(toIsoDateTime(value)));
        } else if (value instanceof Iterable) {
            String cast;
            if (sqlTypeName != null) {
                cast = sqlTypeNameToCast(sqlTypeName);
            } else if (stream((Iterable) value).allMatch(String.class::isInstance)) {
                cast = "varchar[]";
            } else {
                cast = "";
            }
            return toSqlArrayExpr((Iterable) value, cast);
        } else if (value instanceof EntryType) {
            return format("%s::regclass", systemToSqlExpr(entryTypeToSqlExpr(((EntryType) value))));
        } else if (value instanceof byte[]) {
            return byteArrayToSqlExpr(convert(value, byte[].class));
        } else if (value instanceof ToPrimitive) {
            return systemToSqlExpr(((ToPrimitive) value).toPrimitive(), sqlTypeName);
        } else {
            throw new IllegalArgumentException(format("unsupported conversion to sql expr of value = %s ( %s )", value, value.getClass().getName()));
        }
    }

    @Nullable
    private static String sqlTypeNameToCast(SqlTypeName sqlTypeName) {
        switch (sqlTypeName) {
            case _int8:
                return "bigint[]";
            case _varchar:
                return "varchar[]";
            case _bytea:
                return "bytea[]";
            default:
                throw new UnsupportedOperationException(format("TODO: cast of type = %s not implemented yet", sqlTypeName));//TODO
        }
    }

    private static String toSqlArrayExpr(Iterable list, @Nullable String cast) {
        String expr = format("ARRAY[%s]", stream(list).map(SqlQueryUtils::systemToSqlExpr).collect(joining(",")));
        if (isNotBlank(cast)) {
            expr = format("%s::%s", expr, cast);
        }
        return expr;
    }

//	@Deprecated
    public static @Nullable
    Object sqlToSystem(SqlTypeName type, @Nullable Object value) {
        switch (type) {
            case date:
                return CmDateUtils.toDate(value);
            case time:
                return CmDateUtils.toTime(value);
            case timestamp:
            case timestamptz:
                return CmDateUtils.toDateTime(value);
            case _varchar:
                if (value instanceof PgArray) {
                    return (String[][]) pgToJavaArray((PgArray) value);
                } else {
                    return value;
                }
            case _bytea:
                if (value instanceof PgArray) {
                    return (byte[][]) pgToJavaArray((PgArray) value);
                } else {
                    return value;
                }
            default:
                return value;
        }
    }

    private static Object pgToJavaArray(PgArray pgArray) {
        try {
            return pgArray.getArray();
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Deprecated
    public static String systemAttrToQuotedSqlIdentifier(SystemAttributes attribute) {
        return quoteSqlIdentifier(attribute.getDBName());
    }

    @Deprecated
    public static String quoteAttribute(Alias tableAlias, SystemAttributes attribute) {
        return quoteAttribute(tableAlias, attribute.getDBName());
    }

    @Deprecated
    public static String quoteAttribute(Alias tableAlias, String name) {
        return format("%s.%s", aliasToQuotedSql(tableAlias), quoteSqlIdentifier(name));
    }

    @Deprecated
    public static String nameForSystemAttribute(Alias alias, SystemAttributes sa) {
        return nameForUserAttribute(alias, sa.getDBName());
    }

    @Deprecated
    public static String nameForUserAttribute(Alias alias, String name) {
        return format("%s__%s", alias.asString(), name).toLowerCase();
    }

    @Deprecated
    public static String aliasToQuotedSql(Alias alias) {
        return quoteSqlIdentifier(alias.asString());
    }

}
