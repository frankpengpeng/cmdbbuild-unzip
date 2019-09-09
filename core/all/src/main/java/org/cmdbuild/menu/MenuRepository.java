/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.menu;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import javax.annotation.Nullable;

public interface MenuRepository {

    @Nullable
    MenuData getMenuDataForGroupOrNull(String groupName);

    List<MenuInfo> getAllMenuInfos();

    default MenuData getMenuDataById(long menuId) {
        return checkNotNull(getMenuDataByIdOrNull(menuId));
    }

    MenuData getMenuDataByIdOrNull(long menuId);

    MenuData updateMenuData(MenuData menuData);

    MenuData createMenuData(MenuData menuData);

    void delete(long id);
}
