package org.cmdbuild.test.workflow;

import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Iterables.getOnlyElement;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.cmdbuild.test.rest.TestContextProviders.initTomcatAndDb;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
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

@RunWith(CmTestRunner.class)
public class SharkWorkflowIT extends AbstractWsIT {
//
//	private final String ASSET_MGT = "AssetMgt",
//			INCIDENT_MGT = "IncidentMgt";
//
//	private RestClient restClient;
//
//	public static TestContext r2uWithShark() throws Exception {
//		return initTomcatAndDb(DatabaseCreator.R2U_DUMP, true);
//	}
//
//	@Before
//	public void init() {
//		restClient = getRestClient();
//
//		restClient.system().setConfigs("org.cmdbuild.workflow.providers", "shark,river", "org.cmdbuild.workflow.enabled", "true");
//		if (initializedByTomcatManager()) {
//			restClient.system().setConfig("org.cmdbuild.workflow.endpoint", getTomcatManagerForTest().getSharkUrl());
//		}
//		restClient.workflow().uploadPlanVersionAndMigrateProcess(ASSET_MGT, SharkWorkflowIT.class.getResourceAsStream("/org/cmdbuild/test/workflow/AssetMgt.xpdl"), "shark");
//	}
//
//	@Test
//	@Ignore("Issue #826")
//	public void testGetPlanVersionsWithShark() {
//		List<WokflowApi.PlanVersionInfo> planVersions = restClient.workflow().getPlanVersions(ASSET_MGT);
//		assertFalse(planVersions.isEmpty());
//		assertEquals(1, planVersions.stream().filter(PlanVersionInfo::isDefault).count());
//		assertTrue(planVersions.stream().filter((v) -> v.getProvider().equals("shark")).findAny().isPresent());
//	}
//
//	@Test
//	@Ignore("Issue #826")
//	public void testSharkProcess() {
//		logger.info("\n\n\ntestSharkProcess BEGIN\n\n");
//
//		WokflowApi.FlowDataAndStatus flowInfo = restClient.workflow().start(ASSET_MGT, SimpleFlowData.builder().withAttributes(map("Type", "2676")).build()).getFlowData();
//		assertNotNull(flowInfo);
//		String flowCardId = flowInfo.getFlowCardId();
//		assertTrue(!isBlank(flowCardId));
//		logger.info("started process = {}", flowInfo.getFlowCardId());
//
//		List<WokflowApi.TaskInfo> taskList = waitFor(() -> restClient.workflow().getTaskList(ASSET_MGT, flowCardId), (tl) -> !tl.isEmpty());
//		assertEquals(1, taskList.size());
//		String taskId1 = getOnlyElement(taskList).getId();
//		assertNotNull(trimToNull(taskId1));
//
//		restClient.workflow().advance(ASSET_MGT, flowCardId, taskId1, SimpleFlowData.builder().withAttributes(map("GRHeaderAction", "2677", "Supplier", "6034", "Order", "8088")).build());
//
//		waitFor(() -> restClient.workflow().get(ASSET_MGT, flowCardId), (flowData) -> equal("Completed", flowData.getStatus()));
//
//		logger.info("\n\n\ntestProcess END\n\n");
//	}

}
