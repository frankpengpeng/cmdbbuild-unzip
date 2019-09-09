/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.event;

import static com.google.common.base.Objects.equal;
import java.time.ZonedDateTime;
import java.util.Map;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.event.RawEvent.EventDirection.ED_INCOMING;
import static org.cmdbuild.event.RawEvent.EventDirection.ED_OUTGOING;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

public interface RawEvent extends Event {

	final static String EVENT_SESSION_ID_BROADCAST = "BROADCAST";
	final static String EVENT_CODE_ACTION = "action",
			EVENT_CODE_ALERT = "alert";

	EventDirection getDirection();

	@Nullable
	String getSessionIdOrNull();

	String getMessageId();

	String getEventCode();

	Map<String, Object> getPayload();

	ZonedDateTime getTimestamp();

	default boolean hasSessionId() {
		return isNotBlank(getSessionIdOrNull());
	}

	default String getSessionId() {
		return checkNotBlank(getSessionIdOrNull());
	}

	default @Nullable
	String getStringValue(String key) {
		return toStringOrNull(getPayload().get(key));
	}

	default boolean isBroadcast() {
		return equal(EVENT_SESSION_ID_BROADCAST, getSessionIdOrNull());
	}

	default boolean isOutgoing() {
		return equal(ED_OUTGOING, getDirection());
	}

	default boolean isIncoming() {
		return equal(ED_INCOMING, getDirection());
	}

	enum EventDirection {
		ED_INCOMING, ED_OUTGOING;
	}

}
