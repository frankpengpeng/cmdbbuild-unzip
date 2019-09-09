/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli.commands;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Iterables.getFirst;
import static com.google.common.io.Files.copy;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import static java.util.stream.Collectors.joining;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.auth.login.PasswordAlgo;
import static org.cmdbuild.auth.utils.CmPasswordUtils.decryptPasswordIfPossible;
import static org.cmdbuild.auth.utils.CmPasswordUtils.detectPasswordAlgo;
import static org.cmdbuild.auth.utils.CmPasswordUtils.encryptPassword;
import static org.cmdbuild.dao.config.inner.ConfigImportStrategy.CIS_DATA_ONLY;
import static org.cmdbuild.dao.config.inner.ConfigImportStrategy.CIS_RESTORE_BACKUP;
import org.cmdbuild.dao.config.inner.DatabaseCreator;
import static org.cmdbuild.dao.config.inner.DatabaseCreator.EMBEDDED_DATABASES;
import static org.cmdbuild.dao.config.inner.DatabaseCreator.EXISTING_DATABASE;
import org.cmdbuild.dao.config.inner.DatabaseCreatorConfig;
import org.cmdbuild.dao.config.inner.DatabaseCreatorConfigImpl;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.systemToSqlExpr;
import static org.cmdbuild.utils.cli.Main.getCliHome;
import static org.cmdbuild.utils.cli.Main.isRunningFromWebappDir;
import static org.cmdbuild.utils.cli.utils.CliUtils.getDbdumpFileOrNull;
import static org.cmdbuild.utils.cli.utils.CliUtils.hasInteractiveConsole;
import org.cmdbuild.utils.cli.utils.DatabaseUtils;
import static org.cmdbuild.utils.cli.utils.DatabaseUtils.dropDatabase;
import org.cmdbuild.utils.date.CmDateUtils;
import static org.cmdbuild.utils.io.CmIoUtils.javaTmpDir;
import static org.cmdbuild.utils.io.CmIoUtils.tempFile;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.trimAndCheckNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableString;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.cmdbuild.utils.postgres.PostgresUtils;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class DbconfigCommandRunner extends AbstractCommandRunner {

    public DbconfigCommandRunner() {
        super("dbconfig", "configure cmdbuild database");
    }

    @Override
    protected Options buildOptions() {
        Options options = super.buildOptions();
        options.addOption("configfile", true, "cmdbuild database config file (es: database.conf); default to conf/<webapp>/database.conf");
        options.addOption("skippatches", false, "skip patches (do not apply patches)");
        options.addOption("backuprestore", false, "backup-restore import mode (restore all configs from dump)");
        options.addOption("dataonly", false, "data-only import mode (restore no configs from dump - except those strongly coupled with data, such as multitenant mode)");
        options.addOption("keepconfigs", false, "keep local configs for those categories that are excluded from config import");
        options.addOption("xzip", false, "compress dump with xzip (best compression, slow)");
        options.addOption("freezesessions", false, "freeze all existing sessions in db (so that they won't expire); this is useful when importing a bug report");
        return options;
    }

    @Override
    protected void printAdditionalHelp() {
        System.err.println("\nactions:"
                + "\n\tdrop: drop database"
                + "\n\tcreate <database type|dump to import>: create database"
                + "\n\trecreate <database type|dump to import>: drop database, then create database"
                + "\n\tcheck <database type|dump to import>: check dump file"
                + "\n\tpatch: apply patches to existing database"
                + "\n\tdump [target_file]: dump database to file"
                + "\n\tupgradepasswordstorage [target_algo]: upgrade password storage algorythm; available algorythms are " + list(PasswordAlgo.values()).stream().map(a -> serializeEnum(a)).collect(joining(", ")));
        System.err.println("\nconfig file example:\n");
        System.err.println(readToString(getClass().getResourceAsStream("/database.conf_cli_example")));
    }

    @Override
    protected void exec(CommandLine cmd) throws Exception {
        String action = getFirst(cmd.getArgList(), "");
        if (isBlank(action)) {
            System.err.println("no action supplied!");
        } else {
            switch (action.trim().toLowerCase()) {
                case "drop":
                    dropDatabase(getConfigFile(cmd));
                    break;
                case "create":
                    createDatabase(cmd, getConfigFile(cmd));
                    break;
                case "patch":
                    patchDatabase(cmd, getConfigFile(cmd));
                    break;
                case "check":
                    checkDump(cmd);
                    break;
                case "recreate":
                    dropAndCreateDatabase(cmd, getConfigFile(cmd));
                    break;
                case "dump": {
                    DatabaseCreatorConfig config = DatabaseCreatorConfigImpl.builder().withConfig(new FileInputStream(getConfigFile(cmd))).build();
                    boolean xzCompression = cmd.hasOption("xzip");
                    String filename = cmd.getArgList().stream().skip(1).findFirst().orElse(null);
                    File file;
                    if (isNotBlank(filename)) {
                        file = new File(filename);
                        if (file.getName().endsWith(".xz")) {
                            xzCompression = true;
                        }
                    } else {
                        file = new File(javaTmpDir(), format("cmdbuild_%s_%s.%s", config.getDatabaseName(), CmDateUtils.dateTimeFileSuffix(), xzCompression ? "dump.xz" : "dump"));
                    }
                    if (hasInteractiveConsole()) {
                        System.out.printf("dump database = %s to file = %s\n", config.getDatabaseUrl(), file.getAbsolutePath());
                    }
                    PostgresUtils.newHelper(
                            config.getHost(),
                            config.getPort(),
                            config.getAdminUser(),
                            config.getAdminPassword())
                            .withDatabase(config.getDatabaseName())
                            .withXzCompression(xzCompression)
                            .buildHelper()
                            .dumpDatabaseToFile(file);
                    if (hasInteractiveConsole()) {
                        System.out.printf("dump OK to %s %s\n", file.getAbsolutePath(), FileUtils.byteCountToDisplaySize(file.length()));
                    } else {
                        copy(file, System.out);
                        deleteQuietly(file);
                    }
                }
                break;
                case "getuserpassword": {
                    String username = checkNotBlank(cmd.getArgList().get(1));
                    DatabaseCreatorConfig config = DatabaseCreatorConfigImpl.builder().withConfig(new FileInputStream(getConfigFile(cmd))).build();
                    String rawPassword = PostgresUtils.newHelper(config.getHost(), config.getPort(), config.getCmdbuildUser(), config.getCmdbuildPassword()).withDatabase(config.getDatabaseName()).buildHelper()
                            .executeQuery("SELECT \"Password\" FROM \"User\" WHERE \"Username\" = '%s' AND \"Status\" = 'A'", username);
                    String clearPassword = decryptPasswordIfPossible(rawPassword);
                    System.out.printf("username             : %s\npassword (encrypted) : %s\npassword (clear)     : %s\n", username, firstNotBlank(rawPassword, "<password not found>"), firstNotBlank(clearPassword, "<unable to decrypt>"));
                }
                break;
                case "upgradepasswordstorage": {
                    PasswordAlgo targetAlgo = parseEnum(cmd.getArgList().get(1), PasswordAlgo.class);
                    System.out.println("upgrade password to algo = " + serializeEnum(targetAlgo));
                    AtomicInteger processed = new AtomicInteger(0), upgraded = new AtomicInteger(0);
                    JdbcTemplate jdbc = new JdbcTemplate(new DatabaseCreator(DatabaseCreatorConfigImpl.builder().withConfig(new FileInputStream(getConfigFile(cmd))).build()).getCmdbuildDataSource());
                    jdbc.queryForList("SELECT \"Id\",\"Password\" FROM \"User\" WHERE \"Password\" IS NOT NULL").stream().forEach(r -> {
                        Long id = toLong(r.get("Id"));
                        String rawPassword = toStringOrNull(r.get("Password")),
                                clearPassword = decryptPasswordIfPossible(rawPassword);
                        processed.incrementAndGet();
                        if (isNotBlank(clearPassword) && !equal(detectPasswordAlgo(rawPassword), targetAlgo)) {
                            String encrypred = encryptPassword(clearPassword, targetAlgo);
                            logger.debug("upgrade record {}", id);
                            jdbc.execute(format("SELECT _cm3_class_triggers_disable('\"User\"'); UPDATE \"User\" SET \"Password\" = %s WHERE \"Id\" = %s; SELECT _cm3_class_triggers_enable('\"User\"')", systemToSqlExpr(encrypred), id));
                            upgraded.incrementAndGet();
                        };
                    });
                    System.out.printf("done (%s records processed, %s upgraded)\n", processed, upgraded);
                }
                break;
                case "getsystemconfig":
                    System.out.println(mapToLoggableString(getCurrentConfigFromDb(cmd, getConfigFile(cmd))));
                    break;
                default:
                    throw new IllegalArgumentException("unknown action: " + action);
            }
        }
    }

    public static File prepareDumpFile(File sourceFile) {
        if (sourceFile.getName().endsWith(".zip")) {
            sourceFile = extractDatabaseFromZipFile(sourceFile);
        }
        return sourceFile;
    }

    private File getConfigFile(CommandLine cmd) throws Exception {
        File configFile;
        if (isRunningFromWebappDir()) {
            configFile = new File(new File(new File(getCliHome(), "../../conf"), getCliHome().getName()), "database.conf");
        } else {
            configFile = new File("conf/cmdbuild/database.conf");
        }
        if (!configFile.isFile() || cmd.hasOption("configfile")) {
            configFile = getFile(cmd, "configfile", true, "must set valid 'configfile'");
        }
        configFile = configFile.getCanonicalFile();
        logger.debug("using config file = {}", configFile.getAbsolutePath());
        return configFile;
    }

    private Map<String, String> getCurrentConfigFromDb(CommandLine cmd, File configFile) throws Exception {
        return new DatabaseCreator(DatabaseCreatorConfigImpl.fromFile(new FileInputStream(configFile))).getSystemConfigsFromDb();
    }

    private void dropAndCreateDatabase(CommandLine cmd, File configFile) throws Exception {
        Map<String, String> oldConfig = new DatabaseCreator(DatabaseCreatorConfigImpl.fromFile(new FileInputStream(configFile))).getSystemConfigsFromDbSafe();
        dropDatabase(configFile);
        createDatabase(cmd, configFile, oldConfig);
    }

    private void createDatabase(CommandLine cmd, File configFile) throws Exception {
        createDatabase(cmd, configFile, emptyMap());
    }

    private void patchDatabase(CommandLine cmd, File configFile) throws Exception {
        DatabaseUtils.createDatabase(DatabaseCreatorConfigImpl.builder()
                .withConfigImportStrategy(CIS_RESTORE_BACKUP)
                .withSource(EXISTING_DATABASE)
                .withConfig(configFile).build(), true, emptyMap());
    }

    private void checkDump(CommandLine cmd) throws Exception {
        doWithDbFile(getDatabaseType(cmd), (databaseType) -> {
            File file = new File(databaseType);
            try {
                PostgresUtils.checkDumpFile(file);
                System.err.printf("dump OK = %s (%s)\n", file.getAbsolutePath(), byteCountToDisplaySize(file.length()));
            } catch (Exception ex) {
                System.err.printf("\ndump ERROR = %s (%s) : %s\n\n", file.getAbsolutePath(), byteCountToDisplaySize(file.length()), ex.toString());
                throw ex;
            }
        });
    }

    private static String getDatabaseType(CommandLine cmd) {
        return trimAndCheckNotBlank(cmd.getArgList().stream().skip(1).findFirst().orElse(null), "must set non-null 'dbtype' (es: 'empty','demo',...)");
    }

    private void createDatabase(CommandLine cmd, File configFile, Map<String, String> oldConfig) throws Exception {
        doWithDbFile(getDatabaseType(cmd), (databaseType) -> {

            DatabaseCreatorConfig config = DatabaseCreatorConfigImpl.builder().accept(b -> {
                if (cmd.hasOption("backuprestore")) {
                    b.withConfigImportStrategy(CIS_RESTORE_BACKUP);
                } else if (cmd.hasOption("dataonly")) {
                    b.withConfigImportStrategy(CIS_DATA_ONLY);
                }
                if (cmd.hasOption("keepconfigs")) {
                    b.withKeepLocalConfig(true);
                }
            }).withSource(databaseType).withConfig(configFile).build();

            DatabaseUtils.createDatabase(config, !cmd.hasOption("skippatches"), oldConfig);

            if (cmd.hasOption("freezesessions")) {
                System.out.println("freezing sessions");
                new DatabaseCreator(config).freezeSessions();
            }

        });

    }

    private void doWithDbFile(String databaseType, Consumer<String> consumer) {
        File toDelete = null;

        if (!EMBEDDED_DATABASES.contains(databaseType)) { //TODO improve this
            File file = getDbdumpFileOrNull(databaseType);
            if (file != null) {
                databaseType = file.getAbsolutePath();
            }
        }

        if (databaseType.endsWith(".zip")) {
            File file = new File(databaseType);
            checkArgument(file.isFile(), "invalid zip file = %s", databaseType);
            databaseType = extractDatabaseFromZipFile(file).getAbsolutePath();
            toDelete = new File(databaseType);
        }

        try {
            consumer.accept(databaseType);
        } finally {
            deleteQuietly(toDelete);
        }
    }

    private static File extractDatabaseFromZipFile(File file) {
        try (ZipFile zipFile = new ZipFile(file)) {
            Optional<? extends ZipEntry> entry = zipFile.stream().filter((e) -> e.getName().endsWith(".backup")).findAny();
            checkArgument(entry.isPresent(), "database backup not found in zip file = %s", file.getAbsolutePath());
            LoggerFactory.getLogger(DbconfigCommandRunner.class).info("selected database backup file = {}", entry.get().getName());
            File dump = tempFile(null, "dump");
            FileUtils.copyInputStreamToFile(zipFile.getInputStream(entry.get()), dump);
            return dump;
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }

}
