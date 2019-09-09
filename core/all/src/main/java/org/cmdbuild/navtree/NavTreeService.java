package org.cmdbuild.navtree;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;

public interface NavTreeService {

    List<NavTree> getAll();

    default List<NavTree> getAllActive() {
        return getAll().stream().filter(NavTree::getActive).collect(toList());
    }

    @Nullable
    NavTree getTreeOrNull(String type);

    default NavTree getTree(String type) {
        return checkNotNull(getTreeOrNull(type), "nav tree not found for name = %s", type);
    }

    void removeTree(String treeType);

    NavTree create(NavTree tree);

    NavTree update(NavTree tree);

}
