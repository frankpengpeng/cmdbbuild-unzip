package org.cmdbuild.test.rest;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.equalTo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.cmdbuild.test.framework.CmTestRunner;
import org.cmdbuild.test.framework.Context;
import org.cmdbuild.test.rest.utils.AbstractWsIT;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.cmdbuild.test.rest.TestContextProviders.TC_R2U;
import org.junit.Ignore;

@RunWith(CmTestRunner.class)
@Context(TC_R2U)
public class GrantRestIT extends AbstractWsIT {

    @Test
    public void getGrants() {
        String token = getSessionToken();

        List<Object> allRolesIds = given().header(CMDBUILD_AUTH_HEADER, token).get(buildRestV3Url("roles/")).then()
                .statusCode(200).extract().jsonPath().getList("data._id");

        Object random = allRolesIds.get(new Random().nextInt(allRolesIds.size()));

        given().header(CMDBUILD_AUTH_HEADER, token).get(buildRestV3Url("roles/" + random + "/grants")).then()
                .statusCode(200);
    }

    @Test
    public void testLimit() {
        String token = getSessionToken();
        int limit = 3;
        int roleId = 16;

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("limit", limit).when()
                .get(buildRestV3Url("roles/" + roleId + "/grants/")).then()
                .body("data.size()", equalTo(limit)).statusCode(200);
    }

    @Test
    public void testStart() {
        String token = getSessionToken();
        int start = 2;
        int roleId = 16;

        int total = given().header(CMDBUILD_AUTH_HEADER, token).get(buildRestV3Url("roles/" + roleId + "/grants"))
                .then().extract().jsonPath().getInt("data.size()");

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("start", start).when()
                .get(buildRestV3Url("roles/" + roleId + "/grants")).then().body("data.size()", equalTo(total - start))
                .statusCode(200);
    }

    @Test
    public void testGrantCustomPrivileges() {

        String token = getSessionToken();

        given().header(CMDBUILD_AUTH_HEADER, token).when()
                .get(buildRestV3Url("roles/ChangeManager/grants/by-target/class/TechnicalService")).then()
                .body("data", hasKey("_card_relation_disabled"))
                .body("data", hasKey("_card_print_disabled"))
                .body("data", hasKey("_card_create_disabled"))
                .statusCode(200);

    }

    @Test
    @Ignore
    public void testGrantCustomPriviledgesPut() {
        String token = getSessionToken();
        int roleId = 16;
        Map<String, Object> jsonAsMap = createGrant();

        Object grantId = given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(jsonAsMap)
                .when().put(buildRestV3Url("roles/" + roleId + "/grants/_ANY")).then()
                .statusCode(200).extract().path("data._id");

        given().header(CMDBUILD_AUTH_HEADER, token).when()
                .get(buildRestV3Url("roles/16/grants/by-target/class/TechnicalService")).then()
                .body("data._card_relation_disabled", equalTo(true))
                .body("data._card_print_disabled", equalTo(true))
                .statusCode(200);
    }

    public Map<String, Object> createGrant() {
        Map<String, Object> jsonAsMap = new HashMap<>();

        jsonAsMap.put("role", 16);
        jsonAsMap.put("mode", "r");
        jsonAsMap.put("objectType", "class");
        jsonAsMap.put("objectTypeName", "TechnicalService");
        jsonAsMap.put("filter", null);
        jsonAsMap.put("_card_relation_disabled", true);
        jsonAsMap.put("_card_print_disabled", true);

        return jsonAsMap;
    }
}
