/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.scheduler.beans;

import static com.google.common.base.Preconditions.checkNotNull;

public class JobConfigImpl implements JobConfig {

	private final JobTrigger trigger;
	private final JobDetail job;

	public JobConfigImpl(JobTrigger trigger, JobDetail job) {
		this.trigger = checkNotNull(trigger);
		this.job = checkNotNull(job);
	}

	@Override
	public JobTrigger getTrigger() {
		return trigger;
	}

	@Override
	public JobDetail getJob() {
		return job;
	}

	@Override
	public String toString() {
		return "JobConfigImpl{" + "trigger=" + trigger + ", job=" + job + '}';
	}

}
