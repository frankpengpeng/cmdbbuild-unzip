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
import org.cmdbuild.dao.entrytype.Domain;
import static org.cmdbuild.dao.entrytype.DomainCardinality.ONE_TO_MANY;
import org.cmdbuild.dao.entrytype.DomainDefinitionImpl;
import org.cmdbuild.navtree.NavTree;
import org.cmdbuild.navtree.NavTreeImpl;
import org.cmdbuild.navtree.NavTreeNodeImpl;
import org.cmdbuild.navtree.NavTreeService;
import org.cmdbuild.test.framework.CmTestRunner;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.prepareTuid;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.tuid;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CmTestRunner.class)
public class NavTreeIT {

    private final NavTreeService navTreeService;
    private final DaoService dao;

    Classe myClass, myClass2;
    Domain testDomain;

    public NavTreeIT(DaoService dao, NavTreeService navTreeService) {
        this.dao = checkNotNull(dao);
        this.navTreeService = checkNotNull(navTreeService);
    }

    @Before
    public void init() {
        myClass = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("NavTreeClass")).build());
        myClass2 = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("NavTreeClass2")).build());
        testDomain = dao.createDomain(DomainDefinitionImpl.builder().withName(tuid("MyNavTreeTestDomain_"))
                .withSourceClass(myClass).withTargetClass(myClass2).withCardinality(ONE_TO_MANY).build());

        prepareTuid();

    }

    @Test
    public void testNavTreeCreate() {
        NavTree navTree = navTreeService.create(NavTreeImpl.builder().withName("TestTree").withDescription("Test navTree").withActive(true)
                .withData(
                        NavTreeNodeImpl.builder()
                                .withId(randomId())
                                .withTargetClassName(myClass.getName())
                                .build()
                ).build());
        assertEquals(true, navTree.getActive());
        assertEquals("TestTree", navTree.getName());
        assertEquals("Test navTree", navTree.getDescription());
        assertEquals(myClass.getName(), navTree.getData().getTargetClassName());
    }

    @Test
    public void testNavTreeCreateModify() {
        NavTree navTree = navTreeService.create(NavTreeImpl.builder().withName("TestTree2").withDescription("Test navTree").withActive(true)
                .withData(
                        NavTreeNodeImpl.builder()
                                .withId(randomId())
                                .withTargetClassName(myClass.getName())
                                .build()
                ).build());

        assertEquals(true, navTree.getActive());
        assertEquals("TestTree2", navTree.getName());
        assertEquals("Test navTree", navTree.getDescription());

        navTree = navTreeService.update(NavTreeImpl.builder().withName("TestTree2").withDescription("Test navTree modified").withActive(false)
                .withData(
                        NavTreeNodeImpl.builder()
                                .withId(randomId())
                                .withTargetClassName(myClass.getName())
                                .withChildNodes(list(
                                        NavTreeNodeImpl.builder()
                                                .withId(randomId())
                                                .withTargetClassName(myClass2.getName())
                                                .withDomainName(testDomain.getName())
                                                .build()))
                                .build()
                ).build());
        assertEquals(false, navTree.getActive());
        assertEquals("TestTree2", navTree.getName());
        assertEquals("Test navTree modified", navTree.getDescription());
        assertEquals(myClass2.getName(), navTree.getData().getChildNodes().get(0).getTargetClassName());
    }

    @Test
    public void testNavTreeDelete() {
        NavTree navTree = navTreeService.create(NavTreeImpl.builder().withName("TestTree3").withDescription("Test navTree").withActive(true)
                .withData(
                        NavTreeNodeImpl.builder()
                                .withId(randomId())
                                .withTargetClassName(myClass.getName())
                                .build()
                ).build());
        navTreeService.removeTree(navTree.getName());
        assertEquals(null, navTreeService.getTreeOrNull(navTree.getName()));
    }
}
