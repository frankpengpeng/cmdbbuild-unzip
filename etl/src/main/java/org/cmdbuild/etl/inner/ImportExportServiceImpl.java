/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.activation.DataSource;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.etl.ImportExportOperationResult;
import org.cmdbuild.etl.ImportExportProcessorService;
import org.cmdbuild.etl.ImportExportService;
import org.cmdbuild.etl.ImportExportTemplate;
import org.cmdbuild.etl.ImportExportTemplateTarget;
import org.cmdbuild.etl.data.ImportExportTemplateService;
import org.springframework.stereotype.Component;

@Component
public class ImportExportServiceImpl implements ImportExportService {

    private final ImportExportTemplateService templateService;
    private final ImportExportProcessorService processorService;
    private final OperationUserSupplier userSupplier;

    public ImportExportServiceImpl(ImportExportTemplateService templateService, ImportExportProcessorService processorService, OperationUserSupplier userSupplier) {
        this.templateService = checkNotNull(templateService);
        this.processorService = checkNotNull(processorService);
        this.userSupplier = checkNotNull(userSupplier);
    }

    @Override
    public List<ImportExportTemplate> getAllForUser() {
        return getAll().stream().filter(t -> userSupplier.hasPrivileges(p -> p.hasReadAccess(t))).collect(toList());
    }

    @Override
    public List<ImportExportTemplate> getForUserForTargetClassAndRelatedDomains(String classId) {
        return getAllForTargetClassAndRelatedDomains(classId).stream().filter(t -> userSupplier.hasPrivileges(p -> p.hasReadAccess(t))).collect(toList());
    }

    @Override
    public List<ImportExportTemplate> getForUserForTarget(ImportExportTemplateTarget target, String classId) {
        return getAllForTarget(target, classId).stream().filter(t -> userSupplier.hasPrivileges(p -> p.hasReadAccess(t))).collect(toList());
    }

    @Override
    public List<ImportExportTemplate> getAll() {
        return templateService.getAll();
    }

    @Override
    public List<ImportExportTemplate> getAllForTarget(ImportExportTemplateTarget type, String name) {
        return templateService.getAllForTarget(type, name);
    }

    @Override
    public List<ImportExportTemplate> getAllForTargetClassAndRelatedDomains(String classId) {
        return templateService.getAllForTargetClassAndRelatedDomains(classId);
    }

    @Override
    public ImportExportTemplate getOne(long templateId) {
        return templateService.getOne(templateId);
    }

    @Override
    public ImportExportTemplate create(ImportExportTemplate template) {
        return templateService.create(template);
    }

    @Override
    public ImportExportTemplate update(ImportExportTemplate template) {
        return templateService.update(template);
    }

    @Override
    public void delete(long templateId) {
        templateService.delete(templateId);
    }

    @Override
    public DataSource exportDataWithTemplate(ImportExportTemplate template) {
        return processorService.exportDataWithTemplate(template);
    }

    @Override
    public ImportExportOperationResult importDataWithTemplate(DataSource data, ImportExportTemplate template) {
        return processorService.importDataWithTemplate(data, template);
    }

    @Override
    public ImportExportTemplate getTemplateByName(String templateName) {
        return templateService.getTemplateByName(templateName);
    }

}
