package org.cmdbuild.data2.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.addAll;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Iterables.toArray;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.String.format;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.cmdbuild.common.Constants.DESCRIPTION_ATTRIBUTE;
import static org.cmdbuild.dao.postgres.Const.SystemAttributes.Id;
import static org.cmdbuild.dao.query.clause.Functions.queryAliasAttribute;
import static org.cmdbuild.dao.query.clause.QueryAliasAttribute.attribute;
import static org.cmdbuild.dao.query.clause.join.Over.over;
import static org.cmdbuild.dao.query.clause.where.AndWhereClause.and;
import static org.cmdbuild.dao.query.clause.where.InOperatorAndValue.in;
import static org.cmdbuild.dao.query.clause.where.NullOperatorAndValue.isNull;
import static org.cmdbuild.dao.query.clause.where.SimpleWhereClause.condition;
import static org.cmdbuild.dao.query.clause.where.TrueWhereClause.trueWhereClause;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.cmdbuild.dao.entrytype.CMEntryTypeVisitor;
import org.cmdbuild.dao.entrytype.ForwardingEntryTypeVisitor;
import org.cmdbuild.dao.entrytype.NullEntryTypeVisitor;
import org.cmdbuild.dao.query.QuerySpecsBuilder;
import org.cmdbuild.dao.query.clause.OrderByClause;
import org.cmdbuild.dao.query.clause.OrderByClause.Direction;
import org.cmdbuild.dao.query.clause.QueryAliasAttribute;
import org.cmdbuild.dao.query.clause.QueryAttribute;
import org.cmdbuild.dao.query.clause.QueryDomain.Source;
import org.cmdbuild.dao.query.clause.alias.Alias;
import org.cmdbuild.dao.query.clause.join.Over;
import org.cmdbuild.dao.query.clause.where.WhereClause;
import org.cmdbuild.dao.postgres.legacy.relationquery.SorterMapper;
import org.cmdbuild.dao.postgres.legacy.relationquery.JsonAttributeFilterBuilder;
import org.cmdbuild.dao.postgres.legacy.relationquery.JsonFullTextQueryBuilder;
import org.cmdbuild.dao.postgres.legacy.relationquery.JsonSorterMapper;
import org.json.JSONException;

import static java.util.stream.Collectors.toList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.common.data.QueryOptions;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Attribute;
import static org.cmdbuild.dao.query.clause.AnyAttribute.anyAttribute;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CqlFilter;
import org.cmdbuild.data.filter.RelationFilterCardInfo;
import org.cmdbuild.data.filter.RelationFilterRule.RelationFilterDirection;
import org.cmdbuild.data.filter.RelationFilterRule.RelationFilterRuleType;
import org.cmdbuild.data2.impl.QuerySpecsBuilderService.QuerySpecsBuilderHelper;
import org.springframework.stereotype.Component;
import org.cmdbuild.data.filter.beans.CmdbFilterImpl;
import org.cmdbuild.data.filter.beans.CqlFilterImpl;
import org.cmdbuild.ecql.EcqlService;
import static org.cmdbuild.common.Constants.BASE_PROCESS_CLASS_NAME;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.EntryType;
import static org.cmdbuild.dao.query.clause.alias.EntryTypeAlias.canonicalAlias;
import static org.cmdbuild.dao.query.clause.alias.NameAlias.nameAlias;
import org.cmdbuild.cql.legacy.CqlService;
import org.cmdbuild.cql.legacy.CqlProcessingCallback;
import static org.cmdbuild.dao.constants.SystemAttributes.PROCESS_CLASS_RESERVED_ATTRIBUTES;
import org.cmdbuild.dao.view.DataView;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@Component
public class QuerySpecsBuilderServiceImpl implements QuerySpecsBuilderService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final DataView dataView;
	private final CqlService cqlService;
	private final EcqlService ecqlService;

	public QuerySpecsBuilderServiceImpl(DataView dataView, CqlService cqlService, EcqlService ecqlService) {
		this.dataView = checkNotNull(dataView);
		this.cqlService = checkNotNull(cqlService);
		this.ecqlService = checkNotNull(ecqlService);
	}

	@Override
	public QuerySpecsBuilderHelper helper() {
		return new QuerySpecsBuilderHelperImpl();
	}

	private class QuerySpecsBuilderHelperImpl implements QuerySpecsBuilderHelper {

		private QueryOptions queryOptions;
		private EntryType entryType;
		private QuerySpecsBuilder querySpecsBuilder;
		private final List<WhereClause> whereClauses = list();

		@Override
		public QuerySpecsBuilderHelper withQueryOptions(QueryOptions queryOptions) {
			this.queryOptions = checkNotNull(queryOptions);
			return this;
		}

		@Override
		public QuerySpecsBuilderHelper withEntryType(EntryType entryType) {
			this.entryType = checkNotNull(entryType);
			return this;
		}

		@Override
		public Alias getAlias() {
			return canonicalAlias(entryType);
		}

		@Override
		public QuerySpecsBuilder builder() {
			throw new UnsupportedOperationException("BROKEN - TODO");
//			checkArgument(querySpecsBuilder == null);
//			logger.debug("create query specs builder");
//			checkNotNull(queryOptions);
//			List<QueryAliasAttribute> attributeSubsetForSelect = attributes(queryOptions).stream()
//					.map(attribute(entryType))
//					.filter(Attribute.class::isInstance)
//					.map(queryAliasAttribute(entryType))
//					.collect(toList());
//			QueryAttribute[] attributesArray;
//			if (isEmpty(attributeSubsetForSelect)) {
//				attributesArray = new QueryAttribute[]{anyAttribute(entryType)};
//			} else {
//				attributesArray = toArray(attributeSubsetForSelect, QueryAttribute.class);
//			}
//			querySpecsBuilder = dataView.select(attributesArray).from(entryType, (getAlias()));
//			try {
//				fillQuerySpecsBuilderWithFilterOptions();
//			} catch (JSONException ex) {
//				logger.error("Bad filter. The filter is {} ", queryOptions.getFilter().toString());
//				logger.error("Bad filter", ex);
//			}
//			querySpecsBuilder
//					.limit(queryOptions.getLimit())
//					.offset(queryOptions.getOffset());
//			addSortingOptions(querySpecsBuilder);
//			return querySpecsBuilder;
		}

		/**
		 * Returns all attributes (explicitly required plus system ones when
		 * needed).
		 */
		private Collection<String> attributes(QueryOptions queryOptions) {
			return newHashSet(concat(queryOptions.getAttributes(), new ForwardingEntryTypeVisitor() {

				private final CMEntryTypeVisitor DELEGATE = NullEntryTypeVisitor.getInstance();

				private final Collection<String> output = newHashSet();

				@Override
				protected CMEntryTypeVisitor delegate() {
					return DELEGATE;
				}

				public Iterable<String> systemAttributes() {
					if (!isEmpty(queryOptions.getAttributes())) {
						entryType.accept(this);
					}
					return output;
				}

				@Override
				public void visit(Classe type) {
					if (dataView.getClasse(BASE_PROCESS_CLASS_NAME).isAncestorOf(type)) {
						addAll(output, PROCESS_CLASS_RESERVED_ATTRIBUTES);
					}
				}

			}.systemAttributes()));
		}

		private void addSortingOptions(QuerySpecsBuilder querySpecsBuilder) {
			SorterMapper sorterMapper = new JsonSorterMapper(entryType, queryOptions.getSorters(), getAlias());
			List<OrderByClause> clauses = sorterMapper.deserialize();
			if (clauses.isEmpty()) {
				if (entryType != null && entryType.getAttributeOrNull(DESCRIPTION_ATTRIBUTE) != null) {
					querySpecsBuilder.orderBy(attribute(getAlias(), DESCRIPTION_ATTRIBUTE), Direction.ASC);
				}
			} else {
				for (OrderByClause clause : clauses) {
					querySpecsBuilder.orderBy(clause.getAttribute(), clause.getDirection());
				}
			}
		}

		/**
		 * TODO: split into different private methods
		 */
		private void fillQuerySpecsBuilderWithFilterOptions() throws JSONException {

			CmdbFilter filter = queryOptions.getFilter();

			if (filter.hasEcqlFilter()) {
				String cqExprFromEcqlId = ecqlService.prepareCqlExpression(filter.getEcqlFilter().getEcqlId(), filter.getEcqlFilter().getJsContext());
				filter = CmdbFilterImpl
						.copyOf(filter)
						.withEcqlFilter(null)
						.withCqlFilter(new CqlFilterImpl(cqExprFromEcqlId))
						.build();
			}

			if (filter.hasCqlFilter()) {
				CqlFilter cqlFilter = filter.getCqlFilter();
				evaluateCqlFilter(cqlFilter);
			}
			checkNotNull(entryType, "entry type not provided, and not supplied from cql");

			if (filter.hasFulltextFilter()) {
				logger.debug("process full text query");
				whereClauses.add(JsonFullTextQueryBuilder.newInstance()
						.withFullTextQuery(filter.getFulltextFilter().getQuery())
						.withEntryType(entryType)
						.build());
			}

			if (filter.hasCqlFilter()) {
				logger.debug("process cql key");
				querySpecsBuilder.where(isEmpty(whereClauses) ? trueWhereClause() : and(whereClauses));
				return;
			}

			// filter on attributes of the source class
			if (filter.hasAttributeFilter()) {
				logger.debug("process attribute key");
				whereClauses.add(new JsonAttributeFilterBuilder()
						.withFilter(filter.getAttributeFilter())
						.withEntryType(entryType)
						.withDataView(dataView)
						.build());
			}

			// filter on relations
			if (filter.hasRelationFilter()) {
				logger.debug("process relation key");
				querySpecsBuilder.distinct();

				filter.getRelationFilter().getRelationFilterRules().forEach((rule) -> {

					String domainName = rule.getDomain();

					RelationFilterDirection source = rule.getDirection();
					Domain domain = dataView.findDomain(domainName);
					String destinationName = rule.getDestination();
					Classe destinationClass = dataView.findClasse(destinationName);
					boolean left = RelationFilterRuleType.NOONE.equals(rule.getType());

					Alias destinationAlias = nameAlias(format("DST-%s-%s", destinationName, randomNumeric(10)));
					Alias domainAlias = nameAlias(format("DOM-%s-%s", domainName, randomNumeric(10)));

					if (left) {
						querySpecsBuilder.leftJoin(destinationClass, destinationAlias, over(domain, domainAlias), Source.valueOf(source.name()));
					} else {
						querySpecsBuilder.join(destinationClass, destinationAlias, over(domain, domainAlias), Source.valueOf(source.name()));
					}

					switch (rule.getType()) {
						case ONEOF:
							List<Long> oneOfIds = rule.getCardInfos().stream().map(RelationFilterCardInfo::getId).collect(toList());
							whereClauses.add(condition(attribute(destinationAlias, Id.getDBName()), in(oneOfIds.toArray())));
							break;
						case NOONE:
							whereClauses.add(condition(attribute(destinationAlias, Id.getDBName()), isNull()));
							break;
						case ANY:
						/**
						 * Should be empty. WhereClauses not added because I can
						 * detect if a card is in relation with ANY card, using only
						 * the JOIN clause
						 */
					}

				});
			}
			if (!whereClauses.isEmpty()) {
				querySpecsBuilder.where(and(whereClauses));
			} else {
				querySpecsBuilder.where(trueWhereClause());
			}
		}

		private void evaluateCqlFilter(CqlFilter cqlFilter) {
			logger.info("Filter is a CQL filter");
			Map<String, Object> context = queryOptions.getParameters();
			logger.debug("processing cql code = '{}' with context = {}", cqlFilter.getCqlExpression(), context);
			cqlService.compileAndAnalyze(cqlFilter.getCqlExpression(), context, new MyCQLFilterEvaluatorCallback());
			logger.debug("cql code processed");
		}

		private class MyCQLFilterEvaluatorCallback implements CqlProcessingCallback {

			@Override
			public void from(Classe source) {
				logger.debug("cql add: from = {}", source);
				entryType = source;
				querySpecsBuilder.select(anyAttribute(source)).from(source);
			}

			@Override
			public void distinct() {
				logger.debug("cql add: distinct");
				querySpecsBuilder.distinct();
			}

			@Override
			public void leftJoin(Classe target, Alias alias, Over over) {
				logger.debug("cql add: leftJoin = {} alias = {} over = {}", target, alias, over);
				querySpecsBuilder.leftJoin(target, alias, over);
			}

			@Override
			public void join(Classe target, Alias alias, Over over) {
				logger.debug("cql add: join = {} alias = {} over = {}", target, alias, over);
				querySpecsBuilder.join(target, alias, over);
			}

			@Override
			public void where(WhereClause clause) {
				logger.debug("cql add: where = {}", clause);
				whereClauses.add(clause);
				querySpecsBuilder.where(clause);
			}

			@Override
			public void attributes(Iterable<QueryAliasAttribute> attributes) {
//				attributes.forEach((a)->querySpecsBuilder.select(attrDef));
			}

		}

	}
}
