/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.dao.beans.RelationDirection.RD_INVERSE;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.driver.repository.DaoEventService;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeImpl;
import static org.cmdbuild.dao.entrytype.AttributePermissionMode.APM_DEFAULT;
import static org.cmdbuild.dao.entrytype.AttributePermissionMode.APM_IMMUTABLE;
import org.cmdbuild.dao.entrytype.ClassDefinitionImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import static org.cmdbuild.dao.entrytype.DomainCardinality.ONE_TO_MANY;
import org.cmdbuild.dao.entrytype.DomainDefinitionImpl;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.BOOLEAN;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.DECIMAL;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.STRING;
import org.cmdbuild.dao.entrytype.attributetype.BooleanAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DecimalAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.StringAttributeType;
import org.cmdbuild.test.framework.CmTestRunner;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.prepareTuid;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.tuid;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CmTestRunner.class)
public class AttributeModifyIT {

    private final DaoService dao;

    public AttributeModifyIT(DaoService dao, DaoEventService daoEventService) {
        this.dao = checkNotNull(dao);
    }

    @Before
    public void init() {
        prepareTuid();
    }

    @Test
    public void testStringAttributeCreation() {
        Classe classOne = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyClassOne")).withSuperclass(true).build());
        Attribute attributeOfClassOne = dao.createAttribute(AttributeImpl.builder().withName("MyAttr").withDescription("My Attr").withOwner(classOne).withType(new StringAttributeType()).withMeta("my_meta_1", "something 1").withMeta("my_meta_2", "something 2").build());
        assertEquals(STRING, attributeOfClassOne.getType().getName());
        assertEquals("something 1", attributeOfClassOne.getMetadata().get("my_meta_1"));
        assertEquals("something 2", attributeOfClassOne.getMetadata().get("my_meta_2"));
        assertEquals(APM_DEFAULT, attributeOfClassOne.getMode());
    }

    @Test
    public void testBooleanAttributeCreation() {
        Classe classOne = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyClassOne")).withSuperclass(true).build());
        Attribute attributeOfClassOne = dao.createAttribute(AttributeImpl.builder().withName("MyAttr").withDescription("My Attr").withOwner(classOne).withType(new BooleanAttributeType()).build());
        assertEquals(BOOLEAN, attributeOfClassOne.getType().getName());
    }

    @Test
    public void testDecimalAttributeCreation() {
        Classe classOne = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyClassOne")).withSuperclass(true).build());
        Attribute attributeOfClassOne = dao.createAttribute(AttributeImpl.builder().withName("MyAttr").withDescription("My Attr").withOwner(classOne).withType(new DecimalAttributeType(8, 6)).build());
        assertEquals(DECIMAL, attributeOfClassOne.getType().getName());
        assertEquals((Integer) 8, attributeOfClassOne.getType().as(DecimalAttributeType.class).getPrecision());
        assertEquals((Integer) 6, attributeOfClassOne.getType().as(DecimalAttributeType.class).getScale());
    }

    @Test
    public void testAttributeCommentPropagationOnAttributeCreation() {
        Classe classOne = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyClassOne")).build());
        Classe classTwo = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyClassTwo")).withSuperclass(true).build());
        Classe classThree = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyClassThree")).withParent(classTwo).build());
        Domain domain = dao.createDomain(DomainDefinitionImpl.builder().withSourceClass(classOne).withTargetClass(classTwo).withCardinality(ONE_TO_MANY).withName("MyDomainOneTwo").build());

        Attribute attributeOfClassTwo = dao.createAttribute(AttributeImpl.builder().withName("MyReference").withDescription("My Reference").withOwner(classTwo).withType(new ReferenceAttributeType(domain, RD_INVERSE))
                .withMeta("my_meta", "something")
                .withMeta((m) -> m.withAutoValueExpr("my auto value expr")) //TODO validate auto value expr syntax ??
                .build());
        assertEquals("something", attributeOfClassTwo.getMetadata().get("my_meta"));
        assertEquals("my auto value expr", attributeOfClassTwo.getMetadata().getAutoValueExpr());

        attributeOfClassTwo = dao.getClasse(classTwo.getName()).getAttribute(attributeOfClassTwo.getName());
        assertEquals("something", attributeOfClassTwo.getMetadata().get("my_meta"));
        assertEquals("my auto value expr", attributeOfClassTwo.getMetadata().getAutoValueExpr());

        classThree = dao.getClasse(classThree.getName());
        Attribute attributeOfClassThree = classThree.getAttribute(attributeOfClassTwo.getName());

        assertEquals(attributeOfClassTwo.getName(), attributeOfClassThree.getName());
        assertEquals(attributeOfClassTwo.getType().getName(), attributeOfClassThree.getType().getName());
        assertEquals(attributeOfClassTwo.getDescription(), attributeOfClassThree.getDescription());
        assertEquals(attributeOfClassTwo.getMode(), attributeOfClassThree.getMode());
        assertEquals(attributeOfClassTwo.getType().as(ReferenceAttributeType.class).getDomainName(), attributeOfClassThree.getType().as(ReferenceAttributeType.class).getDomainName());
        assertEquals(attributeOfClassTwo.getType().as(ReferenceAttributeType.class).getDirection(), attributeOfClassThree.getType().as(ReferenceAttributeType.class).getDirection());
        assertEquals("something", attributeOfClassThree.getMetadata().get("my_meta"));

        Classe classFour = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyClassFour")).withParent(classTwo).build());
        Attribute attributeOfClassFour = classFour.getAttribute(attributeOfClassTwo.getName());

        assertEquals(attributeOfClassTwo.getName(), attributeOfClassFour.getName());
        assertEquals(attributeOfClassTwo.getType().getName(), attributeOfClassFour.getType().getName());
        assertEquals(attributeOfClassTwo.getDescription(), attributeOfClassFour.getDescription());
        assertEquals(attributeOfClassTwo.getMode(), attributeOfClassFour.getMode());
        assertEquals(attributeOfClassTwo.getType().as(ReferenceAttributeType.class).getDomainName(), attributeOfClassFour.getType().as(ReferenceAttributeType.class).getDomainName());
        assertEquals(attributeOfClassTwo.getType().as(ReferenceAttributeType.class).getDirection(), attributeOfClassFour.getType().as(ReferenceAttributeType.class).getDirection());
        assertEquals("something", attributeOfClassFour.getMetadata().get("my_meta"));
    }

    @Test
    public void testAttributeCommentPropagationOnAttributeModify() {
        Classe classOne = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyClassOne")).withSuperclass(true).build());
        Classe classTwo = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyClassTwo")).withParent(classOne).build());

        Attribute attributeOfClassOne = dao.createAttribute(AttributeImpl.builder().withName("MyAttr").withDescription("My Attr").withOwner(classOne).withType(new StringAttributeType()).withMeta("my_meta_1", "something 1").withMeta("my_meta_2", "something 2").build());
        assertEquals("something 1", attributeOfClassOne.getMetadata().get("my_meta_1"));
        assertEquals("something 2", attributeOfClassOne.getMetadata().get("my_meta_2"));

        classTwo = dao.getClasse(classTwo.getName());
        Attribute attributeOfClassTwo = classTwo.getAttribute(attributeOfClassOne.getName());

        assertEquals(attributeOfClassOne.getName(), attributeOfClassTwo.getName());
        assertEquals(attributeOfClassOne.getType().getName(), attributeOfClassTwo.getType().getName());
        assertEquals(attributeOfClassOne.getDescription(), attributeOfClassTwo.getDescription());
        assertEquals(attributeOfClassOne.getMode(), attributeOfClassTwo.getMode());
        assertEquals(attributeOfClassOne.getMetadata().getGroup(), attributeOfClassTwo.getMetadata().getGroup());
        assertEquals("something 1", attributeOfClassTwo.getMetadata().get("my_meta_1"));
        assertEquals("something 2", attributeOfClassTwo.getMetadata().get("my_meta_2"));

        attributeOfClassTwo = dao.updateAttribute(AttributeImpl.copyOf(attributeOfClassTwo).withDescription("My Modified Attr").withGroup("other group").withMode(APM_IMMUTABLE).withMeta("my_meta_1", "else 1").withMeta("my_meta_3", "else 3").build());
        String groupTwo = attributeOfClassTwo.getGroupNameOrNull();

        assertEquals(attributeOfClassOne.getName(), attributeOfClassTwo.getName());
        assertEquals(attributeOfClassOne.getType().getName(), attributeOfClassTwo.getType().getName());
        assertEquals("My Modified Attr", attributeOfClassTwo.getDescription());
        assertEquals(APM_IMMUTABLE, attributeOfClassTwo.getMode());
        assertEquals(groupTwo, attributeOfClassTwo.getMetadata().getGroup());
        assertEquals("else 1", attributeOfClassTwo.getMetadata().get("my_meta_1"));
        assertEquals("something 2", attributeOfClassTwo.getMetadata().get("my_meta_2"));
        assertEquals("else 3", attributeOfClassTwo.getMetadata().get("my_meta_3"));

        attributeOfClassOne = dao.updateAttribute(AttributeImpl.copyOf(attributeOfClassOne).withDescription("My Modified Attr Descr").withMeta("my_meta_1", "anything 1").build());
        assertEquals("anything 1", attributeOfClassOne.getMetadata().get("my_meta_1"));
        assertEquals("something 2", attributeOfClassOne.getMetadata().get("my_meta_2"));

        classTwo = dao.getClasse(classTwo.getName());
        attributeOfClassTwo = classTwo.getAttribute(attributeOfClassOne.getName());

        assertEquals(attributeOfClassOne.getName(), attributeOfClassTwo.getName());
        assertEquals(attributeOfClassOne.getType().getName(), attributeOfClassTwo.getType().getName());
        assertEquals(attributeOfClassOne.getDescription(), attributeOfClassTwo.getDescription());
        assertEquals(APM_IMMUTABLE, attributeOfClassTwo.getMode());
        assertEquals(groupTwo, attributeOfClassTwo.getMetadata().getGroup());
        assertEquals("anything 1", attributeOfClassTwo.getMetadata().get("my_meta_1"));
        assertEquals("something 2", attributeOfClassTwo.getMetadata().get("my_meta_2"));
        assertEquals("else 3", attributeOfClassTwo.getMetadata().get("my_meta_3"));

    }

}
