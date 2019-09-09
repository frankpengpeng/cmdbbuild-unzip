/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.scheduler;

import org.cmdbuild.scheduler.beans.JobConfig;
import java.util.Collection;

public interface JobSource {

    String getJobSourceName();

    Collection<JobConfig> getJobs();

    void runJob(String key);

    /**
     * register listeners for {@link JobUpdatedEvent} events
     */
    void register(Object listener);

    boolean isEnabled();

}
