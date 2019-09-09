/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.rest;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.cmdbuild.dao.config.inner.DatabaseCreator;
import org.cmdbuild.dao.config.inner.DatabaseCreatorConfig;
import org.cmdbuild.test.framework.ContextProvider;
import org.cmdbuild.test.framework.TestContext;
import static org.cmdbuild.test.framework.TestContextUtils.createTestDatabase;
import org.cmdbuild.test.rest.utils.AbstractWsIT;
import org.cmdbuild.test.rest.utils.TomcatManagerForTest;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.slf4j.LoggerFactory;

@ContextProvider
public class TestContextProviders {

    public static final String TC_R2U = "r2u", TC_DEMO = "demo", TC_EMPTY = "empty";

    private static final String BASE_URL_KEY = "cmdbuild.test.base.url";

    private static TomcatManagerForTest tomcatManagerForTest;
    private static DatabaseCreatorConfig defaultDatabaseConfig;
    private static int activeClients = 0;

    @ContextProvider(TC_R2U)
    public static TestContext r2u() {
        return initTomcatAndDb(DatabaseCreator.R2U_DUMP);
    }

    @ContextProvider(TC_DEMO)
    public static TestContext demo() {
        return initTomcatAndDb(DatabaseCreator.DEMO_DUMP);
    }

    @ContextProvider(TC_EMPTY)
    public static TestContext empty() {
        return initTomcatAndDb(DatabaseCreator.EMPTY_DUMP);
    }

    public static TestContext initTomcatAndDb(String dbType) {
        String baseUrl = trimToNull(System.getProperty(BASE_URL_KEY));
        DatabaseCreator dbConfig;
        if (isBlank(baseUrl)) {
            if (tomcatManagerForTest == null) {
                tomcatManagerForTest = new TomcatManagerForTest();
                tomcatManagerForTest.initTomcatAndDb(DatabaseCreator.EMPTY_DUMP, false);
                defaultDatabaseConfig = tomcatManagerForTest.getDatabaseCreator().getConfig();
            }
            baseUrl = tomcatManagerForTest.getBaseUrl();
            activeClients++;
            dbConfig = createTestDatabase((c) -> c.withSource(dbType));
        } else {
            dbConfig = null;
        }
        System.out.println("\n\n\n");
        return new TestContext(map("baseUrl", baseUrl, "tomcatManagerForTest", tomcatManagerForTest), (c) -> {
            if (dbConfig != null) {
                activeClients--;
                tomcatManagerForTest.reconfigureDatabase(defaultDatabaseConfig);
                dbConfig.dropDatabase();
                if (activeClients == 0) {
                    tomcatManagerForTest.cleanupTomcatAndDb();
                    tomcatManagerForTest = null;
                    defaultDatabaseConfig = null;
                }
            }
        }, c -> {
            if (dbConfig != null) {
                tomcatManagerForTest.reconfigureDatabase(dbConfig.getConfig());
            }
        });
    }

}
