package org.cmdbuild.test.web;


import static org.cmdbuild.test.web.utils.ExtjsUtils.*;
import static org.cmdbuild.test.web.utils.UILocators.*;
import static org.junit.Assert.*;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import org.cmdbuild.test.web.utils.*;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Nullable;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AdministrationModuleAccessIT extends BaseWebIT {

    //TEST rules -related //move to BaseWebIT or some collector class
    UITestRule defaultCLientLogRule = UITestRule.defineClientLogCheckRule(null, ImmutableList.of("404", "401"), null, null);
    UITestRule relaxedClientLogRule = UITestRule.defineClientLogCheckRule(null, ImmutableList.of("404", "401", "400"), null, null);
    UITestRule veryPermissiveClientLogRule = UITestRule.defineClientLogCheckRule(null, ImmutableList.of("404", "401", "400", "500",
            "Cannot read property 'close' of undefined"), null, null);

    String administrationModuleURLFragment = "ui/#administration";
    String managementModuleURLFragment = "ui/#classes";
    String managementExpectedTitleFragment = "Cards";
    String expectedAdminSectionTitle = "Administration"; //TODO
    List<String> expectedAdminSectionTitles = ImmutableList.of("Administration", "Classes");  //TODO




    @After
    public void cleanup() {
//        cleanupTouchedClasses();
        cleanupDB();
    }


    @Test
    public void switchToAdminModuleWorksAndFirstClassIsDisplayedTest() {

        UITestContext context = getDefaultTestContextInstance();
        context.withRule(defaultCLientLogRule);

        testStart(context);
        goFullScreen();
        login(Login.admin());
        sleep(context);

        switchToAdminModule(context);

        sleep(context);

        String url = getCurrentUrl();
        assertTrue("Landing url should contain administration module url fragment: " + administrationModuleURLFragment + " or a messagebox shoould warn that admin module is not yet implemented",
                url.contains(administrationModuleURLFragment) || ExtjsMessageBox.isAnyMessageBoxShown(getDriver()));
        assertNotNull("Administration content section should be visible", waitForElementVisibility(cmdbuildAdministrationContent()));
        WebElement adminSectionTitle = waitForPresenceOfNestedElement(getDriver(), cmdbuildAdministrationHeaderTitle(), cmdbuildAdministrationContent());
        assertTrue("Title of Administration section should be as expected", expectedAdminSectionTitles.contains(Strings.nullToEmpty(adminSectionTitle.getText()).trim()));

        //now check first class in navigation menu is displayed
        Optional<String> renderedClassNameCaption = getCurrentClassNameInCaptionText();
        assertTrue("The name of current class must be shown" ,renderedClassNameCaption.isPresent());
        ExtjsNavigationMenu navigationMenu = new ExtjsNavigationMenu(getDriver());
        Optional<ExtjsNavigationMenu.MenuItem> firstClass =  navigationMenu.fetchFirstClass();
        assertTrue("At least a class must be present in the admin navigation menu for this test" , firstClass.isPresent());
        assertTrue("Class name displayed should be the same of that of first class in menu" , renderedClassNameCaption.get().trim().equals(firstClass.get().getText()));

        testEnd(context);

    }

    @Test
    public void switchToAdminModuleOptionIsShownForAdmininUser() {

        UITestContext context = getDefaultTestContextInstance();
        context.withRule(defaultCLientLogRule);

        testStart(context);
        goFullScreen();
        login(Login.admin());
        sleep(context);


        WebElement headerUserMenuDropDown = waitForElementVisibility(headerUserMenuDropDown());
        WebElement switchtoAdminModuleButton = waitForElementVisibility(headerAdministrationButton());


        focus( switchtoAdminModuleButton);
        assertTrue("Button to switch to administration module should be shown and clickable", switchtoAdminModuleButton.isDisplayed() && switchtoAdminModuleButton.isEnabled());

        safeClick(getDriver(), headerUserMenuDropDown);
        waitForElementPresence(locatorForDropDownMenuItems());
        artificialSleep(500, context); //give dropdown some time to render itself
        List<WebElement> adminSwitchOptions = getDriver().findElements(locatorForDropDownMenuItems());
        adminSwitchOptions.stream().forEach(e -> logger.info("xmenuitem: {}", e.getText()));
        List<WebElement> filteredAminOptions = adminSwitchOptions.stream().
//                filter(e -> "span".equals(e.getTagName())).
        filter(e -> "Administration module".equals(e.getText().trim())).collect(Collectors.toList());

        assertEquals("There should be one (dropdown) option for module switching", 1, filteredAminOptions.size());

    }

    @Test
    public void switchToManagementModuleFromAdminModule() {

        UITestContext context = getDefaultTestContextInstance();
        context.withRule(defaultCLientLogRule);

        testStart(context);
        goFullScreen();
        login(Login.admin());
        sleep(context);

        switchToAdminModule(context);

        sleep(context);

        String url = getCurrentUrl();
        assertTrue("Landing url should contain administration module url fragment: " + administrationModuleURLFragment + " or a messagebox shoould warn that admin module is not yet implemented",
                url.contains(administrationModuleURLFragment) || ExtjsMessageBox.isAnyMessageBoxShown(getDriver()));
        assertNotNull("Administration content section should be visible", waitForElementVisibility(cmdbuildAdministrationContent()));
        WebElement adminSectionTitle = waitForPresenceOfNestedElement(getDriver(), cmdbuildAdministrationHeaderTitle(), cmdbuildAdministrationContent());
        assertTrue("Title of Administration section should be as expected", expectedAdminSectionTitles.contains(adminSectionTitle.getText().trim()));

        WebElement headerUserMenuDropDown = waitForElementVisibility(headerUserMenuDropDown());

        focus(headerUserMenuDropDown);
        assertTrue("Button to switch to administration module should be shown and clickable", headerUserMenuDropDown.isDisplayed() && headerUserMenuDropDown.isEnabled());

        safeClick(getDriver(), headerUserMenuDropDown);
        waitForElementPresence(locatorForDropDownMenuItems());
        artificialSleep(500, context); //give dropdown some time to render itself
        List<WebElement> adminSwitchOptions = getDriver().findElements(locatorForDropDownMenuItems());
        adminSwitchOptions.stream().forEach(e -> logger.info("xmenuitem: {}", e.getText()));
        List<WebElement> filteredAminOptions = adminSwitchOptions.stream().
            filter(e -> "Data management module".equals(e.getText().trim())).collect(Collectors.toList());
        assertEquals("There should be one (dropdown) option for module switching", 1, filteredAminOptions.size());
        safeClick(getDriver(), filteredAminOptions.get(0));
        sleep(context);

        url = getCurrentUrl();
        assertTrue("Landing url should contain data management module url fragment: " + managementModuleURLFragment,
                url.contains(managementModuleURLFragment) );
        assertFalse("Landing url should not contain data management module url fragment: " + administrationModuleURLFragment,
                url.contains(administrationModuleURLFragment));
        WebElement managementSectionTitle = waitForPresenceOfNestedElement(getDriver(), By.className("x-title-text"), cmdbuildManagementContentLocator());
        assertTrue("Title of Data Management Section should be as expected", managementSectionTitle.getText().contains(managementExpectedTitleFragment));

    }

    @Test
    public void switchToAdminModuleOptionIsNotShownForNonAdmininUser() {

        UITestContext context = getDefaultTestContextInstance();

        testStart(context);
        goFullScreen();
        login(Login.demo());
        sleep(context);
        WebElement headerUserMenuDropDown = waitForElementVisibility(headerUserMenuDropDown());
        Optional<WebElement> switchtoAdminModuleButton = ExtjsUtils.waitForElementPresence(getDriver(), headerAdministrationButton(), 3, 100);

        //element is present but not shown...
              assertFalse("Button to switch to administration module should not be present in non admin environment",
                switchtoAdminModuleButton.isPresent() && switchtoAdminModuleButton.get().isEnabled() && switchtoAdminModuleButton.get().isDisplayed());

        safeClick(getDriver(), headerUserMenuDropDown);
        waitForElementPresence(locatorForDropDownMenuItems());
        sleep(context);
        List<WebElement> adminSwitchOptions = getDriver().findElements(locatorForDropDownMenuItems());
        adminSwitchOptions.stream().forEach(e -> logger.info("xmenuitem: {}", e.getText()));
        List<WebElement> filteredAminOptions = adminSwitchOptions.stream().
                filter(e -> "Administration module".equals(e.getText().trim())).collect(Collectors.toList());
        
        assertEquals("There should be no (dropdown) option for admin module switching in non admin environment", 0, filteredAminOptions.size());

        String url = getCurrentUrl();
       assertFalse("Current url should not contain administration module url fragment (in non admin environment): " + administrationModuleURLFragment,
                url.contains(administrationModuleURLFragment));

        testEnd(context);

    }

    @Test
    public void accessAfterExpiredSessionNotAllowed() {

        UITestContext context = getDefaultTestContextInstance();

        testStart(context);
        goFullScreen();
        login(Login.admin());
        sleep(context);

        //expire sessions on db
        JdbcTemplate jdbc = getJdbcTemplate();
        LocalDateTime sessionBeginMark = LocalDateTime.now().minusDays(1);
        LocalDateTime sessionLastActiveMark = sessionBeginMark.plusHours(1);
        int rowsAffected = jdbc.update("update \"_Session\" set \"BeginDate\" = ? , \"LastActiveDate\" = ?" , Timestamp.valueOf(sessionBeginMark) , Timestamp.valueOf(sessionLastActiveMark));
        assertTrue("There must be at least one session persisted" , rowsAffected > 0);

        //drop caches (forces reload from db)
        getRestClient().system().dropAllCaches();

        switchToAdminModule(null);
        waitForLoad();
        sleep(context);
        assertTrue("After expiring session and forcing interaction user should be bounced to login page" ,getCurrentUrl().contains("login"));

        testEnd(context);
    }


    private String classesAlphaOrderedInNavMenu_ClassesNodeMenuCaption = "Classes";
    private String classesAlphaOrderedInNavMenu_StandardClassesNodeMenuCaption = "Standard";
    private String classesAlphaOrderedInNavMenu_SimpleClassesNodeMenuCaption = "Simples";

    @Test
    public void classesAlphaOrderedInNavMenutest() {

        UITestContext context = getDefaultTestContextInstance();
        context.withRule(defaultCLientLogRule);

        testStart(context);
        goFullScreen();
        login(Login.admin());
        sleep(context);
        switchToAdminModule(context);
        sleep(context);
        ExtjsNavigationMenu navMenu = new ExtjsNavigationMenu(getDriver());
        ExtjsNavigationMenu.MenuItem classesMenuRoot = navMenu.fetchFirstLevelNodes().stream().filter(n -> classesAlphaOrderedInNavMenu_ClassesNodeMenuCaption.equals(Strings.nullToEmpty(n.getText()).trim())).findFirst().get();
        classesMenuRoot.expandByClick();
        sleep(context);
        ExtjsNavigationMenu.MenuItem standardClassesMenuRoot = classesMenuRoot.getVisibleChildren().stream().filter(n -> classesAlphaOrderedInNavMenu_StandardClassesNodeMenuCaption.equals(Strings.nullToEmpty(n.getText()).trim())).findFirst().get();
        ExtjsNavigationMenu.MenuItem simpleClassesMenuRoot = classesMenuRoot.getVisibleChildren().stream().filter(n -> classesAlphaOrderedInNavMenu_SimpleClassesNodeMenuCaption.equals(Strings.nullToEmpty(n.getText()).trim())).findFirst().get();

        //checking standard classes
        standardClassesMenuRoot.expandByClick();
        sleep(context);
        List<ExtjsNavigationMenu.MenuItem> classNodes = standardClassesMenuRoot.getVisibleChildren();
        List<String> classes = classNodes.stream().map(cn -> Strings.nullToEmpty(cn.getText()).trim().toLowerCase()).collect(Collectors.toList());
        List<String> orderedClasses = new ArrayList<>();
        orderedClasses.addAll(classes);
        Collections.sort(orderedClasses);
        logger.info("Admin menu class order is : {}" ,classes.toString());
        logger.info("Right class order is : {}" ,orderedClasses.toString());
        for(int i = 0; i < classes.size(); i++) {
            assertEquals("The element at index " + i + " should be the same for ordered and displayed item. Ordered is: "+ orderedClasses.get(i) +", Displayed is: "  + classes.get(i)
                    , classes.get(i) , orderedClasses.get(i));
        }
        testEnd();

    }


    /**
     * Convenience method for switching to Admin module from Management module. Does not check if user is authorized
     *
     * @param context optional
     * @throws WebUICMDBuidTestException if no admin switch option was found in user menu
     */
    private void switchToAdminModule(@Nullable UITestContext context) throws WebUICMDBuidTestException {
        ExtjsUtils.switchToAdminModule(getDriver() ,context );
        sleep(context);
    }

    /**
     * Safe: does not throw exceptions if searched element is not found, empty optional is returned instead
     *
     * @return
     */
    //TODO move to a lib?
    //TODO locators not here!
    private Optional<String> getCurrentClassNameInCaptionText() {

        try {
            By toolbarLocator = By.className("x-toolbar");
            By toolbarTextLocator = By.className("x-toolbar-text");
            String triggerTextForClassCaption = "Class:";

            WebElement toolbar = waitForPresenceOfNestedElement(getDriver(), toolbarLocator, cmdbuildAdministrationContentClassView());
            List<WebElement> toolbarTexts = toolbar.findElements(toolbarTextLocator);
            Optional<String> toolbarClassCaption = toolbarTexts.stream().map(tt -> Strings.nullToEmpty(tt.getText()).trim()).filter(t -> t.startsWith(triggerTextForClassCaption)).findFirst();
            if (toolbarClassCaption.isPresent()) {
                return Optional.of(toolbarClassCaption.get().replace(triggerTextForClassCaption, ""));
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {//the caption is not displayed
            return Optional.empty();
        }


    }



}
