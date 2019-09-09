/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.postgres.test;

import java.net.URI;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import org.cmdbuild.utils.postgres.PostgresServerHelper;
import org.cmdbuild.utils.postgres.PostgresUtils;
import static org.cmdbuild.utils.postgres.PostgresUtils.POSTGRES_SERVER_VERSIONS;
import org.junit.Ignore;
import org.junit.Test;

public class PostgresUtilsTest {

    @Test
    public void testUriParsing() {
        URI uri = URI.create("x://10.0.0.173:5432/cmdbuild_30");
        assertEquals(5432, uri.getPort());
        assertEquals("10.0.0.173", uri.getHost());
        assertEquals("/cmdbuild_30", uri.getPath());
    }

    @Test
//    @Ignore("heavy test")
    public void testPgInstall() {
        for (String pgVersion : POSTGRES_SERVER_VERSIONS) {//TODO parametrized test

            PostgresServerHelper postgres = PostgresUtils.serverHelper().withPostgresVersion(pgVersion);

            postgres.installAndStartPostgres();

            assertTrue(postgres.getInstallDirectory().isDirectory());
            assertTrue(postgres.isRunning());

            postgres.stopPostgres();

            assertFalse(postgres.isRunning());

            postgres.uninstallPostgres();

            assertFalse(postgres.getInstallDirectory().exists());

        }
    }
}
