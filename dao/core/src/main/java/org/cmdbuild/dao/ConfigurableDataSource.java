/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao;

import com.google.common.eventbus.EventBus;
import static java.lang.String.format;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import org.cmdbuild.utils.postgres.PgVersionUtils;
import static org.cmdbuild.utils.postgres.PgVersionUtils.getPostgresServerVersionFromNumber;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 */
public interface ConfigurableDataSource extends DataSource {

	/**
	 * events: {@link DatasourceConfiguredEvent}
	 */
	EventBus getEventBus();

	boolean isReady();

	BasicDataSource getInner();

	String getDatabaseUrl();

	void reloadInner();

	void closeInner();

	boolean hasAdminDataSource();

	void withAdminDataSource(Consumer<DataSource> consumer);

	String getDatabaseUser();

	default String getPostgresServerVersion() {
		try (Connection connection = getInner().getConnection()) {
			return getPostgresServerVersionFromNumber(PgVersionUtils.getPostgresServerVersionNum(connection));
		} catch (SQLException ex) {
			throw runtime(ex);
		}
	}

	default void withAdminJdbcTemplate(Consumer<JdbcTemplate> consumer) {
		withAdminDataSource((ds) -> consumer.accept(new JdbcTemplate(ds)));
	}

	default boolean isSuperuser() {
		return new JdbcTemplate(this).queryForObject("SELECT usesuper FROM pg_user WHERE usename = CURRENT_USER", Boolean.class);
	}

	default void setSuperuser() {
		withAdminJdbcTemplate(t -> t.execute(format("ALTER USER \"%s\" SUPERUSER", getDatabaseUser())));
	}

	default void setNoSuperuser() {
		withAdminJdbcTemplate(t -> t.execute(format("ALTER USER \"%s\" NOSUPERUSER", getDatabaseUser())));
	}

	default boolean isNotSuperuser() {
		return !isSuperuser();
	}

	default void doAsSuperuser(Runnable runnable) {
		boolean shouldClearSuperuser = false;
		if (isNotSuperuser()) {
			setSuperuser();
			shouldClearSuperuser = true;
		}
		try {
			runnable.run();
		} finally {
			if (shouldClearSuperuser) {
				setNoSuperuser();
			}
		}
	}

}
