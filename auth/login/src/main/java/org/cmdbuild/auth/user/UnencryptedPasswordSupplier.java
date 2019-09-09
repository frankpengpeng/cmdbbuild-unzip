package org.cmdbuild.auth.user;

import javax.annotation.Nullable;
import org.cmdbuild.auth.login.LoginUserIdentity;
import org.cmdbuild.utils.lang.CmPreconditions;

public interface UnencryptedPasswordSupplier {

    @Nullable
    String getUnencryptedPasswordOrNull(LoginUserIdentity login);

    default String getUnencryptedPassword(LoginUserIdentity login) {
        return CmPreconditions.checkNotBlank(getUnencryptedPasswordOrNull(login), "unencrypted password is not available for login = %s", login);
    }

}
