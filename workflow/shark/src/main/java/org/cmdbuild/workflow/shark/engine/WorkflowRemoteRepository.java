package org.cmdbuild.workflow.shark.engine;

import java.util.List;
import java.util.Map;
import org.cmdbuild.workflow.model.PlanPackageDefinitionInfo;
import org.cmdbuild.workflow.model.PlanPackageDefinitionInfoAndData;
import org.cmdbuild.workflow.model.TaskInfo;

public interface WorkflowRemoteRepository {

	Map<String, Object> getProcessInstanceVariables(String procInstId);

	List<TaskInfo> findOpenActivitiesForProcessInstance(String procInstId);

	void deleteProcessInstance(String procInstId);

	/**
	 * Retrieves all package versions by package id.
	 *
	 * @param pkgId
	 * @return all package versions
	 * @
	 */
	String[] getPackageVersions(String pkgId);

	/**
	 *
	 * @param pkgId package id can be null if new
	 * @param pkgDefData
	 * @return uploaded package info
	 * @
	 */
	PlanPackageDefinitionInfo uploadPackage(String pkgId, byte[] pkgDefData);

	byte[] downloadPackage(String pkgId, String pkgVer);

	/**
	 * Download the last version of every open package
	 *
	 * @return an array of package versions
	 * @
	 */
	List<PlanPackageDefinitionInfoAndData> downloadAllPackages();
}
