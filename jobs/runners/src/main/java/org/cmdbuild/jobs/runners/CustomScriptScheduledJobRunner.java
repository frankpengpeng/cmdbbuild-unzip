/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.jobs.runners;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import java.util.Map;
import org.cmdbuild.api.fluent.CmApiService;
import static org.cmdbuild.common.error.ErrorAndWarningCollectorService.marker;
import org.cmdbuild.jobs.JobData;
import org.cmdbuild.jobs.JobRunContext;
import org.cmdbuild.jobs.JobRunner;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringInline;
import static org.cmdbuild.utils.lang.CmStringUtils.normalize;
import org.cmdbuild.lang.scriptexecutors.BeanshellScriptExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CustomScriptScheduledJobRunner implements JobRunner {

    public static final String SCHEDULED_SCRIPT_JOB_TYPE = "scheduled_script";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CmApiService apiService;

    public CustomScriptScheduledJobRunner(CmApiService apiService) {
        this.apiService = checkNotNull(apiService);
    }

    @Override
    public String getName() {
        return SCHEDULED_SCRIPT_JOB_TYPE;
    }

    @Override
    public void runJob(JobData jobData, JobRunContext jobContext) {
        String script = jobData.getConfigNotBlank("script");
        Map<String, Object> result = new BeanshellScriptExecutor(script).execute(map(
                "job", jobData,
                "cmdb", apiService.getCmApi(),
                "logger", LoggerFactory.getLogger(format("%s.job_%s_%s", getClass().getName(), jobData.getId(), normalize(jobData.getCode())))));
        logger.info(marker(), "executed job {}, result : {}", jobData, mapToLoggableStringInline(result));
    }

}
