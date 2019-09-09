package org.cmdbuild.extcomponents.contextmenu;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import javax.annotation.Nullable;

public interface ContextMenuComponentRepository {

    List<ContextMenuComponentData> getAll();

    ContextMenuComponentData create(ContextMenuComponentData customPage);

    ContextMenuComponentData update(ContextMenuComponentData customPage);

    void delete(long id);

    @Nullable
    ContextMenuComponentData getByNameOrNull(String name);

    default ContextMenuComponentData getByName(String name) {
        return checkNotNull(getByNameOrNull(name), "context menu component not found for name = %s", name);
    }

    ContextMenuComponentData getById(long id);

}
