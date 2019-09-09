package org.cmdbuild.auth.role;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.math.NumberUtils.isNumber;

import org.cmdbuild.auth.userrole.UserRole;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface RoleRepository {

    List<Role> getAllGroups();

    @Nullable
    Role getByIdOrNull(long groupId);

    default Role getById(long groupId) {
        return checkNotNull(getByIdOrNull(groupId), "role not found for id = %s", groupId);
    }

    @Nullable
    Role getGroupWithNameOrNull(String groupName);

    default Role getGroupWithName(String groupName) {
        return checkNotNull(getGroupWithNameOrNull(groupName), "group not found for name = %s", groupName);
    }

    Map<String, Role> getGroupsByName(Iterable<String> groupNames);

    Role update(Role group);

    Role create(Role group);

    List<UserRole> getUserGroups(long userId);

    void setUserGroups(long userId, Collection<Long> userGroupIds, @Nullable Long defaultGroupId);

    void setUserGroupsByName(long userId, Collection<String> userGroups, @Nullable String defaultGroup);

    default Role getByNameOrId(String roleId) {
        checkNotBlank(roleId);
        if (isNumber(roleId)) {
            return getById(toLong(roleId));
        } else {
            return getGroupWithName(roleId);
        }
    }

}
