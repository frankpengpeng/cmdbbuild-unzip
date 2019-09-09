package org.cmdbuild.test.rest;

import static com.jayway.restassured.RestAssured.given;

import org.cmdbuild.client.rest.RestClient;
import org.cmdbuild.test.framework.CmTestRunner;
import org.cmdbuild.test.framework.Context;
import org.cmdbuild.test.rest.utils.AbstractWsIT;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.cmdbuild.test.rest.TestContextProviders.TC_R2U;

@RunWith(CmTestRunner.class)
@Context(TC_R2U)
public class ProcessConfigurationRestIT extends AbstractWsIT {

	private RestClient restClient;

	@Before
	public void init() {
		restClient = getRestClient();
		restClient.system().setConfig("org.cmdbuild.workflow.enabled", "true");
		restClient.system().setConfig("org.cmdbuild.workflow.providers", "river");
		restClient.workflow().uploadPlanVersion("AssetMgt", ProcessRestIT.class.getResourceAsStream("/org/cmdbuild/test/workflow/assetmgt.xpdl"));
	}

	@Test
	public void getProcessesConfigurationsStatuses() {
		given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("configuration/processes/statuses"))
				.then().statusCode(200);
		
	}
}
