package org.cmdbuild.data.store.dao;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.cmdbuild.dao.query.clause.AnyAttribute.anyAttribute;
import static org.cmdbuild.dao.query.clause.QueryAliasAttribute.attribute;
import static org.cmdbuild.dao.query.clause.where.AndWhereClause.and;
import static org.cmdbuild.dao.query.clause.where.EqualsOperatorAndValue.eq;
import static org.cmdbuild.dao.query.clause.where.SimpleWhereClause.condition;
import static org.cmdbuild.dao.query.clause.where.TrueWhereClause.trueWhereClause;
import static org.cmdbuild.data.store.Groupables.notGroupable;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.cmdbuild.dao.query.CMQueryRow;
import org.cmdbuild.dao.query.clause.where.TrueWhereClause;
import org.cmdbuild.dao.query.clause.where.WhereClause;
import org.cmdbuild.data.store.Groupable;
import org.cmdbuild.data.store.Storable;
import org.cmdbuild.data.store.Store;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import org.apache.commons.lang3.builder.Builder;
import org.cmdbuild.dao.query.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.beans.CardDefinition;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.view.DataView;

public class DataViewStore<T extends Storable> implements Store<T> {

	static final String DEFAULT_IDENTIFIER_ATTRIBUTE_NAME = org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final DataView dataView;
	private final Groupable groupable;
	private final StorableConverter<T> converter;

	protected DataViewStore(DataViewStoreBuilder<T> builder) {
		this.dataView = checkNotNull(builder.dataView);
		this.groupable = checkNotNull(builder.groupable);
		this.converter = checkNotNull(builder.storableConverter);
	}

	/**
	 * @deprecated Use {@link newInstance()} instead.
	 */
	@Deprecated
	public static <T extends Storable> DataViewStore<T> newInstance(DataView view, StorableConverter<T> converter) {
		return DataViewStore.<T>builder() //
				.withDataView(view) //
				.withStorableConverter(converter) //
				.build();
	}

	/**
	 * @deprecated Use {@link newInstance()} instead.
	 */
	@Deprecated
	public static <T extends Storable> DataViewStore<T> newInstance(DataView view, Groupable groupable, StorableConverter<T> converter) {
		return DataViewStore.<T>builder() //
				.withDataView(view) //
				.withGroupable(groupable) //
				.withStorableConverter(converter) //
				.build();
	}

	public static <T extends Storable> DataViewStoreBuilder<T> builder() {
		return new DataViewStoreBuilder<>();
	}

	private Classe storeClass() {
		return dataView.getClasse(converter.getClassName());
	}

	@Override
	public Storable create(T storable) {
		logger.debug("creating a new storable element");

		logger.trace("filling new card's attributes");
		CardDefinition card = converter.fill(dataView.createCardFor(storeClass()) //
				.setUser(converter.getUser(storable)), storable);

		logger.debug("saving card");
		return converter.storableOf(card.save());
	}

	@Override
	public T read(Storable storable) {
		logger.info("reading storable element with identifier '{}'", storable.getIdentifier());

		Card card = findCard(storable);

		logger.debug("converting card to storable element");
		return converter.convert(card);
	}

	@Override
	public Collection<T> readAll() {
		logger.debug("listing all storable elements");
		return readAll(notGroupable());
	}

	@Override
	public Collection<T> readAll(Groupable groupable) {
		logger.debug("listing all storable elements with additional grouping condition '{}'", groupable);
		QueryResult result = dataView //
				.select(anyAttribute(storeClass())) //
				.from(storeClass()) //
				.where(and(builtInGroupWhereClause(), groupWhereClause(groupable))) //
				.run();

		List<T> list = transform(newArrayList(result), (CMQueryRow input) -> converter.convert(input.getCard(storeClass())));
		return list;
	}

	/**
	 * Creates a {@link WhereClause} for the grouping.
	 *
	 * @return the {@link WhereClause} for the grouping, {@link TrueWhereClause}
	 * if no grouping is available.
	 */
	private WhereClause builtInGroupWhereClause() {
		logger.debug("building built-in group where clause");
		return groupWhereClause(groupable);
	}

	private WhereClause groupWhereClause(Groupable groupable) {
		logger.debug("building group where clause");
		WhereClause clause;
		String attributeName = groupable.getGroupAttributeName();
		if (attributeName != null) {
			logger.debug("group attribute name is '{}', building where clause", attributeName);
			Object attributeValue = groupable.getGroupAttributeValue();
			clause = condition(attribute(storeClass(), attributeName), eq(attributeValue));
		} else {
			logger.debug("group attribute name not specified");
			clause = trueWhereClause();
		}
		return clause;
	}

	@Override
	public void update(T storable) {
		logger.debug("updating storable element with identifier '{}'", storable.getIdentifier());

		logger.trace("filling existing card's attributes");
		Card card = findCard(storable);
		CardDefinition updatedCard = converter.fill(dataView.update(card) //
				.setUser(converter.getUser(storable)), storable);

		logger.debug("saving card");
		updatedCard.save();
	}

	@Override
	public void delete(Storable storable) {
		logger.debug("deleting storable element with identifier '{}'", storable.getIdentifier());
		Card cardToDelete = findCard(storable);
		dataView.delete(cardToDelete);
	}

	/**
	 * Returns the {@link Card} corresponding to the {@link Storable}
	 * object.<br>
	 */
	private Card findCard(Storable storable) {
		logger.debug("looking for storable element with identifier '{}'", storable.getIdentifier());
		QueryResult queryResult = dataView.select(anyAttribute(storeClass())).from(storeClass()).where(whereClauseFor(storable)).run();
		checkArgument(queryResult.hasContent(), "storable element not found for classe = %s id = %s", storeClass().getName(), storable.getIdentifier());
		return queryResult.getOnlyRow().getCard(storeClass());
	}

	/**
	 * Builds the where clause for the specified {@link Storable} object.
	 */
	private WhereClause whereClauseFor(Storable storable) {
		logger.debug("building specific where clause");

		String attributeName = converter.getIdentifierAttributeName();
		Object attributeValue;
		if (DEFAULT_IDENTIFIER_ATTRIBUTE_NAME.equals(attributeName)) {
			logger.debug("using default one identifier attribute, converting to default type");
			attributeValue = Long.parseLong(storable.getIdentifier());
		} else {
			attributeValue = storable.getIdentifier();
		}

		return and(builtInGroupWhereClause(), condition(attribute(storeClass(), attributeName), eq(attributeValue)));
	}

	public static class DataViewStoreBuilder<T extends Storable> implements Builder<DataViewStore<T>> {

		private DataView dataView;
		private StorableConverter<T> storableConverter;
		private Groupable groupable = notGroupable();

		private DataViewStoreBuilder() {
		}

		@Override
		public DataViewStore<T> build() {
			validate();
			return new DataViewStore<>(this);
		}

		private void validate() {
			Validate.notNull(dataView, "missing %s", DataView.class.getName());
			Validate.notNull(storableConverter, "missing %s", StorableConverter.class.getName());
			storableConverter = wrap(storableConverter);
			groupable = defaultIfNull(groupable, notGroupable());
		}

		private StorableConverter<T> wrap(StorableConverter<T> converter) {
			return new ForwardingStorableConverter<T>() {

				@Override
				protected StorableConverter<T> delegate() {
					return converter;
				}

				@Override
				public String getIdentifierAttributeName() {
					String name = super.getIdentifierAttributeName();
					return (name == null) ? DEFAULT_IDENTIFIER_ATTRIBUTE_NAME : name;
				}

			};
		}

		public DataViewStoreBuilder<T> withDataView(DataView dataView) {
			this.dataView = dataView;
			return this;
		}

		public DataViewStoreBuilder<T> withStorableConverter(StorableConverter<T> storableConverter) {
			this.storableConverter = storableConverter;
			return this;
		}

		public DataViewStoreBuilder<T> withGroupable(Groupable groupable) {
			this.groupable = groupable;
			return this;
		}

	}
}
