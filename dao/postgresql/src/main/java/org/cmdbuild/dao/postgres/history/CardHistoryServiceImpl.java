/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.history;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CURRENTID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.WhereOperator.EQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_BEGINDATE;
import static org.cmdbuild.dao.core.q3.WhereOperator.LT;
import org.cmdbuild.dao.entrytype.Attribute;
import static org.cmdbuild.data.filter.SorterElement.SorterElementDirection.DESC;

@Component
public class CardHistoryServiceImpl implements CardHistoryService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final DaoService dao;

	public CardHistoryServiceImpl(DaoService dao) {
		this.dao = checkNotNull(dao);
	}

	@Override
	public PagedElements<Card> getHistory(String classId, long cardId, DaoQueryOptions queryOptions) { //TODO apply user permission
		List<Card> cards = dao.select().from(classId).includeHistory()
				.where(ATTR_CURRENTID, EQ, cardId)
				.withOptions(queryOptions)
				.getCards();
		if (queryOptions.isPaged()) {
			int total = dao.selectCount().from(classId).includeHistory()
					.where(ATTR_CURRENTID, EQ, cardId)
					.where(queryOptions.getFilter())
					.getCount();
			return paged(cards, total);
		} else {
			return paged(cards);
		}
	}

	@Override
	public Card getHistoryRecord(String classId, long recordId) { //TODO apply user permission
		Card card = dao.selectAll().from(classId).includeHistory()
				.where(ATTR_ID, EQ, recordId)
				.getCard();

		Card previousCard = dao.selectAll().from(classId).includeHistory()
				.where(ATTR_CURRENTID, EQ, card.getCurrentId())
				.where(ATTR_BEGINDATE, LT, card.getBeginDate())
				.orderBy(ATTR_BEGINDATE, DESC)
				.limit(1l)
				.getCardOrNull();

		if (previousCard != null) {
			card = addDiffToHistoricCard(card, previousCard);
		}

		return card;
	}

	private Card addDiffToHistoricCard(Card thisCard, Card previousCard) {
		logger.debug("addDiffToHistoricCard = {} from previous = {}", thisCard, previousCard);
		return CardImpl.copyOf(thisCard)
				.accept((c) -> {
					thisCard.getType().getServiceAttributes().stream().map(Attribute::getName).forEach((a) -> {
						logger.trace("checking differences in attr = {}", a);
						Object previousValue = previousCard.get(a), thisValue = thisCard.get(a);
						if (!equal(previousValue, thisValue)) {
							logger.trace("attr = {} changed from {} to {}", a, previousValue, thisValue);
							c.addAttribute("_" + a + "_changed", true);
							c.addAttribute("_" + a + "_previous", previousValue);
						}
					});
				}).build();
	}

}
