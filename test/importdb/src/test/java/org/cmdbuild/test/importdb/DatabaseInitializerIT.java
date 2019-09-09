/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.importdb;

import static com.google.common.base.Preconditions.checkArgument;
import org.cmdbuild.dao.config.inner.DatabaseCreator;
import org.junit.After;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.cmdbuild.test.framework.TestContextUtils.createTestDatabase;
import static org.cmdbuild.dao.config.inner.DatabaseCreator.R2U_DUMP;
import static org.cmdbuild.dao.config.inner.DatabaseCreator.EMPTY_DUMP;
import static org.cmdbuild.dao.config.inner.DatabaseCreator.DEMO_DUMP;
import org.junit.Ignore;

public class DatabaseInitializerIT {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private DatabaseCreator databaseCreator;

    @Test
    @Ignore("TODO") //TODO
    public void testEmpty25Database() throws Exception {
        databaseCreator = createTestDatabase((c) -> c.withSource("empty_25.dump.xz"));
        checkArgument(databaseCreator.cmdbuildDatabaseExists());
    }

    @Test
    public void testDemo25Database() throws Exception {
        databaseCreator = createTestDatabase((c) -> c.withSource("demo_25.dump.xz"));
        checkArgument(databaseCreator.cmdbuildDatabaseExists());
    }

    @Test
    public void testR2u25Database() throws Exception {
        databaseCreator = createTestDatabase((c) -> c.withSource("ready2use_25.dump.xz"));
        checkArgument(databaseCreator.cmdbuildDatabaseExists());
    }

    @Test
    public void testDemoDumpDatabase() throws Exception {
        databaseCreator = createTestDatabase((c) -> c.withSource(DEMO_DUMP));
        checkArgument(databaseCreator.cmdbuildDatabaseExists());
    }

    @Test
    public void testEmptyDumpDatabase() throws Exception {
        databaseCreator = createTestDatabase((c) -> c.withSource(EMPTY_DUMP));
        checkArgument(databaseCreator.cmdbuildDatabaseExists());
    }

    @Test
    public void testR2uDumpDatabase() throws Exception {
        databaseCreator = createTestDatabase((c) -> c.withSource(R2U_DUMP));
        checkArgument(databaseCreator.cmdbuildDatabaseExists());
    }

    @After
    public void cleanup() {
        if (databaseCreator != null) {
            try {
                databaseCreator.dropDatabase();
            } catch (Exception ex) {
                logger.error("error dropping database", ex);
            }
            databaseCreator = null;
        }
    }
}
