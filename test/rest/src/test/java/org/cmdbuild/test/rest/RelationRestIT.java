package org.cmdbuild.test.rest;

import static com.jayway.restassured.RestAssured.given;
import java.util.HashMap;
import java.util.Map;

import org.cmdbuild.test.framework.CmTestRunner;
import org.cmdbuild.test.framework.Context;
import org.cmdbuild.test.rest.utils.AbstractWsIT;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.cmdbuild.test.rest.TestContextProviders.TC_R2U;
import org.junit.Ignore;

@RunWith(CmTestRunner.class)
@Context(TC_R2U)
public class RelationRestIT extends AbstractWsIT {

    final String classId = "Desktop";
    final Object cardId = 6293;

    @Test
    public void getFromCards() {
        given().header(CMDBUILD_AUTH_HEADER, getSessionToken())
                .get(buildRestV3Url("classes/" + classId + "/cards/" + cardId + "/relations")).then()
                .statusCode(200);
    }

    @Test
    @Ignore("TODO fix test, avoid duplicate key on _cm3_Map_RoomCI_target")
    public void testPostDeleteRelation() {
        String token = getSessionToken();
        Object sourceId = 6293;
        Object destinationId = 5992;

        Map<Object, Object> jsonAsMap = createRelation(sourceId, destinationId);

        logger.info(jsonAsMap.toString());

        Object relationId = given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(jsonAsMap)
                .when().post(buildRestV3Url("classes/" + classId + "/cards/" + cardId + "/relations")).then().statusCode(200).extract()
                .path("data._id");

        given().header(CMDBUILD_AUTH_HEADER, token).when()
                .delete(buildRestV3Url("classes/" + classId + "/cards/" + cardId + "/relations/" + relationId)).then().statusCode(200);

    }

    public Map<Object, Object> createRelation(Object sourceId, Object destinationId) {
        Map<Object, Object> jsonAsMap = new HashMap<>();

        jsonAsMap.put("_type", "RoomCI");
        jsonAsMap.put("_sourceId", sourceId);
        jsonAsMap.put("_destinationId", destinationId);
        jsonAsMap.put("_sourceType", "Desktop");
        jsonAsMap.put("_is_direct", false);

        return jsonAsMap;
    }

    public JSONObject createJson(String attribute, String operator, Object value) {
        JSONObject json = new JSONObject();
        json.put("attribute", attribute);
        json.put("operator", operator);
        json.put("value", value);

        JSONObject json2 = new JSONObject();
        json2.put("simple", json);

        JSONObject json3 = new JSONObject();
        json3.put("attribute", json2);
        return json3;
    }

}
