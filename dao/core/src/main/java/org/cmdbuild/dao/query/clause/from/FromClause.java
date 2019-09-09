package org.cmdbuild.dao.query.clause.from;

import org.cmdbuild.dao.query.clause.alias.Alias;
import org.cmdbuild.dao.entrytype.EntryType;

public interface FromClause {

	interface EntryTypeStatus {

		boolean isAccessible();

		boolean isActive();

	}

	EntryType getType();

	Alias getAlias();

	boolean isHistory();

	EntryTypeStatus getStatus(EntryType entryType);

}
