/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.data;

import javax.annotation.Nullable; 

public interface ImportExportTemplateData {

    @Nullable
    Long getId();

    String getCode();

    String getDescription();

    boolean isActive();

    ImportExportTemplateConfig getConfig();

    @Nullable
    Long getErrorEmailTemplateId();

    @Nullable
    Long getErrorEmailAccountId();
}
