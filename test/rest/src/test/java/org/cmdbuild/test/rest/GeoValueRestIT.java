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
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.hamcrest.Matchers.equalTo;
import org.json.JSONObject;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CmTestRunner.class)
@Context(TC_R2U)
public class GeoValueRestIT extends AbstractWsIT {

    @Before
    public void init() {
        RestClient restClient = getRestClient();
        restClient.system().setConfig("org.cmdbuild.gis.enabled", "true");
        restClient.system().setConfig("org.cmdbuild.gis.geoserver.enabled", "true");
    }

    @Test
    public void testGeoValueBoundingBox() {
        String token = getSessionToken();
        Map<String, Object> geoAttributeAsMap = createGeoAttribute("testAttr2", "Building", 0);
        Map<String, Object> geoValueAsMap = createGeoValue(1472162.6835386199, 5797711.8698271802);

        int id = given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(geoAttributeAsMap).when()
                .post(buildRestV3Url("classes/Building/geoattributes")).then().statusCode(200).extract().path("data._id");

        String geoAttrName = given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json")
                .get(buildRestV3Url("classes/Building/geoattributes/" + id)).then().statusCode(200).extract().path("data.name");

        List<Object> allCardsIds = given().header(CMDBUILD_AUTH_HEADER, token)
                .get(buildRestV3Url("classes/Building/cards")).then().statusCode(200).extract().jsonPath()
                .getList("data._id");

        Object random = allCardsIds.get(new Random().nextInt(allCardsIds.size()));

        given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(geoValueAsMap).when()
                .put(buildRestV3Url("classes/Building/cards/" + random + "/geovalues/" + geoAttrName)).then().statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token)
                .queryParam("attribute", id)
                .get(buildRestV3Url("classes/_ANY/cards/_ANY/geovalues/area"))
                .then()
                .body("data.size()", equalTo(4))
                .body("success", equalTo(true))
                .body("found", equalTo(true))
                .statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token)
                .queryParam("attribute", id)
                .get(buildRestV3Url("classes/_ANY/cards/_ANY/geovalues/center"))
                .then()
                .body("data.size()", equalTo(2))
                .body("success", equalTo(true))
                .body("found", equalTo(true))
                .statusCode(200);
    }

    @Test
    public void testGetGeoValue() {
        String token = getSessionToken();
        Map<String, Object> geoAttributeAsMap = createGeoAttribute("testAttr1", "Building", 0);
        Map<String, Object> geoValueAsMap = createGeoValue(1472162.6835386199, 5797711.8698271802);
        JSONObject json = new JSONObject();

        JSONObject json2 = new JSONObject();
        json2.put("query", "aaabbbccc");

        int id = given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(geoAttributeAsMap).when()
                .post(buildRestV3Url("classes/Building/geoattributes")).then().statusCode(200).extract().path("data._id");

        String geoAttrName = given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json")
                .get(buildRestV3Url("classes/Building/geoattributes/" + id)).then().statusCode(200).extract().path("data.name");

        List<Object> allCardsIds = given().header(CMDBUILD_AUTH_HEADER, token)
                .get(buildRestV3Url("classes/Building/cards")).then().statusCode(200).extract().jsonPath()
                .getList("data._id");

        Object random = allCardsIds.get(new Random().nextInt(allCardsIds.size()));

        given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(geoValueAsMap).when()
                .put(buildRestV3Url("classes/Building/cards/" + random + "/geovalues/" + geoAttrName)).then().statusCode(200);

        List ids = given().header(CMDBUILD_AUTH_HEADER, token)
                .get(buildRestV3Url("classes/_ANY/cards/_ANY/geovalues?attribute=" + id + "&area=289849.2112573888,4666939.198979716,2200163.4221605137,6687322.730613495&attach_nav_tree=false"))
                .then().statusCode(200).extract().jsonPath()
                .getList("data._id");

        assertEquals(1, ids.size());

        String desc = given().header(CMDBUILD_AUTH_HEADER, token)
                .get(buildRestV3Url("classes/Building/cards/" + random)).then().statusCode(200).extract().jsonPath()
                .get("data.description");
        json.put("query", desc);

        ids = given().header(CMDBUILD_AUTH_HEADER, token)
                .queryParam("filter", json)
                .queryParam("attribute", id)
                .queryParam("area", "289849.2112573888,4666939.198979716,2200163.4221605137,6687322.730613495")
                .queryParam("attach_nav_tree", false)
                .get(buildRestV3Url("classes/_ANY/cards/_ANY/geovalues"))
                .then().statusCode(200).extract().jsonPath()
                .getList("data._id");

        assertEquals(1, ids.size());

        ids = given().header(CMDBUILD_AUTH_HEADER, token)
                .queryParam("filter", json2)
                .queryParam("attribute", id)
                .queryParam("area", "289849.2112573888,4666939.198979716,2200163.4221605137,6687322.730613495")
                .queryParam("attach_nav_tree", false)
                .get(buildRestV3Url("classes/_ANY/cards/_ANY/geovalues"))
                .then().statusCode(200).extract().jsonPath()
                .getList("data._id");

        assertEquals(0, ids.size());
    }

    public Map<String, Object> createGeoAttribute(String name, String owner, int index) {
        Map<String, Object> jsonAsMap = new HashMap<>();

        jsonAsMap.put("name", name);
        jsonAsMap.put("owner_type", owner);
        jsonAsMap.put("active", true);
        jsonAsMap.put("type", "geometry");
        jsonAsMap.put("subtype", "POINT");
        jsonAsMap.put("description", name);
        jsonAsMap.put("index", index);
        jsonAsMap.put("visibility", list("Complex", "Building", "Floor", "Room"));
        jsonAsMap.put("zoomMin", 6);
        jsonAsMap.put("zoomMax", 17);
        jsonAsMap.put("zoomDef", 6);
        jsonAsMap.put("style", map(
                "fillColor", "#F1C232",
                "fillOpacity", 1,
                "pointRadius", 20,
                "strokeColor", "#000000",
                "strokeWidth", 1,
                "strokeOpacity", 1,
                "strokeDashstyle", "solid"
        ));
        return jsonAsMap;
    }

    public Map<String, Object> createGeoValue(double x, double y) {
        Map<String, Object> jsonAsMap = new HashMap<>();

        jsonAsMap.put("_type", "point");
        jsonAsMap.put("x", x);
        jsonAsMap.put("y", y);

        return jsonAsMap;
    }
}
