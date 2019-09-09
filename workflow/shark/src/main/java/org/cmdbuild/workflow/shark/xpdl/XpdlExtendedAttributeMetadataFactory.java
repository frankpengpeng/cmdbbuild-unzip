package org.cmdbuild.workflow.shark.xpdl;

import org.cmdbuild.workflow.model.TaskMetadata;

public interface XpdlExtendedAttributeMetadataFactory {

	TaskMetadata createMetadata(XpdlExtendedAttribute xa);

}
