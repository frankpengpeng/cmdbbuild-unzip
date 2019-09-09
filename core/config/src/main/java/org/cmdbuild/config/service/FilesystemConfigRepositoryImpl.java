/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.service;

import org.cmdbuild.config.api.ConfigEntryImpl;
import org.cmdbuild.config.api.ConfigEntry;
import org.cmdbuild.config.api.ConfigUpdate;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.not;
import com.google.common.base.Splitter;
import static com.google.common.collect.ImmutableList.toImmutableList;
import com.google.common.collect.Multimap;
import static com.google.common.collect.Multimaps.index;
import static com.google.common.io.Files.copy;
import java.io.File;
import java.io.IOException;
import static java.lang.String.format;
import java.util.Collection;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import org.apache.commons.io.FilenameUtils;
import static org.cmdbuild.common.error.ErrorAndWarningCollectorService.marker;
import org.cmdbuild.config.api.ConfigDefinition;
import org.cmdbuild.config.api.DirectoryService;
import org.springframework.stereotype.Component;
import org.cmdbuild.config.api.ConfigDefinitionRepository;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import static org.cmdbuild.config.utils.ConfigUtils.addNamespaceToKey;
import static org.cmdbuild.config.utils.ConfigUtils.stripNamespaceFromKey;
import static org.cmdbuild.config.utils.LegacyConfigUtils.translateLegacyConfigNames;
import org.cmdbuild.utils.crypto.Cm3EasyCryptoUtils;
import static org.cmdbuild.utils.io.CmPropertyUtils.loadProperties;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotEmpty;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.cmdbuild.config.utils.ConfigUtils.hasNamespace;
import org.cmdbuild.utils.date.CmDateUtils;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowConsumer;

@Component
public class FilesystemConfigRepositoryImpl implements ConfigRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DirectoryService directoryService;
    private final ConfigDefinitionRepository configDefinitionRepository;

    public FilesystemConfigRepositoryImpl(DirectoryService directoryService, ConfigDefinitionRepository configDefinitionRepository) {
        this.directoryService = checkNotNull(directoryService);
        this.configDefinitionRepository = checkNotNull(configDefinitionRepository);
    }

    @Override
    public List<ConfigEntry> getConfigEntries() {
        if (!directoryService.hasConfigDirectory()) {
            logger.warn("config directory is not available, skip config file load");
            return emptyList();
        } else {
            try {
                backupConfigFiles();//TODO backup only before file change (?)
                return getCandidateConfigFiles().stream().flatMap(f -> new ConfigFileProcessor(f).getConfigsFromFileSafe().stream()).collect(toImmutableList());
            } catch (Exception ex) {
                logger.error(marker(), "error loading config files", ex);
                return emptyList();
            }
        }
    }

    @Override
    public void updateConfigs(List<? extends ConfigUpdate> configs) {
        if (!configs.isEmpty()) {
            checkArgument(configs.stream().allMatch(configDefinitionRepository::isLocationFileOnly));
            backupConfigFiles();
            Multimap<String, ConfigUpdate> configsByNamespaces = index((List<ConfigUpdate>) configs, ConfigUpdate::getNamespace);
            configsByNamespaces.asMap().forEach((namespace, entries) -> {
                try {
                    File file = getConfigFileForNamespace(namespace);
                    new ConfigFileProcessor(file).saveConfigsToFile(entries);
                } catch (Exception ex) {
                    throw runtime(ex, "error saving config to file for namespace = %s", namespace);
                }
            });
        }
    }

    private List<File> getCandidateConfigFiles() {
        File configDirectory = directoryService.getConfigDirectory();
        return list(configDirectory.listFiles((File file, String name) -> name.toLowerCase().matches(".*[.](conf|config|properties)$")));
    }

    private File getConfigFileForNamespace(String namespace) throws IOException {
        String basename = Splitter.on(".").splitToList(namespace).stream().skip(2).collect(onlyElement());
        File configFile = getCandidateConfigFiles().stream().filter(f -> f.getName().toLowerCase().matches(format("^%s[.](conf|config|properties)$", basename))).findFirst().orElse(null);
        if (configFile == null) {
            String fileName = format("%s.conf", basename);
            configFile = new File(directoryService.getConfigDirectory(), fileName);
        }
        return configFile;
    }

    private void backupConfigFiles() {
        try {
            File backupDir = new File(new File(directoryService.getConfigDirectory(), "backup"), format("config_files_%s", CmDateUtils.dateTimeFileSuffix()));//todo check and avoid name clash (double backup on same dir)
            List<File> files = getCandidateConfigFiles();
            if (!files.isEmpty()) {
                logger.debug("backup config files to dir = {}", backupDir.getAbsolutePath());
                backupDir.mkdirs();
                files.forEach(rethrowConsumer(f -> {
                    logger.debug("backup config file = {} to dir = {}", f.getAbsolutePath(), backupDir.getAbsolutePath());
                    copy(f, new File(backupDir, f.getName()));
                }));
            }
        } catch (Exception ex) {
            logger.warn("config files backup error", ex);
        }
    }

    private class ConfigFileProcessor {

        private final String KEEP_CONFIG_FILE_KEY = "keepconfigfile";

        private final File configFile;
        private final String namespace;

        private boolean keepConfigFile = false;
        private List<ConfigEntry> configs = emptyList();

        public ConfigFileProcessor(File configFile) {
            this.configFile = checkNotNull(configFile);
            namespace = format("org.cmdbuild.%s", FilenameUtils.getBaseName(configFile.getName()));
        }

        public List<ConfigEntry> getConfigsFromFileSafe() {
            logger.debug("load config from file = {}", configFile.getAbsolutePath());
            try {
                loadFileContent();
                updateFileContentSafe();
                return configs;
            } catch (Exception ex) {
                logger.error(marker(), "error loading config files from file = {}", configFile.getAbsolutePath(), ex);
                return emptyList();
            }
        }

        private void saveConfigsToFile(Collection<ConfigUpdate> configUpdate) throws ConfigurationException, IOException {
            if (configFile.exists()) {
                loadFileContent();
            }
            Map<String, String> configMerge = map();
            configs.forEach(e -> configMerge.put(e.getKey(), e.getValue()));
            configUpdate.stream().filter(ConfigUpdate::isUpdate).map(ConfigEntry.class::cast).forEach(e -> configMerge.put(e.getKey(), e.getValue()));
            configUpdate.stream().filter(ConfigUpdate::isDelete).map(ConfigUpdate::getKey).forEach(configMerge::remove);
            configs = configMerge.entrySet().stream().map(e -> new ConfigEntryImpl(e.getKey(), e.getValue())).collect(toImmutableList());
            updateFileContent();
        }

        private void loadFileContent() {
            logger.debug("load config file content from file = {}", configFile.getAbsolutePath());
            keepConfigFile = false;
            Map<String, String> rawConfig = loadProperties(configFile);
            logger.trace("loaded raw config from file = {} config = \n\n{}\n", configFile.getAbsolutePath(), mapToLoggableStringLazy(rawConfig));
            Map<String, String> processedConfig = map();
            rawConfig.forEach((key, value) -> {
                key = addNamespaceToKey(namespace, key);
                value = Cm3EasyCryptoUtils.decryptValue(value);
                if (equal(key, addNamespaceToKey(namespace, KEEP_CONFIG_FILE_KEY)) && toBooleanOrDefault(value, false) == true) {
                    keepConfigFile = true;
                } else {
                    processedConfig.put(key, value);
                }
            });
            Map<String, String> translatedConfig = translateLegacyConfigNames(processedConfig);
            logger.trace("loaded processed config from file = {} config = \n\n{}\n", configFile.getAbsolutePath(), mapToLoggableStringLazy(translatedConfig));
            configs = translatedConfig.entrySet().stream().map(e -> toConfigEntry(e.getKey(), e.getValue())).collect(toImmutableList());
        }

        private ConfigEntry toConfigEntry(String key, @Nullable String value) {
            ConfigDefinition configDefinition = configDefinitionRepository.getOrNull(key);
            if (configDefinition == null) {
                logger.warn("found unknown config key = {} in file = {}", key, configFile.getAbsolutePath());
            }
            return new ConfigEntryImpl(key, value);
        }

        private void updateFileContentSafe() {
            try {
                updateFileContent();
            } catch (Exception ex) {
                logger.warn("unable to update file content for file = {}", configFile.getAbsolutePath(), ex);
            }
        }

        private void updateFileContent() throws ConfigurationException, IOException {
            if (configs.stream().anyMatch(configDefinitionRepository::isLocationFileOnly)) {
                List<ConfigEntry> toUpdate = keepConfigFile ? configs : configs.stream().filter(configDefinitionRepository::isLocationFileOnly).collect(toList());
                checkNotEmpty(toUpdate);
                doUpdateFileContent(toUpdate);
            } else if (keepConfigFile) {
                logger.debug("keep config file = {}", configFile.getAbsolutePath());
            } else {
                logger.debug("delete config file = {}", configFile.getAbsolutePath());
                deleteQuietly(configFile);
            }
        }

        private void doUpdateFileContent(List<ConfigEntry> toUpdate) throws ConfigurationException, IOException {
            Map<String, String> newConfig = toUpdate.stream().collect(toMap(e -> {
                String key = e.getKey();
                if (hasNamespace(namespace, key)) {
                    key = stripNamespaceFromKey(namespace, key);
                }
                return key;
            }, ConfigEntry::getValue));
            if (keepConfigFile) {
                newConfig.put(KEEP_CONFIG_FILE_KEY, TRUE);
            }
            logger.trace("update config file = {} with config = \n\n{}\n", configFile.getAbsolutePath(), mapToLoggableStringLazy(newConfig));
            if (newConfig.isEmpty()) {
                logger.debug("no config data to save, delete config file = {}", configFile.getAbsolutePath());
                deleteQuietly(configFile);
            } else {
                logger.debug("update config file = {}", configFile.getAbsolutePath());
                configFile.createNewFile();
                PropertiesConfiguration properties = new PropertiesConfiguration();
                properties.setDelimiterParsingDisabled(true);
                properties.load(configFile);
                Map<String, String> currentConfigOnFile = (Map) ConfigurationConverter.getMap(properties);
                currentConfigOnFile.keySet().stream().map(String.class::cast).filter(not(newConfig::containsKey)).forEach(properties::clearProperty);
                currentConfigOnFile.forEach((key, value) -> {
                    if (newConfig.containsKey(key) && Cm3EasyCryptoUtils.isEncrypted(value) && !Cm3EasyCryptoUtils.isEncrypted(newConfig.get(key))) {
                        newConfig.put(key, Cm3EasyCryptoUtils.encryptValue(newConfig.get(key)));
                    }
                });
                newConfig.forEach(properties::setProperty);
                properties.save(configFile);
            }
        }

    }

}
