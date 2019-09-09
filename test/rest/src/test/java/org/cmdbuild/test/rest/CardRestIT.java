package org.cmdbuild.test.rest;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.Matchers.endsWith;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cmdbuild.test.framework.CmTestRunner;
import org.cmdbuild.test.framework.Context;
import org.cmdbuild.test.rest.utils.AbstractWsIT;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.cmdbuild.test.rest.TestContextProviders.TC_R2U;

@RunWith(CmTestRunner.class)
@Context(TC_R2U)
public class CardRestIT extends AbstractWsIT {

    @Test
    public void testCards() {
        List<String> allClasses = given().header(CMDBUILD_AUTH_HEADER, getSessionToken())
                .get(buildRestV3Url("classes/")).then().extract().jsonPath().getList("data._id");

        allClasses.forEach((string) -> {
            given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("classes/" + string + "/cards"))
                    .then().statusCode(200);
//			logger.debug(string);
        });
    }

    @Test
    public void testCardDetail() {
        String classid = "Building";
        Long cardId = 5960L;

        given().header(CMDBUILD_AUTH_HEADER, getSessionToken())
                .get(buildRestV3Url("classes/" + classid + "/cards/" + cardId)).then().body("data.Code", equalTo("LMT"))
                .body("data.Description", equalTo("Legg Mason Tower")).body("data.Address", equalTo("100 Main Street"))
                .body("data.City", equalTo("Baltimora")).statusCode(200);
    }

    @Test
    public void testPostDeleteEmployee() {
        String _id = "InternalEmployee";
        String token = getSessionToken();
        Object cardId = createEmployee(token, _id);

//		logger.debug("{}", cardId);
        given().header(CMDBUILD_AUTH_HEADER, token).when().get(buildRestV3Url("classes/" + _id + "/cards/" + cardId))
                .then().body("data.Description", equalTo("Blue Robert")).body("data.Code", equalTo("r.blue"))
                .body("data.Email", equalTo("robert.blue@example.com")).statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).when().delete(buildRestV3Url("classes/" + _id + "/cards/" + cardId))
                .then().statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).when().get(buildRestV3Url("classes/" + _id + "/cards/" + cardId))
                .then().statusCode(404);

    }

    @Test
    public void testCardPostPutDelete() {
        String token = getSessionToken();
        String _id = "ExternalEmployee";
        Object cardId = createEmployee(token, _id);

//		logger.debug("{}", cardId);
        given().header(CMDBUILD_AUTH_HEADER, token).when().get(buildRestV3Url("classes/" + _id + "/cards/" + cardId))
                .then().body("data.Code", equalTo("r.blue")).body("data.Email", equalTo("robert.blue@example.com"))
                .body("data.FirstName", equalTo("Robert")).body("data.Phone", equalTo("65432"))
                .body("data.LastName", equalTo("Blue")).statusCode(200);

        updateEmployee(token, cardId);

        given().header(CMDBUILD_AUTH_HEADER, token).when().get(buildRestV3Url("classes/" + _id + "/cards/" + cardId))
                .then().body("data.Code", equalTo("r.grigi")).body("data.Email", equalTo("roberto.grigi@example.com"))
                .body("data.FirstName", equalTo("Roberto")).body("data.Phone", equalTo("6765756"))
                .body("data.LastName", equalTo("Grigi")).statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).when().delete(buildRestV3Url("classes/" + _id + "/cards/" + cardId))
                .then().statusCode(200);
    }

    @Test
    public void testPostDeleteBuilding() {
        String _id = "Building";
        String token = getSessionToken();
        Object cardId = createBuilding(token);
//		logger.debug("{}", cardId);

        given().header(CMDBUILD_AUTH_HEADER, token).when().get(buildRestV3Url("classes/" + _id + "/cards/" + cardId))
                .then().body("data.Address", equalTo("Main Streen 77")).body("data.City", equalTo("Los Angeles"))
                .body("data.Code", equalTo("AB")).body("data.Country", equalTo(5955))
                .body("data.Description", equalTo("Wilshire Grand Center")).body("data.Postcode", equalTo("58213"))
                .statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).when().delete(buildRestV3Url("classes/" + _id + "/cards/" + cardId))
                .then().statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).when().get(buildRestV3Url("classes/" + _id + "/cards/" + cardId))
                .then().statusCode(404);
    }

    @Test
    public void testPrototype() {
        List<String> allClasses = given().header(CMDBUILD_AUTH_HEADER, getSessionToken())
                .get(buildRestV3Url("classes/")).then().extract().jsonPath()
                .getList("data.findAll{ it.prototype == true}._id ");

        for (String string : allClasses) {
            given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("classes/" + string + "/cards"))
                    .then().assertThat().statusCode(200);
//			logger.debug(string);
        }
    }

    @Test
    public void testPostDeleteFloor() {
        String _id = "Floor";
        String token = getSessionToken();
        Map<String, Object> jsonAsMap = new HashMap<>();

        jsonAsMap.put("Building", 5960);
        jsonAsMap.put("Code", "AB67");
        jsonAsMap.put("Level", "125");

        Object cardId = given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(jsonAsMap).when()
                .post(buildRestV3Url("classes/" + _id + "/cards")).then().statusCode(200).extract().path("data._id");

        given().header(CMDBUILD_AUTH_HEADER, token).when().get(buildRestV3Url("classes/" + _id + "/cards/" + cardId))
                .then().body("data.Building", equalTo(5960)).body("data.Code", equalTo("AB67")).statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).when().delete(buildRestV3Url("classes/" + _id + "/cards/" + cardId))
                .then().statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).when().get(buildRestV3Url("classes/" + _id + "/cards/" + cardId))
                .then().statusCode(404);

    }

    @Test
    public void testPostDeleteVpn() {
        String _id = "VPN";
        String token = getSessionToken();
        Map<String, Object> jsonAsMap = new HashMap<>();

        jsonAsMap.put("Code", "VPN0454");
        jsonAsMap.put("Netmask", "255.255.255.0");
        jsonAsMap.put("Subnet", "192.168.1.45");
        jsonAsMap.put("Name", "VpnMario");

        Object cardId = given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(jsonAsMap).when()
                .post(buildRestV3Url("classes/" + _id + "/cards")).then().statusCode(200).extract().path("data._id");

        given().header(CMDBUILD_AUTH_HEADER, token).when().get(buildRestV3Url("classes/" + _id + "/cards/" + cardId))
                .then().body("data.Code", equalTo("VPN0454")).body("data.Code", equalTo("VPN0454"))
                .body("data.Netmask", equalTo("255.255.255.0")).statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).when().delete(buildRestV3Url("classes/" + _id + "/cards/" + cardId))
                .then().statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).when().get(buildRestV3Url("classes/" + _id + "/cards/" + cardId))
                .then().statusCode(404);
    }

    @Test
    public void testIPFieldVPn() {
        String _id = "VPN";
        String token = getSessionToken();
        Map<String, Object> jsonAsMap = new HashMap<>();

        jsonAsMap.put("Code", "VPN0454");
        jsonAsMap.put("Netmask", "2552552550");
        jsonAsMap.put("Subnet", "192.168.1.45");
        jsonAsMap.put("Name", "VpnMario");

        given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(jsonAsMap).when()
                .post(buildRestV3Url("classes/" + _id + "/cards")).then().statusCode(500);

        jsonAsMap.put("Netmask", "255.255.255.0"); // indirizzo IP valido

        Object cardId = given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(jsonAsMap).when()
                .post(buildRestV3Url("classes/" + _id + "/cards")).then().statusCode(200).extract().path("data._id");

        given().header(CMDBUILD_AUTH_HEADER, token).when().delete(buildRestV3Url("classes/" + _id + "/cards/" + cardId))
                .then().statusCode(200);
    }

    @Test
    public void testTotal() {
        String _id = "Supplier";
        String token = getSessionToken();
        int totalValue = given().header(CMDBUILD_AUTH_HEADER, token).get(buildRestV3Url("classes/" + _id + "/cards"))
                .then().extract().jsonPath().getInt("meta.total");

        logger.debug("Numero di oggetti: {}", totalValue);

        given().header(CMDBUILD_AUTH_HEADER, token).get(buildRestV3Url("classes/" + _id + "/cards")).then()
                .body("data.size()", equalTo(totalValue)).statusCode(200);
    }

    @Test
    public void testTypeClass() {
        String token = getSessionToken();
        String _id = "Printer";

        List<Integer> allCards = given().header(CMDBUILD_AUTH_HEADER, token)
                .get(buildRestV3Url("classes/" + _id + "/cards")).then().extract().jsonPath().getList("data._id");

        logger.debug("Ci sono " + allCards.size() + " elementi");

        allCards.forEach((a) -> {
            given().header(CMDBUILD_AUTH_HEADER, token).get(buildRestV3Url("classes/" + _id + "/cards/" + a)).then()
                    .body("data._type", equalTo(_id)).statusCode(200);
        });

    }

    @Test
    public void testStartLimitSort() {
        String token = getSessionToken();
        String name = "Employee";
        int start = 0;
        int limit = 7;

        JSONArray jArray = new JSONArray();
        JSONObject json = new JSONObject();
        json.put("property", "Code");
        json.put("direction", "ASC");
        jArray.put(json);

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("start", start).queryParam("limit", limit)
                .queryParam("sort", jArray).when().get(buildRestV3Url("classes/" + name + "/cards")).then()
                .statusCode(200);
    }

    @Test
    public void testCardsSize() {
        List<String> allClasses = given().header(CMDBUILD_AUTH_HEADER, getSessionToken())
                .get(buildRestV3Url("classes/")).then().extract().jsonPath().getList("data._id");

        allClasses.forEach((string) -> {
            int v = given().header(CMDBUILD_AUTH_HEADER, getSessionToken())
                    .get(buildRestV3Url("classes/" + string + "/cards")).then().extract().jsonPath()
                    .getInt("data.size()");
            logger.debug(string + ": " + v);
        });
    }

    @Test
    public void testSort() {
        String token = getSessionToken();
        String name = "Room";

        JSONArray jArray = new JSONArray();
        JSONObject json = new JSONObject();
        json.put("property", "Code");
        json.put("direction", "ASC");
        jArray.put(json);

        List<Integer> unSortedList = given().header(CMDBUILD_AUTH_HEADER, token)
                .get(buildRestV3Url("classes/" + name + "/cards")).then().extract().jsonPath().getList("data.Code");

        List<Integer> sortedListASC = given().header(CMDBUILD_AUTH_HEADER, token).queryParam("sort", jArray)
                .get(buildRestV3Url("classes/" + name + "/cards")).then().extract().jsonPath().getList("data.Code");

        json.put("direction", "DESC");
        jArray.put(json);

        List<Integer> sortedListDESC = given().header(CMDBUILD_AUTH_HEADER, token).queryParam("sort", jArray)
                .get(buildRestV3Url("classes/" + name + "/cards")).then().extract().jsonPath().getList("data.Code");

        logger.debug("Lista disordinata: {}", unSortedList);
        logger.debug("Lista ordinata (crescente): {}", sortedListASC);//TODO check that it is really sorted
        logger.debug("Lista ordinata (decrescente): {}", sortedListDESC);//TODO check that it is really sorted
    }

    @Test
    public void testStart() {
        String token = getSessionToken();
        String name = "Monitor";
        int start = 2;

        int total = given().header(CMDBUILD_AUTH_HEADER, token).get(buildRestV3Url("classes/" + name + "/cards")).then()
                .extract().jsonPath().getInt("data.size()");

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("start", start).when()
                .get(buildRestV3Url("classes/" + name + "/cards")).then().body("data.size()", equalTo(total - start))
                .statusCode(200);

    }

    @Test
    public void testLimit() {
        String token = getSessionToken();
        String name = "Class";
        int limit = 3;

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("limit", limit).when()
                .get(buildRestV3Url("classes/" + name + "/cards")).then().body("data.size()", equalTo(limit))
                .statusCode(200);
    }

    @Test
    public void testFilter() {
        String token = getSessionToken();
        String name = "Employee";

        String attribute = "Code";
        String operator = "equal";
        String[] codes = {"j.gray"};

        JSONObject json = createJsonFilter(attribute, operator, codes);

        logger.debug("Query: {}", json);

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("filter", json).when()
                .get(buildRestV3Url("classes/" + name + "/cards")).then().body("data.Code", hasItem("j.gray"))
                .statusCode(200);
    }

    @Test
    public void testFilterAnd() {
        String token = getSessionToken();

        String name = "Employee";
        String[] codes = {"t.smith"};
        String[] numbers = {"IE0210"};
        JSONObject json = createFilterAndJson(codes, numbers);

        logger.debug("Query: {}", json);

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("filter", json).when()
                .get(buildRestV3Url("classes/" + name + "/cards")).then().body("data.LastName", hasItem("Smith"))
                .body("data.FirstName", hasItem("Tom")).statusCode(200);

    }

    @Test
    public void testFilterAndOr() {
        String token = getSessionToken();
        String name = "Computer";

        String[] codes = {"729232-09"};
        String[] models = {"X554LD-XX498H"};
        int[] ram = {8};
        int[] hdSize = {250};
        JSONObject json = createFilterAndOrJson(codes, models, ram, hdSize);

        logger.debug("Query: {}", json);

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("filter", json).when()
                .get(buildRestV3Url("classes/" + name + "/cards")).then().body("data.Code", hasItem("56843-03"))
                .body("data.Hostname", hasItem("pc-ccolding")).body("data.Model", hasItem("X554LD-XX498H"))
                .body("data.RAM", hasItem(8)).body("data.HDSize", hasItem(500)).statusCode(200);
    }

    @Test
    public void testFilterNotEqual() {
        String token = getSessionToken();
        String name = "NetworkDevice";

        LocalDate data = LocalDate.of(2011, Month.SEPTEMBER, 13);

        String attribute = "AcceptanceDate";
        String operator = "notequal";
        String[] date = {data.toString()};

        JSONObject json = createJsonFilter(attribute, operator, date);

        logger.debug("Query 1: {}", json);

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("filter", json).when()
                .get(buildRestV3Url("classes/" + name + "/cards")).then()
                .body("data.AcceptanceDate", not(hasItem(date[0]))).statusCode(200);

        String attribute2 = "Assignee";
        String operator2 = "notequal";
        int[] assignee = {118};

        JSONObject json2 = createJsonFilter(attribute2, operator2, assignee);

        logger.debug("Query 2: {}", json2);

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("filter", json2).when()
                .get(buildRestV3Url("classes/" + name + "/cards")).then()
                .body("data.Assignee", not(hasItem(assignee[0]))).statusCode(200);

        String attribute3 = "SerialNumber";
        String operator3 = "notequal";
        String[] serialNumber = {"YFGE87"};

        JSONObject json3 = createJsonFilter(attribute3, operator3, serialNumber);

        logger.debug("Query 3: {}", json3);

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("filter", json3).when()
                .get(buildRestV3Url("classes/" + name + "/cards")).then()
                .body("data.SerialNumber", not(hasItem(serialNumber[0]))).statusCode(200);
    }

    @Test
    public void testFilterNull() {
        String token = getSessionToken();
        String name = "Floor";

        String attribute = "Notes";
        String operator = "isnull";
        String[] emptyArr = {};

        JSONObject json = createJsonFilter(attribute, operator, emptyArr);

        logger.debug("Query: {}", json);

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("filter", json).when()
                .get(buildRestV3Url("classes/" + name + "/cards")).then().body("data.Notes", everyItem(nullValue()))
                .statusCode(200);
    }

    @Test
    public void testFilterNotNull() {
        String token = getSessionToken();
        String name = "Floor";

        String attribute = "Notes";
        String operator = "isnotnull";
        String[] emptyArr = {};

        JSONObject json = createJsonFilter(attribute, operator, emptyArr);

        logger.debug("Query: {}", json);

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("filter", json).when()
                .get(buildRestV3Url("classes/" + name + "/cards")).then()
                .body("data.Notes", everyItem(not(nullValue()))).statusCode(200);
    }

    @Test
    public void testFilterIn() {
        String token = getSessionToken();
        String name = "Employee";

        String attribute = "Email";
        String operator = "in";
        String[] emails = {"m.brooke@example.com", "c.colding@example.com"};

        JSONObject json = createJsonFilter(attribute, operator, emails);

        logger.debug("Query 1: {}", json);

        for (String email : emails) {
            given().header(CMDBUILD_AUTH_HEADER, token).queryParam("filter", json).when()
                    .get(buildRestV3Url("classes/" + name + "/cards")).then().body("data.Code", hasItem("m.brooke"))
                    .body("data.Code", hasItem("c.colding")).body("data.Email", hasItem(email)).statusCode(200);
        }
    }

    @Test
    public void testFilterLike() {
        String token = getSessionToken();
        String name = "Room";

        String attribute = "Description";
        String operator = "like";
        String[] desc = {"Data Center"};

        JSONObject json = createJsonFilter(attribute, operator, desc);

        logger.debug("Query : {}", json);

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("filter", json).when()
                .get(buildRestV3Url("classes/" + name + "/cards")).then()
                .body("data.Description", everyItem(containsString(desc[0]))).statusCode(200);
    }

    @Test
    public void testFilterContain() {
        String token = getSessionToken();
        String name = "Room";

        String attribute = "Description";
        String operator = "contain";
        String[] desc = {"Data Center"};

        JSONObject json = createJsonFilter(attribute, operator, desc);

        logger.debug("Query : {}", json);

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("filter", json).when()
                .get(buildRestV3Url("classes/" + name + "/cards")).then()
                .body("data.Description", everyItem(containsString(desc[0]))).statusCode(200);
    }

    @Test
    public void testFilterNotContain() {
        String token = getSessionToken();
        String name = "Room";

        String attribute = "Description";
        String operator = "notcontain";
        String[] desc = {"dddd"};

        JSONObject json = createJsonFilter(attribute, operator, desc);

        logger.debug("Query : {}", json);

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("filter", json).when()
                .get(buildRestV3Url("classes/" + name + "/cards")).then()
                .body("data.Description", everyItem(not(containsString(desc[0])))).statusCode(200);
    }

    @Test
    public void testFilterBegin() {
        String token = getSessionToken();
        String name = "Room";

        String attribute = "Description";
        String operator = "begin";
        String[] desc = {"Off"};

        JSONObject json = createJsonFilter(attribute, operator, desc);

        logger.debug("Query: {}", json);

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("filter", json).when()
                .get(buildRestV3Url("classes/" + name + "/cards")).then()
                .body("data.Description", everyItem(containsString(desc[0]))).statusCode(200);
    }

    @Test
    public void testFilterNotBegin() {
        String token = getSessionToken();
        String name = "Employee";

        String attribute = "LastName";
        String operator = "notbegin";
        String[] desc = {"De"};

        JSONObject json = createJsonFilter(attribute, operator, desc);

        logger.debug("Query: {}", json);

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("filter", json).when()
                .get(buildRestV3Url("classes/" + name + "/cards")).then()
                .body("data.Surname", everyItem(not(startsWith(desc[0])))).statusCode(200);
    }

    @Test
    public void testFilterEnd() {
        String token = getSessionToken();
        String name = "Employee";

        String attribute = "Email";
        String operator = "end";
        String[] email = {".com"};

        JSONObject json = createJsonFilter(attribute, operator, email);

        logger.debug("Query: {}", json);

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("filter", json).when()
                .get(buildRestV3Url("classes/" + name + "/cards")).then()
                .body("data.Email", everyItem(endsWith(email[0]))).statusCode(200);
    }

    @Test
    public void testFilterNotEnd() {
        String token = getSessionToken();
        String name = "Employee";

        String attribute = "Phone";
        String operator = "notend";
        String[] phone = {"564"};

        JSONObject json = createJsonFilter(attribute, operator, phone);

        logger.debug("Query: {}", json);

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("filter", json).when()
                .get(buildRestV3Url("classes/" + name + "/cards")).then()
                .body("data.Phone", everyItem(not(endsWith(phone[0])))).statusCode(200);
    }

    public JSONObject createJsonFilter(String attribute, String operator, Object value) {
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

    public JSONObject createFilterAndJson(Object codes, Object numbers) {
        JSONObject json = new JSONObject();
        json.put("attribute", "Code");
        json.put("operator", "equal");
        json.put("value", codes);

        JSONObject json2 = new JSONObject();
        json2.put("attribute", "Number");
        json2.put("operator", "equal");
        json2.put("value", numbers);

        JSONObject jsonsimple1 = new JSONObject();
        jsonsimple1.put("simple", json);

        JSONObject jsonsimple2 = new JSONObject();
        jsonsimple2.put("simple", json2);

        JSONArray and = new JSONArray();
        and.put(jsonsimple1);
        and.put(jsonsimple2);

        JSONObject json3 = new JSONObject();
        json3.put("and", and);

        JSONObject jsontotal = new JSONObject();
        jsontotal.put("attribute", json3);
        return jsontotal;
    }

    public JSONObject createFilterAndOrJson(String[] codes, String[] models, int[] ram, int[] lessThan) {
        JSONObject json = new JSONObject();
        json.put("attribute", "Code");
        json.put("operator", "equal");
        json.put("value", codes);

        JSONObject json2 = new JSONObject();
        json2.put("attribute", "Model");
        json2.put("operator", "equal");
        json2.put("value", models);

        JSONObject json3 = new JSONObject();
        json3.put("attribute", "RAM");
        json3.put("operator", "equal");
        json3.put("value", ram);

        JSONObject json4 = new JSONObject();
        json4.put("attribute", "HDSize");
        json4.put("operator", "equal");
        json4.put("value", lessThan);

        JSONObject jsonsimple1 = new JSONObject();
        jsonsimple1.put("simple", json);

        JSONObject jsonsimple2 = new JSONObject();
        jsonsimple2.put("simple", json2);

        JSONObject jsonsimple3 = new JSONObject();
        jsonsimple3.put("simple", json3);

        JSONObject jsonsimple4 = new JSONObject();
        jsonsimple4.put("simple", json4);

        JSONArray or1 = new JSONArray();
        or1.put(jsonsimple1);
        or1.put(jsonsimple2);

        JSONObject jsonOr1 = new JSONObject();
        jsonOr1.put("or", or1);

        JSONArray or2 = new JSONArray();
        or2.put(jsonsimple3);
        or2.put(jsonsimple4);

        JSONObject jsonOr2 = new JSONObject();
        jsonOr2.put("or", or2);

        JSONArray and = new JSONArray();
        and.put(jsonOr1);
        and.put(jsonOr2);

        JSONObject jsonAnd = new JSONObject();
        jsonAnd.put("and", and);

        JSONObject jsonTotal = new JSONObject();
        jsonTotal.put("attribute", jsonAnd);
        return jsonTotal;
    }

    public Object createEmployee(String token, String employeeType) {
        Map<String, Object> jsonAsMap = new HashMap<>();

        jsonAsMap.put("Code", "r.blue");
        jsonAsMap.put("Email", "robert.blue@example.com");
        jsonAsMap.put("Description", "Robert Blue");
        jsonAsMap.put("Mobile", "6886794");
        jsonAsMap.put("FirstName", "Robert");
        jsonAsMap.put("Phone", "65432");
        jsonAsMap.put("State", 108);
        jsonAsMap.put("LastName", "Blue");

        Object id = given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(jsonAsMap).when()
                .post(buildRestV3Url("classes/" + employeeType + "/cards")).then().statusCode(200).extract()
                .path("data._id");

        return id;
    }

    public Object createBuilding(String token) {
        Map<String, Object> jsonAsMap = new HashMap<>();
        jsonAsMap.put("Address", "Main Streen 77");
        jsonAsMap.put("City", "Los Angeles");
        jsonAsMap.put("Code", "AB");
        jsonAsMap.put("Country", 5955);
        jsonAsMap.put("Description", "Wilshire Grand Center");
        jsonAsMap.put("Postcode", "58213");

        Object cardId = given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(jsonAsMap).when()
                .post(buildRestV3Url("classes/Building/cards")).then().statusCode(200).extract().path("data._id");

        return cardId;
    }

    public void updateEmployee(String token, Object cardId) {
        Map<String, Object> updatedFields = new HashMap<>();

        updatedFields.put("Code", "r.grigi");
        updatedFields.put("Email", "roberto.grigi@example.com");
        updatedFields.put("FirstName", "Roberto");
        updatedFields.put("Phone", "6765756");
        updatedFields.put("LastName", "Grigi");

        given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json").body(updatedFields).when()
                .put(buildRestV3Url("classes/ExternalEmployee/cards/" + cardId)).then().statusCode(200);

    }
}
