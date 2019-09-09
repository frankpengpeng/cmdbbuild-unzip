/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.log;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.joran.spi.JoranException;
import com.google.common.eventbus.EventBus;
import java.io.File;
import java.io.StringReader;
import java.util.List;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.StaticLoggerBinder;
import org.xml.sax.InputSource;

/**
 * this is a service used to configure logback loggin system. It's implemented
 * as a java static singleton and not as a spring singleton, so it can be used
 * to set logging config before spring starts.
 */
public class LogbackConfigServiceHelper {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final static LogbackConfigServiceHelper INSTANCE = new LogbackConfigServiceHelper();

    private final EventBus eventBus = new EventBus();

    private LogbackConfigServiceHelper() {
    }

    public static LogbackConfigServiceHelper getInstance() {
        return INSTANCE;
    }

    /**
     * @return event bus to handle {@link LogbackConfigurationReloadedEvent}
     * events
     */
    public EventBus getEventBus() {
        return eventBus;
    }

    public synchronized void configureLogback(String configXml) {
        try {
            logger.info("configure logback");

            LoggerContext loggerContext = getLoggerContext();
            loggerContext.reset();

            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(loggerContext);
            configurator.doConfigure(new InputSource(new StringReader(configXml)));

            eventBus.post(LogbackConfigurationReloadedEvent.INSTANCE);
        } catch (JoranException ex) {
            throw runtime(ex, "error configuring logback");
        }
    }

    public List<File> getLogFiles() {
        try {
            LoggerContext loggerContext = getLoggerContext();
            return loggerContext.getLoggerList().stream().flatMap(rethrowFunction(l -> list(l.iteratorForAppenders()).stream().filter(FileAppender.class::isInstance)
                    .map(FileAppender.class::cast).map(f -> new File(f.getFile())).filter(File::exists).map(rethrowFunction(File::getCanonicalPath))))
                    .sorted().distinct().map(File::new).collect(toList());
        } catch (Exception ex) {
            throw runtime(ex);
        }
    }

    private LoggerContext getLoggerContext() {
        return (LoggerContext) StaticLoggerBinder.getSingleton().getLoggerFactory();
    }

    /**
     * this event is generated whenever the logback configuration is reloaded.
     * It may be used for services that need to register custom
     * loggers/appenders <i>after</i> the static configuration has been loaded
     * from file.
     */
    public static enum LogbackConfigurationReloadedEvent {
        INSTANCE
    }

}
