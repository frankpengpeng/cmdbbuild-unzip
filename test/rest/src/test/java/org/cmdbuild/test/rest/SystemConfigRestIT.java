package org.cmdbuild.test.rest;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.cmdbuild.test.framework.CmTestRunner;
import org.cmdbuild.test.framework.Context;
import org.cmdbuild.test.rest.utils.AbstractWsIT;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jayway.restassured.http.ContentType;
import static org.cmdbuild.test.rest.TestContextProviders.TC_R2U;

@RunWith(CmTestRunner.class)
@Context(TC_R2U)
public class SystemConfigRestIT extends AbstractWsIT {

	@Test
	public void getSystemConfig() {
		given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("system/config")).then()
				.statusCode(200);
	}

	@Test
	public void getSystemConfigDetailed() {
		given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).queryParam("detailed", true)
				.get(buildRestV3Url("system/config")).then().statusCode(200);
	}

	@Test
	public void getSystemConfigBykey() {
		String key = "org.cmdbuild.workflow.providers";

		given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("system/config/" + key)).then()
				.statusCode(200);
	}

	@Test
	public void putTextPlainBykey() {
		String key = "org.cmdbuild.workflow.providers";

		given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).contentType(ContentType.TEXT).body("river")
				.put(buildRestV3Url("system/config/" + key)).then().statusCode(200);

		given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).queryParam("detailed", true)
				.get(buildRestV3Url("system/config/" + key)).then().body("data", equalTo("river")).statusCode(200);
	}

	@Test
	public void putMany() {
		given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).contentType(ContentType.TEXT).body("something")
				.put(buildRestV3Url("system/config/_MANY")).then().statusCode(200);

		given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).queryParam("detailed", true)
				.get(buildRestV3Url("system/config")).then().body("data._MANY.value", equalTo("something"))
				.statusCode(200);
	}

}
