/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.api;

import com.google.common.eventbus.EventBus;
import java.util.Map;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import javax.annotation.Nullable;

public interface NamespacedConfigService {

    String getNamespace();

    @Nullable
    String getString(String key);

    @Nullable
    String getStringOrDefault(String key);

    @Nullable
    <T> T getOrDefault(String key, Class<T> valueType);

    Map<String, String> getAsMap();

    /**
     * return an eventbus that can be used to listen to
     * {@link ConfigUpdateEvent} events. Only events impacting this
     * configuration (namespace) are fowarded via this eventbus
     *
     * @return
     */
    EventBus getEventBus();

    ConfigDefinition getDefinition(String key);

    Map<String, ConfigDefinition> getAllDefinitions();

    ConfigDefinition addDefinition(ConfigDefinition configDefinition);

    default @Nullable
    String getDefault(String key) {
        return getDefinition(key).getDefaultValue();
    }

    default Map<String, String> getAllOrDefaults() {
        return getAllDefinitions().keySet().stream().collect(toMap(identity(), this::getStringOrDefault));
    }

}
