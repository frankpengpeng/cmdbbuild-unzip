package org.cmdbuild.data.store.dao;

import java.util.Map;

import org.cmdbuild.data.store.Storable;
import org.cmdbuild.dao.beans.CardDefinition;
import org.cmdbuild.dao.beans.Card;

public interface StorableConverter<T extends Storable> {

	/**
	 * @return the name of the class in the store.
	 */
	String getClassName();

	/**
	 * @return the name of the identifier attribute.
	 */
	String getIdentifierAttributeName();

	/**
	 * Converts a card into a {@link Storable}.
	 *
	 * @param card the cards that needs to be converted.
	 *
	 * @return the instance of {@link Storable} representing the card.
	 */
	Storable storableOf(Card card);

	/**
	 * Converts a card into a {@link T}.
	 *
	 * @param card the card that needs to be converted.
	 *
	 * @return the instance of {@link T} representing the card.
	 */
	T convert(Card card);

	/**
	 * Fills a {@link CardDefinition} with all values from {@link T} and
	 * returns the filled {@link CardDefinition}.
	 *
	 * @param card the card that has to be filled.
	 * @param storable
	 *
	 * @return the filled card.
	 */
	CardDefinition fill(final CardDefinition card, final T storable);

	/**
	 * Converts a generic type into a map of <String, Object>, corresponding to
	 * attribute <name, value>.
	 *
	 * @param storable
	 * @return
	 *
	 * @deprecated {@link StorableConverter.fill} should be used instead.
	 */
	@Deprecated
	Map<String, Object> getValues(T storable);

	String getUser(T storable);

}
