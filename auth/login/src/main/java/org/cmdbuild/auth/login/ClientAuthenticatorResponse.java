/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.login;

import javax.annotation.Nullable;
import org.cmdbuild.auth.user.LoginUser;

public interface ClientAuthenticatorResponse {

    @Nullable
    LoginUser getUserOrNull();

    @Nullable
    String getRedirectUrlOrNull();

    default boolean isAuthenticated() {
        return getUserOrNull() != null;
    }

}
