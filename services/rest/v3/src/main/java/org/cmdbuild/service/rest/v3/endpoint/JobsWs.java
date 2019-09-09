package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.login.AuthorityConst.HAS_SYSTEM_ACCESS_AUTHORITY;
import org.cmdbuild.common.utils.PagedElements;

import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import org.cmdbuild.jobs.JobData;
import org.cmdbuild.jobs.JobRun;
import static org.cmdbuild.jobs.JobRunStatusImpl.serializeJobRunStatus;
import org.cmdbuild.jobs.JobService;
import org.cmdbuild.jobs.beans.JobDataImpl;
import org.cmdbuild.jobs.beans.JobDataImpl.JobDataImplBuilder;
import org.cmdbuild.jobs.inner.JobRunHelperService;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DETAILED;
import static org.cmdbuild.service.rest.v3.endpoint.AuditWs.serializeErrors;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import org.springframework.security.access.prepost.PreAuthorize;

@Path("jobs/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@PreAuthorize(HAS_SYSTEM_ACCESS_AUTHORITY)
public class JobsWs {

    private final JobService service;
    private final JobRunHelperService jobRunHelperService;

    public JobsWs(JobService service, JobRunHelperService jobRunHelperService) {
        this.service = checkNotNull(service);
        this.jobRunHelperService = checkNotNull(jobRunHelperService);
    }

    @GET
    @Path(EMPTY)
    public Object readMany(@QueryParam(LIMIT) Long limit, @QueryParam(START) Long offset, @QueryParam(DETAILED) @DefaultValue(FALSE) Boolean detailed) {
        List<JobData> jobs = service.getAllJobs();
        return response(paged(jobs, offset, limit).map(detailed ? this::serializeDetailedJob : this::serializeBasicJob));
    }

    @GET
    @Path("{jobId}")
    public Object readOne(@PathParam("jobId") String jobId) {
        JobData job = service.getOneByIdOrCode(jobId);
        return response(serializeDetailedJob(job));
    }

    @POST
    @Path(EMPTY)
    public Object create(WsJobData data) {
        JobData job = service.createJob(data.toJobData().build());
        return response(serializeDetailedJob(job));
    }

    @PUT
    @Path("{jobId}")
    public Object update(@PathParam("jobId") String jobId, WsJobData data) {
        JobData job = service.updateJob(data.toJobData().withId(service.getOneByIdOrCode(jobId).getId()).build());
        return response(serializeDetailedJob(job));
    }

    @DELETE
    @Path("{jobId}")
    public Object delete(@PathParam("jobId") String jobId) {
        service.deleteJob(service.getOneByIdOrCode(jobId).getId());
        return success();
    }

    @POST
    @Path("{jobId}/run")
    public Object runJobNow(@PathParam("jobId") String jobId) {
        JobData job = service.getOneByIdOrCode(jobId);
        jobRunHelperService.runJob(job);
        return success();
    }

    @GET
    @Path("{jobId}/runs")
    public Object getJobRuns(@PathParam("jobId") String jobId, @QueryParam(START) Long offset, @QueryParam(LIMIT) Long limit) {
        PagedElements<JobRun> res = service.getJobRuns(service.getOneByIdOrCode(jobId).getId(), DaoQueryOptionsImpl.builder().withPaging(offset, limit).build());
        return response(res.map(this::serializeBasicJobRun));
    }

    @GET
    @Path("{jobId}/errors")
    public Object getJobRunErrors(@PathParam("jobId") String jobId, @QueryParam(START) Long offset, @QueryParam(LIMIT) Long limit) {
        PagedElements<JobRun> res = service.getJobErrors(service.getOneByIdOrCode(jobId).getId(), DaoQueryOptionsImpl.builder().withPaging(offset, limit).build());
        return response(res.map(this::serializeBasicJobRun));
    }

    @GET
    @Path("_ANY/runs")
    public Object getJobRuns(@QueryParam(START) Long offset, @QueryParam(LIMIT) Long limit) {
        PagedElements<JobRun> res = service.getJobRuns(DaoQueryOptionsImpl.builder().withPaging(offset, limit).build());
        return response(res.map(this::serializeBasicJobRun));
    }

    @GET
    @Path("_ANY/errors")
    public Object getJobRunErrors(@QueryParam(START) Long offset, @QueryParam(LIMIT) Long limit) {
        PagedElements<JobRun> res = service.getJobErrors(DaoQueryOptionsImpl.builder().withPaging(offset, limit).build());
        return response(res.map(this::serializeBasicJobRun));
    }

    @GET
    @Path("{jobId}/runs/{runId}")
    public Object getJobRuns(@PathParam("jobId") String jobId, @PathParam("runId") Long runId) {
        JobRun jobRun = service.getJobRun(runId);
        return response(serializeDetailedJobRun(jobRun));
    }

    private FluentMap<String, Object> serializeBasicJobRun(JobRun jobRun) {
        return map(
                "_id", jobRun.getId(),
                "jobCode", jobRun.getJobCode(),
                "status", serializeJobRunStatus(jobRun.getJobStatus()),
                "completed", jobRun.isCompleted(),
                "timestamp", toIsoDateTime(jobRun.getTimestamp()),
                "elapsedMillis", jobRun.getElapsedTime()
        );
    }

    private Object serializeDetailedJobRun(JobRun jobRun) {
        return serializeBasicJobRun(jobRun).with(
                "errors", serializeErrors(jobRun.getErrorOrWarningEvents())
        );
    }

    private Object serializeDetailedJob(JobData jobData) {
        return serializeBasicJob(jobData).with(
                "config", jobData.getConfig()
        );
    }

    private FluentMap<String, Object> serializeBasicJob(JobData jobData) {
        return map(
                "_id", jobData.getId(),
                "code", jobData.getCode(),
                "description", jobData.getDescription(),
                "type", jobData.getType(),
                "cronExpression", jobData.getCronExpression(),
                "enabled", jobData.isEnabled()
        );
    }

    public static class WsJobData {

        private final String code, description, type;
        private final Boolean enabled;
        private final Map<String, Object> config;

        public WsJobData(
                @JsonProperty("code") String code,
                @JsonProperty("description") String description,
                @JsonProperty("type") String type,
                @JsonProperty("enabled") Boolean enabled,
                @JsonProperty("config") Map<String, Object> config) {
            this.code = code;
            this.description = description;
            this.type = type;
            this.enabled = enabled;
            this.config = config;
        }

        public JobDataImplBuilder toJobData() {
            return JobDataImpl.builder()
                    .withCode(code)
                    .withConfig(config)
                    .withDescription(description)
                    .withEnabled(enabled)
                    .withType(type);
        }

    }
}
