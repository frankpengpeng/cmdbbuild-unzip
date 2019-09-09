package org.cmdbuild.test.core;

import com.google.common.base.Joiner;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Arrays.asList;
import org.cmdbuild.auth.user.OperationUserStore;
import org.cmdbuild.auth.multitenant.UserTenantContextImpl;
import org.cmdbuild.auth.multitenant.config.MultitenantConfiguration;
import static org.cmdbuild.auth.user.LoginUserImpl.ANONYMOUS_LOGIN_USER;
import org.cmdbuild.auth.user.OperationUser;
import org.cmdbuild.auth.user.OperationUserImpl;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.driver.DatabaseAccessConfig;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import org.springframework.jdbc.core.JdbcTemplate;
import org.cmdbuild.spring.utils.ApplicationContextHelper;
//import static org.cmdbuild.test.dao.utils.DaoTestContextConfiguration.MULTITENANT_CONFIGURATION;
//import org.cmdbuild.test.dao.utils.DaoTestContextInitializer;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.dao.driver.PostgresService;
import org.cmdbuild.test.framework.CmTestRunner;
import org.junit.runner.RunWith;

//@RunWith(CmTestRunner.class)
public class SqlConnectionIT {

//	protected final Logger logger = LoggerFactory.getLogger(getClass());
//
//	private final MultitenantConfiguration multitenantConfiguration;
//	private final DatabaseAccessConfig databaseAccessConfig;
// 
////	private ApplicationContextHelper applicationContext;
//
////	@Before
////	public void init() throws Exception {
////		springContextInitializer = new DaoTestContextInitializer(asList(DaoTestContextConfiguration.class, CustomConfig.class));
////		springContextInitializer.init();
////	}
//
//	@Test
//	public void testSqlConnection() throws Exception { TODO
//		logger.info("testConnection BEGIN");
//
//		assertTrue(multitenantConfiguration.isMultitenantEnabled());
//		assertTrue(databaseAccessConfig.getMultitenantConfiguration().isMultitenantEnabled());
//
//		UserTenantContextImpl userTenantContext = new UserTenantContextImpl(false, asList(10l, 20l, 30l));
//		OperationUser operationUser = OperationUserImpl.builder().withAuthenticatedUser(ANONYMOUS_LOGIN_USER).withUserTenantContext(userTenantContext).build();
//		applicationContext.getBean(OperationUserStore.class).setUser(operationUser);
//
//		logger.info("set operation user with tenants ids = {}", applicationContext.getBean(DatabaseAccessConfig.class).getUserContext().getTenantIds());
//
//		assertFalse(applicationContext.getBean(DatabaseAccessConfig.class).getUserContext().ignoreTenantPolicies());
//		assertFalse(applicationContext.getBean(DatabaseAccessConfig.class).getUserContext().getTenantIds().isEmpty());
//
//		PostgresService dbDriver = applicationContext.getBean(PostgresService.class);
//		JdbcTemplate jdbcTemplate = dbDriver.getJdbcTemplate();
//		logger.info("JdbcTemplate query BEGIN");
//		String res = jdbcTemplate.queryForObject("SHOW cmdbuild.user_tenants", String.class);
//		logger.info("res = {}", res);
//		assertFalse(userTenantContext.getActiveTenantIds().isEmpty());
//		assertEquals("{" + Joiner.on(",").join(userTenantContext.getActiveTenantIds()) + "}", res);
//		logger.info("JdbcTemplate query END");
//
//		logger.info("testConnection END");
//	}
//
////	@Configuration
////	public static class CustomConfig {
////
////		@Bean(MULTITENANT_CONFIGURATION)
////		public MultitenantConfiguration getMultitenantConfiguration() {
////			return new SimpleMultitenantConfiguration(MultitenantConfiguration.MultitenantMode.DB_FUNCTION);
////		}
////
////	}
//
//	@After
//	public void cleanup() {
//		applicationContext.cleanup();
//	}

}
