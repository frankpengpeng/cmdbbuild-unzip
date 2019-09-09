package org.cmdbuild.menu;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import javax.annotation.Nullable;

public interface MenuService {

    default Menu getMenuById(long menuId) {
        return checkNotNull(getMenuByIdOrNull(menuId));
    }

    Menu getMenuByIdOrNull(long menuId);

    @Nullable
    Menu getMenuForGroupOrNull(String groupName);

    default Menu getMenuForGroup(String groupName) {
        return checkNotNull(getMenuForGroupOrNull(groupName), "menu not found for group = %s", groupName);
    }

    Menu create(String groupId, MenuTreeNode menu);

    Menu update(long menuId, MenuTreeNode menu);

    void delete(long menuId);

    MenuTreeNode getMenuForCurrentUser();

    List<MenuInfo> getAllMenuInfos();

}
