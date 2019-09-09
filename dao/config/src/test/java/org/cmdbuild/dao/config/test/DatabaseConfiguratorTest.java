package org.cmdbuild.dao.config.test;

import org.cmdbuild.dao.config.inner.DatabaseCreator;
import org.cmdbuild.dao.config.inner.DatabaseCreatorConfigImpl;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class DatabaseConfiguratorTest {

	@Test
	public void testUrlParsing() throws Exception {
		DatabaseCreator databaseConfigurator = new DatabaseCreator(DatabaseCreatorConfigImpl.builder().withDatabaseUrl("jdbc:postgresql://localhost:5432/cmdbuild_test").build());
		assertEquals("localhost", databaseConfigurator.getConfig().getHost());
		assertEquals(5432, databaseConfigurator.getConfig().getPort());
		assertEquals("cmdbuild_test", databaseConfigurator.getConfig().getDatabaseName());
	}

}
