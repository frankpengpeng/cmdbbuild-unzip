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

import org.cmdbuild.test.framework.CmTestRunner;
import org.cmdbuild.test.framework.Context;
import org.cmdbuild.test.rest.utils.AbstractWsIT;
import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.cmdbuild.test.rest.TestContextProviders.TC_R2U;

@RunWith(CmTestRunner.class)
@Context(TC_R2U)
public class EmailAccountRestIT extends AbstractWsIT {

	@Test
	public void getEmailAccounts() {
		String token = getSessionToken();

		given().header(CMDBUILD_AUTH_HEADER, token).get(buildRestV3Url("email/accounts")).then().statusCode(200);
	}

	@Test
	public void testPostDeleteEmailAccount() {
		String token = getSessionToken();
		Map<String, Object> jsonAsMap = createAccount();

		Object accountId = given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(jsonAsMap)
				.when().post(buildRestV3Url("email/accounts")).then().statusCode(200).extract().path("data._id");

		given().header(CMDBUILD_AUTH_HEADER, token).when().get(buildRestV3Url("email/accounts/" + accountId)).then()
				.body("data.name", equalTo("Mario")).body("data.username", equalTo("testMario"))
				.body("data.password", equalTo("mario23")).statusCode(200);

		given().header(CMDBUILD_AUTH_HEADER, token).when().delete(buildRestV3Url("email/accounts/" + accountId)).then()
				.statusCode(200);
	}

	@Test
	public void testPostPutDeleteEmailAccount() {
		String token = getSessionToken();
		Map<String, Object> jsonAsMap = createAccount();

		Object accountId = given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(jsonAsMap)
				.when().post(buildRestV3Url("email/accounts")).then().statusCode(200).extract().path("data._id");

		given().header(CMDBUILD_AUTH_HEADER, token).when().get(buildRestV3Url("email/accounts/" + accountId)).then()
				.body("data.name", equalTo("Mario")).body("data.username", equalTo("testMario"))
				.body("data.password", equalTo("mario23")).statusCode(200);

		jsonAsMap.put("name", "Roberto");
		jsonAsMap.put("username", "testRoberto");
		jsonAsMap.put("password", "roberto23");

		given().header(CMDBUILD_AUTH_HEADER, token).when().contentType("application/json").body(jsonAsMap)
				.put(buildRestV3Url("email/accounts/" + accountId)).then().statusCode(200);

		given().header(CMDBUILD_AUTH_HEADER, token).when().get(buildRestV3Url("email/accounts/" + accountId)).then()
				.body("data.name", equalTo("Roberto")).body("data.username", equalTo("testRoberto"))
				.body("data.password", equalTo("roberto23")).statusCode(200);

		given().header(CMDBUILD_AUTH_HEADER, token).when().delete(buildRestV3Url("email/accounts/" + accountId)).then()
				.statusCode(200);

	}

	public Map<String, Object> createAccount() {
		Map<String, Object> jsonAsMap = new HashMap<>();

		jsonAsMap.put("name", "Mario");
		jsonAsMap.put("username", "testMario");
		jsonAsMap.put("password", "mario23");

		return jsonAsMap;
	}
}
