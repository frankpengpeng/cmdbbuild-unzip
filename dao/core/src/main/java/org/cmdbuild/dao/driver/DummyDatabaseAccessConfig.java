/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.driver;

import org.cmdbuild.auth.multitenant.config.MultitenantConfiguration;
import static org.cmdbuild.auth.multitenant.config.SimpleMultitenantConfiguration.multitenantDisabled;
import static org.cmdbuild.dao.driver.DatabaseAccessConfig.SimpleDatabaseAccessUserContext.maximumAccessUserContext;
import static org.cmdbuild.spring.configuration.BeanNamesAndQualifiers.SYSTEM_LEVEL_ONE;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier(SYSTEM_LEVEL_ONE)
public class DummyDatabaseAccessConfig implements DatabaseAccessConfig {

    @Override
    public MultitenantConfiguration getMultitenantConfiguration() {
        return multitenantDisabled();
    }

    @Override
    public DatabaseAccessUserContext getUserContext() {
        return maximumAccessUserContext();
    }

}
