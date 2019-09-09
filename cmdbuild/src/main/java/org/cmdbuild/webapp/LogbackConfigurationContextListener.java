package org.cmdbuild.webapp;

import org.cmdbuild.webapp.services.WebappDirectoryServiceImpl;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.cmdbuild.config.api.DirectoryService;
import org.cmdbuild.log.LogbackConfigStore;
import org.cmdbuild.log.LogbackConfigServiceHelper;
import org.cmdbuild.log.LogbackConfigStoreImpl;
import org.cmdbuild.log.LogbackConfigServiceImpl;
import org.slf4j.LoggerFactory;
import org.cmdbuild.common.log.LoggersConfigService;

public class LogbackConfigurationContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try {
            DirectoryService directoryService = new WebappDirectoryServiceImpl(servletContextEvent.getServletContext());
            LogbackConfigStore logbackConfigStore = new LogbackConfigStoreImpl(directoryService);
            LoggersConfigService configService = new LogbackConfigServiceImpl(logbackConfigStore, directoryService);

            String config = configService.getConfigFileContent();

            LogbackConfigServiceHelper.getInstance().configureLogback(config);
            LoggerFactory.getLogger(getClass()).info("logger ready");
        } catch (Exception ex) {
            System.err.println("error loading logback configuration");
            ex.printStackTrace(System.err);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        //nothing to do
    }

}
