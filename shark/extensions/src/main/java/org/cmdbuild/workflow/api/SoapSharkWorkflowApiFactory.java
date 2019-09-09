package org.cmdbuild.workflow.api;

import static com.google.common.reflect.Reflection.newProxy;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.cmdbuild.common.utils.Reflection.unsupported;
import static org.cmdbuild.workflow.model.WorkflowConstants.CURRENT_GROUP_NAME_VARIABLE;
import static org.cmdbuild.workflow.model.WorkflowConstants.CURRENT_USER_USERNAME_VARIABLE;
import static org.cmdbuild.workflow.model.WorkflowConstants.PROCESS_CARD_ID_VARIABLE;

import org.cmdbuild.api.fluent.ExecutorBasedFluentApi;
import org.cmdbuild.api.fluent.FluentApi;
import org.cmdbuild.api.fluent.FluentApiExecutor;
import org.cmdbuild.api.fluent.ws.WsFluentApiExecutor;
import org.cmdbuild.services.soap.client.beans.Private;
import org.cmdbuild.workflow.ConfigurationHelper;
import org.cmdbuild.workflow.CmdbuildClientBuilder;
import org.enhydra.shark.Shark;
import org.enhydra.shark.api.client.wfmc.wapi.WAPI;
import org.enhydra.shark.api.client.wfmc.wapi.WMAttribute;
import org.enhydra.shark.api.client.wfmc.wapi.WMSessionHandle;
import org.enhydra.shark.api.internal.working.CallbackUtilities;

import com.google.common.base.Optional;
import org.cmdbuild.api.fluent.MailApi;
import org.cmdbuild.workflow.SharkExtensionsConfigImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * DO NOT RENAME: referenced in shark.conf as
 * org.cmdbuild.workflow.api.SoapSharkWorkflowApiFactory
 *
 */
public class SoapSharkWorkflowApiFactory implements SharkWorkflowApiFactory {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private CallbackUtilities callbackUtilities;
	private ProcessData processData;

	@Override
	public void setup(CallbackUtilities callbackUtilities) {
		setup(callbackUtilities, null);
	}

	@Override
	public void setup(CallbackUtilities callbackUtilities, WMSessionHandle sessionHandle, String processInstanceId) {
		setup(callbackUtilities, new ProcessData(sessionHandle, processInstanceId));
	}

	private void setup(CallbackUtilities callbackUtilities, ProcessData processData) {
		this.callbackUtilities = callbackUtilities;
		this.processData = processData;
		logger.info("configured soap workflow api factory");
	}

	@Override
	public WorkflowApi createWorkflowApi() {
		logger.info("create workflow api");
		return new WorkflowApiImpl(createWorkflowApiServicesProvider(createDefaultWsClientProxy()));
	}

	private WorkflowApiServicesProvider createWorkflowApiServicesProvider(Private proxy) {
		DelegatingWorkflowApi delegatingWorkflowApi = new DelegatingWorkflowApi();
		SharkSchemaApi schemaApi = new CachedWsSchemaApi(new SoapSchemaApi(proxy), proxy);
		MailApi mailApi = SoapSharkWorkflowApiFactory.this.mailApi();
		SharkWsTypeConverterConfig configuration = new SharkExtensionsConfigImpl(callbackUtilities);
		FluentApiExecutor wsFluentApiExecutor = new WsFluentApiExecutor(proxy, new SharkWsEntryTypeConverter(delegatingWorkflowApi, configuration), new SharkWsRawTypeConverter(delegatingWorkflowApi, configuration));
		SharkFluentApiExecutor sharkFluentApiExecutor = new SharkFluentApiExecutor(wsFluentApiExecutor, currentProcessId(), new MonostateSelfSuspensionRequestHolder());
		FluentApi fluentApi = new ExecutorBasedFluentApi(sharkFluentApiExecutor);
		return new WorkflowApiServicesProvider() {

			@Override
			public FluentApi fluentApi() {
				return fluentApi;
			}

			@Override
			public Private proxy() {
				return proxy;
			}

			@Override
			public SharkSchemaApi schemaApi() {
				return schemaApi;
			}

			@Override
			public MailApi mailApi() {
				return mailApi;
			}

			@Override
			public void callback(WorkflowApiImpl object) {
				delegatingWorkflowApi.setDelegate(object);
			}

			@Override
			public WorkflowApiServicesProvider impersonate(String username, String group) {
				return createWorkflowApiServicesProvider(SoapSharkWorkflowApiFactory.this.createWsClientProxyInpersonatingUserGroup(username, group));
			}

		};
	}

	private Private createDefaultWsClientProxy() {
		return new CmdbuildClientBuilder(new SharkExtensionsConfigImpl(callbackUtilities))
				.withUsername(defaultString(currentUserOrEmptyOnError(), currentUserOrEmptyOnError()))
				.withGroup(defaultString(currentGroupOrEmptyOnError(), currentGroupOrEmptyOnError()))
				.build();
	}

	private Private createWsClientProxyInpersonatingUserGroup(String username, String group) {
		return new CmdbuildClientBuilder(new SharkExtensionsConfigImpl(callbackUtilities))
				.withUsername(defaultString(username, currentUserOrEmptyOnError()))
				.withGroup(defaultString(group, currentGroupOrEmptyOnError()))
				.forciblyImpersonate(true)
				.build();
	}

	private String currentUserOrEmptyOnError() {
		if (processData == null) {
			return EMPTY;
		}
		try {
			WMAttribute attribute = wapi().getProcessInstanceAttributeValue(processData.sessionHandle, processData.processInstanceid, CURRENT_USER_USERNAME_VARIABLE);
			Object value = attribute.getValue();
			return String.class.cast(value);
		} catch (Throwable e) {
			return EMPTY;
		}
	}

	private String currentGroupOrEmptyOnError() {
		if (processData == null) {
			return EMPTY;
		}
		try {
			WMAttribute attribute = wapi().getProcessInstanceAttributeValue(processData.sessionHandle, processData.processInstanceid, CURRENT_GROUP_NAME_VARIABLE);
			Object value = attribute.getValue();
			return String.class.cast(value);
		} catch (Throwable e) {
			return EMPTY;
		}
	}

	private Optional<Long> currentProcessId() {
		if (processData == null) {
			return Optional.absent();
		}
		try {
			WMAttribute attribute = wapi().getProcessInstanceAttributeValue(processData.sessionHandle, processData.processInstanceid, PROCESS_CARD_ID_VARIABLE);
			Object value = attribute.getValue();
			return Optional.of(Number.class.cast(value).longValue());
		} catch (Throwable e) {
			return Optional.absent();
		}
	}

	private WAPI wapi() throws Exception {
		return Shark.getInstance().getWAPIConnection();
	}

	private static final MailApi NULL_MAIL_API = newProxy(MailApi.class, unsupported("should not be used"));

	private MailApi mailApi() {
//		try {
//			ConfigurationHelper helper = new ConfigurationHelper(callbackUtilities);
//			Configuration.All mailApiConfiguration = helper.getMailApiConfiguration();
//			MailApiFactory mailApiFactory = helper.getMailApiFactory();
//			return mailApiFactory.create(mailApiConfiguration);
//			return helper.getMailApiFactory();
//			return helper.getMailApiFactory();
//		} catch (Exception e) {
		return NULL_MAIL_API;
//		}
	}

	private static class ProcessData {

		private final WMSessionHandle sessionHandle;
		private final String processInstanceid;

		public ProcessData(WMSessionHandle shandle, String procInstId) {
			this.sessionHandle = shandle;
			this.processInstanceid = procInstId;
		}

	}

	private static class DelegatingWorkflowApi extends ForwardingWorkflowApi {

		private static final WorkflowApi UNSUPPORTED = newProxy(WorkflowApi.class, unsupported("delegate not setted"));

		private WorkflowApi delegate = UNSUPPORTED;

		@Override
		protected WorkflowApi delegate() {
			return delegate;
		}

		private void setDelegate(WorkflowApi delegate) {
			this.delegate = delegate;
		}

	}

}
