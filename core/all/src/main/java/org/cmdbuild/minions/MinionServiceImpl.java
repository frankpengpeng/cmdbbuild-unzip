/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.minions;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.MoreCollectors.toOptional;
import com.google.common.eventbus.Subscribe;
import java.lang.reflect.Method;
import java.util.Collection;
import static java.util.Collections.unmodifiableCollection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.config.api.AfterConfigReloadEvent;
import org.cmdbuild.config.api.GlobalConfigService;
import org.cmdbuild.config.api.NamespacedConfigService;
import org.cmdbuild.services.AppContextReadyEvent;
import org.cmdbuild.services.Minion;
import org.cmdbuild.services.MinionComponent;
import org.cmdbuild.services.MinionConfig;
import org.cmdbuild.services.MinionService;
import org.cmdbuild.services.MinionStatus;
import org.cmdbuild.services.SystemStartedServicesEvent;
import org.cmdbuild.system.SystemEventService;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmReflectionUtils.executeMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@Component
public class MinionServiceImpl implements MinionService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final GlobalConfigService configService;
	private final Map<String, MinionImpl> minions = map();

	public MinionServiceImpl(MinionBeanRepository repository, GlobalConfigService configService, SystemEventService systemEventService) {
		this.configService = checkNotNull(configService);
		checkNotNull(repository);
		systemEventService.getEventBus().register(new Object() {
			@Subscribe
			public void handleAppContextReadyEvent(AppContextReadyEvent event) {
				try {
					List<InnerBean> beans = repository.getMinionBeans();
					logger.info("loading {} system services", beans.size());
					beans.forEach((innerBean) -> {
						try {
							MinionImpl minion = new MinionImpl(innerBean.getBean());
							minions.put(minion.getId(), minion);
						} catch (Exception ex) {
							throw runtime("error processing system service bean = {} ( {} )", innerBean.getName(), innerBean.getBean(), ex);
						}
					});
				} catch (Exception ex) {
					logger.error("error loading system services", ex);//TODO propagate error
				}
			}

			@Subscribe
			public void handleSystemStartedServicesEvent(SystemStartedServicesEvent event) {
				minions.values().forEach((m) -> {
					try {
						m.refreshStatus();
					} catch (Exception ex) {
						logger.error("error loading service status for service = {}", m, ex);
					}
				});
			}

		});

	}

	@Override
	public Collection<Minion> getMinions() {
		return unmodifiableCollection(minions.values());
	}

	@Override
	public Minion getMinion(String id) {
		return checkNotNull(minions.get(checkNotBlank(id)), "service not found for id = %s", id);
	}

	private final class MinionImpl implements Minion {

		private final String name;
		private final Consumer<Boolean> runSet;
		private final Supplier<MinionStatus> statusSupplier;
		private final Supplier<Boolean> enabled;
		private MinionStatus status;

		public MinionImpl(Object innerBean) {
			MinionComponent annotation = checkNotNull(innerBean.getClass().getAnnotation(MinionComponent.class));
			name = firstNotBlank(annotation.name(), annotation.value());

			Method statusMethod = list(innerBean.getClass().getMethods()).stream().filter(m -> m.getParameterCount() == 0 && MinionStatus.class.isAssignableFrom(m.getReturnType())).collect(toOptional()).orElse(null),
					configMethod = list(innerBean.getClass().getMethods()).stream().filter(m -> m.getParameterCount() == 0 && MinionConfig.class.isAssignableFrom(m.getReturnType())).collect(toOptional()).orElse(null);

			checkNotNull(statusMethod, "missing status method");
			statusSupplier = () -> checkNotNull(executeMethod(innerBean, statusMethod));

			String configNamespace = null;
			if (isNotBlank(annotation.config())) {
				configNamespace = annotation.config();
			} else if (!equal(Void.class, annotation.configBean())) {
				configNamespace = configService.getConfigNamespaceFromConfigBeanClass(annotation.configBean());
			}
			checkNotBlank(configNamespace, "missing namespace config");
			NamespacedConfigService config = configService.getConfig(configNamespace);
			config.getEventBus().register(new Object() {
				@Subscribe
				public void handleAfterConfigReloadEvent(AfterConfigReloadEvent event) {
					refreshStatus();
				}
			});
			runSet = (b) -> configService.putString(config.getNamespace(), "run", Boolean.toString(b));
			enabled = () -> {
				if (configMethod != null) {
					MinionConfig mc = (MinionConfig) checkNotNull(executeMethod(innerBean, configMethod));
					switch (mc) {
						case MC_ENABLED:
							return true;
						case MC_DISABLED:
							return false;
						default:
							throw new UnsupportedOperationException("unsupported minion config value = " + mc);

					}
				} else {
					return toBooleanOrDefault(config.getStringOrDefault("enabled"), true);
				}
			};
		}

		@Override
		public String toString() {
			return "MinionImpl{" + "name=" + name + '}';
		}

		public void refreshStatus() {
			status = statusSupplier.get();
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public MinionStatus getStatus() {
			return checkNotNull(status, "service status not available");
		}

		@Override
		public void startService() {
			runSet.accept(true);
			refreshStatus();
		}

		@Override
		public void stopService() {
			runSet.accept(false);
			refreshStatus();
		}

		@Override
		public boolean isEnabled() {
			return enabled.get();
		}

	}
}
