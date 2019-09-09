package org.cmdbuild.test.rest;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.anyOf;

import java.util.HashMap;
import java.util.Map;

import org.cmdbuild.test.framework.CmTestRunner;
import org.cmdbuild.test.framework.Context;
import org.cmdbuild.test.rest.utils.AbstractWsIT;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.cmdbuild.test.rest.TestContextProviders.TC_R2U;

@RunWith(CmTestRunner.class)
@Context(TC_R2U)

public class ConfigurationRestIT extends AbstractWsIT {

	@Test
	public void getConfigurationPublic() {
		given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("configuration/public")).then().statusCode(200);
	}
	
	@Test
	public void getConfigurationSystem() {
		given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("configuration/system")).then().statusCode(200);
	}
}