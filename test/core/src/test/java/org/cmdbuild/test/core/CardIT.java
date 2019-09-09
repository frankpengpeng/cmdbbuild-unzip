/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Iterables.transform;
import static java.lang.String.format;
import java.util.List;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.beans.IdAndDescription;
import static org.cmdbuild.dao.beans.RelationDirection.RD_INVERSE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.WhereOperator.EQ;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeImpl;
import org.cmdbuild.dao.entrytype.ClassDefinitionImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import static org.cmdbuild.dao.entrytype.DomainCardinality.ONE_TO_MANY;
import org.cmdbuild.dao.entrytype.DomainDefinitionImpl;
import org.cmdbuild.dao.entrytype.attributetype.DateTimeAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import static org.cmdbuild.data.filter.SorterElement.SorterElementDirection.ASC;
import static org.cmdbuild.data.filter.SorterElement.SorterElementDirection.DESC;
import static org.cmdbuild.data.filter.utils.CmdbFilterUtils.parseFilter;
import org.cmdbuild.lookup.Lookup;
import org.cmdbuild.lookup.LookupImpl;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.test.framework.CmTestRunner;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.prepareTuid;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.tuid;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CmTestRunner.class)
public class CardIT {

    private final DaoService dao;
    private final LookupService lookupService;

    public CardIT(DaoService dao, LookupService lookupService) {
        this.dao = checkNotNull(dao);
        this.lookupService = checkNotNull(lookupService);
    }

    @Before
    public void init() {
        prepareTuid();
    }

    @Test
    public void testCardInsertWithReferenceValue() {
        Classe one = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("One")).build());
        Classe two = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("Two")).build());
        Domain domain = dao.createDomain(DomainDefinitionImpl.builder().withCardinality(ONE_TO_MANY).withSourceClass(one).withTargetClass(two).withName(tuid("Domain")).build());
        Attribute attribute = dao.createAttribute(AttributeImpl.builder().withName("MyAttr").withOwner(two).withType(new ReferenceAttributeType(domain, RD_INVERSE)).build());
        two = dao.getClasse(two.getName());

        Card card_one = dao.create(CardImpl.buildCard(one, map()));

        Card card_two_withReferenceFromLong = dao.create(CardImpl.buildCard(two, map(attribute.getName(), card_one.getId())));
        assertEquals(card_one.getId(), card_two_withReferenceFromLong.get(attribute.getName(), IdAndDescription.class).getId());

        Card card_two_withReferenceFromString = dao.create(CardImpl.buildCard(two, map(attribute.getName(), "" + card_one.getId())));
        assertEquals(card_one.getId(), card_two_withReferenceFromString.get(attribute.getName(), IdAndDescription.class).getId());

        Card card_two_withReferenceFromCard = dao.create(CardImpl.buildCard(two, map(attribute.getName(), card_one)));
        assertEquals(card_one.getId(), card_two_withReferenceFromCard.get(attribute.getName(), IdAndDescription.class).getId());

        Card card_two_withNullReference = dao.create(CardImpl.buildCard(two, map(attribute.getName(), null)));
        assertNull(card_two_withNullReference.get(attribute.getName()));

        Card card_two_withBlankReference = dao.create(CardImpl.buildCard(two, map(attribute.getName(), null)));
        assertNull(card_two_withBlankReference.get(attribute.getName()));

    }

    @Test
    public void testCardSelectWithReferenceValueSort() {
        Classe one = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("One")).build());
        Classe two = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("Two")).build());
        Domain domain = dao.createDomain(DomainDefinitionImpl.builder().withCardinality(ONE_TO_MANY).withSourceClass(one).withTargetClass(two).withName(tuid("Domain")).build());
        dao.createAttribute(AttributeImpl.builder().withName("MyAttr").withOwner(two).withType(new ReferenceAttributeType(domain, RD_INVERSE)).build());
        two = dao.getClasse(two.getName());

        Card card_one_1 = dao.create(CardImpl.buildCard(one, map(ATTR_DESCRIPTION, "a")));
        Card card_one_2 = dao.create(CardImpl.buildCard(one, map(ATTR_DESCRIPTION, "b")));
        Card card_one_3 = dao.create(CardImpl.buildCard(one, map(ATTR_DESCRIPTION, "c")));

        Card card_two_1 = dao.create(CardImpl.buildCard(two, map("MyAttr", card_one_1.getId())));
        Card card_two_2 = dao.create(CardImpl.buildCard(two, map("MyAttr", card_one_2.getId())));
        Card card_two_3 = dao.create(CardImpl.buildCard(two, map("MyAttr", card_one_3.getId())));
        Card card_two_4 = dao.create(CardImpl.buildCard(two, map("MyAttr", null)));

        List<Card> cards = dao.selectAll().from(two).orderBy("MyAttr", ASC).getCards();
        assertEquals(list(transform(list(card_two_1, card_two_2, card_two_3, card_two_4), Card::getId)), list(transform(cards, Card::getId)));

        cards = dao.selectAll().from(two).orderBy("MyAttr", DESC).getCards();
        assertEquals(list(transform(list(card_two_4, card_two_3, card_two_2, card_two_1), Card::getId)), list(transform(cards, Card::getId)));
    }

    @Test
    public void testCardFilterWithDateGt() {
        Classe classe = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyClass")).build());
        dao.createAttribute(AttributeImpl.builder().withName("MyAttr").withOwner(classe).withType(new DateTimeAttributeType()).build());

        List<Card> cards = list("2018-03-22T10:12Z", "2018-03-22T20:12Z", "2018-03-24T10:12Z").stream().map(d
                -> dao.create(CardImpl.buildCard(dao.getClasse(classe.getName()), "MyAttr", d))
        ).collect(toList());

        List<Card> list = dao.selectAll().from(dao.getClasse(classe.getName())).where(parseFilter("{\"attribute\":{\"simple\":{\"attribute\":\"MyAttr\",\"operator\":\"greater\",\"value\":[\"2018-03-22T14:12Z\"]}}}")).getCards();
        assertEquals(2, list.size());

        list = dao.selectAll().from(dao.getClasse(classe.getName())).where(parseFilter("{\"attribute\":{\"simple\":{\"attribute\":\"MyAttr\",\"operator\":\"greater\",\"value\":[\"2018-03-22T21:12Z\"]}}}")).getCards();
        assertEquals(1, list.size());
        assertEquals(cards.get(2).getId(), getOnlyElement(list).getId());
    }

    @Test
    public void testCardFilterWithlookup() {
        LookupType lookupType = lookupService.createLookupType(tuid("MyLookupType"));
        Lookup lookupOne = lookupService.createOrUpdateLookup(LookupImpl.builder().withType(lookupType).withCode("one").withDescription("Uno").build());
        Lookup lookupTwo = lookupService.createOrUpdateLookup(LookupImpl.builder().withType(lookupType).withCode("two").withDescription("Two").build());

        Classe classe = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("One")).build());
        dao.createAttribute(AttributeImpl.builder().withName("MyLoAttr").withOwner(classe).withType(new LookupAttributeType(lookupType)).build());
        classe = dao.getClasse(classe.getName());

        Card card_one = dao.create(CardImpl.buildCard(classe, map()));
        Card card_two = dao.create(CardImpl.buildCard(classe, map("MyLoAttr", lookupOne.getId(), ATTR_DESCRIPTION, "Two", ATTR_CODE, "due")));
        Card card_three = dao.create(CardImpl.buildCard(classe, map("MyLoAttr", lookupTwo.getId(), ATTR_DESCRIPTION, "Three", ATTR_CODE, "tre")));

        List<Card> list = dao.selectAll().from(dao.getClasse(classe.getName())).where(parseFilter(format("{\"attribute\":{\"simple\":{\"attribute\":\"MyLoAttr\",\"operator\":\"equal\",\"value\":\"%s\"}}}", lookupOne.getId()))).getCards();
        assertEquals(1, list.size());
        assertEquals(card_two.getId(), getOnlyElement(list).getId());

        list = dao.selectAll().from(dao.getClasse(classe.getName())).where(parseFilter(format("{\"attribute\":{\"simple\":{\"attribute\":\"MyLoAttr\",\"operator\":\"notequal\",\"value\":\"%s\"}}}", lookupOne.getId()))).getCards();
        assertEquals(2, list.size());

        list = dao.selectAll().from(dao.getClasse(classe.getName())).where(parseFilter(format("{\"attribute\":{\"simple\":{\"attribute\":\"Code\",\"operator\":\"equal\",\"value\":\"%s\"}}}", card_three.getCode()))).getCards();
        assertEquals(1, list.size());
        assertEquals(card_three.getId(), getOnlyElement(list).getId());
    }

    @Test
    public void testCardSelectWithLookup() {
        LookupType lookupType = lookupService.createLookupType(tuid("MyLookupType"));
        Lookup lookup = lookupService.createOrUpdateLookup(LookupImpl.builder().withType(lookupType).withCode("one").withDescription("Uno").build());

        Classe one = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("One")).build());
        dao.createAttribute(AttributeImpl.builder().withName("MyLoAttr").withOwner(one).withType(new LookupAttributeType(lookupType)).build());
        one = dao.getClasse(one.getName());

        Card card_one = dao.create(CardImpl.buildCard(one, map()));
        Card card_two = dao.create(CardImpl.buildCard(one, map("MyLoAttr", lookup.getId(), ATTR_DESCRIPTION, "Two")));

        Card card = dao.getCard(one.getName(), card_one.getId());
        assertNotNull(card);
        assertEquals(card.getId(), card_one.getId());
        assertNull(card.get("MyLoAttr"));
        assertNull(card.get("MyLoAttr", Long.class));

        card = dao.getCard(one.getName(), card_two.getId());
        assertNotNull(card);
        assertEquals(card.getId(), card_two.getId());
        assertEquals(lookup.getId(), card.get("MyLoAttr", Long.class));
        assertEquals(lookup.getId(), card.get("MyLoAttr", IdAndDescription.class).getId());
        assertEquals("one", card.get("MyLoAttr", IdAndDescription.class).getCode());
        assertEquals("Uno", card.get("MyLoAttr", IdAndDescription.class).getDescription());
        assertEquals("Two", card.getDescription());

        card = dao.getCard(card_one.getId());
        assertNotNull(card);
        assertEquals(card.getId(), card_one.getId());

        card = dao.select(ATTR_ID, ATTR_CODE, ATTR_DESCRIPTION).from(one).where(ATTR_ID, EQ, card_two.getId()).getCard();
        assertNotNull(card);
        assertEquals(card.getId(), card_two.getId());
        assertEquals("Two", card.getDescription());
        assertNull(card.get("MyLoAttr"));
    }

}
