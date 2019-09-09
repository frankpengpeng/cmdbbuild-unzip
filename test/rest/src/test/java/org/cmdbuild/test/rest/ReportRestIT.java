package org.cmdbuild.test.rest;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import org.cmdbuild.test.framework.CmTestRunner;
import org.cmdbuild.test.framework.Context;
import org.cmdbuild.test.rest.utils.AbstractWsIT;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.cmdbuild.test.rest.TestContextProviders.TC_R2U;

@RunWith(CmTestRunner.class)
@Context(TC_R2U)
public class ReportRestIT extends AbstractWsIT {

    @Test
    public void getReports() {
        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("reports")).then()
                .statusCode(200);
    }

    @Test
    public void testLimit() {
        String token = getSessionToken();

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("limit", 1).get(buildRestV3Url("reports")).then()
                .body("data.size()", equalTo(1)).statusCode(200);
    }

    @Test
    public void testStart() {
        String token = getSessionToken();
        int startValue = 1;

        int totalValue = given().header(CMDBUILD_AUTH_HEADER, token).get(buildRestV3Url("reports")).then().extract()
                .jsonPath().getInt("meta.total");

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("start", startValue).get(buildRestV3Url("reports"))
                .then().body("data.size()", equalTo(totalValue - startValue)).statusCode(200);
    }

    @Test
    public void testDetailed() {
        String token = getSessionToken();

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("detailed", true).get(buildRestV3Url("reports")).then()
                .body("data[0]", hasKey("query")).statusCode(200);

    }

    @Test
    public void getReportById() {
        String token = getSessionToken();
        int id = 11705;

        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("reports/" + id)).then()
                .statusCode(200);
    }

    @Test
    public void getReportTemplate() {
        String token = getSessionToken();
        int id = 13799;

        given().header(CMDBUILD_AUTH_HEADER, getSessionToken())
                .get(buildRestV3Url("reports/" + id + "/template/file.zip")).then().statusCode(200);
    }

    @Test
    public void getReportAttributes() {
        String token = getSessionToken();
        int id = 13799;

        given().header(CMDBUILD_AUTH_HEADER, token).get(buildRestV3Url("reports/" + id + "/attributes")).then()
                .statusCode(200);
    }

}
