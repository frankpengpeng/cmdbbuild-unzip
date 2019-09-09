package org.cmdbuild.auth.user;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.transform;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.auth.multitenant.api.UserAvailableTenantContext;
import org.cmdbuild.auth.role.RoleInfo;

public interface LoginUser {

    @Nullable
    Long getId();

    String getUsername();

    String getDescription();

    List<RoleInfo> getRoleInfos();

    @Nullable
    String getDefaultGroupName();

    UserAvailableTenantContext getAvailableTenantContext();

    @Nullable
    String getEmail();

    boolean isActive();

    boolean isService();

    boolean isPasswordExpired();

    boolean hasMultigroupEnabled();

    ZonedDateTime getPasswordExpirationTimestamp();

    ZonedDateTime getLastPasswordChangeTimestamp();

    ZonedDateTime getLastExpiringNotificationTimestamp();

    default long getIdNotNull() {
        return checkNotNull(getId(), "id not available for user = %s (not a db user, missing id)", this);
    }

    default Collection<String> getGroupNames() {
        return transform(getRoleInfos(), RoleInfo::getName);
    }

    default Collection<String> getGroupDescriptions() {
        return transform(getRoleInfos(), RoleInfo::getDescription);
    }

    default boolean hasDefaultGroup() {
        return isNotBlank(getDefaultGroupName());
    }

}
