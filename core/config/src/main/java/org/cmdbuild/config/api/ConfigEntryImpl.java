/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.api;

import static com.google.common.base.Strings.nullToEmpty;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class ConfigEntryImpl implements ConfigEntry {

    private final String key, value;

    public ConfigEntryImpl(Entry<String, String> entry) {
        this(entry.getKey(), entry.getValue());
    }

    public ConfigEntryImpl(String key, @Nullable String value) {
        this.key = checkNotBlank(key);
        this.value = nullToEmpty(value);
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "ConfigEntry{" + "key=" + key + ", value=" + value + '}';
    }

}
