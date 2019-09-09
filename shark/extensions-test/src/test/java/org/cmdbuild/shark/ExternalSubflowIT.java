package org.cmdbuild.shark;

import static org.cmdbuild.common.collect.Factory.entry;
import static org.cmdbuild.common.collect.Factory.linkedHashMapOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.inOrder;
import static org.cmdbuild.shark.test.utils.EventManagerMatchers.hasActivityDefinitionId;
import static org.cmdbuild.shark.test.utils.EventManagerMatchers.hasProcessDefinitionId;

import org.cmdbuild.workflow.SimpleEventManager.ActivityInstance;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import org.cmdbuild.shark.test.utils.AbstractLocalSharkServiceTest;
import org.junit.Ignore;

@SuppressWarnings("unchecked")
public class ExternalSubflowIT extends AbstractLocalSharkServiceTest {

	private static final String PARENT_VARIABLE = "ParentVariable";
	private static final String PARENT_PACKAGE = "parent";
	private static final String PARENT_PROCESS = "Parent";
	private static final String CHILD_PACKAGE = "child";
	private static final String CHILD_PROCESS = "Child";
	private static final String CHILD_INPUT_VARIABLE = "FormalIn";
	private static final String CHILD_OUTPUT_VARIABLE = "FormalOut";

	private final ArgumentCaptor<ActivityInstance> activityInstanceCaptor = ArgumentCaptor.forClass(ActivityInstance.class);

	@Before
	public void uploadPackages() throws Exception {
		try {
			uploadXpdlResource("/xpdl/Child.xpdl");
			uploadXpdlResource("/xpdl/Parent.xpdl");
		} catch (Exception ex) {
			logger.error("error", ex);
			throw ex;
		}
	}

	@Test
	@Ignore
	public void spawnChildProcess() throws Exception {
		logger.info("spawnChildProcess BEGIN");
		try {
			final String procInstId = getWorkflowRemoteService().executor().startProcess(PARENT_PACKAGE, PARENT_PROCESS).getFlowId();
			final String actInstId = getWorkflowRemoteService().findOpenActivitiesForProcessInstance(procInstId).get(0).getTaskId();

			final InOrder inOrder = inOrder(eventManager);
			inOrder.verify(eventManager).processStarted(argThat(hasProcessDefinitionId(PARENT_PROCESS)));
			inOrder.verify(eventManager).activityStarted(argThat(hasActivityDefinitionId("ParentStart")));

			getWorkflowRemoteService().setProcessInstanceVariables(procInstId, linkedHashMapOf(entry(PARENT_VARIABLE, "Something")));
			getWorkflowRemoteService().executor().completeTask(procInstId, actInstId);

			inOrder.verify(eventManager).activityStarted(argThat(hasActivityDefinitionId("GiveBirth")));
			inOrder.verify(eventManager).activityStarted(argThat(hasActivityDefinitionId("ChildStart")));
			inOrder.verify(eventManager).activityStarted(argThat(hasActivityDefinitionId("CopyInputToOutput")));
			inOrder.verify(eventManager).activityStarted(argThat(hasActivityDefinitionId("ChildEnd")));
			inOrder.verify(eventManager).activityStarted(argThat(hasActivityDefinitionId("VerifyVariable")));

			assertThat(getWorkflowRemoteService().getProcessInstanceVariables(procInstId).get(PARENT_VARIABLE),
					is(equalTo((Object) "Copy of Something")));
		} catch (Exception ex) {
			logger.error("error", ex);
			throw ex;
		}
		logger.info("spawnChildProcess END");
	}

	@Test
	@Ignore
	public void startChildProcessDirectly() throws Exception {
		logger.info("startChildProcessDirectly BEGIN");
		try {
			final String procInstId = getWorkflowRemoteService().executor().startProcess(CHILD_PACKAGE, CHILD_PROCESS).getFlowId();
			final String actInstId = getWorkflowRemoteService().findOpenActivitiesForProcessInstance(procInstId).get(0).getTaskId();

			final InOrder inOrder = inOrder(eventManager);
			inOrder.verify(eventManager).processStarted(argThat(hasProcessDefinitionId(CHILD_PROCESS)));
			inOrder.verify(eventManager).activityStarted(argThat(hasActivityDefinitionId("ChildStart")));
			inOrder.verify(eventManager).activityStarted(argThat(hasActivityDefinitionId("ChildUserActivity")));

			getWorkflowRemoteService().setProcessInstanceVariables(procInstId, linkedHashMapOf(entry(CHILD_INPUT_VARIABLE, "Something else")));
			getWorkflowRemoteService().executor().completeTask(procInstId, actInstId);

			assertThat(getWorkflowRemoteService().getProcessInstanceVariables(procInstId).get(CHILD_OUTPUT_VARIABLE),
					is(equalTo((Object) "Copy of Something else")));
		} catch (Exception ex) {
			logger.error("error", ex);
			throw ex;
		}
		logger.info("startChildProcessDirectly END");
	}

	/*
	 * Utils
	 */
	protected ActivityInstance activityInstanceCapturer() {
		return activityInstanceCaptor.capture();
	}

	protected ActivityInstance capturedActivityInstance() {
		return activityInstanceCaptor.getValue();
	}
}
