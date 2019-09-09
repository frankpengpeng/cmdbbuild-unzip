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

@RunWith(CmTestRunner.class)
@Context(TC_R2U)
public class ClassAttributesRestIT extends AbstractWsIT {

    @Test
    public void getClassAttributes() {
        String classId = "Printer";

        given().header(CMDBUILD_AUTH_HEADER, getSessionToken())
                .get(buildRestV3Url("classes/" + classId + "/attributes/")).then().statusCode(200);
    }

    @Test
    public void getProcessAttributes() {
        String processId = "Building";

        given().header(CMDBUILD_AUTH_HEADER, getSessionToken())
                .get(buildRestV3Url("processes/" + processId + "/attributes/")).then().statusCode(200);
    }

    @Test
    public void getClassAttributesById() {
        String classId = "Printer";
        String attributeId = "Model";

        given().header(CMDBUILD_AUTH_HEADER, getSessionToken())
                .get(buildRestV3Url("classes/" + classId + "/attributes/" + attributeId)).then()
                .body("data.group", equalTo("CI - General Data")).statusCode(200);
    }

    @Test
    public void getProcessAttributesById() {
        String processId = "DynChild";
        String attributeId = "ProcessStatus";

        given().header(CMDBUILD_AUTH_HEADER, getSessionToken())
                .get(buildRestV3Url("processes/" + processId + "/attributes/" + attributeId)).then()
                .body("data.lookupType", equalTo("ITProc - ProcessStatus")).statusCode(200);
    }

    @Test
    public void testLimit() {
        String token = getSessionToken();
        String classId = "Building";
        int limitValue = 2;

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("limit", limitValue)
                .get(buildRestV3Url("classes/" + classId + "/attributes/")).then()
                .body("data.size()", equalTo(limitValue)).statusCode(200);
    }

    @Test
    public void testStart() {
        String token = getSessionToken();
        int startValue = 2;
        String classId = "NetworkDevice";

        int totalValue = given().header(CMDBUILD_AUTH_HEADER, token)
                .get(buildRestV3Url("classes/" + classId + "/attributes/")).then().extract().jsonPath()
                .getInt("meta.total");

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("start", startValue)
                .get(buildRestV3Url("classes/" + classId + "/attributes/")).then()
                .body("data.size()", equalTo(totalValue - startValue)).statusCode(200);
    }

    @Test
    public void postDeleteClassAttribute() {
        String token = getSessionToken();
        String classId = "NetworkDevice";
        Map<String, Object> jsonAsMap = new HashMap<>();

        String attributeId = createAttribute(token, jsonAsMap, classId);

        given().header(CMDBUILD_AUTH_HEADER, token).when()
                .get(buildRestV3Url("classes/" + classId + "/attributes/" + attributeId)).then()
                .body("data.type", equalTo("boolean"))
                .body("data.name", equalTo("Attribute_Test"))
                .body("data.description", equalTo("Attribute_Test"))
                .body("data.active", equalTo(true))
                .body("data.autoValue", equalTo("my auto value expr"))
                .body("data.mode", equalTo("write")).statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).when()
                .delete(buildRestV3Url("classes/" + classId + "/attributes/" + attributeId)).then().statusCode(200);
    }

    public String createAttribute(String token, Map<String, Object> jsonAsMap, String classId) {
        jsonAsMap.put("type", "boolean");
        jsonAsMap.put("name", "Attribute_Test");
        jsonAsMap.put("description", "Attribute_Test");
        jsonAsMap.put("active", true);
        jsonAsMap.put("mode", "write");
        jsonAsMap.put("showInGrid", true);
        jsonAsMap.put("unique", false);
        jsonAsMap.put("mandatory", false);
        jsonAsMap.put("inherited", false);
        jsonAsMap.put("hidden", false);
        jsonAsMap.put("writable", true);
        jsonAsMap.put("_can_read", true);
        jsonAsMap.put("_can_create", true);
        jsonAsMap.put("_can_update", true);
        jsonAsMap.put("_can_modify", true);
        jsonAsMap.put("showInReducedGrid", true);
        jsonAsMap.put("autoValue", "my auto value expr");

        String attributeId = given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(jsonAsMap)
                .when().post(buildRestV3Url("classes/" + classId + "/attributes/")).then().statusCode(200).extract()
                .path("data._id");

        return attributeId;
    }

}
