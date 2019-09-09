/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.task.cardeventprocessing;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;
import static org.cmdbuild.common.error.ErrorAndWarningCollectorService.marker;
import org.cmdbuild.event.CardEvent;
import org.cmdbuild.event.CardEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CardEventServiceImpl implements CardEventService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final EventBus eventBus = new EventBus(new LoggingSubscriberExceptionHandler());//TODO handle errors

	@Override
	public EventBus getEventBus() {
		return eventBus;
	}

	@Override
	public void post(CardEvent event) {
		eventBus.post(event);
	}

	private class LoggingSubscriberExceptionHandler implements SubscriberExceptionHandler {

		@Override
		public void handleException(Throwable exception, SubscriberExceptionContext context) {
//			logger.error(marker(), "error processing card update event within observer = " + ((DefaultObserverCollector.CardEventObserverEventBusAdapter) context.getSubscriber()).inner, exception);
			logger.error(marker(), "error processing card update event within observer = " + context.getSubscriber(), exception);
		}
	}
}
