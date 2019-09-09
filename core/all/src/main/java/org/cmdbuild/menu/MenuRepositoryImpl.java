/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.menu;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.Subscribe;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import org.cmdbuild.auth.grant.GrantEventBusService;
import org.cmdbuild.auth.grant.GrantEventBusService.GrantDataUpdatedEvent;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.Holder;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.springframework.stereotype.Component;
import org.cmdbuild.cache.CmCache;
import org.cmdbuild.dao.core.q3.ResultRow;

@Component
public class MenuRepositoryImpl implements MenuRepository {

    private final GrantEventBusService grantEventBus;
    private final DaoService dao;
    private final CmCache<Optional<MenuData>> menuElementsByGroup;
    private final Holder<List<MenuInfo>> menuInfos;

    public MenuRepositoryImpl(DaoService dao, CacheService cacheService, GrantEventBusService grantEventService) {
        this.dao = checkNotNull(dao);
        this.grantEventBus = checkNotNull(grantEventService);
        menuElementsByGroup = cacheService.newCache("menu_elements_by_group");
        menuInfos = cacheService.newHolder("menu_infos");
        grantEventService.getEventBus().register(new Object() {
            @Subscribe
            public void handleGrantDataUpdatedEvent(GrantDataUpdatedEvent event) {
                invalidateCache();
            }
        });
    }

    private void invalidateCache() {
        menuElementsByGroup.invalidateAll();
        menuInfos.invalidate();
    }

    @Override
    public @Nullable
    MenuData getMenuDataForGroupOrNull(String groupName) {
        return menuElementsByGroup.get(groupName, () -> Optional.ofNullable(doGetMenuElementsForGroupOrNull(groupName))).orElse(null);
    }

    private @Nullable
    MenuData doGetMenuElementsForGroupOrNull(String groupName) {
        return dao.selectAll().from(MenuDataImpl.class).where("GroupName", EQ, checkNotBlank(groupName)).getOneOrNull();
    }

    @Override
    public List<MenuInfo> getAllMenuInfos() {
        return menuInfos.get(() -> doGetAllMenuInfos());
    }

    private List<MenuInfo> doGetAllMenuInfos() {
        return dao.selectAll().from(MenuInfoImpl.class).asList();
    }

    @Override
    @Nullable
    public MenuData getMenuDataByIdOrNull(long menuId) {
        ResultRow res = dao.getByIdOrNull(MenuDataImpl.class, menuId);
        return res == null ? null : res.toModel();
    }

    @Override
    public MenuData updateMenuData(MenuData menuData) {
        menuData = dao.update(menuData);
        invalidateCache();
        return menuData;
    }

    @Override
    public MenuData createMenuData(MenuData menuData) {
        menuData = dao.create(menuData);
        invalidateCache();
        return menuData;
    }

    @Override
    public void delete(long id) {
        dao.delete(MenuData.class, id);
        invalidateCache();
    }
}
