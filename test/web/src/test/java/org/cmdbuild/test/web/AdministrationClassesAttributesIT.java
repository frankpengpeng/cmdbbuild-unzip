package org.cmdbuild.test.web;

import org.apache.commons.lang3.RandomUtils;
import org.cmdbuild.test.web.utils.*;
import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import static io.restassured.RestAssured.*;

import com.google.common.collect.ImmutableList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.cmdbuild.test.web.utils.ExtjsUtils.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AdministrationClassesAttributesIT extends BaseWebIT {

    private String adminContentClassesTabItemsLocator = "administration-content-classes-tabitems-attributes-attributes";
    private String addAttributeButtonCaption = "Add attribute";
    private String emptyAttributeNotAllowedTargetClass = "Cluster";
    private String[] emptyAttributeNotAllowedTargetTreePath = {"Classes", "Standard", emptyAttributeNotAllowedTargetClass};

    @After
    public void cleanup() {
        cleanupDB();
    }

    @Test
    public void createAttributeWOInsertingDataNotAllowedTest() {

        UITestContext context = getDefaultTestContextInstance();

        testStart(context);
        goFullScreen();
        login(Login.admin());
        sleep(context);
        ExtjsUtils.switchToAdminModule(getDriver(), context);
        sleep(context);

        ExtjsNavigationMenu.extract(getDriver()).selectAtPath(emptyAttributeNotAllowedTargetTreePath);
        selectAdministrationClassTab("Attributes");

        // missing data-testid, so we search for <a> containing text "Add attribute"
        waitForVisibilityOfNestedElement(getDriver(), By.tagName("a"),
                testIdLocator(adminContentClassesTabItemsLocator));
        WebElement gridSection = getDriver().findElement(testIdLocator(adminContentClassesTabItemsLocator));
        List<WebElement> candidateButtons = gridSection.findElements(By.tagName("a"));
        Optional<WebElement> addAttributeButton = candidateButtons.stream()
                .filter(cb -> normalized(cb.getText()).equals(addAttributeButtonCaption)).findFirst();
        safeClick(getDriver(), addAttributeButton.get());
        sleep(context);

        WebElement detailsWindow = waitForElementVisibility(testIdLocator("cards-card-administration-detailsWindow"));
        Optional<WebElement> saveButton = detailsWindow.findElements(By.tagName("a")).stream()
                .filter(a -> "Save".equals(normalized(a.getText()))).findFirst();
        assertTrue(saveButton.get().getAttribute("class").contains("x-btn-disabled"));

        testEnd(context);
    }

    @Test
    public void createDeleteNewAttribute() {

        UITestContext context = getDefaultTestContextInstance();

        testStart(context);
        goFullScreen();
        login(Login.admin());
        sleep(context);
        ExtjsUtils.switchToAdminModule2(getDriver(), context);
        sleep(context);

        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "Disk");

        selectAdministrationClassTab("Attributes");

        WebElement attributesClassGrid = ExtjsUtils.findElementByTestId(getDriver(),
                "administration-components-attributes-grid");
        attributesClassGrid.findElement(By.tagName("a")).click();
        sleep(context);

        WebElement detailsWindow = waitForElementVisibility(testIdLocator("cards-card-administration-detailsWindow"));
        Optional<WebElement> saveButton = detailsWindow.findElements(By.tagName("a")).stream()
                .filter(a -> "Save".equals(normalized(a.getText()))).findFirst();
        assertTrue(saveButton.get().getAttribute("class").contains("x-btn-disabled"));

        WebElement grid1 = getDriver().switchTo().activeElement()
                .findElement(By.xpath("//fieldset[@aria-label='General properties field set']"));

        grid1.findElement(By.name("name")).sendKeys("Test01");

        ExtjsUtils.findElementByXPath(getDriver(),
                "//fieldset[@aria-label='Type properties field set']//div[contains(@class, 'x-form-arrow-trigger')]")
                .click();
        ExtjsUtils.findElementByXPath(getDriver(), "//div//ul//li[text()='STRING']").click();
        sleep(context);

        ExtjsUtils.findElementByXPath(getDriver(), "//span[text()='Save and add']").click();
        sleep(context);

        ExtjsUtils.switchToManagementModule(getDriver());
        sleep(context);

        ExtjsUtils.safelyClickOnSideNavLeafWithChecksAgainst(getDriver(), "Disk", "Cards Disk", "Infrastructures",
                "Disks");
        sleep(context);

        ExtjsCardGrid grid = ExtjsCardGrid.extract(getDriver(), context);
        List<String> fieldsBefore = grid.getFields();

        List<ExtjsCardGrid.ColumnState> columns = grid.getColumnStates();
        Optional<ExtjsCardGrid.ColumnState> newColumn = columns.stream().filter(c -> !c.isEnabled()).findFirst();
        // TODO manage all enabled case? not for now...
        logger.info(newColumn.get().getName());
        assertTrue(newColumn.isPresent());
        grid.toggleColumnState(newColumn.get());
        grid = ExtjsCardGrid.extract(getDriver(), context);
        sleep(context);
        List<String> fieldsAfter = grid.getFields();

        assertEquals("After making visible a hidden column there should be one more displayed field...",
                fieldsBefore.size() + 1, fieldsAfter.size());
        assertTrue("All previous displayed columns must be shown after adding a new column",
                fieldsBefore.stream().allMatch(fb -> fieldsAfter.stream().anyMatch(fa -> fa.equals(fb))));
        assertTrue("Added column must be shown", fieldsAfter.contains(newColumn.get().getName()));

        ExtjsUtils.switchToAdminModule2(getDriver(), context);
        sleep(context);
        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "Disk");

        selectAdministrationClassTab("Attributes");
        expandRowAdministrationGrid("Test01");
        sleep(context);
        clickDisableButton();
        sleep(context);
        clickDeleteButton();

        List<String> diskListAttributes = given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
                .get(buildRestV3Url("classes/Disk/attributes")).then().statusCode(200).extract().jsonPath()
                .getList(("data.findAll{ it._id == 'Test01'}._id"));

        if (diskListAttributes.size() != 0) {
            given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
                    .delete(buildRestV3Url("classes/Disk/attributes/" + diskListAttributes.get(0))).then()
                    .statusCode(200);
        }

        testEnd(context);
    }

    @Test
    public void createDeleteNewSuperClassAttribute() {

        UITestContext context = getDefaultTestContextInstance();

        testStart(context);
        goFullScreen();
        login(Login.admin());
        sleep(context);
        ExtjsUtils.switchToAdminModule2(getDriver(), context);
        sleep(context);

        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "Employee");

        selectAdministrationClassTab("Attributes");

        WebElement attributesClassGrid = ExtjsUtils.findElementByTestId(getDriver(),
                "administration-components-attributes-grid");
        attributesClassGrid.findElement(By.tagName("a")).click();
        sleep(context);

        WebElement detailsWindow = waitForElementVisibility(testIdLocator("cards-card-administration-detailsWindow"));
        Optional<WebElement> saveButton = detailsWindow.findElements(By.tagName("a")).stream()
                .filter(a -> "Save".equals(normalized(a.getText()))).findFirst();
        assertTrue(saveButton.get().getAttribute("class").contains("x-btn-disabled"));

        WebElement grid1 = getDriver().switchTo().activeElement()
                .findElement(By.xpath("//fieldset[@aria-label='General properties field set']"));

        grid1.findElement(By.name("name")).sendKeys("Test01");

        ExtjsUtils.findElementByXPath(getDriver(),
                "//fieldset[@aria-label='Type properties field set']//div[contains(@class, 'x-form-arrow-trigger')]")
                .click();
        ExtjsUtils.findElementByXPath(getDriver(), "//div//ul//li[text()='STRING']").click();
        sleep(context);

        ExtjsUtils.findElementByXPath(getDriver(), "//span[text()='Save and add']").click();
        sleep(context);

        ExtjsUtils.switchToManagementModule(getDriver());
        sleep(context);

        ExtjsUtils.safelyClickOnSideNavLeafWithChecksAgainst(getDriver(), "Employee", "Cards Employee", "Employees",
                "All employees");
        sleep(context);

        ExtjsCardGrid grid = ExtjsCardGrid.extract(getDriver(), context);
        List<String> fieldsBefore = grid.getFields();

        List<ExtjsCardGrid.ColumnState> columns = grid.getColumnStates();
        Optional<ExtjsCardGrid.ColumnState> newColumn = columns.stream().filter(c -> !c.isEnabled()).findFirst();
        // TODO manage all enabled case? not for now...
        logger.info(newColumn.get().getName());
        assertTrue(newColumn.isPresent());
        grid.toggleColumnState(newColumn.get());
        grid = ExtjsCardGrid.extract(getDriver(), context);
        sleep(context);
        List<String> fieldsAfter = grid.getFields();

        assertEquals("After making visible a hidden column there should be one more displayed field...",
                fieldsBefore.size() + 1, fieldsAfter.size());
        assertTrue("All previous displayed columns must be shown after adding a new column",
                fieldsBefore.stream().allMatch(fb -> fieldsAfter.stream().anyMatch(fa -> fa.equals(fb))));
        assertTrue("Added column must be shown", fieldsAfter.contains(newColumn.get().getName()));

        ExtjsUtils.switchToAdminModule2(getDriver(), context);
        sleep(context);
        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "Employee");

        selectAdministrationClassTab("Attributes");
        expandRowAdministrationGrid("Test01");
        sleep(context);
        clickDisableButton();
        sleep(context);
        clickDeleteButton();

        List<String> diskListAttributes = given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
                .get(buildRestV3Url("classes/Employee/attributes")).then().statusCode(200).extract().jsonPath()
                .getList(("data.findAll{ it._id == 'Test01'}._id"));

        if (diskListAttributes.size() != 0) {
            given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
                    .delete(buildRestV3Url("classes/Employee/attributes/" + diskListAttributes.get(0))).then()
                    .statusCode(200);
        }

        testEnd(context);
    }

    @Test
    public void createDeleteNewAttributeWithNewCard() {

        UITestContext context = getDefaultTestContextInstance();

        testStart(context);
        goFullScreen();
        login(Login.admin());
        sleep(context);
        ExtjsUtils.switchToAdminModule2(getDriver(), context);
        sleep(context);

        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "Disk");

        selectAdministrationClassTab("Attributes");

        WebElement attributesClassGrid = ExtjsUtils.findElementByTestId(getDriver(),
                "administration-components-attributes-grid");
        attributesClassGrid.findElement(By.tagName("a")).click();
        sleep(context);

        WebElement detailsWindow = waitForElementVisibility(testIdLocator("cards-card-administration-detailsWindow"));
        Optional<WebElement> saveButton = detailsWindow.findElements(By.tagName("a")).stream()
                .filter(a -> "Save".equals(normalized(a.getText()))).findFirst();
        assertTrue(saveButton.get().getAttribute("class").contains("x-btn-disabled"));

        WebElement grid1 = getDriver().switchTo().activeElement()
                .findElement(By.xpath("//fieldset[@aria-label='General properties field set']"));

        grid1.findElement(By.name("name")).sendKeys("Test01");

        ExtjsUtils.findElementByXPath(getDriver(),
                "//fieldset[@aria-label='Type properties field set']//div[contains(@class, 'x-form-arrow-trigger')]")
                .click();
        ExtjsUtils.findElementByXPath(getDriver(), "//div//ul//li[text()='STRING']").click();
        sleep(context);

        ExtjsUtils.findElementByXPath(getDriver(), "//span[text()='Save and add']").click();
        sleep(context);

        ExtjsUtils.switchToManagementModule(getDriver());
        sleep(context);

        ExtjsUtils.safelyClickOnSideNavLeafWithChecksAgainst(getDriver(), "Disk", "Cards Disk", "Infrastructures",
                "Disks");
        sleep(context);

        ExtjsCardGrid grid = ExtjsCardGrid.extract(getDriver(), context);
        List<String> fieldsBefore = grid.getFields();

        List<ExtjsCardGrid.ColumnState> columns = grid.getColumnStates();
        Optional<ExtjsCardGrid.ColumnState> newColumn = columns.stream().filter(c -> !c.isEnabled()).findFirst();
        // TODO manage all enabled case? not for now...
        logger.info(newColumn.get().getName());
        assertTrue(newColumn.isPresent());
        grid.toggleColumnState(newColumn.get());
        grid = ExtjsCardGrid.extract(getDriver(), context);
        sleep(context);
        List<String> fieldsAfter = grid.getFields();

        assertEquals("After making visible a hidden column there should be one more displayed field...",
                fieldsBefore.size() + 1, fieldsAfter.size());
        assertTrue("All previous displayed columns must be shown after adding a new column",
                fieldsBefore.stream().allMatch(fb -> fieldsAfter.stream().anyMatch(fa -> fa.equals(fb))));
        assertTrue("Added column must be shown", fieldsAfter.contains(newColumn.get().getName()));
        sleep(context);

        ExtjsCardGrid cardsGrid1 = ExtjsCardGrid.extract(getDriver(), context);
        int cardsBefore = cardsGrid1.getRows().size();

        ExtjsUtils.findElementByTestId(getDriver(), "classes-cards-grid-container-addbtn").click();
        sleep(context);
        WebElement combo = ExtjsUtils.findElementByXPath(getDriver(),
                "//fieldset[@aria-label = 'Base data field set']");
        ExtjsUtils.fillFormTextFieldAdministration(getDriver(), combo, "Code", "Test Card");
        ExtjsUtils.fillFormTextFieldAdministration(getDriver(), combo, "Description", "Test Card");
        ExtjsUtils.fillFormTextFieldAdministration(getDriver(), combo, "Capacity", "250");

        ExtjsUtils.findElementByTestId(getDriver(), "card-create-saveandclose").click();

        ExtjsCardGrid cardsGrid2 = ExtjsCardGrid.extract(getDriver(), context);
        int cardsAfter = cardsGrid2.getRows().size();
        assertTrue(cardsAfter == cardsBefore + 1);

        ExtjsUtils.switchToAdminModule2(getDriver(), context);
        sleep(context);
        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "Disk");

        selectAdministrationClassTab("Attributes");
        expandRowAdministrationGrid("Test01");
        sleep(context);
        clickDisableButton();
        sleep(context);
        clickDeleteButton();

        Object diskId = given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
                .get(buildRestV3Url("classes/Disk/cards")).then().statusCode(200).extract().jsonPath()
                .getList(("data.findAll{ it.Code == 'Test Card'}._id")).get(0);

        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
                .delete(buildRestV3Url("classes/Disk/cards/" + diskId)).then().statusCode(200);

        List<String> diskListAttributes = given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
                .get(buildRestV3Url("classes/Disk/attributes")).then().statusCode(200).extract().jsonPath()
                .getList(("data.findAll{ it._id == 'Test01'}._id"));

        if (diskListAttributes.size() != 0) {
            given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
                    .delete(buildRestV3Url("classes/Disk/attributes/" + diskListAttributes.get(0))).then()
                    .statusCode(200);
        }

        testEnd(context);
    }

    @Test
    public void createNewAttributeWithNewCardGivingValueDeleteNotAllowed() {

        UITestContext context = getDefaultTestContextInstance();

        testStart(context);
        goFullScreen();
        login(Login.admin());
        sleep(context);
        ExtjsUtils.switchToAdminModule2(getDriver(), context);
        sleep(context);

        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "Disk");

        selectAdministrationClassTab("Attributes");

        WebElement attributesClassGrid = ExtjsUtils.findElementByTestId(getDriver(),
                "administration-components-attributes-grid");
        attributesClassGrid.findElement(By.tagName("a")).click();
        sleep(context);

        WebElement detailsWindow = waitForElementVisibility(testIdLocator("cards-card-administration-detailsWindow"));
        Optional<WebElement> saveButton = detailsWindow.findElements(By.tagName("a")).stream()
                .filter(a -> "Save".equals(normalized(a.getText()))).findFirst();
        assertTrue(saveButton.get().getAttribute("class").contains("x-btn-disabled"));

        WebElement grid1 = getDriver().switchTo().activeElement()
                .findElement(By.xpath("//fieldset[@aria-label='General properties field set']"));

        grid1.findElement(By.name("name")).sendKeys("Test01");

        ExtjsUtils.findElementByXPath(getDriver(),
                "//fieldset[@aria-label='Type properties field set']//div[contains(@class, 'x-form-arrow-trigger')]")
                .click();
        ExtjsUtils.findElementByXPath(getDriver(), "//div//ul//li[text()='STRING']").click();
        sleep(context);

        ExtjsUtils.findElementByXPath(getDriver(), "//span[text()='Save and add']").click();
        sleep(context);

        ExtjsUtils.switchToManagementModule(getDriver());
        sleep(context);

        ExtjsUtils.safelyClickOnSideNavLeafWithChecksAgainst(getDriver(), "Disk", "Cards Disk", "Infrastructures",
                "Disks");
        sleep(context);

        ExtjsCardGrid grid = ExtjsCardGrid.extract(getDriver(), context);
        List<String> fieldsBefore = grid.getFields();

        List<ExtjsCardGrid.ColumnState> columns = grid.getColumnStates();
        Optional<ExtjsCardGrid.ColumnState> newColumn = columns.stream().filter(c -> !c.isEnabled()).findFirst();
        // TODO manage all enabled case? not for now...
        logger.info(newColumn.get().getName());
        assertTrue(newColumn.isPresent());
        grid.toggleColumnState(newColumn.get());
        grid = ExtjsCardGrid.extract(getDriver(), context);
        sleep(context);
        List<String> fieldsAfter = grid.getFields();

        assertEquals("After making visible a hidden column there should be one more displayed field...",
                fieldsBefore.size() + 1, fieldsAfter.size());
        assertTrue("All previous displayed columns must be shown after adding a new column",
                fieldsBefore.stream().allMatch(fb -> fieldsAfter.stream().anyMatch(fa -> fa.equals(fb))));
        assertTrue("Added column must be shown", fieldsAfter.contains(newColumn.get().getName()));
        sleep(context);

        ExtjsCardGrid cardsGrid1 = ExtjsCardGrid.extract(getDriver(), context);
        int cardsBefore = cardsGrid1.getRows().size();

        ExtjsUtils.findElementByTestId(getDriver(), "classes-cards-grid-container-addbtn").click();
        sleep(context);
        WebElement combo = ExtjsUtils.findElementByXPath(getDriver(),
                "//fieldset[@aria-label = 'Base data field set']");
        ExtjsUtils.fillFormTextFieldAdministration(getDriver(), combo, "Code", "Test Card");
        ExtjsUtils.fillFormTextFieldAdministration(getDriver(), combo, "Description", "Test Card");
        ExtjsUtils.fillFormTextFieldAdministration(getDriver(), combo, "Capacity", "250");
        ExtjsUtils.fillFormTextFieldAdministration(getDriver(), combo, "Test01", "TEST DELETE NOT ALLOWED");

        ExtjsUtils.findElementByTestId(getDriver(), "card-create-saveandclose").click();

        ExtjsCardGrid cardsGrid2 = ExtjsCardGrid.extract(getDriver(), context);
        int cardsAfter = cardsGrid2.getRows().size();
        assertTrue(cardsAfter == cardsBefore + 1);

        ExtjsUtils.switchToAdminModule2(getDriver(), context);
        sleep(context);
        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "Disk");

        selectAdministrationClassTab("Attributes");
        expandRowAdministrationGrid("Test01");
        sleep(context);
        clickDisableButton();
        sleep(context);
        clickDeleteButton();

        ExtjsGenericGrid grid2 = ExtjsGenericGrid
                .from(getDriver(), testIdLocator("administration-content-classes-tabitems-attributes-attributes"),
                        By.className("x-grid-header-ct"), By.className("x-grid-body"))
                .extract();
        List<Integer> targetAttributeRowIndex2 = grid2
                .getIndexOfRowsCointainingAllCells(GridCell.from("Name", "Test01"));

        assertTrue(targetAttributeRowIndex2.size() == 1);

        Object diskId = given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
                .get(buildRestV3Url("classes/Disk/cards")).then().statusCode(200).extract().jsonPath()
                .getList(("data.findAll{ it.Code == 'Test Card'}._id")).get(0);

        given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
                .delete(buildRestV3Url("classes/Disk/cards/" + diskId)).then().statusCode(200);

        List<String> diskListAttributes = given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
                .get(buildRestV3Url("classes/Disk/attributes")).then().statusCode(200).extract().jsonPath()
                .getList(("data.findAll{ it._id == 'Test01'}._id"));

        if (diskListAttributes.size() != 0) {
            given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
                    .delete(buildRestV3Url("classes/Disk/attributes/" + diskListAttributes.get(0))).then()
                    .statusCode(200);
        }

        testEnd(context);
    }

    @Test
    public void clearingRequiredFieldsNotAllowed() {

        UITestContext context = getDefaultTestContextInstance();

        testStart(context);
        goFullScreen();
        login(Login.admin());
        sleep(context);
        ExtjsUtils.switchToAdminModule2(getDriver(), context);
        sleep(context);

        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "Groups");
        sleep(context);
        selectAdministrationClassTab("Attributes");
        expandRowAdministrationGrid("Description");
        sleep(context);
        clickEditButton();
        sleep(context);
        WebElement combo = ExtjsUtils.findElementById(getDriver(), "CMDBuildAdministrationDetailsWindow");
        combo.findElement(By.name("description")).clear();;
        assertTrue("Save button should be disabled",
                isDetailFormButtonDisabled(combo, "Save"));

        testEnd(context);
    }

    // Delete mandatory content for an attribute is not allowed test vars
    String delMandatoryContentNA_TargetClass = "Building";
    String delMandatoryContentNA_TargetAttribute = "Code";
    String[] delMandatoryContentNA_TargetClassTreePath = {"Classes", "Standard", delMandatoryContentNA_TargetClass};
    String getDelMandatoryContentNA_MandatoryField = "Description";
    String[] getDelMandatoryContentNA_TargetClassTreePathManagement = {"Locations",
        delMandatoryContentNA_TargetClass + "s"};

    @Test
    public void deleteMandatoryContentNotAllowedTest() {

        UITestContext context = getDefaultTestContextInstance();
        context.withRule(getDefaultCLientLogCheckRule());
        testStart(context);

        goFullScreen();
        login(Login.admin());
        sleep(context);

        // check supposed mandatory field is really set as mandatory (or find all
        // mandatory fields)
        ExtjsUtils.switchToAdminModule(getDriver(), context);
        sleep(context);

        ExtjsNavigationMenu.extract(getDriver()).selectAtPath(delMandatoryContentNA_TargetClassTreePath);
        sleep(context);

        selectAdministrationClassTab("Attributes");
        WebElement rootContent = waitForElementVisibility(By.xpath("//div[@data-testid='administration-content-classes-tabitems-attributes-attributes']"));
        ExtjsGenericGrid grid = ExtjsGenericGrid
                .from(getDriver(), By.xpath("//div[@data-testid='administration-content-classes-tabitems-attributes-attributes']"),
                        By.className("x-grid-header-ct"), By.className("x-grid-body"))
                .extract();
        logger.info("Attribute grid headers found: {}", grid.getHeaders());
        logger.info("Attribute grid content found: {}", grid.getCells());

        FormField mandatoryCondition = FormField.of("Mandatory", FormField.CHECKBOX_CHECKED);
        // TODO add proper methods to ExtjsGenericGrid instead of this...
        List<String> mandatoryAttributes = grid.getCells().stream()
                .filter(row -> row.stream().anyMatch(cell -> cell.matches(mandatoryCondition)))
                .map(row -> row.stream().filter(cell -> "Name".equals(cell.getName())).findFirst().get().getContent())
                .collect(Collectors.toList());
        logger.info("Mandatory fields detected in class {}: {}", delMandatoryContentNA_TargetClass,
                mandatoryAttributes.toString());

        assertTrue("There should be at least one mandatory attribute in target class for the test to be performed",
                mandatoryAttributes.size() > 0);
        // switch back to management and try to delete mandatory content
        switchToManagementModule(getDriver());
        sleep(context);
        safelylickOnSideNavLeaf(getDriver(), getDelMandatoryContentNA_TargetClassTreePathManagement);

        ExtjsCardGrid managementGrid = ExtjsCardGrid.extract(getDriver(), context);
        // edit a random card and try to delete one mandatory field at a time
        assertTrue("There should be at least one card of target class to perform the test",
                managementGrid.getRows().size() > 0);
        int card2CheckIndex = RandomUtils.nextInt(0, managementGrid.getRows().size());
        WebElement gridRow = managementGrid.expandGridRow(card2CheckIndex);
        sleep(context);

        managementGrid.editCard();
        WebElement detailsForm = getManagementDetailsWindow(getDriver());

        Map<String, String> originalCardContent = new HashMap<>();
        mandatoryAttributes.stream()
                .forEach(att -> originalCardContent.put(att, getFormTextFieldContent(detailsForm, att)));
        for (String field : mandatoryAttributes) {
            sleep(context);
            clearFormTextField(getDriver(), detailsForm, field);
            sleep(context);
            assertTrue("Empty mandatory attributes should not be persistable",
                    isDetailFormButtonDisabled(detailsForm, "Save"));
            fillFormTextField2(getDriver(), detailsForm, field, originalCardContent.get(field));
            sleep(context);
        }

        testEnd(context);
    }

    String setUniqueWCardsViolatingConstraintNA_checkedClass = "Rooms";
    String setUniqueWCardsViolatingConstraintNA_checkedAttribute = "Code";

    @Test
    public void setUniqueWhenCardsViolatingContraintIsNotAllowedTest() {

        UITestContext context = getDefaultTestContextInstance();// TODO activate when issue with db snapshots is solved
        // .withTouchedClass(delMandatoryContentNA_TargetClass);
        context.withRule(UITestRule.defineClientLogCheckRule(null, ImmutableList.of("404", "401", "500"), null, null));
        testStart(context);

        goFullScreen();
        login(Login.admin());
        sleep(context);

        ExtjsUtils.clickOnSideNavLeaf(getDriver(), "Locations", "Rooms");
        sleep(context);
        ExtjsCardGrid managementGrid = ExtjsCardGrid.extract(getDriver(), context);
        List<List<GridCell>> gridRows = managementGrid.getRows();

        boolean found = false;
        for (List<GridCell> row : gridRows) {
            Optional<GridCell> checkedCell = row.stream()
                    .filter(cell -> setUniqueWCardsViolatingConstraintNA_checkedAttribute.equals(cell.getName()))
                    .findFirst();
            GridCell searchCell = GridCell.from(setUniqueWCardsViolatingConstraintNA_checkedAttribute,
                    checkedCell.get().getContent());
            if (managementGrid.getRowsContainingAllCells(searchCell).size() > 1) {
                found = true;
                break;
            }
        }

        assertTrue("This test requires at least 2 cards with the same value for the checked attribute", found);

        ExtjsUtils.switchToAdminModule(getDriver(), context);
        sleep(context);

        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "Room");
        selectAdministrationClassTab("Attributes");
        sleep(context);

        expandRowAdministrationGrid("Code");

        clickEditButton();

        clickUniqueCheckbox();

        clickSaveButton();

        assertTrue(ExtjsUtils.findElementByTestId(getDriver(), "message-window-title").isDisplayed());
        assertTrue(ExtjsUtils.findElementByTestId(getDriver(), "message-window-title").getText().contains("Error"));

        testEnd(context);
    }

    String setUniqueWCardsRespectingConstraint_checkedClass = "Employee";
    String setUniqueWCardsRespectingConstraint_checkedAttribute = "Number";

    @Test
    public void setUniqueWhenAllCardsRespectContraintIsAllowedTest() {
        UITestContext context = getDefaultTestContextInstance();// TODO activate when issue with db snapshots is solved
        // .withTouchedClass(delMandatoryContentNA_TargetClass);
        context.withRule(UITestRule.defineClientLogCheckRule(null, ImmutableList.of("404", "401"), null, null));
        testStart(context);

        goFullScreen();
        login(Login.admin());
        sleep(context);

        ExtjsUtils.safelyClickOnSideNavLeafWithChecksAgainst(getDriver(), "InternalEmployee", "Cards Internal employee",
                "Employees", "Internal employees");
        sleep(context);
        ExtjsCardGrid managementGrid = ExtjsCardGrid.extract(getDriver(), context);
        List<List<GridCell>> gridRows = managementGrid.getRows();

        logger.info(gridRows.toString());

        boolean found = false;
        for (List<GridCell> row : gridRows) {
            Optional<GridCell> checkedCell = row.stream()
                    .filter(cell -> setUniqueWCardsRespectingConstraint_checkedAttribute.equals(cell.getName()))
                    .findFirst();
            GridCell searchCell = GridCell.from(setUniqueWCardsRespectingConstraint_checkedAttribute,
                    checkedCell.get().getContent());
            if (managementGrid.getRowsContainingAllCells(searchCell).size() > 1) {
                found = true;
                break;
            }
        }

        assertFalse("This test requires all numbers are different", found);

        ExtjsUtils.switchToAdminModule(getDriver(), context);
        sleep(context);

        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "Employee", "Internal employee");
        sleep(context);
        selectAdministrationClassTab("Attributes");

        expandRowAdministrationGrid("Number");

        clickEditButton();

        clickUniqueCheckbox();

        clickSaveButton();

        sleep(context);

        // Controllo che Unique sia posto a true dalla modifica effettuata
        ExtjsGenericGrid grid = ExtjsGenericGrid
                .from(getDriver(), testIdLocator("administration-content-classes-tabitems-attributes-attributes"),
                        By.className("x-grid-header-ct"), By.className("x-grid-body"))
                .extract();
        List<Integer> targetAttributeRowIndex2 = grid
                .getIndexOfRowsCointainingAllCells(GridCell.from("Name", "Number"));

        assertTrue(
                GridCell.getContent(grid.getCells().get(targetAttributeRowIndex2.get(0)), "Unique").contains("true"));

        // Risetto i campi com'erano prima della modifica
        clickEditButton();

        clickUniqueCheckbox();

        clickSaveButton();

        testEnd(context);
    }

    @Test
    public void editDescription() {
        UITestContext context = getDefaultTestContextInstance();
        context.withRule(UITestRule.defineClientLogCheckRule(null, ImmutableList.of("404", "401"), null, null));
        testStart(context);

        goFullScreen();
        login(Login.admin());
        sleep(context);

        ExtjsUtils.switchToAdminModule2(getDriver(), context);
        sleep(context);

        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "Floor");
        selectAdministrationClassTab("Attributes");

        expandRowAdministrationGrid("Code");

        clickEditButton();

        WebElement descriptionInput = ExtjsUtils.findElementByXPath(getDriver(),
                "//div[@data-testid=\"cards-card-administration-detailsWindow\"]//input[@name='description']");
        safeClick(descriptionInput);
        sleep(context);

        descriptionInput.clear();

        descriptionInput.sendKeys("test");

        sleep(context);

        clickSaveButton();

        sleep(context);

        ExtjsGenericGrid grid = ExtjsGenericGrid
                .from(getDriver(), testIdLocator("administration-content-classes-tabitems-attributes-attributes"),
                        By.className("x-grid-header-ct"), By.className("x-grid-body"))
                .extract();
        List<Integer> targetAttributeRowIndex2 = grid.getIndexOfRowsCointainingAllCells(GridCell.from("Name", "Code"));

        assertTrue(GridCell.getContent(grid.getCells().get(targetAttributeRowIndex2.get(0)), "Description")
                .contains("test"));

        clickEditButton();

        WebElement descriptionInput2 = ExtjsUtils.findElementByXPath(getDriver(),
                "//div[@data-testid=\"cards-card-administration-detailsWindow\"]//input[@name='description']");
        safeClick(descriptionInput2);
        sleep(context);

        descriptionInput2.clear();

        descriptionInput2.sendKeys("Code");

        sleep(context);
        clickSaveButton();

        testEnd(context);

    }

    @Test
    public void editHidden() {
        UITestContext context = getDefaultTestContextInstance();
        context.withRule(UITestRule.defineClientLogCheckRule(null, ImmutableList.of("404", "401"), null, null));
        testStart(context);

        goFullScreen();
        login(Login.admin());
        sleep(context);

        ExtjsUtils.switchToAdminModule2(getDriver(), context);
        sleep(context);

        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "Building");
        selectAdministrationClassTab("Attributes");

        expandRowAdministrationGrid("Address");

        clickEditButton();

        sleep(context);

        // cerco e clicco sulla freccetta per aprire il menu dropdown
        WebElement mode = ExtjsUtils.findElementByXPath(getDriver(),
                "//input[@name='mode']/ancestor::div[@data-ref='inputWrap']/following-sibling::div");
        safeClick(mode);
        sleep(context);

        // clicco sulla voce hidden del menu dropDown
        WebElement menuDropDownHidden = ExtjsUtils.findElementByXPath(getDriver(), "//li[text()=\"Hidden\"]");
        safeClick(menuDropDownHidden);

        clickSaveButton();
        sleep(context);

        // riporto l'attributo ad editable
        clickEditButton();

        WebElement mode2 = ExtjsUtils.findElementByXPath(getDriver(),
                "//input[@name='mode']/ancestor::div[@data-ref='inputWrap']/following-sibling::div");
        safeClick(mode2);

        WebElement menuDropDownEditable = ExtjsUtils.findElementByXPath(getDriver(), "//li[text()=\"Editable\"]");
        safeClick(menuDropDownEditable);

        clickSaveButton();

        testEnd(context);
    }

    @Test
    public void editActive() {
        UITestContext context = getDefaultTestContextInstance();
        context.withRule(UITestRule.defineClientLogCheckRule(null, ImmutableList.of("404", "401"), null, null));
        testStart(context);

        goFullScreen();
        login(Login.admin());
        sleep(context);

        ExtjsUtils.switchToAdminModule2(getDriver(), context);
        sleep(context);

        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "Disk");
        selectAdministrationClassTab("Attributes");

        expandRowAdministrationGrid("Used");

        sleep(context);

        clickEditButton();
        sleep(context);

        clickActiveCheckbox();

        clickSaveButton();

        sleep(context);

        // Controllo che Unique sia posto a true dalla modifica effettuata
        ExtjsGenericGrid grid = ExtjsGenericGrid
                .from(getDriver(), testIdLocator("administration-components-attributes-grid"),
                        By.className("x-grid-header-ct"), By.className("x-grid-body"))
                .extract();
        List<Integer> targetAttributeRowIndex = grid.getIndexOfRowsCointainingAllCells(GridCell.from("Name", "Used"));

        assertTrue(
                GridCell.getContent(grid.getCells().get(targetAttributeRowIndex.get(0)), "Active").contains("false"));

        // riporto il campo alla situzione iniziale
        clickEditButton();
        sleep(context);

        clickActiveCheckbox();

        clickSaveButton();
        testEnd(context);
    }

    @Test
    public void editReadonly() {
        UITestContext context = getDefaultTestContextInstance();
        context.withRule(UITestRule.defineClientLogCheckRule(null, ImmutableList.of("404", "401"), null, null));
        testStart(context);

        goFullScreen();
        login(Login.admin());
        sleep(context);

        ExtjsUtils.switchToAdminModule2(getDriver(), context);
        sleep(context);

        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "Infrastructure");
        selectAdministrationClassTab("Attributes");

        expandRowAdministrationGrid("Name");

        clickEditButton();

        sleep(context);

        // cerco e clicco sulla freccetta per aprire il menu dropdown
        WebElement mode = ExtjsUtils.findElementByXPath(getDriver(),
                "//input[@name='mode']/ancestor::div[@data-ref='inputWrap']/following-sibling::div");
        safeClick(mode);
        sleep(context);

        // clicco sulla voce hidden del menu dropDown
        WebElement menuDropDownHidden = ExtjsUtils.findElementByXPath(getDriver(), "//li[text()=\"Read only\"]");
        safeClick(menuDropDownHidden);

        clickSaveButton();
        sleep(context);

        // Controllo che Unique sia posto a true dalla modifica effettuata
        ExtjsGenericGrid grid = ExtjsGenericGrid
                .from(getDriver(), testIdLocator("administration-content-classes-tabitems-attributes-attributes"),
                        By.className("x-grid-header-ct"), By.className("x-grid-body"))
                .extract();
        List<Integer> targetAttributeRowIndex = grid.getIndexOfRowsCointainingAllCells(GridCell.from("Name", "Name"));

        assertTrue(GridCell.getContent(grid.getCells().get(targetAttributeRowIndex.get(0)), "Editing mode")
                .contains("Read only"));

        // riporto l'attributo ad editable
        clickEditButton();

        WebElement mode2 = ExtjsUtils.findElementByXPath(getDriver(),
                "//input[@name='mode']/ancestor::div[@data-ref='inputWrap']/following-sibling::div");
        safeClick(mode2);
        sleep(context);

        WebElement menuDropDownEditable = ExtjsUtils.findElementByXPath(getDriver(), "//li[text()=\"Editable\"]");
        safeClick(menuDropDownEditable);

        clickSaveButton();
        testEnd(context);
    }

    @Test
    public void editInhetitedAttributeFromSuperClass() {
        UITestContext context = getDefaultTestContextInstance();
        context.withRule(UITestRule.defineClientLogCheckRule(null, ImmutableList.of("404", "401"), null, null));
        testStart(context);

        goFullScreen();
        login(Login.admin());
        sleep(context);

        ExtjsUtils.switchToAdminModule2(getDriver(), context);
        sleep(context);

        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "Company", "Customer");
        selectAdministrationClassTab("Attributes");

        expandRowAdministrationGrid("Website");

        clickEditButton();
        sleep(context);

        WebElement nameForm = ExtjsUtils.findElementByXPath(getDriver(), "//input[@name='name']");
        assertFalse(nameForm.isEnabled());

        WebElement descriptionForm = ExtjsUtils.findElementByXPath(getDriver(), "//fieldset[@aria-label='General properties field set']//input[@name='description']");
        assertTrue(descriptionForm.isEnabled());
        descriptionForm.clear();
        descriptionForm.sendKeys("Test Edit description");
        sleep(context);

        clickSaveButton();
        sleep(context);

        ExtjsGenericGrid grid = ExtjsGenericGrid
                .from(getDriver(), testIdLocator("administration-components-attributes-grid"),
                        By.className("x-grid-header-ct"), By.className("x-grid-body"))
                .extract();
        List<Integer> targetAttributeRowIndex = grid.getIndexOfRowsCointainingAllCells(GridCell.from("Name", "Website"));

        assertTrue(
                GridCell.getContent(grid.getCells().get(targetAttributeRowIndex.get(0)), "Description").contains("Test Edit description"));

        clickEditButton();
        sleep(context);

        WebElement descriptionForm2 = ExtjsUtils.findElementByXPath(getDriver(), "//fieldset[@aria-label='General properties field set']//input[@name='description']");
        assertTrue(descriptionForm2.isEnabled());
        descriptionForm2.clear();
        descriptionForm2.sendKeys("Website");
        clickSaveButton();

        testEnd(context);

    }

    @Test
    public void editSuperClassAttribute() {
        UITestContext context = getDefaultTestContextInstance();
        context.withRule(UITestRule.defineClientLogCheckRule(null, ImmutableList.of("404", "401"), null, null));
        testStart(context);

        goFullScreen();
        login(Login.admin());
        sleep(context);

        ExtjsUtils.switchToAdminModule2(getDriver(), context);
        sleep(context);

        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "Company");
        selectAdministrationClassTab("Attributes");

        expandRowAdministrationGrid("Website");

        clickEditButton();
        sleep(context);

        WebElement nameForm = ExtjsUtils.findElementByXPath(getDriver(), "//input[@name='name']");
        assertFalse(nameForm.isEnabled());

        WebElement descriptionForm = ExtjsUtils.findElementByXPath(getDriver(), "//fieldset[@aria-label='General properties field set']//input[@name='description']");
        assertTrue(descriptionForm.isEnabled());
        descriptionForm.clear();
        descriptionForm.sendKeys("Test Edit description");
        sleep(context);

        clickSaveButton();
        sleep(context);

        ExtjsGenericGrid grid1 = ExtjsGenericGrid
                .from(getDriver(), testIdLocator("administration-components-attributes-grid"),
                        By.className("x-grid-header-ct"), By.className("x-grid-body"))
                .extract();
        List<Integer> targetAttributeRowIndex = grid1.getIndexOfRowsCointainingAllCells(GridCell.from("Name", "Website"));

        assertTrue(
                GridCell.getContent(grid1.getCells().get(targetAttributeRowIndex.get(0)), "Description").contains("Test Edit description"));

        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "Company", "Corporate group");
        selectAdministrationClassTab("Attributes");
        sleep(context);

        ExtjsGenericGrid grid2 = ExtjsGenericGrid
                .from(getDriver(), testIdLocator("administration-components-attributes-grid"),
                        By.className("x-grid-header-ct"), By.className("x-grid-body"))
                .extract();
        List<Integer> targetAttributeRowIndex2 = grid2.getIndexOfRowsCointainingAllCells(GridCell.from("Name", "Website"));

        assertTrue(
                GridCell.getContent(grid2.getCells().get(targetAttributeRowIndex2.get(0)), "Description").contains("Test Edit description"));

        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "Company", "Customer");
        selectAdministrationClassTab("Attributes");
        sleep(context);

        ExtjsGenericGrid grid3 = ExtjsGenericGrid
                .from(getDriver(), testIdLocator("administration-components-attributes-grid"),
                        By.className("x-grid-header-ct"), By.className("x-grid-body"))
                .extract();
        List<Integer> targetAttributeRowIndex3 = grid3.getIndexOfRowsCointainingAllCells(GridCell.from("Name", "Website"));

        assertTrue(
                GridCell.getContent(grid3.getCells().get(targetAttributeRowIndex3.get(0)), "Description").contains("Test Edit description"));

        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "Company", "Supplier");
        selectAdministrationClassTab("Attributes");
        sleep(context);

        ExtjsGenericGrid grid4 = ExtjsGenericGrid
                .from(getDriver(), testIdLocator("administration-components-attributes-grid"),
                        By.className("x-grid-header-ct"), By.className("x-grid-body"))
                .extract();
        List<Integer> targetAttributeRowIndex4 = grid4.getIndexOfRowsCointainingAllCells(GridCell.from("Name", "Website"));

        assertTrue(
                GridCell.getContent(grid4.getCells().get(targetAttributeRowIndex4.get(0)), "Description").contains("Test Edit description"));

        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "Company");
        selectAdministrationClassTab("Attributes");
        sleep(context);

        expandRowAdministrationGrid("Website");

        clickEditButton();
        sleep(context);

        WebElement descriptionForm2 = ExtjsUtils.findElementByXPath(getDriver(), "//fieldset[@aria-label='General properties field set']//input[@name='description']");
        assertTrue(descriptionForm2.isEnabled());
        descriptionForm2.clear();
        descriptionForm2.sendKeys("Website");
        clickSaveButton();

        testEnd(context);
    }

    @Test
    public void createDeleteNewAttributeBoolean() {

        UITestContext context = getDefaultTestContextInstance();
        String type = "BOOLEAN";

        testStart(context);
        goFullScreen();
        login(Login.admin());
        sleep(context);
        ExtjsUtils.switchToAdminModule2(getDriver(), context);
        sleep(context);

        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "KPI");

        selectAdministrationClassTab("Attributes");

        WebElement attributesClassGrid = ExtjsUtils.findElementByTestId(getDriver(),
                "administration-components-attributes-grid");
        attributesClassGrid.findElement(By.tagName("a")).click();
        sleep(context);

        WebElement detailsWindow = waitForElementVisibility(testIdLocator("cards-card-administration-detailsWindow"));
        Optional<WebElement> saveButton = detailsWindow.findElements(By.tagName("a")).stream()
                .filter(a -> "Save".equals(normalized(a.getText()))).findFirst();
        assertTrue(saveButton.get().getAttribute("class").contains("x-btn-disabled"));

        WebElement grid1 = getDriver().switchTo().activeElement()
                .findElement(By.xpath("//fieldset[@aria-label='General properties field set']"));

        grid1.findElement(By.name("name")).sendKeys("Test01");

        ExtjsUtils.findElementByXPath(getDriver(),
                "//fieldset[@aria-label='Type properties field set']//div[contains(@class, 'x-form-arrow-trigger')]")
                .click();
        ExtjsUtils.findElementByXPath(getDriver(), "//div//ul//li[text()='" + type + "']").click();
        sleep(context);

        clickSaveButton();
        sleep(context);

        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "Floor");
        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "KPI");
        sleep(context);

        ExtjsGenericGrid grid = ExtjsGenericGrid
                .from(getDriver(), testIdLocator("administration-components-attributes-grid"),
                        By.className("x-grid-header-ct"), By.className("x-grid-body"))
                .extract();
        List<Integer> targetAttributeRowIndex = grid.getIndexOfRowsCointainingAllCells(GridCell.from("Name", "Test01"));

        assertTrue(
                GridCell.getContent(grid.getCells().get(targetAttributeRowIndex.get(0)), "Type").contains(type));

        expandRowAdministrationGrid("Test01");
        sleep(context);

        clickDisableButton();
        clickDeleteButton();

        List<String> kpiListAttributes = given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
                .get(buildRestV3Url("classes/KPI/attributes")).then().statusCode(200).extract().jsonPath()
                .getList(("data.findAll{ it._id == 'Test01'}._id"));

        if (kpiListAttributes.size() != 0) {
            given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
                    .delete(buildRestV3Url("classes/KPI/attributes/" + kpiListAttributes.get(0))).then()
                    .statusCode(200);
        }

        testEnd(context);
    }

    @Test
    public void createDeleteNewChar() {

        UITestContext context = getDefaultTestContextInstance();
        String type = "CHAR";

        testStart(context);
        goFullScreen();
        login(Login.admin());
        sleep(context);
        ExtjsUtils.switchToAdminModule2(getDriver(), context);
        sleep(context);

        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "KPI");

        selectAdministrationClassTab("Attributes");

        WebElement attributesClassGrid = ExtjsUtils.findElementByTestId(getDriver(),
                "administration-components-attributes-grid");
        attributesClassGrid.findElement(By.tagName("a")).click();
        sleep(context);

        WebElement detailsWindow = waitForElementVisibility(testIdLocator("cards-card-administration-detailsWindow"));
        Optional<WebElement> saveButton = detailsWindow.findElements(By.tagName("a")).stream()
                .filter(a -> "Save".equals(normalized(a.getText()))).findFirst();
        assertTrue(saveButton.get().getAttribute("class").contains("x-btn-disabled"));

        WebElement grid1 = getDriver().switchTo().activeElement()
                .findElement(By.xpath("//fieldset[@aria-label='General properties field set']"));

        grid1.findElement(By.name("name")).sendKeys("Test01");

        ExtjsUtils.findElementByXPath(getDriver(),
                "//fieldset[@aria-label='Type properties field set']//div[contains(@class, 'x-form-arrow-trigger')]")
                .click();
        ExtjsUtils.findElementByXPath(getDriver(), "//div//ul//li[text()='" + type + "']").click();
        sleep(context);

        clickSaveButton();
        sleep(context);

        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "Floor");
        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "KPI");
        sleep(context);

        ExtjsGenericGrid grid = ExtjsGenericGrid
                .from(getDriver(), testIdLocator("administration-components-attributes-grid"),
                        By.className("x-grid-header-ct"), By.className("x-grid-body"))
                .extract();
        List<Integer> targetAttributeRowIndex = grid.getIndexOfRowsCointainingAllCells(GridCell.from("Name", "Test01"));

        assertTrue(
                GridCell.getContent(grid.getCells().get(targetAttributeRowIndex.get(0)), "Type").contains(type));

        expandRowAdministrationGrid("Test01");
        sleep(context);

        clickDisableButton();
        clickDeleteButton();

        List<String> kpiListAttributes = given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
                .get(buildRestV3Url("classes/KPI/attributes")).then().statusCode(200).extract().jsonPath()
                .getList(("data.findAll{ it._id == 'Test01'}._id"));

        if (kpiListAttributes.size() != 0) {
            given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
                    .delete(buildRestV3Url("classes/KPI/attributes/" + kpiListAttributes.get(0))).then()
                    .statusCode(200);
        }

        testEnd(context);
    }

    @Test
    public void createDeleteNewDate() {

        UITestContext context = getDefaultTestContextInstance();
        String type = "DATE";

        testStart(context);
        goFullScreen();
        login(Login.admin());
        sleep(context);
        ExtjsUtils.switchToAdminModule2(getDriver(), context);
        sleep(context);

        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "KPI");

        selectAdministrationClassTab("Attributes");

        WebElement attributesClassGrid = ExtjsUtils.findElementByTestId(getDriver(),
                "administration-components-attributes-grid");
        attributesClassGrid.findElement(By.tagName("a")).click();
        sleep(context);

        WebElement detailsWindow = waitForElementVisibility(testIdLocator("cards-card-administration-detailsWindow"));
        Optional<WebElement> saveButton = detailsWindow.findElements(By.tagName("a")).stream()
                .filter(a -> "Save".equals(normalized(a.getText()))).findFirst();
        assertTrue(saveButton.get().getAttribute("class").contains("x-btn-disabled"));

        WebElement grid1 = getDriver().switchTo().activeElement()
                .findElement(By.xpath("//fieldset[@aria-label='General properties field set']"));

        grid1.findElement(By.name("name")).sendKeys("Test01");

        ExtjsUtils.findElementByXPath(getDriver(),
                "//fieldset[@aria-label='Type properties field set']//div[contains(@class, 'x-form-arrow-trigger')]")
                .click();
        ExtjsUtils.findElementByXPath(getDriver(), "//div//ul//li[text()='" + type + "']").click();
        sleep(context);

        clickSaveButton();
        sleep(context);

        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "Floor");
        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "KPI");
        sleep(context);

        ExtjsGenericGrid grid = ExtjsGenericGrid
                .from(getDriver(), testIdLocator("administration-components-attributes-grid"),
                        By.className("x-grid-header-ct"), By.className("x-grid-body"))
                .extract();
        List<Integer> targetAttributeRowIndex = grid.getIndexOfRowsCointainingAllCells(GridCell.from("Name", "Test01"));

        assertTrue(
                GridCell.getContent(grid.getCells().get(targetAttributeRowIndex.get(0)), "Type").contains(type));

        expandRowAdministrationGrid("Test01");
        sleep(context);

        clickDisableButton();
        clickDeleteButton();

        List<String> kpiListAttributes = given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
                .get(buildRestV3Url("classes/KPI/attributes")).then().statusCode(200).extract().jsonPath()
                .getList(("data.findAll{ it._id == 'Test01'}._id"));

        if (kpiListAttributes.size() != 0) {
            given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
                    .delete(buildRestV3Url("classes/KPI/attributes/" + kpiListAttributes.get(0))).then()
                    .statusCode(200);
        }

        testEnd(context);
    }

    @Test
    public void createDeleteNewDecimal() {

        UITestContext context = getDefaultTestContextInstance();
        String type = "DECIMAL";

        testStart(context);
        goFullScreen();
        login(Login.admin());
        sleep(context);
        ExtjsUtils.switchToAdminModule2(getDriver(), context);
        sleep(context);

        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "KPI");

        selectAdministrationClassTab("Attributes");

        WebElement attributesClassGrid = ExtjsUtils.findElementByTestId(getDriver(),
                "administration-components-attributes-grid");
        attributesClassGrid.findElement(By.tagName("a")).click();
        sleep(context);

        WebElement detailsWindow = waitForElementVisibility(testIdLocator("cards-card-administration-detailsWindow"));
        Optional<WebElement> saveButton = detailsWindow.findElements(By.tagName("a")).stream()
                .filter(a -> "Save".equals(normalized(a.getText()))).findFirst();
        assertTrue(saveButton.get().getAttribute("class").contains("x-btn-disabled"));

        WebElement grid1 = getDriver().switchTo().activeElement()
                .findElement(By.xpath("//fieldset[@aria-label='General properties field set']"));

        grid1.findElement(By.name("name")).sendKeys("Test01");

        ExtjsUtils.findElementByXPath(getDriver(),
                "//fieldset[@aria-label='Type properties field set']//div[contains(@class, 'x-form-arrow-trigger')]")
                .click();
        ExtjsUtils.findElementByXPath(getDriver(), "//div//ul//li[text()='" + type + "']").click();
        sleep(context);

        WebElement precision = ExtjsUtils.waitForElementPresence(getDriver(), By.name("precision"));
        precision.clear();
        precision.sendKeys("78");

        WebElement scale = ExtjsUtils.waitForElementPresence(getDriver(), By.name("scale"));
        scale.clear();
        scale.sendKeys("8");

        clickSaveButton();
        sleep(context);

        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "Floor");
        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "KPI");
        sleep(context);

        ExtjsGenericGrid grid = ExtjsGenericGrid
                .from(getDriver(), testIdLocator("administration-components-attributes-grid"),
                        By.className("x-grid-header-ct"), By.className("x-grid-body"))
                .extract();
        List<Integer> targetAttributeRowIndex = grid.getIndexOfRowsCointainingAllCells(GridCell.from("Name", "Test01"));

        assertTrue(
                GridCell.getContent(grid.getCells().get(targetAttributeRowIndex.get(0)), "Type").contains(type));

        expandRowAdministrationGrid("Test01");
        sleep(context);

        clickDisableButton();
        clickDeleteButton();

        List<String> kpiListAttributes = given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
                .get(buildRestV3Url("classes/KPI/attributes")).then().statusCode(200).extract().jsonPath()
                .getList(("data.findAll{ it._id == 'Test01'}._id"));

        if (kpiListAttributes.size() != 0) {
            given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
                    .delete(buildRestV3Url("classes/KPI/attributes/" + kpiListAttributes.get(0))).then()
                    .statusCode(200);
        }

        testEnd(context);
    }

    @Test
    public void createDeleteNewDouble() {

        UITestContext context = getDefaultTestContextInstance();
        String type = "DOUBLE";

        testStart(context);
        goFullScreen();
        login(Login.admin());
        sleep(context);
        ExtjsUtils.switchToAdminModule2(getDriver(), context);
        sleep(context);

        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "KPI");

        selectAdministrationClassTab("Attributes");

        WebElement attributesClassGrid = ExtjsUtils.findElementByTestId(getDriver(),
                "administration-components-attributes-grid");
        attributesClassGrid.findElement(By.tagName("a")).click();
        sleep(context);

        WebElement detailsWindow = waitForElementVisibility(testIdLocator("cards-card-administration-detailsWindow"));
        Optional<WebElement> saveButton = detailsWindow.findElements(By.tagName("a")).stream()
                .filter(a -> "Save".equals(normalized(a.getText()))).findFirst();
        assertTrue(saveButton.get().getAttribute("class").contains("x-btn-disabled"));

        WebElement grid1 = getDriver().switchTo().activeElement()
                .findElement(By.xpath("//fieldset[@aria-label='General properties field set']"));

        grid1.findElement(By.name("name")).sendKeys("Test01");

        ExtjsUtils.findElementByXPath(getDriver(),
                "//fieldset[@aria-label='Type properties field set']//div[contains(@class, 'x-form-arrow-trigger')]")
                .click();
        ExtjsUtils.findElementByXPath(getDriver(), "//div//ul//li[text()='" + type + "']").click();
        sleep(context);

        clickSaveButton();
        sleep(context);

        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "Floor");
        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "KPI");
        sleep(context);

        ExtjsGenericGrid grid = ExtjsGenericGrid
                .from(getDriver(), testIdLocator("administration-components-attributes-grid"),
                        By.className("x-grid-header-ct"), By.className("x-grid-body"))
                .extract();
        List<Integer> targetAttributeRowIndex = grid.getIndexOfRowsCointainingAllCells(GridCell.from("Name", "Test01"));

        assertTrue(
                GridCell.getContent(grid.getCells().get(targetAttributeRowIndex.get(0)), "Type").contains(type));

        expandRowAdministrationGrid("Test01");
        sleep(context);

        clickDisableButton();
        clickDeleteButton();

        List<String> kpiListAttributes = given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
                .get(buildRestV3Url("classes/KPI/attributes")).then().statusCode(200).extract().jsonPath()
                .getList(("data.findAll{ it._id == 'Test01'}._id"));

        if (kpiListAttributes.size() != 0) {
            given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
                    .delete(buildRestV3Url("classes/KPI/attributes/" + kpiListAttributes.get(0))).then()
                    .statusCode(200);
        }

        testEnd(context);
    }

    @Test
    public void createDeleteNewInteger() {

        UITestContext context = getDefaultTestContextInstance();
        String type = "INTEGER";

        testStart(context);
        goFullScreen();
        login(Login.admin());
        sleep(context);
        ExtjsUtils.switchToAdminModule2(getDriver(), context);
        sleep(context);

        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "KPI");

        selectAdministrationClassTab("Attributes");

        WebElement attributesClassGrid = ExtjsUtils.findElementByTestId(getDriver(),
                "administration-components-attributes-grid");
        attributesClassGrid.findElement(By.tagName("a")).click();
        sleep(context);

        WebElement detailsWindow = waitForElementVisibility(testIdLocator("cards-card-administration-detailsWindow"));
        Optional<WebElement> saveButton = detailsWindow.findElements(By.tagName("a")).stream()
                .filter(a -> "Save".equals(normalized(a.getText()))).findFirst();
        assertTrue(saveButton.get().getAttribute("class").contains("x-btn-disabled"));

        WebElement grid1 = getDriver().switchTo().activeElement()
                .findElement(By.xpath("//fieldset[@aria-label='General properties field set']"));

        grid1.findElement(By.name("name")).sendKeys("Test01");

        ExtjsUtils.findElementByXPath(getDriver(),
                "//fieldset[@aria-label='Type properties field set']//div[contains(@class, 'x-form-arrow-trigger')]")
                .click();
        ExtjsUtils.findElementByXPath(getDriver(), "//div//ul//li[text()='" + type + "']").click();
        sleep(context);

        clickSaveButton();
        sleep(context);

        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "Floor");
        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "KPI");
        sleep(context);

        ExtjsGenericGrid grid = ExtjsGenericGrid
                .from(getDriver(), testIdLocator("administration-components-attributes-grid"),
                        By.className("x-grid-header-ct"), By.className("x-grid-body"))
                .extract();
        List<Integer> targetAttributeRowIndex = grid.getIndexOfRowsCointainingAllCells(GridCell.from("Name", "Test01"));

        assertTrue(
                GridCell.getContent(grid.getCells().get(targetAttributeRowIndex.get(0)), "Type").contains(type));

        expandRowAdministrationGrid("Test01");
        sleep(context);

        clickDisableButton();
        clickDeleteButton();

        List<String> kpiListAttributes = given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
                .get(buildRestV3Url("classes/KPI/attributes")).then().statusCode(200).extract().jsonPath()
                .getList(("data.findAll{ it._id == 'Test01'}._id"));

        if (kpiListAttributes.size() != 0) {
            given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
                    .delete(buildRestV3Url("classes/KPI/attributes/" + kpiListAttributes.get(0))).then()
                    .statusCode(200);
        }

        testEnd(context);
    }

    @Test
    public void createDeleteNewIpAddress() {

        UITestContext context = getDefaultTestContextInstance();
        String type = "IP_ADDRESS";

        testStart(context);
        goFullScreen();
        login(Login.admin());
        sleep(context);
        ExtjsUtils.switchToAdminModule2(getDriver(), context);
        sleep(context);

        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "KPI");

        selectAdministrationClassTab("Attributes");

        WebElement attributesClassGrid = ExtjsUtils.findElementByTestId(getDriver(),
                "administration-components-attributes-grid");
        attributesClassGrid.findElement(By.tagName("a")).click();
        sleep(context);

        WebElement detailsWindow = waitForElementVisibility(testIdLocator("cards-card-administration-detailsWindow"));
        Optional<WebElement> saveButton = detailsWindow.findElements(By.tagName("a")).stream()
                .filter(a -> "Save".equals(normalized(a.getText()))).findFirst();
        assertTrue(saveButton.get().getAttribute("class").contains("x-btn-disabled"));

        WebElement grid1 = getDriver().switchTo().activeElement()
                .findElement(By.xpath("//fieldset[@aria-label='General properties field set']"));

        grid1.findElement(By.name("name")).sendKeys("Test01");

        ExtjsUtils.findElementByXPath(getDriver(),
                "//fieldset[@aria-label='Type properties field set']//div[contains(@class, 'x-form-arrow-trigger')]")
                .click();
        ExtjsUtils.findElementByXPath(getDriver(), "//div//ul//li[text()='" + type + "']").click();
        sleep(context);

        WebElement precision = ExtjsUtils.waitForElementPresence(getDriver(), By.name("ipType"));
        precision.clear();
        precision.sendKeys("IPV4");

        clickSaveButton();
        sleep(context);

        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "Floor");
        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "KPI");
        sleep(context);

        ExtjsGenericGrid grid = ExtjsGenericGrid
                .from(getDriver(), testIdLocator("administration-components-attributes-grid"),
                        By.className("x-grid-header-ct"), By.className("x-grid-body"))
                .extract();
        List<Integer> targetAttributeRowIndex = grid.getIndexOfRowsCointainingAllCells(GridCell.from("Name", "Test01"));

        assertTrue(
                GridCell.getContent(grid.getCells().get(targetAttributeRowIndex.get(0)), "Type").contains(type));

        expandRowAdministrationGrid("Test01");
        sleep(context);

        clickDisableButton();
        clickDeleteButton();

        List<String> kpiListAttributes = given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
                .get(buildRestV3Url("classes/KPI/attributes")).then().statusCode(200).extract().jsonPath()
                .getList(("data.findAll{ it._id == 'Test01'}._id"));

        if (kpiListAttributes.size() != 0) {
            given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
                    .delete(buildRestV3Url("classes/KPI/attributes/" + kpiListAttributes.get(0))).then()
                    .statusCode(200);
        }

        testEnd(context);
    }

    @Test
    public void createDeleteNewLookUp() {

        UITestContext context = getDefaultTestContextInstance();
        String type = "LOOKUP";

        testStart(context);
        goFullScreen();
        login(Login.admin());
        sleep(context);
        ExtjsUtils.switchToAdminModule2(getDriver(), context);
        sleep(context);

        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "KPI");

        selectAdministrationClassTab("Attributes");

        WebElement attributesClassGrid = ExtjsUtils.findElementByTestId(getDriver(),
                "administration-components-attributes-grid");
        attributesClassGrid.findElement(By.tagName("a")).click();
        sleep(context);

        WebElement detailsWindow = waitForElementVisibility(testIdLocator("cards-card-administration-detailsWindow"));
        Optional<WebElement> saveButton = detailsWindow.findElements(By.tagName("a")).stream()
                .filter(a -> "Save".equals(normalized(a.getText()))).findFirst();
        assertTrue(saveButton.get().getAttribute("class").contains("x-btn-disabled"));

        WebElement grid1 = getDriver().switchTo().activeElement()
                .findElement(By.xpath("//fieldset[@aria-label='General properties field set']"));

        grid1.findElement(By.name("name")).sendKeys("Test01");

        ExtjsUtils.findElementByXPath(getDriver(),
                "//fieldset[@aria-label='Type properties field set']//div[contains(@class, 'x-form-arrow-trigger')]")
                .click();
        ExtjsUtils.findElementByXPath(getDriver(), "//div//ul//li[text()='" + type + "']").click();
        sleep(context);

        int indice = RandomUtils.nextInt(0, 130);

        WebElement lookup = ExtjsUtils.waitForElementPresence(getDriver(), By.name("lookup"));
        WebElement div = ExtjsUtils.getParent(ExtjsUtils.getParent(lookup));
        div.findElement(By.className("x-form-arrow-trigger-default")).click();
        sleep(context);
        ExtjsUtils.waitForElementPresence(getDriver(), By.xpath("//div//ul//li[@data-recordindex='" + indice + "']")).click();;

        clickSaveButton();
        sleep(context);

        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "Floor");
        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "KPI");
        sleep(context);

        ExtjsGenericGrid grid = ExtjsGenericGrid
                .from(getDriver(), testIdLocator("administration-components-attributes-grid"),
                        By.className("x-grid-header-ct"), By.className("x-grid-body"))
                .extract();
        List<Integer> targetAttributeRowIndex = grid.getIndexOfRowsCointainingAllCells(GridCell.from("Name", "Test01"));

        assertTrue(
                GridCell.getContent(grid.getCells().get(targetAttributeRowIndex.get(0)), "Type").contains(type));

        expandRowAdministrationGrid("Test01");
        sleep(context);

        clickDisableButton();
        clickDeleteButton();

        List<String> kpiListAttributes = given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
                .get(buildRestV3Url("classes/KPI/attributes")).then().statusCode(200).extract().jsonPath()
                .getList(("data.findAll{ it._id == 'Test01'}._id"));

        if (kpiListAttributes.size() != 0) {
            given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
                    .delete(buildRestV3Url("classes/KPI/attributes/" + kpiListAttributes.get(0))).then()
                    .statusCode(200);
        }

        testEnd(context);
    }

    @Test
    public void createDeleteNewTime() {

        UITestContext context = getDefaultTestContextInstance();
        String type = "TIME";

        testStart(context);
        goFullScreen();
        login(Login.admin());
        sleep(context);
        ExtjsUtils.switchToAdminModule2(getDriver(), context);
        sleep(context);

        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "KPI");

        selectAdministrationClassTab("Attributes");

        WebElement attributesClassGrid = ExtjsUtils.findElementByTestId(getDriver(),
                "administration-components-attributes-grid");
        attributesClassGrid.findElement(By.tagName("a")).click();
        sleep(context);

        WebElement detailsWindow = waitForElementVisibility(testIdLocator("cards-card-administration-detailsWindow"));
        Optional<WebElement> saveButton = detailsWindow.findElements(By.tagName("a")).stream()
                .filter(a -> "Save".equals(normalized(a.getText()))).findFirst();
        assertTrue(saveButton.get().getAttribute("class").contains("x-btn-disabled"));

        WebElement grid1 = getDriver().switchTo().activeElement()
                .findElement(By.xpath("//fieldset[@aria-label='General properties field set']"));

        grid1.findElement(By.name("name")).sendKeys("Test01");

        ExtjsUtils.findElementByXPath(getDriver(),
                "//fieldset[@aria-label='Type properties field set']//div[contains(@class, 'x-form-arrow-trigger')]")
                .click();
        ExtjsUtils.findElementByXPath(getDriver(), "//div//ul//li[text()='" + type + "']").click();
        sleep(context);

        clickSaveButton();
        sleep(context);

        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "Floor");
        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "KPI");
        sleep(context);

        ExtjsGenericGrid grid = ExtjsGenericGrid
                .from(getDriver(), testIdLocator("administration-components-attributes-grid"),
                        By.className("x-grid-header-ct"), By.className("x-grid-body"))
                .extract();
        List<Integer> targetAttributeRowIndex = grid.getIndexOfRowsCointainingAllCells(GridCell.from("Name", "Test01"));

        assertTrue(
                GridCell.getContent(grid.getCells().get(targetAttributeRowIndex.get(0)), "Type").contains(type));

        expandRowAdministrationGrid("Test01");
        sleep(context);

        clickDisableButton();
        clickDeleteButton();

        List<String> kpiListAttributes = given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
                .get(buildRestV3Url("classes/KPI/attributes")).then().statusCode(200).extract().jsonPath()
                .getList(("data.findAll{ it._id == 'Test01'}._id"));

        if (kpiListAttributes.size() != 0) {
            given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
                    .delete(buildRestV3Url("classes/KPI/attributes/" + kpiListAttributes.get(0))).then()
                    .statusCode(200);
        }

        testEnd(context);
    }

    @Test
    public void createDeleteNewTimestamp() {

        UITestContext context = getDefaultTestContextInstance();
        String type = "TIMESTAMP";

        testStart(context);
        goFullScreen();
        login(Login.admin());
        sleep(context);
        ExtjsUtils.switchToAdminModule2(getDriver(), context);
        sleep(context);

        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "KPI");

        selectAdministrationClassTab("Attributes");

        WebElement attributesClassGrid = ExtjsUtils.findElementByTestId(getDriver(),
                "administration-components-attributes-grid");
        attributesClassGrid.findElement(By.tagName("a")).click();
        sleep(context);

        WebElement detailsWindow = waitForElementVisibility(testIdLocator("cards-card-administration-detailsWindow"));
        Optional<WebElement> saveButton = detailsWindow.findElements(By.tagName("a")).stream()
                .filter(a -> "Save".equals(normalized(a.getText()))).findFirst();
        assertTrue(saveButton.get().getAttribute("class").contains("x-btn-disabled"));

        WebElement grid1 = getDriver().switchTo().activeElement()
                .findElement(By.xpath("//fieldset[@aria-label='General properties field set']"));

        grid1.findElement(By.name("name")).sendKeys("Test01");

        ExtjsUtils.findElementByXPath(getDriver(),
                "//fieldset[@aria-label='Type properties field set']//div[contains(@class, 'x-form-arrow-trigger')]")
                .click();
        ExtjsUtils.findElementByXPath(getDriver(), "//div//ul//li[text()='" + type + "']").click();
        sleep(context);

        clickSaveButton();
        sleep(context);

        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "Floor");
        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "KPI");
        sleep(context);

        ExtjsGenericGrid grid = ExtjsGenericGrid
                .from(getDriver(), testIdLocator("administration-components-attributes-grid"),
                        By.className("x-grid-header-ct"), By.className("x-grid-body"))
                .extract();
        List<Integer> targetAttributeRowIndex = grid.getIndexOfRowsCointainingAllCells(GridCell.from("Name", "Test01"));

        assertTrue(
                GridCell.getContent(grid.getCells().get(targetAttributeRowIndex.get(0)), "Type").contains(type));

        expandRowAdministrationGrid("Test01");
        sleep(context);

        clickDisableButton();
        clickDeleteButton();

        List<String> kpiListAttributes = given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
                .get(buildRestV3Url("classes/KPI/attributes")).then().statusCode(200).extract().jsonPath()
                .getList(("data.findAll{ it._id == 'Test01'}._id"));

        if (kpiListAttributes.size() != 0) {
            given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
                    .delete(buildRestV3Url("classes/KPI/attributes/" + kpiListAttributes.get(0))).then()
                    .statusCode(200);
        }

        testEnd(context);
    }

    @Test
    public void createDeleteNewReference() {

        UITestContext context = getDefaultTestContextInstance();
        String type = "REFERENCE";
        String classToTest = "Building";

        testStart(context);
        goFullScreen();
        login(Login.admin());
        sleep(context);
        ExtjsUtils.switchToAdminModule2(getDriver(), context);
        sleep(context);

        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", classToTest);

        selectAdministrationClassTab("Attributes");

        WebElement attributesClassGrid = ExtjsUtils.findElementByTestId(getDriver(),
                "administration-components-attributes-grid");
        attributesClassGrid.findElement(By.tagName("a")).click();
        sleep(context);

        WebElement detailsWindow = waitForElementVisibility(testIdLocator("cards-card-administration-detailsWindow"));
        Optional<WebElement> saveButton = detailsWindow.findElements(By.tagName("a")).stream()
                .filter(a -> "Save".equals(normalized(a.getText()))).findFirst();
        assertTrue(saveButton.get().getAttribute("class").contains("x-btn-disabled"));

        WebElement grid1 = getDriver().switchTo().activeElement()
                .findElement(By.xpath("//fieldset[@aria-label='General properties field set']"));

        grid1.findElement(By.name("name")).sendKeys("Test01");

        ExtjsUtils.findElementByXPath(getDriver(),
                "//fieldset[@aria-label='Type properties field set']//div[contains(@class, 'x-form-arrow-trigger')]")
                .click();
        ExtjsUtils.findElementByXPath(getDriver(), "//div//ul//li[text()='" + type + "']").click();
        sleep(context);

        WebElement domainInput = ExtjsUtils.waitForElementPresence(getDriver(), By.name("domain"));
        WebElement div = ExtjsUtils.getParent(ExtjsUtils.getParent(domainInput));
        div.findElement(By.className("x-form-arrow-trigger-default")).click();
        ExtjsUtils.waitForElementPresence(getDriver(), By.xpath("//div//ul//li[text()='BuildingFloor']")).click();
        sleep(context);

        clickSaveButton();
        sleep(context);

        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "Floor");
        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", classToTest);
        sleep(context);

        ExtjsGenericGrid grid = ExtjsGenericGrid
                .from(getDriver(), testIdLocator("administration-components-attributes-grid"),
                        By.className("x-grid-header-ct"), By.className("x-grid-body"))
                .extract();
        List<Integer> targetAttributeRowIndex = grid.getIndexOfRowsCointainingAllCells(GridCell.from("Name", "Test01"));

        assertTrue(
                GridCell.getContent(grid.getCells().get(targetAttributeRowIndex.get(0)), "Type").contains(type));

        expandRowAdministrationGrid("Test01");
        sleep(context);

        clickDisableButton();
        clickDeleteButton();

        List<String> listAttributes = given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
                .get(buildRestV3Url("classes/" + classToTest + "/attributes")).then().statusCode(200).extract().jsonPath()
                .getList(("data.findAll{ it._id == 'Test01'}._id"));

        if (listAttributes.size() != 0) {
            given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
                    .delete(buildRestV3Url("classes/" + classToTest + "/attributes/" + listAttributes.get(0))).then()
                    .statusCode(200);
        }

        testEnd(context);
    }

    @Test
    public void createDeleteNewReferenceSuperClassAttribute() {

        UITestContext context = getDefaultTestContextInstance();
        String type = "REFERENCE";
        String superClassToTest = "Form";
        String classToTest = "Asset Management form";

        testStart(context);
        goFullScreen();
        login(Login.admin());
        sleep(context);
        ExtjsUtils.switchToAdminModule2(getDriver(), context);
        sleep(context);

        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", superClassToTest, classToTest);

        selectAdministrationClassTab("Attributes");

        WebElement attributesClassGrid = ExtjsUtils.findElementByTestId(getDriver(),
                "administration-components-attributes-grid");
        attributesClassGrid.findElement(By.tagName("a")).click();
        sleep(context);

        WebElement detailsWindow = waitForElementVisibility(testIdLocator("cards-card-administration-detailsWindow"));
        Optional<WebElement> saveButton = detailsWindow.findElements(By.tagName("a")).stream()
                .filter(a -> "Save".equals(normalized(a.getText()))).findFirst();
        assertTrue(saveButton.get().getAttribute("class").contains("x-btn-disabled"));

        WebElement grid1 = getDriver().switchTo().activeElement()
                .findElement(By.xpath("//fieldset[@aria-label='General properties field set']"));

        grid1.findElement(By.name("name")).sendKeys("Test01");

        ExtjsUtils.findElementByXPath(getDriver(),
                "//fieldset[@aria-label='Type properties field set']//div[contains(@class, 'x-form-arrow-trigger')]")
                .click();
        ExtjsUtils.findElementByXPath(getDriver(), "//div//ul//li[text()='" + type + "']").click();
        sleep(context);

        WebElement domainInput = ExtjsUtils.waitForElementPresence(getDriver(), By.name("domain"));
        WebElement div = ExtjsUtils.getParent(ExtjsUtils.getParent(domainInput));
        div.findElement(By.className("x-form-arrow-trigger-default")).click();
        ExtjsUtils.waitForElementPresence(getDriver(), By.xpath("//div//ul//li[text()='FormFormField']")).click();
        sleep(context);

        clickSaveButton();
        sleep(context);

        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "Floor");
        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", superClassToTest, classToTest);
        sleep(context);

        ExtjsGenericGrid grid = ExtjsGenericGrid
                .from(getDriver(), testIdLocator("administration-components-attributes-grid"),
                        By.className("x-grid-header-ct"), By.className("x-grid-body"))
                .extract();
        List<Integer> targetAttributeRowIndex = grid.getIndexOfRowsCointainingAllCells(GridCell.from("Name", "Test01"));

        assertTrue(
                GridCell.getContent(grid.getCells().get(targetAttributeRowIndex.get(0)), "Type").contains(type));

        expandRowAdministrationGrid("Test01");
        sleep(context);

        clickDisableButton();
        clickDeleteButton();

        List<String> listAttributes = given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
                .get(buildRestV3Url("classes/AMForm/attributes")).then().statusCode(200).extract().jsonPath()
                .getList(("data.findAll{ it._id == 'Test01'}._id"));

        if (listAttributes.size() != 0) {
            given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
                    .delete(buildRestV3Url("classes/AMForm/attributes/" + listAttributes.get(0))).then()
                    .statusCode(200);
        }

        testEnd(context);
    }

    @Test
    public void createDeleteNewString() {

        UITestContext context = getDefaultTestContextInstance();
        String type = "STRING";

        testStart(context);
        goFullScreen();
        login(Login.admin());
        sleep(context);
        ExtjsUtils.switchToAdminModule2(getDriver(), context);
        sleep(context);

        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "KPI");

        selectAdministrationClassTab("Attributes");

        WebElement attributesClassGrid = ExtjsUtils.findElementByTestId(getDriver(),
                "administration-components-attributes-grid");
        attributesClassGrid.findElement(By.tagName("a")).click();
        sleep(context);

        WebElement detailsWindow = waitForElementVisibility(testIdLocator("cards-card-administration-detailsWindow"));
        Optional<WebElement> saveButton = detailsWindow.findElements(By.tagName("a")).stream()
                .filter(a -> "Save".equals(normalized(a.getText()))).findFirst();
        assertTrue(saveButton.get().getAttribute("class").contains("x-btn-disabled"));

        WebElement grid1 = getDriver().switchTo().activeElement()
                .findElement(By.xpath("//fieldset[@aria-label='General properties field set']"));

        grid1.findElement(By.name("name")).sendKeys("Test01");

        ExtjsUtils.findElementByXPath(getDriver(),
                "//fieldset[@aria-label='Type properties field set']//div[contains(@class, 'x-form-arrow-trigger')]")
                .click();
        ExtjsUtils.findElementByXPath(getDriver(), "//div//ul//li[text()='" + type + "']").click();
        sleep(context);

        WebElement precision = ExtjsUtils.waitForElementPresence(getDriver(), By.name("maxLength"));
        precision.clear();
        precision.sendKeys("42");

        clickSaveButton();
        sleep(context);

        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "Floor");
        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "KPI");
        sleep(context);

        ExtjsGenericGrid grid = ExtjsGenericGrid
                .from(getDriver(), testIdLocator("administration-components-attributes-grid"),
                        By.className("x-grid-header-ct"), By.className("x-grid-body"))
                .extract();
        List<Integer> targetAttributeRowIndex = grid.getIndexOfRowsCointainingAllCells(GridCell.from("Name", "Test01"));

        assertTrue(
                GridCell.getContent(grid.getCells().get(targetAttributeRowIndex.get(0)), "Type").contains(type));

        expandRowAdministrationGrid("Test01");
        sleep(context);

        clickDisableButton();
        clickDeleteButton();

        List<String> kpiListAttributes = given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
                .get(buildRestV3Url("classes/KPI/attributes")).then().statusCode(200).extract().jsonPath()
                .getList(("data.findAll{ it._id == 'Test01'}._id"));

        if (kpiListAttributes.size() != 0) {
            given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
                    .delete(buildRestV3Url("classes/KPI/attributes/" + kpiListAttributes.get(0))).then()
                    .statusCode(200);
        }

        testEnd(context);
    }

    @Test
    public void createDeleteNewText() {

        UITestContext context = getDefaultTestContextInstance();
        String type = "TEXT";

        testStart(context);
        goFullScreen();
        login(Login.admin());
        sleep(context);
        ExtjsUtils.switchToAdminModule2(getDriver(), context);
        sleep(context);

        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "KPI");

        selectAdministrationClassTab("Attributes");

        WebElement attributesClassGrid = ExtjsUtils.findElementByTestId(getDriver(),
                "administration-components-attributes-grid");
        attributesClassGrid.findElement(By.tagName("a")).click();
        sleep(context);

        WebElement detailsWindow = waitForElementVisibility(testIdLocator("cards-card-administration-detailsWindow"));
        Optional<WebElement> saveButton = detailsWindow.findElements(By.tagName("a")).stream()
                .filter(a -> "Save".equals(normalized(a.getText()))).findFirst();
        assertTrue(saveButton.get().getAttribute("class").contains("x-btn-disabled"));

        WebElement grid1 = getDriver().switchTo().activeElement()
                .findElement(By.xpath("//fieldset[@aria-label='General properties field set']"));

        grid1.findElement(By.name("name")).sendKeys("Test01");

        ExtjsUtils.findElementByXPath(getDriver(),
                "//fieldset[@aria-label='Type properties field set']//div[contains(@class, 'x-form-arrow-trigger')]")
                .click();
        ExtjsUtils.findElementByXPath(getDriver(), "//div//ul//li[text()='" + type + "']").click();
        sleep(context);

        WebElement precision = ExtjsUtils.waitForElementPresence(getDriver(), By.name("editorType"));
        precision.sendKeys("Plain text");

        clickSaveButton();
        sleep(context);

        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "Floor");
        ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", "KPI");
        sleep(context);

        ExtjsGenericGrid grid = ExtjsGenericGrid
                .from(getDriver(), testIdLocator("administration-components-attributes-grid"),
                        By.className("x-grid-header-ct"), By.className("x-grid-body"))
                .extract();
        List<Integer> targetAttributeRowIndex = grid.getIndexOfRowsCointainingAllCells(GridCell.from("Name", "Test01"));

        assertTrue(
                GridCell.getContent(grid.getCells().get(targetAttributeRowIndex.get(0)), "Type").contains(type));

        expandRowAdministrationGrid("Test01");
        sleep(context);

        ExtjsUtils.findElementByTestId(getDriver(), "administration-attributes-tool-editbtn").click();
        sleep(context);

        precision = ExtjsUtils.waitForElementPresence(getDriver(), By.name("editorType"));
        precision.clear();
        precision.sendKeys("Editor HTML");

        clickSaveButton();
        sleep(context);

        clickDisableButton();
        clickDeleteButton();

        List<String> kpiListAttributes = given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
                .get(buildRestV3Url("classes/KPI/attributes")).then().statusCode(200).extract().jsonPath()
                .getList(("data.findAll{ it._id == 'Test01'}._id"));

        if (kpiListAttributes.size() != 0) {
            given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
                    .delete(buildRestV3Url("classes/KPI/attributes/" + kpiListAttributes.get(0))).then()
                    .statusCode(200);
        }

        testEnd(context);
    }

    @PromoteToUITestUtility
    protected boolean selectAdministrationClassTab(String tabCaption) {
        waitForElementVisibility(UILocators.cmdbuildAdministrationClassTab());
        WebElement root = waitForElementPresence(UILocators.cmdbuildAdministrationContent());
        List<WebElement> tabs = root.findElements(UILocators.cmdbuildAdministrationClassTab());
        Optional<WebElement> tab = tabs.stream()
                .filter(t -> tabCaption.equals(t.findElement(By.className("x-tab-inner")).getText())).findFirst();
        if (tab.isPresent()) {
            safeClick(getDriver(), tab.get());
            waitForLoad();
            waitForElementVisibility(By.xpath("//div[@data-testid='administration-content-classes-tabitems-attributes-attributes']"));
            // TODO some assertions here
            return true;
        } else {
            return false;
        }
    }

    protected void clickDeleteButton() {
        WebElement delete = ExtjsUtils.findElementByTestId(getDriver(), "administration-attributes-tool-deletebtn");
        safeClick(delete);
        waitForLoad();
    }

    protected void clickDisableButton() {
        WebElement disable = ExtjsUtils.findElementByTestId(getDriver(), "administration-attributes-tool-disablebtn");
        safeClick(disable);
        waitForLoad();
    }

    protected void clickEditButton() {
        WebElement edit = ExtjsUtils.findElementByTestId(getDriver(), "administration-attributes-tool-editbtn");
        safeClick(edit);
        waitForLoad();
    }

    protected void clickSaveButton() {
        WebElement saveButton = ExtjsUtils.findElementByXPath(getDriver(),
                "//div[@data-testid=\"cards-card-administration-detailsWindow\"]//span[text()=\"Save\"]");
        safeClick(saveButton);
        waitForLoad();
    }

    protected void clickUniqueCheckbox() {
        WebElement uniqueInput = ExtjsUtils.findElementByXPath(getDriver(),
                "//div[@data-testid=\"cards-card-administration-detailsWindow\"]//input[@name='unique']");
        safeClick(uniqueInput);
        waitForLoad();
    }

    protected void clickActiveCheckbox() {
        WebElement activeInput = ExtjsUtils.findElementByXPath(getDriver(),
                "//div[@data-testid=\"cards-card-administration-detailsWindow\"]//input[@name='active']");
        safeClick(activeInput);
        waitForLoad();
    }

    protected void fillCard(WebElement combo, String name, String text) {

    }

    protected void expandRowAdministrationGrid(String name) {
        ExtjsGenericGrid grid = ExtjsGenericGrid
                .from(getDriver(), testIdLocator("administration-components-attributes-grid"),
                        By.className("x-grid-header-ct"), By.className("x-grid-item-container"))
                .extract();
        List<Integer> targetAttributeRowIndex = grid.getIndexOfRowsCointainingAllCells(GridCell.from("Name", name));

        WebElement expander = ExtjsUtils.findElementByXPath(getDriver(),
                "//div[@data-testid='administration-components-attributes-grid']//table[@data-recordindex='"
                + targetAttributeRowIndex.get(0) + "']//div[contains(@class, 'x-grid-row-expander')]");

        // Se l'elemento non è visibile nella viewPort, scrollo la pagina
        if (!ExtjsUtils.isVisibleInViewport(expander)) {
            ((JavascriptExecutor) getDriver()).executeScript("arguments[0].scrollIntoView(true);", expander);
        }

        sleep(200);

        safeClick(expander);

        waitForLoad();
    }

}
