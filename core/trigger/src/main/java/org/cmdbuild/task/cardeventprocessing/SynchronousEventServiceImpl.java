package org.cmdbuild.task.cardeventprocessing;

import static com.google.common.base.Preconditions.checkNotNull;
import org.apache.commons.lang3.Validate;
import org.cmdbuild.logic.taskmanager.SynchronousEventTask;
import org.springframework.stereotype.Component;

@Component
public class SynchronousEventServiceImpl implements SynchronousEventService {

	private final CardObserverService eventService;
	private final SynchronousEventTaskService taskService;

	public SynchronousEventServiceImpl(CardObserverService eventService, SynchronousEventTaskService taskService) {
		this.eventService = checkNotNull(eventService);
		this.taskService = checkNotNull(taskService);
	}

	@Override
	public void startEventProcessingForTask(SynchronousEventTask task) {
		Validate.notNull(task.getId(), "missing id");
		if (task.isActive()) {
			Object listener = taskService.createCardEventListenerForTask(task);
			eventService.add(task.getId().toString(), listener); //TODO ensure that this is propagated on all cluster nodes
		}
	}

	@Override
	public void stopEventProcessingForTask(SynchronousEventTask task) {
		Validate.notNull(task.getId(), "missing id");
		eventService.remove(task.getId().toString()); //TODO ensure that this is propagated on all cluster nodes
	}

}
