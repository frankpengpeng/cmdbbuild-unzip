package org.cmdbuild.config;

import java.util.Map;

public interface SchedulerConfiguration {

	Map<String, String> getQuartzProperties();

	boolean isEnabled();
}
