package org.cmdbuild.test.rest;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.hasItem;

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
import static org.cmdbuild.test.rest.TestContextProviders.TC_R2U;

@RunWith(CmTestRunner.class)
@Context(TC_R2U)
public class RoleRestIT extends AbstractWsIT {

	@Test
	public void rolesTest() {
		given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("roles/")).then().statusCode(200);
	}

	@Test
	public void getUsersByRoleId() {

		List<Object> allCardsIds = given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("roles/"))
				.then().statusCode(200).extract().jsonPath().getList("data._id");

		Object random = allCardsIds.get(new Random().nextInt(allCardsIds.size()));

		given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("roles/" + random + "/users")).then()
				.statusCode(200);
	}

	@Test
	public void getRoleById() {
		String token = getSessionToken();

		long firstId = given().header(CMDBUILD_AUTH_HEADER, token).get(buildRestV3Url("roles")).then().extract()
				.jsonPath().getLong("data[0]._id");

		given().header(CMDBUILD_AUTH_HEADER, token).get(buildRestV3Url("roles/" + firstId)).then()
				.statusCode(200);
	}

	@Test
	public void testLimit() {
		String token = getSessionToken();
		int limit = 2;

		given().header(CMDBUILD_AUTH_HEADER, token).queryParam("limit", limit).when().get(buildRestV3Url("roles"))
				.then().body("data.size()", equalTo(limit)).statusCode(200);
	}

	@Test
	public void testStart() {
		String token = getSessionToken();
		int start = 2;

		int total = given().header(CMDBUILD_AUTH_HEADER, token).get(buildRestV3Url("roles")).then().extract().jsonPath()
				.getInt("data.size()");

		given().header(CMDBUILD_AUTH_HEADER, token).queryParam("start", start).when().get(buildRestV3Url("roles"))
				.then().body("data.size()", equalTo(total - start)).statusCode(200);
	}

	@Test
	public void testPostPut() {
		String token = getSessionToken();
		Map<String, Object> jsonAsMap = new HashMap<>();
		Object id = createRole(token, jsonAsMap);

		given().header(CMDBUILD_AUTH_HEADER, token).when().get(buildRestV3Url("roles/" + id)).then()
				.body("data.name", equalTo(jsonAsMap.get("name")))
				.body("data.description", equalTo(jsonAsMap.get("description"))).statusCode(200);

		jsonAsMap.put("description", "Specialist modificato");

		logger.info("" + id);

		given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(jsonAsMap).when()
				.put(buildRestV3Url("roles/" + id)).then().statusCode(200);

		given().header(CMDBUILD_AUTH_HEADER, token).when().get(buildRestV3Url("roles/" + id)).then()
				.body("data.description", equalTo("Specialist modificato")).statusCode(200);
	}

	public Object createRole(String token, Map<String, Object> jsonAsMap) {
		jsonAsMap.put("name", "Specialist" + randomizedString());
		jsonAsMap.put("type", "default");
		jsonAsMap.put("description", "Specialist " + randomizedString());
		jsonAsMap.put("active", true);

		Object userId = given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(jsonAsMap).when()
				.post(buildRestV3Url("roles")).then().statusCode(200).extract().path("data._id");

		return userId;
	}

	public String randomizedString() {
		String str = RandomStringUtils.randomNumeric(3);
		return str;
	}

}
