/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.eventbus.Subscribe;
import static java.lang.String.format;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.cmdbuild.config.api.ConfigBeanRepository.ConfigServiceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigService;
import org.cmdbuild.config.api.ConfigUpdateEvent;
import org.cmdbuild.config.api.ConfigListener;
import org.cmdbuild.config.api.GlobalConfigService;
import org.cmdbuild.config.api.NamespacedConfigService;
import static org.cmdbuild.config.utils.ConfigBeanUtils.getNamespace;
import static org.cmdbuild.config.utils.ConfigDefinitionUtils.parseBeanForConfigDefinitions;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmReflectionUtils.executeMethod;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@Component
public class ConfigAnnotationProcessorService implements BeanPostProcessor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ConfigListenerBeansRepositoryImpl configListenerBeansRepository;
    private final ConfigBeanRepositoryImpl repository;

    public ConfigAnnotationProcessorService(ConfigBeanRepositoryImpl repository, ConfigListenerBeansRepositoryImpl configListenerBeansRepository) {
        this.repository = checkNotNull(repository);
        this.configListenerBeansRepository = checkNotNull(configListenerBeansRepository);
        logger.debug("ready");
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(ConfigComponent.class)) {
            repository.addBean(new ConfigServiceHelperImpl(bean, beanName));
        }
        scanForConfigListenerMethods(bean);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    private void scanForConfigListenerMethods(Object bean) {
        list(bean.getClass().getMethods()).stream().filter(m -> m.getAnnotation(ConfigListener.class) != null).forEach(m -> {
            configureConfigListener(bean, m);
        });
    }

    private void configureConfigListener(Object bean, Method method) {
        checkArgument(method.getParameterCount() == 0, "unsupported ConfigListener annotation usage on method = %s with non-null param count", method);
        configListenerBeansRepository.addBean(new ConfigListenerHelperImpl(format("%s.%s", method.getDeclaringClass().getName(), method.getName()), method.getAnnotation(ConfigListener.class), () -> executeMethod(bean, method)));
    }

    private static class ConfigListenerHelperImpl implements ConfigListenerBean {

        private final String description;
        private final ConfigListener annotation;
        private final Runnable callback;

        public ConfigListenerHelperImpl(String description, ConfigListener annotation, Runnable callback) {
            this.description = checkNotBlank(description);
            this.annotation = checkNotNull(annotation);
            this.callback = checkNotNull(callback);
        }

        @Override
        public ConfigListener getAnnotation() {
            return annotation;
        }

        @Override
        public void notifyUpdate() {
            callback.run();
        }

        @Override
        public String toString() {
            return "ConfigListenerHelperImpl{" + "description=" + description + '}';
        }

    }

    private class ConfigServiceHelperImpl implements ConfigServiceHelper {

        private final Object bean;
        private final String beanName;
        private NamespacedConfigService localConfigService;
        private String namespace;

        private final Map<String, Field> fieldsToAutowire = map();
        private final Map<String, Method> methodsToAutowire = map();

        public ConfigServiceHelperImpl(Object bean, String beanName) {
            this.bean = checkNotNull(bean);
            this.beanName = checkNotNull(beanName);
        }

        @Override
        public void processBean(GlobalConfigService configAccessService) {
            logger.trace("processing bean {} {}", beanName, bean);
            namespace = getNamespace(bean);
//            repository.addBean(bean);
            logger.debug("autowiring config in bean = {} with namespace = {}", beanName, namespace);

            localConfigService = configAccessService.getConfig(namespace);

            parseMethodsAndFieldsAndLoadDefaults();

            loadConfigs();//load defaults

            localConfigService.getEventBus().register(new Object() {

                @Subscribe
                public void handleConfigUpdateEvent(ConfigUpdateEvent event) {
                    loadConfigs();
                }
            });
            localConfigService.getEventBus().register(bean);

//			helperService.getEventBus().register(new Object() {
//
//				@Subscribe
//				public void handleConfigServiceReadyEvent(ConfigAccessServiceReadyEvent event) {
//					localConfigService.setConfigServiceAndRegisterEventBus(event.getConfigStore(), event.getConfigAccessService().getEventBus());
//				}
//
//			});
        }

        @Override
        public Object getBean() {
            return bean;
        }

        private void parseMethodsAndFieldsAndLoadDefaults() {
            parseBeanForConfigDefinitions(bean.getClass(), localConfigService::addDefinition, (d, f) -> {
                logger.debug("autowiring field = {}.{} for key = {}", beanName, f.getName(), d.getKey());
                fieldsToAutowire.put(d.getKey(), f);
            }, (d, m) -> {
                logger.debug("autowiring method = {}.{} for key = {}", beanName, m.getName(), d.getKey());
                methodsToAutowire.put(d.getKey(), m);
            });
            ReflectionUtils.doWithFields(bean.getClass(), (field) -> {
                if (field.isAnnotationPresent(ConfigService.class)) {
                    logger.debug("autowiring local config service field = {}.{} for namespace = {}", beanName, field.getName(), namespace);
                    FieldUtils.writeField(field, bean, localConfigService, true);
                }
            });
        }

        private void loadConfigs() {
            try {
                fieldsToAutowire.entrySet().forEach((entry) -> {
                    Field field = entry.getValue();
                    String key = entry.getKey();
                    Object value = localConfigService.getOrDefault(key, field.getType());
                    logger.debug("set config field = {}.{} to value = {} for key = {}", beanName, field.getName(), value, key);
                    if (value instanceof Set) {
                        value = ImmutableSet.copyOf((Set) value);
                    } else if (value instanceof Iterable) {
                        value = ImmutableList.copyOf((Iterable) value);
                    }
                    try {
                        FieldUtils.writeField(field, bean, value, true);
                    } catch (IllegalArgumentException | IllegalAccessException ex) {
                        throw new RuntimeException(ex);
                    }
                });
                methodsToAutowire.entrySet().forEach((entry) -> {
                    Method method = entry.getValue();
                    String key = entry.getKey();
                    Object value = localConfigService.getOrDefault(key, method.getParameterTypes()[0]);
                    logger.debug("invoke config method = {}.{} with value = {} for key = {}", beanName, method.getName(), value, key);
                    try {
                        method.invoke(bean, value);
                    } catch (IllegalArgumentException | IllegalAccessException ex) {
                        throw new RuntimeException(ex);
                    } catch (InvocationTargetException ex) {
                        logger.error("error setting config value for method = " + method + " and value = " + value, ex.getCause());
                    }
                });
            } catch (Exception ex) {
                logger.error("error setting config value", ex);
            }
        }

    }

}
