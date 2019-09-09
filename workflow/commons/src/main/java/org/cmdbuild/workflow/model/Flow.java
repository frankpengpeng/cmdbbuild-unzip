package org.cmdbuild.workflow.model;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import javax.annotation.Nullable;
import org.cmdbuild.dao.beans.Card;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import org.cmdbuild.workflow.utils.FlowStatusUtils;

public interface Flow extends Card {

    Long getCardId();

    String getFlowId();

    @Override
    Process getType();

    FlowStatus getStatus();

    @Nullable
    PlanInfo getPlanInfoOrNull();

    default PlanInfo getPlanInfo() {
        return checkNotNull(getPlanInfoOrNull(), "plan info not available for flow = %s", this);
    }

    default String getPlanId() {
        return getPlanInfo().getDefinitionId();
    }

    default List<FlowActivity> getFlowActivities() {
        throw new UnsupportedOperationException("TODO");
    }

    default List<String> getPreviousExecutors() {
        throw new UnsupportedOperationException("TODO");
    }

    default FlowActivity getFlowActivityByDefinitionId(String definitionId) {
        return getFlowActivities().stream().filter(t -> equal(t.getDefinitionId(), definitionId)).collect(onlyElement("task not found for definitionId = %s", definitionId));
    }

    default FlowActivity getFlowActivityByInstanceId(String instanceId) {
        return getFlowActivities().stream().filter(t -> equal(t.getInstanceId(), instanceId)).collect(onlyElement("task not found for instanceId = %s", instanceId));
    }

    default boolean isCompleted() {
        return FlowStatusUtils.isCompleted(getStatus());
    }

}
