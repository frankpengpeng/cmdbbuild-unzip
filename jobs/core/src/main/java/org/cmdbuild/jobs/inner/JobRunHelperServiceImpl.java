/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.jobs.inner;

import org.cmdbuild.jobs.beans.JobRunImpl;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Stopwatch;
import static java.lang.String.format;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.annotation.PreDestroy;
import org.cmdbuild.audit.ErrorMessagesData;
import org.cmdbuild.cluster.NodeIdProvider;
import org.cmdbuild.common.error.ErrorAndWarningCollectorService;
import org.cmdbuild.common.error.ErrorOrWarningEventCollector;
import org.cmdbuild.jobs.JobData;
import org.cmdbuild.jobs.JobException;
import static org.cmdbuild.jobs.JobExecutorService.JOBUSER_SYSTEM;
import org.cmdbuild.jobs.JobRun;
import org.cmdbuild.jobs.JobRunRepository;
import org.cmdbuild.jobs.JobSessionService;
import org.cmdbuild.jobs.JobRunContext;
import static org.cmdbuild.jobs.JobRunStatus.JRS_COMPLETED;
import static org.cmdbuild.jobs.JobRunStatus.JRS_FAILED;
import static org.cmdbuild.jobs.JobRunStatus.JRS_RUNNING;
import org.cmdbuild.jobs.JobRunnerRepository;
import static org.cmdbuild.jobs.utils.JobUtils.jobDataToJobKey;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import static org.cmdbuild.utils.lang.CmExecutorUtils.shutdownQuietly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static org.cmdbuild.utils.lang.CmExecutorUtils.awaitCompletionIgnoreInterrupt;
import static org.cmdbuild.utils.lang.CmExecutorUtils.namedThreadFactory;
import org.slf4j.MDC;

@Component
public class JobRunHelperServiceImpl implements JobRunHelperService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ExecutorService executorService = Executors.newCachedThreadPool(namedThreadFactory(getClass()));

    private final JobRunRepository jobRunRepository;
    private final ErrorAndWarningCollectorService errorAndWarningCollectorService;
    private final JobSessionService jobSessionService;
    private final JobRunnerRepository jobRunnerRepository;
    private final NodeIdProvider nodeIdProvider;

    public JobRunHelperServiceImpl(JobRunRepository jobRunRepository, ErrorAndWarningCollectorService errorAndWarningCollectorService, JobSessionService jobSessionService, JobRunnerRepository jobRunnerRepository, NodeIdProvider nodeIdProvider) {
        this.jobRunRepository = checkNotNull(jobRunRepository);
        this.errorAndWarningCollectorService = checkNotNull(errorAndWarningCollectorService);
        this.jobSessionService = checkNotNull(jobSessionService);
        this.jobRunnerRepository = checkNotNull(jobRunnerRepository);
        this.nodeIdProvider = checkNotNull(nodeIdProvider);
    }

    @PreDestroy
    public void cleanup() {
        shutdownQuietly(executorService);
    }

    @Override
    public JobRun runJob(JobData data) {
        logger.debug("preparing execution for job = {}", data);
        try {
            return prepareAndRunJob(data);
        } catch (Exception ex) {
            throw new JobException(ex, "error executing job = %s", data);
        }
    }

    private JobRun prepareAndRunJob(JobData data) throws Exception {
        return new JobRunner(data).prepareAndRunJob();
    }

    private class JobRunner {

        private final JobData job;
        private ErrorOrWarningEventCollector eventCollector;
        private JobRun jobRun;
        private Stopwatch stopwatch;
        private boolean success;

        public JobRunner(JobData job) {
            this.job = checkNotNull(job);
        }

        public JobRun prepareAndRunJob() throws Exception {
            prepareRun();
            try {
                awaitCompletionIgnoreInterrupt(executorService.submit(this::executeJob));
                success = true;
            } catch (Exception ex) {
                success = false;
                errorAndWarningCollectorService.getCurrentRequestEventCollector().addError(ex);
                throw ex;
            } finally {
                saveRunExitStatusAndErrors();
            }
            return jobRun;
        }

        private void prepareRun() {
            jobRun = JobRunImpl.builder()
                    .withNodeId(nodeIdProvider.getClusterNodeId())
                    .withJobCode(job.getCode())
                    .withJobStatus(JRS_RUNNING)
                    .withTimestamp(now())
                    .withCompleted(false)
                    .build();
            jobRun = jobRunRepository.create(jobRun);
            stopwatch = Stopwatch.createStarted();
            eventCollector = errorAndWarningCollectorService.getCurrentRequestEventCollector();
        }

        private void saveRunExitStatusAndErrors() {
            long elapsedTimeMillis = stopwatch.elapsed(TimeUnit.MILLISECONDS);
            jobRun = JobRunImpl.copyOf(jobRun)
                    .withCompleted(true)
                    .withElapsedTime(elapsedTimeMillis)
                    .withJobStatus(success ? JRS_COMPLETED : JRS_FAILED)
                    .withErrorMessageData(ErrorMessagesData.fromErrorsAndWarningEvents(eventCollector.getCollectedEvents()))
                    .withLogs(eventCollector.getLogs()) //TODO make this configurable
                    .build();
            jobRun = jobRunRepository.update(jobRun);
        }

        private void executeJob() {
            MDC.put("cm_type", "job");
            MDC.put("cm_id", format("job:%s", jobRun.getId()));
            jobSessionService.createJobSessionContext(JOBUSER_SYSTEM, "job %s", jobDataToJobKey(job));//TODO user
//        jobSessionService.createJobSessionContext(JOBUSER_NOBODY, "job %s", jobDataToJobKey(data));//TODO user
            errorAndWarningCollectorService.getCurrentRequestEventCollector().enableFullLogCollection();//TODO make this configurable 
            try {
                logger.info("executing job type = {} id = {} code = {}", job.getType(), job.getId(), job.getCode());
                jobRunnerRepository.getJobRunner(job.getType()).runJob(job, new MyJobRunContext(jobRun.getId()));
            } finally {
                eventCollector.copyErrorsFrom(errorAndWarningCollectorService.getCurrentRequestEventCollector());
                jobSessionService.destroyJobSessionContext();
                MDC.clear();
            }
        }

    }

    private class MyJobRunContext implements JobRunContext {

        private final long jobRunId;

        public MyJobRunContext(Long jobRunId) {
            this.jobRunId = jobRunId;
        }

        @Override
        public long getJobRunId() {
            return jobRunId;
        }

    }
}
