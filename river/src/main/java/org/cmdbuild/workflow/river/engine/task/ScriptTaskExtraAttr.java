/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine.task;

import static com.google.common.base.Strings.nullToEmpty;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.workflow.river.engine.xpdl.XpdlConst.TEXT_JAVA_MIME_TYPE;

public class ScriptTaskExtraAttr {

    private final String scriptContent, scriptType;

    public ScriptTaskExtraAttr(String scriptType, String scriptContent) {
        this.scriptContent = nullToEmpty(scriptContent);
        this.scriptType = firstNotBlank(scriptType, TEXT_JAVA_MIME_TYPE);
    }

    public String getScript() {
        return scriptContent;
    }

    public String getScriptType() {
        return scriptType;
    }

}
