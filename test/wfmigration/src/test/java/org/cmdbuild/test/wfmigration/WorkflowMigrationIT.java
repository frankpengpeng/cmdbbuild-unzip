/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.wfmigration;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import java.util.List;
import java.util.Map;
import org.cmdbuild.auth.login.LoginDataImpl;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.config.api.GlobalConfigService;
import org.cmdbuild.dao.beans.IdAndDescriptionImpl;
import org.cmdbuild.dao.config.DatabaseConfiguration;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.test.framework.CmTestRunner;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.prepareTuid;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDate;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTimeUtc;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableString;
import static org.cmdbuild.workflow.WorkflowCommonConst.RIVER;
import org.cmdbuild.workflow.WorkflowService;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.type.ReferenceType;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(CmTestRunner.class)
public class WorkflowMigrationIT {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final WorkflowService workflowService;
    private final GlobalConfigService configService;
    private final DatabaseConfiguration databaseConfiguration;
    private final SessionService sessionService;

    public WorkflowMigrationIT(DaoService dao, WorkflowService workflowService, GlobalConfigService configService, DatabaseConfiguration databaseConfiguration, SessionService sessionService) {
        this.dao = checkNotNull(dao);
        this.workflowService = checkNotNull(workflowService);
        this.configService = checkNotNull(configService);
        this.databaseConfiguration = checkNotNull(databaseConfiguration);
        this.sessionService = checkNotNull(sessionService);
    }

    @Before
    public void init() {
        prepareTuid();
        configService.putStrings(map(
                "org.cmdbuild.workflow.enabled", "true",
                "org.cmdbuild.workflow.providers", "river",
                "org.cmdbuild.workflow.shark.db.url", databaseConfiguration.getDatabaseUrl(),
                "org.cmdbuild.workflow.shark.db.username", "shark",
                "org.cmdbuild.workflow.shark.db.password", "shark"
        ));
        sessionService.createAndSet(LoginDataImpl.buildNoPasswordRequired("admin"));
    }

    @Test
    public void testWfMigration() {
        workflowService.migrateFlowInstancesToNewProviderWithExistingXpdl("migrationProcess");

        workflowService.getUserFlowCardsByClasseId("migrationProcess").forEach(f -> {

            assertEquals(RIVER, f.getType().getMetadata().getFlowProviderOrNull());

            logger.info("flow = {} data =\n\n{}\n", f, mapToLoggableString(f.getAllValuesAsMap()));
        });

        {
            Flow card = workflowService.getFlowCard("migrationProcess", 1543l);
            Map<String, Object> data = workflowService.getAllFlowData("migrationProcess", 1543l);

            ReferenceType referenceType = (ReferenceType) getOnlyElement(convert(data.get("EmployeeList"), List.class));
            assertEquals(134l, referenceType.getId());
            assertEquals("Employee", referenceType.getClassName());
            assertEquals("10", referenceType.getCode());
            assertEquals("Taylor William", referenceType.getDescription());

            List<String> definitionId = convert(card.get("ActivityDefinitionId"), List.class);
            assertEquals("BloccoActivityParallele_activityset_Step3a1_user", definitionId.get(0));
            assertEquals("BloccoActivityParallele_activityset_Step3b1_user", definitionId.get(1));

            assertEquals(false, data.get("BooleanField"));
            assertEquals("2019-06-07", toIsoDate(data.get("DateField")));
            assertEquals(0, data.get("EmployeeNumber"));
            assertEquals(null, data.get("TimeField"));
            assertEquals(null, data.get("TimestampField"));
        }

        {
            Flow card = workflowService.getFlowCard("migrationProcess", 1550l);
            Map<String, Object> data = workflowService.getAllFlowData("migrationProcess", 1550l);

            List<String> definitionId = convert(card.get("ActivityDefinitionId"), List.class);
            assertEquals("Step4_user", definitionId.get(0));
            assertEquals("65", ((IdAndDescriptionImpl) card.get("CountryLookupField")).getId().toString());

            assertEquals(false, data.get("BooleanField"));
            assertEquals("2019-06-07", toIsoDate(data.get("DateField")));
            assertEquals(0, data.get("EmployeeNumber"));
        }

        {
            Flow card = workflowService.getFlowCard("migrationProcess", 1561l);
            logger.error("taskList" + workflowService.getTaskList(card));
            workflowService.updateProcessWithOnlyTask("migrationProcess", 1561l, map(), true);

            card = workflowService.getFlowCard("migrationProcess", 1561l);
            List<String> definitionId = convert(card.get("ActivityDefinitionId"), List.class);
            assertEquals("BloccoActivityParallele_activityset_Step3a1_user", definitionId.get(0));
            assertEquals("BloccoActivityParallele_activityset_Step3b1_user", definitionId.get(1));

            Map<String, Object> data = workflowService.getAllFlowData("migrationProcess", 1561l);
            assertEquals(true, data.get("BooleanField"));
            assertEquals("2019-06-07", toIsoDate(data.get("DateField")));
            assertEquals(0, data.get("EmployeeNumber"));
            assertEquals(null, data.get("TimeField"));
            assertEquals(null, data.get("TimestampField"));
        }

        {
            Flow card = workflowService.getFlowCard("migrationProcess", 1574l);
            workflowService.updateProcessWithOnlyTask("migrationProcess", 1574l, map(), true);
            Map<String, Object> data = workflowService.getAllFlowData("migrationProcess", 1574l);

            List<String> definitionId = convert(card.get("ActivityDefinitionId"), List.class);
            logger.error("" + definitionId);
            assertEquals(toIsoDateTimeUtc("2019-06-10T12:44:27.08Z"), toIsoDateTimeUtc(data.get("TimestampFieldProcess")));
            assertEquals("00:00:00", data.get("TimeFieldProcess"));
        }

        {
            Map<String, Object> data = workflowService.getAllFlowData("migrationProcess", 1585l);

            assertEquals("test string process", data.get("StringFieldProcess"));
            assertEquals(true, data.get("BooleanFieldProcess"));
            assertEquals(1.90, data.get("FloatFieldProcess"));
            assertEquals(100l, data.get("IntegerFieldProcess"));
        }

    }

}
