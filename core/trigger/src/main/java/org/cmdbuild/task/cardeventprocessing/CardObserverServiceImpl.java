package org.cmdbuild.task.cardeventprocessing;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;

import org.slf4j.Logger;

import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.event.CardEventService;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@Component
public class CardObserverServiceImpl implements CardObserverService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final Map<String, Object> elementsByIdentifier = new ConcurrentHashMap<>();
	private final CardEventService eventService;

	public CardObserverServiceImpl(CardEventService eventService) {
		this.eventService = checkNotNull(eventService);
	}

	@Override
	public void add(String id, Object listener) {
		logger.info("add card event observer = {} {}", id, listener);
		elementsByIdentifier.put(checkNotBlank(id), listener);
		eventService.getEventBus().register(listener);
	}

	@Override
	public void remove(String id) {
		logger.info("remove card event observer = {}", id);
		Object listener = checkNotNull(elementsByIdentifier.remove(checkNotBlank(id)));
		eventService.getEventBus().unregister(listener);
	}
//
//	private class CardEventObserverEventBusAdapter {
//
//		private final CardEventObserver inner;
//
//		public CardEventObserverEventBusAdapter(CardEventObserver inner) {
//			this.inner = checkNotNull(inner);
//		}
//
//		@Subscribe
//		public void handleAfterCreateEvent(AfterCardCreateEvent event) {
//			inner.afterCreate(event.getCurrentCard());
//		}
//
//		@Subscribe
//		public void handleBeforeUpdateEvent(BeforeCardUpdateEvent event) {
//			inner.beforeUpdate(event.getCurrentCard(), event.getNextCard());
//		}
//
//		@Subscribe
//		public void handleAfterUpdateEvent(AfterCardUpdateEvent event) {
//			inner.afterUpdate(event.getPreviousCard(), event.getCurrentCard());
//		}
//
//		@Subscribe
//		public void handleBeforeDeleteEvent(BeforeCardDeleteEvent event) {
//			inner.beforeDelete(event.getCurrentCard());
//		}
//
//		@Override
//		public String toString() {
//			return "CardEventObserverEventBusAdapter{" + "observer=" + inner + '}';
//		}
//
//	}
}
