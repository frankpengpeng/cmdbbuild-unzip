/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.grant;

import com.google.common.base.Function;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Supplier;
import com.google.common.collect.ComparisonChain;
import static com.google.common.collect.Maps.transformValues;
import com.google.common.eventbus.Subscribe;
import java.util.Collection;
import java.util.Collections;
import static java.util.Collections.emptySet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import static java.util.stream.Collectors.toSet;
import javax.annotation.Nullable;
import org.cmdbuild.auth.grant.GrantDataImpl.GrantDataImplBuilder;
import org.cmdbuild.auth.grant.GrantEventBusService.GrantDataUpdatedEvent;
import org.cmdbuild.cache.CacheService;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;
import org.cmdbuild.cardfilter.CardFilterService;
import static org.cmdbuild.common.error.ErrorAndWarningCollectorService.marker;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.utils.lang.CmCollectionUtils.FluentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static org.cmdbuild.auth.grant.GrantMode.GM_NONE;
import static org.cmdbuild.auth.grant.GrantPrivilege.GP_READ;
import static org.cmdbuild.auth.grant.GrantPrivilege.GP_WRITE;
import static org.cmdbuild.auth.grant.GrantUtils.expandPrivileges;
import static org.cmdbuild.auth.grant.PrivilegedObjectType.POT_CLASS;
import static org.cmdbuild.auth.grant.PrivilegedObjectType.POT_CUSTOMPAGE;
import static org.cmdbuild.auth.grant.PrivilegedObjectType.POT_FILTER;
import static org.cmdbuild.auth.grant.PrivilegedObjectType.POT_IMPORT_EXPORT_TEMPLATE;
import static org.cmdbuild.auth.grant.PrivilegedObjectType.POT_REPORT;
import static org.cmdbuild.auth.grant.PrivilegedObjectType.POT_VIEW;
import org.cmdbuild.authorization.CardFilterAsPrivilegeSubject;
import org.cmdbuild.extcomponents.custompage.CustomPageService;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.report.ReportService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBoolean;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.nullToEmpty;
import org.cmdbuild.cache.CmCache;
import static org.cmdbuild.common.Constants.BASE_CLASS_NAME;
import static org.cmdbuild.common.Constants.BASE_PROCESS_CLASS_NAME;
import org.cmdbuild.etl.data.ImportExportTemplateService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.view.ViewDefinitionService;

@Component
public class GrantServiceImpl implements GrantService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final GrantDataRepository repository;
    private final DaoService dao;
    private final ViewDefinitionService viewService;
    private final CardFilterService filterStore;
    private final CustomPageService customPagesService;
    private final ReportService reportService;
    private final CmCache<Collection<Grant>> privilegePairListByTypeAndGroupId;
    private final ImportExportTemplateService importExportTemplateService;

    public GrantServiceImpl(ImportExportTemplateService importExportTemplateService, GrantEventBusService grantEventBus, ReportService reportService, GrantDataRepository repository, DaoService dao, ViewDefinitionService viewService, CardFilterService filterStore, CustomPageService customPagesService, CacheService cacheService) {
        this.repository = checkNotNull(repository);
        this.dao = checkNotNull(dao);
        this.viewService = checkNotNull(viewService);
        this.filterStore = checkNotNull(filterStore);
        this.customPagesService = checkNotNull(customPagesService);
        this.reportService = checkNotNull(reportService);
        this.importExportTemplateService = checkNotNull(importExportTemplateService);
        privilegePairListByTypeAndGroupId = cacheService.newCache("cache_privilege_pair_list_by_type_and_group_id");
        grantEventBus.getEventBus().register(new Object() {

            @Subscribe
            public void handleGrantDataUpdatedEvent(GrantDataUpdatedEvent event) {
                privilegePairListByTypeAndGroupId.invalidateAll();
            }
        });
    }

    @Override
    public List<GrantData> getGrantsForRoleIncludeRecordsWithoutGrant(long roleId) {
        List<GrantData> grantsForRole = repository.getGrantsForRole(roleId);
        Set<String> grantsForRoleKeys = grantsForRole.stream().map((g) -> key(g.getType(), g.getObjectIdOrClassName())).collect(toSet());
        Supplier<GrantDataImplBuilder> builder = () -> GrantDataImpl.builder().withMode(GM_NONE).withRoleId(roleId);
        List<GrantData> list = list(grantsForRole);

        dao.getAllClasses().stream().filter(c -> c.hasServiceListPermission())
                .filter(c -> !equal(c.getName(), BASE_CLASS_NAME) && !equal(c.getName(), BASE_PROCESS_CLASS_NAME)) //TODO improve this
                .filter((c) -> !grantsForRoleKeys.contains(key(POT_CLASS, c.getName()))).map((c) -> builder.get().withType(POT_CLASS).withClassName(c.getName()).build()).forEach(list::add);

        customPagesService.getAll().stream().filter((c) -> !grantsForRoleKeys.contains(key(POT_CUSTOMPAGE, c.getId()))).map((c) -> builder.get().withType(POT_CUSTOMPAGE).withObjectId(c.getId()).build()).forEach(list::add);

        filterStore.readAllSharedFilters().stream().filter((f) -> !grantsForRoleKeys.contains(key(POT_FILTER, f.getId()))).map((f) -> builder.get().withType(POT_FILTER).withObjectId(f.getId()).build()).forEach(list::add);

        reportService.getAll().stream().filter((r) -> !grantsForRoleKeys.contains(key(POT_REPORT, r.getId()))).map((r) -> builder.get().withType(POT_REPORT).withObjectId(r.getId()).build()).forEach(list::add);

        viewService.getAllViews().stream().filter((v) -> !grantsForRoleKeys.contains(key(POT_VIEW, v.getId()))).map((v) -> builder.get().withType(POT_VIEW).withObjectId(v.getId()).build()).forEach(list::add);

        importExportTemplateService.getAll().stream().filter((v) -> !grantsForRoleKeys.contains(key(POT_IMPORT_EXPORT_TEMPLATE, v.getId()))).map((v) -> builder.get().withType(POT_IMPORT_EXPORT_TEMPLATE).withObjectId(v.getId()).build()).forEach(list::add);

        Collections.sort(list, (a, b) -> ComparisonChain.start().compare(a.getType(), b.getType()).compare(a.getObjectIdOrClassName().toString(), b.getObjectIdOrClassName().toString()).result());
        return list;
    }

    @Override
    public @Nullable
    String getGrantObjectDescription(GrantData grant) {
        try {
            switch (grant.getType()) {
                case POT_CLASS:
                    return dao.getClasse(grant.getClassName()).getDescription();
                case POT_CUSTOMPAGE:
                    return customPagesService.get(grant.getObjectId()).getDescription();
                case POT_FILTER:
                    return filterStore.getById(grant.getObjectId()).getDescription();
                case POT_REPORT:
                    return reportService.getById(grant.getObjectId()).getDescription();
                case POT_VIEW:
                    return viewService.getForCurrentUserById(grant.getObjectId()).getDescription();
                case POT_IMPORT_EXPORT_TEMPLATE:
                    return importExportTemplateService.getOne(grant.getObjectId()).getDescription();
                default:
                    throw new IllegalArgumentException("unsupported grant type = " + grant.getType());
            }
        } catch (Exception ex) {
            logger.warn(marker(), "error retrieving description for grant record = {}", grant, ex);
            return null;
        }
    }

    @Override
    public Collection<Grant> getAllPrivilegesByGroupId(long groupId) {
        return list(getClassPrivilegesByGroupId(groupId))
                .with(getViewPrivilegesByGroupId(groupId))
                .with(getFilterPrivilegesByGroupId(groupId))
                .with(getCustomPagesPrivilegesByGroupId(groupId))
                .with(getReportPrivilegesByGroupId(groupId))
                .with(getImportExportTemplatePrivilegesByGroupId(groupId));
    }

    @Override
    public Collection<Grant> getClassPrivilegesByGroupId(long groupId) {
        return getPrivilegesByGroupId(PrivilegedObjectType.POT_CLASS, groupId, (p) -> dao.getClasse(p.getClassName()));
    }

    @Override
    public Collection<Grant> getViewPrivilegesByGroupId(long groupId) {
        return getPrivilegesByGroupId(PrivilegedObjectType.POT_VIEW, groupId, (p) -> viewService.getForCurrentUserById(p.getObjectId()));//TODO refactor like report service below
    }

    @Override
    public Collection<Grant> getFilterPrivilegesByGroupId(long groupId) {
        return getPrivilegesByGroupId(PrivilegedObjectType.POT_FILTER, groupId, (p) -> new CardFilterAsPrivilegeSubject(filterStore.getById(p.getObjectId())));//TODO refactor like report service below
    }

    @Override
    public Collection<Grant> getCustomPagesPrivilegesByGroupId(long groupId) {
        return getPrivilegesByGroupId(PrivilegedObjectType.POT_CUSTOMPAGE, groupId, (p) -> customPagesService.getCustomPageAsPrivilegeSubjectById(p.getObjectId()));
    }

    @Override
    public Collection<Grant> getReportPrivilegesByGroupId(long groupId) {
        return getPrivilegesByGroupId(PrivilegedObjectType.POT_REPORT, groupId, (p) -> reportService.getReportAsPrivilegeSubjectById(p.getObjectId()));
    }

    private Collection<Grant> getImportExportTemplatePrivilegesByGroupId(long groupId) {
        return getPrivilegesByGroupId(PrivilegedObjectType.POT_IMPORT_EXPORT_TEMPLATE, groupId, (p) -> importExportTemplateService.getOne(p.getObjectId()));
    }

    private Collection<Grant> getPrivilegesByGroupId(PrivilegedObjectType type, long groupId, Function<GrantData, PrivilegeSubjectWithInfo> fun) {
        return privilegePairListByTypeAndGroupId.get(key(type.name(), Long.toString(groupId)), () -> doGetPrivilegesByGroupId(type, groupId, fun));
    }

    private Collection<Grant> doGetPrivilegesByGroupId(PrivilegedObjectType type, long groupId, Function<GrantData, PrivilegeSubjectWithInfo> fun) {
        FluentList<Grant> res = list();
        repository.getGrantsForTypeAndRole(type, groupId).forEach((p) -> {
            try {
                PrivilegeSubjectWithInfo object = checkNotNull(fun.apply(p));
                res.add(toGrant(p, object));
            } catch (Exception ex) {
                logger.warn(marker(), "error processing grant record = {}", p, ex);
            }
        });
        return res.immutable();
    }

    private Grant toGrant(GrantData grant, PrivilegeSubjectWithInfo object) {
        return GrantImpl.builder()
                .withObject(object)
                .withObjectType(grant.getType())
                .withPrivilegeFilter(grant.getPrivilegeFilter())
                .accept((b) -> {
                    Set<GrantPrivilege> privileges = modeToPrivileges(grant.getMode());
                    if (equal(POT_CLASS, grant.getType())) {
                        Classe classe = dao.getClasse(object.getName());
                        privileges = set(privileges);
                        nullToEmpty(grant.getCustomPrivileges()).entrySet().stream().filter((e) -> toBoolean(e.getValue()) == false).map(Entry::getKey).map((v) -> parseEnum(v, GrantPrivilege.class)).forEach(privileges::remove);
                        b.withPrivileges(privileges).withAttributePrivileges(map(transformValues(nullToEmpty(grant.getAttributePrivileges()), GrantUtils::parseGrantAttributePrivilege)));
                    } else {
                        b.withPrivileges(privileges);
                    }
                })
                .build();
    }

    private Set<GrantPrivilege> modeToPrivileges(GrantMode mode) {
        switch (mode) {
            case GM_WRITE:
                return expandPrivileges(GP_WRITE);
            case GM_READ:
                return expandPrivileges(GP_READ);
            default:
                return emptySet();
        }
    }

    @Override
    public GrantData getGrantDataByRoleAndTypeAndName(Long id, PrivilegedObjectType objectType, String objectTypeName) {
        GrantData grant;
        List<GrantData> grantData = getGrantsForRoleIncludeRecordsWithoutGrant(id);
        return grantData.stream().filter(p -> (p.getType().equals(objectType) && p.getObjectIdOrClassName().equals(objectTypeName))).findFirst().orElse(null);
    }

}
