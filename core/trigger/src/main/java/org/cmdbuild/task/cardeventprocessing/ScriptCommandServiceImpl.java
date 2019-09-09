/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.task.cardeventprocessing;

import java.io.StringReader;
import java.lang.invoke.MethodHandles;
import static java.util.Collections.emptyMap;
import java.util.Map;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleBindings;
import org.cmdbuild.api.fluent.FluentApi;
import org.cmdbuild.event.AfterCardCreateEvent;
import org.cmdbuild.event.AfterCardUpdateEvent;
import org.cmdbuild.event.BeforeCardDeleteEvent;
import org.cmdbuild.event.BeforeCardUpdateEvent;
import org.cmdbuild.event.CardEvent;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ScriptCommandServiceImpl implements ScriptCommandService {

    private static final String CURRENT = "__current__";
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String NEXT = "__next__";
    private static final String PREVIOUS = "__previous__";

    private static final String CMDB = "__cmdb__";
    private static final String CMDB_V1 = "__cmdb_v1__";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ScriptEngineManager scriptEngineManager;

    public ScriptCommandServiceImpl(FluentApi fluentApi) {
        scriptEngineManager = new ScriptEngineManager();
//		map.put("__taskmanagerlogic__",
//				SpringIntegrationUtils.applicationContext().getBean(TaskService.class));
        Bindings bindings = scriptEngineManager.getBindings();
        bindings.put(CMDB, fluentApi);
        bindings.put(CMDB_V1, fluentApi);
    }

    @Override
    public void executeScript(ScriptCommand script, CardEvent event) {
        executeScript(script, contextFromEvent(event));
    }

    @Override
    public void executeScript(ScriptCommand script, Map<String, Object> context) {

        Map<String, Object> map = map(context)
                .with(LOGGER, new MyScriptLogging(script.getId()));

        try {
            ScriptEngine scriptEngine = scriptEngineManager.getEngineByName(script.getEngine());
            scriptEngine.eval(new StringReader(script.getScript()), new SimpleBindings(map));
        } catch (Exception e) {
            throw runtime(e, "error executing script = %s", script);
        }

    }

    private Map<String, Object> contextFromEvent(CardEvent event) {
        if (event instanceof AfterCardCreateEvent) {
            return map(CURRENT, event.getCurrentCard());
        } else if (event instanceof BeforeCardUpdateEvent) {
            return map(CURRENT, event.getCurrentCard(), NEXT, ((BeforeCardUpdateEvent) event).getNextCard());
        } else if (event instanceof AfterCardUpdateEvent) {
            return map(CURRENT, event.getCurrentCard(), PREVIOUS, ((AfterCardUpdateEvent) event).getPreviousCard());
        } else if (event instanceof BeforeCardDeleteEvent) {
            return map(CURRENT, event.getCurrentCard());
        } else {
            return emptyMap();
        }
    }

    public interface ScriptLogging {

        public void info(String msg);

        public void info(String format, Object... arguments);

        public void error(String msg);

        public void error(String format, Object[] arguments);

        public void warn(String msg);

        public void warn(String format, Object[] arguments);

    }

    private class MyScriptLogging implements ScriptLogging {

        private final String prefix;

        public MyScriptLogging(String scriptId) {
            prefix = checkNotBlank(scriptId) + ": ";
        }

        @Override
        public void info(String msg) {
            logger.info("{}{}", prefix, msg);
        }

        @Override
        public void info(String format, Object... arguments) {
            logger.info(prefix + format, arguments);
        }

        @Override
        public void error(String msg) {
            logger.warn("{}{}", prefix, msg);
        }

        @Override
        public void error(String format, Object[] arguments) {
            logger.error(prefix + format, arguments);
        }

        @Override
        public void warn(String msg) {
            logger.warn("{}{}", prefix, msg);
        }

        @Override
        public void warn(String format, Object[] arguments) {
            logger.warn(prefix + format, arguments);
        }

    }

}
