/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli.utils;

import static com.google.common.base.Preconditions.checkArgument;
import java.io.File;
import java.io.FileInputStream;
import static java.util.Collections.emptyMap;
import java.util.Map;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import org.cmdbuild.dao.config.inner.DatabaseCreator;
import org.cmdbuild.dao.config.inner.DatabaseCreatorConfig;
import org.cmdbuild.dao.config.inner.DatabaseCreatorConfigImpl;
import static org.cmdbuild.utils.cli.Main.getCliHome;
import static org.cmdbuild.utils.cli.Main.isRunningFromWebappDir;
import static org.cmdbuild.utils.io.CmIoUtils.tempDir;
import static org.cmdbuild.utils.io.CmZipUtils.unzipToDir;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class DatabaseUtils {

    public static void dropDatabase(File configFile) throws Exception {
        DatabaseCreator databaseConfigurator = new DatabaseCreator(DatabaseCreatorConfigImpl.builder().withConfig(new FileInputStream(configFile)).build());
        System.err.println("dropping database " + databaseConfigurator.getConfig().getDatabaseName());
        databaseConfigurator.dropDatabase();
        System.err.println("done");
    }

    public static void createDatabase(DatabaseCreatorConfig databaseCreatorConfig) {
        createDatabase(databaseCreatorConfig, true);
    }

    public static void createDatabase(DatabaseCreatorConfig databaseCreatorConfig, boolean applyPatches) {
        createDatabase(databaseCreatorConfig, applyPatches, emptyMap());
    }

    public static void createDatabase(DatabaseCreatorConfig databaseCreatorConfig, boolean applyPatches, Map<String, String> oldConfig) {
        File tempDir = tempDir("sqlsourcesforcmd_");
        try {
            File sqlDir;
            if (isRunningFromWebappDir()) {
                sqlDir = new File(getCliHome(), "WEB-INF/sql/");
            } else {
                unzipToDir(getCliHome(), tempDir);//TODO unzip only sql subpath
                sqlDir = new File(tempDir, "WEB-INF/sql/");
            }
            checkArgument(list(new File(sqlDir, "functions").listFiles()).stream().filter(File::isFile).filter(f -> f.getName().toLowerCase().endsWith(".sql")).findAny().isPresent(), "invalid sql sources (broken build?)");
            DatabaseCreator databaseCreator = new DatabaseCreator(DatabaseCreatorConfigImpl.builder().withSqlPath(sqlDir.getAbsolutePath()).build());

            databaseCreator.setConfig(DatabaseCreatorConfigImpl.copyOf(databaseCreatorConfig)
                    .withSqlPath(databaseCreator.getConfig().getSqlPath())
                    .withUseSharkSchema(true)//TODO check if source file contains shark schema, auto enable if required
                    .build());

            System.err.println("create database " + databaseCreator.getConfig().getDatabaseName() + " " + databaseCreator.getConfig().getDatabaseType()); //TODO use notification callback
            databaseCreator.configureDatabase();
            try {
                if (applyPatches) {
                    System.err.println("apply patches");
                    databaseCreator.applyPatches();
                }
            } finally {
                System.err.println("adjust configs");
                databaseCreator.adjustConfigs(oldConfig);
            }
            System.err.println("done");//TODO use notification callback
        } finally {
            deleteQuietly(tempDir);
        }

    }
}
