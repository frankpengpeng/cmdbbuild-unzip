/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.login;

import javax.annotation.Nullable;
import org.cmdbuild.auth.login.header.HeaderAuthenticatorConfiguration;
import org.cmdbuild.auth.login.ldap.LdapAuthenticatorConfiguration;
import org.cmdbuild.auth.login.cas.CasAuthenticatorConfiguration;
import org.cmdbuild.auth.config.AuthenticationServiceConfiguration;
import org.cmdbuild.auth.config.UserRepositoryConfig;
import org.cmdbuild.auth.login.custom.CustomLoginConfiguration;

public interface AuthenticationConfiguration extends HeaderAuthenticatorConfiguration, CasAuthenticatorConfiguration, LdapAuthenticatorConfiguration, AuthenticationServiceConfiguration, UserRepositoryConfig, CustomLoginConfiguration {

    boolean getForceWSPasswordDigest();

    PasswordAlgo getPreferredPasswordAlgorythm();

    @Nullable
    String getLogoutRedirectUrl();
}
