/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.postgres;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Predicate;
import static com.google.common.base.Strings.nullToEmpty;
import com.google.common.collect.ImmutableMap;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.getLast;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import static java.lang.Math.round;
import static java.lang.String.format;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermissions;
import static java.util.Arrays.asList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream;
import org.apache.commons.io.FileUtils;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import org.apache.commons.io.IOUtils;
import static org.apache.commons.io.IOUtils.copyLarge;
import static org.apache.commons.io.IOUtils.readLines;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.tika.Tika;
import static org.cmdbuild.utils.io.CmIoUtils.tempDir;
import static org.cmdbuild.utils.io.CmIoUtils.tempFile;
import static org.cmdbuild.utils.io.CmIoUtils.writeToFile;
import static org.cmdbuild.utils.io.CmStreamProgressUtils.detailedProgressDescription;
import static org.cmdbuild.utils.io.CmStreamProgressUtils.listenToStreamProgress;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import static org.cmdbuild.utils.postgres.PostgresUtils.POSTGRES_VERSIONS;
import static org.cmdbuild.utils.postgres.PostgresUtils.POSTGRES_VERSION_AUTO;
import static org.cmdbuild.utils.postgres.PostgresUtils.POSTGRES_VERSION_DEFAULT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmExecutorUtils.shutdownQuietly;

public class PostgresHelperImpl implements PostgresHelper {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Tika tika = new Tika();
    private final PostgresHelperConfig config;

    private String libVersion;

    public PostgresHelperImpl(PostgresHelperConfig config) {
        this.config = checkNotNull(config);
        libVersion = config.getPostgresBinariesVersion();
        checkOsIsLinux();
    }

    private void checkOsIsLinux() {
        String os = nullToEmpty(System.getProperty("os.name"));
        checkArgument(os.equalsIgnoreCase("linux"), "pg utils are not supported on this os =< %s > : you will have to import db manually", os);
    }

    @Override
    public String getServerVersion() {
        String serverVersion = executeQuery("show server_version_num", "postgres").trim();
        checkArgument(serverVersion.matches("^[0-9]{5,6}$"), "server version syntax error for value = %s", serverVersion);
        return PgVersionUtils.getPostgresServerVersionFromNumber(Integer.valueOf(serverVersion));
    }

    @Override
    public long getDatabaseSize() {
        return toLong(executeQuery("SELECT pg_database_size(current_database())", config.getDatabase()));
    }

    @Override
    public String executeQuery(String query) {
        return executeQuery(query, config.getDatabase());
    }

    @Override
    public boolean dumpContainsSchema(File dumpFile) {
        try {
            if (equal(libVersion, POSTGRES_VERSION_AUTO)) {
                libVersion = POSTGRES_VERSION_DEFAULT;//TODO detect version from dump file
            }
            dumpFile = getDumpFromFileExtractIfNecessary(dumpFile);
            String res = runCommand("pg_restore", list("-l", dumpFile.getAbsolutePath()));
            return readLines(new StringReader(res)).stream().anyMatch(l -> l.matches(format(".*SCHEMA *- *%s .*", Pattern.quote(getOnlyElement(config.getSchemas())))));
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }

    @Override
    public List<String> getTablesInDump(File dumpFile) {
        try {
            if (equal(libVersion, POSTGRES_VERSION_AUTO)) {
                libVersion = POSTGRES_VERSION_DEFAULT;//TODO detect version from dump file
            }
            dumpFile = getDumpFromFileExtractIfNecessary(dumpFile);
            String res = runCommand("pg_restore", list("-l", dumpFile.getAbsolutePath()).accept((l) -> {
                config.getSchemas().forEach(s -> l.add(format("--schema=%s", s)));
            }));
            String pattern = ".* TABLE [^ ]+ ([^ ]+) .*";
            return readLines(new StringReader(res)).stream().filter(l -> l.matches(pattern)).map(l -> {
                Matcher matcher = Pattern.compile(pattern).matcher(l);
                checkArgument(matcher.find());
                return checkNotBlank(matcher.group(1));
            }).collect(toList());
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }

    @Override
    public void checkDumpFile(File file) {
        if (equal(libVersion, POSTGRES_VERSION_AUTO)) {
            libVersion = POSTGRES_VERSION_DEFAULT;//TODO detect version from dump file; duplicate code!
        }
        runCommand("pg_restore", list(file.getAbsolutePath(), "-f", "/dev/null"));
    }

    @Override
    public void restoreDumpFromFile(File file, @Nullable Predicate<String> itemLineFilter) {
        try {
            checkNotNull(file);
            checkArgument(file.isFile());
            logger.debug("restore from file = {} ({}) to database = {}", file.getAbsolutePath(), FileUtils.byteCountToDisplaySize(file.length()), config.getDatabase());
            File sourceFile = getDumpFromFileExtractIfNecessary(file);
            if (!config.getSchemas().isEmpty() && config.getCreateSchema()) {
                config.getSchemas().stream().filter((schema) -> (!equal(schema, "public"))).forEachOrdered((schema) -> {
                    // we don't need to create public schema
                    runCommand("psql", list(userHostPortParams()).with(
                            "--echo-all",
                            "--dbname", config.getDatabase(),
                            "--command", format("CREATE SCHEMA IF NOT EXISTS \"%s\"", schema)));
                });
            }
            List<String> command = list(userHostPortParams()).with(
                    "--dbname", config.getDatabase(),
                    //				"--exit-on-error",
                    "--no-owner", // any user name can be used for the initial connection, and this user will own all the created objects.
                    "--no-tablespaces", // Do not output commands to select tablespaces. With this option, all objects will be created in whichever tablespace is the default during restore.
                    "--no-privileges", // Prevent restoration of access privileges (grant/revoke commands).
                    "--jobs", String.valueOf(ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors()))
                    .with(transform(config.getSchemas(), (String s) -> "--schema=" + s));

            if (itemLineFilter != null) {

//				List<String> tablesInDump = getTablesInDump(sourceFile);
                List<String> itemList = readLines(new StringReader(runCommand("pg_restore", "-l", sourceFile.getAbsolutePath())));
                itemList = itemList.stream().filter(itemLineFilter).collect(toList());
                File tempListing = tempFile(null, ".txt");
                writeToFile(tempListing, Joiner.on("\n").join(itemList));
                command.add(format("--use-list=%s", tempListing.getAbsolutePath()));
//				String regex = config.getTables().stream().map(t->t.toLowerCase().replaceAll("[^a-z0-9]+", ".*")).collect(joining("|"));

//				itemList= itemList.stream().filter(i->        ).collect(toList());
//			config.getTables().stream().map(t -> format("--table=%s", t)).forEach(command::add);
            }

            command.add(sourceFile.getAbsolutePath());
            runCommand("pg_restore", command);
            logger.debug("restored from file = {} ({}) to database = {}", file.getAbsolutePath(), FileUtils.byteCountToDisplaySize(file.length()), config.getDatabase());
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }

    private String executeQuery(String query, String dbName) {
        return runCommand("psql", list(userHostPortParams()).with("--dbname", dbName, "--no-align", "--tuples-only", "--command", checkNotBlank(query))).trim();
    }

    private File getDumpFromFileExtractIfNecessary(File file) {
        if (isCompressedWithXzip(file)) {
            logger.debug("the dump file is compressed with xz, decompressing before import");
            File res = tempFile("file_", ".dump");
            try (InputStream in = new FileInputStream(file); OutputStream out = new FileOutputStream(res);
                    CompressorInputStream compressorInputStream = new CompressorStreamFactory().createCompressorInputStream(CompressorStreamFactory.XZ, in)) {
                IOUtils.copy(compressorInputStream, out);
            } catch (IOException | CompressorException ex) {
                throw runtime(ex);
            }
            return res;
        } else {
            return file;
        }
    }

    private boolean isCompressedWithXzip(File file) {
        try {
            return tika.detect(file).contains("application/x-xz");
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }

    public static int getCpuNumSafe() { //TODO move this to utils
        try {
            return ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
        } catch (Exception ex) {
//            logger.debug("error",ex);
            //TODO log
            return 1;
        }
    }

    @Override
    public void dumpDatabaseToFile(File file) {
        checkNotNull(file);
        String schemas;
        if (config.getSchemas().isEmpty()) {
            schemas = "*";
        } else {
            schemas = Joiner.on("|").join(config.getSchemas());
        }
        logger.debug("dump database {} (schema = {}) to file = {}", config.getDatabase(), schemas, file.getAbsolutePath());
        boolean enableXzCompression = file.getName().endsWith(".xz") || config.getXzCompression();
        File dumpFile;
        if (enableXzCompression) {
            dumpFile = tempFile("file_", ".dump");
        } else {
            dumpFile = file;
        }
        long dbSize = getDatabaseSize(), estimatePgDumpSize, estimateFinalDumpSize;
        if (enableXzCompression) {
            estimatePgDumpSize = dbSize;
            estimateFinalDumpSize = round(dbSize * 0.045d);//estimate xz compression ratio of uncompressed pg custom dump file
        } else {
            estimateFinalDumpSize = estimatePgDumpSize = round(dbSize * 0.14632d);//estimate pg custom dump compression ratio (when used with standard compression)
        }
        logger.debug("database size = {} estimate dump size = {}", byteCountToDisplaySize(dbSize), byteCountToDisplaySize(estimateFinalDumpSize));
        try {
            long beginTimestamp = System.currentTimeMillis();
            ScheduledExecutorService dumpMonitor = Executors.newSingleThreadScheduledExecutor();
            dumpMonitor.submit(() -> logger.debug("start progress monitor"));
            dumpMonitor.scheduleAtFixedRate(() -> {
                try {
                    long currentSize = dumpFile.exists() ? dumpFile.length() : 0;
                    logger.debug("dump progress: {}", detailedProgressDescription(currentSize, estimatePgDumpSize, beginTimestamp));
                } catch (Exception ex) {
                    logger.warn("error", ex);
                }
            }, 2, 2, TimeUnit.SECONDS);
            runCommand("pg_dump", list(userHostPortParams()).with(
                    "--format", "custom",
                    "--no-owner", "--no-privileges",
                    "--quote-all-identifiers",
                    "--schema", schemas,
                    "--file", dumpFile.getAbsolutePath(),
                    config.getDatabase()).accept(l -> {
                if (enableXzCompression) {
                    l.add("--compress", "0");//xz compression is more effective when working on plain dump (avoid double compression overhead)
                }
            }));
            shutdownQuietly(dumpMonitor);
            checkArgument(dumpFile.isFile() && dumpFile.length() > 0, "pg_dump error: dump file was not created");
            logger.debug("dump complete");
            if (enableXzCompression) {
                logger.debug("compressing dump file with xz");//TODO compress command output stream, avoid intermediate file
                try (OutputStream out = new FileOutputStream(file); InputStream in = new FileInputStream(dumpFile); CompressorOutputStream compressorOutputStream = new XZCompressorOutputStream(out, 9)) {
                    copyLarge(listenToStreamProgress(in, e -> {
                        logger.debug("compression {} -> {} progress: {}  {} IN {} OUT ({})", dumpFile.getName(), file.getName(), e.getProgressDescription(), byteCountToDisplaySize(e.getCount()), byteCountToDisplaySize(file.length()), e.getProgressDescriptionEta());
                    }), compressorOutputStream);
                } catch (Exception ex) {
                    throw runtime(ex);
                }
            }
        } finally {
            if (enableXzCompression) {
                FileUtils.deleteQuietly(dumpFile);
            }
        }
        logger.debug("dumped database {} (schema = {}) to file {} ({})", config.getDatabase(), schemas, file.getAbsolutePath(), FileUtils.byteCountToDisplaySize(file.length()));
    }

    public void executeScript(String database, String sqlScript) {
        checkNotBlank(sqlScript);
        checkNotBlank(database);
        File tempFile = tempFile();
        try {
            writeToFile(tempFile, sqlScript);
            runCommand("psql", list(userHostPortParams()).with("--dbname", database, "--file", tempFile.getAbsolutePath()));
        } finally {
            deleteQuietly(tempFile);
        }
    }

    @Override
    public String runCommand(String command, List<String> params) {
        if (equal(libVersion, POSTGRES_VERSION_AUTO)) {
            logger.debug("auto detect server version");
            List<String> workingVersions = list();
            List<Exception> errors = list();
            String exactMatch = null;
            String versionFromServer;
            for (String version : POSTGRES_VERSIONS) {
                libVersion = version;
                try {
                    versionFromServer = getServerVersion();
                    workingVersions.add(version);
                } catch (Exception ex) {
                    errors.add(ex);
                    logger.debug("psql version = {} does not work with this server, trying next", version, ex);
                    continue;
                }
                if (versionFromServer != null) {
                    String serverMajorMinor = versionFromServer.replaceFirst("[.][0-9]+$", "");
                    exactMatch = find(POSTGRES_VERSIONS, (String s) -> s.startsWith(serverMajorMinor + "."), null);
                    if (exactMatch != null) {
                        break;
                    }
                }
            }
            if (exactMatch != null) {
                logger.debug("selected matching version = {}", exactMatch);
                libVersion = exactMatch;
            } else if (!workingVersions.isEmpty()) {
                libVersion = getLast(workingVersions);
                logger.debug("selected best effort working version = {}", libVersion);
            } else {
                throw runtime(getLast(errors), "unable to find working psql version for this postgres server");
            }
        }
        try (PostgresOperation operation = new PostgresOperation()) {
            if (config.getVerbose()) {
                params = list("--verbose").with(params);
            }
            Pair<Integer, String> res = operation.prepare().runCommand(command, params);
            logger.debug("process output = \n\n{}", res.getRight());

            boolean hasError = res.getLeft() != 0 || Pattern.compile("ERROR", Pattern.CASE_INSENSITIVE & Pattern.DOTALL).matcher(res.getRight()).find();
            if (hasError) {
                if (config.handleExitStatus()) {
                    throw runtime("command %s return error code = %s : %s", command, res.getLeft(), abbreviate(res.getRight()));
                } else {
                    logger.warn(MarkerFactory.getMarker("NOTIFY"), "command {} return error code = {} : {}", command, res.getLeft(), abbreviate(res.getRight()));//TODO duplicate marker factory code
                }
            }
            return res.getValue();

        } catch (Exception ex) {
            throw runtime(ex);
        }
    }

    private List<String> userHostPortParams() {
        return list("--username", config.getUsername(), "--host", config.getHost(), "--port", String.valueOf(config.getPort()));
    }

    private class PostgresOperation implements AutoCloseable {

        private File pgpass, logfile, pwd, bin;
        private Map<String, String> env;

        public PostgresOperation prepare() throws IOException, ZipException {
            pwd = tempDir("postgres_command_");
            logger.debug("create working dir = {} with psql version = {}", pwd.getAbsolutePath(), libVersion);
            checkArgument(pwd.isDirectory());
            pgpass = new File(pwd, ".pgpass");
            logfile = new File(pwd, "command.log");
            FileUtils.writeStringToFile(pgpass, "\n*:*:*:*:" + config.getPassword().replaceAll("[:\\\\]", "\\\\$0"), Charsets.UTF_8); //TODO check password escape
            Files.setPosixFilePermissions(pgpass.toPath(), PosixFilePermissions.fromString("r--------"));
            env = ImmutableMap.of("PGPASSFILE", pgpass.getAbsolutePath());
            {
                File commands = new File(pwd, "commands.zip");
                FileUtils.copyInputStreamToFile(getClass().getResourceAsStream("postgresql-" + libVersion + "-linux-x64-binaries_stripped.zip"), commands);
                ZipFile zipFile = new ZipFile(commands);
                zipFile.extractAll(pwd.getAbsolutePath());
                FileUtils.deleteQuietly(commands);
                for (File executableFile : (bin = new File(pwd, "pgsql/bin/")).listFiles()) {
                    Files.setPosixFilePermissions(executableFile.toPath(), PosixFilePermissions.fromString("r-x------"));
                }
            }
            return this;
        }

        @Override
        public void close() throws Exception {
            logger.debug("clear working dir = {}", pwd.getAbsolutePath());
            FileUtils.deleteQuietly(pwd);
            pwd = pgpass = logfile = null;
            env = null;
        }

        public Pair<Integer, String> runCommand(String command, List<String> commandParams) throws Exception {
            File executableFile = new File(bin, command);
            checkArgument(executableFile.isFile());
            List<String> params = newArrayList(concat(asList(executableFile.getAbsolutePath()), commandParams));
            logger.debug("running command {}", Joiner.on(" ").join(params));
            ProcessBuilder processBuilder = new ProcessBuilder(params)
                    .redirectErrorStream(true)
                    .redirectOutput(logfile)
                    .directory(pwd);
            processBuilder.environment().putAll(env);
            int res = processBuilder.start().waitFor();
            String output = FileUtils.readFileToString(logfile, Charsets.UTF_8);
            return Pair.of(res, output);
        }
    }

}
