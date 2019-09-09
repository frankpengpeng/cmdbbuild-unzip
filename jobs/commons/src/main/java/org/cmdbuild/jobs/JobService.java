/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.jobs;

import static java.lang.Long.parseLong;
import java.util.List;
import static org.apache.commons.lang3.math.NumberUtils.isNumber;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;

public interface JobService {

    List<JobData> getAllJobs();

    JobData getJob(long id);

    JobData getJobByCode(String code);

    JobData createJob(JobData data);

    JobData updateJob(JobData data);

    void deleteJob(long id);

    default JobData getOneByIdOrCode(String idOrCode) {
        return isNumber(idOrCode) ? getJob(parseLong(idOrCode)) : getJobByCode(idOrCode);
    }

    PagedElements<JobRun> getJobRuns(long jobId, DaoQueryOptions queryOptions);

    PagedElements<JobRun> getJobErrors(long jobId, DaoQueryOptions queryOptions);

    PagedElements<JobRun> getJobRuns(DaoQueryOptionsImpl queryOptions);

    PagedElements<JobRun> getJobErrors(DaoQueryOptionsImpl queryOptions);

    JobRun getJobRun(Long runId);

}
