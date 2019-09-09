/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.service;

import static com.google.common.base.Objects.equal;
import org.cmdbuild.config.api.ConfigEntry;
import org.cmdbuild.config.api.ConfigUpdate;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.transform;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.config.api.ConfigDefinitionRepository;
import org.cmdbuild.config.api.ConfigDeleteImpl;
import org.cmdbuild.config.api.ConfigEntryImpl;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@Component
public class ConfigRepositoryFacadeImpl implements ConfigRepositoryFacade {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ConfigRepository databaseConfigRepository, filesystemConfigRepository;
    private final ConfigDefinitionRepository configDefinitionRepository;
    private final Map<String, String> defaultAndEnvConfigs;

    private Map<String, String> configFromFile = emptyMap(), configFromDb = emptyMap(), allConfig = emptyMap();

    public ConfigRepositoryFacadeImpl(DefaultConfigRepositoryImpl defaultConfigRepository, EnvConfigRepositoryImpl envConfigRepository, DatabaseConfigRepositoryImpl databaseConfigRepository, FilesystemConfigRepositoryImpl filesystemConfigRepository, ConfigDefinitionRepository configDefinitionRepository) {
        this.databaseConfigRepository = checkNotNull(databaseConfigRepository);
        this.filesystemConfigRepository = checkNotNull(filesystemConfigRepository);
        this.configDefinitionRepository = checkNotNull(configDefinitionRepository);
        defaultAndEnvConfigs = map(defaultConfigRepository.getConfigEntries(), ConfigEntry::getKey, ConfigEntry::getValue)
                .with(map(envConfigRepository.getConfigEntries(), ConfigEntry::getKey, ConfigEntry::getValue))
                .immutableCopy();
    }

    @Override
    public synchronized void loadConfigFromFiles() {
        configFromFile = map(filesystemConfigRepository.getConfigEntries(), ConfigEntry::getKey, ConfigEntry::getValue).immutableCopy();
        allConfig = map(defaultAndEnvConfigs).with(allConfig).with(configFromFile).immutableCopy();
    }

    @Override
    public synchronized void loadConfigFromDb() {
        configFromDb = map(databaseConfigRepository.getConfigEntries(), ConfigEntry::getKey, ConfigEntry::getValue).immutableCopy();
        allConfig = map(defaultAndEnvConfigs).with(allConfig).with(configFromDb).immutableCopy();
    }

    @Override
    public synchronized void updateConfig(List<? extends ConfigUpdate> configs) {
        if (!configs.isEmpty()) {
            configs = list(transform(configs, (configUpdate) -> {
                if (configUpdate.isUpdate() && configDefinitionRepository.isDefault((ConfigEntry) configUpdate)) {
                    return new ConfigDeleteImpl(configUpdate.getKey());
                } else {
                    return configUpdate;
                }
            }));
            databaseConfigRepository.updateConfigs(configs.stream().filter(configDefinitionRepository::isLocationDefault).collect(toList()));
            filesystemConfigRepository.updateConfigs(configs.stream().filter(configDefinitionRepository::isLocationFileOnly).collect(toList()));

            reloadAllConfig();
        }
    }

    @Override
    public synchronized void reloadAllConfig() {
        clearConfig();
        loadConfigFromFiles();
        loadConfigFromDb();
        updateConfig(configFromFile.entrySet().stream().map(ConfigEntryImpl::new).filter(configDefinitionRepository::isLocationDefault).filter(c -> !equal(c.getValue(), configFromDb.get(c.getKey()))).collect(toList()));
    }

    @Override
    public Map<String, String> getAllConfig() {
        return allConfig;
    }

    @Override
    public Map<String, String> getConfigFromFile() {
        return configFromFile;
    }

    @Override
    public Map<String, String> getConfigFromDb() {
        return configFromDb;
    }

    @Override
    public synchronized void clearConfig() {
        configFromFile = configFromDb = allConfig = emptyMap();
    }

}
