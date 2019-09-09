/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.webapp;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.PongMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import org.cmdbuild.config.BimserverConfiguration;
import static org.cmdbuild.spring.SpringIntegrationUtils.applicationContext;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import org.glassfish.tyrus.client.ClientManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServerEndpoint(value = "/services/bimserver/stream")
public class BimserverWebsocketProxy {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final Cache<String, BimserverWebsocketClient> WEBSOCKET_CLIENTS_BY_UI_SESSION_ID = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build();

	private final BimserverConfiguration configuration;

	public BimserverWebsocketProxy() {
		configuration = applicationContext().getBean(BimserverConfiguration.class);//TODO fix this, autowire spring config
		logger.info("ready");
	}

	@OnOpen
	public void onOpen(Session session) {
		try {
			logger.debug("ui session opened = {}", session.getId());
			BimserverWebsocketClient websocketClient = new BimserverWebsocketClient(session);
			WEBSOCKET_CLIENTS_BY_UI_SESSION_ID.put(session.getId(), websocketClient);
			logger.debug("active sessions = {}", WEBSOCKET_CLIENTS_BY_UI_SESSION_ID.size());
		} catch (Exception ex) {
			logger.warn("error processing open session event", ex);
		}
	}

	private BimserverWebsocketClient getBimserverWebsocketClient(Session session) {
		return checkNotNull(WEBSOCKET_CLIENTS_BY_UI_SESSION_ID.getIfPresent(session.getId()), "bim server client not found for session = %s", session.getId());
	}

	@OnMessage
	public void onMessage(Session session, String message) {
		try {
			logger.debug("string message received from ui, session = {}, message = {}", session.getId(), abbreviate(message));
			getBimserverWebsocketClient(session).sendMessage(message);
		} catch (Exception ex) {
			logger.error("error processing message event", ex);
		}
	}

	@OnMessage
	public void onMessage(Session session, byte[] message) {
		try {
			logger.debug("binary message received from ui, session = {}, message = {}", session.getId(), byteCountToDisplaySize(message.length));
			getBimserverWebsocketClient(session).sendMessage(message);
		} catch (Exception ex) {
			logger.error("error processing message event", ex);
		}
	}

	@OnMessage
	public void onMessage(Session session, PongMessage message) {
		logger.debug("pong message received from ui, session = {}, message = {}", session.getId(), message);
		// TODO Handle pong messages
	}

	@OnClose
	public void onClose(Session session) {
		try {
			logger.debug("ui session closed = {}", session.getId());
			getBimserverWebsocketClient(session).closeConnection();
			WEBSOCKET_CLIENTS_BY_UI_SESSION_ID.invalidate(session.getId());
			logger.debug("active sessions = {}", WEBSOCKET_CLIENTS_BY_UI_SESSION_ID.size());
		} catch (Exception ex) {
			logger.error("error processing close session event", ex);
		}
	}

	@OnError
	public void onError(Session session, Throwable throwable) {
		logger.warn("ui session error, session = {}", session.getId(), throwable);
	}

	private class BimserverWebsocketClient {

		private final Session uiSession;
		private Session bsSession;

		public BimserverWebsocketClient(Session session) throws URISyntaxException, IOException, DeploymentException {
			uiSession = checkNotNull(session);

			ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

			ClientManager client = ClientManager.createClient();
			client.connectToServer(new Endpoint() {

				@Override
				public void onOpen(Session session, EndpointConfig config) {
					try {
						logger.debug("bimserver session opened = {}", session.getId());
						bsSession = session;
					} catch (Exception ex) {
						logger.error("error processing bimserver open session event", ex);
					}

					session.addMessageHandler(new MyTextMessageHandler());
					session.addMessageHandler(new MyBinaryMessageHandler());
				}

				@Override
				public void onClose(Session session, CloseReason closeReason) {
					try {
						logger.debug("bimserver session closed, session = {}, reason = {}", session.getId(), closeReason);
						try {
							uiSession.close(closeReason);
						} finally {
							closeConnection();
						}
					} catch (Exception ex) {
						logger.error("error processing bimserver close session event", ex);
					}
				}

			}, cec, new URI(configuration.getUrl().replaceFirst("http", "ws") + "/stream"));
			checkNotNull(bsSession, "unable to open bimserver websocket session");
		}

		private void sendMessage(String message) throws IOException {
			bsSession.getBasicRemote().sendText(message);
		}

		private void sendMessage(byte[] message) throws IOException {
			bsSession.getBasicRemote().sendBinary(ByteBuffer.wrap(message));
		}

		private void closeConnection() throws IOException {
			if (bsSession != null) {
				try {
					if (bsSession.isOpen()) {
						bsSession.close();
					}
				} catch (Exception ex) {
					logger.debug("error closing websocket connection", ex);
				}
			}
			bsSession = null;
		}

		private class MyTextMessageHandler implements MessageHandler.Whole<String> {

			@Override
			public void onMessage(String msg) {
				try {
					logger.debug("bimserver string message received, session = {}, message = {}", bsSession.getId(), abbreviate(msg));
					uiSession.getBasicRemote().sendText(msg);
				} catch (Exception ex) {
					logger.error("error processing bimserver text message", ex);
				}
			}

		}

		private class MyBinaryMessageHandler implements MessageHandler.Whole<byte[]> {

			@Override
			public void onMessage(byte[] msg) {
				try {
					logger.debug("bimserver binary message received, session = {}, message = {}", bsSession.getId(), byteCountToDisplaySize(msg.length));
					uiSession.getBasicRemote().sendBinary(ByteBuffer.wrap(msg));
				} catch (Exception ex) {
					logger.error("error processing bimserver text message", ex);
				}
			}

		}

	}

}
