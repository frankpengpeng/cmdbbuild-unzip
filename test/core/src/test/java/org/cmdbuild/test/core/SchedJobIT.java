/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.ClassDefinitionImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.jobs.JobData;
import org.cmdbuild.jobs.JobRun;
import org.cmdbuild.jobs.JobService;
import org.cmdbuild.jobs.beans.JobDataImpl;
import org.cmdbuild.jobs.inner.JobRunHelperService;
import org.cmdbuild.test.framework.CmTestRunner;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.prepareTuid;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.tuid;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(CmTestRunner.class)
public class SchedJobIT {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final CacheService cacheService;
    private final JobService jobService;
    private final JobRunHelperService jobRunHelperService;

    public SchedJobIT(DaoService dao, CacheService cacheService, JobService jobService, JobRunHelperService jobRunHelperService) {
        this.dao = checkNotNull(dao);
        this.cacheService = checkNotNull(cacheService);
        this.jobService = checkNotNull(jobService);
        this.jobRunHelperService = checkNotNull(jobRunHelperService);
    }

    @Before
    public void init() {
        prepareTuid();
    }

    @Test
    public void testCustomFunctionJob() {
        Classe myClass = dao.createClass(ClassDefinitionImpl.build(tuid("MyClass")));

        String myFunctionName = tuid("_test_function").toLowerCase();
        dao.getJdbcTemplate().execute(format("CREATE OR REPLACE FUNCTION %s() RETURNS void AS $$ BEGIN INSERT INTO \"%s\" (\"Code\") VALUES ('db_test_ok'); END $$ LANGUAGE PLPGSQL;"
                + "COMMENT ON FUNCTION %s() IS 'TYPE: function';", myFunctionName, myClass.getName(), myFunctionName));
        cacheService.invalidateAll();

        JobData createdJob = jobService.createJob(JobDataImpl.builder()
                .withCode(tuid("ScheduledDbFunctionTest"))
                .withType("scheduled_db_function")
                .withEnabled(false)
                .withConfig(map(
                        "cronExpression", "*/10 * * * *",
                        "function", myFunctionName
                ))
                .build());

        JobRun res = jobRunHelperService.runJob(createdJob);
        assertFalse(res.hasErrors());

        assertEquals("db_test_ok", dao.selectAll().from(myClass).getCard().getCode());
    }

    @Test
    public void testCustomScriptJob() {
        Classe myClass = dao.createClass(ClassDefinitionImpl.build(tuid("MyClass")));

        String script = format("cmdb.newCard(\"%s\").withCode(\"script_test_ok\").create()", myClass.getName());

        JobData createdJob = jobService.createJob(JobDataImpl.builder()
                .withCode(tuid("ScheduledScriptTest"))
                .withType("scheduled_script")
                .withEnabled(false)
                .withConfig(map(
                        "cronExpression", "*/10 * * * *",
                        "script", script
                ))
                .build());

        JobRun res = jobRunHelperService.runJob(createdJob);
        assertFalse(res.hasErrors());

        assertEquals("script_test_ok", dao.selectAll().from(myClass).getCard().getCode());
    }

}
