/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.framework;

import static com.google.common.base.Preconditions.checkArgument;
import java.io.File;
import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import static org.apache.commons.lang.StringUtils.trim;
import static org.cmdbuild.config.SchedulerConfigurationImpl.SCHEDULER_CONFIG_ENABLED_KEY;
import static org.cmdbuild.config.SqlConfigurationImpl.SQL_LOG_ENABLED_KEY;
import org.cmdbuild.config.api.DirectoryService;
import org.cmdbuild.config.api.DirectoryServiceImpl;
import org.cmdbuild.dao.config.inner.DatabaseCreator;
import static org.cmdbuild.dao.config.inner.DatabaseCreator.EXISTING_DATABASE;
import org.cmdbuild.dao.config.inner.DatabaseCreatorConfigImpl;
import org.cmdbuild.dao.config.inner.DatabaseCreatorConfigImpl.DatabaseCreatorConfigBuilder;
import org.cmdbuild.services.SystemService;
import static org.cmdbuild.services.SystemStatus.SYST_ERROR;
import static org.cmdbuild.services.SystemStatus.SYST_READY;
import org.cmdbuild.spring.utils.ApplicationContextHelper;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.io.CmIoUtils.tempDir;
import static org.cmdbuild.utils.io.CmPropertyUtils.writeProperties;
import static org.cmdbuild.utils.io.CmZipUtils.unzipToDir;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmExecutorUtils.waitUntil;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.mapOf;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.postgres.PostgresUtils.getPostgresServerAvailablePort;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestContextUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static TestContext buildTestContext(String databaseType) {
        DatabaseCreator databaseCreator = createTestDatabase((c) -> c.withSource(databaseType));
        File tempDir = tempDir("cm_temp_dir_for_test_"),
                configDir = new File(tempDir, "config");
//                webappDir = new File(tempDir, "webapp"),
//                webinfDir = new File(webappDir, "WEB-INF");
//        webinfDir.mkdirs();
        writeProperties(new File(configDir, "database.conf"), databaseCreator.getConfig().getConfig());
        writeProperties(new File(configDir, "scheduler.conf"), map(SCHEDULER_CONFIG_ENABLED_KEY, false));
        writeProperties(new File(configDir, "sql.conf"), map(SQL_LOG_ENABLED_KEY, true));
//        unzipToDir(TestContextUtils.class.getResourceAsStream("/cmdbuild-report-files.zip"), webinfDir);
        DirectoryService directoryService = DirectoryServiceImpl.builder()
                .withConfigDirectory(configDir)
                //                .withWebappDirectory(webappDir)
                .build();
        ApplicationContextHelper applicationContextInitializer = new ApplicationContextHelper(ApplicationContextConfigurationForIntegrationTest.class, directoryService);
        applicationContextInitializer.init();//TODO remove this
        SystemService systemService = applicationContextInitializer.getBean(SystemService.class);
        waitUntil(() -> systemService.hasStatus(SYST_READY, SYST_ERROR));
        checkArgument(systemService.isSystemReady(), "cmdbuild failed to start");
        return new TestContext(applicationContextInitializer, (c) -> {
            systemService.stopSystem();
            applicationContextInitializer.cleanup();
            if (!databaseCreator.useExistingDatabase()) {
                databaseCreator.dropDatabase();
            }
            deleteQuietly(tempDir);
        }, x -> {
        });
    }

    public static DatabaseCreator createTestDatabase(Consumer<DatabaseCreatorConfigBuilder> consumer) {
        Map<String, String> config = mapOf(String.class, String.class).with(
                "db.url", "AUTO",
                "db.username", "cmdbuild",
                "db.password", "cmdbuild",
                "db.admin.username", "postgres",
                "db.admin.password", "postgres"
        ).accept((m) -> {
            if (isNotBlank(System.getenv("CMDBUILD_TEST_DATABASE"))) {
                m.put("db.url", trim(System.getenv("CMDBUILD_TEST_DATABASE")));
            }
        }).with(map(System.getProperties()).entrySet().stream().filter(e -> e.getKey().startsWith("cmdbuild.test.database.") && isNotBlank(e.getValue())).collect(toMap(e -> e.getKey().replaceFirst("cmdbuild.test.database.", "db."), Entry::getValue)));
        DatabaseCreator databaseCreator;
        if (toBooleanOrDefault(config.get("db.create"), true) == false) {
            databaseCreator = new DatabaseCreator(DatabaseCreatorConfigImpl.builder().withConfig(config).withSource(EXISTING_DATABASE).build());
        } else {
            File tempDir = tempDir("cm_sql_sources_for_test_");
            try {
                unzipToDir(TestContextUtils.class.getResourceAsStream("/cmdbuild-dao-sql.zip"), tempDir);
                databaseCreator = new DatabaseCreator(
                        DatabaseCreatorConfigImpl.builder().accept(c -> {
                            if (config.get("db.url").equalsIgnoreCase("AUTO")) {
                                c.withInstallPostgres(true).withDatabaseUrl("localhost", getPostgresServerAvailablePort(), "cmdbuild_test").withConfig(map(config).withoutKey("db.url"));
                            } else {
                                c.withConfig(config).withDatabaseName("cmdbuild_test_" + randomId(6));
                            }
                        })
                                .withSqlPath(new File(tempDir, "sql").getAbsolutePath())
                                .withUseSharkSchema(true)
                                .accept(consumer)
                                .build());
                databaseCreator.configureDatabase();
                databaseCreator.applyPatches();
            } finally {
                deleteQuietly(tempDir);
            }
        }
        try {
            LOGGER.debug("loading test utils");
            String sql = readToString(TestContextUtils.class.getResourceAsStream("/org/cmdbuild/test/framework/test_utils.sql"));
            try (Connection connection = databaseCreator.getCmdbuildDataSource().getConnection()) {
                connection.prepareStatement(sql).execute();
            }
        } catch (SQLException ex) {
            throw runtime("error loading test utils", ex);
        }
        return databaseCreator;
    }
}
