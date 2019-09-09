package org.cmdbuild.dao.query;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptyMap;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.cmdbuild.common.Constants.LOOKUP_CLASS_NAME;
import static org.cmdbuild.dao.constants.Cardinality.CARDINALITY_1N;
import static org.cmdbuild.dao.constants.Cardinality.CARDINALITY_N1;
import static org.cmdbuild.dao.query.ExternalReferenceAliasHandler.EXTERNAL_ATTRIBUTE;
import static org.cmdbuild.dao.query.clause.QueryAliasAttribute.attribute;
import static org.cmdbuild.dao.query.clause.where.WhereClauses.alwaysTrue;
import static org.cmdbuild.dao.query.clause.where.WhereClauses.and;
import static org.cmdbuild.dao.query.clause.where.WhereClauses.not;
import static org.cmdbuild.dao.query.clause.where.WhereClauses.or;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.cmdbuild.dao.constants.Cardinality;
import org.cmdbuild.dao.entrytype.CMEntryTypeVisitor;
import org.cmdbuild.dao.entrytype.CMFunctionCall;
import org.cmdbuild.dao.entrytype.ForwardingEntryTypeVisitor;
import org.cmdbuild.dao.entrytype.NullEntryTypeVisitor;
import org.cmdbuild.dao.entrytype.attributetype.CMAttributeTypeVisitor;
import org.cmdbuild.dao.entrytype.attributetype.ForeignKeyAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ForwardingAttributeTypeVisitor;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.NullAttributeTypeVisitor;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import org.cmdbuild.dao.query.clause.AnyAttribute;
import org.cmdbuild.dao.query.clause.ClassHistory;
import org.cmdbuild.dao.query.clause.DomainHistory;
import org.cmdbuild.dao.query.clause.OrderByClause;
import org.cmdbuild.dao.query.clause.OrderByClause.Direction;
import org.cmdbuild.dao.query.clause.QueryAliasAttribute;
import org.cmdbuild.dao.query.clause.QueryAttribute;
import org.cmdbuild.dao.query.clause.QueryAttributeVisitor;
import org.cmdbuild.dao.query.clause.QueryDomain;
import org.cmdbuild.dao.query.clause.QueryDomain.Source;
import org.cmdbuild.dao.query.clause.alias.Alias;
import org.cmdbuild.dao.query.clause.from.ClassFromClause;
import org.cmdbuild.dao.query.clause.from.FromClause;
import org.cmdbuild.dao.query.clause.from.FunctionFromClause;
import org.cmdbuild.dao.query.clause.join.DirectJoinClause;
import org.cmdbuild.dao.query.clause.join.JoinClause;
import org.cmdbuild.dao.query.clause.join.Over;
import org.cmdbuild.dao.query.clause.where.AndWhereClause;
import org.cmdbuild.dao.query.clause.where.ForwardingWhereClauseVisitor;
import org.cmdbuild.dao.query.clause.where.NotWhereClause;
import org.cmdbuild.dao.query.clause.where.NullWhereClauseVisitor;
import org.cmdbuild.dao.query.clause.where.OrWhereClause;
import org.cmdbuild.dao.query.clause.where.SimpleWhereClause;
import org.cmdbuild.dao.query.clause.where.WhereClause;
import org.cmdbuild.dao.query.clause.where.WhereClauseVisitor;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.EntryType;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.dao.query.clause.alias.EntryTypeAlias.canonicalAlias;
import static org.cmdbuild.dao.query.clause.alias.NameAlias.nameAlias;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.cmdbuild.dao.view.DataView;

public class QuerySpecsBuilderImpl implements QuerySpecsBuilder {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final Alias DEFAULT_ANYCLASS_ALIAS = nameAlias("_*");
	private static final Map<QueryAttribute, Direction> NO_ORDER = emptyMap();

	private Collection<QueryAttribute> attributes;
	private final Collection<JoinClause> joinClauses;
	private final Collection<DirectJoinClause> directJoinClauses;
	private final Map<QueryAttribute, OrderByClause.Direction> orderings;
	private WhereClause whereClause;
	private Long offset;
	private Long limit;
	private boolean distinct;
	private boolean numbered;
	private WhereClause conditionOnNumberedQuery;
	private boolean count;
	private boolean skipDefaultOrdering;

	private final AliasLibrary aliases;

	private final DataView dataView;

	public QuerySpecsBuilderImpl(DataView view) {
		logger.trace("start");
		this.dataView = checkNotNull(view);
		aliases = new AliasLibrary();
		select();
		throw new UnsupportedOperationException("BROKEN - TODO");
//		_from(anyClass(), DEFAULT_ANYCLASS_ALIAS);
//		joinClauses = Lists.newArrayList();
//		directJoinClauses = Lists.newArrayList();
//		orderings = Maps.newLinkedHashMap();
//		whereClause = EmptyWhereClause.emptyWhereClause();
//		conditionOnNumberedQuery = EmptyWhereClause.emptyWhereClause();
	}

	@Override
	public QuerySpecsBuilder select(QueryAttribute... attrDef) {
		logger.trace("select attrs = {}", (Object) attrDef);
		attributes = new HashSet<>();
		for (final QueryAttribute element : attrDef) {
			attributes.add(element);
		}
		return this;
	}

	@Override
	public QuerySpecsBuilder distinct() {
		logger.trace("distinct");
		distinct = true;
		return this;
	}

	@Override
	public QuerySpecsBuilder _from(EntryType entryType, Alias alias) {// TODO: dafu? _from and from? refactor method names
		logger.trace("_from {} as {}", entryType, alias);
		aliases.setFrom(entryType, alias);
		return this;
	}

	@Override
	public QuerySpecsBuilder from(EntryType fromEntryType, Alias fromAlias) {// TODO: dafu? _from and from? refactor method names
		logger.trace("from {} as {}", fromEntryType, fromAlias);
		aliases.setFrom(transform(fromEntryType), fromAlias);
		return this;
	}

	private void addDirectJoinClausesForLookup(Iterable<Attribute> lookupAttributes, EntryType entryType, Alias entryTypeAlias) {
		Classe lookupClass = dataView.findClasse(LOOKUP_CLASS_NAME);
		for (Attribute attribute : lookupAttributes) {
			Alias lookupClassAlias = nameAlias(new ExternalReferenceAliasHandler(entryType, attribute).buildTableAlias());
			aliases.addAliasIfMissing(lookupClassAlias);
			DirectJoinClause lookupJoinClause = DirectJoinClause.newInstance() //
					.leftJoin(lookupClass) //
					.as(lookupClassAlias) //
					.on(attribute(lookupClassAlias, "Id")) //
					.equalsTo(attribute(entryTypeAlias, attribute.getName())) //
					.build();
			addDirectJoinClause(lookupJoinClause);
		}
	}

	private void addDirectJoinClausesForForeignKey(Iterable<Attribute> foreignKeyAttributes, EntryType entryType, Alias entryTypeAlias) {

		for (Attribute attribute : foreignKeyAttributes) {
			ForeignKeyAttributeType attributeType = (ForeignKeyAttributeType) attribute.getType();
			Classe referencedClass = dataView.findClasse(attributeType.getForeignKeyDestinationClassName());
			Alias referencedClassAlias = nameAlias(new ExternalReferenceAliasHandler(entryType, attribute).buildTableAlias());
			aliases.addAliasIfMissing(referencedClassAlias);
			DirectJoinClause foreignKeyJoinClause = DirectJoinClause.newInstance() //
					.leftJoin(referencedClass) //
					.as(referencedClassAlias) //
					.on(attribute(referencedClassAlias, "Id")) //
					.equalsTo(attribute(entryTypeAlias, attribute.getName())) //
					.build();
			addDirectJoinClause(foreignKeyJoinClause);
		}
	}

	private void addSubclassesJoinClauses(EntryType entryType, Alias entryTypeAlias) {
		Map<Alias, Classe> descendantsByAlias = Maps.newHashMap();
		entryType.accept(new ForwardingEntryTypeVisitor() {

			private final CMEntryTypeVisitor delegate = NullEntryTypeVisitor.getInstance();

			@Override
			protected CMEntryTypeVisitor delegate() {
				return delegate;
			}

			@Override
			public void visit(Classe type) {
				throw new UnsupportedOperationException();
//				for (Classe descendant : type.getDescendants()) {
//					Alias alias = canonicalAlias(descendant);
//					aliases.addAliasIfMissing(alias);
//					descendantsByAlias.put(alias, descendant);
//				}
			}

		});
		whereClause.accept(new ForwardingWhereClauseVisitor() {

			private final WhereClauseVisitor delegate = NullWhereClauseVisitor.getInstance();

			@Override
			protected WhereClauseVisitor delegate() {
				return delegate;
			}

			@Override
			public void visit(AndWhereClause whereClause) {
				for (WhereClause subWhereClause : whereClause.getClauses()) {
					subWhereClause.accept(this);
				}
			}

			@Override
			public void visit(OrWhereClause whereClause) {
				for (WhereClause subWhereClause : whereClause.getClauses()) {
					subWhereClause.accept(this);
				}
			}

			@Override
			public void visit(SimpleWhereClause whereClause) {
				QueryAttribute attribute = whereClause.getAttribute();
				Alias alias = attribute.getAlias();
				aliases.addAliasIfMissing(alias);
				if (descendantsByAlias.containsKey(alias)) {
					Classe type = descendantsByAlias.get(alias);
					DirectJoinClause clause = DirectJoinClause.newInstance() //
							.leftJoin(type) //
							.as(alias) //
							.on(attribute(alias, "Id")) //
							.equalsTo(attribute(entryTypeAlias, "Id")) //
							.build();
					addDirectJoinClause(clause);
				}
			}

		});

	}

	private void addDirectJoinClause(DirectJoinClause directJoinClause) {
		logger.trace("addDirectJoinClause {}", directJoinClause);
		directJoinClauses.add(directJoinClause);
	}

	@Override
	public QuerySpecsBuilder from(Classe cmClass) {
		logger.trace("from ", cmClass);
		return from(transform(cmClass), canonicalAlias(cmClass));
	}

	/*
	 * TODO: Consider more join levels (join with join tables)
	 */
	@Override
	public QuerySpecsBuilder join(Classe joinClass, Over overClause) {
		return join(joinClass, canonicalAlias(joinClass), overClause);
	}

	@Override
	public QuerySpecsBuilder join(Classe joinClass, Alias joinClassAlias, Over overClause) {
		// from must be a class
		Classe fromClass = (Classe) aliases.getFrom();
		JoinClause joinClause = JoinClause.newJoinClause(dataView, dataView, transform(fromClass))
				.withDomain(transform(overClause.getDomain()), overClause.getAlias()) //
				.withTarget(transform(joinClass), joinClassAlias) //
				.build();
		logger.debug("join with classes = {} over domains = {}", joinClause.getTargets(), joinClause.getQueryDomains());
		return join(joinClause, joinClassAlias, overClause);
	}

	@Override
	public QuerySpecsBuilder join(Classe joinClass, Alias joinClassAlias, Over overClause,
			Source source) {
		// from must be a class
		Classe fromClass = (Classe) aliases.getFrom();
		JoinClause joinClause = JoinClause.newJoinClause(dataView, dataView, transform(fromClass))
				.withDomain(new QueryDomain(transform(overClause.getDomain()), source), overClause.getAlias()) //
				.withTarget(transform(joinClass), joinClassAlias) //
				.build();
		return join(joinClause, joinClassAlias, overClause);
	}

	// TODO refactor to have a single join method
	@Override
	public QuerySpecsBuilder leftJoin(Classe joinClass, Alias joinClassAlias, Over overClause) {
		// from must be a class
		Classe fromClass = (Classe) aliases.getFrom();
		JoinClause join = JoinClause.newJoinClause(dataView, dataView, fromClass)
				.withDomain(transform(overClause.getDomain()), overClause.getAlias()) //
				.withTarget(transform(joinClass), joinClassAlias) //
				.left() //
				.build();
		return join(join, joinClassAlias, overClause);
	}

	@Override
	public QuerySpecsBuilder leftJoin(Classe joinClass, Alias joinClassAlias, Over overClause, Source source) {
		// from must be a class
		Classe fromClass = (Classe) aliases.getFrom();
		JoinClause join = JoinClause.newJoinClause(dataView, dataView, fromClass)
				.withDomain(new QueryDomain(transform(overClause.getDomain()), source), overClause.getAlias()) //
				.withTarget(transform(joinClass), joinClassAlias) //
				.left() //
				.build();
		return join(join, joinClassAlias, overClause);
	}

	private QuerySpecsBuilder join(JoinClause joinClause, Alias joinClassAlias, Over overClause) {
		logger.trace("join {} as {} over {}", joinClause, joinClassAlias, overClause);
		joinClauses.add(joinClause);
		aliases.addAlias(joinClassAlias);
		aliases.addAlias(overClause.getAlias());
		return this;
	}

	@Override
	public QuerySpecsBuilder where(WhereClause clause) {
		logger.trace("where {}", clause);
		whereClause = (clause == null) ? alwaysTrue() : clause;
		return this;
	}

	@Override
	public QuerySpecsBuilder offset(Number offset) {
		logger.trace("offset {}", offset);
		this.offset = offset.longValue();
		return this;
	}

	@Override
	public QuerySpecsBuilder limit(Number limit) {
		logger.trace("limit {}", limit);
		this.limit = limit.longValue();
		return this;
	}

	@Override
	public QuerySpecsBuilder orderBy(QueryAttribute attribute, Direction direction) {
		logger.trace("orderBy {} {}", attribute, direction);
		orderings.put(attribute, direction);
		return this;
	}

	@Override
	public QuerySpecsBuilder orderBy(Map<QueryAttribute, Direction> order) {
		logger.trace("orderBy {}", order);
		orderings.putAll(defaultIfNull(order, NO_ORDER));
		return this;
	}

	@Override
	public QuerySpecsBuilder numbered() {
		logger.trace("numbered");
		numbered = true;
		return this;
	}

	@Override
	public QuerySpecsBuilder numbered(WhereClause whereClause) {
		logger.trace("numbered {}", whereClause);
		numbered = true;
		conditionOnNumberedQuery = whereClause;
		return this;
	}

	@Override
	public QuerySpecsBuilder count() {
		logger.trace("count");
		count = true;
		return this;
	}

	@Override
	public QuerySpecsBuilder skipDefaultOrdering() {
		logger.trace("skipDefaultOrdering");
		skipDefaultOrdering = true;
		return this;
	}

	@Override
	public QuerySpecs build() {
		logger.trace("build");
		FromClause fromClause = createFromClause();

		EntryType fromEntryType = fromClause.getType();
		Alias fromAlias = fromClause.getAlias();
		throw new UnsupportedOperationException("BROKEN - TODO");
//		EntryTypeAnalyzer entryTypeAnalyzer = inspect(fromEntryType, new Predicate<Attribute>() {
//
//			private final boolean anyAttribute = !FluentIterable.from(attributes) //
//					.filter(AnyAttribute.class) //
//					.filter(withAlias(fromAlias)) //
//					.isEmpty();
//			private final Collection<String> names = FluentIterable.from(attributes) //
//					.filter(QueryAliasAttribute.class) //
//					.filter(withAlias(fromAlias)) //
//					.transform(name()) //
//					.toList();
//
//			@Override
//			public boolean apply(Attribute input) {
//				return anyAttribute || names.contains(input.getName());
//			}
//
//		}, dataView);
//		if (entryTypeAnalyzer.hasExternalReferences()) {
//			//TODO add multitenant here
//			addDirectJoinClausesForLookup(entryTypeAnalyzer.getLookupAttributes(), fromEntryType, fromAlias);
//			addDirectJoinClausesForReference(entryTypeAnalyzer.getReferenceAttributes(), fromEntryType, fromAlias);
//			addDirectJoinClausesForForeignKey(entryTypeAnalyzer.getForeignKeyAttributes(), fromEntryType, fromAlias);
//		}
//		addSubclassesJoinClauses(fromEntryType, fromAlias);
//
//		QuerySpecsImpl querySpecs = QuerySpecsImpl.newInstance() //
//				.fromClause(fromClause) //
//				.distinct(distinct) //
//				.numbered(numbered) //
//				.conditionOnNumberedQuery(conditionOnNumberedQuery) //
//				.count(count) //
//				.skipDefaultOrdering(skipDefaultOrdering) //
//				.build();
//
//		for (JoinClause joinClause : joinClauses) {
//			if (!joinClause.hasTargets()) {
//				return new EmptyQuerySpecs();
//			}
//			querySpecs.addJoin(joinClause);
//		}
//		for (DirectJoinClause directJoinClause : directJoinClauses) {
//			querySpecs.addDirectJoin(directJoinClause);
//			QueryAliasAttribute externalRefAttribute = attribute(directJoinClause.getTargetClassAlias(), EXTERNAL_ATTRIBUTE);
//			querySpecs.addSelectAttribute(checkAlias(externalRefAttribute));
//		}
//
//		querySpecs.setWhereClause(adapt(whereClause, fromClause, querySpecs));
//		querySpecs.setOffset(offset);
//		querySpecs.setLimit(limit);
//		for (QueryAttribute attribute : orderings.keySet()) {
//			logger.trace("processing ordering attr = {}", attribute);
//			QueryAttribute processedAttribute = new OrderByQueryAttributeVisitor(querySpecs, fromEntryType, fromAlias).visitAndReturn(attribute);
//			logger.trace("processed ordering attr = {}", processedAttribute);
//			querySpecs.addOrderByClause(new OrderByClause(processedAttribute, orderings.get(attribute)));
//		}
//
//		for (QueryAttribute qa : attributes) {
//			querySpecs.addSelectAttribute(checkAlias(qa));
//		}
//
//		return querySpecs;
	}

	private void addDirectJoinClausesForReference(Iterable<Attribute> referenceAttributes, EntryType entryType, Alias entryTypeAlias) {
		logger.trace("addDirectJoinClausesForReference for {} reference attributes", Iterables.size(referenceAttributes));
		for (Attribute attribute : referenceAttributes) {
			logger.debug("add join for reference attribute = {}", attribute.getName());

			Alias referencedClassAlias = nameAlias(new ExternalReferenceAliasHandler(entryType, attribute).buildTableAlias());
//			Alias referencedClassAlias = name(buildCrossTenantClassDescTableAlias(entryType, attribute));
			logger.trace("add alias = {}", referencedClassAlias);
			aliases.addAliasIfMissing(referencedClassAlias);

//			DirectJoinClause directJoinClause = buildJoinForDescription(referencedClassAlias, entryTypeAlias, attribute);
//			addDirectJoinClause(directJoinClause);
		}
	}

	private WhereClause adapt(WhereClause whereClause, FromClause fromClause, QuerySpecsImpl querySpecsImpl) {
		return new ForwardingWhereClauseVisitor() {

			private final WhereClauseVisitor DELEGATE = NullWhereClauseVisitor.getInstance();

			private WhereClause output = whereClause;

			@Override
			protected WhereClauseVisitor delegate() {
				return DELEGATE;
			}

			public WhereClause adapt() {
				output.accept(this);
				return output;
			}

			@Override
			public void visit(AndWhereClause whereClause) {
				output = and(adaptAll(whereClause.getClauses()));
			}

			@Override
			public void visit(NotWhereClause whereClause) {
				output = not(adaptSingle(whereClause.getClause()));
			}

			@Override
			public void visit(OrWhereClause whereClause) {
				output = or(adaptAll(whereClause.getClauses()));
			}

			@Override
			public void visit(SimpleWhereClause whereClause) {
				output = new QueryAttributeVisitor() {

					private WhereClause output = whereClause;

					public WhereClause adapt() {
						whereClause.getAttribute().accept(this);
						return output;
					}

					@Override
					public void accept(AnyAttribute value) {
						Collection<WhereClause> elements = new HashSet<>();
						for (Attribute element : fromClause.getType().getCoreAttributes()) {
							elements.add(new ForwardingAttributeTypeVisitor() {

								private final CMAttributeTypeVisitor DELEGATE = NullAttributeTypeVisitor.getInstance();

								private WhereClause output = SimpleWhereClause.newInstance() //
										.withAttribute(attribute(value.getAlias(), element.getName())) //
										.withOperatorAndValue(whereClause.getOperator()) //
										.withAttributeNameCast(whereClause.getAttributeNameCast()) //
										.build();

								@Override
								protected CMAttributeTypeVisitor delegate() {
									return DELEGATE;
								}

								public WhereClause adapt() {
									element.getType().accept(this);
									return output;
								}

								@Override
								public void visit(ForeignKeyAttributeType attributeType) {
									Alias alias = alias(whereClause.getAttribute());
									output = SimpleWhereClause.newInstance() //
											.withAttribute(attribute(alias, EXTERNAL_ATTRIBUTE)) //
											.withOperatorAndValue(whereClause.getOperator()) //
											.withAttributeNameCast(whereClause.getAttributeNameCast()) //
											.build();
									addDirectJoin(attributeType.getForeignKeyDestinationClassName(), alias);
								}

								@Override
								public void visit(LookupAttributeType attributeType) {
									Alias alias = alias(whereClause.getAttribute());
									output = SimpleWhereClause.newInstance() //
											.withAttribute(attribute(alias, EXTERNAL_ATTRIBUTE)) //
											.withOperatorAndValue(whereClause.getOperator()) //
											.withAttributeNameCast(whereClause.getAttributeNameCast()) //
											.build();
									addDirectJoin(LOOKUP_CLASS_NAME, alias);
								}

								@Override
								public void visit(ReferenceAttributeType attributeType) {
									Alias alias = alias(whereClause.getAttribute());
									output = SimpleWhereClause.newInstance() //
											.withAttribute(attribute(alias, EXTERNAL_ATTRIBUTE)) //
											.withOperatorAndValue(whereClause.getOperator()) //
											.withAttributeNameCast(whereClause.getAttributeNameCast()) //
											.build();
									Classe target;
									Domain domain = dataView.findDomain(attributeType.getDomainName());
									switch (Cardinality.of(domain.getCardinality())) {

										case CARDINALITY_1N:
											target = domain.getSourceClass();
											break;

										case CARDINALITY_N1:
											target = domain.getTargetClass();
											break;

										default:
											throw new IllegalArgumentException(domain.getCardinality());

									}
									addDirectJoin(target.getName(), alias);
								}

								private Alias alias(QueryAttribute value) {
									Alias alias
											= nameAlias(new ExternalReferenceAliasHandler(value.getAlias().toString(), element)
													.buildTableAlias());
									aliases.addAliasIfMissing(alias);
									return alias;
								}

								private void addDirectJoin(String targetName, Alias alias) {
									querySpecsImpl.addDirectJoin(DirectJoinClause.newInstance() //
											.leftJoin(dataView.findClasse(targetName)) //
											.as(alias) //
											.on(attribute(alias, "Id")) //
											.equalsTo(attribute(fromClause.getAlias(), element.getName())) //
											.build());
								}

							}.adapt());

						}
						output = or(elements);
					}

					@Override
					public void visit(QueryAliasAttribute value) {
					}

				}.adapt();
			}

			private Iterable<WhereClause> adaptAll(Iterable<? extends WhereClause> inputs) {
				return FluentIterable.from(inputs) //
						.transform((WhereClause input) -> adaptSingle(input));
			}

			private WhereClause adaptSingle(WhereClause input) {
				return QuerySpecsBuilderImpl.this.adapt(input, fromClause, querySpecsImpl);
			}

		}.adapt();
	}

	private FromClause createFromClause() {
		FromClause output;
		if (aliases.getFrom() instanceof CMFunctionCall) {
			output = new FunctionFromClause(aliases.getFrom(), aliases.getFromAlias());
		} else {
			output = new ClassFromClause(dataView, aliases.getFrom(), aliases.getFromAlias());
		}
		return output;
	}

	private QueryAttribute checkAlias(QueryAttribute queryAttribute) {
		aliases.checkAlias(queryAttribute.getAlias());
		return queryAttribute;
	}

//	private DirectJoinClause buildJoinForDescription(Alias referenceAlias, Alias fromAlias, Attribute attribute) {
//		logger.trace("buildJoinForDescription");
//		return DirectJoinClause.newInstance()
//				.leftJoin(viewForBuild.findClasse("_Class_Description")).as(referenceAlias)
//				.on(attribute(referenceAlias, "Id")).equalsTo(attribute(fromAlias, attribute.getName()))
//				.build();
//	}
	@Override
	public QueryResult run() {
		QuerySpecs querySpecs = build();
		logger.debug("run query = {}", querySpecs);
		return dataView.executeQuery(querySpecs);
	}

	private <T extends EntryType> T transform(T entryType) {
		try {
			return new CMEntryTypeVisitor() {

				private T transformed;

				@Override
				public void visit(Classe type) {
					transformed = (T) dataView.getClassOrNull(type.getId());
					if (type instanceof ClassHistory) {
						transformed = (T) ClassHistory.history((Classe) transformed);
					}
				}

				@Override
				public void visit(Domain type) {
					transformed = (T) dataView.findDomain(type.getId());
					if (type instanceof DomainHistory) {
						transformed = (T) DomainHistory.history((Domain) transformed);
					}
				}

				@Override
				public void visit(CMFunctionCall type) {
					// function does not need transformation
					transformed = entryType;
				}

				public T transform(T entryType) {
					entryType.accept(this);
					return transformed;
				}

			}.transform(entryType);
		} catch (Exception e) {
			return entryType;
		}
	}

	private static class AliasLibrary {

		private final Set<Alias> aliases = set();
		private EntryType fromType;
		private Alias fromAlias;

		public void addAlias(Alias alias) {
			if (aliases.contains(alias)) {
				throw new IllegalArgumentException("Duplicate alias");
			}
			aliases.add(alias);
		}

		public void addAliasIfMissing(Alias alias) {
			if (!aliases.contains(alias)) {
				aliases.add(alias);
			}
		}

		public void setFrom(EntryType type, Alias alias) {
			this.aliases.remove(this.fromAlias);
			addAlias(alias);
			this.fromType = type;
			this.fromAlias = alias;
		}

		public EntryType getFrom() {
			return fromType;
		}

		public Alias getFromAlias() {
			return fromAlias;
		}

		public void checkAlias(Alias alias) {
			if (!aliases.contains(alias)) {
				throw new NoSuchElementException("Alias " + alias + " was not found");
			}
		}

		public boolean containsAlias(Alias alias) {
			return aliases.contains(alias);
		}

	}

	private class OrderByQueryAttributeVisitor implements QueryAttributeVisitor {

		private final QuerySpecsImpl querySpecs;
		private final EntryType fromEntryType;
		private final Alias fromAlias;

		private QueryAttribute output;

		private OrderByQueryAttributeVisitor(QuerySpecsImpl querySpecs, EntryType fromEntryType, Alias fromAlias) {
			this.querySpecs = querySpecs;
			this.fromEntryType = fromEntryType;
			this.fromAlias = fromAlias;
		}

		public QueryAttribute visitAndReturn(QueryAttribute attribute) {
			output = attribute;
			attribute.accept(this);
			return output;
		}

		@Override
		public void accept(AnyAttribute value) {
			throw new IllegalArgumentException(value.toString());
		}

		@Override
		public void visit(QueryAliasAttribute value) {
			output = new OrderByForwardingAttributeVisitor().visitAndReturn(value);
		}

		private class OrderByForwardingAttributeVisitor extends ForwardingAttributeTypeVisitor {

			private final CMAttributeTypeVisitor DELEGATE = NullAttributeTypeVisitor.getInstance();

			private Attribute attribute;
			private CardAttributeType<?> type;
			private QueryAliasAttribute value, output;

			@Override
			protected CMAttributeTypeVisitor delegate() {
				return DELEGATE;
			}

			public QueryAttribute visitAndReturn(QueryAliasAttribute value) {
				this.output = this.value = value;
				attribute = fromEntryType.getAttribute(value.getName());
				type = attribute.getType();

				type.accept(this);

				if (output.equals(value)) {
					int candidates = 0;
					for (QueryAttribute element : attributes) {
						if (new QueryAttributeVisitor() {

							private final QueryAttribute _value = value;
							private boolean output = false;

							public boolean candidate() {
								element.accept(this);
								return output;
							}

							@Override
							public void accept(AnyAttribute value) {
								output = value.getAlias().equals(_value.getAlias());
							}

							@Override
							public void visit(QueryAliasAttribute value) {
								output = value.equals(_value);
							}

						}.candidate()) {
							candidates++;
						}
					}
					if (candidates == 0) {
						attributes.add(output);
					}
				} else {
					attributes.add(output);
				}
				return output;
			}

			@Override
			public void visit(ForeignKeyAttributeType attributeType) {
				Alias alias = alias(value);
				output = attribute(alias, EXTERNAL_ATTRIBUTE);
				addDirectJoin(attributeType.getForeignKeyDestinationClassName(), alias);
			}

			@Override
			public void visit(LookupAttributeType attributeType) {
				Alias alias = alias(value);
				output = attribute(alias, EXTERNAL_ATTRIBUTE);
				addDirectJoin(LOOKUP_CLASS_NAME, alias);
			}

			@Override
			public void visit(ReferenceAttributeType attributeType) {
				Alias alias = alias(value);
				output = attribute(alias, EXTERNAL_ATTRIBUTE);
//				addDirectJoin(buildJoinForDescription(alias, fromAlias, attribute));
			}

			private Alias alias(QueryAttribute value) {
				Alias alias = nameAlias(new ExternalReferenceAliasHandler(value.getAlias().toString(), attribute).buildTableAlias());
				if (!aliases.containsAlias(alias)) {
					aliases.addAlias(alias);
				}
				return alias;
			}

			private void addDirectJoin(String targetName, Alias alias) {
				logger.trace("add join for ordering from {} as {}", targetName, alias);
				addDirectJoin(DirectJoinClause.newInstance() //
						.leftJoin(dataView.findClasse(targetName)) //
						.as(alias) //
						.on(attribute(alias, "Id")) //
						.equalsTo(attribute(fromAlias, attribute.getName())) //
						.build());
			}

			private void addDirectJoin(DirectJoinClause directJoinClause) {
				logger.trace("add join for ordering  {}", directJoinClause);
				querySpecs.addDirectJoin(directJoinClause);
			}

		}
	}

}
