package org.cmdbuild.test.rest;

import static com.jayway.restassured.RestAssured.given;

import org.cmdbuild.test.framework.CmTestRunner;
import org.cmdbuild.test.framework.Context;
import org.cmdbuild.test.rest.utils.AbstractWsIT;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.cmdbuild.test.rest.TestContextProviders.TC_R2U;

@RunWith(CmTestRunner.class)
@Context(TC_R2U)
public class SystemRestIT extends AbstractWsIT {

	@Test
	public void getSystemStatus() {
		given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("system/status")).then()
				.statusCode(200);
	}
	
	@Test
	public void getPatches() {
		given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("system/patches")).then()
				.statusCode(200);
	}

	@Test
	public void getTenants() {
		given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("system/tenants")).then()
				.statusCode(200);
	}

	@Test
	public void getSchedulerStatus() {
		given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("system/scheduler/jobs")).then()
				.statusCode(200);
	}

	@Test
	public void getSchedulerJobs() {
		given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("system/scheduler/jobs")).then()
				.statusCode(200);
	}

	@Test
	public void getLocks() {
		given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("locks")).then()
				.statusCode(200);
	}

	@Test
	public void getLoggers() {
		given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("system/loggers")).then()
				.statusCode(200);
	}

	@Test
	public void getDatabaseDump() {
		given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("system/database/dump")).then()
				.statusCode(200);
	}

	@Test
	public void getDebuginfoDownloads() {
		given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("system/debuginfo/download")).then()
				.statusCode(200);
	}

}
