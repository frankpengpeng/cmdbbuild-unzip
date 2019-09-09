/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import org.cmdbuild.auth.login.LoginDataImpl;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.auth.user.UserData;
import org.cmdbuild.auth.user.UserDataImpl;
import org.cmdbuild.auth.user.UserRepository;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.AttributeGroupData;
import org.cmdbuild.dao.entrytype.AttributeGroupImpl;
import org.cmdbuild.dao.entrytype.AttributeGroupService;
import org.cmdbuild.dao.entrytype.AttributeImpl;
import org.cmdbuild.dao.entrytype.ClassDefinitionImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.attributetype.StringAttributeType;
import org.cmdbuild.test.framework.CmTestRunner;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.prepareTuid;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.tuid;
import org.cmdbuild.translation.TranslationService;
import org.cmdbuild.userconfig.UserConfigService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(CmTestRunner.class)
public class AttributeGroupTranslationIT {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final AttributeGroupService attributeGroupService;
    private final TranslationService translationService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserConfigService userConfigService;
    private final SessionService sessionService;

    public AttributeGroupTranslationIT(DaoService dao, AttributeGroupService ags, TranslationService translationService, UserRepository userRepository, RoleRepository roleRepository,
            UserConfigService userConfigService, SessionService sessionService) {
        this.dao = checkNotNull(dao);
        this.attributeGroupService = checkNotNull(ags);
        this.translationService = checkNotNull(translationService);
        this.userRepository = checkNotNull(userRepository);
        this.roleRepository = checkNotNull(roleRepository);
        this.userConfigService = checkNotNull(userConfigService);
        this.sessionService = checkNotNull(sessionService);
    }

    @Before
    public void init() {
        prepareTuid();
        UserData user = userRepository.create(UserDataImpl.builder().withUsername(tuid("my_username")).build());
        roleRepository.setUserGroupsByName(user.getId(), list("SuperUser"), null);
        sessionService.createAndSet(LoginDataImpl.buildNoPasswordRequired(user.getUsername()));
        userConfigService.setForCurrent("language", "it");
    }

    @Test
    public void testAttributeGroupTranslation() {
        Classe one = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyClass")).build());

        AttributeGroupData attributeGroup = attributeGroupService.create(AttributeGroupImpl.builder().withOwner(one).withName("MyGroup").withDescription("my group").withIndex(12).build());

        logger.error("GetAttrGroup" + one.hasAttributeGroup(attributeGroup.getName()));
        translationService.setTranslation(format("attributegroupclass.%s.%s.description", one.getName(), attributeGroup.getName()), "it", "il mio gruppo");
        assertEquals("il mio gruppo", translationService.translateAttributeGroupDescription(one, attributeGroup));
    }

    @Test
    public void testAttributeGroupTranslationParent() {
        Classe parent = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyClassParent")).withSuperclass(true).build());
        AttributeGroupData attributeGroupParentChildren = attributeGroupService.create(AttributeGroupImpl.builder().withOwner(parent).withName("MyGroup2").withDescription("my group2").withIndex(13).build());

        dao.createAttribute(AttributeImpl.builder().withType(new StringAttributeType()).withName("StringAttr").withOwner(parent).withGroup(attributeGroupParentChildren).build());
        translationService.setTranslation(format("attributegroupclass.%s.%s.description", parent.getName(), attributeGroupParentChildren.getName()), "it", "il mio gruppo");
        Classe children = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyClassChildren")).withParent(parent).build());

        assertEquals("il mio gruppo", translationService.translateAttributeGroupDescription(children, attributeGroupParentChildren));
    }
}
