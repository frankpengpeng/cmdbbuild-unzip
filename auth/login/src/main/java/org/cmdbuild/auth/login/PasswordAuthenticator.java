package org.cmdbuild.auth.login;

public interface PasswordAuthenticator extends AuthenticatorDelegate {

	boolean isPasswordValid(LoginUserIdentity login, String password);

}
