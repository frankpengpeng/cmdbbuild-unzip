/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.scheduler.beans;

import static org.cmdbuild.scheduler.utils.JobKeyUtils.serializeJobKey;

public interface ScheduledJobInfo {

	String getGroup();

	String getName();

	String getTrigger();

	default String getKey() {
		return serializeJobKey(getGroup(), getName());
	}
}
