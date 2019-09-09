package org.cmdbuild.test.rest;

import static com.jayway.restassured.RestAssured.given;

import org.cmdbuild.test.framework.CmTestRunner;
import org.cmdbuild.test.framework.Context;
import org.cmdbuild.test.rest.utils.AbstractWsIT;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.cmdbuild.test.rest.TestContextProviders.TC_R2U;

@RunWith(CmTestRunner.class)
@Context(TC_R2U)
public class ProcessStartActivityRestIT extends AbstractWsIT {

    @Before
    public void init() {
        getRestClient().system().setConfigs("org.cmdbuild.workflow.enabled", "true", "org.cmdbuild.workflow.providers", "river");
        getRestClient().system().setConfigs("org.cmdbuild.workflow.enabled", "true", "org.cmdbuild.workflow.providers", "river");//TODO fix this
        getRestClient().workflow().uploadPlanVersion("AssetMgt", ProcessRestIT.class.getResourceAsStream("/org/cmdbuild/test/workflow/assetmgt.xpdl"));
    }

    @Test
    public void getStartActivities() {
        String token = getSessionToken();
        String processId = "AssetMgt";

        given().header(CMDBUILD_AUTH_HEADER, token).get(buildRestV3Url("processes/" + processId + "/start_activities"))
                .then().statusCode(200);
    }
}
