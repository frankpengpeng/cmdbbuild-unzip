package org.cmdbuild.dao.postgres;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import static java.lang.String.format;
import java.sql.Connection;
import static java.util.stream.Collectors.joining;
import javax.annotation.Nullable;
import javax.inject.Provider;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.dao.DaoException;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDCLASS1;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDCLASS2;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDOBJ1;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDOBJ2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.beans.CMRelation;
import static org.cmdbuild.dao.constants.SystemAttributes.SYSTEM_ATTRIBUTES_NEVER_INSERTED;
import org.cmdbuild.dao.entrytype.Attribute;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.dao.beans.DatabaseRecord;
import org.cmdbuild.dao.beans.IdAndDescription;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.quoteSqlIdentifier;
import static org.cmdbuild.dao.entrytype.AttributePermission.AP_CREATE;
import static org.cmdbuild.dao.entrytype.AttributePermission.AP_UPDATE;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.dao.postgres.utils.SqlQueryUtils;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.classNameToSqlExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.systemToSqlExpr;
import org.cmdbuild.lookup.LookupRepository;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNullOrLtEqZero;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.entryTypeToSqlExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.entryTypeToSqlExpr;

@Component
public class EntryUpdateServiceImpl implements EntryUpdateService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final Provider<LookupRepository> lookupHelper; //TODO this is ok but not great; if possible refactor lookup store, avoid this lazy dependency here
    private final JdbcTemplate jdbcTemplate;

    public EntryUpdateServiceImpl(JdbcTemplate jdbcTemplate, Provider<LookupRepository> lookupHelper) {
        this.jdbcTemplate = checkNotNull(jdbcTemplate);
        this.lookupHelper = checkNotNull(lookupHelper);
    }

    @Override
    public long executeInsertAndReturnKey(DatabaseRecord entry) {
        return new EntryUpdateExecutor(entry).executeInsert();
    }

    @Override
    public void executeUpdate(DatabaseRecord entry) {
        new EntryUpdateExecutor(entry).executeUpdate();
    }

    private class EntryUpdateExecutor {

        protected final DatabaseRecord entry;

        protected final List<AttrAndVaue> attrs = list();

        protected EntryUpdateExecutor(DatabaseRecord entry) {
            this.entry = checkNotNull(entry);
        }

        public long executeInsert() {
            try {
                prepareValues(false);
                KeyHolder keyHolder = new GeneratedKeyHolder();
                jdbcTemplate.update((Connection connection) -> {
                    String query;
                    if (attrs.isEmpty()) {
                        query = format("INSERT INTO %s DEFAULT VALUES", entryTypeToSqlExpr(entry.getType()));
                    } else {
                        query = format("INSERT INTO %s (%s) VALUES (%s)",
                                entryTypeToSqlExpr(entry.getType()),
                                attrs.stream().map(AttrAndVaue::getAttr).map(SqlQueryUtils::quoteSqlIdentifier).collect(joining(", ")),
                                attrs.stream().map(AttrAndVaue::getValue).collect(joining(", ")));
                    }
                    return connection.prepareStatement(query, new String[]{ATTR_ID});
                }, keyHolder);
                return checkNotNull(keyHolder.getKey(), "null id returned from insert query").longValue();
            } catch (Exception ex) {
                throw new DaoException(ex, "error executing insert for entry = %s", entry);
            }
        }

        public void executeUpdate() {
            try {
                prepareValues(true);
                if (attrs.isEmpty()) {
                    logger.warn("attrs is empty, skipping update");
                } else {
                    String sql = format("UPDATE %s SET %s WHERE %s = %s",
                            entryTypeToSqlExpr(entry.getType()),
                            attrs.stream().map((a) -> format("%s = %s", a.getAttr(), a.getValue())).collect(joining(", ")),
                            quoteSqlIdentifier(ATTR_ID), checkNotNull(entry.getId()));
                    jdbcTemplate.update(sql);
                }
            } catch (Exception ex) {
                throw new DaoException(ex, "error executing update for entry = %s", entry);
            }
        }

        private void prepareValues(boolean forUpdate) {
            entry.getAllValuesAsMap().forEach((key, rawValue) -> {
                Attribute attribute = entry.getType().getAttributeOrNull(key);
                if (attribute == null) {
                    logger.debug("attribute not found or reserved for type = {} attr = {}, will not save value on db", entry.getType(), key);
                } else {
                    try {
                        if ((!attribute.hasCorePermission(AP_UPDATE) && forUpdate) || (!attribute.hasCorePermission(AP_CREATE) && !forUpdate)) {
                            logger.debug("ignore new value for immutable attribute = {} val = {}", key, rawValue);
                        } else if (SYSTEM_ATTRIBUTES_NEVER_INSERTED.contains(attribute.getName())) {
                            //skip system attrs
                        } else {
                            addValue(attribute.getName(), attributeValueToSqlExpr(attribute.getType(), rawValue));
//							String marker = "?";
//							String cast = getSystemToSqlCastOrNull(attribute.getType());
//							if (isNotBlank(cast)) {
//								marker += "::" + cast;
//							}
//							addValue(attribute.getName(), sqlValue, marker);
                        }
                    } catch (Exception ex) {
                        throw new DaoException(ex, "error preparing value for attribute = %s.%s", attribute.getOwner().getName(), attribute.getName());
                    }
                }
            });

            if (entry instanceof CMRelation) {
                CMRelation relation = (CMRelation) entry;
                addValue(ATTR_IDOBJ1, relation.getSourceId().toString());
                addValue(ATTR_IDCLASS1, classNameToSqlExpr(relation.getSourceCard().getClassName()));
                addValue(ATTR_IDOBJ2, relation.getTargetId().toString());
                addValue(ATTR_IDCLASS2, classNameToSqlExpr(relation.getTargetCard().getClassName()));
            }
        }

        private String attributeValueToSqlExpr(CardAttributeType attributeType, @Nullable Object value) {
            switch (attributeType.getName()) {
                case LOOKUP:
                    if (value instanceof IdAndDescription) {
                        Long id = ((IdAndDescription) value).getId();
                        String code = ((IdAndDescription) value).getCode();
                        String type = ((LookupAttributeType) attributeType).getLookupTypeName();
                        if (isNullOrLtEqZero(id) && isBlank(code)) {
                            return null;
                        } else {
                            String codeToTry = code;
                            if (isNotNullAndGtZero(id) && lookupHelper.get().hasLookupWithTypeAndId(type, id)) {
                                return systemToSqlExpr(id);
                            } else {
                                codeToTry = firstNotBlank(codeToTry, String.valueOf(id));
                            }
                            if (isNotBlank(codeToTry)) {
                                if (lookupHelper.get().hasLookupWithTypeAndCode(type, codeToTry)) {
                                    return systemToSqlExpr(lookupHelper.get().getOneByTypeAndCode(type, codeToTry).getId());
                                }
                                if (lookupHelper.get().hasLookupWithTypeAndDescription(type, codeToTry)) {
                                    return systemToSqlExpr(lookupHelper.get().getOneByTypeAndDescription(type, codeToTry).getId());
                                }
                            }
                            throw new DaoException("lookup not found for id = %s code = %s type = %s", id, code, type);
                        }
                    } else {
                        return systemToSqlExpr(value, attributeType);
                    }
                default:
                    return systemToSqlExpr(value, attributeType);
            }
        }

        protected void addValue(String name, String expr) {
            attrs.add(new AttrAndVaue(quoteSqlIdentifier(name), expr));
        }

    }

    private final static class AttrAndVaue {

        private final String attr, value;

        public AttrAndVaue(String attr, String value) {
            this.attr = checkNotBlank(attr);
            this.value = checkNotBlank(value);
        }

        public String getAttr() {
            return attr;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "AttrAndVauelAndMarker{" + "attr=" + attr + ", value=" + value + '}';
        }

    }

}
