package org.cmdbuild.test.rest;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.util.HashMap;
import java.util.Map;

import org.cmdbuild.test.framework.CmTestRunner;
import org.cmdbuild.test.framework.Context;
import org.cmdbuild.test.rest.utils.AbstractWsIT;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.cmdbuild.test.rest.TestContextProviders.TC_R2U;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

@RunWith(CmTestRunner.class)
@Context(TC_R2U)
public class MenuRestIT extends AbstractWsIT {

    @Test
    public void getMenues() {
        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("menu/")).then().statusCode(200);
    }

    @Test
    public void getMenuById() {
        String token = getSessionToken();

        Object firstId = given().header(CMDBUILD_AUTH_HEADER, token).get(buildRestV3Url("menu")).then().extract()
                .jsonPath().getLong("data[0]._id");

        given().header(CMDBUILD_AUTH_HEADER, token).get(buildRestV3Url("menu/" + firstId)).then()
                .body("data.menuType", equalTo("root")).body("data.objectDescription", equalTo("ROOT")).statusCode(200);
    }

    @Test
    public void getMenuByIdNotDetailed() {
        String token = getSessionToken();

        given().header(CMDBUILD_AUTH_HEADER, token).get(buildRestV3Url("menu?detailed=false")).then().statusCode(200);
    }

    @Test
    public void getMenuByIdDetailed() {
        String token = getSessionToken();

        int childNumber = given().header(CMDBUILD_AUTH_HEADER, token).get(buildRestV3Url("menu?detailed=true")).then()
                .statusCode(200).extract().path("data.children.size()");

        assertThat(childNumber, greaterThan(0));
    }

    @Test
    public void postDeleteMenu() {
        String token = getSessionToken();
        Map<String, Object> jsonAsMap = createMenu();

        Object menuId = given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).contentType("application/json")
                .body(jsonAsMap).when().post(buildRestV3Url("menu/")).then().statusCode(200).extract().path("data._id");

        given().header(CMDBUILD_AUTH_HEADER, token).when().get(buildRestV3Url("menu/" + menuId)).then()
                .body("data.group", equalTo("newgroup3")).statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).when().delete(buildRestV3Url("menu/" + menuId)).then()
                .statusCode(200);
    }

    @Test
    public void postPutDeleteMenu() {
        String token = getSessionToken();
        Map<String, Object> jsonAsMap = createMenu();

        Object menuId = given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).contentType("application/json")
                .body(jsonAsMap).when().post(buildRestV3Url("menu/")).then().statusCode(200).extract().path("data._id");

        given().header(CMDBUILD_AUTH_HEADER, token).when().get(buildRestV3Url("menu/" + menuId)).then()
                .body("data.group", equalTo("newgroup3")).statusCode(200);

        jsonAsMap.put("group", "group modificato");

        given().header(CMDBUILD_AUTH_HEADER, token).when().contentType("application/json").body(jsonAsMap)
                .put(buildRestV3Url("menu/" + menuId)).then().statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).when().get(buildRestV3Url("menu/" + menuId)).then()
                .statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).when().delete(buildRestV3Url("menu/" + menuId)).then()
                .statusCode(200);

    }

    public Map<String, Object> createMenu() {
        Map<String, Object> jsonAsMap = new HashMap<>();

        jsonAsMap.put("group", "newgroup3");

        return jsonAsMap;
    }

}
