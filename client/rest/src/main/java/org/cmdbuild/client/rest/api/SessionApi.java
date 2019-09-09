/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.api;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import org.cmdbuild.client.rest.model.Session;

public interface SessionApi {

	SessionInfo getSessionInfo();

	Map<String, String> getPreferences();

	Map<String, String> getSystemConfig();

	SessionApiWithSession updateSession(Session session);

	List<SessionInfo> getAllSessionsInfo();

	interface SessionInfo {

		String getSessionToken();

		String getUsername();

		ZonedDateTime getLastActive();
	}

	interface SessionApiWithSession {

		SessionApi then();

		Session getSession();
	}

}
