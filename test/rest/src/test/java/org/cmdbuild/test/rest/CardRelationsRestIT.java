package org.cmdbuild.test.rest;

import static com.jayway.restassured.RestAssured.given;
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
import static org.junit.Assert.assertEquals;

@RunWith(CmTestRunner.class)
@Context(TC_R2U)
public class CardRelationsRestIT extends AbstractWsIT {

    @Test
    public void getCardRelations() {
        String classId = "Employee";
        String token = getSessionToken();

        List<Object> allCardsIds = given().header(CMDBUILD_AUTH_HEADER, token)
                .get(buildRestV3Url("classes/" + classId + "/cards")).then().statusCode(200).extract().jsonPath()
                .getList("data._id");

        Object random = allCardsIds.get(new Random().nextInt(allCardsIds.size()));

        given().header(CMDBUILD_AUTH_HEADER, token)
                .get(buildRestV3Url("classes/" + classId + "/cards/" + random + "/relations")).then().statusCode(200);
    }

    @Test
    public void addRemoveAddRelation() {
        String _id = "InternalEmployee";
        String token = getSessionToken();
        Object employeeCardId = createEmployee(token, _id);
        Object assetCardId = createAsset(token);
        Object relationId = createRelation(token, employeeCardId, assetCardId);

        Object id = given().header(CMDBUILD_AUTH_HEADER, token)
                .get(buildRestV3Url("classes/InternalEmployee/cards/" + employeeCardId + "/relations")).then().statusCode(200).extract()
                .path("data.get(0)._id");
        assertEquals(relationId, id);
        
        given().header(CMDBUILD_AUTH_HEADER, token)
                .delete(buildRestV3Url("classes/InternalEmployee/cards/" + employeeCardId + "/relations/" + relationId)).then().statusCode(200);
        
        relationId = createRelation(token, employeeCardId, assetCardId);
        
        id = given().header(CMDBUILD_AUTH_HEADER, token)
                .get(buildRestV3Url("classes/InternalEmployee/cards/" + employeeCardId + "/relations")).then().statusCode(200).extract()
                .path("data.get(0)._id");
        assertEquals(relationId, id);
    }

    public Object createEmployee(String token, String employeeType) {
        Map<String, Object> jsonAsMap = new HashMap<>();

        jsonAsMap.put("Code", "r.red");
        jsonAsMap.put("Email", "robert.rede@example.com");
        jsonAsMap.put("Description", "Robert Red");
        jsonAsMap.put("Mobile", "6886795");
        jsonAsMap.put("FirstName", "Robert");
        jsonAsMap.put("Phone", "65432");
        jsonAsMap.put("State", 108);
        jsonAsMap.put("LastName", "Red");

        Object id = given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(jsonAsMap).when()
                .post(buildRestV3Url("classes/" + employeeType + "/cards")).then().statusCode(200).extract()
                .path("data._id");

        return id;
    }

    public Object createAsset(String token) {
        Map<String, Object> jsonAsMap = new HashMap<>();

        jsonAsMap.put("Code", "TestPC");
        jsonAsMap.put("Hostname", "TestPC");
        jsonAsMap.put("Description", "TestPC");

        Object id = given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(jsonAsMap).when()
                .post(buildRestV3Url("classes/Notebook/cards")).then().statusCode(200).extract()
                .path("data._id");

        return id;
    }

    public Object createRelation(String token, Object sourceId, Object destinationId) {
        Map<String, Object> jsonAsMap = new HashMap<>();

        jsonAsMap.put("_destinationId", destinationId);
        jsonAsMap.put("_destinationType", "Notebook");
        jsonAsMap.put("_sourceId", sourceId);
        jsonAsMap.put("_sourceType", "InternalEmployee");
        jsonAsMap.put("_type", "AssigneeCI");
        jsonAsMap.put("_is_direct", true);

        Object id = given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(jsonAsMap).when()
                .post(buildRestV3Url("classes/InternalEmployee/cards/" + sourceId + "/relations")).then().statusCode(200).extract()
                .path("data._id");

        return id;
    }
}
