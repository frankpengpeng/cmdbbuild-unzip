package org.cmdbuild.view;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.auth.user.OperationUser;

import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.Holder;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.springframework.stereotype.Component;

@Component
public class ViewDefinitionServiceImpl implements ViewDefinitionService {

    private final DaoService dao;
    private final OperationUserSupplier userStore;
    private final Holder<List<View>> viewCache;

    public ViewDefinitionServiceImpl(DaoService dao, OperationUserSupplier userStore, CacheService cacheService) {
        this.dao = checkNotNull(dao);
        this.userStore = checkNotNull(userStore);
        viewCache = cacheService.newHolder("view_all");
    }

    private void invalidateCache() {
        viewCache.invalidate();
    }

    @Override
    public List<View> getAllViews() {
        return viewCache.get(this::doGetAllViews);
    }

    @Override
    public List<View> getViewsForCurrentUser() {
        return getAllViews().stream().filter(this::isAccessibleByCurrentUser).collect(toList());
    }

    @Override
    public List<View> getActiveViewsForCurrentUser() {
        return getViewsForCurrentUser().stream().filter(this::isActive).collect(toList());
    }

    @Override
    public boolean isActiveAndUserAccessibleByName(String name) {
        return isAccessibleByCurrentUser(getByName(name)) && isActive(getByName(name));
    }

    @Override
    public List<View> getForCurrentUserByType(ViewType type) {
        return getViewsForCurrentUser().stream().filter((v) -> v.isOfType(type)).collect(toList());
    }

    @Override
    public View getForCurrentUserById(long id) {
        return getViewsForCurrentUser().stream().filter((v) -> v.getId() == id).collect(onlyElement("view not found for id = %s", id));
    }

    @Override
    public View getForCurrentUserByName(String name) {
        checkNotBlank(name);
        return getViewsForCurrentUser().stream().filter((v) -> equal(v.getName(), name)).collect(onlyElement("view not found for name = %s", name));
    }

    @Override
    public View getByName(String name) {
        checkNotBlank(name);
        return getAllViews().stream().filter((v) -> equal(v.getName(), name)).collect(onlyElement("view not found for name = %s", name));
    }

    @Override
    public View create(View view) {
        checkArgument(view.getId() == null);
        view = dao.create(view);
        invalidateCache();
        return view;
    }

    @Override
    public View update(View view) {
        if (view.getId() == null) {
            view = ViewImpl.copyOf(view).withId(getForCurrentUserByName(view.getName()).getId()).build();
        }
        view = dao.update(view);
        invalidateCache();
        return view;
    }

    @Override
    public void delete(long id) {
        dao.delete(ViewImpl.class, id);
        invalidateCache();
    }

    private boolean isAccessibleByCurrentUser(View view) {
        OperationUser user = userStore.getUser();
        return user.hasReadAccess(view);
    }

    private boolean isActive(View view) {
        return view.isActive() && (view.isOfType(ViewType.FILTER) ? isClassActive(view.getSourceClass()) : true);
    }

    private List<View> doGetAllViews() {
        return dao.selectAll().from(ViewImpl.class).asList();
    }

    private boolean isClassActive(String classId) {
        return dao.getClasse(classId).isActive();
    }

}
