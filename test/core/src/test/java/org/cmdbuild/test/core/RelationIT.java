/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.auth.AuthConst.GOD_USER;
import org.cmdbuild.auth.login.LoginDataImpl;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.beans.RelationImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.driver.repository.DaoEventService;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeImpl;
import org.cmdbuild.dao.entrytype.ClassDefinitionImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import static org.cmdbuild.dao.entrytype.DomainCardinality.MANY_TO_MANY;
import org.cmdbuild.dao.entrytype.DomainDefinitionImpl;
import org.cmdbuild.dao.entrytype.attributetype.StringAttributeType;
import org.cmdbuild.test.framework.CmTestRunner;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.prepareTuid;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CmTestRunner.class)
public class RelationIT {

    private final SessionService sessionService;
    private final DaoService dao;
//    private final EntryTypeOrAttributeCleanupHelper cleanupHelper;

    public RelationIT(SessionService sessionService, DaoService dao, DaoEventService daoEventService) {
        this.sessionService = checkNotNull(sessionService);
        this.dao = checkNotNull(dao);
//        this.cleanupHelper = new EntryTypeOrAttributeCleanupHelper(dao, daoEventService);
    }

    @Before
    public void init() {
        sessionService.createAndSet(LoginDataImpl.buildNoPasswordRequired(GOD_USER));
        prepareTuid();
    }

    @After
    public void cleanup() {
        dao.getJdbcTemplate().execute("DROP TABLE IF EXISTS \"Map_MyDomain\" CASCADE");
        dao.getJdbcTemplate().execute("DROP TABLE IF EXISTS \"MyClassOne\" CASCADE");
        dao.getJdbcTemplate().execute("DROP TABLE IF EXISTS \"MyClassTwo\" CASCADE");
//        cleanupHelper.cleanup();
        sessionService.deleteCurrentSessionIfExists();
    }

    @Test
    public void testRelationAttributes() {
        Classe classOne = dao.createClass(ClassDefinitionImpl.builder().withName("MyClassOne").build()),
                classTwo = dao.createClass(ClassDefinitionImpl.builder().withName("MyClassTwo").build());
        Domain domain = dao.createDomain(DomainDefinitionImpl.builder().withName("MyDomain").withSourceClass(classOne).withTargetClass(classTwo).withCardinality(MANY_TO_MANY).build());
        Attribute attribute = dao.createAttribute(AttributeImpl.builder().withOwner(domain).withName("MyAttr").withType(new StringAttributeType()).build());
        domain = dao.getDomain(domain.getName());
        assertTrue(domain.hasAttribute(attribute.getName()));

        Card one = dao.create(CardImpl.buildCard(classOne, map(ATTR_CODE, "ally")));
        Card two = dao.create(CardImpl.buildCard(classTwo, map(ATTR_CODE, "bob")));

        String value = "myAttrValue";
        CMRelation relation = dao.create(RelationImpl.builder().withType(domain).withSourceCard(one).withTargetCard(two).addAttribute(attribute.getName(), value).build());

        assertEquals(one.getClassName(), relation.getSourceCard().getClassName());
        assertEquals(one.getId(), relation.getSourceCard().getId());
        assertEquals(two.getClassName(), relation.getTargetCard().getClassName());
        assertEquals(two.getId(), relation.getTargetCard().getId());
        assertEquals(domain.getName(), relation.getType().getName());
        assertEquals(value, relation.get(attribute.getName()));

        relation = dao.getRelation(relation.getId());

        assertEquals(one.getClassName(), relation.getSourceCard().getClassName());
        assertEquals(one.getId(), relation.getSourceCard().getId());
        assertEquals(two.getClassName(), relation.getTargetCard().getClassName());
        assertEquals(two.getId(), relation.getTargetCard().getId());
        assertEquals(domain.getName(), relation.getType().getName());
        assertEquals(value, relation.get(attribute.getName()));

        relation = dao.getRelation(domain.getName(), one.getId(), two.getId());

        assertEquals(one.getClassName(), relation.getSourceCard().getClassName());
        assertEquals(one.getId(), relation.getSourceCard().getId());
        assertEquals(two.getClassName(), relation.getTargetCard().getClassName());
        assertEquals(two.getId(), relation.getTargetCard().getId());
        assertEquals(domain.getName(), relation.getType().getName());
        assertEquals(value, relation.get(attribute.getName()));

        relation = dao.selectAll().from(domain).getRelation();

        assertEquals(one.getClassName(), relation.getSourceCard().getClassName());
        assertEquals(one.getId(), relation.getSourceCard().getId());
        assertEquals(two.getClassName(), relation.getTargetCard().getClassName());
        assertEquals(two.getId(), relation.getTargetCard().getId());
        assertEquals(domain.getName(), relation.getType().getName());
        assertEquals(value, relation.get(attribute.getName()));

        value = "myOtherAttrValue";
        relation = dao.update(RelationImpl.copyOf(relation).addAttribute(attribute.getName(), value).build());

        assertEquals(one.getClassName(), relation.getSourceCard().getClassName());
        assertEquals(one.getId(), relation.getSourceCard().getId());
        assertEquals(two.getClassName(), relation.getTargetCard().getClassName());
        assertEquals(two.getId(), relation.getTargetCard().getId());
        assertEquals(domain.getName(), relation.getType().getName());
        assertEquals(value, relation.get(attribute.getName()));

        relation = dao.getRelation(relation.getId());

        assertEquals(one.getClassName(), relation.getSourceCard().getClassName());
        assertEquals(one.getId(), relation.getSourceCard().getId());
        assertEquals(two.getClassName(), relation.getTargetCard().getClassName());
        assertEquals(two.getId(), relation.getTargetCard().getId());
        assertEquals(domain.getName(), relation.getType().getName());
        assertEquals(value, relation.get(attribute.getName()));

        relation = dao.getRelation(domain.getName(), one.getId(), two.getId());

        assertEquals(one.getClassName(), relation.getSourceCard().getClassName());
        assertEquals(one.getId(), relation.getSourceCard().getId());
        assertEquals(two.getClassName(), relation.getTargetCard().getClassName());
        assertEquals(two.getId(), relation.getTargetCard().getId());
        assertEquals(domain.getName(), relation.getType().getName());
        assertEquals(value, relation.get(attribute.getName()));

        relation = dao.selectAll().from(domain).getRelation();

        assertEquals(one.getClassName(), relation.getSourceCard().getClassName());
        assertEquals(one.getId(), relation.getSourceCard().getId());
        assertEquals(two.getClassName(), relation.getTargetCard().getClassName());
        assertEquals(two.getId(), relation.getTargetCard().getId());
        assertEquals(domain.getName(), relation.getType().getName());
        assertEquals(value, relation.get(attribute.getName()));
    }

}
