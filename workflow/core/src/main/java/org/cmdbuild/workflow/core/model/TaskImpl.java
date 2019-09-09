/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.core.model;

import com.google.common.base.Supplier;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptyList;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.widget.model.Widget;
import org.cmdbuild.workflow.model.Task;
import org.cmdbuild.workflow.model.TaskDefinition;
import org.cmdbuild.workflow.model.Flow;

public class TaskImpl implements Task {

    private final String taskId, flowId;
    private final String activityInstancePerformer;
    private final TaskDefinition taskDefinitionId;
    private final boolean isAdvanceable;
    private final Supplier<List<Widget>> taskWidgetSupplier;
    private final Flow card;

    private TaskImpl(SimpleTaskBuilder builder) {
        this.taskId = checkNotBlank(builder.taskId, "task id cannot be null");
        this.flowId = checkNotBlank(builder.flowId, "flow id cannot be null");
        this.activityInstancePerformer = checkNotBlank(builder.activityInstancePerformer, "task performer is blank for task %s", taskId);
        this.taskDefinitionId = checkNotNull(builder.taskDefinitionId);
        this.isAdvanceable = checkNotNull(builder.isAdvanceable);
        this.taskWidgetSupplier = checkNotNull(builder.taskWidgetSupplier);
        this.card = checkNotNull(builder.card);
    }

    public Flow getCard() {
        return card;
    }

    @Override
    public Flow getProcessInstance() {
        return card;
    }

    @Override
    public String getId() {
        return taskId;
    }

    @Override
    public String getPerformerName() {
        return activityInstancePerformer;
    }

    @Override
    public TaskDefinition getDefinition() {
        return taskDefinitionId;
    }

    public Supplier<List<Widget>> getTaskWidgetSupplier() {
        return taskWidgetSupplier;
    }

    public static SimpleTaskBuilder builder() {
        return new SimpleTaskBuilder();
    }

    @Override
    public String getFlowId() {
        return flowId;
    }

    @Override
    public List<Widget> getWidgets() {
        return getTaskWidgetSupplier().get();
    }

    @Override
    public boolean isWritable() {
        return isAdvanceable;
    }

    @Override
    public String toString() {
        return "Task{" + "taskId=" + taskId + ", definitionId=" + getDefinition().getId() + ", flow=" + card + '}';
    }

    public static SimpleTaskBuilder copyOf(TaskImpl source) {
        return new SimpleTaskBuilder()
                .withCard(source.getCard())
                .withTaskId(source.getId())
                .withFlowId(source.getFlowId())
                .withTaskPerformer(source.getPerformerName())
                .withTaskDefinition(source.getDefinition())
                .isAdvanceable(source.isWritable())
                .withTaskWidgetSupplier(source.getTaskWidgetSupplier());
    }

    public static class SimpleTaskBuilder implements Builder<TaskImpl, SimpleTaskBuilder> {

        private String taskId, flowId;
        private String activityInstancePerformer;
        private TaskDefinition taskDefinitionId;
        private Boolean isAdvanceable = true;
        private Supplier<List<Widget>> taskWidgetSupplier = () -> emptyList();
        private Flow card;

        public SimpleTaskBuilder withCard(Flow card) {
            this.card = card;
            return this;
        }

        public SimpleTaskBuilder withTaskId(String taskId) {
            this.taskId = taskId;
            return this;
        }

        public SimpleTaskBuilder withFlowId(String flowId) {
            this.flowId = flowId;
            return this;
        }

        public SimpleTaskBuilder withTaskPerformer(String activityInstancePerformer) {
            this.activityInstancePerformer = activityInstancePerformer;
            return this;
        }

        public SimpleTaskBuilder withTaskDefinition(TaskDefinition taskDefinitionId) {
            this.taskDefinitionId = taskDefinitionId;
            return this;
        }

        public SimpleTaskBuilder isAdvanceable(Boolean isAdvanceable) {
            this.isAdvanceable = isAdvanceable;
            return this;
        }

        public SimpleTaskBuilder withTaskWidgetSupplier(Supplier<List<Widget>> taskWidgetSupplier) {
            this.taskWidgetSupplier = taskWidgetSupplier;
            return this;
        }

        @Override
        public TaskImpl build() {
            return new TaskImpl(this);
        }

    }
}
