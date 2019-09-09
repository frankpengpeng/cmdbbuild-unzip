/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow;

import java.util.Properties;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.utils.crypto.Cm3EasyCryptoUtils;
import org.cmdbuild.workflow.api.SharkWsTypeConverterConfig;
import org.enhydra.shark.api.internal.working.CallbackUtilities;
import org.cmdbuild.workflow.api.CmdbuildClientConfig;

public class SharkExtensionsConfigImpl implements CmdbuildClientConfig, SharkWsTypeConverterConfig {

    private static final String PREFIX = "org.cmdbuild.";

    private static final String WS_PREFIX = PREFIX + "ws.";
    private static final String WS_URL = WS_PREFIX + "url";
    private static final String WS_USERNAME = WS_PREFIX + "username";
    private static final String WS_PASSWORD = WS_PREFIX + "password";
    private static final String WS_TOKEN = WS_PREFIX + "token.enable";
    private static final String WS_REFERENCE_TYPE_LEGACY = WS_PREFIX + "referencetype.legacy";

    private static final boolean WS_TOKEN_DEFAULT = true;
    private static final boolean WS_REFERENCE_TYPE_LEGACY_DEFAULT = false;

    private final Properties properties;

    public SharkExtensionsConfigImpl(CallbackUtilities callbackUtilities) {
        this.properties = new Properties();
        this.properties.putAll(callbackUtilities.getProperties());
        this.properties.putAll(System.getProperties());
        properties.entrySet().forEach((entry) -> {
            if (entry.getValue() instanceof String && Cm3EasyCryptoUtils.isEncrypted((String) entry.getValue())) {
                entry.setValue(Cm3EasyCryptoUtils.decryptValue((String) entry.getValue()));
            }
        });
    }

    @Override
    public String getUrl() {
        return properties.getProperty(WS_URL);
    }

    @Override
    public String getUsername() {
        return properties.getProperty(WS_USERNAME);
    }

    @Override
    public String getPassword() {
        return properties.getProperty(WS_PASSWORD);
    }

    @Override
    public boolean isTokenEnabled() {
        final String value = properties.getProperty(WS_TOKEN);
        return isBlank(value) ? WS_TOKEN_DEFAULT : Boolean.valueOf(value);
    }

    @Override
    public boolean legacyReference() {
        final String value = properties.getProperty(WS_REFERENCE_TYPE_LEGACY);
        return isBlank(value) ? WS_REFERENCE_TYPE_LEGACY_DEFAULT : Boolean.valueOf(value);
    }

}
