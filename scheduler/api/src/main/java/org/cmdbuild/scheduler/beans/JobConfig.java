/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.scheduler.beans;

public interface JobConfig {

	JobTrigger getTrigger();

	JobDetail getJob();

	default String getKey() {
		return getJob().getKey();
	}
}
