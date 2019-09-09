/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.user;

import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.auth.grant.GrantPrivilege;
import org.cmdbuild.auth.grant.PrivilegeSubject;
import org.cmdbuild.auth.grant.UserPrivileges;
import org.cmdbuild.auth.grant.UserPrivilegesForObject;
import org.cmdbuild.auth.multitenant.api.UserTenantContext;
import org.cmdbuild.auth.role.RolePrivilege;

public interface UserDaoHelperService extends OperationUserSupplier, UserPrivileges, UserTenantContext {

    @Override
    public default boolean hasPrivilege(GrantPrivilege privilege, PrivilegeSubject subject) {
        return getUser().hasPrivilege(privilege, subject);
    }

    @Override
    public default Map<String, UserPrivilegesForObject> getAllPrivileges() {
        return getUser().getAllPrivileges();
    }

    @Override
    public default UserPrivilegesForObject getPrivilegesForObject(PrivilegeSubject object) {
        return getUser().getPrivilegesForObject(object);
    }

    @Override
    public default Set<RolePrivilege> getRolePrivileges() {
        return getUser().getRolePrivileges();
    }

    @Override
    public default Set<Long> getActiveTenantIds() {
        return getUser().getUserTenantContext().getActiveTenantIds();
    }

    @Override
    public default boolean ignoreTenantPolicies() {
        return getUser().getUserTenantContext().ignoreTenantPolicies();
    }

    @Override
    @Nullable
    public default Long getDefaultTenantId() {
        return getUser().getUserTenantContext().getDefaultTenantId();
    }

}
