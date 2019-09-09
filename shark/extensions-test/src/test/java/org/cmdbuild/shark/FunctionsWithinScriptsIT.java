package org.cmdbuild.shark;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.cmdbuild.shark.test.utils.EventManagerMatchers.isActivity;

import java.util.Map;

import org.cmdbuild.workflow.shark.xpdl.XpdlActivity;
import org.cmdbuild.workflow.shark.xpdl.XpdlDocumentHelper.ScriptLanguage;
import org.cmdbuild.workflow.shark.xpdl.XpdlDocumentHelper.StandardAndCustomTypes;
import org.cmdbuild.workflow.shark.xpdl.XpdlProcess;
import org.junit.Before;
import org.junit.Test;

import org.cmdbuild.shark.test.utils.AbstractLocalSharkServiceTest;
import static org.cmdbuild.workflow.XpdlTest.randomName;
import org.junit.Ignore;

public class FunctionsWithinScriptsIT extends AbstractLocalSharkServiceTest {

	private static final String AN_INTEGER = "anInteger";

	private XpdlProcess process;

	@Before
	public void createBasicProcess() throws Exception {
		process = xpdlDocument.createProcess(randomName());
		process.addField(AN_INTEGER, StandardAndCustomTypes.INTEGER);
	}

	@Test
	@Ignore
	public void functionsCanBeDeclaredAndCalled() throws Exception {
		final XpdlActivity scriptActivity = process.createActivity(randomName());
		scriptActivity.setScriptingType(ScriptLanguage.JAVA, //
				"return40() { return 40; }; add2(number) { return (number + 2); }; anInteger = add2(return40());");

		final XpdlActivity noImplActivity = process.createActivity(randomName());

		process.createTransition(scriptActivity, noImplActivity);

		final String procInstId = uploadXpdlAndStartProcess(process).getFlowId();
		verify(eventManager).activityClosed(argThat(isActivity(scriptActivity)));

		final Map<String, Object> variables = getWorkflowRemoteService().getProcessInstanceVariables(procInstId);

		assertThat((Long) variables.get(AN_INTEGER), equalTo(42L));
	}
}
