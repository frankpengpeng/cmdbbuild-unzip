package org.cmdbuild.test.web;

import static org.cmdbuild.test.web.utils.ExtjsUtils.*;
import static org.junit.Assert.*;
import static java.lang.Math.max;
import static com.google.common.base.Strings.*;
import static org.junit.Assert.*;
import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.hasKey;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.cmdbuild.client.rest.model.ClassData;
import org.cmdbuild.test.web.utils.*;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.dao.DataAccessException;

import javax.annotation.Nonnull;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

//TODO: try enforce stricter ClientLogRules (404, see #556)

public class AdministrationClassesBasicsIT extends BaseWebIT {

	@After
	public void cleanup() {
		cleanupDB();
	}

	// FIXME: implement context.withAddedClasses

	// FIXME to be moved to UILocators
	By locatorAddClassButton = By.xpath("//div[@data-testid='administration-class-toolbar-addClassBtn']");
	By locatorRemoveClassButton = By.xpath("//div[@data-testid='administration-class-properties-tool-deletebtn']");
	By locatorEditClassButton = By.xpath("//div[@data-testid='administration-class-properties-tool-editbtn']");
	// data-testid="administration-class-toolbar-addClassBtn

	// SHARED VARS (between tests)
	/**
	 * use when you need a path but you don't care which one is it
	 */
	String[] defaultTreePath = { "Classes", "Standard", "Building" };
	String defaultClassNameToAdd = "Test";
	String sqlQueryThatFailsIfClassDoesNotExists = "select count (*) from \"[class]\"";
	String sqlQueryNumberOfActiveCardsInClassFailingIfClassDoesNotExists = "select count (*) from \"[class]\" where \"Status\" = 'A'";

	// Add Standard class test vars
	String addStd_ClassNameToAdd = defaultClassNameToAdd + RandomStringUtils.randomAlphabetic(3);
	String[] addStd_ManagementAddedClassTreePath = { "All items", "Classes", "Standard", addStd_ClassNameToAdd };
	String[] addStd_AdminAddedClassTreePath = { "Classes", "Standard", addStd_ClassNameToAdd };

	@Test
	public void addStandardClassTest() {

		// prerequisite: check absence of class to add on db
		assertFalse("Random class name already present, cannot perform test. This is a one time issue.",
				getNumberOfRowsInTableIfTableExists(addStd_ClassNameToAdd).isPresent());

		UITestContext context = getDefaultTestContextInstance().withCreatedClass(addStd_ClassNameToAdd);
		context.withRule(UITestRule.defineClientLogCheckRule(
				"Tolerate a lot of noise (401, 404, blob, constructor) Rule",
				ImmutableList.of("400", "404", "401", "blob", "constructor", "Cannot read property"), null, null));

		testStart(context);
		goFullScreen();
		login(Login.admin());
		sleep(context);

		ExtjsUtils.switchToAdminModule2(getDriver(), context);

		sleep(context);

		ExtjsNavigationMenu.extract(getDriver()).selectAtPath(defaultTreePath);

		sleep(context);

		openAddClassDialog(context);
		sleep(context);
		assertTrue("Add class dialog should be already open", getAddClassPropertiesPanel().isPresent());
		WebElement addClassPropertiesPanel = getAddClassPropertiesPanel().get();
		addClassPropertiesPanel.findElement(By.name("description")).sendKeys(addStd_ClassNameToAdd);
		addClassPropertiesPanel.findElement(By.name("classnamefieldadd")).sendKeys(addStd_ClassNameToAdd);
		sleep(context);

		assertFalse("Save button must be enabled  after filling all required fields",
				isButtonDisabled(getAddClassSaveButton()));
		getAddClassSaveButton().click();
		logout();
		sleep(context);

		assertTrue("The table: " + addStd_ClassNameToAdd + " must exist",
				getNumberOfRowsInTableIfTableExists(addStd_ClassNameToAdd).isPresent());
		assertEquals("The table: " + addStd_ClassNameToAdd + " contain no record", new Integer(0),
				getNumberOfRowsInTableIfTableExists(addStd_ClassNameToAdd).get());

		login(Login.admin());
		sleep(context);

		safelyClickOnSideNavLeafWithChecksAgainst(getDriver(), addStd_ClassNameToAdd, addStd_ClassNameToAdd,
				addStd_ManagementAddedClassTreePath);

		// check new class is shown also in administration module
		switchToAdminModule(getDriver(), context);
		sleep(context);

		assertTrue("Fresh class must be present in administration menu...",
				ExtjsNavigationMenu.extract(getDriver()).getAtPath(addStd_AdminAddedClassTreePath).isPresent());

		// delete test class
		ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", addStd_ClassNameToAdd);
		ExtjsUtils.findElementByTestId(getDriver(), "administration-class-properties-tool-deletebtn").click();
		ExtjsUtils.findElementByXPath(getDriver(), "//div[@role = 'alertdialog']//span[text() = 'Yes']").click();

		testEnd(context);

	}

	// Add Simple Class test vars
	String addSimple_classNameToAdd = defaultClassNameToAdd + "Simple" + RandomStringUtils.randomAlphabetic(3);
	String[] addSimple_AdminAddedClassTreePath = { "Classes", "Simples", addSimple_classNameToAdd };
	String[] addSimple_ManagementAddedClassTreePath = { "All items", "Classes", "Simple", addSimple_classNameToAdd };

	@Test
	public void addSimpleClassTest() {
		assertFalse("Random class name already present, cannot perform test. This is a one time issue.",
				getNumberOfRowsInTableIfTableExists(addSimple_classNameToAdd).isPresent());

		UITestContext context = getDefaultTestContextInstance();
		testStart(context);
		goFullScreen();
		login(Login.admin());
		sleep(context);

		ExtjsUtils.switchToAdminModule2(getDriver(), context);
		sleep(context);

		ExtjsNavigationMenu.extract(getDriver()).selectAtPath(defaultTreePath);
		sleep(context);
		openAddClassDialog(context);
		assertTrue("Add class dialog should be already open", getAddClassPropertiesPanel().isPresent());

		WebElement addClassPropertiesPanel = getAddClassPropertiesPanel().get();
		ExtjsUtils.fillFormTextFieldAdministration(getDriver(), addClassPropertiesPanel, "classnamefieldadd",
				addSimple_classNameToAdd);
		ExtjsUtils.fillFormTextFieldAdministration(getDriver(), addClassPropertiesPanel, "type", "Simple");

		sleep(context);
		assertFalse("Save button must be enabled  after filling all required fields",
				isButtonDisabled(getAddClassSaveButton()));
		getAddClassSaveButton().click();

		logout();
		sleep(context);
		// check db for presence of class
		assertTrue("The table: " + addSimple_classNameToAdd + " must exist",
				getNumberOfRowsInTableIfTableExists(addSimple_classNameToAdd).isPresent());
		assertEquals("The table: " + addSimple_classNameToAdd + " contain no record", new Integer(0),
				getNumberOfRowsInTableIfTableExists(addSimple_classNameToAdd).get());

		// log out and check new class is shown in management module
		login(Login.admin());
		sleep(context);
		safelyClickOnSideNavLeafWithChecksAgainst(getDriver(), addSimple_classNameToAdd, addSimple_classNameToAdd,
				addSimple_ManagementAddedClassTreePath);

		// check new class is shown also in administration module
		switchToAdminModule(getDriver(), context);
		sleep(context);
		assertTrue("Fresh class must be present in administration menu...",
				ExtjsNavigationMenu.extract(getDriver()).getAtPath(addSimple_AdminAddedClassTreePath).isPresent());

		// delete test class
		ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Simples", addSimple_classNameToAdd);
		ExtjsUtils.findElementByTestId(getDriver(), "administration-class-properties-tool-deletebtn").click();
		ExtjsUtils.findElementByXPath(getDriver(), "//div[@role = 'alertdialog']//span[text() = 'Yes']").click();

		testEnd(context);
	}

	// Cancel Creation of Standard Class test vars

	/**
	 * Tests also that you can't create new classes if you don't fill all required
	 * fields in
	 */
	@Test
	public void cancelCreationOfStandardClassTest() {

		// prerequisite: check absence of class to add on db
		assertFalse("Random class name already present, cannot perform test. This is a one time issue.",
				getNumberOfRowsInTableIfTableExists(defaultClassNameToAdd).isPresent());

		UITestContext context = getDefaultTestContextInstance();
//        context.withRule(getDefaultCLientLogCheckRule());
		context.withRule(UITestRule.defineClientLogCheckRule(
				"Tolerate some noise (404, blob, constructor, cannotread property, 401) Rule",
				ImmutableList.of("400", "404", "blob", "constructor", "Cannot read property", "401"), null, null));
		// TODO strengthen constraints when possible

		testStart(context);
		goFullScreen();
		login(Login.admin());
		sleep(context);
		ExtjsUtils.switchToAdminModule(getDriver(), context);

		sleep(context);

		ExtjsNavigationMenu.extract(getDriver()).selectAtPath(defaultTreePath);
		sleep(context);
		openAddClassDialog(context);
		assertTrue("Add class dialog should be already open", getAddClassPropertiesPanel().isPresent());
		sleep(context);
		WebElement addClassPropertiesPanel = getAddClassPropertiesPanel().get();
		assertTrue("Save button must be disabled when mandatory data is not filled",
				isButtonDisabled(getAddClassSaveButton()));
		addClassPropertiesPanel.findElement(By.name("description")).sendKeys(defaultClassNameToAdd);
		assertTrue("Save button must be disabled if classname is filled", isButtonDisabled(getAddClassSaveButton()));
		sleep(context);

		addClassPropertiesPanel.findElement(By.name("classnamefieldadd")).sendKeys(defaultClassNameToAdd);
		sleep(context);
		assertFalse("Save button must be enabled  after filling all required fields",
				isButtonDisabled(getAddClassSaveButton()));
		getAddClassCancelButton().click();
		sleep(context);
		assertFalse("The table: " + defaultClassNameToAdd + " must not exist",
				getNumberOfRowsInTableIfTableExists(defaultClassNameToAdd).isPresent());

		testEnd(context);
	}

	// Cancel Creation of Simple Class test vars
	String ccSimpleClassName = defaultClassNameToAdd + RandomStringUtils.randomAlphabetic(3);

	/**
	 * Tests also that you can't create new classes if you don't fill all required
	 * fields in
	 */
	@Test
	public void cancelCreationOfSimpleClassTest() {

		assertFalse("Random class name already present on db, test prerequisite is not satisfied. Just try again...",
				getNumberOfRowsInTableIfTableExists(ccSimpleClassName).isPresent());

		UITestContext context = getDefaultTestContextInstance();
//        context.withRule(getDefaultCLientLogCheckRule());
		context.withRule(UITestRule.defineClientLogCheckRule(
				"Tolerate some noise (404, 401 blob, constructor, ...) Rule",
				ImmutableList.of("400", "404", "blob", "constructor", "401", "Cannot read property 'get' of null"),
				null, null));
		// TODO strengthen constraints when possible

		testStart(context);
		goFullScreen();
		login(Login.admin());
		sleep(context);
		ExtjsUtils.switchToAdminModule(getDriver(), context);
		sleep(context);

		ExtjsNavigationMenu.extract(getDriver()).selectAtPath(defaultTreePath);
		sleep(context);
		openAddClassDialog(context);
		assertTrue("Add class dialog should be already open", getAddClassPropertiesPanel().isPresent());
		WebElement addClassPropertiesPanel = getAddClassPropertiesPanel().get();
		assertTrue("Save button must be disabled when mandatory data is not filled",
				isButtonDisabled(getAddClassSaveButton()));
		ExtjsUtils.fillFormTextFieldAdministration(getDriver(), addClassPropertiesPanel, "description",
				ccSimpleClassName);
		assertTrue("Save button must be disabled if classname is filled", isButtonDisabled(getAddClassSaveButton()));
		ExtjsUtils.fillFormTextFieldAdministration(getDriver(), addClassPropertiesPanel, "classnamefieldadd",
				ccSimpleClassName);
		ExtjsUtils.fillFormTextFieldAdministration(getDriver(), addClassPropertiesPanel, "type", "Simple");

		sleep(context);
		assertFalse("Save button must be enabled  after filling all required fields",
				isButtonDisabled(getAddClassSaveButton()));
		getAddClassCancelButton().click();
		sleep(context);
		// check db for absence of class
		assertFalse("The table: " + ccSimpleClassName + " must exist",
				getNumberOfRowsInTableIfTableExists(addSimple_classNameToAdd).isPresent());

		testEnd(context);
	}

	// Class Name beginning With Digit allowed test
	String digit_ClassNameToAdd = RandomStringUtils.randomNumeric(1) + defaultClassNameToAdd;
	String[] digit_ManagementAddedClassTreePath = { "All items", "Classes", "Standard", digit_ClassNameToAdd };
	String[] digit_AdminAddedClassTreePath = { "Classes", "Standard", digit_ClassNameToAdd };

	@Test
	public void classNameBeginningWithDigitIsNotAllowedTest() {
		// prerequisite: check existence of class to add on db
		assertFalse("Random class name already present, cannot perform test. This is a one time, no repeating issue.",
				getNumberOfRowsInTableIfTableExists(cnbWunderscore_ClassNameToAdd).isPresent());

		UITestContext context = getDefaultTestContextInstance();
		context.withRule(UITestRule.defineClientLogCheckRule("ClassNamesLongerThan20Rule",
				ImmutableList.of("400", "401", "construct", "blob", "404", "Cannot read property"), null, null));
		// TODO remove 404 blob etc. when possibile (see #556)
		testStart(context);
		goFullScreen();
		login(Login.admin());
		sleep(context);
		ExtjsUtils.switchToAdminModule(getDriver(), context);
		sleep(context);

		assertFalse("", addStandardClassFromAdminModule(digit_ClassNameToAdd, digit_ClassNameToAdd,
				Optional.of(context), false, Optional.empty()));

		ExtjsNavigationMenu currNavMenu = ExtjsNavigationMenu.extract(getDriver());
		assertFalse("Class name beginning with underscore should not compare in classlist admin module",
				currNavMenu.getAtPath(digit_AdminAddedClassTreePath).isPresent());
		getRestClient().system().dropAllCaches();
		logout();

		// check management module after drop cache
		sleep(context);
		login(Login.admin());
		sleep(context);
		assertFalse(
				"Class name beginning with underscore should not compare in nav tree (management, after dropCaches)",
				ExtjsNavigationMenu.extract(getDriver()).getAtPath(digit_ManagementAddedClassTreePath).isPresent());
		logout();
		sleep(context);

		// recheck admin module after cache drop and new login
		login(Login.admin());
		sleep(context);
		ExtjsUtils.switchToAdminModule2(getDriver(), context);
		sleep(context);

		assertFalse(
				"Class name beginning with underscore should not compare in nav tree (management, after dropCaches)",
				ExtjsNavigationMenu.extract(getDriver()).getAtPath(digit_AdminAddedClassTreePath).isPresent());

		testEnd(context);
	}

	// Add Superclass Test vars
	String addSuper_ClassNameToAdd = "Superclass" + defaultClassNameToAdd + RandomStringUtils.randomAlphabetic(3);
	String[] addSuper_ManagementAddedClassTreePath = { "All items", "Classes", "Standard", addSuper_ClassNameToAdd };
	String[] addSuper_AdminAddedClassTreePath = { "Classes", "Standard", addSuper_ClassNameToAdd };

	@Test
	public void addSuperclassTest() {

		// prerequisite: check existence of class to add on db
		assertFalse("Random class name already present, cannot perform test. This is a one time issue.",
				getNumberOfRowsInTableIfTableExists(addSuper_ClassNameToAdd).isPresent());

		UITestContext context = getDefaultTestContextInstance();
		testStart(context);
		goFullScreen();
		login(Login.admin());
		sleep(context);
		ExtjsUtils.switchToAdminModule(getDriver(), context);
		sleep(context);

		ExtjsNavigationMenu.extract(getDriver()).selectAtPath(defaultTreePath);
		sleep(context);
		openAddClassDialog(context);
		assertTrue("Add class dialog should be already open", getAddClassPropertiesPanel().isPresent());
		WebElement addClassPropertiesPanel = getAddClassPropertiesPanel().get();
		ExtjsUtils.fillFormTextFieldAdministration(getDriver(), addClassPropertiesPanel, "description",
				addSuper_ClassNameToAdd);
		ExtjsUtils.fillFormTextFieldAdministration(getDriver(), addClassPropertiesPanel, "classnamefieldadd",
				addSuper_ClassNameToAdd);
		ExtjsUtils.fillFormTextFieldAdministration(getDriver(), addClassPropertiesPanel, "type", "Standard");

		Optional<WebElement> checkboxSuperclassRootWE = addClassPropertiesPanel
				.findElements(By.className("x-form-type-checkbox")).stream()
				.filter(cb -> (cb.findElement(By.tagName("label")).getText().contains("Superclass"))).findFirst();
		checkboxSuperclassRootWE.get().findElement(By.tagName("input")).click();

		sleep(context);
		assertFalse("Save button must be enabled  after filling all required fields",
				isButtonDisabled(getAddClassSaveButton()));
		getAddClassSaveButton().click();
		sleep(context);
		logout();
		sleep(context);

		// check db for presence of class
		try {
			Integer newClassCardsCount = getJdbcTemplate().queryForObject(
					sqlQueryThatFailsIfClassDoesNotExists.replace("[class]", addSuper_ClassNameToAdd), Integer.class);
			assertEquals("The table: " + addSuper_ClassNameToAdd + " must exist and must contain no record",
					new Integer(0), newClassCardsCount);
		} catch (DataAccessException e) {
			assertFalse("It should be possibile to query for new class instances...", true);
		}

		// log out and check new class is shown in management module
		login(Login.admin());
		sleep(context);
		Optional<ExtjsNavigationMenu.MenuItem> menuItemNewClass = ExtjsNavigationMenu.extract(getDriver())
				.getAtPath(addSuper_ManagementAddedClassTreePath);

		assertTrue("Menu item for the new class must be present in Management module", menuItemNewClass.isPresent());
		assertTrue("Menu item of superclass should be expandable", !menuItemNewClass.get().isExpandable());
		// check new class is shown also in administration module
		switchToAdminModule(getDriver(), context);
		sleep(context);
		assertTrue("Fresh class must be present in administration menu...",
				ExtjsNavigationMenu.extract(getDriver()).getAtPath(addSuper_AdminAddedClassTreePath).isPresent());

		// delete test class
		ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", addSuper_ClassNameToAdd);
		ExtjsUtils.findElementByTestId(getDriver(), "administration-class-properties-tool-deletebtn").click();
		ExtjsUtils.findElementByXPath(getDriver(), "//div[@role = 'alertdialog']//span[text() = 'Yes']").click();

		testEnd(context);

	}

	// Classname with special chars not allowed
	String wrongClassNames_bannedChars = " %$\t\n-";
	List<String> wrongClassNames_classNames = ImmutableList.of(
			defaultClassNameToAdd + " " + RandomStringUtils.randomAlphabetic(3),
			defaultClassNameToAdd + "-" + RandomStringUtils.randomAlphabetic(3),
			defaultClassNameToAdd + "$" + RandomStringUtils.randomAlphabetic(3),
			defaultClassNameToAdd + "%" + RandomStringUtils.randomAlphabetic(3),
			defaultClassNameToAdd + "\u00E0" + RandomStringUtils.randomAlphabetic(3),
			defaultClassNameToAdd + "\u00F1" + RandomStringUtils.randomAlphabetic(3));

	/**
	 * This test checks you can't add classes with special chars. UI directly
	 * forbids typing of special chars in Name field, so the test checks Ui
	 * guarantees that no special char is accepted. The test checks also filling
	 * field by copy and paste; that leads to a slightly different case (special
	 * char is shown in the field, which in turn results as non valid, thus
	 * disabling Save button.
	 *
	 * Allowed chars in class names: digits and underscore Not Allowed: other
	 * special chars (space, % $ etc)
	 */
	@Test
	public void classNameWithSpecialCharsNotAllowedTest() {

		UITestContext context = getDefaultTestContextInstance();
		context.withRule(UITestRule.defineClientLogCheckRule("Accept all errors rule", null,
				ImmutableList.of("Keep nothing equals accept all..."), null));
		testStart(context);
		goFullScreen();
		login(Login.admin());
		sleep(context);
		ExtjsUtils.switchToAdminModule(getDriver(), context);
		sleep(context);

		ExtjsNavigationMenu.extract(getDriver()).selectAtPath(defaultTreePath);
		sleep(context);
		openAddClassDialog(context);
		assertTrue("Add class dialog should be already open", getAddClassPropertiesPanel().isPresent());

		WebElement addClassPropertiesPanel = getAddClassPropertiesPanel().get();
		ExtjsUtils.fillFormTextFieldAdministration(getDriver(), addClassPropertiesPanel, "type", "Standard");

		for (String classNameToAdd : wrongClassNames_classNames) {

			ExtjsUtils.fillFormTextFieldAdministration(getDriver(), addClassPropertiesPanel, "description",
					"AddSpecialCharNameTestFailed");
			ExtjsUtils.fillFormTextFieldAdministration(getDriver(), addClassPropertiesPanel, "classnamefieldadd",
					classNameToAdd);
			sleep(context);
			String currentText = addClassPropertiesPanel.findElement(By.name("classnamefieldadd")).getText();
			assertFalse("If field name still contains special chars save button should be disabled",
					(!isButtonDisabled(getAddClassSaveButton()))
							&& StringUtils.containsAny(currentText, wrongClassNames_bannedChars));
			if (!isButtonDisabled(getAddClassSaveButton())) {
				assertNotEquals(
						"If save button is enabled field content must be different from class name filled due to special chars epuration",
						classNameToAdd.equals(currentText));
			}
			clearFormTextField(getDriver(), addClassPropertiesPanel, "classnamefieldadd");
			clearFormTextField(getDriver(), addClassPropertiesPanel, "description");
		}

		// check also with copy and paste, that leads to a slightly different case
		// (special char is shown in the field, which in turn results non valid, thus
		// disabling Save button
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Clipboard clipboard = toolkit.getSystemClipboard();
		for (String classNameToAdd : wrongClassNames_classNames) {

			StringSelection strSel = new StringSelection(classNameToAdd);
			clipboard.setContents(strSel, null);
			WebElement nameField = fetchFormTextField(addClassPropertiesPanel, "Name").get()
					.findElement(By.tagName("input"));
			safeClick(nameField);
			nameField.sendKeys(Keys.CONTROL + "v");
			ExtjsUtils.fillFormTextFieldAdministration(getDriver(), addClassPropertiesPanel, "description",
					"AddSpecialCharNameTestFailed");
			sleep(context);
			String currentText = addClassPropertiesPanel.findElement(By.name("classnamefieldadd")).getText();
			assertFalse("If field name still contains special chars save button should be disabled",
					(!isButtonDisabled(getAddClassSaveButton()))
							&& StringUtils.containsAny(currentText, wrongClassNames_bannedChars));
			if (!isButtonDisabled(getAddClassSaveButton())) {
				assertNotEquals(
						"If save button is enabled field content must be different from class name filled due to special chars epuration",
						classNameToAdd.equals(currentText));
			}
			clearFormTextField(getDriver(), addClassPropertiesPanel, "classnamefieldadd");
			clearFormTextField(getDriver(), addClassPropertiesPanel, "description");
		}
		testEnd(context);

	}

	// Class Name Longer than 20 chars not allowed test
	String lt20NA_BaseClassName = "TestLongerThan20";
	String lt20NA_ClassNameToAdd = lt20NA_BaseClassName
			+ RandomStringUtils.randomNumeric(max(5, 21 - lt20NA_BaseClassName.length()));
	String[] lt20_ManagementAddedClassTreePath = { "All items", "Classes", "Standard", lt20NA_ClassNameToAdd };
	String[] lt20_AdminAddedClassTreePath = { "Classes", "Standard", lt20NA_ClassNameToAdd };

	@Test
	public void classNameLongerThan20CharsNotAllowed() {

		// prerequisite: check existence of class to add on db
		assertFalse("Random class name already present, cannot perform test. This is a one time, no repeating issue.",
				getNumberOfRowsInTableIfTableExists(lt20NA_ClassNameToAdd).isPresent());

		UITestContext context = getDefaultTestContextInstance();
		context.withRule(UITestRule.defineClientLogCheckRule("ClassNamesLongerThan20Rule",
				ImmutableList.of("400", "401", "404", "construct", "Cannot read property"), null, null));

		testStart(context);
		goFullScreen();
		login(Login.admin());
		sleep(context);
		ExtjsUtils.switchToAdminModule(getDriver(), context);
		sleep(context);
		assertFalse("", addStandardClassFromAdminModule(lt20NA_ClassNameToAdd, lt20NA_ClassNameToAdd,
				Optional.of(context), false, Optional.empty())); // empty because trying to add the class is test
																	// purpose

		ExtjsNavigationMenu currNavMenu = ExtjsNavigationMenu.extract(getDriver());
		assertFalse("Class names longer than 20 chars should not compare in classlist admin module",
				currNavMenu.getAtPath(lt20_AdminAddedClassTreePath).isPresent());
		getRestClient().system().dropAllCaches();
		logout();

		// check management module after drop cache
		sleep(context);
		login(Login.admin());
		sleep(context);
		assertFalse("Class names longer than 20 chars should not compare in nav tree (management, after dropCaches)",
				ExtjsNavigationMenu.extract(getDriver()).getAtPath(lt20_ManagementAddedClassTreePath).isPresent());
		logout();
		sleep(context);
		// recheck admin module after cache drop and new login
		login(Login.admin());
		sleep(context);
		ExtjsUtils.switchToAdminModule(getDriver(), context);
		sleep(context);

		assertFalse("Class name longer than 20 chars should not compare in nav tree (management, after dropCaches)",
				ExtjsNavigationMenu.extract(getDriver()).getAtPath(lt20_AdminAddedClassTreePath).isPresent());

		testEnd(context);
	}

	// Class name beginning with underscore not allowed test

	String cnbWunderscore_ClassNameToAdd = "_" + defaultClassNameToAdd + RandomStringUtils.randomNumeric(3);
	String[] cnbWunderscore_ManagementAddedClassTreePath = { "All items", "Classes", cnbWunderscore_ClassNameToAdd };
	String[] cnbWunderscore_AdminAddedClassTreePath = { "Classes", "Standard", cnbWunderscore_ClassNameToAdd };

	@Test
	public void classNameBeginningWithUnderscoreNotAllowedTest() {

		// prerequisite: check existence of class to add on db
		assertFalse("Random class name already present, cannot perform test. This is a one time, no repeating issue.",
				getNumberOfRowsInTableIfTableExists(cnbWunderscore_ClassNameToAdd).isPresent());

		UITestContext context = getDefaultTestContextInstance();
		context.withRule(UITestRule.defineClientLogCheckRule("ClassNamesLongerThan20Rule",
				ImmutableList.of("400", "401", "construct", "blob", "404", "Cannot read property"), null, null));
		// TODO remove 404 blob etc. when possibile (see #556)
		testStart(context);
		goFullScreen();
		login(Login.admin());
		sleep(context);
		ExtjsUtils.switchToAdminModule(getDriver(), context);
		sleep(context);

		assertFalse("", addStandardClassFromAdminModule(cnbWunderscore_ClassNameToAdd, cnbWunderscore_ClassNameToAdd,
				Optional.of(context), false, Optional.empty()));

		ExtjsNavigationMenu currNavMenu = ExtjsNavigationMenu.extract(getDriver());
		assertFalse("Class name beginning with underscore should not compare in classlist admin module",
				currNavMenu.getAtPath(cnbWunderscore_AdminAddedClassTreePath).isPresent());
		getRestClient().system().dropAllCaches();
		logout();

		// check management module after drop cache
		sleep(context);
		login(Login.admin());
		sleep(context);
		assertFalse(
				"Class name beginning with underscore should not compare in nav tree (management, after dropCaches)",
				ExtjsNavigationMenu.extract(getDriver()).getAtPath(cnbWunderscore_ManagementAddedClassTreePath)
						.isPresent());
		logout();
		sleep(context);

		// recheck admin module after cache drop and new login
		login(Login.admin());
		sleep(context);
		ExtjsUtils.switchToAdminModule(getDriver(), context);
		sleep(context);

		assertFalse(
				"Class name beginning with underscore should not compare in nav tree (management, after dropCaches)",
				ExtjsNavigationMenu.extract(getDriver()).getAtPath(cnbWunderscore_AdminAddedClassTreePath).isPresent());

		testEnd(context);

	}

	// Delete standard class test vars
	String delStd_ClassName = defaultClassNameToAdd + RandomStringUtils.randomNumeric(1)
			+ RandomStringUtils.randomAlphabetic(2);
	String[] delStd_ClassTreePathInAdminMenu = { "Classes", "Standard", delStd_ClassName };

	@Test
	public void deleteStandardClassTest() {

		UITestContext context = getDefaultTestContextInstance();
		// TODO fill client log rule (expect some 500)
		testStart(context);
		goFullScreen();
		login(Login.admin());
		sleep(context);
		ExtjsUtils.switchToAdminModule(getDriver(), context);
		sleep(context);

		// add the class to delete performing minimal checks
		assertTrue("There was a problem adding class to delete. Class name was: " + delStd_ClassName,
				addStandardClassFromAdminModule(delStd_ClassName, delStd_ClassName, Optional.of(context), false,
						Optional.empty()));
		// try and delete
		sleep(context); // we have to wait new class is displayed
		ExtjsNavigationMenu.extract(getDriver()).selectAtPath("Classes", "Standard", delStd_ClassName);
		sleep(context);

		removeCurrentSelectedClass(context);

		sleep(context); // we have to wait new class is displayed

		// logout and clear cache, make sure new class was deleted
		logout();
		getRestClient().system().dropAllCaches();
		assertFalse("The table: " + delStd_ClassName + " should not exist anymore",
				getNumberOfRowsInTableIfTableExists(delStd_ClassName).isPresent());
		login(Login.admin());
		sleep(context);
		switchToAdminModule(getDriver(), context);
		sleep(context);
		assertFalse("deleted class must not be present in navigation menu",
				ExtjsNavigationMenu.extract(getDriver()).getAtPath(delStd_ClassTreePathInAdminMenu).isPresent());

		testEnd(context);
	}

	// Delete simple class test vars
	String delSimple_ClassName = defaultClassNameToAdd + "Simple" + RandomStringUtils.randomNumeric(3);
	String[] delSimple_ClassTreePathInAdminMenu = { "Classes", "Simple", delSimple_ClassName };

	@Test
	public void deleteSimpleClassTest() {

		UITestContext context = getDefaultTestContextInstance().withCreatedClass(delSimple_ClassName);
		testStart(context);
		goFullScreen();
		login(Login.admin());
		sleep(context);
		ExtjsUtils.switchToAdminModule(getDriver(), context);
		sleep(context);

		// add the class to delete performing minimal checks
		assertTrue("There was a problem adding class to delete. Class name was: " + delSimple_ClassName,
				addSimpleClassFromAdminModule(delSimple_ClassName, delSimple_ClassName, Optional.of(context)));
		sleep(context); // we have to wait new class is displayed

		// try and delete
		removeCurrentSelectedClass(context);
		sleep(context);

		// logout and clear cache, make sure new class was deleted
		logout();
		getRestClient().system().dropAllCaches();
		assertFalse("The table: " + delSimple_ClassName + " should not exist anymore",
				getNumberOfRowsInTableIfTableExists(delSimple_ClassName).isPresent());
		login(Login.admin());
		sleep(context);
		switchToAdminModule(getDriver(), context);
		sleep(context);
		assertFalse("deleted class must not be present in navigation menu",
				ExtjsNavigationMenu.extract(getDriver()).getAtPath(delSimple_ClassTreePathInAdminMenu).isPresent());

		testEnd(context);
	}

	// Delete Superclass without children allowed test
	String delSuperWChildrenNA_ClassName = "TestSuperWC" + RandomStringUtils.randomNumeric(3); // keep lengthbelow 20
	String delSuperWChildrenNA_SubClassName = "TestSuperWCSub" + RandomStringUtils.randomNumeric(3); // keep lengthbelow
																										// 20
	String[] delSuperWChildrenNA_ClassTreePathInAdminMenu = { "Classes", "Standard", delSuperWChildrenNA_ClassName };
	String[] delSuperWChildrenNA_SubClassTreePathInAdminMenu = { "Classes", "Standard",
			delSuperWChildrenNA_SubClassName };

	@Test
	public void deleteSuperClassWithChildrenNotAllowedTest() {

		UITestContext context = getDefaultTestContextInstance().withCreatedClass(delSuperWChildrenNA_ClassName)
				.withCreatedClass(delSuperWChildrenNA_SubClassName);
		testStart(context);
		goFullScreen();
		login(Login.admin());
		sleep(context);
		ExtjsUtils.switchToAdminModule(getDriver(), context);
		sleep(context);

		// add the superclass (with children) to delete
		assertTrue("There was a problem adding superclass to delete. Class name was: " + delSuperWChildrenNA_ClassName,
				addStandardClassFromAdminModule(delSuperWChildrenNA_ClassName, delSuperWChildrenNA_ClassName,
						Optional.of(context), true, Optional.empty()));
		getRestClient().system().dropAllCaches();
		logout();
		sleep(context);

		// add subclass of superclass previously inserted
		login(Login.admin());
		sleep(context);
		ExtjsUtils.switchToAdminModule(getDriver(), context);
		sleep(context);
		assertTrue(
				"There was a problem adding subclass of superclass to delete. Class name was: "
						+ delSuperWChildrenNA_SubClassName,
				addStandardClassFromAdminModule(delSuperWChildrenNA_SubClassName, delSuperWChildrenNA_SubClassName,
						Optional.of(context), true, Optional.of(delSuperWChildrenNA_ClassName)));
		getRestClient().system().dropAllCaches();
		logout();

		sleep(context);
		// try to remove superclass
		login(Login.admin());
		sleep(context);
		ExtjsUtils.switchToAdminModule(getDriver(), context);
		sleep(context);
		ExtjsNavigationMenu navMenu = ExtjsNavigationMenu.extract(getDriver());
		navMenu.selectAtPath(delSuperWChildrenNA_ClassTreePathInAdminMenu);
		sleep(context);
		removeCurrentSelectedClass(context);
		getRestClient().system().dropAllCaches();
		logout();

		sleep(context);
		login(Login.admin());
		sleep(context);
		ExtjsUtils.switchToAdminModule(getDriver(), context);
		assertTrue("Superclass with children should not have been removed from db",
				getNumberOfRowsInTableIfTableExists(delSuperWChildrenNA_ClassName).isPresent());
		ExtjsNavigationMenu navMenuAfter = ExtjsNavigationMenu.extract(getDriver());
		assertTrue("Superclass with children should be shown on Administration Menu ",
				navMenuAfter.getAtPath(delSuperWChildrenNA_ClassTreePathInAdminMenu).isPresent());

		given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
				.delete(buildRestV3Url("classes/" + delSuperWChildrenNA_SubClassName)).then().statusCode(200);

		given().header(CMDBUILD_AUTH_HEADER, getSessionToken()).when()
				.delete(buildRestV3Url("classes/" + delSuperWChildrenNA_ClassName)).then().statusCode(200);

		testEnd(context);
	}

	// Delete Superclass without children allowed test
	String delSuperWOChildren_ClassName = "TestSuperWOC" + RandomStringUtils.randomNumeric(3); // keep lengthbelow 20
	String[] delSuperWOChildren_ClassTreePathInAdminMenu = { "Classes", "Standard", delSuperWOChildren_ClassName };

	@Test
	public void deleteSuperClassWithoutChildrenAllowedTest() {

		UITestContext context = getDefaultTestContextInstance().withCreatedClass(delSuperWOChildren_ClassName);
		// TODO fill some client log rule (expect some 500) when test is stable
		testStart(context);
		goFullScreen();
		login(Login.admin());
		sleep(context);
		ExtjsUtils.switchToAdminModule(getDriver(), context);
		sleep(context);
		// add the class to delete
		assertTrue("There was a problem adding class to delete. Class name was: " + delSuperWOChildren_ClassName,
				addStandardClassFromAdminModule(delSuperWOChildren_ClassName, delSuperWOChildren_ClassName,
						Optional.of(context), true, Optional.empty()));
		// try and delete

		getRestClient().system().dropAllCaches();
		logout();
		sleep(context);
		login(Login.admin());
		sleep(context);
		ExtjsUtils.switchToAdminModule(getDriver(), context);
		sleep(context);

		ExtjsNavigationMenu navMenu = ExtjsNavigationMenu.extract(getDriver());
		navMenu.selectAtPath(delSuperWOChildren_ClassTreePathInAdminMenu);
		sleep(context);
		removeCurrentSelectedClass(context);

		sleep(context); // we have to wait new class is displayed

		// logout and clear cache, make sure new class was deleted
		getRestClient().system().dropAllCaches();
		logout();
		sleep(context);
		assertFalse("The table: " + delSuperWOChildren_ClassName + " should not exist anymore",
				getNumberOfRowsInTableIfTableExists(delSuperWOChildren_ClassName).isPresent());
		login(Login.admin());
		sleep(context);
		switchToAdminModule(getDriver(), context);
		sleep(context);
		assertFalse("deleted class must not be present in navigation menu", ExtjsNavigationMenu.extract(getDriver())
				.getAtPath(delSuperWOChildren_ClassTreePathInAdminMenu).isPresent());

		testEnd(context);
	}

	// UTILITY METHODS

	@PromoteToUITestUtility
	private void openAddClassDialog(UITestContext context) {
		WebElement addClassButton = waitForElementPresence(locatorAddClassButton);
		safeClick(addClassButton);
		sleep(context);
	}

	@PromoteToUITestUtility
	// TODO FIXME: not yet implmented, so we cannot test and we DON'T KNOW IF THERE
	// WILL BE SOME FURTHER USER ACTION REQUIRED (e.g.: confirmation)
	private void removeCurrentSelectedClass(UITestContext context) {
		ExtjsUtils.findElementByTestId(getDriver(), "administration-class-properties-tool-deletebtn").click();
		sleep(context);
		ExtjsUtils.findElementByXPath(getDriver(), "//div[@role = 'alertdialog']//span[text() = 'Yes']").click();
		sleep(context);
	}

	private Optional<WebElement> getAddClassPropertiesPanel() {
		return getDriver().findElementsByClassName("x-panel").stream().filter(
				p -> p.getAttribute("id").contains("administration-content-classes-tabitems-properties-properties"))
				.findFirst();
	}

	private WebElement getAddClassSaveButton() {
		Optional<WebElement> footerToolbar = getDriver().findElements(By.className(("x-toolbar-footer"))).stream()
				.filter(tb -> tb.isEnabled()).findFirst();
		return footerToolbar.get().findElements(By.tagName("a")).stream()
				.filter(b -> b.getAttribute("class").contains("x-btn")).filter(b -> "Save".equals(b.getText()))
				.findFirst().get();
	}

	private WebElement getAddClassCancelButton() {
		Optional<WebElement> footerToolbar = getDriver().findElements(By.className(("x-toolbar-footer"))).stream()
				.filter(tb -> tb.isEnabled()).findFirst();
		return footerToolbar.get().findElements(By.tagName("a")).stream()
				.filter(b -> b.getAttribute("class").contains("x-btn")).filter(b -> "Cancel".equals(b.getText()))
				.findFirst().get();
	}

	@PromoteToUITestUtility
	private boolean isButtonDisabled(@Nonnull WebElement buttonRootADiv) {
		return buttonRootADiv.getAttribute("class").contains("x-btn-disabled");
	}

	/**
	 *
	 * Login and switch to admin module are prerequisites.
	 *
	 * @param name        name of the class (db table name) to add
	 * @param description description (UI name) of the added class.
	 * @param testContext (optional) if present, time spent to confirm that/if class
	 *                    (table) has been inserted to db is notified to the context
	 *                    as artificial (test generated delay)
	 * @return true if new class was inserted. If an error occured (i.e. class name
	 *         contains special chars) false is returned
	 *
	 *         Performs a quick test against the presence of the new table in DB
	 */
	@PromoteToUITestUtility
	private boolean addSimpleClassFromAdminModule(String name, String description,
			Optional<UITestContext> testContext) {

		// perform minimum UI actions in order to add the required class
		ExtjsNavigationMenu.extract(getDriver()).selectAtPath(defaultTreePath);
		sleep(testContext.get());
		openAddClassDialog(testContext.get());
		sleep(testContext.get());
		if (!getAddClassPropertiesPanel().isPresent()) {
			logger.error("Add class dialog was not open");
			return false;
		}
		WebElement addClassPropertiesPanel = getAddClassPropertiesPanel().get();
		ExtjsUtils.fillFormTextFieldAdministration(getDriver(), addClassPropertiesPanel, "type", "Simple");
		ExtjsUtils.fillFormTextFieldAdministration(getDriver(), addClassPropertiesPanel, "description", description);
		ExtjsUtils.fillFormTextFieldAdministration(getDriver(), addClassPropertiesPanel, "classnamefieldadd", name);

		sleep(testContext.get());
		if (isButtonDisabled(getAddClassSaveButton())) {
			logger.error("Save Class button is disabled");
			getDriver().switchTo().activeElement().sendKeys(Keys.ESCAPE, Keys.ESCAPE);
			return false;
		}
		getAddClassSaveButton().click();
		sleep(testContext.get());

		// check if class is present on db, giving the backend a little breathing to
		// carry the task...
		long timeSpentWaitingForBackendToCreateNewClass = 0;
		LocalDateTime checkStart = LocalDateTime.now();
		while (ChronoUnit.MILLIS.between(checkStart, LocalDateTime.now()) < backendAddClassTimeoutMillis) {
			try {
				Integer newClassCardsCount = getJdbcTemplate()
						.queryForObject(sqlQueryThatFailsIfClassDoesNotExists.replace("[class]", name), Integer.class);
				if (newClassCardsCount == 0) {
					if (testContext.isPresent()) {
						testContext.get().notifyArtificialDelayTime(timeSpentWaitingForBackendToCreateNewClass);
						getDriver().switchTo().activeElement().sendKeys(Keys.ESCAPE, Keys.ESCAPE);
						return true;
					}
					logger.error("DB table for class {} was created but contains records!!", name);
					getDriver().switchTo().activeElement().sendKeys(Keys.ESCAPE, Keys.ESCAPE);
					return false; // Table is created and contains records!! What's up??
				}
			} catch (DataAccessException e) {
				try {
					sleep(backendAddClassTimeoutMillis);
					timeSpentWaitingForBackendToCreateNewClass += backendCheckForClassAddedSnoozeMillis;
				} catch (Exception e1) {
				}
			}
		}
		logger.warn("Class {} was not added to DB (at least after a {} ms wait...)", name,
				backendAddClassTimeoutMillis);
		return false;

	}

	private long backendAddClassTimeoutMillis = 8000;
	private long backendCheckForClassAddedSnoozeMillis = 50;

	/**
	 *
	 * Login and switch to admin module are prerequisites.
	 *
	 * @param name        name of the simple class (db table name) to add
	 * @param description description (UI name) of the added class.
	 * @param testContext (optional) if present, time spent to confirm that/if class
	 *                    (table) has been inserted to db is notified to the context
	 *                    as artificial (test generated delay)
	 * @param superclass  (optional) if true set new class as superclass
	 * @return true if new class was inserted. If an error occured (i.e. class name
	 *         contains special chars) false is returned
	 *
	 *         Performs a quick test against the presence of the new table in DB
	 */
	@PromoteToUITestUtility
	private boolean addStandardClassFromAdminModule(String name, String description,
			Optional<UITestContext> testContext, boolean isSuperclass, Optional<String> superclass) {

		// perform minimum UI actions in order to add the required class
		ExtjsNavigationMenu.extract(getDriver()).selectAtPath(defaultTreePath);
		sleep(testContext.get());
		openAddClassDialog(testContext.get());
		sleep(testContext.get());
		if (!getAddClassPropertiesPanel().isPresent()) {
			logger.error("Add class dialog was not open");
			return false;
		}
		WebElement addClassPropertiesPanel = getAddClassPropertiesPanel().get();
		ExtjsUtils.fillFormTextFieldAdministration(getDriver(), addClassPropertiesPanel, "type", "Standard");
		ExtjsUtils.fillFormTextFieldAdministration(getDriver(), addClassPropertiesPanel, "description", description);
		ExtjsUtils.fillFormTextFieldAdministration(getDriver(), addClassPropertiesPanel, "classnamefieldadd", name);

		if (superclass.isPresent())
			ExtjsUtils.fillFormTextFieldAdministration(getDriver(), addClassPropertiesPanel, "parent",
					superclass.get());

		if (isSuperclass) {
			Optional<WebElement> checkboxSuperclassRootWE = addClassPropertiesPanel
					.findElements(By.className("x-form-type-checkbox")).stream()
					.filter(cb -> (cb.findElement(By.tagName("label")).getText().contains("Superclass"))).findFirst();
			checkboxSuperclassRootWE.get().findElement(By.tagName("input")).click();
		}
		sleep(testContext.get());
		if (isButtonDisabled(getAddClassSaveButton())) {
			logger.error("Save Class button is disabled");
			getDriver().switchTo().activeElement().sendKeys(Keys.ESCAPE, Keys.ESCAPE);
			return false;
		}
		if (!Strings.nullToEmpty(ExtjsUtils.getFormTextFieldContent(addClassPropertiesPanel, "Name")).trim()
				.equals(name.trim())) {
			logger.warn(
					"Input control in add class changed the content of Name field! This is expceted if class name is longer than 20 chars...");
			return false;
		}
		getAddClassSaveButton().click();
		sleep(testContext.get());

		// check if class is present on db, giving the backend a little breathing to
		// carry the task...
		long timeSpentWaitingForBackendToCreateNewClass = 0;
		LocalDateTime checkStart = LocalDateTime.now();
		while (ChronoUnit.MILLIS.between(checkStart, LocalDateTime.now()) < backendAddClassTimeoutMillis) {
			try {
				Integer newClassCardsCount = getJdbcTemplate()
						.queryForObject(sqlQueryThatFailsIfClassDoesNotExists.replace("[class]", name), Integer.class);
				if (newClassCardsCount == 0) {
					if (testContext.isPresent()) {
						testContext.get().notifyArtificialDelayTime(timeSpentWaitingForBackendToCreateNewClass);
						getDriver().switchTo().activeElement().sendKeys(Keys.ESCAPE, Keys.ESCAPE);
						return true;
					}
					logger.error("DB table for class {} was created but contains records!!", name);
					getDriver().switchTo().activeElement().sendKeys(Keys.ESCAPE, Keys.ESCAPE);
					return false; // Table is created and contains records!! What's up??
				}
			} catch (DataAccessException e) {
				try {
					sleep(backendCheckForClassAddedSnoozeMillis);
					timeSpentWaitingForBackendToCreateNewClass += backendCheckForClassAddedSnoozeMillis;
				} catch (Exception e1) {
				}
			}
		}
		logger.warn("Class {} was not added to DB (at least after a {} ms wait...)", name,
				backendAddClassTimeoutMillis);
		return false;

	}

	/**
	 *
	 * @param tableName the name of the db table to check for existence or content
	 *                  (in number of rows)
	 * @return an empty Optional, if table does not exists. Optional of integer
	 *         representing the raw number of rows (not cards) the table contains.
	 *
	 *         Does not throw exceptions if table is not found.
	 */
	private Optional<Integer> getNumberOfRowsInTableIfTableExists(String tableName) {
		try {
			return Optional.of(getJdbcTemplate().queryForObject(
					sqlQueryThatFailsIfClassDoesNotExists.replace("[class]", tableName), Integer.class));
		} catch (DataAccessException e) {
			return Optional.empty();
		}
	}

	/**
	 *
	 * @param tableName the name of the db table to check for active cards (if
	 *                  existing)
	 * @return an empty Optional, if table does not exists. Optional of integer
	 *         representing the raw number of active cardsthe table contains.
	 *
	 *         Does not throw exceptions if table is not found.
	 */
	private Optional<Integer> getNumberOfActiveRowsInTableIfTableExists(String tableName) {
		try {
			return Optional.of(getJdbcTemplate().queryForObject(
					sqlQueryNumberOfActiveCardsInClassFailingIfClassDoesNotExists.replace("[class]", tableName),
					Integer.class));
		} catch (DataAccessException e) {
			logger.error("Weird jdbc exception {}", e.toString());
			e.printStackTrace();
			return Optional.empty();
		}
	}

}
