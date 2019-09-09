package org.cmdbuild.dao.beans;

import javax.annotation.Nullable;
import org.cmdbuild.utils.lang.ToPrimitive;

public interface IdAndDescription extends ToPrimitive<Long> {

    @Nullable
    Long getId();

    @Nullable
    String getDescription();

    @Nullable
    String getCode();

    @Override
    default Long toPrimitive() {
        return getId();
    }

}
