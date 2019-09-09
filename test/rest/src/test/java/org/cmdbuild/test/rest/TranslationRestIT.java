package org.cmdbuild.test.rest;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.containsString;

import java.util.HashMap;
import java.util.Map;

import org.cmdbuild.test.framework.CmTestRunner;
import org.cmdbuild.test.framework.Context;
import org.cmdbuild.test.rest.utils.AbstractWsIT;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.cmdbuild.test.rest.TestContextProviders.TC_R2U;

@RunWith(CmTestRunner.class)
@Context(TC_R2U)
public class TranslationRestIT extends AbstractWsIT {

	@Test
	public void getTransalations() {
		given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("translations/")).then().assertThat()
				.statusCode(200);
	}

	@Test
	public void getTransalationById() {
		String code = "class.PC.description";

		given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("translations/" + code)).then()
				.statusCode(200);
	}

	@Test
	public void getTestLangParam() {
		String token = getSessionToken();
		Map<String, Object> jsonAsMap = new HashMap<>();

		jsonAsMap.put("en", "Printer");
		jsonAsMap.put("it", "Stampante");
		jsonAsMap.put("es", "impresora");

		given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(jsonAsMap)
				.pathParam("code", "class.Printer.description").when().put(buildRestV3Url("translations/{code}")).then()
				.statusCode(200);

		given().header(CMDBUILD_AUTH_HEADER, token).pathParam("code", "class.Printer.description")
				.queryParam("lang", "es").get(buildRestV3Url("translations/{code}")).then()
				.body("data.value", equalTo("impresora")).statusCode(200);

		given().header(CMDBUILD_AUTH_HEADER, token).pathParam("code", "class.Printer.description")
				.delete(buildRestV3Url("translations/{code}")).then().statusCode(200);
	}

	@Test
	public void getTestOptionalParamLimit() {
		String token = getSessionToken();
		Map<String, Object> jsonAsMap = new HashMap<>();

		jsonAsMap.put("en", "Printer");
		jsonAsMap.put("it", "Stampante");
		jsonAsMap.put("es", "impresora");

		given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(jsonAsMap)
				.pathParam("code", "class.Printer.description").when().put(buildRestV3Url("translations/{code}")).then()
				.statusCode(200);

		int limit = 1;

		given().header(CMDBUILD_AUTH_HEADER, token).queryParam("limit", limit).get(buildRestV3Url("translations"))
				.then().body("data.size()", equalTo(limit)).statusCode(200);

		given().header(CMDBUILD_AUTH_HEADER, token).pathParam("code", "class.Printer.description")
				.delete(buildRestV3Url("translations/{code}")).then().statusCode(200);
	}

	@Test
	public void getTestOptionalParamsLimitOffset() {
		String token = getSessionToken();
		Map<String, Object> jsonAsMap = new HashMap<>();

		jsonAsMap.put("en", "Printer");
		jsonAsMap.put("it", "Stampante");
		jsonAsMap.put("es", "impresora");

		given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(jsonAsMap)
				.pathParam("code", "class.Printer.description").when().put(buildRestV3Url("translations/{code}")).then()
				.statusCode(200);

		int limit = 2;
		int offset = 3;

		given().header(CMDBUILD_AUTH_HEADER, token).queryParam("limit", limit).queryParam("offset", offset)
				.get(buildRestV3Url("translations")).then().body("data.size()", equalTo(limit)).statusCode(200);

		given().header(CMDBUILD_AUTH_HEADER, token).pathParam("code", "class.Printer.description")
				.delete(buildRestV3Url("translations/{code}")).then().statusCode(200);
	}

	@Test
	public void getTestOptionalParamFilter() {
		String token = getSessionToken();
		Map<String, Object> jsonAsMap = new HashMap<>();

		jsonAsMap.put("en", "Printer");
		jsonAsMap.put("it", "Stampante");
		jsonAsMap.put("es", "impresora");

		given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(jsonAsMap)
				.pathParam("code", "class.Printer.description").when().put(buildRestV3Url("translations/{code}")).then()
				.statusCode(200);

		String filter = "class.Pri";

		given().header(CMDBUILD_AUTH_HEADER, token).queryParam("filter", filter).get(buildRestV3Url("translations"))
				.then().body("data.code", everyItem(containsString("class.Pri"))).statusCode(200);

		given().header(CMDBUILD_AUTH_HEADER, token).pathParam("code", "class.Printer.description")
				.delete(buildRestV3Url("translations/{code}")).then().statusCode(200);
	}

	@Test
	public void testPutDelete() {
		String token = getSessionToken();
		Map<String, Object> jsonAsMap = new HashMap<>();

		jsonAsMap.put("en", "Printer");
		jsonAsMap.put("it", "Stampante");

		given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(jsonAsMap)
				.pathParam("code", "class.Printer.description").when().put(buildRestV3Url("translations/{code}")).then()
				.statusCode(200);

		given().header(CMDBUILD_AUTH_HEADER, token).pathParam("code", "class.Printer.description")
				.get(buildRestV3Url("translations/{code}")).then().body("data.en", equalTo("Printer"))
				.body("data.it", equalTo("Stampante")).statusCode(200);

		given().header(CMDBUILD_AUTH_HEADER, token).pathParam("code", "class.Printer.description")
				.delete(buildRestV3Url("translations/{code}")).then().statusCode(200);

		given().header(CMDBUILD_AUTH_HEADER, token).get(buildRestV3Url("translations")).then()
				.body("data.code", not(hasItem("class.Printer.description"))).statusCode(200);
	}

	@Test
	public void testPutDeleteWithLangParam() {
		String token = getSessionToken();
		Map<String, Object> jsonAsMap = new HashMap<>();

		jsonAsMap.put("pl", "Komputer");
		
		given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(jsonAsMap)
				.pathParam("code", "class.PC.description").when().put(buildRestV3Url("translations/{code}")).then()
				.statusCode(200);

		given().header(CMDBUILD_AUTH_HEADER, token).pathParam("code", "class.PC.description")
				.get(buildRestV3Url("translations/{code}")).then().body("data.pl", equalTo("Komputer")).statusCode(200);

		given().header(CMDBUILD_AUTH_HEADER, token).pathParam("code", "class.PC.description").queryParam("lang", "pl")
				.delete(buildRestV3Url("translations/{code}")).then().statusCode(200);

		given().header(CMDBUILD_AUTH_HEADER, token).pathParam("code", "class.PC.description")
				.get(buildRestV3Url("translations/{code}")).then().body("data", not(hasItem("pl"))).statusCode(200);
	}

	@Test
	public void testTotal() {
		String token = getSessionToken();

		int totalValue = given().header(CMDBUILD_AUTH_HEADER, token).get(buildRestV3Url("translations")).then()
				.extract().jsonPath().getInt("meta.total");

		logger.info("" + totalValue);

		given().header(CMDBUILD_AUTH_HEADER, token).get(buildRestV3Url("translations")).then()
				.body("data.size()", equalTo(totalValue)).statusCode(200);
	}

	@Test
	public void testSuccessStatus() {
		given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("translations")).then()
				.body("success", equalTo(true)).statusCode(200);
	}

}
