package org.cmdbuild.test.rest;

import static com.jayway.restassured.RestAssured.given;
import static org.cmdbuild.test.rest.TestContextProviders.TC_R2U;
import static org.hamcrest.Matchers.hasKey;

import java.io.File;
import java.util.List;

import org.cmdbuild.test.framework.CmTestRunner;
import org.cmdbuild.test.framework.Context;
import org.cmdbuild.test.rest.utils.AbstractWsIT;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CmTestRunner.class)
@Context(TC_R2U)
public class AttachmentRestIT extends AbstractWsIT {

    @Before
    public void init() {
        getRestClient().system().setConfigs(
                "org.cmdbuild.dms.enabled", "true",
                "org.cmdbuild.dms.service.type", "postgres"
        );
    }

    @Test
    public void getAttachments() {
        String classId = "Employee";
        String token = getSessionToken();

        List<Object> allCardsIds = given().header(CMDBUILD_AUTH_HEADER, token)
                .get(buildRestV3Url("classes/" + classId + "/cards")).then().statusCode(200).extract().jsonPath()
                .getList("data._id");

//		Collections.shuffle(allCardsIds);
        given().header(CMDBUILD_AUTH_HEADER, token)
                .get(buildRestV3Url("classes/" + classId + "/cards/" + allCardsIds.iterator().next() + "/attachments")).then().statusCode(200);
    }

    @Test
    public void postDeleteNewAttachment() {
        String classId = "Printer";
        String token = getSessionToken();

        List<Object> allCardsIds = given().header(CMDBUILD_AUTH_HEADER, token)
                .get(buildRestV3Url("classes/" + classId + "/cards")).then().statusCode(200).extract().jsonPath()
                .getList("data._id");

//        Object cardId = allCardsIds.get(new Random().nextInt(allCardsIds.size()));
        Object cardId = allCardsIds.iterator().next();

        Object attachmentId = createAttachment(classId, cardId, token);

        given().header(CMDBUILD_AUTH_HEADER, token)
                .get(buildRestV3Url("classes/" + classId + "/cards/" + cardId + "/attachments/" + attachmentId)).then()
                .statusCode(200);

        deleteAttachment(classId, cardId, attachmentId, token);
    }

    @Test
    public void getAttachmentPreview() {
        String classId = "Printer";
        String token = getSessionToken();

        List<Object> allCardsIds = given().header(CMDBUILD_AUTH_HEADER, token)
                .get(buildRestV3Url("classes/" + classId + "/cards")).then().statusCode(200).extract().jsonPath()
                .getList("data._id");

        Object cardId = allCardsIds.iterator().next();

        Object attachmentId = createAttachment(classId, cardId, token);

        given().header(CMDBUILD_AUTH_HEADER, token)
                .get(buildRestV3Url("classes/" + classId + "/cards/" + cardId + "/attachments/" + attachmentId + "/preview")).then()
                .body("data", hasKey("hasPreview")).statusCode(200);

        deleteAttachment(classId, cardId, attachmentId, token);
    }

    @Test
    public void getAttachmentHistory() {
        String classId = "Printer";
        String token = getSessionToken();

        List<Object> allCardsIds = given().header(CMDBUILD_AUTH_HEADER, token)
                .get(buildRestV3Url("classes/" + classId + "/cards")).then().statusCode(200).extract().jsonPath()
                .getList("data._id");

        Object cardId = allCardsIds.iterator().next();

        Object attachmentId = createAttachment(classId, cardId, token);

        given().header(CMDBUILD_AUTH_HEADER, token)
                .get(buildRestV3Url("classes/" + classId + "/cards/" + cardId + "/attachments/" + attachmentId + "/history")).then()
                .statusCode(200);

        deleteAttachment(classId, cardId, attachmentId, token);
    }

    @Test
    public void getAttachmentFile() {
        String classId = "Printer";
        String token = getSessionToken();

        List<Object> allCardsIds = given().header(CMDBUILD_AUTH_HEADER, token)
                .get(buildRestV3Url("classes/" + classId + "/cards")).then().statusCode(200).extract().jsonPath()
                .getList("data._id");

        Object cardId = allCardsIds.iterator().next();

        Object attachmentId = createAttachment(classId, cardId, token);

        given().header(CMDBUILD_AUTH_HEADER, token)
                .get(buildRestV3Url("classes/" + classId + "/cards/" + cardId + "/attachments/" + attachmentId + "/history/1.0/file")).then()
                .statusCode(200);

        deleteAttachment(classId, cardId, attachmentId, token);
    }

    private Object createAttachment(String classId, Object cardId, String token) {

        Object attachmentId = given().header(CMDBUILD_AUTH_HEADER, token)
                .multiPart(new File("src/test/resources/org/cmdbuild/test/rest/pdfProva.pdf"))
                .post(buildRestV3Url("classes/" + classId + "/cards/" + cardId + "/attachments/")).then()
                .statusCode(200).extract().path("data._id");

        return attachmentId;
    }

    private void deleteAttachment(String classId, Object cardId, Object attachmentId, String token) {

        given().header(CMDBUILD_AUTH_HEADER, token)
                .delete(buildRestV3Url("classes/" + classId + "/cards/" + cardId + "/attachments/" + attachmentId))
                .then().statusCode(200);
    }

}
