/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.rest;

import static com.jayway.restassured.RestAssured.given;
import java.util.List;
import org.cmdbuild.client.rest.RestClient;
import org.cmdbuild.test.framework.CmTestRunner;
import org.cmdbuild.test.framework.Context;
import static org.cmdbuild.test.rest.TestContextProviders.TC_R2U;
import org.cmdbuild.test.rest.utils.AbstractWsIT;
import org.json.JSONObject;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CmTestRunner.class)
@Context(TC_R2U)
public class FunctionFiltersRestIT extends AbstractWsIT {

    private RestClient restClient;

    @Before
    public void init() {
        restClient = getRestClient();
    }

    @Test
    public void testFunctionFilter() {
        String token = getSessionToken();
        JSONObject json = createJson("tags", "CONTAIN", "card2value");

        restClient.system().eval(
                "cmdb.getSystemApi().executeQuery(\"CREATE OR REPLACE FUNCTION _test(_card bigint,OUT _value varchar) returns setof varchar AS $$ BEGIN "
                + "    RETURN QUERY select (\\\"Code\\\"||'_test')::varchar from \\\"Class\\\" where \\\"Id\\\" = _card; "
                + "END $$ LANGUAGE PLPGSQL;"
                + "COMMENT ON FUNCTION _test(_card bigint,OUT _value varchar) IS 'TYPE: FUNCTION|TAGS: card2value|SOURCE: Class'\")");
        restClient.system().dropAllCaches();//TODO drop only function cache

        List name = given().header(CMDBUILD_AUTH_HEADER, token)
                .queryParam("filter", json)
                .get(buildRestV3Url("functions"))
                .then().statusCode(200).extract().jsonPath()
                .getList("data.name");

        assertEquals("_test", name.get(0));
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
