package org.cmdbuild.auth.grant;

public interface PrivilegeSubject {

	String getPrivilegeId();

	default boolean hasInfo() {
		return false;
	}

	default PrivilegeSubjectWithInfo getInfo() {
		return (PrivilegeSubjectWithInfo) this;
	}

}
