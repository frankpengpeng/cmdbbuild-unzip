package org.cmdbuild.task.wizardconnector;

import com.google.common.base.Objects;
import static org.cmdbuild.common.Constants.DESCRIPTION_ATTRIBUTE;
import static org.cmdbuild.dao.constants.Cardinality.CARDINALITY_1N;
import static org.cmdbuild.dao.constants.Cardinality.CARDINALITY_N1;
import static org.cmdbuild.dao.query.clause.AnyAttribute.anyAttribute;
import static org.cmdbuild.dao.query.clause.QueryAliasAttribute.attribute;
import static org.cmdbuild.dao.query.clause.where.EqualsOperatorAndValue.eq;
import static org.cmdbuild.dao.query.clause.where.SimpleWhereClause.condition;
import java.util.Map;

import org.cmdbuild.dao.beans.IdAndDescriptionImpl;
import org.cmdbuild.dao.beans.LookupValueImpl;
import org.cmdbuild.dao.entrytype.attributetype.CMAttributeTypeVisitor;
import org.cmdbuild.dao.entrytype.attributetype.ForwardingAttributeTypeVisitor;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.NullAttributeTypeVisitor;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import org.cmdbuild.dao.query.CMQueryRow;
import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.legacy.etl.ClassType;
import org.cmdbuild.legacy.etl.AttributeValueAdapter;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.stream.Collectors;
import org.cmdbuild.dao.query.QueryResult;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.lookup.Lookup;
import org.cmdbuild.lookup.LookupRepository;
import static org.cmdbuild.spring.configuration.BeanNamesAndQualifiers.SYSTEM_LEVEL_TWO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.view.DataView;

@Component
public class DefaultAttributeValueAdapter implements AttributeValueAdapter {

	private final DataView dataView;
	private final LookupRepository lookupStore;

	public DefaultAttributeValueAdapter(@Qualifier(SYSTEM_LEVEL_TWO) DataView dataView, LookupRepository lookupStore) {
		this.dataView = checkNotNull(dataView);
		this.lookupStore = checkNotNull(lookupStore);
	}

	@Override
	public Iterable<Map.Entry<String, Object>> toInternal(final ClassType type,
			final Iterable<? extends Map.Entry<String, ? extends Object>> values) {
		final String typeName = type.getName();
		final Classe targetType = dataView.findClasse(typeName);
		// TODO handle class not found
		final Map<String, Object> adapted = Maps.newHashMap();
		for (final Map.Entry<String, ? extends Object> entry : values) {
			final String attributeName = entry.getKey();
			final Object attributeValue = entry.getValue();
			new ForwardingAttributeTypeVisitor() {

				private final CMAttributeTypeVisitor DELEGATE = NullAttributeTypeVisitor.getInstance();

				private Object adaptedValue;

				@Override
				protected CMAttributeTypeVisitor delegate() {
					return DELEGATE;
				}

				public void adapt() {
					adaptedValue = attributeValue;
					if (attributeValue != null) {
						targetType.getAttributeOrNull(attributeName).getType().accept(this);
					}
					adapted.put(attributeName, adaptedValue);
				}

				@Override
				public void visit(final ReferenceAttributeType attributeType) {
					if (attributeValue instanceof String) {
						final String shouldBeCode = attributeValue.toString();
						final String domainName = attributeType.getDomainName();
						final Domain domain = dataView.findDomain(domainName);
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
								final QueryResult queryResult = dataView.select(anyAttribute(destination)) //
										.from(destination)
										//
										.where(condition(attribute(destination, DESCRIPTION_ATTRIBUTE),
												eq(shouldBeCode))) //
										.run();
								if (!queryResult.isEmpty()) {
									final CMQueryRow row = queryResult.iterator().next();
									final Card referredCard = row.getCard(destination);
									adaptedValue = referredCard.getId();
								} else {
									throw new RuntimeException("Conversion error");
								}
							} else {
								throw new RuntimeException("Conversion error");
							}
						}
					} else {
						adaptedValue = attributeValue;
					}
				}

				@Override
				public void visit(LookupAttributeType attributeType) {
					String lookupTypeName = attributeType.getLookupTypeName();
					LookupType lookupType = lookupStore.getTypeByName(lookupTypeName);
					String shouldBeDescription = attributeValue.toString();
					List<Lookup> lookups = lookupStore.getAllByType(lookupType).stream().filter((l) -> Objects.equal(l.getDescription(), shouldBeDescription)).collect(Collectors.toList());
					adaptedValue = lookups.isEmpty() ? null : getOnlyElement(lookups).getId();
				}

			}.adapt();
		}
		return adapted.entrySet();
	}

	@Override
	public Iterable<Map.Entry<String, Object>> toSynchronizer(final ClassType type,
			final Iterable<? extends Map.Entry<String, ? extends Object>> values) {
		final String typeName = type.getName();
		final Classe targetType = dataView.findClasse(typeName);
		// TODO handle class not found
		final Map<String, Object> adapted = Maps.newHashMap();
		for (final Map.Entry<String, ? extends Object> entry : values) {
			final String attributeName = entry.getKey();
			final Object attributeValue = entry.getValue();
			new ForwardingAttributeTypeVisitor() {

				private final CMAttributeTypeVisitor DELEGATE = NullAttributeTypeVisitor.getInstance();

				private Object adaptedValue;

				@Override
				protected CMAttributeTypeVisitor delegate() {
					return DELEGATE;
				}

				public void adapt() {
					adaptedValue = attributeValue;
					targetType.getAttributeOrNull(attributeName).getType().accept(this);
					adapted.put(attributeName, adaptedValue);
				}

				@Override
				public void visit(final LookupAttributeType attributeType) {
					final LookupValueImpl lookupValue = LookupValueImpl.class.cast(attributeValue);
					adaptedValue = lookupValue.getDescription();
				}

				@Override
				public void visit(final ReferenceAttributeType attributeType) {
					final IdAndDescriptionImpl referenceValue = IdAndDescriptionImpl.class.cast(attributeValue);
					adaptedValue = referenceValue.getDescription();
				}

			}.adapt();
		}
		return adapted.entrySet();
	}

}
