package org.cmdbuild.test.workflow;

import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.MoreCollectors.onlyElement;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.cmdbuild.test.rest.TestContextProviders.initTomcatAndDb;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import static org.cmdbuild.utils.testutils.CmdbTestUtils.waitFor;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.cmdbuild.client.rest.RestClient;
import org.cmdbuild.client.rest.api.WokflowApi;
import org.cmdbuild.client.rest.api.WokflowApi.PlanVersionInfo;
import org.cmdbuild.client.rest.model.SimpleFlowData;
import org.cmdbuild.dao.config.inner.DatabaseCreator;
import org.cmdbuild.test.framework.CmTestRunner;
import org.cmdbuild.test.framework.TestContext;
import org.cmdbuild.test.rest.utils.AbstractWsIT;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(CmTestRunner.class)
public class WorkflowRestIT extends AbstractWsIT {

    private final String ASSET_MGT = "AssetMgt",
            INCIDENT_MGT = "IncidentMgt";

    private RestClient restClient;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static TestContext demoForWf() throws Exception {
        return initTomcatAndDb(DatabaseCreator.DEMO_DUMP);
    }

    @Before
    public void init() throws InterruptedException {
        restClient = getRestClient();
        restClient.system().setConfig("org.cmdbuild.workflow.enabled", "false");
        restClient.system().setConfig("org.cmdbuild.workflow.enabled", "true");
        restClient.system().setConfig("org.cmdbuild.workflow.providers", "river");
        for (int i = 0; i < 20; i++) {
            if (restClient.system().getConfig("org.cmdbuild.workflow.enabled").equals("false")) {
                logger.warn("workflow not enabled, retrying in 1 second");
                restClient.system().setConfig("org.cmdbuild.workflow.enabled", "true");
                Thread.sleep(1000);
            } else {
                assertEquals("true", restClient.system().getConfig("org.cmdbuild.workflow.enabled"));
                restClient.workflow().uploadPlanVersion(ASSET_MGT, WorkflowRestIT.class.getResourceAsStream("/org/cmdbuild/test/workflow/assetmgt.xpdl"));
                return;
            }
        }
    }

    @Test
    public void testWorkflowConfigEnabled() {
        restClient.system().getConfig("org.cmdbuild.workflow.enabled");
        assertEquals("true", restClient.system().getConfig("org.cmdbuild.workflow.enabled"));
    }

    @Test
    public void testWorkflowConfigDisabled1() {
        restClient.system().setConfig("org.cmdbuild.workflow.enabled", "false");
        assertEquals("false", restClient.system().getConfig("org.cmdbuild.workflow.enabled"));
    }

    @Test
    public void testWorkflowConfigProviders() {
        restClient.system().setConfig("org.cmdbuild.workflow.providers", "river");
        assertEquals("river", restClient.system().getConfig("org.cmdbuild.workflow.providers"));
        restClient.system().setConfig("org.cmdbuild.workflow.providers", "shark");
        assertEquals("shark", restClient.system().getConfig("org.cmdbuild.workflow.providers"));
        restClient.system().setConfig("org.cmdbuild.workflow.providers", "river");
    }

    @Test
    public void testPlanVersionUpdate() {
        PlanVersionInfo versionInfo = restClient.workflow().getPlanVersions(ASSET_MGT).stream().filter(PlanVersionInfo::isDefault).collect(onlyElement());
        String ver = versionInfo.getPlanId();

        restClient.workflow().uploadPlanVersion(ASSET_MGT, WorkflowRestIT.class.getResourceAsStream("/org/cmdbuild/test/workflow/assetmgt_var.xpdl"));

        versionInfo = restClient.workflow().getPlanVersions(ASSET_MGT).stream().filter(PlanVersionInfo::isDefault).collect(onlyElement());
        assertTrue(!equal(ver, versionInfo.getPlanId()));
        ver = versionInfo.getPlanId();

        restClient.workflow().uploadPlanVersion(ASSET_MGT, WorkflowRestIT.class.getResourceAsStream("/org/cmdbuild/test/workflow/assetmgt.xpdl"));

        versionInfo = restClient.workflow().getPlanVersions(ASSET_MGT).stream().filter(PlanVersionInfo::isDefault).collect(onlyElement());
        assertTrue(!equal(ver, versionInfo.getPlanId()));
    }

    @Test
    public void testGetPlanVersions() {
        List<WokflowApi.PlanVersionInfo> planVersions = restClient.workflow().getPlanVersions(ASSET_MGT);
        logger.info(planVersions.toString());
        assertFalse(planVersions.isEmpty());
        assertEquals(1, planVersions.stream().filter(PlanVersionInfo::isDefault).count());
    }

    @Test
    public void testGetPlan() {
        assertEquals("true", restClient.system().getConfig("org.cmdbuild.workflow.enabled"));
        WokflowApi.PlanInfo plan = restClient.workflow().getPlan("RequestForChange");
        assertEquals("RequestForChange", plan.getId());
        assertEquals("Request for change", plan.getDescription());
        assertEquals("river", plan.getProvider());
    }

    @Test
    public void testGetTemplate() {
        String xpdlTemplate = restClient.workflow().getXpdlTemplate("RequestForChange");
        assertTrue(xpdlTemplate.contains("ExtendedAttribute Name=\"cmdbuildBindToClass\" Value=\"RequestForChange\""));//TODO
    }

    @Test
    public void testProcess() {

        WokflowApi.FlowDataAndStatus flowInfo = restClient.workflow().start("RequestForChange", SimpleFlowData.builder().withAttributes(map("Requester", 128,
                "RFCDescription", "test_" + randomId(6))).build()).getFlowData();
        assertNotNull(flowInfo);
        String flowCardId = flowInfo.getFlowCardId();
        assertTrue(!isBlank(flowCardId));
        logger.info("started process = {}", flowInfo.getFlowCardId());

        List<WokflowApi.TaskInfo> taskList = restClient.workflow().getTaskList("RequestForChange", flowCardId);
        assertEquals(1, taskList.size());
        String taskId1 = getOnlyElement(taskList).getId();
        assertNotNull(trimToNull(taskId1));
    }

    @Test
    @Ignore
    public void testGroovyProcess() {
        restClient.workflow().uploadPlanVersion(ASSET_MGT, WorkflowRestIT.class.getResourceAsStream("/org/cmdbuild/test/workflow/assetmgt_groovy.xpdl"));

        WokflowApi.FlowDataAndStatus flowInfo = restClient.workflow().start(ASSET_MGT, SimpleFlowData.builder().withAttributes(map("Type", "2676")).build()).getFlowData();
        assertNotNull(flowInfo);
        String flowCardId = flowInfo.getFlowCardId();
        assertTrue(!isBlank(flowCardId));
        logger.info("started process = {}", flowInfo.getFlowCardId());

        List<WokflowApi.TaskInfo> taskList = restClient.workflow().getTaskList(ASSET_MGT, flowCardId);
        assertEquals(1, taskList.size());
        String taskId1 = getOnlyElement(taskList).getId();
        assertNotNull(trimToNull(taskId1));

        restClient.workflow().advance(ASSET_MGT, flowCardId, taskId1, SimpleFlowData.builder().withAttributes(map("GRHeaderAction", "2677", "Supplier", "6034", "Order", "8088")).build());

        waitFor(() -> restClient.workflow().get(ASSET_MGT, flowCardId), (flowData) -> equal("Completed", flowData.getStatus()));
    }

    @Test
    @Ignore
    public void testIncidentMgtProcess() {

        restClient.workflow().uploadPlanVersion(INCIDENT_MGT, WorkflowRestIT.class.getResourceAsStream("/org/cmdbuild/test/workflow/incidentmgt.xpdl"));

        WokflowApi.FlowDataAndStatus flowInfo = restClient.workflow().start("IncidentMgt", SimpleFlowData.builder().withAttributes(map(
                "Requester", 6083,
                "ShortDescr", "test_" + randomId(6),
                "Channel", 3869
        )).build()).getFlowData();

        assertNotNull(flowInfo);
        String flowCardId = flowInfo.getFlowCardId();
        assertTrue(!isBlank(flowCardId));
        logger.info("started process = {}", flowInfo.getFlowCardId());

        {
            List<WokflowApi.TaskInfo> taskList = waitFor(() -> restClient.workflow().getTaskList("IncidentMgt", flowCardId), (tl) -> !tl.isEmpty());
            assertEquals(1, taskList.size());
            String taskId1 = getOnlyElement(taskList).getId();
            assertNotNull(trimToNull(taskId1));

            restClient.workflow().advance("IncidentMgt", flowCardId, taskId1, SimpleFlowData.builder().withAttributes(map(
                    "Category", "6494",
                    "Subcategory", "6506",
                    "Urgency", "3871",
                    "Impact", "3875",
                    "SPClassificAction", "3960",
                    "SPNextRole", "3897",
                    "CurrentUser", "18"
            )).build());
        }

        {
            List<WokflowApi.TaskInfo> taskList = waitFor(() -> restClient.workflow().getTaskList("RequestForChange", flowCardId), (tl) -> !tl.isEmpty());
            assertEquals(1, taskList.size());
            String taskId1 = getOnlyElement(taskList).getId();
            assertNotNull(trimToNull(taskId1));

            restClient.workflow().advance("RequestForChange", flowCardId, taskId1, SimpleFlowData.builder().withAttributes(map(
                    "Outcome", "3880",
                    "Answer", "test_ok",
                    "SPClosureAction", "3975",
                    "CurrentUser", "18"
            )).build());
        }

        waitFor(() -> restClient.workflow().get("RequestForChange", flowCardId), (flowData) -> equal("Completed", flowData.getStatus()));

        logger.info("\n\n\ntestIncidentMgtProcess END\n\n");
    }

    @Test
    @Ignore("Issue #825")
    public void testIncidentMgtProcess2() {
        logger.info("\n\n\ntestIncidentMgtProcess2 BEGIN\n\n");

        restClient.workflow().uploadPlanVersion(INCIDENT_MGT, WorkflowRestIT.class.getResourceAsStream("/org/cmdbuild/test/workflow/incidentmgt.xpdl"));

        WokflowApi.FlowDataAndStatus flowInfo = restClient.workflow().start(INCIDENT_MGT, SimpleFlowData.builder().withAttributes(map(
                "Requester", "6083",
                "ShortDescr", "test_" + randomId(6),
                "OpeningChannel", "3869",
                "TTSPClassification", null
        )).build()).getFlowData();
        assertNotNull(flowInfo);
        String flowCardId = flowInfo.getFlowCardId();
        assertTrue(!isBlank(flowCardId));
        logger.info("started process = {}", flowInfo.getFlowCardId());

        {
            List<WokflowApi.TaskInfo> taskList = waitFor(() -> restClient.workflow().getTaskList(INCIDENT_MGT, flowCardId), (tl) -> !tl.isEmpty());
            assertEquals(1, taskList.size());
            String taskId1 = getOnlyElement(taskList).getId();
            assertNotNull(trimToNull(taskId1));

            restClient.workflow().advance(INCIDENT_MGT, flowCardId, taskId1, SimpleFlowData.builder().withAttributes(map(
                    "Category", "6494",
                    "Subcategory", "6506",
                    "Urgency", "3871",
                    "Impact", "3874",
                    "SPClassificAction", "3960",
                    "SPNextRole", "3897",
                    "CurrentUser", "18",
                    "TTSPClassification", null
            )).build());
        }

        {
            List<WokflowApi.TaskInfo> taskList = waitFor(() -> restClient.workflow().getTaskList(INCIDENT_MGT, flowCardId), (tl) -> !tl.isEmpty());
            assertEquals(1, taskList.size());
            String taskId1 = getOnlyElement(taskList).getId();
            assertNotNull(trimToNull(taskId1));

            restClient.workflow().advance(INCIDENT_MGT, flowCardId, taskId1, SimpleFlowData.builder().withAttributes(map(
                    "Outcome", "3880",
                    "Answer", "test_ok",
                    "SPClosureAction", "3975",
                    "CurrentUser", "18",
                    "TTSPClassification", null
            )).build());
        }

        waitFor(() -> restClient.workflow().get(INCIDENT_MGT, flowCardId), (flowData) -> equal("Completed", flowData.getStatus()));

        logger.info("\n\n\ntestIncidentMgtProcess2 END\n\n");
    }

}
