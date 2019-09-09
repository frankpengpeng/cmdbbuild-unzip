/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.login;

public class RequesthAuthenticatorResponseImpl implements RequesthAuthenticatorResponse {

	final LoginUserIdentity login;
	final String redirectUrl;

	public static RequesthAuthenticatorResponse newLoginResponse(LoginUserIdentity login) {
		return new RequesthAuthenticatorResponseImpl(login, null);
	}

	public static RequesthAuthenticatorResponse newRedirectResponse(String redirectUrl) {
		return new RequesthAuthenticatorResponseImpl(null, redirectUrl);
	}

	private RequesthAuthenticatorResponseImpl(LoginUserIdentity login, String redirectUrl) {
		this.login = login;
		this.redirectUrl = redirectUrl;
	}

	@Override
	public final LoginUserIdentity getLogin() {
		return login;
	}

	@Override
	public final String getRedirectUrl() {
		return redirectUrl;
	}

}
