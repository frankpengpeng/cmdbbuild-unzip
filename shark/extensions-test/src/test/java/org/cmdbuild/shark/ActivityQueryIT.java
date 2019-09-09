package org.cmdbuild.shark;

import java.util.List;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.apache.commons.lang3.StringUtils;
import org.cmdbuild.workflow.shark.xpdl.XpdlActivity;
import org.cmdbuild.workflow.shark.xpdl.XpdlDocumentHelper.ScriptLanguage;
import org.cmdbuild.workflow.shark.xpdl.XpdlProcess;
import org.junit.Before;
import org.junit.Test;

import org.cmdbuild.shark.test.utils.AbstractLocalSharkServiceTest;
import static org.cmdbuild.workflow.XpdlTest.randomName;
import org.cmdbuild.workflow.model.TaskInfo;
import org.junit.Ignore;
import org.cmdbuild.workflow.model.FlowInfo;

public class ActivityQueryIT extends AbstractLocalSharkServiceTest {

	private XpdlProcess p1;
	private XpdlProcess p2;
	private XpdlProcess p3;

	@Before
	@Ignore
	public void createBasicProcess() {
		p1 = xpdlDocument.createProcess(randomName());
		p1.createTransition(p1.createActivity("A"), p1.createActivity("B"));
		p1.createActivity("C").setScriptingType(ScriptLanguage.JAVA, StringUtils.EMPTY);

		p2 = xpdlDocument.createProcess(randomName());
		p2.createActivity("X");

		p3 = xpdlDocument.createProcess(randomName());
		XpdlActivity yActivity = p3.createActivity("Y");
		yActivity.setScriptingType(ScriptLanguage.JAVA, StringUtils.EMPTY);
		p3.createTransition(yActivity, p3.createActivity("Z"));
	}

	@Test
	@Ignore
	public void openActivitiesCanBeQueriedForAProcessInstance() {
		logger.info("openActivitiesCanBeQueriedForAProcessInstance BEGIN");
		try {
			FlowInfo pi11 = uploadXpdlAndStartProcess(p1);
			FlowInfo pi21 = startProcess(p2);
			FlowInfo pi12 = startProcess(p1);
			FlowInfo pi31 = startProcess(p3);

			assertThat(openActivitiesForProcessInstance(pi11), is(new String[]{"A"}));
			assertThat(openActivitiesForProcessInstance(pi12), is(new String[]{"A"}));
			assertThat(openActivitiesForProcessInstance(pi21), is(new String[]{"X"}));
			assertThat(openActivitiesForProcessInstance(pi31), is(new String[]{"Z"}));
		} catch (Exception ex) {
			logger.error("error", ex);
			throw ex;
		}
		logger.info("openActivitiesCanBeQueriedForAProcessInstance END");
	}

	@Test
	@Ignore
	public void openActivitiesCanBeQueriedForAProcess() {
		logger.info("openActivitiesCanBeQueriedForAProcess BEGIN");
		try {
			uploadXpdlAndStartProcess(p1);
			startProcess(p2);
			startProcess(p1);
			startProcess(p3);

			assertThat(openActivitiesForProcess(p1.getId()), is(new String[]{"A", "A"}));
			assertThat(openActivitiesForProcess(p2.getId()), is(new String[]{"X"}));
			assertThat(openActivitiesForProcess(p3.getId()), is(new String[]{"Z"}));
		} catch (Exception ex) {
			logger.error("error", ex);
			throw ex;
		}
		logger.info("openActivitiesCanBeQueriedForAProcess END");
	}

	private String[] openActivitiesForProcessInstance(FlowInfo processInstanceInfo) {
		return defIds(getWorkflowRemoteService().findOpenActivitiesForProcessInstance(processInstanceInfo.getFlowId()));
	}

	private String[] openActivitiesForProcess(String processDefinitionId) {
		return defIds(getWorkflowRemoteService().findOpenActivitiesForProcess(processDefinitionId));
	}

	private String[] defIds(List<TaskInfo> activityInstances) {
		return activityInstances.stream().map(TaskInfo::getTaskDefinitionId).collect(toList()).toArray(new String[]{});
	}

}
