/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.grant;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;
import javax.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface GrantData {

    @Nullable
    Long getId();

    PrivilegedObjectType getType();

    GrantMode getMode();

    @Nullable
    String getClassName();

    @Nullable
    Long getObjectId();

    long getRoleId();

    @Nullable
    String getPrivilegeFilter();

    @Nullable
    Map<String, String> getAttributePrivileges();

    @Nullable
    Map<String, Object> getCustomPrivileges();

    default Object getObjectIdOrClassName() {
        switch (getType()) {
            case POT_CLASS:
                return checkNotBlank(getClassName());
            case POT_VIEW:
            case POT_FILTER:
            case POT_CUSTOMPAGE:
            case POT_REPORT:
            case POT_IMPORT_EXPORT_TEMPLATE:
                return checkNotNull(getObjectId());
            default:
                throw unsupported("unsupported grant type = %s", getType());
        }
    }

}
