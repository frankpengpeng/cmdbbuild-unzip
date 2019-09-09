package org.cmdbuild.lookup;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import javax.annotation.Nullable;
import static org.cmdbuild.common.Constants.LOOKUP_CLASS_NAME;
import org.cmdbuild.dao.beans.IdAndDescription;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_NOTES;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmNullableUtils.ltEqZeroToNull;

@CardMapping(LOOKUP_CLASS_NAME)
public class LookupImpl implements Lookup, IdAndDescription {//TODO move IdAndDescription to Lookup interface

    private final Long id;
    private final String code, textColor, iconColor, iconImage, iconFont;
    private final IconType iconType;
    private final String description;
    private final String notes;
    private final LookupType type;
    private final Integer number;
    private final boolean isDefault, isActive;
    private final Long parentId;
    private final Lookup parent;

    private LookupImpl(LookupBuilder builder) {
        this.id = ltEqZeroToNull(builder.id);
        this.code = checkNotBlank(builder.code);
        this.description = builder.description;
        this.notes = builder.notes;
        this.type = LookupTypeImpl.builder().withName(builder.typeName).withParent(builder.parentName).build();
        this.number = firstNotNull(builder.number, 0);
        this.isDefault = firstNotNull(builder.isDefault, false);
        this.isActive = firstNotNull(builder.isActive, true);
        this.parentId = ltEqZeroToNull(builder.parentId);
        this.parent = builder.parent;
        checkArgument(parent == null || parentId != null, "cannot set parentId null and parent not null");
        this.textColor = emptyToNull(builder.textColor);
        this.iconColor = emptyToNull(builder.iconColor);
        this.iconType = checkNotNull(builder.iconType);
        switch (iconType) {
            case FONT:
                this.iconFont = checkNotBlank(builder.iconFont);
                this.iconImage = null;
                break;
            case IMAGE:
                this.iconFont = null;
                this.iconImage = checkNotBlank(builder.iconImage);
                break;
            case NONE:
                this.iconFont = null;
                this.iconImage = null;
                break;
            default:
                throw new IllegalArgumentException("unsupported icon type = " + iconType);
        }
    }

    @Override
    @CardAttr(ATTR_CODE)
    public String getCode() {
        return code;
    }

    @Override
    @CardAttr(ATTR_DESCRIPTION)
    public String getDescription() {
        return description;
    }

    @Override
    @CardAttr(ATTR_NOTES)
    public String getNotes() {
        return notes;
    }

    @CardAttr("Type")
    public String getTypeName() {
        return type.getName();
    }

    @Override
    public LookupType getType() {
        return type;
    }

    @Override
    @CardAttr("Index")
    public Integer getIndex() {
        return number;
    }

    @Override
    @CardAttr("IsDefault")
    public boolean isDefault() {
        return isDefault;
    }

    @Override
    @CardAttr("IsActive")
    public boolean isActive() {
        return isActive;
    }

    @Override
    @CardAttr("ParentId")
    @Nullable
    public Long getParentId() {
        return parentId;
    }

    @Nullable
    @CardAttr("ParentType")
    public String getParentName() {
        return type.getParentOrNull();
    }

    @Override
    @Nullable
    public Lookup getParent() {
        return parent;
    }

    @Override
    @CardAttr(ATTR_ID)
    public Long getId() {
        return id;
    }

    @Override
    public IconType getIconType() {
        return iconType;
    }

    @CardAttr("IconType")
    public String getIconTypeAsString() {
        return iconType.name().toLowerCase();
    }

    @Override
    @Nullable
    @CardAttr("IconImage")
    public String getIconImage() {
        return iconImage;
    }

    @Override
    @Nullable
    @CardAttr("IconFont")
    public String getIconFont() {
        return iconFont;
    }

    @Override
    @Nullable
    @CardAttr("TextColor")
    public String getTextColor() {
        return textColor;
    }

    @Override
    @Nullable
    @CardAttr("IconColor")
    public String getIconColor() {
        return iconColor;
    }

    @Override
    public String toString() {
        return "LookupImpl{" + "id=" + id + ", code=" + code + ", type=" + type.getName() + '}';
    }

    public static LookupImpl.LookupBuilder builder() {
        return new LookupBuilder();
    }

    public static LookupImpl.LookupBuilder copyOf(Lookup lookup) {
        return new LookupBuilder()
                .withCode(lookup.getCode())
                .withDefault(lookup.isDefault())
                .withDescription(lookup.getDescription())
                .withId(lookup.getId())
                .withNotes(lookup.getNotes())
                .withIndex(lookup.getIndex())
                .withParent(lookup.getParent())
                .withParentId(lookup.getParentId())
                .withType(lookup.getType())
                .withTextColor(lookup.getTextColor())
                .withIconColor(lookup.getIconColor())
                .withIconType(lookup.getIconType())
                .withIconFont(lookup.getIconFont())
                .withActive(lookup.isActive())
                .withIconImage(lookup.getIconImage());
    }

    public static class LookupBuilder implements Builder<LookupImpl, LookupBuilder> {

        private Long id;
        private String code, textColor, iconColor, iconImage, iconFont, typeName, parentName;
        private IconType iconType = IconType.NONE;
        private String description;
        private String notes;
        private Integer number;
        private Boolean isDefault, isActive;
        private Long parentId;
        private Lookup parent;

        public LookupImpl.LookupBuilder withId(Long value) {
            this.id = value;
            return this;
        }

        public LookupImpl.LookupBuilder withCode(String value) {
            this.code = value;
            return this;
        }

        public LookupImpl.LookupBuilder withIconType(IconType value) {
            this.iconType = value;
            return this;
        }

        public LookupImpl.LookupBuilder withIconTypeAsString(String value) {
            return this.withIconType(IconType.valueOf(checkNotBlank(value).toUpperCase()));
        }

        public LookupImpl.LookupBuilder withIconImage(String value) {
            this.iconImage = value;
            return this;
        }

        public LookupImpl.LookupBuilder withIconFont(String value) {
            this.iconFont = value;
            return this;
        }

        public LookupImpl.LookupBuilder withIconColor(String iconColor) {
            this.iconColor = iconColor;
            return this;
        }

        public LookupImpl.LookupBuilder withTextColor(String textColor) {
            this.textColor = textColor;
            return this;
        }

        public LookupImpl.LookupBuilder withDescription(String value) {
            this.description = value;
            return this;
        }

        public LookupBuilder withNotes(String value) {
            this.notes = value;
            return this;
        }

        public LookupImpl.LookupBuilder withType(LookupType value) {
            if (value != null) {
                this.typeName = value.getName();
                this.parentName = value.getParentOrNull();
            }
            return this;
        }

        public LookupImpl.LookupBuilder withTypeName(String value) {
            this.typeName = value;
            return this;
        }

        public LookupImpl.LookupBuilder withParentName(String value) {
            this.parentName = value;
            return this;
        }

        public LookupImpl.LookupBuilder withIndex(Integer value) {
            this.number = value;
            return this;
        }

        public LookupImpl.LookupBuilder withDefault(boolean value) {
            this.isDefault = value;
            return this;
        }

        public LookupImpl.LookupBuilder withActive(boolean value) {
            this.isActive = value;
            return this;
        }

        public LookupImpl.LookupBuilder withParentId(Long value) {
            this.parentId = value;
            return this;
        }

        public LookupImpl.LookupBuilder withParent(@Nullable Lookup value) {
            this.parentId = value == null ? null : value.getId();
            this.parent = value;
            if (value != null) {
                parentName = value.getType().getName();
            }
            return this;
        }

        @Override
        public LookupImpl build() {
            return new LookupImpl(this);
        }

    }

}
