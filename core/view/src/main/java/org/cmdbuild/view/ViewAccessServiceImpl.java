/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.view;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.common.utils.PagedElements.isPaged;
import org.cmdbuild.dao.beans.Card;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.WhereOperator.EQ;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.function.StoredFunction;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;
import org.cmdbuild.data.filter.utils.CmdbFilterUtils;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.view.ViewType.FILTER;
import static org.cmdbuild.view.ViewType.SQL;
import static org.cmdbuild.view.ViewUtils.checkViewIsFilterView;
import org.springframework.stereotype.Component;

@Component
public class ViewAccessServiceImpl implements ViewAccessService {

    private final DaoService dao;

    public ViewAccessServiceImpl(DaoService dao) {
        this.dao = checkNotNull(dao);
    }

    @Override
    public Card getCardById(View view, long cardId) {
        checkViewIsFilterView(view);
        return dao.getCard(view.getSourceClass(), cardId);//TODO apply filter        
    }

    @Override
    public PagedElements<Map<String, Object>> getRecords(View view, DaoQueryOptions queryOptions) {
        checkArgument(view.isOfType(SQL), "cannot get records from view with type = %s", view.getType());
        CmdbFilter filterParam = queryOptions.getFilter();

//        CmdbSorter sorter = mapSorterAttributeNames(CmdbSorterUtils.fromNullableJson(sort)); TODO map
        CmdbSorter sorter = queryOptions.getSorter();

        long offset = queryOptions.getOffset();
        Long limit = queryOptions.getLimit();

        StoredFunction function = dao.getFunctionByName(view.getSourceFunction());

//        CmMapUtils.FluentMap meta = map(); //TODO duplicate code  
        if (queryOptions.hasPositionOf()) {
//                    checkArgument(hasLimit(limit), "must set valid 'limit' along with 'positionOf'");
            long rowNumber = dao.selectRowNumber().where(ATTR_ID, EQ, queryOptions.hasPositionOf()).then()
                    .from(function)
                    .orderBy(sorter)
                    .where(filterParam)
                    .build().getRowNumberOrNull();
            long positionInPage = rowNumber % limit;
            offset = rowNumber - positionInPage;
//                    meta.put("positions", map(positionOf, positionInPage), START, offset, LIMIT, limit);
        }

        List<Map<String, Object>> records = dao.selectAll()
                .from(function)
                .orderBy(sorter)
                .where(filterParam)
                .paginate(offset, limit)
                .run().stream().map(r -> r.asMap()).collect(toList());

        long total;
        if (isPaged(offset, limit)) {
            total = dao.selectCount()
                    .from(function)
                    .where(filterParam)
                    .getCount();
        } else {
            total = records.size();
        }

        return PagedElements.paged(records, total);
//                return response(cards.stream().map(helper::serializeCard).collect(toList()), total, meta);
    }

    @Override
    public PagedElements<Card> getCards(View view, DaoQueryOptions queryOptions) {//TODO user permission check
        checkArgument(view.isOfType(FILTER), "cannot get cards from view with type = %s", view.getType());
        CmdbFilter filterParam = queryOptions.getFilter();

//        CmdbSorter sorter = mapSorterAttributeNames(CmdbSorterUtils.fromNullableJson(sort)); TODO map
        CmdbSorter sorter = queryOptions.getSorter();

        long offset = queryOptions.getOffset();
        Long limit = queryOptions.getLimit();

        String classId = view.getSourceClass();
        CmdbFilter viewFilter = CmdbFilterUtils.parseFilter(view.getFilter());
        CmdbFilter filter = CmdbFilterUtils.merge(viewFilter, filterParam);

//                CmMapUtils.FluentMap meta = map(); //TODO duplicate code from CardWs.
        if (queryOptions.hasPositionOf()) {
            long rowNumber = dao.selectRowNumber().where(ATTR_ID, EQ, queryOptions.getPositionOf()).then()
                    .from(classId)
                    .orderBy(sorter)
                    .where(filter)
                    .build().getRowNumberOrNull();
            long positionInPage = rowNumber % limit;
            offset = rowNumber - positionInPage;
//                    meta.put("positions", map(queryOptions.getPositionOf(), positionInPage), "start", offset, "limit",limit);
        }

        List<Card> cards = dao.selectAll()
                .from(classId)
                .orderBy(sorter)
                .where(filter)
                .paginate(offset, limit)
                .getCards();

        long total;
        if (isPaged(offset, limit)) {
            total = dao.selectCount()
                    .from(classId)
                    .where(filter)
                    .getCount();
        } else {
            total = cards.size();
        }

//                return response(cards.stream().map(helper::serializeCard).collect(toList()), total, meta);
        return PagedElements.paged(cards, total);

    }

    @Override
    public Collection<Attribute> getAttributesForView(View view) {
        switch (view.getType()) {
            case FILTER:
                return dao.getClasse(view.getSourceClass()).getAllAttributes();
            case SQL:
                return dao.getFunctionByName(view.getSourceFunction()).getAllAttributes();
            default:
                throw unsupported("unsupported view type = %s", view.getType());
        }
    }

}
