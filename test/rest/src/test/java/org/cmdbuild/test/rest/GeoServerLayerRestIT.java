/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.rest;

import static com.jayway.restassured.RestAssured.given;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.cmdbuild.client.rest.RestClient;
import org.cmdbuild.test.framework.CmTestRunner;
import org.cmdbuild.test.framework.Context;
import static org.cmdbuild.test.rest.TestContextProviders.TC_R2U;
import org.cmdbuild.test.rest.utils.AbstractWsIT;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CmTestRunner.class)
@Context(TC_R2U)
public class GeoServerLayerRestIT extends AbstractWsIT {

    @Before
    public void init() {
        RestClient restClient = getRestClient();
        restClient.system().setConfig("org.cmdbuild.gis.enabled", "true");
        restClient.system().setConfig("org.cmdbuild.gis.geoserver.enabled", "true");
    }

    @Test
    @Ignore
    public void testPostGetGeoServerLayer() {
        String classId = "Employee";
        String token = getSessionToken();
        Map<String, Object> jsonAsMap = new HashMap<>();

        List<Object> allCardsIds = given().header(CMDBUILD_AUTH_HEADER, token)
                .get(buildRestV3Url("classes/" + classId + "/cards")).then().statusCode(200).extract().jsonPath()
                .getList("data._id");

        Object random = allCardsIds.get(new Random().nextInt(allCardsIds.size()));
        logger.error("random . " + random);
        jsonAsMap = createCard(classId, random);

        logger.info(given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(jsonAsMap).when()
                .post(buildRestV3Url("classes/" + classId + "/cards/" + random + "/geolayers")).then().statusCode(200).extract().path("data.active"));
    }

    public Map<String, Object> createCard(String className, Object cardId) {
        Map<String, Object> jsonAsMap = new HashMap<>();

        jsonAsMap.put("name", "testCard");
        jsonAsMap.put("description", "Test");
        jsonAsMap.put("active", true);
        jsonAsMap.put("type", "SHAPE");
        jsonAsMap.put("index", 1);
        jsonAsMap.put("geoserver_name", "CPX01__TT01__F00N");
        jsonAsMap.put("description", "CPX01__TT01__F00N");
        jsonAsMap.put("zoomMin", 18);
        jsonAsMap.put("zoomDef", 18);
        jsonAsMap.put("zoomMax", 25);
        jsonAsMap.put("owner_type", className);
        jsonAsMap.put("owner_id", cardId);

        return jsonAsMap;
    }

}
