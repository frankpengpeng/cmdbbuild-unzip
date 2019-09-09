/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.jobs;

public interface JobRunner {

    String getName();

    void runJob(JobData jobData, JobRunContext jobContext);

    default void vaildateJob(JobData jobData) {

    }

}
