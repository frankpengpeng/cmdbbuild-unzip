package org.cmdbuild.task.dao;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import static org.cmdbuild.common.Constants.DESCRIPTION_ATTRIBUTE;

import java.util.Map;

import org.cmdbuild.data.store.dao.BaseStorableConverter;

import com.google.common.collect.Maps;
import org.cmdbuild.logic.taskmanager.Task.TaskType;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.beans.Card;

@Component
public class TaskDefinitionConverter extends BaseStorableConverter<TaskDefinition> {

	private static final String CLASSNAME = "_Task";

	private static final String CRON_EXPRESSION = "CronExpression";
	private static final String TYPE = "Type";
	private static final String RUNNING = "Running";

	private static final String TYPE_ASYNCHRONOUS_EVENT = "asynchronous_event";
	private static final String TYPE_CONNECTOR = "connector";
	private static final String TYPE_EMAIL = "emailService";
	private static final String TYPE_GENERIC = "generic";
	private static final String TYPE_SYNCHRONOUS_EVENT = "synchronous_event";
	private static final String TYPE_WORKFLOW = "workflow";

	@Override
	public String getClassName() {
		return CLASSNAME;
	}

	@Override
	public TaskDefinition convert(Card card) {
		return TaskDefinition.builder()
				.withTaskType(taskTypeFromCardType(card).name())
				.withId(card.getId())
				.withDescription(card.get(DESCRIPTION_ATTRIBUTE, String.class))
				.withCronExpression(card.get(CRON_EXPRESSION, String.class))
				.withRunning(card.get(RUNNING, Boolean.class))
				.build();
	}

	@Override
	public Map<String, Object> getValues(TaskDefinition storable) {
		Map<String, Object> values = map();
		values.put(DESCRIPTION_ATTRIBUTE, storable.getDescription());
		values.put(CRON_EXPRESSION, storable.getCronExpression());
		values.put(TYPE, cardTypeFromTaskType(TaskType.valueOf(storable.getTaskType())));
		values.put(RUNNING, storable.isRunning());
		return values;
	}

	private final static BiMap<String, TaskType> CARD_TASK_TO_TASK_TYPE = Maps.unmodifiableBiMap(HashBiMap.create(map(
			TYPE_ASYNCHRONOUS_EVENT, TaskType.ASYNC_EVENT,
			TYPE_CONNECTOR, TaskType.CONNECTOR,
			TYPE_EMAIL, TaskType.READ_EMAIL,
			TYPE_GENERIC, TaskType.GENERIC,
			TYPE_SYNCHRONOUS_EVENT, TaskType.SYNC_EVENT,
			TYPE_WORKFLOW, TaskType.START_WORKFLOW
	)));
	private final static Map<TaskType, String> TASK_TYPE_TO_CARD_TASK = CARD_TASK_TO_TASK_TYPE.inverse();

	private TaskType taskTypeFromCardType(Card card) {
		String type = card.get(TYPE, String.class);
		return checkNotNull(CARD_TASK_TO_TASK_TYPE.get(type), "unsupported task type from card = %s", type);
	}

	private String cardTypeFromTaskType(TaskType taskType) {
		return checkNotNull(TASK_TYPE_TO_CARD_TASK.get(taskType), "unsupported task type = %s", taskType);
	}
}
