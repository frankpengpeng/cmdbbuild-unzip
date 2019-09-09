package org.cmdbuild.extcomponents.contextmenu;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.MoreCollectors.onlyElement;
import static com.google.common.collect.MoreCollectors.toOptional;
import java.util.List;
import javax.annotation.Nullable;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.Holder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.core.q3.DaoService;

@Component
public class ContextMenuComponentRepositoryImpl implements ContextMenuComponentRepository {

    private final DaoService dao;
    private final Holder<List<ContextMenuComponentData>> contextMenuComponentsHolder;

    public ContextMenuComponentRepositoryImpl(DaoService dao, CacheService cacheService) {
        this.dao = checkNotNull(dao);
        contextMenuComponentsHolder = cacheService.newHolder("all_context_menu_components");
    }

    private void invalidateCache() {
        contextMenuComponentsHolder.invalidate();
    }

    @Override
    public List<ContextMenuComponentData> getAll() {
        return contextMenuComponentsHolder.get(this::doReadAll);
    }

    @Override
    @Nullable
    public ContextMenuComponentData getByNameOrNull(String name) {
        checkNotBlank(name);
        return getAll().stream().filter((c) -> equal(c.getName(), name)).collect(toOptional()).orElse(null);
    }

    @Override
    public ContextMenuComponentData getById(long id) {
        return getAll().stream().filter((c) -> equal(c.getId(), id)).collect(onlyElement());
    }

    @Override
    public ContextMenuComponentData create(ContextMenuComponentData component) {
        component = dao.create(component);
        invalidateCache();
        return component;
    }

    @Override
    public ContextMenuComponentData update(ContextMenuComponentData component) {
        component = dao.update(component);
        invalidateCache();
        return component;
    }

    @Override
    public void delete(long id) {
        dao.delete(ContextMenuComponentData.class, id);
        invalidateCache();
    }

    private List<ContextMenuComponentData> doReadAll() {
        return dao.selectAll().from(ContextMenuComponentData.class).asList();
    }

}
