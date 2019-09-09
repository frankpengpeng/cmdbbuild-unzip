/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.user;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.auth.login.AuthenticationConfiguration;
import org.cmdbuild.auth.utils.CmPasswordUtils;
import org.cmdbuild.utils.crypto.Cm3EasyCryptoUtils;
import org.cmdbuild.utils.crypto.Cm3PasswordUtils;
import org.cmdbuild.utils.crypto.CmLegacyPasswordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UserPasswordServiceImpl implements UserPasswordService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final AuthenticationConfiguration config;

    public UserPasswordServiceImpl(AuthenticationConfiguration config) {
        this.config = checkNotNull(config);
    }

    @Override
    @Nullable
    public String encryptPassword(@Nullable String plaintextPassword) {
        return CmPasswordUtils.encryptPassword(plaintextPassword, config.getPreferredPasswordAlgorythm());
    }

    @Override
    public boolean verifyPassword(@Nullable String plaintextPassword, @Nullable String storedEncryptedPassword) {
        if (isBlank(plaintextPassword) || isBlank(storedEncryptedPassword)) {
            return false;
        } else if (Cm3PasswordUtils.hasMagic(storedEncryptedPassword)) {
            logger.trace("password encrypted with CMV3, unable to decrypt");
            return Cm3PasswordUtils.isValid(plaintextPassword, storedEncryptedPassword);
        } else if (Cm3EasyCryptoUtils.isEncrypted(storedEncryptedPassword)) {
            logger.trace("password encrypted with CM3EASY");
            return equal(plaintextPassword, Cm3EasyCryptoUtils.decryptValue(storedEncryptedPassword));
        } else {
            logger.trace("password encrypted with CMLEGACY");
            return equal(plaintextPassword, CmLegacyPasswordUtils.decrypt(storedEncryptedPassword));
        }
    }

    @Override
    @Nullable
    public String decryptPasswordIfPossible(@Nullable String password) {
        return CmPasswordUtils.decryptPasswordIfPossible(password);
    }

}
