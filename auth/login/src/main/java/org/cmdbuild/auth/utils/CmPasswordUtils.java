/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.utils;

import java.lang.invoke.MethodHandles;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.auth.login.PasswordAlgo;
import static org.cmdbuild.auth.login.PasswordAlgo.PA_CM3;
import static org.cmdbuild.auth.login.PasswordAlgo.PA_CM3EASY;
import static org.cmdbuild.auth.login.PasswordAlgo.PA_LEGACY;
import org.cmdbuild.utils.crypto.Cm3EasyCryptoUtils;
import org.cmdbuild.utils.crypto.Cm3PasswordUtils;
import org.cmdbuild.utils.crypto.CmLegacyPasswordUtils;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmPasswordUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Nullable
    public static String encryptPassword(@Nullable String plaintextPassword, PasswordAlgo algo) {
        if (isBlank(plaintextPassword)) {
            return null;
        } else {
            switch (algo) {
                case PA_CM3:
                    return Cm3PasswordUtils.hash(plaintextPassword);
                case PA_CM3EASY:
                    return Cm3EasyCryptoUtils.encryptValue(plaintextPassword);
                case PA_LEGACY:
                    return CmLegacyPasswordUtils.encrypt(plaintextPassword);
                default:
                    throw new UnsupportedOperationException("unsupported password algorythm = " + algo);
            }
        }
    }

    @Nullable
    public static String decryptPasswordIfPossible(@Nullable String password) {
        if (isBlank(password)) {
            return null;
        } else if (Cm3PasswordUtils.hasMagic(password)) {
            LOGGER.trace("password encrypted with CMV3, unable to decrypt");
            return null;
        } else if (Cm3EasyCryptoUtils.isEncrypted(password)) {
            LOGGER.trace("password encrypted with CM3EASY");
            return Cm3EasyCryptoUtils.decryptValue(password);
        } else {
            LOGGER.trace("password encrypted with CMLEGACY");
            return CmLegacyPasswordUtils.decrypt(password);
        }
    }

    public static PasswordAlgo detectPasswordAlgo(String password) {
        checkNotBlank(password);
        if (Cm3PasswordUtils.hasMagic(password)) {
            return PA_CM3;
        } else if (Cm3EasyCryptoUtils.isEncrypted(password)) {
            return PA_CM3EASY;
        } else {
            return PA_LEGACY;
        }
    }
}
