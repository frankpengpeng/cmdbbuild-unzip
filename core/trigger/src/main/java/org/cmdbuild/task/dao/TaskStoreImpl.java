package org.cmdbuild.task.dao;

import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Maps.difference;
import static com.google.common.collect.Maps.transformEntries;
import static com.google.common.collect.Maps.transformValues;
import static com.google.common.collect.Maps.uniqueIndex;
import static org.cmdbuild.task.dao.TaskParameterGroupable.groupedBy;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.Validate;
import org.cmdbuild.data.store.Groupable;
import org.cmdbuild.data.store.Storable;
import org.cmdbuild.data.store.Store;

import com.google.common.base.Optional;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.MapDifference;
import com.google.common.collect.MapDifference.ValueDifference;
import com.google.common.collect.Maps.EntryTransformer;
import static java.util.Collections.emptyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * This {@link Store} handles the saving process of {@link TaskData} elements.
 *
 * @since 2.2
 */
@Component
public class TaskStoreImpl implements TaskStore {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final Store<TaskDefinition> definitionsStore;
	private final Store<TaskParameter> parametersStore;
	private final Store<TaskRuntime> runtimeStore;

	public TaskStoreImpl(Store<TaskDefinition> definitionsStore, Store<TaskParameter> parametersStore, Store<TaskRuntime> runtimeStore) {
		this.definitionsStore = checkNotNull(definitionsStore);
		this.parametersStore = checkNotNull(parametersStore);
		this.runtimeStore = checkNotNull(runtimeStore);
	}

	@Override
	public Storable create(TaskData storable) {
		logger.info("creating new element '{}'", storable);
		return new CreateAction(storable).execute();
	}

	@Override
	public TaskData read(Storable storable) {
		logger.info("reading existing element '{}'", storable);
		return new ReadAction(storable).execute();
	}

	@Override
	public Collection<TaskData> readAll() {
		logger.info("getting all existing elements");
		return new ReadAllAction().execute();
	}

	@Override
	public Collection<TaskData> readAll(Groupable groupable) {
		logger.info("getting all existing elements for group '{}'", groupable);
		return new ReadAllAction(groupable).execute();
	}

	@Override
	public void update(TaskData storable) {
		logger.info("updating existing element '{}'", storable);
		new UpdateAction(storable).execute();
	}

	@Override
	public void delete(Storable storable) {
		logger.info("deleting existing element '{}'", storable);
		new DeleteAction(storable).execute();
	}

	@Override
	public TaskData read(Long id) {
		logger.info("reading existing element with id '{}'", id);
		for (final TaskData element : readAll()) {
			if (element.getId().equals(id)) {
				return element;
			}
		}
		throw new NoSuchElementException();
	}

	@Override
	public void updateLastExecution(final TaskData storable) {
		logger.info("updating only last execution for existing element '{}'", storable);
		new UpdateLastExecutionAction(storable).execute();
	}

	private abstract class AbstractAction {

		protected TaskDefinition definitionOf(TaskData task) {
			return TaskDefinition.builder() //
					.withTaskType(task.getTaskType())
					.withId(task.getId()) //
					.withDescription(task.getDescription()) //
					.withRunning(task.isRunning()) //
					.withCronExpression(task.getCronExpression()) //
					.build();
		}

		protected TaskData merge(TaskDefinition definition, Iterable<TaskParameter> parameters, TaskRuntime runtime) {
			return TaskData.builder()
					.withTaskType(definition.getTaskType())
					.withId(definition.getId()) //
					.withDescription(definition.getDescription()) //
					.withRunningStatus(definition.isRunning()) //
					.withCronExpression(definition.getCronExpression()) //
					.withLastExecution(runtime.getLastExecution()) //
					.withParameters(transformValues( //
							uniqueIndex(parameters, TaskParameter::getKey), //
							TaskParameter::getValue)) //
					.build();
		}

		protected EntryTransformer<String, String, TaskParameter> toTaskParameterMapOf(TaskDefinition definition) {
			return (final String key, final String value) -> TaskParameter.newInstance().withOwner(definition.getId()) //
					.withKey(key) //
					.withValue(value) //
					.build();
		}

	}

	private class CreateAction extends AbstractAction {

		private final TaskData storable;

		public CreateAction(TaskData storable) {
			this.storable = storable;
		}

		public Storable execute() {
			final TaskDefinition definition = definitionOf(storable);
			final Storable createdDefinition = definitionsStore.create(definition);
			final TaskDefinition readedDefinition = definitionsStore.read(createdDefinition);
			for (final TaskParameter element : transformEntries(storable.getParameters(),
					toTaskParameterMapOf(readedDefinition)).values()) {
				parametersStore.create(element);
			}
			final Storable createdRuntime = runtimeStore.create(TaskRuntime.newInstance() //
					.withOwner(readedDefinition.getId()) //
					.withLastExecution(storable.getLastExecution()) //
					.build());
			final TaskRuntime readedRuntime = runtimeStore.read(createdRuntime);
			return merge(readedDefinition, emptyList(), readedRuntime);
		}

	}

	private class ReadAction extends AbstractAction {

		private final Storable storable;

		public ReadAction(Storable storable) {
			this.storable = storable;
		}

		public TaskData execute() {
			final TaskData task = TaskData.class.cast(storable);
			final TaskDefinition definition = definitionsStore.read(definitionOf(task));
			final Iterable<TaskParameter> parameters = parametersStore.readAll(groupedBy(definition));
			final Optional<TaskRuntime> _runtime = from(runtimeStore.readAll(groupedBy(definition))).first();
			final TaskRuntime runtime = _runtime.isPresent() ? _runtime.get()
					: TaskRuntime.newInstance() //
							.withOwner(definition.getId()) //
							.build();
			return merge(definition, parameters, runtime);
		}

	}

	private class ReadAllAction extends AbstractAction {

		private final Groupable groupable;

		public ReadAllAction() {
			this(null);
		}

		public ReadAllAction(Groupable groupable) {
			this.groupable = groupable;
		}

		public List<TaskData> execute() {
			Iterable<TaskDefinition> list = (groupable == null) ? definitionsStore.readAll() : definitionsStore.readAll(groupable);
			return from(list) //
					.transform((TaskDefinition input) -> {
						Iterable<TaskParameter> parameters = parametersStore.readAll(groupedBy(input));
						Optional<TaskRuntime> _runtime = from(runtimeStore.readAll(groupedBy(input))).first();
						TaskRuntime runtime = _runtime.isPresent() ? _runtime.get() : TaskRuntime.newInstance().withOwner(input.getId()).build();
						return merge(input, parameters, runtime);
					}) //
					.toList();
		}

	}

	private class UpdateAction extends AbstractAction {

		private final TaskData storable;

		public UpdateAction(TaskData storable) {
			this.storable = storable;
		}

		public void execute() {
			final TaskDefinition definition = definitionOf(storable);
			definitionsStore.update(definition);
			final Map<String, TaskParameter> left = transformEntries(storable.getParameters(),
					toTaskParameterMapOf(definition));
			final Map<String, TaskParameter> right = uniqueIndex(parametersStore.readAll(groupedBy(definition)), TaskParameter::getKey);
			final MapDifference<String, TaskParameter> difference = difference(left, right);
			for (final TaskParameter element : difference.entriesOnlyOnLeft().values()) {
				parametersStore.create(element);
			}
			for (final ValueDifference<TaskParameter> valueDifference : difference.entriesDiffering().values()) {
				final TaskParameter element = valueDifference.leftValue();
				parametersStore.update(TaskParameter.newInstance() //
						.withId(valueDifference.rightValue().getId()) //
						.withOwner(element.getOwner()) //
						.withKey(element.getKey()) //
						.withValue(element.getValue()) //
						.build());
			}
			for (final TaskParameter element : difference.entriesOnlyOnRight().values()) {
				parametersStore.delete(element);
			}
			/*
			 * should be one-only, but who knows...
			 */
			boolean done = false;
			for (final TaskRuntime element : runtimeStore.readAll(groupedBy(definition))) {
				runtimeStore.update(TaskRuntime.newInstance() //
						.withId(element.getId()) //
						.withOwner(element.getOwner()) //
						.withLastExecution(storable.getLastExecution()) //
						.build());
				done = true;
			}
			if (!done) {
				runtimeStore.create(TaskRuntime.newInstance() //
						.withOwner(storable.getId()) //
						.withLastExecution(storable.getLastExecution()) //
						.build());
			}
		}

	}

	private class DeleteAction extends AbstractAction {

		private final Storable storable;

		public DeleteAction(Storable storable) {
			this.storable = storable;
		}

		public void execute() {
			Validate.isInstanceOf(TaskData.class, storable);
			final TaskData task = TaskData.class.cast(storable);
			final TaskDefinition definition = definitionsStore.read(definitionOf(task));
			/*
			 * should be one-only, but who knows...
			 */
			for (final Storable element : runtimeStore.readAll(groupedBy(definition))) {
				runtimeStore.delete(element);
			}
			for (final Storable element : parametersStore.readAll(groupedBy(definition))) {
				parametersStore.delete(element);
			}
			definitionsStore.delete(definition);
		}

	}

	private class UpdateLastExecutionAction extends AbstractAction {

		private final TaskData storable;

		public UpdateLastExecutionAction(TaskData storable) {
			this.storable = storable;
		}

		public void execute() {
			final TaskDefinition definition = definitionOf(storable);
			/*
			 * should be one-only, but who knows...
			 */
			boolean done = false;
			for (final TaskRuntime element : runtimeStore.readAll(groupedBy(definition))) {
				runtimeStore.update(TaskRuntime.newInstance() //
						.withId(element.getId()) //
						.withOwner(element.getOwner()) //
						.withLastExecution(storable.getLastExecution()) //
						.build());
				done = true;
			}
			if (!done) {
				runtimeStore.create(TaskRuntime.newInstance() //
						.withOwner(storable.getId()) //
						.withLastExecution(storable.getLastExecution()) //
						.build());
			}
		}

	}

}
