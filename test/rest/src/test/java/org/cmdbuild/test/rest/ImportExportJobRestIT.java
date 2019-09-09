package org.cmdbuild.test.rest;

import static com.jayway.restassured.RestAssured.given;
import java.io.File;
import java.util.Map;
import static org.apache.commons.io.FileUtils.deleteQuietly;

import org.cmdbuild.test.framework.CmTestRunner;
import org.cmdbuild.test.framework.Context;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.prepareTuid;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.tuid;
import static org.cmdbuild.test.rest.ImportExportRestIT.buildTemplatePayload;
import org.cmdbuild.test.rest.utils.AbstractWsIT;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.cmdbuild.test.rest.TestContextProviders.TC_R2U;
import static org.cmdbuild.utils.io.CmIoUtils.cmTmpDir;
import static org.cmdbuild.utils.io.CmIoUtils.tempDir;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import static org.hamcrest.Matchers.equalTo;
import org.junit.After;
import org.junit.Before;

@RunWith(CmTestRunner.class)
@Context(TC_R2U)
public class ImportExportJobRestIT extends AbstractWsIT {

    private File tempDir;
    private String csvTemplateCode;

    private String emailAccountCode = "default", emailTemplateCode = "RF-EmailReceivedNotification"; //TODO improve this

    @Before
    public void init() {
        prepareTuid();
        tempDir = tempDir();
        csvTemplateCode = toStringNotBlank(given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).contentType("application/json")
                .body(buildTemplatePayload())
                .when().post(buildRestV3Url("etl/templates")).then()
                .statusCode(200)
                .extract().path("data.code"));
    }

    @After
    public void cleanup() {
        deleteQuietly(tempDir);
    }

    @Test
    public void testCreateImportJobConfig() {

        Map<String, Object> payload = getImportJobConfigPayload();

        long jobId = toLong(given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).contentType("application/json").body(payload).when().post(buildRestV3Url("jobs")).then()
                .statusCode(200)
                .extract().path("data._id"));

        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when().get(buildRestV3Url("jobs/" + jobId)).then()
                .statusCode(200)
                .body("data.code", equalTo(payload.get("code")))
                .body("data.description", equalTo(payload.get("description")))
                .body("data.type", equalTo(payload.get("type")))
                .body("data.enabled", equalTo(false))
                .body("data.cronExpression", equalTo("*/10 * * * *"))
                .body("data.config.errorEmailTemplate", equalTo(emailTemplateCode));
    }

    @Test
    public void testCreateExportJobConfig() {

        Map<String, Object> payload = getExportJobConfigPayload();

        long jobId = toLong(given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).contentType("application/json").body(payload).when().post(buildRestV3Url("jobs")).then()
                .statusCode(200)
                .extract().path("data._id"));

        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when().get(buildRestV3Url("jobs/" + jobId)).then()
                .statusCode(200)
                .body("data.code", equalTo(payload.get("code")))
                .body("data.description", equalTo(payload.get("description")))
                .body("data.type", equalTo(payload.get("type")))
                .body("data.enabled", equalTo(false))
                .body("data.cronExpression", equalTo("*/10 * * * *"))
                .body("data.config.emailTemplate", equalTo(emailTemplateCode));
    }

    private Map<String, Object> getImportJobConfigPayload() {
        return map(
                "code", tuid("myImportJob"),
                "description", "my import job",
                "type", "import_file",
                "enabled", false,
                "config", map(
                        "cronExpression", "*/10 * * * *",
                        "template", csvTemplateCode,
                        "source", "file",
                        "directory", tempDir.getAbsolutePath(),
                        "filePattern", ".*[.]csv",
                        "errorEmailTemplate", emailTemplateCode,
                        "errorEmailAccount", emailAccountCode,
                        "postImportAction", "disable_files"
                )
        );
    }

    private Map<String, Object> getExportJobConfigPayload() {
        return map(
                "code", tuid("myExportJob"),
                "description", "my export job",
                "type", "export_file",
                "enabled", false,
                "config", map(
                        "cronExpression", "*/10 * * * *",
                        "template", csvTemplateCode,
                        "directory", cmTmpDir().getAbsolutePath(),
                        "fileName", "my_export_file_TIMESTAMP.txt",
                        "emailTemplate", emailTemplateCode,
                        "emailAccount", emailAccountCode,
                        "notificationMode", "always"
                )
        );
    }
}
