/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.jobs.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.common.utils.PagedElements.paged;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import static org.cmdbuild.data.filter.SorterElement.SorterElementDirection.DESC;
import org.cmdbuild.jobs.JobRun;
import org.cmdbuild.jobs.JobRunRepository;
import org.springframework.stereotype.Component;

@Component
public class JobRunRepositoryImpl implements JobRunRepository {

    private final DaoService dao;

    public JobRunRepositoryImpl(DaoService dao) {
        this.dao = checkNotNull(dao);
    }

    @Override
    public JobRun create(JobRun jobRun) {
        return dao.create(jobRun);
    }

    @Override
    public JobRun update(JobRun jobRun) {
        return dao.update(jobRun);
    }

    @Override
    public JobRun getJobRun(Long runId) {
        return dao.getById(JobRun.class, runId).toModel();
    }

    @Override
    public PagedElements<JobRun> getJobRuns(String jobCode, DaoQueryOptions queryOptions) {
        if (queryOptions.getSorter().isNoop()) {
            queryOptions = DaoQueryOptionsImpl.copyOf(queryOptions).orderBy("Timestamp", DESC).build();
        }
        List<JobRun> list = dao.selectAll().from(JobRun.class).where("Job", EQ, jobCode).withOptions(queryOptions).asList();
        if (queryOptions.isPaged()) {
            return paged(list, dao.selectCount().from(JobRun.class).where("Job", EQ, jobCode).where(queryOptions.getFilter()).getCount());
        } else {
            return paged(list);
        }
    }

    @Override
    public PagedElements<JobRun> getJobErrors(String jobCode, DaoQueryOptions queryOptions) {
        if (queryOptions.getSorter().isNoop()) {
            queryOptions = DaoQueryOptionsImpl.copyOf(queryOptions).orderBy("Timestamp", DESC).build();
        }
        List<JobRun> list = dao.selectAll().from(JobRun.class).where("Job", EQ, jobCode).whereExpr("\"Errors\"::varchar LIKE '%ERROR%'").withOptions(queryOptions).asList(); //TODO improve this
        if (queryOptions.isPaged()) {
            return paged(list, dao.selectCount().from(JobRun.class).where("Job", EQ, jobCode).whereExpr("\"Errors\"::varchar LIKE '%ERROR%'").where(queryOptions.getFilter()).getCount()); //TODO improve this
        } else {
            return paged(list);
        }
    }

    @Override
    public PagedElements<JobRun> getJobRuns(DaoQueryOptionsImpl queryOptions) {
        if (queryOptions.getSorter().isNoop()) {
            queryOptions = DaoQueryOptionsImpl.copyOf(queryOptions).orderBy("Timestamp", DESC).build();
        }
        List<JobRun> list = dao.selectAll().from(JobRun.class).withOptions(queryOptions).asList();
        if (queryOptions.isPaged()) {
            return paged(list, dao.selectCount().from(JobRun.class).where(queryOptions.getFilter()).getCount());
        } else {
            return paged(list);
        }
    }

    @Override
    public PagedElements<JobRun> getJobErrors(DaoQueryOptionsImpl queryOptions) {
        if (queryOptions.getSorter().isNoop()) {
            queryOptions = DaoQueryOptionsImpl.copyOf(queryOptions).orderBy("Timestamp", DESC).build();
        }
        List<JobRun> list = dao.selectAll().from(JobRun.class).whereExpr("\"Errors\"::varchar LIKE '%ERROR%'").withOptions(queryOptions).asList(); //TODO improve this
        if (queryOptions.isPaged()) {
            return paged(list, dao.selectCount().from(JobRun.class).whereExpr("\"Errors\"::varchar LIKE '%ERROR%'").where(queryOptions.getFilter()).getCount()); //TODO improve this
        } else {
            return paged(list);
        }
    }

}
