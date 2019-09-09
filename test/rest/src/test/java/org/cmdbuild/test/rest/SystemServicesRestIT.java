package org.cmdbuild.test.rest;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasKey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.cmdbuild.test.framework.CmTestRunner;
import org.cmdbuild.test.framework.Context;
import org.cmdbuild.test.rest.utils.AbstractWsIT;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.cmdbuild.test.rest.TestContextProviders.TC_R2U;

@RunWith(CmTestRunner.class)
@Context(TC_R2U)
public class SystemServicesRestIT extends AbstractWsIT {

	@Test
	public void getSystemServices() {
		given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("system_services/")).then()
				.body("data[1]", hasKey("_can_start")).body("data[1]", hasKey("_can_stop")).statusCode(200);
	}

	@Test
	public void getSystemServiceById() {
		String token = getSessionToken();

		List<String> allSystemServicesIds = given().header(CMDBUILD_AUTH_HEADER, token)
				.get(buildRestV3Url("system_services/")).then().statusCode(200).extract().jsonPath()
				.getList("data._id");

		String systemServiceId = allSystemServicesIds.get(new Random().nextInt(allSystemServicesIds.size()));

		logger.info(systemServiceId);

		given().header(CMDBUILD_AUTH_HEADER, token).get(buildRestV3Url("system_services/" + systemServiceId)).then()
				.body("data", hasKey("_can_start")).body("data", hasKey("_can_stop")).statusCode(200);
	}

	
	
	
}
