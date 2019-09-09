/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres;

import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;

public interface PostgresDatabaseAdapterService {

	final static String OPERATION_USER_USERNAME_POSTGRES_VARIABLE = "cmdbuild.operation_user",
			USER_TENANTS_POSTGRES_VARIABLE = "cmdbuild.user_tenants",
			IGNORE_TENANT_POLICIES_POSTGRES_VARIABLE = "cmdbuild.ignore_tenant_policies";

	DataSource getDataSource();

	JdbcTemplate getJdbcTemplate();
}
