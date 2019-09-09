/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.view;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.entrytype.Attribute;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class ViewServiceImpl implements ViewService {

    private final ViewAccessService viewAccessService;
    private final ViewDefinitionService viewDefinitionService;

    public ViewServiceImpl(ViewAccessService viewAccessService, ViewDefinitionService viewDefinitionService) {
        this.viewAccessService = checkNotNull(viewAccessService);
        this.viewDefinitionService = checkNotNull(viewDefinitionService);
    }

    @Override
    public Collection<Attribute> getAttributesForView(View view) {
        return viewAccessService.getAttributesForView(view);
    }

    @Override
    public PagedElements<Map<String, Object>> getRecords(View view, DaoQueryOptions queryOptions) {
        return viewAccessService.getRecords(view, queryOptions);
    }

    @Override
    public List<View> getAllViews() {
        return viewDefinitionService.getAllViews();
    }

    @Override
    public View getByName(String name) {
        return viewDefinitionService.getByName(name);
    }

    @Override
    public List<View> getViewsForCurrentUser() {
        return viewDefinitionService.getViewsForCurrentUser();
    }

    @Override
    public List<View> getForCurrentUserByType(ViewType type) {
        return viewDefinitionService.getForCurrentUserByType(type);
    }

    @Override
    public View getForCurrentUserById(long id) {
        return viewDefinitionService.getForCurrentUserById(id);
    }

    @Override
    public View getForCurrentUserByName(String name) {
        return viewDefinitionService.getForCurrentUserByName(name);
    }

    @Override
    public View create(View view) {
        return viewDefinitionService.create(view);
    }

    @Override
    public View update(View view) {
        return viewDefinitionService.update(view);
    }

    @Override
    public void delete(long id) {
        viewDefinitionService.delete(id);
    }

    @Override
    public boolean isActiveAndUserAccessibleByName(String name) {
        return viewDefinitionService.isActiveAndUserAccessibleByName(name);
    }

    @Override
    public List<View> getActiveViewsForCurrentUser() {
        return viewDefinitionService.getActiveViewsForCurrentUser();
    }

    @Override
    public View getForCurrentUserByIdOrName(String viewId) {
        return viewDefinitionService.getForCurrentUserByIdOrName(viewId);
    }

    @Override
    public Card getCardById(View view, long cardId) {
        return viewAccessService.getCardById(view, cardId);
    }

    @Override
    public PagedElements<Card> getCards(View view, DaoQueryOptions queryOptions) {
        return viewAccessService.getCards(view, queryOptions);
    }

}
