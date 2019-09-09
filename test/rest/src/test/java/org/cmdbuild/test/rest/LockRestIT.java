package org.cmdbuild.test.rest;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isOneOf;

import java.util.List;
import java.util.Random;

import org.cmdbuild.test.framework.CmTestRunner;
import org.cmdbuild.test.framework.Context;
import org.cmdbuild.test.rest.utils.AbstractWsIT;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.cmdbuild.test.rest.TestContextProviders.TC_R2U;

@RunWith(CmTestRunner.class)
@Context(TC_R2U)
public class LockRestIT extends AbstractWsIT {

    @Test
    public void getLocks() {
        String token = getSessionToken();

        given().header(CMDBUILD_AUTH_HEADER, token).get(buildRestV3Url("locks")).then().statusCode(200);
    }

    @Test
    public void postDeleteNewLock() {
        String classId = "Room";
        String token = getSessionToken();

        List<Object> allCardsIds = given().header(CMDBUILD_AUTH_HEADER, token)
                .get(buildRestV3Url("classes/" + classId + "/cards")).then().statusCode(200).extract().jsonPath()
                .getList("data._id");

        Object cardId = allCardsIds.get(new Random().nextInt(allCardsIds.size()));

        String lockId = given().header(CMDBUILD_AUTH_HEADER, token).contentType("application/json")
                .post(buildRestV3Url("classes/" + classId + "/cards/" + cardId + "/lock")).then().statusCode(200)
                .extract().path("data._id");

        given().header(CMDBUILD_AUTH_HEADER, token).get(buildRestV3Url("locks/" + lockId)).then()
                .body("data.sessionId", equalTo(token)).statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).delete(buildRestV3Url("locks/" + lockId)).then().statusCode(200);
    }

    @Test
    public void deleteAllLocks() {
        String classId = "Computer";
        String token = getSessionToken();

        List<Object> allCardsIds = given().header(CMDBUILD_AUTH_HEADER, token)
                .get(buildRestV3Url("classes/" + classId + "/cards")).then().statusCode(200).extract().jsonPath()
                .getList("data._id");

        int locksTotali = given().header(CMDBUILD_AUTH_HEADER, token).get(buildRestV3Url("locks")).then()
                .statusCode(200).extract().path("meta.total");

        for (Object cardId : allCardsIds) {
            given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).contentType("application/json")
                    .post(buildRestV3Url("classes/" + classId + "/cards/" + cardId + "/lock")).then().statusCode(200);
        }

        given().header(CMDBUILD_AUTH_HEADER, token).get(buildRestV3Url("locks")).then()
                .body("meta.total", equalTo(locksTotali + allCardsIds.size())).statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, token).delete(buildRestV3Url("locks/_ANY")).then().statusCode(isOneOf(200, 204));

        given().header(CMDBUILD_AUTH_HEADER, token).get(buildRestV3Url("locks")).then().body("meta.total", equalTo(0))
                .statusCode(200);

    }
}
