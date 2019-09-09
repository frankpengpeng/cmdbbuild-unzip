package org.cmdbuild.workflow.shark.xpdl;

import org.cmdbuild.workflow.model.TaskMetadata;
import org.cmdbuild.workflow.core.xpdl.XpdlTaskUtils;
import org.springframework.stereotype.Component;

@Component
public class SharkStyleXpdlExtendedAttributeMetadataFactory implements XpdlExtendedAttributeMetadataFactory {

	@Override
	public TaskMetadata createMetadata(XpdlExtendedAttribute xa) {
		String key = xa.getKey();
		String value = xa.getValue();
		return XpdlTaskUtils.taskMetadataFromXpdlKeyValue(key, value);
	}

}
