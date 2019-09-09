/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.utils;

import org.cmdbuild.etl.ImportExportTemplateTarget;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;

public class ImportExportTemplateTargetUtils {

    public static String serializeImportExportTemplateTarget(ImportExportTemplateTarget target) {
        return target.name().replaceFirst("IET_", "").toLowerCase();
    }

    public static ImportExportTemplateTarget parseImportExportTemplateTarget(String value) {
        return parseEnum(value, ImportExportTemplateTarget.class);
    }
}
