/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.driver;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.auth.multitenant.api.UserTenantContext;
import org.cmdbuild.auth.multitenant.config.MultitenantConfiguration;
import org.cmdbuild.auth.user.OperationUser;
import org.cmdbuild.dao.driver.DatabaseAccessConfig;
import static org.cmdbuild.spring.configuration.BeanNamesAndQualifiers.SYSTEM_LEVEL_TWO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
@Qualifier(SYSTEM_LEVEL_TWO)
@Primary
public class DatabaseAccessConfigImpl implements DatabaseAccessConfig {

	private final MultitenantConfiguration multitenantConfiguration;
	private final OperationUserSupplier operationUserSupplier;

	public DatabaseAccessConfigImpl(MultitenantConfiguration multitenantConfiguration, OperationUserSupplier operationUserSupplier) {
		this.multitenantConfiguration = checkNotNull(multitenantConfiguration);
		this.operationUserSupplier = checkNotNull(operationUserSupplier);
	}

	@Override
	public MultitenantConfiguration getMultitenantConfiguration() {
		return multitenantConfiguration;
	}

	@Override
	public DatabaseAccessUserContext getUserContext() {
		OperationUser operationUser = operationUserSupplier.getUser();
		UserTenantContext userTenantContext = operationUser.getUserTenantContext();
		return new SimpleDatabaseAccessUserContext(operationUser.getUsername(), userTenantContext.ignoreTenantPolicies(), userTenantContext.getActiveTenantIds());
	}

}
