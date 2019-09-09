package org.cmdbuild.workflow.inner;

import org.cmdbuild.workflow.FlowAdvanceResponse;
import java.util.Map;
import javax.activation.DataSource;
import org.cmdbuild.workflow.WorkflowService.WorkflowVariableProcessingStrategy;
import org.cmdbuild.workflow.model.Task;
import org.cmdbuild.workflow.model.TaskDefinition;
import org.cmdbuild.workflow.model.WorkflowNamedService;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.model.Process;

public interface WorkflowServiceDelegate extends WorkflowServiceBase, WorkflowNamedService {

    Task getUserTask(Flow card, String userTaskId);

    Map<String, Object> getFlowData(Flow flowCard);

    TaskDefinition getTaskDefinition(Flow flowCard, String taskId);

    FlowAdvanceResponse startProcess(Process classe, Map<String, ?> vars, WorkflowVariableProcessingStrategy variableProcessingStrategy, boolean advance);

    FlowAdvanceResponse updateProcess(Flow card, String taskId, Map<String, ?> vars, WorkflowVariableProcessingStrategy variableProcessingStrategy, boolean advance);

    DataSource getXpdlForClasse(Process classe);

}
