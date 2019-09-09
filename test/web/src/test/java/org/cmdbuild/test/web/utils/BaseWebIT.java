package org.cmdbuild.test.web.utils;

import com.google.common.base.Stopwatch;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import static com.google.common.collect.MoreCollectors.toOptional;
import org.cmdbuild.client.rest.RestClient;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.dao.DataAccessException;

import org.cmdbuild.client.rest.RestClientImpl;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.junit.Assert.fail;
import org.openqa.selenium.StaleElementReferenceException;

public class BaseWebIT extends AbstractWebIT {

    public static final long SECONDS = 1000L;

    private boolean failOnBrowserConsoleErrors = false;

    protected ClientLog currentClientLog = null;
    protected String lastCallerMethod = null;
    /**
     * This solution implies only one test of this class instance is run at the same time
     */
    protected Set<String> runningTestTouchedClasses = new HashSet<>();
    /**
     * This solution implies only one test of this class instance is run at the same time
     */
    protected UITestContext runningTestContext; //needed for runningTestCleanupQueries, could replace runningTestTouchedClasses
    //protected List<String> runningTestCleanupQueries = new ArrayList<>(); //can be added after test is executed!!

    private RestClient restClient;
    private Login clientCredentials = Login.admin();

    //LOCATORS (move?)
    protected By languageDropDownLocator = By.xpath("//div[@data-testid='login-inputlanguage']");
    protected By roleDropDownLocator = By.xpath("//div[@data-testid='login-inputrole']");
    protected By tenantDropDownLocator = By.xpath("//div[@data-testid='login-inputtenant']");
    protected By managementContentLocator = By.id("CMDBuildManagementContent");
    protected By administrationContentLocator = By.id("CMDBuildAdministrationContent");

    public String getBaseUrlUI() {
        return getBaseUrl() + "ui";
    }

    public void login(Login credentials) {
        getUrl(getBaseUrlUI());

        loginFormInputUsernameAndPasswordInLoginForm(credentials);
        loginFormSelectLanguage(firstNotBlank(credentials.getLanguage(), "English"));
        submitLoginForm();

        //TODO second step (?)
        waitForOrFail(this::isUserLoggedIn, "login failed");
        logger.info("login completed with credentials = {}", credentials);
    }

    public void logout() {
        clickLogoutElement();
        waitForOrFail(this::isUserAtLoginPage, "logout failed");
    }

    protected void selectElementsFromMenuTree(String... treePath) {
        logger.info("select elements from nav menu, elements = {}", list(treePath));
        WebElement container = findElementByTestId("management-navigation-container"),
                element = null;
        for (String elementText : treePath) {
            logger.info("select nav menu element =< {} >", elementText);
            if (element == null) {
                element = waitForElement(() -> container.findElements(By.cssSelector(".x-treelist-root-container > .x-treelist-item")).stream()
                        .filter(e -> e.findElement(By.cssSelector("#" + e.getAttribute("id") + " > .x-treelist-row .x-treelist-item-text")).getText().equals(elementText))
                        .collect(toOptional()).orElse(null));
            } else {
                WebElement prevElement = element;
                element = waitForElement(() -> prevElement.findElements(By.cssSelector("#" + prevElement.getAttribute("id") + "> .x-treelist-container > .x-treelist-item")).stream()
                        .filter(e -> e.findElement(By.cssSelector("#" + e.getAttribute("id") + "> .x-treelist-row .x-treelist-item-text")).getText().equals(elementText))
                        .collect(toOptional()).orElse(null));
            }
            element.click();
            waitForLoad();
        }
    }

    protected WebElement findElementByXPath(String xPath) {
        return ExtjsUtils.findElementByXPath(getDriver(), xPath);
    }

    protected WebElement findElementByTestId(String testId) {
        return ExtjsUtils.findElementByTestId(getDriver(), testId);
    }

    protected void findElementByTestIdAndSendkeysToInputField(String testId, String keys) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        while (true) {
            try {
                WebElement element = findElementByTestId(testId);
                WebElement inputField = element.findElement(By.tagName("input"));
                inputField.sendKeys(keys);
                return;
            } catch (StaleElementReferenceException ex) {
                //ignore and continue
                if (stopwatch.elapsed(TimeUnit.MILLISECONDS) > maxWaitFor) {
                    fail("timeout while waiting for input element with test id = " + testId);
                }
                sleep();
            }
        }
    }

    /**
     * @param testId
     * @return found WebElement, waiting for the element to be present 
     */
    protected WebElement findElementById(String testId) {
        return ExtjsUtils.findElementById(getDriver(), testId);
    }

    /**
     * @param clickableWebElement
     * @return the same clickableWebElement, when the WebElement is ready to be clicked
     */
    protected WebElement fetchClickableWebElement(WebElement clickableWebElement) {
        return ExtjsUtils.fetchClickableWebElement(getDriver(), clickableWebElement);
    }

    protected String getCurrentUrl() {
        waitForLoad();
        return getDriver().getCurrentUrl();
    }

    //FIXME use callerMethod() ???
    @Deprecated //REMOVE, use testStart(UITestContext)
    protected UITestContext testStart() {

        currentClientLog = null;
        this.logTestPhase(Thread.currentThread().getStackTrace()[2].getMethodName(), "START");

        return new UITestContext().withWebDriver(getDriver());

    }

    @Deprecated
    protected void testStart(UITestContext testContext) {
        currentClientLog = null;
        //TODO substitute with id from context
        this.logTestPhase(Thread.currentThread().getStackTrace()[2].getMethodName(), "START");
        if (testContext != null) {
            runningTestTouchedClasses.addAll(testContext.getTouchedClasses());
            runningTestContext = testContext;
            //runningTestCleanupQueries.addAll(testContex.getCleanupQueries());
            createTouchedClassesSnapshot(testContext.getTouchedClasses());
            testContext.setStart(LocalDateTime.now());
        }
    }

    //FIXME extract suffix of temporary table
    protected void createTouchedClassesSnapshot(Set<String> touchedClasses) throws WebUICMDBuidTestException {

        for (String className : touchedClasses) {
            //CHECK name is valid
            logger.info("Creating snapshot for class: {}", className);
            Connection conn = null;
            Statement stm = null;
            boolean hasHistory = hasClassHistory(className);
            try {
                conn = getJdbcTemplate().getDataSource().getConnection();
                conn.setAutoCommit(false);
                stm = conn.createStatement();
                stm.execute("BEGIN;");
                //stm.execute("set session_replication_role = replica;");
                stm.execute("DROP TABLE IF EXISTS \"?_history_test_copy\";".replace("?", className));
                stm.execute("DROP TABLE IF EXISTS \"?_test_copy\";".replace("?", className));
                stm.execute("CREATE UNLOGGED TABLE IF NOT EXISTS \"?_test_copy\" AS (SELECT * FROM \"?\");".replace("?", className));
                stm.execute("COMMIT");
                //FIXME: should be executed only if class has history. Simple classes...?)
                if (hasHistory) {
                    stm.execute("BEGIN;");
                    stm.execute("CREATE UNLOGGED TABLE IF NOT EXISTS \"?_history_test_copy\" AS (SELECT * FROM \"?_history\");".replace("?", className));
                    stm.execute("COMMIT");
                }
                conn.commit(); //SHOULD BE REDUNDANT (check please)
                logger.info("Snapshot for class: {} successfully created", className);
            } catch (Exception e) {
                logger.error("Could not create a snapshot of db tables for class{}", className);
                throw new WebUICMDBuidTestException("Could not create a snapshot of db tables for class " + className + ".   Cause: " + e.toString());
            } finally { // FIXME use safer methods
                if (stm != null) {
                    try {
                        stm.close();
                    } catch (SQLException e) {
                    }
                }
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        logger.error("Could not close connection!");
                    }
                }
            }
        }
    }

    private boolean hasClassHistory(String className) {

        try {
            if (getJdbcTemplate().queryForObject("select _cm_comment_for_class('?');".replace("?", className), String.class).contains("TYPE: simpleclass")) {
                return false;
            }
        } catch (DataAccessException e) {
        }
        return true;
    }

    //FIXME extract suffix of temporary tables
    protected void restoreTouchedClassesSnapshot(Set<String> touchedClasses) throws WebUICMDBuidTestException {

        for (String className : touchedClasses) {
            //CHECK name is valid
            logger.info("Restoring snapshot for class: {} ", className);
            Connection conn = null;
            Statement stm = null;
            LocalDateTime restoreTMS = LocalDateTime.now();
            boolean hasHistory = hasClassHistory(className);
            try {
                conn = getJdbcTemplate().getDataSource().getConnection();
                conn.setAutoCommit(false);
                stm = conn.createStatement();
                stm.execute("BEGIN;");
                stm.execute("set session_replication_role = replica;");

                if (hasHistory) {
                    stm.execute("TRUNCATE TABLE \"?_history\" CONTINUE IDENTITY".replace("?", className));
                }
                stm.execute("TRUNCATE TABLE \"?\" CONTINUE IDENTITY".replace("?", className));

                stm.execute("INSERT INTO \"?\" SELECT * FROM \"?_test_copy\";".replace("?", className));
                if (hasHistory) {
                    stm.execute("INSERT INTO \"?_history\" SELECT * FROM \"?_history_test_copy\";".replace("?", className));
                }

                stm.execute("DROP TABLE IF EXISTS \"?_history_test_copy\";".replace("?", className));
                stm.execute("DROP TABLE IF EXISTS \"?_test_copy\";".replace("?", className));

                stm.execute("set session_replication_role = DEFAULT;");
                stm.execute("COMMIT");
                conn.commit(); //SHOULD BE REDUNDANT (check please)
                logger.info("Restored snapshot for class {} in {} ms", className, ChronoUnit.MILLIS.between(restoreTMS, LocalDateTime.now()));
            } catch (Exception e) {
                logger.error("Could not restore the snapshot of db tables for class {} because of: {}", className, e.toString());
                throw new WebUICMDBuidTestException("Could not restore the snapshot of db tables for class " + className + ".   Cause: " + e.toString());
            } finally { // FIXME use safer methods
                if (stm != null) {
                    try {
                        stm.close();
                    } catch (SQLException e) {
                    }
                }
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        logger.error("Could not close connection!");
                    }
                }
            }
        }
    }

    @Deprecated
    protected void testEnd() {

        logTestPhase(callerMethod(), " END");
//		try { 
//			fetchClientLog().printLogs(logger, null);
//		} catch (Exception e) {
//			logger.error("Printing client logs failed");
//		}
//        close();
    }

    //TODO check duration  / client errors here
    @Deprecated
    protected void testEnd(@Nonnull UITestContext context) {
        context.testEnd();
        logger.info("Test {} took {} to complete (full time: {} )", context.getDescription(), context.getMeasuredTimeSpent(), context.getFullTimeSpent());
        checkTest(context); //move to @after
        testEnd();
    }

    //FIX: make private
    protected void checkTest(@Nonnull UITestContext context) {
        boolean passesRules = (context.passesRules());
//		restoreTouchedClassesSnapshot(context.getTouchedClasses()); moved into @After...
        assertTrue(passesRules);

    }

    protected void checkNoErrorsOnBrowserConsole() {
        ClientLog log = fetchClientLog().supress("favicon");
        log.getEntries().forEach(e -> {
            logger.warn("browser console log: {}: {}", e.getLevel(), e.getMessage());
        });
        log = log
                .supress("Use of the Application Cache is deprecated on insecure origins")
                .supress("This page includes a password or credit card input in a non-secure context");
//                .supress("404").supress("401")
        if (failOnBrowserConsoleErrors && log.error()) {
            fail("test failed, found errors in client log = " + log.getErrorsForLogs());
        }
    }

    //TODO add parameters to add checks
//	protected void checkClient(boolean performDefaultChecksAgainstClientLogs) {
//		ClientLog client = fetchClientLog();
//		if (performDefaultChecksAgainstClientLogs) {//TODO refactor move out
//			assertTrue(passesDefaultClientLogs());
//		}
//	}
    //TODO: manage log level?
    protected void logTestPhase(String testName, String phase) {

        StringBuilder message = new StringBuilder("WebUITest - UIBasics ");
        message.append(testName).append(" ").append(phase);
        logger.info(message.toString());
    }

//    @Deprecated
//    protected boolean noErrorsOnBrowserConsole() {
//        ClientLog log = fetchClientLog().printLogs(LOGGER, null).supress("favicon");
//        if (log.success()) {
//            return true;
//        } else {
//            LOGGER.warn("found {} errors in client log", log.errors());
//            return false;
//        }
//    }
    /**
     * Preferred way to retrieve client log.
     * 
     * @return ClientLog immutable instance
     */
    protected ClientLog fetchClientLog() {

        if (currentClientLog == null || (!callerMethod().equals(lastCallerMethod))) {
            currentClientLog = ClientLog.fetchFromWebDriver(getDriver());
        }
        lastCallerMethod = callerMethod();
        return currentClientLog;
    }

    protected String callerMethod() {
        return Thread.currentThread().getStackTrace()[2].getMethodName();
    }

    @Deprecated
    protected void sleepDeprecated() {
        sleep();
    }

    @Deprecated
    public void sleepDeprecated(long time) {
        sleep(time);
    }

    /**
     * 
     * Pause for the given amount of milliseconds, notifying the test context to deduct this pause from test completion time
     * 
     * @param millis Pause length
     * @params optional test context to be notified
     */
    public void artificialSleep(long millis, @Nullable UITestContext context) {
        sleep(millis);
        if (context != null) {
            context.notifyArtificialDelayTime(millis);
        }
    }

    /**
     *
     * Pause for the given amount of milliseconds, notifying the test context to deduct this pause from test completion time
     *
     * @param millis Pause length
     * @params optional test est context to be notified
     */
    public void artificialSleep(long millis, Optional<UITestContext> optionalContext) {
        sleep(millis);
        if (optionalContext.isPresent()) {
            optionalContext.get().notifyArtificialDelayTime(millis);
        }
    }

    /**
     *
     * Use instead of sleepDeprecated and artificialSleep when it is ok to introduce pauses not depending on conditions in tests where execution time is not measured not of concern
     * @param millis
     */
    public void artificialSleep(long millis) {
        sleep(millis);
    }

    /**
     * 
     * Convenience method for getting the default (admin) rest client.
     * Use WebServiceUtilities to obtain a client with specific credentials
     * @return rest client logged in with default admin credentials
     */
    protected RestClient getRestClient() {
        if (restClient == null) {
            restClient = getClientInstance(clientCredentials);
        }
        return restClient;
    }

    /**
     * @param clientCredentials to be used for default rest client instantiation
     */
    protected void setClientCredentials(Login clientCredentials) {
        this.clientCredentials = clientCredentials;
    }
//
//    /**
//     * @return new admin credentials instance that can be manipulated
//     */
//    protected Login Login.admin() {
//        return Login.admin();
//    }

    /**
     * @return new demouser credentials instance that can be manipulated
     */
//    protected Login Login.demo() {
//        return Login.defaultCredentialsInstance(Login.DEFAULT_USER_DEMOUSER);
//    }
//
//    protected Login defaultGuestCredentialsInstance() {
//        return Login.defaultCredentialsInstance(Login.DEFAULT_GUEST);
//    }
    //FIXME avoid fixed delays if possible (and accept a TestContext object to account for artificial wait time 

    /*public void waitForLoad(UITestContext context) {
		LocalDateTime start = LocalDateTime.now();
		//super.waitForLoad();
		//waitFor((WebDriver thisDriver) -> ((JavascriptExecutor) thisDriver).executeScript("return document.readyState").equals("complete"));
		sleep(500); //Explicit wait alone does not suffice
		FluentWait<WebDriver> wait = (new WebDriverWait(getDriver(), 10))
				.pollingEvery(100, TimeUnit.MILLISECONDS);

		wait.until((WebDriver thisDriver) -> ((JavascriptExecutor) thisDriver).executeScript("return document.readyState").equals("complete"));
		logger.info("WAIT4LOAD took {} ms @ {}", ChronoUnit.MILLIS.between(start, LocalDateTime.now()), Thread.currentThread().getStackTrace()[2].getMethodName());
	
		ExtjsClientBusyDetection.DetectionStrategy.defaultStrategy().waitUntilClientIsReady(getDriver(), context);
	}
     */
    public WebElement waitForElementVisibility(By locator) {
        return ExtjsUtils.waitForElementVisibility(getDriver(), locator);
    }

    public WebElement waitForElementPresence(By locator) {
        return ExtjsUtils.waitForElementPresence(getDriver(), locator);
    }

    /**
     * Must be called in @After of sublcasses
     */
    protected void cleanupDB() {
        cleanupCreatedClasses();
        cleanupTouchedClasses();
        executeCleanUpQueries();
    }

    protected void cleanupTouchedClasses() {

        try {
            restoreTouchedClassesSnapshot(runningTestTouchedClasses);
            runningTestTouchedClasses.clear();
        } catch (WebUICMDBuidTestException e) {
            logger.error("UI Test cleanup failed");
        }
    }

    protected void cleanupCreatedClasses() {

        for (String class2Remove : runningTestContext.getCreatedClasses()) {
            //TODO remove history
            //TODO USE cmdbuild pgplsql function to remove class
            try {
                getJdbcTemplate().update("truncate \"" + class2Remove + "_history\"");
            } catch (Exception e) {
            }
            try {
                getJdbcTemplate().update("truncate \"" + class2Remove + "\"");
            } catch (Exception e) {
                logger.error("Could not remove table content for created class {} because of: {}", class2Remove, e.toString());
            }
            try {
                getJdbcTemplate().queryForObject("select ('" + class2Remove + "')", Object.class);
                logger.info("Removed class {} created by test", class2Remove);
            } catch (Exception e) {
                logger.error("Could not remove table content for created class {} because of:", class2Remove, e.toString());
            }
        }
    }

    protected void executeCleanUpQueries() {
        if (runningTestContext != null) {
            for (String plainQuery : runningTestContext.getCleanupQueries()) {
                try {
                    logger.info("Executing cleanup plain query: {}", plainQuery);
                    getJdbcTemplate().execute(plainQuery);
                } catch (DataAccessException e) {
                    logger.error("Impossible to execute plain cleanup query: {}", plainQuery);
                }
            }
        }

    }

    //TODO create overload with default rules?
    protected UITestContext getDefaultTestContextInstance() {
        return new UITestContext().withWebDriver(getDriver());
    }

    public String getCmdbuildSystemConfig(String property) {
        return getAdminClient().system().getConfig(property);
    }

    public String setCmdbuildSystemConfig(String property, String value) {

        String previous = getAdminClient().system().getConfig(property);
        getAdminClient().system().setConfig(property, value);
        return previous;
    }

    public RestClient getClientInstance(@Nonnull Login credentials) {
        RestClient client = RestClientImpl.builder().withServerUrl(getBaseUrl()).build();
        client = client.doLogin(credentials.getUserName(), credentials.getPassword());
        return client;
    }

    public void focus(WebElement elementToFocus) {
        ExtjsUtils.focus(getDriver(), elementToFocus);
    }

    public void goFullScreen() {
        ExtjsUtils.goFullScreen(getDriver());
    }

    /**
     * 
     * @return a client instance logged in with default admin user
     * 
     * Prefer using specific credentials for each test
     */
    public RestClient getAdminClient() {
        Login credentials = Login.admin();
        return getClientInstance(credentials);
    }

    public UITestRule getDefaultCLientLogCheckRule() {
        return UITestRule.defineClientLogCheckRule(null, ImmutableList.of("404", "401"), null, null);
    }

    //login utils BEGIN
    protected void loginFormInputUsernameAndPasswordInLoginForm(Login credentials) {
        findElementByTestIdAndSendkeysToInputField("login-inputusername", credentials.getUserName());
        sleep();
        findElementByTestIdAndSendkeysToInputField("login-inputpassword", credentials.getPassword());
        sleep();

    }

    protected void submitLoginForm() {
        findElementByTestId("login-btnlogin").click();
        waitForLoad();
    }

    protected boolean isUserLoggedIn() {
        return getDriver().getCurrentUrl().matches(".*/cmdbuild/ui/#(classes|management).*");//TODO improve this, test session
    }

    protected boolean isUserAtLoginPage() {
        return getDriver().getCurrentUrl().matches(".*/cmdbuild/ui/#login.*");//TODO improve this, test session
    }

    protected boolean loginFormPageIsShowingSecondLoginStep() {
        List<WebElement> userNameStillPresent = getDriver().findElements(By.xpath("//div[@data-testid='login-inputusername']"));
        return getDriver().getCurrentUrl().contains("#login") && !userNameStillPresent.isEmpty() && (ExtjsUtils.isDropDownActive(tenantDropDownLocator, getDriver()) || ExtjsUtils.isDropDownActive(roleDropDownLocator, getDriver()));

    }

    protected void loginFormSelectFirstlanguage() {
        By languageDivCriteria = By.xpath("//div[@data-testid='login-inputlanguage']");
        ExtjsUtils.clickOnDropdownOption(languageDivCriteria, null, getDriver(), true);
        sleep();
    }

    protected void loginFormSelectLanguage(String language) {
        By languageDivCriteria = By.xpath("//div[@data-testid='login-inputlanguage']");
        ExtjsUtils.clickOnDropdownOption(languageDivCriteria, language, getDriver(), true);
        sleep();
    }

    protected void loginFormSelectFirstRoleIfAny() {
        if (ExtjsUtils.isDropDownActive(roleDropDownLocator, getDriver())) {
            ExtjsUtils.clickOnDropdownOption(roleDropDownLocator, null, getDriver(), true);
            sleep();
        }
    }

    protected void loginFormSelectFirstTenantIfAny() {
        if (ExtjsUtils.isDropDownActive(tenantDropDownLocator, getDriver())) {
            ExtjsUtils.clickOnDropdownOption(tenantDropDownLocator, null, getDriver(), true);
            sleep();
        }
    }

    protected void clickLogoutElement() {
        findElementByXPath("//div[@data-testid='header-logout']").click();
        waitForLoad();
    }
    //login utils END
}
