package org.cmdbuild.auth.user;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.ImmutableList.toImmutableList;

import com.google.common.collect.Ordering;
import java.time.ZonedDateTime;
import java.util.List;
import javax.annotation.Nullable;
import static org.cmdbuild.auth.multitenant.UserAvailableTenantContextImpl.minimalAccess;
import org.cmdbuild.auth.multitenant.api.UserAvailableTenantContext;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import org.cmdbuild.auth.role.RoleInfo;
import static org.cmdbuild.auth.role.RoleInfoImpl.ANONYMOUS_LOGIN_ROLE;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class LoginUserImpl implements LoginUser {

    public static final LoginUser ANONYMOUS_LOGIN_USER = LoginUserImpl.builder().withUsername("anonymous").withGroups(list(ANONYMOUS_LOGIN_ROLE)).build();

    private final Long id;
    private final String username;
    private final String description;
    private final String email;
    private final boolean active;
    private final boolean service, hasMultigroupEnabled;
    private final UserAvailableTenantContext availableTenantContext;
    private final List<RoleInfo> roles;
    private final String defaultGroupName;
    private final ZonedDateTime passwordExpirationTimestamp;
    private final ZonedDateTime lastPasswordChange;
    private final ZonedDateTime lastExpiringNotification;

    private LoginUserImpl(LoginUserImplBuilder builder) {
        this.id = builder.id;
        this.username = checkNotBlank(builder.username);
        this.description = nullToEmpty(builder.description);
        this.email = builder.email;
        this.active = firstNotNull(builder.active, true);
        this.service = firstNotNull(builder.service, false);
        this.hasMultigroupEnabled = firstNotNull(builder.hasMultigroupEnabled, false);
        this.defaultGroupName = builder.defaultGroupName;
        this.passwordExpirationTimestamp = builder.passwordExpirationTimestamp;
        this.lastPasswordChange = builder.lastPasswordChange;
        this.lastExpiringNotification = builder.lastExpiringNotification;
        this.availableTenantContext = firstNotNull(builder.availableTenantContext, minimalAccess());
        this.roles = builder.groups.stream().sorted(Ordering.natural().onResultOf(RoleInfo::getName)).distinct().collect(toImmutableList());
        checkArgument(!roles.isEmpty(), "invalid login user =< %s > : this user has no valid groups", username);
    }

    @Override
    @Nullable
    public Long getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public List<RoleInfo> getRoleInfos() {
        return roles;
    }

    @Override
    public String getDefaultGroupName() {
        return defaultGroupName;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public boolean isService() {
        return service;
    }

    @Override
    public boolean isPasswordExpired() {
        return passwordExpirationTimestamp != null && now().isAfter(passwordExpirationTimestamp);
    }

    @Override
    public boolean hasMultigroupEnabled() {
        return hasMultigroupEnabled;
    }

    @Override
    public ZonedDateTime getPasswordExpirationTimestamp() {
        return passwordExpirationTimestamp;
    }

    @Override
    public ZonedDateTime getLastPasswordChangeTimestamp() {
        return lastPasswordChange;
    }

    @Override
    public ZonedDateTime getLastExpiringNotificationTimestamp() {
        return lastExpiringNotification;
    }

    @Override
    public UserAvailableTenantContext getAvailableTenantContext() {
        return availableTenantContext;
    }

    @Override
    public String toString() {
        return "UserImpl{" + "id=" + id + ", username=" + username + '}';
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!LoginUser.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final LoginUser other = LoginUser.class.cast(obj);
        return username.equals(other.getUsername());
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    public static LoginUserImplBuilder builder() {
        return new LoginUserImplBuilder();
    }

    public static LoginUserImplBuilder copyOf(LoginUser source) {
        return new LoginUserImplBuilder()
                .withId(source.getId())
                .withActiveStatus(source.isActive())
                .withAvailableTenantContext(source.getAvailableTenantContext())
                .withDefaultGroupName(source.getDefaultGroupName())
                .withDescription(source.getDescription())
                .withEmail(source.getEmail())
                .withLastExpiringNotification(source.getLastExpiringNotificationTimestamp())
                .withLastPasswordChange(source.getLastPasswordChangeTimestamp())
                .withServiceStatus(source.isService())
                .withUsername(source.getUsername())
                .withpasswordExpirationTimestamp(source.getPasswordExpirationTimestamp())
                .withMultigroupEnabled(source.hasMultigroupEnabled())
                .withGroups(source.getRoleInfos());
    }

    public static class LoginUserImplBuilder implements Builder<LoginUserImpl, LoginUserImplBuilder> {

        private Long id;
        private String username;
        private String description;
        private String email;
        private Boolean active;
        private Boolean service, hasMultigroupEnabled;
        private final List<RoleInfo> groups = list();
        private String defaultGroupName;
        private UserAvailableTenantContext availableTenantContext;
        private ZonedDateTime passwordExpirationTimestamp;
        private ZonedDateTime lastPasswordChange;
        private ZonedDateTime lastExpiringNotification;

        public LoginUserImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public LoginUserImplBuilder withUsername(String username) {
            this.username = username;
            return this;
        }

        public LoginUserImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public LoginUserImplBuilder addGroup(RoleInfo group) {
            this.groups.add(group);
            return this;
        }

        public LoginUserImplBuilder withGroups(List<RoleInfo> groups) {
            this.groups.clear();
            this.groups.addAll(groups);
            return this;
        }

        public LoginUserImplBuilder withAvailableTenantContext(UserAvailableTenantContext availableTenantContext) {
            this.availableTenantContext = availableTenantContext;
            return this;
        }

        public LoginUserImplBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public LoginUserImplBuilder withActiveStatus(Boolean active) {
            this.active = active;
            return this;
        }

        public LoginUserImplBuilder withServiceStatus(Boolean service) {
            this.service = service;
            return this;
        }

        public LoginUserImplBuilder withDefaultGroupName(String defaultGroupName) {
            this.defaultGroupName = defaultGroupName;
            return this;
        }

        public LoginUserImplBuilder withMultigroupEnabled(Boolean hasMultigroupEnabled) {
            this.hasMultigroupEnabled = hasMultigroupEnabled;
            return this;
        }

        public LoginUserImplBuilder withpasswordExpirationTimestamp(ZonedDateTime passwordExpirationTimestamp) {
            this.passwordExpirationTimestamp = passwordExpirationTimestamp;
            return this;
        }

        public LoginUserImplBuilder withLastPasswordChange(ZonedDateTime lastPasswordChange) {
            this.lastPasswordChange = lastPasswordChange;
            return this;
        }

        public LoginUserImplBuilder withLastExpiringNotification(ZonedDateTime lastExpiringNotification) {
            this.lastExpiringNotification = lastExpiringNotification;
            return this;
        }

        @Override
        public LoginUserImpl build() {
            return new LoginUserImpl(this);
        }

    }
}
