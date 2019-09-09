/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.core;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Maps.uniqueIndex;
import com.google.common.collect.Ordering;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import java.util.stream.IntStream;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.beans.IdAndDescription;
import static org.cmdbuild.dao.beans.RelationDirection.RD_DIRECT;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.DaoService.COUNT;
import org.cmdbuild.dao.core.q3.ResultRow;
import org.cmdbuild.dao.entrytype.AttributeImpl;
import org.cmdbuild.dao.entrytype.ClassDefinitionImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import static org.cmdbuild.dao.entrytype.DomainCardinality.MANY_TO_ONE;
import org.cmdbuild.dao.entrytype.DomainDefinitionImpl;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import org.cmdbuild.test.framework.CmTestRunner;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.prepareTuid;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.tuid;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CmTestRunner.class)
public class SelectDistinctIT {

    private final DaoService dao;

    public SelectDistinctIT(DaoService dao) {
        this.dao = checkNotNull(dao);
    }

    @Before
    public void init() {
        prepareTuid();
    }

    @Test
    public void testSelectDistinctValue() {
        Classe classe = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyClass")).build());

        IntStream.range(0, 10).forEach(i -> dao.create(CardImpl.buildCard(classe, ATTR_CODE, "one", ATTR_DESCRIPTION, "test_" + i)));
        IntStream.range(0, 20).forEach(i -> dao.create(CardImpl.buildCard(classe, ATTR_CODE, "two", ATTR_DESCRIPTION, "test_" + i)));
        IntStream.range(0, 3).forEach(i -> dao.create(CardImpl.buildCard(classe, ATTR_CODE, "three", ATTR_DESCRIPTION, "test_" + i)));

        assertEquals(33, dao.selectAll().from(classe).getCards().size());
        assertEquals(33, dao.selectCount().from(classe).getCount());

        List<String> values = dao.selectDistinct(ATTR_CODE).from(classe).run().stream().map(r -> r.get(ATTR_CODE, String.class)).collect(toList());

        assertEquals(3, values.size());
        assertEquals(Ordering.natural().sortedCopy(list("one", "two", "three")), Ordering.natural().sortedCopy(values));
    }

    @Test
    public void testSelectDistinctReference() {
        Classe c = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyClass")).build()),
                ref = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyOtherClass")).build());
        Domain domain = dao.createDomain(DomainDefinitionImpl.builder().withName(tuid("MyDomain")).withSourceClass(c).withTargetClass(ref).withCardinality(MANY_TO_ONE).build());
        dao.createAttribute(AttributeImpl.builder().withOwner(c).withName("MyRef").withType(new ReferenceAttributeType(domain, RD_DIRECT)).build());
        Classe classe = dao.getClasse(c);

        Card one = dao.create(CardImpl.buildCard(ref, ATTR_CODE, "one", ATTR_DESCRIPTION, "One")),
                two = dao.create(CardImpl.buildCard(ref, ATTR_CODE, "two", ATTR_DESCRIPTION, "Two")),
                three = dao.create(CardImpl.buildCard(ref, ATTR_CODE, "three", ATTR_DESCRIPTION, "Three"));

        IntStream.range(0, 10).forEach(i -> dao.create(CardImpl.buildCard(classe, "MyRef", one, ATTR_DESCRIPTION, "test_" + i)));
        IntStream.range(0, 20).forEach(i -> dao.create(CardImpl.buildCard(classe, "MyRef", two, ATTR_DESCRIPTION, "test_" + i)));
        IntStream.range(0, 3).forEach(i -> dao.create(CardImpl.buildCard(classe, "MyRef", three, ATTR_DESCRIPTION, "test_" + i)));

        assertEquals(33, dao.selectAll().from(classe).getCards().size());
        assertEquals(33, dao.selectCount().from(classe).getCount());

        List<IdAndDescription> values = dao.selectDistinct("MyRef").from(classe).run().stream().map(r -> r.get("MyRef", IdAndDescription.class)).collect(toList());

        assertEquals(3, values.size());
        assertEquals(Ordering.natural().sortedCopy(list(one.getId(), two.getId(), three.getId())), Ordering.natural().sortedCopy(transform(values, IdAndDescription::getId)));
        assertEquals(Ordering.natural().sortedCopy(list("one", "two", "three")), Ordering.natural().sortedCopy(transform(values, IdAndDescription::getCode)));
        assertEquals(Ordering.natural().sortedCopy(list("One", "Two", "Three")), Ordering.natural().sortedCopy(transform(values, IdAndDescription::getDescription)));
    }

    @Test
    public void testSelectDistinctCountValue() {
        Classe classe = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyClass")).build());

        IntStream.range(0, 10).forEach(i -> dao.create(CardImpl.buildCard(classe, ATTR_CODE, "one", ATTR_DESCRIPTION, "test_" + i)));
        IntStream.range(0, 20).forEach(i -> dao.create(CardImpl.buildCard(classe, ATTR_CODE, "two", ATTR_DESCRIPTION, "test_" + i)));
        IntStream.range(0, 3).forEach(i -> dao.create(CardImpl.buildCard(classe, ATTR_CODE, "three", ATTR_DESCRIPTION, "test_" + i)));

        assertEquals(33, dao.selectAll().from(classe).getCards().size());
        assertEquals(33, dao.selectCount().from(classe).getCount());

        Map<String, ResultRow> rows = uniqueIndex(dao.selectDistinct(ATTR_CODE).selectCount().from(classe).run(), r -> r.get(ATTR_CODE, String.class));

        assertEquals(3, rows.size());
        assertEquals(Ordering.natural().sortedCopy(list("one", "two", "three")), Ordering.natural().sortedCopy(rows.keySet()));

        assertEquals(10, (int) rows.get("one").get(COUNT, Integer.class));
        assertEquals(20, (int) rows.get("two").get(COUNT, Integer.class));
        assertEquals(3, (int) rows.get("three").get(COUNT, Integer.class));
    }

    @Test
    public void testSelectDistinctCountReference() {
        Classe c = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyClass")).build()),
                ref = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyOtherClass")).build());
        Domain domain = dao.createDomain(DomainDefinitionImpl.builder().withName(tuid("MyDomain")).withSourceClass(c).withTargetClass(ref).withCardinality(MANY_TO_ONE).build());
        dao.createAttribute(AttributeImpl.builder().withOwner(c).withName("MyRef").withType(new ReferenceAttributeType(domain, RD_DIRECT)).build());
        Classe classe = dao.getClasse(c);

        Card one = dao.create(CardImpl.buildCard(ref, ATTR_CODE, "one", ATTR_DESCRIPTION, "One")),
                two = dao.create(CardImpl.buildCard(ref, ATTR_CODE, "two", ATTR_DESCRIPTION, "Two")),
                three = dao.create(CardImpl.buildCard(ref, ATTR_CODE, "three", ATTR_DESCRIPTION, "Three"));

        IntStream.range(0, 10).forEach(i -> dao.create(CardImpl.buildCard(classe, "MyRef", one, ATTR_DESCRIPTION, "test_" + i)));
        IntStream.range(0, 20).forEach(i -> dao.create(CardImpl.buildCard(classe, "MyRef", two, ATTR_DESCRIPTION, "test_" + i)));
        IntStream.range(0, 3).forEach(i -> dao.create(CardImpl.buildCard(classe, "MyRef", three, ATTR_DESCRIPTION, "test_" + i)));

        assertEquals(33, dao.selectAll().from(classe).getCards().size());
        assertEquals(33, dao.selectCount().from(classe).getCount());

        Map<Long, ResultRow> rows = uniqueIndex(dao.selectDistinct("MyRef").selectCount().from(classe).run(), r -> r.get("MyRef", IdAndDescription.class).getId());

        assertEquals(3, rows.size());
        assertEquals(Ordering.natural().sortedCopy(list(one.getId(), two.getId(), three.getId())), Ordering.natural().sortedCopy(rows.keySet()));

        assertEquals(10, (int) rows.get(one.getId()).get(COUNT, Integer.class));
        assertEquals(20, (int) rows.get(two.getId()).get(COUNT, Integer.class));
        assertEquals(3, (int) rows.get(three.getId()).get(COUNT, Integer.class));
    }

}
