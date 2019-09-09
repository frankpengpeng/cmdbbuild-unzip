/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.entrytype;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;
import java.util.Set;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_WRITE;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_READ;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_MODIFY;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_LIST;
import static org.cmdbuild.dao.entrytype.PermissionScope.PS_CORE;
import static org.cmdbuild.dao.entrytype.PermissionScope.PS_SERVICE;
import static org.cmdbuild.dao.entrytype.PermissionScope.PS_UI;

public interface ClassPermissions {

    Map<PermissionScope, Set<ClassPermission>> getPermissionsMap();

    default Set<ClassPermission> getPermissionsForScope(PermissionScope scope) {
        return checkNotNull(getPermissionsMap().get(scope), "permissions not found for scope = %s", scope);
    }

    default boolean hasPermission(PermissionScope scope, ClassPermission permission) {
        return getPermissionsForScope(scope).contains(permission);
    }

    default void checkPermission(PermissionScope scope, ClassPermission permission) {
        checkArgument(hasPermission(scope, permission), "forbidden operation: missing permission %s.%s", scope, permission);
    }

    default boolean hasCoreReadPermission() {
        return hasPermission(PS_CORE, CP_READ);
    }

    default boolean hasCoreWritePermission() {
        return hasPermission(PS_CORE, CP_WRITE);
    }

    default boolean hasServiceListPermission() {
        return hasPermission(PS_SERVICE, CP_LIST);
    }

    default boolean hasServicePermission(ClassPermission permission) {
        return hasPermission(PS_SERVICE, permission);
    }

    default boolean hasServiceReadPermission() {
        return hasPermission(PS_SERVICE, CP_READ);
    }

    default boolean hasServiceWritePermission() {
        return hasPermission(PS_SERVICE, CP_WRITE);
    }

    default boolean hasServiceModifyPermission() {
        return hasPermission(PS_SERVICE, CP_MODIFY);
    }

    default boolean hasUiModifyPermission() {
        return hasPermission(PS_UI, CP_MODIFY);
    }

    default boolean hasUiPermission(ClassPermission permission) {
        return hasPermission(PS_UI, permission);
    }

}
