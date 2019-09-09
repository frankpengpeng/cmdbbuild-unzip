package org.cmdbuild.auth.login;

import static com.google.common.collect.ImmutableSet.copyOf;
import java.util.Collection;
import java.util.Set;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.cmdbuild.auth.multitenant.api.TenantLoginData;
import org.cmdbuild.auth.user.LoginUser;
import org.cmdbuild.utils.lang.Builder;

public class LoginDataImpl implements LoginData, TenantLoginData {

    private final String loginString;
    private final String unencryptedPassword;
    private final String loginGroupName;
    private final boolean passwordRequired, serviceUsersAllowed;
    private final Boolean ignoreTenantPolicies;
    private final Long defaultTenant;
    private final Set<Long> activeTenants;

    private LoginDataImpl(LoginDataImplBuilder builder) {
        this.loginString = builder.loginString;
        this.unencryptedPassword = builder.unencryptedPassword;
        this.loginGroupName = builder.loginGroupName;
        this.passwordRequired = builder.passwordRequired;
        this.serviceUsersAllowed = builder.serviceUsersAllowed;
        this.defaultTenant = builder.defaultTenant;
        this.activeTenants = builder.activeTenants;
        this.ignoreTenantPolicies = builder.ignoreTenantPolicies;
    }

    @Override
    public String getLoginString() {
        return loginString;
    }

    @Override
    public String getPassword() {
        return unencryptedPassword;
    }

    @Override
    public String getLoginGroupName() {
        return loginGroupName;
    }

    @Override
    public boolean isPasswordRequired() {
        return passwordRequired;
    }

    @Override
    public boolean isServiceUsersAllowed() {
        return serviceUsersAllowed;
    }

    public @Nullable
    @Override
    Long getDefaultTenant() {
        return defaultTenant;
    }

    public @Nullable
    @Override
    Set<Long> getActiveTenants() {
        return activeTenants;
    }

    @Override
    @Nullable
    public Boolean ignoreTenantPolicies() {
        return ignoreTenantPolicies;
    }

    @Override
    public String toString() {
        return "LoginDataImpl{" + "login=" + loginString + ", group=" + loginGroupName + '}';
    }

    public static LoginDataImplBuilder builder() {
        return new LoginDataImplBuilder();
    }

    public static LoginData buildNoPasswordRequired(String username) {
        return builder().withLoginString(username).withNoPasswordRequired().allowServiceUser().build();
    }

    public static class LoginDataImplBuilder implements Builder<LoginDataImpl, LoginDataImplBuilder> {

        private String loginString;
        private String unencryptedPassword;
        private String loginGroupName;
        public boolean passwordRequired = true, serviceUsersAllowed;
        public Boolean ignoreTenantPolicies;
        private Long defaultTenant;
        private Set<Long> activeTenants;

        @Override
        public LoginDataImpl build() {
            return new LoginDataImpl(this);
        }

        public LoginDataImplBuilder withLoginString(String loginString) {
            this.loginString = loginString;
            return this;
        }

        public LoginDataImplBuilder withPassword(String unencryptedPassword) {
            this.unencryptedPassword = unencryptedPassword;
            this.passwordRequired = true;
            return this;
        }

        public LoginDataImplBuilder withGroupName(@Nullable String loginGroupName) {
            this.loginGroupName = loginGroupName;
            return this;
        }

        public LoginDataImplBuilder withNoPasswordRequired() {
            this.passwordRequired = false;
            return this;
        }

        public LoginDataImplBuilder allowServiceUser() {
            return this.withServiceUsersAllowed(true);
        }

        public LoginDataImplBuilder withIgnoreTenantPolicies(Boolean ignoreTenantPolicies) {
            this.ignoreTenantPolicies = ignoreTenantPolicies;
            return this;
        }

        public LoginDataImplBuilder withServiceUsersAllowed(boolean serviceUsersAllowed) {
            this.serviceUsersAllowed = serviceUsersAllowed;
            return this;
        }

        public LoginDataImplBuilder withDefaultTenant(@Nullable Long defaultTenant) {
            this.defaultTenant = defaultTenant;
            return this;
        }

        public LoginDataImplBuilder withActiveTenants(@Nullable Collection<Long> activeTenants) {
            this.activeTenants = activeTenants == null ? null : copyOf(activeTenants);
            return this;
        }

        public LoginDataImplBuilder withUser(LoginUser user) {
            return this.withLoginString(user.getUsername()).withGroupName(user.getDefaultGroupName());
        }

    }
}
