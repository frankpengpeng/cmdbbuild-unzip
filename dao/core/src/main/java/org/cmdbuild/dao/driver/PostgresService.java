package org.cmdbuild.dao.driver;

import org.cmdbuild.dao.driver.repository.AttributeRepository;
import org.cmdbuild.dao.driver.repository.ClasseRepository;
import org.cmdbuild.dao.driver.repository.DomainRepository;
import org.cmdbuild.dao.driver.repository.FunctionRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.dao.beans.DatabaseRecord;

public interface PostgresService extends DomainRepository, AttributeRepository, FunctionRepository, ClasseRepository {

	Long create(DatabaseRecord entry);

	void update(DatabaseRecord entry);

	void delete(DatabaseRecord entry);

	void truncate(EntryType type);

	JdbcTemplate getJdbcTemplate();

}
