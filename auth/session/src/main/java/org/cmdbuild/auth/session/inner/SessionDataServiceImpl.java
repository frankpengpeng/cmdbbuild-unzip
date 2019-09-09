/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.session.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptyMap;
import java.util.Map;
import javax.annotation.Nullable;
import javax.inject.Provider;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.auth.session.dao.SessionRepository;
import org.cmdbuild.auth.session.model.Session;
import org.cmdbuild.auth.session.model.SessionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class SessionDataServiceImpl implements SessionDataService {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final Provider<SessionRepository> sessionStore;// TODO: provider injection is not great, it should be possibile to refactor session repo and remove the dependency loop
	private final CurrentSessionHolder currentSessionIdHolder;

	public SessionDataServiceImpl(Provider<SessionRepository> sessionStore, CurrentSessionHolder currentSessionIdHolder) {
		this.sessionStore = checkNotNull(sessionStore);
		this.currentSessionIdHolder = checkNotNull(currentSessionIdHolder);
	}

	@Override
	public SessionData getCurrentSessionDataSafe() {
		return new SessionData() {

			@Override
			public Map<String, Object> getSessionData() {
				Session session = getSession();
				if (session == null) {
					logger.warn("no session available, using dummy session data");
					return emptyMap();
				} else {
					return session.getSessionData();
				}
			}

			private @Nullable
			Session getSession() {
				String sessionId = currentSessionIdHolder.getOrNull();
				if (isBlank(sessionId)) {
					return null;
				} else {
					return sessionStore.get().getSessionByIdOrNull(sessionId);
				}
			}
		};
	}
}
