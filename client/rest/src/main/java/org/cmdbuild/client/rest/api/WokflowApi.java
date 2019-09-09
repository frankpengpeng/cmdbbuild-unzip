/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.api;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.cmdbuild.client.rest.core.RestServiceClient;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.client.rest.model.AttributeDetail;
import org.cmdbuild.workflow.model.AdvancedFlowStatus;

public interface WokflowApi extends RestServiceClient {

    WokflowServiceWithFlowDataAndStatus start(String processId, FlowData process);

    FlowData get(String processId, String instanceId);

    WokflowServiceWithFlowDataAndStatus advance(String processId, String instanceId, String activityId, FlowData simpleProcessInstance);

    List<TaskInfo> getTaskList(String processId, String instanceId);

    TaskDetail getTask(String processId, String instanceId, String taskId);

    List<PlanInfo> getPlans();

    PlanInfo getPlan(String processId);

    WokflowServiceWithPlanVersionInfo uploadPlanVersion(String processId, InputStream data);

    void migrateProcess(String processId);

    void uploadPlanVersionAndMigrateProcess(String processId, InputStream data);

    String downloadPlanVersion(String classId, String planId);

    byte[] downloadFlowGraph(String classId, long toLong);

    byte[] downloadSimplifiedFlowGraph(String classId, long toLong);

    List<PlanVersionInfo> getPlanVersions(String processId);

    String getXpdlTemplate(String processId);

    TaskDetail getStartProcessTask(String processId);

    interface WokflowServiceWithFlowDataAndStatus {

        FlowDataAndStatus getFlowData();

        WokflowApi then();
    }

    interface WokflowServiceWithPlanVersionInfo {

        PlanVersionInfo getPlanVersionInfo();

        WokflowApi then();
    }

    interface PlanVersionInfo {

        String getId();

        String getVersion();

        String getProvider();

        String getPlanId();

        boolean isDefault();
    }

    interface PlanInfo {

        String getId();

        String getDescription();

        @Nullable
        String getProvider();

    }

    interface TaskInfo {

        String getId();

        String getDescription();
    }

    interface TaskDetail extends TaskInfo {

        List<TaskParam> getParams();

    }

    interface TaskParam {

        String getName();

        boolean isWritable();

        boolean isRequired();

        boolean isAction();

        AttributeDetail getDetail();
    }

    interface FlowDataAndStatus extends FlowData {

        String getFlowCardId();

        AdvancedFlowStatus getFlowStatus();

        List<TaskDetail> getTaskList();
    }

    interface FlowData {

        @Nullable
        String getFlowId();

        default boolean hasId() {
            return !isBlank(getFlowId());
        }

        Map<String, Object> getAttributes();

        String getStatus();
    }

}
