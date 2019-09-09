/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.core;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.dao.beans.CardImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import org.cmdbuild.dao.entrytype.AttributeImpl;
import org.cmdbuild.dao.entrytype.ClassDefinitionImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.attributetype.StringAttributeType;
import static org.cmdbuild.data.filter.SorterElement.SorterElementDirection.DESC;
import org.cmdbuild.test.framework.CmTestRunner;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.prepareTuid;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.tuid;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CmTestRunner.class)
public class CardDistinctIT {

    private final DaoService dao;

    public CardDistinctIT(DaoService dao) {
        this.dao = checkNotNull(dao);
    }

    @Before
    public void init() {
        prepareTuid();
    }

    @Test
    public void testCardDistinctSelect() {
        Classe one = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("One")).build());
        dao.createAttribute(AttributeImpl.builder().withName("MyAttr").withOwner(one).withType(new StringAttributeType()).build());
        one = dao.getClasse(one.getName());

        dao.create(CardImpl.buildCard(one, ATTR_CODE, "1", "MyAttr", "2"));
        dao.create(CardImpl.buildCard(one, ATTR_CODE, "1", "MyAttr", "4"));
        dao.create(CardImpl.buildCard(one, ATTR_CODE, "1", "MyAttr", "6"));
        dao.create(CardImpl.buildCard(one, ATTR_CODE, "3", "MyAttr", "8"));
        dao.create(CardImpl.buildCard(one, ATTR_CODE, "3", "MyAttr", "8"));
        dao.create(CardImpl.buildCard(one, ATTR_CODE, "3", "MyAttr", "8"));
        dao.create(CardImpl.buildCard(one, ATTR_CODE, "5", "MyAttr", "2"));
        dao.create(CardImpl.buildCard(one, ATTR_CODE, "5", "MyAttr", "4"));
        dao.create(CardImpl.buildCard(one, ATTR_CODE, "5", "MyAttr", "6"));

        {
            List<String> list = dao.selectDistinct(ATTR_CODE).from(one).run().stream().map(r -> r.get(ATTR_CODE, String.class)).collect(toList());
            assertEquals(3, list.size());
            assertEquals(set("1", "3", "5"), set(list));
        }
        {
            List<String> list = dao.selectDistinct("MyAttr").from(one).run().stream().map(r -> r.get("MyAttr", String.class)).collect(toList());
            assertEquals(4, list.size());
            assertEquals(set("2", "4", "6", "8"), set(list));
        }
        {
            List<String> list = dao.selectDistinct(ATTR_CODE).from(one).where("MyAttr", EQ, "2").run().stream().map(r -> r.get(ATTR_CODE, String.class)).collect(toList());
            assertEquals(2, list.size());
            assertEquals(set("1", "5"), set(list));
        }
        {
            List<String> list = dao.selectDistinct("MyAttr").from(one).where(ATTR_CODE, EQ, "5").run().stream().map(r -> r.get("MyAttr", String.class)).collect(toList());
            assertEquals(3, list.size());
            assertEquals(set("2", "4", "6"), set(list));
        }

        {
            List<String> list = dao.selectDistinct(ATTR_CODE).from(one).orderBy(ATTR_CODE).run().stream().map(r -> r.get(ATTR_CODE, String.class)).collect(toList());
            assertEquals(3, list.size());
            assertEquals(list("1", "3", "5"), list);
        }
        {
            List<String> list = dao.selectDistinct(ATTR_CODE).from(one).orderBy(ATTR_CODE, DESC).run().stream().map(r -> r.get(ATTR_CODE, String.class)).collect(toList());
            assertEquals(3, list.size());
            assertEquals(list("5", "3", "1"), list);
        }

        {
            List<String> list = dao.selectDistinct(ATTR_CODE).from(one).where("MyAttr", EQ, "2").orderBy(ATTR_CODE).run().stream().map(r -> r.get(ATTR_CODE, String.class)).collect(toList());
            assertEquals(2, list.size());
            assertEquals(list("1", "5"), list);
        }
        {
            List<String> list = dao.selectDistinct(ATTR_CODE).from(one).where("MyAttr", EQ, "2").orderBy(ATTR_CODE, DESC).run().stream().map(r -> r.get(ATTR_CODE, String.class)).collect(toList());
            assertEquals(2, list.size());
            assertEquals(list("5", "1"), list);
        }
    }

}
