package org.cmdbuild.data2.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Lists.newArrayList;
import static org.cmdbuild.dao.query.clause.AnyAttribute.anyAttribute;
import static org.cmdbuild.dao.query.clause.alias.EntryTypeAlias.canonicalAlias;
import static org.cmdbuild.dao.query.clause.join.Over.over;
import static org.cmdbuild.dao.query.clause.where.AndWhereClause.and;
import static org.cmdbuild.dao.query.clause.where.TrueWhereClause.trueWhereClause;

import java.util.List;
import static org.apache.commons.lang3.StringUtils.isBlank;

import org.apache.commons.lang3.builder.Builder;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.entrytype.CMFunctionCall;
import org.cmdbuild.dao.query.CMQueryRow;
import org.cmdbuild.dao.query.QuerySpecsBuilder;
import org.cmdbuild.dao.query.clause.OrderByClause;
import org.cmdbuild.dao.query.clause.alias.Alias;
import org.cmdbuild.dao.query.clause.where.WhereClause;
//import org.cmdbuild.dao.driver.postgres.relationquery.inner.FilterMapper;
import org.cmdbuild.dao.postgres.legacy.relationquery.SorterMapper;
import org.cmdbuild.dao.postgres.legacy.relationquery.JsonSorterMapper;
import org.cmdbuild.dao.query.QueryResult;
import org.cmdbuild.common.data.QueryOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.data2.impl.QuerySpecsBuilderService.QuerySpecsBuilderHelper;
import static org.cmdbuild.spring.SpringIntegrationUtils.applicationContext;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.view.DataView;

public class DataViewCardFetcher {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final DataView dataView;
	private final String className;
	private final QueryOptions queryOptions;
	private final QuerySpecsBuilderHelper querySpecsBuilderFiller;

	private DataViewCardFetcher(DataViewCardFetcherBuilder builder) {
		this.dataView = checkNotNull(builder.dataView);
		this.className = builder.className;
		this.queryOptions = builder.queryOptions;
		querySpecsBuilderFiller = applicationContext().getBean(QuerySpecsBuilderService.class).withQueryOptions(queryOptions).withNullableEntryType(isBlank(className) ? null : dataView.getClasse(className));//TODO springify
	}

	public PagedElements<Card> fetch() {
		QueryResult result = querySpecsBuilderFiller.builder().count().run();
		List<Card> filteredCards = newArrayList();
		Alias alias = querySpecsBuilderFiller.getAlias();
		for (CMQueryRow row : result) {
			if (row.hasCard(alias)) {
				filteredCards.add(row.getCard(alias));
			}
		}
		return new PagedElements<>(filteredCards, result.totalSize());
	}

	public PagedElements<CMQueryRow> fetchNumbered(WhereClause conditionOnNumberedQuery) {
		QuerySpecsBuilder querySpecsBuilder = querySpecsBuilderFiller.builder();
		querySpecsBuilder.numbered(conditionOnNumberedQuery);
		QueryResult result = querySpecsBuilder.run();
		return new PagedElements<>(result, result.size());
	}

	public static DataViewCardFetcherBuilder newInstance() {
		return new DataViewCardFetcherBuilder();
	}

	public static class DataViewCardFetcherBuilder implements Builder<DataViewCardFetcher> {

		private DataView dataView;
		private String className;
		private QueryOptions queryOptions;

		public DataViewCardFetcherBuilder withDataView(DataView value) {
			dataView = checkNotNull(value);
			return this;
		}

		public DataViewCardFetcherBuilder withClassName(String value) {
			className = checkNotNull(value);
			return this;
		}

		public DataViewCardFetcherBuilder withQueryOptions(QueryOptions value) {
			queryOptions = value;
			return this;
		}

		@Override
		public DataViewCardFetcher build() {
			return new DataViewCardFetcher(this);
		}

	}

	/**
	 * @deprecated use QuerySpecsBuilder instead
	 */
	@Deprecated
	private static abstract class AbstractQuerySpecsBuilderBuilder implements Builder<QuerySpecsBuilder> {

		protected DataView dataView;
		protected DataView systemDataView;
		protected QueryOptions queryOptions;

		public AbstractQuerySpecsBuilderBuilder withDataView(final DataView value) {
			dataView = value;
			return this;
		}

		public AbstractQuerySpecsBuilderBuilder withSystemDataView(final DataView value) {
			systemDataView = value;
			return this;
		}

		public AbstractQuerySpecsBuilderBuilder withQueryOptions(final QueryOptions value) {
			queryOptions = value;
			return this;
		}

//		protected void addJoinOptions(final QuerySpecsBuilder querySpecsBuilder, final QueryOptions options,
//				final Iterable<FilterMapper.JoinElement> joinElements) {
//			if (!isEmpty(joinElements)) {
//				querySpecsBuilder.distinct();
//			}
//			for (final FilterMapper.JoinElement joinElement : joinElements) {
//				final Domain domain = dataView.findDomain(joinElement.domain);
//				final Classe clazz = dataView.findClasse(joinElement.destination);
//				if (joinElement.left) {
//					querySpecsBuilder.leftJoin(clazz, canonicalAlias(clazz), over(domain));
//				} else {
//					querySpecsBuilder.join(clazz, canonicalAlias(clazz), over(domain));
//				}
//			}
//		}

		protected static void addSortingOptions(final QuerySpecsBuilder querySpecsBuilder,
				final Iterable<OrderByClause> clauses) {
			for (final OrderByClause clause : clauses) {
				querySpecsBuilder.orderBy(clause.getAttribute(), clause.getDirection());
			}
		}

	}

	/**
	 * @deprecated use QuerySpecsBuilderFiller instead
	 */
	@Deprecated
	public static class SqlQuerySpecsBuilderBuilder extends AbstractQuerySpecsBuilderBuilder {

		private CMFunctionCall functionCall;
		private Alias functionAlias;

		@Override
		public QuerySpecsBuilder build() {
			throw new UnsupportedOperationException("broken for 30");
//			final FilterMapper filterMapper = JsonFilterMapper.newInstance() //
//					.withDataView(dataView) //
//					.withDataView(systemDataView) //
//					.withEntryType(functionCall) //
//					.withEntryTypeAlias(functionAlias) //
//					.withFilter(queryOptions.getFilter()) //
//					.build();
//			final Iterable<WhereClause> whereClauses = filterMapper.whereClauses();
//			final WhereClause whereClause = isEmpty(whereClauses) ? trueWhereClause() : and(whereClauses);
//			final Iterable<FilterMapper.JoinElement> joinElements = filterMapper.joinElements();
//			final QuerySpecsBuilder querySpecsBuilder = dataView //
//					.select(anyAttribute(functionCall.getFunction(), functionAlias)) //
//					.from(functionCall, functionAlias) //
//					.where(whereClause) //
//					.limit(queryOptions.getLimit()) //
//					.offset(queryOptions.getOffset());
//			addJoinOptions(querySpecsBuilder, queryOptions, joinElements);
//			addSortingOptions(querySpecsBuilder, queryOptions, functionCall, functionAlias);
//			return querySpecsBuilder;
		}

		private void addSortingOptions( //
				final QuerySpecsBuilder querySpecsBuilder, //
				final QueryOptions options, //
				final CMFunctionCall functionCall, //
				final Alias alias) { //

			final SorterMapper sorterMapper = new JsonSorterMapper(functionCall, options.getSorters(), alias);
			final List<OrderByClause> clauses = sorterMapper.deserialize();

			addSortingOptions(querySpecsBuilder, clauses);
		}

		@Override
		public SqlQuerySpecsBuilderBuilder withDataView(final DataView value) {
			return (SqlQuerySpecsBuilderBuilder) super.withDataView(value);
		}

		@Override
		public SqlQuerySpecsBuilderBuilder withSystemDataView(final DataView value) {
			return (SqlQuerySpecsBuilderBuilder) super.withSystemDataView(value);
		}

		@Override
		public SqlQuerySpecsBuilderBuilder withQueryOptions(final QueryOptions value) {
			return (SqlQuerySpecsBuilderBuilder) super.withQueryOptions(value);
		}

		public SqlQuerySpecsBuilderBuilder withFunction(final CMFunctionCall value) {
			functionCall = value;
			return this;
		}

		public SqlQuerySpecsBuilderBuilder withAlias(final Alias value) {
			functionAlias = value;
			return this;
		}

	}
}
