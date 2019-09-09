/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import org.springframework.stereotype.Component;

@Component
@ConfigComponent("org.cmdbuild.preferences")
public class PreferencesConfigurationImpl implements PreferencesConfiguration {

    @ConfigValue(key = "preferredOfficeSuite", description = "preferred office suite (`msoffice` or `default`)", defaultValue = "default")
    private PreferredOfficeSuite preferredOfficeSuite;

    @Override
    public PreferredOfficeSuite getPreferredOfficeSuite() {
        return checkNotNull(preferredOfficeSuite);
    }
}
