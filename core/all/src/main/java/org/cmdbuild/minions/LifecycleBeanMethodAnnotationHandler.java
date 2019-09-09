/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.minions;

import org.cmdbuild.services.PreShutdown;
import org.cmdbuild.services.PostStartup;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.Subscribe;
import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.cmdbuild.services.SystemStartingServicesEvent;
import org.cmdbuild.services.SystemStoppingServicesEvent;
import org.cmdbuild.system.SystemEventService;

@Component
public class LifecycleBeanMethodAnnotationHandler implements BeanPostProcessor {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final SystemEventService systemEventService;

	public LifecycleBeanMethodAnnotationHandler(SystemEventService systemEventService) {
		this.systemEventService = checkNotNull(systemEventService);
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean; // nothing to do
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		ReflectionUtils.doWithMethods(bean.getClass(), (Method method) -> {
			if (method.isAnnotationPresent(PostStartup.class)) {
				logger.info("register method {}#{} for PostStartup hook", beanName, method.getName());
				systemEventService.getEventBus().register(new Object() {
					@Subscribe
					public void handleSystemStartingServicesEvent(SystemStartingServicesEvent event) {
						logger.info("run PostStartup method {}#{}", beanName, method.getName());
						try {
							method.invoke(bean);
						} catch (Exception ex) {
							logger.error("error invoking PostStartup method {}#{}", beanName, method.getName(), ex);
						}
					}
				});
			}
			if (method.isAnnotationPresent(PreShutdown.class)) {
				logger.info("register method {}#{} for PreShutdown hook", beanName, method.getName());
				systemEventService.getEventBus().register(new Object() {
					@Subscribe
					public void handleSystemStoppingServicesEvent(SystemStoppingServicesEvent event) {
						logger.info("run PreShutdown method {}#{}", beanName, method.getName());
						try {
							method.invoke(bean);
						} catch (Exception ex) {
							logger.error("error invoking PreShutdown method {}#{}", beanName, method.getName(), ex);
						}
					}
				});
			}
		});
		return bean;

	}

}
