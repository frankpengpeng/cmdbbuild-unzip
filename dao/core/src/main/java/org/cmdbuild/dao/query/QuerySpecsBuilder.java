package org.cmdbuild.dao.query;

import java.util.Map;
import javax.annotation.Nullable;

import org.apache.commons.lang3.builder.Builder;
import org.cmdbuild.dao.query.clause.OrderByClause.Direction;
import org.cmdbuild.dao.query.clause.QueryAttribute;
import org.cmdbuild.dao.query.clause.QueryDomain.Source;
import org.cmdbuild.dao.query.clause.alias.Alias;
import org.cmdbuild.dao.query.clause.join.Over;
import org.cmdbuild.dao.query.clause.where.WhereClause;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.query.clause.where.OperatorAndValue;
import static org.cmdbuild.dao.query.clause.where.SimpleWhereClause.condition;
import org.cmdbuild.dao.entrytype.EntryType;

/**
 * Builder for {@link QuerySpecs}.
 */
public interface QuerySpecsBuilder extends Builder<QuerySpecs> {

	QuerySpecsBuilder select(QueryAttribute... attrDef);

	QuerySpecsBuilder distinct();

	QuerySpecsBuilder _from(EntryType entryType, Alias alias);

	QuerySpecsBuilder from(EntryType fromEntryType, Alias fromAlias);

	QuerySpecsBuilder from(Classe cmClass);

	/*
	 * TODO: Consider more join levels (join with join tables)
	 */
	QuerySpecsBuilder join(Classe joinClass, Over overClause);

	QuerySpecsBuilder join(Classe joinClass, Alias joinClassAlias, Over overClause);

	QuerySpecsBuilder join(Classe joinClass, Alias joinClassAlias, Over overClause, Source source);

	/*
	 * TODO refactor to have a single join method
	 */
	QuerySpecsBuilder leftJoin(Classe joinClass, Alias joinClassAlias, Over overClause);

	QuerySpecsBuilder leftJoin(Classe joinClass, Alias joinClassAlias, Over overClause, Source source);

	QuerySpecsBuilder where(WhereClause clause);

	default QuerySpecsBuilder where(QueryAttribute attribute, OperatorAndValue operator) {
		return where(condition(attribute, operator));
	}

	QuerySpecsBuilder offset(Number offset);

	default QuerySpecsBuilder offsetFromNullable(@Nullable Number offset) {
		if (offset == null) {
			return this;
		} else {
			return this.offset(offset);
		}
	}

	QuerySpecsBuilder limit(Number limit);

	default QuerySpecsBuilder limitFromNullable(@Nullable Number limit) {
		if (limit == null) {
			return this;
		} else {
			return this.limit(limit);
		}
	}

	QuerySpecsBuilder orderBy(QueryAttribute attribute, Direction direction);

	QuerySpecsBuilder orderBy(Map<QueryAttribute, Direction> order);

	QuerySpecsBuilder numbered();

	QuerySpecsBuilder numbered(WhereClause whereClause);

	QuerySpecsBuilder count();

	/**
	 * @deprecated used temporary for performance improvements since it seems
	 *             there is an issue with PostgresSQL queries.
	 */
	@Deprecated
	QuerySpecsBuilder skipDefaultOrdering();

	QueryResult run();

}
