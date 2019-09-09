/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.impl;

import org.cmdbuild.client.rest.core.RestWsClient;
import org.cmdbuild.client.rest.core.AbstractServiceClientImpl;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.transform;
import com.google.gson.JsonElement;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.client.rest.api.SessionApi;
import org.cmdbuild.client.rest.model.Session;
import org.cmdbuild.utils.date.CmDateUtils;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class SessionApiImpl extends AbstractServiceClientImpl implements SessionApi {

	public SessionApiImpl(RestWsClient restClient) {
		super(restClient);
	}

	@Override
	public SessionInfo getSessionInfo() {
		JsonElement response = get("sessions/current").asJson();
		return toSessionInfo(response.getAsJsonObject().get("data"));
	}

	@Override
	public List<SessionInfo> getAllSessionsInfo() {
		return list(transform(get("sessions").asJson().getAsJsonObject().getAsJsonArray("data"), this::toSessionInfo));
	}

	private SessionInfo toSessionInfo(JsonElement e) {
		return new SessionInfoImpl(e.getAsJsonObject().getAsJsonPrimitive("_id").getAsString(),
				e.getAsJsonObject().getAsJsonPrimitive("username").getAsString(),
				CmDateUtils.toDateTime(e.getAsJsonObject().getAsJsonPrimitive("lastActive").getAsString()));
	}

	@Override
	public Map<String, String> getPreferences() {
		logger.debug("getPreferences");
		Map<String, String> map = map();
		get("sessions/current/preferences").asJson().getAsJsonObject().getAsJsonObject("data").entrySet().forEach((entry) -> {
			map.put(entry.getKey(), toString(entry.getValue()));
		});
		return map;
	}

	@Override
	public Map<String, String> getSystemConfig() {
		logger.debug("getSystemConfig");
		Map<String, String> map = map();
		get("configuration").asJson().getAsJsonObject().getAsJsonObject("data").entrySet().forEach((entry) -> {
			map.put(entry.getKey(), toString(entry.getValue()));
		});
		return map;
	}

	@Override
	public SessionApiWithSession updateSession(Session session) {
		Session response = fromJson(put("sessions/current", session).asJackson().get("data"), Session.class);
		return new SessionApiWithSession() {
			@Override
			public SessionApi then() {
				return SessionApiImpl.this;
			}

			@Override
			public Session getSession() {
				return response;
			}
		};
	}

	private static class SessionInfoImpl implements SessionInfo {

		private final String sessionToken, username;
		private final ZonedDateTime lastActive;

		public SessionInfoImpl(String sessionToken, String username, ZonedDateTime lastActive) {
			this.sessionToken = checkNotBlank(sessionToken);
			this.username = checkNotBlank(username);
			this.lastActive = checkNotNull(lastActive);
		}

		@Override
		public String getSessionToken() {
			return sessionToken;
		}

		@Override
		public String getUsername() {
			return username;
		}

		@Override
		public ZonedDateTime getLastActive() {
			return lastActive;
		}

		@Override
		public String toString() {
			return "SimpleSessionInfo{" + "sessionToken=" + sessionToken + '}';
		}

	}

}
