package org.cmdbuild.scheduler.beans;

public interface JobTrigger {

	JobTriggerType getTriggerType();

	String toUniqueKey();

	enum JobTriggerType {
		JT_ONCE, JT_CRON
	}

}
