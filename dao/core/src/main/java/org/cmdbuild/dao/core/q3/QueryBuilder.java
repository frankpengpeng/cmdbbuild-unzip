/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.core.q3;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.function.StoredFunction;
import org.cmdbuild.data.filter.CmdbSorter;
import org.cmdbuild.data.filter.SorterElement;
import org.cmdbuild.data.filter.beans.CmdbSorterImpl;
import org.cmdbuild.data.filter.beans.SorterElementImpl;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLongOrNull;

public interface QueryBuilder extends CommonQueryBuilderMethods<QueryBuilder> {

    static final WhereOperator EQ = WhereOperator.EQ;
    static final WhereOperator NOTEQ = WhereOperator.NOTEQ;

    RowNumberQueryBuilder selectRowNumber();

    QueryBuilder selectDistinct(String attr);

    QueryBuilder selectCount();

    QueryBuilder from(String classe);

    QueryBuilder fromDomain(String domain);

    QueryBuilder fromFunction(String function);

    QueryBuilder from(Classe classe);

    QueryBuilder from(Class model);

    QueryBuilder from(Domain domain);

    QueryBuilder from(StoredFunction function);

    QueryBuilder includeHistory();

    QueryBuilder orderBy(CmdbSorter sort);

    QueryBuilder offset(@Nullable Long offset);

    QueryBuilder limit(@Nullable Long limit);
    
    QueryBuilder includeJoinForLookupAndRefFkCodeAndDescription(boolean joinForRefCodeDescription);

    PreparedQuery build();

    default QueryBuilder orderBy(String property, SorterElement.SorterElementDirection direction) {
        return orderBy(new CmdbSorterImpl(new SorterElementImpl(property, direction)));
    }

    default QueryBuilder orderBy(String property1, SorterElement.SorterElementDirection direction1, String property2, SorterElement.SorterElementDirection direction2) {
        return orderBy(new CmdbSorterImpl(new SorterElementImpl(property1, direction1), new SorterElementImpl(property2, direction2)));
    }

    default QueryBuilder orderBy(String property) {
        return orderBy(property, SorterElement.SorterElementDirection.ASC);
    }

    default QueryBuilder offset(@Nullable Integer offset) {
        return offset(toLongOrNull(offset));
    }

    default QueryBuilder limit(@Nullable Integer limit) {
        return limit(toLongOrNull(limit));
    }

    default QueryBuilder paginate(@Nullable Integer offset, @Nullable Integer limit) {
        return this.offset(convert(offset, Long.class)).limit(convert(limit, Long.class));
    }

    default QueryBuilder paginate(@Nullable Long offset, @Nullable Long limit) {
        return this.offset(offset).limit(limit);
    }

    default QueryBuilder withOptions(DaoQueryOptions queryOptions) {
        return this
                .where(queryOptions.getFilter())
                .orderBy(queryOptions.getSorter())
                .paginate(queryOptions.getOffset(), queryOptions.getLimit());
    }

    default QueryBuilder accept(Consumer<QueryBuilder> visitor) {
        visitor.accept(this);
        return this;
    }

    @Override
    default int getCount() {//TODO change count to long
        return build().getCount();
    }

}
