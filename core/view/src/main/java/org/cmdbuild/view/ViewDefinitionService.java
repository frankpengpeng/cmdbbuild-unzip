package org.cmdbuild.view;

import java.util.List;
import static org.apache.commons.lang3.math.NumberUtils.isNumber;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;

public interface ViewDefinitionService {

    List<View> getAllViews();

    View getByName(String name);

    List<View> getViewsForCurrentUser();

    List<View> getActiveViewsForCurrentUser();

    List<View> getForCurrentUserByType(ViewType type);

    View getForCurrentUserById(long id);

    View getForCurrentUserByName(String name);

    View create(View view);

    View update(View view);

    void delete(long id);

    boolean isActiveAndUserAccessibleByName(String name);

    default View getForCurrentUserByIdOrName(String viewId) {
        return isNumber(viewId) ? getForCurrentUserById(toLong(viewId)) : getForCurrentUserByName(viewId);
    }

}
