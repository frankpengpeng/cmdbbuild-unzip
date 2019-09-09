/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.rest;

import static com.jayway.restassured.RestAssured.given;
import java.util.HashMap;
import java.util.Map;
import org.cmdbuild.client.rest.RestClient;
import org.cmdbuild.test.framework.CmTestRunner;
import org.cmdbuild.test.framework.Context;
import static org.cmdbuild.test.rest.TestContextProviders.TC_R2U;
import org.cmdbuild.test.rest.utils.AbstractWsIT;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CmTestRunner.class)
@Context(TC_R2U)
public class GeoAttributeRestIT extends AbstractWsIT {

    @Before
    public void init() {
        RestClient restClient = getRestClient();
        restClient.system().setConfig("org.cmdbuild.gis.enabled", "true");
        restClient.system().setConfig("org.cmdbuild.gis.geoserver.enabled", "true");
    }

    @Test
    public void testGetGeoAttributePostDelete() {
        String token = getSessionToken();
        Map<String, Object> geoAttributeAsMap = createGeoAttribute("testAttrDelete", "Building", 0);

        int id = given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(geoAttributeAsMap).when()
                .post(buildRestV3Url("classes/Building/geoattributes")).then().statusCode(200).extract().path("data._id");

        given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json")
                .get(buildRestV3Url("classes/Building/geoattributes/" + id)).then().statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").when()
                .delete(buildRestV3Url("classes/Building/geoattributes/" + id)).then().statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json")
                .get(buildRestV3Url("classes/Building/geoattributes/" + id)).then().statusCode(404);
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

}
