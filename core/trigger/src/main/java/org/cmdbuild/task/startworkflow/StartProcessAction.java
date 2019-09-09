package org.cmdbuild.task.startworkflow;

import org.cmdbuild.workflow.WorkflowService;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.transformValues;
import static java.util.Collections.emptyMap;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.Validate;
import org.cmdbuild.workflow.model.WorkflowException;

import com.google.common.base.Function;
import static com.google.common.collect.Iterables.getOnlyElement;
import org.cmdbuild.easytemplate.DummyEasytemplateProcessor;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.easytemplate.EasytemplateProcessor;
import org.cmdbuild.workflow.model.Task;
import org.cmdbuild.workflow.model.Flow;

public class StartProcessAction {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final WorkflowService workflowLogic;
	private final StartProcessActionCallback callback;
	private final EasytemplateProcessor templateResolver;
	private final String className;
	private final Map<String, Object> attributes;
	private final boolean advance;

	private StartProcessAction(Builder builder) {
		this.workflowLogic = builder.workflowLogic;
		this.callback = builder.callback;
		this.templateResolver = builder.templateResolver;
		this.className = builder.className;
		this.attributes = builder.attributes;
		this.advance = builder.advance;
	}

	public void execute() {
		try {
			Flow created = workflowLogic.startProcess(className, newHashMap(transformValues(attributes, APPLY_TEMPLATE_RESOLVER)), false).getFlowCard();
			callback.created(created);
			if (advance) {
				Task task = getOnlyElement(workflowLogic.getTaskList(created));
				Flow advanced = workflowLogic.updateProcess(className, created.getCardId(), task.getId(), emptyMap(), true).getFlowCard();
				callback.advanced(advanced);
			}
		} catch (WorkflowException e) {
			logger.error("error starting process", e);
			throw new RuntimeException(e);
		}
	}

	public static Builder newInstance() {
		return new Builder();
	}

	private final Function<Object, Object> APPLY_TEMPLATE_RESOLVER = new Function<Object, Object>() {

		@Override
		public Object apply(Object input) {
			Object resolved;
			if (input instanceof String) {
				String template = String.class.cast(input);
				resolved = templateResolver.processExpression(template);
			} else {
				resolved = input;
			}
			return resolved;
		}

	};

	public static interface StartProcessActionCallback {

		void created(Flow userProcessInstance);

		void advanced(Flow userProcessInstance);

	}

	private static final StartProcessActionCallback NOP_HOOK = new StartProcessActionCallback() {

		@Override
		public void created(final Flow userProcessInstance) {
			// nothing to do
		}

		@Override
		public void advanced(final Flow userProcessInstance) {
			// nothing to do
		}

	};

	public static class Builder implements org.apache.commons.lang3.builder.Builder<StartProcessAction> {

		private static final Map<String, Object> EMPTY_ATTRIBUTES = emptyMap();

		private WorkflowService workflowLogic;
		private StartProcessActionCallback callback;
		private EasytemplateProcessor templateResolver;
		private String className;
		private final Map<String, Object> attributes = map();
		private Boolean advance;

		private Builder() {
		}

		@Override
		public StartProcessAction build() {
			validate();
			return new StartProcessAction(this);
		}

		private void validate() {
			Validate.notNull(workflowLogic, "missing workflow logic");
			Validate.notBlank(className, "missing class name");
			callback = defaultIfNull(callback, NOP_HOOK);
			templateResolver = defaultIfNull(templateResolver, DummyEasytemplateProcessor.getInstance());
			advance = defaultIfNull(advance, Boolean.TRUE);
		}

		public Builder withWorkflowLogic(WorkflowService workflowLogic) {
			this.workflowLogic = workflowLogic;
			return this;
		}

		public Builder withHook(StartProcessActionCallback hook) {
			this.callback = hook;
			return this;
		}

		public Builder withTemplateResolver(EasytemplateProcessor templateResolver) {
			this.templateResolver = templateResolver;
			return this;
		}

		public Builder withClassName(String classname) {
			this.className = classname;
			return this;
		}

		public Builder withAttribute(String name, Object value) {
			if (isNotBlank(name)) {
				attributes.put(name, value);
			}
			return this;
		}

		public Builder withAttributes(Map<String, ?> attributes) {
			for (Entry<String, ?> entry : defaultIfNull(attributes, EMPTY_ATTRIBUTES).entrySet()) {
				withAttribute(entry.getKey(), entry.getValue());
			}
			return this;
		}

		public Builder withAdvanceStatus(boolean advance) {
			this.advance = advance;
			return this;
		}

	}
}
