/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.wfmigration;

import java.io.File;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import org.cmdbuild.test.framework.ContextProvider;
import org.cmdbuild.test.framework.TestContext;
import static org.cmdbuild.test.framework.TestContextUtils.buildTestContext;
import static org.cmdbuild.utils.io.CmIoUtils.copy;
import static org.cmdbuild.utils.io.CmIoUtils.tempDir;

@ContextProvider
public class TestContextProviders {

    public static TestContext getWfMigrationTestContext() {
        File dumpFile = new File(tempDir(), "file.dump.xz");
        try {
            copy(TestContextProviders.class.getResourceAsStream("/org/cmdbuild/test/wfmigration/wf_migration_test_db.dump.xz"), dumpFile);
            return buildTestContext(dumpFile.getAbsolutePath());
        } finally {
            deleteQuietly(dumpFile.getParentFile());
        }
    }
}
