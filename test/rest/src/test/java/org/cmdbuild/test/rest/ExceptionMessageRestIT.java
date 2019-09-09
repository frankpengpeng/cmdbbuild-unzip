/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.rest;

import static com.jayway.restassured.RestAssured.given;
import java.util.HashMap;
import java.util.Map;
import org.cmdbuild.test.framework.CmTestRunner;
import org.cmdbuild.test.framework.Context;
import static org.cmdbuild.test.rest.TestContextProviders.TC_R2U;
import org.cmdbuild.test.rest.utils.AbstractWsIT;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CmTestRunner.class)
@Context(TC_R2U)
public class ExceptionMessageRestIT extends AbstractWsIT {

    @Test
    @Ignore
    public void testTriggerUserFriendlyException() {
        String token = getSessionToken();
        Map<String, Object> classJsonAsMap = createClass();
        Map<String, Object> attributeJsonAsMap = new HashMap<>();
        Map<String, Object> jsonAsMap = new HashMap<>();

        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).contentType("application/json").body(classJsonAsMap).when()
                .post(buildRestV3Url("classes")).then().statusCode(200);

        String attributeId = createAttribute(token, attributeJsonAsMap, "testclass");
        getRestClient().system().eval("cmdb.getSystemApi().executeQuery(\"CREATE OR REPLACE FUNCTION exception_test_function() RETURNS trigger AS"
                + "        $BODY$"
                + "        BEGIN"
                + "            IF NEW.GenerateException = true THEN"
                + "                BEGIN RAISE EXCEPTION 'CM_CUSTOM_EXCEPTION: friendly exception!';"
                + "                END;"
                + "            END IF;"
                + "            RETURN NEW;"
                + "        END;"
                + "        $BODY$"
                + "        LANGUAGE PLPGSQL;"
                + "        "
                + "        CREATE TRIGGER exception_trigger"
                + "        BEFORE UPDATE"
                + "        ON testclass"
                + "        FOR EACH ROW"
                + "        EXECUTE PROCEDURE exception_test_function();\")");

        /* Trigger function
        CREATE OR REPLACE FUNCTION exception_test_function() RETURNS trigger AS
        $BODY$
        BEGIN
            IF NEW.GenerateException = true THEN
                BEGIN RAISE EXCEPTION 'CM_CUSTOM_EXCEPTION: friendly exception!';
                END;
            END IF;
            RETURN NEW;
        END;
        $BODY$
        LANGUAGE PLPGSQL;
        
        CREATE TRIGGER exception_trigger
        BEFORE UPDATE
        ON TestClass
        FOR EACH ROW
        EXECUTE PROCEDURE exception_test_function();
         */
        jsonAsMap.put("Code", "Test");
        jsonAsMap.put("GenerateException", true);

        logger.error(given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(jsonAsMap).when()
                .post(buildRestV3Url("classes/" + "testclass" + "/cards")).then().statusCode(200).extract().path("data.errors"));
    }

    public Map<String, Object> createClass() {
        Map<String, Object> jsonAsMap = new HashMap<>();

        jsonAsMap.put("parent", "Class");
        jsonAsMap.put("name", "testclass");
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

    public String createAttribute(String token, Map<String, Object> jsonAsMap, String classId) {
        jsonAsMap.put("type", "boolean");
        jsonAsMap.put("name", "GenerateException");
        jsonAsMap.put("description", "just a test");
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
