package org.cmdbuild.test.rest.utils;

import static com.google.common.base.Preconditions.checkNotNull;
import java.io.IOException;
import javax.annotation.Nullable;
import static org.apache.commons.io.FileUtils.deleteQuietly;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.cmdbuild.auth.login.file.FileAuthUtils.AuthFile;
import org.cmdbuild.client.rest.RestClient;
import org.cmdbuild.client.rest.RestClientImpl;
import static org.cmdbuild.test.framework.TestContextHelper.getCurrentTestContext;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractWsIT {

    protected static final String CMDBUILD_AUTH_HEADER = "CMDBuild-Authorization";

    @Nullable
    protected static TomcatManagerForTest getTomcatManagerForTest() {
        return getCurrentTestContext().get("tomcatManagerForTest");
    }

    protected static boolean initializedByTomcatManager() {
        return getTomcatManagerForTest() != null;
    }
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private CloseableHttpClient httpClient;

    @Before
    public void setUp() throws Exception {
        createHttpClient();
    }

    @After
    public void tearDown() throws Exception {
        flushLogs();
        destroyHttpClient();
    }

    protected static void flushLogs() {
        if (getTomcatManagerForTest() != null) {
            getTomcatManagerForTest().getTomcatManager().flushLogs();
        }
    }

    private void createHttpClient() throws IOException {
        destroyHttpClient();
        httpClient = HttpClientBuilder.create().build();
    }

    private void destroyHttpClient() throws IOException {
        if (httpClient != null) {
            httpClient.close();
            httpClient = null;
        }
    }

    public CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    public CloseableHttpClient newHttpClient() throws IOException {
        createHttpClient();
        return getHttpClient();
    }

    protected String buildRestV3Url(String path) {
        return getBaseUrl() + "services/rest/v3/" + path;
    }

    protected String buildSoapWsUrl() {
        return getBaseUrl() + "services/soap/Private";
    }

    protected String getBaseUrl() {
        return checkNotNull(getCurrentTestContext().get("baseUrl"));
    }

    protected String getSessionToken() {
        try (RestClient client = createRestClient()) {
            if (getTomcatManagerForTest() != null) {
                AuthFile authFile = getTomcatManagerForTest().getTomcatManager().buildAuthFile();
                try {
                    return client.login().doLogin("admin", authFile.getPassword()).getSessionToken();
                } finally {
                    deleteQuietly(authFile.getFile());
                }
            } else {
                return client.login().doLogin("admin", "admin").getSessionToken();//TODO configurable account
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected RestClient getRestClient() {
        return createRestClient().withSessionToken(getSessionToken());
    }

    protected RestClient getUnauthenitcatedRestClient() {
        return createRestClient();
    }

    protected RestClient createRestClient() {
        return RestClientImpl.build(getBaseUrl());
    }

}
