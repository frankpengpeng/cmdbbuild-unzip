/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.webapp;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.PongMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.auth.session.SessionService;
import static org.cmdbuild.event.RawEvent.EVENT_CODE_ACTION;
import static org.cmdbuild.event.RawEvent.EventDirection.ED_INCOMING;
import static org.cmdbuild.event.RawEvent.EventDirection.ED_OUTGOING;
import org.cmdbuild.event.RawEventImpl;
import org.cmdbuild.event.EventService;
import org.cmdbuild.event.NewSessionConnectedEvent;
import static org.cmdbuild.spring.SpringIntegrationUtils.applicationContext;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_OBJECTS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.event.RawEvent;
import org.cmdbuild.event.SessionClosedEvent;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@ServerEndpoint(value = "/services/websocket/v1/main")
public class WebsocketEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final Cache<String, SessionHandler> WEBSOCKET_CLIENTS_AUTH_TOKEN_BY_SOCKET_SESSION_ID = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build();

    private final EventService eventService;
    private final SessionService sessionService;

    public WebsocketEndpoint() {
        eventService = applicationContext().getBean(EventService.class);//TODO fix this, autowire spring config
        sessionService = applicationContext().getBean(SessionService.class);//TODO fix this, autowire spring config 
        logger.info("ready");
    }

    @OnOpen
    public void onOpen(Session session) {
        try {
            logger.debug("session opened = {}", session.getId());
            WEBSOCKET_CLIENTS_AUTH_TOKEN_BY_SOCKET_SESSION_ID.put(session.getId(), new SessionHandler(session));
            logger.debug("active sessions = {}", WEBSOCKET_CLIENTS_AUTH_TOKEN_BY_SOCKET_SESSION_ID.size());
        } catch (Exception ex) {
            logger.warn("error processing open session event", ex);
        }
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        SessionHandler sessionHandler = null;
        try {
            logger.debug("string message received from ui, session = {}, message = {}", session.getId(), abbreviate(message));
            sessionHandler = WEBSOCKET_CLIENTS_AUTH_TOKEN_BY_SOCKET_SESSION_ID.get(session.getId(), () -> new SessionHandler(session));

            Map<String, Object> payload = fromJson(message, MAP_OF_OBJECTS);
            String action = checkNotBlank(toStringOrNull(payload.get("_action")), "missing required '_action' param");
            String cmSessionId = getSessionIdOrNull(session);
            if (isBlank(cmSessionId)) {
                checkArgument(equal(action, "socket.session.login"), "this websocket connection has not completed login sequence; only accepted action is 'socket.session.login'");
                String token = checkNotBlank(toStringOrNull(payload.get("token")), "missing required 'token' param");
                logger.debug("identifying websocket session with token = {}", token);
                org.cmdbuild.auth.session.model.Session cmSession = sessionService.getSessionById(token);
//				checkArgument(cmSession.isValid(), "invalid session for session id = %s", token);
                cmSessionId = cmSession.getSessionId();
                sessionHandler.setCmSessionId(cmSessionId);
                sessionHandler.sendMessageSafe(RawEventImpl.builder().withDirection(ED_OUTGOING).withEventCode("socket.session.ok").withSessionId(cmSessionId).build());
                eventService.handleReceivedEventMessage(new NewSessionConnectedEventImpl(cmSessionId));
            } else {
                org.cmdbuild.auth.session.model.Session cmSession = sessionService.getSessionById(cmSessionId);
//				checkArgument(cmSession.isValid(), "invalid session for session id = %s", cmSessionId);
                eventService.handleReceivedEventMessage(RawEventImpl.builder()
                        .withDirection(ED_INCOMING)
                        .withEventCode(EVENT_CODE_ACTION)
                        .withMessageId(checkNotBlank(toStringOrNull(payload.get("_id")), "missing required '_id' param"))
                        .withSessionId(cmSessionId)
                        .withPayload(payload)
                        .build());
            }
        } catch (Exception ex) {
            logger.error("error processing message event", ex);
            if (sessionHandler != null) {
                sessionHandler.sendMessageSafe(RawEventImpl.builder().withDirection(ED_OUTGOING).withEventCode("socket.error").withSessionId("x").withPayload("message", ex.toString()).build());
            }
        }
    }

    @OnMessage
    public void onMessage(Session session, byte[] message) {
        logger.warn("received unsupported binary message from session = {}, message will be ignored", session.getId());
    }

    @OnMessage
    public void onMessage(Session session, PongMessage message) {
        logger.debug("pong message received from ui, session = {}, message = {}", session.getId(), message);
        // TODO Handle pong messages
    }

    @OnClose
    public void onClose(Session session) {
        try {
            logger.debug("session closed = {}", session.getId());
            String sessionId = getSessionIdOrNull(session);
            WEBSOCKET_CLIENTS_AUTH_TOKEN_BY_SOCKET_SESSION_ID.invalidate(session.getId());
            if (isNotBlank(sessionId)) {
                eventService.handleReceivedEventMessage(new SessionClosedEventImpl(sessionId));
            }
            logger.debug("active sessions = {}", WEBSOCKET_CLIENTS_AUTH_TOKEN_BY_SOCKET_SESSION_ID.size());
        } catch (Exception ex) {
            logger.error("error processing close session event", ex);
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.warn("error, session = {}", session.getId(), throwable);
    }

    public static boolean isConnected(String sessionId) {
        checkNotBlank(sessionId);
        return list(WEBSOCKET_CLIENTS_AUTH_TOKEN_BY_SOCKET_SESSION_ID.asMap().values()).stream().anyMatch(s -> equal(s.getCmSessionIdOrNull(), sessionId));
    }

    public static void sendEventMessage(RawEvent message) {
        if (message.isBroadcast()) {
            LOGGER.debug("send broadcast message = {}", message);
            list(WEBSOCKET_CLIENTS_AUTH_TOKEN_BY_SOCKET_SESSION_ID.asMap().values()).forEach(h -> h.sendMessageSafe(message));
        } else {
            SessionHandler sessionHandler = getSessionHandlerByCmdbuildSessionIdOrNull(message.getSessionIdOrNull());
            if (sessionHandler == null) {
                LOGGER.debug("unable to send message = {}, websocket session not found for session id = {}", message, message.getSessionIdOrNull());
            } else {
                sessionHandler.sendMessageSafe(message);
            }
        }
    }

    private @Nullable
    String getSessionIdOrNull(Session session) {
        return checkNotNull(WEBSOCKET_CLIENTS_AUTH_TOKEN_BY_SOCKET_SESSION_ID.getIfPresent(session.getId()), "websocket session info not found for ws sessionId = %s", session.getId()).getCmSessionIdOrNull();
    }

    private static @Nullable
    SessionHandler getSessionHandlerByCmdbuildSessionIdOrNull(String sessionId) {
        return map(WEBSOCKET_CLIENTS_AUTH_TOKEN_BY_SOCKET_SESSION_ID.asMap()).entrySet().stream()
                .filter((e) -> e.getValue().hasCmSessionId())
                .filter(e -> e.getValue().getCmSessionId().equals(sessionId))
                .findAny().map(Entry::getValue).orElse(null);
    }

    private class SessionHandler {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        private final Session session;
        private String cmSessionId;

        public SessionHandler(Session session) {
            this.session = checkNotNull(session);
        }

        public void setCmSessionId(@Nullable String cmSessionId) {
            this.cmSessionId = cmSessionId;
        }

        public boolean hasCmSessionId() {
            return isNotBlank(cmSessionId);
        }

        public @Nullable
        String getCmSessionIdOrNull() {
            return cmSessionId;
        }

        public String getCmSessionId() {
            return checkNotBlank(cmSessionId);
        }

        public Session getSession() {
            return session;
        }

        public void sendMessageSafe(RawEvent message) {
            checkArgument(message.isOutgoing());

            String payload = toJson(map(message.getPayload())
                    .with("_event", message.getEventCode())
                    .with("_id", message.getMessageId()));

            sendMessageSafe(payload);
        }

        private synchronized void sendMessageSafe(String payload) {
            try {
                logger.debug("send message to session = {} ({}) message = {}", cmSessionId, session.getId(), abbreviate(payload));
                session.getBasicRemote().sendText(checkNotBlank(payload));
            } catch (Exception ex) {
                logger.error("error sending websocket message = {} to session = {}", abbreviate(payload), session, ex);
            }
        }

    }

    private class NewSessionConnectedEventImpl implements NewSessionConnectedEvent {

        private final String sessionId;

        public NewSessionConnectedEventImpl(String sessionId) {
            this.sessionId = checkNotBlank(sessionId);
        }

        @Override
        public String getSessionId() {
            return sessionId;
        }

    }

    private class SessionClosedEventImpl implements SessionClosedEvent {

        private final String sessionId;

        public SessionClosedEventImpl(String sessionId) {
            this.sessionId = checkNotBlank(sessionId);
        }

        @Override
        public String getSessionId() {
            return sessionId;
        }

    }

}
