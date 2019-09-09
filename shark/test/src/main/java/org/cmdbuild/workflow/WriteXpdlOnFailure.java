package org.cmdbuild.workflow;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.apache.commons.lang3.Validate;
import org.cmdbuild.workflow.shark.xpdl.XpdlDocumentHelper;
import org.cmdbuild.workflow.shark.xpdl.XpdlPackageFactory;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class WriteXpdlOnFailure extends TestWatcher {

	/**
	 * Annotate tests with this annotation to print the XPDL on success as well.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface WriteXpdl {
	}

	private final String tmpDir = System.getProperty("java.io.tmpdir");

	private final XpdlTest xpdlTest;

	public WriteXpdlOnFailure(XpdlTest xpdlTest) {
		Validate.notNull(xpdlTest);
		this.xpdlTest = xpdlTest;
	}

	@Override
	protected void succeeded(Description description) {
		if (description.getAnnotation(WriteXpdl.class) != null) {
			writeXpdl(description);
		}
	}

	@Override
	protected void failed(Throwable e, Description description) {
		writeXpdl(description);
	}

	private void writeXpdl(Description description) {
		XpdlDocumentHelper xpdl = xpdlTest.getXpdlDocument();
		if (xpdl != null) {
			try {
				String fileName = getFileName(description);
				System.err.println("Saving XPDL from test to " + fileName);
				FileOutputStream fos = new FileOutputStream(fileName);
				XpdlPackageFactory.writeXpdl(xpdl.getPkg(), fos);
			} catch (Throwable t) {
				System.err.println("Cannot save XPDL: " + t.getMessage());
			}
		}
	}

	private String getFileName(Description description) {
		String testName = String.format("%s.%s", description.getClassName(), description.getMethodName());
		return String.format("%s%s%s.xpdl", tmpDir, File.separator, testName.replace(".", "_"));
	}

}
