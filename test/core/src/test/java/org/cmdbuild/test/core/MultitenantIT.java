/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Maps.transformValues;
import java.util.List;
import java.util.Map;
import org.cmdbuild.auth.login.LoginDataImpl;
import org.cmdbuild.auth.multitenant.api.MultitenantService;
import org.cmdbuild.auth.multitenant.api.TenantInfo;
import org.cmdbuild.auth.role.Role;
import org.cmdbuild.auth.role.RoleImpl;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.auth.user.UserData;
import org.cmdbuild.auth.user.UserDataImpl;
import org.cmdbuild.auth.userrole.UserRoleService;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.ClassDefinitionImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.test.framework.CmTestRunner;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.prepareTuid;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.tuid;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CmTestRunner.class)
public class MultitenantIT {

    private final DaoService dao;
    private final UserRoleService userRoleService;
    private final MultitenantService multitenantService;
    private final SessionService sessionService;

    public MultitenantIT(DaoService dao, UserRoleService userRoleService, MultitenantService multitenantService, SessionService sessionService) {
        this.dao = checkNotNull(dao);
        this.userRoleService = checkNotNull(userRoleService);
        this.multitenantService = checkNotNull(multitenantService);
        this.sessionService = checkNotNull(sessionService);
    }

    @Before
    public void init() {
        prepareTuid();
    }

    @Test
    public void testMutitenant1() {
        Classe tenantClass = dao.createClass(ClassDefinitionImpl.builder().withName("MyTenantClass").build());
        Card tenatOne = dao.create(CardImpl.buildCard(tenantClass, map(ATTR_CODE, "TenantOne")));
        Card tenatTwo = dao.create(CardImpl.buildCard(tenantClass, map(ATTR_CODE, "TenantTwo")));

        multitenantService.enableMultitenantClassMode(tenantClass.getName());

        List<TenantInfo> activeTenants = multitenantService.getAllActiveTenants();
        assertEquals(set(tenatOne.getCode(), tenatTwo.getCode()), set(transform(activeTenants, TenantInfo::getDescription)));

        Map<String, Long> tenantIdsByDescr = transformValues(map(activeTenants, TenantInfo::getDescription), TenantInfo::getId);

        Role role = userRoleService.create(RoleImpl.builder().withName(tuid("MyRole")).build());

        UserData userOne = userRoleService.create(UserDataImpl.builder().withUsername("user_one").build());
        UserData userTwo = userRoleService.create(UserDataImpl.builder().withUsername("user_two").build());

        userRoleService.addRoleToUser(userOne, role);
        userRoleService.addRoleToUser(userTwo, role);

        assertEquals(0, multitenantService.getAvailableTenantContextForUser(userOne.getId()).getAvailableTenantIds().size());
        assertEquals(0, multitenantService.getAvailableTenantContextForUser(userTwo.getId()).getAvailableTenantIds().size());

        multitenantService.setUserTenants(userOne.getId(), list(checkNotNull(tenantIdsByDescr.get(tenatOne.getCode()))));
        multitenantService.setUserTenants(userTwo.getId(), list(checkNotNull(tenantIdsByDescr.get(tenatTwo.getCode()))));

        assertEquals(tenatOne.getCode(), multitenantService.getTenantDescription(getOnlyElement(multitenantService.getAvailableTenantContextForUser(userOne.getId()).getAvailableTenantIds())));
        assertEquals(tenatTwo.getCode(), multitenantService.getTenantDescription(getOnlyElement(multitenantService.getAvailableTenantContextForUser(userTwo.getId()).getAvailableTenantIds())));

        assertEquals(false, multitenantService.getAvailableTenantContextForUser(userOne.getId()).ignoreTenantPolicies());
        assertEquals(false, multitenantService.getAvailableTenantContextForUser(userTwo.getId()).ignoreTenantPolicies());

        sessionService.createAndSet(LoginDataImpl.buildNoPasswordRequired(userOne.getUsername()));
        assertEquals(userOne.getUsername(), sessionService.getCurrentSession().getOperationUser().getUsername());
        assertEquals(tenatOne.getCode(), multitenantService.getTenantDescription(getOnlyElement(sessionService.getCurrentSession().getOperationUser().getUserTenantContext().getActiveTenantIds())));
        assertEquals(false, sessionService.getCurrentSession().getOperationUser().getUserTenantContext().ignoreTenantPolicies());

        //TODO test query for user_one here
        sessionService.deleteCurrentSessionIfExists();

        sessionService.createAndSet(LoginDataImpl.buildNoPasswordRequired(userTwo.getUsername()));
        assertEquals(userTwo.getUsername(), sessionService.getCurrentSession().getOperationUser().getUsername());
        assertEquals(tenatTwo.getCode(), multitenantService.getTenantDescription(getOnlyElement(sessionService.getCurrentSession().getOperationUser().getUserTenantContext().getActiveTenantIds())));
        assertEquals(false, sessionService.getCurrentSession().getOperationUser().getUserTenantContext().ignoreTenantPolicies());

        //TODO test query for user_two here
        sessionService.deleteCurrentSessionIfExists();
    }
}
