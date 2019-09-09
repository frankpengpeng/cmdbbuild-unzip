package org.cmdbuild.bim;

import static org.cmdbuild.bim.utils.BimConfigUtils.BIMSERVER_CONFIG_ENABLED;
import static org.cmdbuild.bim.utils.BimConfigUtils.BIMSERVER_CONFIG_NAMESPACE;
import static org.cmdbuild.bim.utils.BimConfigUtils.BIMSERVER_CONFIG_PASSWORD;
import static org.cmdbuild.bim.utils.BimConfigUtils.BIMSERVER_CONFIG_URL;
import static org.cmdbuild.bim.utils.BimConfigUtils.BIMSERVER_CONFIG_USERNAME;
import org.cmdbuild.config.BimserverConfiguration;
import static org.cmdbuild.config.api.ConfigCategory.CC_ENV;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.springframework.stereotype.Component;

@Component
@ConfigComponent(BIMSERVER_CONFIG_NAMESPACE)
public final class BimserverConfigurationImpl implements BimserverConfiguration {

    @ConfigValue(key = BIMSERVER_CONFIG_ENABLED, description = "bim server enabled", defaultValue = FALSE)
    private boolean isEnabled;

    @ConfigValue(key = BIMSERVER_CONFIG_USERNAME, description = "bim server username", defaultValue = "admin@bimserver.com", category = CC_ENV)
    private String username;

    @ConfigValue(key = BIMSERVER_CONFIG_PASSWORD, description = "bim server password", defaultValue = "admin", category = CC_ENV)
    private String password;

    @ConfigValue(key = BIMSERVER_CONFIG_URL, description = "bim server url", defaultValue = "http://localhost:8080/bimserver", category = CC_ENV)
    private String url;

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUrl() {
        return url;
    }

}
