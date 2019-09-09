package org.cmdbuild.auth.grant;

import static java.util.Collections.emptySet;
import java.util.Map;
import java.util.Set;
import static org.cmdbuild.auth.grant.GrantPrivilege.GP_READ;
import static org.cmdbuild.auth.grant.GrantPrivilege.GP_WRITE;
import static org.cmdbuild.auth.role.RolePrivilege.RP_DATA_ALL_READ;
import static org.cmdbuild.auth.role.RolePrivilege.RP_DATA_ALL_WRITE;
import org.cmdbuild.auth.role.RolePrivilegeHolder;

public interface UserPrivileges extends RolePrivilegeHolder {

	boolean hasPrivilege(GrantPrivilege privilege, PrivilegeSubject privilegedObject);

	default boolean hasReadAccess(PrivilegeSubject privilegedObject) {
		return hasPrivileges(RP_DATA_ALL_READ) || hasPrivilege(GP_READ, privilegedObject);
	}

	default boolean hasWriteAccess(PrivilegeSubject privilegedObject) {
		return hasPrivileges(RP_DATA_ALL_WRITE) || hasPrivilege(GP_WRITE, privilegedObject);
	}

	Map<String, UserPrivilegesForObject> getAllPrivileges();

	UserPrivilegesForObject getPrivilegesForObject(PrivilegeSubject object);

	default boolean hasSourceGroups() {
		return !getSourceGroups().isEmpty();
	}

	default Set<String> getSourceGroups() {
		return emptySet();
	}

}
