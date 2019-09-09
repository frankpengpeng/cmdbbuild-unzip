/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.ClassDefinitionImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.menu.Menu;
import org.cmdbuild.menu.MenuItemType;
import org.cmdbuild.menu.MenuService;
import org.cmdbuild.menu.MenuTreeNodeImpl;
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
public class MenuIT {

    private final MenuService menuService;
    private final DaoService dao;

    private MenuTreeNodeImpl menu;
    private Menu createdMenu;
    private Classe myClass;

    public MenuIT(MenuService menuService, DaoService dao) {
        this.menuService = checkNotNull(menuService);
        this.dao = checkNotNull(dao);
    }

    @Before
    public void init() {
        prepareTuid();
        myClass = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyTestClass")).build());
        menu = MenuTreeNodeImpl.builder()
                .withCode(randomId())
                .withDescription("ROOT")
                .withType(MenuItemType.ROOT)
                .withChildren(list(MenuTreeNodeImpl.builder()
                        .withCode(randomId())
                        .withTarget(myClass.getName())
                        .withDescription("Node1")
                        .withType(MenuItemType.CLASS)
                        .withChildren(list()).build())).
                build();

        createdMenu = menuService.create(tuid("testMenu"), menu);
    }

    @Test
    public void getMenu() {
        Menu menuById = menuService.getMenuById(createdMenu.getId());
        assertEquals("ROOT", menuById.getRootNode().getDescription());
        assertEquals("Node1", getOnlyElement(menuById.getRootNode().getChildren()).getDescription());
    }

    @Test
    public void deleteMenu() {
        Menu menuById = menuService.getMenuById(createdMenu.getId());
        menuService.delete(menuById.getId());
        assertEquals(null, menuService.getMenuByIdOrNull(createdMenu.getId()));
    }

}
