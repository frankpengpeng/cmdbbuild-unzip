package org.cmdbuild.task.asyncevent;

import static java.lang.Boolean.FALSE;
import static java.util.Collections.emptyMap;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import java.util.Map;
import org.cmdbuild.logic.taskmanager.ScheduledTask;
import org.cmdbuild.logic.taskmanager.Task.TaskType;
import org.joda.time.DateTime;

public class AsynchronousEventTask implements ScheduledTask {

	private final Long id;
	private final String description;
	private final boolean active;
	private final String cronExpression;
	private final DateTime lastExecution;
	private final String classname;
	private final String filter;
	private final boolean notificationActive;
	private final String notificationAcccount;
	private final String notificationTemplate;
	private final boolean reportActive;
	private final String reportName;
	private final String reportExtension;
	private final Map<String, String> reportParameters;

	private AsynchronousEventTask(Builder builder) {
		this.id = builder.id;
		this.description = builder.description;
		this.active = builder.active;
		this.cronExpression = builder.cronExpression;
		this.lastExecution = builder.lastExecution;
		this.classname = builder.classname;
		this.filter = builder.filter;
		this.notificationActive = builder.notificationActive;
		this.notificationAcccount = builder.notificationAcccount;
		this.notificationTemplate = builder.notificationTemplate;
		this.reportActive = builder.reportActive;
		this.reportName = builder.reportName;
		this.reportExtension = builder.reportExtension;
		this.reportParameters = builder.reportParameters;
	}

	@Override
	public TaskType getType() {
		return TaskType.ASYNC_EVENT;
	}

	public static Builder newInstance() {
		return new Builder();
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public boolean isExecutable() {
		return true;
	}

	@Override
	public String getCronExpression() {
		return cronExpression;
	}

	@Override
	public DateTime getLastExecution() {
		return lastExecution;
	}

	public String getTargetClassname() {
		return classname;
	}

	public String getFilter() {
		return filter;
	}

	public boolean isNotificationActive() {
		return notificationActive;
	}

	public String getNotificationAccount() {
		return notificationAcccount;
	}

	public String getNotificationTemplate() {
		return notificationTemplate;
	}

	public boolean isReportActive() {
		return reportActive;
	}

	public String getReportName() {
		return reportName;
	}

	public String getReportExtension() {
		return reportExtension;
	}

	public Map<String, String> getReportParameters() {
		return reportParameters;
	}

	@Override
	public String toString() {
		return reflectionToString(this, SHORT_PREFIX_STYLE);
	}

	public static class Builder implements org.apache.commons.lang3.builder.Builder<AsynchronousEventTask> {

		private static final Map<String, String> NO_PARAMETERS = emptyMap();

		private Long id;
		private String description;
		private Boolean active;
		private String cronExpression;
		private DateTime lastExecution;
		private String classname;
		private String filter;
		private Boolean notificationActive;
		private String notificationAcccount;
		private String notificationTemplate;
		private Boolean reportActive;
		private String reportName;
		private String reportExtension;
		private Map<String, String> reportParameters;

		private Builder() {
			// use factory method
		}

		@Override
		public AsynchronousEventTask build() {
			validate();
			return new AsynchronousEventTask(this);
		}

		private void validate() {
			active = defaultIfNull(active, FALSE);
			notificationActive = defaultIfNull(notificationActive, FALSE);
			reportActive = defaultIfNull(reportActive, FALSE);
			reportParameters = defaultIfNull(reportParameters, NO_PARAMETERS);
		}

		public Builder withId(final Long id) {
			this.id = id;
			return this;
		}

		public Builder withDescription(final String description) {
			this.description = description;
			return this;
		}

		public Builder withActiveStatus(final boolean active) {
			this.active = active;
			return this;
		}

		public Builder withCronExpression(final String cronExpression) {
			this.cronExpression = cronExpression;
			return this;
		}

		public Builder withLastExecution(final DateTime lastExecution) {
			this.lastExecution = lastExecution;
			return this;
		}

		public Builder withTargetClass(final String classname) {
			this.classname = classname;
			return this;
		}

		public Builder withFilter(final String filter) {
			this.filter = filter;
			return this;
		}

		public Builder withNotificationStatus(final Boolean notificationActive) {
			this.notificationActive = notificationActive;
			return this;
		}

		public Builder withNotificationAccount(final String notificationAcccount) {
			this.notificationAcccount = notificationAcccount;
			return this;
		}

		public Builder withNotificationErrorTemplate(final String notificationTemplate) {
			this.notificationTemplate = notificationTemplate;
			return this;
		}

		public Builder withReportActive(final Boolean reportActive) {
			this.reportActive = reportActive;
			return this;
		}

		public Builder withReportName(final String reportName) {
			this.reportName = reportName;
			return this;
		}

		public Builder withReportExtension(final String reportExtension) {
			this.reportExtension = reportExtension;
			return this;
		}

		public Builder withReportParameters(final Map<String, String> reportParameters) {
			this.reportParameters = reportParameters;
			return this;
		}

	}
}
