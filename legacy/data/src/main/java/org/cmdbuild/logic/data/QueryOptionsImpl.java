package org.cmdbuild.logic.data;

import static com.google.common.base.Functions.identity;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.transformValues;
import static java.lang.Integer.MAX_VALUE;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import java.util.Map;

import org.apache.commons.lang3.builder.Builder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.common.data.QueryOptions;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;
import org.cmdbuild.data.filter.utils.CmdbSorterUtils;
import org.cmdbuild.data.filter.utils.CmdbFilterUtils;
import static org.cmdbuild.data.filter.utils.CmdbFilterUtils.noopFilter;

/**
 * Simple DTO that represents the options for a query in CMDBuild
 */
public class QueryOptionsImpl implements QueryOptions {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final int limit;
	private final int offset;
	private final CmdbFilter filter;
	private final CmdbSorter sorters;
	private final Iterable<String> attributes;
	private final Map<String, Object> parameters;

	private QueryOptionsImpl(QueryOptionsBuilder builder) {
		this.limit = builder.limit == null || builder.limit <= 0 ? MAX_VALUE : builder.limit;
		this.offset = defaultIfNull(builder.offset, 0);
		checkArgument(offset >= 0, "offset must be >=0");
		this.filter = defaultIfNull(builder.filter, noopFilter());
		this.sorters = builder.sorters;
		this.attributes = defaultIfNull(builder.attributeSubset, emptyList());
		this.parameters = transformValues(builder.parameters, identity());
	}

	@Override
	public int getLimit() {
		return limit;
	}

	@Override
	public int getOffset() {
		return offset;
	}

	@Override
	public CmdbFilter getFilter() {
		return filter;
	}

	@Override
	public CmdbSorter getSorters() {
		return sorters;
	}

	@Override
	public Iterable<String> getAttributes() {
		return attributes;
	}

	@Override
	public Map<String, Object> getParameters() {
		return parameters;
	}

	@Override
	public String toString() {
		return "SimpleQueryOptions{" + "limit=" + limit + ", offset=" + offset + ", filter=" + filter + ", sorters=" + sorters + ", attributes=" + attributes + ", parameters=" + parameters + '}';
	}

	public static QueryOptionsBuilder builder() {
		return new QueryOptionsBuilder();
	}

	public static QueryOptionsBuilder copyOf(QueryOptions queryOptions) {
		return new QueryOptionsBuilder().clone(queryOptions);
	}

	public static class QueryOptionsBuilder implements Builder<QueryOptionsImpl> {

//		private static final Iterable<String> EMPTY_ATTRIBUTES = emptyList();
		private final Logger logger = LoggerFactory.getLogger(getClass());

		private Integer limit;
		private Integer offset;
		private CmdbFilter filter;
		private CmdbSorter sorters;
		private Iterable<String> attributeSubset;
		private Map<String, ? extends Object> parameters;

		private QueryOptionsBuilder() {
			limit = MAX_VALUE;
			offset = 0;
			filter = CmdbFilterUtils.noopFilter();
			sorters = CmdbSorterUtils.noopSorter();
			parameters = newHashMap();
		}

		@Override
		public QueryOptionsImpl build() {
//			preReleaseHackToFixCqlFilters();
			return new QueryOptionsImpl(this);
		}

		/*
		 * FIXME remove this and fix JavaScript ASAP
		 */
//		private void preReleaseHackToFixCqlFilters() {//TODO
		//TODO fix this, or rewrite cql stuff
//			try {
//				final Map<String, Object> cqlParameters = newHashMap();
//				boolean addParameters = false;
//				for (final Entry<String, ? extends Object> entry : parameters.entrySet()) {
//					final String key = entry.getKey();
//					if (key.equals(CQL_KEY)) {
//						filter.put(CQL_KEY, entry.getValue());
//						addParameters = true;
//					} else if (key.startsWith("p")) {
//						cqlParameters.put(key, entry.getValue());
//					}
//				}
//				if (addParameters) {
//					for (final Entry<String, Object> entry : cqlParameters.entrySet()) {
//						filter.put(entry.getKey(), entry.getValue());
//					}
//				}
//			} catch (final Throwable e) {
//				logger.error("error while hacking filter", e);
//			}
//		}
		public QueryOptionsBuilder limit(Integer limit) {
			this.limit = limit;
			return this;
		}

		public QueryOptionsBuilder offset(Integer offset) {
			this.offset = offset;
			return this;
		}

		/**
		 * @param sorters JSON array representing sorting clauses, can be
		 * {@code null}.
		 */
		@Deprecated //use new orderBy
		public QueryOptionsBuilder orderBy(JSONArray sorters) {
			return this.orderBy(CmdbSorterUtils.fromJson(defaultIfNull(sorters, new JSONArray())));
		}

		public QueryOptionsBuilder orderBy(CmdbSorter sorters) {
			this.sorters = defaultIfNull(sorters, CmdbSorterUtils.noopSorter());
			return this;
		}

		/**
		 * @param filter JSON object representing a filter, can be {@code null}.
		 */
		@Deprecated
		public QueryOptionsBuilder filter(JSONObject filter) {
			filter = defaultIfNull(filter, new JSONObject());
			this.filter = CmdbFilterUtils.fromJson(filter);
			return this;
		}

		public QueryOptionsBuilder filter(CmdbFilter filter) {
			this.filter = filter;
			return this;
		}

		public QueryOptionsBuilder onlyAttributes(Iterable<String> attributes) {
			this.attributeSubset = attributes;
			return this;
		}

		public QueryOptionsBuilder parameters(Map<String, ? extends Object> parameters) {
			this.parameters = parameters;
			return this;
		}

		public QueryOptionsBuilder clone(QueryOptions queryOptions) {
			limit = queryOptions.getLimit();
			offset = queryOptions.getOffset();
			filter = queryOptions.getFilter();
			sorters = queryOptions.getSorters();
			attributeSubset = queryOptions.getAttributes();
			parameters = queryOptions.getParameters();
			return this;
		}

	}
}
