package org.cmdbuild.config;

import org.springframework.stereotype.Component;
import org.cmdbuild.config.api.ConfigValue;
import org.cmdbuild.config.api.ConfigComponent;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.cmdbuild.dao.config.SqlConfiguration;
import static org.cmdbuild.config.SqlConfigurationImpl.SQL_CONFIGURATION;

@Component(SQL_CONFIGURATION)
@ConfigComponent("org.cmdbuild.sql")
public class SqlConfigurationImpl implements SqlConfiguration {

    public static final String SQL_CONFIGURATION = "sqlConfiguration",
            DEFAULT_EXCLUDE_REGEXP = "(SET SESSION|RESET) cmdbuild[.].*| quartz.qrtz_|INSERT INTO \"(_Request|_SystemStatusLog|_Temp|_Uploads)\"|SELECT COUNT.*FROM \"_Session\"",
            DEFAULT_DDL_INCLUDE_REGEXP = "_cm3_(attribute|class)_[^(]*(create|modify|set|delete)|(INSERT|UPDATE).*\"(LookUp|_Menu)\"", //TODO
            SQL_LOG_ENABLED_KEY = "log.enabled";

    @ConfigValue(key = SQL_LOG_ENABLED_KEY, description = "enable logging of all sql queries (on logback category org.cmdbuild.sql)", defaultValue = FALSE)
    private Boolean sqlLoggingEnabled;

    @ConfigValue(key = "log.exclude", description = "exclude from logs sql queryes matching this regex", defaultValue = DEFAULT_EXCLUDE_REGEXP)
    private String sqlLoggingExclude;

    @ConfigValue(key = "log.trackTimes", description = "track and log running time of all queries", defaultValue = FALSE)
    private Boolean sqlLoggingTrackTimes;

    @ConfigValue(key = "ddl_log.enabled", description = "enable logging of ddl queries (on logback category org.cmdbuild.sql_ddl)", defaultValue = FALSE)
    private Boolean ddlLoggingEnabled;

    @ConfigValue(key = "ddl_log.include", description = "include in ddl log only queryes matching this regex (default should be good for most applications", defaultValue = DEFAULT_DDL_INCLUDE_REGEXP)
    private String ddlLoggingInclude;

    @Override
    public boolean enableSqlLogging() {
        return sqlLoggingEnabled;
    }

    @Override
    public String excludeSqlRegex() {
        return sqlLoggingExclude;
    }

    @Override
    public boolean enableSqlLoggingTimeTracking() {
        return sqlLoggingTrackTimes;
    }

    @Override
    public boolean enableDdlLogging() {
        return ddlLoggingEnabled;
    }

    @Override
    public String includeDdlRegex() {
        return ddlLoggingInclude;
    }

}
