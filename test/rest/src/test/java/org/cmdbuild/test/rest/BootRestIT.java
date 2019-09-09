package org.cmdbuild.test.rest;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.cmdbuild.test.framework.CmTestRunner;
import org.cmdbuild.test.framework.Context;
import org.cmdbuild.test.rest.utils.AbstractWsIT;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.cmdbuild.test.rest.TestContextProviders.TC_R2U;

@RunWith(CmTestRunner.class)
@Context(TC_R2U)
public class BootRestIT extends AbstractWsIT {

	@Test
	public void getBootStatus() {
		given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("boot/status")).then()
				.body("status", equalTo("READY")).statusCode(200);
	}

}
