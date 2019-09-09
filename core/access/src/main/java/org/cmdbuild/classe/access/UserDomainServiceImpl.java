/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.classe.access;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.isNull;
import static com.google.common.base.Predicates.not;
import com.google.common.collect.ComparisonChain;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Maps.uniqueIndex;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.cmdbuild.classe.access.UserClassUtils.ROLE_PRIVILEGES_TO_CLASS_PERMISSIONS;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.common.utils.PagedElements.paged;
import org.cmdbuild.dao.beans.CMRelation;
import static org.cmdbuild.dao.beans.CardIdAndClassNameImpl.card;
import org.cmdbuild.dao.beans.RelationImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.entrytype.ClassPermission;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_DELETE;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_READ;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_UPDATE;
import org.cmdbuild.dao.entrytype.ClassPermissionsImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.DomainImpl;
import static org.cmdbuild.dao.entrytype.PermissionScope.PS_SERVICE;
import org.cmdbuild.dao.user.UserDaoHelperService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.springframework.stereotype.Component;

@Component
public class UserDomainServiceImpl implements UserDomainService {

    private final UserDaoHelperService userHelper;
    private final UserClassService userClassService;
    private final DaoService dao;

    public UserDomainServiceImpl(UserDaoHelperService userHelper, UserClassService userClassService, DaoService dao) {
        this.userHelper = checkNotNull(userHelper);
        this.userClassService = checkNotNull(userClassService);
        this.dao = checkNotNull(dao);
    }

    @Override
    public List<Domain> getUserDomains() {
        UserDomainPermissionHelper helper = new UserDomainPermissionHelper();
        return dao.getAllDomains().stream().map(helper::toUserDomain).filter(Domain::hasServiceListPermission).collect(toList());
    }

    @Override
    public Domain getUserDomain(String domainId) {
        Domain userDomain = new UserDomainPermissionHelper().toUserDomain(dao.getDomain(domainId));
        checkArgument(userDomain.hasServiceListPermission(), "user is not allowed to read domain = %s", userDomain);
        return userDomain;
    }

    @Override
    public PagedElements<CMRelation> getUserRelations(String domainId, DaoQueryOptions queryOptions) {
        Domain domain = getUserDomain(domainId);
        checkArgument(domain.hasServiceReadPermission(), "user is not allowed to read relations for domain = %s", domain);
        List<CMRelation> list = dao.selectAll().from(domain).withOptions(queryOptions).getRelations().stream().map(r -> fixRelationPermissions(domain, r)).collect(toImmutableList());
        if (queryOptions.isPaged()) {
            int count = dao.selectCount().from(domain).where(queryOptions.getFilter()).getCount();
            return paged(list, count);
        } else {
            return paged(list);
        }
    }

    @Override
    public PagedElements<CMRelation> getUserRelationsForCard(String classId, long cardId, DaoQueryOptions queryOptions) {

        List<CMRelation> relations = list(dao.getServiceRelationsForCard(card(classId, cardId)));
//
        Collections.sort(relations, (r1, r2) -> {
            Domain d1 = r1.getDomainWithThisRelationDirection(),
                    d2 = r2.getDomainWithThisRelationDirection();

            return ComparisonChain.start()
                    .compare(d1.getIndexForSource(), d2.getIndexForSource())//TODO check this
                    .compare(d1.getName(), d2.getName())
                    .compare(r1.getTargetCard().getClassName(), r2.getTargetCard().getClassName())
                    .result();

        });
//
        if (queryOptions.isPaged()) {
            return paged(relations, queryOptions.getOffset(), queryOptions.getLimit());
        } else {
            return paged(relations);
        }
    }

    @Override
    public CMRelation getUserRelation(String domainId, long relationId) {
        Domain domain = getUserDomain(domainId);
        checkArgument(domain.hasServiceReadPermission(), "user is not allowed to read relations for domain = %s", domain);
        CMRelation relation = dao.selectAll().from(domain).where(ATTR_ID, EQ, relationId).getRelation();
        return fixRelationPermissions(domain, relation);
    }

    private CMRelation fixRelationPermissions(Domain domain, CMRelation relation) {
        Classe source = userClassService.getUserClass(relation.getSourceClassName()),//TODO user card ?? performance ??
                target = userClassService.getUserClass(relation.getTargetClassName());//TODO user card ?? performance ??
        boolean hasSourceReference = source.hasReferenceForDomain(domain),
                hasTargetReference = target.hasReferenceForDomain(domain);
        boolean hasWritePermissionFromClasses;
        //TODO
//        if(source.hasServiceWritePermission() && target.hasServiceWritePermission()){
//            hasWritePermissionFromClasses=true;
//        }
        //TODO check reference attrs
        boolean canUpdate = domain.hasServicePermission(CP_UPDATE),
                canDelete = domain.hasServicePermission(CP_DELETE);

        if (!relation.canReadSource() || !relation.canReadTarget()) {
            canUpdate = false;
            canDelete = false;
        }
        Set<ClassPermission> permissions = EnumSet.of(CP_READ);
        if (canUpdate) {
            permissions.add(CP_UPDATE);
        }
        if (canDelete) {
            permissions.add(CP_DELETE);
        }
        Domain userRelationDomain = DomainImpl.copyOf(domain).withPermissions(ClassPermissionsImpl.copyOf(domain).intersectPermissions(PS_SERVICE, permissions).build()).build();
        return RelationImpl.copyOf(relation).withType(userRelationDomain).build();
    }

    private class UserDomainPermissionHelper {

        private final Map<String, Classe> userClasses;

        public UserDomainPermissionHelper() {
            this.userClasses = uniqueIndex(userClassService.getAllUserClasses(), Classe::getName);
        }

        public Domain toUserDomain(Domain domain) {
            Set<ClassPermission> rolePermissions = userHelper.getRolePrivileges().stream().map(ROLE_PRIVILEGES_TO_CLASS_PERMISSIONS::get).filter(not(isNull())).collect(toSet());

            //TODO fix this
//			ClassPermissions sourcePermissions = domain.getSourceClasses().stream().map(Classe::getName).map(userClasses::get).filter(not(isNull())).map(ClassPermissions.class::cast).reduce((a, b) -> ClassPermissionsImpl.copyOf(a).addPermissions(b).build()).orElse(ClassPermissionsImpl.none());
//			ClassPermissions targetPermissions = domain.getTargetClasses().stream().map(Classe::getName).map(userClasses::get).filter(not(isNull())).map(ClassPermissions.class::cast).reduce((a, b) -> ClassPermissionsImpl.copyOf(a).addPermissions(b).build()).orElse(ClassPermissionsImpl.none());
//			ClassPermissions userPermissions = ClassPermissionsImpl.copyOf(sourcePermissions).intersectPermissions(targetPermissions).addPermissions(PS_SERVICE, rolePermissions).build();
//			return DomainImpl.copyOf(domain).withPermissions(ClassPermissionsImpl.copyOf(domain).intersectPermissions(userPermissions).build()).build();
            return domain;
        }

    }
}
