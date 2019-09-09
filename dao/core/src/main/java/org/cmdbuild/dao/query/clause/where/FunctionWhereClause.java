package org.cmdbuild.dao.query.clause.where;

import org.cmdbuild.dao.query.clause.QueryAliasAttribute;
import org.cmdbuild.dao.entrytype.EntryType;

public class FunctionWhereClause implements WhereClause {

	public final QueryAliasAttribute attribute;
	public final String name;
	public final Long userId;
	public final Long roleId;
	public final EntryType entryType;

	public FunctionWhereClause(final QueryAliasAttribute attribute, final String name, final Long userId,
			final Long roleId, final EntryType entryType) {
		this.attribute = attribute;
		this.name = name;
		this.userId = userId;
		this.roleId = roleId;
		this.entryType = entryType;
	}

	@Override
	public void accept(final WhereClauseVisitor visitor) {
		visitor.visit(this);
	}

}
