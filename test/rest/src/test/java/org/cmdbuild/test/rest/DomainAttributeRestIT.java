package org.cmdbuild.test.rest;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.anyOf;

import java.util.ArrayList;
import java.util.Collections;
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
public class DomainAttributeRestIT extends AbstractWsIT {

	@Test
	public void getDomainAttributes() {
		String domainId = "UserRole";

		given().header(CMDBUILD_AUTH_HEADER, getSessionToken())
				.get(buildRestV3Url("domains/" + domainId + "/attributes")).then().statusCode(200);
	}

	@Test
	public void getDomainAttributeById() {
		String domainId = "UserRole";
		String attributeId = "DefaultGroup";

		given().header(CMDBUILD_AUTH_HEADER, getSessionToken())
				.get(buildRestV3Url("domains/" + domainId + "/attributes/" + attributeId)).then()
				.body("data.name", equalTo("DefaultGroup")).statusCode(200);
	}

	@Test
	public void postDeleteDomainAttribute() {
		String domainId = "RoleITArea";
		String token = getSessionToken();
		Map<String, Object> jsonAsMap = new HashMap<>();

		String attributeId = createAttribute(token, jsonAsMap, domainId);

		given().header(CMDBUILD_AUTH_HEADER, token).when()
				.get(buildRestV3Url("domains/" + domainId + "/attributes/" + attributeId)).then()
				.body("data.type", equalTo("boolean")).body("data.name", equalTo("Attribute Test"))
				.body("data.description", equalTo("Attribute Test")).body("data.active", equalTo(true))
				.body("data.mode", equalTo("write")).statusCode(200);

		given().header(CMDBUILD_AUTH_HEADER, token).when()
				.delete(buildRestV3Url("domains/" + domainId + "/attributes/" + attributeId)).then().statusCode(200);
	}

	@Test
	public void postPutDeleteDomainAttribute() {
		String domainId = "UserRole";
		String token = getSessionToken();
		Map<String, Object> jsonAsMap = new HashMap<>();

		String attributeId = createAttribute(token, jsonAsMap, domainId);

		given().header(CMDBUILD_AUTH_HEADER, token).when()
				.get(buildRestV3Url("domains/" + domainId + "/attributes/" + attributeId)).then()
				.body("data.type", equalTo("boolean")).body("data.name", equalTo("Attribute Test"))
				.body("data.description", equalTo("Attribute Test")).body("data.active", equalTo(true))
				.body("data.mode", equalTo("write")).statusCode(200);

		jsonAsMap.put("description", "AttributeMoficato");

		given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(jsonAsMap).when()
				.put(buildRestV3Url("domains/" + domainId + "/attributes/" + attributeId)).then().statusCode(200);

		given().header(CMDBUILD_AUTH_HEADER, token).when()
				.get(buildRestV3Url("domains/" + domainId + "/attributes/" + attributeId)).then()
				.body("data.description", equalTo("AttributeMoficato")).statusCode(200);

		given().header(CMDBUILD_AUTH_HEADER, token).when()
				.delete(buildRestV3Url("domains/" + domainId + "/attributes/" +attributeId)).then().statusCode(200);
	}


	public String createAttribute(String token, Map<String, Object> jsonAsMap, String domainId) {
		jsonAsMap.put("type", "boolean");
		jsonAsMap.put("name", "Attribute Test");
		jsonAsMap.put("description", "Attribute Test");
		jsonAsMap.put("active", true);
		jsonAsMap.put("mode", "write");
		jsonAsMap.put("showInGrid", true);
		jsonAsMap.put("unique", false);
		jsonAsMap.put("mandatory", false);
		jsonAsMap.put("inherited", false);
		jsonAsMap.put("hidden", false);
		jsonAsMap.put( "_can_read", true);
		jsonAsMap.put("_can_create", true);
		jsonAsMap.put("_can_update", true);
		jsonAsMap.put("_can_modify", true);
		jsonAsMap.put("showInReducedGrid", true);

		String attributeId = given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(jsonAsMap)
				.when().post(buildRestV3Url("domains/" + domainId + "/attributes")).then().statusCode(200).extract()
				.path("data._id");

		return attributeId;
	}

}
