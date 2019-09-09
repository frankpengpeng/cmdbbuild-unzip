package org.cmdbuild.test.web;

import static org.cmdbuild.test.web.utils.ExtjsUtils.clearFormTextField;
import static org.cmdbuild.test.web.utils.ExtjsUtils.getFormTextFieldContent;
import static org.cmdbuild.test.web.utils.UILocators.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static com.google.common.base.Strings.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.annotation.Nonnull;

import com.google.common.base.Splitter;
import org.cmdbuild.test.web.utils.BaseWebIT;
import org.cmdbuild.test.web.utils.ExtjsCardGrid;
import org.cmdbuild.test.web.utils.GridCell;
import org.cmdbuild.test.web.utils.ExtjsUtils;
import org.cmdbuild.test.web.utils.UITestContext;
import org.cmdbuild.test.web.utils.UITestDefaults;
import org.cmdbuild.test.web.utils.UITestRule;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import org.cmdbuild.test.web.utils.Login;

public class ManagementTabHistoryIT extends BaseWebIT {

    ////TEST PARAMETERS
    protected long tmsToleranceTresholdMillis = 3000L;

    //TAB HISTORY test parameters
    protected long testTHDurationMillis = 95 * SECONDS;
    protected List<String> testTHIgnoreClientErrors = ImmutableList.of("401", "404");

    ////TEST VARIABLES
    //TAB HISTORY test variables
    protected String testTHMainMenu = "Locations";
    protected String testTHSubMenu = "Buildings";
    protected String testTHTargetClass = "Building";
    protected String testTHTargetCardSelectionCriteria1Column = "Code";
    protected String testTHTargetCardSelectionCriteria1Content = "LMT";
    protected String testTHTargetCardSelectionCriteria2Column = "Description";
    protected String testTHTargetCardSelectionCriteria2Content = "Legg Mason Tower";
    protected String testTHTargetCardFieldToChange = "Postcode";

    Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * All in one test. This test:
     * <ul>
     * <li> opens card history of a selected element to check how many history versions the card has
     * <li> edits the card (thus adding 1 item to history)
     * <li> checks again the card history counting items
     * <li> checks date field is correct (TODO specs needed)
     */
    @Test
    public void tabHistoryTest() {

        UITestContext testContext = testStart();
        testContext.withRule(UITestRule.define("Duration", "Test execution took longer than allowed to complete",
                 c -> c.getMeasuredTimeSpent() < testTHDurationMillis));
        testContext.withRule(UITestRule.defineClientLogCheckRule(null, testTHIgnoreClientErrors, null, null));
        testStart(testContext);
        goFullScreen();

        login(Login.admin()); //also tested with defaultDemoUserCredentialsInstance

        //STEP 1: count current history items
        ExtjsUtils.safelylickOnSideNavLeaf(getDriver(), testTHMainMenu, testTHSubMenu);
        ExtjsCardGrid grid = ExtjsCardGrid.extract(getDriver(), testContext);
        List<Integer> idxs = grid.getIndexOfRowsCointainingAllCells(new GridCell(testTHTargetCardSelectionCriteria1Column, testTHTargetCardSelectionCriteria1Content),
                new GridCell(testTHTargetCardSelectionCriteria2Column, testTHTargetCardSelectionCriteria2Content));
        assertEquals(1, idxs.size());
        int idx = idxs.get(0);
        WebElement targetRow = grid.expandGridRow(idx);
        WebElement cmdbuildManagementDetailsWindow = grid.openCard(targetRow);
        // open tab view
        //TODO refactor using API version
        WebElement leftTab = cmdbuildManagementDetailsWindow.findElement(By.className("x-tab-bar-vertical"));
        WebElement spanHistory = leftTab.findElement(By.className("fa-history"));
        spanHistory.click();
        WebElement historyGrid = ExtjsUtils.waitForVisibilityOfNestedElement(getDriver(), cardDetailHistoryGrid(), cmdbuildManagementDetailsWindowLocator());
        List<WebElement> historyRows = historyGrid.findElements(By.tagName("table"));
        int cardHistoryItemsBefore = historyRows.size();
        logger.info("Card History Items (before): {}", cardHistoryItemsBefore);
        artificialSleep(2000); //VIDEO CMDBUILD DAY
        grid.closeCardDetails();
        waitForLoad();

        //STEP 2: modify the card to increment history items count
        grid = ExtjsCardGrid.extract(getDriver(), testContext);
        int targetRowIndex = grid.getIndexOfRowsCointainingAllCells(new GridCell(testTHTargetCardSelectionCriteria1Column, testTHTargetCardSelectionCriteria1Content),
                new GridCell(testTHTargetCardSelectionCriteria2Column, testTHTargetCardSelectionCriteria2Content)).get(0);
        if (grid.isGridRowExpanded(targetRowIndex)) {
            grid.collapseGridRow(targetRowIndex);
        }
        targetRow = grid.expandGridRow(targetRowIndex);
        grid.editCard();

        WebElement detailsForm = ExtjsUtils.getManagementDetailsWindow(getDriver());
        String fieldContent = getFormTextFieldContent(detailsForm, testTHTargetCardFieldToChange);
        logger.info("Field {} contains: {}", testTHTargetCardFieldToChange, fieldContent);
        artificialSleep(1200); //VIDEO CMDBUILD DAY
        clearFormTextField(getDriver(), detailsForm, testTHTargetCardFieldToChange);
        artificialSleep(1800); //VIDEO CMDBUILD DAY
        ExtjsUtils.fillFormTextField(detailsForm, testTHTargetCardFieldToChange, "" + (new Random().nextInt(98765)));
        artificialSleep(1800); //VIDEO CMDBUILD DAY
        ExtjsUtils.clickDetailFormButton(detailsForm, UITestDefaults.cardEditSaveButtonCaptionDefault);
        artificialSleep(1000, testContext);
        ExtjsUtils.closeDetaiForm(getDriver());

        LocalDateTime cardEditedTimestamp = LocalDateTime.now();

        //STEP 3: check history count incremented by 1
        artificialSleep(1000, testContext);
        targetRow = grid.expandGridRowCheckingIfAlreadyOpen(idx);
        cmdbuildManagementDetailsWindow = grid.openCard(targetRow);
        leftTab = cmdbuildManagementDetailsWindow.findElement(By.className("x-tab-bar-vertical"));
        spanHistory = leftTab.findElement(By.className("fa-history"));
        spanHistory.click();
        WebElement historyGridAfter = ExtjsUtils.waitForVisibilityOfNestedElement(getDriver(), cardDetailHistoryGrid(), cmdbuildManagementDetailsWindowLocator());
        List<WebElement> historyRowsAfter = historyGridAfter.findElements(By.tagName("table"));
        int cardHistoryItemsAfter = historyRowsAfter.size();
        logger.info("Card History Items (after): {}", cardHistoryItemsAfter);
        assertEquals(cardHistoryItemsBefore + 1, cardHistoryItemsAfter);

        //STEP 4: check timestamp of last edit is correct
        //fetch first and second item
        String lastBeginDateS = historyRowsAfter.get(0).findElements(By.tagName("td")).get(UITestDefaults.historyDetailsColumnBeginDateDefault).getText();
        String lastEndDateS = historyRowsAfter.get(0).findElements(By.tagName("td")).get(UITestDefaults.historyDetailsColumnEndDateDateDefault).getText();
        String previousEndDateS = historyRowsAfter.get(1).findElements(By.tagName("td")).get(UITestDefaults.historyDetailsColumnEndDateDateDefault).getText();

        //Using only times is a workaround simplyifing ui datetime representation management in test (italian / english / other locales, feautures not yet implemented in developmement stage, and so on...)
        LocalTime lastBeginTms = extractTimeFromUIDateTimeString(lastBeginDateS).get();
        LocalTime previousEndTms = extractTimeFromUIDateTimeString(previousEndDateS).get();

        logger.info("Card last version begin time: {}", lastBeginDateS);
        logger.info("Card last version end time: {}", lastEndDateS);
        logger.info("Card  previous version end time: {}", lastEndDateS);

        logger.debug("DEBUG: check conversion... {}", lastBeginTms);

        assertTrue(Strings.isNullOrEmpty(Strings.nullToEmpty(lastEndDateS).trim()));
        assertInstantsAreEqual(lastBeginTms, previousEndTms, tmsToleranceTresholdMillis);
        assertInstantsAreEqual(cardEditedTimestamp.toLocalTime(), previousEndTms, tmsToleranceTresholdMillis);

        //STEP 5 make sure dates are shown (or not)
        assertTrue("Last history item should not have any value in End date field", isNullOrEmpty(nullToEmpty(lastEndDateS).trim()));
        assertTrue("Last history item begin date must be shown (loose check)", lastBeginDateS.contains("" + lastBeginTms.getMinute()) && lastBeginDateS.contains("" + lastBeginTms.getHour()));

        artificialSleep(4000); //VIDEO CMDBUILD DAY

        testEnd(testContext);
    }

    //Abandoned since some quirks since 2018-11  //too many quirks / chanhges / cases  with UI datetime representation
    private void assertInstantsAreEqual(@Nonnull LocalDateTime instant1, @Nonnull LocalDateTime instant2,
            long tmsToleranceTresholdMillis) {

        assertNotNull(instant1);
        assertNotNull(instant2);
        //logger.debug("Timestamp difference:  {} between {}, {}" , Math.abs(ChronoUnit.MILLIS.between(instant1, instant2)) , instant1.toString(), instant2.toString());
        assertTrue(Math.abs(ChronoUnit.MILLIS.between(instant1, instant2)) <= tmsToleranceTresholdMillis);

    }

    private Optional<LocalTime> extractTimeFromUIDateTimeString(@Nonnull String dateTimeString) {
        try {
            List<String> dateTimeTokens = Splitter.on(" ").trimResults().omitEmptyStrings().splitToList(dateTimeString);
            return Optional.ofNullable(LocalTime.parse(dateTimeTokens.get(dateTimeTokens.size() - 1)));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private void assertInstantsAreEqual(@Nonnull LocalTime instant1, @Nonnull LocalTime instant2,
            long tmsToleranceTresholdMillis) {
        assertNotNull(instant1);
        assertNotNull(instant2);
        logger.info("Timestamp difference:  {} between {}, {}", Math.abs(ChronoUnit.MILLIS.between(instant1, instant2)), instant1.toString(), instant2.toString());
        assertTrue(Math.abs(ChronoUnit.MILLIS.between(instant1, instant2)) <= tmsToleranceTresholdMillis);

    }

}
