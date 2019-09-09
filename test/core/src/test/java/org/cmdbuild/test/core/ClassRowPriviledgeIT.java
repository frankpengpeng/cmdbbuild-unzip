/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.core;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.auth.grant.GrantDataImpl;
import org.cmdbuild.auth.grant.GrantDataRepository;
import static org.cmdbuild.auth.grant.GrantMode.GM_WRITE;
import static org.cmdbuild.auth.grant.PrivilegedObjectType.POT_CLASS;
import org.cmdbuild.auth.login.LoginDataImpl;
import org.cmdbuild.auth.role.Role;
import org.cmdbuild.auth.role.RoleImpl;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.auth.user.UserData;
import org.cmdbuild.auth.user.UserDataImpl;
import org.cmdbuild.auth.user.UserRepository;
import org.cmdbuild.classe.access.UserCardService;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeImpl;
import org.cmdbuild.dao.entrytype.ClassDefinitionImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.attributetype.StringAttributeType;
import org.cmdbuild.test.framework.CmTestRunner;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.prepareTuid;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.tuid;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(CmTestRunner.class)
public class ClassRowPriviledgeIT {

    private final DaoService dao;
    private final SessionService sessionService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final GrantDataRepository repository;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public ClassRowPriviledgeIT(DaoService dao, SessionService sessionService, UserRepository userRepository, RoleRepository roleRepository, GrantDataRepository repository) {
        this.dao = checkNotNull(dao);
        this.sessionService = checkNotNull(sessionService);
        this.roleRepository = checkNotNull(roleRepository);
        this.repository = checkNotNull(repository);
        this.userRepository = checkNotNull(userRepository);
    }

    @Before
    public void init() {
        prepareTuid();
        sessionService.createAndSet(LoginDataImpl.buildNoPasswordRequired("admin"));
    }

    @Test
    public void testRowPriviledgeChildClass() {
        Role changeManager = roleRepository.create(RoleImpl.builder().withName(tuid("ChangeManager")).build());
        UserData user = userRepository.create(UserDataImpl.builder().withUsername(tuid("ChangeManagerUser")).build());
        Classe parent = dao.createClass(ClassDefinitionImpl.build(tuid("Parent")));
        Classe children = dao.createClass(ClassDefinitionImpl.build(tuid("ChildrenPriviledge")));

        Attribute attribute = dao.createAttribute(AttributeImpl.builder().withOwner(children).withType(new StringAttributeType()).withName("FilterAttr").build());
        Card card1 = dao.create(CardImpl.buildCard(children, map(attribute.getName(), "filterme")));
        Card card2 = dao.create(CardImpl.buildCard(children, map(attribute.getName(), "other")));

        repository.setGrantsForRole(changeManager.getId(), list(GrantDataImpl.builder()
                .withClassName(children.getName())
                .withRoleId(changeManager.getId())
                .withType(POT_CLASS)
                .withMode(GM_WRITE)
                .withPrivilegeFilter("{\"attribute\":{\"simple\":{\"attribute\":\"FilterAttr\",\"operator\":\"equal\",\"parameterType\":\"fixed\",\"value\":[\"filterme\"]}}}")
                .build()));
        //TODO finish tests when row filter works
    }
}
