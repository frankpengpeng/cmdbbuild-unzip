/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.api;

import static com.google.common.base.Preconditions.checkNotNull;
import javax.annotation.Nullable;
import static org.cmdbuild.config.api.ConfigCategory.CC_DEFAULT;
import static org.cmdbuild.config.api.ConfigLocation.CL_DEFAULT;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class ConfigDefinitionImpl implements ConfigDefinition {

    private final String key, description, defaultValue;
    private final boolean isProtected;
    private final ConfigLocation location;
    private final ConfigCategory category;

    private ConfigDefinitionImpl(ConfigDefinitionImplBuilder builder) {
        this.key = checkNotBlank(builder.key);
        this.description = checkNotNull(builder.description);
        this.location = firstNotNull(builder.location, CL_DEFAULT);
        this.category = firstNotNull(builder.category, CC_DEFAULT);
        this.defaultValue = builder.defaultValue;
        this.isProtected = builder.isProtected;
    }

    @Override
    public ConfigLocation getLocation() {
        return location;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public @Nullable
    @Override
    String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public boolean isProtected() {
        return isProtected;
    }

    @Override
    public ConfigCategory getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return "ConfigDefinition{" + "key=" + key + ", description=" + description + ", defaultValue=" + defaultValue + '}';
    }

    public static ConfigDefinitionImplBuilder builder() {
        return new ConfigDefinitionImplBuilder();
    }

    public static ConfigDefinitionImplBuilder copyOf(ConfigDefinition configDefinition) {
        return builder()
                .withDefaultValue(configDefinition.getDefaultValue())
                .withKey(configDefinition.getKey())
                .withDescription(configDefinition.getDescription())
                .withLocation(configDefinition.getLocation())
                .withCategory(configDefinition.getCategory())
                .withProtected(configDefinition.isProtected());
    }

    public static class ConfigDefinitionImplBuilder implements Builder<ConfigDefinitionImpl, ConfigDefinitionImplBuilder> {

        private String key, description, defaultValue;
        private boolean isProtected = false;
        private ConfigLocation location;
        private ConfigCategory category;

        private ConfigDefinitionImplBuilder() {
        }

        public ConfigDefinitionImplBuilder withKey(String key) {
            this.key = key;
            return this;
        }

        public ConfigDefinitionImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public ConfigDefinitionImplBuilder withLocation(ConfigLocation location) {
            this.location = location;
            return this;
        }

        public ConfigDefinitionImplBuilder withCategory(ConfigCategory category) {
            this.category = category;
            return this;
        }

        public ConfigDefinitionImplBuilder withDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public ConfigDefinitionImplBuilder withProtected(boolean protect) {
            this.isProtected = protect;
            return this;
        }

        @Override
        public ConfigDefinitionImpl build() {
            return new ConfigDefinitionImpl(this);
        }

    }

}
