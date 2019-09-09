/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.login;

import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import static org.cmdbuild.auth.login.AuthorityConst.ADMIN_ACCESS_AUTHORITY;
import static org.cmdbuild.auth.login.AuthorityConst.SYSTEM_ACCESS_AUTHORITY;
import org.cmdbuild.auth.role.RolePrivilege;
import static org.cmdbuild.auth.role.RolePrivilege.RP_ADMIN_ACCESS;
import static org.cmdbuild.auth.role.RolePrivilege.RP_SYSTEM_ACCESS;
import org.cmdbuild.auth.user.OperationUser;

public class AuthorityUtils {

	private final static Map<RolePrivilege, String> ROLE_PRIVILEGE_AUTHORITY_MAPPING = ImmutableMap.of(
			RP_ADMIN_ACCESS, ADMIN_ACCESS_AUTHORITY,
			RP_SYSTEM_ACCESS, SYSTEM_ACCESS_AUTHORITY
	//			RP_ADMIN_REPORTS_MODIFY, SYSTEM_ACCESS_AUTHORITY,
	//			RP_ADMIN_REPORTS_VIEW, REPORTS_VIEW_AUTHORITY
	);

	public static List<String> getAutoritiesForUser(OperationUser user) {
		return user.getRolePrivileges().stream().map(ROLE_PRIVILEGE_AUTHORITY_MAPPING::get).filter(notNull()).distinct().collect(toImmutableList());
	}
}
