package org.cmdbuild.test.rest;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.util.Map;
import org.cmdbuild.test.framework.CmTestRunner;
import org.cmdbuild.test.framework.Context;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.prepareTuid;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.tuid;
import org.cmdbuild.test.rest.utils.AbstractWsIT;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.cmdbuild.test.rest.TestContextProviders.TC_R2U;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.junit.Before;

@RunWith(CmTestRunner.class)
@Context(TC_R2U)
public class ViewRestIT extends AbstractWsIT {

    @Test
    public void viewTest() {
        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("views/")).then().statusCode(200);
    }

    @Before
    public void init() {
        prepareTuid();
    }

    @Test
    public void createAndGetViewById() {
        String token = getSessionToken();
        Map<String, Object> data = getDataForView();

        Object viewId = given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).contentType("application/json")
                .body(data).when().post(buildRestV3Url("views/")).then().statusCode(200).extract()
                .path("data._id");

        given().header(CMDBUILD_AUTH_HEADER, token).when().get(buildRestV3Url("views/" + viewId)).then()
                .body("data.type", equalTo("FILTER")).body("data.name", equalTo(data.get("name")))
                .body("data.description", equalTo("room")).body("data.sourceClassName", equalTo("Room"))
                .statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).when().delete(buildRestV3Url("views/" + viewId)).then()
                .statusCode(200);
    }

    @Test
    public void postDeleteView() {
        String token = getSessionToken();
        Map<String, Object> data = getDataForView();

        Object viewId = given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).contentType("application/json")
                .body(data).when().post(buildRestV3Url("views/")).then().statusCode(200).extract()
                .path("data._id");

        given().header(CMDBUILD_AUTH_HEADER, token).when().get(buildRestV3Url("views/" + viewId)).then()
                .body("data.type", equalTo("FILTER")).body("data.name", equalTo(data.get("name")))
                .body("data.description", equalTo("room")).body("data.sourceClassName", equalTo("Room"))
                .statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).when().delete(buildRestV3Url("views/" + viewId)).then()
                .statusCode(200);
    }

    @Test
    public void testPostPutDeleteView() {
        String token = getSessionToken();
        Map<String, Object> data = getDataForView();

        Object viewId = given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).contentType("application/json")
                .body(data).when().post(buildRestV3Url("views/")).then().statusCode(200).extract()
                .path("data._id");

        given().header(CMDBUILD_AUTH_HEADER, token).when().get(buildRestV3Url("views/" + viewId)).then()
                .body("data.type", equalTo("FILTER")).body("data.name", equalTo(data.get("name")))
                .body("data.description", equalTo("room")).body("data.sourceClassName", equalTo("Room"))
                .statusCode(200);

        data.put("description", "room updated");

        given().header(CMDBUILD_AUTH_HEADER, token).when().contentType("application/json").body(data)
                .put(buildRestV3Url("views/" + viewId)).then().statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).when().get(buildRestV3Url("views/" + viewId)).then()
                .body("data.description", equalTo("room updated")).statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).when().delete(buildRestV3Url("views/" + viewId)).then()
                .statusCode(200);
    }

    private Map<String, Object> getDataForView() {
        return map("type", "filter",
                "name", tuid("Room"),
                "sourceClassName", "Room",
                "description", "room",
                "filter", false);
    }
}
