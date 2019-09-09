package org.cmdbuild.test.rest;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.hasKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cmdbuild.test.framework.CmTestRunner;
import org.cmdbuild.test.framework.Context;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.prepareTuid;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.tuid;
import static org.cmdbuild.test.rest.ClassFilterRestIT.prepareClassFilterData;
import org.cmdbuild.test.rest.utils.AbstractWsIT;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.cmdbuild.test.rest.TestContextProviders.TC_R2U;
import org.junit.Before;

@RunWith(CmTestRunner.class)
@Context(TC_R2U)
public class ClassRestIT extends AbstractWsIT {

    @Before
    public void init() {
        prepareTuid();
    }

    @Test
    public void testClasses() {
        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("classes/")).then().assertThat()
                .statusCode(200);
    }

    @Test
    public void testClassDetail() {
        List<String> allClasses = given().header(CMDBUILD_AUTH_HEADER, getSessionToken())
                .get(buildRestV3Url("classes/")).then().extract().jsonPath().getList("data._id");

        for (String string : allClasses) {
            given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("classes/" + string)).then()
                    .assertThat().statusCode(200);
//			logger.info(string);
        }
    }

    @Test
    public void testStart() {
        String token = getSessionToken();
        int startValue = 22;
        int totalValue = given().header(CMDBUILD_AUTH_HEADER, token).get(buildRestV3Url("classes")).then().extract()
                .jsonPath().getInt("meta.total");

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("start", startValue).get(buildRestV3Url("classes"))
                .then().body("data.size()", equalTo(totalValue - startValue)).statusCode(200);
    }

    @Test
    public void testLimit() {
        String token = getSessionToken();

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("limit", 1).get(buildRestV3Url("classes")).then()
                .body("data.size()", equalTo(1)).statusCode(200);
    }

    @Test
    public void testDetailed() {
        String token = getSessionToken();

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("detailed", true).get(buildRestV3Url("classes")).then()
                .body("data", everyItem((hasKey("widgets"))))
                .body("data", everyItem((hasKey("defaultOrder")))).statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("detailed", false).get(buildRestV3Url("classes")).then()
                .body("data", everyItem(not(hasKey("widgets")))).body("data", everyItem(not(hasKey("defaultOrder"))))
                .statusCode(200);
    }

    @Test
    public void postDeleteClass() {
        String token = getSessionToken();
        Map<String, Object> data = buildClassData();

        given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(data).when()
                .post(buildRestV3Url("classes")).then().statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).when().get(buildRestV3Url("classes/" + data.get("name")))
                .then().body("data.name", equalTo(data.get("name")))
                .body("data.description", equalTo("Test")).body("data.active", equalTo(true))
                .body("data.prototype", equalTo(false)).statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).when().delete(buildRestV3Url("classes/" + data.get("name")))
                .then().statusCode(200);
    }

    @Test
    public void testPostPutDeleteClass() {
        String token = getSessionToken();
        Map<String, Object> data = buildClassData();

        given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(data).when()
                .post(buildRestV3Url("classes/")).then().statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).when().get(buildRestV3Url("classes/" + data.get("name")))
                .then().body("data.name", equalTo(data.get("name"))).body("data.parent", equalTo("Class"))
                .body("data.description", equalTo("Test")).body("data.type", equalTo("standard")).statusCode(200);

        data.put("description", "This is a test");

        Object filterId = given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json")
                .body(prepareClassFilterData((String) data.get("name"))).when().post(buildRestV3Url("classes/" + data.get("name") + "/filters/")).then().statusCode(200)
                .extract().path("data._id");

        data.put("defaultFilter", filterId);

        given().header(CMDBUILD_AUTH_HEADER, token).when().contentType("application/json").body(data)
                .put(buildRestV3Url("classes/" + data.get("name"))).then().statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).when().get(buildRestV3Url("classes/" + data.get("name")))
                .then().body("data.description", equalTo("This is a test")).statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).when().delete(buildRestV3Url("classes/" + data.get("name")))
                .then().statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).when().get(buildRestV3Url("classes/" + data.get("name")))
                .then().statusCode(404);
    }

    public Map<String, Object> buildClassData() {
        Map<String, Object> jsonAsMap = new HashMap<>();

        jsonAsMap.put("parent", "Class");
        jsonAsMap.put("name", tuid("TestClass"));
        jsonAsMap.put("description", "Test");
        jsonAsMap.put("active", true);
        jsonAsMap.put("_can_read", true);
        jsonAsMap.put("_can_create", true);
        jsonAsMap.put("_can_update", true);
        jsonAsMap.put("_can_clone", true);
        jsonAsMap.put("_can_delete", true);
        jsonAsMap.put("_can_modify", true);
        jsonAsMap.put("prototype", false);
        jsonAsMap.put("type", "standard");

        return jsonAsMap;
    }
}
