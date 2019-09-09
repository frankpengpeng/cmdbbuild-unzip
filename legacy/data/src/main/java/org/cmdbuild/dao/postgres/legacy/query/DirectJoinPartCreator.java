package org.cmdbuild.dao.postgres.legacy.query;

import static com.google.common.collect.FluentIterable.from;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.join;
import static org.cmdbuild.dao.postgres.Const.SystemAttributes.Status;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.quoteAttribute;

import java.util.Iterator;

import org.cmdbuild.dao.CardStatus;
import org.cmdbuild.dao.query.QuerySpecs;
import org.cmdbuild.dao.query.clause.HistoricEntryType;
import org.cmdbuild.dao.query.clause.QueryAliasAttribute;
import org.cmdbuild.dao.query.clause.join.DirectJoinClause;

import com.google.common.base.Function;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.aliasToQuotedSql;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.entryTypeToSqlExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.entryTypeToSqlExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.entryTypeToSqlExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.entryTypeToSqlExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.entryTypeToSqlExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.entryTypeToSqlExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.entryTypeToSqlExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.entryTypeToSqlExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.entryTypeToSqlExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.entryTypeToSqlExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.entryTypeToSqlExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.entryTypeToSqlExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.entryTypeToSqlExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.entryTypeToSqlExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.entryTypeToSqlExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.entryTypeToSqlExpr;

public class DirectJoinPartCreator extends PartCreator {

	private static class DirectJoinClauseToString implements Function<DirectJoinClause, String> {

		private static final String JOIN = "JOIN";
		private static final String LEFT_JOIN = "LEFT " + JOIN;

		private static final String FORMAT = "%s %s AS %s ON %s = %s";

		@Override
		public String apply(final DirectJoinClause input) {
			final QueryAliasAttribute sourceAttribute = input.getSourceAttribute();
			final QueryAliasAttribute targetAttribute = input.getTargetAttribute();
			String output = format(FORMAT, //
					input.isLeft() ? LEFT_JOIN : JOIN, entryTypeToSqlExpr(input.getTargetClass()),//
aliasToQuotedSql(input.getTargetClassAlias()), //
					quoteAttribute(targetAttribute.getAlias(), targetAttribute.getName()), //
					quoteAttribute(sourceAttribute.getAlias(), sourceAttribute.getName()) //
			);
			if (input.getTargetClass().hasHistory() && !(input.getTargetClass() instanceof HistoricEntryType)) {
				output = format("%s AND %s = '%s'", //
						output, //
						quoteAttribute(targetAttribute.getAlias(), Status), //
						CardStatus.ACTIVE.value() //
				);

			}
			return output;
		}

	}

	private static DirectJoinClauseToString TO_STRING = new DirectJoinClauseToString();

	private static final String SEPARATOR = "\n";

	public DirectJoinPartCreator(final QuerySpecs querySpecs) {
		super();
		sb.append(join(directJoinClausesAsStrings(querySpecs), SEPARATOR));
	}

	private Iterator<String> directJoinClausesAsStrings(final QuerySpecs querySpecs) {
		return from(querySpecs.getDirectJoins()) //
				.transform(TO_STRING) //
				.toSet() // used here to avoid duplicate clauses
				.iterator();
	}

}
