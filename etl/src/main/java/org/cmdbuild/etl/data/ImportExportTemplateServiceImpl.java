/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.data;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import java.util.List;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.CmCache;
import org.cmdbuild.cache.Holder;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDOBJ1;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDOBJ2;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.EntryType;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.FOREIGNKEY;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.LOOKUP;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.REFERENCE;
import static org.cmdbuild.etl.ImportExportColumnMode.IECM_DEFAULT;
import org.cmdbuild.etl.ImportExportTemplate;
import org.cmdbuild.etl.ImportExportTemplateImpl;
import org.cmdbuild.etl.ImportExportTemplateTarget;
import static org.cmdbuild.etl.ImportExportTemplateTarget.IET_CLASS;
import static org.cmdbuild.etl.ImportExportTemplateTarget.IET_DOMAIN;
import static org.cmdbuild.etl.utils.ImportExportTemplateTargetUtils.serializeImportExportTemplateTarget;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class ImportExportTemplateServiceImpl implements ImportExportTemplateService {

    private final DaoService dao;
    private final CmCache<List<ImportExportTemplate>> templatesByTarget;
    private final CmCache<ImportExportTemplate> templatesById;
    private final Holder<List<ImportExportTemplate>> templates;

    public ImportExportTemplateServiceImpl(DaoService dao, CacheService cacheService) {
        this.dao = checkNotNull(dao);
        templatesByTarget = cacheService.newCache("ietemplates_by_target");
        templatesById = cacheService.newCache("ietemplates_by_id");
        templates = cacheService.newHolder("ietemplates_all");
    }

    private void invalidateCaches() {
        templatesByTarget.invalidateAll();
        templatesById.invalidateAll();
        templates.invalidate();
    }

    @Override
    public List<ImportExportTemplate> getAll() {
        return templates.get(() -> dao.selectAll().from(ImportExportTemplateData.class).asList(ImportExportTemplateData.class).stream().map(this::dataToTemplate).collect(toImmutableList()));
    }

    @Override
    public List<ImportExportTemplate> getAllForTarget(ImportExportTemplateTarget type, String name) {
        checkNotNull(type);
        checkNotBlank(name);
        return templatesByTarget.get(key(serializeImportExportTemplateTarget(type), name), () -> getAll().stream().filter(t -> equal(t.getTargetType(), type) && equal(t.getTargetName(), name)).collect(toImmutableList()));
    }

    @Override
    public List<ImportExportTemplate> getAllForTargetClassAndRelatedDomains(String classId) {
        Classe classe = dao.getClasse(classId);
        List<Domain> domains = dao.getDomainsForClasse(classe);
        return list(getAllForTarget(IET_CLASS, classe.getName())).accept(l -> domains.stream().map(d -> getAllForTarget(IET_DOMAIN, d.getName())).forEach(l::addAll));
    }

    @Override
    public ImportExportTemplate getOne(long templateId) {
        return templatesById.get(templateId, () -> getAll().stream().filter(t -> equal(t.getId(), templateId)).collect(onlyElement("import/export template not found for id = %s", templateId)));
    }

    @Override
    public ImportExportTemplate getTemplateByName(String templateName) {
        return getAll().stream().filter(t -> equal(t.getCode(), templateName)).collect(onlyElement("import/export template not found for name = %s", templateName));
    }

    @Override
    public ImportExportTemplate create(ImportExportTemplate template) {
        template = dataToTemplate(dao.create(templateToData(template)));
        invalidateCaches();
        return template;
    }

    @Override
    public ImportExportTemplate update(ImportExportTemplate template) {
        template = dataToTemplate(dao.update(templateToData(template)));
        invalidateCaches();
        return template;
    }

    @Override
    public void delete(long templateId) {
        dao.delete(ImportExportTemplateData.class, templateId);
        invalidateCaches();
    }

    private ImportExportTemplate dataToTemplate(ImportExportTemplateData data) {
        ImportExportTemplate template = ImportExportTemplateImpl.copyOf(data.getConfig())
                .withId(data.getId())
                .withActive(data.isActive())
                .withCode(data.getCode())
                .withDescription(data.getDescription())
                .withErrorEmailAccountId(data.getErrorEmailAccountId())
                .withErrorEmailTemplateId(data.getErrorEmailTemplateId())
                .build();
        validateTemplate(template);
        return template;
    }

    private ImportExportTemplateData templateToData(ImportExportTemplate template) {
        validateTemplate(template);
        return ImportExportTemplateDataImpl.builder()
                .withActive(template.isActive())
                .withCode(template.getCode())
                .withDescription(template.getDescription())
                .withErrorEmailAccountId(template.getErrorEmailAccountId())
                .withErrorEmailTemplateId(template.getErrorEmailTemplateId())
                .withId(template.getId())
                .withConfig(ImportExportTemplateConfigImpl.copyOf(template).build())
                .build();
    }

    private void validateTemplate(ImportExportTemplate template) {
        EntryType entryType;
        switch (template.getTargetType()) {
            case IET_CLASS:
                entryType = dao.getClasse(template.getTargetName());
                if (template.isImportTemplate()) {
                    checkArgument(!((Classe) entryType).isSuperclass(), "cannot create an import template on a super class");
                }
                break;
            case IET_DOMAIN:
                entryType = dao.getDomain(template.getTargetName());
                break;
            default:
                throw new IllegalArgumentException("unsupported target type = " + template.getTargetType());
        }
        template.getColumns().forEach(c -> {
            Attribute attribute = entryType.getAttribute(c.getAttributeName());
            if (attribute.isOfType(REFERENCE, LOOKUP, FOREIGNKEY) || set(ATTR_IDOBJ1, ATTR_IDOBJ2).contains(attribute.getName())) {
                checkArgument(!equal(c.getMode(), IECM_DEFAULT), "invalid column mode = %s for attribute = %s", c.getMode(), attribute);
            } else {
                checkArgument(equal(c.getMode(), IECM_DEFAULT), "invalid column mode = %s for attribute = %s", c.getMode(), attribute);
            }
        });
    }

}
