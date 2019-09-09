package org.cmdbuild.workflow.shark;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableMap;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.clustering.ClusterMessageImpl;
import org.cmdbuild.clustering.ClusterMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.clustering.ClusterService;

/**
 * Workflow event manager that uses the legacy persistence layer.
 *
 *
 * FIXME: this event manager will store events received, and hold them to be
 * processed later.<br>
 * The call sequence is defined inside {@link SharkWorkflowServiceClient}, for
 * example in the {@link AbstractSharkService.advanceActivityInstance} method:
 *
 * <pre>
 * {@code
 * 	public void advanceActivityInstance(final String procInstId, final String actInstId) throws CMWorkflowException {
 *		final WMSessionHandle handle = handle();
 *		try {
 *			wapi().changeActivityInstanceState(handle, procInstId, actInstId, WMActivityInstanceState.OPEN_RUNNING);
 *		} catch (final Exception e) {
 *			// Ignore: it might be open-running already...
 *		}
 *		try {
 *			wapi().changeActivityInstanceState(handle, procInstId, actInstId, WMActivityInstanceState.CLOSED_COMPLETED);
 *			updateOperationListener.activityInstanceAdvanced(handle.getId());
 *		} catch (final Exception e) {
 *			updateOperationListener.abortedOperation(handle.getId());
 *			throw new CMWorkflowException(e);
 *		}
 *	}
 * }
 * </pre>
 *
 * <ol>
 * <li>wapi().changeActivityInstanceState(): this will generate a call towards
 * shark service, which in turn will send back workflow events that will be
 * received by this {@link SharkEventServiceImpl};</li>
 * <li>updateOperationListener.activityInstanceAdvanced(handle.getId()): this
 * will process the events received, and synchronize data between shark and
 * cmdbuild</li>
 * </ol>
 *
 * Proposed refactoring: the
 * {@link AbstractSharkService.advanceActivityInstance} and other similar
 * methods should be modified to work as like this:
 *
 * <ol>
 * <li>register a listener for the process events, in
 * {@link SharkEventServiceImpl}</li>
 * <li>call wpapi, do work; meanwhile the listener will collect events for the
 * workflow</li>
 * <li>un-register the listener; process events collected by the listener
 * (already filtered for the session</li>
 * </ol>
 *
 * {@link SharkEventServiceImpl} will have to be modified to handle
 * registering/unregistering of session-scoped event listeners/collectors at
 * runtime; all events not collected will be discarded and will not clutter the
 * memory.<br>
 * With this change it will be absolutely safe to forward events to all cluster
 * nodes, since all unexpected events will be discarded, unless the application
 * has explicitely registered a listener for them.
 *
 */
@Component
public class SharkEventServiceImpl implements SharkEventService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final ClusterService clusteringService;

	private final static String WORKFLOW_CLUSTER_MESSAGE_TYPE = "org.cmdbuild.workflow.WorkflowEvent";

	private final EventBus eventBus = new EventBus();

	public SharkEventServiceImpl(ClusterService clusteringService) {
		this.clusteringService = checkNotNull(clusteringService);
		clusteringService.getEventBus().register(new Object() {
			@Subscribe
			public void handleClusterMessageReceivedEvent(ClusterMessageReceivedEvent event) {
				if (event.isOfType(WORKFLOW_CLUSTER_MESSAGE_TYPE)) {
					Pair<Integer, SharkEvent> pair = deserializeWorkflowEvent(event.getClusterMessage().getMessageData());
					logger.info("received event from cluster, sessionId = {} event = {}", pair.getLeft(), pair.getRight());
					pushEvent(pair.getLeft(), pair.getRight(), false);
				}
			}
		});
	}

	@Override
	public EventBus getEventBus() {
		return eventBus;
	}

	private void pushEventOnCluster(int sessionId, SharkEvent event) {
		logger.info("pushEventOnCluster sessionId = {} event = {}", sessionId, event);
		clusteringService.sendMessage(ClusterMessageImpl.builder()
				.withMessageType(WORKFLOW_CLUSTER_MESSAGE_TYPE)
				.withMessageData(serializeWorkflowEvent(sessionId, event))
				.requireRsvp(true)
				.build());
	}

	//TODO move this somewere else (serialization/deserialization)
	private Map<String, Object> serializeWorkflowEvent(int sessionId, SharkEvent event) {
		return ImmutableMap.of(
				"sessionId", String.valueOf(sessionId),
				"type", event.getType().name(),
				"processDefinitionId", event.getPlanId(),
				"processInstanceId", event.getWalkId()
		);
	}

	//TODO move this somewere else (serialization/deserialization)
	private Pair<Integer, SharkEvent> deserializeWorkflowEvent(Map<String, Object> map) {
		return Pair.of(Integer.valueOf((String) map.get("sessionId")), new SharkEvent(SharkEvent.Type.valueOf((String) map.get("type")), (String) map.get("processDefinitionId"), (String) map.get("processInstanceId")));
	}

	@Override
	public synchronized void pushEvent(int sessionId, SharkEvent event) {
		pushEvent(sessionId, event, true);
	}

	private void pushEvent(int sessionId, SharkEvent event, boolean propagateOnCluster) {
		logger.info("pushing event '{}' for session '{}'", ToStringBuilder.reflectionToString(event, ToStringStyle.SHORT_PREFIX_STYLE), sessionId);
		logger.info("pushing event = {} sessionId = {}", event, sessionId);
		if (propagateOnCluster) {
			pushEventOnCluster(sessionId, event);
		}
		SharkEventReceived workflowEventReceived = new SimpleWorkflowEventReceived(sessionId, event);
		eventBus.post(workflowEventReceived);
	}

	private static class SimpleWorkflowEventReceived implements SharkEventReceived {

		private final int sessionId;
		private final SharkEvent workflowEvent;

		public SimpleWorkflowEventReceived(int sessionId, SharkEvent workflowEvent) {
			this.sessionId = sessionId;
			this.workflowEvent = checkNotNull(workflowEvent);
		}

		@Override
		public int getSessionId() {
			return sessionId;
		}

		@Override
		public SharkEvent getEvent() {
			return workflowEvent;
		}

		@Override
		public String toString() {
			return "SimpleWorkflowEventReceived{" + "sessionId=" + sessionId + ", workflowEvent=" + workflowEvent + '}';
		}

	}

}
