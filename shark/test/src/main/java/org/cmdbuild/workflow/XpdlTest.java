package org.cmdbuild.workflow;

import java.util.UUID;
import org.cmdbuild.workflow.shark.xpdl.XpdlDocumentHelper;

public interface XpdlTest {

	XpdlDocumentHelper getXpdlDocument();

	static String randomName() {
		return UUID.randomUUID().toString();
	}
}
