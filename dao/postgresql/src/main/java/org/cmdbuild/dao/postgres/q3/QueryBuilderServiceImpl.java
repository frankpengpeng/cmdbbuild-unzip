/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.q3;

import org.cmdbuild.dao.core.q3.WhereOperator;
import org.cmdbuild.dao.core.q3.ResultRow;
import org.cmdbuild.dao.core.q3.PreparedQuery;
import org.cmdbuild.dao.core.q3.QueryBuilder;
import org.cmdbuild.dao.core.q3.QueryBuilderService;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collection;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.cmdbuild.dao.core.q3.CommonQueryBuilderMethods;
import org.cmdbuild.dao.core.q3.CompositeWhereHelper;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.orm.CardMapper;
import org.cmdbuild.dao.orm.CardMapperService;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;
import static org.cmdbuild.data.filter.utils.CmdbSorterUtils.noopSorter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.quoteSqlIdentifier;
import org.cmdbuild.dao.core.q3.CompositeWhereOperator;
import org.cmdbuild.dao.core.q3.JoinQueryBuilder;
import org.cmdbuild.dao.core.q3.RowNumberQueryBuilder;
import org.cmdbuild.dao.driver.repository.ClasseReadonlyRepository;
import org.cmdbuild.dao.driver.repository.DomainRepository;
import org.cmdbuild.dao.function.StoredFunction;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.dao.driver.repository.FunctionRepository;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.exprContainsQ3Markers;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.wrapExprWithBrackets;
import static org.cmdbuild.utils.lang.CmMapUtils.mapOf;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;

@Component
public class QueryBuilderServiceImpl implements QueryBuilderService {

    public final static String WHERE_ELEMENT_FOR_ROW_NUMBER = "WHERE_ELEMENT_FOR_ROW_NUMBER",
            REQUIRE_WITH_EXPR = "REQUIRE_WITH_EXPR",
            SELECT_DISTINCT = "SELECT_DISTINCT",
            SQL_TYPE_SELECT_ELEMENT_HINT = "sqlTypeHint",
            JOIN_ID_DEFAULT = "DEFAULT",
            JOIN_ARG_1 = "JOIN_ARG_1",
            JOIN_ARG_2 = "JOIN_ARG_2",
            WHERE_ATTR_IS_JOIN_ON_EXPR = "WHERE_ATTR_IS_JOIN_ON_EXPR",
            SELECT_FROM_JOIN_ID = "SELECT_FROM_JOIN_ID",
            ENABLE_SMART_EXPR_ALIAS_PROCESSING = "ENABLE_SMART_EXPR_ALIAS_PROCESSING",
            ENABLE_EXPLICIT_EXPR_MARKER_PROCESSING = "ENABLE_EXPLICIT_EXPR_MARKER_PROCESSING";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ClasseReadonlyRepository classeRepository;
    private final FunctionRepository functionRepository;
    private final DomainRepository domainRepository;
    private final CardMapperService mapper;
    private final QueryBuilderHelperService processorService;

    public QueryBuilderServiceImpl(ClasseReadonlyRepository classeRepository, FunctionRepository functionRepository, DomainRepository domainRepository, CardMapperService mapper, QueryBuilderHelperService processorService) {
        this.classeRepository = checkNotNull(classeRepository);
        this.functionRepository = checkNotNull(functionRepository);
        this.domainRepository = checkNotNull(domainRepository);
        this.mapper = checkNotNull(mapper);
        this.processorService = checkNotNull(processorService);
    }

    @Override
    public QueryBuilder query() {
        return new QueryBuilderImpl();
    }

    protected abstract class QueryBuilderCommons<T extends CommonQueryBuilderMethods> implements CommonQueryBuilderMethods<T> {

        protected final List<SelectArg> select = list();
        protected final List<WhereArg> where = list();
        protected final List<CmdbFilter> filters = list();

        @Override
        public T select(Collection<String> attrs) {
            attrs.stream().map((a) -> new SelectArg(a, quoteSqlIdentifier(a))).forEach(this::addSelect);
            return (T) this;
        }

        @Override
        public T selectExpr(String name, String expr) {
            addSelect(new SelectArg(name, expr, mapOf(String.class, Object.class).accept(m -> {
                if (exprContainsQ3Markers(expr)) {
                    m.put(ENABLE_EXPLICIT_EXPR_MARKER_PROCESSING, true);
                } else {
                    m.put(ENABLE_SMART_EXPR_ALIAS_PROCESSING, true);
                }
            }), emptyList()));
            return (T) this;
        }

        @Override
        public T selectMatchFilter(String name, CmdbFilter filter) {
            addSelect(new SelectArg(name, filter));
            return (T) this;
        }

        @Override
        public T where(String attr, WhereOperator operator, Object... params) {
            where.add(new WhereArg(attr, operator, emptyMap(), list(params)));
            return (T) this;
        }

        @Override
        public T where(CompositeWhereOperator operator, Consumer<CompositeWhereHelper> consumer) {
            CompositeWhereHelperImpl helper = new CompositeWhereHelperImpl();
            consumer.accept(helper);
            where.add(new WhereArg(operator, helper.inners, emptyMap()));
            return (T) this;
        }

        @Override
        public T where(CmdbFilter filter) {
            filters.add(checkNotNull(filter));
            return (T) this;
        }

        @Override
        public T where(PreparedQuery query) {
            InnerPreparedQuery src = (InnerPreparedQuery) query;
            src.getFilters().forEach((w) -> where.add(new WhereArg(w.getExpr(), SpecialOperators.EXPR, w.getMeta(), emptyList())));//TODO improve this
            return (T) this;
        }

        @Override
        public T whereExpr(String expr, Object... params) {
            return whereExpr(expr, list(params));
        }

        @Override
        public T whereExpr(String expr, Collection params) {
            where.add(new WhereArg(wrapExprWithBrackets(expr), SpecialOperators.EXPR, emptyMap(), CmCollectionUtils.toList(params)));
            return (T) this;
        }

        protected void addSelect(SelectArg selectArg) {
            logger.trace("add select = {}", selectArg);
            select.add(selectArg);
        }

    }

    protected class QueryBuilderImpl extends QueryBuilderCommons<QueryBuilder> implements QueryBuilder {

        protected EntryType from;
        protected Long offset, limit;
        protected CmdbSorter sorter = noopSorter();
        protected boolean selectRowNumber = false, activeCardsOnly = true, count = false, joinForRefCodeDescription = true;
        protected CardMapper cardMapper;
        protected final List<JoinQueryArgs> joinArgs = list();

        @Override
        public QueryBuilder selectCount() {
            count = true;
            return this;
        }

        @Override
        public RowNumberQueryBuilder selectRowNumber() {
            selectRowNumber = true;
            return new RowNumberQueryBuilderImpl();
        }

        @Override
        public QueryBuilder selectDistinct(String attr) {
            addSelect(new SelectArg(attr, quoteSqlIdentifier(attr), map(SELECT_DISTINCT, true), emptyList()));
            return this;
        }

        @Override
        public QueryBuilder from(Class model) {
            cardMapper = mapper.getMapperForModel(model);
            return from(cardMapper.getClassId()).includeJoinForLookupAndRefFkCodeAndDescription(false);
        }

        @Override
        public QueryBuilder from(String classe) {
            return from(classeRepository.getClasse(classe));
        }

        @Override
        public QueryBuilder fromDomain(String domain) {
            return from(domainRepository.getDomain(domain));
        }

        @Override
        public QueryBuilder fromFunction(String function) {
            return from(functionRepository.getFunctionByName(function));
        }

        @Override
        public QueryBuilder from(Classe classe) {
            return doFrom(classe);
        }

        @Override
        public QueryBuilder from(StoredFunction function) {
            return doFrom(function);
        }

        @Override
        public QueryBuilder from(Domain domain) {
            return doFrom(domain);
        }

        @Override
        public QueryBuilder includeHistory() {
            activeCardsOnly = false;
            return this;
        }

        @Override
        public QueryBuilder includeJoinForLookupAndRefFkCodeAndDescription(boolean include) {
            this.joinForRefCodeDescription = include;
            return this;
        }

        @Override
        public QueryBuilder orderBy(CmdbSorter sort) {
            sorter = checkNotNull(sort);
            return this;
        }

        @Override
        public QueryBuilder offset(@Nullable Long offset) {
            this.offset = offset;
            return this;
        }

        @Override
        public QueryBuilder limit(@Nullable Long limit) {
            this.limit = limit;
            return this;
        }

        @Override
        public PreparedQuery build() {
            return processorService.buildQuery(this);
        }

        @Override
        public List<ResultRow> run() {
            return build().run();
        }

        private QueryBuilder doFrom(EntryType entryType) {
            checkNotNull(entryType);
            checkArgument(entryType.isClasse() || entryType.isDomain() || entryType.isFunction(), "invalid entry type for query = %s", entryType);
            from = entryType;
            return this;
        }

        @Override
        public JoinQueryBuilder join(EntryType entryType) {
            JoinQueryArgs join = new JoinQueryArgs(this, JOIN_ID_DEFAULT, entryType);
            joinArgs.add(join);
            return join;
        }

        @Override
        public JoinQueryBuilder join(String classId) {
            return join(classeRepository.getClasse(classId));
        }

        @Override
        public JoinQueryBuilder joinDomain(String domainId) {
            return join(domainRepository.getDomain(domainId));
        }

        private class RowNumberQueryBuilderImpl implements RowNumberQueryBuilder {

            @Override
            public RowNumberQueryBuilder where(String attr, WhereOperator operator, Object... params) {
                where.add(new WhereArg(attr, operator, map(WHERE_ELEMENT_FOR_ROW_NUMBER, true), list(params)));
                return this;
            }

            @Override
            public QueryBuilder then() {
                return QueryBuilderImpl.this;
            }

        }

    }

    protected class JoinQueryArgs extends QueryBuilderCommons<JoinQueryBuilder> implements JoinQueryBuilder {

        protected final String joinId = randomId();
        protected final List<WhereArg> onExprs = list();
        protected final EntryType from;
        protected final QueryBuilderImpl root;
        protected final String parentId;

        public JoinQueryArgs(QueryBuilderImpl root, String parentId, EntryType from) {
            this.root = checkNotNull(root);
            this.parentId = checkNotBlank(parentId);
            this.from = checkNotNull(from);
        }

        @Override
        public JoinQueryBuilder on(String attr1, WhereOperator operator, String attr2) {
            onExprs.add(new WhereArg(attr1, operator, map(
                    JOIN_ARG_1, parentId,
                    JOIN_ARG_2, joinId,
                    WHERE_ATTR_IS_JOIN_ON_EXPR, true
            ), list(quoteSqlIdentifier(attr2))));
            return this;
        }

        @Override
        public JoinQueryBuilder join(EntryType entryType) {
            JoinQueryArgs join = new JoinQueryArgs(root, joinId, entryType);
            root.joinArgs.add(join);
            return join;
        }

        @Override
        public JoinQueryBuilder join(String classId) {
            return join(classeRepository.getClasse(classId));
        }

        @Override
        public JoinQueryBuilder joinDomain(String domainId) {
            return join(domainRepository.getDomain(domainId));
        }

        @Override
        public QueryBuilder then() {
            return root;
        }

        @Override
        public List<ResultRow> run() {
            return then().build().run();
        }
    }

    private class CompositeWhereHelperImpl implements CompositeWhereHelper {

        final List<WhereArg> inners = list();

        @Override
        public CompositeWhereHelper where(String attr, WhereOperator operator, Object... params) {
            inners.add(new WhereArg(attr, operator, emptyMap(), list(params)));//TODO duplicate code, merge with main
            return this;
        }
    }
}
