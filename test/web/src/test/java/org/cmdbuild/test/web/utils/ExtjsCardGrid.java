package org.cmdbuild.test.web.utils;

//import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Strings.nullToEmpty;

import java.lang.IllegalArgumentException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Nonnull;

import com.google.common.base.Splitter;
import java.lang.invoke.MethodHandles;
import static org.cmdbuild.test.web.utils.ExtjsUtils.artificialSleep;
import static org.cmdbuild.test.web.utils.ExtjsUtils.fillFormTextField2;
import static org.cmdbuild.test.web.utils.ExtjsUtils.getManagementDetailsWindow;
import static org.cmdbuild.test.web.utils.ExtjsUtils.getMsgBoxButton;
import static org.cmdbuild.test.web.utils.ExtjsUtils.getParent;
import static org.cmdbuild.test.web.utils.ExtjsUtils.getWebDriverFromWebElement;
import static org.cmdbuild.test.web.utils.ExtjsUtils.safeClick;
import static org.cmdbuild.test.web.utils.ExtjsUtils.waitForElementPresence;
import static org.cmdbuild.test.web.utils.ExtjsUtils.waitForElementVisibility;
import static org.cmdbuild.test.web.utils.ExtjsUtils.waitForFirstVisibleElementSatisfyingLocator;
import static org.cmdbuild.test.web.utils.ExtjsUtils.waitForLoad;
import static org.cmdbuild.test.web.utils.ExtjsUtils.waitForPresenceOfNestedElement;
import static org.cmdbuild.test.web.utils.ExtjsUtils.waitForVisibilityOfNestedElement;
import static org.cmdbuild.test.web.utils.UILocators.cardDetailHistoryGrid;
import static org.cmdbuild.test.web.utils.UILocators.cardDetailsCloseButton;
import static org.cmdbuild.test.web.utils.UILocators.cardDetailsRelationsGrid;
import static org.cmdbuild.test.web.utils.UILocators.cmdbuildManagementDetailsWindowLocator;
import static org.cmdbuild.test.web.utils.UILocators.gridRowsTable;
import static org.cmdbuild.test.web.utils.UILocators.locatorAutocontainerInnerCt;
import static org.cmdbuild.test.web.utils.UILocators.locatorCardDetailsWindowBody;
import static org.cmdbuild.test.web.utils.UILocators.locatorCardDetailsWindowBodyContent;
import static org.cmdbuild.test.web.utils.UILocators.locatorCardDetailsWindowTabNotesContentPanelreadOnly;
import static org.cmdbuild.test.web.utils.UILocators.locatorInRowFormEditButton;
import static org.cmdbuild.test.web.utils.UILocators.locatorOpenCardButton;
import org.openqa.selenium.By;
import org.openqa.selenium.By.ByClassName;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.WrapsDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TODO: when class becomes too fat, separate grid utilities from details utilities creating one or more further utility classes
/**
 * Class for easy access and search the whole content of a generic grid of
 * cards. Also provides interaction whith the grid.
 * if you manipulate interface using search, ordering and CRUD
 * operations the instance must be refreshed.
 *
 */
public class ExtjsCardGrid {

    private final static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    //Locators (configurable?)
    protected static By locatorCMDBuildManagementContent = By.id("CMDBuildManagementContent");
    protected static By locatorGridHeader = By.className("x-grid-header-ct");
    protected static By locatorColumnHeader = By.className("x-leaf-column-header");

    protected static By locatorCardsToolbar = By.className("x-toolbar-default");

    protected ArtificialTestDelayListener artificialTestTimeListener;
    protected final WebDriver driver;
    protected List<String> fields = new ArrayList<>();
    protected Map<String, String> fieldIds = new HashMap<>(); // maps dynamic ids assigned by extjs to fields (headers)
    protected List<List<GridCell>> rows = new ArrayList<>();

    //Defaults
    protected static int defaultWaitForGridIsStablePollingIntervalMillis = 50;
    protected static int defaultMaxIterationsWithoutChange = 20;
    protected static int defaultMaxIterations = 60;
    protected static long defaultMaxWaitForTableBeforeInferGridIsEmpty = 1000; //Todo: a much lower value should be ok.

    public ExtjsCardGrid(@Nonnull WebDriver driver) {
        this.driver = driver;
    }

    public ExtjsCardGrid(@Nonnull WebDriver driver, ArtificialTestDelayListener artificialTestTimeListener) {
        this.driver = driver;
        this.artificialTestTimeListener = artificialTestTimeListener;
    }

    /**
     * @param listener only one listener can be associated
     */
    public void registerArtificialTestTimeListener(ArtificialTestDelayListener listener) {
        this.artificialTestTimeListener = listener;
    }

    public List<List<GridCell>> getRows() {
        return rows;
    }

    public List<String> getFields() {
        return fields;
    }

    public boolean containsAnyCells(GridCell... cells) {
        if (getIndexOfRowsCointainingAnyCells(cells).size() > 0) {
            return true;
        }
        return false;
    }

    public boolean containsAnyCells(List<FormField> fields) {
        List<GridCell> cells = formfieldsToCells(fields);
        if (getIndexOfRowsCointainingAnyCells((GridCell[]) cells.toArray()).size() > 0) {
            return true;
        }
        return false;
    }

    public boolean hasRowContainingAllCells(GridCell... cells) {
        return !getRowsContainingAllCells(cells).isEmpty();
    }

    public boolean hasRowContainingAllCells(List<GridCell> cells) {
        return !getRowsContainingAllCells(cells).isEmpty();
    }

    public List<List<GridCell>> getRowsContainingAllCells(List<GridCell> cells) {
        List<List<GridCell>> matchingRows = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            List<GridCell> row = rows.get(i);
            if (rowContainingAllCells(row, cells)) {
                matchingRows.add(row);
            }
        }
        return matchingRows;
    }

    public boolean hasRowContainingAllFields(List<FormField> fields) {
        GridCell[] cells = formfieldsToCellArray(fields);
        return !getRowsContainingAllCells(cells).isEmpty();
    }

    public List<List<GridCell>> getRowsContainingAnyCells(GridCell... cells) {

        List<List<GridCell>> matchingRows = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            List<GridCell> row = rows.get(i);
            if (rowContainingAnyCells(row, cells)) {
                matchingRows.add(row);
            }
        }
        return matchingRows;
    }

    public List<List<GridCell>> getRowsContainingAllCells(GridCell... cells) {

        List<List<GridCell>> matchingRows = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            List<GridCell> row = rows.get(i);
            if (rowContainingAllCells(row, cells)) {
                matchingRows.add(row);
            }
        }
        return matchingRows;
    }

    public List<Integer> getIndexOfRowsCointainingAnyCells(GridCell... cells) {

        List<Integer> matchingRows = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            List<GridCell> row = rows.get(i);
            if (rowContainingAnyCells(row, cells)) {
                matchingRows.add(new Integer(i));
            }
        }
        return matchingRows;
    }

    public List<Integer> getIndexOfRowsCointainingAllCells(GridCell... cells) {

        List<Integer> matchingRows = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            List<GridCell> row = rows.get(i);
            if (rowContainingAllCells(row, cells)) {
                matchingRows.add(new Integer(i));
            }
        }
        return matchingRows;
    }

    private boolean rowContainingAnyCells(List<GridCell> row, GridCell... searchCells) {
        for (GridCell cell : row) {
            for (GridCell sc : searchCells) {
                if (cell.matches(sc)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean rowContainingAllCells(List<GridCell> row, GridCell... searchCells) {

        for (GridCell cell : searchCells) {
            boolean found = false;
            for (GridCell sc : row) {
                if (cell.matches(sc)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    private boolean rowContainingAllCells(List<GridCell> row, List<GridCell> searchCells) {

        for (GridCell cell : searchCells) {
            boolean found = false;
            for (GridCell sc : row) {
                if (cell.matches(sc)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    //refactoring...
    //FIXME #397 fails on new UI when empty (timeout triggered) [under dev, check regression]
    public ExtjsCardGrid refresh() {

        rows.clear();
        fields.clear();
        fieldIds.clear();

        waitForLoad(driver);//almost useless

        //ExtjsCardGrid grid = this; //FIXME when finished refactoring (and code is stable)
        WebElement cmdbuildManagementContentSection = waitForElementVisibility(driver, locatorCMDBuildManagementContent);//  findElementById(driver,"CMDBuildManagementContent");
        WebElement gridHeaderWE = waitForPresenceOfNestedElement(driver, locatorGridHeader, locatorCMDBuildManagementContent);
        waitForElementVisibility(driver, gridHeaderWE);
        waitForElementVisibility(driver,
                waitForPresenceOfNestedElement(driver, locatorColumnHeader, locatorGridHeader));

        //waitForElementVisibility(driver, locatorGridHeader); //TODO does not guarantee it's CMDBuildManagementContent specific
        WebElement headerSection = cmdbuildManagementContentSection.findElement(locatorGridHeader);
        // extract headers (fields)
        List<WebElement> extHeaders = headerSection.findElements(locatorColumnHeader);
        for (WebElement extHeader : extHeaders) {
            WebElement headerTextSpan = extHeader.findElement(By.className("x-column-header-text-inner"));
            String fieldName = nullToEmpty(headerTextSpan.getText()).trim(); //found " " case as well...
            String fieldId = headerTextSpan.getAttribute("id").replace("-textInnerEl", "");
            if ((!fieldName.isEmpty())) {
                fields.add(fieldName);
                fieldIds.put(fieldId, fieldName);
            }
        }
        // extract cell content
        //TODO manage Exception (empty table) or skip this step!!!!!!!!!!!!!!!!!!!!!!!!!
        Optional<WebElement> table = waitForPresenceOfNestedElement(driver, gridRowsTable(), locatorCMDBuildManagementContent, defaultMaxWaitForTableBeforeInferGridIsEmpty);
        if (table.isPresent()) {
            waitForElementVisibility(driver, table.get());

            List<WebElement> gridRows = cmdbuildManagementContentSection.findElements(gridRowsTable());
            for (WebElement gridRow : gridRows) {
                List<GridCell> row = new ArrayList<>();
                rows.add(row);
                List<WebElement> gridCells = gridRow.findElements(By.tagName("td"));
                for (WebElement gridCell : gridCells) {
                    try {// data-columnid not always present
                        String field = nullToEmpty(fieldIds.get(gridCell.getAttribute("data-columnid"))).trim();//.replace("gridcolumn-", ""));
                        if (!field.isEmpty()) {
                            String content = gridCell.getText();
                            row.add(new GridCell(field, content));
                        }
                    } catch (Exception e) {//Silent
                    }
                }
            }
        } else { //gtid is empty, so table is missing
            if (artificialTestTimeListener != null) {
                artificialTestTimeListener.notifyArtificialDelayTime(defaultMaxWaitForTableBeforeInferGridIsEmpty);
            }
            //additional time was spent to detect an empty grid
        }
        return this;
    }

    /**
     * @deprecated since 2018-11 due to behavior change in UI. Use expandGridRowCheckingIfAlreadyOpen instead
     *
     * @param rowIndex zero based
     * @return grid row webelement
     */
    @Deprecated
    public WebElement expandGridRow(int rowIndex) {
        List<WebElement> gridRows = contentSection().findElements(gridRowsTable());
        WebElement gridRow = gridRows.get(rowIndex);
        WebElement expander = gridRow.findElement(By.className("x-grid-row-expander"));
        expander.click();
        //return gridRow;
        return contentSection().findElements(gridRowsTable()).get(rowIndex);
    }

    /**
     * <p>This version is safer than expandGridRow because checks if row is already open</p>
     *
     * @param rowIndex zero based
     * @return grid row webelement
     */
    public WebElement expandGridRowCheckingIfAlreadyOpen(int rowIndex) {

        if (isGridRowExpanded(rowIndex)) {
            collapseGridRow(rowIndex);
        }
        List<WebElement> gridRows = contentSection().findElements(gridRowsTable());
        WebElement gridRow = gridRows.get(rowIndex);
        WebElement expander = gridRow.findElement(By.className("x-grid-row-expander"));
        expander.click();
        //return gridRow;
        return contentSection().findElements(gridRowsTable()).get(rowIndex);
    }

    public boolean isGridRowExpanded(int rowIndex) {

        List<WebElement> gridRows = contentSection().findElements(gridRowsTable());
        WebElement gridRow = gridRows.get(rowIndex);
        if (gridRow.getAttribute("class").contains("x-grid-item-selected")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean collapseGridRow(int rowIndex) {

        if (!isGridRowExpanded(rowIndex)) {
            return false;
        }
        List<WebElement> gridRows = contentSection().findElements(gridRowsTable());
        WebElement gridRow = gridRows.get(rowIndex);
        WebElement collapser = gridRow.findElement(By.className("x-grid-row-expander"));
        collapser.click();
        return true;
    }

    public void editCard() {
        LocalDateTime start = LocalDateTime.now();
        WebElement editButton = waitForElementVisibility(driver, locatorInRowFormEditButton());

        editButton.click();
    }

    /*
	 * PAY ATTENTION
	 * Replaced CLICK WITH ENTER becauase click failed in cases where button is visible but not clickable (near page boundaries)
     */
    /**
     * TODO modelled from editCard, needs a revision
     * FIXME REQUIRED: seems to work only for 1 or first element
     *
     * @param gridRowWebElement row as returned from others methods of this class
     * @return CMDBuildManagementDetailsWindow element for further actions
     */
    @Deprecated //see fixme
    public WebElement openCard(WebElement gridRowWebElement) {
        LocalDateTime start = LocalDateTime.now();
//		WebElement openButton = waitForElementVisibility(driver, By.xpath("//div[@aria-label='Open card']"));
//		WebElement openButton = waitForPresenceOfNestedElement(driver, By.xpath("//div[@aria-label='Open card']"), gridRowWebElement);
        WebElement openButton = waitForPresenceOfNestedElement(driver, locatorOpenCardButton(), gridRowWebElement); //works only for English but we can force language
        //openButton.sendKeys(Keys.ENTER);
        //FIXME: viene preso quello della riga predente
        safeClick(driver, openButton);
//		openButton.click();
        WebElement cmdbuildManagementDetailsWindow = waitForElementVisibility(driver, cmdbuildManagementDetailsWindowLocator());
//		WebElement cmdbuildManagementDetailsWindow = waitForElementPresence(driver, cmdbuildManagementDetailsWindowLocator()); //FIXME 2018-08 visibility no more working
        LOGGER.warn("openCard took {} ms", ChronoUnit.MILLIS.between(start, LocalDateTime.now()));
        return cmdbuildManagementDetailsWindow;
    }

    //This version works, but open button must be clickable (visible)
    public WebElement openCardAlt(WebElement gridRowWebElement) {
        LocalDateTime start = LocalDateTime.now();
        List<WebElement> obs = gridRowWebElement.findElements(By.xpath("//*[@data-testid='cards-card-view-openBtn']"));
        LOGGER.info("Found {} openbuttons... (please remove me when done", obs.size());
        WebElement openButton = obs.stream().filter(b -> b.isEnabled() && b.isDisplayed()).findFirst().get();
        openButton.sendKeys(Keys.ENTER);
        WebElement cmdbuildManagementDetailsWindow = waitForElementVisibility(driver, cmdbuildManagementDetailsWindowLocator());
        LOGGER.warn("openCard took {} ms", ChronoUnit.MILLIS.between(start, LocalDateTime.now()));
        return cmdbuildManagementDetailsWindow;
    }

    //NOT WORKING AS EXPECTED
    //probably wait for element visibility picks up the first (hidden instance) of open card button, which is invisible
    public WebElement openCardWaitingForOpenButtonClickable(WebDriver driver, WebElement gridRowWebElement) {
        LocalDateTime start = LocalDateTime.now();

        WebElement openButton = waitForFirstVisibleElementSatisfyingLocator(driver, By.xpath("//*[@aria-label='Open card']"));
        //openButton.click();
        artificialSleep(100, null);
        safeClick(driver, openButton);
        WebElement cmdbuildManagementDetailsWindow = waitForElementVisibility(driver, cmdbuildManagementDetailsWindowLocator());
        LOGGER.warn("openCard took {} ms", ChronoUnit.MILLIS.between(start, LocalDateTime.now()));
        return cmdbuildManagementDetailsWindow;
    }

    /**
     * @param columnName 
     * 
     * fails if not found
     */
    public void clickOnGridHeader(@Nonnull String columnName) {
        WebElement gridHeader = contentSection().findElement(ByClassName.className("x-grid-header-ct"));
        List<WebElement> columnHeaders = gridHeader.findElements(ByClassName.className("x-column-header"));
        Optional<WebElement> header = columnHeaders.stream().filter(h -> columnName.equals(h.getText())).findFirst();
        header.get().click();
    }

    public int rows() {
        return getRows().size();
    }

    public static ExtjsCardGrid extract(WebDriver driver) {

        ExtjsCardGrid grid = new ExtjsCardGrid(driver);
        LocalDateTime start = LocalDateTime.now();
        grid.waitForGridIsStable();
        LOGGER.info("GRID STABILITY took: {} ms to complete",
                ChronoUnit.MILLIS.between(start, LocalDateTime.now()));
        grid.refresh();
        return grid;
    }

    public static ExtjsCardGrid extract(WebDriver driver, ArtificialTestDelayListener listener) {

        ExtjsCardGrid grid = new ExtjsCardGrid(driver, listener);
        LocalDateTime start = LocalDateTime.now();
        long artificialTime = grid.waitForGridIsStable();
        LOGGER.info("GRID STABILITY took: {} ms to complete, of which {} ms weere artificially introduced by stability check to  assure grid stability",
                ChronoUnit.MILLIS.between(start, LocalDateTime.now()),
                artificialTime);
        grid.refresh();
        return grid;
    }

    private WebElement contentSection() {
        return driver.findElement(locatorCMDBuildManagementContent);
    }

    public int getColumnIndexOfColumn(String columnName) {
        return fields.indexOf(columnName);
    }

    /**
     * 
     * fails if not found
     */
    public WebElement getHeaderWebElement(String columnName) {
        WebElement gridHeader = contentSection().findElement(ByClassName.className("x-grid-header-ct"));
        List<WebElement> columnHeaders = gridHeader.findElements(ByClassName.className("x-column-header"));
        Optional<WebElement> header = columnHeaders.stream().filter(h -> columnName.equals(h.getText())).findFirst();
        return header.get();
    }

    /**
     * @return time spent (milliseconds) to assure the grid is stable, not imputable to client
     * 
     */
    public long waitForGridIsStable() {
        return waitForGridIsStable(defaultWaitForGridIsStablePollingIntervalMillis, defaultMaxIterationsWithoutChange, defaultMaxIterations);
    }

    /**
     * @return time spent (milliseconds) to assure the grid is stable, not imputable to client
     * 
     */
    public long waitForGridIsStable(int pollingIntervalMillis, int maxIterationsWithoutChange, int maxIterations) {

        waitForLoad(driver);//almost useless
//		LocalDateTime stabilityCheckStart = LocalDateTime.now();

        WebElement cmdbuildManagementContentSection = waitForElementVisibility(driver, locatorCMDBuildManagementContent);
        WebElement gridHeaderWE = waitForPresenceOfNestedElement(driver, locatorGridHeader, locatorCMDBuildManagementContent);
        waitForElementVisibility(driver, gridHeaderWE);
        waitForElementVisibility(driver,
                waitForPresenceOfNestedElement(driver, locatorColumnHeader, locatorGridHeader));

        int previoustableSize = 0;
        int iterationsWithoutChange = 0;
        int lastIterationWithChange = 0;
        int i;
        for (i = 0; iterationsWithoutChange < maxIterationsWithoutChange && i < maxIterations; i++) {
            List<WebElement> gridRows = cmdbuildManagementContentSection.findElements(gridRowsTable());
            if (gridRows.size() == previoustableSize) {
                iterationsWithoutChange++;
            } else {
                LOGGER.info("Checking content grid stability: grid rows number change detected: from {} to {}", previoustableSize, gridRows.size());
                previoustableSize = gridRows.size();
                iterationsWithoutChange = 0;
                lastIterationWithChange = i;
            }
            try {
                Thread.sleep(pollingIntervalMillis);
            } catch (InterruptedException e) {
            }
        }

        long timeArtificiallyIntroducedByStabilityCheck = 0;
        //	long timeSpent = ChronoUnit.MILLIS.between(stabilityCheckStart, LocalDateTime.now());
        if (lastIterationWithChange > 0) {
            timeArtificiallyIntroducedByStabilityCheck = (i - lastIterationWithChange) * pollingIntervalMillis;
        }
        timeArtificiallyIntroducedByStabilityCheck = Math.max(timeArtificiallyIntroducedByStabilityCheck, 0);
        if (artificialTestTimeListener != null) {
            artificialTestTimeListener.notifyArtificialDelayTime(timeArtificiallyIntroducedByStabilityCheck);
        }
        return timeArtificiallyIntroducedByStabilityCheck;
    }

    public void closeCardDetails() {
        WebElement closeButton = waitForVisibilityOfNestedElement(driver, cardDetailsCloseButton(), cmdbuildManagementDetailsWindowLocator());
        closeButton.click();
    }

    public void deleteCard(WebElement gridRow) {

        LocalDateTime start = LocalDateTime.now();
        WebElement deleteButton = ExtjsUtils.findElementByXPath(driver, "//div[@data-testid='cards-card-view-deleteBtn']");
        deleteButton.click();
        WebElement confirmationButton = ExtjsUtils.findElementByXPath(driver, "//span[text()='Yes']");
        //Optional<WebElement> confirmationButton = msgBox.findElements(By.tagName("a")).stream().filter(b -> "Yes".equalsIgnoreCase(nullToEmpty(b.getText()))).findAny();
        safeClick(driver, confirmationButton);
    }

    /**
     * Can be used only with simple cards (when no edit is needed or wanted to clone the card)
     * Not working if card contatins reference fields
     *
     * @param gridRow expanded row of grid representing card to clone
     */
    public WebElement cloneCard(WebElement gridRow) {

        LocalDateTime start = LocalDateTime.now();
        //WebElement cloneButton = waitForElementVisibility(driver, By.xpath("//div[@aria-label='Clone card']")); //works only for English but we can force language
        //above: does not work on second iteration, perhaps because the one (hidden) of first row is found instead of the right one.
        //It seems (Umberto) that extjs keep expanded section of previously expanded rows attached to the dom (not visible)
        WebElement cloneButton = gridRow.findElement(By.xpath("//div[@data-testid='cards-card-view-cloneBtn']"));
        //WebElement cloneButton = ExtjsUtils.findElementByTestId(driver, "cards-card-view-cloneBtn");
        safeClick(driver, cloneButton);
        waitForLoad(driver);
        sleepDeprecated(2000);
        WebElement cloneButton2 = ExtjsUtils.findElementByXPath(driver, "//a[@data-qtip= 'Clone']");  //prende il primo
        safeClick(driver, cloneButton2);
        //cloneButton.sendKeys(Keys.ENTER); //fails on iteration 2 (not visible)
        //   WebyElement cmdbuildManagementDetailsWindow = waitForElementPresence(driver, By.id("CMDBuildManagementDetailsWindow-bodyWrap"));
        sleepDeprecated(1200);
        WebElement cmdbuildManagementDetailsWindow = waitForElementVisibility(driver, cmdbuildManagementDetailsWindowLocator());
        LOGGER.warn("CloneCard took {} ms", ChronoUnit.MILLIS.between(start, LocalDateTime.now()));
        return cmdbuildManagementDetailsWindow;
    }

    public WebElement cloneCard2(WebElement gridRow) {

        LocalDateTime start = LocalDateTime.now();
        sleepDeprecated(500);

        WebElement cloneButtonInitialWait = waitForElementPresence(driver, By.xpath("//a[@data-qtip= 'Clone']"));
        List<WebElement> cloneButtons = driver.findElements(By.xpath("//a[@data-qtip= 'Clone']"));
        LOGGER.error("Found {} clonebuttons", cloneButtons.size());
        List<WebElement> enabledCloneButtons = cloneButtons.stream().filter(cb -> cb.isEnabled()).collect(Collectors.toList());
        LOGGER.error("Enabled clonebuttons found: {}", enabledCloneButtons.size());
        List<WebElement> displayedCloneButtons = cloneButtons.stream().filter(cb -> cb.isDisplayed()).collect(Collectors.toList());
        LOGGER.error("Displayed clonebuttons found: {}", displayedCloneButtons.size());
        Optional<WebElement> cloneButton = cloneButtons.stream().filter(cb -> cb.isDisplayed()).findFirst();
        LOGGER.error("CloneButton present? {} ", cloneButton.isPresent());
        safeClick(driver, cloneButton.get());
        //cloneButton.sendKeys(Keys.ENTER); //fails on iteration 2 (not visible)
        //   WebElement cmdbuildManagementDetailsWindow = waitForElementPresence(driver, By.id("CMDBuildManagementDetailsWindow-bodyWrap"));
        sleepDeprecated(1200);
        WebElement cmdbuildManagementDetailsWindow = waitForElementVisibility(driver, cmdbuildManagementDetailsWindowLocator());
        LOGGER.warn("CloneCard took {} ms", ChronoUnit.MILLIS.between(start, LocalDateTime.now()));
        return cmdbuildManagementDetailsWindow;
    }

    /*
	 * PAY ATTENTION
	 * Replaced CLICK WITH ENTER beacuase click failed in cases where button is visible but not clickable (near page boundaries)
     */
    //TODO check if move to ExtjsUtils
    /**
     * Works only with Cmdbuild classes which are leaves in their hierarchy.
     * If invoked in acontext where a superclass is focused it fails (does not choose sublcass) 
     * @return Card details form. Waits for opening of the details form.
     */
    public WebElement newCard() {
        if (isNewCardSubclassSelectionMode()) {
            throw new WebUICMDBuidTestException("Trying to create a new card without required specification of leaf class");
        }
        WebElement addCardButton = ExtjsUtils.findElementByTestId(driver, "classes-cards-grid-container-addbtn");
        addCardButton.click();
        return getManagementDetailsWindow(driver);
    }

    public boolean isNewCardSubclassSelectionMode() {

        String subClassSelectionModeTriggerClass = "x-btn-arrow-right"; //TODO move out
        WebElement toolbar = waitForVisibilityOfNestedElement(driver, locatorCardsToolbar, locatorCMDBuildManagementContent);
        WebElement addCardButton = waitForVisibilityOfNestedElement(driver, By.xpath("//div[@data-testid = 'classes-cards-grid-container-addbtn']"), toolbar);
        WebElement childSpan = addCardButton.findElement(By.tagName("span"));
        if (childSpan.getAttribute("class").contains(subClassSelectionModeTriggerClass)) {
            return true;
        }
        return false;

    }

    //TODO UNDER DEVELOPEMENT
    /**
     * @param leafClass the name of a subclass of parent class that is also a leaf (intermediate classes are not shown) 
     * @return
     */
    public WebElement newCardOfLeafClass(@Nonnull String leafClass) {

        if (!isNewCardSubclassSelectionMode()) {
            throw new WebUICMDBuidTestException("Trying to create a new card of a subclass but the context does not admit it");
        }
        WebElement toolbar = waitForVisibilityOfNestedElement(driver, locatorCardsToolbar, locatorCMDBuildManagementContent);
        WebElement addCardButton = waitForVisibilityOfNestedElement(driver, By.tagName("a"), toolbar);
        LOGGER.info("Add Card button text detected: {}", addCardButton.getText()); //TODO remove
        String menuId = addCardButton.getAttribute("aria-owns");
        LOGGER.info("Menu ID is {}", menuId);

        addCardButton.click();

        By menuItemListLocator = By.id(menuId);
        WebElement menuList = waitForElementVisibility(driver, menuItemListLocator);
        List<WebElement> classList = menuList.findElements(By.tagName("a"));
        WebElement classItemToClick = null;
        for (WebElement classItem : classList) {
            if (leafClass.equals(classItem.getText())) {
                classItemToClick = classItem;
                break;
            }
        }
        if (classItemToClick == null) {
            throw new WebUICMDBuidTestException("Sublcass " + leafClass + " not found in Add Card dropdown");
        }
        classItemToClick.click();

        /*elementi in piu' //
		
		x-btn-arrow-right (ce l'ha solo la versione espandibile
		
		
		* class="x-btn-wrap x-btn-wrap-default-toolbar-small x-btn-arrow x-btn-arrow-right">
		* aria-haspopup="true"
		* aria-owns="menu...
         */
        return getManagementDetailsWindow(driver);
    }

    /*
	 * returns list of history elements (tag: table)
     */
    public List<WebElement> openCardsDetailHistory(WebElement detailsWindow) {
        openCardDetailsSection(By.className("fa-history"), detailsWindow);
        WebElement historyGrid = ExtjsUtils.waitForVisibilityOfNestedElement(driver, cardDetailHistoryGrid(), cmdbuildManagementDetailsWindowLocator());
        List<WebElement> historyRows = historyGrid.findElements(By.tagName("table"));
        return historyRows;
    }

    //TODO use Enum
    public void openCardDetailsSection(By sectionLocator, WebElement detailsWindow) {
        //TODO: use waitfor
//		WebElement leftTab = detailsWindow.findElement(By.className("x-tab-bar-default-docked-left"));
        WebElement leftTab = detailsWindow.findElement(By.className("x-tab-bar-vertical"));
        WebElement spanClassSelection = leftTab.findElement(sectionLocator);
        spanClassSelection.click();
    }

    /*
	 * return list of elements (html tables) containing active relations
     */
    public WebElement openCardsDetailTabRelations(WebElement detailsWindow) {
        openCardDetailsSection(By.className("fa-link"), detailsWindow);
        WebElement detailsWindowsTabMasterDetailContent = ExtjsUtils.waitForVisibilityOfNestedElement(driver, locatorCardDetailsWindowBody(), cmdbuildManagementDetailsWindowLocator());
        return detailsWindowsTabMasterDetailContent;
    }

    public WebElement openCardsTabMails(WebElement detailsWindow) {
        openCardDetailsSection(By.className("fa-link"), detailsWindow);
        WebElement detailsWindowsTabMasterDetailContent = ExtjsUtils.waitForVisibilityOfNestedElement(driver, locatorCardDetailsWindowBody(), cmdbuildManagementDetailsWindowLocator());
        return detailsWindowsTabMasterDetailContent;
    }

    public List<WebElement> openCardsDetailTabRelationsAndGetRelations(WebElement detailsWindow) {
        openCardDetailsSection(By.className("fa-link"), detailsWindow);
        WebElement relationsGrid = ExtjsUtils.waitForVisibilityOfNestedElement(driver, cardDetailsRelationsGrid(), cmdbuildManagementDetailsWindowLocator());
        List<WebElement> relations = relationsGrid.findElements(By.tagName("table"));
        return relations;
    }

    public WebElement openCardsDetailTabNotes(WebElement detailsWindow) {
        openCardDetailsSection(By.className("fa-sticky-note"), detailsWindow);
        WebElement detailsWindowsTabNotesContent = ExtjsUtils.waitForVisibilityOfNestedElement(driver, locatorCardDetailsWindowBody(), cmdbuildManagementDetailsWindowLocator());
        return detailsWindowsTabNotesContent;
    }

    public WebElement openCardsDetailTabMasterDetails(WebElement detailsWindow) {
        openCardDetailsSection(By.className("fa-th-list"), detailsWindow);
        WebElement detailsWindowsTabMasterDetailContent = ExtjsUtils.waitForVisibilityOfNestedElement(driver, locatorCardDetailsWindowBody(), cmdbuildManagementDetailsWindowLocator());
        return detailsWindowsTabMasterDetailContent;
    }

    public WebElement openCardsDetailTabAttachments(WebElement detailsWindow) {
        openCardDetailsSection(By.className("fa-paperclip"), detailsWindow);
        WebElement detailsWindowsTabNotesContent = ExtjsUtils.waitForVisibilityOfNestedElement(driver, locatorCardDetailsWindowBody(), cmdbuildManagementDetailsWindowLocator());
        return detailsWindowsTabNotesContent;
    }

    //FIXME please structure if needed more than once
    public static List<Relation> relationsFromGrid(List<WebElement> gridOfRelations) {
        List<Relation> relations = new ArrayList<>();
        String currentRelationDescription = null;
        for (WebElement rwe : gridOfRelations) {
            List<WebElement> trows = rwe.findElements(By.tagName("tr"));
            for (WebElement tr : trows) {
                if (!tr.getAttribute("class").contains("x-grid-row")) {//is a relation
                    currentRelationDescription = Splitter.on('(').omitEmptyStrings().trimResults().splitToList(tr.getText()).get(0);
                } else {//is a relation instance
                    List<WebElement> tds = tr.findElements(By.tagName("td"));
                    Relation r = new Relation(rwe, currentRelationDescription, tds.get(0).getText(), tds.get(1).getText(), tds.get(2).getText());
                    relations.add(r);
                }
            }

        }

        return relations;
    }

    //FIXME BROKEN
    public void fillForm(List<FormField> testCreateCardFormFields) {

        WebElement detailsForm = ExtjsUtils.getManagementDetailsWindow(driver);
        for (FormField ff : testCreateCardFormFields) {
            //		clearFormTextField(detailsForm, ff.getName()); //FIXME has to differentiate if combo/text
            fillFormTextField2(driver, detailsForm, ff.getName(), ff.getContent());
        }

    }

    ///STATIC METHODS
    //TODO make public?
    public static String getContent(@Nonnull List<GridCell> row, @Nonnull String fieldName) throws NoSuchElementException {
        return row.stream().filter(c -> fieldName.equalsIgnoreCase(c.getName())).findFirst().get().getContent();
    }

    private static GridCell formFieldToCell(@Nonnull FormField ff) {
        return new GridCell(ff.getName(), ff.getContent());
    }

    private static List<GridCell> formfieldsToCells(@Nonnull List<FormField> fields) {
        return fields.stream().map(ff -> new GridCell(ff.getName(), ff.getContent())).collect(Collectors.toList());
    }

    @Deprecated // (from here)
    private static @Nonnull
    GridCell[] formfieldsToCellArray(@Nonnull List<FormField> fields) {
        GridCell[] cells = new GridCell[fields.size()];
        IntStream.range(0, fields.size()).forEach(i -> cells[i] = formFieldToCell(fields.get(i)));
        return cells;
    }

    public static class ColumnState {

        private final String name;
        private final boolean enabled;
        private final int index;

        public ColumnState(String name, boolean enabled, int index) {
            this.name = name;
            this.enabled = enabled;
            this.index = index;
        }

        public String getName() {
            return name;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public int getIndex() {
            return index;
        }

        public String toString() {
            return "( " + getIndex() + " , " + getName() + " , " + isEnabled() + " )";
        }
    }

    protected int hardLimitColumnStatesToRetrieve = 500;

    /**
     * @return list of all columns states
     *
     * Pay attention: must click
     */
    //FIXME: columns can have the same name (it happens), so change loop condition to tag id (or parent tag) instead of text
    public List<ColumnState> getColumnStates() {

        List<ColumnState> columns = new ArrayList<>();
        List<String> fields = getFields();
        List<String> scannedElementsIds = new ArrayList<>();
        if (fields.size() == 0) {
            return columns;
        }
        WebElement firstColumnHeader = getHeaderWebElement(fields.get(0));
        safeClick(driver, firstColumnHeader);
        //TODO please EXTERNALIZE. sequence may change. If it becomes variable then we need to find out another strategy
        //firstColumnHeader.sendKeys(Keys.ARROW_DOWN, Keys.ARROW_DOWN , Keys.ARROW_DOWN, Keys.ENTER, Keys.RIGHT); //not always working
        WebElement currentDropdownTreeElement = firstColumnHeader;
        currentDropdownTreeElement.sendKeys(Keys.ARROW_DOWN);
        sleepDeprecated(100);
        currentDropdownTreeElement = driver.switchTo().activeElement();
        currentDropdownTreeElement.sendKeys(Keys.ARROW_DOWN);
        sleepDeprecated(100);
        currentDropdownTreeElement = driver.switchTo().activeElement();
        currentDropdownTreeElement.sendKeys(Keys.ARROW_DOWN);
        sleepDeprecated(100);
        currentDropdownTreeElement = driver.switchTo().activeElement();
        currentDropdownTreeElement.sendKeys(Keys.ENTER);
        sleepDeprecated(250);
        currentDropdownTreeElement = driver.switchTo().activeElement();
//        currentDropdownTreeElement.sendKeys(Keys.ARROW_RIGHT); currentDropdownTreeElement = driver.switchTo().activeElement();

        WebElement currentElement = driver.switchTo().activeElement();
        int index = 0;
//		boolean again = true;
//		while (again && columns.size() < hardLimitColumnStatesToRetrieve) {
        while (columns.size() < hardLimitColumnStatesToRetrieve) {
            String currentOption = currentElement.getText();
            String currentElementId = currentElement.getAttribute("id");
            if (scannedElementsIds.contains(currentElementId)) {
                break;
            }
            scannedElementsIds.add(currentElementId);
            //TODO: prendere il padre, e vedere che classe e':
            //x-menu-item-checked
            //x-menu-item-unchecked
            WebElement parent = getParent(currentElement);
            boolean enabled = parent.getAttribute("class").contains("x-menu-item-checked");
            columns.add(new ColumnState(currentOption, enabled, index++));
//			logger.info("Current option element id is: {} ; class: {}" , currentElement.getAttribute("id") , currentElement.getAttribute("class"));
//			logger.info("Current option is: {}" , currentElement.getText());
            if (!"x-menu-item-link".equals(currentElement.getAttribute("class"))) {
                break; //TODO perhaps never hit
            }//			again = "x-menu-item-link".equals(currentElement.getAttribute("class"));
//			if (again) {
            currentElement.sendKeys(Keys.ARROW_DOWN);
            sleepDeprecated(100);
            currentElement = driver.switchTo().activeElement();
//			}
        }
        currentElement.sendKeys(Keys.ESCAPE);
        LOGGER.info("Found the following Column states: [ {} ]",
                String.join(" ; "), columns.stream().map(c -> c.toString()).collect(Collectors.toList()));
        return columns;

    }

    //TODO fill comment
    public void toggleColumnState(ColumnState columnToEnable) {

        List<String> fields = getFields();
        //List<String> columnsText = new ArrayList<>();
        WebElement firstColumnHeader = getHeaderWebElement(fields.get(0));
        safeClick(driver, firstColumnHeader);
        //TODO please EXTERNALIZE. sequence may change. If it becomes variable then we need to find out another strategy
        WebElement currentDropdownTreeElement = firstColumnHeader;
        currentDropdownTreeElement.sendKeys(Keys.ARROW_DOWN);
        sleepDeprecated(100);
        currentDropdownTreeElement = driver.switchTo().activeElement();
        currentDropdownTreeElement.sendKeys(Keys.ARROW_DOWN);
        sleepDeprecated(100);
        currentDropdownTreeElement = driver.switchTo().activeElement();
        currentDropdownTreeElement.sendKeys(Keys.ARROW_DOWN);
        sleepDeprecated(100);
        currentDropdownTreeElement = driver.switchTo().activeElement();
        currentDropdownTreeElement.sendKeys(Keys.ENTER);
        sleepDeprecated(250);
        currentDropdownTreeElement = driver.switchTo().activeElement();

        WebElement currentElement = driver.switchTo().activeElement();
        int index = 0;
        while (index < hardLimitColumnStatesToRetrieve && index <= columnToEnable.getIndex()) {
            String currentOption = currentElement.getText();

            //x-menu-item-unchecked
            WebElement parent = getParent(currentElement);
            boolean enabled = parent.getAttribute("class").contains("x-menu-item-checked");
            if (columnToEnable.getIndex() == index && columnToEnable.getName().equals(currentOption)
                    && (columnToEnable.isEnabled() == enabled)) {//found column
                currentElement.sendKeys(Keys.ENTER);
                LOGGER.info("Column state toggled for column: {}", columnToEnable);
                return;
            } else {
                currentElement.sendKeys(Keys.ARROW_DOWN);
                sleepDeprecated(100);
                currentElement = driver.switchTo().activeElement();
                index++;
            }
//			}
        }
        throw new WebUICMDBuidTestException("Column to enable not found: " + columnToEnable.toString());
    }

    public List<FormField> fetchFormFields(WebElement window) {

        List<FormField> fields = new ArrayList<>();
        List<WebElement> rawFields = window.findElements(By.className("x-field"));
        for (WebElement rawField : rawFields) {
            fields.add(FormField.of(rawField.findElement(By.className("x-form-item-label-inner")).getText(),
                    rawField.findElement(By.className("x-form-field")).getText()));
            LOGGER.info(fields.toString());
        }
        return fields;
    }

    /**
     * @return Notes displayed content, in plain text.
     *
     * Notes tab in card's detail must already be open
     */
    public String getTabNotesContent() {
        WebElement windowBodyContent = waitForPresenceOfNestedElement(driver, locatorCardDetailsWindowBodyContent(), locatorCardDetailsWindowBody());
        final StringBuilder sbContent = new StringBuilder();
        List<WebElement> outers = windowBodyContent.findElements(locatorCardDetailsWindowTabNotesContentPanelreadOnly());
        outers.stream().forEach(o -> {
            o.findElements(locatorAutocontainerInnerCt()).stream().filter(i -> i.isDisplayed()) //filter is needed because there are a lot of hidden divs of the same type, with content in no way related to notes. This is a workaround
                    .forEach(i -> {
                        sbContent.append(i.getText());
                    });
        });
        return sbContent.toString();
    }

    @Deprecated
    private void sleepDeprecated(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
        }
    }

    public static class Relation {

        //public Relation() {} //FIXME remove?
        //TODO? expose only a factory method which takes care of instantiation from rootwebelement
        public Relation(WebElement rootTableWebElement, String relationDescription, String type, String code, String description) {
            if (!"table".equals(rootTableWebElement.getTagName())) {
                throw new IllegalArgumentException("Relation root web elemente must be a <table>");
            }
            this.relationDescription = relationDescription;
            this.type = type;
            this.code = code;
            this.description = description;
            this.rootWebElement = rootTableWebElement;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String descriptions) {
            this.description = descriptions;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getRelationDescription() {
            return relationDescription;
        }

        public void setRelationDescription(String relationDescription) {
            this.relationDescription = relationDescription;
        }

        private WebElement rootWebElement; //The <table> element
        private String relationDescription;
        private String description;
        private String code;
        private String type;

        public void remove() {
            //TODO move out locators
            WebElement deleteButton = rootWebElement.findElements(By.className("relations-grid-action")).stream().filter(a -> a.getAttribute("class").contains("fa-trash")).findFirst().get();
            safeClick(deleteButton);
            artificialSleep(500, null);
            getMsgBoxButton(((WrapsDriver) rootWebElement).getWrappedDriver(), "Yes").click();
        }

        public boolean matches(Relation r) {
            if (r == null) {
                return false;
            }
            if (!normalized(code).equals(normalized(r.code))) {
                return false;
            }
            if (!normalized(description).equals(normalized(r.description))) {
                return false;
            }
            if (!normalized(type).equals(normalized(r.type))) {
                return false;
            }
            if (!normalized(relationDescription).equals(normalized(r.relationDescription))) {
                return false;
            }
            return true;
        }

        //TODO //table has class x-grid-item-selected
        public boolean isSelected() {

            return rootWebElement.getAttribute("class").contains("x-grid-item-selected");
//			String selectionStatus = null;
//			try {
//				WebElement tr = rootWebElement.findElement(By.tagName("tr"));
//				selectionStatus = tr.getAttribute("aria-selected");
//			} catch (Exception e) {
//			}
//			if ("true".equals(selectionStatus))
//				return true;
//			return false;
        }

        public boolean isDisabled() {
            return rootWebElement.findElement(By.tagName("tr")).findElement(By.tagName("td")).getAttribute("class").contains("x-item-disabled");
        }

        public static String normalized(String s) {
            return nullToEmpty(s).trim();
        } //TODO move to ExtjsUtils

        public WebElement editRelation() {
            Optional<WebElement> editButton = rootWebElement.findElements(By.className("relations-grid-action")).stream().filter(div -> div.getAttribute("class").contains("x-fa fa-pencil")).findFirst();
            safeClick(editButton.get());
            return waitForElementPresence(getWebDriverFromWebElement(rootWebElement), By.id("popup-edit-relation"));
        }
    }

}
