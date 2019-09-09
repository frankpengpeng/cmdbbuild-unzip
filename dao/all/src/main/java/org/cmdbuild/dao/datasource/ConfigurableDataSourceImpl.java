package org.cmdbuild.dao.datasource;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.EventBus;
import com.p6spy.engine.common.P6Util;
import com.p6spy.engine.common.StatementInformation;
import com.p6spy.engine.event.CompoundJdbcEventListener;
import com.p6spy.engine.event.DefaultEventListener;
import com.p6spy.engine.event.JdbcEventListener;
import com.p6spy.engine.event.SimpleJdbcEventListener;
import com.p6spy.engine.spy.JdbcEventListenerFactory;
import com.p6spy.engine.spy.P6DataSource;
import static java.lang.String.format;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import javax.annotation.PreDestroy;

import javax.sql.DataSource;
import static org.apache.commons.lang3.StringUtils.isBlank;

import org.apache.commons.lang3.Validate;
import org.apache.commons.dbcp2.BasicDataSource;
import static org.cmdbuild.common.error.ErrorAndWarningCollectorService.marker;
import org.cmdbuild.common.java.sql.ForwardingDataSource;
import org.cmdbuild.config.api.ConfigListener;
import org.cmdbuild.dao.config.DatabaseConfiguration;
import org.cmdbuild.dao.config.SqlConfiguration;
import org.cmdbuild.dao.ConfigurableDataSource;
import static org.cmdbuild.spring.configuration.BeanNamesAndQualifiers.SYSTEM_LEVEL_ONE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.DaoException;
import org.cmdbuild.dao.DatasourceConfiguredEvent;
import static org.cmdbuild.spring.configuration.BeanNamesAndQualifiers.RAW_DATA_SOURCE;
import static org.cmdbuild.utils.postgres.PgVersionUtils.getPostgresServerVersionNum;
import static org.cmdbuild.utils.postgres.PgVersionUtils.getPostgresServerVersionFromNumber;
import org.cmdbuild.dao.PostgresDriverAutoconfigureHelperService;
import org.cmdbuild.services.PostStartup;
import static org.cmdbuild.services.SystemStatus.SYST_LOADING_CONFIG;
import static org.cmdbuild.services.SystemStatus.SYST_LOADING_CONFIG_FILES;
import static org.cmdbuild.services.SystemStatus.SYST_NOT_RUNNING;
import static org.cmdbuild.services.SystemStatus.SYST_READY;
import static org.cmdbuild.utils.lang.CmReflectionUtils.existsOnClasspath;

/**
 * note: if basic datasource lookup is null, or if lookup fails, a new
 * {@link BasicDataSource} instance will be used
 *
 * return bare datasource; with multitenant this cannot be used directly for
 * cmdbuild tables, and must usually be wrapped in a tenant-aware proxy
 *
 * act as a wrapper and configurator around a regular basic data source, which
 * will handle actual connection and pooling
 */
@Component(RAW_DATA_SOURCE)
@Qualifier(SYSTEM_LEVEL_ONE)
public class ConfigurableDataSourceImpl extends ForwardingDataSource implements ConfigurableDataSource {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final EventBus eventBus = new EventBus();
    private final PostgresDriverAutoconfigureHelperService platformHelper;
    private final DatabaseConfiguration databaseConfiguration;
    private final SqlConfiguration sqlConfiguration;

    private BasicDataSource innnerDataSource;
    private P6DataSource loggerDataSource;
    private DataSource delegateDataSource;
    private boolean ready = false;

    public ConfigurableDataSourceImpl(PostgresDriverAutoconfigureHelperService platformHelper, DatabaseConfiguration configuration, SqlConfiguration sqlConfiguration) {
        this.databaseConfiguration = checkNotNull(configuration);
        this.sqlConfiguration = checkNotNull(sqlConfiguration);
        this.platformHelper = checkNotNull(platformHelper);

        if (databaseConfiguration.hasConfig()) {
            configureDatasource();
        }
    }

    @Override
    public String getDatabaseUrl() {
        return databaseConfiguration.getDatabaseUrl();
    }

    @Override
    public BasicDataSource getInner() {
        return checkNotNull(innnerDataSource, "inner data source is null (not configured or already closed)");
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public boolean isReady() {
        return ready;
    }

    @Override
    public void closeInner() {
        cleanupSafe();
    }

    @Override
    public void reloadInner() {
        cleanupSafe();
        doConfigureDatasource();
    }

    @Override
    public String getDatabaseUser() {
        return databaseConfiguration.getDatabaseUser();
    }

    @Override
    public boolean hasAdminDataSource() {
        return isReady() && databaseConfiguration.hasAdminAccount();
    }

    @Override
    public void withAdminDataSource(Consumer<DataSource> consumer) {
        BasicDataSource adminDataSource = getAdminDataSource();
        try {
            consumer.accept(adminDataSource);
        } finally {
            try {
                adminDataSource.close();
            } catch (SQLException ex) {
                logger.error("error closing admin data source", ex);
            }
        }
    }

    private BasicDataSource getAdminDataSource() {
        checkArgument(hasAdminDataSource(), "unable to get admin data source: admin account not configured");
        BasicDataSource adminDataSource = new BasicDataSource();
        configureDatasourceCommons(adminDataSource);
        adminDataSource.setUsername(databaseConfiguration.getDatabaseAdminUsername());
        adminDataSource.setPassword(databaseConfiguration.getDatabaseAdminPassword());
        return adminDataSource;
    }

    @PreDestroy
    public void cleanupSafe() {
        try {
            if (innnerDataSource != null && !innnerDataSource.isClosed()) {
                logger.info("close inner data source");
                innnerDataSource.close();
            }
        } catch (Exception ex) {
            logger.error("error closing inner data source", ex);
        }
        innnerDataSource = null;
        delegateDataSource = null;
        loggerDataSource = null;
        ready = false;
    }

    @ConfigListener(value = DatabaseConfiguration.class, requireSystemStatus = {SYST_NOT_RUNNING, SYST_LOADING_CONFIG_FILES})
    public final void configureDatasource() {
        cleanupSafe();
        if (databaseConfiguration.hasConfig()) {
            checkPostgresDriver();
            doConfigureDatasource();
        } else {
            logger.warn("cannot configure data source: missing database configuration!");
            ready = false;
        }
    }

    @ConfigListener(SqlConfiguration.class)
    @PostStartup
    public void checkAndConfigureSqlLogger() {
        if (isReady()) {
            configureSqlLogger();
        }
    }

    private void doConfigureDatasource() {
        ready = false;
        logger.info("configure datasource with url = {} with user = {}", databaseConfiguration.getDatabaseUrl(), databaseConfiguration.getDatabaseUser());
        innnerDataSource = new BasicDataSource();
        delegateDataSource = innnerDataSource;
        configureDatasourceCommons(innnerDataSource);
        innnerDataSource.setUsername(databaseConfiguration.getDatabaseUser());
        innnerDataSource.setPassword(databaseConfiguration.getDatabasePassword());
        if (databaseConfiguration.enableDatabaseConnectionEagerCheck()) {
            checkDatabaseConnectionAndStuff();
        }
        ready = true;
        eventBus.post(DatasourceConfiguredEvent.INSTANCE);
        configureSqlLogger();
    }

    private void configureDatasourceCommons(BasicDataSource dataSource) {
        dataSource.addConnectionProperty("autosave", "conservative");
        dataSource.setDriverClassName(databaseConfiguration.getDriverClassName());
        dataSource.setUrl(databaseConfiguration.getDatabaseUrl());
    }

    private void configureSqlLogger() {
        logger.debug("configureSqlLogger");
        checkArgument(isReady());
        if (sqlConfiguration.enableSqlOrDdlLogging() && !datasourceLoggingEnabled()) {
            wrapDatasourceWithLogger();
        } else if (!sqlConfiguration.enableSqlOrDdlLogging() && datasourceLoggingEnabled()) {
            unwrapDatasourceLogger();
        }

    }

    @Override
    public Connection getConnection() throws SQLException {
        checkDatasource();
        return super.getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        checkDatasource();
        return super.getConnection(username, password);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        Validate.notNull(iface, "Interface argument must not be null");
        if (!DataSource.class.equals(iface)) {
            final String message = format("data source of type '%s' can only be unwrapped as '%s', not as '%s'", //
                    getClass().getName(), //
                    DataSource.class.getName(), //
                    iface.getName());
            throw new SQLException(message);
        }
        return (T) this;
    }

    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return DataSource.class.equals(iface);
    }

    @Override
    protected DataSource delegate() {
        return checkNotNull(delegateDataSource, "delegate data source is null (not configured or already closed)");
    }

    private boolean datasourceLoggingEnabled() {
        return loggerDataSource != null;
    }

    private void wrapDatasourceWithLogger() {
        logger.info("enable sql logger");
        checkArgument(!datasourceLoggingEnabled());
        loggerDataSource = new P6DataSource(getInner());
        loggerDataSource.setJdbcEventListenerFactory(new MyJdbcEventListenerFactory());
        delegateDataSource = loggerDataSource;
    }

    private void unwrapDatasourceLogger() {
        logger.info("disable datasource logger");
        checkArgument(datasourceLoggingEnabled());
        delegateDataSource = innnerDataSource;
        loggerDataSource = null;
    }

    private void checkDatabaseConnectionAndStuff() {
        try (Connection connection = innnerDataSource.getConnection()) {
            int versionNum = getPostgresServerVersionNum(connection);
            String versionString = getPostgresServerVersionFromNumber(versionNum);
            logger.info("postgres server version = {}", versionString);
            if (versionNum < databaseConfiguration.getMinSupportedPgVersion() || versionNum > databaseConfiguration.getMaxSupportedPgVersion()) {
                logger.warn(marker(), "using unsupported postgres version = {} (recommended version is 9.5.x to 10.6.x)", versionNum);
            }
        } catch (Exception ex) {
            logger.error(marker(), "error checking database connection", ex);
        }
    }

    private void checkDatasource() {
        checkArgument(ready, "the datasource is not configured!");
    }

    private void checkPostgresDriver() {
        if (!postgresDriverExistsOnClasspath()) {
            logger.warn("postgres driver not found on classpath, trying to auto configure");
            platformHelper.autoconfigurePostgresDriver();
        }
        checkArgument(postgresDriverExistsOnClasspath(), "failed to auto configure postgres driver; postgres driver not available (configured driver class = %s)", databaseConfiguration.getDriverClassName());
        checkPostgresDriverVersion();
    }

    private boolean postgresDriverExistsOnClasspath() {
        return existsOnClasspath(databaseConfiguration.getDriverClassName());
    }

    private void checkPostgresDriverVersion() {
        try {
            Class pgdriver = Class.forName(databaseConfiguration.getDriverClassName());
            logger.info("postgres driver = {}", pgdriver.getName());
            String pgDriverVersion = (String) pgdriver.getMethod("getVersion").invoke(null);
            logger.info("postgres driver version = {}", pgDriverVersion);
            String normalizedPgDriverVersion = pgDriverVersion.replaceAll("[^0-9.]*", "");
            if (!normalizedPgDriverVersion.matches("42[.]2[.]5")) {
                logger.warn("unsupported postgres jdbc driver: recommended postgres driver version is 42.2.5");
            }
        } catch (Exception ex) {
            throw new DaoException(ex, "error checking postgres driver version");
        }
    }

    private class MyJdbcEventListenerFactory extends SimpleJdbcEventListener implements JdbcEventListenerFactory {

        private final Logger sqlLogger = LoggerFactory.getLogger("org.cmdbuild.sql");
        private final Logger ddlLogger = LoggerFactory.getLogger("org.cmdbuild.sql_ddl");

        private final JdbcEventListener listener;

        public MyJdbcEventListenerFactory() {
            CompoundJdbcEventListener compoundEventListener = new CompoundJdbcEventListener();
            compoundEventListener.addListender(DefaultEventListener.INSTANCE);
            compoundEventListener.addListender(this);
            listener = compoundEventListener;
        }

        @Override
        public JdbcEventListener createJdbcEventListener() {
            return listener;
        }

        @Override
        public void onAfterAnyExecute(StatementInformation statementInformation, long timeElapsedNanos, SQLException e) {
            String sql = statementInformation.getSql();
            boolean logToSql = logToSql(sql);
            boolean logToDdl = logToDdl(sql);
            if (e != null || logToSql || logToDdl) {
                sql = normalizeSql(statementInformation.getSqlWithValues());
                if (e == null) {
                    if (logToSql) {
                        sqlLogger.info(sql);
                    } else {
                        sqlLogger.trace(sql);
                    }
                    if (logToDdl) {
                        ddlLogger.info(sql);
                    }
                } else {
                    sqlLogger.error("sql exception = {} on query = {}", e.toString(), sql);
                }
                if (sqlConfiguration.enableSqlLoggingTimeTracking()) {
                    String message = format(" -- elapsed time = %sms", timeElapsedNanos / 1000000);
                    if (logToSql) {
                        sqlLogger.info(message);
                    } else {
                        sqlLogger.trace(message);
                    }
                }
            }
        }

        private String normalizeSql(String sql) {
            return format("%s;", P6Util.singleLine(sql));
        }

        private boolean logToSql(String statement) {
            return sqlConfiguration.enableSqlLogging() && (isBlank(sqlConfiguration.excludeSqlRegex()) || !Pattern.compile(sqlConfiguration.excludeSqlRegex(), Pattern.DOTALL).matcher(statement).find());
        }

        private boolean logToDdl(String statement) {
            return sqlConfiguration.enableDdlLogging() && (isBlank(sqlConfiguration.includeDdlRegex()) || Pattern.compile(sqlConfiguration.includeDdlRegex(), Pattern.DOTALL).matcher(statement).find());
        }

    }

}
