/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.classe.access;

import static com.google.common.base.Predicates.notNull;
import com.google.common.collect.ImmutableMap;
import static java.lang.String.format;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import org.cmdbuild.auth.grant.GrantAttributePrivilege;
import static org.cmdbuild.auth.grant.GrantAttributePrivilege.GAP_READ;
import static org.cmdbuild.auth.grant.GrantAttributePrivilege.GAP_WRITE;
import org.cmdbuild.auth.grant.GrantPrivilege;
import static org.cmdbuild.auth.grant.GrantPrivilege.GP_CLONE;
import static org.cmdbuild.auth.grant.GrantPrivilege.GP_CREATE;
import static org.cmdbuild.auth.grant.GrantPrivilege.GP_DELETE;
import static org.cmdbuild.auth.grant.GrantPrivilege.GP_READ;
import static org.cmdbuild.auth.grant.GrantPrivilege.GP_UPDATE;
import static org.cmdbuild.auth.grant.GrantPrivilege.GP_WRITE;
import org.cmdbuild.auth.grant.GroupOfPrivileges;
import org.cmdbuild.auth.role.RolePrivilege;
import static org.cmdbuild.auth.role.RolePrivilege.RP_ADMIN_CLASSES_MODIFY;
import static org.cmdbuild.auth.role.RolePrivilege.RP_ADMIN_CLASSES_VIEW;
import static org.cmdbuild.auth.role.RolePrivilege.RP_DATA_ALL_READ;
import static org.cmdbuild.auth.role.RolePrivilege.RP_DATA_ALL_WRITE;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.core.q3.QueryBuilder;
import org.cmdbuild.dao.entrytype.AttributePermission;
import static org.cmdbuild.dao.entrytype.AttributePermission.AP_LIST;
import static org.cmdbuild.dao.entrytype.AttributePermission.AP_MODIFY;
import static org.cmdbuild.dao.entrytype.AttributePermission.AP_READ;
import static org.cmdbuild.dao.entrytype.AttributePermission.AP_WRITE;
import org.cmdbuild.dao.entrytype.AttributePermissionsImpl;
import org.cmdbuild.dao.entrytype.AttributeWithoutOwner;
import org.cmdbuild.dao.entrytype.AttributeWithoutOwnerImpl;
import org.cmdbuild.dao.entrytype.ClassPermission;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_CLONE;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_CREATE;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_DELETE;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_LIST;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_MODIFY;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_READ;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_UPDATE;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_WRITE;
import org.cmdbuild.dao.entrytype.ClassPermissions;
import org.cmdbuild.dao.entrytype.ClassPermissionsImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.ClasseImpl;
import static org.cmdbuild.dao.entrytype.DaoPermissionUtils.intersectAttributePermissions;
import org.cmdbuild.dao.entrytype.PermissionScope;
import static org.cmdbuild.dao.entrytype.PermissionScope.PS_SERVICE;
import static org.cmdbuild.utils.lang.CmCollectionUtils.isNullOrEmpty;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

public class UserClassUtils {

	private final static Map<GrantPrivilege, ClassPermission> GRANT_PRIVILEGES_TO_CLASS_PERMISSIONS = ImmutableMap.copyOf(map(
			GP_READ, CP_READ,
			GP_WRITE, CP_WRITE,
			GP_CREATE, CP_CREATE,
			GP_UPDATE, CP_UPDATE,
			GP_DELETE, CP_DELETE,
			GP_CLONE, CP_CLONE
	));

	public final static Map<RolePrivilege, ClassPermission> ROLE_PRIVILEGES_TO_CLASS_PERMISSIONS = ImmutableMap.copyOf(map(
			RP_ADMIN_CLASSES_MODIFY, CP_MODIFY,
			RP_ADMIN_CLASSES_VIEW, CP_LIST,
			RP_DATA_ALL_READ, CP_READ,
			RP_DATA_ALL_WRITE, CP_WRITE
	));

	private final static Map<GrantAttributePrivilege, AttributePermission> GRANT_ATTRIBUTE_PRIVILEGES_TO_ATTRIBUTE_PERMISSIONS = ImmutableMap.copyOf(map(
			GAP_READ, AP_READ,
			GAP_WRITE, AP_WRITE
	));

	private final static Map<RolePrivilege, AttributePermission> ROLE_PRIVILEGES_TO_ATTRIBUTE_PERMISSIONS = ImmutableMap.copyOf(map(
			RP_DATA_ALL_READ, AP_READ,
			RP_DATA_ALL_WRITE, AP_WRITE
	));
	private final static Map<ClassPermission, AttributePermission> CLASS_PERMISSIONS_TO_ATTRIBUTE_PERMISSIONS = ImmutableMap.copyOf(map(
			CP_MODIFY, AP_MODIFY,
			CP_LIST, AP_LIST
	));

	public static Classe applyPrivilegesToClass(Set<RolePrivilege> rolePrivileges, GroupOfPrivileges grantPrivileges, Classe classe) {
		Set<ClassPermission> permissionsFromUser = set();
		grantPrivileges.getPrivileges().stream().map(GRANT_PRIVILEGES_TO_CLASS_PERMISSIONS::get).filter(notNull()).forEach(permissionsFromUser::add);
		rolePrivileges.stream().map(ROLE_PRIVILEGES_TO_CLASS_PERMISSIONS::get).filter(notNull()).forEach(permissionsFromUser::add);
		ClassPermissions permissions = ClassPermissionsImpl.copyOf(classe).intersectPermissions(PermissionScope.PS_SERVICE, permissionsFromUser).build();
		classe = ClasseImpl.copyOf(classe).withPermissions(permissions).withAttributes(classe.getAllAttributes().stream().map((a) -> toUserAttribute(rolePrivileges, permissions, grantPrivileges, a)).collect(toList())).build();
		return classe;
	}

	private static AttributeWithoutOwner toUserAttribute(Set<RolePrivilege> rolePrivileges, ClassPermissions classPermissions, GroupOfPrivileges privileges, AttributeWithoutOwner attribute) {
		Map<PermissionScope, Set<AttributePermission>> permissions = attribute.getPermissionMap();

		permissions = intersectAttributePermissions(permissions, set(CLASS_PERMISSIONS_TO_ATTRIBUTE_PERMISSIONS.values()),
				map(PS_SERVICE, classPermissions.getPermissionsForScope(PS_SERVICE).stream().map(CLASS_PERMISSIONS_TO_ATTRIBUTE_PERMISSIONS::get).filter(notNull()).collect(toSet())));

		if (!isNullOrEmpty(privileges.getAttributePrivileges())) {
			Set<GrantAttributePrivilege> attrPrivileges = privileges.getAttributePrivileges().get(attribute.getName());
			if (attrPrivileges != null) {
				Set<AttributePermission> attributePermissions = rolePrivileges.stream().map(ROLE_PRIVILEGES_TO_ATTRIBUTE_PERMISSIONS::get).filter(notNull()).collect(toSet());
				attrPrivileges.stream().map(GRANT_ATTRIBUTE_PRIVILEGES_TO_ATTRIBUTE_PERMISSIONS::get).filter(notNull()).forEach(attributePermissions::add);
				permissions = intersectAttributePermissions(permissions,
						set(AP_READ, AP_WRITE),
						map(PS_SERVICE, attributePermissions));
			}
		}
		return AttributeWithoutOwnerImpl.copyOf(attribute).withPermissions(AttributePermissionsImpl.builder().withPermissions(permissions).build()).build();
	}

}
