package org.cmdbuild.workflow;

import org.cmdbuild.workflow.inner.WorkflowServiceBase;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.activation.DataSource;
import javax.annotation.Nullable;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.workflow.model.Task;
import org.cmdbuild.widget.model.WidgetData;
import static org.cmdbuild.workflow.WorkflowService.WorkflowVariableProcessingStrategy.SET_ONLY_TASK_VARIABLES;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.model.Process;
import org.cmdbuild.workflow.model.TaskDefinition;
import org.cmdbuild.workflow.model.XpdlInfo;

public interface WorkflowService extends WorkflowServiceBase {

    TaskDefinition getEntryTaskForCurrentUser(String processId);

    boolean isWorkflowEnabled();

    boolean isWorkflowEnabledAndProcessRunnable(String classId);

    PagedElements<Flow> getUserFlowCardsByClasseIdAndQueryOptions(String classId, DaoQueryOptions queryOptions);

    PagedElements<Flow> getFlowCardsByClasseAndQueryOptions(Classe classe, DaoQueryOptions queryOptions);

    @Nullable
    Flow getFlowCardOrNull(Process classe, Long cardId);

    Process getProcess(String classId);

    XpdlInfo addXpdl(String classId, String provider, DataSource dataSource);

    DataSource getXpdlByClasseIdAndPlanId(String classId, String planId);

    Collection<Process> getActiveProcessClasses();

    Collection<Process> getAllProcessClasses();

    Task getUserTask(Flow card, String activityInstanceId);

    List<WidgetData> getWidgetsForUserTask(String classeId, Long cardId, String taskId);

    FlowAdvanceResponse startProcess(String classId, Map<String, ?> vars, WorkflowVariableProcessingStrategy variableProcessingStrategy, boolean advance);

    FlowAdvanceResponse updateProcess(String classId, Long cardId, String taskId, Map<String, ?> vars, WorkflowVariableProcessingStrategy variableProcessingStrategy, boolean advance);

    FlowAdvanceResponse updateProcessWithOnlyTask(String classId, Long cardId, Map<String, ?> vars, WorkflowVariableProcessingStrategy variableProcessingStrategy, boolean advance);

    DataSource getXpdlTemplate(String classId);

    void suspendProcess(String classId, Long cardId);

    void resumeProcess(String classId, Long cardId);

    void abortProcess(String classId, Long cardId);

    void migrateFlowInstancesToNewProviderWithExistingXpdl(String classId);

    void migrateFlowInstancesToNewProvider(String classId, DataSource newXpdlDataSource);

    String getDefaultProvider();

    default List<Flow> getUserFlowCardsByClasseId(String classId) {
        return getUserFlowCardsByClasseIdAndQueryOptions(classId, DaoQueryOptionsImpl.emptyOptions()).elements();
    }

    default Flow getFlowCard(Process classe, Long cardId) {
        return checkNotNull(getFlowCardOrNull(classe, cardId), "flow card not found for classe = %s cardId = %s", classe, cardId);
    }

    default Flow getFlowCard(String classId, Long cardId) {
        Process classe = getProcess(classId);
        return getFlowCard(classe, cardId);
    }

    default FlowAdvanceResponse startProcess(String classId, Map<String, ?> vars, boolean advance) {
        return startProcess(classId, vars, SET_ONLY_TASK_VARIABLES, advance);
    }

    default FlowAdvanceResponse updateProcess(String classId, Long cardId, String taskId, Map<String, ?> vars, boolean advance) {
        return updateProcess(classId, cardId, taskId, vars, SET_ONLY_TASK_VARIABLES, advance);
    }

    default FlowAdvanceResponse updateProcessWithOnlyTask(String classId, Long cardId, Map<String, ?> vars, boolean advance) {
        return updateProcessWithOnlyTask(classId, cardId, vars, SET_ONLY_TASK_VARIABLES, advance);
    }

    enum WorkflowVariableProcessingStrategy {
        SET_ONLY_TASK_VARIABLES, SET_ALL_CLASS_VARIABLES
    }

}
