package org.cmdbuild.task.util;

import static java.util.Arrays.asList;
import static org.cmdbuild.logic.mapping.json.Constants.FilterOperator.EQUAL;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.ATTRIBUTE_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.OPERATOR_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.VALUE_KEY;

import org.cmdbuild.logic.mapping.json.JsonFilterHelper.FilterElementGetter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.dao.beans.Card;

public class CardIdFilterElementGetter implements FilterElementGetter {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public static CardIdFilterElementGetter of(final Card card) {
		return new CardIdFilterElementGetter(card);
	}

	private final Card card;

	private CardIdFilterElementGetter(final Card card) {
		this.card = card;
	}

	@Override
	public boolean hasElement() {
		return true;
	}

	@Override
	public JSONObject getElement() throws JSONException {
		logger.debug("creating JSON element for '{}'", card.getId());
		final JSONObject element = new JSONObject();
		element.put(ATTRIBUTE_KEY, "Id");
		element.put(OPERATOR_KEY, EQUAL);
		element.put(VALUE_KEY, new JSONArray(asList(card.getId())));
		logger.debug("resulting element is '{}'", element);
		return element;
	}

}
