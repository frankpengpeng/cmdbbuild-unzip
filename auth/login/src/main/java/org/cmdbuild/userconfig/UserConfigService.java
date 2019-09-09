/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.userconfig;

import com.google.common.base.Optional;
import java.util.Map;
import javax.annotation.Nullable;

public interface UserConfigService {

    Map<String, String> getByUsername(String username);

    void setByUsername(String username, Map<String, String> data);

    @Nullable
    Optional<String> getByUsername(String username, String key);

    void setByUsername(String username, String key, @Nullable String value);

    void deleteByUsername(String username, String key);

    @Nullable
    String getForCurrentUsernameOrNull(String key);

    Map<String, String> getForCurrentUsername();

    void setForCurrent(String key, String value);

    default void setByUsernameDeleteIfNull(String username, String key, @Nullable String value) {
        if (value == null) {
            deleteByUsername(username, key);
        } else {
            setByUsername(username, key, value);
        }
    }

    default @Nullable
    String getByUsernameOrNull(String username, String key) {
        Optional<String> optional = getByUsername(username, key);
        return optional == null ? null : optional.orNull();
    }

}
