package org.cmdbuild.scheduler;

import org.cmdbuild.scheduler.beans.ScheduledJobInfo;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import static org.cmdbuild.scheduler.utils.JobKeyUtils.deserializeJobKey;

public interface SchedulerService {

	List<ScheduledJobInfo> getConfiguredJobs();

	void triggerJobImmediately(String group, String name);

	default void triggerJobImmediately(String key) {
		Pair<String, String> pair = deserializeJobKey(key);
		triggerJobImmediately(pair.getLeft(), pair.getRight());
	}

}
