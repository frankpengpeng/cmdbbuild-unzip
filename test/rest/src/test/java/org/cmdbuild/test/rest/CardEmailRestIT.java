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
public class CardEmailRestIT extends AbstractWsIT {

	@Test
	public void getCardEmails() {
		String classId = "Employee";
		String token = getSessionToken();

		List<Object> allCardsIds = given().header(CMDBUILD_AUTH_HEADER, token)
				.get(buildRestV3Url("classes/" + classId + "/cards")).then().statusCode(200).extract().jsonPath()
				.getList("data._id");

		Object cardId = allCardsIds.get(new Random().nextInt(allCardsIds.size()));

		given().header(CMDBUILD_AUTH_HEADER, token)
				.get(buildRestV3Url("classes/" + classId + "/cards/" + cardId + "/emails")).then().statusCode(200);
	}

	@Test
	public void testPostDeleteCardEmail() {
		String token = getSessionToken();
		String classId = "Employee";
		Map<String, Object> jsonAsMap = createEmail();

		List<Object> allCardsIds = given().header(CMDBUILD_AUTH_HEADER, token)
				.get(buildRestV3Url("classes/" + classId + "/cards")).then().statusCode(200).extract().jsonPath()
				.getList("data._id");

		Object cardId = allCardsIds.get(new Random().nextInt(allCardsIds.size()));

		Object emailId = given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(jsonAsMap)
				.when().post(buildRestV3Url("classes/" + classId + "/cards/" + cardId + "/emails")).then()
				.statusCode(200).extract().path("data._id");

		given().header(CMDBUILD_AUTH_HEADER, token).when()
				.get(buildRestV3Url("classes/" + classId + "/cards/" + cardId + "/emails/" + emailId)).then()
				.body("data.from", equalTo("white.walter@example.com"))
				.body("data.to", equalTo("mario.rossi@example.com")).body("data.subject", equalTo("ciao"))
				.statusCode(200);

		given().header(CMDBUILD_AUTH_HEADER, token).when()
				.delete(buildRestV3Url("classes/" + classId + "/cards/" + cardId + "/emails/" + emailId)).then()
				.statusCode(200);
	}

	@Test
	public void testPostPutDeleteCardEmail() {
		String token = getSessionToken();
		String classId = "InternalEmployee";
		Map<String, Object> jsonAsMap = createEmail();

		List<Object> allCardsIds = given().header(CMDBUILD_AUTH_HEADER, token)
				.get(buildRestV3Url("classes/" + classId + "/cards")).then().statusCode(200).extract().jsonPath()
				.getList("data._id");

		Object cardId = allCardsIds.get(new Random().nextInt(allCardsIds.size()));

		Object emailId = given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(jsonAsMap)
				.when().post(buildRestV3Url("classes/" + classId + "/cards/" + cardId + "/emails")).then()
				.statusCode(200).extract().path("data._id");

		given().header(CMDBUILD_AUTH_HEADER, token).when()
				.get(buildRestV3Url("classes/" + classId + "/cards/" + cardId + "/emails/" + emailId)).then()
				.body("data.from", equalTo("white.walter@example.com"))
				.body("data.to", equalTo("mario.rossi@example.com")).body("data.subject", equalTo("ciao"))
				.statusCode(200);

		jsonAsMap.put("subject", "Hello");

		given().header(CMDBUILD_AUTH_HEADER, token).when().contentType("application/json").body(jsonAsMap)
				.put(buildRestV3Url("classes/" + classId + "/cards/" + cardId + "/emails/" + emailId)).then()
				.statusCode(200);

		given().header(CMDBUILD_AUTH_HEADER, token).when()
				.get(buildRestV3Url("classes/" + classId + "/cards/" + cardId + "/emails/" + emailId)).then()
				.body("data.from", equalTo("white.walter@example.com"))
				.body("data.subject", equalTo("Hello")).statusCode(200);

		given().header(CMDBUILD_AUTH_HEADER, token).when()
				.delete(buildRestV3Url("classes/" + classId + "/cards/" + cardId + "/emails/" + emailId)).then()
				.statusCode(200);

	}

	public Map<String, Object> createEmail() {
		Map<String, Object> jsonAsMap = new HashMap<>();

		jsonAsMap.put("from", "white.walter@example.com");
		jsonAsMap.put("to", "mario.rossi@example.com");
		jsonAsMap.put("subject", "ciao");
		jsonAsMap.put("body", "ciao come stai");
		jsonAsMap.put("status", "ES_DRAFT");

		return jsonAsMap;
	}

}
