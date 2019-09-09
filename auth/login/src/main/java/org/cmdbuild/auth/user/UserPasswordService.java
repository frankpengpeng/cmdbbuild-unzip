/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.user;

import javax.annotation.Nullable;

public interface UserPasswordService {

    @Nullable
    String encryptPassword(@Nullable String plaintextPassword);

    boolean verifyPassword(@Nullable String plaintextPassword, @Nullable String storedEncryptedPassword);

    @Nullable
    String decryptPasswordIfPossible(@Nullable String password);
}
