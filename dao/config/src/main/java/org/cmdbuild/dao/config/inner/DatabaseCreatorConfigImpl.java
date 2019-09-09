package org.cmdbuild.dao.config.inner;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.commons.lang3.builder.Builder;
import static org.cmdbuild.dao.config.DatabaseConfiguration.DATABASE_CONFIG_ADMIN_PASSWORD;
import static org.cmdbuild.dao.config.DatabaseConfiguration.DATABASE_CONFIG_ADMIN_USERNAME;
import static org.cmdbuild.dao.config.DatabaseConfiguration.DATABASE_CONFIG_PASSWORD;
import static org.cmdbuild.dao.config.DatabaseConfiguration.DATABASE_CONFIG_TABLESPACE;
import static org.cmdbuild.dao.config.DatabaseConfiguration.DATABASE_CONFIG_URL;
import static org.cmdbuild.dao.config.DatabaseConfiguration.DATABASE_CONFIG_USERNAME;
import static org.cmdbuild.dao.config.inner.ConfigImportStrategy.CIS_DEFAULT;
import static org.cmdbuild.utils.io.CmPropertyUtils.loadProperties;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import org.cmdbuild.utils.postgres.PostgresUtils;

public final class DatabaseCreatorConfigImpl implements DatabaseCreatorConfig {

    private final static String JDBC_URL_PATTERN = "^jdbc:postgresql://([^:]+):([0-9]+)/(.+)$";

    private final String adminUser, adminPassword, limitedUser, limitedPassword, sqlPath, dbHost, dbName;
    private final int dbPort;
    private final String cmdbuildDatabaseType, tablespace;
    private final boolean useLimited, useSharkSchema, createDatabase, keepLocalConfig, installPostgres;
    private final ConfigImportStrategy configImportStrategy;

    private DatabaseCreatorConfigImpl(DatabaseCreatorConfigBuilder builder) {
        this.adminUser = builder.adminUser;
        this.adminPassword = builder.adminPassword;
        this.limitedUser = builder.limitedUser;
        this.limitedPassword = builder.limitedPassword;
        this.sqlPath = builder.sqlPath;
        this.dbHost = checkNotBlank(builder.dbHost);
        this.dbName = checkNotBlank(builder.dbName);
        this.dbPort = builder.dbPort;
        this.cmdbuildDatabaseType = builder.cmdbuildDatabaseType;
        this.useLimited = builder.useLimited;
        this.useSharkSchema = builder.useSharkSchema;
        this.tablespace = builder.tablespace;
        this.createDatabase = firstNonNull(builder.createDatabase, true);
        this.keepLocalConfig = firstNonNull(builder.keepLocalConfig, false);
        this.installPostgres = firstNonNull(builder.installPostgres, false);
        this.configImportStrategy = firstNonNull(builder.configImportStrategy, CIS_DEFAULT);
    }

    @Override
    @Nullable
    public String getTablespace() {
        return tablespace;
    }

    @Override
    public boolean createDatabase() {
        return createDatabase;
    }

    @Override
    public Map<String, String> getConfig() {
        return (Map) map(DATABASE_CONFIG_URL, getDatabaseUrl(),
                DATABASE_CONFIG_USERNAME, getCmdbuildUser(),
                DATABASE_CONFIG_PASSWORD, getCmdbuildPassword())
                .skipNullValues()
                .with(
                        DATABASE_CONFIG_ADMIN_USERNAME, emptyToNull(getAdminUser()),
                        DATABASE_CONFIG_ADMIN_PASSWORD, emptyToNull(getAdminPassword()),
                        DATABASE_CONFIG_TABLESPACE, emptyToNull(getTablespace()));
    }

    @Override
    public boolean installPostgres() {
        return installPostgres;
    }

    @Override
    public boolean keepLocalConfig() {
        return keepLocalConfig;
    }

    @Override
    public ConfigImportStrategy getConfigImportStrategy() {
        return configImportStrategy;
    }

    @Override
    public boolean useLimitedUser() {
        return useLimited;
    }

    @Override
    public boolean useSharkSchema() {
        return useSharkSchema;
    }

    @Override
    public String getCmdbuildUser() {
        if (useLimitedUser()) {
            checkNotNull(limitedUser, "limited user enabled but limited username not set!");
            return limitedUser;
        } else {
            return getAdminUser();
        }
    }

    @Override
    public String getCmdbuildPassword() {
        if (useLimitedUser()) {
            checkNotNull(limitedPassword, "limited user enabled but limited password not set!");
            return limitedPassword;
        } else {
            return getAdminPassword();
        }
    }

    @Override
    public String getDatabaseUrl() {
        return String.format("jdbc:postgresql://%1$s:%2$s/%3$s", checkNotNull(dbHost), checkNotNull(dbPort), checkNotNull(dbName));
    }

    @Override
    public String getHost() {
        return dbHost;
    }

    @Override
    public int getPort() {
        return dbPort;
    }

    @Override
    public String getDatabaseName() {
        return dbName;
    }

    @Override
    public String getAdminUser() {
        return adminUser;
    }

    @Override
    public String getAdminPassword() {
        return adminPassword;
    }

    @Override
    public String getLimitedUser() {
        return limitedUser;
    }

    @Override
    public String getLimitedPassword() {
        return limitedPassword;
    }

    @Override
    public String getDatabaseType() {
        checkNotNull(emptyToNull(cmdbuildDatabaseType), "database type not set !");
        return cmdbuildDatabaseType;
    }

    @Override
    public String getSqlPath() {
        return sqlPath;
    }

    @Override
    public void checkConfig() {
        String serverVersion = PostgresUtils.newHelper(getHost(), getPort(), getCmdbuildUser(), getCmdbuildPassword()).buildHelper().getServerVersion();
//TODO more checks
    }

    public static DatabaseCreatorConfigBuilder builder() {
        return new DatabaseCreatorConfigBuilder();
    }

    public static DatabaseCreatorConfig fromFile(InputStream source) {
        return builder().withConfig(source).build();
    }

    public static DatabaseCreatorConfigBuilder copyOf(DatabaseCreatorConfig source) {
        return new DatabaseCreatorConfigBuilder()
                .withKeepLocalConfig(source.keepLocalConfig())
                .withConfigImportStrategy(source.getConfigImportStrategy())
                .withAdminUser(source.getAdminUser(), source.getAdminPassword())
                .withLimitedUser(source.getLimitedUser(), source.getLimitedPassword())
                .withSqlPath(source.getSqlPath())
                .withDatabaseUrl(source.getDatabaseUrl()).withSource(source.getDatabaseType())
                .withUseLimitedUser(source.useLimitedUser())
                .withCreateDatabase(source.createDatabase())
                .withUseSharkSchema(source.useSharkSchema())
                .withInstallPostgres(source.installPostgres())
                .withTablespace(source.getTablespace());
    }

    public static class DatabaseCreatorConfigBuilder implements Builder<DatabaseCreatorConfig> {

        private String adminUser, adminPassword, limitedUser, limitedPassword, sqlPath, dbHost = "localhost", dbName = "cmdbuild", tablespace;
        private Integer dbPort = 5432;
        private String cmdbuildDatabaseType;
        private boolean useLimited = false, useSharkSchema = false;
        private Boolean createDatabase, keepLocalConfig, installPostgres;
        private ConfigImportStrategy configImportStrategy;

        private DatabaseCreatorConfigBuilder() {
        }

        public DatabaseCreatorConfigBuilder withConfig(Map<String, String> config) {
            String defaultUser = emptyToNull(config.get(DATABASE_CONFIG_USERNAME)),
                    defaultPsw = emptyToNull(config.get(DATABASE_CONFIG_PASSWORD));
            String adminUserParam = firstNotBlank(config.get(DATABASE_CONFIG_ADMIN_USERNAME), defaultUser),
                    adminPswParam = firstNotBlank(config.get(DATABASE_CONFIG_ADMIN_PASSWORD), defaultPsw);
            String limitedUserParam = firstNotBlank(config.get("db.user.username"), defaultUser),
                    limitedPswParam = firstNotBlank(config.get("db.user.password"), defaultPsw);
            tablespace = config.get(DATABASE_CONFIG_TABLESPACE);

            withDatabaseUrl(config.get(DATABASE_CONFIG_URL))
                    .withAdminUser(adminUserParam, adminPswParam)
                    .withUseLimitedUser(true)
                    .withLimitedUser(limitedUserParam, limitedPswParam);
            return this;
        }

        public DatabaseCreatorConfigBuilder withUseLimitedUser(boolean useLimited) {
            this.useLimited = useLimited;
            return this;
        }

        public DatabaseCreatorConfigBuilder withInstallPostgres(boolean installPostgres) {
            this.installPostgres = installPostgres;
            return this;
        }

        public DatabaseCreatorConfigBuilder withTablespace(String tablespace) {
            this.tablespace = tablespace;
            return this;
        }

        public DatabaseCreatorConfigBuilder withUseSharkSchema(boolean useSharkSchema) {
            this.useSharkSchema = useSharkSchema;
            return this;
        }

        public DatabaseCreatorConfigBuilder withAdminUser(String username, String password) {
            this.adminUser = username;
            this.adminPassword = password;
            return this;
        }

        public DatabaseCreatorConfigBuilder withLimitedUser(String username, String password) {
            this.limitedUser = username;
            this.limitedPassword = password;
            return this;
        }

        public DatabaseCreatorConfigBuilder withDatabaseUrl(String cmdbuildDatabaseUrl) {
            if (isNotBlank(cmdbuildDatabaseUrl)) {
                Matcher matcher = Pattern.compile(JDBC_URL_PATTERN).matcher(cmdbuildDatabaseUrl);
                checkArgument(matcher.find(), "database url syntax mismatch for url = '%s' with regexp = %s", cmdbuildDatabaseUrl, JDBC_URL_PATTERN);
                return this.withDatabaseUrl(matcher.group(1), Integer.valueOf(matcher.group(2)), emptyToNull(matcher.group(3)));
            } else {
                return this;
            }
        }

        public DatabaseCreatorConfigBuilder withDatabaseName(String databaseName) {
            dbName = emptyToNull(databaseName);
            return this;
        }

        public DatabaseCreatorConfigBuilder withCreateDatabase(Boolean createDatabase) {
            this.createDatabase = createDatabase;
            return this;
        }

        public DatabaseCreatorConfigBuilder withKeepLocalConfig(Boolean keepLocalConfig) {
            this.keepLocalConfig = keepLocalConfig;
            return this;
        }

        public DatabaseCreatorConfigBuilder withConfigImportStrategy(ConfigImportStrategy configImportStrategy) {
            this.configImportStrategy = configImportStrategy;
            return this;
        }

        public DatabaseCreatorConfigBuilder withDatabaseUrl(String host, int port, String databaseName) {
            this.dbHost = host;
            this.dbPort = port;
            return this.withDatabaseName(databaseName);
        }

        public DatabaseCreatorConfigBuilder withSource(String databaseType) {
            this.cmdbuildDatabaseType = databaseType.trim();
            return this;
        }

        public DatabaseCreatorConfigBuilder withSqlPath(String sqlPath) {
            this.sqlPath = sqlPath;
            return this;
        }

        public DatabaseCreatorConfigBuilder withConfig(InputStream inputStream) {
            return this.withConfig(loadProperties(inputStream));
        }

        public DatabaseCreatorConfigBuilder withConfig(File configFile) {
            return this.withConfig(loadProperties(configFile));
        }

        public DatabaseCreatorConfigBuilder accept(Consumer<DatabaseCreatorConfigBuilder> visitor) {
            visitor.accept(this);
            return this;
        }

        @Override
        public DatabaseCreatorConfig build() {
            return new DatabaseCreatorConfigImpl(this);
        }

    }

}
