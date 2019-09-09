package org.cmdbuild.auth.user;

import static com.google.common.base.Preconditions.checkArgument;
import java.util.Collection;
import org.cmdbuild.auth.grant.UserPrivileges;

public interface OperationUserSupplier {

	/**
	 * Returns the current operation user (or anonymous when missing).
	 *
	 * @return the operation user for this request
	 */
	OperationUser getUser();

	default UserPrivileges getPrivileges() {
		return getUser().getPrivilegeContext();
	}

	default String getUsername() {
		return getUser().getUsername();
	}

	default String getCurrentGroup() {
		return getUser().getDefaultGroupName();
	}

	default void checkPrivileges(PrivilegeChecker checker, String message, Object... args) {
		checkArgument(hasPrivileges(checker), message, args);
	}

	default boolean hasPrivileges(PrivilegeChecker checker) {
		return checker.hasPrivileges(getPrivileges());
	}

	default Collection<String> getActiveGroupNames() {
		return getUser().getActiveGroupNames();
	}

	interface PrivilegeChecker {

		boolean hasPrivileges(UserPrivileges privileges);
	}
}
