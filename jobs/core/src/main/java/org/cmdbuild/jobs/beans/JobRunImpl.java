/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.jobs.beans;

import static com.google.common.base.Preconditions.checkNotNull;
import java.time.ZonedDateTime;
import java.util.List;
import javax.annotation.Nullable;
import org.cmdbuild.audit.ErrorMessageDataImpl;
import org.cmdbuild.audit.ErrorMessagesData;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.cmdbuild.jobs.JobRun;
import org.cmdbuild.jobs.JobRunStatus;
import static org.cmdbuild.jobs.JobRunStatusImpl.serializeJobRunStatus;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@CardMapping("_JobRun")
public class JobRunImpl implements JobRun {

    private final Long id, elapsedTime;
    private final String jobCode, logs, nodeId;
    private final JobRunStatus jobStatus;
    private final ZonedDateTime timestamp;
    private final boolean completed, hasError;
    private final ErrorMessagesData errorMessageData;

    private JobRunImpl(JobRunImplBuilder builder) {
        this.id = builder.id;
        this.jobCode = checkNotBlank(builder.jobCode);
        this.jobStatus = checkNotNull(builder.jobStatus);
        this.timestamp = checkNotNull(builder.timestamp);
        this.completed = builder.completed;
        if (completed) {
            this.elapsedTime = checkNotNull(builder.elapsedTime);
            this.errorMessageData = checkNotNull(builder.errorMessageData);
            this.hasError = builder.errorMessageData.getData().stream().anyMatch(ErrorMessageDataImpl::isError);
        } else {
            this.elapsedTime = null;
            this.errorMessageData = null;
            this.hasError = false;
        }
        this.logs = builder.logs;
        this.nodeId = builder.nodeId;
    }

    @Override
    @Nullable
    @CardAttr(ATTR_ID)
    public Long getId() {
        return id;
    }

    @Override
    @Nullable
    @CardAttr
    public Long getElapsedTime() {
        return elapsedTime;
    }

    @Override
    @CardAttr("Job")
    public String getJobCode() {
        return jobCode;
    }

    @Override
    public JobRunStatus getJobStatus() {
        return jobStatus;
    }

    @CardAttr("JobStatus")
    public String getJobStatusAsString() {
        return serializeJobRunStatus(jobStatus);
    }

    @Override
    @CardAttr
    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    @CardAttr
    public boolean isCompleted() {
        return completed;
    }

    @Override
    @Nullable
    @CardAttr("Errors")
    public ErrorMessagesData getErrorMessageData() {
        return errorMessageData;
    }

    @Override
    @Nullable
    @CardAttr(value = "HasError", readFromDb = false)
    public boolean hasErrors() {
        return hasError;
    }

    @Override
    @Nullable
    @CardAttr
    public String getLogs() {
        return logs;
    }

    @Override
    @Nullable
    @CardAttr
    public String getNodeId() {
        return nodeId;
    }

    public static JobRunImplBuilder builder() {
        return new JobRunImplBuilder();
    }

    public static JobRunImplBuilder copyOf(JobRun source) {
        return new JobRunImplBuilder()
                .withId(source.getId())
                .withElapsedTime(source.getElapsedTime())
                .withJobCode(source.getJobCode())
                .withJobStatus(source.getJobStatus())
                .withTimestamp(source.getTimestamp())
                .withCompleted(source.isCompleted())
                .withNodeId(source.getNodeId())
                .withLogs(source.getLogs())
                .withErrorMessageData(source.getErrorMessageData());
    }

    public static class JobRunImplBuilder implements Builder<JobRunImpl, JobRunImplBuilder> {

        private Long id;
        private Long elapsedTime;
        private String jobCode, nodeId, logs;
        private JobRunStatus jobStatus;
        private ZonedDateTime timestamp;
        private Boolean completed;
        private ErrorMessagesData errorMessageData;

        public JobRunImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public JobRunImplBuilder withElapsedTime(Long elapsedTime) {
            this.elapsedTime = elapsedTime;
            return this;
        }

        public JobRunImplBuilder withJobCode(String jobCode) {
            this.jobCode = jobCode;
            return this;
        }

        public JobRunImplBuilder withNodeId(String nodeId) {
            this.nodeId = nodeId;
            return this;
        }

        public JobRunImplBuilder withLogs(String logs) {
            this.logs = logs;
            return this;
        }

        @CardAttr("JobStatus")
        public JobRunImplBuilder withJobStatus(JobRunStatus jobStatus) {
            this.jobStatus = jobStatus;
            return this;
        }

        public JobRunImplBuilder withTimestamp(ZonedDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public JobRunImplBuilder withCompleted(Boolean completed) {
            this.completed = completed;
            return this;
        }

        public JobRunImplBuilder withErrorMessageData(ErrorMessagesData errorMessageData) {
            this.errorMessageData = errorMessageData;
            return this;
        }

        public JobRunImplBuilder withErrorMessages(List<ErrorMessageDataImpl> data) {
            return this.withErrorMessageData(new ErrorMessagesData(data));
        }

        @Override
        public JobRunImpl build() {
            return new JobRunImpl(this);
        }

    }
}
