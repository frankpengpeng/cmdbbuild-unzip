/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.core;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.ClassDefinitionImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.test.framework.CmTestRunner;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.prepareTuid;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.tuid;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CmTestRunner.class)
public class ClassIT {

    private final DaoService dao;

    public ClassIT(DaoService dao) {
        this.dao = checkNotNull(dao);
    }

    @Before
    public void init() {
        prepareTuid();
    }

    @Test
    public void testClassCreation() {
        Classe superOne = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("TestClassOneParent")).withSuperclass(true).build());
        Classe one = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("TestClassOneChild")).withParent(superOne).build());
        Classe two = dao.createClass(ClassDefinitionImpl.builder().withName("TestClassName").build());

        assertEquals(one.getParent(), superOne.getName());
        assertEquals(two.getName(), "TestClassName");
    }

    @Test
    public void testGetDeletedClass() {
        Classe two = dao.createClass(ClassDefinitionImpl.builder().withName("ClassToDelete").build());
        dao.deleteClass(two);

        try {
            dao.getClasse(two);
        } catch (NullPointerException e) {
            assertEquals("classe not found for name = ClassToDelete", e.getMessage());
        }
    }

    @Test
    public void testDeleteParentClassWithChild() {
        Classe superOne = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("TestClassOneParent")).withSuperclass(true).build());
        dao.createClass(ClassDefinitionImpl.builder().withName(tuid("TestClassOneChild")).withParent(superOne).build());

        try {
            dao.deleteClass(superOne);
        } catch (Exception e) {
            assertEquals(true, e.getMessage().contains("class has descendants"));
        }
    }
}
