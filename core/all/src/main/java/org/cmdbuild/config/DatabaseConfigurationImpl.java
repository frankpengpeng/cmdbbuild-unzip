package org.cmdbuild.config;

import org.cmdbuild.dao.config.DatabaseConfiguration;
import org.cmdbuild.config.api.ConfigComponent;
import static org.cmdbuild.config.api.ConfigLocation.CL_FILE_ONLY;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import static org.cmdbuild.config.DatabaseConfigurationImpl.DATABASE_CONFIG_NAMESPACE;
import org.springframework.stereotype.Component;

@Component("databaseConfiguration")
@ConfigComponent(DATABASE_CONFIG_NAMESPACE)
public final class DatabaseConfigurationImpl implements DatabaseConfiguration {

	@ConfigValue(key = DATABASE_CONFIG_URL, description = "database url", defaultValue = "", location = CL_FILE_ONLY, isProtected = true)
	private String url;

	@ConfigValue(key = DATABASE_CONFIG_USERNAME, description = "database username", defaultValue = "", location = CL_FILE_ONLY, isProtected = true)
	private String username;

	@ConfigValue(key = DATABASE_CONFIG_PASSWORD, description = "database password", defaultValue = "", location = CL_FILE_ONLY, isProtected = true)
	private String password;

	@ConfigValue(key = "db.driverClassName", description = "database driver", defaultValue = DEFAULT_DB_DRIVER_CLASS_NAME, location = CL_FILE_ONLY, isProtected = true)
	private String driverClassName;

	@ConfigValue(key = DATABASE_CONFIG_ADMIN_USERNAME, description = "database admin username", defaultValue = "", location = CL_FILE_ONLY, isProtected = true)
	private String adminUsername;

	@ConfigValue(key = DATABASE_CONFIG_ADMIN_PASSWORD, description = "database admin password", defaultValue = "", location = CL_FILE_ONLY, isProtected = true)
	private String adminPassword;

	@ConfigValue(key = "db.autopatch.enable", description = "enable db auto patch", defaultValue = FALSE, location = CL_FILE_ONLY, isProtected = true)
	private boolean enableAutoPatch;

	@ConfigValue(key = "checkConnectionAtStartup", description = "check db connection at startup", defaultValue = TRUE, location = CL_FILE_ONLY, isProtected = true)
	private boolean checkDbAtStartup;

	@Override
	public String getDatabaseUrl() {
		return url;
	}

	@Override
	public String getDatabaseUser() {
		return username;
	}

	@Override
	public String getDatabasePassword() {
		return password;
	}

	@Override
	public String getDatabaseAdminUsernameOrNull() {
		return adminUsername;
	}

	@Override
	public String getDatabaseAdminPasswordOrNull() {
		return adminPassword;
	}

	@Override
	public String getDriverClassName() {
		return driverClassName;
	}

	@Override
	public boolean enableAutoPatch() {
		return enableAutoPatch;
	}

	@Override
	public boolean enableDatabaseConnectionEagerCheck() {
		return checkDbAtStartup;
	}

}
