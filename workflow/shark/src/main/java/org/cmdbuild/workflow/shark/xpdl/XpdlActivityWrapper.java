package org.cmdbuild.workflow.shark.xpdl;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.workflow.model.TaskMetadata;
import static com.google.common.collect.FluentIterable.from;
import java.util.ArrayList;
import java.util.List;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static com.google.common.base.Predicates.notNull;
import static java.lang.String.format;

import org.cmdbuild.common.annotations.Legacy;
import org.cmdbuild.widget.WidgetFactoryService;
import org.cmdbuild.widget.model.WidgetData;
import static org.cmdbuild.widget.utils.WidgetUtils.isWorwflowWidgetType;
import static org.cmdbuild.widget.utils.WidgetUtils.toWidgetData;
import org.cmdbuild.workflow.model.TaskPerformer;
import org.cmdbuild.workflow.model.TaskDefinition;
import org.cmdbuild.workflow.model.TaskAttribute;

public class XpdlActivityWrapper implements TaskDefinition {

    @Legacy("As in 1.x")
    public static final String ADMIN_START_XA = "AdminStart";

    private final XpdlActivity task;
    private final XpdlExtendedAttributeVariableFactory variableFactory;
    private final XpdlExtendedAttributeMetadataFactory metadataFactory;

    public XpdlActivityWrapper(XpdlActivity xpdlActivity, XpdlExtendedAttributeVariableFactory variableFactory, XpdlExtendedAttributeMetadataFactory metadataFactory, WidgetFactoryService widgetFactory) {
        this.task = checkNotNull(xpdlActivity, "missing " + XpdlActivity.class);
        this.variableFactory = checkNotNull(variableFactory, "missing " + XpdlExtendedAttributeVariableFactory.class);
        this.metadataFactory = checkNotNull(metadataFactory, "missing " + XpdlExtendedAttributeMetadataFactory.class);
    }

    @Override
    public List<TaskPerformer> getPerformers() {
        final List<TaskPerformer> out = new ArrayList<>();
        out.add(getFirstNonAdminPerformer());
        if (isAdminStart()) {
            out.add(TaskPerformer.newAdminPerformer());
        }
        return out;
    }

    @Legacy("As in 1.x")
    private boolean isAdminStart() {
        return task.hasExtendedAttributeIgnoreCase(ADMIN_START_XA); //TODO replicate this in river
    }

    @Override
    public String getId() {
        return task.getId();
    }

    @Override
    public String getDescription() {
        return task.getName();
    }

    @Override
    public String getInstructions() {
        return task.getDescription();
    }

    @Override
    public TaskPerformer getFirstNonAdminPerformer() {
        String performerString = task.getFirstPerformer();
        if (performerString == null) {
            return TaskPerformer.newUnknownPerformer();
        }
        if (task.getProcess().hasRoleParticipant(performerString)) {
            return TaskPerformer.newRolePerformer(performerString);
        } else {
            return TaskPerformer.newExpressionPerformer(performerString);
        }
    }

    @Override
    public List<TaskAttribute> getVariables() {
        final List<TaskAttribute> vars = new ArrayList<>();
        for (final XpdlExtendedAttribute xa : task.getExtendedAttributes()) {
            final TaskAttribute v = variableFactory.createVariable(xa);
            if (v != null) {
                vars.add(v);
            }
        }
        return vars;
    }

    @Override
    public Iterable<TaskMetadata> getMetadata() {
        return from(task.getExtendedAttributes()) //
                .transform((final XpdlExtendedAttribute input) -> metadataFactory.createMetadata(input)) //
                .filter(TaskMetadata.class);
    }

    @Override
    public List<WidgetData> getWidgets() {
        return task.getExtendedAttributes().stream().map((xa) -> {
            if (!isBlank(xa.getKey()) && !isBlank(xa.getValue()) && isWorwflowWidgetType(xa.getKey())) {
                return toWidgetData(format("shark_proc_%s_task_%s_", task.getProcess().getId(), task.getId()), xa.getKey(), xa.getValue());
            } else {
                return null;
            }
        }).filter(notNull()).collect(toList());
    }

}
