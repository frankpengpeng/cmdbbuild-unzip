package org.cmdbuild.test.web;

import static org.cmdbuild.test.web.utils.ExtjsUtils.*;
import static org.cmdbuild.test.web.utils.UILocators.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.gargoylesoftware.htmlunit.javascript.background.JavaScriptExecutor;
import org.apache.commons.lang.RandomStringUtils;
import org.cmdbuild.test.web.utils.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class ManagementCardDetailsMiscellaneousIT  extends BaseWebIT {

    @After
    public void cleanup() {
        cleanupDB();
    }


    private String[] notes_TargetClasstreePath = {"Software" , "Applications"};
    private String[] notes_OtherClasstreePath = {"Locations" , "Buildings"};
    private String notes_TuouchedClass = "Application";
    private String notes_idFieldName ="Version"; //must be unique for each field; used because order may change
    private String randomContent = "test random content " + RandomStringUtils.randomAlphabetic(5);
    /**
     * check that edit notes in dedicated tab of Card details works...
     */
    @Test
    public void notesTabTest() {

        UITestContext context = getDefaultTestContextInstance();
        context.withRule(getDefaultCLientLogCheckRule());
        context.withTouchedClass(notes_TuouchedClass);

        testStart(context);
        goFullScreen();
        login(Login.admin());
        waitForLoad();
        safelyClickOnSideNavLeafWithChecksAgainst(getDriver(), notes_TuouchedClass , notes_TuouchedClass, notes_TargetClasstreePath);
        ExtjsCardGrid grid = ExtjsCardGrid.extract(getDriver(), context);
        WebElement firstRow = grid.expandGridRow(0);
        WebElement details = grid.openCard(firstRow);
        waitForLoad();
        WebElement detailsWindowTabNotesContent = grid.openCardsDetailTabNotes(details);
        GridCell idCell = grid.getRows().get(0).stream().filter(c -> notes_idFieldName.equals(c.getName())).findFirst().get();

        WebElement editButton = ExtjsUtils.findElementByTestId(getDriver(), "notes-panel-editbtn");
        safeClick(getDriver(),editButton);
        waitForLoad();
        //edit content
        WebElement editor = waitForVisibilityOfNestedElement(getDriver(), locatorCardDetailsWindowTabNotesEditor(), detailsWindowTabNotesContent);
        getDriver().switchTo().frame(editor);
        artificialSleep(300, context);
        WebElement editorBody = waitForElementPresence(By.tagName("body"));
        editorBody.click();
        editorBody.sendKeys(randomContent);
        getDriver().switchTo().defaultContent();
        //save
        WebElement saveButton = ExtjsUtils.findElementByTestId(getDriver(), "notes-panel-savebtn");
        safeClick(getDriver(), saveButton);
        waitForLoad();
        artificialSleep(1500, context);

        WebElement windowBodyContent = ExtjsUtils.findElementByTestId(getDriver(), "cards-card-detailsWindow");
        
        String notesContentAfterEdit = windowBodyContent.getText();
        
        assertTrue("Edited notes must contain random test injected", notesContentAfterEdit.contains(randomContent));

        // exit card and change class
        clickOnSideNavLeaf (getDriver(), notes_OtherClasstreePath);
        ExtjsCardGrid.extract(getDriver(), context);

        //return in notes and check edited notes are still there...
        safelyClickOnSideNavLeafWithChecksAgainst(getDriver(), notes_TuouchedClass , notes_TuouchedClass, notes_TargetClasstreePath);
        grid = ExtjsCardGrid.extract(getDriver(), context);
        firstRow = grid.expandGridRow(grid.getIndexOfRowsCointainingAllCells(idCell).get(0));
        
        details = grid.openCard(firstRow);
        sleep(context);
        detailsWindowTabNotesContent = grid.openCardsDetailTabNotes(details);
        WebElement windowBodyContent2 = ExtjsUtils.findElementByTestId(getDriver(), "cards-card-detailsWindow");
        String notesContentComingBack = windowBodyContent2.getText();
        assertTrue( "" , notesContentComingBack.contains(randomContent) );
        assertTrue( "" , notesContentComingBack.trim().equals(notesContentAfterEdit.trim()) );
        testEnd(context);
    }


    private String randomContentDiscard = "test random content " + RandomStringUtils.randomAlphabetic(5);

   
    
    
    @Test
    public void notesTabDiscardEditTest() {

        UITestContext context = getDefaultTestContextInstance();
        context.withRule(getDefaultCLientLogCheckRule());

        testStart(context);
        goFullScreen();
        login(Login.admin());
        waitForLoad();
        safelyClickOnSideNavLeafWithChecksAgainst(getDriver(), notes_TuouchedClass , notes_TuouchedClass, notes_TargetClasstreePath);
        ExtjsCardGrid grid = ExtjsCardGrid.extract(getDriver(), context);
        WebElement firstRow = grid.expandGridRow(0);
        WebElement details = grid.openCard(firstRow);
        waitForLoad();
        WebElement detailsWindowTabNotesContent = grid.openCardsDetailTabNotes(details);

        String notesContentBeforeEdit = grid.getTabNotesContent();
        logger.info("Notes content (before insert): {} " , notesContentBeforeEdit );
        //edit content
        WebElement editButton = ExtjsUtils.findElementByTestId(getDriver(), "notes-panel-editbtn");
        safeClick(getDriver(),editButton);
        waitForLoad();
        artificialSleep(2000, context);
        WebElement editor = waitForVisibilityOfNestedElement(getDriver(), locatorCardDetailsWindowTabNotesEditor(), detailsWindowTabNotesContent);
        getDriver().switchTo().frame(editor);
        artificialSleep(300, context);
        WebElement editorBody = waitForElementPresence(By.tagName("body"));
        editorBody.click();
        editorBody.sendKeys(randomContentDiscard);
        getDriver().switchTo().defaultContent();
        //cancel
        WebElement footerToolbar = detailsWindowTabNotesContent.findElement(locatorCardDetailsWindowTabNotesFooterToolbarEditMode());
        WebElement cancelButton = ExtjsUtils.findElementByTestId(getDriver(), "notes-panel-cancelbtn");
        safeClick(getDriver(), cancelButton);
        waitForLoad();
        artificialSleep(1500, context);
 
        String notesContentAfterDiscardedEdit = grid.getTabNotesContent();
        logger.info("Notes content (after insert): {} " , notesContentAfterDiscardedEdit);
        assertFalse("Edited notes must not contain random test injected", notesContentAfterDiscardedEdit.contains(randomContentDiscard));
        assertTrue("Content after discarded edit should be the the same of original one", notesContentAfterDiscardedEdit.equals(notesContentBeforeEdit));

        testEnd(context);
    }

   
}
