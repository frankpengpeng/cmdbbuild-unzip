package org.cmdbuild.test.web;

import static org.cmdbuild.test.web.utils.ExtjsUtils.*;
import static org.cmdbuild.test.web.utils.UILocators.*;
import static org.junit.Assert.*;
import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.hasKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.RenderingHints.Key;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import com.google.common.base.Splitter;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.cmdbuild.test.web.utils.*;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import io.restassured.http.ContentType;
import junit.framework.Assert;

import org.springframework.jdbc.core.JdbcTemplate;

public class ManagementClassListIT extends BaseWebIT {

	//TEST PARAMETERS
	UITestRule defaultCLientLogRule = UITestRule.defineClientLogCheckRule(null, ImmutableList.of("404","401"), null, null);
	//TODO: since some tests (e.g.: saveButtonDisabledWhenLeavingARequiredFieldBlankTest andsaveButtonDisabledWhenLeavingARequiredFieldBlankTestOnNewCard)
	// are known to generate http 400 / 500 errors, following relaxed rule is temporary adopted:
	UITestRule relaxedClientLogRule = UITestRule.defineClientLogCheckRule(null, ImmutableList.of("404","401","400","500"), null, null);

	//TEST rules -related //TODO move up
	long testSBDMaxDurationMillis = 35 * SECONDS; //Raised for Remote Driver performance compatibility
	long testSBDNewCardMaxDurationMillis = 90 * SECONDS;
	long defaultTestDurationMillis = 30 * SECONDS; //TODO move out, to be used when UI performance is not of concern or test is quick



	//Commons
	String cardEditSaveButtonCaption = UITestDefaults.cardEditSaveButtonCaptionDefault;
	String cardNewSaveButtonCaption = UITestDefaults.cardNewSaveButtonCaptionDefault;

	//By messageBoxLocator = By.xpath("//div[@class='x-message-box']");


	//TEST variables
	// saveButtonDisabledWhenLeavingARequiredFieldBlankTest
	String testSBDMenuClassMain = "Workplaces";
	String testSBDMenuClassSub = "All computers";
	String testSBDCardFieldToModify = "Code";
	// saveButtonDisabledWhenLeavingARequiredFieldBlankTest
	String testSBDNewCardMenuClassMain = "Locations";
	String testSBDNewCardMenuClassSub = "Buildings";
	String testSBDNewCardCardRequiredFieldToLeaveBlank = "Description";
	String testSBDNewCardCardField1ToFill = "City";
	String testSBDNewCardCardField1ToFillContent = "Udine";
	String testSBDNewCardCardField2ToFill = "Code";
	String testSBDNewCardCardField2ToFillContent = "TEST";
	//abortCardCreationTest
	String testAbortCCMenuClassMain = "Workplaces";
	String testAbortCCMenuClassSub = "Desktops";
//	String testAbortCCMenuClassSub = "All computers";
	//createCardTest
	//Integer testAbortCCMenu = "All computers";

	String testCreateCardTouchedClass = "VLAN";
	String testCreateCardMenuClassMain = "Networks";
	String testCreateCardMenuClassSub = "VLANs";
	String testCreateCardMenuClassOtherMain = "Workplaces";
	String testCreateCardMenuClassOtherSub = "All computers";
	Integer testCreateCardRandomSuffix = (int)Math.round(Math.random() * 999999);
	//referenceDropDownTest
	String testReferenceDropDownCommonMenuClassMain = "Locations";
	String testReferenceDropDownReferencedClass1MenuSub = "Buildings";
	String testReferenceDropDownReferencedClass2MenuSub = "Floors";
	String testReferenceDropDownReferencerClassMenuSub = "Rooms";
	String testReferenceDropDownCodeFieldName = "Code";
	String testReferenceDropDownReference1FieldName = "Building";
	String testReferenceDropDownReference2FieldName = "Floor";
	String testReferenceDropDownReferenceCQLSimulatedFilterFieldName = "Building";

	//addCardOffersSubClassSelection
	String testAddCardOffersSubClassSelectionMainClass = "Workplaces";
	String testAddCardOffersSubClassSelectionSubClass = "All computers";
	List<String> testAddCardOffersSubClassSelectionExpectedLeafClasses = ImmutableList.of("Desktop", "Mobile" , "Notebook"); //dropdown must contains All items in this list,but others can be present
	//deleteCardWithoutRelations
	String testDeleteCardWORelationsMenuLevel1 = "All items";
	String testDeleteCardWORelationsMenuLevel2 = "Classes";
	String testDeleteCardWORelationsMenuLevel3 = "Standard";
	String testDeleteCardWORelationsTargetClass = "Parameter";
	String testDeleteCardWORelationsExpectedTitle = "Cards Parameter";
	String testDeleteCardWORelationsExpectedURLFragment = "Parameter";
	String testDeleteCardWORelationsChangeClassMenuLevel1 = "Locations";
	String testDeleteCardWORelationsChangeClassMenuLevel2 = "Floors";
	//referenceIsProperlyDisplayedInCards
	String  testReferenceProperlyDisplayedCommonMenuClassMain = "Locations";
	String  testReferenceProperlyDisplayedReferencedClass1MenuSub = "Buildings";
	String  testReferenceProperlyDisplayedReferencedClass2MenuSub = "Floors";
	String  testReferenceProperlyDisplayedReferencerClassMenuSub = "Rooms";
	String  testReferenceProperlyDisplayedDescriptionFieldName = "Description";
	String  testReferenceProperlyDisplayedReference1FieldName = "Building";
	String  testReferenceProperlyDisplayedReference2FieldName = "Floor";
	//delete card with N:N relation
	List<String> testDeleteCardWNNRelationTouchedClasses = ImmutableList.of("Monitor","Map_SupplierCI");
//	String testDeleteCardWNNRelationMenuLevel1 = "All items";
//	String testDeleteCardWNNRelationMenuLevel2 = "Classes";
//	String testDeleteCardWNNRelationMenuLevel3 = "Monitor";
	String testDeleteCardWNNRelationExpectedTitle = "Cards Employee";
	String testDeleteCardWNNRelationExpectedURLFragment = "Employee";
	String[]  testDeleteCardWNNRelationMenuTreeTargetClass = new String[] {"Employees" , "All employees" };
	GridCell testDeleteCardWNNRelation_CardToDeleteIdentifier = new GridCell("Code" , "m.brooke");
	String testDeleteCardWNNRelationFieldNameOfLinkedCardDescription = "Organizational unit";
	String[] testDeleteCardWNNRelationMenuTreeLinkedClass = new String[] {"Suppliers" , "Suppliers"};
	String testDeleteCardWNNRelation_sqlChekCardIsDeleted = "select count(*) from \"Monitor\" where \"Code\" = '849123-45' and \"Status\" = 'N';"; //could be made dependent on identifier
	String testDeleteCardWNNRelation_sqlChekRelationIsDeleted = "select count(*) from \"Map_SupplierCI\" a join \"Monitor\" m on m.\"Id\" = a.\"IdObj2\" and m.\"Code\" = '849123-45' and a.\"Status\" ='N'";

	//delete card with N:1 relation
	String testDeleteCardWN1Relation_NewCardCode = "RoomTest-" + RandomStringUtils.randomAlphabetic(5);
	List<String> testDeleteCardWN1Relation_TouchedClasses = ImmutableList.of("Room", "Map_FloorRoom");
	String testDeleteCardWN1Relation_TargetClass = "Floor";
	String[] testDeleteCardWN1Relation_MenuTreeTargetClass = new String[] {"Locations" , "Rooms"};
	String[] testDeleteCardWN1RelationMenuTreeLinkedClass = new String[] {"Locations" , "Floors"};
	//FIXME make formfield and card to delete identifier dependent upon each other
//	Cell testDeleteCardWN1Relation_CardToDeleteIdentifier = new Cell ("Description", "AC Aon Center - 02 RoomTest2");
	GridCell testDeleteCardWN1Relation_CardToDeleteIdentifier = new GridCell("Code", testDeleteCardWN1Relation_NewCardCode);
	GridCell testDeleteCardWN1Relation_CardToDeleteLinkedCardIdentifier = new GridCell("Code", "F02");
    List<FormField> testDeleteCardWN1Relation_NewCardFormFields;
    {
        testDeleteCardWN1Relation_NewCardFormFields = FormField.listOf(
                ImmutableList.of("Building", "Floor", "Code", "Use" )
                , ImmutableList.of("Aon Center" , "AC Aon Center - 02", testDeleteCardWN1Relation_NewCardCode, "Office"));
    }

    String testDeleteCardWN1Relation_sqlChekCardIsDeleted = "select \"Status\" from \"Room\" where \"Code\" = '" + testDeleteCardWN1Relation_NewCardCode +"' and \"Status\" <> 'U';"; //could be made dependent on identifier
	String testDeleteCardWN1Relation_sqlChekRelationIsDeleted = "select a.\"Status\" from \"Map_FloorRoom\" a join \"Room\" r on r.\"Id\" = a.\"IdObj2\" and r.\"Code\" = '"+testDeleteCardWN1Relation_NewCardCode+"' and a.\"Status\" <>'U'";
	//

	//deleteCardW1NRelationFailsIfNSideNotEmpty
	List<String> deleteCardW1NRelationFailsIfNSideNotEmpty_TouchedClasses = ImmutableList.of("Floor", "Map_BuildingFloor");
	String deleteCardW1NRelationFailsIfNSideNotEmpty_TargetClass = "Floor";
	String[] deleteCardW1NRelationFailsIfNSideNotEmpty_MenuTreeTargetClass = new String[] {"Locations" , "Floors"};
	String[] deleteCardW1NRelationFailsIfNSideNotEmpty_MenuTreeOtherClass = new String[] {"Locations" , "Buildings"};
	GridCell deleteCardW1NRelationFailsIfNSideNotEmpty_CardToDeleteIdentifier = new GridCell("Code", "F02");

	String deleteCardW1NRelationFailsIfNSideNotEmpty_sqlChekCardIsNotDeleted = "select count(*) from \"Floor\" where \"Code\" = 'F02' and \"Status\" = 'A';"; //could be made dependent on identifier
	String deleteCardW1NRelationFailsIfNSideNotEmpty_sqlChekRelationIsNotDeleted = "select count(*) from \"Map_BuildingFloor\" a join \"Floor\" f on f.\"Id\" = a.\"IdObj2\" and f.\"Code\" = 'F02' and a.\"Status\" ='A'";
	//TODO move (createCardTest variables)
	List<FormField> testCreateCardFormFields;
	{
		testCreateCardFormFields = FormField.listOf(
				ImmutableList.of("Code", "Name", "Subnet" , "Numdber" ) //intentionally left Netmask empty
				, ImmutableList.of("TVLAN-" +testCreateCardRandomSuffix, "Test VLAN" , "10.0.0.0" , "" + testCreateCardRandomSuffix ));
	}

	//cannotDeleteCardThatIsReferenceDestinationTest
	List<String> cannotDeleteReferenceDest_TouchedClasses = ImmutableList.of("Building",  "Map_BuildingFloor");
	String[] cannotDeleteReferenceDest_MenuTreeTargetClass = {"Locations", "Buildings"};
	String[] cannotDeleteReferenceDest_MenuTreeReferencingClass = {"Locations", "Floors"};
	String cannotDeleteReferenceDest_TargetClass = "Building";
	//String cannotDeleteReferenceDest_ReferencingClass = "Floor";
	GridCell[] cannotDeleteReferenceDest_CardToDeleteIdentifier = {new GridCell("Code", "AC") ,
			new GridCell("Description", "Aon Center")};
	GridCell cannotDeleteReferenceDest_FilterCardsWithReferencesToDeletedCard = new GridCell("Building", "Aon Center");
	String cannotDeleteReferenceDest__sqlChekCardIsNotDeleted = "select count(*) from \"" + cannotDeleteReferenceDest_TargetClass
			+"\" where \"" + cannotDeleteReferenceDest_CardToDeleteIdentifier[0].getName() +"\" = '" + cannotDeleteReferenceDest_CardToDeleteIdentifier[0].getContent()+"' and \"Status\" = 'A';"; //could be made dependent on identifier
	String cannotDeleteReferenceDest_sqlChekRelationIsNotDeleted = "select count(*) from \"Map_BuildingFloor\" a join \"" + cannotDeleteReferenceDest_TargetClass
			+"\" d on d.\"Id\" = a.\"IdObj1\" and d.\""+ cannotDeleteReferenceDest_CardToDeleteIdentifier[0].getName()
			+"\" = '" + cannotDeleteReferenceDest_CardToDeleteIdentifier[0].getContent()+"' and a.\"Status\" ='A'";

	//
	Random random;


	@After
	public void cleanup() {
		//cleanupTouchedClasses();
		cleanupDB();
	}


	@Test 
	public void cannotDeleteCardThatIsReferenceDestinationTest() {

		UITestContext context = getDefaultTestContextInstance();
		context.withRule(UITestRule.defineClientLogCheckRule(null, ImmutableList.of("401", "500", "404"), null, null));
		testStart(context);
		
		goFullScreen();

		login(Login.admin());

		//count instances of cards referencing card to be deleted
		ExtjsUtils.safelylickOnSideNavLeaf(getDriver(),cannotDeleteReferenceDest_MenuTreeReferencingClass  );
		ExtjsCardGrid gridReferencingBeforeDeletionAttempt = ExtjsCardGrid.extract(getDriver());
		int cardsReferencingBeforeDeletionAttempt = gridReferencingBeforeDeletionAttempt.getRowsContainingAllCells(cannotDeleteReferenceDest_FilterCardsWithReferencesToDeletedCard).size();
		assertNotEquals("No referencing cards. Test prerequisite unsatisfied", 0, cardsReferencingBeforeDeletionAttempt);

		JdbcTemplate jdbc = getJdbcTemplate();
		int activeCardsBefore = jdbc.queryForObject(cannotDeleteReferenceDest__sqlChekCardIsNotDeleted, Integer.class);
		int activeRelationsBefore = jdbc.queryForObject(cannotDeleteReferenceDest_sqlChekRelationIsNotDeleted, Integer.class);
		assertEquals("There should be exactly one card matching deletion criteria. Test prerequisite",1, activeCardsBefore);
		assertNotEquals("There should be any active relation related tocard to delete. Test prerequisite ", 0, activeRelationsBefore );

		//deletion attempt
		ExtjsUtils.safelylickOnSideNavLeaf(getDriver(),cannotDeleteReferenceDest_MenuTreeTargetClass  );
		ExtjsCardGrid gridDeletionAttempt = ExtjsCardGrid.extract(getDriver());
		List<Integer> cellsMatching = gridDeletionAttempt.getIndexOfRowsCointainingAllCells(cannotDeleteReferenceDest_CardToDeleteIdentifier);
		assertEquals("Only one card must match deletion criteria", 1, cellsMatching.size());
		int numOfMatchingCardsBeforeDeletionAttempt = cellsMatching.size();
		WebElement rowToDelete = gridDeletionAttempt.expandGridRow(cellsMatching.get(0));
		gridDeletionAttempt.deleteCard(rowToDelete);
		
		//check error message is thrown and other amenities
		WebElement msgBoxHeader = ExtjsUtils.waitForElementVisibility(getDriver(), locatorMsgBoxTitle());

		assertTrue("Error".equalsIgnoreCase(StringUtils.trimToEmpty(msgBoxHeader.getText())));

		sleep(context);
		//check all instances of card referencing are in place
		ExtjsUtils.safelylickOnSideNavLeaf(getDriver(),cannotDeleteReferenceDest_MenuTreeReferencingClass  );
		ExtjsCardGrid gridReferencingAfterDeletionAttempt = ExtjsCardGrid.extract(getDriver());
		int cardsReferencingAfterDeletionAttempt = gridReferencingAfterDeletionAttempt.getRowsContainingAllCells(cannotDeleteReferenceDest_FilterCardsWithReferencesToDeletedCard).size();
		assertEquals("No referencing card must be deleted", cardsReferencingBeforeDeletionAttempt, cardsReferencingAfterDeletionAttempt);

		//check card to delete was not deleted
		ExtjsUtils.safelylickOnSideNavLeaf(getDriver(),cannotDeleteReferenceDest_MenuTreeTargetClass  );
		ExtjsCardGrid gridAfterDeletionAttempt = ExtjsCardGrid.extract(getDriver());
		List<Integer> cellsMatchingAfter = gridAfterDeletionAttempt.getIndexOfRowsCointainingAllCells(cannotDeleteReferenceDest_CardToDeleteIdentifier);
		assertEquals(numOfMatchingCardsBeforeDeletionAttempt , cellsMatching.size());

		//check also db
		int activeCardsAfter = jdbc.queryForObject(cannotDeleteReferenceDest__sqlChekCardIsNotDeleted, Integer.class);
		int activeRelationsAfter = jdbc.queryForObject(cannotDeleteReferenceDest_sqlChekRelationIsNotDeleted, Integer.class);
		assertEquals("Deletion candidate should not have been deleted",activeCardsBefore, activeCardsAfter);
		assertEquals("No relation should have been deleted", activeRelationsBefore, activeRelationsAfter);
		
		testEnd(context);
	}


	@Test
	public void saveButtonDisabledWhenLeavingARequiredFieldBlankTest() {

		UITestContext testContext = getDefaultTestContextInstance();
		
		testContext.withRule(UITestRule.defineClientLogCheckRule(null, ImmutableList.of("404","401","400"), null, null));
		testStart(testContext);
		
		goFullScreen();

		login(Login.admin());

		ExtjsUtils.safelylickOnSideNavLeaf(getDriver(), testSBDMenuClassMain , testSBDMenuClassSub);

		ExtjsCardGrid grid = ExtjsCardGrid.extract(getDriver(), testContext);
		WebElement gridRow = grid.expandGridRow(grid.rows() -1);
		sleep(testContext);

		grid.editCard();
		sleep(testContext);

		WebElement detailsForm = getManagementDetailsWindow(getDriver());
		String fieldContent = getFormTextFieldContent(detailsForm, testSBDCardFieldToModify);
		logger.info("Field {} contains: {}" ,testSBDCardFieldToModify ,fieldContent);

		assertFalse(Strings.isNullOrEmpty(fieldContent));
		assertFalse(isDetailFormButtonDisabled(detailsForm, cardEditSaveButtonCaption));

		clearFormTextField(getDriver(), detailsForm, testSBDCardFieldToModify);
		assertTrue(Strings.isNullOrEmpty( getFormTextFieldContent(detailsForm, testSBDCardFieldToModify)));
		assertTrue(isDetailFormButtonDisabled(detailsForm, cardEditSaveButtonCaption));

		sleep(testContext);
		testEnd(testContext);
	}

	@Test 
	public void saveButtonDisabledWhenLeavingARequiredFieldBlankTestOnNewCard() {

		UITestContext testContext = getDefaultTestContextInstance();
		testContext.withRule(UITestRule.define("Duration", "Test execution took longer than allowed to complete"
				, c -> c.getMeasuredTimeSpent() < testSBDNewCardMaxDurationMillis));
		testContext.withRule(relaxedClientLogRule);
		testStart(testContext);
		goFullScreen();

		login(Login.admin());

		ExtjsUtils.safelylickOnSideNavLeaf(getDriver(), testSBDNewCardMenuClassMain , testSBDNewCardMenuClassSub);

		ExtjsCardGrid grid = ExtjsCardGrid.extract(getDriver(), testContext); WebElement gridRow = grid.expandGridRow(grid.rows() -1);
		waitForLoad();

		WebElement detailsForm =  grid.newCard();
		//TODO maybe we need to wait for field edit / validation to complete before assertion checks...
		//Add only if test starts to fail
		assertTrue(isDetailFormButtonDisabled(detailsForm, cardNewSaveButtonCaption));
		fillFormTextField(detailsForm, testSBDNewCardCardField1ToFill, testSBDNewCardCardField1ToFillContent);
		assertTrue(isDetailFormButtonDisabled(detailsForm, cardNewSaveButtonCaption));
		fillFormTextField(detailsForm, testSBDNewCardCardField2ToFill, testSBDNewCardCardField2ToFillContent);
		assertTrue(isDetailFormButtonDisabled(detailsForm, cardNewSaveButtonCaption));
		fillFormTextField(detailsForm, testSBDNewCardCardRequiredFieldToLeaveBlank, "This is a test");
		assertFalse(isDetailFormButtonDisabled(detailsForm, cardNewSaveButtonCaption));
		clearFormTextField(getDriver(), detailsForm, testSBDNewCardCardRequiredFieldToLeaveBlank);
		assertTrue(isDetailFormButtonDisabled(detailsForm, cardNewSaveButtonCaption));

		testEnd(testContext);

	}



	@Test 
	public void abortCardCreationTest() {

		UITestContext testContext = getDefaultTestContextInstance();
		testContext.withRule(UITestRule.define("Duration", "Test execution took longer than allowed to complete"
				, c -> c.getMeasuredTimeSpent() < testSBDNewCardMaxDurationMillis));
		testContext.withRule(relaxedClientLogRule);
		testStart(testContext);
		goFullScreen();

		login(Login.admin());

		ExtjsUtils.safelylickOnSideNavLeaf(getDriver(), testAbortCCMenuClassMain , testAbortCCMenuClassSub);

		//TODO identify
		ExtjsCardGrid grid = ExtjsCardGrid.extract(getDriver(), testContext); WebElement gridRow = grid.expandGridRow(0);
		waitForLoad();

		artificialSleep(2500, testContext);
		assertTrue(grid.isGridRowExpanded(0));

		//case 1: quit edit modal pressing esc
		WebElement detailsForm =  grid.newCard();
		detailsForm.sendKeys(Keys.ESCAPE);
		artificialSleep(500, testContext);
		assertTrue(grid.isGridRowExpanded(0));

		//case 2: quit edit modal clicking Cancel button
		grid = ExtjsCardGrid.extract(getDriver(), testContext);
		artificialSleep(2000, testContext);
		gridRow = grid.expandGridRow(1);
		waitForLoad();
		artificialSleep(1000, testContext);
		detailsForm =  grid.newCard();
		Optional<WebElement> cancelButton = getDetailFormButton(detailsForm, "Cancel");
		cancelButton.get().click();
		artificialSleep(500, testContext);
		grid = ExtjsCardGrid.extract(getDriver(), testContext); //not really needed
		assertTrue(grid.isGridRowExpanded(1));

		testEnd(testContext);
	}

	@Test
	public void createCardTest() {

		UITestContext testContext = new UITestContext().withWebDriver(getDriver());
		testContext.withRule(UITestRule.define("Duration", "Test execution took longer than allowed to complete"
				, c -> c.getMeasuredTimeSpent() < defaultTestDurationMillis * 2));
		testContext.withRule(relaxedClientLogRule);
		testContext.withTouchedClass(testCreateCardTouchedClass);

		testStart(testContext);
		goFullScreen();

		login(Login.admin());

		ExtjsUtils.safelylickOnSideNavLeaf(getDriver(), testCreateCardMenuClassMain , testCreateCardMenuClassSub);
		waitForLoad();

		ExtjsCardGrid grid = ExtjsCardGrid.extract(getDriver(), testContext);
		waitForLoad();
		int gridItemsBeforeNewCard = grid.rows();

		artificialSleep(500, testContext);
		WebElement detailsForm =  grid.newCard();
		
		WebElement nameField = ExtjsUtils.findElementByXPath(getDriver(), "//div[@data-testid='cards-card-detailsWindow']//input[@name='Name']");
		WebElement subnetField = ExtjsUtils.findElementByXPath(getDriver(), "//div[@data-testid='cards-card-detailsWindow']//input[@name='Subnet']");
		WebElement numberField = ExtjsUtils.findElementByXPath(getDriver(), "//div[@data-testid='cards-card-detailsWindow']//input[@name='Number']");
		
		nameField.sendKeys("VLAN test");
		subnetField.sendKeys("130.234.252.0");
		numberField.sendKeys("003");

		Optional<WebElement> saveButton = getDetailFormButton(detailsForm, cardNewSaveButtonCaption);
		saveButton.get().click();
		//close details window
		artificialSleep(3000, testContext);
		detailsForm = getManagementDetailsWindow(getDriver());
		detailsForm.sendKeys(Keys.ESCAPE);

		artificialSleep(2500, testContext);

		// go somewhere else and come back, assure the new card is present
		ExtjsUtils.safelylickOnSideNavLeaf(getDriver(), testCreateCardMenuClassOtherMain , testCreateCardMenuClassOtherSub);
		waitForLoad();
		grid = ExtjsCardGrid.extract(getDriver(), testContext); //make sure grid is refreshed
		ExtjsUtils.safelylickOnSideNavLeaf(getDriver(), testCreateCardMenuClassMain , testCreateCardMenuClassSub);
		waitForLoad();
		grid = ExtjsCardGrid.extract(getDriver(), testContext); //make sure grid is refreshed
		assertEquals(gridItemsBeforeNewCard +1, grid.rows());
		grid.hasRowContainingAllCells(GridCell.from("Name", "VLAN test"));
		waitForLoad();

		testEnd(testContext);
	}

	@Test
	public void referenceDropdownTest() {

		UITestContext context = getDefaultTestContextInstance();
		testStart(context);
		
		goFullScreen();

		login(Login.demo());
		sleep(context);

		ExtjsUtils.safelylickOnSideNavLeaf(getDriver(), testReferenceDropDownCommonMenuClassMain , testReferenceDropDownReferencedClass1MenuSub);
		sleep(context);

		ExtjsCardGrid grid1 = ExtjsCardGrid.extract(getDriver(), context);
		List<String> reference1Values = new ArrayList<>();
		grid1.getRows().forEach(cl -> reference1Values.add(ExtjsCardGrid.getContent(cl, testReferenceDropDownCodeFieldName)));
		logger.info("Relation 1 values: {}", reference1Values.toString());

		ExtjsUtils.safelylickOnSideNavLeaf(getDriver(), testReferenceDropDownCommonMenuClassMain, testReferenceDropDownReferencedClass2MenuSub);
		sleep(context);
		List<String> reference2Values = new ArrayList<>();
		ExtjsCardGrid grid2 = ExtjsCardGrid.extract(getDriver(), context);
		grid2.getRows().forEach(cl -> reference2Values.add(ExtjsCardGrid.getContent(cl, testReferenceDropDownCodeFieldName)));
		logger.info("Relation 2 values: {}", reference2Values.toString());

		ExtjsUtils.safelylickOnSideNavLeaf(getDriver(), testReferenceDropDownCommonMenuClassMain , testReferenceDropDownReferencerClassMenuSub);
		sleep(context);
		ExtjsCardGrid grid = ExtjsCardGrid.extract(getDriver(), context);
		sleep(context);
		WebElement gridRow = grid.expandGridRow(0);
		
		grid.editCard();
		WebElement detailsForm =  getManagementDetailsWindow(getDriver());
		
		sleep(context);

		List<String> optionsField1 = ExtjsUtils.getDetailFormDropDownFieldOptions(getDriver(), detailsForm, testReferenceDropDownReference1FieldName);
		List<String> optionsField2 = ExtjsUtils.getDetailFormDropDownFieldOptions(getDriver(), detailsForm, testReferenceDropDownReference2FieldName);
		
		ExtjsCardGrid.extract(getDriver());
		
		logger.info("Field {} dropdown options: {}" ,testReferenceDropDownReference1FieldName , optionsField1.toString());
		logger.info("Field {} dropdown options: {}" ,testReferenceDropDownReference2FieldName , optionsField2.toString());

		assertEquals(reference1Values.size(), optionsField1.size());
		
		String simulatedCQLFilter = grid.getRows().get(0).stream().filter(cell -> testReferenceDropDownReferenceCQLSimulatedFilterFieldName.equalsIgnoreCase(cell.getName())).findAny().get().getContent();
		List<String> filteredReference2Values = optionsField2.stream().filter(r -> r.contains(simulatedCQLFilter)).collect(Collectors.toList());

		assertEquals(filteredReference2Values.size(), optionsField2.size());
		
		testEnd(context);
	}



	@Test
	public void addCardOffersSubclassSelectionOptions() {

		UITestContext context = getDefaultTestContextInstance();
		context.withRule(relaxedClientLogRule);
		testStart(context);
		
		goFullScreen();

		login(Login.admin());

		ExtjsUtils.safelylickOnSideNavLeaf(getDriver(), testAddCardOffersSubClassSelectionMainClass , testAddCardOffersSubClassSelectionSubClass);
		sleep(context);

		WebElement addButton = ExtjsUtils.findElementByTestId(getDriver(), "classes-cards-grid-container-addbtn");
		addButton.click();
		
		assertTrue(ExtjsUtils.findElementByXPath(getDriver(), "//div[contains (@class, 'x-menu ') ]//span[text()='Desktop']").isDisplayed());
		assertTrue(ExtjsUtils.findElementByXPath(getDriver(), "//div[contains (@class, 'x-menu ') ]//span[text()='Mobile']").isDisplayed());
		assertTrue(ExtjsUtils.findElementByXPath(getDriver(), "//div[contains (@class, 'x-menu ') ]//span[text()='Notebook']").isDisplayed());
		
		sleep(context);
		testEnd(context);
	}

	@Test 
	public void deleteCardWithoutRelationsGridRefreshExpected() {

		UITestContext context = getDefaultTestContextInstance();
		context.withRule(relaxedClientLogRule);
		context.withTouchedClass(testDeleteCardWORelationsTargetClass);
		testStart(context);
		
		goFullScreen();

		login(Login.admin());

		ExtjsUtils.safelyClickOnSideNavLeafWithChecksAgainst(getDriver(), testDeleteCardWORelationsExpectedURLFragment, testDeleteCardWORelationsExpectedTitle,
				testDeleteCardWORelationsMenuLevel1, testDeleteCardWORelationsMenuLevel2, testDeleteCardWORelationsMenuLevel3, testDeleteCardWORelationsTargetClass);
		sleep(context);
		ExtjsCardGrid grid = ExtjsCardGrid.extract(getDriver(), context);

		int itemsBefore = grid.rows();
		assertTrue(itemsBefore > 0);
		List<GridCell> cardToBeDeleted = grid.getRows().get(0);
		int rowsMatchingDeletedOneBefore = grid.getRowsContainingAllCells(cardToBeDeleted).size(); //generally 1
		WebElement firstRow = grid.expandGridRow(0);
		grid.deleteCard(firstRow);

		grid = ExtjsCardGrid.extract(getDriver(), context);

		ExtjsUtils.safelylickOnSideNavLeaf(getDriver(), testDeleteCardWORelationsChangeClassMenuLevel1, testDeleteCardWORelationsChangeClassMenuLevel2);
		ExtjsCardGrid.extract(getDriver(), context);
		ExtjsUtils.safelylickOnSideNavLeaf(getDriver(), testDeleteCardWORelationsMenuLevel1, testDeleteCardWORelationsMenuLevel2, testDeleteCardWORelationsMenuLevel3, testDeleteCardWORelationsTargetClass);
		ExtjsCardGrid gridDeleted = ExtjsCardGrid.extract(getDriver(), context);

		int itemsaAfter = gridDeleted.rows();
		assertEquals(itemsBefore -1, itemsaAfter);
		int rowsMatchingDeletedOneAfter = gridDeleted.getRowsContainingAllCells(cardToBeDeleted).size(); //generally 0
		assertEquals(rowsMatchingDeletedOneBefore -1, rowsMatchingDeletedOneAfter);

		testEnd(context);

	}

	@Test
	public void deleteCardWithoutRelationsTest() {

		UITestContext context = getDefaultTestContextInstance();
		context.withRule(relaxedClientLogRule);
		context.withTouchedClass(testDeleteCardWORelationsTargetClass);
		testStart(context);
		goFullScreen();

		login(Login.admin());

		ExtjsUtils.safelyClickOnSideNavLeafWithChecksAgainst(getDriver(), testDeleteCardWORelationsExpectedURLFragment, testDeleteCardWORelationsExpectedTitle,
				testDeleteCardWORelationsMenuLevel1, testDeleteCardWORelationsMenuLevel2, testDeleteCardWORelationsMenuLevel3, testDeleteCardWORelationsTargetClass);
		waitForLoad();
		ExtjsCardGrid grid = ExtjsCardGrid.extract(getDriver(), context);

		int itemsBefore = grid.rows();
		assertTrue(itemsBefore > 0);
		List<GridCell> cardToBeDeleted = grid.getRows().get(0);
		int rowsMatchingDeletedOneBefore = grid.getRowsContainingAllCells(cardToBeDeleted).size(); //generally 1
		WebElement firstRow = grid.expandGridRow(0);
		grid.deleteCard(firstRow);

		//end of Refactor
		grid = ExtjsCardGrid.extract(getDriver(), context);

		//close tree otherwise reopening parameter class fails
		//ExtjsUtils.safelylickOnSideNavLeaf(getDriver(),testDeleteCardWORelationsChangeClassMenuLevel1 );
		ExtjsUtils.collapseSideNavRootNode(getDriver(), testDeleteCardWORelationsMenuLevel1);
		ExtjsUtils.safelylickOnSideNavLeaf(getDriver(), testDeleteCardWORelationsMenuLevel1, testDeleteCardWORelationsMenuLevel2,testDeleteCardWORelationsMenuLevel3, testDeleteCardWORelationsTargetClass);
		ExtjsCardGrid.extract(getDriver(), context);
		ExtjsUtils.safelylickOnSideNavLeaf(getDriver(), "Locations", "Floor");
		ExtjsCardGrid gridDeleted = ExtjsCardGrid.extract(getDriver(), context);

		int itemsaAfter = gridDeleted.rows();
		assertEquals(itemsBefore -1, itemsaAfter);
		int rowsMatchingDeletedOneAfter = gridDeleted.getRowsContainingAllCells(cardToBeDeleted).size(); //generally 0
		assertEquals(rowsMatchingDeletedOneBefore -1, rowsMatchingDeletedOneAfter);
		
		testEnd(context);

	}
	

	@Test
	public void deleteCardW1NRelationFailsIfNSideNotEmpty() {

		UITestContext context = getDefaultTestContextInstance();
		context.withRule(relaxedClientLogRule);
		context.withTouchedClasses(deleteCardW1NRelationFailsIfNSideNotEmpty_TouchedClasses);
		testStart(context);
		goFullScreen();

		login(Login.admin());
		
		assertTrue(safelylickOnSideNavLeaf(getDriver(),deleteCardW1NRelationFailsIfNSideNotEmpty_MenuTreeTargetClass));

		ExtjsCardGrid grid = ExtjsCardGrid.extract(getDriver(), context);
		int itemsBefore = grid.rows();
		assertTrue(itemsBefore > 0);
		assertTrue(grid.hasRowContainingAllCells(deleteCardW1NRelationFailsIfNSideNotEmpty_CardToDeleteIdentifier));
		List<Integer> deleteIndex = grid.getIndexOfRowsCointainingAllCells(deleteCardW1NRelationFailsIfNSideNotEmpty_CardToDeleteIdentifier);
		assertEquals(1, deleteIndex.size());
		WebElement row = grid.expandGridRow(deleteIndex.get(0));
		
		grid.deleteCard(row);

		//should appear an error message box
		WebElement msgBoxHeader = ExtjsUtils.waitForElementVisibility(getDriver(), locatorMsgBoxTitle());
		assertTrue("Error".equalsIgnoreCase(msgBoxHeader.getText()));
		//WebElement okButton = ExtjsUtils.waitForVisibilityOfNestedElement(getDriver(), By.className("x-btn"), By.className("x-message-box"));
		//okButton.click();

		ExtjsCardGrid.extract(getDriver(), context);
		assertTrue(safelylickOnSideNavLeaf(getDriver(),deleteCardW1NRelationFailsIfNSideNotEmpty_MenuTreeOtherClass));
		waitForLoad();

		//Check card was not deleted
		assertTrue(safelylickOnSideNavLeaf(getDriver(),deleteCardW1NRelationFailsIfNSideNotEmpty_MenuTreeTargetClass));
		ExtjsCardGrid gridDeleted = ExtjsCardGrid.extract(getDriver(), context);
		assertEquals(itemsBefore  , gridDeleted.rows());
		assertTrue(gridDeleted.hasRowContainingAllCells(deleteCardW1NRelationFailsIfNSideNotEmpty_CardToDeleteIdentifier));

		//db checks against deleted card and relation
		JdbcTemplate jdbc = getJdbcTemplate();
		assertEquals(new Integer(1),jdbc.queryForObject(deleteCardW1NRelationFailsIfNSideNotEmpty_sqlChekCardIsNotDeleted, Integer.class));
		assertEquals(new Integer(1),jdbc.queryForObject(deleteCardW1NRelationFailsIfNSideNotEmpty_sqlChekRelationIsNotDeleted, Integer.class));

		testEnd(context);
	}

 
    //cross test (card cloning) related parameters
	private String[] cloneCard_anotherClassPath = {"Software" , "Applications"};
	private String cloneCard_anotherClassCheckFragment = "Application";

	//TEST cloneAndSaveSimpleCardWOreferences parameters
	private String  cloneSimpleCard_expectedCardTitle = "Cards VLAN";
	private String  cloneSimpleCard_expectedURLFragment = "VLAN";
	private String[] cloneSimpleCard_targetClassPath = {"Networks" , "VLANs"};
	private List<String> cloneSimpleCard_TouchedClasses = ImmutableList.of("VLAN");

    @Test
    public void cloneAndSaveSimpleCardWreferences() {

		UITestContext context = getDefaultTestContextInstance();
		context.withRule(getDefaultCLientLogCheckRule());
		context.withTouchedClasses(cloneSimpleCard_TouchedClasses);
		testStart(context);
		goFullScreen();

		login(Login.admin());

		safelyClickOnSideNavLeafWithChecksAgainst(getDriver(), cloneSimpleCard_expectedURLFragment, cloneSimpleCard_expectedCardTitle, cloneSimpleCard_targetClassPath);
		ExtjsCardGrid grid = ExtjsCardGrid.extract(getDriver(), context);

		WebElement firstRow = grid.expandGridRow(0);
		List<GridCell> clonedRow = grid.getRows().get(0);
		int rowsAsClonedBefore =  grid.getRowsContainingAllCells(clonedRow).size();
		assertTrue("There should be at least one row like the one closed (metatest)" , rowsAsClonedBefore > 0);
		int itemsBefore = grid.rows();
		WebElement window = grid.cloneCard(firstRow);
		clickDetailFormButton(window, "Save");
		artificialSleep(3000, context);
		grid.closeCardDetails();

		ExtjsCardGrid gridAfter = ExtjsCardGrid.extract(getDriver(), context);
		int rowsAsClonedAfter =  gridAfter.getRowsContainingAllCells(clonedRow).size();

		//does not work if grid is full (max displayable items are already shown)
		assertEquals("Number of rows should be incremented by one" , itemsBefore +1 , gridAfter.rows());
		assertEquals("Number of rows like the one cloned should be incremented by one" , rowsAsClonedBefore +1 , rowsAsClonedAfter);

		//TODO go away and come back (ensure client is not cheating :) )
		safelyClickOnSideNavLeafWithChecksAgainst(getDriver(), cloneCard_anotherClassCheckFragment, cloneCard_anotherClassCheckFragment, cloneCard_anotherClassPath);
		ExtjsCardGrid gridFake = ExtjsCardGrid.extract(getDriver(), context);

		safelyClickOnSideNavLeafWithChecksAgainst(getDriver(), cloneSimpleCard_expectedURLFragment, cloneSimpleCard_expectedCardTitle, cloneSimpleCard_targetClassPath);
		ExtjsCardGrid gridComeBack = ExtjsCardGrid.extract(getDriver(), context);
		assertEquals("Number of rows should be incremented by one" , itemsBefore +1 , gridComeBack.rows());
		assertEquals("Number of rows like the one cloned should be incremented by one" , rowsAsClonedBefore +1
				, gridComeBack.getRowsContainingAllCells(clonedRow).size());

		testEnd(context);
	}


	private String  cloneCardBlank_expectedCardTitle = "Room";
	private String  cloneCardBlank_expectedURLFragment = "Room";
	private String[] cloneCardBlank_targetClassPath = {"Locations" , "Rooms"};
	private String  cloneCardBlank_mandatoryReferenceFieldM = "Building";
	private String  cloneCardBlank_optionalReferenceField = "Floor"; //this field is optional, nevertheless must be blanked if present in cloned card
	private List<String> cloneCardBlank_referenceFields = ImmutableList.of(cloneCardBlank_mandatoryReferenceFieldM , cloneCardBlank_optionalReferenceField);
	private List<String> cloneCardBlank_nonReferenceFields = ImmutableList.of("Code", "Use");
	private int cloneCardBlank_maxItemsToCheck = 3;


	@Test
	public void cloneCardShouldBlankReferenceFields() {

		UITestContext context = getDefaultTestContextInstance();
		context.withRule(getDefaultCLientLogCheckRule());
		testStart(context);
		goFullScreen();

		login(Login.admin());

		safelyClickOnSideNavLeafWithChecksAgainst(getDriver(), cloneCardBlank_expectedCardTitle, cloneCardBlank_expectedURLFragment  , cloneCardBlank_targetClassPath);
		ExtjsCardGrid grid = ExtjsCardGrid.extract(getDriver(), context);
		WebElement firstRow = grid.expandGridRowCheckingIfAlreadyOpen(0);
		sleep(context);
		WebElement cardDetail = grid.openCardAlt(firstRow);
		sleep(context);
		WebElement cardMasterDetail = grid.openCardsDetailTabMasterDetails(cardDetail);
		List<WebElement> references = cardMasterDetail.findElements(By.tagName("table"));
		int referencesBefore = references.size();
		
		assertTrue(referencesBefore>0);
		sleep(context);
		
		getDriver().switchTo().activeElement().sendKeys(Keys.ESCAPE);
		
		ExtjsCardGrid grid2 = ExtjsCardGrid.extract(getDriver(), context);
		
		firstRow = grid2.expandGridRowCheckingIfAlreadyOpen(0);
		grid2.cloneCard(firstRow);
		sleep(context);
		ExtjsUtils.findElementByTestId(getDriver(), "card-create-saveandclose").click();
		sleep(context);
		
		ExtjsCardGrid grid3 = ExtjsCardGrid.extract(getDriver(), context);
		WebElement clonedRow = grid3.expandGridRowCheckingIfAlreadyOpen(1);
		
		WebElement cardDetail2 = grid3.openCardAlt(clonedRow);
		sleep(context);
		WebElement cardMasterDetail2 = grid3.openCardsDetailTabMasterDetails(cardDetail2);
		List<WebElement> references2 = cardMasterDetail2.findElements(By.tagName("table"));
		int referencesAfter = references2.size();
		
		assertTrue(referencesAfter==0);
		
		getDriver().switchTo().activeElement().sendKeys(Keys.ESCAPE);
		sleep(context);
		
		grid3.deleteCard(clonedRow);

		testEnd(context);

	}



	private String disableClass_targetDBClassName = "VPN";
	private String disableClass_targetUIClassName = "VPN";
	private String[] disableClass_targetClassMenuTree = {"Networks" , "VPNs"};

    @Test
	public void disableClassFromNavigationMenuTest() {

		UITestContext context = getDefaultTestContextInstance();
		context.withRule(getDefaultCLientLogCheckRule());
		testStart(context);
		
		goFullScreen();

		login(Login.admin());
		waitForLoad();

		ExtjsNavigationMenu navMenu = new ExtjsNavigationMenu(getDriver());
		Optional<ExtjsNavigationMenu.MenuItem> targetMenuItem = navMenu.getAtPath(disableClass_targetClassMenuTree);
		targetMenuItem.get().click();
		assertTrue("Target class should be present in navigation menu before disabling it..." , targetMenuItem.isPresent());
		
		Map<String, Object> jsonAsMap = new HashMap<>();
		jsonAsMap.put("name", "VPN");
		jsonAsMap.put("type", "standard");
		jsonAsMap.put("active", false);
		
		given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when().contentType("application/json").body(jsonAsMap)
			.put(buildRestV3Url("classes/" + jsonAsMap.get("name"))).then().statusCode(200);
		
		getRestClient().system().dropAllCaches();
		logout();
		waitForLoad();
		login(Login.admin());
		waitForLoad();
		ExtjsNavigationMenu navMenuAfter = new ExtjsNavigationMenu(getDriver());
		Optional<ExtjsNavigationMenu.MenuItem> targetMenuItemAfter = navMenuAfter.getAtPath(disableClass_targetClassMenuTree);
		assertFalse("Target class should no more be present in navigation menu after having disabled it..." , targetMenuItemAfter.isPresent());
		
		// reimposto ad 'active=true' la classe
		
		jsonAsMap.put("active", true);
		
		given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when().contentType("application/json").body(jsonAsMap)
			.put(buildRestV3Url("classes/" + jsonAsMap.get("name"))).then().statusCode(200);
		
		testEnd(context);

	}
    
    @Test
	public void addRemoveNewFilter() {

		UITestContext context = getDefaultTestContextInstance();
		context.withRule(getDefaultCLientLogCheckRule());
		testStart(context);
		goFullScreen();
		 
		login(Login.admin());
		sleep(context);
		
		safelyClickOnSideNavLeafWithChecksAgainst(getDriver(), "InternalEmployee", "Cards Internal employee", "Employees", "Internal employees");
		
		createNewFilter(context, "Code", "Equals", "t.smith", "Test Filter");
		
		ExtjsCardGrid grid = ExtjsCardGrid.extract(getDriver(), context);
		
		assertTrue(grid.getRows().size()==1);
		
		logout();
		sleep(context);
		
		login(Login.admin());
		sleep(context);

		safelyClickOnSideNavLeafWithChecksAgainst(getDriver(), "InternalEmployee", "Cards Internal employee", "Employees", "Internal employees");
		sleep(context);
		
		deleteFilter(context);
		
		testEnd(context);

    }
    
    @Test
	public void printCard() {

		UITestContext context = getDefaultTestContextInstance();
		context.withRule(getDefaultCLientLogCheckRule());
		testStart(context);
		goFullScreen();
		 
		login(Login.admin());
		sleep(context);
		
		safelyClickOnSideNavLeafWithChecksAgainst(getDriver(), "Desktop", "Cards Desktop", "Workplaces", "Desktops");
		
		ExtjsCardGrid grid = ExtjsCardGrid.extract(getDriver(), context);
		
		grid.expandGridRowCheckingIfAlreadyOpen(0);
		
		ExtjsUtils.findElementByTestId(getDriver(), "cards-card-view-printBtn").sendKeys(Keys.ENTER);
		sleep(context);
		
		getDriver().switchTo().activeElement().findElement(By.xpath("//a[@data-qtip='Print as PDF']//div")).click();
		sleep(context);
		
		assertTrue(linkResponse(getDriver().getCurrentUrl()));
		sleep(context);
		
		testEnd(context);

    }
    
    
    public static boolean linkResponse(String url){
        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;    
        }
        
    }
    
    
	public void createNewFilter(UITestContext context, String attribute, String operator, String value, String filterName) {
		ExtjsUtils.findElementByTestId(getDriver(), "filters-launcher-mainbutton").click();

		WebElement attributeChoose = ExtjsUtils.findElementByTestId(getDriver(),
				"filters-attributes-row-attributecombo");
		attributeChoose.findElement(By.className("x-form-arrow-trigger")).click();
		sleep(context);

		ExtjsUtils.findElementByXPath(getDriver(), "//div[@data-ref='listWrap']//ul//li[text()='"+attribute+"']").click();

		WebElement operatorChoose = ExtjsUtils.findElementByTestId(getDriver(), "filters-attributes-row-operatorcombo");
		operatorChoose.findElement(By.className("x-form-arrow-trigger")).click();
		sleep(context);

		ExtjsUtils.findElementByXPath(getDriver(), "//div[@data-ref='listWrap']//ul//li[text()='"+operator+"']").click();
		sleep(context);

		WebElement span = ExtjsUtils.findElementByXPath(getDriver(), "//span[text()='Value']");
		WebElement span2 = ExtjsUtils.getParent(span);
		WebElement label = ExtjsUtils.getParent(span2);
		WebElement div = ExtjsUtils.getParent(label);
		div.findElement(By.tagName("input")).sendKeys(value);
		sleep(context);

		ExtjsUtils.findElementByXPath(getDriver(), "//div[@data-qtip='Add filter']").click();
		
		ExtjsUtils.findElementByTestId(getDriver(), "filters-panel-savebutton").click();
		sleep(context);
		
		WebElement newFilter = getDriver().switchTo().activeElement().findElement(By.tagName("Input"));
		newFilter.clear();
		newFilter.sendKeys(filterName);
		sleep(context);
		
		getDriver().switchTo().activeElement().findElement(By.xpath("//span[text()='Save']")).click();
		
		
	}
	
	
	public void deleteFilter(UITestContext context) {
		ExtjsUtils.findElementByTestId(getDriver(), "filters-launcher-mainbutton").click();
		sleep(context);
		ExtjsUtils.findElementByXPath(getDriver(), "//div[@data-qtip='Delete']").click();
	}

	public static class DeleteCardResult {

		private final int rowsBeforeDeletiontion;
		private final List<GridCell> deletedRow;

		public DeleteCardResult(int rowsBeforeDeletion, List<GridCell> deletedRow) {
			this.deletedRow = deletedRow; 
			this.rowsBeforeDeletiontion = rowsBeforeDeletion;
		}
		public int getRowsBeforeDeletiontion() {return rowsBeforeDeletiontion;}
		public List<GridCell> getDeletedRow() {	return deletedRow;}
	}


}
