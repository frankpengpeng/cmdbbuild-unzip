package org.cmdbuild.auth.login;

public interface ClientRequestAuthenticator extends AuthenticatorDelegate {

    RequesthAuthenticatorResponse authenticate(AuthRequestInfo request);

}
