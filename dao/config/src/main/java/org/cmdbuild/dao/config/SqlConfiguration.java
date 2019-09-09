package org.cmdbuild.dao.config;

public interface SqlConfiguration {

	boolean enableSqlLogging();

	String excludeSqlRegex();

	boolean enableSqlLoggingTimeTracking();

	boolean enableDdlLogging();

	String includeDdlRegex();

	default boolean enableSqlOrDdlLogging() {
		return enableSqlLogging() || enableDdlLogging();
	}
}
