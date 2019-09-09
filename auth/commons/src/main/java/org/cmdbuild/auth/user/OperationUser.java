package org.cmdbuild.auth.user;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newLinkedHashSet;
import java.util.Collection;
import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import java.util.List;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.auth.grant.GrantPrivilege;
import org.cmdbuild.auth.multitenant.api.UserTenantContext;
import org.cmdbuild.auth.grant.UserPrivileges;
import org.cmdbuild.auth.grant.UserPrivilegesForObject;
import org.cmdbuild.auth.role.Role;
import org.cmdbuild.auth.grant.PrivilegeSubject;

/**
 * an operation user is the user which does an operations, with privileges,
 * groups, etc
 */
public interface OperationUser extends UserPrivileges {

	default @Nullable
	Long getId() {
		return getLoginUser().getId();
	}

	default boolean hasId() {
		return getId() != null;
	}

	/**
	 *
	 * @return userTenantContext
	 */
	UserTenantContext getUserTenantContext();

	/**
	 * An authenticated user is valid if it has a preferred group selected. The
	 * preferred group is the group that the user chose at the login. If the
	 * user belongs to one group or if it belongs to multiple groups but it has
	 * a default group, the preferred group is already selected.
	 *
	 * @return
	 */
	default boolean hasDefaultGroup() {
		return getDefaultGroupOrNull() != null;
	}

	LoginUser getLoginUser();//TODO replace with 'userInfo'

	default String getUsername() {
		return getLoginUser().getUsername();
	}

	/**
	 * Returns the group with which the user logged in. It can be the default
	 * group or the only group which the user belongs to or the selected group
	 *
	 * note: if the user has not a default group, this will return an instance
	 * of NullGroup
	 *
	 * @return
	 */
	@Nullable
	Role getDefaultGroupOrNull();

	default Role getDefaultGroup() {
		return checkNotNull(getDefaultGroupOrNull(), "default group not set for user = %s", this);
	}

	default String getDefaultGroupName() {
		return getDefaultGroup().getName();
	}

	@Nullable
	default String getDefaultGroupNameOrNull() {
		return hasDefaultGroup() ? getDefaultGroupName() : null;
	}

	UserPrivileges getPrivilegeContext();

	@Override
	default boolean hasReadAccess(PrivilegeSubject privilegedObject) {
		return getPrivilegeContext().hasReadAccess(privilegedObject);
	}

	@Override
	default boolean hasWriteAccess(PrivilegeSubject privilegedObject) {
		return getPrivilegeContext().hasWriteAccess(privilegedObject);
	}

	@Override
	@Deprecated
	default boolean hasAdminAccess() {
		return getPrivilegeContext().hasAdminAccess();
	}

	@Override
	default boolean hasPrivilege(GrantPrivilege requested, PrivilegeSubject privilegedObject) {
		return getPrivilegeContext().hasPrivilege(requested, privilegedObject);
	}

	@Override
	public default UserPrivilegesForObject getPrivilegesForObject(PrivilegeSubject object) {
		return getPrivilegeContext().getPrivilegesForObject(object);
	}

	default boolean isPasswordExpired() {
		return getLoginUser().isPasswordExpired();
	}

	default List<String> getGroupNamesDefaultFirst() {
		return newArrayList(newLinkedHashSet(concat(hasDefaultGroup() ? singleton(getDefaultGroupName()) : emptyList(), getGroupNames())));
	}

	default Collection<String> getGroupNames() {
		return getLoginUser().getGroupNames();
	}

	default Collection<String> getActiveGroupNames() {
		if (isMultiGroup()) {
			return getGroupNames();
		} else {
			return singleton(getDefaultGroupName());
		}
	}

	default boolean isMultiGroup() {
		return isNotBlank(getLoginUser().getDefaultGroupName());
	}

	default boolean hasActiveGroupName(String groupName) {
		return getActiveGroupNames().contains(groupName);
	}

}
