package org.cmdbuild.workflow.shark.model;

import org.enhydra.shark.api.client.wfmc.wapi.WMProcessDefinition;
import org.cmdbuild.workflow.model.PlanInfo;

public class PlanInfoImpl extends org.cmdbuild.workflow.model.PlanInfoImpl {

	public PlanInfoImpl(String packageId, String packageVersion, String processDefinitionId) {
		super(packageId, packageVersion, processDefinitionId);
	}

	static PlanInfo newInstance(WMProcessDefinition processDefinition) {
		return new PlanInfoImpl(processDefinition.getPackageId(), processDefinition.getVersion(), processDefinition.getId());
	}

	public static PlanInfo newInstance(String pkgId, String pkgVer, String procDefId) {
		return new PlanInfoImpl(pkgId, pkgVer, procDefId);
	}

}
