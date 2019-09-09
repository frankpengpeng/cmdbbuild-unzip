/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine.task.scriptexecutors;

import java.util.Map;

public interface GroovyScriptService {

	GroovyScriptExecutor getScriptExecutor(String taskId, String scriptContent, Iterable<String> scriptParams, Map<String, String> hints);

}
