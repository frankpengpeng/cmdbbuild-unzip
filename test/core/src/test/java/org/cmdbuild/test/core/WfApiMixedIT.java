/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.core;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.auth.user.UserRepository;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.AttributeImpl;
import org.cmdbuild.dao.entrytype.ClassDefinitionImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.attributetype.BooleanAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DateAttributeType;
import org.cmdbuild.test.framework.CmTestRunner;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.prepareTuid;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.tuid;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDate;
import org.cmdbuild.workflow.core.fluentapi.WorkflowApiService;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CmTestRunner.class)
public class WfApiMixedIT {

    private final DaoService dao;
    private final WorkflowApiService workflowApiService;

    public WfApiMixedIT(DaoService dao, UserRepository userRepository, RoleRepository roleRepository, WorkflowApiService workflowApiService, SessionService sessionService) {
        this.dao = checkNotNull(dao);
        this.workflowApiService = checkNotNull(workflowApiService);
    }

    @Before
    public void init() {
        prepareTuid();
    }

    @Test
    public void testWfCardBooleanAttributeType() {

        Classe classe = dao.createClass(ClassDefinitionImpl.build(tuid("MyClass")));
        dao.createAttribute(AttributeImpl.builder().withOwner(classe).withName("MyAttr").withType(new BooleanAttributeType()).build());
        classe = dao.getClasse(classe.getName());

        Card one = dao.create(CardImpl.buildCard(classe, ATTR_CODE, "one", "MyAttr", true));
        Card two = dao.create(CardImpl.buildCard(classe, ATTR_CODE, "two", "MyAttr", false));
        Card three = dao.create(CardImpl.buildCard(classe, ATTR_CODE, "three"));

        {
            Object val = workflowApiService.getWorkflowApi().existingCard(classe.getName(), one.getId()).fetch().get("MyAttr");
            assertThat(val, instanceOf(Boolean.class));
            assertEquals(true, val);
        }
        {
            Object val = workflowApiService.getWorkflowApi().existingCard(classe.getName(), two.getId()).fetch().get("MyAttr");
            assertThat(val, instanceOf(Boolean.class));
            assertEquals(false, val);
        }
        {
            Object val = workflowApiService.getWorkflowApi().existingCard(classe.getName(), three.getId()).fetch().get("MyAttr");
//            assertNull(val);
            assertThat(val, instanceOf(Boolean.class));//default for null
            assertEquals(false, val);//default for null
        }

    }

    @Test
    public void testWfCardDateAttributeType() {
        Classe classe = dao.createClass(ClassDefinitionImpl.build(tuid("MyClass")));
        dao.createAttribute(AttributeImpl.builder().withOwner(classe).withName("MyAttr").withType(new DateAttributeType()).build());
        classe = dao.getClasse(classe.getName());
        workflowApiService.getWorkflowApi().newCard(classe.getName()).withAttribute("MyAttr", "2019-09-21T02:00:00Z").create();

        assertEquals("2019-09-21", toIsoDate(dao.selectAll().from(classe).getCard().get("MyAttr")));
    }

}
