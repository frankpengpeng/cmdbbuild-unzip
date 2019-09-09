package org.cmdbuild.data.store.dao;

import static org.cmdbuild.data.store.dao.DataViewStore.DEFAULT_IDENTIFIER_ATTRIBUTE_NAME;

import java.util.Map;
import java.util.Map.Entry;

import org.cmdbuild.data.store.Storable;
import org.cmdbuild.data2.impl.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.dao.beans.CardDefinition;
import org.cmdbuild.dao.beans.Card;

public abstract class BaseStorableConverter<T extends Storable> implements StorableConverter<T> {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private static final String SYSTEM_USER = "system"; // FIXME

	@Override
	public String getIdentifierAttributeName() {
		return DEFAULT_IDENTIFIER_ATTRIBUTE_NAME;
	}

	@Override
	public Storable storableOf(Card card) {
		return () -> {
			final String attributeName = getIdentifierAttributeName();
			final String value;
			if (DEFAULT_IDENTIFIER_ATTRIBUTE_NAME.equals(attributeName)) {
				value = Long.toString(card.getId());
			} else {
				value = card.get(getIdentifierAttributeName(), String.class);
			}
			return value;
		};
	}

	@Override
	public CardDefinition fill(CardDefinition card, T storable) {
		Map<String, Object> values = getValues(storable);
		for (Entry<String, Object> entry : values.entrySet()) {
			logger.debug("setting attribute '{}' with value '{}'", entry.getKey(), entry.getValue());
			card.set(entry.getKey(), entry.getValue());
		}
		return card;
	}

	@Override
	public String getUser(T storable) {
		return SYSTEM_USER;
	}

	/**
	 * @deprecated use static methods directly instead
	 */
	@Deprecated
	protected String readStringAttribute(final Card card, final String attributeName) {
		return Utils.readString(card, attributeName);
	}

}
