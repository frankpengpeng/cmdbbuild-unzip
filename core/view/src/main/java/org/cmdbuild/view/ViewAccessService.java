package org.cmdbuild.view;

import java.util.Collection;
import java.util.Map;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.entrytype.Attribute;

public interface ViewAccessService {

    Card getCardById(View view, long cardId);

    PagedElements<Card> getCards(View view, DaoQueryOptions queryOptions);

    PagedElements<Map<String, Object>> getRecords(View view, DaoQueryOptions queryOptions);

    Collection<Attribute> getAttributesForView(View view);

}
