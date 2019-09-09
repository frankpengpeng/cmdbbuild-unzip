package org.cmdbuild.dao.query.clause;

import org.cmdbuild.dao.entrytype.EntryType;

public interface HistoricEntryType<T extends EntryType> {

	T getType();

}
