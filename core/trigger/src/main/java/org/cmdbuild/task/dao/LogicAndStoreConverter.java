package org.cmdbuild.task.dao;

import org.cmdbuild.task.dao.TaskData;
import org.cmdbuild.logic.taskmanager.Task;

public interface LogicAndStoreConverter {

	TaskData taskToTaskData(Task source);

	Task taskDataToTask(TaskData source);

}
