package org.cmdbuild.test.rest;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.util.Collections;
import java.util.List;

import org.cmdbuild.test.rest.utils.AbstractWsIT;
import org.junit.Test;

import org.cmdbuild.test.framework.CmTestRunner;
import org.cmdbuild.test.framework.Context;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import org.junit.runner.RunWith;
import static org.cmdbuild.test.rest.TestContextProviders.TC_R2U;

@RunWith(CmTestRunner.class)
@Context(TC_R2U)
public class LookUpRestIT extends AbstractWsIT {

	@Test
	public void simpleTest() {
		given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("lookup_types/")).then().assertThat()
				.statusCode(200);
	}

	@Test
	public void testLookUpTypesDetail() {
		String _id = "Country";

		given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("lookup_types/" + _id)).then()
				.assertThat().statusCode(200);
	}

	@Test
	public void simpleTestValues() {
		String name = "Country";

		given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("lookup_types/" + name + "/values"))
				.then().assertThat().statusCode(200);
	}

	@Test
	public void testPostDelete() {
		String token = getSessionToken();

		given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json")
				.body("{\"name\" : \"my_lookup\", \"parent\" : \"Brand\"}").when().post(buildRestV3Url("lookup_types"))
				.then().statusCode(200);

		given().header(CMDBUILD_AUTH_HEADER, token).when().get(buildRestV3Url("lookup_types")).then()
				.body("data._id", hasItem("my_lookup")).body("data.name", hasItem("my_lookup"))
				.body("data.parent", hasItem("Brand")).statusCode(200);

		given().header(CMDBUILD_AUTH_HEADER, token).pathParam("name", "my_lookup").when()
				.delete(buildRestV3Url("lookup_types/{name}")).then().statusCode(200);

		given().header(CMDBUILD_AUTH_HEADER, token).pathParam("name", "my_lookup").when()
				.get(buildRestV3Url("lookup_types/{name}")).then().statusCode(404);
	}

	@Test
	public void testPostPutDelete() {
		String token = getSessionToken();

		given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body("{\"name\" : \"my_lookup\"}")
				.when().post(buildRestV3Url("lookup_types")).then().statusCode(200);

		given().header(CMDBUILD_AUTH_HEADER, token).pathParam("name", "my_lookup").when()
				.get(buildRestV3Url("lookup_types/{name}")).then().statusCode(200);

		given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body("{\"name\" : \"aaa\"}")
				.pathParam("name", "my_lookup").when().put(buildRestV3Url("lookup_types/{name}")).then()
				.statusCode(200);

		given().header(CMDBUILD_AUTH_HEADER, token).when().get(buildRestV3Url("lookup_types")).then()
				.body("data.name", hasItem("aaa")).statusCode(200);

		given().header(CMDBUILD_AUTH_HEADER, token).pathParam("name", "aaa").when()
				.delete(buildRestV3Url("lookup_types/{name}")).then().statusCode(200);
	}

	@Test
	public void testWrongGet() {
		String token = getSessionToken();
		String pathParameter = "hhhh";

		given().header(CMDBUILD_AUTH_HEADER, token).pathParam("name", pathParameter).when()
				.get(buildRestV3Url("lookup_types/{name}")).then().body("success", is(false)).statusCode(404);
	}

	@Test
	public void testMissingField() {
		String token = getSessionToken();

		given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body("{\"name\" : \"\"}").when()
				.post(buildRestV3Url("lookup_types")).then().body("success", is(false)).statusCode(500);
	}

	@Test
	public void testTotal() {
		String token = getSessionToken();

		int totalValue = given().header(CMDBUILD_AUTH_HEADER, token).get(buildRestV3Url("lookup_types")).then()
				.extract().jsonPath().getInt("meta.total");

		logger.info("" + totalValue);

		given().header(CMDBUILD_AUTH_HEADER, token).get(buildRestV3Url("lookup_types")).then()
				.body("data.size()", equalTo(totalValue)).statusCode(200);
	}

	@Test
	public void testWrongDelete() {
		String pathParameter = "hhhh";

		given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).pathParam("name", pathParameter).when()
				.delete(buildRestV3Url("lookup_types/{name}")).then().body("success", is(false)).statusCode(404);
	}

	@Test
	public void testPostDeleteValues() {
		String token = getSessionToken();
		String name = "Country";

		Object id = given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").pathParam("name", name)
				.body("{\"code\" : \"HU\", \"icon_type\" : \"none\", \"description\" : \"Hungary\", \"default\" : false, \"active\" : true}").when()
				.post(buildRestV3Url("lookup_types/{name}/values")).then().statusCode(200).extract().path("data._id");

		given().header(CMDBUILD_AUTH_HEADER, token).when().get(buildRestV3Url("lookup_types/" + name + "/values"))
				.then().body("data.code", hasItem("HU")).body("data.description", hasItem("Hungary")).statusCode(200);

		given().header(CMDBUILD_AUTH_HEADER, token).pathParam("name", name).when()
				.delete(buildRestV3Url("lookup_types/{name}/values/" + id)).then().statusCode(200);

		given().header(CMDBUILD_AUTH_HEADER, token).pathParam("name", name).when()
				.get(buildRestV3Url("lookup_types/{name}/values/" + id)).then().body("success", is(false))
				.statusCode(not(200));
	}

	@Test
	public void testExistingValue() {
		String token = getSessionToken();
		String name = "Country";

		given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").pathParam("name", name)
				.body("{\"code\" : \"UK\", \"icon_type\" : \"none\"}").when()
				.post(buildRestV3Url("lookup_types/{name}/values")).then().statusCode(500);
	}

	@Test
	public void testNullFields() {
		String token = getSessionToken();
		String name = "Country";

		given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").pathParam("name", name)
				.body("{\"code\" : \"FF\", \"icon_type\" : \"\"}").when()
				.post(buildRestV3Url("lookup_types/{name}/values")).then().statusCode(500);

		given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").pathParam("name", name)
				.body("{\"code\" : \"\", \"icon_type\" : \"mmm\"}").when()
				.post(buildRestV3Url("lookup_types/{name}/values")).then().statusCode(500);

		given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").pathParam("name", name)
				.body("{\"code\" : \"\", \"icon_type\" : \"\"}").when()
				.post(buildRestV3Url("lookup_types/{name}/values")).then().statusCode(500);
	}

	@Test
	public void testPutValue() {
		String token = getSessionToken();
		String name = "Country";

		Object id = given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").pathParam("name", name)
				.body("{\"code\" : \"HU\", \"icon_type\" : \"none\", \"description\" : \"Hungary\", \"default\" : false, \"active\" : true}").when()
				.post(buildRestV3Url("lookup_types/{name}/values")).then().statusCode(200).extract().path("data._id");

		given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").pathParam("name", name)
				.body("{\"code\" : \"LT\", \"icon_type\" : \"none\", \"description\" : \"Lithuania\", \"default\" : false, \"active\" : true}").when()
				.put(buildRestV3Url("lookup_types/{name}/values/" + id)).then().statusCode(200);

		given().header(CMDBUILD_AUTH_HEADER, token).pathParam("name", name).when()
				.get(buildRestV3Url("lookup_types/{name}/values/" + id)).then().body("data.code", is("LT"))
				.body("data.description", is("Lithuania")).statusCode(200);

		given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").pathParam("name", name)
				.body("{\"code\" : \"CA\", \"icon_type\" : \"none\", \"description\" : \"Canada\", \"default\" : false, \"active\" : true}").when()
				.put(buildRestV3Url("lookup_types/{name}/values/" + id)).then().statusCode(200);

		given().header(CMDBUILD_AUTH_HEADER, token).pathParam("name", name).when()
				.get(buildRestV3Url("lookup_types/{name}/values/" + id)).then().body("data.code", is("CA"))
				.body("data.description", is("Canada")).statusCode(200);

		given().header(CMDBUILD_AUTH_HEADER, token).pathParam("name", name).when()
				.delete(buildRestV3Url("lookup_types/{name}/values/" + id)).then().statusCode(200);
	}

	@Test
	public void testOrder() {
		String token = getSessionToken();
		String name = "Country";

		List<Integer> index = given().header(CMDBUILD_AUTH_HEADER, token)
				.get(buildRestV3Url("lookup_types/" + name + "/values")).then().extract().jsonPath()
				.getList("data._id ");

		Collections.sort(index);

		logger.info("" + index.toString());

		given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(index).when()
				.post(buildRestV3Url("lookup_types/" + name + "/values/order")).then().statusCode(200);

		for (int i = 0; i < index.size(); i++) {
			given().header(CMDBUILD_AUTH_HEADER, token).when()
					.get(buildRestV3Url("lookup_types/" + name + "/values/" + index.get(i))).then()
					.body("data.index", is(i + 1)).statusCode(200);

			int v = given().header(CMDBUILD_AUTH_HEADER, token)
					.get(buildRestV3Url("lookup_types/" + name + "/values/" + index.get(i))).then().extract().jsonPath()
					.getInt("data.index");
			logger.info("_id: " + index.get(i) + ",  index: " + v);
		}
	}

}
