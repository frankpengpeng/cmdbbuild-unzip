package org.cmdbuild.workflow.shark.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.enhydra.shark.api.client.wfmc.wapi.WMActivityInstance;
import org.enhydra.shark.api.client.wfmc.wapi.WMParticipant;
import org.cmdbuild.workflow.model.TaskInfo;

public class TaskInfoImpl implements TaskInfo {

	private WMActivityInstance inner;

	public static TaskInfo newInstance(final WMActivityInstance activityInstance) {
		final TaskInfoImpl instance = new TaskInfoImpl();
		instance.inner = activityInstance;
		return instance;
	}

	@Override
	public String getFlowId() {
		return inner.getProcessInstanceId();
	}

	@Override
	public String getTaskDefinitionId() {
		return inner.getActivityDefinitionId();
	}

	@Override
	public String getTaskId() {
		return inner.getId();
	}

	@Override
	public String getTaskName() {
		return inner.getName();
	}

	@Override
	public String getTaskDescription() {
		return inner.getDescription();
	}

	@Override
	public String[] getParticipants() {
		final WMParticipant[] participants = inner.getParticipants();
		final String[] names;
		if (participants == null) {
			return new String[0];
		} else {
			names = new String[participants.length];
			for (int i = 0; i < participants.length; ++i) {
				names[i] = participants[i].getName();
			}
		}
		return names;
	}

	/*
	 * Object overrides
	 */

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("processInstanceId", getFlowId())
				.append("activityDefinitionId", getTaskDefinitionId())
				.append("activityInstanceId", getTaskId()).append("activityName", getTaskName())
				.append("activityDescription", getTaskDescription()).append("participants", getParticipants())
				.toString();
	}
}
