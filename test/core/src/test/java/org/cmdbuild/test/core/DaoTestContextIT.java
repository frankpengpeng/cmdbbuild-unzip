/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.core;

import org.cmdbuild.services.SystemService;
import org.cmdbuild.test.framework.TestContext;
import org.junit.After;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DaoTestContextIT {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private TestContext testContext;

	@Test
	@Ignore("heavy test, use only to test context loading")
	public void testSpringContext() throws Exception {
		testContext = TestContextProviders.getEmptyDb();
		assertTrue(testContext.getBean(SystemService.class).isSystemReady());
	}

	@After
	public void cleanup() {
		if (testContext != null) {
			try {
				testContext.cleanup();
			} catch (Exception ex) {
				logger.error("error clearing test context", ex);
			}
		}
	}
}
