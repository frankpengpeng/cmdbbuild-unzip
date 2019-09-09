/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.entrytype;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.dao.entrytype.EntryType.EntryTypeType;
import static org.cmdbuild.dao.entrytype.EntryType.EntryTypeType.ET_DOMAIN;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.dao.entrytype.EntryType.EntryTypeType.ET_CLASS;

public class AttributeGroupImpl implements AttributeGroupData {

    private final String name, description, ownerName;
    private final EntryTypeType ownerType;
    private final int index;

    private AttributeGroupImpl(AttributeGroupImplBuilder builder) {
        this.name = checkNotBlank(builder.name);
        this.description = firstNotBlank(builder.description, name);
        this.ownerType = checkNotNull(builder.ownerType);
        checkArgument(set(ET_CLASS, ET_DOMAIN).contains(ownerType));
        this.ownerName = checkNotBlank(builder.ownerName);
        this.index = builder.index;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public String getOwnerName() {
        return ownerName;
    }

    @Override
    public EntryTypeType getOwnerType() {
        return ownerType;
    }

    @Override
    public String toString() {
        return "AttributeGroupImpl{" + "name=" + name + ", owner=" + ownerName + '}';
    }

    public static AttributeGroupImplBuilder builder() {
        return new AttributeGroupImplBuilder();
    }

    public static AttributeGroupImplBuilder copyOf(AttributeGroupData source) {
        return new AttributeGroupImplBuilder()
                .withName(source.getName())
                .withDescription(source.getDescription())
                .withOwnerName(source.getOwnerName())
                .withOwnerType(source.getOwnerType())
                .withIndex(source.getIndex());
    }

    public static AttributeGroupImplBuilder copyOf(AttributeGroupInfo source) {
        return new AttributeGroupImplBuilder()
                .withName(source.getName())
                .withDescription(source.getDescription());
    }

    public static class AttributeGroupImplBuilder implements Builder<AttributeGroupImpl, AttributeGroupImplBuilder> {

        private String name;
        private String description, ownerName;
        private EntryTypeType ownerType;
        private Integer index;

        public AttributeGroupImplBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public AttributeGroupImplBuilder withIndex(Integer index) {
            this.index = index;
            return this;
        }

        public AttributeGroupImplBuilder withOwnerName(String ownerName) {
            this.ownerName = ownerName;
            return this;
        }

        public AttributeGroupImplBuilder withOwnerType(EntryTypeType ownerType) {
            this.ownerType = ownerType;
            return this;
        }

        public AttributeGroupImplBuilder withOwnerType(String ownerType) {
            return this.withOwnerType(parseEnum(ownerType, EntryTypeType.class));
        }

        public AttributeGroupImplBuilder withOwner(EntryType entryType) {
            return this.withOwnerName(entryType.getName()).withOwnerType(entryType.getEtType());
        }

        public AttributeGroupImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        @Override
        public AttributeGroupImpl build() {
            return new AttributeGroupImpl(this);
        }

    }
}
