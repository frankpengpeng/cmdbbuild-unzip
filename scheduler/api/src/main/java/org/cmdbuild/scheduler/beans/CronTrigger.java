package org.cmdbuild.scheduler.beans;

import static java.lang.String.format;
import static org.cmdbuild.scheduler.beans.JobTrigger.JobTriggerType.JT_CRON;
import static org.cmdbuild.utils.encode.CmEncodeUtils.encodeString;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class CronTrigger implements JobTrigger {

	private final String cronExpression;

	public CronTrigger(String cronExpression) {
		this.cronExpression = checkNotBlank(cronExpression);
	}

	public static CronTrigger at(String cronExpression) {
		return new CronTrigger(cronExpression);
	}

	public String getCronExpression() {
		return cronExpression;
	}

	@Override
	public String toString() {
		return "RecurringTrigger{" + "cronExpression=" + cronExpression + '}';
	}

	@Override
	public JobTriggerType getTriggerType() {
		return JT_CRON;
	}

	@Override
	public String toUniqueKey() {
		return encodeString(format("every_%s", getCronExpression().replaceAll("[*]", "X").replaceAll("[?]", "Q").replaceAll("[/]", "I").replaceAll("[^0-9A-Z]+", "_")));
	}

}
