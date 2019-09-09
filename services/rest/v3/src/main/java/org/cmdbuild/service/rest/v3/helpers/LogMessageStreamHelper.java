/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v3.helpers;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.eventbus.Subscribe;
import java.util.Map;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.event.EventService;
import org.cmdbuild.event.SessionClosedEvent;
import org.cmdbuild.event.WebsocketService;
import org.cmdbuild.log.LogService;
import org.cmdbuild.log.LogService.LogEvent;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LogMessageStreamHelper {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final Cache<String, MyEventListener> eventListenersBySessionId = CacheBuilder.newBuilder().build();//TODO cleanup

	private final SessionService sessionService;
	private final LogService logService;
	private final EventService eventService;
	private final WebsocketService websocketService;

	public LogMessageStreamHelper(SessionService sessionService, LogService logService, EventService eventService, WebsocketService websocketService) {
		this.sessionService = checkNotNull(sessionService);
		this.logService = checkNotNull(logService);
		this.eventService = checkNotNull(eventService);
		this.websocketService = checkNotNull(websocketService);
	}

	public void startReceivingLogMessages() {
		stopReceivingLogMessages();//cleanup of other listeners for this session
		String sessionId = sessionService.getCurrentSession().getSessionId();
		checkArgument(websocketService.isConnected(sessionId), "current session is not connected to socket service: unable to start log message streaming");
		MyEventListener listener = new MyEventListener(sessionId);
		eventListenersBySessionId.put(sessionId, listener);
		eventService.getEventBus().register(listener);
		logService.getEventBus().register(listener);
		logger.info("session = {} is receiving log messages", sessionId);
	}

	public void stopReceivingLogMessages() {
		String sessionId = sessionService.getCurrentSession().getSessionId();
		MyEventListener listener = eventListenersBySessionId.getIfPresent(sessionId);
		if (listener != null) {
			listener.close();
		}
	}

	private class MyEventListener {

		private final String sessionId;

		public MyEventListener(String sessionId) {
			this.sessionId = checkNotBlank(sessionId);
		}

		@Subscribe
		public void handleSessionClosedEvent(SessionClosedEvent event) {
			close();
		}

		@Subscribe
		public void handleLogEvent(LogEvent event) {
			eventService.sendEventMessage(sessionId, "log.message", (Map) map(
					"level", event.getLevel().name().toLowerCase().replaceFirst("ll_", ""),
					"timestamp", toIsoDateTime(event.getTimestamp()),
					"message", event.getMessage(),
					"line", event.getLine()
			).accept(m -> {
				if (event.hasException()) {
					m.put("stacktrace", ExceptionUtils.getStackTrace(event.getException()));
				}
			}));
		}

		public void close() {
			logger.debug("closing log message event helper for session = {}", sessionId);
			eventService.getEventBus().unregister(this);
			logService.getEventBus().unregister(this);
			eventListenersBySessionId.invalidate(sessionId);
		}

	}

}
