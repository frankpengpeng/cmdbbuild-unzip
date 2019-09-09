package org.cmdbuild.test.rest;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.hasItem;

import org.cmdbuild.test.rest.utils.AbstractWsIT;
import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.Test;

import com.jayway.restassured.http.ContentType;

import org.apache.commons.lang3.RandomStringUtils;
import org.cmdbuild.test.framework.CmTestRunner;
import org.cmdbuild.test.framework.Context;
import org.junit.runner.RunWith;
import static org.cmdbuild.test.rest.TestContextProviders.TC_R2U;

@RunWith(CmTestRunner.class)
@Context(TC_R2U)
public class MixedRestIT extends AbstractWsIT {

    @Test
    public void logSessionToken() {
        // http://192.168.1.248:8090/cmdbuild/services/rest/v3/system/status
        logger.debug("session token: " + getSessionToken());
    }

    @Test
    public void simpleTest() {
        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("system/status")).then().assertThat()
                .statusCode(200);
    }

    @Test
    public void testBootStatus() {
        given().get(buildRestV3Url("boot/status")).then().body("status", equalTo("READY")).statusCode(200);
    }

    @Test
    public void testLanguages1() {
        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("configuration/languages")).then()
                .body("data.code", hasItem("en")).body("data.description", hasItem("English")).statusCode(200);
    }

    @Test
    public void testLanguages2() {
        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("configuration/languages/")).then()
                .body("data.code", hasItem("nl")).body("data.description", hasItem("Nederlands")).statusCode(200);
    }

    @Test
    public void testUiHome1() {
        given().get(getBaseUrl() + "ui").then().body(containsString("<html")).body(containsString("var Ext = Ext"))
                .statusCode(200);
    }

    @Test
    public void testUiHome2() {
        given().get(getBaseUrl() + "ui/").then().body(containsString("<html")).body(containsString("var Ext = Ext"))
                .statusCode(200);
    }

    @Test
    public void testUserPreferences() {
        String sessionId = given().contentType(ContentType.JSON)
                .body("{\"username\" : \"admin\", \"password\" : \"admin\"}").post(buildRestV3Url("sessions?scope=ui")).then()
                .statusCode(200).extract().path("data._id");

        given().header(CMDBUILD_AUTH_HEADER, sessionId).get(buildRestV3Url("sessions/current/preferences"))
                .then().body(containsString("cm_ui_startingClass")).statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, sessionId).contentType(ContentType.TEXT).body("my-value")
                .put(buildRestV3Url("sessions/current/preferences/my_config")).then().statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, sessionId).get(buildRestV3Url("sessions/current/preferences"))
                .then().body(containsString("\"my_config\":\"my-value\"")).statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, sessionId)
                .get(buildRestV3Url("sessions/current/preferences/my_config")).then().body(equalTo("my-value"))
                .statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, sessionId)
                .delete(buildRestV3Url("sessions/current/preferences/my_config")).then().statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, sessionId)
                .get(buildRestV3Url("sessions/current/preferences/my_config")).then().statusCode(204);

        given().header(CMDBUILD_AUTH_HEADER, sessionId).get(buildRestV3Url("sessions/current/preferences"))
                .then().body(not(containsString("\"my_config\":\"my-value\""))).statusCode(200);
    }

    @Test
    public void testSimpleSecurity() {
        given().get(buildRestV3Url("classes/Email")).then().statusCode(anyOf(is(401), is(403)));
    }

    @Test
    public void doTestLogin1() {
        given().contentType(ContentType.JSON).body("{\"username\" : \"admin\", \"password\" : \"admin\"}")
                .post(buildRestV3Url("sessions?scope=ui")).then().statusCode(200);
    }

    @Test
    public void doTestLogin2() {
        given().contentType(ContentType.JSON).body("{\"username\" : \"admin\", \"password\" : \"admin\"}")
                .post(buildRestV3Url("sessions/?scope=ui")).then().statusCode(200);
    }

    @Test
    public void doImpersonate() {
        String token = getSessionToken();
        String userToImpersonate = "guest";

        given().header(CMDBUILD_AUTH_HEADER, token).contentType(ContentType.JSON)
                .post(buildRestV3Url("sessions/current/impersonate/" + userToImpersonate)).then()
                .statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).when()
                .delete(buildRestV3Url("sessions/current/impersonate/")).then().statusCode(200);
    }

    @Test
    public void testListDashboards() {
        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("dashboards/")).then()
                .statusCode(200);
    }

    @Test
    public void testListCustomPages() {
        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("custompages/")).then()
                .statusCode(200);
    }

    @Test
    public void testCommentsInWadl1() {
        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("?_wadl")).then()
                .body(containsString("<application xmlns=\"http://wadl.dev.java.net/2009/02\" xmlns:xs=")).statusCode(200);
    }

    @Test
    public void testCommentsInWadl2() {
        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("classes?_wadl")).then()
                .body(containsString("<application xmlns=\"http://wadl.dev.java.net/2009/02\" xmlns:xs=")).statusCode(200);
    }

    public Object createUser(String token, Map<String, Object> jsonAsMap, boolean active, boolean service) {

        JSONObject json1 = new JSONObject();
        json1.put("_id", 677);
        json1.put("username", "Helpdesk");
        json1.put("description", "Helpdesk");

        JSONObject json2 = new JSONObject();
        json2.put("_id", 940);
        json2.put("username", "ChangeManager");
        json2.put("description", "ChangeManager");

        JSONObject[] jsonArray = {json1, json2};

        jsonAsMap.put("username", "wwhite" + randomString());
        jsonAsMap.put("description", "White Walter");
        jsonAsMap.put("email", "walter.white" + randomString() + "@example.com");
        jsonAsMap.put("active", active);
        jsonAsMap.put("service", service);
        jsonAsMap.put("password", "wwhite");
        jsonAsMap.put("userGroups", jsonArray);

        Object userId = given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(jsonAsMap).when()
                .post(buildRestV3Url("users")).then().statusCode(200).extract().path("data._id");

        return userId;
    }

    public String randomString() {
        String str = RandomStringUtils.randomNumeric(6);
        return str;
    }

}
