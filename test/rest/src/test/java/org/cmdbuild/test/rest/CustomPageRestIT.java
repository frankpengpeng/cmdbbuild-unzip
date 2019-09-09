package org.cmdbuild.test.rest;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.cmdbuild.test.framework.CmTestRunner;
import org.cmdbuild.test.framework.Context;
import org.cmdbuild.test.rest.utils.AbstractWsIT;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.cmdbuild.test.rest.TestContextProviders.TC_R2U;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;

@RunWith(CmTestRunner.class)
@Context(TC_R2U)
public class CustomPageRestIT extends AbstractWsIT {

    @Test
    public void getCustomPages() {
        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("custompages/")).then()
                .statusCode(200);
    }

    @Test
    public void getCustomPageById() {
        Object customPageId = createCustomPage();

        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("custompages/" + customPageId))
                .then().statusCode(200);

        deleteCustomPage(customPageId);
    }

    @Test
    public void postDeleteNewCustomPage() {
        Object customPageId = createCustomPage();

        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("custompages/" + customPageId))
                .then().body("data.name", equalTo("classescp")).statusCode(200);

        deleteCustomPage(customPageId);
    }

    @Test
    public void postPutDeleteNewCustomPage() {
        Map<String, Object> jsonAsMap = new HashMap<>();
        jsonAsMap.put("description", "classescp_modificata");

        Object customPageId = createCustomPage();

        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("custompages/" + customPageId))
                .then().body("data.name", equalTo("classescp")).body("data.description", equalTo("myCPage")).statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when().contentType("application/json").body(jsonAsMap)
                .put(buildRestV3Url("custompages/" + customPageId)).then().statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("custompages/" + customPageId))
                .then().body("data.description", equalTo("classescp_modificata")).statusCode(200);

        deleteCustomPage(customPageId);
    }

    @Test
    public void componentIdCustomPageTest() {
        Object customPageId = createCustomPage();

        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("custompages/" + customPageId))
                .then()
                .body("data.name", equalTo("classescp"))
                .body("data.componentId", equalTo("CMDBuildUI.view.custompages.classescp.Panel"))
                .statusCode(200);

        deleteCustomPage(customPageId);
    }

    @Test
    public void downloadCustomPage() {
        Object customPageId = createCustomPage();

        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("custompages/" + customPageId))
                .then().statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("custompages/" + customPageId) + "/file.zip")
                .then().statusCode(200);

        deleteCustomPage(customPageId);
    }

    public Object createCustomPage() {
        Object customPageId = given().header(CMDBUILD_AUTH_HEADER, getSessionToken())
                .multiPart("file", "file.zip", toByteArray(getClass().getResourceAsStream("/org/cmdbuild/test/rest/classescp.zip")))
                .multiPart("data", "{\"active\":\"true\",\"description\":\"myCPage\"}", "application/json")
                .when()
                .post(buildRestV3Url("custompages/")).then().statusCode(200).extract().path("data._id");

        return customPageId;
    }

    public void deleteCustomPage(Object customPageId) {
        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
                .delete(buildRestV3Url("custompages/" + customPageId)).then().statusCode(200);

    }

}
