package org.cmdbuild.task.syncevent;

import static java.lang.Boolean.FALSE;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.google.common.collect.Maps;
import org.cmdbuild.logic.taskmanager.SynchronousEventTask;

public class SynchronousEventTaskImpl implements SynchronousEventTask {

	private final Long id;
	private final String description;
	private final boolean active;
	private final Phase phase;
	private final Iterable<String> groups;
	private final String classname;
	private final String filter;
	private final boolean emailEnabled;
	private final String emailAccount;
	private final String emailTemplate;
	private final boolean workflowEnabled;
	private final String workflowClassName;
	private final Map<String, String> workflowAttributes;
	private final boolean workflowAdvanceable;
	private final boolean scriptingEnabled;
	private final String scriptingEngine;
	private final String scriptingScript;
	private final boolean scriptingSafe;

	private SynchronousEventTaskImpl(Builder builder) {
		this.id = builder.id;
		this.description = builder.description;
		this.active = builder.active;
		this.phase = builder.phase;
		this.groups = builder.groups;
		this.classname = builder.classname;
		this.filter = builder.filter;
		this.emailEnabled = builder.emailEnabled;
		this.emailAccount = builder.emailAccount;
		this.emailTemplate = builder.emailTemplate;
		this.workflowEnabled = builder.workflowEnabled;
		this.workflowClassName = builder.workflowClassName;
		this.workflowAttributes = builder.workflowAttributes;
		this.workflowAdvanceable = builder.workflowAdvanceable;
		this.scriptingEnabled = builder.scriptingEnabled;
		this.scriptingEngine = builder.scriptingEngine;
		this.scriptingScript = builder.scriptingScript;
		this.scriptingSafe = builder.scriptingSafe;
	}

	@Override
	public TaskType getType() {
		return TaskType.SYNC_EVENT;
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
		return false;
	}

	@Override
	public Phase getPhase() {
		return phase;
	}

	@Override
	public Iterable<String> getGroups() {
		return groups;
	}

	@Override
	public String getTargetClassname() {
		return classname;
	}

	@Override
	public String getFilter() {
		return filter;
	}

	@Override
	public boolean isEmailEnabled() {
		return emailEnabled;
	}

	@Override
	public String getEmailAccount() {
		return emailAccount;
	}

	@Override
	public String getEmailTemplate() {
		return emailTemplate;
	}

	@Override
	public boolean isWorkflowEnabled() {
		return workflowEnabled;
	}

	@Override
	public String getWorkflowClassName() {
		return workflowClassName;
	}

	@Override
	public Map<String, String> getWorkflowAttributes() {
		return workflowAttributes;
	}

	@Override
	public boolean isWorkflowAdvanceable() {
		return workflowAdvanceable;
	}

	@Override
	public boolean isScriptingEnabled() {
		return scriptingEnabled;
	}

	@Override
	public String getScriptingEngine() {
		return scriptingEngine;
	}

	@Override
	public String getScriptingScript() {
		return scriptingScript;
	}

	@Override
	public boolean isScriptingSafe() {
		return scriptingSafe;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	public static class Builder implements org.apache.commons.lang3.builder.Builder<SynchronousEventTaskImpl> {

		private static final Iterable<String> EMPTY_GROUPS = Collections.emptyList();
		private static final Map<String, String> EMPTY_ATTRIBUTES = Collections.emptyMap();

		private Long id;
		private String description;
		private Boolean active;
		private Phase phase;
		private Iterable<String> groups;
		private String classname;
		private String filter;
		private Boolean emailEnabled;
		private String emailAccount;
		private String emailTemplate;
		private Boolean workflowEnabled;
		private String workflowClassName;
		private final Map<String, String> workflowAttributes = Maps.newHashMap();
		private Boolean workflowAdvanceable;
		private Boolean scriptingEnabled;
		private String scriptingEngine;
		private String scriptingScript;
		private Boolean scriptingSafe;

		private Builder() {
			// use factory method
		}

		@Override
		public SynchronousEventTaskImpl build() {
			validate();
			return new SynchronousEventTaskImpl(this);
		}

		private void validate() {
			active = defaultIfNull(active, FALSE);

			groups = defaultIfNull(groups, EMPTY_GROUPS);

			emailEnabled = defaultIfNull(emailEnabled, FALSE);

			workflowEnabled = defaultIfNull(workflowEnabled, FALSE);
			workflowAdvanceable = defaultIfNull(workflowAdvanceable, FALSE);

			scriptingEnabled = defaultIfNull(scriptingEnabled, FALSE);
			scriptingSafe = defaultIfNull(scriptingSafe, FALSE);
		}

		public Builder withId(Long id) {
			this.id = id;
			return this;
		}

		public Builder withDescription(String description) {
			this.description = description;
			return this;
		}

		public Builder withActiveStatus(boolean active) {
			this.active = active;
			return this;
		}

		public Builder withPhase(Phase phase) {
			this.phase = phase;
			return this;
		}

		public Builder withGroups(Iterable<String> groups) {
			this.groups = groups;
			return this;
		}

		public Builder withTargetClass(String classname) {
			this.classname = classname;
			return this;
		}

		public Builder withFilter(String filter) {
			this.filter = filter;
			return this;
		}

		public Builder withEmailEnabled(boolean enabled) {
			this.emailEnabled = enabled;
			return this;
		}

		public Builder withEmailAccount(String account) {
			this.emailAccount = account;
			return this;
		}

		public Builder withEmailTemplate(String template) {
			this.emailTemplate = template;
			return this;
		}

		public Builder withWorkflowEnabled(boolean enabled) {
			this.workflowEnabled = enabled;
			return this;
		}

		public Builder withWorkflowClassName(String className) {
			this.workflowClassName = className;
			return this;
		}

		public Builder withWorkflowAttributes(Map<String, String> attributes) {
			this.workflowAttributes.putAll(defaultIfNull(attributes, EMPTY_ATTRIBUTES));
			return this;
		}

		public Builder withWorkflowAdvanceable(boolean advanceable) {
			this.workflowAdvanceable = advanceable;
			return this;
		}

		public Builder withScriptingEnableStatus(boolean scriptingEnabled) {
			this.scriptingEnabled = scriptingEnabled;
			return this;
		}

		public Builder withScriptingEngine(String scriptingEngine) {
			this.scriptingEngine = scriptingEngine;
			return this;
		}

		public Builder withScript(String scriptingScript) {
			this.scriptingScript = scriptingScript;
			return this;
		}

		public Builder withScriptingSafeStatus(boolean scriptingSafe) {
			this.scriptingSafe = scriptingSafe;
			return this;
		}

	}

	public static Builder newInstance() {
		return new Builder();
	}
}
