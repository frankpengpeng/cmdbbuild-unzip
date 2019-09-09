/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.classe.access;

import com.google.common.base.Joiner;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import static com.google.common.collect.Lists.transform;
import static com.google.common.collect.Maps.filterKeys;
import static com.google.common.collect.Maps.uniqueIndex;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.auth.grant.GrantUtils.mergePrivilegeGroups;
import org.cmdbuild.auth.grant.GroupOfPrivileges;
import org.cmdbuild.auth.grant.UserPrivilegesForObject;
import org.cmdbuild.auth.role.RolePrivilege;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;
import static org.cmdbuild.classe.access.UserClassUtils.applyPrivilegesToClass;
import static org.cmdbuild.classe.access.userCardAccessUtils.buildFilterMarkName;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.entrytype.AttributePermission.AP_CREATE;
import static org.cmdbuild.dao.entrytype.AttributePermission.AP_UPDATE;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_CREATE;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_DELETE;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_READ;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.user.UserDaoHelperService;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.beans.CompositeFilterImpl;
import org.cmdbuild.data.filter.utils.CmdbFilterUtils;
import static org.cmdbuild.data.filter.utils.CmdbFilterUtils.noopFilter;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.springframework.stereotype.Component;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDTENANT;
import org.cmdbuild.dao.core.q3.QueryBuilder;
import static org.cmdbuild.dao.core.q3.WhereOperator.EQ;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class UserCardServiceImpl implements UserCardService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final UserDaoHelperService userHelper;
    private final UserClassService classService;
    private final DaoService dao;

    public UserCardServiceImpl(UserDaoHelperService userHelper, UserClassService classService, DaoService dao) {
        this.userHelper = checkNotNull(userHelper);
        this.classService = checkNotNull(classService);
        this.dao = checkNotNull(dao);
    }

    @Override
    public Card getUserCard(String classId, long cardId) {
        UserCardAccess cardAccess = getUserCardAccess(classId);
        Card card = cardAccess.addCardAccessPermissionsFromSubfilterMark(dao.selectAll().from(classId)
                .accept(cardAccess.addSubsetFilterMarkersToQueryVisitor())
                .where(cardAccess.getWholeClassFilter())
                .where(ATTR_ID, EQ, checkNotNull(cardId))
                .getCard());
        checkArgument(card.getType().hasServiceReadPermission(), "user not authorized to access card %s.%s", classId, cardId);
        return card;
    }

    @Override
    public Card createCard(String classId, Map<String, Object> values) {
        Classe classe = classService.getUserClass(classId);//TODO apply filters on values before card create
        checkArgument(classe.hasServicePermission(CP_CREATE), "permission denied: cannot create card of class = %s", classe);
        Card card = CardImpl.buildCard(classe, map(values).filterKeys(classe::hasAttribute).filterKeys((s) -> classe.getAttribute(s).hasServicePermission(AP_CREATE) || hasSpecialAccessPermission(classe, s)));
        checkSpecialWriteConstraint(card);
        return dao.create(card);
    }

    @Override
    public Card updateCard(String classId, long cardId, Map<String, Object> values) {
        Card card = getUserCard(classId, cardId);//TODO apply filters on values before card update
        Classe classe = card.getType();
        card = CardImpl.copyOf(card).withAttributes(map(values).filterKeys(classe::hasAttribute).filterKeys((s) -> classe.getAttribute(s).hasServicePermission(AP_UPDATE) || hasSpecialAccessPermission(classe, s))).build();
        checkSpecialWriteConstraint(card);
        return dao.update(card);
    }

    @Override
    public void deleteCard(String classId, long cardId) {
        Card card = getUserCard(classId, cardId);
        checkArgument(card.getType().hasServicePermission(CP_DELETE), "permission denied: cannot delete card = %s", card);
        dao.delete(card);
    }

    @Override
    public UserCardAccess getUserCardAccess(String classId) {
        Classe classe = classService.getUserClass(classId);
        checkArgument(classe.hasServicePermission(CP_READ), "permission denied: cannot read cards of class = %s", classe);
        UserPrivilegesForObject privilegeGroups = userHelper.getPrivilegesForObject(classe);
        Set<RolePrivilege> rolePrivileges = userHelper.getRolePrivileges();
        CmdbFilter wholeClassFilter;
        List<UserCardAccessWithFilterImpl> subsetFilters;
        if (privilegeGroups.hasPrivilegesWithFilter()) {
            subsetFilters = privilegeGroups.getPrivilegeGroupsWithFilter().stream().map((p) -> {
                CmdbFilter filter = p.getFilter();//TODO check filter conversion
                return new UserCardAccessWithFilterImpl(p.getSource(), filter, p);//TODO check that p.source is unique
            }).collect(toList());
            if (applyPrivilegesToClass(rolePrivileges, privilegeGroups.getMinPrivilegesForAllRecords(), classe).hasServicePermission(CP_READ)) {
                wholeClassFilter = noopFilter();
            } else {
                wholeClassFilter = CompositeFilterImpl.or(transform(subsetFilters, UserCardAccessWithFilter::getFilter));
            }
        } else {
            wholeClassFilter = noopFilter();
            subsetFilters = emptyList();
        }
        return new UserCardAccessImpl(classe, rolePrivileges, privilegeGroups.getMinPrivilegesForAllRecords(), wholeClassFilter, subsetFilters);
    }

//    private GroupOfPrivileges getPrivilegesForCard(UserPrivilegesForObject privilegeGroups, Card card) {
//        
//        UserCardAccess cardAccess = getUserCardAccess(classId);
//        
//		Card card = cardAccess.addCardAccessPermissionsFromSubfilterMark(dao.selectAll().from(classId)
//				.accept(cardAccess.addSubsetFilterMarkersToQueryVisitor())
//				.where(cardAccess.getWholeClassFilter())
//				.where(ATTR_ID, EQ, checkNotNull(cardId))
//				.getCard());
//		checkArgument(card.getType().hasServiceReadPermission(), "user not authorized to access card %s.%s", classId, cardId);
//        
//        privilegeGroups.getPrivilegeGroupsWithFilter()
//        //TODO run filter query, check that card is in, etc
//        throw new UnsupportedOperationException("TODO");
//    }
//    private Card filterCardWithPrivileges(GroupOfPrivileges grantPrivileges, Card card) {
//        Classe classe = applyPrivilegesToClass(userHelper.getRolePrivileges(), grantPrivileges, card.getType());
//        return CardImpl.copyOf(card).withType(classe).build();
//    }
    private boolean hasSpecialAccessPermission(Classe classe, String attr) {
        return equal(attr, ATTR_IDTENANT) && classe.hasMultitenantEnabled();
    }

    private void checkSpecialWriteConstraint(Card card) {
        if (card.getType().hasMultitenantEnabled()) {
            Long tenantId = card.getTenantId();
            checkArgument(tenantId == null || userHelper.canAccessTenant(tenantId), "permission denied: user is not authorized to access tenant = %s", tenantId);
        }
    }

    private static class UserCardAccessImpl implements UserCardAccess {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        private final Classe baseClass;
        private final Set<RolePrivilege> rolePrivileges;
        private final GroupOfPrivileges basePrivileges;
        private final CmdbFilter wholeClassFilter;
        private final Map<String, UserCardAccessWithFilterImpl> subsetFiltersByName;
        private final Cache<String, Classe> userClassCache = CacheBuilder.newBuilder().build();

        public UserCardAccessImpl(Classe baseClass, Set<RolePrivilege> rolePrivileges, GroupOfPrivileges basePrivileges, CmdbFilter wholeClassFilter, List<UserCardAccessWithFilterImpl> subsetFilters) {
            this.baseClass = checkNotNull(baseClass);
            this.rolePrivileges = checkNotNull(rolePrivileges);
            this.wholeClassFilter = checkNotNull(wholeClassFilter);
            this.basePrivileges = checkNotNull(basePrivileges);
            this.subsetFiltersByName = uniqueIndex(subsetFilters, UserCardAccessWithFilter::getName);
        }

        @Override
        public CmdbFilter getWholeClassFilter() {
            return wholeClassFilter;
        }

        @Override
        public Map<String, UserCardAccessWithFilter> getSubsetFiltersByName() {
            return (Map) subsetFiltersByName;
        }

        @Override
        public Classe getUserClass(Set<String> activeFilters) {
            try {
                return userClassCache.get(key(activeFilters), () -> {
                    GroupOfPrivileges privileges = mergePrivilegeGroups(list(basePrivileges).with(filterKeys(subsetFiltersByName, activeFilters::contains).values().stream().map(UserCardAccessWithFilterImpl::getPrivileges)))
                            .withSource("filters_" + Joiner.on("+").join(activeFilters)).build();
                    return applyPrivilegesToClass(rolePrivileges, privileges, baseClass);
                });
            } catch (ExecutionException ex) {
                throw runtime(ex);
            }
        }

        @Override
        public Consumer<QueryBuilder> addSubsetFilterMarkersToQueryVisitor() {
            return (q) -> {
                getSubsetFiltersByName().forEach((key, a) -> {
                    String mark = buildFilterMarkName(key);
                    logger.debug("add query mark =< {} > for filter = {} {}", mark, a.getName(), a.getFilter());
                    q.selectMatchFilter(mark, a.getFilter());
                });
            };
        }

    }

    private static class UserCardAccessWithFilterImpl implements UserCardAccessWithFilter {

        final String name;
        final CmdbFilter filter;
        final GroupOfPrivileges privileges;

        public UserCardAccessWithFilterImpl(String name, CmdbFilter filter, GroupOfPrivileges privileges) {
            this.name = checkNotBlank(name);
            this.filter = checkNotNull(filter);
            this.privileges = checkNotNull(privileges);
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public CmdbFilter getFilter() {
            return filter;
        }

        public GroupOfPrivileges getPrivileges() {
            return privileges;
        }

    }

}
