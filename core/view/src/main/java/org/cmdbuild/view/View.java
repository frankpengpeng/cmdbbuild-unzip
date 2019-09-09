package org.cmdbuild.view;

import static com.google.common.base.Objects.equal;
import javax.annotation.Nullable;
import org.cmdbuild.auth.grant.PrivilegeSubjectWithInfo;

public interface View extends PrivilegeSubjectWithInfo {

    @Override
    @Nullable
    Long getId();

    @Override
    String getName();

    @Override
    String getDescription();

    @Nullable
    String getSourceClass();

    @Nullable
    String getSourceFunction();

    @Nullable
    String getFilter();

    ViewType getType();

    boolean isActive();

    @Override
    String getPrivilegeId();

    default boolean isOfType(ViewType type) {
        return equal(getType(), type);
    }

}
