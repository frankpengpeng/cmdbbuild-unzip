/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.scheduler.annotatedmethodjobs;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.scheduler.beans.JobTrigger;
import org.cmdbuild.scheduler.JobClusterMode;

/**
 *
 */
public class AnnotatedMethodJobImpl implements AnnotatedMethodJob {

	private final String beanName, methodName;
	private final JobTrigger trigger;
	private final JobClusterMode clusterMode;

	public AnnotatedMethodJobImpl(String beanName, String methodName, JobTrigger trigger, JobClusterMode clusterMode) {
		this.beanName = checkNotNull(beanName);
		this.methodName = checkNotNull(methodName);
		this.trigger = checkNotNull(trigger);
		this.clusterMode = checkNotNull(clusterMode);
	}

	@Override
	public String getBeanName() {
		return beanName;
	}

	@Override
	public String getMethodName() {
		return methodName;
	}

	@Override
	public JobTrigger getTrigger() {
		return trigger;
	}

	@Override
	public JobClusterMode getClusterMode() {
		return clusterMode;
	}

	@Override
	public String toString() {
		return "AnnotatedMethodJobImpl{" + "beanName=" + beanName + ", methodName=" + methodName + ", trigger=" + trigger + '}';
	}

}
