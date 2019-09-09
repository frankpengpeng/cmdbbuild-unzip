/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.core;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.auth.login.LoginDataImpl;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.auth.user.UserData;
import org.cmdbuild.auth.user.UserDataImpl;
import org.cmdbuild.auth.user.UserRepository;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import org.cmdbuild.dao.entrytype.ClassDefinitionImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.test.framework.CmTestRunner;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.prepareTuid;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.tuid;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.workflow.core.fluentapi.ExtendedApi;
import org.cmdbuild.workflow.core.fluentapi.WorkflowApiService;
import org.cmdbuild.workflow.core.fluentapi.beans.ApiUser;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CmTestRunner.class)
public class WfApiImpersonateIT {

    private final DaoService dao;
    private final UserRepository userRepository;//TODO improve user creation
    private final RoleRepository roleRepository;//TODO improve user creation
    private final WorkflowApiService workflowApiService;
    private final SessionService sessionService;

    public WfApiImpersonateIT(DaoService dao, UserRepository userRepository, RoleRepository roleRepository, WorkflowApiService workflowApiService, SessionService sessionService) {
        this.dao = checkNotNull(dao);
        this.userRepository = checkNotNull(userRepository);
        this.roleRepository = checkNotNull(roleRepository);
        this.workflowApiService = checkNotNull(workflowApiService);
        this.sessionService = checkNotNull(sessionService);
    }

    @Before
    public void init() {
        prepareTuid();
    }

    @Test
    public void testWfImpersonate() {

        Classe classe = dao.createClass(ClassDefinitionImpl.build(tuid("MyClass")));

        UserData one = userRepository.create(UserDataImpl.builder().withUsername(tuid("testUserOne")).build()),
                two = userRepository.create(UserDataImpl.builder().withUsername(tuid("testUserTwo")).build());

        roleRepository.setUserGroupsByName(one.getId(), list("SuperUser"), null);//TODO improve user creation
        roleRepository.setUserGroupsByName(two.getId(), list("SuperUser"), null);//TODO improve user creation

        sessionService.createAndSet(LoginDataImpl.buildNoPasswordRequired(one.getUsername()));

        assertEquals(one.getUsername(), workflowApiService.getWorkflowApi().getCurrentUser().getUsername());

        workflowApiService.getWorkflowApi().newCard(classe.getName()).withCode("one").create();
        assertEquals(one.getUsername(), dao.selectAll().from(classe).where(ATTR_CODE, EQ, "one").getCard().getUser());

        ExtendedApi impersonate = (ExtendedApi) workflowApiService.getWorkflowApi().impersonate().username(two.getUsername()).impersonate();//TODO fix api, remove cast
        ApiUser apiUser = impersonate.getCurrentUser();
        assertEquals(two.getUsername(), apiUser.getUsername());

        impersonate.newCard(classe.getName()).withCode("two").create();
        assertEquals(two.getUsername(), dao.selectAll().from(classe).where(ATTR_CODE, EQ, "two").getCard().getUser());

    }

}
