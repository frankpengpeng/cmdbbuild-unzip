/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.listener;

import static java.lang.String.format;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;

public interface PostgresNotificationService {

	void sendNotification(String channel, String payload);

	default void sendInfo(String message, Object... args) {
		sendInfo(format(message, args));
	}

	default void sendInfo(Object payload) {
		String payloadStr;
		if (!(payload instanceof String)) {
			payloadStr = toJson(payload);
		} else {
			payloadStr = (String) payload;
		}
		sendNotification("cminfo", payloadStr);
	}
}
