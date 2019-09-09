package org.cmdbuild.dao.entrytype;

import static com.google.common.base.Preconditions.checkNotNull;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;

public interface AttributeWithoutOwner extends AttributePermissions {

    CardAttributeType<?> getType();

    String getName();

    default String getDescriptionOrName() {
        return firstNotBlank(getDescription(), getName());
    }

    @Nullable
    default String getDescription() {
        return getMetadata().getDescription();
    }

    default boolean isInherited() {
        return getMetadata().isInherited();
    }

    default boolean isActive() {
        return getMetadata().isActive();
    }

    default boolean showInGrid() {
        return getMetadata().showInGrid();
    }

    default boolean showInReducedGrid() {
        return getMetadata().showInReducedGrid();
    }

    default boolean isMandatory() {
        return getMetadata().isMandatory();
    }

    default boolean isUnique() {
        return getMetadata().isUnique();
    }

    default AttributePermissionMode getMode() {
        return getMetadata().getMode();
    }

    default int getIndex() {
        return getMetadata().getIndex();
    }

    default String getDefaultValue() {
        return getMetadata().getDefaultValue();
    }

    @Nullable
    AttributeGroupInfo getGroupOrNull();

    default AttributeGroupInfo getGroup() {
        return checkNotNull(getGroupOrNull());
    }

    default boolean hasGroup() {
        return getGroupOrNull() != null;
    }

    default String getGroupName() {
        return getGroup().getName();
    }

    @Nullable
    default String getGroupNameOrNull() {
        return hasGroup() ? getGroup().getName() : null;
    }

    @Nullable
    default String getGroupDescriptionOrNull() {
        return hasGroup() ? getGroup().getDescription() : null;
    }

    default int getClassOrder() {
        return getMetadata().getClassOrder();
    }

    default String getEditorType() {
        return getMetadata().getEditorType();
    }

    default boolean isDomainKey() {
        return getMetadata().isDomainKey();
    }

    default String getFilter() {
        return getMetadata().getFilter();
    }

    default boolean hasFilter() {
        return !isBlank(getFilter());
    }

    default String getForeignKeyDestinationClassName() {
        return getMetadata().getForeignKeyDestinationClassName();
    }

    AttributeMetadata getMetadata();

}
