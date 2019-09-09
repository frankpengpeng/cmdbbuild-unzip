package org.cmdbuild.test.web;

import static org.cmdbuild.test.web.utils.ExtjsUtils.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static io.restassured.RestAssured.*;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import com.google.common.base.Strings;
import org.cmdbuild.test.web.utils.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManagementGridIT extends BaseWebIT {

//	@Override //TODO make configurable
//	protected boolean noErrorsOnBrowserConsole() {
//		return fetchClientLog().supress("404").supress("401").supress("400").success();
//	}
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Several checks against UI facilities for card sorting.
     * 
     * TODO: check whole tree, not only first level nodes
     * TODO: add checks for ordering via column header dropdown buttons (when found a general viable solution for dropdowns)
     * TODO: add checks for menu type
     */
    @Test
    public void interactiveCardSortingTest() {

        UITestContext context = getDefaultTestContextInstance();
        //TODO configuration, externalize
        List<String> columns2OrderBy = Arrays.asList(new String[]{"Hostname", "Code", "Assignee" /*contains nulls*/});

        testStart(context);
        goFullScreen();
        login(Login.admin());
        sleep(context);

        ExtjsUtils.safelylickOnSideNavLeaf(getDriver(), "Workplaces", "All computers");
        sleep(context);

        for (String column2OrderBy : columns2OrderBy) {

            logger.info("Ordering by column {}", column2OrderBy);
            ExtjsCardGrid gridAscending = getCardGrid(getDriver());
            assertTrue(gridAscending.rows() > 1); //can't check sorting otherwise

            gridAscending.clickOnGridHeader(column2OrderBy);
            sleep(context);
            gridAscending.refresh();
            List<List<GridCell>> ascendingOrderRows = gridAscending.getRows();

            gridAscending.clickOnGridHeader(column2OrderBy);
            sleep(context);
            ExtjsCardGrid gridDescending = getCardGrid(getDriver());
            //grid1.refresh();
            List<List<GridCell>> descendingOrderRows = gridDescending.getRows();
            //checks against ordering
            //PAY ATTENTION: if you order by non unique field you can only check against that field
            int idxOfColumn = gridAscending.getColumnIndexOfColumn(column2OrderBy);
            assertEquals(ascendingOrderRows.size(), descendingOrderRows.size());
            assertTrue(descendingOrderRows.size() > 1);
            assertTrue(ascendingOrderRows.size() > 1);
            //check asc and desc ordered grids are reversed and each grid order is respected
            int size = ascendingOrderRows.size();
            for (int i = 0; i < size; i++) {
                logger.info("Comparing row {}, ASC: {} , DESC {}", i, ascendingOrderRows.get(i).get(idxOfColumn),
                        descendingOrderRows.get(size - i - 1).get(idxOfColumn));
                assertTrue(ascendingOrderRows.get(i).get(idxOfColumn)
                        .matches(descendingOrderRows.get(size - i - 1).get(idxOfColumn)));
                if (i > 0) {
                    logger.info("ASC Comparing # {} with {} : {} - {}", i, i - 1, ascendingOrderRows.get(i).get(idxOfColumn).getContent(), ascendingOrderRows.get(i - 1).get(idxOfColumn).getContent());;
                    assertTrue(anyEmpty(ascendingOrderRows.get(i).get(idxOfColumn).getContent(), ascendingOrderRows.get(i - 1).get(idxOfColumn).getContent())
                            || (ascendingOrderRows.get(i).get(idxOfColumn).getContent().
                                    compareToIgnoreCase(ascendingOrderRows.get(i - 1).get(idxOfColumn).getContent()) >= 0));
                    logger.info("DESC Comparing # {} with {} : {} - {}", i, i - 1, descendingOrderRows.get(i).get(idxOfColumn).getContent(), descendingOrderRows.get(i - 1).get(idxOfColumn).getContent());
                    assertTrue(anyEmpty(descendingOrderRows.get(i).get(idxOfColumn).getContent(), descendingOrderRows.get(i - 1).get(idxOfColumn).getContent())
                            || (descendingOrderRows.get(i).get(idxOfColumn).getContent().
                                    compareToIgnoreCase(descendingOrderRows.get(i - 1).get(idxOfColumn).getContent()) <= 0));
                }
            }

        }
        checkNoErrorsOnBrowserConsole();
        testEnd();

    }

    //workaround for #395 (whitechars seem to be added to field for some reason...)
    private boolean anyEmpty(String... strings) {
        return Arrays.stream(strings).anyMatch(s -> Strings.isNullOrEmpty(s) || Strings.isNullOrEmpty(s.trim()));
    }

    /**
     * HP: Defined criteria are those returned by ws [class] "attributes" for the user
     * TODO: (if applicable) check column list is different for users with different class visualization configuration
     */
    @Test
    public void gridColumnsOrderedAccordingToDefinedCriteriaTest() {
        UITestContext context = getDefaultTestContextInstance();

        testStart(context);
        goFullScreen();

        login(Login.admin());

        ExtjsUtils.safelylickOnSideNavLeaf(getDriver(), "Workplaces", "Notebooks");
        waitForLoad();
        sleep(context);

        List<String> displayedColumnsList = given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
                .get(buildRestV3Url("classes/Notebook/attributes")).then().statusCode(200).extract().jsonPath()
                .getList(("data.findAll{ it.showInGrid == true && it.hidden == false}.description"));

        ExtjsCardGrid grid = ExtjsCardGrid.extract(getDriver());
        List<String> gridColumns = grid.getFields();
        assertTrue(gridColumns.size() == displayedColumnsList.size());
        for (int i = 0; i < gridColumns.size(); i++) {
            logger.info("Comparing #{} column: ws: {} -  ui: {}", i, displayedColumnsList.get(i), gridColumns.get(i)); //demote to debug level when done
            assertEquals(displayedColumnsList.get(i), gridColumns.get(i));
        }

        checkNoErrorsOnBrowserConsole();
        testEnd();

    }

    /**
     * 
     * Delegates cleanup to @After
     * 
     * This test is completely dependent on r2u.
     * 
     * It assumes AttributesPrivileges are null for the (Class,Role) tested, and is aware of the attributes of the class being used
     * 
     * 
     * @throws Exception 
     */
    @Test
    public void visibilityOfCardColumnsAsDefinedTest() throws Exception {
        UITestContext context = getDefaultTestContextInstance();

        testStart(context);
        goFullScreen();

        login(Login.admin());
        sleep(context);

        ExtjsUtils.safelylickOnSideNavLeaf(getDriver(), "Employees", "All employees");

        ExtjsCardGrid grid = ExtjsCardGrid.extract(getDriver());
        List<String> g = grid.getFields();
        logger.info(g.toString());

        JSONArray jArray = new JSONArray();
        JSONObject json = new JSONObject();
        json.put("property", "Code");
        json.put("direction", "ASC");
        jArray.put(json);

        List<String> unSortedList = given().header(CMDBUILD_AUTH_HEADER, getSessionToken())
                .get(buildRestV3Url("classes/Employee/cards")).then().extract().jsonPath().getList("data.Code");

        List<String> sortedListASC = given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).queryParam("sort", jArray)
                .get(buildRestV3Url("classes/Employee/cards")).then().extract().jsonPath().getList("data.Code");

        logger.info(unSortedList.toString());
        logger.info(sortedListASC.toString());

        ExtjsUtils.safelylickOnSideNavLeaf(getDriver(), "Locations", "Rooms");

        ExtjsCardGrid grid2 = ExtjsCardGrid.extract(getDriver());
        String gridColumns2 = grid.getRows().toString();

        ExtjsUtils.safelylickOnSideNavLeaf(getDriver(), "Employees", "All employees");

        logger.info(gridColumns2);

        testEnd();
    }

    /**
     * Maybe desired behaviour will be overturned. If so, simply negate final conditions
     */
    @Test
    public void columnOrderRestoreAfterShufflingTest() {

        UITestContext context = getDefaultTestContextInstance();

        context.withRule(getDefaultCLientLogCheckRule());

        testStart(context);
        goFullScreen();
        login(Login.demo());
        sleep(context);

        ExtjsUtils.safelylickOnSideNavLeaf(getDriver(), "Locations", "Buildings");
        ExtjsCardGrid gridDefault = ExtjsCardGrid.extract(getDriver());
        List<String> fieldsDefault = gridDefault.getFields();
        WebElement source = gridDefault.getHeaderWebElement(fieldsDefault.get(0));
        WebElement target = gridDefault.getHeaderWebElement(fieldsDefault.get(2));

        LocalDateTime start = LocalDateTime.now();
        new Actions(getDriver()).dragAndDrop(source, target).perform();
        logger.warn("Drag and drop action took {} ms", ChronoUnit.MILLIS.between(start, LocalDateTime.now()));
        sleep(context);

        logger.info(source.getText());
        logger.info(target.getText());

        ExtjsCardGrid gridSwapped = ExtjsCardGrid.extract(getDriver());
        List<String> fieldsSwapped = gridSwapped.getFields();

        assertTrue(fieldsSwapped.get(1).equals(fieldsDefault.get(0)));
        assertTrue(fieldsSwapped.get(0).equals(fieldsDefault.get(1)));

        //"refresh" to default order by going to another card and coming back...
        ExtjsUtils.safelylickOnSideNavLeaf(getDriver(), "Locations", "Floors");
        ExtjsUtils.safelylickOnSideNavLeaf(getDriver(), "Locations", "Buildings");
        ExtjsCardGrid gridDefaultRestored = ExtjsCardGrid.extract(getDriver());
        List<String> fieldsDefaultRestored = gridDefaultRestored.getFields();
        assertTrue(IntStream.range(0, fieldsDefault.size()).allMatch(i -> fieldsDefault.get(i).equals(fieldsDefaultRestored.get(i))));
        assertTrue(IntStream.range(0, fieldsDefault.size()).anyMatch(i -> !fieldsDefault.get(i).equals(fieldsSwapped.get(i)))); //redundant

        testEnd();
    }

    public String[] showHiddenColumn_TargetTreePath = {"Workplaces", "All computers"};

    @Test
    public void showHiddenColumnTest() {

        UITestContext context = getDefaultTestContextInstance();

        context.withRule(getDefaultCLientLogCheckRule());

        testStart(context);
        goFullScreen();
        login(Login.admin());
        sleep(context);

        safelylickOnSideNavLeaf(getDriver(), showHiddenColumn_TargetTreePath);

        ExtjsCardGrid grid = ExtjsCardGrid.extract(getDriver(), context);
        List<String> fieldsBefore = grid.getFields();

        List<ExtjsCardGrid.ColumnState> columns = grid.getColumnStates();
        Optional<ExtjsCardGrid.ColumnState> firstDisabledColumn = columns.stream().filter(c -> c.isEnabled() == false).findFirst();
        //TODO manage all enabled case? not for now...
        assertTrue(firstDisabledColumn.isPresent());

        //Enable first column
        grid.toggleColumnState(firstDisabledColumn.get());

        grid = ExtjsCardGrid.extract(getDriver(), context);
        sleep(context);
        List<String> fieldsAfter = grid.getFields();

        assertEquals("After making visible a hidden column there should be one more displayed field...", fieldsBefore.size() + 1, fieldsAfter.size());
        assertTrue("All previous displayed columns must be shown after adding a new column", fieldsBefore.stream().allMatch(fb -> fieldsAfter.stream().anyMatch(fa -> fa.equals(fb))));
        assertTrue("Added column must be shown", fieldsAfter.contains(firstDisabledColumn.get().getName()));

        testEnd(context);

    }

    @Test
    public void hideVisibleColumnTest() {

        UITestContext context = getDefaultTestContextInstance();
        context.withRule(getDefaultCLientLogCheckRule());

        testStart(context);
        goFullScreen();
        login(Login.admin());

        safelylickOnSideNavLeaf(getDriver(), showHiddenColumn_TargetTreePath);

        sleep(context);
        ExtjsCardGrid grid = ExtjsCardGrid.extract(getDriver(), context);
        List<String> fieldsBefore = grid.getFields();

        List<ExtjsCardGrid.ColumnState> columns = grid.getColumnStates();
        Optional<ExtjsCardGrid.ColumnState> firstDisabledColumn = columns.stream().filter(c -> c.isEnabled() == true).findFirst();
        //TODO manage all disabled case? not for now...
        assertTrue(firstDisabledColumn.isPresent());

        //Disable first enabled column
        grid.toggleColumnState(firstDisabledColumn.get());

        grid = ExtjsCardGrid.extract(getDriver(), context);
        List<String> fieldsAfter = grid.getFields();

        assertEquals("After hiding visible a visible column there should be one less displayed field...", fieldsBefore.size() - 1, fieldsAfter.size());
        assertEquals("All previous displayed columns (but one) must be shown after adding a new column", fieldsBefore.size() - 1, fieldsBefore.stream().filter(fb -> fieldsAfter.stream().anyMatch(fa -> fa.equals(fb))).count());
        assertEquals("Hidden column must not be shown", false, fieldsAfter.contains(firstDisabledColumn.get().getName()));
        assertTrue("All displayed columns were showing before disabling a column", fieldsAfter.stream().allMatch(fa -> fieldsBefore.stream().anyMatch(fb -> fb.equals(fa))));

        testEnd(context);
    }

    @Test
    public void printCardsGridPDF() {

        UITestContext context = getDefaultTestContextInstance();
        context.withRule(getDefaultCLientLogCheckRule());
        testStart(context);
        goFullScreen();

        login(Login.admin());
        sleep(context);

        safelyClickOnSideNavLeafWithChecksAgainst(getDriver(), "NetworkInterface", "Cards Network interface", "Networks", "Network interfaces");
        sleep(context);
        ExtjsUtils.findElementByTestId(getDriver(), "grid-printbtn").click();
        sleep(context);

        getDriver().switchTo().activeElement().findElement(By.xpath("//a[@role='menuitem']//div[contains(@class,'x-fa fa-file-pdf-o')]")).click();
        sleep(context);

        assertTrue(linkResponse(getDriver().getCurrentUrl()));
        sleep(context);

        testEnd(context);

    }

    @Test
    public void printCardsGridCSV() {

        UITestContext context = getDefaultTestContextInstance();
        context.withRule(getDefaultCLientLogCheckRule());
        testStart(context);
        goFullScreen();

        login(Login.admin());
        sleep(context);

        safelyClickOnSideNavLeafWithChecksAgainst(getDriver(), "NetworkInterface", "Cards Network interface", "Networks", "Network interfaces");
        sleep(context);
        ExtjsUtils.findElementByTestId(getDriver(), "grid-printbtn").click();
        sleep(context);

        getDriver().switchTo().activeElement().findElement(By.xpath("//a[@role='menuitem']//div[contains(@class,'x-fa fa-file-excel-o')]")).click();
        sleep(context);

        assertTrue(linkResponse(getDriver().getCurrentUrl()));
        sleep(context);

        testEnd(context);

    }

    public static boolean linkResponse(String url) {
        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    @After
    public void cleanupManagementGridIT() {
        getJdbcTemplate().update(sqlSetDefaultPrivilegesOnClassBuildingForGroupHWHelpdesk);
    }

    String sqlSetDefaultPrivilegesOnClassBuildingForGroupHWHelpdesk = "update  \"_Grant\" set \"AttributePrivileges\" = null  "
            + " where \"Status\"='A' and \"IdRole\" = (select \"Id\" from \"Role\" where \"Status\"='A' and \"Code\"='HWHelpdesk') and \"ObjectClass\"='\"Building\"'::regclass;";

}
