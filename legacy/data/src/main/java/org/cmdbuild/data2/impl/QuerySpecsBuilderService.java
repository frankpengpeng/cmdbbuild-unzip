package org.cmdbuild.data2.impl;

import javax.annotation.Nullable;
import org.cmdbuild.dao.query.QuerySpecsBuilder;
import org.cmdbuild.common.data.QueryOptions;
import org.cmdbuild.dao.query.clause.alias.Alias;
import org.cmdbuild.dao.entrytype.EntryType;

public interface QuerySpecsBuilderService {

	QuerySpecsBuilderHelper helper();

	default QuerySpecsBuilderHelper withQueryOptions(QueryOptions queryOptions) {
		return helper().withQueryOptions(queryOptions);
	}

	default QuerySpecsBuilder builder(QueryOptions queryOptions) {
		return withQueryOptions(queryOptions).builder();
	}

	default QuerySpecsBuilder builder(QueryOptions queryOptions, EntryType entryType) {
		return withQueryOptions(queryOptions).withEntryType(entryType).builder();
	}

	interface QuerySpecsBuilderHelper {

		QuerySpecsBuilderHelper withQueryOptions(QueryOptions queryOptions);

		QuerySpecsBuilderHelper withEntryType(EntryType entryType);

		default QuerySpecsBuilderHelper withNullableEntryType(@Nullable EntryType entryType) {
			if (entryType != null) {
				return this.withEntryType(entryType);
			} else {
				return this;
			}
		}

		QuerySpecsBuilder builder();

		Alias getAlias();

	}
}
