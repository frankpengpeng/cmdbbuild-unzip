/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.rest;

import static com.jayway.restassured.RestAssured.given;

import org.cmdbuild.test.framework.CmTestRunner;
import org.cmdbuild.test.framework.Context;
import org.cmdbuild.test.rest.utils.AbstractWsIT;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.cmdbuild.test.rest.TestContextProviders.TC_R2U;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.hamcrest.Matchers.equalTo;

@RunWith(CmTestRunner.class)
@Context(TC_R2U)
public class ContextMenuComponentRestIT extends AbstractWsIT {

    @Test
    public void getContextMenuComponent() {
        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("components/contextmenu/")).then()
                .statusCode(200);
    }

    @Test
    public void getContextMenuById() {
        Object contextMenuId = createContextMenuComponent();

        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("components/contextmenu/" + contextMenuId))
                .then().statusCode(200);

        deleteContextMenu(contextMenuId);
    }

    @Test
    public void postDeleteNewContextMenu() {
        Object contextMenuId = createContextMenuComponent();

        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("components/contextmenu/" + contextMenuId))
                .then().body("data.name", equalTo("firstcomponent")).statusCode(200);

        deleteContextMenu(contextMenuId);
    }

    @Test
    public void downloadContextMenu() {
        Object contextMenuId = createContextMenuComponent();

        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("components/contextmenu/" + contextMenuId))
                .then().body("data.name", equalTo("firstcomponent")).statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("components/contextmenu/" + contextMenuId + "/file.zip"))
                .then().statusCode(200);

        deleteContextMenu(contextMenuId);
    }

    public Object createContextMenuComponent() {
        Object contextMenuId = given().header(CMDBUILD_AUTH_HEADER, getSessionToken())
                .multiPart("file", "file.zip", toByteArray(getClass().getResourceAsStream("/org/cmdbuild/test/rest/firstcomponent.zip")))
                .multiPart("data", "{\"active\":\"true\",\"description\":\"myCPage\"}", "application/json").when()
                .post(buildRestV3Url("components/contextmenu/")).then().statusCode(200).extract().path("data._id");

        return contextMenuId;
    }

    public void deleteContextMenu(Object contextMenuId) {
        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
                .delete(buildRestV3Url("components/contextmenu/" + contextMenuId)).then().statusCode(200);

    }
}
