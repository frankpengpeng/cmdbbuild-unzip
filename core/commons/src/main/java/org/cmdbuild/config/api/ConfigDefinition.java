/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.api;

import static com.google.common.base.Objects.equal;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.config.api.ConfigLocation.CL_DEFAULT;
import static org.cmdbuild.config.api.ConfigLocation.CL_FILE_ONLY;

public interface ConfigDefinition {

    String getKey();

    @Nullable
    String getDefaultValue();

    String getDescription();

    boolean isProtected();

    ConfigLocation getLocation();

    ConfigCategory getCategory();

    default boolean isLocationFileOnly() {
        return equal(getLocation(), CL_FILE_ONLY);
    }

    default boolean isLocationDefault() {
        return equal(getLocation(), CL_DEFAULT);
    }

    default boolean hasDescription() {
        return isNotBlank(getDescription());
    }
}
