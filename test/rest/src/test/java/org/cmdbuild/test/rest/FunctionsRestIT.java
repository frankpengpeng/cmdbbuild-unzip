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
public class FunctionsRestIT extends AbstractWsIT {

	@Test
	public void functionsTest() {
		given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("functions/")).then()
				.statusCode(200);
	}

	@Test
	public void testLimit() {
		given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).queryParam("limit", 1).get(buildRestV3Url("functions")).then()
				.body("data.size()", equalTo(1)).statusCode(200);
	}

	@Test
	public void testStart() {
		String token = getSessionToken();
		int startValue = 3;

		int totalValue = given().header(CMDBUILD_AUTH_HEADER, token).get(buildRestV3Url("functions")).then().extract()
				.jsonPath().getInt("meta.total");

		given().header(CMDBUILD_AUTH_HEADER, token).queryParam("start", startValue).get(buildRestV3Url("functions"))
				.then().body("data.size()", equalTo(totalValue - startValue)).statusCode(200);
	}

	@Test
	public void testDetailed() {
		String token = getSessionToken();

		given().header(CMDBUILD_AUTH_HEADER, token).queryParam("detailed", true).get(buildRestV3Url("functions/"))
				.then().body("data[1]", hasKey("metadata")).body("data[1]", hasKey("parameters")).statusCode(200);

		given().header(CMDBUILD_AUTH_HEADER, token).queryParam("detailed", false).get(buildRestV3Url("functions/"))
				.then().body("data[1]", not(hasKey("metadata"))).body("data[1]", not(hasKey("parameters")))
				.statusCode(200);
	}

	@Test
	public void getFunctionById() {
		String token = getSessionToken();

		Object firstId = given().header(CMDBUILD_AUTH_HEADER, token).get(buildRestV3Url("functions")).then().extract()
				.jsonPath().getList("data._id").get(0);

		given().header(CMDBUILD_AUTH_HEADER, token).get(buildRestV3Url("functions/" + firstId)).then()
				.statusCode(200);
	}

	@Test
	public void getFunctionParameters() {
		String token = getSessionToken();

	List<Object> idsList = given().header(CMDBUILD_AUTH_HEADER, token).get(buildRestV3Url("functions")).then().extract()
				.jsonPath().getList("data._id");
	
	Object element = idsList.get(idsList.size()-2);

		given().header(CMDBUILD_AUTH_HEADER, token).get(buildRestV3Url("functions/" + element + "/parameters/")).then()
				.statusCode(200);
	}

	@Test
	public void getFunctionAttributes() {
		String token = getSessionToken();

		Object firstId = given().header(CMDBUILD_AUTH_HEADER, token).get(buildRestV3Url("functions")).then().extract()
				.jsonPath().getList("data._id").get(0);

		given().header(CMDBUILD_AUTH_HEADER, token).get(buildRestV3Url("functions/" + firstId + "/attributes/")).then()
				.statusCode(200);
	}

	@Test
	public void getFunctionOutputs() {
		String token = getSessionToken();

		Object firstId = given().header(CMDBUILD_AUTH_HEADER, token).get(buildRestV3Url("functions")).then().extract()
				.jsonPath().getList("data._id").get(6);

		given().header(CMDBUILD_AUTH_HEADER, token)
				.get(buildRestV3Url("functions/" + firstId + "/outputs/")).then()
				.statusCode(200);
	}
	
	@Test
	public void testFilter() {
		String token = getSessionToken();

		String attribute = "name";
		String operator = "equal";
		String[] codes = { "_graph_get_related_classes" };

		JSONObject json = createJson(attribute, operator, codes);

		logger.info("Query: {}", json);

		given().header(CMDBUILD_AUTH_HEADER, token).queryParam("filter", json).when()
				.get(buildRestV3Url("functions/")).then().body("data.name", everyItem(equalTo("_graph_get_related_classes")))
				.statusCode(200);
	}
	

	public JSONObject createJson(String attribute, String operator, Object value) {
		JSONObject json = new JSONObject();
		json.put("attribute", attribute);
		json.put("operator", operator);
		json.put("value", value);

		JSONObject json2 = new JSONObject();
		json2.put("simple", json);

		JSONObject json3 = new JSONObject();
		json3.put("attribute", json2);
		return json3;
	}
}
