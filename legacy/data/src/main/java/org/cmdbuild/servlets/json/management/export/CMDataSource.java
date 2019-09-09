package org.cmdbuild.servlets.json.management.export;

import org.cmdbuild.dao.beans.DatabaseRecord;

public interface CMDataSource {

	Iterable<String> getHeaders();

	Iterable<DatabaseRecord> getEntries();

}
