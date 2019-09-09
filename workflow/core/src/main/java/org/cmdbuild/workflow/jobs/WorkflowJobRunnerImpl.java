/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.jobs;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptyMap;
import java.util.Map;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.auth.session.SessionService;
import static org.cmdbuild.common.error.ErrorAndWarningCollectorService.marker;
import org.cmdbuild.jobs.JobData;
import org.cmdbuild.jobs.JobRunContext;
import org.cmdbuild.jobs.JobRunner;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlankOrNull;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.cmdbuild.workflow.FlowAdvanceResponse;
import org.cmdbuild.workflow.WorkflowConfiguration;
import org.cmdbuild.workflow.WorkflowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WorkflowJobRunnerImpl implements JobRunner {

    public static final String WORKFLOW_JOB_TYPE = "workflow";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final WorkflowService service;
    private final WorkflowConfiguration config;
    private final SessionService sessionService;

    public WorkflowJobRunnerImpl(WorkflowService service, WorkflowConfiguration config, SessionService sessionService) {
        this.service = checkNotNull(service);
        this.config = checkNotNull(config);
        this.sessionService = checkNotNull(sessionService);
    }

    @Override
    public String getName() {
        return WORKFLOW_JOB_TYPE;
    }

    @Override
    public void runJob(JobData jobData, JobRunContext jobContext) {
        new WfJobHelper(jobData, jobContext).runJob();
    }

    private class WfJobHelper {

        private final JobData jobData;
        private final JobRunContext jobContext;
        private final String classname, username;

        public WfJobHelper(JobData jobData, JobRunContext jobContext) {
            this.jobData = checkNotNull(jobData);
            this.jobContext = checkNotNull(jobContext);
            classname = jobData.getConfigNotBlank("classname");
            username = firstNotBlankOrNull(toStringOrNull(jobData.getConfig().get("username")), config.getDefaultUserForWfJobs());
        }

        public void runJob() {
            if (isNotBlank(username)) {
                sessionService.impersonate(username);
                try {
                    doRunJob();
                } finally {
                    sessionService.deimpersonate();
                }
            } else {
                doRunJob();
            }
        }

        private void doRunJob() {
            Map<String, Object> attributes = convert(jobData.getConfig().get("attributes"), Map.class, emptyMap());
            logger.info("start process = {} with attrs = {}", classname, attributes);
            FlowAdvanceResponse response = service.startProcess(classname, attributes, true);//TODO eval attributes ??
            logger.info(marker(), "started process = {}", response.getFlowCard());
        }

    }
}
