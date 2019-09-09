/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.classe;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.cardfilter.CardFilterService;
import org.cmdbuild.cardfilter.StoredFilter;
import org.cmdbuild.classe.access.MetadataValidatorService;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.ClassMetadata;
import org.cmdbuild.etl.ImportExportTemplate;
import org.cmdbuild.etl.data.ImportExportTemplateService;
import org.springframework.stereotype.Component;

@Component
public class MetadataValidatorServiceImpl implements MetadataValidatorService {

    private final DaoService dao;
    private final CardFilterService filterService;
    private final ImportExportTemplateService importExportTemplateService;

    public MetadataValidatorServiceImpl(DaoService dao, CardFilterService filterService, ImportExportTemplateService importExportTemplateService) {
        this.dao = checkNotNull(dao);
        this.filterService = checkNotNull(filterService);
        this.importExportTemplateService = checkNotNull(importExportTemplateService);
    }

    @Override
    public void validateMedata(String classId, ClassMetadata metadata) {
        if (metadata.getDefaultFilterOrNull() != null) {
            StoredFilter filter = filterService.getById(metadata.getDefaultFilterOrNull());
            checkArgument(filter.isShared());
//            checkArgument(filter.isShared()&&filter.isForClass(dao.getClass))
        }
        if (metadata.getDefaultImportTemplateOrNull() != null) {
            ImportExportTemplate template = importExportTemplateService.getOne(metadata.getDefaultImportTemplateOrNull());
            checkArgument(template.isImportTemplate() && template.hasTarget(dao.getClasse(classId)), "invalid import template = %s for class = %s", template, classId);
        }
        if (metadata.getDefaultExportTemplateOrNull() != null) {
            ImportExportTemplate template = importExportTemplateService.getOne(metadata.getDefaultExportTemplateOrNull());
            checkArgument(template.isExportTemplate() && template.hasTarget(dao.getClasse(classId)), "invalid export template = %s for class = %s", template, classId);
        }
    }

}
