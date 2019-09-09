package org.cmdbuild.test.rest;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.cmdbuild.test.framework.CmTestRunner;
import org.cmdbuild.test.framework.Context;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.tuid;
import org.cmdbuild.test.rest.utils.AbstractWsIT;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.cmdbuild.test.rest.TestContextProviders.TC_R2U;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

@RunWith(CmTestRunner.class)
@Context(TC_R2U)
public class ViewCardRestIT extends AbstractWsIT {

    @Test
    public void getCardsByViewId() {
        String token = getSessionToken();
        Map<String, Object> jsonAsMap = createView();

        Object viewId = given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(jsonAsMap).when()
                .post(buildRestV3Url("views/")).then().statusCode(200).extract().path("data._id");

        given().header(CMDBUILD_AUTH_HEADER, token).get(buildRestV3Url("views/" + viewId + "/cards")).then()
                .statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).when().delete(buildRestV3Url("views/" + viewId)).then()
                .statusCode(200);
    }

    @Test
    public void getCardById() {
        String token = getSessionToken();
        Map<String, Object> jsonAsMap = createView();

        Object viewId = given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(jsonAsMap).when()
                .post(buildRestV3Url("views/")).then().statusCode(200).extract().path("data._id");

        List<Object> allCardsIds = given().header(CMDBUILD_AUTH_HEADER, getSessionToken())
                .get(buildRestV3Url("views/" + viewId + "/cards")).then().statusCode(200).extract().jsonPath()
                .getList("data._id");

        Object random = allCardsIds.get(new Random().nextInt(allCardsIds.size()));

        given().header(CMDBUILD_AUTH_HEADER, getSessionToken())
                .get(buildRestV3Url("views/" + viewId + "/cards/" + random)).then().statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).when().delete(buildRestV3Url("views/" + viewId)).then()
                .statusCode(200);
    }

    @Test
    public void testLimit() {
        String token = getSessionToken();
        Map<String, Object> jsonAsMap = createView();
        int limit = 3;

        Object viewId = given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(jsonAsMap).when()
                .post(buildRestV3Url("views/")).then().statusCode(200).extract().path("data._id");

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("limit", limit).when()
                .get(buildRestV3Url("views/" + viewId + "/cards/")).then()
                .body("data.size()", equalTo(limit)).statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).when().delete(buildRestV3Url("views/" + viewId)).then()
                .statusCode(200);
    }

    @Test
    public void testStart() {
        String token = getSessionToken();
        Map<String, Object> jsonAsMap = createView();
        int start = 2;

        Object viewId = given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(jsonAsMap).when()
                .post(buildRestV3Url("views/")).then().statusCode(200).extract().path("data._id");

        int total = given().header(CMDBUILD_AUTH_HEADER, token).get(buildRestV3Url("views/" + viewId + "/cards")).then()
                .extract().jsonPath().getInt("data.size()");

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("start", start).when()
                .get(buildRestV3Url("views/" + viewId + "/cards")).then().body("data.size()", equalTo(total - start))
                .statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).when().delete(buildRestV3Url("views/" + viewId)).then()
                .statusCode(200);
    }

    @Test
    public void testSort() {
        String token = getSessionToken();
        Map<String, Object> jsonAsMap = createView();

        JSONArray jArray = new JSONArray();
        JSONObject json = new JSONObject();
        json.put("property", "_id");
        json.put("direction", "ASC");
        jArray.put(json);

        Object viewId = given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(jsonAsMap).when()
                .post(buildRestV3Url("views/")).then().statusCode(200).extract().path("data._id");

        List<Object> unSortedList = given().header(CMDBUILD_AUTH_HEADER, token)
                .get(buildRestV3Url("views/" + viewId + "/cards")).then().extract().jsonPath().getList("data._id");

        List<Object> sortedListASC = given().header(CMDBUILD_AUTH_HEADER, token).queryParam("sort", jArray)
                .get(buildRestV3Url("views/" + viewId + "/cards")).then().extract().jsonPath().getList("data._id");

        json.put("direction", "DESC");
        jArray.put(json);

        List<Object> sortedListDESC = given().header(CMDBUILD_AUTH_HEADER, token).queryParam("sort", jArray)
                .get(buildRestV3Url("views/" + viewId + "/cards")).then().extract().jsonPath().getList("data._id");

        given().header(CMDBUILD_AUTH_HEADER, token).when().delete(buildRestV3Url("views/" + viewId)).then()
                .statusCode(200);

        logger.info("Lista disordinata: {}", unSortedList);
        logger.info("Lista ordinata (crescente): {}", sortedListASC);
        logger.info("Lista ordinata (decrescente): {}", sortedListDESC);
    }

    @Test
    public void testFilter() {
        String token = getSessionToken();
        Map<String, Object> jsonAsMap = createView();

        String attribute = "LastName";
        String operator = "equal";
        String[] codes = {"Brooke"};

        JSONObject json = createJson(attribute, operator, codes);

        logger.info("Query: {}", json);

        Object viewId = given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(jsonAsMap).when()
                .post(buildRestV3Url("views/")).then().statusCode(200).extract().path("data._id");

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("filter", json).when()
                .get(buildRestV3Url("views/" + viewId + "/cards")).then()
                .body("data.LastName", everyItem(equalTo("Brooke"))).statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).when().delete(buildRestV3Url("views/" + viewId)).then()
                .statusCode(200);
    }

    @Test
    public void testPositionOf() {
        String token = getSessionToken();
        Map<String, Object> jsonAsMap = createView();

        Object viewId = given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(jsonAsMap).when()
                .post(buildRestV3Url("views/")).then().statusCode(200).extract().path("data._id");

        List<Object> allCardsIds = given().header(CMDBUILD_AUTH_HEADER, getSessionToken())
                .get(buildRestV3Url("views/" + viewId + "/cards")).then().statusCode(200).extract().jsonPath()
                .getList("data._id");

        Object random = allCardsIds.get(new Random().nextInt(allCardsIds.size()));

        logger.info("" + random);

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("start", 1).queryParam("limit", 3)
                .queryParam("positionOf", random).when().get(buildRestV3Url("views/" + viewId + "/cards")).then()
                .statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).when().delete(buildRestV3Url("views/" + viewId)).then()
                .statusCode(200);
    }

    @Test
    public void testPostDeleteCard() {
        String token = getSessionToken();
        Map<String, Object> jsonAsMap = createView();
        Map<String, Object> jsonAsMap2 = createCard();

        Object viewId = given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(jsonAsMap).when()
                .post(buildRestV3Url("views/")).then().statusCode(200).extract().path("data._id");

        Object cardId = given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(jsonAsMap2)
                .when().post(buildRestV3Url("views/" + viewId + "/cards")).then().statusCode(200).extract()
                .path("data._id");

        given().header(CMDBUILD_AUTH_HEADER, token).when().get(buildRestV3Url("views/" + viewId + "/cards/" + cardId))
                .then().body("data.FirstName", equalTo("Walter")).body("data.LastName", equalTo("White"))
                .body("data.Code", equalTo("w.white")).statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).when()
                .delete(buildRestV3Url("views/" + viewId + "/cards/" + cardId)).then().statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).when().delete(buildRestV3Url("views/" + viewId)).then()
                .statusCode(200);
    }

    @Test
    public void testPostPutDeleteCard() {
        String token = getSessionToken();
        Map<String, Object> jsonAsMap = createView();
        Map<String, Object> jsonAsMap2 = createCard();

        Object viewId = given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).contentType("application/json")
                .body(jsonAsMap).when().post(buildRestV3Url("views/")).then().statusCode(200).extract()
                .path("data._id");

        Object cardId = given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(jsonAsMap2)
                .when().post(buildRestV3Url("views/" + viewId + "/cards")).then().statusCode(200).extract()
                .path("data._id");

        given().header(CMDBUILD_AUTH_HEADER, token).when().get(buildRestV3Url("views/" + viewId + "/cards/" + cardId))
                .then().body("data.FirstName", equalTo("Walter")).body("data.LastName", equalTo("White"))
                .body("data.Code", equalTo("w.white")).statusCode(200);

        jsonAsMap2.put("LastName", "Rossi");
        jsonAsMap2.put("FirstName", "Mario");

        given().header(CMDBUILD_AUTH_HEADER, token).when().contentType("application/json").body(jsonAsMap2)
                .put(buildRestV3Url("views/" + viewId + "/cards/" + cardId)).then().statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).when().get(buildRestV3Url("views/" + viewId + "/cards/" + cardId)).then()
                .body("data.LastName", equalTo("Rossi")).body("data.FirstName", equalTo("Mario")).statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).when()
                .delete(buildRestV3Url("views/" + viewId + "/cards/" + cardId)).then().statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).when().delete(buildRestV3Url("views/" + viewId)).then()
                .statusCode(200);
    }

    private Map<String, Object> createView() {
        return map("type", "filter",
                "name", tuid("InternalEmployee"),
                "sourceClassName", "InternalEmployee",
                "description", "employee",
                "filter", false);
    }

    private Map<String, Object> createCard() {
        return map("Code", "w.white",
                "Description", "Walter White",
                "LastName", "White",
                "FirstName", "Walter",
                "Email", "white.walter@example.com",
                "State", 108,
                "Phone", "4534635",
                "Mobile", "565479");
    }

    private JSONObject createJson(String attribute, String operator, Object value) {
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
