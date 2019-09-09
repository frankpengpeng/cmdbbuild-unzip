package org.cmdbuild.shark;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.cmdbuild.shark.test.utils.EventManagerMatchers.isActivity;
import static org.cmdbuild.shark.test.utils.EventManagerMatchers.isProcess;

import org.apache.commons.lang3.StringUtils;
import org.cmdbuild.workflow.shark.xpdl.XpdlActivity;
import org.cmdbuild.workflow.shark.xpdl.XpdlDocumentHelper.ScriptLanguage;
import org.cmdbuild.workflow.shark.xpdl.XpdlProcess;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import org.cmdbuild.shark.test.utils.AbstractLocalSharkServiceTest;
import static org.cmdbuild.workflow.XpdlTest.randomName;
import org.junit.Ignore;

public class EventDelegationIT extends AbstractLocalSharkServiceTest {

	private XpdlProcess process;

	@Before
	public void createBasicProcess() throws Exception {
		process = xpdlDocument.createProcess(randomName());
	}

	@Test
	@Ignore
	public void startScriptAndStop() throws Exception {
		final XpdlActivity activity = process.createActivity(randomName());
		activity.setScriptingType(ScriptLanguage.JAVA, StringUtils.EMPTY);

		uploadXpdlAndStartProcess(process);

		final InOrder inOrder = inOrder(eventManager);
		inOrder.verify(eventManager).processStarted(argThat(isProcess(process)));
		inOrder.verify(eventManager).activityStarted(argThat(isActivity(activity)));
		inOrder.verify(eventManager).activityClosed(argThat(isActivity(activity)));
		inOrder.verify(eventManager).processClosed(argThat(isProcess(process)));
		verifyNoMoreInteractions(eventManager);
	}

	@Test
	@Ignore
	public void startStopsAtFirstNoImplementationActivity() throws Exception {
		// order matters for this test
		final XpdlActivity noImplActivity = process.createActivity(randomName());
		final XpdlActivity scriptActivity = process.createActivity(randomName());
		scriptActivity.setScriptingType(ScriptLanguage.JAVA, StringUtils.EMPTY);
		process.createTransition(scriptActivity, noImplActivity);

		uploadXpdlAndStartProcess(process);

		final InOrder inOrder = inOrder(eventManager);
		inOrder.verify(eventManager).processStarted(argThat(isProcess(process)));
		inOrder.verify(eventManager).activityStarted(argThat(isActivity(scriptActivity)));
		inOrder.verify(eventManager).activityClosed(argThat(isActivity(scriptActivity)));
		inOrder.verify(eventManager).activityStarted(argThat(isActivity(noImplActivity)));
		verifyNoMoreInteractions(eventManager);
	}

	@Test
	@Ignore
	public void subflowStartAndStop() throws Exception {
		final XpdlProcess subprocess = xpdlDocument.createProcess(randomName());
		final XpdlActivity scriptActivity = subprocess.createActivity(randomName());
		scriptActivity.setScriptingType(ScriptLanguage.JAVA, StringUtils.EMPTY);

		final XpdlActivity subflowActivity = process.createActivity(randomName());
		subflowActivity.setSubProcess(subprocess);

		uploadXpdlAndStartProcess(process);

		final InOrder inOrder = inOrder(eventManager);
		inOrder.verify(eventManager).processStarted(argThat(isProcess(process)));
		inOrder.verify(eventManager).activityStarted(argThat(isActivity(subflowActivity)));
		inOrder.verify(eventManager).processStarted(argThat(isProcess(subprocess)));
		inOrder.verify(eventManager).activityStarted(argThat(isActivity(scriptActivity)));
		inOrder.verify(eventManager).activityClosed(argThat(isActivity(scriptActivity)));
		inOrder.verify(eventManager).processClosed(argThat(isProcess(subprocess)));
		inOrder.verify(eventManager).activityClosed(argThat(isActivity(subflowActivity)));
		inOrder.verify(eventManager).processClosed(argThat(isProcess(process)));
		verifyNoMoreInteractions(eventManager);
	}

	@Test
	@Ignore
	public void suspendResume() throws Exception {
		final XpdlActivity noImplementationActivity = process.createActivity(randomName());

		final String procInstId = uploadXpdlAndStartProcess(process).getFlowId();
		getWorkflowRemoteService().executor().suspendFlow(procInstId);
		getWorkflowRemoteService().executor().resumeFlow(procInstId);

		final InOrder inOrder = inOrder(eventManager);
		inOrder.verify(eventManager).processStarted(argThat(isProcess(process)));
		inOrder.verify(eventManager).activityStarted(argThat(isActivity(noImplementationActivity)));
		inOrder.verify(eventManager).processSuspended(argThat(isProcess(process)));
		inOrder.verify(eventManager).processResumed(argThat(isProcess(process)));
		verifyNoMoreInteractions(eventManager);
	}

}
