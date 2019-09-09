/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine.task.scriptexecutors;

import static com.google.common.base.Preconditions.checkNotNull;
import groovy.lang.GroovyClassLoader;
import java.util.Collection;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import static org.cmdbuild.workflow.river.engine.xpdl.XpdlConst.ENABLE_GROOVY_SMART_VARIABLES;
import static org.cmdbuild.workflow.river.engine.xpdl.XpdlConst.NEXT_FLAGS_TO_ACTIVATE_SCRIPT_VAR;
import static org.cmdbuild.utils.hash.CmHashUtils.hash;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroovyScriptExecutor {//TODO move this in its own utility project

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final MyGroovyScript compiledScript;
    private final Collection<String> paramNames;

    public GroovyScriptExecutor(String scriptContent, Iterable<String> globalParameters, Map<String, String> hints) {
        logger.info("create groovy script executor for script = \n\n{}\n\n", scriptContent);

        Stream<String> stream = set(globalParameters).with(NEXT_FLAGS_TO_ACTIVATE_SCRIPT_VAR).stream();

        if (toBooleanOrDefault(hints.get(ENABLE_GROOVY_SMART_VARIABLES), false)) {
            stream = stream.filter((key) -> {
                return scriptContent.contains(key);//TODO warning, this won't find vars used by scripts or other; TODO: add script hint to enable/disable this feature
            });
        }

        paramNames = stream.sorted().collect(toList());

        String groovyClassScript;
        try {
            groovyClassScript = new GroovyScriptParser(scriptContent, paramNames).buildGroovyClassScript();
        } catch (Exception ex) {
            throw new WorkflowScriptProcessingException(ex, "error building groovy script");
        }

        logger.debug("compiling groovy script = \n\n{}\n\n", groovyClassScript);

        GroovyClassLoader groovyClassLoader = new GroovyClassLoader();
        Class<MyGroovyScript> groovyClass = checkNotNull(groovyClassLoader.parseClass(groovyClassScript, hash(scriptContent)));
        try {
            compiledScript = groovyClass.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            logger.error("error compiling groovy script from class script = \n\n{}\n", groovyClassScript);
            throw new WorkflowScriptProcessingException(ex, "error compiling groovy script");
        }
    }

    public Map<String, Object> execute(Map<String, Object> dataIn) {
        Map<String, Object> dataOut = map();
        compiledScript.execute(dataIn, dataOut);
        return map(dataIn).with(dataOut);
    }

    public interface MyGroovyScript {

        public void execute(Map<String, Object> dataIn, Map<String, Object> dataOut);
    }
}
