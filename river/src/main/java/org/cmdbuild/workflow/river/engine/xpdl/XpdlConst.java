/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine.xpdl;

/**
 *
 * @author davide
 */
public class XpdlConst {

    public static final String TEXT_JAVA_MIME_TYPE = "text/java",
            EXPR_ENGINE_CONFIG = "CM_EXPR_ENGINE",
            SCRIPT_ENGINE_CONFIG = "CM_SCRIPT_ENGINE",
            SCRIPT_ENGINE_CONFIG_GROOVY = "groovy",
            SCRIPT_ENGINE_CONFIG_BEANSHELL = "beanshell",
            ENABLE_GROOVY_SMART_VARIABLES = "CM_RIVER_GROOVY_SMART_VARIABLES",
            ALWAYS_INITIALIZE_GLOBAL_VARIABLES = "CM_RIVER_ALWAYS_INITIALIZE_GLOBAL_VARIABLES";

    public static final String NEXT_FLAGS_TO_ACTIVATE_SCRIPT_VAR = "river_nextFlagsToActivate";

    public static final String TASK_ATTR_PERFORMER_VALUE = "xpdl.performer.value",
            TASK_ATTR_PERFORMER_TYPE = "xpdl.performer.type",
            TASK_ATTR_ACTIVITY_ID = "xpdl.activityId",
            TASK_ATTR_NAME = "xpdl.name",
            TASK_ATTR_DESCRIPTION = "xpdl.description";

    public static final String TASK_PERFORMER_TYPE_ROLE = "ROLE",
            TASK_PERFORMER_TYPE_EXPR = "EXPR";

    public enum ScriptEngine {
        GROOVY, BEANSHELL
    }

}
