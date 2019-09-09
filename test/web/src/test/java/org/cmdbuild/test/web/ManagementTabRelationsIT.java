package org.cmdbuild.test.web;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import org.cmdbuild.test.web.utils.*;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.internal.WrapsDriver;
import org.springframework.dao.DataAccessException;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.hasKey;

import java.awt.dnd.DragSourceAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.cmdbuild.test.web.utils.ExtjsUtils.*;
import static org.cmdbuild.test.web.utils.UILocators.cardDetailsRelationsGrid;
import static org.cmdbuild.test.web.utils.UILocators.cmdbuildManagementDetailsWindowLocator;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ManagementTabRelationsIT extends BaseWebIT {

	@After
	public void cleanup() {
		cleanupDB();
	}

	public static class RelationSelectionGridRow {

		final List<String> content = new ArrayList<>();
		// private WebElement rowRootTableElement;
		private WebElement checkBox;
		private boolean initiallySelected;
		private boolean disabled;

		public List<String> getContent() {
			return content;
		}

		public boolean containsFragment(String fragment) {
			return content.stream().anyMatch(c -> c.contains(fragment));
		}

		public boolean contains(String stringToMatch) {
			return content.stream().anyMatch(c -> stringToMatch.equals(c));
		}

		public void checkItem() { // It is not possible to uncheck (toggle) as of 2018-08-10
			safeClick(((WrapsDriver) checkBox).getWrappedDriver(), checkBox);
			// selected = true;
		}

		public boolean isInitiallySelected() {
			return initiallySelected;
		}

		public static RelationSelectionGridRow from(WebElement rowRootTableElement) {

			RelationSelectionGridRow r = new RelationSelectionGridRow();
			if (rowRootTableElement.getAttribute("class").contains("x-grid-item-selected"))
				r.initiallySelected = true;
			else
				r.initiallySelected = false;
			List<WebElement> tds = rowRootTableElement.findElements(By.tagName("td"));
			for (WebElement td : tds) {
				if (td.getAttribute("class").contains("x-grid-cell-first")) {// select box
					r.checkBox = td;
				} else {// field content
					r.content.add(Strings.nullToEmpty(td.getText()).trim());
				}
			}
			if (tds.get(0).getAttribute("class").contains("x-item-disabled"))
				r.disabled = true;

			return r;
		}

		public boolean isDisabled() {
			return disabled;
		}
	}

	// Cross Test Vars
	// Add Relation Test Vars

	private String addRel_RelationNameDirect = "depends on";
	private String addRel_RelationNameInverse = "depends on";

	private String[] addRel_Class1_TargetPath = { "Locations", "Rooms" };
	private String[] addRel_Class2_TargetPath = { "Infrastructures", "Storages" };
	private String addRel_Class1ExpectedUrlAndTitleFragment = "Room";
	private String addRel_Class2ExpectedUrlAndTitleFragment = "Storage";
	private String addRel_Class1CardCode = "PostgreSQL02";
	private String addRel_Class2CardCode = "Storage03";
	private String addRel_TextSearchCard2Query = "R66M";
	private List<FormField> addRel_Class1FormFieldQueryTargetCard = ImmutableList
			.of(FormField.of("Description", "vincent PostgreSQL 9.4.1 9.3.1"));
	private List<FormField> addRel_Class2FormFieldQueryTargetCard = ImmutableList
			.of(FormField.of("Code", addRel_Class2CardCode), FormField.of("Serial number", "S003"));

	private List<String> addRel_TouchedClasses = ImmutableList.of("Storage", "Database", "Map_CIDependency");

	@Test
	public void addRelationTest() {

		UITestContext context = getDefaultTestContextInstance();
		context.withTouchedClasses(addRel_TouchedClasses);

		testStart(context);
		goFullScreen();
		login(Login.admin());
		sleep(context);

		safelyClickOnSideNavLeafWithChecksAgainst(getDriver(), "Database", "Cards Database", "Software", "Databases");
		ExtjsCardGrid grid1 = ExtjsCardGrid.extract(getDriver(), context);
		List<Integer> idxOfTargetCard = grid1.getIndexOfRowsCointainingAllCells(
				GridCell.formfieldsToCellArray(addRel_Class1FormFieldQueryTargetCard));
		assertTrue("Not found target card", idxOfTargetCard.size() >= 1);
		WebElement cardRow = grid1.expandGridRow(idxOfTargetCard.get(0));
		WebElement details1 = grid1.openCard(cardRow);
		sleep(context);
		WebElement relationPanel1 = grid1.openCardsDetailTabRelations(details1);
		sleep(context);
		int itemsBefore = waitForVisibilityOfNestedElement(getDriver(), By.className("x-grid-item-container"),
				relationPanel1).findElements(By.tagName("table")).size();

		addARelation("Storage03", "depends on");

		// check new relation instance was added
		int itemsAfter = waitForVisibilityOfNestedElement(getDriver(), By.className("x-grid-item-container"),
				relationPanel1).findElements(By.tagName("table")).size();
		assertEquals("Shown relations must be incremented by 1", itemsBefore + 1, itemsAfter);

		getDriver().switchTo().activeElement().sendKeys(Keys.ESCAPE, Keys.ESCAPE);
		sleep(context);
		WebElement detailsAfter = grid1.openCard(cardRow);
		List<ExtjsCardGrid.Relation> relationsAfter = ExtjsCardGrid
				.relationsFromGrid(grid1.openCardsDetailTabRelationsAndGetRelations(detailsAfter));
		assertTrue("Added relation must be listed immediately in card relations",
				relationsAfter.stream().anyMatch(r -> (r.getRelationDescription().contains(addRel_RelationNameDirect))
						&& (addRel_Class2CardCode.equals(r.getCode()))));

		// check relation was added also onto the other side
		safelyClickOnSideNavLeafWithChecksAgainst(getDriver(), addRel_Class2ExpectedUrlAndTitleFragment,
				addRel_Class2ExpectedUrlAndTitleFragment, addRel_Class2_TargetPath);
		ExtjsCardGrid grid2 = ExtjsCardGrid.extract(getDriver(), context);
		List<Integer> idxOfTargetCard2 = grid2.getIndexOfRowsCointainingAllCells(
				GridCell.formfieldsToCellArray(addRel_Class2FormFieldQueryTargetCard));
		assertTrue("Not found target card", idxOfTargetCard2.size() >= 1);
		WebElement cardRow2 = grid2.expandGridRow(idxOfTargetCard2.get(0));
		WebElement details2 = grid2.openCard(cardRow2);
		sleep(context);
		List<ExtjsCardGrid.Relation> relations2 = ExtjsCardGrid
				.relationsFromGrid(grid2.openCardsDetailTabRelationsAndGetRelations(details2));

		assertTrue("Added relation must be listed also in the linked card on other side of relation",
				relations2.stream().anyMatch(r -> (r.getRelationDescription().contains(addRel_RelationNameInverse))
						&& (addRel_Class1CardCode.equals(r.getCode()))));

		// delete new relation
		Optional<ExtjsCardGrid.Relation> targetExistingRelation = relations2.stream()
				.filter(r -> ((r.getRelationDescription().contains(addRel_RelationNameInverse))
						&& (addRel_Class1CardCode.equals(r.getCode()))))
				.findFirst();

		targetExistingRelation.get().remove();

		testEnd(context);
	}

	private String arworef_RelationNameDirect = "owned by";
	private String arworef_RelationNameInverse = "owner of";

	private String[] arworef_Class1_TargetPath = { "Software", "Applications" };
	private String[] arworef_Class2_TargetPath = { "Software", "Databases" };
	private String arworef_Class1ExpectedUrlAndTitleFragment = "Application";
	private String arworef_Class2ExpectedUrlAndTitleFragment = "Database";
	private String arworef_Class1CardCode = "Parallels Desktop 10 for Mac";
	private String arworef_Class2CardCode = "KAYBA";
	private String arworef_TextSearchCard2Query = "postgres";
	private FormField arworef_Class1FormFieldQueryTargetCard = FormField.of("Serial number", "00036");
	private FormField arworef_Class2FormFieldQueryTargetCard = FormField.of("Code", "KAYBA");

	private List<String> arworef_TouchedClasses = ImmutableList.of("Application", "Database", "Map_CIDependency");

	@Test
	public void addRelationWOReferenceTest() {

		UITestContext context = getDefaultTestContextInstance();
		context.withTouchedClasses(arworef_TouchedClasses).withRule(getDefaultCLientLogCheckRule());

		testStart(context);
		goFullScreen();
		login(Login.admin());

		safelyClickOnSideNavLeafWithChecksAgainst(getDriver(), "SWLicense", "Cards Software license", "Software",
				"Software licenses");
		ExtjsCardGrid grid1 = ExtjsCardGrid.extract(getDriver(), context);
		List<Integer> idxOfTargetCard = grid1
				.getIndexOfRowsCointainingAllCells(GridCell.formFieldToCell(arworef_Class1FormFieldQueryTargetCard));
		assertTrue("Not found target card", idxOfTargetCard.size() >= 1);
		WebElement cardRow = grid1.expandGridRow(idxOfTargetCard.get(0));
		WebElement details1 = grid1.openCard(cardRow);
		sleep(context);
		WebElement relationPanel1 = grid1.openCardsDetailTabRelations(details1);
		sleep(context);
		int itemsBefore = waitForVisibilityOfNestedElement(getDriver(), By.className("x-grid-item-container"),
				relationPanel1).findElements(By.tagName("table")).size();

		addARelation("KAYBA", "owned by");

		// check new relation instance was added
		int itemsAfter = waitForVisibilityOfNestedElement(getDriver(), By.className("x-grid-item-container"),
				relationPanel1).findElements(By.tagName("table")).size();
		assertEquals("Shown relations must be incremented by 1", itemsBefore + 1, itemsAfter);

		getDriver().switchTo().activeElement().sendKeys(Keys.ESCAPE, Keys.ESCAPE);
		sleep(context);
		WebElement detailsAfter = grid1.openCard(cardRow);

		List<ExtjsCardGrid.Relation> relationsAfter = ExtjsCardGrid
				.relationsFromGrid(grid1.openCardsDetailTabRelationsAndGetRelations(detailsAfter));
		assertTrue("Added relation must be listed immediately in card relations",
				relationsAfter.stream().anyMatch(r -> (r.getRelationDescription().contains(arworef_RelationNameDirect))
						&& (arworef_Class2CardCode.equals(r.getCode()))));

		// check relation was added also onto the other side
		safelyClickOnSideNavLeafWithChecksAgainst(getDriver(), "Supplier", "Cards Supplier", "Suppliers", "Suppliers");
		ExtjsCardGrid grid2 = ExtjsCardGrid.extract(getDriver(), context);
		List<Integer> idxOfTargetCard2 = grid2
				.getIndexOfRowsCointainingAllCells(GridCell.formFieldToCell(arworef_Class2FormFieldQueryTargetCard));
		assertTrue("Not found target card", idxOfTargetCard2.size() >= 1);
		WebElement cardRow2 = grid2.expandGridRow(idxOfTargetCard2.get(0));
		WebElement details2 = grid2.openCard(cardRow2);
		sleep(context);
		List<ExtjsCardGrid.Relation> relations2 = ExtjsCardGrid
				.relationsFromGrid(grid2.openCardsDetailTabRelationsAndGetRelations(details2));
		assertTrue("Added relation must be listed immediately in side 2 card relations",
				relations2.stream().anyMatch(r -> (r.getRelationDescription().contains(arworef_RelationNameInverse))
						&& (arworef_Class1CardCode.equals(r.getCode()))));

		// delete new relation
		Optional<ExtjsCardGrid.Relation> targetExistingRelation = relations2.stream()
				.filter(r -> ((r.getRelationDescription().contains(arworef_RelationNameInverse))
						&& (arworef_Class1CardCode.equals(r.getCode()))))
				.findFirst();

		targetExistingRelation.get().remove();

		testEnd(context);

	}

	private String oneToMany_RelationNameDirect = "has (Floor)";
	private String oneToMany_RelationNameInverse = "belongs to";
	private String oneToMany_RelationDBName = "Map_BuildingFloor";

	private String[] oneToMany_Class1_TargetPath = { "Locations", "Buildings" };
	private String[] oneToMany_Class2_TargetPath = { "Locations", "Floors" };
	private String oneToMany_Class1ExpectedUrlAndTitleFragment = "Building";
	private String oneToMany_Class2ExpectedUrlAndTitleFragment = "Floor";
	private String oneToMany_Class2DBClassName = "Floor";
	private String oneToMany_Class1CardCode = "LMT";
	private String oneToMany_Class2CardCode = "F00";
	private String oneToMany_Class2CardField2Name = "Description";
	private String oneToMany_Class2CardField2Content = "AC Aon Center - 00";
	// private String oneToMany_TextSearchCard2Query = "849123-45";
	private List<FormField> oneToMany_Class1FFQueryTargetCard = ImmutableList
			.of(FormField.of("Code", oneToMany_Class1CardCode));
	private List<FormField> oneToMany_Class2FFQueryTargetCard = ImmutableList.of(
			FormField.of("Code", oneToMany_Class2CardCode),
			FormField.of(oneToMany_Class2CardField2Name, oneToMany_Class2CardField2Content));

	private String oneToMany_SqlFetchCardIdTemplate = "select \"Id\" from \"[class]\" where \"code\" = ? and \"[field2]\" = ? and \"Status\" = 'A'";
	private String oneToMany_SqlCheckRelationExistsTemplate = "select count(*) from \"[relation]\" where \"IdObj2\" = ? and \"Status\" = 'A'";
	// private List<String> oneToMany_TouchedClasses = ImmutableList.of("Floor" ,
	// "Building" , oneToMany_RelationDBName);

	@Test
	public void multipleAssociationsNotAllowedForOneToManyRelationsTest() {

		UITestContext context = getDefaultTestContextInstance();

		testStart(context);
		goFullScreen();
		login(Login.admin());
		safelyClickOnSideNavLeafWithChecksAgainst(getDriver(), "VLAN", "Cards VLAN", "Networks", "VLANs");
		ExtjsCardGrid grid1 = ExtjsCardGrid.extract(getDriver(), context);
		List<Integer> idxOfTargetCard = grid1.getIndexOfRowsCointainingAllCells(GridCell.from("Code", "VLANAon001"));

		assertTrue(idxOfTargetCard.size() >= 1);
		WebElement cardRow = grid1.expandGridRowCheckingIfAlreadyOpen(idxOfTargetCard.get(0));
		WebElement details1 = grid1.openCard(cardRow);
		sleep(context);
		WebElement relationPanel1 = grid1.openCardsDetailTabRelations(details1);
		sleep(context);
		WebElement itemsBefore = waitForVisibilityOfNestedElement(getDriver(), By.className("x-grid-item-container"),
				relationPanel1).findElement(By.xpath("//div[text()='AonIPR2']"));

		assertTrue(itemsBefore.isDisplayed());

		WebElement addDetailButton = ExtjsUtils.findElementByTestId(getDriver(), "relations-list-container-addbtn");
		String addDetailButtonPopupId = addDetailButton.getAttribute("aria-owns");
		addDetailButton.click();
		sleep(context);
		WebElement popupOptionsContainer = waitForElementPresence(By.id(addDetailButtonPopupId));
		List<WebElement> options = popupOptionsContainer.findElements(By.tagName("a"));
		WebElement option = options.stream().filter(
				a -> a.findElement(By.className("x-menu-item-text")).getText().trim().startsWith("has addresses"))
				.findFirst().get();
		safeClick(getDriver(), option);
		sleep(context);

		WebElement popupAddForm = waitForElementVisibility(By.id("popup-add-relation"));
		assertTrue("add relation title should contain: " + "has addresses", waitForPresenceOfNestedElement(getDriver(),
				By.id("popup-add-relation_header-title-textEl"), popupAddForm).getText().contains("has addresses"));

		List<WebElement> itemGrid = popupAddForm
				.findElements(By.xpath("//div[contains(@class, 'x-panel-relationslist')]//table"));
		List<RelationSelectionGridRow> relationsSeletionGrid = itemGrid.stream()
				.map(ig -> RelationSelectionGridRow.from(ig)).collect(Collectors.toList());
		Optional<RelationSelectionGridRow> cardToAssociate = relationsSeletionGrid.stream()
				.filter(r -> r.containsFragment("AonIPR1")).findFirst();
		assertTrue("Cannot find the card to associate", cardToAssociate.isPresent());

		WebElement relationGrid = ExtjsUtils.findElementById(getDriver(), "popup-add-relation-body");
		List<WebElement> itemGrid2 = relationGrid.findElements(By.tagName("table"));
		List<RelationSelectionGridRow> relationsSeletionGrid2 = itemGrid2.stream()
				.map(ig -> RelationSelectionGridRow.from(ig)).collect(Collectors.toList());
		Optional<RelationSelectionGridRow> cardToAssociate2 = relationsSeletionGrid2.stream()
				.filter(r -> r.containsFragment("AonIPR1")).findFirst();
		assertTrue(cardToAssociate2.get().isDisabled());

		ExtjsUtils.findElementByTestId(getDriver(), "relations-list-add-gridcontainer-cancel").click();
		sleep(context);

		List<ExtjsCardGrid.Relation> relations2 = ExtjsCardGrid.relationsFromGrid(itemGrid);
		assertTrue("Relation must be listed", relations2.stream().anyMatch(
				r -> (r.getRelationDescription().contains("has addresses")) && ("AonIPR1".equals(r.getCode()))));

		// delete new relation
		Optional<ExtjsCardGrid.Relation> targetExistingRelation = relations2.stream().filter(
				r -> ((r.getRelationDescription().contains("has addresses")) && ("AonIPR1".equals(r.getCode()))))
				.findFirst();

		targetExistingRelation.get().remove();

		ExtjsUtils.findElementByTestId(getDriver(), "relations-list-container-addbtn").click();
		sleep(context);
		ExtjsUtils.findElementByXPath(getDriver(),
				"//div[contains(@class, 'x-box-scroller')]//span[text()='has addresses (IP range)']").click();
		sleep(context);

		WebElement relationGrid3 = ExtjsUtils.findElementById(getDriver(), "popup-add-relation-body");
		List<WebElement> itemGrid3 = relationGrid3.findElements(By.tagName("table"));
		List<RelationSelectionGridRow> relationsSeletionGrid3 = itemGrid3.stream()
				.map(ig -> RelationSelectionGridRow.from(ig)).collect(Collectors.toList());
		Optional<RelationSelectionGridRow> cardToAssociate3 = relationsSeletionGrid3.stream()
				.filter(r -> r.containsFragment("AonIPR1")).findFirst();
		assertFalse(cardToAssociate3.get().isDisabled());

		cardToAssociate3.get().checkItem();
		ExtjsUtils.findElementByTestId(getDriver(), "relations-list-add-gridcontainer-save").click();
		sleep(context);

		testEnd(context);

	}

	private String oneToManyAlt_RelationNameDirect = "belongs to";
	private String oneToManyAlt_RelationNameInverse = "has (Floor)";
	private String oneToManyAlt_RelationDBName = "Map_BuildingFloor";

	private String[] oneToManyAlt_Class1_TargetPath = { "Locations", "Floors" };
	private String[] oneToManyAlt_Class2_TargetPath = { "Locations", "Buildings" };
	private String oneToManyAlt_Class1ExpectedUrlAndTitleFragment = "Internal Employee";
	private String oneToManyAlt_Class2ExpectedUrlAndTitleFragment = "Building";
//    private String oneToManyAlt_Class2DBClassName = "Building";
	private String oneToManyAlt_Class1DBClassName = "Floor";
	private String oneToManyAlt_Class1CardCode = "c.colding";
	private String oneToManyAlt_Class2CardCode = "LMT";
	private String oneToManyAlt_Class1CardField2Name = "Description";
	private String oneToManyAlt_Class1CardField2Content = "AC Aon Center - 00";
	private List<FormField> oneToManyAlt_Class2FFQueryTargetCard = ImmutableList
			.of(FormField.of("Code", oneToManyAlt_Class2CardCode));
	private List<FormField> oneToManyAlt_Class1FFQueryTargetCard = ImmutableList.of(
			FormField.of("Code", oneToManyAlt_Class1CardCode),
			FormField.of(oneToManyAlt_Class1CardField2Name, oneToManyAlt_Class1CardField2Content));

	private String oneToManyAlt_SqlFetchCardIdTemplate = "select \"Id\" from \"[class]\" where \"code\" = ? and \"[field2]\" = ? and \"Status\" = 'A'";
	private String oneToManyAlt_SqlCheckRelationExistsTemplate = "select count(*) from \"[relation]\" where \"IdObj2\" = ? and \"Status\" = 'A'";
	// private List<String> oneToManyAlt_TouchedClasses = ImmutableList.of("Floor" ,
	// "Building" , oneToMany_RelationDBName);

	@Test
	public void multipleAssociationsNotAllowedForOneToManyRelationsAlternativeTest() {

		UITestContext context = getDefaultTestContextInstance();

		testStart(context);
		goFullScreen();
		login(Login.admin());
		safelyClickOnSideNavLeafWithChecksAgainst(getDriver(), "InternalEmployee", "Cards Internal employee",
				"Employees", "Internal employees");
		ExtjsCardGrid grid1 = ExtjsCardGrid.extract(getDriver(), context);
		List<Integer> idxOfTargetCard = grid1.getIndexOfRowsCointainingAllCells(GridCell.from("Code", "c.colding"));

		assertTrue("Not found target card, class: " + oneToManyAlt_Class1ExpectedUrlAndTitleFragment + " code: "
				+ oneToManyAlt_Class1CardCode, idxOfTargetCard.size() >= 1);
		WebElement cardRow = grid1.expandGridRow(idxOfTargetCard.get(0));
		WebElement details1 = grid1.openCard(cardRow);
		sleep(context);
		WebElement relationPanel1 = grid1.openCardsDetailTabRelations(details1);
		sleep(context);

		WebElement itemsBefore = waitForVisibilityOfNestedElement(getDriver(), By.className("x-grid-item-container"),
				relationPanel1).findElement(By.xpath("//div[text()='SERVER_1_SQL']"));

		assertTrue(itemsBefore.isDisplayed());

		WebElement addDetailButton = ExtjsUtils.findElementByTestId(getDriver(), "relations-list-container-addbtn");
		String addDetailButtonPopupId = addDetailButton.getAttribute("aria-owns"); // looks like it does not change...
		addDetailButton.click();
		sleep(context);
		WebElement popupOptionsContainer = waitForElementPresence(By.id(addDetailButtonPopupId));
		List<WebElement> options = popupOptionsContainer.findElements(By.tagName("a"));
		WebElement option = options.stream().filter(a -> a.findElement(By.className("x-menu-item-text")).getText()
				.trim().startsWith("belongs to (Organizational unit)")).findFirst().get();
		safeClick(getDriver(), option);
		sleep(context);

		WebElement popupAddForm = waitForElementVisibility(By.id("popup-add-relation"));
		assertTrue("add relation title should contain: " + "belongs to (Organizational unit)",
				waitForPresenceOfNestedElement(getDriver(), By.id("popup-add-relation_header-title-textEl"),
						popupAddForm).getText().contains("belongs to (Organizational unit)"));

		List<WebElement> itemGrid = popupAddForm.findElements(By.tagName("table"));
		List<RelationSelectionGridRow> relationsSeletionGrid = itemGrid.stream()
				.map(ig -> RelationSelectionGridRow.from(ig)).collect(Collectors.toList());
		Optional<RelationSelectionGridRow> cardToAssociate = relationsSeletionGrid.stream()
				.filter(r -> r.containsFragment("SERVER_1_SQL")).findFirst();

		assertTrue("Cannot find the card to associate", cardToAssociate.isPresent());

		assertTrue(cardToAssociate.get().isDisabled());

		Optional<RelationSelectionGridRow> cardToAssociate2 = relationsSeletionGrid.stream()
				.filter(r -> r.containsFragment("SERVER_1_EXC")).findFirst();

		assertTrue("Cannot find the card to associate", cardToAssociate2.isPresent());

		assertFalse(cardToAssociate2.get().isDisabled());
		cardToAssociate2.get().checkItem();
		sleep(context);

		ExtjsUtils.findElementByTestId(getDriver(), "relations-list-add-gridcontainer-cancel").click();

		testEnd(context);
	}

	// Add Existing Relation (between two already relationed objects) is not allowed

	private String addRelSameObjects_RelationNameDirect = "belongs to (Building)";
	private String addRelSameObjects_RelationShortNameDirect = "belongs";
	private String addRelSameObjects_RelationNameInverse = "has (Floor)";
	private String addRelSameObjects_RelationDBName = "Map_BuildingRoom";

	private String[] addRelSameObjects_Class1_TargetPath = { "Locations", "Rooms" };
	private String addRelSameObjects_Class1ExpectedUrlAndTitleFragment = "Room";
	private String addRelSameObjects_Class1DBClassName = "Room";
	private String addRelSameObjects_Class1CardCode = "R01";
//    private String addRelSameObjects_Class2CardCode = "LMT";
	private String addRelSameObjects_Class1CardField2Name = "Floor";
	private String addRelSameObjects_Class1CardCode2Content = "LMT";
	private String addRelSameObjects_Class1CardDescription2Content = "Legg Mason Tower";
	private String addRelSameObjects_Class1CardField2Content = addRelSameObjects_Class1CardCode2Content + " "
			+ addRelSameObjects_Class1CardDescription2Content + " - 01";
	// private List<FormField> addRelSameObjects_Class2FFQueryTargetCard =
	// ImmutableList.of(FormField.of("Code" , oneToManyAlt_Class2CardCode));
	private List<FormField> addRelSameObjects_Class1FFQueryTargetCard = ImmutableList.of(
			FormField.of("Code", addRelSameObjects_Class1CardCode),
			FormField.of(addRelSameObjects_Class1CardField2Name, addRelSameObjects_Class1CardField2Content));

	private String addRelSameObjects_Class2Name = "Building";

	private String addRelSameObjects_SqlFetchCardIdTemplate = "select \"Id\" from \"[class]\" where \"code\" = ? and \"[field2]\" = ? and \"Status\" = 'A'";
	private String addRelSameObjects_SqlCheckRelationExistsTemplate = "select count(*) from \"[relation]\" where \"IdObj2\" = ? and \"Status\" = 'A'";
	// private List<String> addRelSameObjects_TouchedClasses =
	// ImmutableList.of("Floor" , "Building" , oneToMany_RelationDBName);

	// Remove Relation From 1 - Side (Test Vars)

	private String[] removeRelationFrom1Side_TargetClass_TargetPath = { "Locations", "Buildings" };
	private String[] removeRelationFrom1Side_TargetClassNSide_TargetPath = { "Workplaces", "Notebooks" };
	private String removeRelationFrom1Side_TargetClassExpectedUrlAndTitleFragment = "Building";
	private String removeRelationFrom1Side_TargetClassNSideExpectedUrlAndTitleFragment = "Notebook";
	private List<String> removeRelationFrom1Side_TouchedClasses = ImmutableList.of("Building", "Map_BuildingCI",
			"Notebook"); // TODO FILL all. Also relations tables... Check them
	private FormField removeRelationFrom1Side_targetCardCodeField = FormField.of("Code", "AC");
	private List<FormField> removeRelationFrom1Side_TargetCard = ImmutableList
			.of(removeRelationFrom1Side_targetCardCodeField, FormField.of("Description", "Aon Center"));

	private String removeRelationFrom1Side_TypeOfRelationToDelete = "contains equipment";
	private String removeRelationFrom1Side_TypeOfInertedrelation = "located into building";
	private String removeRelationFrom1Side_TypeOfItemToRemoveFromRelation = "Notebook";

	@Test
	public void removeRelationFrom1SideTest() {

		UITestContext context = getDefaultTestContextInstance();
		context.withTouchedClasses(removeRelationFrom1Side_TouchedClasses).withRule(getDefaultCLientLogCheckRule());

		testStart(context);
		goFullScreen();
		login(Login.admin());

		safelyClickOnSideNavLeafWithChecksAgainst(getDriver(),
				removeRelationFrom1Side_TargetClassExpectedUrlAndTitleFragment,
				removeRelationFrom1Side_TargetClassExpectedUrlAndTitleFragment,
				removeRelationFrom1Side_TargetClass_TargetPath);
		ExtjsCardGrid grid = ExtjsCardGrid.extract(getDriver(), context);
		List<Integer> idxOfTargetCard = grid
				.getIndexOfRowsCointainingAllCells(GridCell.formfieldsToCellArray(removeRelationFrom1Side_TargetCard));
		assertTrue("Not found target card", idxOfTargetCard.size() >= 1);
		WebElement cardRow = grid.expandGridRow(idxOfTargetCard.get(0));
		WebElement details = grid.openCard(cardRow);
		sleep(context);

		List<ExtjsCardGrid.Relation> relations = ExtjsCardGrid
				.relationsFromGrid(grid.openCardsDetailTabRelationsAndGetRelations(details));
		List<ExtjsCardGrid.Relation> removableItems = relations.stream()
				.filter(r -> r.getRelationDescription().contains(removeRelationFrom1Side_TypeOfRelationToDelete)
						&& removeRelationFrom1Side_TypeOfItemToRemoveFromRelation.equals(r.getType()))
				.collect(Collectors.toList());
		assertTrue("There should be at least one target item to remove. Cannot perform relation deletion...",
				removableItems.size() > 0);
		ExtjsCardGrid.Relation relationToRemove = removableItems.get(0);
		relationToRemove.remove();
		sleep(context);

		List<ExtjsCardGrid.Relation> relationsAfter = ExtjsCardGrid
				.relationsFromGrid(grid.openCardsDetailTabRelationsAndGetRelations(details));
		assertEquals("There should be one item in the grid after deletion", relations.size() - 1,
				relationsAfter.size());
		assertFalse("Relation deleted seems still present",
				relationsAfter.stream().anyMatch(r -> relationToRemove.matches(r)));

		safelyClickOnSideNavLeafWithChecksAgainst(getDriver(),
				removeRelationFrom1Side_TargetClassNSideExpectedUrlAndTitleFragment,
				removeRelationFrom1Side_TargetClassNSideExpectedUrlAndTitleFragment,
				removeRelationFrom1Side_TargetClassNSide_TargetPath);
		ExtjsCardGrid gridNSide = ExtjsCardGrid.extract(getDriver(), context);
		List<Integer> idxs = gridNSide.getIndexOfRowsCointainingAllCells(
				GridCell.formFieldToCell(FormField.of("Code", relationToRemove.getCode())));
		assertTrue("Cannot identify removed card (not found or more than one card with the same code",
				idxs.size() == 1);
		WebElement cardNSide = gridNSide.expandGridRow(idxs.get(0));
		WebElement detailsNSide = gridNSide.openCard(cardNSide);

		sleep(context);
		List<ExtjsCardGrid.Relation> relationsNside = ExtjsCardGrid
				.relationsFromGrid(gridNSide.openCardsDetailTabRelationsAndGetRelations(detailsNSide));
		assertFalse("Deleted relation not expected",
				relationsNside.stream().anyMatch(
						r -> r.getRelationDescription().contains(removeRelationFrom1Side_TypeOfInertedrelation)
								&& normalized(r.getCode()).equals(removeRelationFrom1Side_targetCardCodeField)));
		assertFalse("Relations 1:N in N side must be empty for the relation type of  the removed relation",
				relationsNside.stream().anyMatch(
						r -> r.getRelationDescription().contains(removeRelationFrom1Side_TypeOfInertedrelation)));

		testEnd(context);
	}

	// Remove Relation From N - Side (Test Vars)

	private String[] removeRelationFromNSide_TargetClass_TargetPath = { "Workplaces", "Desktops" };
	private String[] removeRelationFromNSide_TargetClass1Side_TargetPath = { "Locations", "Rooms" };
	private String removeRelationFromNSide_TargetClassExpectedUrlAndTitleFragment = "Desktop";
	private String removeRelationFromNSide_TargetClass1SideExpectedUrlAndTitleFragment = "Room";
	private List<String> removeRelationFromNSide_TouchedClasses = ImmutableList.of("Room", "Map_BuildingCI", "Desktop");
	private FormField removeRelationFromNSide_targetCardCodeField = FormField.of("Code", "729232-10");
	private List<FormField> removeRelationFromNSide_TargetCard = ImmutableList
			.of(removeRelationFromNSide_targetCardCodeField, FormField.of("Hostname", "pc-aspencer"));

	private String removeRelationFromNSide_TypeOfRelationToDelete = "is placed in";
	private String removeRelationFromNSide_TypeOfInvertedRelation = "contains equipment";
	private String removeRelationFromNSide_TypeOfItemToRemoveFromRelation = "Room";

	@Test
	public void removeRelationFromNSideTest() {

		UITestContext context = getDefaultTestContextInstance();
		context.withTouchedClasses(removeRelationFromNSide_TouchedClasses).withRule(getDefaultCLientLogCheckRule());

		testStart(context);
		goFullScreen();
		login(Login.admin());

		safelyClickOnSideNavLeafWithChecksAgainst(getDriver(),
				removeRelationFromNSide_TargetClassExpectedUrlAndTitleFragment,
				removeRelationFromNSide_TargetClassExpectedUrlAndTitleFragment,
				removeRelationFromNSide_TargetClass_TargetPath);
		ExtjsCardGrid grid = ExtjsCardGrid.extract(getDriver(), context);
		List<Integer> idxOfTargetCard = grid
				.getIndexOfRowsCointainingAllCells(GridCell.formfieldsToCellArray(removeRelationFromNSide_TargetCard));
		assertTrue("Not found target card", idxOfTargetCard.size() >= 1);
		WebElement cardRow = grid.expandGridRow(idxOfTargetCard.get(0));
		WebElement details = grid.openCard(cardRow);
		sleep(context);

		List<ExtjsCardGrid.Relation> relations = ExtjsCardGrid
				.relationsFromGrid(grid.openCardsDetailTabRelationsAndGetRelations(details));
		List<ExtjsCardGrid.Relation> removableItems = relations.stream()
				.filter(r -> r.getRelationDescription().contains(removeRelationFromNSide_TypeOfRelationToDelete)
						&& removeRelationFromNSide_TypeOfItemToRemoveFromRelation.equals(r.getType()))
				.collect(Collectors.toList());
		logger.info("pre" + removableItems.size());
		if (removableItems.size() == 0) {
			ExtjsUtils.findElementByTestId(getDriver(), "relations-list-container-addbtn").click();
			sleep(context);
			WebElement placedInRoom = ExtjsUtils.waitForElementVisibility(getDriver(), By.xpath("//a[@role='menuitem']//span[text()='is placed in (Room)']"));
			placedInRoom.click();
			sleep(context);
			WebElement relationGrid3 = ExtjsUtils.findElementById(getDriver(), "popup-add-relation-body");
			List<WebElement> itemGrid3 = relationGrid3.findElements(By.tagName("table"));
			List<RelationSelectionGridRow> relationsSeletionGrid3 = itemGrid3.stream()
					.map(ig -> RelationSelectionGridRow.from(ig)).collect(Collectors.toList());
			Optional<RelationSelectionGridRow> cardToAssociate3 = relationsSeletionGrid3.stream()
					.filter(r -> r.containsFragment("R02") && r.containsFragment("Office")).findFirst();
			assertFalse(cardToAssociate3.get().isDisabled());

			cardToAssociate3.get().checkItem();
			ExtjsUtils.findElementByTestId(getDriver(), "relations-list-add-gridcontainer-save").click();
			sleep(context);
		}

		WebElement relationsGrid = ExtjsUtils.waitForVisibilityOfNestedElement(getDriver(), cardDetailsRelationsGrid(),
				cmdbuildManagementDetailsWindowLocator());
		List<ExtjsCardGrid.Relation> relations2 = ExtjsCardGrid
				.relationsFromGrid(relationsGrid.findElements(By.tagName("table")));
		removableItems = relations2.stream()
				.filter(r -> r.getRelationDescription().contains(removeRelationFromNSide_TypeOfRelationToDelete)
						&& removeRelationFromNSide_TypeOfItemToRemoveFromRelation.equals(r.getType()))
				.collect(Collectors.toList());
		logger.info("" + removableItems.size());
		assertTrue("There should be at least one target item to remove. Cannot perform relation deletion...",
				removableItems.size() > 0);
		ExtjsCardGrid.Relation relationToRemove = removableItems.get(0);
		relationToRemove.remove();
		sleep(context);

		List<ExtjsCardGrid.Relation> relationsAfter = ExtjsCardGrid
				.relationsFromGrid(grid.openCardsDetailTabRelationsAndGetRelations(details));
		assertEquals("There should be one item in the grid after deletion", relations2.size() - 1,
				relationsAfter.size());
		assertFalse("Relation deleted seems still present",
				relationsAfter.stream().anyMatch(r -> relationToRemove.matches(r)));

		// go to N side and confirm deletion

		safelyClickOnSideNavLeafWithChecksAgainst(getDriver(),
				removeRelationFromNSide_TargetClass1SideExpectedUrlAndTitleFragment,
				removeRelationFromNSide_TargetClass1SideExpectedUrlAndTitleFragment,
				removeRelationFromNSide_TargetClass1Side_TargetPath);
		ExtjsCardGrid grid1Side = ExtjsCardGrid.extract(getDriver(), context);
		List<Integer> idxs = grid1Side.getIndexOfRowsCointainingAllCells(
				GridCell.formFieldToCell(FormField.of("Code", relationToRemove.getCode())),
				GridCell.formFieldToCell(FormField.of("Floor", relationToRemove.getDescription().substring(0, 18))));
		logger.info(idxs.toString());
		assertTrue("Cannot identify removed card (not found or more than one card with the same code",
				idxs.size() == 1);
		WebElement card1Side = grid1Side.expandGridRow(idxs.get(0));
		WebElement details1Side = grid1Side.openCard(card1Side);

		sleep(context);
		List<ExtjsCardGrid.Relation> relations1side = ExtjsCardGrid
				.relationsFromGrid(grid1Side.openCardsDetailTabRelationsAndGetRelations(details1Side));
		assertFalse("Deleted relation not expected",
				relations1side.stream().anyMatch(
						r -> r.getRelationDescription().contains(removeRelationFromNSide_TypeOfInvertedRelation)
								&& normalized(r.getCode()).equals(removeRelationFromNSide_targetCardCodeField)));
		assertFalse("Relations 1:N in N side must be empty for the relation type of  the removed relation",
				relations1side.stream().anyMatch(
						r -> r.getRelationDescription().contains(removeRelationFromNSide_TypeOfInvertedRelation)));

		testEnd(context);
	}

	// REMOVE RELATION W/O REFERENCES
	// TODO adding relation directly on db because adding via UI does not work yet
	// (8/2018(

	private String rrworef_RelationName = "Map_CIDependency";
	private String rrworef_RelationNameDirect = "depends on";
	private String rrworef_RelationNameInverse = "depends on";
	private String rrworef_Class1DB = "Application";
	private String rrworef_Class2DB = "Database";

	private String[] rrworef_Class1_TargetPath = { "Software", "Applications" };
	private String[] rrworef_Class2_TargetPath = { "Software", "Databases" };
	private String rrworef_Class1ExpectedUrlAndTitleFragment = "Application";
	private String rrworef_Class2ExpectedUrlAndTitleFragment = "Database";
	private String rrworef_Class1CardCode = "CMDBuild";
	private String rrworef_Class2CardCode = "PostgreSQL02";
	private FormField rrworef_Class1FormFieldQueryTargetCard = FormField.of("Description", "vincent CMDBuild 2.3.0");
	private FormField rrworef_Class2FormFieldQueryTargetCard = FormField.of("Version", "9.3.1");
	private String rrworef_TextSearchCard2Query = "postgres";
	private List<String> rrworef_TouchedClasses = ImmutableList.of("Application", "Database", "Map_CIDependency");

	private String rrworef_sqlFetchObjIdTemplate = "select \"Id\" from \"[class]\"  where \"Code\" = ?  and \"Status\" = 'A' LIMIT 1";
	private String rrworef_sqlCheckExistingMapping = "select count(*) from \"Map_CIDependency\" where \"IdObj1\" = ? and \"IdObj2\" = ? and \"Status\" = 'A';";
	private String rrworef__sqlInsertRelationTemplate = "insert into \"[relname]\" (\"IdDomain\", \"IdClass1\" , \"IdClass2\", \"IdObj1\" , \"IdObj2\" , \"Status\" , \"User\" , \"BeginDate\") "
			+ " values ('\"[relname]\"'::regclass, '\"[domain1]\"'::regclass, '\"[domain2]\"'::regclass, ?, ?, 'A' , 'admin' , now());";
	private String rrworef_sqlFetchRelDescriptions = "SELECT obj_description(oid) as meta FROM pg_class WHERE relname = ?";

	@Test
	public void removeRelationWOReferenceTest() {

		UITestContext context = getDefaultTestContextInstance();
		context.withTouchedClasses(rrworef_TouchedClasses).withRule(getDefaultCLientLogCheckRule());

		testStart(context);
		goFullScreen();
		login(Login.admin());

		// Check if there is at least one known relation w/o reference to delete
		// (otherwise create a fresh one)

		safelyClickOnSideNavLeafWithChecksAgainst(getDriver(), rrworef_Class1ExpectedUrlAndTitleFragment,
				rrworef_Class1ExpectedUrlAndTitleFragment, rrworef_Class1_TargetPath);
		ExtjsCardGrid grid = ExtjsCardGrid.extract(getDriver(), context);
		List<Integer> idxOfTargetCard = grid
				.getIndexOfRowsCointainingAllCells(GridCell.formFieldToCell(rrworef_Class1FormFieldQueryTargetCard));
		assertTrue("Not found target card", idxOfTargetCard.size() >= 1);
		WebElement cardRow = grid.expandGridRowCheckingIfAlreadyOpen(idxOfTargetCard.get(0));
		sleep(context);
		WebElement details = grid.openCardAlt(cardRow);
		sleep(context);
		List<ExtjsCardGrid.Relation> relations = ExtjsCardGrid
				.relationsFromGrid(grid.openCardsDetailTabRelationsAndGetRelations(details));
		List<ExtjsCardGrid.Relation> removableItems = relations.stream()
				.filter(r -> r.getRelationDescription().contains(rrworef_RelationNameDirect)
						&& rrworef_Class2DB.equals(r.getType()))
				.collect(Collectors.toList());

		if (removableItems.size() == 0) {
			logger.info(
					"Adding missing relation instance for deletion: Relation: {} , Code card 1: {} ->  Code card 2: {}",
					rrworef_RelationNameDirect, rrworef_Class1CardCode, rrworef_Class2CardCode);

			getDriver().switchTo().activeElement().sendKeys(Keys.ESCAPE, Keys.ESCAPE);
			sleep(context);
			safelyClickOnSideNavLeafWithChecksAgainst(getDriver(), rrworef_Class1ExpectedUrlAndTitleFragment,
					rrworef_Class1ExpectedUrlAndTitleFragment, rrworef_Class1_TargetPath);
			grid = ExtjsCardGrid.extract(getDriver(), context);
			idxOfTargetCard = grid.getIndexOfRowsCointainingAllCells(
					GridCell.formFieldToCell(rrworef_Class1FormFieldQueryTargetCard));
			cardRow = grid.expandGridRowCheckingIfAlreadyOpen(idxOfTargetCard.get(0));
			sleep(context);
			details = grid.openCardAlt(cardRow);
			sleep(context);
			WebElement relationPanel1 = grid.openCardsDetailTabRelations(details);
			sleep(context);
			int itemsBefore = waitForVisibilityOfNestedElement(getDriver(), By.className("x-grid-item-container"),
					relationPanel1).findElements(By.tagName("table")).size();

			WebElement addDetailButton = ExtjsUtils.findElementByTestId(getDriver(), "relations-list-container-addbtn");
			String addDetailButtonPopupId = addDetailButton.getAttribute("aria-owns"); 
			addDetailButton.click();
			sleep(context);
			WebElement popupOptionsContainer = waitForElementPresence(By.id(addDetailButtonPopupId));
			List<WebElement> options = popupOptionsContainer.findElements(By.tagName("a"));
			WebElement option = options.stream().filter(a -> a.findElement(By.className("x-menu-item-text")).getText()
					.trim().startsWith(rrworef_RelationNameDirect)).findFirst().get();
			safeClick(getDriver(), option);
			sleep(context);
			
			WebElement popupAddForm = waitForElementVisibility(By.id("popup-add-relation"));
			assertTrue("add relation title should contain: " + rrworef_RelationNameDirect,
					waitForPresenceOfNestedElement(getDriver(), By.id("popup-add-relation_header-title-textEl"),
							popupAddForm).getText().contains(rrworef_RelationNameDirect));
			// FIXME No searchbox anymore
			WebElement searchBox = popupAddForm.findElements(By.tagName("input")).stream()
					.filter(i -> "search".equals(i.getAttribute("name"))).findFirst().get();
			searchBox.sendKeys(rrworef_TextSearchCard2Query);
			sleep(context);
			searchBox.sendKeys(Keys.ENTER);
			sleep(context);
			List<WebElement> itemGrid = popupAddForm.findElements(By.tagName("table"));
			List<RelationSelectionGridRow> relationsSeletionGrid = itemGrid.stream()
					.map(ig -> RelationSelectionGridRow.from(ig)).collect(Collectors.toList());
			Optional<RelationSelectionGridRow> cardToAssociate = relationsSeletionGrid.stream()
					.filter(r -> r.containsFragment(rrworef_Class2CardCode)).findFirst();
			assertTrue("Cannot find the card to associate", cardToAssociate.isPresent());
			cardToAssociate.get().checkItem();
			sleep(context);
			WebElement popupAddRelationBody = waitForElementPresence(By.id("popup-add-relation-body"));
			WebElement saveRelationButton = popupAddRelationBody.findElements(By.className("x-btn-button")).stream()
					.filter(b -> "Save".equals(b.getText())).findFirst().get();
			safeClick(getDriver(), saveRelationButton);
			sleep(context);

		}
		// now delete relation instance
		getDriver().switchTo().activeElement().sendKeys(Keys.ESCAPE, Keys.ESCAPE);

		grid = ExtjsCardGrid.extract(getDriver(), context);
		idxOfTargetCard = grid
				.getIndexOfRowsCointainingAllCells(GridCell.formFieldToCell(rrworef_Class1FormFieldQueryTargetCard));
		cardRow = grid.expandGridRowCheckingIfAlreadyOpen(idxOfTargetCard.get(0));
		sleep(context);
		details = grid.openCardAlt(cardRow);
		sleep(context);
		relations = ExtjsCardGrid.relationsFromGrid(grid.openCardsDetailTabRelationsAndGetRelations(details));
		removableItems = relations.stream().filter(r -> r.getRelationDescription().contains(rrworef_RelationNameDirect)
				&& rrworef_Class2DB.equals(r.getType())).collect(Collectors.toList());
		assertTrue("There should be at least one target item to remove. Cannot perform relation deletion...",
				removableItems.size() > 0);
		ExtjsCardGrid.Relation relationToRemove = removableItems.get(0);

		relationToRemove.remove();

		sleep(context);
		List<ExtjsCardGrid.Relation> relationsAfter = ExtjsCardGrid
				.relationsFromGrid(grid.openCardsDetailTabRelationsAndGetRelations(details));
		assertEquals("There should be one item in the grid after deletion", relations.size() - 1,
				relationsAfter.size());
		assertFalse("Relation deleted seems still present",
				relationsAfter.stream().anyMatch(r -> relationToRemove.matches(r)));

		// go to other side of relation and confirm deletion
		safelyClickOnSideNavLeafWithChecksAgainst(getDriver(), rrworef_Class2ExpectedUrlAndTitleFragment,
				rrworef_Class2ExpectedUrlAndTitleFragment, rrworef_Class2_TargetPath);
		ExtjsCardGrid gridOtherSide = ExtjsCardGrid.extract(getDriver(), context);
		List<Integer> idxs = gridOtherSide
				.getIndexOfRowsCointainingAllCells(GridCell.formFieldToCell(rrworef_Class2FormFieldQueryTargetCard));
		assertTrue("Cannot identify removed card (not found or more than one card with the same code",
				idxs.size() == 1);
		WebElement cardOtherSide = gridOtherSide.expandGridRowCheckingIfAlreadyOpen(idxs.get(0));
		WebElement detailsOtherSide = gridOtherSide.openCard(cardOtherSide);

		sleep(context);
		List<ExtjsCardGrid.Relation> relationsOtherside = ExtjsCardGrid
				.relationsFromGrid(gridOtherSide.openCardsDetailTabRelationsAndGetRelations(detailsOtherSide));
		assertFalse("Deleted relation not expected",
				relationsOtherside.stream()
						.anyMatch(r -> r.getRelationDescription().contains(rrworef_RelationNameInverse)
								&& normalized(r.getCode()).equals(rrworef_Class1CardCode)));

		testEnd(context);

	}

	// Multiple Selection Allowed When Adding Relations From 1-Side Test vars

	private String[] msa_TargetClass_TargetPath = { "Locations", "Rooms" };
	private String msa_TargetClassExpectedUrlAndTitleFragment = "Room";

	private String msa_TypeOfRelationToAdd = "contains (Configuration item)"; // must be 1:N

	@Test
	public void multipleSelectionAllowedWhenAddingRelationsFrom1SideTest() {

		UITestContext context = getDefaultTestContextInstance();
		context.withRule(getDefaultCLientLogCheckRule());
		context.withTouchedClasses(rrworef_TouchedClasses).withRule(getDefaultCLientLogCheckRule());

		testStart(context);
		goFullScreen();
		login(Login.admin());

		safelyClickOnSideNavLeafWithChecksAgainst(getDriver(), msa_TargetClassExpectedUrlAndTitleFragment,
				msa_TargetClassExpectedUrlAndTitleFragment, msa_TargetClass_TargetPath);
		ExtjsCardGrid grid = ExtjsCardGrid.extract(getDriver(), context);

		WebElement cardRow = grid.expandGridRow(0);
		WebElement details = grid.openCard(cardRow);
		sleep(context);
		WebElement relationsPanel = grid.openCardsDetailTabRelations(details);
		sleep(context);

		WebElement addRelationButton = ExtjsUtils.findElementByTestId(getDriver(), "relations-list-container-addbtn");
		String addRelationButtonPopupId = addRelationButton.getAttribute("aria-owns"); // looks like it does not
																						// change...
		addRelationButton.click();
		sleep(context);
		WebElement popupOptionsContainer = waitForElementPresence(By.id(addRelationButtonPopupId));
		List<WebElement> options = popupOptionsContainer.findElements(By.tagName("a"));
		WebElement option = options.stream().filter(a -> a.findElement(By.className("x-menu-item-text")).getText()
				.trim().startsWith(msa_TypeOfRelationToAdd)).findFirst().get();
		safeClick(getDriver(), option);

		sleep(context);
		WebElement popupAddForm = waitForElementVisibility(By.id("popup-add-relation"));
		assertTrue("add relation title should contain: " + msa_TypeOfRelationToAdd,
				waitForPresenceOfNestedElement(getDriver(), By.id("popup-add-relation_header-title-textEl"),
						popupAddForm).getText().contains(msa_TypeOfRelationToAdd));
		sleep(context);
		List<WebElement> itemGrid = popupAddForm.findElements(By.tagName("table"));
		List<RelationSelectionGridRow> relationsSeletionGrid = itemGrid.stream()
				.map(ig -> RelationSelectionGridRow.from(ig)).collect(Collectors.toList());
		List<ExtjsCardGrid.Relation> relations = ExtjsCardGrid.relationsFromGrid(itemGrid);
		List<String> codesList = new ArrayList<>();

		for (int i = 0; i < 13; i++) {
			if (!relationsSeletionGrid.get(i).isDisabled()) {
				relationsSeletionGrid.get(i).checkItem();
				String relationCode = relations.get(i).getDescription();
				codesList.add(relationCode);
				sleep(context);
			}
		}

		ExtjsUtils.findElementByTestId(getDriver(),"relations-list-add-gridcontainer-save").click();
		sleep(context);

		// deleting relations added

		logger.info(codesList.toString());

		for (String code : codesList) {
			Object relationId = given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
					.get(buildRestV3Url("classes/Room/cards/6016/relations")).then().statusCode(200).extract()
					.jsonPath().getList(("data.findAll{ it._destinationCode == '" + code + "'}._id")).get(0);

			given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
					.delete(buildRestV3Url("classes/Room/cards/6016/relations/" + relationId)).then().statusCode(200);

		}

		testEnd(context);
	}

	@Test
	public void existingDomainsAreShownTest() {

		UITestContext context = getDefaultTestContextInstance();

		context.withRule(UITestRule.defineClientLogCheckRule("Offline test, please remove",
				ImmutableList.of("40", "http"), null, null));
		testStart(context);
		goFullScreen();
		login(Login.admin());

		safelyClickOnSideNavLeafWithChecksAgainst(getDriver(), "Building", "cards Building", "Locations", "Buildings");
		ExtjsCardGrid grid = ExtjsCardGrid.extract(getDriver(), context);

		WebElement cardRow = grid.expandGridRow(0);
		WebElement details = grid.openCard(cardRow);
		sleep(context);
		WebElement relationsPanel = grid.openCardsDetailTabRelations(details);
		sleep(context);

		WebElement addRelationButton = ExtjsUtils.findElementByTestId(getDriver(), "relations-list-container-addbtn");
		String addRelationButtonPopupId = addRelationButton.getAttribute("aria-owns"); // looks like it does not
																						// change...
		addRelationButton.click();

		sleep(context);
		WebElement popupOptionsContainer = waitForElementPresence(By.id(addRelationButtonPopupId));
		List<WebElement> options = popupOptionsContainer.findElements(By.tagName("a"));
		List<String> domainsForClass = options.stream()
				.map(a -> a.findElement(By.className("x-menu-item-text")).getText().trim())
				.collect(Collectors.toList());
		logger.info("Found the following allowed domains for the class: {}", domainsForClass);

		List<String> domainList = given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
				.get(buildRestV3Url("domains")).then().statusCode(200).extract().jsonPath().getList(("data._id"));

		List<String> domainsFromWs = new ArrayList<>();

		for (String _id : domainList) {
			String source = given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
					.get(buildRestV3Url("domains/" + _id)).then().statusCode(200).extract().jsonPath()
					.getString(("data.source"));

			Boolean active = given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
					.get(buildRestV3Url("domains/" + _id)).then().statusCode(200).extract().jsonPath()
					.getBoolean(("data.active"));

			if (source.contains("Building") && active)
				domainsFromWs.add(source);

		}
		assertTrue(domainsFromWs.size() == domainsForClass.size());

		testEnd(context);
	}

	// EXISTING RELATIONS ARE SHOWN TEST vars

	private String[] existingRelations_TargetClass_TargetPath = { "Employees", "All employees" };
	private String existingRelations_TargetClassExpectedUrlAndTitleFragment = "Employee";
	private String existingRelations_TargetClassDBName = "Employee";
	private String existingRelations_CardIdField = "Code";


	@Test
	public void existingRelationsAreShownTest1() {

		UITestContext context = getDefaultTestContextInstance();
		String classId = "InternalEmployee";

		context.withRule(UITestRule.defineClientLogCheckRule("Offline test, please remove",
				ImmutableList.of("40", "http"), null, null));
		testStart(context);
		goFullScreen();
		login(Login.admin());

		safelyClickOnSideNavLeafWithChecksAgainst(getDriver(), "InternalEmployee", "cards Internal employee",
				"Employees", "Internal employees");

		List<Object> allCardsIds = given().header(CMDBUILD_AUTH_HEADER, getSessionToken())
				.get(buildRestV3Url("classes/" + classId + "/cards")).then().statusCode(200).extract().jsonPath()
				.getList("data._id");

		Object random = allCardsIds.get(new Random().nextInt(allCardsIds.size()));

		String code = given().header(CMDBUILD_AUTH_HEADER, getSessionToken())
				.get(buildRestV3Url("classes/" + classId + "/cards/" + random)).then().statusCode(200).extract()
				.jsonPath().getString("data.Code");

		ExtjsCardGrid grid = ExtjsCardGrid.extract(getDriver(), context);

		List<Integer> list = grid.getIndexOfRowsCointainingAllCells(GridCell.from("Code", code));

		WebElement cardRow = grid.expandGridRowCheckingIfAlreadyOpen(list.get(0));
		WebElement details = grid.openCardAlt(cardRow);
		sleep(context);
		WebElement relationsPanel = grid.openCardsDetailTabRelations(details);
		sleep(context);

		List<WebElement> itemGrid = getDriver().switchTo().activeElement()
				.findElements(By.xpath("//div[contains(@class, 'x-panel-relationslist')]//table"));
		List<ExtjsCardGrid.Relation> relations = ExtjsCardGrid.relationsFromGrid(itemGrid);
		List<String> relationsDescriptionsUI = new ArrayList<>();

		int numberOfRelationsUI = relations.size();

		for (int i = 0; i < numberOfRelationsUI; i++) {
			String a = relations.get(i).getDescription();
			relationsDescriptionsUI.add(a);
		}

		List<String> relationsDescriptionsRest = given().header(CMDBUILD_AUTH_HEADER, getSessionToken())
				.get(buildRestV3Url("classes/" + classId + "/cards/" + random + "/relations")).then().statusCode(200)
				.extract().jsonPath().getList("data._destinationDescription");

		relationsDescriptionsRest = relationsDescriptionsRest.stream().map(String::trim).collect(Collectors.toList());

		for (String descriptionRest : relationsDescriptionsRest) {
			assertTrue(relationsDescriptionsUI.contains(descriptionRest));
		}

		assertTrue(relationsDescriptionsRest.size() == relationsDescriptionsUI.size());

		testEnd(context);
	}

	@Test
	public void existingRelationsAreShownTest2() {

		UITestContext context = getDefaultTestContextInstance();
		String classId = "Supplier";

		context.withRule(UITestRule.defineClientLogCheckRule("Offline test, please remove",
				ImmutableList.of("40", "http"), null, null));
		testStart(context);
		goFullScreen();
		login(Login.admin());

		safelyClickOnSideNavLeafWithChecksAgainst(getDriver(), "Supplier", "cards Supplier", "Suppliers", "Suppliers");

		List<Object> allCardsIds = given().header(CMDBUILD_AUTH_HEADER, getSessionToken())
				.get(buildRestV3Url("classes/" + classId + "/cards")).then().statusCode(200).extract().jsonPath()
				.getList("data._id");

		Object random = allCardsIds.get(new Random().nextInt(allCardsIds.size()));

		String code = given().header(CMDBUILD_AUTH_HEADER, getSessionToken())
				.get(buildRestV3Url("classes/" + classId + "/cards/" + random)).then().statusCode(200).extract()
				.jsonPath().getString("data.Code");

		ExtjsCardGrid grid = ExtjsCardGrid.extract(getDriver(), context);

		List<Integer> list = grid.getIndexOfRowsCointainingAllCells(GridCell.from("Code", code));

		WebElement cardRow = grid.expandGridRowCheckingIfAlreadyOpen(list.get(0));
		sleep(context);
		WebElement details = grid.openCardAlt(cardRow);
		sleep(context);
		WebElement relationsPanel = grid.openCardsDetailTabRelations(details);
		sleep(context);

		List<WebElement> itemGrid = getDriver().switchTo().activeElement()
				.findElements(By.xpath("//div[contains(@class, 'x-panel-relationslist')]//table"));
		List<ExtjsCardGrid.Relation> relations = ExtjsCardGrid.relationsFromGrid(itemGrid);
		List<String> relationsDescriptionsUI = new ArrayList<>();

		int numberOfRelationsUI = relations.size();

		for (int i = 0; i < numberOfRelationsUI; i++) {
			String a = relations.get(i).getDescription();
			relationsDescriptionsUI.add(a);
		}

		List<String> relationsDescriptionsRest = given().header(CMDBUILD_AUTH_HEADER, getSessionToken())
				.get(buildRestV3Url("classes/" + classId + "/cards/" + random + "/relations")).then().statusCode(200)
				.extract().jsonPath().getList("data._destinationDescription");

		relationsDescriptionsRest = relationsDescriptionsRest.stream().map(String::trim).collect(Collectors.toList());

		for (String descriptionRest : relationsDescriptionsRest) {
			assertTrue(relationsDescriptionsUI.contains(descriptionRest));
		}

		assertTrue(relationsDescriptionsRest.size() == relationsDescriptionsUI.size());

		testEnd(context);
	}

	@Test
	public void existingRelationsAreShownTest3() {

		UITestContext context = getDefaultTestContextInstance();
		String classId = "Notebook";

		context.withRule(UITestRule.defineClientLogCheckRule("Offline test, please remove",
				ImmutableList.of("40", "http"), null, null));
		testStart(context);
		goFullScreen();
		login(Login.admin());

		safelyClickOnSideNavLeafWithChecksAgainst(getDriver(), "Notebook", "cards Notebook", "Workplaces", "Notebooks");

		List<Object> allCardsIds = given().header(CMDBUILD_AUTH_HEADER, getSessionToken())
				.get(buildRestV3Url("classes/" + classId + "/cards")).then().statusCode(200).extract().jsonPath()
				.getList("data._id");

		Object random = allCardsIds.get(new Random().nextInt(allCardsIds.size()));

		String code = given().header(CMDBUILD_AUTH_HEADER, getSessionToken())
				.get(buildRestV3Url("classes/" + classId + "/cards/" + random)).then().statusCode(200).extract()
				.jsonPath().getString("data.Code");

		ExtjsCardGrid grid = ExtjsCardGrid.extract(getDriver(), context);

		List<Integer> list = grid.getIndexOfRowsCointainingAllCells(GridCell.from("Code", code));

		WebElement cardRow = grid.expandGridRowCheckingIfAlreadyOpen(list.get(0));
		sleep(context);
		WebElement details = grid.openCardAlt(cardRow);
		sleep(context);
		WebElement relationsPanel = grid.openCardsDetailTabRelations(details);
		sleep(context);

		List<WebElement> itemGrid = getDriver().switchTo().activeElement()
				.findElements(By.xpath("//div[contains(@class, 'x-panel-relationslist')]//table"));
		List<ExtjsCardGrid.Relation> relations = ExtjsCardGrid.relationsFromGrid(itemGrid);
		List<String> relationsDescriptionsUI = new ArrayList<>();

		int numberOfRelationsUI = relations.size();

		for (int i = 0; i < numberOfRelationsUI; i++) {
			String a = relations.get(i).getDescription();
			relationsDescriptionsUI.add(a);
		}

		List<String> relationsDescriptionsRest = given().header(CMDBUILD_AUTH_HEADER, getSessionToken())
				.get(buildRestV3Url("classes/" + classId + "/cards/" + random + "/relations")).then().statusCode(200)
				.extract().jsonPath().getList("data._destinationDescription");

		relationsDescriptionsRest = relationsDescriptionsRest.stream().map(String::trim).collect(Collectors.toList());

		for (String descriptionRest : relationsDescriptionsRest) {
			assertTrue(relationsDescriptionsUI.contains(descriptionRest));
		}

		assertTrue(relationsDescriptionsRest.size() == relationsDescriptionsUI.size());

		testEnd(context);
	}
	// Edit relation With Reference Test vars
	// Rack-> PhisicalServer(PhisicalServer.Rack)

	private String editRelWRef_RelationNameDirect = "is placed in";
	private String editRelWRef_RelationNameInverse = "contains";
	private String editRelWRef_RelationDBName = "Map_RackServerHW";

	private String[] editRelWRef_Class1_TargetPath = { "Infrastructures", "Physical servers" };
	private String[] editRelWRef_Class2_TargetPath = { "Infrastructures", "Racks" };
	private String editRelWRef_Class1ExpectedTitleFragment = "Physical server";
	private String editRelWRef_Class1ExpectedURLFragment = "PhysicalServer";
	private String editRelWRef_Class2ExpectedUrlAndTitleFragment = "Rack";
	private String editRelWRef_Class2DBClassName = "Rack";
	private String editRelWRef_Class1DBClassName = "PhysicalServer";
	private String editRelWRef_Class1CardCode = "23PSL55";
	private String editRelWRef_Class2CardCodePreviousRelationTarget = "R66M5";
	private String editRelWRef_Class2CardCodeNewRelationTarget = "R66M6";

	private List<FormField> editRelWRef_Class1FFQueryTargetCard = ImmutableList
			.of(FormField.of("Code", editRelWRef_Class1CardCode));
	private List<FormField> editRelWRef_Class2FFQueryTargetPreviousRelationCard = ImmutableList
			.of(FormField.of("Code", editRelWRef_Class2CardCodePreviousRelationTarget));
	private List<FormField> editRelWRef_Class2FFQueryTargetNewRelationCard = ImmutableList
			.of(FormField.of("Code", editRelWRef_Class2CardCodeNewRelationTarget));

	private String editRelWRef_SqlFetchCardIdTemplate = "select \"Id\" from \"[class]\" where \"code\" = ? and \"Status\" = 'A' limit 1";
	private String editRelWRef_SqlCheckRelationExistsTemplate = "select count(*) from \"[relation]\" where \"IdObj2\" = ? and \"Status\" = 'A'";
	private List<String> editRelWRef_TouchedClasses = ImmutableList.of(editRelWRef_Class1DBClassName,
			editRelWRef_Class2DBClassName, editRelWRef_RelationDBName);

	@Test
	@Ignore
	public void editRelationWReference() {

		UITestContext context = getDefaultTestContextInstance();

		context.withRule(UITestRule.defineClientLogCheckRule("Detect http status 500 (known bug)", null,
				ImmutableList.of("500"), null));
		context.withTouchedClasses(editRelWRef_TouchedClasses);

		testStart(context);
		goFullScreen();
		login(Login.admin());
		safelyClickOnSideNavLeafWithChecksAgainst(getDriver(), editRelWRef_Class1ExpectedURLFragment,
				editRelWRef_Class1ExpectedTitleFragment, editRelWRef_Class1_TargetPath);
		ExtjsCardGrid grid1 = ExtjsCardGrid.extract(getDriver(), context);
		List<Integer> idxOfTargetCard = grid1
				.getIndexOfRowsCointainingAllCells(GridCell.formfieldsToCellArray(editRelWRef_Class1FFQueryTargetCard));

		assertTrue("Not found target card, class: " + editRelWRef_Class1ExpectedTitleFragment + " code: "
				+ editRelWRef_Class1CardCode, idxOfTargetCard.size() >= 1);
		WebElement cardRow = grid1.expandGridRow(idxOfTargetCard.get(0));
		WebElement details1 = grid1.openCard(cardRow);
		waitForLoad();
		sleep(2000);
		List<ExtjsCardGrid.Relation> relations = ExtjsCardGrid
				.relationsFromGrid(grid1.openCardsDetailTabRelationsAndGetRelations(details1));
		Optional<ExtjsCardGrid.Relation> relation2edit = relations.stream()
				.filter(r -> r.getRelationDescription().contains(editRelWRef_RelationNameDirect)
						&& editRelWRef_Class2CardCodePreviousRelationTarget.equals(r.getCode()))
				.findFirst();
		assertTrue("Impossible to find relation to edit", relation2edit.isPresent());
		WebElement popupEditRelation = relation2edit.get().editRelation();
		assertTrue("edit  relation title should contain: " + editRelWRef_RelationNameDirect,
				waitForPresenceOfNestedElement(getDriver(), By.id("popup-edit-relation_header-title-textEl"),
						popupEditRelation).getText().contains(editRelWRef_RelationNameDirect));

		// extract selectable relations instances
		List<WebElement> itemGrid = popupEditRelation.findElements(By.tagName("table"));
		List<RelationSelectionGridRow> relationsSeletionGrid = itemGrid.stream()
				.map(ig -> RelationSelectionGridRow.from(ig)).collect(Collectors.toList());
		Optional<RelationSelectionGridRow> cardToAssociate = relationsSeletionGrid.stream()
				.filter(r -> r.contains(editRelWRef_Class2CardCodeNewRelationTarget)).findFirst();
		assertTrue("Cannot find the card to associate", cardToAssociate.isPresent());

		// select card
		assertFalse("Card to associate must not be already selected (in a relation with current card)",
				cardToAssociate.get().initiallySelected);
		cardToAssociate.get().checkItem();
		artificialSleep(200, context);
		WebElement popupEditRelationBody = waitForElementPresence(UILocators.locatorEditRelationsPopup());
		WebElement saveRelationButton = popupEditRelationBody.findElements(By.className("x-btn-button")).stream()
				.filter(b -> "Save".equals(b.getText())).findFirst().get();
		WebElement cancelRelationButton = popupEditRelationBody.findElements(By.className("x-btn-button")).stream()
				.filter(b -> "Cancel".equals(b.getText())).findFirst().get();

		safeClick(saveRelationButton);
		waitForLoad();
		artificialSleep(2000, context);

		// Workaround to manage #532 (popup remains hung when a server side error occurs
		// when saving)
		// or search for element with id = popup-edit-relation_header-title
		Optional<WebElement> editPopupStillOpen = fetchOptionalVisibleElement(getDriver(),
				By.id("popup-edit-relation_header-title"), 1);
		if (editPopupStillOpen.isPresent()) {
			Optional<WebElement> clickableCancelButton = fetchOptionalClickableWebElement(getDriver(),
					cancelRelationButton);
			if (((Optional) clickableCancelButton).isPresent())
				safeClick(getDriver(), clickableCancelButton.get());
			else // this seems not to work
				getDriver().switchTo().activeElement().sendKeys(Keys.ESCAPE, Keys.ESCAPE); // really needed when popup
																							// is not closed due to a
																							// server side error
		}
		// artificialSleep(1500, context);
		// safelylickOnSideNavLeaf(getDriver(),"Locations", "Room");

		// check that new relation has changed.
		// 1. Checking on the other side of relation that the new target is now
		// associated to the card

		safelyClickOnSideNavLeafWithChecksAgainst(getDriver(), editRelWRef_Class2ExpectedUrlAndTitleFragment,
				editRelWRef_Class2ExpectedUrlAndTitleFragment, editRelWRef_Class2_TargetPath);
		ExtjsCardGrid grid2 = ExtjsCardGrid.extract(getDriver(), context);
		List<Integer> idxOfTargetCard2 = grid2.getIndexOfRowsCointainingAllCells(
				GridCell.formfieldsToCellArray(editRelWRef_Class2FFQueryTargetNewRelationCard));
		List<Integer> idxOfCardThatLostRelationship = grid2.getIndexOfRowsCointainingAllCells(
				GridCell.formfieldsToCellArray(editRelWRef_Class2FFQueryTargetPreviousRelationCard));
		assertTrue("Not found target card on side 2", idxOfTargetCard2.size() >= 1);
		WebElement cardRow2 = grid2.expandGridRow(idxOfTargetCard2.get(0));
		WebElement details2 = grid2.openCard(cardRow2);
		waitForLoad();
		sleep(2000);
		List<ExtjsCardGrid.Relation> relations2 = ExtjsCardGrid
				.relationsFromGrid(grid2.openCardsDetailTabRelationsAndGetRelations(details2));
		assertTrue("Relation tentatively  added must not be shown on second (N) side item relations ",
				relations2.stream().filter(r -> (r.getRelationDescription().contains(editRelWRef_RelationNameInverse))
						&& (editRelWRef_Class1CardCode.equals(r.getCode()))).count() < 1);

		// 2. Checking on the other side of relation that the oldtarget is no more
		// associated to the card
		// safelyClickOnSideNavLeafWithChecksAgainst(getDriver(),
		// editRelWRef_Class2ExpectedUrlAndTitleFragment,
		// editRelWRef_Class2ExpectedUrlAndTitleFragment,
		// editRelWRef_Class2_TargetPath);
		assertTrue("Not found card that lost the relationship", idxOfCardThatLostRelationship.size() > 0);
		getDriver().switchTo().activeElement().sendKeys(Keys.ESCAPE, Keys.ESCAPE); // really needed when popup is
		grid2 = ExtjsCardGrid.extract(getDriver(), context);
		idxOfCardThatLostRelationship = grid2.getIndexOfRowsCointainingAllCells(
				GridCell.formfieldsToCellArray(editRelWRef_Class2FFQueryTargetPreviousRelationCard));
		cardRow2 = grid2.expandGridRow(idxOfCardThatLostRelationship.get(0));
		details2 = grid2.openCardAlt(cardRow2);
		waitForLoad();
		sleepDeprecated(2000);
		relations2 = ExtjsCardGrid.relationsFromGrid(grid2.openCardsDetailTabRelationsAndGetRelations(details2));
		assertTrue("Card that lost relationship should no more be present in relations list ",
				relations2.stream().filter(r -> (r.getRelationDescription().contains(editRelWRef_RelationNameInverse))
						&& (editRelWRef_Class1CardCode.equals(r.getCode()))).count() < 1);

		// check on direct side
		safelyClickOnSideNavLeafWithChecksAgainst(getDriver(), editRelWRef_Class1ExpectedURLFragment,
				editRelWRef_Class1ExpectedTitleFragment, editRelWRef_Class1_TargetPath);
		grid1 = ExtjsCardGrid.extract(getDriver(), context);
		cardRow = grid1.expandGridRow(grid1
				.getIndexOfRowsCointainingAllCells(GridCell.formfieldsToCellArray(editRelWRef_Class1FFQueryTargetCard))
				.get(0));
		WebElement detailsAfter = grid1.openCard(cardRow);
		List<ExtjsCardGrid.Relation> relationsAfter = ExtjsCardGrid
				.relationsFromGrid(grid1.openCardsDetailTabRelationsAndGetRelations(detailsAfter));
		assertTrue(relationsAfter.stream()
				.anyMatch(r -> r.getRelationDescription().contains(editRelWRef_RelationNameDirect)
						&& r.getCode().equals(editRelWRef_Class2CardCodeNewRelationTarget)));
		assertTrue(relationsAfter.stream()
				.noneMatch(r -> r.getRelationDescription().contains(editRelWRef_RelationNameDirect)
						&& r.getCode().equals(editRelWRef_Class2CardCodePreviousRelationTarget)));

		// check db
		Integer idCardPrevious = getJdbcTemplate().queryForObject(
				editRelWRef_SqlFetchCardIdTemplate.replace("[class]", editRelWRef_Class2DBClassName), Integer.class,
				editRelWRef_Class2CardCodePreviousRelationTarget);
		Integer idCardNew = getJdbcTemplate().queryForObject(
				editRelWRef_SqlFetchCardIdTemplate.replace("[class]", editRelWRef_Class2DBClassName), Integer.class,
				editRelWRef_Class2CardCodeNewRelationTarget);
		Integer relationInstancesCardNew = getJdbcTemplate().queryForObject(
				editRelWRef_SqlCheckRelationExistsTemplate.replace("[relation]", editRelWRef_RelationDBName),
				Integer.class, idCardNew);
		Integer relationInstancesCardPrevious = getJdbcTemplate().queryForObject(
				editRelWRef_SqlCheckRelationExistsTemplate.replace("[relation]", editRelWRef_RelationDBName),
				Integer.class, idCardPrevious);
		assertEquals("There should be exactly one instance of relation instance for the newly associated card...",
				relationInstancesCardNew == 1);
		assertEquals(
				"There should no more be active instances of relation instance for the previously associated card...",
				relationInstancesCardPrevious == 0);

		testEnd(context);
	}

	// Edit relation Without Reference Test vars
	// TODO choose classes
	// ci->swinstance->swserver provare a usare una N:N (1 percheé non sono coperte
	// finora nei test, due perché tutte le 1:N hanno reference)
	// Physical Server has (Server software) which is instance of and
	private String editRelWORef_RelationNameDirectInRelationGrid = "has";
	private String editRelWORef_RelationNameDirectInAddRelationDropDown = "has (Server software)";
	private List<String> editRelWORef_Side2SubClasses = ImmutableList.of("Application", "InfrastructureSW", "Database");
	private String editRelWORef_RelationNameInverseUI = "is on execution on"; // TODO check if truly so in UI
	private String editRelWORef_RelationDBName = "Map_ServerSWServer";

	private String[] editRelWORef_Class1_TargetPath = { "Infrastructures", "Physical servers" };
	private String editRelWORef_Class1ExpectedTitleFragment = "Physical server";
	private String editRelWORef_Class1ExpectedURLFragment = "PhysicalServer";
	private String editRelWORef_Class2DBClassName = "ServerSW";
	private String editRelWORef_Class1DBClassName = "PhysicalServer";

	@Test // KNOWN TO FAIL due to #532
	@Ignore
	public void editRelationWOReference() {

		UITestContext context = getDefaultTestContextInstance();

		context.withRule(UITestRule.defineClientLogCheckRule("Detect http status 500 (known bug)", null,
				ImmutableList.of("500"), null));

		testStart(context);
		goFullScreen();
		login(Login.admin());
		safelyClickOnSideNavLeafWithChecksAgainst(getDriver(), editRelWORef_Class1ExpectedURLFragment,
				editRelWORef_Class1ExpectedTitleFragment, editRelWORef_Class1_TargetPath);
		ExtjsCardGrid grid = ExtjsCardGrid.extract(getDriver(), context);

		assertTrue("At least one card is needed to perform the test with...", grid.rows() > 0);
		List<GridCell> workingCardContent = grid.getRows().get(0); // we need it lator to locate the card
		WebElement cardRow = grid.expandGridRow(0);
		WebElement cardDetails = grid.openCardAlt(cardRow);
		waitForLoad();
		sleep(2000);
		List<ExtjsCardGrid.Relation> relations = ExtjsCardGrid
				.relationsFromGrid(grid.openCardsDetailTabRelationsAndGetRelations(cardDetails));
		// Check if there is a relation to edit (of the right domain)
		Optional<ExtjsCardGrid.Relation> relation2edit = relations.stream()
				.filter(r -> r.getRelationDescription().contains(editRelWORef_RelationNameDirectInRelationGrid))
				.filter(r -> editRelWORef_Side2SubClasses.contains(r.getType())).findFirst();

		if (!relation2edit.isPresent()) {
			// TODO complete
			// we must create a relation instance before editing it...
			getDriver().switchTo().activeElement().sendKeys(Keys.ESCAPE, Keys.ESCAPE);
			// grid.refresh();
			WebElement relationsTabWE = grid.openCardsDetailTabRelations(cardDetails);
			sleep(2000);
			List<WebElement> itemGrid = relationsTabWE.findElements(By.tagName("table"));
			List<RelationSelectionGridRow> relationsSeletionGrid = itemGrid.stream()
					.map(ig -> RelationSelectionGridRow.from(ig)).collect(Collectors.toList());
			Optional<RelationSelectionGridRow> cardToAssociate = relationsSeletionGrid.stream()
					.filter(r -> !(r.isDisabled() || r.isInitiallySelected())).findFirst();
			assertTrue("Cannot find a card to associate", cardToAssociate.isPresent());
			cardToAssociate.get().checkItem();
			artificialSleep(300, context);
			WebElement popupAddRelationBody = waitForElementPresence(By.id("popup-add-relation-body"));
			WebElement saveRelationButton = popupAddRelationBody.findElements(By.className("x-btn-button")).stream()
					.filter(b -> "Save".equals(b.getText())).findFirst().get();
			safeClick(getDriver(), saveRelationButton);
			waitForLoad();
			artificialSleep(2000);
			getDriver().switchTo().activeElement().sendKeys(Keys.ESCAPE, Keys.ESCAPE);
			grid.refresh();
		}

		WebElement popupEditRelation = relation2edit.get().editRelation();
		List<WebElement> linkableItemsGridWE = popupEditRelation.findElements(By.tagName("table"));
		List<RelationSelectionGridRow> linkableItemsGrid = linkableItemsGridWE.stream()
				.map(i -> RelationSelectionGridRow.from(i)).collect(Collectors.toList());
		assertTrue("No more than one element must be selected",
				linkableItemsGrid.stream().filter(i -> i.isInitiallySelected()).count() <= 1);
		assertTrue("There should  not be a disabled item which is also not selected",
				linkableItemsGrid.stream().noneMatch(i -> i.isDisabled() && (!i.isInitiallySelected())));
		Optional<RelationSelectionGridRow> firstNotLinkedSelectableItem = linkableItemsGrid.stream()
				.filter(i -> !(i.isDisabled() || i.isInitiallySelected())).findFirst();
		assertTrue("We need at least one selectable item to continue the test",
				firstNotLinkedSelectableItem.isPresent());
		// switch item involved in relation
		RelationSelectionGridRow itemSwitchToRelationSelection = firstNotLinkedSelectableItem.get();
		List<String> itemSwitchToContent = itemSwitchToRelationSelection.getContent();
		itemSwitchToRelationSelection.checkItem();
		WebElement saveRelationButton = popupEditRelation.findElements(By.className("x-btn-button")).stream()
				.filter(b -> "Save".equals(b.getText())).findFirst().get();
		safeClick(saveRelationButton);

		// At this point some error can raise, so it is prudential to exit and reenter
		// the card
		getDriver().switchTo().activeElement().sendKeys(Keys.ESCAPE, Keys.ESCAPE);

		safelyClickOnSideNavLeafWithChecksAgainst(getDriver(), editRelWORef_Class1ExpectedURLFragment,
				editRelWORef_Class1ExpectedTitleFragment, editRelWORef_Class1_TargetPath);
		ExtjsCardGrid gridAfter = ExtjsCardGrid.extract(getDriver(), context);
		List<Integer> workingCardIdxAfter = gridAfter
				.getIndexOfRowsCointainingAllCells(workingCardContent.toArray(new GridCell[workingCardContent.size()]));
		assertEquals("Impossible to identify working card: no one or to many (>1) found", 1,
				workingCardIdxAfter.size());
		WebElement cardRowAfter = gridAfter.expandGridRow(workingCardIdxAfter.get(0));
		WebElement cardDetailsAfter = gridAfter.openCardAlt(cardRowAfter);
		List<WebElement> relationsAfterWE = gridAfter.openCardsDetailTabRelationsAndGetRelations(cardDetailsAfter);
		List<ExtjsCardGrid.Relation> relationsAfter = ExtjsCardGrid.relationsFromGrid(relationsAfterWE);
		// check new relation is present in working card relation grid
		assertTrue("Freshly linked card must be present in relations of card which had relation edited...",
				relationsAfter.stream().anyMatch(
						r -> (r.getRelationDescription().startsWith(editRelWORef_RelationNameDirectInRelationGrid)
								&& editRelWORef_Side2SubClasses.contains(r.getType())
								&& itemSwitchToContent.contains(r.getCode())
								&& itemSwitchToContent.contains(r.getDescription()))));
		// ...check also edited relation is no more present
		assertTrue("", relationsAfter.stream()
				.noneMatch(r -> (r.getRelationDescription().startsWith(editRelWORef_RelationNameDirectInRelationGrid)
						&& relation2edit.get().getType().equals(r.getType())
						&& relation2edit.get().getCode().equals(r.getCode())
						&& relation2edit.get().getDescription().equals(r.getDescription()))));

		testEnd(context);

	}

	public void addARelation(String code, String relationType) {

		sleep(getDefaultTestContextInstance());
		WebElement addDetailButton2 = ExtjsUtils.findElementByTestId(getDriver(), "relations-list-container-addbtn");
		String addDetailButtonPopupId2 = addDetailButton2.getAttribute("aria-owns");
		addDetailButton2.click();
		sleep(getDefaultTestContextInstance());

		WebElement popupOptionsContainer2 = waitForElementPresence(By.id(addDetailButtonPopupId2));
		List<WebElement> options2 = popupOptionsContainer2.findElements(By.tagName("a"));
		WebElement option2 = options2.stream()
				.filter(a -> a.findElement(By.className("x-menu-item-text")).getText().trim().startsWith(relationType))
				.findFirst().get();
		safeClick(getDriver(), option2);
		sleep(getDefaultTestContextInstance());

		WebElement popupAddForm = waitForElementVisibility(By.id("popup-add-relation"));
		assertTrue("add relation title should contain: " + relationType, waitForPresenceOfNestedElement(getDriver(),
				By.id("popup-add-relation_header-title-textEl"), popupAddForm).getText().contains(relationType));

		List<WebElement> itemGrid = popupAddForm.findElements(By.tagName("table"));
		WebElement cardToAssociate = itemGrid.stream().filter((element) -> element.getText().contains(code)).findFirst()
				.orElse(null);

		if (!isVisibleInViewport(cardToAssociate))
			((JavascriptExecutor) getDriver()).executeScript("arguments[0].scrollIntoView(true);", cardToAssociate);

		sleep(getDefaultTestContextInstance());

		WebElement checkBox = cardToAssociate.findElement(By.className("x-grid-checkcolumn"));
		checkBox.click();

		ExtjsUtils.findElementByTestId(getDriver(), "relations-list-add-gridcontainer-save").click();
		sleep(getDefaultTestContextInstance());
	}

}
