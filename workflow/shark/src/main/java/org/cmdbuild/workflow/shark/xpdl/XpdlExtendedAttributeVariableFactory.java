package org.cmdbuild.workflow.shark.xpdl;

import org.cmdbuild.workflow.model.TaskAttribute;

public interface XpdlExtendedAttributeVariableFactory {

	TaskAttribute createVariable(final XpdlExtendedAttribute xa);
}
