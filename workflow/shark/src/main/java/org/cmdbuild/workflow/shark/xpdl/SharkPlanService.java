/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.shark.xpdl;

import java.util.List;
import javax.activation.DataSource;
import javax.annotation.Nullable;
import org.cmdbuild.workflow.model.TaskDefinition;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.model.Process;

public interface SharkPlanService {

	@Nullable
	String getPlanIdOrNull(Classe classe);

	List<TaskDefinition> getEntryTasks(String planId);

	/**
	 * Returns the process definition versions available in the repository. They
	 * can be in use or not. /
	 *
	 **
	 * @param process
	 * @return list of process definition versions
	 */
	String[] getVersions(Process process);

	/**
	 * Returns one version of the process definition document for the process.
	 *
	 * @param process
	 * @param version
	 * @return document
	 * @
	 */
	DataSource getDefinition(String classId, String version);

	/**
	 * Associates a package definition to a process
	 *
	 * @param process
	 * @param pkgDefData
	 * @
	 */
	void updateDefinition(Process process, DataSource pkgDefData);

	/**
	 * Gets the activity definition by process instance and identifier.
	 *
	 * @param processInstance process instance
	 * @param activityDefinitionId activity definition id
	 * @return activity definition
	 * @
	 */
	TaskDefinition getTaskDefinition(Flow processInstance, String activityDefinitionId);

	/**
	 * Returns the package id for the process.
	 *
	 * @param process
	 * @return package id
	 */
	String getPackageId(Process process);

	/**
	 * Returns the process definition id for the process.
	 *
	 * @param process
	 * @return process definition id
	 */
	String getProcessDefinitionId(Process process);

	/**
	 * Returns the process class name for that process definition id.
	 *
	 * The signature should be changed to return a CMProcessClass.
	 *
	 * @param process definition id
	 * @return process class name
	 */
	String getClassNameOrNull(String processDefinitionId);
}
