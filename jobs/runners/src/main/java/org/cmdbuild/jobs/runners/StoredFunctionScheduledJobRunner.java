/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.jobs.runners;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptyList;
import java.util.List;
import static org.cmdbuild.common.error.ErrorAndWarningCollectorService.marker;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.core.q3.ResultRow;
import org.cmdbuild.dao.function.StoredFunction;
import org.cmdbuild.jobs.JobData;
import org.cmdbuild.jobs.JobRunContext;
import org.cmdbuild.jobs.JobRunner;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringInline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class StoredFunctionScheduledJobRunner implements JobRunner {

    public static final String STORED_FUNCTION_JOB_TYPE = "scheduled_db_function";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;

    public StoredFunctionScheduledJobRunner(DaoService dao) {
        this.dao = checkNotNull(dao);
    }

    @Override
    public String getName() {
        return STORED_FUNCTION_JOB_TYPE;
    }

    @Override
    public void runJob(JobData jobData, JobRunContext jobContext) {
        String functionName = jobData.getConfigNotBlank("function");
        StoredFunction function = dao.getFunctionByName(functionName);
        logger.info(marker(), "execute stored function = {}", function);
        List<ResultRow> result = dao.selectFunction(function, emptyList()).run();
        for (int i = 0; i < result.size(); i++) {
            logger.info(marker(), "result row {} : {}", i, mapToLoggableStringInline(result.get(i).asMap()));
        }
    }

}
