/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl;

import java.util.List;
import javax.activation.DataSource;
import org.cmdbuild.etl.data.ImportExportTemplateService;

public interface ImportExportService extends ImportExportTemplateService, ImportExportProcessorService {

    default DataSource exportDataWithTemplate(long templateId) {
        return exportDataWithTemplate(getOne(templateId));
    }

    default ImportExportOperationResult importDataWithTemplate(DataSource data, long templateId) {
        return importDataWithTemplate(data, getOne(templateId));
    }

    List<ImportExportTemplate> getAllForUser();

    List<ImportExportTemplate> getForUserForTargetClassAndRelatedDomains(String classId);

    List<ImportExportTemplate> getForUserForTarget(ImportExportTemplateTarget target, String classId);

}
