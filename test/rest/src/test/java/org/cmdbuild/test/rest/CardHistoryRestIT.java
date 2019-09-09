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
import static org.hamcrest.Matchers.allOf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.cmdbuild.test.framework.CmTestRunner;
import org.cmdbuild.test.framework.Context;
import org.cmdbuild.test.rest.utils.AbstractWsIT;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.cmdbuild.test.rest.TestContextProviders.TC_R2U;

@RunWith(CmTestRunner.class)
@Context(TC_R2U)
public class CardHistoryRestIT extends AbstractWsIT {

	@Test
	public void getCardHistory() {
		String classId = "Employee";

		List<Object> allCardsIds = given().header(CMDBUILD_AUTH_HEADER, getSessionToken())
				.get(buildRestV3Url("classes/" + classId + "/cards")).then().statusCode(200).extract().jsonPath()
				.getList("data._id");

		Object random = allCardsIds.get(new Random().nextInt(allCardsIds.size()));

		given().header(CMDBUILD_AUTH_HEADER, getSessionToken())
				.get(buildRestV3Url("classes/" + classId + "/cards/" + random + "/history")).then().statusCode(200);
	}

	@Test
	public void getCardHistoryByRecordId() {
		String classId = "Employee";

		List<Object> allCardsIds = given().header(CMDBUILD_AUTH_HEADER, getSessionToken())
				.get(buildRestV3Url("classes/" + classId + "/cards")).then().statusCode(200).extract().jsonPath()
				.getList("data._id");

		Object random = allCardsIds.get(new Random().nextInt(allCardsIds.size()));

		given().header(CMDBUILD_AUTH_HEADER, getSessionToken())
				.get(buildRestV3Url("classes/" + classId + "/cards/" + random + "/history")).then().statusCode(200);

		List<Object> allRecordIds = given().header(CMDBUILD_AUTH_HEADER, getSessionToken())
				.get(buildRestV3Url("classes/" + classId + "/cards/" + random + "/history")).then().statusCode(200)
				.extract().jsonPath().getList("data._id");

		Object random2 = allRecordIds.get(new Random().nextInt(allRecordIds.size()));

		given().header(CMDBUILD_AUTH_HEADER, getSessionToken())
				.get(buildRestV3Url("classes/" + classId + "/cards/" + random + "/history/" + random2)).then()
				.statusCode(200);
	}
}