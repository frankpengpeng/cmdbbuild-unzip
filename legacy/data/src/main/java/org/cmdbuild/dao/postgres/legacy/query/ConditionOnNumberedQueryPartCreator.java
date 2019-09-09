package org.cmdbuild.dao.postgres.legacy.query;

import static java.lang.String.format;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.cmdbuild.dao.postgres.Const.SystemAttributes.Id;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.nameForSystemAttribute;
import static org.cmdbuild.dao.query.clause.where.EmptyWhereClause.emptyWhereClause;

import org.cmdbuild.dao.query.QuerySpecs;
import org.cmdbuild.dao.query.clause.QueryAttribute;
import org.cmdbuild.dao.query.clause.where.EqualsOperatorAndValue;
import org.cmdbuild.dao.query.clause.where.ForwardingOperatorAndValueVisitor;
import org.cmdbuild.dao.query.clause.where.ForwardingWhereClauseVisitor;
import org.cmdbuild.dao.query.clause.where.NullOperatorAndValueVisitor;
import org.cmdbuild.dao.query.clause.where.NullWhereClauseVisitor;
import org.cmdbuild.dao.query.clause.where.OperatorAndValueVisitor;
import org.cmdbuild.dao.query.clause.where.SimpleWhereClause;
import org.cmdbuild.dao.query.clause.where.WhereClause;
import org.cmdbuild.dao.query.clause.where.WhereClauseVisitor;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.aliasToQuotedSql;
import static org.cmdbuild.dao.query.clause.alias.NameAlias.nameAlias;

public class ConditionOnNumberedQueryPartCreator extends PartCreator {

	public ConditionOnNumberedQueryPartCreator(final QuerySpecs querySpecs, final StringBuilder main) {
		final WhereClause whereClause = defaultIfNull(querySpecs.getConditionOnNumberedQuery(), emptyWhereClause());
		whereClause.accept(new ForwardingWhereClauseVisitor() {

			private final WhereClauseVisitor delegate = NullWhereClauseVisitor.getInstance();

			@Override
			protected WhereClauseVisitor delegate() {
				return delegate;
			}

			@Override
			public void visit(final SimpleWhereClause whereClause) {
				whereClause.getOperator().accept(new ForwardingOperatorAndValueVisitor() {

					private final OperatorAndValueVisitor delegate = NullOperatorAndValueVisitor.getInstance();

					@Override
					protected OperatorAndValueVisitor delegate() {
						return delegate;
					}

					@Override
					public void visit(final EqualsOperatorAndValue operatorAndValue) {
						final QueryAttribute attribute = whereClause.getAttribute();
						final String quotedName = aliasToQuotedSql(nameAlias(nameForSystemAttribute(attribute.getAlias(), Id)));
						final String actual = main.toString();
						main.setLength(0);
						sb.append(format("SELECT * FROM (%s) AS numbered WHERE %s = %s", //
								actual, //
								quotedName, //
								operatorAndValue.getValue()));
					}

				});
			}

		});
	}

}
