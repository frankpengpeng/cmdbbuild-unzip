package org.cmdbuild.test.rest;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.anyOf;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.cmdbuild.test.framework.CmTestRunner;
import org.cmdbuild.test.framework.Context;
import org.cmdbuild.test.rest.utils.AbstractWsIT;
import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jayway.restassured.http.ContentType;
import static org.cmdbuild.test.rest.TestContextProviders.TC_R2U;

@RunWith(CmTestRunner.class)
@Context(TC_R2U)
public class ImpersonationRestIT extends AbstractWsIT {

	@Test
	public void testImpersonateUser() {
		String token = getSessionToken();
		String userToImpersonate = "guest";


		given().header(CMDBUILD_AUTH_HEADER, token).contentType(ContentType.JSON)
				.post(buildRestV3Url("sessions/current/impersonate/"+userToImpersonate)).then()
				.statusCode(200);

		given().header(CMDBUILD_AUTH_HEADER, token).when()
				.delete(buildRestV3Url("sessions/current/impersonate/")).then().statusCode(200);
	}

}