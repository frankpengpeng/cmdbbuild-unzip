/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.q3;

import org.cmdbuild.dao.core.q3.WhereOperator;
import org.cmdbuild.dao.core.q3.PreparedQuery;
import com.google.common.base.Joiner;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.not;
import static com.google.common.base.Predicates.notNull;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.ImmutableList.toImmutableList;
import com.google.common.collect.ImmutableMap;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.MoreCollectors.toOptional;
import com.google.common.collect.Ordering;
import static java.lang.String.format;
import java.util.Collection;
import java.util.Collections;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import javax.inject.Provider;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.auth.user.OperationUser;
import org.cmdbuild.auth.user.OperationUserSupplier;
import static org.cmdbuild.common.utils.PagedElements.hasLimit;
import static org.cmdbuild.common.utils.PagedElements.hasOffset;
import static org.cmdbuild.cql.CqlUtils.compileAndCheck;
import org.cmdbuild.cql.compiler.impl.ClassDeclarationImpl;
import org.cmdbuild.cql.compiler.impl.CqlQueryImpl;
import org.cmdbuild.cql.compiler.impl.FieldImpl;
import org.cmdbuild.cql.compiler.impl.GroupImpl;
import org.cmdbuild.cql.compiler.impl.WhereImpl;
import org.cmdbuild.cql.compiler.where.Field;
import org.cmdbuild.dao.DaoException;
import static org.cmdbuild.dao.beans.CMRelation.ATTR_CANREAD1;
import static org.cmdbuild.dao.beans.CMRelation.ATTR_CANREAD2;
import static org.cmdbuild.dao.beans.CMRelation.ATTR_CODE1;
import static org.cmdbuild.dao.beans.CMRelation.ATTR_CODE2;
import static org.cmdbuild.dao.beans.CMRelation.ATTR_DESCRIPTION1;
import static org.cmdbuild.dao.beans.CMRelation.ATTR_DESCRIPTION2;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDCLASS1;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDCLASS2;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDOBJ1;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDOBJ2;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_STATUS;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_STATUS_A;
import static org.cmdbuild.dao.constants.SystemAttributes.DOMAIN_RESERVED_ATTRIBUTES;
import static org.cmdbuild.dao.constants.SystemAttributes.PROCESS_CLASS_RESERVED_ATTRIBUTES;
import static org.cmdbuild.dao.constants.SystemAttributes.SIMPLE_CLASS_RESERVED_ATTRIBUTES;
import static org.cmdbuild.dao.constants.SystemAttributes.STANDARD_CLASS_RESERVED_ATTRIBUTES;
import static org.cmdbuild.dao.core.q3.DaoService.ALL;
import static org.cmdbuild.dao.core.q3.DaoService.COUNT;
import static org.cmdbuild.dao.core.q3.DaoService.ROW_NUMBER;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.addSqlCastIfRequired;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName;
import org.cmdbuild.dao.entrytype.attributetype.ForeignKeyAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import org.cmdbuild.dao.orm.CardMapper;
import org.cmdbuild.data.filter.AttributeFilter;
import org.cmdbuild.data.filter.AttributeFilterCondition;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;
import org.cmdbuild.data.filter.CqlFilter;
import org.cmdbuild.data.filter.FulltextFilter;
import org.cmdbuild.data.filter.RelationFilter;
import org.cmdbuild.data.filter.RelationFilterCardInfo;
import org.cmdbuild.data.filter.RelationFilterRule;
import org.cmdbuild.data.filter.SorterElement;
import org.cmdbuild.ecql.EcqlService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.entrytype.EntryType;
import static org.cmdbuild.data.filter.utils.CmdbSorterUtils.toJsonString;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.quoteSqlIdentifier;
import org.cmdbuild.data.filter.CompositeFilter;
import org.cmdbuild.data.filter.beans.CmdbFilterImpl;
import org.cmdbuild.data.filter.beans.CqlFilterImpl;
import static org.cmdbuild.utils.lang.CmNullableUtils.getClassOfNullable;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDTENANT;
import org.cmdbuild.dao.core.q3.CompositeWhereOperator;
import org.cmdbuild.dao.driver.repository.ClasseReadonlyRepository;
import org.cmdbuild.dao.driver.repository.DomainRepository;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.FOREIGNKEY;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.LONG;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.LOOKUP;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.REFERENCE;
import org.cmdbuild.dao.function.StoredFunction;
import org.cmdbuild.dao.postgres.SqlType;
import org.cmdbuild.dao.postgres.SqlTypeName;
import static org.cmdbuild.dao.postgres.SqlTypeName._bytea;
import static org.cmdbuild.dao.postgres.SqlTypeName._int8;
import static org.cmdbuild.dao.postgres.SqlTypeName._varchar;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.systemToSqlExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.attributeTypeToSqlType;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.dao.driver.repository.FunctionRepository;
import static org.cmdbuild.dao.postgres.q3.QueryBuilderServiceImpl.ENABLE_EXPLICIT_EXPR_MARKER_PROCESSING;
import static org.cmdbuild.dao.postgres.q3.QueryBuilderServiceImpl.ENABLE_SMART_EXPR_ALIAS_PROCESSING;
import static org.cmdbuild.dao.postgres.q3.QueryBuilderServiceImpl.JOIN_ARG_2;
import static org.cmdbuild.dao.postgres.q3.QueryBuilderServiceImpl.JOIN_ID_DEFAULT;
import org.cmdbuild.dao.postgres.q3.QueryBuilderServiceImpl.JoinQueryArgs;
import org.cmdbuild.dao.postgres.q3.QueryBuilderServiceImpl.QueryBuilderImpl;
import static org.cmdbuild.dao.postgres.q3.QueryBuilderServiceImpl.REQUIRE_WITH_EXPR;
import static org.cmdbuild.dao.postgres.q3.QueryBuilderServiceImpl.SELECT_DISTINCT;
import static org.cmdbuild.dao.postgres.q3.QueryBuilderServiceImpl.SELECT_FROM_JOIN_ID;
import static org.cmdbuild.dao.postgres.q3.QueryBuilderServiceImpl.WHERE_ELEMENT_FOR_ROW_NUMBER;
import static org.cmdbuild.dao.postgres.q3.QueryMode.QM_COUNT;
import static org.cmdbuild.dao.postgres.q3.QueryMode.QM_DISTINCT;
import static org.cmdbuild.dao.postgres.q3.QueryMode.QM_GROUP;
import static org.cmdbuild.dao.postgres.q3.QueryMode.QM_ROWNUMBER;
import static org.cmdbuild.dao.postgres.q3.QueryMode.QM_SIMPLE;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.functionCallSqlExpr;
import org.cmdbuild.dao.postgres.utils.SqlQueryUtils;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.Q3_MASTER;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildCodeAttrName;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildDescAttrName;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildLookupCodeExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildLookupDescExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildReferenceCodeExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildReferenceDescExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildReferenceExistsExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.wrapExprWithBrackets;
import org.cmdbuild.data.filter.FunctionFilter;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.entryTypeToSqlExpr;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;

@Component
public class QueryBuilderHelperServiceImpl implements QueryBuilderHelperService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ClasseReadonlyRepository classeRepository;
    private final FunctionRepository functionRepository;
    private final DomainRepository domainRepository;
    private final PreparedQueryHelperService executorService;
    private final EcqlService ecqlService;
    private final Provider<OperationUserSupplier> operationUserSupplier;//TODO: refactor and remove this; user supplier should not be accessed from dao

    public QueryBuilderHelperServiceImpl(Provider<OperationUserSupplier> operationUserSupplier, PreparedQueryHelperService executorService, FunctionRepository functionRepository, ClasseReadonlyRepository classeRepository, DomainRepository domainRepository, EcqlService ecqlService) {
        this.classeRepository = checkNotNull(classeRepository);
        this.domainRepository = checkNotNull(domainRepository);
        this.ecqlService = checkNotNull(ecqlService);
        this.functionRepository = checkNotNull(functionRepository);
        this.operationUserSupplier = checkNotNull(operationUserSupplier);
        this.executorService = checkNotNull(executorService);
    }

    @Override
    public PreparedQuery buildQuery(QueryBuilderImpl source) {
        return new QueryBuilderProcessor(source).doBuild();
    }

    private class JoinQueryElement {

        private final String joinId;
        private final EntryType from;
        private final List<WhereElement> on = list();
        private final String alias;

        public JoinQueryElement(String joinId, EntryType from, String alias) {
            this.joinId = checkNotBlank(joinId);
            this.from = checkNotNull(from);
            this.alias = checkNotBlank(alias);
        }

    }

    private class QueryBuilderProcessor {

        private final AliasBuilder aliasBuilder = new AliasBuilder();
        private final SelectHolder select = new SelectHolder();
        private final List<JoinQueryElement> joinElements = list();
        private EntryType from;
        private final String fromAlias, rowNumberSubExprAlias = aliasBuilder.buildAlias("x");
        private final List<WhereElement> where = list();
        private final Long offset, limit;
        private final CmdbSorter sorter;
        private final Boolean activeCardsOnly, hasJoin, addJoinForRefCodeDescription;
        private final CardMapper cardMapper;
        private final Map<String, CqlQueryImpl> processedCqlFilters = map();
        private final QueryMode queryMode;

        private QueryBuilderProcessor(QueryBuilderImpl source) {
            from = source.from;
            offset = source.offset;
            limit = source.limit;
            sorter = source.sorter;
            activeCardsOnly = source.activeCardsOnly;
            cardMapper = source.cardMapper;
            addJoinForRefCodeDescription = source.joinForRefCodeDescription;

            boolean count = source.count,
                    selectRowNumber = source.selectRowNumber,
                    selectDistinctValue = source.select.stream().anyMatch(SelectArg::isDistinct);

            checkArgument(!((count && selectRowNumber) || (selectDistinctValue && selectRowNumber)), "cannot mix `count`, `select row number` and `select distinct` within the same query");

            if (count && selectDistinctValue) {
                queryMode = QM_GROUP;
            } else if (count) {
                queryMode = QM_COUNT;
            } else if (selectRowNumber) {
                queryMode = QM_ROWNUMBER;
            } else if (selectDistinctValue) {
                queryMode = QM_DISTINCT;
            } else {
                queryMode = QM_SIMPLE;
            }

            List<CmdbFilter> filters = source.filters;

            filters = preProcessCqlFilters(filters);

            checkNotNull(from, "from param is null");
            fromAlias = aliasBuilder.buildAlias(from.getName());

            List<SelectArg> selectArgs = prepareSelectArgs(from, source.select, true);
            selectArgs = selectAttrsForSorter(selectArgs);

            hasJoin = !source.joinArgs.isEmpty() || selectArgs.stream().map(SelectArg::getName).map(from::getAttributeOrNull).filter(Objects::nonNull).anyMatch(a -> a.isOfType(REFERENCE, FOREIGNKEY, LOOKUP));

            processSelectArgs(selectArgs);
            if (isQueryMode(QM_COUNT)) {
                if (select.isEmpty()) {
                    selectAttr(ATTR_ID);
                }
            }
            selectExtendedAttrs();

            processJoinArgs(source.joinArgs);

            List<WhereArg> whereArgs = preProcessWhereArgs(source.where);
            processWhereArgs(whereArgs);
            processFilters(filters);//TODO check 
            addStandardWhereArgs();
        }

        private List<SelectArg> prepareSelectArgs(EntryType entryType, List<SelectArg> selectArgs, boolean includeReservedAttrs) {
            Collection<String> reservedAttrs = getReservedAttrs(entryType);
            selectArgs = list(selectArgs);
            Set<String> explicitSelectArgs = selectArgs.stream().map(SelectArg::getName).collect(toSet());
            boolean selectAll = selectArgs.removeIf((s) -> equal(s.getName(), ALL));
            if (selectAll) {
                includeReservedAttrs = true;
            }
            if (!isQueryMode(QM_COUNT)) {
                if (includeReservedAttrs) {
                    reservedAttrs.stream().filter(not(explicitSelectArgs::contains)).map(a -> new SelectArg(a, quoteSqlIdentifier(a))).forEach(selectArgs::add);
                }
                if (selectAll) {
                    if (entryType.isDomain()) {//TODO improve this
                        Domain domain = (Domain) from;
                        selectExpr(ATTR_DESCRIPTION1, buildReferenceDescExpr(domain.getSourceClass(), fromAlias, quoteSqlIdentifier(ATTR_IDOBJ1)));
                        selectExpr(ATTR_DESCRIPTION2, buildReferenceDescExpr(domain.getTargetClass(), fromAlias, quoteSqlIdentifier(ATTR_IDOBJ2)));
                        selectExpr(ATTR_CODE1, buildReferenceCodeExpr(domain.getSourceClass(), fromAlias, quoteSqlIdentifier(ATTR_IDOBJ1)));
                        selectExpr(ATTR_CODE2, buildReferenceCodeExpr(domain.getTargetClass(), fromAlias, quoteSqlIdentifier(ATTR_IDOBJ2)));
                        selectExpr(ATTR_CANREAD1, buildReferenceExistsExpr(domain.getSourceClass(), fromAlias, quoteSqlIdentifier(ATTR_IDOBJ1)));
                        selectExpr(ATTR_CANREAD2, buildReferenceExistsExpr(domain.getTargetClass(), fromAlias, quoteSqlIdentifier(ATTR_IDOBJ2)));
                    }
                }
            }
            if (selectAll) {
                entryType.getCoreAttributes().stream().filter(Attribute::isActive).map(Attribute::getName).filter(not(reservedAttrs::contains)).filter(not(explicitSelectArgs::contains)).sorted(Ordering.natural()).map(a -> new SelectArg(a, quoteSqlIdentifier(a))).forEach(selectArgs::add);
            }
            return selectArgs;
        }

        private boolean isQueryMode(QueryMode mode) {
            return equal(this.queryMode, mode);
        }

        private List<CmdbFilter> preProcessCqlFilters(List<CmdbFilter> filters) {
            filters = filters.stream().map(this::preProcessCqlFilter).collect(toImmutableList());
            List<Classe> fromsFromCql = processedCqlFilters.values().stream().map((cql) -> {
                ClassDeclarationImpl mainClass = cql.getFrom().mainClass();
                Classe cqlFrom = mainClass.getId() > 0 ? classeRepository.getClasse(mainClass.getId()) : classeRepository.getClasse(mainClass.getName());
                if (!equal(cqlFrom, from)) {
                    if (from == null || (from instanceof Classe && ((Classe) from).isAncestorOf(cqlFrom))) {
                        return cqlFrom;
                    } else if (from instanceof Classe && cqlFrom.isAncestorOf((Classe) from)) {
                        return null;
                    } else {
                        throw new IllegalArgumentException(format("invalid cql from = %s (it is not consistent with this class = %s)", cqlFrom, from));
                    }
                } else {
                    return null;
                }
            }).filter(Objects::nonNull).distinct().collect(toList());
            checkArgument(fromsFromCql.size() <= 1, "error processing cql filter: multiple cql filters with incompatible FROM class expr = %s", fromsFromCql);
            if (!fromsFromCql.isEmpty()) {
                from = getOnlyElement(fromsFromCql);
            }
            return filters;
        }

        private CmdbFilter preProcessCqlFilter(CmdbFilter filter) {
            if (filter.hasCompositeFilter()) {
                filter = CmdbFilterImpl.copyOf(filter).withCompositeFilter(filter.getCompositeFilter().mapElements(this::preProcessCqlFilter)).build();
            }
            if (filter.hasEcqlFilter()) {
                String cqlExpr = ecqlService.prepareCqlExpression(filter.getEcqlFilter().getEcqlId(), filter.getEcqlFilter().getJsContext());
                filter = CmdbFilterImpl.copyOf(filter).withEcqlFilter(null).withCqlFilter(new CqlFilterImpl(cqlExpr)).build();
            }
            if (filter.hasCqlFilter()) {
                processedCqlFilters.put(cqlFilterKey(filter.getCqlFilter()), compileCql(filter.getCqlFilter()));
            }
            return filter;
        }

        private void processSelectArgs(List<SelectArg> list) {
            logger.trace("process select args");
            list.forEach(this::processSelectArg);
        }

        private void processSelectArg(SelectArg arg) {
            logger.trace("process select arg = {}", arg);
            if (arg.getExpr() instanceof String) {
                String expr = (String) arg.getExpr();
                if (hasJoin) {
                    if (toBooleanOrDefault(arg.getMeta().get(ENABLE_SMART_EXPR_ALIAS_PROCESSING), false)) {
                        logger.trace("executing smart alias processing for expr =< {} >", expr);
                        //smart alias processing of expr; note: kinda weak
                        Matcher matcher = Pattern.compile("\"([^\"]+)\"").matcher(expr);
                        StringBuffer stringBuffer = new StringBuffer();
                        while (matcher.find()) {
                            matcher.appendReplacement(stringBuffer, Matcher.quoteReplacement(getAliasForJoinElement(arg.getSelectForJoin()) + ".") + "$0");
                        }
                        matcher.appendTail(stringBuffer);
                        expr = stringBuffer.toString();
                        logger.trace("executed smart alias processing for expr, output =< {} >", expr);
                    } else if (toBooleanOrDefault(arg.getMeta().get(ENABLE_EXPLICIT_EXPR_MARKER_PROCESSING), false)) {
                        logger.trace("executing explicit alias processing for expr =< {} >", expr);
                        expr = expr.replace(Q3_MASTER, fromAlias);
                        logger.trace("executed explicit alias processing for expr, output =< {} >", expr);
                    } else {
                        expr = format("%s.%s", getAliasForJoinElement(arg.getSelectForJoin()), expr);
                    }
                }
                select.add(SelectElement.builder()
                        .withAlias(aliasBuilder.buildAlias(arg.getName()))
                        .withExpr(expr)
                        .withMeta(map(arg.getMeta()).with(SELECT_FROM_JOIN_ID, arg.getSelectForJoin()))
                        .withName(arg.getName())
                        .withParams(arg.getParams())
                        .build());
            } else if (arg.getExpr() instanceof CmdbFilter) {
                logger.trace("processing select arg of type filter = {}", arg.getExpr());
                List<WhereElement> wheres = buildWheresForFilter((CmdbFilter) arg.getExpr());
                String expr;
                if (wheres.isEmpty()) {
                    expr = "TRUE";
                } else {
                    WhereElement whereElement = compactWhereElements(wheres);
                    expr = wrapExprWithBrackets(whereElement.getExpr());
                }
                logger.trace("using filter expr = {}", expr);
                select.add(SelectElement.builder()
                        .withName(arg.getName())
                        .withExpr(expr)
                        .withAlias(aliasBuilder.buildAlias(arg.getName()))
                        //							.withParams(whereElement.getParams())
                        .build());
            } else {
                throw new DaoException("unsupported select arg expr type = %s (%s)", arg.getExpr(), getClassOfNullable(arg.getExpr()).getName());
            }
        }

        private Collection<String> getReservedAttrs(EntryType entryType) {
            Collection<String> reservedAttrs;
            if (entryType.isClasse()) {
                if (((Classe) entryType).isStandardClass()) {
                    if (((Classe) entryType).isProcess()) {
                        reservedAttrs = PROCESS_CLASS_RESERVED_ATTRIBUTES;//TODO: fix this, standardize and move ws customization back to flow/commons
                    } else {
                        reservedAttrs = STANDARD_CLASS_RESERVED_ATTRIBUTES;
                    }
                } else {
                    reservedAttrs = SIMPLE_CLASS_RESERVED_ATTRIBUTES;
                }
                if (((Classe) entryType).hasMultitenantEnabled()) {
                    reservedAttrs = set(reservedAttrs).with(ATTR_IDTENANT);
                }
            } else if (entryType.isDomain()) {
                reservedAttrs = DOMAIN_RESERVED_ATTRIBUTES;
            } else if (entryType.isFunction()) {
                reservedAttrs = emptyList();
            } else {
                throw unsupported("unsuppoted 'from' type = %s", from);
            }
            return reservedAttrs;
        }

        private SelectElement selectAttr(String attr) {
            return selectAttr(attr, JOIN_ID_DEFAULT);
        }

        private SelectElement selectAttr(String attr, String joinId) {
            String expr = quoteSqlIdentifier(attr);
            if (hasJoin) {
                expr = format("%s.%s", getAliasForJoinElement(joinId), expr);//TODO check this (?)
            }
            SelectElement element = SelectElement.build(attr, expr, aliasBuilder.buildAlias(attr));
            select.add(element);
            return element;
        }

        private void selectExpr(String name, String expr) {
            select.add(SelectElement.build(name, expr, aliasBuilder.buildAlias(name)));
        }

        private List<SelectArg> selectAttrsForSorter(List<SelectArg> args) {
            List<SelectArg> res = list(args);
            sorter.getElements().forEach((s) -> {
                if (!res.stream().anyMatch((a) -> a.getName().equals(s.getProperty()))) {
                    res.add(new SelectArg(s.getProperty(), quoteSqlIdentifier(s.getProperty())));
                }
            });
            return res;
        }

        private void selectExtendedAttrs() {
            if (addJoinForRefCodeDescription) {
                list(select.getElements()).stream().filter(a -> from.hasAttribute(a.getName())).forEach((a) -> selectExtendedAttrs(a, from.getAttribute(a.getName())));
            }
        }

        private void selectExtendedAttrs(SelectElement select, Attribute a) {
            switch (a.getType().getName()) {
                case REFERENCE:
                case FOREIGNKEY:
                    Classe targetClass = getTargetClassForAttribute(a);
                    if (targetClass.hasAttribute(ATTR_DESCRIPTION) || targetClass.hasAttribute(ATTR_CODE)) {
                        JoinQueryElement join = new JoinQueryElement(randomId(), targetClass, aliasBuilder.buildAlias(a.getName()));
                        join.on.add(new WhereElement(format("%s.\"Id\" = %s.%s", join.alias, getAliasForJoinElement(JOIN_ID_DEFAULT), quoteSqlIdentifier(a.getName()))));
                        if (targetClass.hasHistory() && activeCardsOnly) {
                            join.on.add(new WhereElement(format("%s.\"Status\" = 'A'", join.alias)));
                        }
                        joinElements.add(join);
                        if (targetClass.hasAttribute(ATTR_CODE)) {
                            selectExtendedAttr(select, buildCodeAttrName(a.getName()), format("%s.\"Code\"", join.alias));
                        }
                        if (targetClass.hasAttribute(ATTR_DESCRIPTION)) {
                            selectExtendedAttr(select, buildDescAttrName(a.getName()), format("%s.\"Description\"", join.alias));
                        }
                    }
                    break;
                case LOOKUP:
                    JoinQueryElement join = new JoinQueryElement(randomId(), classeRepository.getClasse("LookUp"), aliasBuilder.buildAlias(a.getName()));
                    join.on.add(new WhereElement(format("%s.\"Id\" = %s.%s", join.alias, getAliasForJoinElement(JOIN_ID_DEFAULT), quoteSqlIdentifier(a.getName()))));
                    if (activeCardsOnly) {
                        join.on.add(new WhereElement(format("%s.\"Status\" = 'A'", join.alias)));
                    }
                    joinElements.add(join);
                    selectExtendedAttr(select, buildCodeAttrName(a.getName()), format("%s.\"Code\"", join.alias));
                    selectExtendedAttr(select, buildDescAttrName(a.getName()), format("%s.\"Description\"", join.alias));
                    break;
            }
        }

        private void selectExtendedAttr(SelectElement parentSelect, String name, String expr) {
            SelectElement selectElement = buildExtendedAttr(name, expr);
            if (parentSelect.isDistinct()) {
                selectElement = selectElement.withMeta(SELECT_DISTINCT, true);
            }
            select.add(selectElement);
        }

        private SelectElement buildExtendedAttr(String name, String expr) {
            return SelectElement.build(name, expr, aliasBuilder.buildAlias(name));
        }

        private List<WhereArg> preProcessWhereArgs(List<WhereArg> list) {
            return list.stream().map((w) -> {
                switch (w.getType()) {
                    case WA_SIMPLE:
                        if (w.getExpr().contains("unnest")) {//TODO improve this
                            w = new WhereArg(w.getExpr(), w.getOperator(), map(w.getMeta()).with(REQUIRE_WITH_EXPR, true), w.getParams());
                        }
                        return w;
                    case WA_COMPOSITE:
                        List<WhereArg> inners = preProcessWhereArgs(w.getInners());
                        if (inners.stream().anyMatch(WhereArg::requireWithExpr)) {
                            inners = inners.stream().map(i -> i.withMeta(map(REQUIRE_WITH_EXPR, true))).collect(toList());
                        }
                        return new WhereArg(w.getOperator(), inners, w.getMeta());
                    default:
                        throw new IllegalArgumentException("unsupported where arg type = " + w.getType());
                }
            }).collect(toList());
        }

        private void processWhereArgs(List<WhereArg> list) {
            list.stream().map(this::whereArgToWhereElement).forEach(where::add);
        }

        private void addStandardWhereArgs() {
            if (activeCardsOnly && from.hasHistory()) {
                where.add(whereArgToWhereElement(new WhereArg(ATTR_STATUS, EQ, emptyMap(), singletonList(ATTR_STATUS_A))));
            }
        }

        private WhereElement whereArgToWhereElement(WhereArg arg) {
            switch (arg.getType()) {
                case WA_SIMPLE:
                    if (arg.getOperator() instanceof WhereOperator) {
                        SelectElement attr = getAttrWithAliasForWhere(arg.getExpr(), arg.getSelectFromJoin());

                        Function<SelectElement, String> attrExprExtractor;

                        if (toBooleanOrDefault(arg.getMeta().get(REQUIRE_WITH_EXPR), false)) {
                            attrExprExtractor = SelectElement::getAlias;
                        } else if (toBooleanOrDefault(arg.getMeta().get(WHERE_ELEMENT_FOR_ROW_NUMBER), false)) {
                            attrExprExtractor = (a) -> format("%s.%s", rowNumberSubExprAlias, a.getAlias());//TODO check this when enableWithSubexpr is true
                        } else {
                            attrExprExtractor = SelectElement::getExpr;
                        }

                        return buildWhere(attr, attrExprExtractor, (WhereOperator) arg.getOperator(), arg.getMeta(), arg.getParams());
                    } else if (arg.getOperator() instanceof SpecialOperators) {
                        switch ((SpecialOperators) arg.getOperator()) {
                            case EXPR:
                                try {
                                    String expr = arg.getExpr();
                                    Matcher matcher = Pattern.compile(Pattern.quote("?") + "+").matcher(expr);
                                    Iterator params = arg.getParams().iterator();
                                    StringBuffer sb = new StringBuffer();
                                    while (matcher.find()) {
                                        if (equal(matcher.group(), "?")) {
                                            matcher.appendReplacement(sb, Matcher.quoteReplacement(systemToSqlExpr(params.next())));
                                        } else if (equal(matcher.group(), "??")) {
                                            matcher.appendReplacement(sb, Matcher.quoteReplacement("?"));
                                        } else {
                                            throw new DaoException("invalid part =< %s > found in expr =< %s >", matcher.group(), expr);
                                        }
                                    }
                                    matcher.appendTail(sb);
                                    checkArgument(!params.hasNext(), "found more params than replacement expressions ('?')");
                                    expr = sb.toString();
                                    return new WhereElement(expr, arg.getMeta());
                                } catch (Exception ex) {
                                    throw new DaoException(ex, "error processing sql expr = %s with params = %s", arg.getExpr(), arg.getParams());
                                }
                            default:
                                throw new DaoException("unsupported special operator = %s", arg.getOperator());
                        }
                    } else {
                        throw new DaoException("unsupported operator = %s", arg.getOperator());
                    }
                case WA_COMPOSITE:
                    List<WhereElement> inners = arg.getInners().stream().map(this::whereArgToWhereElement).collect(toList());
                    String expr;
                    switch (((CompositeWhereOperator) arg.getOperator())) {
                        case AND:
                            expr = inners.stream().map(WhereElement::getExpr).collect(joining(" AND "));
                            break;
                        case OR:
                            expr = wrapExprWithBrackets(inners.stream().map(WhereElement::getExpr).collect(joining(" OR ")));
                            break;
                        case NOT:
                            expr = wrapExprWithBrackets(format("NOT %s", getOnlyElement(inners).getExpr()));
                            break;
                        default:
                            throw new DaoException("unsupported composite operator = %s", arg.getOperator());
                    }
                    return new WhereElement(expr, arg.getMeta());
                default:
                    throw new IllegalArgumentException("unsupported where arg type = " + arg.getType());
            }

        }

        private void processFilters(List<CmdbFilter> list) {
            list.forEach((filter) -> {
                buildWheresForFilter(filter).forEach(where::add);
            });
        }

        private SelectElement getAttrWithAliasForWhere(String attr, String joindId) {
            SelectElement attrWithAlias = select.stream().filter((a) -> a.getName().equals(attr) && a.getSelectFromJoin().equals(joindId)).collect(toOptional()).orElse(null);
            if (attrWithAlias == null) {
                String expr = attrNameToSqlIdentifierExpr(attr, joindId);
                attrWithAlias = SelectElement.build(attr, expr, expr);
            }
            if (from.hasAttribute(attr) && from.getAttribute(attr).isOfType(AttributeTypeName.REGCLASS)) {
                attrWithAlias = SelectElement.build(attr, format("_cm3_utils_regclass_to_name(%s)", quoteSqlIdentifier(attr)), quoteSqlIdentifier(attr));//TODO improve this
            }
            return attrWithAlias;
        }

        private WhereElement buildWhere(SelectElement attr, Function<SelectElement, String> attrExprExtractor, WhereOperator operator, Map<String, Object> meta, List<Object> params) {
            String attrExpr = attrExprExtractor.apply(attr);
            Supplier<String> onlyElementExprSupplier = () -> systemToSqlExpr(getOnlyElement(params));
            if (isNotBlank(meta.get(JOIN_ARG_2))) {
                String alias = getAliasForJoinElement(toStringNotBlank(meta.get(JOIN_ARG_2)));
                onlyElementExprSupplier = () -> format("%s.%s", alias, getOnlyElement(params));
            }
            switch (operator) {
                case ISNULL:
                    checkArgument(params.isEmpty());
                    return new WhereElement(format("%s IS NULL", attrExpr), meta);
                case ISNOTNULL:
                    checkArgument(params.isEmpty());
                    return new WhereElement(format("%s IS NOT NULL", attrExpr), meta);
                case EQ:
                    return new WhereElement(format("%s = %s", attrExpr, onlyElementExprSupplier.get(), attr.getSqlTypeHint()), meta);
                case EQ_CASE_INSENSITIVE:
                    return new WhereElement(format("LOWER(%s) = LOWER(%s)", attrExpr, onlyElementExprSupplier.get(), attr.getSqlTypeHint()), meta);
                case NOTEQ:
                    return new WhereElement(format("%s <> %s", attrExpr, onlyElementExprSupplier.get(), attr.getSqlTypeHint()), meta);
                case LT:
                    return new WhereElement(format("%s < %s", attrExpr, onlyElementExprSupplier.get(), attr.getSqlTypeHint()), meta);
                case GT:
                    return new WhereElement(format("%s > %s", attrExpr, onlyElementExprSupplier.get(), attr.getSqlTypeHint()), meta);
                case IN:
                    return new WhereElement(format("%s = ANY (%s)", attrExpr, systemToSqlExpr(set((Iterable) getOnlyElement(params))), attr.getSqlTypeHint()), meta);
                case INTERSECTS:
                    return new WhereElement(format("%s && %s", attrExpr, systemToSqlExpr(set((Iterable) getOnlyElement(params))), attr.getSqlTypeHint()), meta);
                case LIKE:
                    return new WhereElement(format("%s LIKE %s", attrExpr, onlyElementExprSupplier.get(), attr.getSqlTypeHint()), meta);
                case MATCHES_REGEXP:
                    return new WhereElement(format("%s ~ %s", attrExpr, onlyElementExprSupplier.get(), attr.getSqlTypeHint()), meta);
                case NOT_MATCHES_REGEXP:
                    return new WhereElement(format("%s !~ %s", attrExpr, onlyElementExprSupplier.get(), attr.getSqlTypeHint()), meta);
                default:
                    throw new UnsupportedOperationException("unsupported operator = " + operator);
            }
        }

        private void processJoinArgs(List<JoinQueryArgs> joinArgs) {
            joinArgs.forEach(this::processJoinArg);
        }

        private void processJoinArg(JoinQueryArgs arg) {
            JoinQueryElement element = new JoinQueryElement(arg.joinId, arg.from, aliasBuilder.buildAlias(arg.from.getName()));
            joinElements.add(element);
            List<SelectArg> selectArgs = prepareSelectArgs(arg.from, arg.select, false);
            selectArgs.stream().map(sa -> sa.withMeta(SELECT_FROM_JOIN_ID, element.joinId)).forEach(this::processSelectArg);
            arg.onExprs.stream().map(this::whereArgToWhereElement).forEach(element.on::add);
            if (element.from.hasHistory() && activeCardsOnly) {
                element.on.add(new WhereElement(format("%s.\"Status\" = 'A'", element.alias)));
            }
            arg.where.stream().map(wa -> wa.withMeta(SELECT_FROM_JOIN_ID, arg.joinId)).map(this::whereArgToWhereElement).forEach(where::add);//TODO check this
            checkArgument(arg.filters.isEmpty(), "join filters are not supported yet");//TODO
        }

        private String getAliasForJoinElement(String joinId) {
            if (equal(joinId, JOIN_ID_DEFAULT)) {
                return fromAlias;
            } else {
                return joinElements.stream().filter(j -> equal(j.joinId, joinId)).map(j -> j.alias).collect(onlyElement("join element not found for joinId =< %s >", joinId));
            }
        }

        public PreparedQuery doBuild() {

            checkNotNull(from);

            String fromExpr = entryTypeToSqlExpr(from);

            List<String> selectExprs = list();

            select.stream().map((a) -> {
                String expr = a.getExpr();
                Attribute attr = from.getAttributeOrNull(a.getName());
                if (attr != null) {
                    expr = addSqlCastIfRequired(attr.getType(), expr);
                }
                return format("%s %s", expr, a.getAlias());
            }).forEach(selectExprs::add);

            String query = format("SELECT %s FROM %s %s", Joiner.on(", ").join(selectExprs), fromExpr, fromAlias);

            query += joinElements.stream().map(j -> {
                String joinQuery = format(" LEFT JOIN %s %s", entryTypeToSqlExpr(j.from), j.alias);
                if (!j.on.isEmpty()) {
                    joinQuery += format(" ON %s", j.on.stream().map(WhereElement::getExpr).distinct().collect(joining(" AND ")));
                }
                return joinQuery;
            }).collect(joining());

            List<WhereElement> whereElementsRequiringWithExpr = where.stream().filter(WhereElement::requireWithExpr).collect(toList());
            List<WhereElement> whereElementsNotForRowNumber = where.stream().filter(not(WhereElement::requireWithExpr)).filter(not(WhereElement::forRowNumber)).collect(toList());
            List<WhereElement> whereElementsFowRowNumber = where.stream().filter(not(WhereElement::requireWithExpr)).filter(WhereElement::forRowNumber).collect(toList());

            query += whereElementsToWhereExprBlankIfEmpty(whereElementsNotForRowNumber);

            if (!whereElementsRequiringWithExpr.isEmpty()) {
                String alias = aliasBuilder.buildAlias("subquery");
                query = format("WITH %s AS ( %s ) SELECT * FROM %s", alias, query, alias);
                query += whereElementsToWhereExprBlankIfEmpty(whereElementsRequiringWithExpr);
            }

            if (isQueryMode(QM_DISTINCT)) {
                String selectDistinctAlias = aliasBuilder.buildAlias("y"),
                        selectDistinctExpr = select.stream().filter(SelectElement::isDistinct).map(SelectElement::getAlias).collect(joining(", "));
                query = format("SELECT DISTINCT %s FROM ( %s ) %s", selectDistinctExpr, query, selectDistinctAlias);
            } else if (isQueryMode(QM_GROUP)) {
                SelectElement selectCount = buildCountSelectElement().withMeta(SELECT_DISTINCT, true);
                String selectDistinctAlias = aliasBuilder.buildAlias("y"),
                        selectDistinctExpr = select.stream().filter(SelectElement::isDistinct).map(SelectElement::getAlias).collect(joining(", "));
                select.add(selectCount);
                query = format("SELECT %s, %s %s FROM ( %s ) %s GROUP BY %s", selectDistinctExpr, selectCount.getExpr(), selectCount.getAlias(), query, selectDistinctAlias, selectDistinctExpr);
            }

            if (!sorter.isNoop()) {
                try {
                    List<String> sortExprList = sorter.getElements().stream().map((s) -> {
                        String name = s.getProperty();
                        String direction = checkNotNull(ImmutableMap.of(
                                SorterElement.SorterElementDirection.ASC, "ASC",
                                SorterElement.SorterElementDirection.DESC, "DESC"
                        ).get(s.getDirection()));
                        SelectElement attr = getAttrFromSelectByName(name);
                        if (from.hasAttribute(name)) {
                            Attribute a = from.getAttribute(name);
                            switch (a.getType().getName()) {
                                case REFERENCE:
                                case FOREIGNKEY:
                                    attr = getAttrFromSelectByName(buildDescAttrName(name), () -> buildExtendedAttr(buildDescAttrName(name), buildDescAttrExprForReference(a)));
                                    break;
                                case LOOKUP:
                                    attr = getAttrFromSelectByName(buildDescAttrName(name), () -> buildExtendedAttr(buildDescAttrName(name), buildDescAttrExprForLookup(a)));
                                    break;
                            }
                        }
                        return format("%s %s", attr.getAlias(), direction);
                    }).collect(toList());
                    query += " ORDER BY " + Joiner.on(", ").join(sortExprList);
                } catch (Exception ex) {
                    throw new DaoException(ex, "error processing query ordering = %s", toJsonString(sorter));
                }
            }

            String rowNumberAlias = null;
            if (isQueryMode(QM_ROWNUMBER)) {
                rowNumberAlias = aliasBuilder.buildAlias(ROW_NUMBER);
                query = format("SELECT *, ROW_NUMBER() OVER () AS %s FROM ( %s ) _rownumber_subquery", rowNumberAlias, query);
                query = format("SELECT * FROM ( %s ) %s %s", query, rowNumberSubExprAlias, whereElementsToWhereExprBlankIfEmpty(whereElementsFowRowNumber));
            }

            if (hasOffset(offset)) {
                query += format(" OFFSET %s", offset);
            }
            if (hasLimit(limit)) {
                query += format(" LIMIT %s", limit);
            }

            List<SelectElement> preparedQuerySelect;
            switch (queryMode) {
                case QM_COUNT:
                    SelectElement countAttr = buildCountSelectElement();
                    preparedQuerySelect = singletonList(countAttr);
                    query = format("SELECT %s %s FROM ( %s ) %s", countAttr.getExpr(), countAttr.getAlias(), query, aliasBuilder.buildAlias("subquery"));
                    break;
                case QM_ROWNUMBER:
                    preparedQuerySelect = list(select.getElements()).with(SelectElement.build(ROW_NUMBER, ROW_NUMBER, checkNotBlank(rowNumberAlias)));
                    break;
                case QM_DISTINCT:
                    preparedQuerySelect = select.stream().filter(SelectElement::isDistinct).collect(toList());
                    break;
                case QM_GROUP:
                    preparedQuerySelect = select.stream().filter(SelectElement::isDistinct).collect(toList());
                    break;
                case QM_SIMPLE:
                default:
                    preparedQuerySelect = select.getElements();
            }

            return executorService.prepareQuery(query, preparedQuerySelect, where, from, cardMapper);
        }

        private SelectElement buildCountSelectElement() {
            return SelectElement.build(COUNT, "COUNT(*)", aliasBuilder.buildAlias(COUNT));
        }

        private String whereElementsToWhereExprBlankIfEmpty(List<WhereElement> whereElements) {
            if (whereElements.isEmpty()) {
                return "";
            } else {
                return " WHERE " + whereElements.stream().map(WhereElement::getExpr).distinct().collect(joining(" AND "));
            }
        }

        private List<WhereElement> buildWheresForFilter(CmdbFilter filter) {
            List<WhereElement> list = list();
            if (filter.hasAttributeFilter()) {
                list.add(buildFilterExpr(filter.getAttributeFilter()));
            }
            if (filter.hasRelationFilter()) {
                list.add(buildFilterWhereExpr(filter.getRelationFilter()));
            }
            if (filter.hasCqlFilter()) {
                CqlQueryImpl cql = compileCql(filter.getCqlFilter());
                Optional.ofNullable(new CqlExprBuilder(cql.getWhere()).buildWhereExprOrNull()).ifPresent(list::add);
            }
            if (filter.hasFulltextFilter()) {
                list.add(buildFulltextFilterWhere(filter.getFulltextFilter()));
            }
            if (filter.hasCompositeFilter()) {
                list.addAll(buildWheresForCompositeFilter(filter.getCompositeFilter()));
            }
            if (filter.hasFunctionFilter()) {
                list.addAll(buildWheresForFunctionFilter(filter.getFunctionFilter()));
            }
            return list;
        }

        private List<WhereElement> buildWheresForFunctionFilter(FunctionFilter functionFilter) {
            OperationUser user = operationUserSupplier.get().getUser();//TODO this is not gread (user should not be accessed from here)
            Long userId = user.getId(),
                    groupId = user.hasDefaultGroup() ? user.getDefaultGroup().getId() : null;
            String className = from == null ? null : from.getName();
            return functionFilter.getFunctions().stream().map(f -> {
                String functionName = f.getName();
                logger.debug("processing function filter with name =< {} >", functionName);
                StoredFunction storedFunction = functionRepository.getFunctionByName(functionName);
                logger.debug("processing function filter with function = {}", storedFunction);
                return new WhereElement(format("%s IN (SELECT %s)", attrNameToSqlIdentifierExpr(ATTR_ID), functionCallSqlExpr(storedFunction.getName(), userId, groupId, className)));//TODO auto set filter function params, improve filter function params
            }).collect(toList());
        }

        private List<WhereElement> buildWheresForCompositeFilter(CompositeFilter compositeFilter) {
            switch (compositeFilter.getMode()) {
                case CFM_AND:
                    return compositeFilter.getElements().stream().map(this::buildWheresForFilter).flatMap(List::stream).collect(toList());
                case CFM_OR:
                    return singletonList(compactWhereElements(compositeFilter.getElements().stream().map(this::buildWheresForFilter).flatMap(List::stream).collect(toList()), "OR"));
                case CFM_NOT:
                    WhereElement element = compactWhereElements(buildWheresForFilter(compositeFilter.getElement()));
//					return singletonList(new WhereElement(format("NOT %s", element.getExpr()), element.getParams()));
                    return singletonList(new WhereElement(format("NOT %s", element.getExpr())));
                default:
                    throw unsupported("unsupported composite filter mode = %s", compositeFilter.getMode());
            }
        }

        private WhereElement compactWhereElements(List<WhereElement> list) {
            return compactWhereElements(list, "AND");
        }

        private WhereElement compactWhereElements(List<WhereElement> list, String operator) {
            checkArgument(!list.isEmpty(), "unable to compact empty where element list");
            if (list.size() == 1) {
                return getOnlyElement(list);
            } else {
                return new WhereElement(wrapExprWithBrackets(list.stream().map(WhereElement::getExpr).map(SqlQueryUtils::wrapExprWithBrackets).collect(joining(" " + operator + " "))));
//						list.stream().map(WhereElement::getParams).flatMap(List::stream).collect(toList()));
            }
        }

        private boolean selectHasAttrWithName(String name) {
            return select.stream().map(SelectElement::getName).anyMatch(equalTo(name));
        }

        private SelectElement getAttrFromSelectByName(String name) {
            try {
                return getOptionalAttrFromSelectByName(name).get();
            } catch (Exception ex) {
                throw new DaoException(ex, "attr not found in select for name = %s", name);
            }
        }

        private SelectElement getAttrFromSelectByName(String name, Supplier<SelectElement> supplierIfNull) {
            return getOptionalAttrFromSelectByName(name).orElseGet(supplierIfNull);
        }

        private Optional<SelectElement> getOptionalAttrFromSelectByName(String name) {
            checkNotBlank(name);
            return select.stream().filter((a) -> a.getName().equals(name)).collect(toOptional());
        }

        private WhereElement buildFulltextFilterWhere(FulltextFilter fulltextFilter) {
            return buildFulltextFilterWhere(fulltextFilter.getQuery());
        }

        private WhereElement buildFulltextFilterWhere(String query) {
            List<String> parts = list();
            String queryVal = format("%%%s%%", checkNotBlank(query));

            from.getServiceAttributes().stream().map((a) -> {
                switch (a.getType().getName()) {
                    case STRING:
                    case TEXT:
                        return format("%s ILIKE %s", attrNameToSqlIdentifierExpr(a.getName()), systemToSqlExpr(queryVal));
                    case DECIMAL:
                    case INTEGER:
                    case LONG:
                    case DOUBLE:
                        return format("%s::varchar ILIKE %s", attrNameToSqlIdentifierExpr(a.getName()), systemToSqlExpr(queryVal));
                    case REFERENCE:
                    case FOREIGNKEY:
                        return format("%s ILIKE %s", buildDescAttrExprForReference(a), systemToSqlExpr(queryVal));//TODO use join (?)
                    case LOOKUP:
                        return format("%s ILIKE %s", buildDescAttrExprForLookup(a), systemToSqlExpr(queryVal));//TODO use join (?)
                    default:
                        return null;
                }
            }).filter(notNull()).forEach(parts::add);

            String thisExpr = wrapExprWithBrackets(Joiner.on(" OR ").join(parts));
            return new WhereElement(thisExpr);
        }

        private String attrNameToSqlIdentifierExpr(String name) {
            return attrNameToSqlIdentifierExpr(name, JOIN_ID_DEFAULT);
        }

        private String attrNameToSqlIdentifierExpr(String name, String joinId) {
            String expr = quoteSqlIdentifier(name);
            if (hasJoin) {
                expr = format("%s.%s", getAliasForJoinElement(joinId), expr);
            }
            return expr;
        }

        private class CqlExprBuilder {

            private final WhereImpl cqlWhere;

            public CqlExprBuilder(WhereImpl cqlWhere) {
                this.cqlWhere = checkNotNull(cqlWhere);
            }

            public @Nullable
            WhereElement buildWhereExprOrNull() {
                String whereExpr = buildWhereExprOrNull(cqlWhere);
                if (isBlank(whereExpr)) {
                    return null;
                } else {
                    return new WhereElement(whereExpr);
                }
            }

            private @Nullable
            String buildWhereExprOrNull(org.cmdbuild.cql.compiler.where.WhereElement whereElement) {
                if (whereElement instanceof FieldImpl) {
                    return buildWhereExpr((FieldImpl) whereElement);
                } else if (whereElement instanceof GroupImpl || whereElement instanceof WhereImpl) {
                    return whereElement.getElements().stream().map(this::buildWhereExprOrNull).collect(joining(" AND "));
                } else {
                    throw unsupported("unsupported cql where element = %s", whereElement);
                }
            }

            private String buildWhereExpr(FieldImpl field) {
                String name = field.getId().getId();
                String attrExpr;
                if (selectHasAttrWithName(name)) {
                    SelectElement attr = getAttrFromSelectByName(name);
                    attrExpr = attr.getExpr();
                } else {
                    attrExpr = attrNameToSqlIdentifierExpr(name);
                }
                Attribute attribute = from.getAttribute(name);
                switch (field.getOperator()) {
                    case EQ: {
                        Object value = getFieldValue(field);
                        if (value == null) {
                            return format("%s IS NULL", attrExpr);
                        } else {
                            switch (getOnlyElement(field.getValues()).getType()) {
                                case NATIVE:
                                    return format("%s = %s", attrExpr, wrapExprWithBrackets(toStringNotBlank(value)));
                                default:
                                    return format("%s = %s", attrExpr, systemToSqlExpr(value, attribute));
                            }
                        }
                    }
                    case GT: {
                        Object value = checkNotNull(getFieldValue(field));
                        switch (getOnlyElement(field.getValues()).getType()) {
                            case NATIVE:
                                return format("%s > %s", attrExpr, wrapExprWithBrackets(toStringNotBlank(value)));
                            default:
                                return format("%s > %s", attrExpr, systemToSqlExpr(value, attribute));
                        }
                    }
                    case LT: {
                        Object value = checkNotNull(getFieldValue(field));
                        switch (getOnlyElement(field.getValues()).getType()) {
                            case NATIVE:
                                return format("%s < %s", attrExpr, wrapExprWithBrackets(toStringNotBlank(value)));
                            default:
                                return format("%s < %s", attrExpr, systemToSqlExpr(value, attribute));
                        }
                    }
                    case IN: {
                        Object value = getFieldValue(field);
                        if (value == null) {
                            return format("%s IS NULL", attrExpr);
                        } else {
                            switch (getOnlyElement(field.getValues()).getType()) {
                                case NATIVE:
                                    return format("%s IN %s", attrExpr, wrapExprWithBrackets(toStringNotBlank(value)));
                                default:
                                    throw unsupported("unsupported cql field value type = %s", getOnlyElement(field.getValues()).getType());
                            }
                        }
                    }
                    default:
                        throw unsupported("unsupported cql operator = %s", field.getOperator());
                }
            }

            private @Nullable
            Object getFieldValue(FieldImpl field) {
                Field.FieldValue value = getOnlyElement(field.getValues(), null);
                if (value == null) {
                    return null;
                } else {
                    return value.getValue();
                }
            }

        }

        public WhereElement buildFilterExpr(AttributeFilter filter) {
            try {
                switch (filter.getMode()) {
                    case AND:
                        return filter.hasOnlyElement() ? buildFilterExpr(filter.getOnlyElement()) : joinWhereElements(filter.getElements().stream().map(this::buildFilterExpr).collect(toList()), "AND");
                    case OR:
                        return filter.hasOnlyElement() ? buildFilterExpr(filter.getOnlyElement()) : joinWhereElements(filter.getElements().stream().map(this::buildFilterExpr).collect(toList()), "OR").mapExpr(SqlQueryUtils::wrapExprWithBrackets);
                    case SIMPLE:
                        return buildFilterExpr(filter.getCondition());
                    case NOT:
                        return buildFilterExpr(filter.getOnlyElement()).mapExpr((x) -> format("NOT %s", x));
                    default:
                        throw new UnsupportedOperationException("unsupported filter mode = " + filter.getMode());
                }
            } catch (Exception ex) {
                throw new DaoException(ex, "error processing attribute filter = %s", filter);
            }
        }

        private WhereElement joinWhereElements(Collection<WhereElement> elements, String operator) {
            String expr = elements.stream().map(WhereElement::getExpr).map(SqlQueryUtils::wrapExprWithBrackets).collect(joining(format(" %s ", operator)));
            return new WhereElement(expr);
        }

        public WhereElement buildFilterExpr(AttributeFilterCondition condition) {
            return new ConditionExprBuilder(condition).build();
        }

        private class ConditionExprBuilder {

            private final AttributeFilterCondition condition;
            private final Attribute attribute;
            private final String attrExpr;
            private final SqlType sqlType;

            public ConditionExprBuilder(AttributeFilterCondition condition) {
                this.condition = condition;
                attribute = from.getAttribute(condition.getKey());
                attrExpr = attrNameToSqlIdentifierExpr(attribute.getName());
                sqlType = attributeTypeToSqlType(attribute.getType());
                //TODO validate operator for attribute type (es INET_* operators require Inet attr type)
            }

            public WhereElement build() {
                return new WhereElement(buildConditionExpr());
            }

            private String buildConditionExpr() {
                switch (condition.getOperator()) {
                    case ISNULL:
                        return format("%s IS NULL", attrExpr);
                    case ISNOTNULL:
                        return format("%s IS NOT NULL", attrExpr);
                    case EQUAL:
                        return format("%s IS NOT DISTINCT FROM %s", attrExpr, getSingleValue());
                    case NOTEQUAL:
                        return format("%s IS DISTINCT FROM %s", attrExpr, getSingleValue());
                    case IN:
                        return format("%s = ANY (%s)", attrExpr, systemToSqlExpr(condition.getValues(), getArraySqlType()));
                    case BEGIN:
                        return format("%s ILIKE %s", attrExpr, getSingleValueForLike(null, "%"));
                    case END:
                        return format("%s ILIKE %s", attrExpr, getSingleValueForLike("%", null));
                    case CONTAIN:
                        return format("%s ILIKE %s", attrExpr, getSingleValueForLike("%", "%"));
                    case LIKE:
                        return format("%s ILIKE %s", attrExpr, getSingleValueForLike(null, null));
                    case NOTBEGIN:
                        return format("( %s IS NULL OR NOT %s ILIKE %s )", attrExpr, attrExpr, getSingleValueForLike(null, "%"));
                    case NOTCONTAIN:
                        return format("( %s IS NULL OR NOT %s ILIKE %s )", attrExpr, attrExpr, getSingleValueForLike("%", "%"));
                    case NOTEND:
                        return format("( %s IS NULL OR NOT %s ILIKE %s )", attrExpr, attrExpr, getSingleValueForLike("%", null));
                    case GREATER:
                        return format("%s > %s", attrExpr, getSingleValue());
                    case LESS:
                        return format("%s < %s", attrExpr, getSingleValue());
                    case BETWEEN:
                        checkArgument(condition.getValues().size() == 2, "between operator requires exactly two parameters");
                        return format("%s BETWEEN %s AND %s", attrExpr, systemToSqlExpr(condition.getValues().get(0)), systemToSqlExpr(condition.getValues().get(1)));
                    case NET_CONTAINED:
                        return format("%s << %s", attrExpr, getSingleValue());
                    case NET_CONTAINEDOREQUAL:
                        return format("%s <<= %s", attrExpr, getSingleValue());
                    case NET_CONTAINS:
                        return format("%s >> %s", attrExpr, getSingleValue());
                    case NET_CONTAINSOREQUAL:
                        return format("%s >>= %s", attrExpr, getSingleValue());
                    case NET_RELATION:
                        String valueExpr = getSingleValue();
                        return format("( %s <<= %s OR %s = %s OR %s >>= %s )", attrExpr, valueExpr, attrExpr, valueExpr, attrExpr, valueExpr);
                    default:
                        throw new UnsupportedOperationException("unsupported condition operator = " + condition.getOperator());
                }
            }

            @Nullable
            private SqlTypeName getArraySqlType() {
                switch (sqlType.getType()) {
                    case int8:
                        return _int8;
                    case varchar:
                        return _varchar;
                    case bytea:
                        return _bytea;
                    default:
                        return null;//TODO improve this;
                }
            }

            private String getSingleValue() {
                return systemToSqlExpr(condition.getSingleValue(), attribute);
            }

            private String getSingleValueForLike(@Nullable String before, @Nullable String after) {
                return systemToSqlExpr(nullToEmpty(before) + checkNotBlank(condition.getSingleValue()) + nullToEmpty(after));
            }

        }

        private WhereElement buildFilterWhereExpr(RelationFilter filter) {
            List<WhereElement> list = filter.getRelationFilterRules().stream().map(this::buildFilterExpr).collect(toList());
            if (list.size() == 1) {
                return getOnlyElement(list);
            } else {
                String expr = list.stream().map(WhereElement::getExpr).map(SqlQueryUtils::wrapExprWithBrackets).collect(joining(" AND "));
                return new WhereElement(expr);
            }
        }

        private WhereElement buildFilterExpr(RelationFilterRule filter) {
            Domain domain = domainRepository.getDomain(filter.getDomain());
            String srcExpr, targetExpr, srcClassExpr, targetClassExpr;
            switch (filter.getDirection()) {
                case _1:
                    srcExpr = quoteSqlIdentifier(ATTR_IDOBJ1);
                    targetExpr = quoteSqlIdentifier(ATTR_IDOBJ2);
                    srcClassExpr = quoteSqlIdentifier(ATTR_IDCLASS1);
                    targetClassExpr = quoteSqlIdentifier(ATTR_IDCLASS2);
                    break;
                case _2:
                    srcExpr = quoteSqlIdentifier(ATTR_IDOBJ2);
                    targetExpr = quoteSqlIdentifier(ATTR_IDOBJ1);
                    srcClassExpr = quoteSqlIdentifier(ATTR_IDCLASS2);
                    targetClassExpr = quoteSqlIdentifier(ATTR_IDCLASS1);
                    break;
                default:
                    throw new UnsupportedOperationException("unsupported domain direction = " + filter.getDirection());
            }
            String fromExpr = fromAlias,
                    domainExpr = entryTypeToSqlExpr(domain);
            String subqueryBase = format("SELECT 1 FROM %s WHERE %s = %s.\"Id\" AND %s = %s.\"IdClass\" AND \"Status\" = 'A'", domainExpr, srcExpr, fromExpr, srcClassExpr, fromExpr);
            switch (filter.getType()) {
                case ANY:
                    return new WhereElement(format("EXISTS %s", wrapExprWithBrackets(subqueryBase)));
                case NOONE:
                    return new WhereElement(format("NOT EXISTS %s", wrapExprWithBrackets(subqueryBase)));
                case ONEOF:
                    List<String> exprs = list();
                    filter.getCardInfos().stream().map(RelationFilterCardInfo::getClassName).distinct().forEach((c) -> {
                        Classe classe = classeRepository.getClasse(c);
                        Set<Long> ids = filter.getCardInfos().stream().filter((ci) -> ci.getClassName().equals(c)).map(RelationFilterCardInfo::getId).collect(toSet());
                        String idExpr;
                        if (ids.size() == 1) {
                            idExpr = format("= %s", systemToSqlExpr(getOnlyElement(ids)));
                        } else {
                            idExpr = format("= ANY (%s)", systemToSqlExpr(ids));
                        }
                        exprs.add(format("%s = %s AND %s %s", targetClassExpr, systemToSqlExpr(classe), targetExpr, idExpr));
                    });
                    String oneOfExpr;
                    if (exprs.size() == 1) {
                        oneOfExpr = getOnlyElement(exprs);
                    } else {
                        oneOfExpr = wrapExprWithBrackets(exprs.stream().map(SqlQueryUtils::wrapExprWithBrackets).collect(joining(" OR ")));
                    }
                    return new WhereElement(format("EXISTS (%s AND %s)", subqueryBase, oneOfExpr));
                default:
                    throw new UnsupportedOperationException("unsupported relation filter type = " + filter.getType());
            }
        }

        private String buildDescAttrExprForLookup(Attribute a) {
            checkArgument(a.isOfType(AttributeTypeName.LOOKUP));
            return buildLookupDescExpr(fromAlias, exprForAttribute(a));
        }

        private String buildCodeAttrExprForLookup(Attribute a) {
            checkArgument(a.isOfType(AttributeTypeName.LOOKUP));
            return buildLookupCodeExpr(fromAlias, exprForAttribute(a));
        }

        private String buildDescAttrExprForReference(Attribute a) {
            Classe targetClass = getTargetClassForAttribute(a);
            checkArgument(targetClass.hasAttribute(ATTR_DESCRIPTION), "cannot select description for referenced class = %s: this class does not have a description attr", targetClass);
            return buildReferenceDescExpr(targetClass, fromAlias, exprForAttribute(a));
        }

        private String buildCodeAttrExprForReference(Attribute a) {
            Classe targetClass = getTargetClassForAttribute(a);
            checkArgument(targetClass.hasAttribute(ATTR_CODE), "cannot select code for referenced class = %s: this class does not have a code attr", targetClass);
            return buildReferenceCodeExpr(targetClass, fromAlias, exprForAttribute(a));
        }

        private boolean targetClassForAttributeHasCode(Attribute a) {
            return getTargetClassForAttribute(a).hasAttribute(ATTR_CODE);
        }

        private boolean targetClassForAttributeHasDescription(Attribute a) {
            return getTargetClassForAttribute(a).hasAttribute(ATTR_DESCRIPTION);
        }

        private Classe getTargetClassForAttribute(Attribute a) {
            checkArgument(a.isOfType(AttributeTypeName.FOREIGNKEY, AttributeTypeName.REFERENCE));
            if (a.getType().isOfType(REFERENCE)) {
                return domainRepository.getDomain((a.getType().as(ReferenceAttributeType.class)).getDomainName()).getReferencedClass(a);
            } else {
                return classeRepository.getClasse(a.getType().as(ForeignKeyAttributeType.class).getForeignKeyDestinationClassName());
            }
        }

        private String exprForAttribute(Attribute a) {
            return quoteSqlIdentifier(a.getName());
        }

    }

    private class SelectHolder {

        private final List<SelectElement> select = list();
        private final Map<String, SelectElement> selectByNameAndJoinId = map();

        public void add(SelectElement element) {
            logger.trace("add select element = {}", element);
            select.add(element);
            checkArgument(selectByNameAndJoinId.put(key(element.getSelectFromJoin(), element.getName()), element) == null, "duplicate select element for name =< %s > joinId =< %s >", element.getName(), element.getSelectFromJoin());
        }

        @Nullable
        public SelectElement getByNameAndJoin(String joinId, String name) {
            return selectByNameAndJoinId.get(key(joinId, name));
        }

        @Nullable
        public SelectElement getByName(String name) {
            return getByNameAndJoin(JOIN_ID_DEFAULT, name);
        }

        public boolean isEmpty() {
            return select.isEmpty();
        }

        public List<SelectElement> getElements() {
            return Collections.unmodifiableList(select);
        }

        public Stream<SelectElement> stream() {
            return select.stream();
        }
    }

    private static String cqlFilterKey(CqlFilter filter) {
        return checkNotBlank(filter.getCqlExpression());
    }

    private CqlQueryImpl compileCql(CqlFilter filter) {
        logger.debug("processing cql filter = '{}'", abbreviate(filter.getCqlExpression()));
        return compileAndCheck(filter.getCqlExpression());
    }

}
