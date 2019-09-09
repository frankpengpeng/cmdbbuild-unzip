package org.cmdbuild.cql.legacy;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Iterables.isEmpty;
import static java.lang.String.format;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.cmdbuild.dao.query.clause.QueryAliasAttribute.attribute;
import static org.cmdbuild.dao.query.clause.join.Over.over;
import static org.cmdbuild.dao.query.clause.where.AndWhereClause.and;
import static org.cmdbuild.dao.query.clause.where.BeginsWithOperatorAndValue.beginsWith;
import static org.cmdbuild.dao.query.clause.where.ContainsOperatorAndValue.contains;
import static org.cmdbuild.dao.query.clause.where.EndsWithOperatorAndValue.endsWith;
import static org.cmdbuild.dao.query.clause.where.EqualsOperatorAndValue.eq;
import static org.cmdbuild.dao.query.clause.where.GreaterThanOperatorAndValue.gt;
import static org.cmdbuild.dao.query.clause.where.InOperatorAndValue.in;
import static org.cmdbuild.dao.query.clause.where.LessThanOperatorAndValue.lt;
import static org.cmdbuild.dao.query.clause.where.NotWhereClause.not;
import static org.cmdbuild.dao.query.clause.where.NullOperatorAndValue.isNull;
import static org.cmdbuild.dao.query.clause.where.OrWhereClause.or;
import static org.cmdbuild.dao.query.clause.where.SimpleWhereClause.condition;
import static org.cmdbuild.dao.query.clause.where.TrueWhereClause.trueWhereClause;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.cmdbuild.cql.CQLBuilderListener.FieldInputValue;
import org.cmdbuild.cql.CQLBuilderListener.FieldValueType;
import org.cmdbuild.cql.compiler.impl.ClassDeclarationImpl;
import org.cmdbuild.cql.compiler.impl.DomainDeclarationImpl;
import org.cmdbuild.cql.compiler.impl.DomainObjectsReferenceImpl;
import org.cmdbuild.cql.compiler.impl.FieldImpl;
import org.cmdbuild.cql.compiler.impl.FieldSelectImpl;
import org.cmdbuild.cql.compiler.impl.GroupImpl;
import org.cmdbuild.cql.compiler.impl.CqlQueryImpl;
import org.cmdbuild.cql.compiler.impl.SelectImpl;
import org.cmdbuild.cql.compiler.impl.WhereImpl;
import org.cmdbuild.cql.compiler.select.FieldSelect;
import org.cmdbuild.cql.compiler.select.SelectItem;
import org.cmdbuild.cql.compiler.where.DomainObjectsReference;
import org.cmdbuild.cql.compiler.where.Field.FieldValue;
import org.cmdbuild.cql.compiler.where.Group;
import org.cmdbuild.cql.compiler.where.WhereElement;
import org.cmdbuild.cql.compiler.where.fieldid.LookupFieldId;
import org.cmdbuild.cql.compiler.where.fieldid.SimpleFieldId;
import org.cmdbuild.dao.entrytype.SystemAttributeImpl;
import org.cmdbuild.dao.postgres.Const;
import org.cmdbuild.dao.entrytype.attributetype.CMAttributeTypeVisitor;
import org.cmdbuild.dao.entrytype.attributetype.DateAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ForwardingAttributeTypeVisitor;
import org.cmdbuild.dao.entrytype.attributetype.IntegerAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.NullAttributeTypeVisitor;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.StringAttributeType;
import org.cmdbuild.dao.query.clause.QueryAliasAttribute;
import org.cmdbuild.dao.query.clause.alias.Alias;
import org.cmdbuild.dao.query.clause.where.FalseWhereClause;
import org.cmdbuild.dao.query.clause.where.Native;
import org.cmdbuild.dao.query.clause.where.WhereClause;
import org.cmdbuild.lookup.Lookup;
import org.cmdbuild.lookup.LookupType;
import org.slf4j.Logger;

import com.google.common.collect.Lists;
import org.cmdbuild.cql.EcqlException;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Attribute;
import static org.cmdbuild.dao.utils.AttributeConversionUtils.rawToSystem;
import org.cmdbuild.lookup.LookupRepository;
import org.cmdbuild.lookup.LookupTypeImpl;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.EntryType;
import static org.cmdbuild.dao.query.clause.alias.EntryTypeAlias.canonicalAlias;
import static org.cmdbuild.dao.query.clause.alias.NameAlias.nameAlias;
import org.cmdbuild.dao.view.DataView;

@Component
public class CqlProcessorServiceImpl implements CqlProcessorService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final DataView dataView;
	private final LookupRepository lookupRepository;

	public CqlProcessorServiceImpl(DataView dataView, LookupRepository lookupStore) {
		this.dataView = checkNotNull(dataView);
		this.lookupRepository = checkNotNull(lookupStore);
	}

	@Override
	public void analyze(CqlQueryImpl q, Map<String, Object> vars, CqlProcessingCallback callback) {
		new CqlProcessor(q, vars, callback).processCql();
	}

	private class CqlProcessor {

		private final CqlQueryImpl query;
		private final Map<String, Object> context;
		private final CqlProcessingCallback callback;

		private Classe fromClass;
		private final Collection<QueryAliasAttribute> attributes;
		private final Collection<WhereClause> whereClauses;
		private final Collection<JoinElement> joinElements;

		private CqlProcessor(CqlQueryImpl query, Map<String, Object> vars, CqlProcessingCallback callback) {
			this.query = query;
			this.context = vars;
			this.callback = callback;
			this.attributes = Lists.newArrayList();
			this.whereClauses = Lists.newArrayList();
			this.joinElements = Lists.newArrayList();
		}

		public void processCql() {
			doProcessCql();
			callback();
		}

		private void callback() {
			callback.from(fromClass);
			callback.attributes(attributes);
			callback.where(isEmpty(whereClauses) ? trueWhereClause() : and(whereClauses));
			if (!joinElements.isEmpty()) {
				callback.distinct();
			}
			for (JoinElement joinElement : joinElements) {
				Domain domain = dataView.findDomain(joinElement.domain);
				Alias domainAlias = (joinElement.domainAlias == null) ? canonicalAlias(domain) : joinElement.domainAlias;
				Classe clazz = dataView.findClasse(joinElement.destination);
				Alias targetAlias = (joinElement.alias == null) ? canonicalAlias(clazz) : joinElement.alias;
				if (joinElement.left) {
					callback.leftJoin(clazz, targetAlias, over(domain, domainAlias));
				} else {
					callback.join(clazz, targetAlias, over(domain, domainAlias));
				}
			}
		}

		private void doProcessCql() {
			ClassDeclarationImpl mainClass = query.getFrom().mainClass();

//			fromClass = mainClass.getClassTable(dataView);
			throw new UnsupportedOperationException("broken");

//			SelectImpl select = query.getSelect();
//			if (!select.isDefault()) {
//				for (SelectItem item : select.get(mainClass).getElements()) {
//					if (item instanceof FieldSelect) {
//						FieldSelect fieldSelect = FieldSelectImpl.class.cast(item);
//						String name = fieldSelect.getName();
//						attributes.add(attribute(fromClass, name));
//					}
//				}
//			}
//
//			WhereImpl where = query.getWhere();
//			for (WhereElement element : where.getElements()) {
//				handleWhereElement(element, fromClass);
//			}
		}

		private void handleWhereElement(WhereElement whereElement, Classe table) {
			if (whereElement instanceof FieldImpl) {
				logger.debug("adding field");
				handleField((FieldImpl) whereElement, table);
			} else if (whereElement instanceof DomainObjectsReference) {
				logger.debug("add domain objs");
				DomainObjectsReferenceImpl domainObjectReference = DomainObjectsReferenceImpl.class
						.cast(whereElement);
				DomainDeclarationImpl domainDeclaration = DomainDeclarationImpl.class.cast(domainObjectReference
						.getScope());
				Domain domain = domainDeclaration.getDirectedDomain(dataView);
				Classe target = domain.getSourceClass().isAncestorOf(fromClass) ? domain.getTargetClass() : domain.getSourceClass();
				joinElements.add(JoinElement.newInstance() //
						.domainName(domain.getName()) //
						.domainAlias(nameAlias(domain.getName() + randomNumeric(10))) //
						.destinationName(target.getName()) //
						.isLeft(false) //
						.build());
				for (WhereElement element : domainObjectReference.getElements()) {
					handleWhereElement(element, target);
				}
			} else if (whereElement instanceof GroupImpl) {
				logger.debug("add group");
				Group group = Group.class.cast(whereElement);
				for (WhereElement element : group.getElements()) {
					handleWhereElement(element, table);
				}
			} else {
				logger.warn("unsupported type '{}'", whereElement.getClass());
			}
		}

		private void handleField(FieldImpl field, Classe table) {
			if (field.getId() instanceof SimpleFieldId) {
				handleSimpleField(SimpleFieldId.class.cast(field.getId()), field, table);
			} else if (field.getId() instanceof LookupFieldId) {
				handleLookupField((LookupFieldId) field.getId(), field, table);
			} else {
				throw new RuntimeException("Complex field ids are not supported!");
			}
		}

		private void handleSimpleField(SimpleFieldId simpleFieldId, FieldImpl field, Classe table) {
			Attribute attribute = getSystemAttribute(simpleFieldId.getId(), table);

			if (attribute == null) {
				attribute = checkNotNull(table.getAttributeOrNull(simpleFieldId.getId()), "attribute not found for field = %s", simpleFieldId);
			}

			QueryAliasAttribute attributeForQuery = attribute(table, attribute.getName());
			List<Object> values = getValuesForField(field, table, attribute);

			WhereClause whereClause = null;
			if (values.isEmpty()) {
				switch (field.getOperator()) {
					case IN:
						whereClause = FalseWhereClause.falseWhereClause();
						break;
					case ISNULL:
					case EQ:
						whereClause = condition(attributeForQuery, isNull());
						break;
					default:
//						logger.warn("empty values for cql field expression, ignoring field");
						throw new EcqlException("empty values for cql field expression with operator = %s", field.getOperator());//TODO check if strict validation is acceptable
				}
			} else {
				switch (field.getOperator()) {
					case LTEQ:
						whereClause = or(condition(attributeForQuery, eq(getOnlyElement(values))), condition(attributeForQuery, lt(getOnlyElement(values))));
						break;
					case GTEQ:
						whereClause = or(condition(attributeForQuery, eq(getOnlyElement(values))), condition(attributeForQuery, gt(getOnlyElement(values))));
						break;
					case LT:
						whereClause = condition(attributeForQuery, lt(getOnlyElement(values)));
						break;
					case GT:
						whereClause = condition(attributeForQuery, gt(getOnlyElement(values)));
						break;
					case EQ:
						whereClause = condition(attributeForQuery, eq(getOnlyElement(values)));
						break;
					case CONT:
						whereClause = condition(attributeForQuery, contains(getOnlyElement(values)));
						break;
					case BGN:
						whereClause = condition(attributeForQuery, beginsWith(getOnlyElement(values)));
						break;
					case END:
						whereClause = condition(attributeForQuery, endsWith(getOnlyElement(values)));
						break;
					case BTW:
						whereClause = and(condition(attributeForQuery, gt(getOnlyElement(values))), condition(attributeForQuery, lt(values.get(1))));
						break;
					case IN:
						whereClause = condition(attributeForQuery, in(values.toArray()));
						break;
					default:
						throw new IllegalArgumentException(format("invalid operator '%s'", field.getOperator()));
				}
			}

			if (whereClause != null) {
				whereClauses.add(field.isNot() ? not(whereClause) : whereClause);
			}

		}

		private Attribute getSystemAttribute(String attributeName, EntryType entryType) {
			Attribute attribute = null;
			if (Const.SystemAttributes.Id.getDBName().equals(attributeName)) {
				attribute = new SystemAttributeImpl(attributeName, entryType, new IntegerAttributeType(), false);
			} else if (Const.SystemAttributes.IdClass.getDBName().equals(attributeName)) {
				attribute = new SystemAttributeImpl(attributeName, entryType, new IntegerAttributeType(), false);
			} else if (Const.SystemAttributes.BeginDate.getDBName().equals(attributeName)) {
				attribute = new SystemAttributeImpl(attributeName, entryType, new DateAttributeType(), false);
			} else if (Const.SystemAttributes.Status.getDBName().equals(attributeName)) {
				attribute = new SystemAttributeImpl(attributeName, entryType, new StringAttributeType(), false);
			}

			return attribute;
		}

		private void handleLookupField(LookupFieldId fid, FieldImpl field, Classe table) {
			throw new RuntimeException("error: broken code");//TODO this whole code block is broken, it either fail with npe, or is quietly ignored
//			LookupOperatorTree node = fid.getTree();
//			if (node.getOperator().equalsIgnoreCase("parent")) {
//				Object value = 0;
//				FieldValue fieldValue = field.getValues().iterator().next();
//				if (node.getAttributeName() == null) {
//					if (fieldValue.getType() == FieldValueType.INT) {
//						value = fieldValue.getValue();
//					} else if (fieldValue.getType() == FieldValueType.STRING) {
//						for (Lookup lookupDto : lookupStore.getAll()) {
//							if (lookupDto.getDescription().equals(fieldValue.getValue().toString())) {
//								value = lookupDto.getId();
//							}
//						}
//					} 
////					else {
////						try {
//////							Field lookupDtoField = LookupImpl.class.getField(node.getAttributeName());
////							for (Lookup lookupDto : lookupStore.getAll()) {
////								Object value = BeanUtils.get
////								if (lookupDtoField.get(lookupDto).equals(fieldValue.getValue().toString())) {
////									value = lookupDto.getId();
////								}
////							}
////						} catch (Exception e) {
////							logger.error("error getting field", e);
////						}
////					}
//					Attribute attribute = table.getAttribute(node.getAttributeName());
//					Object __value = rawToSystem(attribute.getType(), value);
//					QueryAliasAttribute attributeForQuery = attribute(fromClass, attribute.getName());
//					WhereClause whereClause = condition(attributeForQuery, eq(__value));
//					whereClauses.add(field.isNot() ? not(whereClause) : whereClause);
//				} else {
//					throw new RuntimeException("unsupported lookup operator: " + node.getOperator());
//				}
//			}
		}

		private List<Object> getValuesForField(FieldImpl field, Classe classe, Attribute attribute) {
			List<Object> values = Lists.newArrayList();
			for (FieldValue fieldValue : field.getValues()) {
				Object convertedValue = convert(attribute, fieldValue, context);
				logger.debug("converted field value = {} to cql value = '{}'", fieldValue, convertedValue);
				values.add(convertedValue);
			}

			if (!values.isEmpty()) {
				Object firstValue = values.get(0);
				String firstStringValue = (firstValue instanceof String) ? (String) firstValue : null;

				if (firstStringValue != null) {
					attribute.getType().accept(new ForwardingAttributeTypeVisitor() {

						private final CMAttributeTypeVisitor DELEGATE = NullAttributeTypeVisitor.getInstance();

						@Override
						protected CMAttributeTypeVisitor delegate() {
							return DELEGATE;
						}

						@Override
						public void visit(LookupAttributeType attributeType) {
							if (field.getValues().iterator().next().getType() != FieldValueType.NATIVE) {
								try {
									rawToSystem(attributeType, firstStringValue);
								} catch (Exception e) {
									logger.debug("error converting attribute value", e);
									values.clear();

									Lookup searchedLookup = null;
									LookupType lookupType = LookupTypeImpl.builder() //
											.withName(attributeType.getLookupTypeName()) //
											.build();

									for (Lookup lookup : lookupRepository.getAllByType(lookupType)) {
										if (lookup.getDescription().equals(firstStringValue)) {
											searchedLookup = lookup;
											values.add(searchedLookup.getId());
											break;
										}
									}
								}
							}
						}

						@Override
						public void visit(ReferenceAttributeType attributeType) {
							if (field.getValues().iterator().next().getType() != FieldValueType.NATIVE) {
								try {
									Integer.parseInt(firstStringValue);
								} catch (NumberFormatException e) {
									logger.debug("error processing number", e);
									String domainName = attributeType.getDomainName();
									Domain domain = dataView.findDomain(domainName);
									Classe target;
									if (domain.getSourceClass().isAncestorOf(classe)) {
										target = domain.getTargetClass();
									} else {
										target = domain.getSourceClass();
									}

									Alias destinationAlias = nameAlias(String.format("DST-%s-%s", target.getName(),
											randomNumeric(10)));

									whereClauses.add( //
											condition(attribute(destinationAlias, "Description"), eq(firstStringValue)));
									joinElements.add(JoinElement.newInstance() //
											.domainName(domainName) //
											.domainAlias(nameAlias(domain.getName() + randomNumeric(10))) //
											.destinationName(target.getName()) //
											.destinationAlias(destinationAlias) //
											.isLeft(true) //
											.build());
								}
							}
						}

					});
				}
			}
			List<Object> convertedValues = Lists.newArrayList();
			for (Object value : values) {
				Object converted;
				if (field.getValues().iterator().next().getType() == FieldValueType.NATIVE) {
					converted = value;
				} else {
					converted = rawToSystem(attribute.getType(), value);
				}
				convertedValues.add(converted);
			}

			return convertedValues;
		}

		private Object convert(Attribute attribute, FieldValue fieldValue, Map<String, Object> context) {
			switch (fieldValue.getType()) {
				case BOOL:
				case DATE:
				case FLOAT:
				case INT:
				case STRING:
				case TIMESTAMP:
					return fieldValue.getValue().toString();
				case NATIVE:
					return Native.of(fieldValue.getValue().toString());
				case INPUT:
					FieldInputValue fieldInputValue = FieldInputValue.class.cast(fieldValue.getValue());
					String variableName = fieldInputValue.getVariableName();
					Object value = checkNotNull(context.get(variableName), "missing context variable for name = %s", variableName);
					if (value instanceof java.util.Date) {
						return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format((Date) value);
					} else {
						return value.toString();
					}
				case SUBEXPR:
					throw new EcqlException("subqueries are not supported");
				default:
					throw new EcqlException("cannot convert value " + fieldValue.getType().name() + ": " + fieldValue.getValue() + " to string!");
			}
		}
	}

	private static class JoinElement {

		public static class Builder implements org.apache.commons.lang3.builder.Builder<JoinElement> {

			private String domain;
			private Alias domainAlias;
			private String destination;
			private Alias alias;
			private boolean left;

			@Override
			public JoinElement build() {
				return new JoinElement(this);
			}

			public Builder domainName(String domain) {
				this.domain = domain;
				return this;
			}

			public Builder domainAlias(Alias domainAlias) {
				this.domainAlias = domainAlias;
				return this;
			}

			public Builder destinationName(String destination) {
				this.destination = destination;
				return this;
			}

			public Builder destinationAlias(Alias alias) {
				this.alias = alias;
				return this;
			}

			public Builder isLeft(boolean left) {
				this.left = left;
				return this;
			}

		}

		public static Builder newInstance() {
			return new Builder();
		}

		public final String domain;
		public final Alias domainAlias;
		public final String destination;
		public final Alias alias;
		public final boolean left;

		private JoinElement(Builder builder) {
			this.domain = builder.domain;
			this.domainAlias = builder.domainAlias;
			this.destination = builder.destination;
			this.alias = builder.alias;
			this.left = builder.left;
		}

	}

}
