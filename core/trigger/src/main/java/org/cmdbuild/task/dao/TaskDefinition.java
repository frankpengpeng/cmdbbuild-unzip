package org.cmdbuild.task.dao;

import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import org.cmdbuild.data.store.Storable;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class TaskDefinition implements Storable {

	private final Long id;
	private final String description;
	private final boolean running;
	private final String cronExpression, taskType;

	private TaskDefinition(TaskDefinitionBuilder builder) {
		this.id = builder.id;
		this.description = builder.description;
		this.running = builder.running;
		this.cronExpression = builder.cronExpression;
		this.taskType = checkNotBlank(builder.taskType);
	}

	@Override
	public String getIdentifier() {
		return id.toString();
	}

	public String getTaskType() {
		return taskType;
	}

	public Long getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public boolean isRunning() {
		return running;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	@Override
	public String toString() {
		return reflectionToString(this, SHORT_PREFIX_STYLE);
	}

	public static TaskDefinitionBuilder builder() {
		return new TaskDefinitionBuilder();
	}

	public static class TaskDefinitionBuilder implements org.apache.commons.lang3.builder.Builder<TaskDefinition> {

		private Long id;
		private String description;
		private String cronExpression, taskType;
		private Boolean running;

		private TaskDefinitionBuilder() {
		}

		@Override
		public TaskDefinition build() {
			validate();
			return new TaskDefinition(this);
		}

		private void validate() {
			running = (running == null) ? Boolean.FALSE : running;
		}

		@Override
		public String toString() {
			return reflectionToString(this, SHORT_PREFIX_STYLE);
		}

		public TaskDefinitionBuilder withId(final Long id) {
			this.id = id;
			return this;
		}

		public TaskDefinitionBuilder withDescription(final String description) {
			this.description = description;
			return this;
		}

		public TaskDefinitionBuilder withTaskType(final String taskType) {
			this.taskType = taskType;
			return this;
		}

		public TaskDefinitionBuilder withRunning(final Boolean running) {
			this.running = running;
			return this;
		}

		public TaskDefinitionBuilder withCronExpression(final String cronExpression) {
			this.cronExpression = cronExpression;
			return this;
		}

	}

}
