package org.cmdbuild.test.rest;

import static com.jayway.restassured.RestAssured.given;

import org.cmdbuild.test.framework.CmTestRunner;
import org.cmdbuild.test.framework.Context;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.prepareTuid;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.tuid;
import org.cmdbuild.test.rest.utils.AbstractWsIT;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.cmdbuild.test.rest.TestContextProviders.TC_R2U;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.junit.Before;

@RunWith(CmTestRunner.class)
@Context(TC_R2U)
public class ClassFilterRestIT extends AbstractWsIT {

    @Before
    public void init() {
        prepareTuid();
    }

    @Test
    public void getClassFilters() {
        String classId = "Room";

        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("classes/" + classId + "/filters"))
                .then().statusCode(200);
    }

    @Test
    public void postDeleteClassFilters() {
        String classId = "Monitor";
        String token = getSessionToken();

        Object filterId = given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).contentType("application/json")
                .body(prepareClassFilterData(classId)).when().post(buildRestV3Url("classes/" + classId + "/filters/")).then().statusCode(200)
                .extract().path("data._id");

        given().header(CMDBUILD_AUTH_HEADER, token).when()
                .delete(buildRestV3Url("classes/" + classId + "/filters/" + filterId)).then().statusCode(200);
    }

    public static FluentMap<String, Object> prepareClassFilterData(String classId) {
        return map(
                "name", tuid("filter_" + classId),
                "target", classId,
                "description", "monitor",
                "shared", true,
                "configuration", toJson(map("attribute", map("simple", map("attribute", "Code", "operator", "equal", "value", "something"))))
        );
    }
}
