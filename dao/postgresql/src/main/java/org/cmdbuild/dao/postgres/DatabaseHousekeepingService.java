/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.config.CoreConfiguration;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.scheduler.ScheduledJob;
import static org.cmdbuild.scheduler.JobClusterMode.CM_RUN_ON_SINGLE_NODE;
import org.cmdbuild.services.PostStartup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DatabaseHousekeepingService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CoreConfiguration config;
    private final DaoService dao;

    public DatabaseHousekeepingService(CoreConfiguration coreConfiguration, DaoService dao) {
        this.dao = checkNotNull(dao);
        this.config = checkNotNull(coreConfiguration);
    }

    @PostStartup
    public void runDatabaseHousekeepingAtStartup() {
        if (config.runDatabaseHousekeepingFunctionAtStartup()) {
            doRunDatabaseHousekeeping();
        }
    }

    @ScheduledJob(value = "0 0 4 * * ?", clusterMode = CM_RUN_ON_SINGLE_NODE) //run every day at 4 am
    public void runDatabaseHousekeepingJob() {
        doRunDatabaseHousekeeping();
    }

    private synchronized void doRunDatabaseHousekeeping() {
        logger.info("execute database housekeeping function");
        try {
            dao.getJdbcTemplate().queryForObject("SELECT _cm3_system_housekeeping()", Object.class);
            logger.debug("database housekeeping completed");
        } catch (Exception ex) {
            logger.error("database housekeeping error", ex);
        }
    }

}
