package org.cmdbuild.test.rest;

import static com.jayway.restassured.RestAssured.given;
import static org.cmdbuild.test.rest.TestContextProviders.TC_EMPTY;

import java.util.List;
import java.util.Random;

import org.cmdbuild.test.framework.CmTestRunner;
import org.cmdbuild.test.framework.Context;
import org.cmdbuild.test.rest.utils.AbstractWsIT;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CmTestRunner.class)
@Context(TC_EMPTY)
public class AuditRestIT extends AbstractWsIT {

	@Test
	public void getMark() {
		String mark = given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("system/audit/mark"))
				.then().extract().jsonPath().getString("data.mark");

		logger.info(mark);
	}

	@Test
	public void getRequests() {
		given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("system/audit/requests")).then()
				.statusCode(200);
	}

	@Test
	public void getErrors() {
		given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("system/audit/errors")).then()
				.statusCode(200);
	}

	@Test
	public void getRequestById() {
		List<String> allRequestsIds = given().header(CMDBUILD_AUTH_HEADER, getSessionToken())
				.get(buildRestV3Url("system/audit/requests")).then().extract().jsonPath().getList("data.requestId");

		String randomId = allRequestsIds.get(new Random().nextInt(allRequestsIds.size()));

		given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("system/audit/requests/" + randomId))
				.then().statusCode(200);
	}

}
