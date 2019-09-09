package org.cmdbuild.task.cardeventprocessing;

import org.cmdbuild.logic.taskmanager.SynchronousEventTask;

public interface SynchronousEventTaskService {

	Object createCardEventListenerForTask(SynchronousEventTask task);

}