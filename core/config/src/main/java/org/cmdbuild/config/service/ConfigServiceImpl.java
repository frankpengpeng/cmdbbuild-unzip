/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.service;

import org.cmdbuild.config.utils.ConfigBeanUtils;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Maps;
import static com.google.common.collect.Maps.filterEntries;
import static com.google.common.collect.MoreCollectors.onlyElement;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import static java.util.Arrays.asList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.concat;
import javax.annotation.Nullable;
import org.cmdbuild.clustering.ClusterMessageImpl;
import org.cmdbuild.clustering.ClusterMessageReceivedEvent;
import org.cmdbuild.config.api.AfterConfigReloadEventImpl;
import org.cmdbuild.config.api.ConfigBeanRepository;
import org.cmdbuild.config.api.ConfigReloadEventImpl;
import org.cmdbuild.config.api.ConfigDefinition;
import org.cmdbuild.config.api.ConfigUpdateEventImpl;
import org.cmdbuild.utils.crypto.Cm3EasyCryptoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.config.api.GlobalConfigService;
import org.cmdbuild.config.api.NamespacedConfigService;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotEmpty;
import org.cmdbuild.clustering.ClusterService;
import org.cmdbuild.common.error.ErrorAndWarningCollectorService;
import org.cmdbuild.config.api.ConfigDefinitionImpl;
import org.cmdbuild.config.api.ConfigDefinitionRepository;
import org.cmdbuild.config.api.ConfigDeleteImpl;
import org.cmdbuild.config.api.ConfigEntry;
import org.cmdbuild.config.api.ConfigEntryImpl;
import org.cmdbuild.config.api.ConfigEvent;
import static org.cmdbuild.config.utils.ConfigUtils.addNamespaceToKey;
import static org.cmdbuild.config.utils.ConfigUtils.stripNamespaceFromKey;
import org.cmdbuild.services.AppContextReadyEvent;
import org.cmdbuild.services.SystemLoadingConfigEvent;
import org.cmdbuild.services.SystemLoadingConfigFilesEvent;
import org.cmdbuild.system.SystemEventService;
import org.cmdbuild.utils.lang.CmConvertUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableString;
import static org.cmdbuild.config.utils.ConfigUtils.hasNamespace;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

/**
 *
 */
@Component
public class ConfigServiceImpl implements GlobalConfigService {

    private final static String CLUSTER_MESSAGE_RELOAD_MANY = "config.reload_many",
            CLUSTER_MESSAGE_DATA_KEYS_PARAM = "keys",
            CLUSTER_MESSAGE_RELOAD_ALL = "config.reload_all";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final EventBus globalEventBus = new EventBus();

    private final ConfigRepositoryFacade configStore;
    private final ConfigDefinitionRepository configDefinitionStore;
    private final ClusterService clusteringService;
    private final ConfigBeanRepository configBeanRepository;

    public ConfigServiceImpl(ConfigRepositoryFacade configStore, ConfigBeanRepository configBeanRepository, ClusterService clusteringService, ConfigDefinitionRepository configDefinitionStore, SystemEventService systemEventService) {
        this.configStore = checkNotNull(configStore);
        this.configDefinitionStore = checkNotNull(configDefinitionStore);
        this.configBeanRepository = checkNotNull(configBeanRepository);
        this.clusteringService = checkNotNull(clusteringService);
        clusteringService.getEventBus().register(new Object() {
            @Subscribe
            public void handleClusterMessageReceivedEvent(ClusterMessageReceivedEvent event) {
                if (event.isOfType(CLUSTER_MESSAGE_RELOAD_ALL)) {
                    logger.debug("invalidate all config in response to a cluster message");
                    configStore.reloadAllConfig();
                    postUpdate();
                } else if (event.isOfType(CLUSTER_MESSAGE_RELOAD_MANY)) {
                    List<String> keys = checkNotEmpty(event.getData(CLUSTER_MESSAGE_DATA_KEYS_PARAM));
                    logger.debug("invalidate config keys in response to a cluster message, keys = {}", keys);
                    configStore.reloadAllConfig();
                    postUpdate(keys);
                }
            }
        });
        systemEventService.getEventBus().register(new Object() {

            @Subscribe
            public void handleAppContextReadyEvent(AppContextReadyEvent event) {
                logger.info("init config beans");
                configBeanRepository.getConfigHelpers().forEach(b -> b.processBean(ConfigServiceImpl.this));
            }

            @Subscribe
            public void handleSystemLoadingConfigFilesEvent(SystemLoadingConfigFilesEvent event) {
                logger.info("load config from files");
                configStore.loadConfigFromFiles();
                Set<String> configsFromFiles = configDefinitionStore.getAll().values().stream().filter(ConfigDefinition::isLocationFileOnly).map(ConfigDefinition::getKey).collect(toSet());
                postUpdate(configsFromFiles);
            }

            @Subscribe
            public void handleSystemLoadingConfigEvent(SystemLoadingConfigEvent event) {
                logger.info("load config from db");
                configStore.loadConfigFromDb();
                List<ConfigEntry> toUpdate = configStore.getConfigFromFile().entrySet().stream()
                        .filter(e -> configDefinitionStore.isLocationDefault(e.getKey()) && !equal(e.getValue(), configStore.getConfigFromDb().get(e.getKey())))
                        .map(ConfigEntryImpl::new).collect(toList());
                if (!toUpdate.isEmpty()) {
                    logger.info("update on db configs from file = \n\n{}\n", mapToLoggableString(map(toUpdate, ConfigEntry::getKey, ConfigEntry::getValue)));
                    configStore.updateConfig(toUpdate);
                }
                postUpdateAll();
            }
        });
    }

    private void postUpdate(String... keys) {
        postUpdate(asList(keys));
    }

    private void postUpdate(Iterable<String> keys) {
        globalEventBus.post(new ConfigUpdateEventImpl(keys));
        globalEventBus.post(new ConfigReloadEventImpl(keys));
        globalEventBus.post(new AfterConfigReloadEventImpl(keys));
    }

    private void postUpdateAll() {
        globalEventBus.post(new ConfigUpdateEventImpl());
        globalEventBus.post(new ConfigReloadEventImpl());
        globalEventBus.post(new AfterConfigReloadEventImpl());
    }

    @Override
    public String getConfigNamespaceFromConfigBeanClass(Class configBeanClass) {
        return configBeanRepository.getConfigBeans().stream().filter(configBeanClass::isInstance).map(ConfigBeanUtils::getNamespace).collect(onlyElement());
    }

    @Override
    public NamespacedConfigService getConfig(String namespace) {
        return new NamespacedConfigServiceImpl(namespace);
    }

    @Override
    public EventBus getEventBus() {
        return globalEventBus;
    }

    @Override
    public @Nullable
    String getString(String key) {
        Optional<String> optional = doGetConfig(key);
        return optional != null && optional.isPresent() ? optional.get() : null;
    }

    @Override
    public @Nullable
    String getStringOrDefault(String key) {
        return Optional.ofNullable(doGetConfig(key)).orElse(Optional.ofNullable(configDefinitionStore.get(key).getDefaultValue())).orElse(null);
    }

    @Override
    @Nullable
    public String getStringOrDefault(String namespace, String key) {
        return getStringOrDefault(addNamespaceToKey(namespace, key));
    }

    @Nullable
    private Optional<String> doGetConfig(String key) {
        Map<String, String> config = configStore.getAllConfig();
        if (config.containsKey(key)) {
            return Optional.ofNullable(config.get(key));
        } else {
            return null;
        }
    }

    @Override
    public void putString(String key, String value, boolean encrypt) {
        if (encrypt) {
            value = Cm3EasyCryptoUtils.encryptValue(value);
        }
        putString(key, value);
    }

    @Override
    public void putString(String key, @Nullable String value) {
        putStrings(map(key, value));
    }

    @Override
    public void putString(String namespace, String key, String value) {
        putStrings(namespace, map(key, value));
    }

    @Override
    public void putStrings(String namespace, Map<String, String> map) {
        putStrings(map.entrySet().stream().collect(toMap(e -> addNamespaceToKey(namespace, e.getKey()), Entry::getValue)));
    }

    @Override
    public void putStrings(Map<String, String> map) {
        Set<String> updatedKeys = putStringsNoEvents(map);
        if (!updatedKeys.isEmpty()) {
            postUpdate(updatedKeys);
            notifyConfigUpdateOnCluster(updatedKeys);
        }
    }

    private Set<String> putStringsNoEvents(Map<String, String> map) {
        map.keySet().forEach((key) -> checkNotBlank(key));
        map = map(filterEntries(map, (e) -> !equal(e.getValue(), getString(e.getKey()))));
        configStore.updateConfig(map);
        return map.keySet();
    }

    @Override
    public void delete(String key) {
        configStore.updateConfig(list(new ConfigDeleteImpl(key)));
        postUpdate(key);
        notifyConfigUpdateOnCluster(key);
    }

    @Override
    public Map<String, String> getConfigAsMap() {
        return configStore.getAllConfig();
    }

    @Override
    public Map<String, String> getConfigOrDefaultsAsMap() {
        return map(configDefinitionStore.getAllDefaults()).with(getConfigAsMap());
    }

    @Override
    public Map<String, ConfigDefinition> getConfigDefinitions() {
        return configDefinitionStore.getAll();
    }

    @Override
    public synchronized void reloadFromFilesSkipNotify() {
        configStore.clearConfig();
        configStore.loadConfigFromFiles();
    }

    @Override
    public synchronized void reload() {
        configStore.reloadAllConfig();
        postUpdate();
        notifyConfigReloadOnCluster();
    }

    private void notifyConfigReloadOnCluster() {
        clusteringService.sendMessage(ClusterMessageImpl.builder().withMessageType(CLUSTER_MESSAGE_RELOAD_ALL).build());
    }

    private void notifyConfigUpdateOnCluster(String... keys) {
        notifyConfigUpdateOnCluster(list(keys));
    }

    private void notifyConfigUpdateOnCluster(Collection<String> keys) {
        clusteringService.sendMessage(ClusterMessageImpl.builder()
                .withMessageType(CLUSTER_MESSAGE_RELOAD_MANY)
                .withMessageData(map(CLUSTER_MESSAGE_DATA_KEYS_PARAM, list(keys)))
                .build());
    }

    private class NamespacedConfigServiceImpl implements NamespacedConfigService {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        private final String namespace;
        private final EventBus namespacedEventBus = new EventBus();

        public NamespacedConfigServiceImpl(String namespace) {
            this.namespace = checkNotBlank(namespace);
            globalEventBus.register(new Object() {

                @Subscribe
                public void handleConfigEvent(ConfigEvent event) {
                    if (event.impactNamespace(namespace)) {
                        namespacedEventBus.post(event);
                    }
                }
            });
        }

        @Override
        @Nullable
        public String getString(String key) {
            return configStore.getAllConfig().get(addNamespaceToKey(namespace, key));
        }

        @Override
        @Nullable
        public String getStringOrDefault(String key) {
            key = addNamespaceToKey(namespace, key);
            return configStore.getAllConfig().getOrDefault(key, configDefinitionStore.getDefaultOrNull(key));
        }

        @Override
        public <T> T getOrDefault(String key, Class<T> valueType) {
            String value = getStringOrDefault(key);
            try {
                return CmConvertUtils.convert(value, valueType);
            } catch (Exception ex) {
                logger.error(ErrorAndWarningCollectorService.marker(), String.format("error converting config value = '%s' for key = %s.%s to type = %s; returning default value", value, namespace, key, valueType), ex);
                return CmConvertUtils.convert(getDefault(key), valueType);
            }
        }

        @Override
        public String getNamespace() {
            return namespace;
        }

        @Override
        public Map<String, String> getAsMap() {
            Map<String, String> map = map();
            concat(configDefinitionStore.getAllDefaults().entrySet().stream(), configStore.getAllConfig().entrySet().stream())
                    .filter((entry) -> hasNamespace(namespace, entry.getKey()))
                    .forEach((java.util.Map.Entry<java.lang.String, java.lang.String> entry) -> map.put(stripNamespaceFromKey(namespace, entry.getKey()), entry.getValue()));
            return map;
        }

        @Override
        public EventBus getEventBus() {
            return namespacedEventBus;
        }

        @Override
        public ConfigDefinition getDefinition(String key) {
            ConfigDefinition namespacedConfigDefinition = configDefinitionStore.get(addNamespaceToKey(namespace, key));
            ConfigDefinition localConfigDefinition = ConfigDefinitionImpl.copyOf(namespacedConfigDefinition).withKey(stripNamespaceFromKey(namespace, namespacedConfigDefinition.getKey())).build();
            return localConfigDefinition;
        }

        @Override
        public Map<String, ConfigDefinition> getAllDefinitions() {
            return Maps.filterKeys(configDefinitionStore.getAll(), (key) -> key.startsWith(namespace + ".")).values().stream()
                    .map(c -> ConfigDefinitionImpl.copyOf(c).withKey(stripNamespaceFromKey(namespace, c.getKey())).build())
                    .collect(toMap(ConfigDefinition::getKey, identity()));
        }

        @Override
        public ConfigDefinition addDefinition(ConfigDefinition configDefinition) {
            ConfigDefinition namespacedConfigDefinition = ConfigDefinitionImpl.copyOf(configDefinition).withKey(addNamespaceToKey(namespace, configDefinition.getKey())).build();
            configDefinitionStore.put(namespacedConfigDefinition);
            return namespacedConfigDefinition;
        }

    }

}
