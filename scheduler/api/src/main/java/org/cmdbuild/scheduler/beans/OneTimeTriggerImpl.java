package org.cmdbuild.scheduler.beans;

import static java.lang.String.format;
import java.util.Date;
import static org.cmdbuild.scheduler.beans.JobTrigger.JobTriggerType.JT_ONCE;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.utils.encode.CmEncodeUtils.encodeString;


public class OneTimeTriggerImpl implements JobTrigger {

	private final Date date;

	private OneTimeTriggerImpl(Date date) {
		this.date = date;
	}

	public Date getDate() {
		return date;
	}

	public static OneTimeTriggerImpl at(Date date) {
		return new OneTimeTriggerImpl(date);
	}

	@Override
	public String toString() {
		return "OneTimeTrigger{" + "date=" + date + '}';
	}

	@Override
	public JobTriggerType getTriggerType() {
		return JT_ONCE;
	}

	@Override
	public String toUniqueKey() {
		return encodeString(format("at_time_%s", toIsoDateTime(date).replaceAll("[^0-9]+", "_")));
	}

}
