package org.cmdbuild.shark.test.utils;

import org.cmdbuild.workflow.WriteXpdlOnFailure;

import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.cmdbuild.workflow.model.WorkflowException;
import org.cmdbuild.workflow.XpdlTest;
import static org.cmdbuild.workflow.XpdlTest.randomName;
import org.cmdbuild.workflow.shark.xpdl.XpdlDocumentHelper;
import org.cmdbuild.workflow.shark.xpdl.XpdlDocumentHelper.ScriptLanguage;
import org.cmdbuild.workflow.shark.xpdl.XpdlException;
import org.cmdbuild.workflow.shark.xpdl.XpdlPackageFactory;
import org.cmdbuild.workflow.shark.xpdl.XpdlProcess;
import org.enhydra.jxpdl.elements.Package;
import org.enhydra.shark.api.client.wfmc.wapi.WAPI;
import org.enhydra.shark.client.utilities.SharkInterfaceWrapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.workflow.shark.engine.WorkflowRemoteService;
import org.cmdbuild.workflow.model.FlowInfo;

public abstract class AbstractSharkServiceTest implements XpdlTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected XpdlDocumentHelper xpdlDocument;

	@Rule
	public TestRule testWatcher = new WriteXpdlOnFailure(this);

	@Before
	public void createXpdlDocument() throws Exception {
		xpdlDocument = newXpdl(randomName());
	}

	@Override
	public XpdlDocumentHelper getXpdlDocument() {
		return xpdlDocument;
	}

	protected abstract WorkflowRemoteService getWorkflowRemoteService();

	/**
	 * Returns the WAPI connection to Shark.
	 *
	 * Shark should have been already initialized by the
	 * {@link AbstractRemoteSharkClient}.
	 *
	 * @return Shark WAPI interface
	 * @throws Exception
	 */
	protected final WAPI wapi() throws Exception {
		return SharkInterfaceWrapper.getShark().getWAPIConnection();
	}

	/*
	 * Utils
	 */
	/**
	 * Creates a new {@link XpdlDocumentHelper} with default scripting language
	 * {@code ScriptLanguages.JAVA}.
	 *
	 * @param packageId
	 * @return
	 * @throws org.cmdbuild.workflow.shark.xpdl.XpdlException
	 */
	protected XpdlDocumentHelper newXpdl(String packageId) throws XpdlException {
		XpdlDocumentHelper xpdl = newXpdlNoScriptingLanguage(packageId);
		xpdl.setDefaultScriptingLanguage(ScriptLanguage.JAVA);
		return xpdl;
	}

	/**
	 * Creates a new {@link XpdlDocumentHelper} with no default scripting language.
	 *
	 * @param packageId
	 * @return
	 * @throws org.cmdbuild.workflow.shark.xpdl.XpdlException
	 */
	protected XpdlDocumentHelper newXpdlNoScriptingLanguage(String packageId) throws XpdlException {
		return new XpdlDocumentHelper(packageId);
	}

	/**
	 * Uploads the {@link XpdlDocumentHelper} and starts the specified
	 * {@link XpdlProcess}.
	 *
	 * @param xpdlProcess
	 * @return the process instance's info
	 * @throws org.cmdbuild.workflow.model.WorkflowException
	 * @throws org.cmdbuild.workflow.shark.xpdl.XpdlException
	 */
	protected FlowInfo uploadXpdlAndStartProcess(XpdlProcess xpdlProcess) throws WorkflowException, XpdlException {
		upload(xpdlDocument);
		return startProcess(xpdlProcess);
	}

	/**
	 * Uploads an {@link XpdlDocumentHelper}.
	 *
	 * @param xpdlDocument
	 * @throws WorkflowException
	 * @throws XpdlException
	 */
	protected void upload(XpdlDocumentHelper xpdlDocument) throws XpdlException, WorkflowException {
		logger.info("upload xpdl document = {}", xpdlDocument);
		getWorkflowRemoteService().uploadPackage(xpdlDocument.getPackageId(), serialize(xpdlDocument));
	}

	/**
	 * Uploads an XPDL resource give its name.
	 *
	 * @param resourceName
	 * @throws Exception
	 * @return the uploaded package
	 */
	protected Package uploadXpdlResource(String resourceName) throws Exception {
		logger.info("uploadXpdlResource = {}", resourceName);
		byte[] data = IOUtils.toByteArray(getClass().getResourceAsStream(resourceName));

//		ByteArrayInputStream is = new ByteArrayInputStream(data);
		Package pkg = XpdlPackageFactory.readXpdl(data);

		getWorkflowRemoteService().uploadPackage(pkg.getId(), data);

		return pkg;
	}

	/**
	 * Starts the specified {@link XpdlProcess}.
	 *
	 * @param xpdlProcess
	 * @return the process instance's info
	 * @throws org.cmdbuild.workflow.model.WorkflowException
	 * @throws org.cmdbuild.workflow.shark.xpdl.XpdlException
	 */
	protected FlowInfo startProcess(XpdlProcess xpdlProcess) throws WorkflowException, XpdlException {
		return startProcess(xpdlProcess.getId());
	}

	/**
	 * Starts the specified process Id.
	 *
	 * @param processId
	 * @return the process instance's info
	 * @throws org.cmdbuild.workflow.model.WorkflowException
	 * @throws org.cmdbuild.workflow.shark.xpdl.XpdlException
	 */
	protected FlowInfo startProcess(String processId) throws WorkflowException, XpdlException {
		logger.info("start process = {}", processId);
		return getWorkflowRemoteService().executor().startProcess(xpdlDocument.getPackageId(), processId);
	}

	/**
	 * Serializes an {@link XpdlDocumentHelper} in a byte array.
	 *
	 * @param xpdl
	 * @return
	 * @throws org.cmdbuild.workflow.shark.xpdl.XpdlException
	 */
	protected byte[] serialize(XpdlDocumentHelper xpdl) throws XpdlException {
		return XpdlPackageFactory.xpdlByteArray(xpdl.getPkg());
	}

	/**
	 * Returns the instance variables for the specified process instance.
	 *
	 * @param processInstInfo
	 * @return the instance variables for the specified process instance
	 * @throws WorkflowException
	 */
	protected Map<String, Object> instanceVariablesForProcess(FlowInfo processInstInfo) throws WorkflowException {
		return getWorkflowRemoteService().getProcessInstanceVariables(processInstInfo.getFlowId());
	}

}
