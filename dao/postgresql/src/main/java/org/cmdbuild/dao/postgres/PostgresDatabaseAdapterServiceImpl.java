/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres;

import com.google.common.base.Joiner;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableMap;
import static java.lang.String.format;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;
import org.cmdbuild.dao.driver.DatabaseAccessConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class PostgresDatabaseAdapterServiceImpl implements PostgresDatabaseAdapterService {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private final DatabaseAccessConfig databaseAccessConfig;
	private final DataSource dataSource;
	private final JdbcTemplate jdbcTemplate;

	public PostgresDatabaseAdapterServiceImpl(DataSource innerDataSource, DatabaseAccessConfig databaseAccessConfig) {
		this.databaseAccessConfig = checkNotNull(databaseAccessConfig);
		this.dataSource = new TenantAwareDataSource(checkNotNull(innerDataSource));
		this.jdbcTemplate = new MyJdbcTemplate(this.dataSource);
	}

	@Override
	public DataSource getDataSource() {
		return dataSource;
	}

	@Override
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	private class TenantAwareDataSource extends ConnectionWrapperDataSource {

		public TenantAwareDataSource(DataSource dataSource) {
			super(dataSource);
		}

		@Override
		protected void prepareConnection(Connection connection) throws SQLException {
			boolean ignoreTenantPolicies;
			Set<Long> tenantIds;
			if (databaseAccessConfig.getMultitenantConfiguration().isMultitenantDisabled()) {
				ignoreTenantPolicies = true;
				tenantIds = emptySet();
			} else {
				DatabaseAccessConfig.DatabaseAccessUserContext userContext = databaseAccessConfig.getUserContext();
				ignoreTenantPolicies = userContext.ignoreTenantPolicies();
				tenantIds = ignoreTenantPolicies ? emptySet() : userContext.getTenantIds();
			}
			String username = databaseAccessConfig.getUserContext().getUsername();
			logger.trace("ignoreTenantPolicies = {} tenantIds = {}", ignoreTenantPolicies, tenantIds);
			for (Map.Entry<String, String> entry : ImmutableMap.of(
					OPERATION_USER_USERNAME_POSTGRES_VARIABLE, username,
					IGNORE_TENANT_POLICIES_POSTGRES_VARIABLE, Boolean.toString(ignoreTenantPolicies),
					USER_TENANTS_POSTGRES_VARIABLE, "{" + Joiner.on(",").join(tenantIds) + "}").entrySet()) {
				logger.trace("set {} session variable to value = {}", entry.getKey(), entry.getValue());
				try (Statement statement = connection.createStatement()) {
					statement.execute(format("SET SESSION %s = '%s'", entry.getKey(), entry.getValue()));//TODO escape value
				}
			}
		}

		@Override
		protected void releaseConnection(Connection connection) throws SQLException {
			for (String key : asList(OPERATION_USER_USERNAME_POSTGRES_VARIABLE, IGNORE_TENANT_POLICIES_POSTGRES_VARIABLE, USER_TENANTS_POSTGRES_VARIABLE)) {
				logger.trace("reset {} session variable", key);
				try (Statement statement = connection.createStatement()) {
					statement.execute(format("RESET %s", key));
				}
			}
		}

	}
}
