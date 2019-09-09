/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.data;

import java.util.List;
import org.cmdbuild.etl.ImportExportTemplate;
import org.cmdbuild.etl.ImportExportTemplateTarget;

public interface ImportExportTemplateService {

    List<ImportExportTemplate> getAll();

    List<ImportExportTemplate> getAllForTarget(ImportExportTemplateTarget type, String name);

    List<ImportExportTemplate> getAllForTargetClassAndRelatedDomains(String classId);

    ImportExportTemplate getOne(long templateId);

    ImportExportTemplate getTemplateByName(String templateName);

    ImportExportTemplate create(ImportExportTemplate template);

    ImportExportTemplate update(ImportExportTemplate template);

    void delete(long templateId);

}
