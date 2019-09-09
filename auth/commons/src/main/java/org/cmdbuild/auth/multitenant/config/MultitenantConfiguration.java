package org.cmdbuild.auth.multitenant.config;

import static com.google.common.base.Objects.equal;
import javax.annotation.Nullable;

public interface MultitenantConfiguration {

	public static final String MT_NAMESPACE = "org.cmdbuild.multitenant",
			MT_TENANT_CLASS = "tenantClass",
			MT_TENANT_DOMAIN = "tenantDomain",
			MT_MODE = "mode",
			MULTITENANT_CONFIG_PROPERTY_TENANT_CLASS = MT_NAMESPACE + "." + MT_TENANT_CLASS,
			MULTITENANT_CONFIG_PROPERTY_TENANT_DOMAIN = MT_NAMESPACE + "." + MT_TENANT_DOMAIN,
			MULTITENANT_CONFIG_PROPERTY_MODE = MT_NAMESPACE + "." + MT_MODE;

	static final String DEFAULT_DB_FUNCTION_NAME = "_cm3_multitenant_get",
			DEFAULT_TENANT_CLASS_NAME = "Tenant";

	static final Long IGNORE_TENANT_POLICIES_TENANT_ID = -1l;

	enum MultitenantMode {
		DISABLED, CMDBUILD_CLASS, DB_FUNCTION
	}

	/**
	 * return multitenant mode, which is one of <ul>
	 * <li>{@link MultitenantMode#DISABLED}: multitenant is disabled;</li>
	 * <li>{@link MultitenantMode#CMDBUILD_CLASS}: multitenant will use the
	 * supplied class (and relation with cmdbuild user) as its tenant
	 * source;</li>
	 * <li>{@link MultitenantMode#DB_FUNCTION}: multitenant will use a database
	 * function to retrieve tenants for the user.</li>
	 *
	 * @return multitenant mode
	 */
	MultitenantMode getMultitenantMode();

	/**
	 *
	 * @return cmdbuild class to be used as tenant source
	 * @throws IllegalArgumentException if mode is not
	 * {@link MultitenantMode#CMDBUILD_CLASS}
	 */
	String getTenantClass();

	@Nullable
	String getTenantDomain();

	/**
	 *
	 * @return database function that return tenants for user
	 * @throws IllegalArgumentException if mode is not
	 * {@link MultitenantMode#DB_FUNCTION}
	 */
	String getDbFunction();
	
	boolean tenantAdminIgnoresTenantByDefault();

	default boolean isMultitenantEnabled() {
		return !isMultitenantDisabled();
	}

	default boolean isMultitenantDisabled() {
		return equal(getMultitenantMode(), MultitenantMode.DISABLED);
	}

}
