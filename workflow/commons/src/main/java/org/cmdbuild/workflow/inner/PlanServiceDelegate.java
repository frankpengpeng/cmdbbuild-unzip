package org.cmdbuild.workflow.inner;

import com.google.common.eventbus.EventBus;
import java.util.List;
import javax.annotation.Nullable;
import org.cmdbuild.workflow.model.TaskDefinition;
import org.cmdbuild.workflow.model.WorkflowNamedService;
import org.cmdbuild.dao.entrytype.Classe;

public interface PlanServiceDelegate extends WorkflowNamedService {

    @Nullable
    String getPlanIdOrNull(Classe classe);

    @Nullable
    String getClassNameOrNull(String planId);

    List<TaskDefinition> getEntryTasks(String planId);

    List<TaskDefinition> getAllTasks(String planId);

    boolean hasPlanId(String suggestedPlanId);

    EventBus getEventBus();

}
