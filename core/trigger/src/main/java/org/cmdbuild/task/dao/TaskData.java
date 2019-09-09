package org.cmdbuild.task.dao;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.cmdbuild.data.store.Storable;
import org.joda.time.DateTime;

import com.google.common.collect.Maps;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class TaskData implements Storable {

	private final Long id;
	private final String description;
	private final boolean running;
	private final DateTime lastExecution;
	private final String cronExpression;
	private final Map<String, String> parameters;
	private final String taskType;

	private TaskData(TaskDataBuilder builder) {
		this.id = builder.id;
		this.description = builder.description;
		this.running = builder.running;
		this.cronExpression = builder.cronExpression;
		this.lastExecution = builder.lastExecution;
		this.parameters = builder.parameters;
		this.taskType = checkNotBlank(builder.taskType);
	}

	public static TaskDataBuilder builder() {
		return new TaskDataBuilder();
	}

	public TaskDataBuilder copyOf() {
		return builder() //
				.withId(id) //
				.withDescription(description) //
				.withRunningStatus(running) //
				.withLastExecution(lastExecution) //
				.withCronExpression(cronExpression) //
				.withParameters(parameters);
	}

	public String getTaskType() {
		return taskType;
	}

	@Override
	public String getIdentifier() {
		return id.toString();
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

	// TODO move to parameters
	public String getCronExpression() {
		return cronExpression;
	}

	// TODO move some where else
	public DateTime getLastExecution() {
		return lastExecution;
	}

	// TODO use something different from Map
	public Map<String, String> getParameters() {
		return parameters;
	}

	public String getParameter(final String key) {
		return parameters.get(key);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof TaskData)) {
			return false;
		}
		final TaskData other = TaskData.class.cast(obj);
		return new EqualsBuilder() //
				.append(id, other.id) //
				.append(description, other.description) //
				.append(running, other.running) //
				.append(cronExpression, other.cronExpression) //
				.append(parameters, other.parameters) //
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder() //
				.append(id) //
				.append(description) //
				.append(running) //
				.append(cronExpression) //
				.append(parameters) //
				.toHashCode();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, SHORT_PREFIX_STYLE);
	}

	public static class TaskDataBuilder implements org.apache.commons.lang3.builder.Builder<TaskData> {

		private static final Map<String, String> NO_PARAMETERS = Collections.emptyMap();

		private Long id;
		private String description;
		private Boolean running;
		private String cronExpression, taskType;
		private DateTime lastExecution;
		private final Map<String, String> parameters = Maps.newHashMap();

		protected TaskDataBuilder() {
			// usable by subclasses only
		}

		@Override
		public TaskData build() {
			validate();
			return new TaskData(this);
		}

		private void validate() {
			running = (running == null) ? Boolean.FALSE : running;
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
		}

		public TaskDataBuilder withId(final Long id) {
			this.id = id;
			return this;
		}

		public TaskDataBuilder withDescription(final String description) {
			this.description = description;
			return this;
		}

		public TaskDataBuilder withTaskType(String taskType) {
			this.taskType = taskType;
			return this;
		}

		public TaskDataBuilder withRunningStatus(final Boolean running) {
			this.running = running;
			return this;
		}

		public TaskDataBuilder withCronExpression(final String cronExpression) {
			this.cronExpression = cronExpression;
			return this;
		}

		public TaskDataBuilder withLastExecution(final DateTime lastExecution) {
			this.lastExecution = lastExecution;
			return this;
		}

		public TaskDataBuilder withParameters(final Map<String, ? extends String> parameters) {
			this.parameters.putAll(defaultIfNull(parameters, NO_PARAMETERS));
			return this;
		}

		public TaskDataBuilder withParameter(final String key, final String value) {
			this.parameters.put(key, value);
			return this;
		}

	}
}
