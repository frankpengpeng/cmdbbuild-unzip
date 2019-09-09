package org.cmdbuild.dao.query.clause.from;

import org.apache.commons.lang3.Validate;
import org.cmdbuild.dao.entrytype.CMFunctionCall;
import org.cmdbuild.dao.query.clause.alias.Alias;
import org.cmdbuild.dao.entrytype.EntryType;

public class FunctionFromClause implements FromClause {

	private final EntryType entryType;
	private final Alias alias;

	public FunctionFromClause(final EntryType entryType, final Alias alias) {
		Validate.isTrue(entryType instanceof CMFunctionCall, "from clause must be for function calls only");
		this.entryType = entryType;
		this.alias = alias;
	}

	@Override
	public EntryType getType() {
		return entryType;
	}

	@Override
	public Alias getAlias() {
		return alias;
	}

	@Override
	public boolean isHistory() {
		return false;
	}

	@Override
	public EntryTypeStatus getStatus(final EntryType entryType) {
		return new EntryTypeStatus() {

			@Override
			public boolean isActive() {
				return true;
			}

			@Override
			public boolean isAccessible() {
				return true;
			}

		};
	}

}
