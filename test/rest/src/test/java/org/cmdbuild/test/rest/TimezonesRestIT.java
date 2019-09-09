package org.cmdbuild.test.rest;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItems;

import org.cmdbuild.test.framework.CmTestRunner;
import org.cmdbuild.test.framework.Context;
import org.cmdbuild.test.rest.utils.AbstractWsIT;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.cmdbuild.test.rest.TestContextProviders.TC_R2U;

@RunWith(CmTestRunner.class)
@Context(TC_R2U)
public class TimezonesRestIT extends AbstractWsIT {

    @Test
    public void getTimezones() {
        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("timezones/")).then()
                .statusCode(200)
                .body("data._id", hasItems("Europe/Rome", "America/New_York"))
                .body("data.description", hasItems("Europe/Rome", "America/New_York"));
    }
}
