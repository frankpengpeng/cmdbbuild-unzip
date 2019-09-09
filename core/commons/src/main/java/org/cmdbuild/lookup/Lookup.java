package org.cmdbuild.lookup;

import static com.google.common.base.Preconditions.checkNotNull;
import javax.annotation.Nullable;

public interface Lookup {

    @Nullable
    Long getId();

    String getCode();

    String getDescription();

    String getNotes();

    LookupType getType();

    Integer getIndex();

    boolean isDefault();

    boolean isActive();

    @Nullable
    Long getParentId();

    @Nullable
    Lookup getParent();

    IconType getIconType();

    @Nullable
    String getTextColor();

    @Nullable
    String getIconImage();

    @Nullable
    String getIconFont();

    @Nullable
    String getIconColor();

    default boolean hasId() {
        return getId() != null;
    }

    @Nullable
    default String getParentTypeOrNull() {
        return hasParent() ? getParentNotNull().getType().getName() : null;
    }

    default boolean hasParentId() {
        return getParentId() != null;
    }

    default boolean hasParent() {
        return getParent() != null;
    }

    default Lookup getParentNotNull() {
        return checkNotNull(getParent());
    }

    enum IconType {
        FONT, IMAGE, NONE
    }
}
