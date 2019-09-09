/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.user;

import java.io.Serializable;
import java.util.List;
import org.cmdbuild.auth.multitenant.api.UserTenantContext;
import org.cmdbuild.auth.grant.UserPrivileges;
import org.cmdbuild.auth.role.Role;

/**
 *
 * instances of this class are serializable and immutable
 *
 * @author davide
 */
public interface OperationUserStack extends OperationUser, Serializable {

	OperationUser getCurrentOperationUser();

	OperationUser getRootOperationUser();

	/**
	 * return current stack of operation users (root first, current last)
	 *
	 * @return list of operation users
	 */
	List<OperationUser> getOperationUserStack();

	/**
	 *
	 * @return size of operation user stack (always >=1)
	 */
	int getOperationUserStackSize();

	@Override
	default UserTenantContext getUserTenantContext() {
		return getCurrentOperationUser().getUserTenantContext();
	}

	@Override
	default boolean hasDefaultGroup() {
		return getCurrentOperationUser().hasDefaultGroup();
	}

	@Override
	default LoginUser getLoginUser() {
		return getCurrentOperationUser().getLoginUser();
	}

	@Override
	default Role getDefaultGroupOrNull() {
		return getCurrentOperationUser().getDefaultGroupOrNull();
	}

	@Override
	default UserPrivileges getPrivilegeContext() {
		return getCurrentOperationUser().getPrivilegeContext();
	}
}
