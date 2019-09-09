package org.cmdbuild.workflow.shark.xpdl;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.EventBus;
import java.util.List;
import javax.annotation.Nullable;

import org.cmdbuild.workflow.model.TaskDefinition;
import org.cmdbuild.workflow.model.PlanInfo;
import org.cmdbuild.workflow.model.PlanPackageDefinitionInfo;

public interface XpdlPlanRepository {

	List<TaskDefinition> getEntryTasks(String processDefinitionId);

	TaskDefinition getTask(PlanInfo procDefInfo, String activityDefinitionId);

	String[] getPackageVersions(String className);

	String getPackageId(String className);

	String getProcessDefinitionId(String className);

	@Nullable
	ProcessInfo getProcessInfoByPlanIdOrNull(String planId);

	@Nullable
	ProcessInfo getProcessInfoByClassIdOrNull(String classId);

	default ProcessInfo getProcessInfoByPlanId(String planId) {
		return checkNotNull(getProcessInfoByPlanIdOrNull(planId), "process info not found for planId = '%s'", planId);
	}

	@Nullable
	String getProcessClassName(String processDefinitionId);

	byte[] downloadPackage(String className, String pkgVer);

	PlanPackageDefinitionInfo uploadPackage(String className, byte[] pkgDefData);

	interface ProcessInfo {

		String getClassName();

		String getDefinitionId();

		List<TaskDefinition> getEntryTasks();

		List<TaskDefinition> getAllTasks();

		TaskDefinition getTaskById(String taskId);

		String getPlanId();
	}
}
