package org.cmdbuild.shark;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.cmdbuild.workflow.model.WorkflowException;
import org.cmdbuild.workflow.shark.xpdl.XpdlDocumentHelper.ScriptLanguage;
import org.cmdbuild.workflow.shark.xpdl.XpdlException;
import org.cmdbuild.workflow.shark.xpdl.XpdlPackageFactory;
import org.enhydra.jxpdl.elements.Package;
import org.enhydra.shark.api.client.wfservice.PackageInvalid;
import org.junit.Test;

import org.cmdbuild.shark.test.utils.AbstractLocalSharkServiceTest;
import static org.cmdbuild.workflow.XpdlTest.randomName;
import org.junit.Ignore;

public class XpdlHandlingIT extends AbstractLocalSharkServiceTest {

	@Test
	@Ignore
	public void definitionsCannotBeRubbish() throws XpdlException, WorkflowException {
		logger.info("definitionsCannotBeRubbish BEGIN");
		try {
			try {
				getWorkflowRemoteService().uploadPackage(xpdlDocument.getPackageId(), new byte[0]);
				fail();
			} catch (WorkflowException we) {
				assertThat(we.getCause().getMessage(), containsString("The package byte[] representation can't be parsed"));
			}
		} catch (Exception ex) {
			logger.error("error", ex);
			throw ex;
		}
		logger.info("definitionsCannotBeRubbish END");
	}

	@Test
	@Ignore
	public void definitionsMustHaveDefaultScriptingLanguage() throws XpdlException, WorkflowException {
		logger.info("definitionsMustHaveDefaultScriptingLanguage BEGIN");
		try {
			try {
				upload(newXpdlNoScriptingLanguage(xpdlDocument.getPackageId()));
				fail();
			} catch (WorkflowException we) {
				assertThat(we.getCause(), instanceOf(PackageInvalid.class));
				PackageInvalid sharkException = (PackageInvalid) we.getCause();
				assertThat(sharkException.getMessage(), containsString("Error in package"));
				assertThat(sharkException.getXPDLValidationErrors(), containsString("Unsupported script language"));
			}
			xpdlDocument.setDefaultScriptingLanguage(ScriptLanguage.JAVA);
			upload(xpdlDocument);
		} catch (Exception ex) {
			logger.error("error", ex);
			throw ex;
		}
		logger.info("definitionsMustHaveDefaultScriptingLanguage END");
	}

	@Test
	@Ignore
	public void packageVersionIncreasesWithEveryUpload() {
		logger.info("packageVersionIncreasesWithEveryUpload BEGIN");
		try {
			String[] versions = getWorkflowRemoteService().getPackageVersions(xpdlDocument.getPackageId());
			assertEquals(0, versions.length);

			upload(xpdlDocument);

			versions = getWorkflowRemoteService().getPackageVersions(xpdlDocument.getPackageId());
			assertThat(versions, is(new String[]{"1"}));

			upload(xpdlDocument);
			upload(xpdlDocument);
			versions = getWorkflowRemoteService().getPackageVersions(xpdlDocument.getPackageId());
			assertThat(versions, is(new String[]{"1", "2", "3"}));
		} catch (Exception ex) {
			logger.error("error", ex);
			throw ex;
		}
		logger.info("packageVersionIncreasesWithEveryUpload END");
	}

	@Test
	@Ignore
	public void anyPackageVersionCanBeDownloaded() {
		logger.info("anyPackageVersionCanBeDownloaded BEGIN");
		try {
			Package pkg = xpdlDocument.getPkg();

			pkg.setName("n1");
			upload(xpdlDocument);

			pkg.setName("n2");
			upload(xpdlDocument);

			pkg.setName("n3");
			upload(xpdlDocument);

			pkg = XpdlPackageFactory.readXpdl(getWorkflowRemoteService().downloadPackage(xpdlDocument.getPackageId(), "1"));
			assertThat(pkg.getName(), is("n1"));

			pkg = XpdlPackageFactory.readXpdl(getWorkflowRemoteService().downloadPackage(xpdlDocument.getPackageId(), "3"));
			assertThat(pkg.getName(), is("n3"));
		} catch (Exception ex) {
			logger.error("error", ex);
			throw ex;
		}
		logger.info("anyPackageVersionCanBeDownloaded END");
	}

	@Test
	@Ignore
	public void xpdl1PackagesAreNotConvertedToXpdl2() {
		logger.info("xpdl1PackagesAreNotConvertedToXpdl2 BEGIN");
		try {
			Package pkg = xpdlDocument.getPkg();

			pkg.getPackageHeader().setXPDLVersion("1.0");
			upload(xpdlDocument);

			pkg = XpdlPackageFactory.readXpdl(getWorkflowRemoteService().downloadPackage(xpdlDocument.getPackageId(), "1"));
			assertThat(pkg.getPackageHeader().getXPDLVersion(), is("1.0"));
		} catch (Exception ex) {
			logger.error("error", ex);
			throw ex;
		}
		logger.info("xpdl1PackagesAreNotConvertedToXpdl2 END");
	}

	@Test
	@Ignore
	public void canDownloadAllPackages() {
		logger.info("canDownloadAllPackages BEGIN");
		try {
			String ID1 = randomName();
			String ID2 = randomName();
			int initialSize = getWorkflowRemoteService().downloadAllPackages().size();

			upload(newXpdl(ID1));

			assertThat(getWorkflowRemoteService().downloadAllPackages().size(), is(initialSize + 1));

			upload(newXpdl(ID2));
			upload(newXpdl(ID2));

			assertThat(getWorkflowRemoteService().downloadAllPackages().size(), is(initialSize + 2));
		} catch (Exception ex) {
			logger.error("error", ex);
			throw ex;
		}
		logger.info("canDownloadAllPackages END");
	}

}
