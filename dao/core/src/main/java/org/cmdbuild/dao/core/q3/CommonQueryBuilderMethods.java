/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.core.q3;

import static java.lang.String.format;
import java.util.Collection;
import java.util.function.Consumer;
import static org.cmdbuild.dao.core.q3.DaoService.ALL;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.data.filter.CmdbFilter;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public interface CommonQueryBuilderMethods<T extends CommonQueryBuilderMethods> extends PreparedQuery {

    T select(Collection<String> attrs);

    T selectExpr(String name, String expr);

    T selectMatchFilter(String name, CmdbFilter filter);

    T where(String attr, WhereOperator operator, Object... params);

    T whereExpr(String expr, Object... params);

    T whereExpr(String expr, Collection params);

    T where(CmdbFilter filter);

    T where(PreparedQuery query);

    T where(CompositeWhereOperator operator, Consumer<CompositeWhereHelper> consumer);

    JoinQueryBuilder join(EntryType entryType);

    JoinQueryBuilder join(String classId);

    JoinQueryBuilder joinDomain(String domainId);

    default T select(String... attrs) {
        return select(list(attrs));
    }

    default T selectExpr(String name, String expr, Object... args) {
        return selectExpr(name, format(expr, args));
    }

    default T selectAll() {
        return select(ALL);
    }

    default T where(WhereOperator operator, String attr, Object... params) {
        return where(attr, operator, params);
    }

    default T where(QueryBuilder query) {
        return where(query.build());
    }

}
