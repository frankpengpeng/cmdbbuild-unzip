package org.cmdbuild.test.rest;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;

import java.io.File;

import org.cmdbuild.client.rest.RestClient;
import org.cmdbuild.test.framework.CmTestRunner;
import org.cmdbuild.test.framework.Context;
import org.cmdbuild.test.rest.utils.AbstractWsIT;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.cmdbuild.test.rest.TestContextProviders.TC_R2U;

@RunWith(CmTestRunner.class)
@Context(TC_R2U)
public class ProcessRestIT extends AbstractWsIT {

    @Before
    public void init() throws InterruptedException {
        RestClient restClient = getRestClient();
        restClient.system().setConfig("org.cmdbuild.workflow.enabled", "true");
        restClient.system().setConfig("org.cmdbuild.workflow.providers", "river");
        restClient.workflow().uploadPlanVersion("AssetMgt", ProcessRestIT.class.getResourceAsStream("/org/cmdbuild/test/workflow/assetmgt.xpdl"));
    }

    @Test
    public void getProcesses() {
        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("processes/")).then()
                .statusCode(200);
    }

    @Test
    public void testLimit() {
        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).queryParam("limit", 1).get(buildRestV3Url("processes"))
                .then().body("data.size()", equalTo(1)).statusCode(200);
    }

    @Test
    public void testStart() {
        String token = getSessionToken();
        int startValue = 2;

        int totalValue = given().header(CMDBUILD_AUTH_HEADER, token).get(buildRestV3Url("processes")).then().extract()
                .jsonPath().getInt("meta.total");

        given().header(CMDBUILD_AUTH_HEADER, token).queryParam("start", startValue).get(buildRestV3Url("processes"))
                .then().body("data.size()", equalTo(totalValue - startValue)).statusCode(200);
    }

    @Test
    public void getProcessesById() {
        String id = "IPAddressMgt";

        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("processes/" + id)).then()
                .body("data.description", equalTo("IP Addresses management"))
                .statusCode(200);
    }

    @Test
    public void getVersions() {
        String id = "RequestForChange";

        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("processes/" + id + "/versions"))
                .then().body("success", equalTo(true)).statusCode(200);
    }

    @Test
    public void getTemplate() {
        String id = "IncidentMgt";

        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("processes/" + id + "/template"))
                .then().body(startsWith("<?xml version=")).statusCode(200);
    }

    @Test
    public void postFileNewVersion() {
        String id = "RequestForChange";

        given().header(CMDBUILD_AUTH_HEADER, getSessionToken())
                .multiPart(new File("src/test/resources/org/cmdbuild/test/workflow/requestforchange_1.xpdl")).when()
                .post(buildRestV3Url("processes/" + id + "/versions")).then().statusCode(200);
    }

    @Test
    public void getFile() {
        String token = getSessionToken();
        String id = "IncidentMgt";

        String planId = given().header(CMDBUILD_AUTH_HEADER, token)
                .multiPart(new File("src/test/resources/org/cmdbuild/test/workflow/incidentmgt.xpdl")).when()
                .post(buildRestV3Url("processes/" + id + "/versions")).then().statusCode(200).extract()
                .path("data.planId");

        given().header(CMDBUILD_AUTH_HEADER, token)
                .get(buildRestV3Url("processes/" + id + "/versions/" + planId + "/file")).then()
                .body(startsWith("<?xml version=")).statusCode(200);
    }

    @Test
    public void postFileMigration() {
        String id = "IncidentMgt";

        given().header(CMDBUILD_AUTH_HEADER, getSessionToken())
                .multiPart(new File("src/test/resources/org/cmdbuild/test/workflow/incidentmgt.xpdl"))
                .queryParam("provider", "river").when().post(buildRestV3Url("processes/" + id + "/migration")).then()
                .statusCode(200);
    }

}
