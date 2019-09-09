package org.cmdbuild.auth.grant;

import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public interface Grant {

	PrivilegedObjectType getObjectType();

	Set<GrantPrivilege> getPrivileges();

	PrivilegeSubjectWithInfo getSubject();

	default String getName() {
		return getSubject().getPrivilegeId();
	}

	@Nullable
	String getFilterOrNull();

	default boolean hasFilter() {
		return isNotBlank(getFilterOrNull());
	}

	Map<String, GrantAttributePrivilege> getAttributePrivileges();

}
