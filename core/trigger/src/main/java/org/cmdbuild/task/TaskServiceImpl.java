package org.cmdbuild.task;

import static com.google.common.base.Predicates.instanceOf;
import static com.google.common.collect.FluentIterable.from;
import static org.joda.time.DateTime.now;

import org.apache.commons.lang3.Validate;
import org.cmdbuild.data.store.Storable;
import org.cmdbuild.task.dao.TaskStore;
import org.cmdbuild.exception.TaskManagerException.TaskManagerExceptionType; 
import org.cmdbuild.task.dao.LogicAndStoreConverter;

import com.google.common.base.Function;
import org.cmdbuild.logic.taskmanager.ScheduledTask;
import org.cmdbuild.logic.taskmanager.Task;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailService;
import org.cmdbuild.task.dao.TaskData;
import org.cmdbuild.services.PostStartup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.cmdbuild.logic.taskmanager.TaskService;
import org.cmdbuild.task.cardeventprocessing.SynchronousEventService;
import org.cmdbuild.email.mta.EmailMtaService;

@Component
public class TaskServiceImpl implements TaskService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final LogicAndStoreConverter converter;
	private final TaskStore store;
//	private final SchedulerFacade schedulerFacade;
	private final SynchronousEventService synchronousEventFacade;
	private final EmailService emailLogic;

//	public TaskServiceImpl(LogicAndStoreConverter converter, TaskStore store, SchedulerFacade schedulerFacade, SynchronousEventService synchronousEventFacade, EmailService emailLogic) {
	public TaskServiceImpl(LogicAndStoreConverter converter, TaskStore store,SynchronousEventService synchronousEventFacade, EmailService emailLogic) {
		this.converter = checkNotNull(converter);
		this.store = checkNotNull(store);
//		this.schedulerFacade = checkNotNull(schedulerFacade);
		this.synchronousEventFacade = checkNotNull(synchronousEventFacade);
		this.emailLogic = checkNotNull(emailLogic);
	}

//	@PostStartup
//	public void init() {
//		logger.info("init");
//		read().stream().filter(Task::isActive).forEach((task) -> {
//			try {
//				logger.info("starting task '{}'", task.getId());
//				activate(task.getId());
//			} catch (Exception e) {
//				logger.error("task '{}' cannot be started due to an error", task.getId());
//				logger.error("error starting task", e);
//			}
//		});
//	}
//
//	@Override
//	@Transactional
//	public Long create(Task task) {
//		logger.info("creating a new task '{}'", task);
//		return new CreateAction(task).execute();
//	}
//
//	@Override
//	public List<Task> read() {
//		logger.info("reading all existing tasks");
//		return new ReadAllAction().execute();
//	}
//
//	@Override
//	public List<Task> read(Class<? extends Task> type) {
//		logger.info("reading all existing tasks for type '{}'", type);
//		return new ReadAllAction(type).execute();
//	}
//
//	@Override
//	public <T extends Task> T read(T task, Class<T> type) {
//		logger.info("reading task's details of '{}'", task);
//		return type.cast(new ReadAction(task).execute());
//	}

//	@Override
//	@Transactional
//	public void update(Task task) {
//		logger.info("updating an existing task '{}'", task);
//		new UpdateAction(task).execute();
//	}
//
//	@Override
//	@Transactional
//	public void delete(Task task) {
//		logger.info("deleting an existing task '{}'", task);
//		new AssureNotActiveTaskAction(task).execute();
//		new DeleteEmailsTaskAction(task).execute();
//		new DeleteAction(task).execute();
//	}
//
//	@Override
//	@Transactional
//	public void activate(Long id) {
//		logger.info("activating the existing task '{}'", id);
//		new ActivateAction(id).execute();
//	}
//
//	@Override
//	@Transactional
//	public void deactivate(Long id) {
//		logger.info("deactivating the existing task '{}'", id);
//		new DeactivateAction(id).execute();
//	}
//
//	@Override
//	public void execute(Long id) {
//		try {
//			logger.info("executing the existing task '{}'", id);
//			new ExecuteTaskAction(id).execute();
//		} catch (final Throwable e) {
//			throw TaskManagerExceptionType.TASK_EXECUTION_ERROR.createException(e, id.toString());
//		}
//	}

//	private static class StoreLastExecutionCallback implements Callback {
//
//		private final TaskStore store;
//		private final ScheduledTask task;
//
//		public StoreLastExecutionCallback(final TaskStore store, final ScheduledTask task) {
//			this.store = store;
//			this.task = task;
//		}
//
//		@Override
//		public void start() {
//			// nothing to do
//		}
//
//		@Override
//		public void stop() {
//			final TaskData readed = store.read(task.getId());
//			final TaskData updated = readed.copyOf() //
//					.withLastExecution(now()) //
//					.build();
//			store.updateLastExecution(updated);
//		}
//
//		@Override
//		public void error(final Throwable e) {
//			// nothing to do
//		}
//
//	}

	private static interface Action<T> {

		T execute();

	}

//	private abstract class AbstractTaskAction {
//
//		protected void deleteTask(Task task) {
//			if (task.isScheduledTask()) {
//				schedulerFacade.delete(task.asScheduledTask());
//			}
//			if (task.isSynchronousEventTask()) {
//				synchronousEventFacade.stopEventProcessingForTask(task.asSynchronousEventTask());
//			}
//		}
//
//		protected void createTask(Task task) {
//			if (task.isScheduledTask()) {
//				schedulerFacade.create(task.asScheduledTask(), storeLastExecutionOf(task.asScheduledTask()));
//			}
//			if (task.isSynchronousEventTask()) {
//				synchronousEventFacade.startEventProcessingForTask(task.asSynchronousEventTask());
//			}
//		}
//
//		protected StoreLastExecutionCallback storeLastExecutionOf(final ScheduledTask task) {
//			return new StoreLastExecutionCallback(store, task);
//		}
//	}

//	private class CreateAction extends AbstractTaskAction {
//
//		private final Task task;
//
//		public CreateAction(Task task) {
//			this.task = task;
//		}
//
//		public Long execute() {
//			TaskData storable = converter.taskToTaskData(task);
//			Storable created = store.create(storable);
//			TaskData read = store.read(created);
//			Task taskWithId = converter.taskDataToTask(read);
//			createTask(taskWithId);
//			return read.getId();
//		}
//
//	}

	private static Class<Object> ALL_TYPES = Object.class;

	private class ReadAllAction {

		private final Class<?> type;

		public ReadAllAction() {
			this(null);
		}

		public ReadAllAction(Class<? extends Task> type) {
			this.type = (type == null) ? ALL_TYPES : type;
		}

		public List<Task> execute() {
			return from(store.readAll()) //
					.transform(toLogic()) //
					.filter(instanceOf(type)) //
					.toList();
		}

		private Function<TaskData, Task> toLogic() {
			return (TaskData input) -> converter.taskDataToTask(input);
		}

	}

	private class ReadAction {

		private final Task task;

		public ReadAction(Task task) {
			this.task = task;
		}

		public Task execute() {
			Validate.isTrue(task.getId() != null, "invalid id");
			final TaskData stored = converter.taskToTaskData(task);
			final TaskData read = store.read(stored);
			final Task raw = converter.taskDataToTask(read);
			return raw;
		}

	}

//	private class UpdateAction extends AbstractTaskAction {
//
//		private final Task task;
//
//		public UpdateAction(Task task) {
//			this.task = task;
//		}
//
//		public void execute() {
//			Validate.isTrue(task.getId() != null, "invalid id");
//			TaskData storable = converter.taskToTaskData(task);
//			TaskData read = store.read(storable);
//			Task previous = converter.taskDataToTask(read);
//			deleteTask(previous);
//			store.update(storable);
//			createTask(task);
//		}
//
//	}

//	private class DeleteAction {
//
//		private final Task task;
//
//		public DeleteAction(Task task) {
//			this.task = task;
//		}
//
//		public void execute() {
//			Validate.isTrue(task.getId() != null, "invalid id");
//			if (task.isScheduledTask()) {
//				schedulerFacade.delete(task.asScheduledTask());
//			}
//			if (task.isSynchronousEventTask()) {
//				synchronousEventFacade.stopEventProcessingForTask(task.asSynchronousEventTask());
//			}
//			TaskData storable = converter.taskToTaskData(task);
//			store.delete(storable);
//		}
//
//	}
//
//	private class ActivateAction extends AbstractTaskAction {
//
//		private final Long id;
//
//		public ActivateAction(Long id) {
//			this.id = id;
//		}
//
//		public void execute() {
//			Validate.isTrue(id != null, "invalid id");
//			final TaskData updated;
//			final TaskData stored = store.read(id);
//			if (!stored.isRunning()) {
//				updated = stored.copyOf() //
//						.withRunningStatus(true) //
//						.build();
//			} else {
//				updated = stored;
//			}
//			store.update(updated);
//			Task task = converter.taskDataToTask(updated);
//			createTask(task);
//		}
//
//	}

//	private class DeactivateAction extends AbstractTaskAction {
//
//		private final Long id;
//
//		public DeactivateAction(Long id) {
//			this.id = id;
//		}
//
//		public void execute() {
//			Validate.isTrue(id != null, "invalid id");
//			final TaskData updated;
//			final TaskData stored = store.read(id);
//			if (stored.isRunning()) {
//				updated = stored.copyOf() //
//						.withRunningStatus(false) //
//						.build();
//				store.update(updated);
//			} else {
//				updated = stored;
//			}
//			Task task = converter.taskDataToTask(updated);
//			deleteTask(task);
//		}
//
//	}

//	private class ExecuteTaskAction {
//
//		private final Long id;
//
//		public ExecuteTaskAction(Long id) {
//			this.id = id;
//		}
//
//		public void execute() {
//			Validate.isTrue(id != null, "invalid id");
//			final TaskData stored = store.read(id);
//			final Task executable = converter.taskDataToTask(stored);
//			if (executable.isScheduledTask()) {
//				execute(executable.asScheduledTask());
//			} else {
//				throw new UnsupportedOperationException("unsupported execution for task = " + executable);
//			}
//		}
//
//		private void execute(ScheduledTask task) {
//			schedulerFacade.execute(task, storeLastExecutionOf(task));
//		}
//
//		private StoreLastExecutionCallback storeLastExecutionOf(ScheduledTask task) {
//			return new StoreLastExecutionCallback(store, task);
//		}
//
//	}

	private class DeleteEmailsTaskAction {

		private final Task task;

		public DeleteEmailsTaskAction(Task task) {
			this.task = task;
		}

		public void execute() {
			Validate.isTrue(task.getId() != null, "invalid id");
			for (final Email element : emailLogic.getAllForCard(task.getId())) {
				emailLogic.delete(element);
			}
		}

	}

	private class AssureNotActiveTaskAction {

		private final Task task;

		public AssureNotActiveTaskAction(Task task) {
			this.task = task;
		}

		public void execute() {
			Validate.isTrue(task.getId() != null, "invalid id");
			if (store.read(task.getId()).isRunning()) {
				throw new IllegalStateException("cannot delete task since it's still active");
			}
		}

	}
}
