/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config;

import static org.cmdbuild.config.api.ConfigCategory.CC_ENV;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.springframework.stereotype.Component;

@Component
@ConfigComponent("org.cmdbuild.jobs")
public class JobsConfigurationImpl implements JobsConfiguration {

    @ConfigValue(key = "enabled", description = "enable scheduled jobs", defaultValue = FALSE, category = CC_ENV)
    private boolean isEnabled;

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
