/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.shark.engine;

import org.cmdbuild.workflow.model.FlowInfo;

public interface WorkflowActionExecutorCallback {

	void startFlow(FlowInfo walkInfo);

	void updateFlow(FlowInfo walkInfo);

}
