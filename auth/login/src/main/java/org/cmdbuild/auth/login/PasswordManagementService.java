package org.cmdbuild.auth.login;

import java.time.LocalDateTime;

public interface PasswordManagementService {

	boolean isPasswordManagementEnabled();

	LocalDateTime getPasswordExpiration();

	boolean isValidPassword(String newPassword, String username, String newPasswordOrHash, String previousPasswordOrHash);

	boolean isValidPassword(String password);
}
