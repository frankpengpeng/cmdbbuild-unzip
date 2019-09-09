package org.cmdbuild.workflow.shark;

import java.util.Map;
import org.cmdbuild.workflow.model.FlowInfo;
import org.cmdbuild.workflow.model.Flow;

public interface SharkFlowVariablesSynchronizer {

	Flow syncProcessStateActivitiesAndVariables(Flow processInstance, FlowInfo processInstanceInfo);

	Flow syncProcessStateAndActivities(Flow processInstance, FlowInfo processInstanceInfo);

	Map<String, Object> getSharkVariablesForCard(Flow flow);

	Map<String, Object> getRawSharkVariables(Flow flow);

}
