/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.common.error;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;
import static org.cmdbuild.common.error.ErrorAndWarningCollectorService.marker;
import org.slf4j.Logger;

public class EventBusUtils {

	public static SubscriberExceptionHandler logExceptions(Logger logger) {
		return new MySubscriberExceptionHandler(logger);
	}

	private final static class MySubscriberExceptionHandler implements SubscriberExceptionHandler {

		private final Logger logger;

		public MySubscriberExceptionHandler(Logger logger) {
			this.logger = checkNotNull(logger);
		}

		@Override
		public void handleException(Throwable exception, SubscriberExceptionContext context) {
			logger.error(marker(), "error processing event handlers for event = {}, subscriber = {}", context.getEvent(), context.getSubscriberMethod(), exception);
		}

	}
}
