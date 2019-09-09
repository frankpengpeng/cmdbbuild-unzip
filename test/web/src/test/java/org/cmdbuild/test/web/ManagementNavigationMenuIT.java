package org.cmdbuild.test.web;
import static org.cmdbuild.test.web.utils.ExtjsUtils.getCardGrid;
import static org.cmdbuild.test.web.utils.ExtjsUtils.safelyClickOnSideNavLeafWithChecksAgainst;
import static org.cmdbuild.test.web.utils.ExtjsUtils.checkCorrectClassIsDisplayed;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Random;

import org.cmdbuild.client.rest.RestClient;
import org.cmdbuild.test.web.utils.*;
import org.cmdbuild.test.web.utils.ExtjsNavigationMenu.MenuItem;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;


public class ManagementNavigationMenuIT extends BaseWebIT {
	
//	@Override
//	protected boolean noErrorsOnBrowserConsole() {
//		return fetchClientLog().supress("404").supress("401").success();
//	}
	
	private RestClient restClient;
	private Login clientCredentials = Login.admin();
	
	
	//TEST PARAMETERS
	private long defaultMaxExecutionTimeAllowed = 60 * SECONDS;
	private UITestRule defaultDurationRule = UITestRule.define("Duration", "Test execution took longer than allowed to complete"
			, c -> c.getMeasuredTimeSpent() < defaultMaxExecutionTimeAllowed);
	UITestRule relaxedCLientLogRule = UITestRule.defineClientLogCheckRule(null, ImmutableList.of("404","401","400","500"), null, null);
	
	@Test
	public void subClassSelectionTest() {
		
		UITestContext context = getDefaultTestContextInstance();
		context.withDescription("Sub class selection").withRule(defaultDurationRule).withRule(relaxedCLientLogRule);
		
		testStart(context);
		goFullScreen();
		
		login(Login.admin());
		
		safelyClickOnSideNavLeafWithChecksAgainst(getDriver(),"Computer", " computer", "Workplaces" , "All computers");
		ExtjsCardGrid grid = getCardGrid(getDriver());
		assertEquals(9 , grid.rows());
		assertEquals( 1 ,(grid.getRowsContainingAnyCells(new GridCell("Hostname", "laptop-external-01"))).size());
		
		safelyClickOnSideNavLeafWithChecksAgainst(getDriver(),"Notebook", " notebook", "Workplaces", "Notebooks");
		grid = getCardGrid(getDriver());
		assertEquals(5 , grid.rows());
		assertEquals( 1 ,(grid.getRowsContainingAnyCells(new GridCell("Hostname", "laptop-external-01"))).size());
		
		//checkClient();
		testEnd(context);
		
	}
	
	@Test
	public void firstItemOfNavigationMenuIsSelectedAfterLoginTest() {
		UITestContext context = getDefaultTestContextInstance();
		
		testStart(context);
		goFullScreen();
		login(Login.admin());
		
		assertTrue(getDriver().getCurrentUrl().contains("#classes/Employee/cards"));
		WebElement content = getDriver().findElement(By.id("CMDBuildManagementContent"));
		assertTrue(content.findElement(By.className("x-title-text")).getText().toLowerCase().contains("cards employee"));
		
		logout();
		
		sleep(1000);
		login(Login.admin());
		assertTrue(getDriver().getCurrentUrl().contains("#classes/Employee/cards"));
		WebElement content2 = getDriver().findElement(By.id("CMDBuildManagementContent"));
		assertTrue(content2.findElement(By.className("x-title-text")).getText().toLowerCase().contains("cards employee"));
		
		testEnd();
		
	}

	@Test  
	public void menuIsVisibleAndFirstItemSelectedTest() {
		
		testStart();
		goFullScreen();
		login(Login.admin());
		
		
		sleep(200);
		//MOVE OUT (test configuration)
		int expectedMenuItemsShownAtStartup = 14;
		int minDimensionForWebElementToBeConsideredVisible = 4;
		String expectedSelectedClassName = "Employee";
		String[] expectedSelectedClassSelectionPath = {"Employees" , "All employees"};
		Integer expectedSelectedClassCards = 7;

		
		//1: TODO check menu is visible
		ExtjsNavigationMenu menu = new ExtjsNavigationMenu(getDriver());
		List<MenuItem> l1 = menu.fetchFirstLevelNodes();
		//TODO rethink all constraints
		assertTrue(l1.size() == expectedMenuItemsShownAtStartup);
		assertTrue(l1.stream().allMatch(m -> m.isDisplayed() &&  (!Strings.isNullOrEmpty(m.getText())) 
				&& m.getSize().height > minDimensionForWebElementToBeConsideredVisible && m.getSize().width > minDimensionForWebElementToBeConsideredVisible));
		assertTrue(l1.stream().allMatch(m -> m.isFirstLevel()));
		
		//2: Check that right class is selected

		ExtjsCardGrid gridStartup = getCardGrid(getDriver());
		assertEquals(expectedSelectedClassCards, new Integer(gridStartup.rows()) );
		assertTrue(checkCorrectClassIsDisplayed(getDriver(),"Employee"));
		
		safelyClickOnSideNavLeafWithChecksAgainst(getDriver(), expectedSelectedClassName, expectedSelectedClassName.toLowerCase(), expectedSelectedClassSelectionPath);
		ExtjsCardGrid gridSelectedClass = getCardGrid(getDriver());
		assertEquals( gridSelectedClass.rows(), gridStartup.rows());
		for (int r = 0; r  < gridStartup.getRows().size(); r++) {
			List<GridCell> rowStartup = gridStartup.getRows().get(r);
			List<GridCell> rowSelected = gridSelectedClass.getRows().get(r);
			assertEquals(rowStartup.size() , rowSelected.size());
			for (int c = 0; c < rowSelected.size() ; c++ ) {
				assertTrue(rowStartup.get(c).matches(rowSelected.get(c)));
			}
		}
		
		checkNoErrorsOnBrowserConsole();
		testEnd();
		
	}
	
	@Test
	public void renderPdfReport() {
		UITestContext context = getDefaultTestContextInstance();
		context.withRule(UITestRule.defineClientLogCheckRule(null, ImmutableList.of("404", "401"), null, null));
		testStart(context);

		goFullScreen();
		login(Login.admin());
		waitForLoad();
		
		ExtjsUtils.safelylickOnSideNavLeaf(getDriver(), "Service desk" , "Report", "RF - Open processes");
		
		sleep(2000);
		
		assertTrue(getDriver().getCurrentUrl().contains("#reports/RF-Open/pdf"));
		
	}
	
	@Test
	public void pdfReportWithPopUp() {
		UITestContext context = getDefaultTestContextInstance();
		context.withRule(UITestRule.defineClientLogCheckRule(null, ImmutableList.of("404", "401"), null, null));
		testStart(context);

		goFullScreen();
		login(Login.admin());
		waitForLoad();
		
		ExtjsUtils.safelylickOnSideNavLeaf(getDriver(), "Service desk" , "Report", "Active tickets at a specified date");
		
		sleep(context);
		
		assertTrue(getDriver().getCurrentUrl().contains("#reports/ActiveTicketsInDate/pdf"));
		
		assertTrue(getDriver().findElementByXPath("//div[contains(@class, 'x-panel-management-closable')]//span[text()='Group']").isDisplayed());
		
		testEnd(context);
		
	}
	
	@Test
	public void addingNewProcessInstance() {
		UITestContext context = getDefaultTestContextInstance();
		context.withRule(UITestRule.defineClientLogCheckRule(null, ImmutableList.of("404", "401"), null, null));
		Random rand = new Random();
		
		testStart(context);
		
		goFullScreen();
		login(Login.admin());
		sleep(context);

		ExtjsUtils.safelyClickOnSideNavLeafWithChecksAgainst(getDriver(), "AssetMgt", "Workflow Asset management", "All items" , "Processes", "Asset management");
		sleep(context);
		
		ExtjsCardGrid grid1 = getCardGrid(getDriver());
		
		int rowsBeforeAddingNewProcess = grid1.getRows().size();
		
		ExtjsUtils.findElementByTestId(getDriver(), "processes-instances-grid-addbtn").click();
		sleep(context);
		
		ExtjsUtils.findElementByXPath(getDriver(), "//fieldset[@aria-label ='Base data field set']//div[@data-ref='triggerWrap']//div[contains(@class, 'x-form-arrow-trigger')]").click();
		sleep(context);
		
		ExtjsUtils.findElementByXPath(getDriver(), "//div[@data-ref='listWrap']//ul//li[@data-recordindex='"+rand.nextInt(9)+"']").click();
		sleep(context);
		
		ExtjsUtils.findElementByTestId(getDriver(), "processinstance-save").click();
		
		ExtjsCardGrid grid2 = getCardGrid(getDriver());
		
		int rowsAfterAddingNewProcess =  grid2.getRows().size();
		
		assertTrue(rowsAfterAddingNewProcess == rowsBeforeAddingNewProcess +1);
		
		testEnd(context);
	}
	

}
