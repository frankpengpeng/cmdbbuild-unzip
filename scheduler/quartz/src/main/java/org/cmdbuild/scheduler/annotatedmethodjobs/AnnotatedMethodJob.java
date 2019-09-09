/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.scheduler.annotatedmethodjobs;

import org.cmdbuild.scheduler.beans.JobTrigger;
import org.cmdbuild.scheduler.JobClusterMode;

/**
 *
 * @author davide
 */
public interface AnnotatedMethodJob {

	String getBeanName();

	String getMethodName();

	JobTrigger getTrigger();

	JobClusterMode getClusterMode();
}
