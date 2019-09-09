package org.cmdbuild.workflow.shark.engine;

import java.util.List;
import java.util.Map;
import org.cmdbuild.workflow.model.TaskInfo;
import org.cmdbuild.workflow.model.WorkflowNamedService;
import org.cmdbuild.workflow.model.FlowInfo;

public interface WorkflowRemoteService extends WorkflowRemoteRepository, WorkflowNamedService {

	/**
	 * List open process instances by process definition id.
	 *
	 * @param procDefId
	 * @return
	 * @
	 */
	FlowInfo[] listOpenProcessInstances(String procDefId);

	/**
	 * Retrieve informations about an open process instance.
	 *
	 * @param procInstId process instance id
	 * @return
	 * @
	 */
	FlowInfo getWalkInfo(String procInstId);

	void setProcessInstanceVariables(String procInstId, Map<String, ?> variables);

	/**
	 * Returns a list of open activities for a process definition.
	 *
	 * @param procDefId
	 * @return list of open activity instances for the process definition
	 * @
	 */
	List<TaskInfo> findOpenActivitiesForProcess(String procDefId);

	WorkflowActionExecutor executor();

}
