package org.cmdbuild.model.profile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import static java.util.Collections.emptyList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UIConfiguration {

//	private List<String> disabledModules;
//	private List<String> disabledCardTabs;
//	private List<String> disabledProcessTabs;
	private boolean hideSidePanel;
	private boolean fullScreenMode;
	private boolean simpleHistoryModeForCard;
	private boolean simpleHistoryModeForProcess;
	private boolean processWidgetAlwaysEnabled;

	public UIConfiguration() {
//		this.disabledModules = emptyList();
//		this.disabledCardTabs = emptyList();
//		this.disabledProcessTabs = emptyList();
		this.hideSidePanel = false;
		this.fullScreenMode = false;
		this.simpleHistoryModeForCard = false;
		this.simpleHistoryModeForProcess = false;
		this.processWidgetAlwaysEnabled = false;
	}

//	/**
//	 * @return the modules that are not displayed in the UI
//	 */
//	public List<String> getDisabledModules() {
//		return disabledModules;
//	}
//
//	/**
//	 * set the modules that will be not displayed in the UI
//	 * 
//	 * @param disabledModules
//	 *            the modules to disable
//	 */
//	public void setDisabledModules(List<String> disabledModules) {
//		this.disabledModules = disabledModules;
//	}
//
//	/**
//	 * @return the tabs that are not displayed in the card module
//	 */
//	public List<String> getDisabledCardTabs() {
//		return disabledCardTabs;
//	}
//
//	/**
//	 * set the tabs that will be not displayed in the card module
//	 * 
//	 * @param disabledCardTabs
//	 *            the tabs to disable
//	 */
//	public void setDisabledCardTabs(List<String> disabledCardTabs) {
//		this.disabledCardTabs = disabledCardTabs;
//	}
//
//	/**
//	 * 
//	 * @return the tabs that are not displayed in the process module
//	 */
//	public List<String> getDisabledProcessTabs() {
//		return disabledProcessTabs;
//	}
//
//	/**
//	 * set the tabs that will be not displayed in the process module
//	 * 
//	 * @param disabledProcessTabs
//	 *            the tabs to disable
//	 */
//	public void setDisabledProcessTabs(List<String> disabledProcessTabs) {
//		this.disabledProcessTabs = disabledProcessTabs;
//	}

	/**
	 * 
	 * @return if the side panel of the main UI must be shown
	 */
	public boolean isHideSidePanel() {
		return hideSidePanel;
	}

	/**
	 * set if the side panel of the main UI must be shown
	 * 
	 * @param hideSidePanel
	 */
	public void setHideSidePanel(final boolean hideSidePanel) {
		this.hideSidePanel = hideSidePanel;
	}

	/**
	 * the full screen mode display the grid in the whole window
	 * 
	 * @return if the UI must be in full screen mode
	 */
	public boolean isFullScreenMode() {
		return fullScreenMode;
	}

	/**
	 * set if the UI must be in full screen mode
	 * 
	 * @param fullScreenMode
	 */
	public void setFullScreenMode(final boolean fullScreeMode) {
		this.fullScreenMode = fullScreeMode;
	}

	/**
	 * the simple mode for the history panel shows only start/end date and the
	 * user
	 * 
	 * @return if the history tab of the card module is in simple mode
	 */
	public boolean isSimpleHistoryModeForCard() {
		return simpleHistoryModeForCard;
	}

	/**
	 * set if the history tab of the card module is in simple mode
	 * 
	 * @param simpleHistoryModeForCard
	 */
	public void setSimpleHistoryModeForCard(final boolean simpleHistoryModeForCard) {
		this.simpleHistoryModeForCard = simpleHistoryModeForCard;
	}

	/**
	 * the simple mode for the history panel shows only start/end date and the
	 * user
	 * 
	 * @return if the history tab of the process module is in simple mode
	 */
	public boolean isSimpleHistoryModeForProcess() {
		return simpleHistoryModeForProcess;
	}

	/**
	 * set if the history tab of the process module is in simple mode
	 * 
	 * @param simpleHistoryModeForProcess
	 */
	public void setSimpleHistoryModeForProcess(final boolean simpleHistoryModeForProcess) {
		this.simpleHistoryModeForProcess = simpleHistoryModeForProcess;
	}

	/**
	 * 
	 * @return if the widget of the process module must be always enabled, also
	 *         when the activity is not in editing mode
	 */
	public boolean isProcessWidgetAlwaysEnabled() {
		return processWidgetAlwaysEnabled;
	}

	/**
	 * set if the widget of the process must be enabled also when the activity
	 * is not in editing mode
	 * 
	 * @param processWidgetAlwaysEnabled
	 */
	public void setProcessWidgetAlwaysEnabled(final boolean processWidgetAlwaysEnabled) {
		this.processWidgetAlwaysEnabled = processWidgetAlwaysEnabled;
	}

}
