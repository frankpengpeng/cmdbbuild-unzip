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
public class CardLockRestIT extends AbstractWsIT {

	@Test
	public void getCardLocks() {
		String classId = "Building";
		String token = getSessionToken();

		List<Object> allCardsIds = given().header(CMDBUILD_AUTH_HEADER, token)
				.get(buildRestV3Url("classes/" + classId + "/cards")).then().statusCode(200).extract().jsonPath()
				.getList("data._id");

		Object cardId = allCardsIds.get(new Random().nextInt(allCardsIds.size()));

		given().header(CMDBUILD_AUTH_HEADER, token)
				.get(buildRestV3Url("classes/" + classId + "/cards/" + cardId + "/lock")).then()
				.body("success", equalTo(true)).statusCode(200);
	}

	@Test
	public void postDeleteCardLock() {
		String classId = "Building";
		String token = getSessionToken();

		List<Object> allCardsIds = given().header(CMDBUILD_AUTH_HEADER, token)
				.get(buildRestV3Url("classes/" + classId + "/cards")).then().statusCode(200).extract().jsonPath()
				.getList("data._id");

		Object cardId = allCardsIds.get(new Random().nextInt(allCardsIds.size()));

		given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json")
				.post(buildRestV3Url("classes/" + classId + "/cards/" + cardId + "/lock")).then().statusCode(200);

		given().header(CMDBUILD_AUTH_HEADER, token)
				.delete(buildRestV3Url("classes/" + classId + "/cards/" + cardId + "/lock")).then().statusCode(200);
	}
}
