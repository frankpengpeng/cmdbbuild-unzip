package org.cmdbuild.workflow.shark.xpdl;

import static org.cmdbuild.workflow.core.xpdl.XpdlTaskUtils.taskVariableFromXpdlKeyValue;
import org.springframework.stereotype.Component;
import org.cmdbuild.workflow.model.TaskAttribute;

@Component
public class SharkStyleXpdlExtendedAttributeVariableFactory implements XpdlExtendedAttributeVariableFactory {

	@Override
	public TaskAttribute createVariable(XpdlExtendedAttribute xa) {
		String key = xa.getKey();
		String value = xa.getValue();
		return taskVariableFromXpdlKeyValue(key, value);
	}

}
