package org.cmdbuild.task.dao;

import org.cmdbuild.data.store.Store;

public interface TaskStore extends Store<TaskData> {

	TaskData read(Long id);

	void updateLastExecution(TaskData storable);

}