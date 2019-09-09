/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.menu;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class MenuImpl implements Menu {

    private final long id;
    private final MenuTreeNode rootNode;
    private final String groupName;

    public MenuImpl(Long id, MenuTreeNode rootNode, String groupName) {
        this.id = id;
        this.rootNode = checkNotNull(rootNode);
        this.groupName = checkNotBlank(groupName);
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getGroup() {
        return groupName;
    }

    @Override
    public MenuTreeNode getRootNode() {
        return rootNode;
    }

}
