/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.q3;

import org.cmdbuild.dao.core.q3.ResultRow;
import org.cmdbuild.dao.core.q3.PreparedQuery;
import org.cmdbuild.dao.core.q3.QueryBuilder;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.core.q3.QueryBuilderService;
import com.google.common.base.Joiner;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import static java.lang.String.format;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.dao.DaoException;
import org.cmdbuild.dao.beans.CMRelation;
import static org.cmdbuild.dao.beans.CMRelation.ATTR_CODE2;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.orm.CardMapperService;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardIdAndClassName;
import org.cmdbuild.dao.beans.CardIdAndClassNameImpl;
import org.cmdbuild.dao.beans.DatabaseRecord;
import org.cmdbuild.dao.beans.RelationDirection;
import org.cmdbuild.dao.beans.IdAndDescription;
import org.cmdbuild.dao.beans.IdAndDescriptionImpl;
import org.cmdbuild.dao.beans.LookupValue;
import org.cmdbuild.dao.beans.LookupValueImpl;
import org.cmdbuild.dao.beans.RelationImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_BEGINDATE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_USER;
import org.cmdbuild.dao.driver.PostgresService;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.ReverseDomain;
import org.cmdbuild.utils.date.CmDateUtils;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDDOMAIN;
import static org.cmdbuild.dao.constants.SystemAttributes.DOMAIN_RESERVED_ATTRIBUTES;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.ClassDefinition;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.cmdbuild.dao.function.StoredFunction;
import static org.cmdbuild.dao.utils.AttributeConversionUtils.rawToSystem;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.dao.entrytype.attributetype.ForeignKeyAttributeType;
import org.cmdbuild.dao.function.StoredFunctionOutputParameter;
import org.cmdbuild.dao.function.StoredFunctionParameter;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.quoteSqlIdentifier;
import org.cmdbuild.dao.entrytype.DomainDefinition;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.LOOKUP;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.attributeTypeToSqlCast;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildCodeAttrName;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildDescAttrName;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildLookupCodeExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildLookupDescExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildReferenceCodeExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildReferenceDescExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.classNameToSqlExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.sqlTableToClassName;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.sqlTableToDomainName;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.systemToSqlExpr;
import org.cmdbuild.dao.user.UserDaoHelperService;
import org.cmdbuild.dao.user.UserDaoService;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNullAndGtZero;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.entryTypeToSqlExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.parseEntryTypeQueryResponseData;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowFunction;

@Component
public class DaoServiceImpl implements DaoService, UserDaoService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final PostgresService database;
    private final CardMapperService mapper;
    private final QueryBuilderService queryBuilderService;
    private final UserDaoHelperService userDaoHelper;

    public DaoServiceImpl(PostgresService database, CardMapperService mapper, QueryBuilderService queryBuilderService, UserDaoHelperService userDaoHelper) {
        this.database = checkNotNull(database);
        this.mapper = checkNotNull(mapper);
        this.queryBuilderService = checkNotNull(queryBuilderService);
        this.userDaoHelper = checkNotNull(userDaoHelper);
    }

    @Override
    public UserDaoHelperService getUserDaoHelper() {
        return userDaoHelper;
    }

    @Override
    public List<StoredFunction> getAllFunctions() {
        return database.getAllFunctions();
    }

    @Override
    public StoredFunction getFunctionOrNull(String name) {
        return database.getFunctionOrNull(name);
    }

    @Override
    public QueryBuilder query() {
        return queryBuilderService.query();
    }

    @Override
    public <T> T create(T model) {
        Card card = mapper.objectToCard(model);
        card = create(card);
        return mapper.cardToObject(card);
    }

    @Override
    public <T> long createOnly(T model) {
        Card card = mapper.objectToCard(model);
        return createOnly(card);
    }

    @Override
    public long createOnly(Card card) {
        return database.create(card);
    }

    @Override
    public <T> T update(T model) {
        Card card = mapper.objectToCard(model);
        card = update(card);
        return mapper.cardToObject(card);
    }

    @Override
    public <T> void updateOnly(T model) {
        Card card = mapper.objectToCard(model);
        updateOnly(card);
    }

    @Override
    public void updateOnly(Card card) {
        database.update(card);
    }

    @Override
    public void delete(Class model, long cardId) {
        delete(mapper.getClasseForModelOrBuilder(model), cardId);
    }

    @Override
    public JdbcTemplate getJdbcTemplate() {
        return database.getJdbcTemplate();
    }

    @Override
    public Classe createClass(ClassDefinition definition) {
        return database.createClass(definition);
    }

    @Override
    public Classe updateClass(ClassDefinition definition) {
        return database.updateClass(definition);
    }

    @Override
    public void deleteClass(Classe dbClass) {
        database.deleteClass(dbClass);
    }

    @Override
    public Card create(Card card) {
        long id = database.create(card);
        return getCard(card.getType(), id);
    }

    @Override
    public Card update(Card card) {
        database.update(card);
        return getCard(card.getType(), card.getId());
    }

    @Override
    public List<Classe> getAllClasses() {
        return database.getAllClasses();
    }

    @Override
    public Classe getClasseOrNull(long oid) {
        return database.getClasseOrNull(oid);
    }

    @Override
    public Classe getClasseOrNull(String name) {
        return database.getClasseOrNull(name);
    }

    @Override
    public Domain getDomain(String domainId) {
        return database.getDomain(domainId);
    }

    @Override
    public Domain getDomainOrNull(Long id) {
        return database.getDomainOrNull(id);
    }

    @Override
    public Domain getDomainOrNull(String localname) {
        return database.getDomainOrNull(localname);
    }

    @Override
    public List<Domain> getAllDomains() {
        return database.getAllDomains();
    }

    @Override
    public Domain createDomain(DomainDefinition definition) {
        return database.createDomain(definition);
    }

    @Override
    public Domain updateDomain(DomainDefinition definition) {
        return database.updateDomain(definition);
    }

    @Override
    public void deleteDomain(Domain domain) {
        database.deleteDomain(domain);
    }

    @Override
    public List<Domain> getDomainsForClasse(Classe classe) {
        return database.getDomainsForClasse(classe);
    }

    @Override
    public void delete(DatabaseRecord card) {
        database.delete(card);
    }

    @Override
    public void delete(Object model) {
        try {
            long id = mapper.getCardId(model);
            delete(model.getClass(), id);
        } catch (Exception ex) {
            throw new DaoException(ex, "error deleting card for bean = %s", model);
        }
    }

    @Override
    public Attribute createAttribute(Attribute definition) {
        return database.createAttribute(definition);
    }

    @Override
    public List<Attribute> updateAttributes(List<Attribute> attributes) {
        return database.updateAttributes(attributes);
    }

    @Override
    public void deleteAttribute(Attribute dbAttribute) {
        database.deleteAttribute(dbAttribute);
    }

    @Override
    public List<CMRelation> getServiceRelationsForCard(CardIdAndClassName sourceCard) {
        Classe sourceClass = getClasse(sourceCard.getClassName());
        List<Domain> domains = database.getDomainsForClasse(sourceClass).stream()
                .filter(Domain::hasServiceReadPermission)
                .map((d) -> d.getThisDomainDirectAndOrReversedForClass(sourceClass))
                .flatMap(List::stream).collect(toList());

        AliasBuilder aliasBuilder = new AliasBuilder();
        String targetClassAlias = aliasBuilder.buildAlias("targetclass"),
                targetIdAlias = aliasBuilder.buildAlias("targetid"),
                targetDescAlias = aliasBuilder.buildAlias("targetdesc"),
                targetCodeAlias = aliasBuilder.buildAlias("targetcode"),
                directionAlias = aliasBuilder.buildAlias("direction");

        Map<String, String> customAttributeAliasesForAllDomains = domains.stream().flatMap(d -> {
            String direction = RelationDirectionQueryHelper.forDomain(d).name();
            return getDomainAttributesForRelationQuery(d).stream().map(a -> {
                String alias = aliasBuilder.buildAliasAndStore(a.getName(), a.getOwner().getName(), direction, a.getName());
                String expr = format("NULL::%s %s", attributeTypeToSqlCast(a.getType()), alias);
                if (a.isOfType(LOOKUP)) {
                    String codeAlias = aliasBuilder.buildAliasAndStore(buildCodeAttrName(a.getName()), a.getOwner().getName(), direction, buildCodeAttrName(a.getName())),
                            descAlias = aliasBuilder.buildAliasAndStore(buildDescAttrName(a.getName()), a.getOwner().getName(), direction, buildDescAttrName(a.getName()));
                    expr += format(", NULL::varchar %s, NULL::varchar %s", codeAlias, descAlias);
                }
                return Pair.of(alias, expr);
            });
        }).collect(toMap(Pair::getKey, Pair::getValue));

        List<String> domainQuery = domains.stream().map((d) -> {
            RelationDirectionQueryHelper direction = RelationDirectionQueryHelper.forDomain(d);
            String domainExpr = entryTypeToSqlExpr(d);
            String targetExpr = entryTypeToSqlExpr(d.getTargetClass());
            Map<String, String> customAttributeAliasesAndExprs = getDomainAttributesForRelationQuery(d).stream().map(a -> {
                String alias = aliasBuilder.getAlias(a.getOwner().getName(), direction.name(), a.getName()),
                        attrExpr = quoteSqlIdentifier(a.getName()),
                        expr = format("%s.%s %s", domainExpr, attrExpr, alias);              //TODO proper attribute processing                
                if (a.isOfType(LOOKUP)) {
                    String codeAlias = aliasBuilder.getAlias(a.getOwner().getName(), direction.name(), buildCodeAttrName(a.getName())),
                            descAlias = aliasBuilder.getAlias(a.getOwner().getName(), direction.name(), buildDescAttrName(a.getName()));
                    expr += format(", %s %s, %s %s", buildLookupCodeExpr(domainExpr, attrExpr), codeAlias, buildLookupDescExpr(domainExpr, attrExpr), descAlias);
                }
                return Pair.of(alias, expr);
            }).collect(toMap(Pair::getKey, Pair::getValue));
            String customAttrsQuery = customAttributeAliasesForAllDomains.keySet().stream().map(a -> {
                if (customAttributeAliasesAndExprs.containsKey(a)) {
                    return customAttributeAliasesAndExprs.get(a);
                } else {
                    return customAttributeAliasesForAllDomains.get(a);
                }
            }).collect(joining(", "));
            if (isNotBlank(customAttrsQuery)) {
                customAttrsQuery = ", " + customAttrsQuery;
            }
            return String.format("SELECT '%s' %s, %s.\"Id\", %s.\"IdDomain\", %s.\"User\", %s.\"BeginDate\", %s.%s %s, %s.%s %s, "
                    + "%s.\"Description\" %s, %s.\"Code\" %s"//TODO replace with cross tenant function
                    + "%s "
                    + "FROM %s LEFT JOIN %s ON %s.%s = %s.\"IdClass\" AND %s.%s = %s.\"Id\" "
                    + "WHERE %s.\"Status\" = 'A' AND %s.\"Status\" = 'A' AND %s.%s = %s AND %s.%s = %s",
                    direction.name(), directionAlias, domainExpr, domainExpr, domainExpr, domainExpr, domainExpr, direction.getTargetClassIdExpr(), targetClassAlias, domainExpr, direction.getTargetCardIdExpr(), targetIdAlias,
                    targetExpr, targetDescAlias, targetExpr, targetCodeAlias,
                    customAttrsQuery,
                    domainExpr, targetExpr, domainExpr, direction.getTargetClassIdExpr(), targetExpr, domainExpr, direction.getTargetCardIdExpr(), targetExpr,
                    domainExpr, targetExpr, domainExpr, direction.getSourceClassIdExpr(), classNameToSqlExpr(sourceClass.getName()), domainExpr, direction.getSourceCardIdExpr(), sourceCard.getId());
        }).collect(toList());

        String query = Joiner.on(" UNION ALL ").join(domainQuery);
        return database.getJdbcTemplate().query(query, (ResultSet rs, int rowNum) -> {
            Long relationId = checkNotNullAndGtZero(rs.getLong(ATTR_ID));
            String domainId = sqlTableToDomainName(checkNotBlank(rs.getString(ATTR_IDDOMAIN)));
            RelationDirectionQueryHelper direction = RelationDirectionQueryHelper.valueOf(rs.getString(directionAlias));
            String user = rs.getString(ATTR_USER);
            Timestamp beginDate = rs.getTimestamp(ATTR_BEGINDATE);
            String targetDescription = rs.getString(targetDescAlias);
            String targetCode = rs.getString(targetCodeAlias);
            String targetClassId = sqlTableToClassName(checkNotBlank(rs.getString(targetClassAlias)));
            Long targetCardId = checkNotNullAndGtZero(rs.getLong(targetIdAlias));
            Domain domain = database.getDomain(domainId);
            Classe targetClass = database.getClasse(targetClassId);
            return RelationImpl.builder()
                    .withDirection(direction.toRelationDirection())
                    .withType(domain)
                    .withId(relationId)
                    .withUser(user)
                    .withBeginDate(CmDateUtils.toDateTime(beginDate))
                    .withSourceCard(sourceCard)
                    .withTargetCard(CardIdAndClassNameImpl.card(targetClass.getName(), targetCardId))
                    .withTargetDescription(targetDescription)
                    .addAttribute(ATTR_CODE2, targetCode)
                    .addAttributes(parseEntryTypeQueryResponseData(domain, getDomainAttributesForRelationQuery(domain).stream().map(a -> {
                        if (a.isOfType(LOOKUP)) {
                            return list(a.getName(), buildCodeAttrName(a.getName()), buildDescAttrName(a.getName()));
                        } else {
                            return list(a.getName());
                        }
                    }).flatMap(List::stream).collect(toList()), identity(), rethrowFunction(a -> {
                        String alias = aliasBuilder.getAlias(domain.getName(), direction.name(), a);
                        return rs.getObject(alias);
                    })))
                    //TODO tenant id ??
                    .build();
        });
    }

    private List<Attribute> getDomainAttributesForRelationQuery(Domain domain) {
        return domain.getCoreAttributes().stream().filter(a -> !DOMAIN_RESERVED_ATTRIBUTES.contains(a.getName())).collect(toList());
    }

    @Override
    public PreparedQuery selectFunction(StoredFunction function, List<Object> input, List<StoredFunctionOutputParameter> outputParamMapping) {
        AliasBuilder aliasBuilder = new AliasBuilder();
        String functionAlias = aliasBuilder.buildAlias(function.getName());

        List<Pair<String, String>> outputExprAndAlias = list();
        List<Pair<String, StoredFunctionOutputParameter>> outputAliasAndParam = list();
        checkArgument(outputParamMapping.size() == function.getOutputParameters().size());
        outputParamMapping.forEach((param) -> {
            String name = param.getName(),
                    expr = quoteSqlIdentifier(name),
                    alias = aliasBuilder.buildAlias(name);
            outputExprAndAlias.add(Pair.of(expr, alias));
            outputAliasAndParam.add(Pair.of(alias, param));
            CardAttributeType<?> type = param.getType();
            switch (type.getName()) {
                case LOOKUP:
                    outputExprAndAlias.add(Pair.of(buildLookupDescExpr(functionAlias, expr), buildDescAttrName(alias)));
                    outputExprAndAlias.add(Pair.of(buildLookupCodeExpr(functionAlias, expr), buildCodeAttrName(alias)));
                    break;
                case FOREIGNKEY:
                    Classe targetClass = database.getClasse(((ForeignKeyAttributeType) type).getForeignKeyDestinationClassName());
                    outputExprAndAlias.add(Pair.of(buildReferenceDescExpr(targetClass, functionAlias, expr), buildDescAttrName(alias)));
                    outputExprAndAlias.add(Pair.of(buildReferenceCodeExpr(targetClass, functionAlias, expr), buildCodeAttrName(alias)));
            }
        });

        checkArgument(input.size() == function.getInputParameters().size());
        List<Object> functionArgs = Streams.zip(function.getInputParameters().stream().map(StoredFunctionParameter::getType), input.stream(), (type, value) -> {
            value = rawToSystem(type, value);
            value = systemToSqlExpr(value, type);
            return value;
        }).collect(toList());

        String attrQuery = outputExprAndAlias.stream().map((p) -> format("%s AS %s", p.getLeft(), p.getRight())).collect(joining(", "));

        String query = format("SELECT %s FROM %s(%s) %s", attrQuery, quoteSqlIdentifier(function.getName()), Joiner.on(", ").join(functionArgs), functionAlias);

        return new FunctionPreparedQuery(query, outputAliasAndParam);
    }

    @Override
    public CMRelation create(CMRelation relation) {
        long id = database.create(relation);
        return getRelation(relation.getType(), id);
    }

    @Override
    public CMRelation update(CMRelation relation) {
        database.update(relation);
        return getRelation(relation.getType(), relation.getId());
    }

    @Override
    public void delete(CMRelation relation) {
        database.delete(relation);
    }

    private class FunctionPreparedQuery implements PreparedQuery {

        private final String query;
        private final List<Pair<String, StoredFunctionOutputParameter>> outputParametersWithAlias;

        public FunctionPreparedQuery(String query, List<Pair<String, StoredFunctionOutputParameter>> outputParametersWithAlias) {
            this.query = checkNotBlank(query);
            this.outputParametersWithAlias = ImmutableList.copyOf(outputParametersWithAlias);
        }

        @Override
        public List<ResultRow> run() {
            return database.getJdbcTemplate().query(query, (rs, i) -> {
                Map<String, Object> map = map();
                for (Pair<String, StoredFunctionOutputParameter> param : outputParametersWithAlias) {
                    String alias = param.getLeft();
                    Object value = rs.getObject(alias);
                    String name = param.getRight().getName();
                    CardAttributeType<?> attributeType = param.getRight().getType();
                    value = rawToSystem(attributeType, value);

                    switch (attributeType.getName()) {//TODO duplicate processing code, refactor and unify
                        case LOOKUP:
                            LookupValue lookupValue = (LookupValue) value;
                            if (lookupValue != null) {
                                String desc = rs.getString(buildDescAttrName(alias));
                                String code = rs.getString(buildCodeAttrName(alias));
                                value = LookupValueImpl.copyOf(lookupValue).withCode(code).withDescription(desc).build();
                            }
                            break;
                        case FOREIGNKEY:
                            IdAndDescription idAndDescription = (IdAndDescription) value;
                            if (idAndDescription != null) {
                                String desc = rs.getString(buildDescAttrName(alias));
                                String code = rs.getString(buildCodeAttrName(alias));
                                value = new IdAndDescriptionImpl(idAndDescription.getId(), desc, code);
                            }
                    }

                    map.put(name, value);
                }
                return new FunctionResultRow(map);
            });
        }

        private class FunctionResultRow extends AbstractResultRow {

            private final Map<String, Object> map;

            public FunctionResultRow(Map<String, Object> map) {
                this.map = Collections.unmodifiableMap(checkNotNull(map));
            }

            @Override
            public <T> T toModel(Class<T> type) {
                return (T) mapper.getMapperForModelOrBuilder(type).dataToObject(asMap()::get);
            }

            @Override
            public Map<String, Object> asMap() {
                return map;
            }

            @Override
            public <T> T toModel() {
                throw new UnsupportedOperationException("function result cannot be mapped to model without explicit model class param provided");
            }

            @Override
            public Card toCard() {
                throw new UnsupportedOperationException("function result cannot be mapped to card");
            }

            @Override
            public CMRelation toRelation() {
                throw new UnsupportedOperationException("function result cannot be mapped to relation");
            }

        }

    }

    private enum RelationDirectionQueryHelper {
        DIRECT("1", "2", RelationDirection.RD_DIRECT),
        INVERSE("2", "1", RelationDirection.RD_INVERSE);

        private final String one, two;
        private final RelationDirection direction;

        public static RelationDirectionQueryHelper forDomain(Domain d) {
            return d instanceof ReverseDomain ? RelationDirectionQueryHelper.INVERSE : RelationDirectionQueryHelper.DIRECT;
        }

        private RelationDirectionQueryHelper(String one, String two, RelationDirection direction) {
            this.one = one;
            this.two = two;
            this.direction = direction;
        }

        public RelationDirection toRelationDirection() {
            return direction;
        }

        String getSourceCardIdExpr() {
            return format("\"IdObj%s\"", one);
        }

        String getTargetCardIdExpr() {
            return format("\"IdObj%s\"", two);
        }

        String getSourceClassIdExpr() {
            return format("\"IdClass%s\"", one);
        }

        String getTargetClassIdExpr() {
            return format("\"IdClass%s\"", two);
        }
    }

}
