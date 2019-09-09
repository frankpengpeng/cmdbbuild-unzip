/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.shark.engine;

import java.util.Map;
import org.cmdbuild.workflow.model.FlowInfo;

public interface WorkflowActionExecutor {

	WorkflowActionExecutor withCallback(WorkflowActionExecutorCallback callback);

	/**
	 * Create and start the latest version of a process.
	 *
	 * @param pkgId package id
	 * @param procDefId workflow process definition id (as defined in the xpdl)
	 * @return newly created process instance id
	 * @
	 */
	FlowInfo startProcess(String pkgId, String procDefId);

	/**
	 * Create the latest version of a process, sets the variables, starts it.
	 *
	 * @param pkgId package id
	 * @param procDefId workflow process definition id (as defined in the xpdl)
	 * @param variables values for variables
	 * @return newly created process instance id
	 * @
	 */
	FlowInfo startProcess(String pkgId, String procDefId, Map<String, ?> variables);

	/**
	 * Aborts the specified activity, stopping that flow path.
	 *
	 * @param procInstId process instance id
	 * @param actInstId activity instance id
	 * @
	 */
	void abortTask(String procInstId, String actInstId);

	/**
	 * Advances the specified activity, returning when the flow has stopped.
	 *
	 * @param procInstId process instance id
	 * @param actInstId activity instance id
	 * @
	 */
	void completeTask(String procInstId, String actInstId);

	/**
	 * Aborts the specified process instance
	 *
	 * @param procInstId process instance id
	 * @
	 */
	void abortFlow(String procInstId);

	/**
	 * Suspends the specified process instance
	 *
	 * @param procInstId process instance id
	 * @
	 */
	void suspendFlow(String procInstId);

	/**
	 * Suspends the specified process instance
	 *
	 * @param procInstId process instance id
	 * @
	 */
	void resumeFlow(String procInstId);
}
