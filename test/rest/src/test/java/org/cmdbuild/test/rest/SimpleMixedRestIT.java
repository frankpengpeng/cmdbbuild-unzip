package org.cmdbuild.test.rest;

import static org.apache.http.entity.ContentType.APPLICATION_JSON;
import static org.apache.http.entity.ContentType.TEXT_PLAIN;
import static org.hamcrest.Matchers.either;
import static org.hamcrest.Matchers.not;

import java.io.IOException;
import java.net.URI;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.cmdbuild.test.framework.CmTestRunner;
import org.cmdbuild.test.framework.Context;
import org.cmdbuild.test.rest.utils.AbstractWsIT;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.jayway.restassured.RestAssured.given;
import com.jayway.restassured.http.ContentType;
import java.util.List;
import org.cmdbuild.client.rest.api.LookupApi;
import org.cmdbuild.client.rest.model.ClassData;
import org.cmdbuild.client.rest.model.MenuEntry;
import static org.cmdbuild.test.rest.TestContextProviders.TC_R2U;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

@RunWith(CmTestRunner.class)
@Context(TC_R2U)
public class SimpleMixedRestIT extends AbstractWsIT {

    @Test
    public void testAppJsServiceUrlRewrite() throws IOException {
        int port = URI.create(getBaseUrl()).toURL().getPort();
        HttpGet request = new HttpGet("http://localhost:" + port + "/cmdbuild/ui/cmdbuild/app.js");
        HttpResponse response = getHttpClient().execute(request);
        String responseContent = IOUtils.toString(response.getEntity().getContent());
        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
    }

    @Test
    public void testSystemStatusWs() throws IOException {

        String sessionId = getSessionToken();
        HttpGet request = new HttpGet(buildRestV3Url("system/status"));
        request.setHeader("CMDBuild-Authorization", sessionId);
        HttpResponse response = getHttpClient().execute(request);
        String responseContent = IOUtils.toString(response.getEntity().getContent());
        logger.debug("response = {}", responseContent);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
    }

    @Test
    public void testBootStatus() throws IOException {
        HttpGet request = new HttpGet(buildRestV3Url("boot/status"));
        HttpResponse response = getHttpClient().execute(request);
        String responseContent = IOUtils.toString(response.getEntity().getContent());
        logger.debug("response = {}", responseContent);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
        assertEquals("{\"success\":true,\"status\":\"READY\"}", responseContent);

    }

    @Test
    public void testLanguages1() throws IOException {
        HttpGet get = new HttpGet(buildRestV3Url("configuration/languages"));
        HttpResponse response = getHttpClient().execute(get);
        String responseContent = IOUtils.toString(response.getEntity().getContent());
        logger.debug("response = {}", responseContent);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
        assertThat(responseContent, containsString("\"code\":\"en\",\"description\":\"English\""));
    }

    @Test
    public void testLanguages2() throws IOException {
        HttpGet get = new HttpGet(buildRestV3Url("configuration/languages/"));
        HttpResponse response = getHttpClient().execute(get);
        String responseContent = IOUtils.toString(response.getEntity().getContent());
        logger.debug("response = {}", responseContent);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
        assertThat(responseContent, containsString("\"code\":\"en\",\"description\":\"English\""));

    }

    @Test
    public void testUiHome1() throws IOException {

        HttpGet get = new HttpGet(getBaseUrl() + "/ui");
        HttpResponse response = getHttpClient().execute(get);
        String responseContent = IOUtils.toString(response.getEntity().getContent());
        logger.debug("response = {}", responseContent);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
        assertThat(responseContent, containsString("<html"));
        assertThat(responseContent, containsString("var Ext = Ext"));

    }

    @Test
    public void testUiHome2() throws IOException {
        HttpGet get = new HttpGet(getBaseUrl() + "/ui/");
        HttpResponse response = getHttpClient().execute(get);
        String responseContent = IOUtils.toString(response.getEntity().getContent());
        logger.debug("response = {}", responseContent);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
        assertThat(responseContent, containsString("<html"));
        assertThat(responseContent, containsString("var Ext = Ext"));
    }

    @Test
    public void testUserPreferences() throws IOException {
        String sessionId = given().contentType(ContentType.JSON)
                .body("{\"username\" : \"admin\", \"password\" : \"admin\"}").post(buildRestV3Url("sessions?scope=ui")).then()
                .statusCode(200).extract().path("data._id");

        {
            HttpGet request = new HttpGet(buildRestV3Url("sessions/current/preferences"));
            request.setHeader("CMDBuild-Authorization", sessionId);
            HttpResponse response = getHttpClient().execute(request);
            flushLogs();
            String responseContent = IOUtils.toString(response.getEntity().getContent());
            logger.debug("response = {}", responseContent);
            assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
            assertThat(responseContent, containsString("cm_ui_startingClass"));
            EntityUtils.consume(response.getEntity());
        }
        {
            HttpPut request = new HttpPut(buildRestV3Url("sessions/current/preferences/my_config"));
            request.setHeader("CMDBuild-Authorization", sessionId);
            request.setEntity(new StringEntity("my-value", TEXT_PLAIN));
            HttpResponse response = getHttpClient().execute(request);
            flushLogs();
            assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
            EntityUtils.consume(response.getEntity());
        }
        {
            HttpGet request = new HttpGet(buildRestV3Url("sessions/current/preferences"));
            request.setHeader("CMDBuild-Authorization", sessionId);
            HttpResponse response = getHttpClient().execute(request);
            flushLogs();
            String responseContent = IOUtils.toString(response.getEntity().getContent());
            logger.debug("response = {}", responseContent);
            assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
            assertThat(responseContent, containsString("\"my_config\":\"my-value\""));
            EntityUtils.consume(response.getEntity());
        }
        {
            HttpGet request = new HttpGet(buildRestV3Url("sessions/current/preferences/my_config"));
            request.setHeader("CMDBuild-Authorization", sessionId);
            HttpResponse response = getHttpClient().execute(request);
            flushLogs();
            String responseContent = IOUtils.toString(response.getEntity().getContent());
            logger.debug("response = {}", responseContent);
            assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
            assertThat(responseContent, equalTo("my-value"));
            EntityUtils.consume(response.getEntity());
        }
        {
            HttpDelete request = new HttpDelete(buildRestV3Url("sessions/current/preferences/my_config"));
            request.setHeader("CMDBuild-Authorization", sessionId);
            HttpResponse response = getHttpClient().execute(request);
            flushLogs();
            logger.debug("delete OK");
            assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
            EntityUtils.consume(response.getEntity());
        }
        {
            HttpGet request = new HttpGet(buildRestV3Url("sessions/current/preferences/my_config"));
            request.setHeader("CMDBuild-Authorization", sessionId);
            HttpResponse response = getHttpClient().execute(request);
            flushLogs();
            assertThat(response.getStatusLine().getStatusCode(), equalTo(204));
            EntityUtils.consume(response.getEntity());
        }
        {
            HttpGet request = new HttpGet(buildRestV3Url("sessions/current/preferences"));
            request.setHeader("CMDBuild-Authorization", sessionId);
            HttpResponse response = getHttpClient().execute(request);
            flushLogs();
            String responseContent = IOUtils.toString(response.getEntity().getContent());
            logger.debug("response = {}", responseContent);
            assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
            assertThat(responseContent, not(containsString("\"my_config\":\"my-value\"")));
            EntityUtils.consume(response.getEntity());
        }
    }

    @Test
    public void testSimpleSecurity() throws IOException {
        HttpGet get = new HttpGet(buildRestV3Url("classes/Email"));
        HttpResponse response = getHttpClient().execute(get);
        String responseContent = IOUtils.toString(response.getEntity().getContent());
        logger.debug("response = {}", responseContent);
        assertThat(response.getStatusLine().getStatusCode(), either(equalTo(401)).or(equalTo(403))); // TODO restrict to
        // only one
        // return code
        // (??)
    }

    @Test
    public void doTestLogin1() throws IOException {
        HttpPost post = new HttpPost(buildRestV3Url("sessions?scope=ui"));
        post.setEntity(new StringEntity("{\"username\" : \"admin\", \"password\" : \"admin\"}", APPLICATION_JSON));
        HttpResponse response = getHttpClient().execute(post);

        String responseContent = IOUtils.toString(response.getEntity().getContent());
        logger.debug("response = {}", responseContent);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));

    }

    @Test
    public void doTestLogin2() throws IOException {
        HttpPost post = new HttpPost(buildRestV3Url("sessions/?scope=ui"));
        post.setEntity(new StringEntity("{\"username\" : \"admin\", \"password\" : \"admin\"}", APPLICATION_JSON));
        HttpResponse response = getHttpClient().execute(post);

        String responseContent = IOUtils.toString(response.getEntity().getContent());
        logger.debug("response = {}", responseContent);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
    }

    @Test
    public void testListDashboards() throws IOException {
        String sessionId = getSessionToken();

        HttpGet request = new HttpGet(buildRestV3Url("dashboards/"));
        request.setHeader("CMDBuild-Authorization", sessionId);
        HttpResponse response = newHttpClient().execute(request);
        flushLogs();
        String responseContent = IOUtils.toString(response.getEntity().getContent());
        logger.debug("response = {}", responseContent);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
        assertThat(responseContent, containsString("success"));
    }

    @Test
    public void testListCustomPages() throws IOException {
        String sessionId = getSessionToken();

        HttpGet request = new HttpGet(buildRestV3Url("custompages/"));
        request.setHeader("CMDBuild-Authorization", sessionId);
        HttpResponse response = newHttpClient().execute(request);
        flushLogs();
        String responseContent = IOUtils.toString(response.getEntity().getContent());
        logger.debug("response = {}", responseContent);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
    }

    @Test
    public void testCommentsInWadl1() throws IOException {
        HttpGet request = new HttpGet(buildRestV3Url("?_wadl"));
        request.setHeader("CMDBuild-Authorization", getSessionToken());
        HttpResponse response = getHttpClient().execute(request);
        String responseContent = IOUtils.toString(response.getEntity().getContent());
        logger.debug("response = {}", responseContent);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
        assertThat(responseContent,
                containsString("<application xmlns=\"http://wadl.dev.java.net/2009/02\" xmlns:xs="));
    }

    @Test
    public void testCommentsInWadl2() throws IOException {
        HttpGet request = new HttpGet(buildRestV3Url("classes?_wadl"));
        request.setHeader("CMDBuild-Authorization", getSessionToken());
        HttpResponse response = getHttpClient().execute(request);
        String responseContent = IOUtils.toString(response.getEntity().getContent());
        logger.debug("response = {}", responseContent);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
        assertThat(responseContent,
                containsString("<application xmlns=\"http://wadl.dev.java.net/2009/02\" xmlns:xs="));
    }

    @Test
    public void testCurrentSessionWs() throws IOException {
        String responseContent = loginAndGet("sessions/current");
        assertThat(responseContent, containsString("\"success\":true"));
    }

    @Test
    public void testBootStatusWs() throws IOException {
        String responseContent = loginAndGet("boot/status");
        assertEquals("{\"success\":true,\"status\":\"READY\"}", responseContent);
    }

    @Test
    public void testSystemConfigWs() throws IOException {
        getRestClient().system().setConfig("my.config", "something");
        String responseContent = loginAndGet("system/config");
        assertThat(responseContent, containsString("\"my.config\":\"something\""));
    }

    @Test
    public void testClassesWs() throws IOException {
        String responseContent = loginAndGet("classes");
        assertThat(responseContent, containsString("\"success\":true"));
        assertThat(responseContent, containsString("\"name\":\"Building\""));
        assertThat(responseContent, containsString("\"_id\":\"Building\""));
    }

    @Test
    public void testClassDetailWs() throws IOException {
        ClassData classe = getRestClient().classe().getById("Employee");
        assertEquals("Employee", classe.getName());
        assertEquals("Employee", classe.getId());
        assertEquals("standard", classe.getType());//TODO test all data
    }

    @Test
    public void testLookupWs() throws IOException {
        List<LookupApi.LookupValue> values = getRestClient().lookup().getValues("ScreenSize");
        assertEquals(4, values.size());
        LookupApi.LookupValue value = values.get(0);
        assertEquals("13'", value.getCode());
        assertEquals("13'", value.getDescription());
    }

    @Test
    public void testCardsWs() throws IOException {
        String responseContent = loginAndGet("classes/Building/cards");
        assertThat(responseContent, containsString("\"success\":true"));
        assertThat(responseContent, containsString("\"_type\":\"Building\""));
        assertThat(responseContent, containsString("\"Description\":\"Aon Center\""));
    }

    @Test
    public void testDomainsWs() throws IOException {
        String responseContent = loginAndGet("domains");
        assertThat(responseContent, containsString("\"success\":true"));
    }

    @Test
    public void testMenuWs() throws IOException {
        String responseContent = loginAndGet("menu");
        assertThat(responseContent, containsString("\"success\":true"));
    }

    @Test
    public void testMenuWs2() throws IOException {
        MenuEntry menu = getRestClient().menu().getMenu();
        assertEquals("root", menu.getMenuType());
        assertFalse(menu.getChildren().isEmpty());
    }

    private String loginAndGet(String path) throws IOException {
        String sessionId = getSessionToken();
        HttpGet request = new HttpGet(buildRestV3Url(path));
        request.setHeader("CMDBuild-Authorization", sessionId);
        HttpResponse response = newHttpClient().execute(request);
        flushLogs();
        String responseContent = IOUtils.toString(response.getEntity().getContent());
        logger.debug("response = {}", responseContent);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
        return responseContent;
    }
}
