/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.driver;

import static com.google.common.collect.ImmutableSet.copyOf;
import static java.util.Collections.emptySet;
import java.util.Set;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import org.cmdbuild.auth.multitenant.config.MultitenantConfiguration;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface DatabaseAccessConfig {

    final static Long IGNORE_TENANT_POLICIES = -1l;

    MultitenantConfiguration getMultitenantConfiguration();

    DatabaseAccessUserContext getUserContext();

    interface DatabaseAccessUserContext {

        /**
         *
         * @return true if this user is authorized to ignore tenant policy (es:
         * super admin)
         */
        boolean ignoreTenantPolicies();

        /**
         *
         * @return set of tenant ids this user is authorized to see; result
         * should be ignored if {@link #ignoreTenantPolicies()} is true
         *
         * always return valid non-null set
         */
        Set<Long> getTenantIds();

        String getUsername();
    }

    static class SimpleDatabaseAccessUserContext implements DatabaseAccessUserContext {

        public static final DatabaseAccessUserContext IGNORE_TENANT_POLICIES_DB_DRIVER_INFO = new SimpleDatabaseAccessUserContext("system", true, null);

        private final boolean ignoreTenantPolicies;
        private final Set<Long> tenantIdSet;
        private final String username;

        public SimpleDatabaseAccessUserContext(String username, Long... tenantIdSet) {
            this(username, copyOf(tenantIdSet));
        }

        public SimpleDatabaseAccessUserContext(String username, @Nullable Set<Long> tenantIdSet) {
            this.username = checkNotBlank(username);
            tenantIdSet = firstNonNull(tenantIdSet, emptySet());
            this.ignoreTenantPolicies = tenantIdSet.contains(IGNORE_TENANT_POLICIES);
            this.tenantIdSet = ignoreTenantPolicies ? emptySet() : copyOf(tenantIdSet);
        }

        public SimpleDatabaseAccessUserContext(String username, boolean ignoreTenantPolicies, @Nullable Set<Long> tenantIdSet) {
            this.username = checkNotBlank(username);
            this.ignoreTenantPolicies = ignoreTenantPolicies;
            this.tenantIdSet = ignoreTenantPolicies ? emptySet() : copyOf(tenantIdSet);
        }

        @Override
        public boolean ignoreTenantPolicies() {
            return ignoreTenantPolicies;
        }

        @Override
        public Set<Long> getTenantIds() {
            return tenantIdSet;
        }

        @Override
        public String getUsername() {
            return username;
        }

        @Override
        public String toString() {
            return "SimpleDatabaseAccessUserContext{" + "ignoreTenantPolicies=" + ignoreTenantPolicies + ", tenantIdSet=" + tenantIdSet + ", username=" + username + '}';
        }

        public static DatabaseAccessUserContext maximumAccessUserContext() {
            return IGNORE_TENANT_POLICIES_DB_DRIVER_INFO;
        }
    }
}
