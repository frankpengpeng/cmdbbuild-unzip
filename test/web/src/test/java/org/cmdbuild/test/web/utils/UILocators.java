package org.cmdbuild.test.web.utils;

import org.openqa.selenium.By;

//FIXME:CLEANUP  make names uniform (use locator always or never)
public class UILocators {


	//Message box (error / successpopups)
	public static By locatorMsgBoxWindow() { return locatorMsgBoxWindow;}
	public static By locatorMsgBoxTitle() { return locatorMsgBoxTitle;}
	public static By locatorMsgBoxText() { return locatorMsgBoxText;}


	public static By cmdbuildManagementDetailsWindowLocator() {return cmdbuildManagementDetailsWindowLocator;}
	public static By cmdbuildManagementContentLocator() {return cmdbuildManagementContentLocator;}
	public static By cmdbuildManagementContentBodyLocator() {return cmdbuildManagementContentBodyLocator;}
	public static By cardDetailHistoryGrid() {return cardDetailHistoryGrid;}
	public static By cardDetailsRelationsGrid() {return cardDetailsRelationsGrid;}
	public static By cardDetailsCloseButton() {return cardDetailsCloseButton;}
	public static By messageBoxLocator() {return By.className("x-message-box");}
	public static By messageBoxButtonLocator() {return By.className("x-btn");}
	//grid (all types) related
	public static  By gridRowsTable() {return rowsTable;}
	//workflow related
	public static By workflowXGrid() {return workflowXGrid ;}
	public static By workflowHeader() {return workflowHeader ;}
	public static By workflowHeaderColumn() {return workflowHeaderColumn ;}
	public static By workflowGridRowsTable() {return workflowGridRowsTable ;}
	public static By workflowGridRowEditButton() {return workflowGridRowEditButton;}
	public static By workflowGridRowOpenButton() {return workflowGridRowOpenButton;}
	public static By workflowGridRowDeleteButton() {return workflowGridRowDeleteButton;}
	/**
	 * @return Process detail floating window
	 */
	public static By workflowProcessDetailWindow() {return  workflowProcessDetailWindow;}


	//In Row Form (content of expanded row in first level grid)
	public static By locatorInRowFormEditButton() {return locatorInRowFormEditButton;}



	//Card details view (open card details from expanded cards, tabbed (side) window
	//TODO sort (first split into general and then specific tab related)
	public static By locatorCardDetailsWindow() {return locatorCardDetailsWindow;}
	public static By locatorCardDetailsWindowHeader() {return locatorCardDetailsWindowHeader;}
	/**
	 * contains content, toolbars and side tab menu
	 */
	public static By locatorCardDetailsWindowBody() {return locatorCardDetailsWindowBody;}
	/**
	 *  Nested search only.
	 *  Only content, no toolbars and no side menu content, must be used.
	 */
	public static By locatorCardDetailsWindowBodyContent() {return locatorCardDetailsWindowBodyContent;}
	/**
	 * search for tag anchor , with class x-btn containing this icon
	 */
	public static By locatorCardDetailsWindowTabNotesEditButtonIcon() {return locatorCardDetailsWindowTabNotesEditButtonIcon;}
	public static By locatorOpenCardButton() {return locatorOpenCardButton; }
	/**
	 *	This is where notes are displayed.
	 *	Content is html
	 */
	public static By locatorCardDetailsWindowTabNotesContentPanelreadOnly() {return locatorCardDetailsWindowTabNotesContentPanelreadOnly;}

	/**
	 * @return notes editor
	 */
	public static By locatorCardDetailsWindowTabNotesEditor() {return locatorCardDetailsWindowTabNotesEditor;}
	/**
	 * this toolbar is shown only when editing, and contains Save and Cancel Buttons
	 */
	public static By locatorCardDetailsWindowTabNotesFooterToolbarEditMode() {return locatorCardDetailsWindowTabNotesFooterToolbarEditMode;}

	/**
	 * @return the container div of pupup open when clicked edit relation button from a relation in card's relation tab
	 */
	public static By locatorEditRelationsPopup() {return locatorEditRelationsPopup;}



	/**
	 * @return the ghost (floating) process (edit) window that pops when you execute a process (TODO review definition when more use cases encountered)
	 */
	public static By workflowProcessGhostWindow() {return workflowProcessGhostWindow;}

	@Deprecated //probably wrong locator
	public static By workflowProcessGhostWindowBody() {return workflowProcessGhostWindowBody;}
	/**
	 * @return the toolbar containing action buttons in workflowProcessEditGhostWindow
	 */
	public static By workflowProcessEditGhostWindowToolbar() {return workflowProcessGhostWindowToolbar;}

	public static By workflowProcessDetailWindowCloseButton() {return workflowProcessDetailWindowCloseButton;}
	public static By cardDetailsCloseToolButton() {return cardDetailsCloseToolButton;}

	//NAVBAR related

	public static By headerAdministrationButton() {return headerAdministrationButton;}
	public static By headerUserMenuDropDown() {return headerUserMenuDropDown;}

	//General
	public static By cmdbuildMainContent() {return cmdbuildMainContent;}
	public static By button() {return button;}
	//for example: html content in card notes...
	public static By locatorAutocontainerInnerCt() {return locatorAutocontainerInnerCt;}

	/**
	 * @return admin content (contains header and body)
	 */
	//ADMIN MODULE related
	public static By cmdbuildAdministrationContent() {return cmdbuildAdministrationContent;}
	public static By cmdbuildAdministrationContentClassView() {return cmdbuildAdministrationContentClassView;}
	public static By cmdbuildAdministrationHeaderTitle() {return cmdbuildAdministrationHeaderTitle;}

	public static By cmdbuildAdministrationClassTab() {return cmdbuildAdministrationClassTab;}
	public static By cmdbuildAdministrationAttributeTabContent() {return cmdbuildAdministrationAttributeTabContent;}

	///MISCELLANEOUS
	public static By locatorForDropDownMenuItems() {return locatorForDropDownMenuItems;}
	
	
	/////////////////////////////////////////////////////


	//MsgBox (error,success)

	private static By locatorMsgBoxWindow = By.xpath("//div[@data-testid='message-window']");
	private static By locatorMsgBoxTitle = By.xpath("//*[@data-testid='message-window-title']");
	private static By locatorMsgBoxText = By.xpath("//*[@data-testid='message-window-text']");


	//General

	private static By button = By.className("x-btn");

	private static By cmdbuildMainContent = By.id("CMDBuildMainContent");

	private static By cmdbuildManagementDetailsWindowLocator = By.id("CMDBuildManagementDetailsWindow");
	private static By cmdbuildManagementContentLocator = By.id("CMDBuildManagementContent");
	private static By cmdbuildManagementContentBodyLocator = By.id("CMDBuildManagementContent-body");
	//	private static By cardDetailHistoryGrid = By.className("x-panel-default");
	private static By cardDetailHistoryGrid = By.className("x-grid-item-container");
	private static By cardDetailsCloseButton = By.xpath("//div[@data-qtip='Close dialog']");
	private static By cardDetailsRelationsGrid = By.className("x-grid-with-row-lines");

	private static By locatorOpenCardButton = By.xpath("//div[@data-testid='cards-card-view-openBtn']");

	//Card details view (open card details from expanded cards, tabbed (side) window
	//use By.id("CMDBuildManagementDetailsWindow") as root locator, then
	private static By locatorCardDetailsWindow = By.id("CMDBuildManagementDetailsWindow");
	private static By locatorCardDetailsWindowHeader = By.id("CMDBuildManagementDetailsWindow-ghost_header-title");
	private static By locatorCardDetailsWindowBody = By.id("CMDBuildManagementDetailsWindow-body");
	private static By locatorCardDetailsWindowBodyContent = By.className("x-panel-body");
	private static By locatorCardDetailsWindowTabNotesEditButtonIcon = By.className("fa-pencil");
	private static By locatorCardDetailsWindowTabNotesContentPanelreadOnly = By.className("x-autocontainer-innerCt");
	private static By locatorCardDetailsWindowTabNotesEditor = By.className("x-htmleditor-iframe");
	private static By locatorCardDetailsWindowTabNotesFooterToolbarEditMode = By.className("x-toolbar-footer");
	private static By locatorEditRelationsPopup = By.id("popup-edit-relation");
	// Toolbar buttons save and cancel: -> search for tag <a> with class x-btn and gettext to differentiate between buttons

	private static By locatorAutocontainerInnerCt = By.className("x-autocontainer-innerCt");

	//In Row Form
	private static By locatorInRowFormEditButton =  By.xpath("//div[@data-testid='cards-card-view-editBtn']");

	//Grid (all types) related
	private static By rowsTable = By.tagName("table");

	//workflow related
	private static By cardDetailsCloseToolButton = By.className("x-tool-close");
	private static By workflowXGrid = By.className(("x-grid"));
	private static By workflowHeader = By.className(("x-grid-header-ct"));
	private static By workflowHeaderColumn = By.className(("x-column-header"));
	private static By workflowGridRowsTable = By.tagName("table");
	private static By workflowProcessDetailWindow = By.xpath("//div[@data-testid='cards-card-detailsWindow']");
	private static By workflowProcessGhostWindow = By.id("CMDBuildManagementDetailsWindow-ghost");
	//FIXME: probably wrong
	private static By workflowProcessGhostWindowBody = By.id("CMDBuildManagementDetailsWindow-body");
	private static By workflowProcessGhostWindowToolbar = By.className("x-toolbar-footer");
	private static By workflowProcessDetailWindowCloseButton = By.className("x-tool-close");
	//FIXME: duplicate of locatorInRowFormEditButton (possibly a lot of cases after grids have been unified
	private static By workflowGridRowEditButton = By.xpath("//div[@data-testid='processes-instance-view-editBtn']");
	private static By workflowGridRowOpenButton = By.xpath("//div[@data-testid='processes-instance-view-openBtn']");
	private static By workflowGridRowDeleteButton = By.xpath("//div[@data-testid='processes-instance-view-deleteBtn']");

	//NAVBAR related
	private static  By headerUserMenuDropDown = By.xpath("//div[@data-testid='header-usermenu']");
	private static  By headerAdministrationButton = By.xpath("//div[@data-testid='header-administration']");

	//ADMIN MODULE related
	private static By cmdbuildAdministrationContent = By.id("CMDBuildAdministrationContent");
	private static By cmdbuildAdministrationContentClassView = By.id("CMDBuildAdministrationContentClassView");
	private static By cmdbuildAdministrationHeaderTitle = By.id("CMDBuildAdministrationContent_header-title");

	private static By cmdbuildAdministrationClassTab = By.className("x-tab-administration-tab-item-top");

	private static By cmdbuildAdministrationAttributeTabContent = By.xpath("//div[@data-testid='administration-content-classes-tabitems-domains-grid']");
//	private static By cmdbuildAdministrationAttributeTabContent = By.id("CMDBuildAdminClassAttributesGridContent"); //"grid" in the id is wrong. Furthermore too broad, makes fetching header columns really hard


	//MISCELLANEOUS
	private static By locatorForDropDownMenuItems = By.className("x-menu-item");// By.xpath("//span[@class='x-menu-item']")




}
