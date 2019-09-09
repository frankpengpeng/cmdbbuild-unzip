package org.cmdbuild.workflow.model;

import java.util.List;
import org.cmdbuild.widget.model.WidgetData;

public interface TaskDefinition {

    String getId();

    String getDescription();

    String getInstructions();

    List<TaskPerformer> getPerformers();

    TaskPerformer getFirstNonAdminPerformer();

    List<TaskAttribute> getVariables();

    Iterable<TaskMetadata> getMetadata();

    List<WidgetData> getWidgets();

}
