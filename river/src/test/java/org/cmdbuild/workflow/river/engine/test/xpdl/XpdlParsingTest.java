/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine.test.xpdl;

import org.cmdbuild.workflow.river.engine.xpdl.XpdlParser;
import static com.google.common.base.Strings.emptyToNull;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.cmdbuild.workflow.river.engine.utils.PlanToDotGraphPlotter;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.workflow.river.engine.RiverPlan;
import org.cmdbuild.workflow.river.engine.RiverTaskType;
import org.cmdbuild.workflow.river.engine.core.Step;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author davide
 */
public class XpdlParsingTest {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private String requestForChangeXpdlContent,
			assetMgtXpdlContent,
			complexProcessOneXpdlContent,
			incidentMgtXpdlContent;

	@Before
	public void init() throws IOException {
		requestForChangeXpdlContent = IOUtils.toString(getClass().getResourceAsStream("/RequestForChange.xpdl"));
		assetMgtXpdlContent = IOUtils.toString(getClass().getResourceAsStream("/AssetMgt.xpdl"));
		complexProcessOneXpdlContent = IOUtils.toString(getClass().getResourceAsStream("/ComplexProcessOne.xpdl"));
		incidentMgtXpdlContent = IOUtils.toString(getClass().getResourceAsStream("/IncidentMgt.xpdl"));
	}

	@Test
	public void testRequestForChangeParsing() {
		RiverPlan plan = XpdlParser.parseXpdlWithDefaultOptions(requestForChangeXpdlContent);
		assertNotNull(plan);
		assertEquals("RequestForChange", plan.attributes().get("cmdbuildBindToClass"));
	}

	@Test
	public void testAssetMgtParsing() {
		RiverPlan plan = XpdlParser.parseXpdlWithDefaultOptions(assetMgtXpdlContent);
		assertNotNull(plan);
		assertEquals("AssetMgt", plan.attributes().get("cmdbuildBindToClass"));
	}

	@Test
	public void testGestioneTickerParsing() {
		RiverPlan plan = XpdlParser.parseXpdlWithDefaultOptions(readToString(getClass().getResourceAsStream("/GestioneTicket_44.xpdl")));
		assertNotNull(plan);
		assertEquals("GestioneTicket", plan.attributes().get("cmdbuildBindToClass"));
	}

	@Test
	public void testIncidentMgtParsing() {
		RiverPlan plan = XpdlParser.parseXpdlWithDefaultOptions(incidentMgtXpdlContent);
		assertNotNull(plan);
		assertEquals("IncidentMgt", plan.attributes().get("cmdbuildBindToClass"));

		logger.debug("entry point steps = {}", plan.getEntryPointStepIds());
		assertFalse(plan.getEntryPointStepIds().contains("Process_incidentmgt_act1"));
		assertTrue(plan.getEntryPointStepIds().contains("IM02-HDOpening"));
	}

	@Test
	public void testAssetMgtInlineFieldAttrParsing() {
		RiverPlan plan = XpdlParser.parseXpdlWithDefaultOptions(assetMgtXpdlContent);
		assertNotNull(plan);
		Step step = plan.getStepById("SYS01-SetOpeningData");
		assertEquals(RiverTaskType.SCRIPT_INLINE, step.getTask().getTaskType());
	}

	@Test
	public void testComplexProcessOneParsing() {
		RiverPlan plan = XpdlParser.parseXpdlWithDefaultOptions(complexProcessOneXpdlContent);
		assertNotNull(plan);
	}

	@Test
	public void testAssetMgtPlotting() {
		RiverPlan plan = XpdlParser.parseXpdlWithDefaultOptions(assetMgtXpdlContent);
		assertNotNull(plan);

		String dotGraph = PlanToDotGraphPlotter.planToDotGraph(plan);
		assertNotNull(emptyToNull(dotGraph));

		logger.debug("dot graph = \n\n{}\n", dotGraph);
	}

	@Test
	public void testRequestForChangePlotting() {
		RiverPlan plan = XpdlParser.parseXpdlWithDefaultOptions(requestForChangeXpdlContent);
		assertNotNull(plan);

		String dotGraph = PlanToDotGraphPlotter.planToDotGraph(plan);
		assertNotNull(emptyToNull(dotGraph));

		logger.debug("dot graph = \n\n{}\n", dotGraph);
	}

	@Test
	public void testComplexProcessOnePlotting() {
		RiverPlan plan = XpdlParser.parseXpdlWithDefaultOptions(complexProcessOneXpdlContent);
		assertNotNull(plan);

		String dotGraph = PlanToDotGraphPlotter.planToDotGraph(plan);
		assertNotNull(emptyToNull(dotGraph));

		logger.debug("dot graph = \n\n{}\n", dotGraph);
	}

}
