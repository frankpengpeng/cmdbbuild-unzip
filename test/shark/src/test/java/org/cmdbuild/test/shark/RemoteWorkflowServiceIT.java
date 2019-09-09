package org.cmdbuild.test.shark;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;
import org.cmdbuild.test.shark.utils.AbstractRemoteSharkServiceTest;

import org.cmdbuild.workflow.model.WorkflowException;
import static org.cmdbuild.workflow.XpdlTest.randomName;
import org.cmdbuild.workflow.type.LookupType;
import org.cmdbuild.workflow.type.ReferenceType;
import org.cmdbuild.workflow.shark.xpdl.XpdlPackageFactory;
import org.cmdbuild.workflow.shark.xpdl.XpdlProcess;
import org.enhydra.jxpdl.elements.Package;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Smoke tests to be reasonably sure that the web connection works just like the
 * local one. This is not tested throughly because we assume that it is going to
 * work just like the embedded Shark instance.
 */
public class RemoteWorkflowServiceIT extends AbstractRemoteSharkServiceTest {

	@Test
	@Ignore //TODO fix and restore this
	public void startProcessTest() throws Exception {
		logger.info("startProcessTest BEGIN");
		try {
			XpdlProcess process = xpdlDocument.createProcess(randomName());
			process.createActivity(randomName());
//			XpdlActivity activity = process.createActivity(randomName());
//			activity.setScriptingType(ScriptLanguage.JAVA, "");
			uploadXpdlAndStartProcess(process);
		} catch (Exception ex) {
			logger.error("error", ex);
			throw ex;
		} finally {
			getTomcatManager().getLogManager().flushLogs();
		}
		logger.info("startProcessTest END");
	}

	@Test
	@Ignore //TODO fix and restore this
	public void packagesCanBeUploadedAndDownloaded() throws WorkflowException {
		Package pkg = xpdlDocument.getPkg();

		assertEquals(0, getWorkflowRemoteService().getPackageVersions(pkg.getId()).length);

		pkg.setName("n1");
		upload(xpdlDocument);

		assertEquals(1, getWorkflowRemoteService().getPackageVersions(pkg.getId()).length);

		pkg.setName("n2");
		upload(xpdlDocument);

		pkg.setName("n3");
		upload(xpdlDocument);

		assertEquals(3, getWorkflowRemoteService().getPackageVersions(pkg.getId()).length);

		pkg = XpdlPackageFactory.readXpdl(getWorkflowRemoteService().downloadPackage(pkg.getId(), "1"));
		assertThat(pkg.getName(), is("n1"));
	}

	/**
	 * Uses {@link LookupType} because the service was initialized with the
	 * {@link IdentityTypesConverter}.
	 *
	 * @throws org.cmdbuild.workflow.model.WorkflowException
	 */
	@Test
	@Ignore //TODO fix and restore this
	public void lookupVariablesCanBeSaved() throws WorkflowException {
		XpdlProcess process = xpdlDocument.createProcess(randomName());

		process.createActivity(randomName());

		String procInstId = uploadXpdlAndStartProcess(process).getFlowId();

		// TODO CMLookup it should be used!
		getWorkflowRemoteService().setProcessInstanceVariables(procInstId, new HashMap<String, Object>() {
			{
				put("lookupVar", new LookupType(42, "type", "desc", "code"));
			}
		});

		LookupType val = (LookupType) getWorkflowRemoteService().getProcessInstanceVariables(procInstId).get("lookupVar");
		assertThat(val.getId(), is(42));
		assertThat(val.getType(), is("type"));
		assertThat(val.getDescription(), is("desc"));
		assertThat(val.getCode(), is("code"));
	}

	/**
	 * Uses {@link ReferenceType} because the service was initialized with the
	 * {@link IdentityTypesConverter}.
	 *
	 * @throws org.cmdbuild.workflow.model.WorkflowException
	 */
	@Test
	@Ignore //TODO fix and restore this
	public void referenceVariablesCanBeSaved() throws WorkflowException {
		XpdlProcess process = xpdlDocument.createProcess(randomName());

		process.createActivity(randomName());

		String procInstId = uploadXpdlAndStartProcess(process).getFlowId();

		// TODO CMLookup it should be used!
		getWorkflowRemoteService().setProcessInstanceVariables(procInstId, new HashMap<String, Object>() {
			{
				put("referenceVar", new ReferenceType(42, 666, "desc"));
			}
		});

		ReferenceType val = (ReferenceType) getWorkflowRemoteService().getProcessInstanceVariables(procInstId).get("referenceVar");
		assertThat(val.getId(), is(42));
		assertThat(val.getIdClass(), is(666));
		assertThat(val.getDescription(), is("desc"));
	}

	@Test
	@Ignore //TODO fix and restore this
	public void variablesNotDefinedInTheXpdlCanBeSettedAnyway() throws Exception {
		XpdlProcess process = xpdlDocument.createProcess(randomName());
		process.createActivity(randomName());

		String procInstId = uploadXpdlAndStartProcess(process).getFlowId();

		Map<String, Object> settedVariables = new HashMap<>();
		settedVariables.put("UNDEFINED", "baz");
		getWorkflowRemoteService().setProcessInstanceVariables(procInstId, settedVariables);

		Map<String, Object> readVariables = getWorkflowRemoteService().getProcessInstanceVariables(procInstId);
		assertThat((String) readVariables.get("UNDEFINED"), is("baz"));
	}
}
