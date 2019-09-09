package org.cmdbuild.task.cardeventprocessing;

import org.cmdbuild.logic.taskmanager.SynchronousEventTask;

public interface SynchronousEventService {

	void startEventProcessingForTask(SynchronousEventTask task);

	void stopEventProcessingForTask(SynchronousEventTask task);

}
