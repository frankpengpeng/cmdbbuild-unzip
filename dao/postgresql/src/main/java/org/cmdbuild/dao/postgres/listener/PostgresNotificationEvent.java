/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.listener;

import static com.google.common.base.Objects.equal;
import java.util.Map;
import org.cmdbuild.utils.json.CmJsonUtils;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

public interface PostgresNotificationEvent {

	int getServerPid();

	String getChannel();

	String getPayload();

	default String getType() {
		return checkNotBlank(toStringOrNull(getPayloadAsMap().get("type")));
	}

	default boolean isCommand() {
		return equal("command", getType());
	}

	default boolean isEvent() {
		return equal("event", getType());
	}

	default String getAction() {
		return checkNotBlank(toStringOrNull(getPayloadAsMap().get("action")));
	}

	default String getEvent() {
		return checkNotBlank(toStringOrNull(getPayloadAsMap().get("event")));
	}

	default Map<String, Object> getPayloadAsMap() {
		return CmJsonUtils.fromJson(getPayload(), CmJsonUtils.MAP_OF_OBJECTS);
	}

	default boolean isEvent(String event) {
		return isEvent() && equal(getEvent(), event);
	}

}
