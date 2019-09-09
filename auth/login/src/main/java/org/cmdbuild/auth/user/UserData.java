/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.user;

import java.time.ZonedDateTime;
import javax.annotation.Nullable;

public interface UserData {

	static final String USER_CLASS_NAME = "User";

	@Nullable
	Long getId();

	@Nullable
	String getDescription();

	String getUsername();

	@Nullable
	String getPassword();

	@Nullable
	String getEmail();

	boolean isActive();

	boolean isService();

	@Nullable
	ZonedDateTime getPasswordExpiration();

	@Nullable
	ZonedDateTime getLastPasswordChange();

	@Nullable
	ZonedDateTime getLastExpiringNotification();

}
