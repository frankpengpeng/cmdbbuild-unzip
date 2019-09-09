package org.cmdbuild.test.web;

import static org.cmdbuild.test.web.utils.ExtjsUtils.*;
import static org.cmdbuild.test.web.utils.UIDefinitions.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.RandomStringUtils;
import org.cmdbuild.client.rest.RestClient;
import org.cmdbuild.client.rest.api.LookupApi;
import org.cmdbuild.test.web.utils.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WorkflowBasicsIT extends BaseWebIT {

    //TEST rules -related //move to BaseWebIT
    UITestRule defaultCLientLogRule = UITestRule.defineClientLogCheckRule(null, ImmutableList.of("404", "401"), null, null);
    UITestRule relaxedClientLogRule = UITestRule.defineClientLogCheckRule(null, ImmutableList.of("404", "401", "400"), null, null);
    UITestRule veryPermissiveClientLogRule = UITestRule.defineClientLogCheckRule(null, ImmutableList.of("404", "401", "400", "500",
            "Cannot read property 'close' of undefined"), null, null);

    long veryLongTestDurationMillis = 250 * SECONDS; //TODO move out, to be used when UI performance is not of concern or test is quick
    private RestClient restClient;

    @After
    public void cleanup() {
        cleanupDB();
    }

    @Before
    public void startWorkflow() {
        restClient = getRestClient();
        restClient.system().setConfig("org.cmdbuild.workflow.enabled", "true");
        restClient.system().setConfig("org.cmdbuild.workflow.providers", "river");
        restClient.workflow().uploadPlanVersion("AssetMgt",
                WorkflowBasicsIT.class.getResourceAsStream("/org/cmdbuild/test/workflow/assetmgt.xpdl"));
    }

    protected Login defaultWorkflowUserCredentialsInstance() {
        return Login.demo().withRole("Software helpdesk");
    }

    //TEST WORKFLOWDETAILSARESHOWN
    List<String> workflowAllInOneTest_TouchedClasses = ImmutableList.of("_Flow", "IncidentMgt");
    String[] workflowAllInOneTest_TreePathWorkflow = {"Service desk", "Incident management"};
    String[] workflowAllInOneTest_TreePathOther = {"Customers", "Customer"};
    String workflowAllInOneTest_ExpectedUrlFragment = "#processes/IncidentMgt";
    String workflowAllInOneTest_ExpectedTitle = "Workflow Incident management";
    String randSuffix = RandomStringUtils.randomAlphabetic(4);
    List<FormField> workflowAllInOneTest_processFormFields = ImmutableList.of(FormField.of("Requester", "Anderson Aaron"),
            FormField.of("Short description", "TEST-" + randSuffix), FormField.of("Channel", "Phone"));
    int workflowAllInOneTest_maxInstancesToCheck = 5;
    String workflowAllInOneTest_ReferenceFieldToCheck = uiDefinitionWorkflowFieldName_Requester();
    String workflowAllInOneTest_LookupFieldNameToCheck = "Channel";
    String workflowAllInOneTest_LookupClassToCheck = "Channel";
    String[] workflowAllInOneTest_ReferenceToCheckMenuPath = {"Employees", "All employees"}; //TODO  a more specific class can probabily be found
    String workflowAllInOneTest_ReferenceDescriptionFieldName = "Last name";
    String workflowAllInOneTest_ReferenceClass = "Employee"; //only used in cheks menu class selection
    String workflowAllInOneTest_LookupId = "ITProc - Channel"; //only used in cheks menu class selection
    //TEST SAVE BUTTON MUST CALL REST WS ONLY ONCE
    String[] onlyOneRestCall_TreePathWorkflow = {"Service desk", "Incident management"};
    String onlyOneRestCall_ExpectedUrlFragment = "#processes/IncidentMgt";
    List<FormField> onlyOneRestCall_processFormFields = ImmutableList.of(FormField.of("Requester", "Anderson Aaron"),
            FormField.of("Short description", "TEST-" + randSuffix), FormField.of("Channel", "Phone"));
    String onlyOneRestCall_sqlCountRestCalls = "select count(*) from \"_Request\" where \"Method\" <> 'GET' and \"Timestamp\" > ? and \"Path\" ilike '%/services/rest/v3/processes/IncidentMgt/instances%' ";

    /**
     * This test performs several checks:
     * <ul>
     *     <li><a new instance process is created, persisted  and listed (state is not checked as it depends on process definition)</li>
     *     <li><process details are shown</li>
     *     <li><process details are shown consistently in each of the three views (flat grid row, expanded grid row, details window</li>
     *     <li><activity name is set (in details window title)</li>
     *     <li><execution role is present</li>
     *     <li><reference fields show the description (both in grid and in details window?)</li>
     *     <li><lookups show the description (both in grid and in details window?)</li>
     *     <li><freshly created  instance should be shown without explicit refresh of the page</li>
     * </ul>
     *
     * It creates an instance (removed after test) to make sure there is at least one instance to check
     */
    // This test is known to randomly raise errors 500 and Cannot read property 'close' of undefined on the client side.
    // These errors are not blocking,
    @Test
    public void workflowAllInOneTest() {

        UITestContext context = getDefaultTestContextInstance();

        context.withRule(veryPermissiveClientLogRule);

        testStart(context);
        goFullScreen();

        login(Login.admin());

        safelyClickOnSideNavLeafWithChecksAgainstUrlFragment(getDriver(), workflowAllInOneTest_ExpectedUrlFragment, workflowAllInOneTest_TreePathWorkflow);
        sleep(context);
        ExtjsWorkflowGrid grid = ExtjsWorkflowGrid.extract(getDriver(), context);
        int processesBefore = grid.rows(); //TODO check if processes are pruned from view when finished
        //below DOES NOT WORK because not some fields are only displayed in detail view

//        int processesAsOneInsertedBefore = grid.getIndexOfRowsCointainingAllCells(GridCell.formfieldsToCellArray(workflowAllInOneTest_processFormFields)).size(); //near always 0
        int processesAsOneInsertedBefore = grid.getIndexOfRowsCointainingAllDisplayedCells(GridCell.formfieldsToCellArray(workflowAllInOneTest_processFormFields)).size(); //near always 0
        WebElement startProcessForm = grid.openStartProcessForm();
        sleep(context);
        startProcessForm.findElement(By.name("Requester")).sendKeys("Anderson Aaron");
        sleep(context);
        startProcessForm.findElement(By.name("ShortDescr")).sendKeys("TEST-" + randSuffix);
        sleep(context);
        startProcessForm.findElement(By.name("OpeningChannel")).sendKeys("Phone");
        startProcessForm.findElement(By.name("ShortDescr")).click();
        sleep(context);

        //before saving a new instance, only two tokens are present in window title (process short description is missing...)
        assertTrue("Activity name must be shown in details window title", checkActivityNameIsShownInWindowTitle(startProcessForm, 2));
        sleep(context);
        startProcessForm.findElement(By.xpath("//div[@data-testid='processinstance-execute']")).click();
        sleep(context);
        ExtjsUtils.findElementByTestId(getDriver(), "processinstance-cancel").sendKeys(Keys.ENTER);

        //check new instance is shown after insert without having to force refresh
        sleep(context);
        ExtjsWorkflowGrid gridImmediatelyAfterNewProcessCreation = ExtjsWorkflowGrid.extract(getDriver());
        int processesAsOneInsertedImmediatelyAfter = gridImmediatelyAfterNewProcessCreation.getIndexOfRowsCointainingAllDisplayedCells(GridCell.formfieldsToCellArray(workflowAllInOneTest_processFormFields)).size(); //near always 0
        assertTrue("There should be more processes (like the one freshly inserted) immediately after workflow instance addition", processesAsOneInsertedBefore < processesAsOneInsertedImmediatelyAfter);

        sleep(context);
        safelylickOnSideNavLeaf(getDriver(), workflowAllInOneTest_TreePathOther);
        sleep(context);
        safelylickOnSideNavLeaf(getDriver(), workflowAllInOneTest_TreePathWorkflow);
        sleep(context);
        //check new process is listed
        ExtjsWorkflowGrid gridAfter = ExtjsWorkflowGrid.extract(getDriver(), context);

//        assertTrue(processesAsOneInsertedBefore < gridAfter.getIndexOfRowsCointainingAllCells(GridCell.formfieldsToCellArray(workflowAllInOneTest_processFormFields)).size() );
        assertTrue(processesAsOneInsertedBefore < gridAfter.getIndexOfRowsCointainingAllDisplayedCells(GridCell.formfieldsToCellArray(workflowAllInOneTest_processFormFields)).size());

        // check activity name is shown (for all or max istances) , if column is enabled
        assertTrue("All instances must have " + uiDefinitionWorkflowFieldName_ActivityName() + "set",
                (!gridAfter.getFields().contains(uiDefinitionWorkflowFieldName_ActivityName()))
                || gridAfter.getRows().stream().limit(workflowAllInOneTest_maxInstancesToCheck)
                        .allMatch(gr -> gr.stream().anyMatch(cell -> (uiDefinitionWorkflowFieldName_ActivityName().equalsIgnoreCase(cell.getName())) && (!Strings.isNullOrEmpty(cell.getContent())))));
        //TODO also check role exists
        assertTrue("All instances must have " + uiDefinitionWorkflowFieldName_CurrentRole() + "set",
                //5 check execution role is set(for all or max istances)
                gridAfter.getRows().stream().limit(workflowAllInOneTest_maxInstancesToCheck)
                        .allMatch(gr -> gr.stream().anyMatch(cell -> (uiDefinitionWorkflowFieldName_CurrentRole().equalsIgnoreCase(cell.getName())) && (!Strings.isNullOrEmpty(cell.getContent())))));

        //check lookups are displayed showing description
        List<LookupApi.LookupValue> lookupValuesFromRest = getRestClient().lookup().getValues(workflowAllInOneTest_LookupId);
        List<String> lookupValues = lookupValuesFromRest.stream().map(l -> l.getDescription()).collect(Collectors.toList());
        logger.info("Lookup values for lookup-id {} retrieved from rest are: {}", workflowAllInOneTest_LookupId, lookupValues);

        IntStream.range(0, Math.min(gridAfter.rows(), workflowAllInOneTest_maxInstancesToCheck)).forEach(i -> {
            WebElement details = gridAfter.safelyEpandGridRow(i);
            //FIXME: first time row is accessed a ws call is made to retrieve data, so SOMETIMES we HAVE TO WAIT FOR data to be retrieved.
            //PLEASE INTRODUCE a non trivial wait upon this
            sleep(context);
            //sleepDeprecated(1000);
            List<FormField> instanceFields = gridAfter.fetchFormFieldsInExpandedRowSection(details);
            logger.trace("Instance Fields are: {}", String.join(" , ", instanceFields.stream().map(ff -> FormField.asString(ff)).collect(Collectors.toList())));
            String lookupValueToCheck = instanceFields.stream().filter(ff -> workflowAllInOneTest_LookupFieldNameToCheck.equals(ff.getName())).findFirst().get().getContent();
            logger.info("Lookup value to check is: {}", lookupValueToCheck);
            lookupValueToCheck = Strings.nullToEmpty((lookupValueToCheck)).trim();//relaxing a bit the condition (non mandatory, dirty db, etc.)
            if (Strings.isNullOrEmpty(lookupValueToCheck)) {
                logger.info("Skipping empty lookup value check...");
            } else {
                assertTrue("Lookup values found in process instances must be in Lookup values. Lookup class being checked is: " + workflowAllInOneTest_LookupClassToCheck + "and missing value is:" + lookupValueToCheck,
                        lookupValues.contains(lookupValueToCheck));
            }
        }
        );

        //check references are displayed showing description field
        List<String> referencesToCheck = gridAfter.getRows().stream().map(gr -> gr.stream().filter(cell -> workflowAllInOneTest_ReferenceFieldToCheck.equals(cell.getName())).findFirst())
                .map(cell -> cell.get().getContent()).collect(Collectors.toList());
        //before moving away check random process has detail windows with 3 tokens (so it has also activity name
        WebElement window = gridAfter.openProcessCard(0);
        String lastName = referencesToCheck.get(0).substring(0, 8);

        logger.info(lastName + "<----------1");

        assertTrue("Activity name must be shown in details window title", checkActivityNameIsShownInWindowTitle(window, 3));
        //now continue checking references..
        safelyClickOnSideNavLeafWithChecksAgainstUrlFragment(getDriver(), workflowAllInOneTest_ReferenceClass, workflowAllInOneTest_ReferenceToCheckMenuPath); //deprecated
        ExtjsCardGrid gridReferenceToCheck = ExtjsCardGrid.extract(getDriver(), context);
        List<String> referenceValues = gridReferenceToCheck.getRows().stream().map(r -> r.stream().filter(cell -> workflowAllInOneTest_ReferenceDescriptionFieldName.equals(cell.getName())).findFirst())
                .map(cell -> cell.get().getContent()).collect(Collectors.toList());

        assertTrue("All references of process instances must be shown in " + workflowAllInOneTest_ReferenceDescriptionFieldName + " field of referenced class ",
                referenceValues.contains(lastName));

        safelylickOnSideNavLeaf(getDriver(), workflowAllInOneTest_TreePathWorkflow);
        sleep(context);//remove all this sh...
        final ExtjsWorkflowGrid gridAfter2 = ExtjsWorkflowGrid.extract(getDriver(), context);
        //moved here because after insertion grid refresh asks for the number of items the grid originally had
        if (gridAfter2.rows() < uiDefinitionGridVirtualizationThreshold()) {
            assertTrue("There should be more processes after workflow instance addition", processesBefore < gridAfter2.rows());
        } else {
            logger.warn("Skipped active process instances count because grid may be virtualized");
        }

        IntStream.range(0, Math.min(gridAfter.rows(), workflowAllInOneTest_maxInstancesToCheck)).forEach(i -> {
            List<GridCell> gridFields = gridAfter2.getRows().get(i);
            WebElement inPlaceDetails = gridAfter2.safelyEpandGridRow(i);
            sleep(context);
            List<FormField> inPlaceInstanceFields = gridAfter2.fetchFormFieldsInExpandedRowSection(inPlaceDetails);

            logger.info("Process: grid row representation: {}", GridCell.asString(gridFields));
            logger.info("Process: expanded grid row representation: {}", FormField.asString(inPlaceInstanceFields));

            assertTrue("No fields with same name but different content should exists in two different views of the same process instance",
                    gridFields.stream().noneMatch(gc1 -> GridCell.formfieldsToCells(inPlaceInstanceFields).stream().anyMatch(
                    gc2 -> gc2.getName().equals(gc1.getName()) && (!Strings.nullToEmpty(gc2.getContent()).trim().equals(Strings.nullToEmpty(gc1.getContent()).trim())))));
        });

        testEnd(context);
    }

    private boolean checkActivityNameIsShownInWindowTitle(WebElement startProcessForm, int expectedTokens) {
        WebElement title = waitForVisibilityOfNestedElement(getDriver(), By.className("x-title-text"), startProcessForm);
        List<String> tokens = Splitter.on("—").trimResults().omitEmptyStrings().splitToList(title.getText());
        logger.info("Title tokens: {} , tokens are : {}", tokens.size(), tokens.toString());
        return (tokens.size() == expectedTokens);
    }

    /**
     * This test checks only one call to rest ws is done by client when saving a process instance.
     * Referenced bug: #426
     * Tries to recycle a random IncidentManagement if there is one.
     * KNOWN TO FAIL
     */
    @Test
    public void saveButtonMustIssueOnlyOneRestCallTest() {

        UITestContext context = getDefaultTestContextInstance();
        context.withRule(relaxedClientLogRule);
        testStart(context);
        goFullScreen();

        login(Login.admin());

        safelyClickOnSideNavLeafWithChecksAgainstUrlFragment(getDriver(), onlyOneRestCall_ExpectedUrlFragment, onlyOneRestCall_TreePathWorkflow);
        artificialSleep(1000);
        ExtjsWorkflowGrid grid = ExtjsWorkflowGrid.extract(getDriver());

        if (grid.rows() == 0) {

            WebElement startProcessForm = grid.openStartProcessForm();
            sleep(context);
            startProcessForm.findElement(By.name("Requester")).sendKeys("Anderson Aaron");
            sleep(context);
            startProcessForm.findElement(By.name("ShortDescr")).sendKeys("TEST-" + randSuffix);
            sleep(context);
            startProcessForm.findElement(By.name("OpeningChannel")).sendKeys("Phone");
            startProcessForm.findElement(By.name("ShortDescr")).click();
            sleep(context);
            startProcessForm.findElement(By.xpath("//div[@data-testid='processinstance-execute']")).click();
            sleep(context);
            ExtjsUtils.findElementByTestId(getDriver(), "processinstance-cancel").sendKeys(Keys.ENTER);
            sleep(context);
            grid = ExtjsWorkflowGrid.extract(getDriver());
        }

        grid.editProcessCard(0);
        sleep(context);

        LocalDateTime tmsBeforeSavingProcess = LocalDateTime.now();
        int restCallsBefore = getJdbcTemplate().queryForObject(onlyOneRestCall_sqlCountRestCalls, Integer.class, java.sql.Timestamp.valueOf(tmsBeforeSavingProcess)); //should be 0
        getDriver().switchTo().activeElement().findElement(By.xpath("//div[@data-testid='processinstance-save']")).click();
        sleep(context);
        int restCallsAfter = getJdbcTemplate().queryForObject(onlyOneRestCall_sqlCountRestCalls, Integer.class, java.sql.Timestamp.valueOf(tmsBeforeSavingProcess)); //should be 1
        assertEquals("Only one rest call must be issued when saving a process",
                restCallsBefore + 1, restCallsAfter);

        testEnd(context);

    }
}
