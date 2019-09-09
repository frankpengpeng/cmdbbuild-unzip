package org.cmdbuild.test.rest;

import static com.jayway.restassured.RestAssured.given;
import java.io.File;
import java.util.List;
import static org.hamcrest.Matchers.equalTo;
import java.util.Map;
import java.util.regex.Pattern;
import static java.util.regex.Pattern.DOTALL;
import static org.apache.commons.codec.binary.Base64.encodeBase64;
import static org.apache.commons.codec.binary.StringUtils.newStringUsAscii;

import org.cmdbuild.test.framework.CmTestRunner;
import org.cmdbuild.test.framework.Context;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.prepareTuid;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.tuid;
import org.cmdbuild.test.rest.utils.AbstractWsIT;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.cmdbuild.test.rest.TestContextProviders.TC_R2U;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmExceptionUtils.lazyString;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.hamcrest.text.MatchesPattern.matchesPattern;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(CmTestRunner.class)
@Context(TC_R2U)
public class ImportExportRestIT extends AbstractWsIT {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private long csvTemplateId, xlsTemplateId, xlsxTemplateId;
    public static long emailAccountId = 2971, emailTemplateId = 11610; //TODO improve this

    @Before
    public void init() {
        prepareTuid();

        csvTemplateId = toLong(given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).contentType("application/json")
                .body(buildTemplatePayload())
                .when().post(buildRestV3Url("etl/templates")).then()
                .statusCode(200)
                .extract().path("data._id"));
        xlsTemplateId = toLong(given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).contentType("application/json")
                .body(map(buildTemplatePayload()).with("code", tuid("myXlsTemplate"), "fileFormat", "xls"))
                .when().post(buildRestV3Url("etl/templates")).then()
                .statusCode(200)
                .extract().path("data._id"));
        xlsxTemplateId = toLong(given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).contentType("application/json")
                .body(map(buildTemplatePayload()).with("code", tuid("myXlsxTemplate"), "fileFormat", "xlsx"))
                .when().post(buildRestV3Url("etl/templates")).then()
                .statusCode(200)
                .extract().path("data._id"));
    }

    @After
    public void cleanup() {
        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).delete(buildRestV3Url("etl/templates/" + csvTemplateId)).then().statusCode(200);
    }

    @Test
    public void getTemplates() {
        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("etl/templates")).then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    public void testCreateUpdateDeleteTemplate() {
        Map<String, Object> payload = map(buildTemplatePayload()).with("code", "myTestTemplateForCreation");

        long id = toLong(given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).contentType("application/json").body(payload).when()
                .post(buildRestV3Url("etl/templates")).then()
                .statusCode(200)
                .extract().path("data._id"));

        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("etl/templates/" + id)).then()
                .statusCode(200)
                .body("data._id", equalTo((int) id))
                .body("data.code", equalTo(payload.get("code")))
                .body("data.targetName", equalTo("Building"))
                .body("data.errorEmailTemplate", equalTo((int) emailTemplateId))
                .body("data.errorEmailAccount", equalTo((int) emailAccountId))
                .body("data.active", equalTo(true));

        payload = map(payload).with("_id", id, "description", "my new description");

        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).contentType("application/json").body(payload).when()
                .put(buildRestV3Url("etl/templates/" + id)).then()
                .statusCode(200)
                .body("data._id", equalTo((int) id))
                .body("data.description", equalTo("my new description"));

        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("etl/templates/" + id)).then()
                .statusCode(200)
                .body("data._id", equalTo((int) id))
                .body("data.description", equalTo("my new description"));

        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).delete(buildRestV3Url("etl/templates/" + id)).then()
                .statusCode(200);

        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("etl/templates/" + id)).then()
                .statusCode(404);
    }

    @Test
    public void testCsvTemplateExecute1() {
        String export_csv = given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("etl/templates/" + csvTemplateId + "/export/file.csv")).then()
                .statusCode(200)
                .assertThat().contentType("text/csv")
                .extract().body().asString();
        logger.info("export csv = \n\n{}\n", export_csv);
        assertThat(export_csv, matchesPattern(Pattern.compile("COD,LABEL\r\n.*", DOTALL)));
    }

    @Test
    public void testCsvTemplateExecute2() {
        String export_csv = given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("etl/templates/" + csvTemplateId + "/export")).then()
                .statusCode(200)
                .assertThat().contentType("text/csv")
                .extract().body().asString();
        logger.info("export csv = \n\n{}\n", export_csv);
        assertThat(export_csv, matchesPattern(Pattern.compile("COD,LABEL\r\n.*", DOTALL)));
    }

    @Test
    public void testXlsTemplateExecute() {
        byte[] export = given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("etl/templates/" + xlsTemplateId + "/export/file.xls")).then()
                .statusCode(200)
                .assertThat().contentType("application/vnd.ms-excel")
                .extract().body().asByteArray();
        logger.debug("export data = \n\n{}\n", lazyString(() -> newStringUsAscii(encodeBase64(export, true))));
    }

    @Test
    public void testXlsxTemplateExecute() {
        byte[] export = given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).get(buildRestV3Url("etl/templates/" + xlsxTemplateId + "/export/file.xlsx")).then()
                .statusCode(200)
                .assertThat().contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .extract().body().asByteArray();
        logger.debug("export data = \n\n{}\n", lazyString(() -> newStringUsAscii(encodeBase64(export, true))));
    }

    @Test
    public void textCsvImportTemplate1() {

        long importTemplateId = toLong(given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).contentType("application/json")
                .body(buildTemplatePayload2())
                .when().post(buildRestV3Url("etl/templates")).then()
                .statusCode(200)
                .extract().path("data._id"));

        given().header(CMDBUILD_AUTH_HEADER, getSessionToken())
                .multiPart(new File("src/test/resources/org/cmdbuild/test/rest/testImport.csv"))
                .post(buildRestV3Url("etl/templates/" + importTemplateId + "/import/")).then()
                .statusCode(200)
                .content("data.processed", equalTo(3))
                .content("success", equalTo(true))
                .content("errors", equalTo(null));
    }

    public static Map<String, Object> buildTemplatePayload() {
        return map(
                "code", tuid("myTemplate"),
                "description", "my building template",
                "targetType", "class",
                "targetName", "Building",
                "active", true,
                "type", "import_export",
                "fileFormat", "csv",
                "errorEmailTemplate", emailTemplateId,
                "errorEmailAccount", emailAccountId,
                "exportFilter", "",
                "mergeMode", "update_attr_on_missing",
                "mergeMode_when_missing_update_attr", "Notes",
                "mergeMode_when_missing_update_value", "DELETED",
                "importKeyAttribute", "Code",
                "columns", list(map(
                        "attribute", "Code",
                        "columnName", "COD",
                        "default", null,
                        "mode", null
                ), map(
                        "attribute", "Description",
                        "columnName", "LABEL",
                        "default", "<no label>",
                        "mode", null
                ))
        );
    }

    public static Map<String, Object> buildTemplatePayload2() {
        return map(
                "code", tuid("myImportTemplate"),
                "description", "my building template",
                "targetType", "class",
                "targetName", "InternalEmployee",
                "active", true,
                "type", "import_export",
                "fileFormat", "csv",
                "errorEmailTemplate", emailTemplateId,
                "errorEmailAccount", emailAccountId,
                "exportFilter", "",
                "mergeMode", "update_attr_on_missing",
                "mergeMode_when_missing_update_attr", "Notes",
                "mergeMode_when_missing_update_value", "DELETED",
                "importKeyAttribute", "Code",
                "columns", list(map(
                        "attribute", "Code",
                        "columnName", "CODE",
                        "default", null,
                        "mode", null
                ), map(
                        "attribute", "Description",
                        "columnName", "DESCRIPTION",
                        "default", null,
                        "mode", null
                ), map(
                        "attribute", "LastName",
                        "columnName", "LASTNAME",
                        "default", null,
                        "mode", null
                ))
        );
    }

}
