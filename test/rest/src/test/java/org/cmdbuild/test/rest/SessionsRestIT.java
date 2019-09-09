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
public class SessionsRestIT extends AbstractWsIT {

	@Test
	public void postNewSession() {
		given().contentType(ContentType.JSON).body("{\"username\" : \"admin\", \"password\" : \"admin\"}")
				.post(buildRestV3Url("sessions?scope=ui")).then().statusCode(200);
	}

	@Test
	public void getSessionbyId() {
		String token = getSessionToken();

		given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("sessions/" + token)).then()
				.statusCode(200);
	}
        
        @Test
	public void getAdminSessionbyId() {
		String sessionId = given().contentType(ContentType.JSON)
				.body("{\"username\" : \"admin\", \"password\" : \"admin\"}").post(buildRestV3Url("sessions?scope=ui")).then()
				.statusCode(200).extract().path("data._id");
                
                given().header(CMDBUILD_AUTH_HEADER, sessionId).contentType(ContentType.JSON)
				.get(buildRestV3Url("sessions/" + sessionId)).then().statusCode(200);
	}

	@Test
	public void deleteAllSessions() {
		given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).delete(buildRestV3Url("sessions/all")).then()
				.statusCode(200);
	}

	@Test
	public void keepAliveSession() {

		String sessionId = given().contentType(ContentType.JSON)
				.body("{\"username\" : \"admin\", \"password\" : \"admin\"}").post(buildRestV3Url("sessions?scope=ui")).then()
				.statusCode(200).extract().path("data._id");

		given().header(CMDBUILD_AUTH_HEADER, sessionId).contentType(ContentType.JSON)
				.post(buildRestV3Url("sessions/" + sessionId + "/keepalive")).then().statusCode(200);

	}

	@Test
	public void deleteSessionById() {

		String sessionId = given().contentType(ContentType.JSON)
				.body("{\"username\" : \"admin\", \"password\" : \"admin\"}").post(buildRestV3Url("sessions?scope=ui")).then()
				.statusCode(200).extract().path("data._id");
		logger.info(""+sessionId);

		given().header(CMDBUILD_AUTH_HEADER, sessionId).delete(buildRestV3Url("sessions/" + sessionId)).then()
				.statusCode(200);

	}

	@Test
	public void putSessionById() {

		String sessionId = given().contentType(ContentType.JSON)
				.body("{\"username\" : \"admin\", \"password\" : \"admin\"}").post(buildRestV3Url("sessions?scope=ui")).then()
				.statusCode(200).extract().path("data._id");

		given().header(CMDBUILD_AUTH_HEADER, sessionId).contentType(ContentType.JSON)
				.body("{\"userDescription\" : \"AdministratorModificato\", \"role\" : \"SuperUser\"}")
				.put(buildRestV3Url("sessions/" + sessionId)).then().statusCode(200);
		logger.info(sessionId);

	}
}