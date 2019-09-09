/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import org.cmdbuild.config.api.ConfigDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.config.api.ConfigDefinitionRepository;

@Component
public class ConfigDefinitionStoreImpl implements ConfigDefinitionRepository {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final Map<String, ConfigDefinition> configDefinitions = new TreeMap<>();

	@Override
	public synchronized void put(ConfigDefinition configDefinition) {
		logger.debug("register config definition = {}", configDefinition);
		checkNotNull(configDefinition, "config definition cannot be null");
		checkArgument(!configDefinitions.containsKey(configDefinition.getKey()), "config definition already present for key = %s", configDefinition.getKey());
		configDefinitions.put(configDefinition.getKey(), configDefinition);
	}

	@Override
	public Map<String, ConfigDefinition> getAll() {
		return Collections.unmodifiableMap(configDefinitions);
	}

	@Override
	public ConfigDefinition getOrNull(String key) {
		checkNotNull(key, "key cannot be null");
		return configDefinitions.get(key);
	}

}
