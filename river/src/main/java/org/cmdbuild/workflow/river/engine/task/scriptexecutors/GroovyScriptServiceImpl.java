/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine.task.scriptexecutors;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;

public class GroovyScriptServiceImpl implements GroovyScriptService {

	private final Cache<String, GroovyScriptExecutor> cache = CacheBuilder.newBuilder().build();//TODO configure cache defaults

	@Override
	public GroovyScriptExecutor getScriptExecutor(String taskId, String scriptContent, Iterable<String> scriptParams, Map<String, String> hints) {
		try {
			return cache.get(taskId, () -> {
				return new GroovyScriptExecutor(scriptContent, scriptParams, hints);
			});
		} catch (ExecutionException ex) {
			throw runtime(ex, "error processing groovy script for task = %s with content = %s", taskId, abbreviate(scriptContent));
		}
	}

}
