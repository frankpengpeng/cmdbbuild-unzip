package org.cmdbuild.extcomponents.contextmenu;

import javax.annotation.Nullable;

public interface ContextMenuComponentData {

    static final String CONTEXT_MENU_COMPONENT_TABLE_NAME = "_ContextMenuComp";

    @Nullable
    Long getId();

    boolean getActive();

    String getName();

    String getDescription();

    byte[] getData();
}
