/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.jobs;

import static com.google.common.base.Objects.equal;
import java.util.Map;
import javax.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface JobData {

    @Nullable
    Long getId();

    String getCode();

    String getDescription();

    String getType();

    boolean isEnabled();

    Map<String, Object> getConfig();

    default @Nullable
    String getCronExpression() {
        return (String) getConfig().get("cronExpression");
    }

    default boolean isOfType(String type) {
        return equal(getType(), type);
    }

    default String getConfigNotBlank(String key) {
        return checkNotBlank((String) getConfig().get(key), "config not found for key = %s", key);
    }
}
