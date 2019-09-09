package org.cmdbuild.test.rest;

import java.io.IOException;
import java.util.Map;
import org.cmdbuild.test.framework.CmTestRunner;
import org.cmdbuild.test.framework.Context;
import org.cmdbuild.test.rest.utils.AbstractWsIT;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.cmdbuild.test.rest.TestContextProviders.TC_R2U;

@RunWith(CmTestRunner.class)
@Context(TC_R2U)
public class UserConfigRestIT extends AbstractWsIT {

	@Test
	public void testGetUserConfig() throws IOException {
		Map<String, String> userConfig = getRestClient().session().getPreferences();
		logger.info("user config = {}", userConfig);
		assertNotNull(userConfig);
		assertTrue(!userConfig.isEmpty());
		//TODO test values; test startingClass
	}

}
