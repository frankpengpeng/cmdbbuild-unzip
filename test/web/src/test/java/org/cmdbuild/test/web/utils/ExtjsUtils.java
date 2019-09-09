package org.cmdbuild.test.web.utils;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.internal.WrapsDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.google.common.base.Strings.nullToEmpty;
import java.lang.invoke.MethodHandles;
import static org.junit.Assert.assertTrue;
import static org.cmdbuild.test.web.utils.ExtjsUtils.safeClick;
import static org.cmdbuild.test.web.utils.UILocators.*;

public class ExtjsUtils {

    protected static int defaultWaitSeconds = 10;//FIXME externalize to configuration
    protected static int timeoutWaitForElementPresenceSeconds = 20; //TODO make configurable
    protected static int timeoutWaitForElementVisibilitySeconds = 10; //TODO make configurable
    protected static int pollingIntervalMillis = 100; //TODO make configurable

    private final static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static WebElement getParent(WebElement son) {
        return son.findElement(By.xpath(".."));
    }

    public static Optional<WebElement> getFirstAncestorByAttribute(WebElement inner, String searchAttribute, String searchContent) {
        try {
            WebElement current = inner;
            do {
                current = getParent(current);
                LOGGER.debug("PARENT: {}", current.getAttribute(searchAttribute));
            } while (!current.getAttribute(searchAttribute).contains(searchContent));
            return Optional.of(current);
        } catch (NoSuchElementException e) {
            return Optional.empty();
        }
    }

    public static boolean isClass(WebElement element, String cssClass) {
        try {
            return element.getAttribute("class").contains(cssClass);
        } catch (Exception e) {
            LOGGER.warn("Problem in isClass: {}", e.toString());
            return false;
        }
    }

    public static WebElement findElementByTestId(WebDriver driver, String testId) {
        WebElement element = new WebDriverWait(driver, defaultWaitSeconds).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@data-testid='" + testId + "']")));
        return element;
    }

    public static void checkNotStale(WebElement element) {
        checkArgument(!isStale(element), "element = %s is stale", element);
    }

    public static boolean isStale(WebElement element) {
        try {
            element.isEnabled();
            return true;
        } catch (StaleElementReferenceException expected) {
            return false;
        }
    }

    public static By testIdLocator(String testId) {
        return By.xpath("//*[@data-testid='" + testId + "']");
    }

    /**
     * @param driver
     * @param xPath
     * @return
     * 
     * Safely waits for the element to be present
     */
    public static WebElement findElementByXPath(WebDriver driver, String xPath) {
        return (new WebDriverWait(driver, defaultWaitSeconds))
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath(xPath)));
    }

    /**
     * @param driver
     * @param id
     * @return
     * 
     * Safely waits for the element to be present
     */
    public static WebElement findElementById(WebDriver driver, String id) {
        return (new WebDriverWait(driver, defaultWaitSeconds))
                .until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
    }

    public static WebElement fetchClickableWebElement(WebDriver driver, WebElement clickableWebElement) {
        return (new WebDriverWait(driver, defaultWaitSeconds))
                .until(ExpectedConditions.elementToBeClickable(clickableWebElement));
    }

    public static WebElement fetchClickableWebElement(WebDriver driver, By clickableWebElementLocator) {
        return (new WebDriverWait(driver, defaultWaitSeconds))
                .until(ExpectedConditions.elementToBeClickable(clickableWebElementLocator));
    }

    public static Optional<WebElement> fetchOptionalClickableWebElement(WebDriver driver, WebElement clickableWebElement) {
        try {
            WebElement clickable = (new WebDriverWait(driver, defaultWaitSeconds))
                    .until(ExpectedConditions.elementToBeClickable(clickableWebElement));
            return Optional.of(clickable);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     *
     * Use to detect if an element is visible (not always precise)
     * @param driver
     * @param possiblyVisibleWebElement Element you want to know if it is visible
     * @param maxWaitSeconds max time to wait (in seconds)
     * @return
     */
    public static Optional<WebElement> fetchOptionalVisibleElement(WebDriver driver, WebElement possiblyVisibleWebElement, long maxWaitSeconds) {
        try {
            WebElement clickable = (new WebDriverWait(driver, maxWaitSeconds))
                    .until(ExpectedConditions.visibilityOf(possiblyVisibleWebElement));
            return Optional.of(possiblyVisibleWebElement);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     *
     * Use to detect if an element is visible (not always precise)
     * @param driver
     * @param elementLocator Locator of element you want to know if it is visible
     * @param maxWaitSeconds max time to wait (in seconds)
     * @return
     */
    public static Optional<WebElement> fetchOptionalVisibleElement(WebDriver driver, By elementLocator, long maxWaitSeconds) {
        try {
            WebElement visible = (new WebDriverWait(driver, maxWaitSeconds))
                    .until(ExpectedConditions.visibilityOfElementLocated(elementLocator));
            return Optional.of(visible);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static boolean checkCorrectClassIsDisplayed(WebDriver driver, String expectedSelectedClassName) {
        return ((driver.getCurrentUrl().contains("#classes/" + expectedSelectedClassName + "/cards"))
                && (driver.findElement(By.id("CMDBuildManagementContent")).findElement(By.className("x-title-text"))
                        .getText().toLowerCase().contains(expectedSelectedClassName.toLowerCase())));
    }

    //FIXME does not work with form combos
    @Deprecated
    public static void selectFirstOptionInExtjsCombo(WebDriver driver, WebElement combo) {

        WebElement input = combo.findElement(By.tagName("input"));
        input.click();
        String comboList = input.getAttribute("aria-owns");
        List<String> comboOwnedNodeIds = Splitter.on(' ').trimResults().omitEmptyStrings().splitToList((comboList));
        Optional<String> comboListId = comboOwnedNodeIds.stream().filter(id -> id.contains("picker-list")).findFirst();
        WebElement comboListUL = driver.findElement(By.id(comboListId.get()));
        List<WebElement> options = comboListUL.findElements(By.tagName("li"));
        if (!options.isEmpty()) {
            WebElement clickableOption = (new WebDriverWait(driver, 10))
                    .until(ExpectedConditions.elementToBeClickable(options.get(0)));
            clickableOption.click();
        }
    }

    //FORM RELATED
    public static Optional<WebElement> fetchCombo(WebElement formOrParent, String fieldName) {

        List<WebElement> fields = formOrParent.findElements(By.className("x-field"));
        String fieldNameVariant = fieldName + " *";
        for (WebElement field : fields) {
            if (field.getAttribute("id").startsWith("combo-")) {
                WebElement label = field.findElement(By.className("x-form-item-label-text")); //TODO check (perhaps too restrictive)
                if (fieldName.equals(label.getText()) || fieldNameVariant.equals(label.getText())) {
                    return Optional.of(field);
                }
            }
        }
        return Optional.empty();

    }

    public static boolean clickOnSideNavLeaf(WebDriver driver, String... treePath) {

        WebElement navigationContainer = findElementByTestId(driver, "management-navigation-container");
        boolean foundAndClicked = recursiveLocateAndClickSideNavLeaf(navigationContainer, treePath, 0);
        return foundAndClicked;
    }

    /**
     * 
     * @deprecated Use safe version
     * 
     * @param driver
     * @param treePath tree nodes to open (last one must be leaf)
     * @return true if leaf node was found and opened
     * @throws WebUICMDBuidTestException when leaf was not found, or default checks failed. Default checks test for:
     * <ul>
     * 	<li> URL path is correct</li>
     * 	<li> Content section title reports rightclass name</li>
     * 	<li> </li>
     * </ul>
     * 
     * 
     */
    @Deprecated
    public static void clickOnSideNavLeafWithChecks(WebDriver driver, String... treePath) throws WebUICMDBuidTestException {

        if (!clickOnSideNavLeaf(driver, treePath)) {
            throw new WebUICMDBuidTestException("SideNav Path not found");
        }
        sleep(1000); //FIXME
        assertTrue(driver.getCurrentUrl().contains("#classes/" + treePath[treePath.length - 1] + "/cards"));
        WebElement content = driver.findElement(By.id("CMDBuildManagementContent"));
        assertTrue(content.findElement(By.className("x-title-text")).getText().toLowerCase().contains("cards " + treePath[treePath.length - 1].toLowerCase()));

    }

    /**
     * Consider using ExtjsNavigationMenu methods, instead
     * 
     * @param driver
     * @param expectedUrlPathFragment landing URL must contain: "#classes/" + expectedUrlTreePathFragment +"/cards")
     * @param expectedCmdbuildManagementSectionTitle This string must be contained in the Management section 
     * @param treePath tree nodes to open (last one must be leaf)
     * @return true if leaf node was found and opened
     * @throws WebUICMDBuidTestException when leaf was not found, or default checks failed. Default checks test for:
     * <ul>
     * 	<li> URL path is correct</li>
     * 	<li> Content section title reports rightclass name</li>
     * 	<li> </li>
     * </ul>
     * 
     * 
     */
    //TODO deprecate and empower ExtjsNavigationMenu?
    public static void safelyClickOnSideNavLeafWithChecksAgainst(WebDriver driver, String expectedUrlPathFragment, String expectedCmdbuildManagementSectionTitle, String... treePath) throws WebUICMDBuidTestException {

        if (!safelylickOnSideNavLeaf(driver, treePath)) {
            throw new WebUICMDBuidTestException("SideNav Path not found");
        }
        waitForLoad(driver);
        sleep(1000); //FIXME
//		WebElement content = driver.findElement(By.id("CMDBuildManagementContent"));
        WebElement content = waitForElementVisibility(driver, By.id("CMDBuildManagementContent"));

        boolean fragmentConforms = isURLFragmentConforming(driver, expectedUrlPathFragment);

        LOGGER.info("" + fragmentConforms);
        if (!fragmentConforms) {
            LOGGER.warn("URL Fragment check does not conform with standard paths. Fragment is: {}", expectedUrlPathFragment);
        }
        assertTrue(fragmentConforms || driver.getCurrentUrl().contains(expectedUrlPathFragment));
        assertTrue(content.findElement(By.className("x-title-text")).getText().toLowerCase().contains(expectedCmdbuildManagementSectionTitle.toLowerCase()));

    }

    public static void safelyClickOnSideNavLeafWithChecksAgainstUrlFragment(WebDriver driver, String expectedUrlPathFragment, String... treePath) throws WebUICMDBuidTestException {

        if (!safelylickOnSideNavLeaf(driver, treePath)) {
            throw new WebUICMDBuidTestException("SideNav Path not found");
        }
        waitForLoad(driver);
        sleep(1000); //FIXME
//		WebElement content = driver.findElement(By.id("CMDBuildManagementContent"));
        WebElement content = waitForElementVisibility(driver, By.id("CMDBuildManagementContent"));

        boolean fragmentConforms = isURLFragmentConforming(driver, expectedUrlPathFragment);
        if (!fragmentConforms) {
            LOGGER.warn("URL Fragment check does not conform with standard paths. Fragment is: {}", expectedUrlPathFragment);
        }
        assertTrue(fragmentConforms || driver.getCurrentUrl().contains(expectedUrlPathFragment));
//		assertTrue(driver.getCurrentUrl().contains("#classes/" + expectedUrlPathFragment +"/cards")
//			|| driver.getCurrentUrl().contains("#processes/" + expectedUrlPathFragment +"/instances")
//			|| driver.getCurrentUrl().contains(expectedUrlPathFragment));

    }

    private static boolean isURLFragmentConforming(WebDriver driver, String fragment) {
        LOGGER.info(fragment);
        LOGGER.info(driver.getCurrentUrl());
        return (driver.getCurrentUrl().contains("#classes/" + fragment + "/cards")
                || driver.getCurrentUrl().contains("#processes/" + fragment + "/instances"));
    }

    /**
     * @Prefer safelyClickOnSideNavLeafWithChecksAgainst. Class names in navigation menu can be renamed, so use at your own risk
     * 
     * @param driver
     * @param treePath tree nodes to open (last one must be leaf)
     * @return true if leaf node was found and opened
     * @throws WebUICMDBuidTestException when leaf was not found, or default checks failed. Default checks test for:
     * <ul>
     * 	<li> URL path is correct</li>
     * 	<li> Content section title reports rightclass name</li>
     * 	<li> </li>
     * </ul>
     * 
     * 
     */
    //TODO refactor using safelyClickOnSideNavLeafWithChecksAgainst
    //@Deprecated // use ...Against version: Class names in navigation menu can be overridden
    public static void safelyClickOnSideNavLeafWithDefaultChecks(WebDriver driver, String... treePath) throws WebUICMDBuidTestException {

        if (!safelylickOnSideNavLeaf(driver, treePath)) {
            throw new WebUICMDBuidTestException("SideNav Path not found");
        }
        waitForLoad(driver);
        sleep(2000); //FIXME
        assertTrue(driver.getCurrentUrl().contains("#classes/" + treePath[treePath.length - 1] + "/cards"));
        WebElement content = driver.findElement(By.id("CMDBuildManagementContent"));
        assertTrue(content.findElement(By.className("x-title-text")).getText().toLowerCase().contains("cards " + treePath[treePath.length - 1].toLowerCase()));

    }

    /*
	 * Makes sure tree (root) node is collapsed, so it can be opened safely using clickOnSideNavLeaf... methods
     */
    public static void collapseSideNavRootNode(WebDriver driver, String nodeCaption) {
        WebElement navigationContainer = findElementByTestId(driver, "management-navigation-container");
        List<WebElement> navItems = navigationContainer.findElements(By.tagName("li"));
        Optional<WebElement> navItem = navItems.stream().filter(ni -> {
            WebElement textWrapper = ni.findElement(By.className("x-treelist-item-text"));
            return (textWrapper != null && !Strings.isNullOrEmpty(textWrapper.getText())
                    && nodeCaption.equalsIgnoreCase(textWrapper.getText()));
        }).findFirst();
        String navItemClass = navItem.get().getAttribute("class");
        //x-treelist-item-collapsed x-treelist-item-expanded
        //if expanded collapse
        if (navItemClass.contains("x-treelist-item-expanded")) {
            WebElement expander = navItem.get().findElement(By.className("x-treelist-item-expander"));
            (new Actions(driver)).moveToElement(expander).perform();
            waitForElementVisibility(driver, expander);
        }
    }

    public static boolean safelylickOnSideNavLeaf(WebDriver driver, String... treePath) {

        WebElement navigationContainer = findElementByTestId(driver, "management-navigation-container");
        boolean foundAndClicked = recursiveLocateAndSafelySelectSideNavLeaf(navigationContainer, treePath, 0);
        waitForLoad(driver);
        return foundAndClicked;
    }

    @Deprecated //broken
    private static boolean recursiveLocateAndClickSideNavLeaf(WebElement currentNode, String[] treePath, int depth) {
        List<WebElement> navItems = currentNode.findElements(By.tagName("li"));
        boolean leaf = (treePath.length == depth + 1);
        for (WebElement navItem : navItems) {
            WebElement textWrapper = navItem.findElement(By.className("x-treelist-item-text"));
            if (textWrapper != null && !Strings.isNullOrEmpty(textWrapper.getText()) && treePath[depth].equalsIgnoreCase(textWrapper.getText())) {
                if (leaf) {
                    textWrapper.click();
                    return true;
                } else {
                    WebElement expander = navItem.findElement(By.className("x-treelist-item-expander"));
                    if (expander != null && expander.isEnabled()) {
                        expander.click();
//                        sleep();
                        boolean found = recursiveLocateAndClickSideNavLeaf(navItem, treePath, depth + 1);
                        if (found) {
                            return true;
                        } else {
                            expander.click();
                        }
                    }
                }
            }
        }
        return false;
    }

    //works with already open intermediate nodes as well
    private static boolean recursiveLocateAndSafelySelectSideNavLeaf(WebElement currentNode, String[] treePath, int depth) {

        //TODO: waitforpresence (don't use visibility because an item can be rendered outside the viewport
        List<WebElement> navItems = currentNode.findElements(By.tagName("li"));
        boolean leaf = (treePath.length == depth + 1);
        for (WebElement navItem : navItems) {

            String cssClass = navItem.getAttribute("class");
            boolean collapsed = cssClass.contains("x-treelist-item-collapsed");

            WebElement textWrapper = navItem.findElement(By.className("x-treelist-item-text"));
            if (textWrapper != null && !Strings.isNullOrEmpty(textWrapper.getText())
                    && treePath[depth].equalsIgnoreCase(textWrapper.getText())) {
                if (leaf) {
                    WebDriver driver = ((WrapsDriver) currentNode).getWrappedDriver();
                    (new Actions(driver)).moveToElement(textWrapper).perform(); //does not always work
                    try {
                        textWrapper.click();
                    } catch (Exception e) {
                        LOGGER.error("Could not click a menu node item, trying a workaround....");
                        safeClick(driver, textWrapper);
                    }
                    return true;
                } else {
                    if (leaf) {
                        return false; //FIXME Remove
                    }
                    if (collapsed) {
                        WebElement expander = navItem.findElement(By.className("x-treelist-item-expander"));
                        if (expander != null && expander.isEnabled()) {
                            WebDriver driver = ((WrapsDriver) currentNode).getWrappedDriver();
                            (new Actions(driver)).moveToElement(expander).perform();
                            expander.click();
                            sleep(200);
                            boolean found = recursiveLocateAndSafelySelectSideNavLeaf(navItem, treePath, depth + 1);
                            if (found) {
                                return true;
                            } else { //FIXME ???
                                (new Actions(driver)).moveToElement(expander).perform();
                                expander.click(); //collapse node
                            }
                        }
                    } else {//already expanded
//						boolean found =  recursiveLocateAndClickSideNavLeaf(navItem, treePath, depth +1);
                        boolean found = recursiveLocateAndSafelySelectSideNavLeaf(navItem, treePath, depth + 1);
                        return found;
                    }
                }
            }

        }
        return false;
    }

    /**
     * @param driver
     * @return
     * 
     * Deprecated: use getCardGrid instead
     */
    @Deprecated
    public static List<List<String>> cardGridTextContent(WebDriver driver) {

        ArrayList<List<String>> rows = new ArrayList<>();

        WebElement content = driver.findElement(By.id("CMDBuildManagementContent"));
        List<WebElement> gridRows = content.findElements(By.tagName("table"));
        for (WebElement gridRow : gridRows) {
            ArrayList<String> row = new ArrayList<>();
            rows.add(row);
            List<WebElement> gridCells = gridRow.findElements(By.tagName("td"));
            for (WebElement gridCell : gridCells) {
                row.add(gridCell.getText());
            }
        }
        return rows;
    }

    public static ExtjsCardGrid getCardGrid(WebDriver driver) {
        return ExtjsCardGrid.extract(driver);
    }

    /**
     * @param driver
     * @param searchText 
     * @param clearTextBeforeTyping Whether to clear previous text or not before typing. If null defaults to true.
     * @param sendEnterAfterText Whether to press enter key after havng enetered the text or not before typing. If null defaults to true.
     * @return
     */
    public static boolean setQuickSearch4CardsText(WebDriver driver, String searchText, Boolean clearTextBeforeTyping, Boolean sendEnterAfterText) {

        if (!Boolean.FALSE.equals(clearTextBeforeTyping)) {
            clearQuickSearch4CardsText(driver);
            sleep(1000); //FIXME
        }
        WebElement content = driver.findElement(By.id("CMDBuildManagementContent"));
        List<WebElement> inputs = content.findElements(By.tagName("input"));
        for (WebElement input : inputs) {
            if (input.isDisplayed() && input.isEnabled()
                    && "Search...".equalsIgnoreCase(input.getAttribute("placeholder"))) {//FIXME: Externalize strings
                input.sendKeys(searchText);
                if (!Boolean.FALSE.equals(sendEnterAfterText)) {
                    sleep(1000); //FIXME
                    input.sendKeys(Keys.ENTER);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * @param driver
     * @return list of LogEntries related to browser.
     * Call this method once because it consumes logs! //TODO check
     */
    @Deprecated //use ClientLog
    public static List<LogEntry> fetchBrowserLogEntries(WebDriver driver) {
        return driver.manage().logs().get(LogType.BROWSER).toJson();
    }

    @Deprecated //use ClientLog
    private static LogEntries fetchLogs(WebDriver driver) {
        return driver.manage().logs().get(LogType.BROWSER);
    }

    @Deprecated //use ClientLog
    public static List<LogEntry> filterLogEntries(List<LogEntry> logs, Level severity) {
        return logs.stream().filter(le -> le.getLevel().intValue() >= severity.intValue()).collect(Collectors.toList());
    }

    @Deprecated //use ClientLog
    public static List<LogEntry> filterLogEntries(List<LogEntry> logs, Level severity, String... triggers) {
        List<String> triggerList = Arrays.asList(triggers);
        return logs.stream().filter(le -> le.getLevel().intValue() >= severity.intValue())
                .filter(le -> triggerList.stream().anyMatch(le.toString()::contains))
                .collect(Collectors.toList());
    }

    @Deprecated //use ClientLog
    public static void printLogs(List<LogEntry> logs) {
        LOGGER.info("Reporting Browser logs [unknown filters]");
        logs.stream().forEach(le -> LOGGER.info("@: {} Severity: {} Message: {}", le.getTimestamp(), le.getLevel().getName(), le.getMessage()));
    }

    @Deprecated //use ClientLog
    public static void printLogs(List<LogEntry> logs, String title) {
        LOGGER.info(title);
        logs.stream().forEach(le -> LOGGER.info("@: {} Severity: {} Message: {}", le.getTimestamp(), le.getLevel().getName(), le.getMessage()));
    }

    @Deprecated //use ClientLog
    public static void printLogs(List<LogEntry> logs, Level severity, String... triggers) {
        List<String> triggerList = Arrays.asList(triggers);
        LOGGER.info("Reporting Browser log with severity level of {} or greater and with one of the following keys: {} ...", severity.getName(), triggerList.toString());
        logs.stream().forEach(le -> LOGGER.info("@: {} Severity: {} Message: {}", le.getTimestamp(), le.getLevel().getName(), le.getMessage()));
    }

    public static WebElement getManagementDetailsWindow(WebDriver driver) {

        WebElement details = (new WebDriverWait(driver, 10)) //TODO EXTERNALIZE
                .ignoring(NoSuchElementException.class)
                .pollingEvery(100, TimeUnit.MILLISECONDS)
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@data-testid='cards-card-detailsWindow']")));
        //now make sure details windows is shown by checking its visibility (so form fields and buttons are usable)
        details = (new WebDriverWait(driver, 10)) //TODO externalise? 
                .ignoring(NoSuchElementException.class)
                .pollingEvery(100, TimeUnit.MILLISECONDS)
                .until(ExpectedConditions.visibilityOf(details));

        return details;
    }

    public static String getFormTextFieldContent(WebElement formContainerWebElement, String fieldLabel) {

        Optional<WebElement> field = fetchFormTextField(formContainerWebElement, fieldLabel);
        if (field.isPresent()) {
            WebElement input = field.get().findElement(By.tagName("input"));
            return input.getAttribute("value");
        } else {
            throw new WebUICMDBuidTestException("Cannot find form text field: " + fieldLabel);
        }

    }

    public static Optional<WebElement> fetchFormTextField(WebElement formContainerWebElement, String fieldLabel) {
        List<WebElement> fields = formContainerWebElement.findElements(By.className("x-field"));

        String fieldLabelVariant = fieldLabel + " *";
        for (WebElement field : fields) {
            WebElement label = field.findElement(By.className("x-form-item-label-text")); //In combos the label element has been moved out of x-field element
            String labelText = Strings.nullToEmpty(label.getText()).replace(":", "");
            if (Strings.isNullOrEmpty(labelText)) { //case of combobox, label is outside the element
                WebElement parent = getParent(field);
                List<WebElement> siblingLabels = parent.findElements(By.className("x-form-item-label-text"));
                if (!siblingLabels.isEmpty()) {
                    labelText = Strings.nullToEmpty(siblingLabels.get(0).getText()).replace(":", "");
                } else {
                    Optional<WebElement> formFieldContainer = getFirstAncestorByAttribute(field, "class", "x-form-fieldcontainer");
                    if (formFieldContainer.isPresent()) {
                        List<WebElement> labels = formFieldContainer.get().findElements(By.className("x-form-item-label-text"));
                        if (!labels.isEmpty()) {
                            labelText = Strings.nullToEmpty(labels.get(0).getText()).replace(":", "");
                        }
                    }
                }
            }
            //logger.debug("Field label found: {}" , labelText);
            if (fieldLabel.equals(labelText) || fieldLabelVariant.equals(labelText)) {
                return Optional.of(field);
            }
        }
        return Optional.empty();

    }

    //FIXME not working with textareas & some combos?????
    @Deprecated
    public static void fillFormTextField(WebElement formContainerWebElement, String fieldLabel, String content) {

        LOGGER.info("Filling field {} with value {}", fieldLabel, content);
        Optional<WebElement> field = fetchFormTextField(formContainerWebElement, fieldLabel);
        if (field.isPresent()) {
            WebElement input = null;
            List<WebElement> inputs = field.get().findElements(By.tagName("input"));
            if (inputs.size() > 0) {
                input = inputs.get(0);
            } else {//textArea
                List<WebElement> textareas = field.get().findElements(By.tagName("textarea"));
                if (textareas.size() > 0) {
                    input = textareas.get(0);
                }
            }
            //WebElement input = field.get().findElement(By.tagName("input"));

            //	input.click();
            //	input.sendKeys(Keys.chord(Keys.CONTROL,Keys.CANCEL));
            //	input.sendKeys(Keys.chord(Keys.CONTROL,Keys.BACK_SPACE));
            input.sendKeys(content);
            input.sendKeys(Keys.ENTER);
        } else {
            throw new WebUICMDBuidTestException("Cannot find form text field: " + fieldLabel);
        }

    }

    public static void fillFormTextField2(WebDriver driver, WebElement formContainerWebElement, String fieldLabel, String content) {

        LOGGER.info("Filling field {} with value {}", fieldLabel, content);

        WebElement input = ExtjsUtils.findElementByXPath(driver, "//div[@data-testid='cards-card-detailsWindow']//input[@name='" + fieldLabel + "']");

        LOGGER.info("" + input.isDisplayed());
        if (isCombo(input)) {
            fillCombo(input, fieldLabel, content);
        } else {
            input.sendKeys(content);
            input.sendKeys(Keys.ENTER);
        }
    }

    public static void fillFormTextFieldAdministration(WebDriver driver, WebElement formContainerWebElement, String fieldLabel, String content) {

        LOGGER.info("Filling field {} with value {}", fieldLabel, content);

        WebElement input = ExtjsUtils.findElementByXPath(driver, "//input[@name ='" + fieldLabel + "']");

        LOGGER.info("" + input.isDisplayed());
        if (isCombo(input)) {
            fillCombo(input, fieldLabel, content);
        } else {
            input.sendKeys(content);
            input.sendKeys(Keys.ENTER);
        }
    }

    private static boolean isCombo(@Nullable WebElement input) {
        if (input == null) {
            return false;
        }
        if ("combobox".equals(input.getAttribute("role"))) {
            return true;
        }
        return false;
    }

//FIXME see #409 wait for option list completion, otherwise not all list options could be captured
    //DOES NO MORE IN ANY CASE (some combos require two keydowns to select first value
//	private static void fillCombo(WebElement comboWebElement, String fieldLabel, String content) {
//
//		String comboId = comboWebElement.getAttribute("data-componentid"); //data-componentid="combo-1285"
//		String comboIdBoundView = comboId + "-picker";
//		logger.info("Searching for options with comboIdBoundView : {}" ,comboIdBoundView);
//		comboWebElement.click();
//		comboWebElement.sendKeys(Keys.DOWN);
//		//FIXME some (variable) time is needed to let the list populate, so it is mandatory to introduce a wait
//
//		sleep(200);
//		WebDriver driver = ((WrapsDriver) comboWebElement).getWrappedDriver();
//		//List<WebElement> options = driver.findElements(By.xpath("//li[@data-boundview='" + comboId + "-picker']"));
//		//options.stream().forEach(o-> logger.info("Option {} found for combo {}", o.getText(), fieldLabel));
//		List<WebElement> candidateOptions = driver.findElements(By.tagName("li"));
//		List<WebElement> candidateOptions2 = candidateOptions.stream().filter(o -> "option".equals(o.getAttribute("role"))
//			&& comboIdBoundView.equals(o.getAttribute("data-boundview"))).collect(Collectors.toList());
////		List<WebElement> candidateOptions2 = candidateOptions.stream().filter(o -> "option".equals(o.getAttribute("role"))
////			).collect(Collectors.toList());
//		candidateOptions2.stream().forEach(o-> logger.info("Found candidate option {} with index {} and data-boundview {}", o.getText(), o.getAttribute("data-recordindex"), o.getAttribute("data-boundview") ));
//		Optional<WebElement> option = candidateOptions2.stream().filter(o -> content.equals(o.getText())).findFirst();
//		//Optional<WebElement> option = options.stream().filter(o -> content.equals(o.getText())).findFirst();
//		if (option.isPresent()) {
//			//beware: retrieve all data from webelement before sending escaper to combo (makes current options to vanish)
//			int optionPosition = Integer.parseInt(option.get().getAttribute("data-recordindex"));
//			comboWebElement.sendKeys(Keys.ESCAPE);
//			sleep(100);
//			comboWebElement.click();
//			sleep(100);
//			comboWebElement.sendKeys(Keys.DOWN);
//			logger.info("Combo: navigating to option with index: " ,optionPosition);
//			for (int i = 0; i < optionPosition ; i++) {
//				comboWebElement.sendKeys(Keys.DOWN);
//				sleep(20)
//;			}
//			comboWebElement.sendKeys(Keys.ENTER);
//		} else {
//			throw new WebUICMDBuidTestException("Option " + content + " not found for combo " + fieldLabel);
//		}
//	}
    //FIXME: don't know if it works when co
    public static void fillCombo(WebElement comboWebElement, String fieldLabel, String content) {

        String comboId = comboWebElement.getAttribute("data-componentid"); //data-componentid="combo-1285"
        String comboIdBoundView = comboId + "-picker";
        LOGGER.info("Searching for options with comboIdBoundView : {}", comboIdBoundView);
        comboWebElement.click();
        comboWebElement.sendKeys(Keys.DOWN);
        //FIXME some (variable) time is needed to let the list populate, so it is mandatory to introduce a wait

        sleep(2000);
        WebDriver driver = ((WrapsDriver) comboWebElement).getWrappedDriver();
        //List<WebElement> options = driver.findElements(By.xpath("//li[@data-boundview='" + comboId + "-picker']"));
        //options.stream().forEach(o-> logger.info("Option {} found for combo {}", o.getText(), fieldLabel));
        List<WebElement> candidateOptions = driver.findElements(By.tagName("li"));
        List<WebElement> candidateOptions2 = candidateOptions.stream().filter(o -> "option".equals(o.getAttribute("role"))
                && comboIdBoundView.equals(o.getAttribute("data-boundview"))).collect(Collectors.toList());
//		List<WebElement> candidateOptions2 = candidateOptions.stream().filter(o -> "option".equals(o.getAttribute("role"))
//			).collect(Collectors.toList());
        candidateOptions2.stream().forEach(o -> LOGGER.info("Found candidate option {} with index {} and data-boundview {}", o.getText(), o.getAttribute("data-recordindex"), o.getAttribute("data-boundview")));
        Optional<WebElement> option = candidateOptions2.stream().filter(o -> content.equals(o.getText())).findFirst();
        //Optional<WebElement> option = options.stream().filter(o -> content.equals(o.getText())).findFirst();
        if (option.isPresent()) {
            safeClick(driver, option.get());
        } else {
            throw new WebUICMDBuidTestException("Option " + content + " not found for combo " + fieldLabel);
        }
    }

    public static void clearFormTextField(WebDriver driver, WebElement formContainerWebElement, String fieldLabel) {

        WebElement input = ExtjsUtils.findElementByXPath(driver, "//input[@name ='" + fieldLabel + "']");

        if (isCombo(input)) {
            String comboIdPrefix = input.getAttribute("componentid");
            List<WebElement> clearComboTriggerButton = getWebDriverFromWebElement(input).findElements(By.id(comboIdPrefix + "-trigger-clear"));
            if (!clearComboTriggerButton.isEmpty()) {
                clearComboTriggerButton.get(0).click();
            }
        } else {
            input.clear();
        }

    }

    /**
     * @param formContainerWebElement a form web element
     * @param buttonCaption Caption of the button to click
     * 
     * This method throws exception if no button or more than one button with the same caption are found.
     */
    //FIXME: refactor. return clickable or promote form to Class
    public static void clickDetailFormButton(WebElement formContainerWebElement, @Nonnull String buttonCaption) throws WebUICMDBuidTestException {
        List<WebElement> buttons = formContainerWebElement.findElements(By.className("x-btn-inner")); // TODO: refine isolating section
        List<WebElement> filteredButtons = buttons.stream().filter(b -> buttonCaption.equals(b.getText()))
                .filter(b -> b.isEnabled() && b.isDisplayed()).collect(Collectors.toList());
        if (filteredButtons.size() == 1) {
            filteredButtons.get(0).click();
        } else {
            if (filteredButtons.size() == 0) {
                throw new WebUICMDBuidTestException(WebUICMDBuidTestException.MESSAGE_NO_NAMED_BUTTON_FOUND + buttonCaption + "' found!");
            } else {
                throw new WebUICMDBuidTestException(WebUICMDBuidTestException.MESSAGE_MORE_THAN_ONE_NAMED_BUTTON_FOUND + buttonCaption + "' found!");
            }

        }
    }

    public static boolean isDetailFormButtonDisabled(WebElement formContainerWebElement, String buttonCaption) throws WebUICMDBuidTestException {
        List<WebElement> buttons = formContainerWebElement.findElements(By.className("x-btn-inner")); // TODO: refine isolating section
        List<WebElement> filteredButtons = buttons.stream().filter(b -> buttonCaption.equals(b.getText()))
                .filter(b -> b.isEnabled() && b.isDisplayed()).collect(Collectors.toList());
        if (filteredButtons.size() == 1) {
//			Optional<WebElement> button = getFirstAncestorByAttribute(filteredButtons.get(0), "class", "x-btn");
            Optional<WebElement> button = getFirstAncestorByAttribute(filteredButtons.get(0), "class", "x-toolbar-item");
            if (button.isPresent()) {
                return button.get().getAttribute("class").contains("x-btn-disabled");
            } else {
                throw new WebUICMDBuidTestException(WebUICMDBuidTestException.MESSAGE_NO_NAMED_BUTTON_FOUND + buttonCaption + "' found! (2)");
            }
        } else {
            if (filteredButtons.size() == 0) {
                throw new WebUICMDBuidTestException(WebUICMDBuidTestException.MESSAGE_NO_NAMED_BUTTON_FOUND + buttonCaption + "' found!");
            } else {
                throw new WebUICMDBuidTestException(WebUICMDBuidTestException.MESSAGE_MORE_THAN_ONE_NAMED_BUTTON_FOUND + buttonCaption + "' found!");
            }

        }
    }

    /**
     * 
     * Does not wait for element presence / visibility
     * 
     * @param formContainerWebElement
     * @param buttonCaption
     * @return
     * @throws WebUICMDBuidTestException
     */
    public static Optional<WebElement> getDetailFormButton(WebElement formContainerWebElement, String buttonCaption) throws WebUICMDBuidTestException {
        List<WebElement> buttons = formContainerWebElement.findElements(By.className("x-btn-inner")); // TODO: refine isolating section
        List<WebElement> filteredButtons = buttons.stream().filter(b -> buttonCaption.equals(b.getText()))
                .filter(b -> b.isEnabled() && b.isDisplayed()).collect(Collectors.toList());
        if (filteredButtons.size() == 1) {
            Optional<WebElement> button = getFirstAncestorByAttribute(filteredButtons.get(0), "class", "x-toolbar-item");
            if (button.isPresent()) {
                return button;
            } else {
                throw new WebUICMDBuidTestException(WebUICMDBuidTestException.MESSAGE_NO_NAMED_BUTTON_FOUND + buttonCaption + "' found! (2)");
            }
        } else {
            if (filteredButtons.size() == 0) {
                throw new WebUICMDBuidTestException(WebUICMDBuidTestException.MESSAGE_NO_NAMED_BUTTON_FOUND + buttonCaption + "' found!");
            } else {
                throw new WebUICMDBuidTestException(WebUICMDBuidTestException.MESSAGE_MORE_THAN_ONE_NAMED_BUTTON_FOUND + buttonCaption + "' found!");
            }

        }
    }

    /**
     * Prefer filtered variants.
     * 
     * @param driver instance of webdriver used for current test
     */
//	@Deprecated
//	public static void printAllLogEntries(WebDriver driver) {
//		
//		LogEntries wholeLog = fetchLogs(driver);
//		List<LogEntry> logs = wholeLog.toJson();
//		logs.stream().forEach(le -> logger.info("@: {} Severity: {} Message: {}" , le.getTimestamp() ,le.getLevel().getName(), le.getMessage()));
//	}
//	/**
//	 * @param driver instance of webdriver used for current test
//	 * @param min severity severity level a log must have to be reported
//	 */
//	@Deprecated
//	public static void printLogEntries(WebDriver driver, Level severity) {
//		logger.info("Reporting Browser log with severity level of {} or greater..." , severity.getName());
//		List<LogEntry> logs = fetchLogs(driver).toJson();
//		if (logs.size() > 0)
//			logs.stream().filter(le -> le.getLevel().intValue() >= severity.intValue()) .forEach(le -> logger.info("Severity: {} Message: {}" ,le.getLevel().getName(), le.getMessage()));
//		else
//			logger.info("NO browser log reported with requested severity level");
//	}
//	/**
//	 * @param driver instance of webdriver used for current test
//	 * @param min severity severity level a log must have to be reported
//	 * @param triggers keyword(s) message must contain to be reported (ANY / OR)
//	 */
//	@Deprecated
//	public static void printLogEntries(WebDriver driver, Level severity, String... triggers) {
//		List<String> triggerList = Arrays.asList(triggers);
//		logger.info("Reporting Browser log with severity level of {} or greater and with one of the following keys: {} ..." , severity.getName(), triggerList.toString());
//		List<LogEntry> logs = fetchLogs(driver).toJson();
//		if (logs.size() > 0)
//			logs.stream().filter(le -> le.getLevel().intValue() >= severity.intValue()) 
//			.filter(le -> triggerList.stream().anyMatch(le.toString()::contains))
//			.forEach(le -> logger.info("Severity: {} Message: {}" ,le.getLevel().getName(), le.getMessage()));
//		else
//			logger.info("NO browser log reported with requested severity level and triggers");
//	}
    //TODO NOT TESTED YET (feature not implemented)
    @Deprecated //refine search strategy, xpath probably not working...
    public static boolean clearQuickSearch4CardsText(WebDriver driver) {

        WebElement content = driver.findElement(By.id("CMDBuildManagementContent"));
        //TODO refine xpath as soon as possible (search for id = 'textfield-*-trigger-clear' AND classes) 
        //id=textfield-1048-trigger-clear ,  class: -form-trigger x-form-trigger-default x-form-clear-trigger x-form-clear-trigger-default  x-form-trigger-click
        //TODO VERIFY
//		List<WebElement> clearDivs = content.findElements(By.cssSelector("div#-trigger-clear.x-form-clear-trigger-default"));
//		List<WebElement> clearDivs = content.findElements(By.xpath("//div[@id contains('-trigger-clear')]"));
//		List<WebElement> clearDivs = content.findElements(By.xpath("//div[(@id='textfield-*-trigger-clear') and (@class='x-form-clear-trigger-default')]\""));
        List<WebElement> clearDivs = content.findElements(By.className("x-form-clear-trigger-default")); //FIXME: weak search constraint
        for (WebElement cd : clearDivs) {
            if (cd.isDisplayed() && cd.isEnabled()) {
                cd.click();
                return true;
            }
        }
        return false;
    }

    public static boolean isDropDownActive(By dropdownIdentificationCriteria, WebDriver driver) {

        try {
            List<WebElement> checkIfDropDownDivPresent = driver.findElements(dropdownIdentificationCriteria);
            if (checkIfDropDownDivPresent.size() == 0) {
                return false;
            }
            WebElement dropDownDiv = checkIfDropDownDivPresent.get(0);
            List<WebElement> checkIfDDInputPresent = dropDownDiv.findElements(By.tagName("input"));
            if (checkIfDDInputPresent.size() == 0) {
                return false;
            }
            WebElement dropDownInput = checkIfDDInputPresent.get(0);
            return dropDownInput.isEnabled() && dropDownDiv.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean clickOnDropdownOption(By dropdownIdentificationCriteria, String option, WebDriver driver, boolean defaultToFirstifOptionNotFound) {

        boolean matched = false;
        WebElement dropDownDiv = driver.findElement(dropdownIdentificationCriteria);
        WebElement dropDownInput = dropDownDiv.findElement(By.tagName("input"));
        dropDownInput.click();
        waitForLoad(driver);
        sleep(300);
        String comboList = dropDownInput.getAttribute("aria-owns");
        List<String> comboOwnedNodeIds = Splitter.on(' ').trimResults().omitEmptyStrings().splitToList((comboList));
        Optional<String> comboListId = comboOwnedNodeIds.stream().filter(id -> id.contains("picker-list")).findFirst();
        WebElement comboUL = driver.findElement(By.id(comboListId.get()));
        List<WebElement> comboOptions = comboUL.findElements(By.tagName("li"));
        if (!comboOptions.isEmpty()) {
            int selectedOptionIndex = 0;
            if (!Strings.isNullOrEmpty(option)) {
                OptionalInt matchingOptionIndex = IntStream.range(0, comboOptions.size())
                        .filter(i -> option.equalsIgnoreCase(comboOptions.get(i).getText())).findFirst();
                matched = matchingOptionIndex.isPresent();
                if (matched) {
                    selectedOptionIndex = matchingOptionIndex.getAsInt();
                }
            }
            WebElement clickableOption = (new WebDriverWait(driver, 5))
                    .pollingEvery(100, TimeUnit.MILLISECONDS)
                    .until(ExpectedConditions.elementToBeClickable(comboOptions.get(selectedOptionIndex)));
            // sleep(300);
            if (!matched && !defaultToFirstifOptionNotFound) {
                throw new WebUICMDBuidTestException("Dropdown has no entry matching " + option + " and default to first option is disabled");
            }
            clickableOption.click();
            return matched;
        } else {
            throw new WebUICMDBuidTestException("Dropdown has no clickable option!");
        }
    }

    public static void waitForLoad(WebDriver driver) {

        LocalDateTime start = LocalDateTime.now();
        waitFor(driver, (WebDriver d) -> ((JavascriptExecutor) d).executeScript("return document.readyState").equals("complete"));
        LOGGER.debug("Wait4Load took {} ms", ChronoUnit.MILLIS.between(start, LocalDateTime.now()));
    }

    public static void waitFor(WebDriver driver, ExpectedCondition<Boolean> expectedCondition) {
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(expectedCondition);
    }

    /**
     * @param driver
     * @param locator
     * @return
     */
    public static WebElement waitForElementPresence(WebDriver driver, By locator) {
        LocalDateTime start = LocalDateTime.now();
        WebElement details = (new WebDriverWait(driver, timeoutWaitForElementPresenceSeconds))
                .ignoring(NoSuchElementException.class) //TODO not useful here?? 
                .pollingEvery(pollingIntervalMillis, TimeUnit.MILLISECONDS)
                .until(ExpectedConditions.presenceOfElementLocated(locator));
        LOGGER.debug("waitForElementPresence took {} ms", ChronoUnit.MILLIS.between(start, LocalDateTime.now()));
        return details;
    }

    public static Optional<WebElement> waitForElementPresence(WebDriver driver, By locator, int customMaxWaitSeconds, int customPollingIntervalMillis) {
        LocalDateTime start = LocalDateTime.now();
        WebElement details = null;
        try {
            details = (new WebDriverWait(driver, customMaxWaitSeconds))
                    .ignoring(NoSuchElementException.class) //TODO not useful here??
                    .pollingEvery(customPollingIntervalMillis, TimeUnit.MILLISECONDS)
                    .until(ExpectedConditions.presenceOfElementLocated(locator));
            LOGGER.debug("waitForElementPresence took {} ms", ChronoUnit.MILLIS.between(start, LocalDateTime.now()));
            return Optional.of(details);
        } catch (Exception e) {
            LOGGER.info("No element located by: {} was found in {} seconds", locator.toString(), customMaxWaitSeconds);
            return Optional.empty();
        }

    }

    /**
     * @param driver
     * @param locator
     * @return
     */
    //TODO if needed build full configurable overloaded method
    public static WebElement waitForElementVisibility(WebDriver driver, By locator) {

//        LocalDateTime start = LocalDateTime.now();
        WebElement details = waitForElementPresence(driver, locator);
        details = (new WebDriverWait(driver, timeoutWaitForElementVisibilitySeconds))
                .ignoring(NoSuchElementException.class)
                .pollingEvery(pollingIntervalMillis, TimeUnit.MILLISECONDS)
                .until(ExpectedConditions.visibilityOf(details));
//        LOGGER.warn("waitForElementVisibility took {} ms", ChronoUnit.MILLIS.between(start, LocalDateTime.now()));
        return details;
    }

    public static WebElement waitForElementVisibility(WebDriver driver, WebElement webElement) {

//        LocalDateTime start = LocalDateTime.now();
        WebElement visibleWE = (new WebDriverWait(driver, timeoutWaitForElementVisibilitySeconds))
                .ignoring(NoSuchElementException.class)
                .pollingEvery(pollingIntervalMillis, TimeUnit.MILLISECONDS)
                .until(ExpectedConditions.visibilityOf(webElement));
//        LOGGER.warn("waitForElementVisibility took {} ms", ChronoUnit.MILLIS.between(start, LocalDateTime.now()));
        return visibleWE;
    }

    //DOES NOT WORK WHEN THERE ARE MORE THAN ONE ELEMENT SHARING THE SAME LOCATOR
    public static WebElement waitForClickableElement(WebDriver driver, By locator) {

//        LocalDateTime start = LocalDateTime.now();
        WebElement clickableWE = (new WebDriverWait(driver, timeoutWaitForElementVisibilitySeconds))
                .ignoring(NoSuchElementException.class)
                .pollingEvery(pollingIntervalMillis, TimeUnit.MILLISECONDS)
                .until(ExpectedConditions.elementToBeClickable(locator));
//        LOGGER.warn("waitForElementVisibility took {} ms", ChronoUnit.MILLIS.between(start, LocalDateTime.now()));
        return clickableWE;
    }

    //beware: it's synchronous (blocking)
    //use this when you need to find currently visible element among several elements with same locator; Selenium does not work well.
    //i.e.: button to open a card in the card grids (there are many open buttons which are not visible)
    public static WebElement waitForFirstVisibleElementSatisfyingLocator(WebDriver driver, By locator) {

        LocalDateTime timeout = LocalDateTime.now().plusSeconds(timeoutWaitForElementVisibilitySeconds);
        do {
            try {
                List<WebElement> fitElements = driver.findElements(locator);
                Optional<WebElement> firstDisplayed = fitElements.stream().filter(fe -> fe.isDisplayed()).findFirst();
                if (firstDisplayed.isPresent()) {
                    LOGGER.info("found first visible Element satisfying locator {} in {} ms", locator.toString(), timeoutWaitForElementVisibilitySeconds - ChronoUnit.MILLIS.between(LocalDateTime.now(), timeout));
                    LOGGER.warn("class of element found: {}", firstDisplayed.get().getAttribute("class"));
                    return firstDisplayed.get();// waitForElementVisibility(driver, locator);
                }
                Thread.currentThread().sleep(pollingIntervalMillis);
            } catch (Exception e) {
                LOGGER.error("waitForFirstVisibleElementSatisfyingLocator raised an exception, please check... ", e);
//                e.printStackTrace();
            }
        } while (LocalDateTime.now().isBefore(timeout));

        LOGGER.warn("waitForFirstVisibleElementSatisfyingLocator could not find a visible element for locator: {} after {} ms", locator.toString(), timeoutWaitForElementVisibilitySeconds);
        return null;

    }

    //TODO VERY IMPORTANT implement javascript query to determine how many http calls are pending in client
    public static boolean hasClientPendingHttpRequests() {
        //TODO implment
        return false;
    }

    /**
     * @param driver
     * @param locator
     * @param parentLocator
     * @return
     */
    public static WebElement waitForPresenceOfNestedElement(WebDriver driver, By locator, By parentLocator) {

        LocalDateTime start = LocalDateTime.now();
        WebElement details = waitForElementPresence(driver, locator);
        details = (new WebDriverWait(driver, timeoutWaitForElementPresenceSeconds))
                .ignoring(NoSuchElementException.class)
                .pollingEvery(pollingIntervalMillis, TimeUnit.MILLISECONDS)
                .until(ExpectedConditions.presenceOfNestedElementLocatedBy(parentLocator, locator));
        LOGGER.debug("waitForPresenceOfNestedElement took {} ms", ChronoUnit.MILLIS.between(start, LocalDateTime.now()));
        return details;
    }

    public static Optional<WebElement> waitForPresenceOfNestedElement(WebDriver driver, By locator, By parentLocator, long timeout) {

        LocalDateTime start = LocalDateTime.now();
        try {
            WebElement details = waitForElementPresence(driver, locator);
            details = (new WebDriverWait(driver, timeout))
                    .ignoring(NoSuchElementException.class)
                    .pollingEvery(pollingIntervalMillis, TimeUnit.MILLISECONDS)
                    .until(ExpectedConditions.presenceOfNestedElementLocatedBy(parentLocator, locator));
            LOGGER.debug("waitForPresenceOfNestedElement took {} ms", ChronoUnit.MILLIS.between(start, LocalDateTime.now()));
            return Optional.of(details);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static WebElement waitForVisibilityOfNestedElement(WebDriver driver, By locator, By parentLocator) {

        LocalDateTime start = LocalDateTime.now();
        WebElement nested = waitForPresenceOfNestedElement(driver, locator, parentLocator);
        nested = waitForElementVisibility(driver, nested);

        LOGGER.debug("waitForVisibilityOfNestedElement took {} ms", ChronoUnit.MILLIS.between(start, LocalDateTime.now()));
        return nested;
    }

    public static WebElement waitForVisibilityOfNestedElement(WebDriver driver, By locator, WebElement parent) {

        LocalDateTime start = LocalDateTime.now();
        WebElement nested = waitForPresenceOfNestedElement(driver, locator, parent);
        nested = waitForElementVisibility(driver, nested);

        LOGGER.debug("waitForVisibilityOfNestedElement took {} ms", ChronoUnit.MILLIS.between(start, LocalDateTime.now()));
        return nested;
    }

    public static WebElement waitForPresenceOfNestedElement(WebDriver driver, By locator, WebElement parent) {

        LocalDateTime start = LocalDateTime.now();
        WebElement details = waitForElementPresence(driver, locator);
        details = (new WebDriverWait(driver, timeoutWaitForElementPresenceSeconds))
                .ignoring(NoSuchElementException.class)
                .pollingEvery(pollingIntervalMillis, TimeUnit.MILLISECONDS)
                .until(ExpectedConditions.presenceOfNestedElementLocatedBy(parent, locator));
        LOGGER.debug("waitForPresenceOfNestedElement took {} ms", ChronoUnit.MILLIS.between(start, LocalDateTime.now()));
        return details;
    }

    @Deprecated
    private static void sleep(long millis) {

        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
        }
    }

    public static void setTimeoutWaitForElementPresenceSeconds(int timeoutWaitForElementPresenceSeconds) {
        ExtjsUtils.timeoutWaitForElementPresenceSeconds = timeoutWaitForElementPresenceSeconds;
    }

    public static void setTimeoutWaitForElementVisibilitySeconds(int timeoutWaitForElementVisibilitySeconds) {
        ExtjsUtils.timeoutWaitForElementVisibilitySeconds = timeoutWaitForElementVisibilitySeconds;
    }

    public static void setPollingIntervalMillis(int pollingIntervalMillis) {
        ExtjsUtils.pollingIntervalMillis = pollingIntervalMillis;
    }

    //TODO detailsForm parameter could be retrieved by the method itself
    /**
     * @param driver
     * @param detailsForm 
     * @param fieldName name of the dropdown field
     * @return list of options or null if dropdown not found for fieldName
     */
    public static @Nullable
    List<String> getDetailFormDropDownFieldOptions(WebDriver driver, WebElement detailsForm, String fieldName) {

        waitForVisibilityOfNestedElement(driver, By.tagName("input"), detailsForm);
        List<WebElement> formInputs = detailsForm.findElements(By.tagName("input")); //not relaying on name attribute alone...

        Optional<WebElement> dropDownInput = formInputs.stream().filter(we -> ((we.getAttribute("name").equalsIgnoreCase(fieldName)) && (we.getAttribute("role").equalsIgnoreCase("combobox")))).findFirst();
        if (dropDownInput == null) {
            return null;
        }
        dropDownInput.get().click();
        waitForLoad(driver);
        String dropdownOptionListId = dropDownInput.get().getAttribute("id").replace("-inputEl", "-picker-listEl");
        String dropdownTrriggerId = dropDownInput.get().getAttribute("id").replace("-inputEl", "-trigger-picker");
        WebElement trigger = waitForElementVisibility(driver, By.id(dropdownTrriggerId));
        //sleep(1500);
        trigger.click();
        //sleep(2500);
        // WebElement ulOptions = driver.findElement(By.id(dropdownOptionListId));
        WebElement ulOptions = waitForElementPresence(driver, By.id(dropdownOptionListId));
        List<WebElement> optionList = ulOptions.findElements(By.tagName("li"));
        ArrayList<String> options = new ArrayList<>();
        optionList.stream().forEach(o -> options.add(o.getText()));
        dropDownInput.get().sendKeys(Keys.ESCAPE);
        waitForLoad(driver);
        return options;
    }

    public static WebElement getMsgBoxButton(WebDriver driver, String buttonCaption) {

        //WebElement messageBox = ExtjsUtils.waitForElementVisibility(driver, messageBoxLocator());
        //ExtjsUtils.waitForPresenceOfNestedElement(driver, messageBoxButtonLocator(), messageBox);
        //List<WebElement> msgBoxButtons = messageBox.findElements(messageBoxButtonLocator());
        //Optional<WebElement> button = msgBoxButtons.stream().filter(b -> buttonCaption.equalsIgnoreCase(b.getText())).findFirst();
        WebElement button = ExtjsUtils.findElementByXPath(driver, "//span[text()='" + buttonCaption + "']");
        return button;
    }

    public static void closeDetaiForm(WebDriver driver) {
        WebElement closeToolButton = waitForVisibilityOfNestedElement(driver, cardDetailsCloseToolButton(), getManagementDetailsWindow(driver));
        closeToolButton.click();
    }

    /**
     * Performs click on a webelement even if it's outside viewport
     * @param clickable a clickable web element
     */
    //TODO  * @return millis artificially spent checking for conditions
    public static long safeClick(WebDriver driver, WebElement clickable) {

        Actions actions = new Actions(driver);
        actions.moveToElement(clickable);
        actions.perform();
        //clickable.click(); //not always clickable
        //clickable.sendKeys(Keys.ENTER);NOT WORKING
        long artificialTime = 0;
        LocalDateTime start = LocalDateTime.now();
        Optional<WebElement> clickableWrapper = tryFetchClickableWebElement(driver, clickable, 2);
        if (clickableWrapper.isPresent()) {
//			clickableWrapper.get().click();
            clickable.click();
        } else {
            artificialTime = ChronoUnit.MILLIS.between(start, LocalDateTime.now());
            //FIXME don't know what to do here
            //forceElementIntoView(driver, clickable);
            focus(driver, clickable);
            clickable.sendKeys(Keys.ENTER);
        }
        return artificialTime;
    }

    /**
     *
     * Convenience method for safeClick(Webdriver, WebElement)
     * @param clickableWebElementWrappingDriver
     * @return
     */
    public static long safeClick(WebElement clickableWebElementWrappingDriver) {
        return safeClick(((WrapsDriver) clickableWebElementWrappingDriver).getWrappedDriver(), clickableWebElementWrappingDriver);
    }

    //FIXME wrong
//	private static Optional<WebElement> tryFetchClickableWebElement(WebDriver driver, WebElement clickable, long maxWait) {
//
//
//		try {
//			(new WebDriverWait(driver, maxWait))
//				   .until(ExpectedConditions.elementToBeClickable(clickable));
//			(new WebDriverWait(driver, maxWait))
//				   .until(ExpectedConditions.visibilityOf(clickable));
//			//forceElementIntoView(driver , clickable); makes even working cases to fail
//		} catch (Exception e) {
//			return Optional.empty(); //never got here
//		}
//		return Optional.of(clickable);
//	}
    private static Optional<WebElement> tryFetchClickableWebElement(WebDriver driver, WebElement clickable, long maxWait) {

        WebElement ok = null;
        try {
            ok = (new WebDriverWait(driver, maxWait))// .ignoring(Exception.class)
                    .until(ExpectedConditions.elementToBeClickable(clickable));
        } catch (Exception e) {
            LOGGER.info("element is not clickable... trying to bring it into viewport"); //debug level
        }
        if (ok == null) {
            try {
//				ok = (new WebDriverWait(driver, maxWait))
//						.until(ExpectedConditions.visibilityOf(clickable));
                focus(driver, clickable);
                ok = clickable;
            } catch (Exception e) {
                LOGGER.warn("Failed to bring element into viewport. Exception is: {}", e.toString());
            }
        }
        //forceElementIntoView(driver , clickable); makes even working cases to fail
        if (ok == null) {
            return Optional.empty();
        } else {
            return Optional.of(clickable);
        }
    }

    /**
     *
     * Forces element into viewport using javascript scrolling. Use only when selenium Action moveToElement fails
     *
     * @param driver
     * @param elementToBringIntoView
     */
    public static void forceElementIntoView(WebDriver driver, WebElement elementToBringIntoView) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", elementToBringIntoView);
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
        }
    }

    public static void focus(WebDriver driver, WebElement elementToFocus) {
        new Actions(driver).moveToElement(elementToFocus).perform();
    }

    public static void goFullScreen(WebDriver driver) {
        driver.manage().window().maximize();
    }

//    public static WebElement getParent(WebDriver driver, WebElement element) {
//		return (WebElement) ((JavascriptExecutor) driver).executeScript( "return arguments[0].parentNode;", element);
//	}
    /**
     * @param driver
     * @param context optional
     * @throws WebUICMDBuidTestException if no admin switch option was found in user menu
     */
    //TODO create checked exception for this
    public static void switchToAdminModule(WebDriver driver, @Nullable UITestContext context) throws WebUICMDBuidTestException {

        WebElement headerUserMenuDropDown = waitForElementVisibility(driver, headerUserMenuDropDown());
        WebElement switchtoAdminModuleButton = waitForElementVisibility(driver, headerAdministrationButton());

        focus(driver, switchtoAdminModuleButton);
        assertTrue("Button to switch to administration module should be shown and clickable", switchtoAdminModuleButton.isDisplayed() && switchtoAdminModuleButton.isEnabled());

        safeClick(driver, headerUserMenuDropDown);
        waitForElementPresence(driver, locatorForDropDownMenuItems());
        artificialSleep(500, context); //give dropdown some time to render itself
        List<WebElement> adminSwitchOptions = driver.findElements(locatorForDropDownMenuItems());
        adminSwitchOptions.stream().forEach(e -> LOGGER.info("xmenuitem: {}", e.getText()));
        List<WebElement> filteredAminOptions = adminSwitchOptions.stream().
                //                filter(e -> "span".equals(e.getTagName())).
                filter(e -> "Administration module".equals(e.getText().trim())).collect(Collectors.toList());
        if (filteredAminOptions.size() != 1) {
            throw new WebUICMDBuidTestException(WebUICMDBuidTestException.MESSAGE_NO_ADMIN_SWITCH_OPTIO_FOUND); //TODO create checked exception
        }		//filteredAminOptions.get(0).sendKeys(Keys.ENTER);
        safeClick(driver, filteredAminOptions.get(0));
        //waitForLoad(driver);
    }

    public static void switchToAdminModule2(WebDriver driver, @Nullable UITestContext context) throws WebUICMDBuidTestException {
        WebElement adminButton = ExtjsUtils.findElementByTestId(driver, "header-administration");
        safeClick(adminButton);
    }

    public static void switchToManagementModule(WebDriver driver) {
        WebElement switch2ManagementButton = waitForElementPresence(driver, testIdLocator("header-management"));
        safeClick(switch2ManagementButton);
    }

    protected static void artificialSleep(long millis, @Nullable UITestContext context) {
        try {
            Thread.currentThread().sleep(millis);
        } catch (InterruptedException e) {
        }
        if (context != null) {
            context.notifyArtificialDelayTime(millis);
        }
    }

    public static String normalized(String s) {
        return nullToEmpty(s).trim();
    }

    /**
     * No check is performed to detect if web element really wraps a web driver, but as far no element is known to not carry the driver within
     * @param webElement element from which the driver will be extracted
     * @return WebDriver wrapped by WebElement
     */
    public static WebDriver getWebDriverFromWebElement(@Nonnull WebElement webElement) {
        return ((WrapsDriver) webElement).getWrappedDriver();
    }

    public static Boolean isVisibleInViewport(WebElement element) {
        WebDriver driver = ((RemoteWebElement) element).getWrappedDriver();

        return (Boolean) ((JavascriptExecutor) driver).executeScript(
                "var elem = arguments[0],                 "
                + "  box = elem.getBoundingClientRect(),    "
                + "  cx = box.left + box.width / 2,         "
                + "  cy = box.top + box.height / 2,         "
                + "  e = document.elementFromPoint(cx, cy); "
                + "for (; e; e = e.parentElement) {         "
                + "  if (e === elem)                        "
                + "    return true;                         "
                + "}                                        "
                + "return false;                            ",
                element);
    }

}
