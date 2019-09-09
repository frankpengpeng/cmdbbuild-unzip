/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.jobs;

import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;

public interface JobRunRepository {

    JobRun create(JobRun jobRun);

    JobRun update(JobRun jobRun);

    JobRun getJobRun(Long runId);

    PagedElements<JobRun> getJobRuns(String jobCode, DaoQueryOptions queryOptions);

    PagedElements<JobRun> getJobErrors(String jobCode, DaoQueryOptions queryOptions);

    PagedElements<JobRun> getJobRuns(DaoQueryOptionsImpl queryOptions);

    PagedElements<JobRun> getJobErrors(DaoQueryOptionsImpl queryOptions);
}
