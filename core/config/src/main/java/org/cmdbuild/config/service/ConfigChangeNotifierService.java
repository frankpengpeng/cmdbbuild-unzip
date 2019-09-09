/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.service;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableSet;
import com.google.common.eventbus.Subscribe;
import static java.util.Collections.unmodifiableSet;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.tuple.Triple;
import org.cmdbuild.config.api.ConfigListener;
import org.cmdbuild.config.api.ConfigReloadEvent;
import org.cmdbuild.config.api.GlobalConfigService;
import static org.cmdbuild.config.utils.ConfigBeanUtils.getNamespace;
import org.cmdbuild.services.AppContextReadyEvent;
import org.cmdbuild.services.SystemStatus;
import org.cmdbuild.system.SystemEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.cmdbuild.services.SystemService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@Component
public class ConfigChangeNotifierService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final ConfigListenerBeansRepository repository;
	private final SystemService systemStatusService;
	private final ApplicationContext applicationContext;

	private final List<Triple<String, Set<SystemStatus>, ConfigListenerBean>> configListeners = list();

	public ConfigChangeNotifierService(SystemService systemStatusService, GlobalConfigService configService, ConfigListenerBeansRepository repository, ApplicationContext applicationContext, SystemEventService systemEventService) {
		this.repository = checkNotNull(repository);
		this.applicationContext = checkNotNull(applicationContext);
		this.systemStatusService = checkNotNull(systemStatusService);
		systemEventService.getEventBus().register(new Object() {
			@Subscribe
			public void handleAppContextReadyEvent(AppContextReadyEvent event) {
				initConfigListeners();
			}

		});
		configService.getEventBus().register(new Object() {
			@Subscribe
			public void handleConfigReloadEvent(ConfigReloadEvent event) {
				processConfigReloadEvent(event);
			}
		});
	}

	private void initConfigListeners() {
		logger.info("init {} config change listeners", repository.getConfigListeners().size());
		repository.getConfigListeners().forEach(this::initConfigListener);
	}

	private void processConfigReloadEvent(ConfigReloadEvent event) {
		SystemStatus systemStatus = systemStatusService.getSystemStatus();
		configListeners.stream().filter((l) -> event.impactNamespace(l.getLeft())).filter(l -> l.getMiddle().contains(systemStatus)).forEach((l) -> {
			try {
				logger.debug("trigger config listener = {}", l.getRight());
				l.getRight().notifyUpdate();
			} catch (Exception ex) {
				logger.error("error sending config event update for event = {} to listener = {}", event, l, ex);
			}
		});
	}

	private void initConfigListener(ConfigListenerBean bean) {
		try {
			ConfigListener annotation = bean.getAnnotation();
			Object configBean = applicationContext.getBean(annotation.value());
			String namespace = getNamespace(configBean);
			logger.debug("init config listener = {} for namespace = {}", bean, namespace);
			Set<SystemStatus> requireSystemStatus;
			if (annotation.requireSystemStatus() == null || annotation.requireSystemStatus().length == 0) {
				requireSystemStatus = unmodifiableSet(EnumSet.allOf(SystemStatus.class));
			} else {
				requireSystemStatus = ImmutableSet.copyOf(annotation.requireSystemStatus());
			}
			configListeners.add(Triple.of(namespace, requireSystemStatus, bean));
		} catch (Exception ex) {
			logger.error("error configuring config listener bean = {}", bean, ex);
		}
	}
}
