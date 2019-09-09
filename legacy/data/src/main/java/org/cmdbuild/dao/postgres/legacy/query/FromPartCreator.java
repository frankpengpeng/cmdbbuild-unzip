package org.cmdbuild.dao.postgres.legacy.query;

import org.cmdbuild.dao.query.QuerySpecs;
import org.cmdbuild.dao.query.clause.from.FromClause;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.aliasToQuotedSql;

public class FromPartCreator extends PartCreator {

	public FromPartCreator(final QuerySpecs querySpecs) {
		super();
		sb.append("FROM ");
		/*
		 * TODO check if this is really needed
		 * 
		 * if (query.getFromType().holdsHistory()) { sb.append("ONLY "); }
		 */
		sb.append(quoteType(fromClause(querySpecs).getType())).append(" AS ")
				.append(aliasToQuotedSql(fromClause(querySpecs).getAlias()));
	}

	private FromClause fromClause(final QuerySpecs querySpecs) {
		return querySpecs.getFromClause();
	}

}
