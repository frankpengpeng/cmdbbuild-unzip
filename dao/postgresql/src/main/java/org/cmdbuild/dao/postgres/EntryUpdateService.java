package org.cmdbuild.dao.postgres;

import org.cmdbuild.dao.beans.DatabaseRecord;

public interface EntryUpdateService {

	long executeInsertAndReturnKey(DatabaseRecord entry);

	void executeUpdate(DatabaseRecord entry);
}
