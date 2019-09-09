package org.cmdbuild.test.web;

import static com.google.common.collect.MoreCollectors.toOptional;
import static org.cmdbuild.test.web.utils.ExtjsUtils.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import org.cmdbuild.test.web.utils.*;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class BasicUiWebIT extends BaseWebIT {

    @Before
    public void init() {
        goFullScreen();
        getUrl(getBaseUrl());
        login(Login.admin());
    }

    @Test
    public void simpleTest() {
        waitForXpath("//div[@data-testid='management-navigation-container']//div[text()='Employees']");
        waitForXpath("//div[@data-testid='header-administration']").click();
        waitForLoad();
        waitForXpath("//div[@data-testid='administration-navigation-container']//div[text()='Standard']");
        waitForXpath("//div[@data-testid='header-management']").click();
        waitForLoad();
        waitForXpath("//div[@data-testid='management-navigation-container']//div[text()='Employees']");
    }

    @Test
    public void uiNavigatesToProperCardGridOnSidebarNavigationInteractionTest() throws Exception {
        selectElementsFromMenuTree("Locations", "Floors");

        assertTrue(getCurrentUrl().contains("#classes/Floor/cards"));
        assertTrue(findByCss("#CMDBuildManagementContent .x-title-text").getText().toLowerCase().contains("cards floor"));

        selectElementsFromMenuTree("Software", "Databases");

        assertTrue(getCurrentUrl().contains("#classes/Database/cards"));
        assertTrue(findByCss("#CMDBuildManagementContent .x-title-text").getText().toLowerCase().contains("cards database"));
    }

    @Test
    public void checkSelectedCardsAreShownTest() throws Exception {
        selectElementsFromMenuTree("Locations", "Floors");

        assertTrue(getCurrentUrl().contains("#classes/Floor/cards"));
        assertTrue(findByCss("#CMDBuildManagementContent .x-title-text").getText().toLowerCase().contains("cards floor"));

        ExtjsCardGrid grid = getCardGrid(getDriver());

        assertEquals(3, (grid.getRowsContainingAnyCells(new GridCell("Building", "Aon Center"))).size());
        assertEquals(2, (grid.getRowsContainingAnyCells(new GridCell("Code", "F01"))).size());
        assertEquals(2, (grid.getRowsContainingAnyCells(new GridCell("Level", "00"))).size());
        assertEquals(0, (grid.getRowsContainingAnyCells(new GridCell("Description", "AC Aon Center - 01"))).size());
        assertEquals((grid.getRowsContainingAllCells(new GridCell("Description", "AC Aon Center - 01"))).size(), (grid.getRowsContainingAnyCells(new GridCell("Description", "AC Aon Center - 01"))).size());
        assertNotEquals(
                (grid.getRowsContainingAllCells(new GridCell("Building", "Aon Center"), new GridCell("Code", "F00"))).size(),
                (grid.getRowsContainingAnyCells(new GridCell("Building", "Aon Center"), new GridCell("Code", "F00"))).size()
        );

        selectElementsFromMenuTree("Locations", "Buildings");

        assertTrue(getCurrentUrl().contains("#classes/Building/cards"));

        assertTrue(getDriver().findElement(By.id("CMDBuildManagementContent")).findElement(By.className("x-title-text")).getText().toLowerCase().contains("cards building"));

        grid = getCardGrid(getDriver());

        assertEquals(0, (grid.getRowsContainingAllCells(new GridCell("Building", "Aon Center"))).size());
        assertEquals(1, (grid.getRowsContainingAllCells(
                new GridCell("Code", "AC"),
                new GridCell("Description", "Aon Center"),
                new GridCell("City", "Chicago")
        )).size());
        assertEquals(0, (grid.getRowsContainingAllCells(
                new GridCell("Code", "LMT"),
                new GridCell("Description", "Aon Center"),
                new GridCell("City", "Chicago")
        )).size());
        assertEquals(2, (grid.getIndexOfRowsCointainingAllCells(
                new GridCell("Country", "United States of America")
        )).size());
        assertEquals((grid.getIndexOfRowsCointainingAllCells( //test for ExtjsCardGrid
                new GridCell("City", "Baltimora")
        )).size(),
                (grid.getRowsContainingAllCells(
                        new GridCell("City", "Baltimora")
                )).size());

    }

    @Test
    public void cardsSearchTest() {
        selectElementsFromMenuTree("Suppliers", "Suppliers");
        assertTrue(getCurrentUrl().contains("#classes/Supplier/cards"));
        WebElement content = getDriver().findElement(By.id("CMDBuildManagementContent"));
        assertTrue(content.findElement(By.className("x-title-text")).getText().toLowerCase().contains("cards supplier"));

        //Search tests from here
        ExtjsCardGrid grid = getCardGrid(getDriver());
        int numberOfCardsShownNoFilter = grid.getRows().size();
        assertEquals(6, numberOfCardsShownNoFilter);
        setQuickSearch4CardsText(getDriver(), "lee", null, null);
        waitForLoad();
        grid = getCardGrid(getDriver());
        assertEquals(1, grid.getRows().size());
        //Changing searchtext content w/o pressing enter should not change displayed cards
        setQuickSearch4CardsText(getDriver(), "Catch nothing filter... ", false, false);
        waitForLoad();
        assertEquals(1, getCardGrid(getDriver()).getRows().size());
        //no rows with catch nothing filter
        setQuickSearch4CardsText(getDriver(), "Catch nothing filter... ", true, true);
        waitForLoad();
        assertEquals(0, getCardGrid(getDriver()).getRows().size());

        setQuickSearch4CardsText(getDriver(), "ia", true, true);
        waitForLoad();
        assertEquals(3, getCardGrid(getDriver()).getRows().size());
        //check case insensitive and other fields
        setQuickSearch4CardsText(getDriver(), "[KaYbA]", null, null);
        waitForLoad();
        assertEquals(1, getCardGrid(getDriver()).getRows().size());
        //clear should display the initial number of cards
        clearQuickSearch4CardsText(getDriver());
        waitForLoad();
        assertEquals(numberOfCardsShownNoFilter, getCardGrid(getDriver()).getRows().size());
    }

    @Test
    public void cardUpdateTest() {

        selectElementsFromMenuTree("Locations", "Rooms");

        ExtjsCardGrid grid = ExtjsCardGrid.extract(getDriver());
        Optional<GridCell> cellBeforeEdit = grid.getRows().get(grid.rows() - 1).stream().filter(r -> "Code".equals(r.getName())).findFirst();
        WebElement gridRow = grid.expandGridRow(grid.rows() - 1);
        sleep(1000);
        grid.editCard();
        sleep(3000);
        WebElement detailsForm = getManagementDetailsWindow(getDriver());
        fillFormTextField(detailsForm, "Code", "T");
        clickDetailFormButton(detailsForm, "Save and close");

        Optional<GridCell> cellAfterEdit = grid.refresh().getRows().get(grid.rows() - 1).stream().filter(r -> "Code".equals(r.getName())).findFirst();
        assertNotEquals(cellAfterEdit.get().getContent(), cellBeforeEdit.get().getContent());

        grid.editCard();
        detailsForm = getManagementDetailsWindow(getDriver());

        // restore the field updated
        clearFormTextField(getDriver(), detailsForm, "Code");
        fillFormTextField(detailsForm, "Code", "R01");
        clickDetailFormButton(detailsForm, "Save and close");
        waitForLoad();

    }

    //addresses cardUpdateTest known to fail (goes away from class of edited card and comes back)
    @Test
    public void cardUpdateWithExplicitRefreshTest() {

        selectElementsFromMenuTree("Locations", "Rooms"); //beware: fails if with checks version used 

        ExtjsCardGrid grid = ExtjsCardGrid.extract(getDriver());
        Optional<GridCell> cellBeforeEdit = grid.getRows().get(grid.rows() - 1).stream().filter(r -> "Code".equals(r.getName())).findFirst();
        WebElement gridRow = grid.expandGridRow(grid.rows() - 1);
        sleep(1000);
        grid.editCard();
        sleep(3000);
        WebElement detailsForm = getManagementDetailsWindow(getDriver());

        fillFormTextField(detailsForm, "Code", "T");
        clickDetailFormButton(detailsForm, "Save and close");
        sleep(2000);

        //workaround: move away and come back, forcing grid refresh
        safelyClickOnSideNavLeafWithChecksAgainst(getDriver(), "Supplier", "Cards Supplier", "Suppliers", "Suppliers");
        sleep(2000);

        safelyClickOnSideNavLeafWithChecksAgainst(getDriver(), "Room", "Cards Room", "Locations", "Rooms");

        ExtjsCardGrid grid2 = ExtjsCardGrid.extract(getDriver());

        Optional<GridCell> cellAfterEdit = grid.refresh().getRows().get(grid.rows() - 1).stream().filter(r -> "Code".equals(r.getName())).findFirst();
        assertNotEquals(cellAfterEdit.get().getContent(), cellBeforeEdit.get().getContent());

        gridRow = grid.refresh().expandGridRow(grid.rows() - 1);
        grid.editCard();
        detailsForm = getManagementDetailsWindow(getDriver());

        // restore the field updated
        clearFormTextField(getDriver(), detailsForm, "Code");
        fillFormTextField(detailsForm, "Code", "R01");
        waitForLoad();
        clickDetailFormButton(detailsForm, "Save and close");
        sleep(3000);

    }

    @Test
    public void demoGridContent() throws Exception {

        safelyClickOnSideNavLeafWithChecksAgainst(getDriver(), "Floor", "cards Floor", "Locations", "Floors");
        WebElement content = getDriver().findElement(By.id("CMDBuildManagementContent"));
        assertTrue(content.findElement(By.className("x-title-text")).getText().toLowerCase().contains("cards floor"));
        sleep();

        ExtjsCardGrid grid = getCardGrid(getDriver());

        List<String> gridFields = grid.getFields();
        getClass();
        gridFields.stream().forEach(f -> logger.info("Grid Field: {}", f));

        int r = 0;
        for (List<GridCell> row : grid.getRows()) {
            logger.info("\nGrid row {}", ++r);
            row.stream().forEach(cell -> logger.info(" {}", cell));
        }

    }

}
