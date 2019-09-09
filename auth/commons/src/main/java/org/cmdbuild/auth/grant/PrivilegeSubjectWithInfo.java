package org.cmdbuild.auth.grant;

import javax.annotation.Nullable;

public interface PrivilegeSubjectWithInfo extends PrivilegeSubject {

	@Nullable
	Long getId();

	String getName();

	String getDescription();

	@Override
	default boolean hasInfo() {
		return true;
	}
}
