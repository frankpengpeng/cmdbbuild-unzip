/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.core.inner;

import java.util.Collection;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import javax.activation.DataSource;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.workflow.FlowAdvanceResponse;
import org.cmdbuild.workflow.WorkflowService.WorkflowVariableProcessingStrategy;
import org.cmdbuild.workflow.inner.WorkflowServiceDelegate;
import org.cmdbuild.workflow.model.Task;
import org.cmdbuild.workflow.model.TaskDefinition;
import org.cmdbuild.workflow.model.XpdlInfo;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.model.Process;

public class DummyWorkflowServiceDelegate implements WorkflowServiceDelegate {

    @Override
    public FlowAdvanceResponse startProcess(Process process, Map<String, ?> vars, WorkflowVariableProcessingStrategy variableProcessingStrategy, boolean advance) {
        throw new UnsupportedOperationException("workflow is not enabled!");
    }

    @Override
    public FlowAdvanceResponse updateProcess(Flow card, String taskId, Map<String, ?> vars, WorkflowVariableProcessingStrategy variableProcessingStrategy, boolean advance) {
        throw new UnsupportedOperationException("workflow is not enabled!");
    }

    @Override
    public Map<String, Object> getFlowData(Flow flowCard) {
        throw new UnsupportedOperationException("workflow is not enabled!");
    }

    @Override
    public TaskDefinition getTaskDefinition(Flow flowCard, String taskId) {
        throw new UnsupportedOperationException("workflow is not enabled!");
    }

    @Override
    public List<XpdlInfo> getXpdlInfosOrderByVersionDesc(String planClasseId) {
        throw new UnsupportedOperationException("workflow is not enabled!");
    }

    @Override
    public DataSource getXpdlForClasse(Process classe) {
        throw new UnsupportedOperationException("workflow is not enabled!");
    }

    @Override
    public XpdlInfo addXpdl(String processClassIdToClasseId, DataSource wrapAsDataSource) {
        throw new UnsupportedOperationException("workflow is not enabled!");
    }

    @Override
    public List<Task> getTaskListForCurrentUserByClassIdAndCardId(String planId, Long cardId) {
        return emptyList();
    }

    @Override
    public PagedElements<Task> getTaskListForCurrentUserByClassIdSkipFlowData(String classId, DaoQueryOptions queryOptions) {
        return new PagedElements<>(emptyList());
    }

    @Override
    public void abortProcessInstance(Flow processInstance) {
        throw new UnsupportedOperationException("workflow is not enabled!");
    }

    @Override
    public void suspendProcessInstance(Flow processInstance) {
        throw new UnsupportedOperationException("workflow is not enabled!");
    }

    @Override
    public void resumeProcessInstance(Flow processInstance) {
        throw new UnsupportedOperationException("workflow is not enabled!");
    }

    @Override
    public Task getUserTask(Flow card, String activityInstanceId) {
        throw new UnsupportedOperationException("workflow is not enabled!");
    }

    @Override
    public String getName() {
        return "dummy";
    }

    @Override
    public Task getTask(Flow flowCard, String taskId) {
        throw new UnsupportedOperationException("workflow is not enabled!");
    }

    @Override
    public Collection<Task> getTaskList(Flow flowCard) {
        throw new UnsupportedOperationException("workflow is not enabled!");
    }

}
