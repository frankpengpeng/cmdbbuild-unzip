/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.navtree;

import java.util.List;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.utils.lang.JsonBean;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@JsonBean(NavTreeNodeImpl.class)
public interface NavTreeNode {

    String getId();

    @Nullable
    String getParentId();

    String getTargetClassName();

    String getTargetClassDescription();

    @Nullable
    String getDomainName();

    boolean getDirect();

    boolean getShowOnlyOne();

    List<NavTreeNode> getChildNodes();

    default boolean hasChildNodes() {
        return !getChildNodes().isEmpty();
    }

    @Nullable
    String getTargetFilter();

    boolean getEnableRecursion();

    default List<NavTreeNode> getThisNodeAndAllDescendants() {
        return list(this).accept(l -> getChildNodes().stream().map(NavTreeNode::getThisNodeAndAllDescendants).forEach(l::addAll));
    }

    default boolean hasParent() {
        return isNotBlank(getParentId());
    }
}
