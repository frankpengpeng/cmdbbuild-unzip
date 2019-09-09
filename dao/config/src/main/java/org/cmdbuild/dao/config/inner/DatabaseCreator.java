package org.cmdbuild.dao.config.inner;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableSet;
import static com.google.common.collect.Maps.filterKeys;
import java.io.File;
import static java.lang.String.format;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.toSet;
import javax.annotation.Nullable;

import javax.sql.DataSource;
import static org.apache.commons.io.FilenameUtils.getBaseName;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.config.api.ConfigCategory;
import static org.cmdbuild.config.api.ConfigCategory.CC_DEFAULT;
import static org.cmdbuild.config.api.ConfigCategory.CC_ENV;
import org.cmdbuild.config.api.ConfigDefinition;
import static org.cmdbuild.config.utils.ConfigDefinitionUtils.getAllConfigDefinitionsFromClasspath;
import org.cmdbuild.dao.DaoException;
import org.cmdbuild.dao.config.utils.PatchManagerUtils;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.systemToSqlExpr;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_STRINGS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;

import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.utils.postgres.PostgresUtils;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import org.cmdbuild.utils.postgres.PostgresServerHelper;

public final class DatabaseCreator {

    public final static String EXISTING_DATABASE = "existing",
            EMPTY_DUMP = "empty.dump.xz",
            DEMO_DUMP = "demo.dump.xz",
            R2U_DUMP = "ready2use.dump.xz";

    public static final Set<String> EMBEDDED_DATABASES = ImmutableSet.of(EXISTING_DATABASE, EMPTY_DUMP, DEMO_DUMP, R2U_DUMP);

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static final String POSTGRES_SUPER_DATABASE = "postgres";

    public static final String SHARK_PASSWORD = "shark";
    public static final String SHARK_USERNAME = "shark";
    private static final String SHARK_SCHEMA = "shark", QUARTZ_SCHEMA = "quartz", GIS_SCHEMA = "gis", PUBLIC_SCHEMA = "public";

    private static final String CREATE_SCHEMA = "CREATE SCHEMA IF NOT EXISTS \"%s\"";
    private static final String GRANT_SCHEMA_PRIVILEGES = "GRANT ALL ON SCHEMA \"%s\" TO \"%s\"";

    private DatabaseCreatorConfig config = DatabaseCreatorConfigImpl.builder().build();
    private PostgresServerHelper serverHelper;

    public DatabaseCreator() {
    }

    public DatabaseCreator(DatabaseCreatorConfig config) {
        setConfig(config);
    }

    public void setConfig(DatabaseCreatorConfig config) {
        this.config = checkNotNull(config);
    }

    public String getDatabaseUrl() {
        return config.getDatabaseUrl();
    }

    public DatabaseCreatorConfig getConfig() {
        return config;
    }

    private String getSqlPath(String sub) {
        checkArgument(!StringUtils.isBlank(config.getSqlPath()));
        return new File(config.getSqlPath(), sub).getAbsolutePath();
    }

    private String getSharkSqlPath() {
        return getSqlPath("schemas/shark_schema");
    }

    private boolean hasDumpDir() {
        return isNotBlank(config.getSqlPath()) && getDumpDir().isDirectory();
    }

    private File getDumpDir() {
        return checkNotNull(getDumpDirOrNull());
    }

    @Nullable
    private File getDumpDirOrNull() {
        if (isBlank(config.getSqlPath())) {
            return null;
        } else {
            return new File(config.getSqlPath(), "dump");
        }
    }

    public DataSource getAdminDataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setServerName(config.getHost());
        dataSource.setPortNumber(config.getPort());
        dataSource.setUser(config.getAdminUser());
        dataSource.setPassword(config.getAdminPassword());
        dataSource.setDatabaseName(POSTGRES_SUPER_DATABASE);
        return dataSource;
    }

    public DataSource getCmdbuildDataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setServerName(config.getHost());
        dataSource.setPortNumber(config.getPort());
        dataSource.setUser(config.getCmdbuildUser());
        dataSource.setPassword(config.getCmdbuildPassword());
        dataSource.setDatabaseName(config.getDatabaseName());
        return dataSource;
    }

    public DataSource sharkDataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setServerName(config.getHost());
        dataSource.setPortNumber(config.getPort());
        dataSource.setUser(SHARK_USERNAME);
        dataSource.setPassword(SHARK_PASSWORD);
        dataSource.setDatabaseName(config.getDatabaseName());
        return dataSource;
    }

    public boolean cmdbuildDatabaseExists() { //TODO check with a query ?
        logger.info("checking database");
        DataSource dataSource = getCmdbuildDataSource();
        try {
            try (Connection connection = dataSource.getConnection()) {
                checkNotNull(connection);
                logger.info("database found");
                return true;
            }
        } catch (SQLException ex) {
            logger.info("database not found");
            return false;
        }
    }

    public boolean useExistingDatabase() {
        return equal(EXISTING_DATABASE, config.getDatabaseType());
    }

    public void configureDatabase() {
        try {
            if (config.installPostgres()) {
                doInstallPostgres();
            }

            if (!useExistingDatabase()) {
                logger.info("create database = {} from source = {}", config.getDatabaseUrl(), config.getDatabaseType());

                if (config.createDatabase()) {
                    checkArgument(!cmdbuildDatabaseExists(), "database %s already exists; if you really want to trash it, drop it manually before running this procedure", config.getDatabaseName());
                    createDatabase();
                } else {
                    checkArgument(cmdbuildDatabaseExists(), "database not found = %s", config.getDatabaseName());
                }

                if (config.useLimitedUser() && config.isLimitedUserNotEqualToAdminUser()) {
                    createUser(config.getLimitedUser(), config.getLimitedPassword());
                    getAdminJdbcTemplate().execute(format("ALTER DATABASE \"%s\" OWNER TO \"%s\"", config.getDatabaseName(), config.getCmdbuildUser()));
                    getAdminJdbcTemplate().update(format("ALTER USER \"%s\" SUPERUSER", config.getLimitedUser()));
                }

                restoreDump();

                //TODO move this to custom sql function (?)
                getCmdbuildJdbcTemplate().execute("CREATE TABLE IF NOT EXISTS \"_DatabaseImportLog\" (\"Source\" varchar not null,\"ImportTime\" timestamp not null default now())");
                getCmdbuildJdbcTemplate().update("INSERT INTO \"_DatabaseImportLog\" (\"Source\") VALUES (?)", config.getDatabaseType());

                if (config.useLimitedUser() && config.isLimitedUserNotEqualToAdminUser()) {
                    getAdminJdbcTemplate().update(format("ALTER USER \"%s\" NOSUPERUSER", config.getLimitedUser()));
                }

            }
        } catch (Exception e) {
            throw new DaoException(e, "Error while configuring the database");
        }
    }

    private void doInstallPostgres() {
        logger.info("install postgres server");
        serverHelper = PostgresUtils.serverHelper()
                .withServerPort(config.getPort())
                .withAdminPassword(config.getAdminPassword())
                .installAndStartPostgres();
    }

    public void applyPatches() {
        PatchManagerUtils.applyPatches(this, new File(config.getSqlPath()));
    }

    public void adjustConfigs() {
        adjustConfigs(null);
    }

    public void adjustConfigs(@Nullable Map<String, String> previousCmdbuildConfigs) {
        JdbcTemplate jdbcTemplate = getCmdbuildJdbcTemplate();
        Set<ConfigCategory> categoriesToSkip;
        switch (config.getConfigImportStrategy()) {
            case CIS_RESTORE_BACKUP:
                logger.debug("backup restore mode, import all configs");
                categoriesToSkip = emptySet();
                break;
            case CIS_DEFAULT:
                logger.debug("default restore mode, do not import env configs");
                categoriesToSkip = EnumSet.of(CC_ENV);
                break;
            case CIS_DATA_ONLY:
                logger.debug("data only restore mode, import only data configs");
                categoriesToSkip = EnumSet.of(CC_ENV, CC_DEFAULT);
                break;
            default:
                throw new DaoException("unsupported config import strategy = %s", config.getConfigImportStrategy());
        }
        Set<String> configsToSkipFromImport = getAllConfigDefinitionsFromClasspath().stream().filter(ConfigDefinition::isLocationDefault).filter(d -> categoriesToSkip.contains(d.getCategory())).map(ConfigDefinition::getKey).collect(toSet());
        configsToSkipFromImport.forEach(c -> jdbcTemplate.execute(format("DO $$ BEGIN IF to_regclass('\"_SystemConfig\"') IS NOT NULL THEN UPDATE \"_SystemConfig\" SET \"Status\" = 'N', \"Notes\" = 'disabled after import' WHERE \"Status\" = 'A' AND \"Code\" = %s; END IF; END $$ LANGUAGE PLPGSQL;", systemToSqlExpr(c))));

        if (!firstNotNull(previousCmdbuildConfigs, emptyMap()).isEmpty() && config.keepLocalConfig()) {
            logger.debug("keep local configs (from previous db)");
            filterKeys(previousCmdbuildConfigs, configsToSkipFromImport::contains).forEach((k, v) -> {
                jdbcTemplate.execute(format("DO $$ BEGIN IF to_regclass('\"_SystemConfig\"') IS NOT NULL THEN INSERT INTO \"_SystemConfig\" (\"Code\", \"Value\") VALUES (%s, %s); END IF; END $$ LANGUAGE PLPGSQL;", systemToSqlExpr(k), systemToSqlExpr(v)));
            });
        }
    }

    public Map<String, String> getSystemConfigsFromDbSafe() {
        try {
            if (cmdbuildDatabaseExists()) {
                return getSystemConfigsFromDb();
            } else {
                return emptyMap();
            }
        } catch (Exception ex) {
            logger.warn("error retrieving currend system config from db", ex);
            return emptyMap();
        }
    }

    public Map<String, String> getSystemConfigsFromDb() {
        try {
            DataSource dataSource = getCmdbuildDataSource();
            try (Connection connection = dataSource.getConnection()) {
                connection.createStatement().executeUpdate("CREATE OR REPLACE FUNCTION pg_temp._cm3_query_aux_configs_as_map_safe() RETURNS jsonb AS $$ BEGIN IF to_regclass('\"_SystemConfig\"') IS NOT NULL THEN RETURN (SELECT COALESCE(jsonb_object_agg(\"Code\", \"Value\"), '{}'::jsonb) FROM \"_SystemConfig\" WHERE \"Status\" = 'A'); ELSE RETURN '{}'::jsonb; END IF; END $$ LANGUAGE PLPGSQL;");
                try (ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM pg_temp._cm3_query_aux_configs_as_map_safe() _config;")) {
                    checkArgument(resultSet.next());
                    return fromJson(resultSet.getString("_config"), MAP_OF_STRINGS);
                } finally {
                    connection.createStatement().executeUpdate("DROP FUNCTION pg_temp._cm3_query_aux_configs_as_map_safe()");
                }
            }
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
    }

    private void createDatabase() {
        logger.info("Creating database {}", config.getDatabaseName());
        String query = format("CREATE DATABASE \"%s\" ENCODING 'UTF8'", checkValidName(config.getDatabaseName()));
        if (isNotBlank(config.getTablespace())) {
            query += format(" TABLESPACE %s", config.getTablespace());
        }
        getAdminJdbcTemplate().execute(query);
    }

    private void restoreDump() {
        try {
            String dumpToRestore = checkNotBlank(config.getDatabaseType(), "database type is null");
            File dumpFile = getDumpFile(dumpToRestore);
            logger.info("restoring database from dump = {}", dumpFile.getAbsolutePath());
            checkArgument(dumpFile.isFile() && dumpFile.length() > 0, "invalid dump file = %s", dumpFile);
            boolean hasGisSchema = PostgresUtils.dumpContainsSchema(dumpFile, "gis");
            if (hasGisSchema) {
                logger.info("creating gis schema with postgis extension");
                new JdbcTemplate(getCmdbuildDataSource()).execute("CREATE SCHEMA gis");
                new JdbcTemplate(getCmdbuildDataSource()).execute("CREATE EXTENSION postgis SCHEMA gis");
            }
            if (config.useSharkSchema()) {
                createSharkRole();
                createSchema(SHARK_SCHEMA);
                grantSchemaPrivileges(SHARK_SCHEMA, SHARK_USERNAME);
                logger.info("restore shark schema from dump");
                PostgresUtils.newHelper(config.getHost(), config.getPort(), SHARK_USERNAME, SHARK_PASSWORD)
                        .withCreateSchema(false)
                        .withSchema(SHARK_SCHEMA)
                        .withDatabase(config.getDatabaseName())
                        .buildHelper()
                        .restoreDumpFromFile(dumpFile);
            }
            logger.info("restore public,quartz schemas from dump");
            PostgresUtils.newHelper(config.getHost(), config.getPort(), config.getCmdbuildUser(), config.getCmdbuildPassword())
                    .withDatabase(config.getDatabaseName())
                    .withSchemas(list(PUBLIC_SCHEMA, QUARTZ_SCHEMA, "bim"))
                    .buildHelper()
                    .restoreDumpFromFile(dumpFile);
            if (hasGisSchema) {
                logger.info("restore gis schema from dump");
                PostgresUtils.newHelper(config.getHost(), config.getPort(), config.getCmdbuildUser(), config.getCmdbuildPassword())
                        .withDatabase(config.getDatabaseName())
                        .withSchema(GIS_SCHEMA)
                        .buildHelper()
                        .restoreDumpFromFile(dumpFile, (s) -> {
                            return s.matches(".* (TABLE|COMMENT|TRIGGER|INDEX|CONSTRAINT) .*") && !s.matches(".*(spatial_ref|geometry_columns|geography_columns).*");
                        });
            }
        } catch (Exception ex) {
            throw new DaoException(ex);
        }
    }

    private File getDumpFile(String dumpName) {
        checkNotBlank(dumpName);
        return list(new File(dumpName)).accept(l -> {
            if (hasDumpDir()) {
                list(getDumpDir().listFiles()).stream().filter(f -> equal(f.getName(), dumpName)).forEach(l::add);
                list(getDumpDir().listFiles()).stream().filter(f -> equal(getBaseName(getBaseName(f.getName())), getBaseName(getBaseName(dumpName)))).forEach(l::add);
            }
        }).stream().filter(File::exists).findFirst().orElseThrow(() -> new DaoException("dump file not found for name = %s", dumpName));
    }

//    private void createSharkTables() {
//        try {
//            logger.info("Creating shark tables");
//            new JdbcTemplate(sharkDataSource()).execute(FileUtils.readFileToString(new File(getSharkSqlPath(), "02_shark_emptydb.sql")));
//        } catch (IOException ex) {
//            throw new DaoException(ex);
//        }
//    }
    private void createSchema(String schema) {
        logger.info("create schema = {}", schema);
        getCmdbuildJdbcTemplate().execute(String.format(CREATE_SCHEMA, checkValidName(schema)));
    }

    private JdbcTemplate getAdminJdbcTemplate() {
        return new JdbcTemplate(getAdminDataSource());
    }

    private JdbcTemplate getCmdbuildJdbcTemplate() {
        return new JdbcTemplate(getCmdbuildDataSource());
    }

    private void grantSchemaPrivileges(String schema, String role) {
        logger.info("Granting schema privileges");
        new JdbcTemplate(getCmdbuildDataSource()).execute(String.format(GRANT_SCHEMA_PRIVILEGES, checkValidName(schema), checkValidName(role)));
    }

    private void createUser(String roleName, String rolePassword) {
        logger.info("Creating role = {}", roleName);
        checkValidName(roleName);
        checkArgument(!rolePassword.contains("'"), "invalid password syntax");
        getAdminJdbcTemplate().execute(format("DO $$ BEGIN IF NOT EXISTS (SELECT * FROM pg_roles WHERE rolname = '%s') THEN CREATE USER \"%s\" PASSWORD '%s'; END IF; END $$ LANGUAGE PLPGSQL", roleName, roleName, rolePassword));
    }

    private void createSharkRole() {
        logger.info("Creating shark role");
        createUser(SHARK_USERNAME, SHARK_PASSWORD);
        getAdminJdbcTemplate().execute(String.format("ALTER ROLE \"%s\" SET search_path=%s", SHARK_USERNAME, "pg_default,shark"));
    }

    private String checkValidName(String name) {
        checkArgument(!name.contains("\""), "invalid name = %s", name);
        return name;
    }

    public void dropDatabase() {
        logger.info("drop database = {}", config.getDatabaseUrl());
        String dbName = checkValidName(config.getDatabaseName()); //TODO make connection termination configurable (?)
        getAdminJdbcTemplate().execute(format("SELECT pg_terminate_backend(pid) from pg_stat_activity WHERE pg_stat_activity.datname = '%s' AND pid <> pg_backend_pid(); DROP DATABASE IF EXISTS \"%s\"", dbName, dbName));

        if (config.installPostgres() && serverHelper != null) {
            serverHelper.uninstallPostgres();
            serverHelper = null;
        }
    }

    public void freezeSessions() {
        logger.info("freeze sessions on db (set expiration strategy to 'never')");
        getCmdbuildJdbcTemplate().execute("UPDATE \"_Session\" SET \"ExpirationStrategy\" = 'never'");
    }

}
