/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.shark;

import static com.google.common.base.Objects.equal;
import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.Lists.newArrayList;
import com.google.common.eventbus.Subscribe;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SharkEventCollector {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final List<SharkEvent> collectedEvents = newArrayList();

	private final int sessionId;

	public SharkEventCollector(int sessionId) {
		this.sessionId = sessionId;
	}

	@Subscribe
	public void handleWorkflowEventReceived(SharkEventService.SharkEventReceived event) {
		if (event.getSessionId() == sessionId) {
			/*
				 * start events must not be overridden by updates, and they
				 * always come first!
			 */
			if (collectedEvents.stream().anyMatch((collectedEvent) -> equal(collectedEvent.getWalkId(), event.getEvent().getWalkId()))) {
				logger.info("discarding event = {}, we already have a 'start event' for this processInstanceId = {}", event, event.getEvent().getWalkId());
			} else {
				collectedEvents.add(event.getEvent());
			}
		}
	}

	public List<SharkEvent> getAndRemoveCollectedEvents() {
		List<SharkEvent> list = copyOf(collectedEvents);
		collectedEvents.clear();
		return list;
	}

}
