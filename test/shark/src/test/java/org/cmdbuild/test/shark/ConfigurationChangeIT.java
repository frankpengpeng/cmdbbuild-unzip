package org.cmdbuild.test.shark;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.net.UnknownHostException;

import org.apache.commons.lang3.StringUtils;
import org.cmdbuild.test.shark.utils.AbstractRemoteSharkServiceTest;
import org.cmdbuild.workflow.model.WorkflowException;
import static org.cmdbuild.workflow.XpdlTest.randomName;
import org.cmdbuild.workflow.shark.xpdl.XpdlActivity;
import org.cmdbuild.workflow.shark.xpdl.XpdlDocumentHelper.ScriptLanguage;
import org.cmdbuild.workflow.shark.xpdl.XpdlException;
import org.cmdbuild.workflow.shark.xpdl.XpdlProcess;
import org.junit.Ignore;
import org.junit.Test;

public class ConfigurationChangeIT extends AbstractRemoteSharkServiceTest {

	private static final String FAKE_SERVER_HOST = "foo.bar.baz";

	@Test
	@Ignore //TODO fix and restore this
	public void startProcessFailsAfterReconfigurationWithWrongConfiguration() throws Exception {
		startSimpleProcess();
		try {
			changeConfiguration();
			startSimpleProcess();
			fail();
		} catch (WorkflowException e) {
			e.printStackTrace();
			assertThat(e.getCause().getMessage(), containsString(UnknownHostException.class.getName()));
		}finally {
			revertConfiguration();
		}
	}

	private void startSimpleProcess() throws WorkflowException, XpdlException {
		XpdlProcess process = xpdlDocument.createProcess(randomName());
		XpdlActivity activity = process.createActivity(randomName());
		activity.setScriptingType(ScriptLanguage.JAVA, StringUtils.EMPTY);
		uploadXpdlAndStartProcess(process);
	}

	private void changeConfiguration() {
		configuration.setServerHost(FAKE_SERVER_HOST);
		configuration.notifyChange();
	}

	private void revertConfiguration() {
		configuration.setServerHost(SERVER_HOST);
		configuration.notifyChange();
	}

}
