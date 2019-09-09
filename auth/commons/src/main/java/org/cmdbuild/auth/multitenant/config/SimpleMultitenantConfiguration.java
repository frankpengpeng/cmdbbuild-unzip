/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.multitenant.config;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import javax.annotation.Nullable;

/**
 *
 */
public class SimpleMultitenantConfiguration implements MultitenantConfiguration {

	private final static MultitenantConfiguration DISABLED_MULTITENANT_CONFIGURATION = new SimpleMultitenantConfiguration(MultitenantMode.DISABLED);
	private final MultitenantMode multitenantMode;

	public SimpleMultitenantConfiguration(MultitenantMode multitenantMode) {
		this.multitenantMode = requireNonNull(multitenantMode);
	}

	@Override
	public MultitenantMode getMultitenantMode() {
		return multitenantMode;
	}

	@Override
	public String getTenantClass() throws IllegalArgumentException {
		checkArgument(equal(getMultitenantMode(), MultitenantMode.CMDBUILD_CLASS));
		return DEFAULT_TENANT_CLASS_NAME;
	}

	@Override
	public String getDbFunction() throws IllegalArgumentException {
		checkArgument(equal(getMultitenantMode(), MultitenantMode.DB_FUNCTION));
		return DEFAULT_DB_FUNCTION_NAME;
	}

	public static MultitenantConfiguration multitenantDisabled() {
		return DISABLED_MULTITENANT_CONFIGURATION;
	}

	@Override
	@Nullable
	public String getTenantDomain() {
		return null;
	}

	@Override
	public boolean tenantAdminIgnoresTenantByDefault() {
		return true;
	}
}
