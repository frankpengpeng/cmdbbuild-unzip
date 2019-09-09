/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.scheduler.quartz;

import org.cmdbuild.scheduler.beans.ScheduledJobInfo;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class ScheduledJobInfoImpl implements ScheduledJobInfo {

	private final String group, name, trigger;

	public ScheduledJobInfoImpl(String group, String name, String trigger) {
		this.group = checkNotBlank(group);
		this.name = checkNotBlank(name);
		this.trigger = checkNotBlank(trigger);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getGroup() {
		return group;
	}

	@Override
	public String getTrigger() {
		return trigger;
	}

}
