/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl;

import static java.lang.String.format;
import javax.annotation.Nullable;
import org.cmdbuild.auth.grant.PrivilegeSubjectWithInfo;
import org.cmdbuild.etl.data.ImportExportTemplateConfig;

public interface ImportExportTemplate extends ImportExportTemplateConfig, PrivilegeSubjectWithInfo {

    @Nullable
    @Override
    Long getId();

    String getCode();

    @Override
    String getDescription();

    boolean isActive();

    @Nullable
    Long getErrorEmailTemplateId();

    @Nullable
    Long getErrorEmailAccountId();

    @Override
    public default String getName() {
        return getCode();
    }

    @Override
    public default String getPrivilegeId() {
        return format("iet:%s", getId());
    }

}
