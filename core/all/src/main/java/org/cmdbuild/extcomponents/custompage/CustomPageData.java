package org.cmdbuild.extcomponents.custompage;

import javax.annotation.Nullable;

public interface CustomPageData {

    static final String CUSTOM_PAGE_TABLE_NAME = "_CustomPage";

    @Nullable
    Long getId();

    boolean getActive();

    String getName();

    String getDescription();

    byte[] getData();
}
