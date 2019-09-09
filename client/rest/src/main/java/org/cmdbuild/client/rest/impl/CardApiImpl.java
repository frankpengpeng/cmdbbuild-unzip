/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.impl;

import org.cmdbuild.client.rest.core.RestWsClient;
import org.cmdbuild.client.rest.core.AbstractServiceClientImpl;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Streams.stream;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import static java.lang.String.format;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.cmdbuild.client.rest.model.Card;
import org.cmdbuild.client.rest.model.SimpleCard;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.trimAndCheckNotBlank;
import org.cmdbuild.client.rest.api.CardApi;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;
import org.cmdbuild.data.filter.utils.CmdbFilterUtils;
import org.cmdbuild.data.filter.utils.CmdbSorterUtils;

public class CardApiImpl extends AbstractServiceClientImpl implements CardApi {

	public CardApiImpl(RestWsClient restClient) {
		super(restClient);
	}

	@Override
	public CardServiceWithCard createCard(String classId, Card card) {
		logger.debug("delete card for classId = {} card data = {}", classId, card);
		Map map = map(card.getAttributes());
		JsonElement response = post("classes/" + checkNotNull(trimToNull(classId)) + "/cards", map).asJson();
		String cardId = trimAndCheckNotBlank(toString(response.getAsJsonObject().getAsJsonObject("data").getAsJsonPrimitive("_id")));
		logger.info("created card with id = {}", cardId);
		Card newCard = new SimpleCard(classId, cardId, map);//TODO insert in map generated/default attr values, that should be returned by ws
		return new CardServiceWithCardImpl(newCard);
	}

	@Override
	public CardServiceWithCard updateCard(String classId, Card card) {
		//TODO
		throw new UnsupportedOperationException("not implemented yet");
	}

	@Override
	public CardApi deleteCard(String classId, String cardId) {
		logger.debug("delete card for classId = {} cardId = {}", classId, cardId);
		delete("classes/" + checkNotNull(trimToNull(classId)) + "/cards/" + checkNotNull(trimToNull(cardId)));
		return this;
	}

	@Override
	public Card getCard(String classId, String cardId) {
		logger.debug("get card for classId = {} cardId = {}", classId, cardId);
		JsonElement response = get("classes/" + checkNotNull(trimToNull(classId)) + "/cards/" + checkNotNull(trimToNull(cardId))).asJson();
		Map<String, Object> attributes = readAttributes(response.getAsJsonObject().getAsJsonObject("data"));
		checkArgument(equal(trimAndCheckNotBlank((String) attributes.get("_id")), cardId));
		checkArgument(equal(trimAndCheckNotBlank((String) attributes.get("_type")), classId));
		return new SimpleCard(classId, cardId, attributes);
	}

	@Override
	public List<Card> getCards(String classeId) {
		logger.debug("get cards for classeId = {}", classeId);
		JsonElement response = get("classes/" + checkNotNull(trimToNull(classeId)) + "/cards/").asJson();
		return parseCardsResponse(response);
	}

	@Override
	public CardQuery queryCards() {
		return new CardQueryImpl();
	}

	private List<Card> parseCardsResponse(JsonElement response) {
		return stream(response.getAsJsonObject().getAsJsonArray("data")).map(JsonElement::getAsJsonObject).map((card) -> {
			return new SimpleCard(toString(card.get("_type")), toString(card.get("_id")), readAttributes(card));
		}).collect(toList());
	}

	private Map<String, Object> readAttributes(JsonObject jsonObject) {
		Map<String, Object> attributes = map();
		jsonObject.entrySet().forEach((entry) -> {
			attributes.put(entry.getKey(), toString(entry.getValue()));
		});
		return attributes;
	}

	private class CardQueryImpl implements CardQuery {

		private Integer limit, start;
		private CmdbFilter filter;
		private CmdbSorter sort;

		@Override
		public CardQuery limit(@Nullable Integer limit) {
			this.limit = limit;
			return this;
		}

		@Override
		public CardQuery offset(@Nullable Integer start) {
			this.start = start;
			return this;
		}

		@Override
		public CardQuery filter(@Nullable CmdbFilter filter) {
			this.filter = filter;
			return this;
		}

		@Override
		public CardQuery sort(@Nullable CmdbSorter sort) {
			this.sort = sort;
			return this;
		}

		@Override
		public List<Card> getCards() {
			logger.debug("get cards for query");
			JsonElement response = get(format("cql?filter=%s&sort=%s&limit=%s&start=%s",
					encodeUrlQuery(filter == null ? "" : CmdbFilterUtils.serializeFilter(filter)),
					encodeUrlQuery(sort == null ? "" : CmdbSorterUtils.toJsonString(sort)),
					firstNonNull(limit, ""),
					firstNonNull(start, ""))).asJson();
			return parseCardsResponse(response);
		}

	}

	private class CardServiceWithCardImpl implements CardServiceWithCard {

		private final Card card;

		public CardServiceWithCardImpl(Card card) {
			this.card = checkNotNull(card);
		}

		@Override
		public CardApi then() {
			return CardApiImpl.this;
		}

		@Override
		public Card getCard() {
			return card;
		}
	}

}
