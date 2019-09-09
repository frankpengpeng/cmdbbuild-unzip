package org.cmdbuild.workflow;

import static com.google.common.reflect.Reflection.newProxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.cmdbuild.services.soap.client.beans.AbstractWorkflowEvent;
import org.cmdbuild.services.soap.client.beans.Private;
import org.cmdbuild.services.soap.client.beans.ProcessStartEvent;
import org.cmdbuild.services.soap.client.beans.ProcessUpdateEvent;
import org.cmdbuild.workflow.api.MonostateSelfSuspensionRequestHolder;
import org.enhydra.shark.Shark;
import org.enhydra.shark.api.client.wfmc.wapi.WMAttribute;
import org.enhydra.shark.api.internal.working.CallbackUtilities;

import com.google.common.base.Optional;
import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.reflect.AbstractInvocationHandler;
import static java.lang.String.format;
import static org.cmdbuild.workflow.model.WorkflowConstants.PROCESS_CARD_ID_VARIABLE;
import static org.cmdbuild.workflow.model.WorkflowConstants.PROCESS_CLASSNAME_VARIABLE;
import static org.enhydra.shark.api.client.wfmc.wapi.WMProcessInstanceState.OPEN_NOTRUNNING_SUSPENDED;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.workflow.api.CmdbuildClientConfig;

/**
 * DO NOT RENAME: referenced in shark.conf as
 * org.cmdbuild.workflow.CMDBuildEventAuditManager
 */
public class CMDBuildEventAuditManager extends DelegatingEventAuditManager {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void configure(CallbackUtilities cus) throws Exception {
		super.configure(cus);
		Private proxy = newProxy(Private.class, new AbstractInvocationHandler() {

			private final CmdbuildClientConfig configuration = new SharkExtensionsConfigImpl(cus);
			private Supplier<Private> privateSupplier;

			{
				initWsClient();
			}

			private void initWsClient() {
				logger.info("init ws client");
				privateSupplier = Suppliers.memoize(() -> {
					return new CmdbuildClientBuilder(configuration).build();
				});
			}

			/**
			 * Ugly way for determining if there is a problem with the
			 * management of the session.
			 *
			 * This happens when session management is thinked at the end and
			 * not at the beginning and an adeguate rewrite is not allowed.
			 */
			@Override
			protected Object handleInvocation(Object proxy, Method method, Object[] args) throws Throwable {
				try {
					logger.debug("invoke ws client method {}", method.getName());
					return method.invoke(privateSupplier.get(), args);
				} catch (InvocationTargetException e) {
					logger.warn("error", e);
					if (!configuration.isTokenEnabled()) {
						throw e.getCause();
					} else if (wsClientIsGood()) {
						throw e.getCause();
					} else {
						try {
							initWsClient();
							return method.invoke(privateSupplier.get(), args);
						} catch (InvocationTargetException e2) {
							logger.warn("error", e2);
							throw e2.getCause();
						}
					}
				}

			}

			private boolean wsClientIsGood() {
				checkArgument(configuration.isTokenEnabled());
				try {
					privateSupplier.get().getUserInfo();
					return true;
				} catch (Exception ex) {
					logger.debug("token test failed with error", ex);
					return false;
				}
			}

		});
		setEventManager(new WSEventNotifier(proxy, cus));

	}

	protected static class WSEventNotifier implements SimpleEventManager {

		private final Private proxy;
		private final CallbackUtilities cus;

		protected WSEventNotifier(Private proxy, CallbackUtilities cus) {
			this.proxy = proxy;
			this.cus = cus;
		}

		@Override
		public void processStarted(SimpleEventManager.ProcessInstance processInstance) {
			cus.info(null, format("process '%s' started", processInstance.getProcessDefinitionId()));
			sendProcessStartEvent(processInstance);
		}

		@Override
		public void processClosed(SimpleEventManager.ProcessInstance processInstance) {
			cus.info(null, format("process '%s' closed", processInstance.getProcessDefinitionId()));
			sendProcessUpdateEvent(processInstance);
		}

		@Override
		public void processSuspended(SimpleEventManager.ProcessInstance processInstance) {
			cus.info(null, format("process '%s' suspended", processInstance.getProcessDefinitionId()));
			sendProcessUpdateEvent(processInstance);
		}

		@Override
		public void processResumed(SimpleEventManager.ProcessInstance processInstance) {
			cus.info(null, format("process '%s' resumed", processInstance.getProcessDefinitionId()));
			sendProcessUpdateEvent(processInstance);
		}

		@Override
		public void activityStarted(SimpleEventManager.ActivityInstance activityInstance) {
			cus.info(null, format("activity '%s' started", activityInstance.getActivityDefinitionId()));
			sendProcessUpdateEventIfNoImpl(activityInstance);
		}

		@Override
		public void activityClosed(SimpleEventManager.ActivityInstance activityInstance) {
			cus.info(null, format("activity '%s' closed", activityInstance.getActivityDefinitionId()));
			sendProcessUpdateEventIfNoImpl(activityInstance);
		}

		private void sendProcessUpdateEventIfNoImpl(SimpleEventManager.ActivityInstance activityInstance) {
			if (activityInstance.isNoImplementationActivity()) {
				cus.info(null, format("sending notification for activity '%s'", activityInstance.getActivityDefinitionId()));
				sendProcessUpdateEvent(activityInstance);

				Optional<String> processClass = processClass(activityInstance);
				Optional<Long> processId = processId(activityInstance);
				if (processClass.isPresent() && processId.isPresent()) {
					if (new MonostateSelfSuspensionRequestHolder().remove(processId.get())) {
						try {
							/*
							 * Calling CMDBuild API for suspend current process
							 * will result in an error because process's state
							 * is not "stable" at the moment. So that we must
							 * call Shark API.
							 */
							Shark.getInstance().getWAPIConnection().changeProcessInstanceState(
									activityInstance.getSessionHandle(), activityInstance.getProcessInstanceId(),
									OPEN_NOTRUNNING_SUSPENDED);
						} catch (Exception e) {
							cus.error(activityInstance.getSessionHandle(), format("cannot suspend the current process: %s", activityInstance.getProcessInstanceId()), e);
						}
					}
				}
			}
		}

		private void sendProcessUpdateEvent(SimpleEventManager.ProcessInstance processInstance) {
			cus.info(null, format("sending notification for update of process '%s'", processInstance.getProcessDefinitionId()));
			AbstractWorkflowEvent event = new ProcessUpdateEvent();
			fillEventProperties(processInstance, event);
			proxy.notify(event);
		}

		private void sendProcessStartEvent(SimpleEventManager.ProcessInstance processInstance) {
			cus.info(null, format("sending notification for start of process '%s'", processInstance.getProcessDefinitionId()));
			AbstractWorkflowEvent event = new ProcessStartEvent();
			fillEventProperties(processInstance, event);
			proxy.notify(event);
		}

		private void fillEventProperties(SimpleEventManager.ProcessInstance processInstance, AbstractWorkflowEvent workflowEvent) {
			int sessionId = processInstance.getSessionHandle().getId();
			workflowEvent.setSessionId(sessionId);
			workflowEvent.setProcessDefinitionId(processInstance.getProcessDefinitionId());
			workflowEvent.setProcessInstanceId(processInstance.getProcessInstanceId());
		}

		private Optional<String> processClass(SimpleEventManager.ProcessInstance processInstance) {
			try {
				WMAttribute attribute = Shark.getInstance().getWAPIConnection().getProcessInstanceAttributeValue(processInstance.getSessionHandle(), processInstance.getProcessInstanceId(), PROCESS_CLASSNAME_VARIABLE);
				Object value = attribute.getValue();
				return Optional.of(String.class.cast(value));
			} catch (Throwable e) {
				return Optional.absent();
			}
		}

		private Optional<Long> processId(SimpleEventManager.ProcessInstance processInstance) {
			try {
				WMAttribute attribute = Shark.getInstance().getWAPIConnection().getProcessInstanceAttributeValue(processInstance.getSessionHandle(), processInstance.getProcessInstanceId(), PROCESS_CARD_ID_VARIABLE);
				Object value = attribute.getValue();
				return Optional.of(Number.class.cast(value).longValue());
			} catch (Throwable e) {
				return Optional.absent();
			}
		}

	}
}
