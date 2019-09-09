/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.webapp;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.cmdbuild.services.SystemService;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class ServletContextShutdownListener implements ServletContextListener {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		//do nothing
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		logger.info("received context destroyed event");
		try {
			WebApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(event.getServletContext());
			SystemService systemService = applicationContext.getBean(SystemService.class);
			systemService.stopSystem();
		} catch (Exception ex) {
			logger.error("error processing context destroyed event", ex);
			throw runtime(ex);
		}
	}

}
