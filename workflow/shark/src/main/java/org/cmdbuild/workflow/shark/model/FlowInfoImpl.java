package org.cmdbuild.workflow.shark.model;

import javax.annotation.Nullable;
import org.cmdbuild.workflow.model.FlowStatus;
import org.enhydra.shark.api.client.wfmc.wapi.WMProcessInstance;
import org.enhydra.shark.api.client.wfmc.wapi.WMProcessInstanceState;
import org.enhydra.shark.utilities.MiscUtilities;
import org.cmdbuild.workflow.model.FlowInfo;

public class FlowInfoImpl extends PlanInfoImpl implements FlowInfo {

	private final String walkId;
	private final FlowStatus status;

	public FlowInfoImpl(String packageId, String packageVersion, String processDefinitionId, String walkId, FlowStatus status) {
		super(packageId, packageVersion, processDefinitionId);
		this.walkId = walkId;
		this.status = status;
	}

	public static @Nullable
	FlowInfo newInstance(@Nullable WMProcessInstance processInstance) {
		if (processInstance == null) {
			return null;
		} else {
			return new FlowInfoImpl(MiscUtilities.getProcessMgrPkgId(processInstance.getProcessFactoryName()),
					MiscUtilities.getProcessMgrVersion(processInstance.getProcessFactoryName()),
					processInstance.getProcessDefinitionId(),
					processInstance.getId(),
					convertStatus(processInstance.getState()));
		}
	}

	private static FlowStatus convertStatus(final WMProcessInstanceState state) {
		if (state == null) {
			// We have no control over this field, so it's
			// best to assume that it might be null.
			return FlowStatus.UNSUPPORTED;
		}
		switch (state.value()) {
			case WMProcessInstanceState.OPEN_RUNNING_INT:
				return FlowStatus.OPEN;
			case WMProcessInstanceState.CLOSED_COMPLETED_INT:
				return FlowStatus.COMPLETED;
			case WMProcessInstanceState.CLOSED_ABORTED_INT:
				return FlowStatus.ABORTED;
			case WMProcessInstanceState.CLOSED_TERMINATED_INT:
				return FlowStatus.TERMINATED;
			case WMProcessInstanceState.OPEN_NOTRUNNING_SUSPENDED_INT:
				return FlowStatus.SUSPENDED;
			case WMProcessInstanceState.OPEN_NOTRUNNING_NOTSTARTED_INT:
			default:
				return FlowStatus.UNSUPPORTED;
		}
	}

	@Override
	public String getFlowId() {
		return walkId;
	}

	@Override
	public FlowStatus getStatus() {
		return status;
	}

	@Override
	public String toString() {
		return "WalkInfoImpl{" + "walkId=" + walkId + ", status=" + status + '}';
	}

}
