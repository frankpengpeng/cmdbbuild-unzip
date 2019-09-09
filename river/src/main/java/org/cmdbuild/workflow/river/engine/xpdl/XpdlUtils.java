/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine.xpdl;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import java.util.function.Function;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.workflow.river.engine.RiverPlan;
import org.cmdbuild.workflow.river.engine.RiverTask;
import org.cmdbuild.workflow.river.engine.xpdl.XpdlConst.ScriptEngine;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.workflow.river.engine.xpdl.XpdlConst.SCRIPT_ENGINE_CONFIG;

public class XpdlUtils {

	public static ScriptEngine getScriptEngineFromXpdlAttributes(RiverPlan plan, RiverTask task) {
		for (String value : asList(task.getAttr(SCRIPT_ENGINE_CONFIG), plan.getAttOrNull(SCRIPT_ENGINE_CONFIG))) {
			if (!isBlank(value)) {
				return ScriptEngine.valueOf(value.toUpperCase());
			}
		}
		return ScriptEngine.BEANSHELL;
	}

	public static @Nullable
	ScriptEngine getExpressionScriptEngineFromXpdlAttributesOrNull(Function<String, String> planAttrs) {
		String value = planAttrs.apply(SCRIPT_ENGINE_CONFIG);
		if (!isBlank(value)) {
			return ScriptEngine.valueOf(value.toUpperCase());
		} else {
			return null;
		}
	}

	public static String buildUserTaskId(String stepId) {
		return format("%s_user", stepId);
	}

	public static String buildStepIdPrefixFromParentActivityId(String parentActivityId) {
		return format("%s_activityset_", checkNotBlank(parentActivityId));
	}

	public static String buildStepIdFromParentActivityIdAndActivityId(String parentActivityId, String activityId) {
		return buildStepIdPrefixFromParentActivityId(parentActivityId) + checkNotBlank(activityId);
	}

}
