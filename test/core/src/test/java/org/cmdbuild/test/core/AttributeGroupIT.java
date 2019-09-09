/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.core;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeGroupImpl;
import org.cmdbuild.dao.entrytype.AttributeGroupService;
import org.cmdbuild.dao.entrytype.AttributeImpl;
import org.cmdbuild.dao.entrytype.ClassDefinitionImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.attributetype.StringAttributeType;
import org.cmdbuild.test.framework.CmTestRunner;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.prepareTuid;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.tuid;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CmTestRunner.class)
public class AttributeGroupIT {

    private final DaoService dao;
    private final AttributeGroupService attributeGroupService;

    public AttributeGroupIT(DaoService dao, AttributeGroupService ags) {
        this.dao = checkNotNull(dao);
        this.attributeGroupService = checkNotNull(ags);
    }

    @Before
    public void init() {
        prepareTuid();
    }

    @Test
    public void testSimpleAttributeGroupCreation() {
        Classe one = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyClassOne")).build());

        attributeGroupService.create(AttributeGroupImpl.builder().withOwner(one).withName("MyGroup").withDescription("my group").withIndex(12).build());

        assertEquals("MyGroup", attributeGroupService.get(one, "MyGroup").getName());
        assertEquals("my group", attributeGroupService.get(one, "MyGroup").getDescription());
        assertEquals(12, attributeGroupService.get(one, "MyGroup").getIndex());
        assertEquals(one.getName(), attributeGroupService.get(one, "MyGroup").getOwnerName());
        assertEquals(one.getEtType(), attributeGroupService.get(one, "MyGroup").getOwnerType());

        attributeGroupService.delete(attributeGroupService.get(one, "MyGroup"));

        assertNull(attributeGroupService.getOrNull(one, "MyGroup"));
    }

    @Test
    public void testAttributeGroupHandling() {

        Classe one = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyClassOne")).withSuperclass(true).build());
        dao.createAttribute(AttributeImpl.builder().withOwner(one).withType(new StringAttributeType()).withName("MyAttr").withGroup("MyGroupOne").build());
        one = dao.getClasse(one.getName());

        assertNotNull(attributeGroupService.get(one, "MyGroupOne"));
        assertEquals("MyGroupOne", attributeGroupService.get(one, "MyGroupOne").getDescription());

        attributeGroupService.update(AttributeGroupImpl.copyOf(attributeGroupService.get(one, "MyGroupOne")).withDescription("my description").build());

        assertEquals("my description", attributeGroupService.get(one, "MyGroupOne").getDescription());

        Classe two = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyClassTwo")).withParent(one).build());

        assertNotNull(attributeGroupService.get(two, "MyGroupOne"));
        assertEquals("my description", attributeGroupService.get(two, "MyGroupOne").getDescription());

        attributeGroupService.update(AttributeGroupImpl.copyOf(attributeGroupService.get(two, "MyGroupOne")).withDescription("my description two").build());

        assertEquals("my description", attributeGroupService.get(one, "MyGroupOne").getDescription());
        assertEquals("my description two", attributeGroupService.get(two, "MyGroupOne").getDescription());

        dao.createAttribute(AttributeImpl.builder().withOwner(one).withType(new StringAttributeType()).withName("MyOtherAttr").withGroup("MyGroupTwo").build());
        one = dao.getClasse(one.getName());
        two = dao.getClasse(two.getName());

        assertNotNull(attributeGroupService.get(one, "MyGroupTwo"));
        assertEquals("MyGroupTwo", attributeGroupService.get(one, "MyGroupTwo").getDescription());
        assertEquals("MyGroupTwo", attributeGroupService.get(two, "MyGroupTwo").getDescription());
    }

    @Test
    public void testAutoAttributeGroupHandling1() {

        String one = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyClassOne")).withSuperclass(true).build()).getName();

        String two = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyClassTwo")).withParent(one).build()).getName();

        attributeGroupService.create(AttributeGroupImpl.builder().withOwner(dao.getClasse(one)).withName("MyGroup").withDescription("my group").withIndex(12).build());

        assertNotNull(attributeGroupService.get(dao.getClasse(one), "MyGroup"));
        assertNotNull(attributeGroupService.get(dao.getClasse(two), "MyGroup"));
        assertEquals(12, attributeGroupService.get(dao.getClasse(one), "MyGroup").getIndex());
        assertEquals(1, attributeGroupService.get(dao.getClasse(two), "MyGroup").getIndex());

        attributeGroupService.delete(attributeGroupService.get(dao.getClasse(two), "MyGroup"));

        assertNotNull(attributeGroupService.get(dao.getClasse(one), "MyGroup"));
        assertNull(attributeGroupService.getOrNull(dao.getClasse(two), "MyGroup"));

        dao.createAttribute(AttributeImpl.builder().withOwner(dao.getClasse(one)).withType(new StringAttributeType()).withName("MyAttr").withGroup("MyGroup").build());

        assertNotNull(attributeGroupService.get(dao.getClasse(one), "MyGroup"));
        assertNotNull(attributeGroupService.get(dao.getClasse(two), "MyGroup"));
        assertEquals("my group", attributeGroupService.get(dao.getClasse(one), "MyGroup").getDescription());
        assertEquals("my group", attributeGroupService.get(dao.getClasse(two), "MyGroup").getDescription());
        assertEquals(12, attributeGroupService.get(dao.getClasse(one), "MyGroup").getIndex());
        assertEquals(1, attributeGroupService.get(dao.getClasse(two), "MyGroup").getIndex());

        dao.createAttribute(AttributeImpl.builder().withOwner(dao.getClasse(one)).withType(new StringAttributeType()).withName("MyOtherAttr").withGroup("MyOtherGroup").build());

        assertNotNull(attributeGroupService.get(dao.getClasse(one), "MyOtherGroup"));
        assertNotNull(attributeGroupService.get(dao.getClasse(two), "MyOtherGroup"));
        assertEquals("MyOtherGroup", attributeGroupService.get(dao.getClasse(one), "MyOtherGroup").getDescription());
        assertEquals("MyOtherGroup", attributeGroupService.get(dao.getClasse(two), "MyOtherGroup").getDescription());
        assertEquals(13, attributeGroupService.get(dao.getClasse(one), "MyOtherGroup").getIndex());
        assertEquals(2, attributeGroupService.get(dao.getClasse(two), "MyOtherGroup").getIndex());

        attributeGroupService.create(AttributeGroupImpl.builder().withOwner(dao.getClasse(two)).withName("MyLastGroup").withDescription("my last group").withIndex(22).build());

        dao.createAttribute(AttributeImpl.builder().withOwner(dao.getClasse(one)).withType(new StringAttributeType()).withName("MyOtherOtherAttr").withGroup("MyLastGroup").build());

        assertNotNull(attributeGroupService.get(dao.getClasse(one), "MyLastGroup"));
        assertNotNull(attributeGroupService.get(dao.getClasse(two), "MyLastGroup"));
        assertEquals("MyLastGroup", attributeGroupService.get(dao.getClasse(one), "MyLastGroup").getDescription());
        assertEquals("my last group", attributeGroupService.get(dao.getClasse(two), "MyLastGroup").getDescription());
        assertEquals(14, attributeGroupService.get(dao.getClasse(one), "MyLastGroup").getIndex());
        assertEquals(22, attributeGroupService.get(dao.getClasse(two), "MyLastGroup").getIndex());
    }

    @Test
    public void testAutoAttributeGroupHandling2() {

        String one = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyClassOne")).withSuperclass(true).build()).getName();

        String two = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyClassTwo")).withParent(one).build()).getName();

        attributeGroupService.create(AttributeGroupImpl.builder().withOwner(dao.getClasse(one)).withName("MyGroup").withDescription("my group").withIndex(12).build());

        dao.createAttribute(AttributeImpl.builder().withOwner(dao.getClasse(one)).withType(new StringAttributeType()).withName("MyAttr").build());

        assertNotNull(attributeGroupService.get(dao.getClasse(one), "MyGroup"));
        assertNotNull(attributeGroupService.get(dao.getClasse(two), "MyGroup"));
        assertEquals(12, attributeGroupService.get(dao.getClasse(one), "MyGroup").getIndex());
        assertEquals(1, attributeGroupService.get(dao.getClasse(two), "MyGroup").getIndex());

        attributeGroupService.delete(attributeGroupService.get(dao.getClasse(two), "MyGroup"));

        assertNotNull(attributeGroupService.get(dao.getClasse(one), "MyGroup"));
        assertNull(attributeGroupService.getOrNull(dao.getClasse(two), "MyGroup"));

        dao.updateAttribute(AttributeImpl.copyOf(dao.getClasse(one).getAttribute("MyAttr")).withGroup("MyGroup").build());

        assertNotNull(attributeGroupService.get(dao.getClasse(one), "MyGroup"));
        assertNotNull(attributeGroupService.get(dao.getClasse(two), "MyGroup"));
        assertEquals("my group", attributeGroupService.get(dao.getClasse(one), "MyGroup").getDescription());
        assertEquals("my group", attributeGroupService.get(dao.getClasse(two), "MyGroup").getDescription());
        assertEquals(12, attributeGroupService.get(dao.getClasse(one), "MyGroup").getIndex());
        assertEquals(1, attributeGroupService.get(dao.getClasse(two), "MyGroup").getIndex());
    }
}
