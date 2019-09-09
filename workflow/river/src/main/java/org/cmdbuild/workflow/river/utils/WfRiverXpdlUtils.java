/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.utils;

import java.util.Map;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import org.cmdbuild.workflow.river.engine.RiverPlan;
import org.cmdbuild.workflow.river.engine.RiverVariableInfo;
import org.cmdbuild.workflow.river.engine.core.PlanImpl;
import static org.cmdbuild.workflow.river.engine.core.VariableInfoImpl.variableInfo;
import org.cmdbuild.workflow.river.engine.xpdl.XpdlParser;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.workflow.model.WorkflowConstants.CURRENT_GROUP_NAME_VARIABLE;
import static org.cmdbuild.workflow.model.WorkflowConstants.CURRENT_PERFORMER_VARIABLE;
import static org.cmdbuild.workflow.model.WorkflowConstants.CURRENT_USER_USERNAME_VARIABLE;
import static org.cmdbuild.workflow.model.WorkflowConstants.CURRENT_USER_VARIABLE;
import static org.cmdbuild.workflow.model.WorkflowConstants.PROCESS_CARD_ID_VARIABLE;
import static org.cmdbuild.workflow.model.WorkflowConstants.PROCESS_CLASSNAME_VARIABLE;
import static org.cmdbuild.workflow.model.WorkflowConstants.PROCESS_INSTANCE_ID_VARIABLE;
import org.cmdbuild.workflow.type.ReferenceType;
import static org.cmdbuild.workflow.river.engine.xpdl.XpdlConst.ALWAYS_INITIALIZE_GLOBAL_VARIABLES;
import org.cmdbuild.workflow.model.PlanInfoImpl;
import static org.cmdbuild.workflow.river.WfRiverUtils.RIVER_FAKE_PACKAGE_VERSION;
import static org.cmdbuild.workflow.river.WfRiverUtils.RIVER_FAKE_PACKAGE;

public class WfRiverXpdlUtils {

	private final static Map<String, RiverVariableInfo<?>> CMDBUILD_EXTENDED_GLOBAL_VARIABLES = map(CmCollectionUtils.<RiverVariableInfo<?>>list(
			variableInfo(PROCESS_CARD_ID_VARIABLE, Long.class),
			variableInfo(PROCESS_CLASSNAME_VARIABLE, String.class),
			variableInfo(PROCESS_INSTANCE_ID_VARIABLE, String.class),
			variableInfo(CURRENT_USER_USERNAME_VARIABLE, String.class),
			variableInfo(CURRENT_GROUP_NAME_VARIABLE, String.class),
			variableInfo(CURRENT_USER_VARIABLE, ReferenceType.class),
			variableInfo(CURRENT_PERFORMER_VARIABLE, ReferenceType.class)
	), RiverVariableInfo::getKey).immutable();

	public static RiverPlan parseXpdlForCmdb(String xpdl) {
		RiverPlan riverPlan = new XpdlParser()
				.withDefaultAttributes(map(ALWAYS_INITIALIZE_GLOBAL_VARIABLES, TRUE))
				.parseXpdl(xpdl);

		riverPlan = PlanImpl.copyOf(riverPlan)
				.withGlobals(map(CMDBUILD_EXTENDED_GLOBAL_VARIABLES).with(riverPlan.getGlobalVariables()))
				.build();

		return riverPlan;
	}

	public static String riverPlanIdToLegacyUniqueProcessDefinition(String planId) {
		return riverPlanIdToPlanInfo(planId).getPlanId();
	}

	public static PlanInfoImpl riverPlanIdToPlanInfo(String planId) {
		return new PlanInfoImpl(RIVER_FAKE_PACKAGE, RIVER_FAKE_PACKAGE_VERSION, planId);
	}
}
