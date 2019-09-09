package org.cmdbuild.test.rest;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.containsString;
import java.util.List;
import java.util.Map;

import org.cmdbuild.test.framework.CmTestRunner;
import org.cmdbuild.test.framework.Context;
import org.cmdbuild.test.rest.utils.AbstractWsIT;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.restassured.http.ContentType;
import java.lang.invoke.MethodHandles;
import static java.util.Collections.emptyList;

import static org.cmdbuild.test.framework.UniqueTestIdUtils.prepareTuid;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.tuid;
import static org.cmdbuild.test.rest.TestContextProviders.TC_R2U;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.junit.Before;

@RunWith(CmTestRunner.class)
@Context(TC_R2U)
public class UserRestIT extends AbstractWsIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Before
    public void init() {
        prepareTuid();
    }

    @Test
    public void getUsers() {
        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("users/")).then()
                .body("data.username", hasItem("admin")).statusCode(200);
    }

    @Test
    public void getUserById() {
        String token = getSessionToken();
        int id = (int) given().header(CMDBUILD_AUTH_HEADER, token).get(buildRestV3Url("users")).then().extract()
                .jsonPath().getList("data.findAll{ it.username == 'admin'}._id ").get(0);

        given().header(CMDBUILD_AUTH_HEADER, token).get(buildRestV3Url("users/" + id)).then()
                .body("data.description", equalTo("admin")).body("data.active", equalTo(true)).statusCode(200);
    }

    @Test
    public void testLimit() {
        String token = getSessionToken();

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("limit", 1).get(buildRestV3Url("users")).then()
                .body("data.size()", equalTo(1)).statusCode(200);
    }

    @Test
    public void testStart() {
        String token = getSessionToken();
        int startValue = 3;

        int totalValue = given().header(CMDBUILD_AUTH_HEADER, token).get(buildRestV3Url("users")).then().extract()
                .jsonPath().getInt("meta.total");

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("start", startValue).get(buildRestV3Url("users")).then()
                .body("data.size()", equalTo(totalValue - startValue)).statusCode(200);
    }

    @Test
    public void testSort() {
        String token = getSessionToken();

        JSONArray jArray = new JSONArray();
        JSONObject json = new JSONObject();
        json.put("property", "Id");
        json.put("direction", "ASC");
        jArray.put(json);

        List<Integer> unSortedList = given().header(CMDBUILD_AUTH_HEADER, token).get(buildRestV3Url("users")).then()
                .extract().jsonPath().getList("data._id");

        logger.info("" + unSortedList);

        List<Integer> sortedListASC = given().header(CMDBUILD_AUTH_HEADER, token).queryParam("sort", jArray)
                .get(buildRestV3Url("users")).then().extract().jsonPath().getList("data._id");

        logger.info("" + sortedListASC);

        json.put("direction", "DESC");
        jArray.put(json);

        List<Integer> sortedListDESC = given().header(CMDBUILD_AUTH_HEADER, token).queryParam("sort", jArray)
                .get(buildRestV3Url("users")).then().extract().jsonPath().getList("data._id");

        logger.info("" + sortedListDESC);

        logger.info("Lista disordinata: {}", unSortedList);
        logger.info("Lista ordinata (crescente): {}", sortedListASC);
        logger.info("Lista ordinata (decrescente): {}", sortedListDESC);
    }

    @Test
    public void testFilterEqual() {
        String token = getSessionToken();

        String attribute = "Username";
        String operator = "equal";
        String[] codes = {"admin"};

        JSONObject json = createJson(attribute, operator, codes);

        logger.info("Query: {}", json);

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("filter", json).when().get(buildRestV3Url("users"))
                .then().body("data.username", hasItem("admin")).statusCode(200);
    }

    @Test
    public void testFilterNotEqual() {
        String token = getSessionToken();

        String attribute = "Email";
        String operator = "notequal";
        String[] codes = {"michael.davis@example.com"};

        JSONObject json = createJson(attribute, operator, codes);

        logger.info("Query: {}", json);

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("filter", json).when().get(buildRestV3Url("users"))
                .then().body("data.email", not(hasItem("michael.davis@example.com"))).statusCode(200);

    }

    @Test
    public void testFilterNull() {
        String token = getSessionToken();

        String attribute = "Email";
        String operator = "isnull";
        String[] emptyArr = {};

        JSONObject json = createJson(attribute, operator, emptyArr);

        logger.info("Query: {}", json);

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("filter", json).when().get(buildRestV3Url("users"))
                .then().body("data.email", everyItem(nullValue())).statusCode(200);
    }

    @Test
    public void testFilterNotNull() {
        String token = getSessionToken();

        String attribute = "Email";
        String operator = "isnotnull";
        String[] emptyArr = {};

        JSONObject json = createJson(attribute, operator, emptyArr);

        logger.info("Query: {}", json);

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("filter", json).when().get(buildRestV3Url("users"))
                .then().body("data.email", everyItem(not(nullValue()))).statusCode(200);
    }

    @Test
    public void testFilterLike() {
        String token = getSessionToken();

        String attribute = "Description";
        String operator = "like";
        String[] desc = {"Administrator"};

        JSONObject json = createJson(attribute, operator, desc);

        logger.info("Query : {}", json);

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("filter", json).when().get(buildRestV3Url("users"))
                .then().body("data.description", everyItem(containsString(desc[0]))).statusCode(200);
    }

    @Test
    public void testFilterContain() {
        String token = getSessionToken();

        String attribute = "Description";
        String operator = "contain";
        String[] desc = {"work"};

        JSONObject json = createJson(attribute, operator, desc);

        logger.info("Query : {}", json);

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("filter", json).when().get(buildRestV3Url("users"))
                .then().body("data.description", everyItem(containsString(desc[0]))).statusCode(200);
    }

    @Test
    public void testFilterNotContain() {
        String token = getSessionToken();

        String attribute = "Description";
        String operator = "notcontain";
        String[] desc = {"workflow"};

        JSONObject json = createJson(attribute, operator, desc);

        logger.info("Query : {}", json);

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("filter", json).when().get(buildRestV3Url("users"))
                .then().body("data.description", everyItem(not(containsString(desc[0])))).statusCode(200);
    }

    @Test
    public void testPostPut() {
        String token = getSessionToken();
        Map<String, Object> data = createUser(token, true, false);

        given().header(CMDBUILD_AUTH_HEADER, token).when().get(buildRestV3Url("users/" + data.get("_id"))).then()
                .body("data.username", equalTo(data.get("username")))
                .body("data.description", equalTo("White Walter")).body("data.email", equalTo(data.get("email")))
                .body("data.active", equalTo(true)).body("data.service", equalTo(false)).statusCode(200);

        data.put("description", "White Walter modificato");

        given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(data).when()
                .put(buildRestV3Url("users/" + data.get("_id"))).then().statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).when().get(buildRestV3Url("users/" + data.get("_id"))).then()
                .body("data.description", equalTo("White Walter modificato")).statusCode(200);
    }

    @Test
    public void testLoginAdminUser() {
        given().contentType(ContentType.JSON).body("{\"username\" : \"admin\", \"password\" : \"admin\"}")
                .post(buildRestV3Url("sessions?scope=ui")).then().statusCode(200);
    }

    @Test
    public void testLoginNewUser() {
        String token = getSessionToken();
        Map<String, Object> data = createUser(token, true, false);

        JSONObject json = new JSONObject();
        json.put("username", data.get("username"));
        json.put("password", data.get("password"));

        given().contentType(ContentType.JSON).body(json).post(buildRestV3Url("sessions?scope=ui")).then().statusCode(200);
    }

    @Test
    public void testLoginNewUserServiceTrue() {
        String token = getSessionToken();
        Map<String, Object> data = createUser(token, true, true);

        JSONObject json = new JSONObject();
        json.put("username", data.get("username"));
        json.put("password", data.get("password"));

        given().contentType(ContentType.JSON).body(json).post(buildRestV3Url("sessions?scope=ui"))
                .then().statusCode(not(200));
    }

    @Test
    public void testLoginNewUserServiceTrueAsService() {
        String token = getSessionToken();
        Map<String, Object> data = createUser(token, true, true);

        JSONObject json = new JSONObject();
        json.put("username", data.get("username"));
        json.put("password", data.get("password"));

        given().contentType(ContentType.JSON).body(json).post(buildRestV3Url("sessions?scope=service"))
                .then().statusCode(200);
    }

    @Test
    public void testLoginNewUserActiveFalse() {
        String token = getSessionToken();
        Map<String, Object> data = createUser(token, false, false);

        JSONObject json = new JSONObject();
        json.put("username", data.get("username"));
        json.put("password", data.get("password"));

        given().contentType(ContentType.JSON).body(json).post(buildRestV3Url("sessions?scope=ui")).then().statusCode(not(200));
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

    public Map<String, Object> createUser(String token, boolean active, boolean service) {
        String username = tuid("wwhite");
        Map<String, Object> data = map(
                "username", username,
                "description", "White Walter",
                "email", tuid("walter.white") + "@example.com",
                "active", active,
                "service", service,
                "password", "wwhite",
                "userGroups", emptyList());

        Object userId = given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(data).when()
                .post(buildRestV3Url("users")).then().statusCode(200).extract().path("data._id");
        data.put("_id", userId);

        Object roleId = given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(map("name", tuid(username + "_group"))).when().post(buildRestV3Url("roles")).then().statusCode(200)
                .extract().path("data._id");

        given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(map("add", list(userId), "remove", emptyList())).when().post(buildRestV3Url("roles/" + roleId + "/users")).then().statusCode(200);

        return data;
    }

}
