package org.cmdbuild.clustering;

import java.time.ZonedDateTime;
import java.util.Map;
import javax.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;

public interface ClusterMessage {

	final static String THIS_INSTANCE_ID = "THIS";

	String getSourceInstanceId();

	String getMessageId();

	ZonedDateTime getTimestamp();

	String getMessageType();

	Map<String, Object> getMessageData();

	/**
	 * Message.RSVP flag: when this flag is encountered, the message send blocks
	 * until all members have acknowledged reception of the message (of course
	 * excluding members which crashed or left meanwhile).
	 *
	 * This also serves as another purpose: if we send an RSVP-tagged message,
	 * then - when the send() returns - we’re guaranteed that all messages sent
	 * before will have been delivered at all members as well. So, for example,
	 * if P sends message 1-10, and marks 10 as RSVP, then, upon JChannel.send()
	 * returning, P will know that all members received messages 1-10 from P.
	 *
	 * Note that since RSVP’ing a message is costly, and might block the sender
	 * for a while, it should be used sparingly. For example, when completing a
	 * unit of work (ie. member P sending N messages), and P needs to know that
	 * all messages were received by everyone, then RSVP could be used.
	 *
	 * @return
	 */
	boolean requireRsvp();

	@Nullable
	default <T> T getValue(String key, Class<T> type) {
		return convert(getMessageData().get(key), type);
	}

	@Nullable
	default String getValue(String key) {
		return getValue(key, String.class);
	}
}
