package org.cmdbuild.servlets.json.management.dataimport;

import static org.cmdbuild.common.Constants.CODE_ATTRIBUTE;
import static org.cmdbuild.dao.constants.Cardinality.CARDINALITY_1N;
import static org.cmdbuild.dao.constants.Cardinality.CARDINALITY_N1;
import static org.cmdbuild.dao.query.clause.AnyAttribute.anyAttribute;
import static org.cmdbuild.dao.query.clause.QueryAliasAttribute.attribute;
import static org.cmdbuild.dao.query.clause.where.EqualsOperatorAndValue.eq;
import static org.cmdbuild.dao.query.clause.where.SimpleWhereClause.condition;

import org.cmdbuild.dao.beans.IdAndDescriptionImpl;
import org.cmdbuild.dao.beans.LookupValueImpl;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import org.cmdbuild.dao.query.CMQueryRow;
import org.cmdbuild.lookup.Lookup;
import org.cmdbuild.lookup.LookupType;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.cmdbuild.dao.query.QueryResult;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Attribute;
import org.slf4j.LoggerFactory;
import org.cmdbuild.lookup.LookupRepository;
import org.cmdbuild.lookup.LookupTypeImpl;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.beans.CardDefinition;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.view.DataView;

public class CardFiller {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	final private Classe destinationClass;
	final private DataView view;
	final private LookupRepository lookupStore;

	public CardFiller( //
			final Classe destinationClass, //
			final DataView view, //
			final LookupRepository lookupStore //
	) {
		this.destinationClass = destinationClass;
		this.view = view;
		this.lookupStore = lookupStore;
	}

	@SuppressWarnings("serial")
	public static class CardFillerException extends Exception {
		public final String attributeName;
		public final Object attributeValue;

		public CardFillerException(final String attributeName, final Object value) {
			this.attributeName = attributeName;
			this.attributeValue = value;
		}
	}

	/**
	 * Try to set the given value to the attribute with the given name for the
	 * diven card
	 * 
	 * If the value is a JSONObject try to build a CardReference and set it to
	 * the attribute: so the value must have "id" and "description" attributes
	 * 
	 * Otherwise, check the type of the attribute, - if it is a
	 * ReferenceAttributeType try to retrieve the referenced card comparing the
	 * given value with the "code" attribute and build a CardReference. - if it
	 * is a LookupAttributeType try to retrieve the lookup with the given
	 * description and build a CardReference
	 */
	public void fillCardAttributeWithValue( //
			final CardDefinition card, //
			final String attributeName, //
			final Object value //
	) throws CardFillerException, JSONException {

		// if the attribute has no value do nothing
		if (value == null || "".equals(value)) {

			return;
		}

		final Attribute attribute = destinationClass.getAttributeOrNull(attributeName);

		if (attribute == null) {
			logger.warn("attribute '{}' not found");
		} else if (!attribute.isActive()) {
			logger.warn("attribute '{}' is not active");
		}

		if (value instanceof JSONObject) {
			final JSONObject jsonValue = (JSONObject) value;
			final IdAndDescriptionImpl cardReference = new IdAndDescriptionImpl( //
					(Long) jsonValue.getLong("id"), //
					(String) jsonValue.get("description") //
			);

			card.set(attributeName, cardReference);

		} else {
			if (attribute.getType() instanceof ReferenceAttributeType) {

				// Use the Code attribute of the referenced card
				manageReferenceAttribute(card, attributeName, value, attribute);

			} else if (attribute.getType() instanceof LookupAttributeType) {

				// For the lookup use the Description
				manageLookupAttribute(card, attributeName, value, attribute);

			} else {
				/*
				 * Business rule: 16 July 2013 Do not manage the ForeignKey no
				 * one has asked to do that
				 */
				try {
					card.set(attributeName, value);
				} catch (final Exception ex) {
					throw new CardFillerException(attributeName, value);
				}
			}
		}
	}

	private void manageLookupAttribute( //
			final CardDefinition mutableCard, //
			final String attributeName, //
			final Object value, //
			final Attribute attribute //
	) throws CardFillerException {

		final LookupAttributeType type = (LookupAttributeType) attribute.getType();
		final String lookupTypeName = type.getLookupTypeName();
		final LookupType lookupType = LookupTypeImpl.builder().withName(lookupTypeName).build();

		boolean set = false;
		for (final Lookup lookup : lookupStore.getAllByType(lookupType)) {
			if (value.equals(lookup.getDescription())) {
				mutableCard.set(//
						attributeName, //
						new LookupValueImpl( //
								lookup.getId(), //
lookup.getDescription(), //
								lookupTypeName //
								) //
						);

				set = true;
				break;
			}
		}

		if (!set) {
			throw new CardFillerException(attributeName, value);
		}
	}

	private void manageReferenceAttribute( //
			final CardDefinition mutableCard, //
			final String attributeName, //
			final Object value, //
			final Attribute attribute //
	) throws CardFillerException {

		final ReferenceAttributeType type = (ReferenceAttributeType) attribute.getType();
		final String domainName = type.getDomainName();
		final Domain domain = view.findDomain(domainName);
		if (domain != null) {

			// retrieve the destination
			final String cardinality = domain.getCardinality();
			Classe destination = null;
			if (CARDINALITY_1N.value().equals(cardinality)) {
				destination = domain.getSourceClass();
			} else if (CARDINALITY_N1.value().equals(cardinality)) {
				destination = domain.getTargetClass();
			}

			if (destination != null) {
				final QueryResult queryResult = view.select(anyAttribute(destination)) //
						.from(destination) //
						.where(condition(attribute(destination, CODE_ATTRIBUTE), eq(value))) //
						.run();

				if (!queryResult.isEmpty()) {
					final CMQueryRow row = queryResult.iterator().next();
					final Card referredCard = row.getCard(destination);
					mutableCard.set(attributeName, buildCardReference(referredCard));
				} else {
					throw new CardFillerException(attributeName, value);
				}
			} else {
				throw new CardFillerException(attributeName, value);
			}
		}
	}

	private IdAndDescriptionImpl buildCardReference(final Card referredCard) {
		IdAndDescriptionImpl cardReference;
		final Object description = referredCard.getDescription();
		if (description == null) {
			cardReference = new IdAndDescriptionImpl(referredCard.getId(), "");
		} else {
			cardReference = new IdAndDescriptionImpl(referredCard.getId(), (String) referredCard.getDescription());
		}
		return cardReference;
	}
}