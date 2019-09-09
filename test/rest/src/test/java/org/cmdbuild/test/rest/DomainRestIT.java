package org.cmdbuild.test.rest;

import static com.jayway.restassured.RestAssured.given;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasKey;
import java.util.Map;

import org.cmdbuild.test.framework.CmTestRunner;
import org.cmdbuild.test.framework.Context;
import org.cmdbuild.test.rest.utils.AbstractWsIT;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.cmdbuild.test.rest.TestContextProviders.TC_R2U;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

@RunWith(CmTestRunner.class)
@Context(TC_R2U)
public class DomainRestIT extends AbstractWsIT {

    @Test
    public void domainsTest() {
        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("domains/")).then().statusCode(200);
    }

    @Test
    public void testFilterEqual() {
        String token = getSessionToken();

        String[] sources = {"Supplier"};
        String attribute = "source";
        String operator = "equal";

        JSONObject json = createJson(attribute, operator, sources);

        logger.info("Query 1: {}", json);

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("filter", json).get(buildRestV3Url("domains/")).then()
                .body("data.name", everyItem(startsWith(sources[0]))).statusCode(200);

        String[] destinations = {"Room"};
        attribute = "destination";

        json = createJson(attribute, operator, destinations);

        logger.info("Query 2: {}", json);

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("filter", json).get(buildRestV3Url("domains/")).then()
                .body("data.name", everyItem(endsWith(destinations[0]))).statusCode(200);

        Boolean[] value = {false};
        attribute = "active";

        json = createJson(attribute, operator, value);

        logger.info("Query 3: {}", json);

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("filter", json).get(buildRestV3Url("domains/")).then()
                .body("data.size()", equalTo(0)).statusCode(200);

    }

    @Test
    public void testFilterIn() {
        String token = getSessionToken();

        String attribute = "source";
        String operator = "in";
        String[] sources = {"Tenant"};

        JSONObject json = createJson(attribute, operator, sources);

        logger.info("Query 1: {}", json);

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("filter", json).get(buildRestV3Url("domains/")).then()
                .body("data.name", everyItem(startsWith("Tenant"))).statusCode(200);

        attribute = "destination";
        String[] destinations = {"Room"};

        json = createJson(attribute, operator, destinations);

        logger.info("Query 2: {}", json);

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("filter", json).get(buildRestV3Url("domains/")).then()
                .body("data.name", everyItem(endsWith(destinations[0]))).statusCode(200);

        Boolean[] value = {false};
        attribute = "active";

        json = createJson(attribute, operator, value);

        logger.info("Query 3: {}", json);

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("filter", json).get(buildRestV3Url("domains/")).then()
                .body("data.size()", equalTo(0)).statusCode(200);

    }

    @Test
    public void testLimit() {
        String token = getSessionToken();

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("limit", 1).get(buildRestV3Url("domains")).then()
                .body("data.size()", equalTo(1)).statusCode(200);
    }

    @Test
    public void testStart() {
        String token = getSessionToken();
        int startValue = 1;

        int totalValue = given().header(CMDBUILD_AUTH_HEADER, token).get(buildRestV3Url("domains")).then().extract()
                .jsonPath().getInt("meta.total");

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("start", startValue).get(buildRestV3Url("domains"))
                .then().body("data.size()", equalTo(totalValue - startValue)).statusCode(200);
    }

    @Test
    public void testIncludeFullDetails() {
        String token = getSessionToken();

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("ext", true).get(buildRestV3Url("domains")).then()
                .body("data[0]", hasKey("cardinality")).body("data[0]", hasKey("descriptionDirect"))
                .body("data[0]", hasKey("descriptionInverse")).body("data[0]", hasKey("indexDirect"))
                .body("data[0]", hasKey("isMasterDetail")).statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("ext", false).get(buildRestV3Url("domains")).then()
                .body("data[0]", not(hasKey("cardinality"))).body("data[0]", not(hasKey("descriptionDirect")))
                .body("data[0]", not(hasKey("descriptionInverse"))).body("data[0]", not(hasKey("indexDirect")))
                .body("data[0]", not(hasKey("isMasterDetail"))).statusCode(200);
    }

    @Test
    public void testDomainDetails() {
        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("domains/BuildingRoom")).then()
                .body("data.source", equalTo("Building")).body("data.destination", equalTo("Room")).statusCode(200);
    }

    @Test
    public void testPostDeleteDomain() {
        String token = getSessionToken();
        Map<String, Object> jsonAsMap = createDomain();

        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).contentType("application/json").body(jsonAsMap).when()
                .post(buildRestV3Url("domains/")).then().statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).when().get(buildRestV3Url("domains/" + jsonAsMap.get("name"))).then()
                .body("data.description", equalTo("Server room"))
                .body("data.source", equalTo("Room"))
                .body("data.destination", equalTo("NetworkDevice"))
                .body("data.cardinality", equalTo("1:N"))
                .body("data.inline", equalTo(true))
                .body("data.defaultClosed", equalTo(false))
                .statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).when().delete(buildRestV3Url("domains/" + jsonAsMap.get("name")))
                .then().statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).when().get(buildRestV3Url("domains/" + jsonAsMap.get("name")))
                .then().statusCode(404);
    }

    @Test
    public void testPostPutDeleteDomain() {
        String token = getSessionToken();
        Map<String, Object> jsonAsMap = createDomain();

        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).contentType("application/json").body(jsonAsMap).when()
                .post(buildRestV3Url("domains/")).then().statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).when().get(buildRestV3Url("domains/" + jsonAsMap.get("name")))
                .then().body("data.description", equalTo("Server room")).body("data.source", equalTo("Room"))
                .body("data.destination", equalTo("NetworkDevice")).body("data.cardinality", equalTo("1:N"))
                .statusCode(200);

        jsonAsMap.put("description", "Server office");

        given().header(CMDBUILD_AUTH_HEADER, token).when().contentType("application/json").body(jsonAsMap)
                .put(buildRestV3Url("domains/" + jsonAsMap.get("name"))).then().statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).when().get(buildRestV3Url("domains/" + jsonAsMap.get("name")))
                .then().body("data.description", equalTo("Server office")).statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).when().delete(buildRestV3Url("domains/" + jsonAsMap.get("name")))
                .then().statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).when().get(buildRestV3Url("domains/" + jsonAsMap.get("name")))
                .then().statusCode(404);
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

    public Map<String, Object> createDomain() {
        return map(
                "source", "Room",
                "name", "RoomNetworkDevice",
                "description", "Server room",
                "destination", "NetworkDevice",
                "cardinality", "1:N",
                "descriptionDirect", "contains network devices",
                "descriptionInverse", "located in room",
                "indexDirect", -1,
                "indexInverse", -1,
                "descriptionMasterDetail", "Network Devices",
                "filterMasterDetail", null,
                "active", true,
                "isMasterDetail", true,
                "inline", true,
                "defaultClosed", false,
                "disabledSourceDescendants", emptyList(),
                "disabledDestinationDescendants", emptyList()
        );
    }

}
