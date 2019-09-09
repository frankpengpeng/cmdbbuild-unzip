/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.jobs;

import static com.google.common.base.Preconditions.checkArgument;
import org.cmdbuild.jobs.inner.JobRunHelperService;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.EventBus;
import static java.lang.String.format;
import java.util.Collection;
import java.util.List;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.config.JobsConfiguration;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import static org.cmdbuild.jobs.utils.JobUtils.jobDataToJobKey;
import static org.cmdbuild.jobs.utils.JobUtils.jobKeyToCardId;
import org.cmdbuild.scheduler.beans.JobConfig;
import org.cmdbuild.scheduler.JobSource;
import org.cmdbuild.scheduler.JobUpdatedEvent;
import org.cmdbuild.scheduler.beans.CronTrigger;
import org.cmdbuild.scheduler.beans.JobConfigImpl;
import org.cmdbuild.scheduler.beans.JobDetail;
import org.cmdbuild.scheduler.beans.JobTrigger;
import org.cmdbuild.services.MinionComponent;
import org.cmdbuild.services.MinionStatus;
import static org.cmdbuild.services.MinionStatus.MS_DISABLED;
import static org.cmdbuild.services.MinionStatus.MS_READY;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@MinionComponent(name = "Scheduled Jobs", configBean = JobsConfiguration.class)
public class JobServiceImpl implements JobService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final JobRepository jobRepository;
    private final JobRunHelperService helper;
    private final JobRunRepository jobRunRepository;
    private final JobsConfiguration configuration;
    private final JobRunnerRepository jobRunnerRepository;

    private final EventBus eventBus = new EventBus();
    private final MyJobSource myJobSource = new MyJobSource();

    public JobServiceImpl(JobRepository jobRepository, JobRunHelperService helper, JobRunRepository jobRunRepository, JobsConfiguration configuration, JobRunnerRepository jobRunnerRepository) {
        this.jobRepository = checkNotNull(jobRepository);
        this.helper = checkNotNull(helper);
        this.jobRunRepository = checkNotNull(jobRunRepository);
        this.configuration = checkNotNull(configuration);
        this.jobRunnerRepository = checkNotNull(jobRunnerRepository);
    }

    public MinionStatus getServiceStatus() {
        if (configuration.isEnabled()) {
            return MS_READY;
        } else {
            return MS_DISABLED;
        }
    }

    @Bean
    public JobSource getJobSource() {
        return myJobSource;
    }

    @Override
    public List<JobData> getAllJobs() {
        return jobRepository.getAllJobs();
    }

    @Override
    public JobData getJob(long id) {
        return jobRepository.getOne(id);
    }

    @Override
    public JobData getJobByCode(String code) {
        return jobRepository.getOneByCode(code);
    }

    @Override
    public JobData createJob(JobData data) {
        validateJobData(data);
        data = jobRepository.create(data);
        eventBus.post(JobUpdatedEvent.INSTANCE);
        return data;
    }

    @Override
    public JobData updateJob(JobData data) {
        validateJobData(data);
        data = jobRepository.update(data);
        eventBus.post(JobUpdatedEvent.INSTANCE);
        return data;
    }

    @Override
    public void deleteJob(long id) {
        jobRepository.delete(id);
        eventBus.post(JobUpdatedEvent.INSTANCE);
    }

    @Override
    public JobRun getJobRun(Long runId) {
        return jobRunRepository.getJobRun(runId);
    }

    @Override
    public PagedElements<JobRun> getJobRuns(long jobId, DaoQueryOptions queryOptions) {
        return jobRunRepository.getJobRuns(getJob(jobId).getCode(), queryOptions);
    }

    @Override
    public PagedElements<JobRun> getJobErrors(long jobId, DaoQueryOptions queryOptions) {
        return jobRunRepository.getJobErrors(getJob(jobId).getCode(), queryOptions);
    }

    @Override
    public PagedElements<JobRun> getJobRuns(DaoQueryOptionsImpl queryOptions) {
        return jobRunRepository.getJobRuns(queryOptions);
    }

    @Override
    public PagedElements<JobRun> getJobErrors(DaoQueryOptionsImpl queryOptions) {
        return jobRunRepository.getJobErrors(queryOptions);
    }

    private void validateJobData(JobData data) {
        jobRunnerRepository.getJobRunner(data.getType()).vaildateJob(data);
    }

    private class MyJobSource implements JobSource {

        @Override
        public String getJobSourceName() {
            return "jobs";
        }

        @Override
        public Collection<JobConfig> getJobs() {
            return jobRepository.getAllJobs().stream().filter(j -> j.isEnabled() && isNotBlank(j.getCronExpression())).map(this::buildJobConfig).collect(toList());
        }

        @Override
        public void runJob(String key) {
            JobData job = jobRepository.getOne(jobKeyToCardId(key));
            checkArgument(job.isEnabled(), "error: this job is not enabled");
            helper.runJob(job);
        }

        @Override
        public void register(Object listener) {
            eventBus.register(listener);
        }

        private JobConfig buildJobConfig(JobData jobData) {
            JobTrigger trigger = new CronTrigger(format("0 %s", checkNotBlank(jobData.getCronExpression())));
            JobDetail jobDetail = new JobDetailImpl(jobData);
            return new JobConfigImpl(trigger, jobDetail);
        }

        @Override
        public boolean isEnabled() {
            return configuration.isEnabled();
        }
    }

    private static class JobDetailImpl implements JobDetail {

        private final String key;

        public JobDetailImpl(JobData data) {
            this.key = jobDataToJobKey(data);
        }

        @Override
        public String getKey() {
            return key;
        }
    }

}
