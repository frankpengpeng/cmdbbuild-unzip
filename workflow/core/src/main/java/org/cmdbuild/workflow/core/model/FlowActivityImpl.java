/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.core.model;

import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.workflow.model.FlowActivity;

public class FlowActivityImpl implements FlowActivity {

	private final String instanceId, definitionId, performerGroup;

	public FlowActivityImpl(String instanceId, String definitionId, String performerGroup) {
		this.instanceId = checkNotBlank(instanceId);
		this.definitionId = checkNotBlank(definitionId);
		this.performerGroup = checkNotBlank(performerGroup);
	}

	@Override
	public String getInstanceId() {
		return instanceId;
	}

	@Override
	public String getDefinitionId() {
		return definitionId;
	}

	@Override
	public String getPerformerGroup() {
		return performerGroup;
	}

	@Override
	public String toString() {
		return "FlowActivityImpl{" + "instanceId=" + instanceId + ", definitionId=" + definitionId + ", performerGroup=" + performerGroup + '}';
	}

}
