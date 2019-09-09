package org.cmdbuild.test.web.utils;


import com.google.common.base.Strings;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Strings.nullToEmpty;
import static org.cmdbuild.test.web.utils.ExtjsUtils.*;
import static org.cmdbuild.test.web.utils.UILocators.*;

//or call Utils?
public class ExtjsWorkflowGrid {

    protected ArtificialTestDelayListener artificialTestTimeListener;
    protected final WebDriver driver;

    protected List<String> fields = new ArrayList<>();
    protected Map<String, String> fieldIds = new HashMap<>(); // maps dynamic ids assigned by extjs to fields (headers)
    protected List<List<GridCell>> rows = new ArrayList<>();

    private static  Logger logger = LoggerFactory.getLogger(ExtjsWorkflowGrid.class);

    private long defaultMaxWaitForTableBeforeInferGridIsEmptyMillis = 4000;
    private int maxWaitForGridRefreshCheckSeconds = 3; //FIXME: make parametric (refresh, extract)

    public ExtjsWorkflowGrid(@Nonnull WebDriver driver) {this.driver = driver;}

    public ExtjsWorkflowGrid(@Nonnull WebDriver driver , @Nullable  ArtificialTestDelayListener artificialDelayListener) {
        this.driver = driver;
        this.artificialTestTimeListener = artificialDelayListener;
    }

    public static ExtjsWorkflowGrid extract(WebDriver driver) {
        ExtjsWorkflowGrid grid = new ExtjsWorkflowGrid(driver);
        LocalDateTime start = LocalDateTime.now();
        grid.waitForGridIsStable();
        logger.info("Workflow grid stability took: {} ms to complete",
                ChronoUnit.MILLIS.between(start, LocalDateTime.now()));
        grid.refresh();
        return grid;
    }

    public static  ExtjsWorkflowGrid extract(WebDriver driver, ArtificialTestDelayListener adl) {

        ExtjsWorkflowGrid grid = new ExtjsWorkflowGrid(driver, adl);
        LocalDateTime start = LocalDateTime.now();
        long artificialTime  = grid.waitForGridIsStable();
        logger.info("Workflow GRID STABILITY took: {} ms to complete, of which {} ms were artificially introduced by stability check to  assure grid stability",
                ChronoUnit.MILLIS.between(start,LocalDateTime.now()) ,
                artificialTime);
        grid.refresh();
        return grid;

    }
////FIXME see #406 UNDER DEVELOPEMENT needs hard testing
    private  long waitForGridIsStable() {

        long artificialWaitTime = 0;
        Optional<String> innerPanelId = Optional.empty();
        LocalDateTime start = LocalDateTime.now();
        List<WebElement> body = driver.findElements(cmdbuildManagementContentBodyLocator());
        if (body.size() > 0) {
           List<WebElement>  xpanel = body.get(0).findElements(By.className("x-panel"));
           if (xpanel.size() > 0) innerPanelId = Optional.of(xpanel.get(0).getAttribute("id"));
        }
        if (innerPanelId.isPresent()) {//don't know is grid rebuild has already trigger
            FluentWait<WebDriver> wait = (new WebDriverWait(driver, maxWaitForGridRefreshCheckSeconds))
                    .pollingEvery(100, TimeUnit.MILLISECONDS);
            //TODO narrow exceptions ignored please (we must ignore timeot only)
            try {
                wait.until(ExpectedConditions.not(ExpectedConditions.presenceOfElementLocated(By.id(innerPanelId.get()))));
            } catch (org.openqa.selenium.TimeoutException e) {//we need to catch TimeoutException, which is not ignorable
               logger.info("Looks like process grid was already rendered...");
            }
            //check if id of inner panel is the same or has changed
            WebElement finalXPanel = waitForVisibilityOfNestedElement(driver, By.className("x-panel"), cmdbuildManagementContentBodyLocator());
            if (innerPanelId.get().equals(finalXPanel.getAttribute("id")))
                artificialWaitTime = ChronoUnit.MILLIS.between( start, LocalDateTime.now());

        } else {//just wait for element visibility
            //TODO check if further waiting / checks  needed
        }
        waitForVisibilityOfNestedElement(driver, UILocators.workflowXGrid() ,  UILocators.cmdbuildManagementContentLocator());
        return artificialWaitTime;
    }

    public ExtjsWorkflowGrid refresh() {

        rows.clear();
        fields.clear();
        fieldIds.clear();

        WebElement xGrid = waitForVisibilityOfNestedElement(driver, UILocators.workflowXGrid() ,  UILocators.cmdbuildManagementContentLocator());
        WebElement headerWE = waitForVisibilityOfNestedElement(driver, workflowHeader(), xGrid);

        //extract headers
        List<WebElement> headersWE = headerWE.findElements(workflowHeaderColumn());
        headersWE.stream().filter(we -> ! we.getAttribute("class").contains("x-column-header-first"))
                .map(we -> we.findElement(By.className("x-column-header-text-inner")))
                .forEach(h -> {String fieldName = Strings.nullToEmpty(h.getText()).trim();
                    if (fieldName.length() !=0) {
                        String fieldId =  h.getAttribute("id").replace("-textInnerEl", "");
                        fields.add(fieldName); fieldIds.put(fieldId, fieldName) ;
                    }
                });

        logger.info("Found workflow grid headers: {}" , fields.toString() );

        //extract cell content
        Optional<WebElement> table = waitForPresenceOfNestedElement(driver, workflowGridRowsTable(), workflowXGrid(), defaultMaxWaitForTableBeforeInferGridIsEmptyMillis);
        if (table.isPresent()) {
            waitForElementVisibility(driver, table.get());

            List<WebElement> gridRows = xGrid.findElements(workflowGridRowsTable());
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
            logger.info("Workflow grid  has {} rows and is: {}" , rows.size() , rows.toString());

        } else { //empty grid
            logger.info("Found an empty process grid");
            if (artificialTestTimeListener != null)
                artificialTestTimeListener.notifyArtificialDelayTime(defaultMaxWaitForTableBeforeInferGridIsEmptyMillis);
            //additional time was spent to detect an empty grid
        }

        return this;
    }

    public WebElement openStartProcessForm() {

        WebElement startButton = ExtjsUtils.findElementByTestId(driver, "processes-instances-grid-addbtn");
        startButton.click();
        WebElement processWindow = waitForElementVisibility(driver, workflowProcessDetailWindow());
        return  processWindow;
    }

    //FIXME: need to check the locators used
    public WebElement getEditProcessGhostForm() {

        WebElement window = waitForElementPresence(driver, workflowProcessGhostWindowBody());
        return  window;

    }
    //TODO remove?
//    public WebElement getEditProcessGhostWindow() {
//
//        WebElement window = waitForElementPresence(driver, workflowProcessGhostWindow());
//        return  window;
//
//    }

    //TODO collect buttons into enum
    public void clickEditProcessGhostFormButton(String buttonCaption) {
        WebElement window = getEditProcessGhostForm();
        WebElement toolbar = waitForPresenceOfNestedElement(driver, workflowProcessEditGhostWindowToolbar(), window);
//        WebElement toolbar = waitForPresenceOfNestedElement(driver, workflowProcessEditGhostWindowToolbar() , cmdbuildManagementContentLocator());
        List<WebElement> buttons = toolbar.findElements(button());
//        List<WebElement> buttons = window.findElements(button());
        Optional<WebElement> button = buttons.stream().filter(b -> "a".equals(b.getTagName()) &&
                b.findElement(By.className("x-btn-inner")).getText().equalsIgnoreCase(buttonCaption)
        ).findFirst();
        if (button.isPresent())
            safeClick(driver, button.get());
        else
            throw new WebUICMDBuidTestException("Button with caption " + buttonCaption +" not found in edit process ghost window toolbar");

    }

    public void fillForm(List<FormField> formFields) {

        WebElement processDetailsForm = waitForElementVisibility(driver, workflowProcessDetailWindow());
        for (FormField ff : formFields) {
            clearFormTextField(driver, processDetailsForm, ff.getName());
            fillFormTextField2(driver, processDetailsForm, ff.getName(), ff.getContent());
        }

    }

    public List<Integer> getIndexOfRowsCointainingAllCells(GridCell... cells) {

        List<Integer> matchingRows = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            List<GridCell> row = rows.get(i);
            if (rowContainingAllCells(row, cells))
                matchingRows.add(new Integer(i));
        }
        return matchingRows;
    }

    /**
     *
     * @param cells
     * @return the list of indices of rows containing matching all cells, limiting the constraint only to displayed fields in grid row.
     * <br/>i.e.: if the grid does not show a particular field in cells, that field is excluded from checking, but if the field is shown, contents must match
     */
    public List<Integer> getIndexOfRowsCointainingAllDisplayedCells(GridCell... cells) {

        List<Integer> matchingRows = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            List<GridCell> row = rows.get(i);
            if (rowContainingAllDisplayedCells(row, cells))
                matchingRows.add(new Integer(i));
        }
        return matchingRows;
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
            if  (!found) return false;
        }
        return true;
    }

    private boolean rowContainingAllDisplayedCells(List<GridCell> row, GridCell... searchCells) {

        for (GridCell cell : searchCells) {
            if (fields.contains(cell.getName())) {
                if (!row.stream().anyMatch(gc -> gc.matches(cell)))
                    return false;
            }
        }
        return true;
    }

    public List<List<GridCell>> getRows() {
        return rows;
    }

    public List<String> getFields() {
        return fields;
    }

    public int rows() {
        return getRows().size();
    }


    //    //TODO REFACTOR evolved from cardgrid expandgrid row, this version is better but we must guarantee backwards compatibility
    /**
     * @param rowIndex zero based
     * @return grid row webelement
     */
    public WebElement safelyEpandGridRow(int rowIndex) {
        List<WebElement> gridRows = contentSection().findElements(gridRowsTable());
        WebElement gridRow = gridRows.get(rowIndex);
        if (! isGridRowExpanded(rowIndex)) {
            WebElement expander = gridRow.findElement(By.className("x-grid-row-expander"));
//            expander.click();
            safeClick(driver, expander);
        }
        return gridRow;
    }
    //TODO REFACTOR shared with cardgrid
    public boolean isGridRowExpanded(int rowIndex) {
        List<WebElement> gridRows = contentSection().findElements(gridRowsTable());
        WebElement gridRow = gridRows.get(rowIndex);
        if (gridRow.getAttribute("class").contains("x-grid-item-selected"))
            return true;
        else
            return false;
    }

    //TODO refactor same of extjsgrid (fix locator in cardgrid, this is right) MOVE to superclass?
    ////TODO REFACTOR shared with cardgrid
    private WebElement contentSection() {
        return waitForElementPresence(driver, cmdbuildManagementContentLocator());
                //return  driver.findElement(cmdbuildManagementContentLocator());
    }



    //TODO beware: it's tabbed, we simply assume first tab is selected. If not it is necessary to force first tab click
    //FIX: not working in every case
    public List<FormField> fetchFormFields(WebElement rowDetails) {

        List<FormField> fields  = new ArrayList<>();
        WebElement panelBody = waitForVisibilityOfNestedElement(driver, By.className("x-panel-body"), rowDetails);
        List<WebElement> rawFields = panelBody.findElements(By.className("x-field"));
        for (WebElement rawField : rawFields ) {
            fields.add(FormField.of(rawField.findElement(By.className("x-form-item-label-inner")).getText() ,
                    rawField.findElement(By.className("x-form-field")).getText()));
//                    rawField.findElement(By.className("x-form-display-field")).getText()));
        }
        return fields;
    }

    @Deprecated
    public List<FormField> fetchFormFieldsInExpandedRowSection(WebElement window) {

        List<FormField> fields  = new ArrayList<>();
        List<WebElement> rawFields = window.findElements(By.className("x-field"));
        for (WebElement rawField : rawFields ) {
            fields.add(FormField.of(rawField.findElement(By.className("x-form-item-label-inner")).getText() ,
                    rawField.findElement(By.className("x-form-display-field")).getText()));
        }
        return fields;
    }

    /**
     * @param rowIndex
     * @return open details windows
     *
     * This method takes care of all, row can be expanded or collapsed
     */
    public WebElement openProcessCard(int rowIndex) {

        WebElement row = safelyEpandGridRow(rowIndex);
       // WebElement button = waitForPresenceOfNestedElement(driver, workflowGridRowOpenButton(), row); //not nested?
        WebElement button = waitForElementPresence(driver, workflowGridRowOpenButton()); //
        safeClick(driver, button);
//        WebElement window = waitForElementVisibility(driver, workflowProcessDetailWindow()); //not always working
        WebElement window = waitForElementPresence(driver, workflowProcessDetailWindow());
        return  window;
    }

    //TODO FIXME very important: previous grid is still expanded but not shown. Expansion section is thrown away when row is removed,
    // SO PAY ATTENTION when fetching elements in a non grid hierarchical fashion!!!!
    public WebElement openProcessCard2(int rowIndex) {

        WebElement row = safelyEpandGridRow(rowIndex);
        WebElement button = waitForPresenceOfNestedElement(driver, workflowGridRowOpenButton(), row); //not nested?
        safeClick(driver, button);
        WebElement window = waitForElementPresence(driver, workflowProcessDetailWindow());
        return  window;
    }

    public WebElement editProcessCard(int rowIndex) {

        WebElement row = safelyEpandGridRow(rowIndex);
       // WebElement button = waitForPresenceOfNestedElement(driver, workflowGridRowOpenButton(), row); //not nested?
        WebElement button = waitForElementPresence(driver, workflowGridRowEditButton());
        safeClick(driver, button);
//        WebElement window = waitForElementVisibility(driver, workflowProcessDetailWindow()); //not always working
        WebElement window = waitForElementPresence(driver, workflowProcessDetailWindow());
        return  window;
    }

    public void closeDetailsWindow(WebDriver driver, WebElement openInstanceWindow) {

        WebElement closeButton = openInstanceWindow.findElement(workflowProcessDetailWindowCloseButton());
//        WebElement closeButton = waitForPresenceOfNestedElement(driver, workflowProcessDetailWindowCloseButton(), openInstanceWindow);
       closeButton.click();
//        safeClick(driver, closeButton);
//        (new WebDriverWait(driver, defaultWaitSeconds))
//                .until(ExpectedConditions.invisibilityOf(openInstanceWindow));
        //FIXME: remove as soon as possibile, use a proper waitcondition (if any applies); e.g.: visibility of cmdbuild content window?
        waitForLoad(driver);
    }

    //TODO REMOVE, seems not to work
//    public void closeGhostEditWindow(WebDriver driver) {
//
//        WebElement window = getEditProcessGhostForm();
//      //  window.sendKeys(Keys.TAB);
//        focus(driver, window);
//        WebElement closeButton = waitForPresenceOfNestedElement(driver, workflowProcessDetailWindowCloseButton(), window);
//        focus(driver,closeButton);
//        closeButton.click();
//
//    }

    //TODO write also edit method and delete click




}
