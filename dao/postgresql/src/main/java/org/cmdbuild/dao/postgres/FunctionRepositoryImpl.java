package org.cmdbuild.dao.postgres;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.cmdbuild.dao.function.StoredFunctionImpl;
import org.cmdbuild.dao.function.FunctionMetadata;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.isNull;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.MoreCollectors.toOptional;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import org.cmdbuild.cache.CacheConfig;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.Holder;
import org.cmdbuild.dao.DaoException;
import org.cmdbuild.dao.beans.FunctionMetadataImpl;
import static org.cmdbuild.dao.entrytype.attributetype.UndefinedAttributeType.isUndefined;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.createAttributeType;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.isVoidSqlType;
import org.cmdbuild.dao.driver.repository.FunctionRepository;
import org.cmdbuild.dao.function.StoredFunctionImpl.FunctionBuilder;
import static org.cmdbuild.spring.configuration.BeanNamesAndQualifiers.SYSTEM_LEVEL_ONE;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.function.StoredFunction;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_STRINGS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrNull;
import org.cmdbuild.utils.lang.CmMapUtils;
import org.cmdbuild.cache.CmCache;
import static org.cmdbuild.common.error.ErrorAndWarningCollectorService.marker;
import static org.cmdbuild.dao.postgres.utils.CommentUtils.FUNCTION_COMMENT_TO_METADATA_MAPPING;
import static org.cmdbuild.dao.postgres.utils.CommentUtils.parseCommentFromFeatures;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@Component
@Primary
public class FunctionRepositoryImpl implements FunctionRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final String DEFAULT_SCHEMA = "public", SEPARATOR = "|", KEY_VALUE_SEPARATOR = ": ";

    private final JdbcTemplate jdbcTemplate;

    private final Holder<List<StoredFunction>> allFunctionsCache;
    private final CmCache<Optional<StoredFunction>> functionsCacheByName;

    public FunctionRepositoryImpl(CacheService cacheService, @Qualifier(SYSTEM_LEVEL_ONE) JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = checkNotNull(jdbcTemplate);

        allFunctionsCache = cacheService.newHolder("dao_all_functions", CacheConfig.SYSTEM_OBJECTS);
        functionsCacheByName = cacheService.newCache("dao_functions_by_name", CacheConfig.SYSTEM_OBJECTS);
    }

    @Override
    public List<StoredFunction> getAllFunctions() {
        logger.debug("getting all functions");
        return allFunctionsCache.get(this::doFindAllFunctions);
    }

    @Override
    @Nullable
    public StoredFunction getFunctionOrNull(@Nullable String name) {
        if (isBlank(name)) {
            return null;
        } else {
            return functionsCacheByName.get(name, () -> Optional.ofNullable(doGetFunctionOrNull(name))).orElse(null);
        }
    }

    @Nullable
    private StoredFunction doGetFunctionOrNull(String name) {
        return getAllFunctions().stream().filter((fun) -> equal(fun.getName(), name)).collect(toOptional()).orElse(null);
    }

    private List<StoredFunction> doFindAllFunctions() {
        List<StoredFunction> functionList = newArrayList(filter(jdbcTemplate.query("SELECT * FROM _cm3_function_list_detailed()", new RowMapper<StoredFunctionImpl>() {
            @Override
            public StoredFunctionImpl mapRow(ResultSet rs, int rowNum) throws SQLException {
                String name = "<unknown>";
                try {
                    name = rs.getString("function_name");
                    logger.trace("processing function {}", name);
                    Long id = rs.getLong("function_id");
                    boolean returnsSet = rs.getBoolean("returns_set");
                    Map<String, String> metadata = fromJson(rs.getString("metadata"), MAP_OF_STRINGS),
                            comment = fromJson(rs.getString("comment"), MAP_OF_STRINGS);
                    FunctionMetadata meta = new FunctionMetadataImpl(CmMapUtils.<String, String>map()
                            .with(metadata)
                            .with(new FunctionMetadataImpl(parseCommentFromFeatures(comment, FUNCTION_COMMENT_TO_METADATA_MAPPING)).getAll()));
                    return StoredFunctionImpl.builder()
                            .withName(name)
                            .withId(id)
                            .withReturnSet(returnsSet)
                            .withMetadata(meta)
                            .accept((builder) -> addParameters(rs, builder))
                            .build();
                } catch (Exception ex) {
                    logger.error(marker(), "error processing function = {} ", name, ex);
                    return null;
                }
            }

            private void addParameters(ResultSet rs, FunctionBuilder builder) {
                try {
                    String[] argIo = (String[]) rs.getArray("arg_io").getArray();
                    String[] argNames = (String[]) rs.getArray("arg_names").getArray();
                    String[] argTypes = (String[]) rs.getArray("arg_types").getArray();
                    checkArgument(argIo.length == argNames.length && argNames.length == argTypes.length, "arg io, names, types mismatch!");
                    for (int i = 0; i < argIo.length; ++i) {
                        logger.trace("processing function param name = {}, type = {}, io = {}", argNames[i], argTypes[i], argIo[i]);
                        String name = checkNotBlank(argNames[i], "arg name is null"), sqlTypeName = checkNotBlank(argTypes[i], "arg type is null");
                        try {
                            boolean isBaseDsp = firstNonNull(toBooleanOrNull(builder.getFunctionMetadata().getCustomMetadata().get(format("attribute.%s.basedsp", name))), true);
                            String io = checkNotBlank(argIo[i], "arg io is null").toLowerCase();
                            switch (io) {
                                case "i":
                                    logger.trace("add input param name = {} type = {}", name, sqlTypeName);
                                    builder.withInputParameter(name, getType(sqlTypeName));
                                    break;
                                case "o":
                                    if (!isVoidSqlType(sqlTypeName)) {
                                        logger.trace("add output param name = {} type = {}", name, sqlTypeName);
                                        builder.withOutputParameter(name, getType(sqlTypeName), isBaseDsp);
                                    }
                                    break;
                                case "io":
                                    logger.trace("add input/output param name = {} type = {}", name, sqlTypeName);
                                    builder.withInputParameter(name, getType(sqlTypeName));
                                    builder.withOutputParameter(name, getType(sqlTypeName), isBaseDsp);
                                    break;
                                default:
                                    throw new IllegalArgumentException("unsupported io param = " + io);
                            }
                        } catch (Exception ex) {
                            throw new DaoException(ex, "error processing function param = %s for function = %s", name, builder.getName());
                        }
                    }
                } catch (SQLException ex) {
                    throw new DaoException(ex);
                }
            }

            private CardAttributeType getType(String sqlType) {
                CardAttributeType type = createAttributeType(sqlType);
                checkArgument(!isUndefined(type), "param type is 'undefined', sql type = %s", sqlType);
                return type;
            }

        }), not(isNull())));
        return functionList;
    }

}
