/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.web.utils;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.client.rest.RestClient;
import org.cmdbuild.client.rest.RestClientImpl;
import org.cmdbuild.test.rest.utils.TomcatManagerForTest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import com.google.common.base.Stopwatch;
import static java.lang.Integer.max;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.cmdbuild.dao.config.inner.DatabaseCreator;
import org.cmdbuild.dao.config.inner.DatabaseCreatorConfigImpl;
import static org.cmdbuild.utils.lang.CmConvertUtils.toInt;
import static org.cmdbuild.utils.lang.CmConvertUtils.toIntegerOrDefault;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.openqa.selenium.TimeoutException;

public abstract class AbstractWebIT {

    public static final int DEFAULT_DELAY = 100, DEFAULT_WAIT_FOR = 30000;
    public static final String CMDBUILD_AUTH_HEADER = "CMDBuild-Authorization";

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private static TomcatManagerForTest tomcatManagerForTest;
    private static String baseUrl;

    protected static int defaultDelay, maxWaitFor;
    private WebDriverManager webDriverManager;

    @BeforeClass
    public static void setupClass() throws Exception {
        defaultDelay = toIntegerOrDefault(System.getProperty("cmdbuild.test.delay"), DEFAULT_DELAY);
        maxWaitFor = defaultDelay * DEFAULT_WAIT_FOR / DEFAULT_DELAY;
        baseUrl = trimToNull(System.getProperty("cmdbuild.test.base.url"));

        if (isBlank(baseUrl)) {
            tomcatManagerForTest = new TomcatManagerForTest();
            tomcatManagerForTest.initTomcatAndDb(DatabaseCreator.R2U_DUMP);
            baseUrl = tomcatManagerForTest.getBaseUrl();
        }
    }

    @Deprecated
    public static boolean isInteractiveMode() {
        return false;
    }

    @AfterClass
    public static void tearDownClass() {
        if (tomcatManagerForTest != null) {
            tomcatManagerForTest.cleanupTomcatAndDb();
            tomcatManagerForTest = null;
        }
    }

    @Before
    public void setUp() throws Exception {
        logger.info("setup test");
        logger.info("cmdbuild base url = {}", baseUrl);

        webDriverManager = new WebDriverManager();
        webDriverManager.init();
    }

    @After
    public void tearDown() {
        logger.info("teardown test");
        webDriverManager.cleanup();
        webDriverManager = null;
    }

    public RemoteWebDriver getDriver() {
        return webDriverManager.getDriver();
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void waitForLoad() {
        waitFor((WebDriver thisDriver) -> ((JavascriptExecutor) thisDriver).executeScript("return document.readyState").equals("complete"));
        Stopwatch stopwatch = Stopwatch.createStarted();
        boolean waited = false;
        while (true) {
            int pending = toInt(((JavascriptExecutor) getDriver()).executeScript("try{ return CMDBuildUI.util.Ajax.currentPending(); }catch(e){ return -1; }"));
            if (pending == 0) {
                if (waited) {
                    logger.info("all ajax requests have completed");
                }
                break;
            } else if (stopwatch.elapsed(TimeUnit.MILLISECONDS) > maxWaitFor) {//TODO configurable max thime
                fail("max wait time exceeded while waiting for ajax requests to complete");
            } else {
                logger.info("waiting for {} pending ajax requests", pending < 0 ? "<unknown>" : pending);
                waited = true;
                sleep();
            }
        }
    }

    public void waitForOrFail(Supplier<Boolean> expectedCondition) {
        assertTrue(waitForOrReturnFalse(expectedCondition));
    }

    public void waitForOrFail(Supplier<Boolean> expectedCondition, String failMessage) {
        assertTrue(failMessage, waitForOrReturnFalse(expectedCondition));
    }

    public boolean waitForOrReturnFalse(Supplier<Boolean> expectedCondition) {
        try {
            waitFor((d) -> expectedCondition.get());
            return true;
        } catch (TimeoutException ex) {
            return false;
        }
    }

    public void waitFor(Supplier<Boolean> expectedCondition) {
        waitFor((d) -> expectedCondition.get());
    }

    public <T> T waitForElement(Supplier<T> supplier) {
        AtomicReference<T> ref = new AtomicReference<>();
        waitFor((d) -> {
            T val = supplier.get();
            ref.set(val);
            return val != null;
        });
        return checkNotNull(ref.get());
    }

    public void waitFor(ExpectedCondition<Boolean> expectedCondition) {
        sleep();
        WebDriverWait wait = new WebDriverWait(getDriver(), max(1, maxWaitFor / 1000));
        wait.until(expectedCondition);
    }

    public WebElement waitForXpath(String xpath) {
        waitFor((driver) -> !driver.findElements(By.xpath(xpath)).isEmpty());
        return findByXpath(xpath);
    }

    public WebElement waitFor(String css) {
        waitFor((driver) -> !driver.findElements(By.cssSelector(css)).isEmpty());
        return findByXpath(css);
    }

    public WebElement waitFor(String css, String content) {
        waitFor(css);
        waitFor((driver) -> driver.findElement(By.cssSelector(css)).getText().equals(content));
        return findByXpath(css);
    }

    public WebElement findByXpath(String xpath) {
        return getDriver().findElement(By.xpath(xpath));
    }

    public WebElement findByCss(String css) {
        return getDriver().findElement(By.cssSelector(css));
    }

    public void getUrl(String url) {
        getDriver().get(url);
        waitForLoad();
    }

    public static void sleep() {
        sleep(defaultDelay);
    }

    public static void sleepLong() {
        sleep(defaultDelay * 5);
    }

    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void sleep(UITestContext context) {
        ExtjsClientBusyDetectionUtils.DetectionStrategy.defaultStrategy().waitUntilClientIsReady(getDriver(), context);
    }

    public JdbcTemplate getJdbcTemplate() {
        if (tomcatManagerForTest != null) {
            return tomcatManagerForTest.getJdbcTemplate();
        } else {
            DatabaseCreator databaseCreator = new DatabaseCreator(DatabaseCreatorConfigImpl.builder()
                    .withDatabaseUrl(checkNotBlank(System.getProperty("cmdbuild.test.database.url"), "unable to get jdbc connection, tomcat manager is null and cmdbuild.test.database.url is null"))
                    .withAdminUser(firstNotBlank(System.getProperty("cmdbuild.test.database.username"), "postgres"), firstNotBlank(System.getProperty("cmdbuild.test.database.password"), "postgres"))
                    .build());
            return new JdbcTemplate(databaseCreator.getCmdbuildDataSource());
        }
    }

    protected String getSessionToken() {
        try {
            try (RestClient client = createRestClient()) {
                return client.login().doLogin("admin", "admin").getSessionToken();
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected RestClient createRestClient() {
        return RestClientImpl.build(getBaseUrl());
    }

    protected String buildRestV3Url(String path) {
        return getBaseUrl() + "services/rest/v3/" + path;
    }

}
