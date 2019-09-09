/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.entrytype;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import org.cmdbuild.dao.beans.AttributeMetadataImpl;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;

public class AttributeWithoutOwnerImpl implements AttributeWithoutOwner {

    private final CardAttributeType<?> type;
    private final String name;
    private final AttributeMetadata meta;
    private final AttributeGroupInfo group;
    private final AttributePermissions permissions;

    private AttributeWithoutOwnerImpl(AttributeWithoutOwnerImplBuilder builder) {
        this.type = checkNotNull(builder.type);
        this.name = checkNotBlank(builder.name);
        this.meta = firstNotNull(builder.meta, new AttributeMetadataImpl());
        this.group = builder.group;
        this.permissions = AttributePermissionsImpl.builder().withPermissions(Optional.ofNullable(builder.permissions).map(AttributePermissions::getPermissionMap).orElse(meta.getPermissionMap())).build();
    }

    @Override
    public CardAttributeType<?> getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public AttributeMetadata getMetadata() {
        return meta;
    }

    @Override
    @Nullable
    public AttributeGroupInfo getGroupOrNull() {
        return group;
    }

    public static AttributeWithoutOwnerImplBuilder builder() {
        return new AttributeWithoutOwnerImplBuilder();
    }

    public static AttributeWithoutOwnerImplBuilder copyOf(AttributeWithoutOwner source) {
        return new AttributeWithoutOwnerImplBuilder()
                .withType(source.getType())
                .withName(source.getName())
                .withGroup(source.getGroupOrNull())
                .withPermissions(source)
                .withMeta(source.getMetadata());
    }

    @Override
    public Map<PermissionScope, Set<AttributePermission>> getPermissionMap() {
        return permissions.getPermissionMap();
    }

    public static class AttributeWithoutOwnerImplBuilder implements Builder<AttributeWithoutOwnerImpl, AttributeWithoutOwnerImplBuilder> {

        private CardAttributeType<?> type;
        private String name;
        private AttributeMetadata meta;
        private AttributeGroupInfo group;
        private AttributePermissions permissions;

        public AttributeWithoutOwnerImplBuilder withType(CardAttributeType<?> type) {
            this.type = type;
            return this;
        }

        public AttributeWithoutOwnerImplBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public AttributeWithoutOwnerImplBuilder withMeta(AttributeMetadata meta) {
            this.meta = meta;
            return this;
        }

        public AttributeWithoutOwnerImplBuilder withGroup(AttributeGroupInfo group) {
            this.group = group;
            return this;
        }

        public AttributeWithoutOwnerImplBuilder withPermissions(AttributePermissions permissions) {
            this.permissions = permissions;
            return this;
        }

        @Override
        public AttributeWithoutOwnerImpl build() {
            return new AttributeWithoutOwnerImpl(this);
        }

    }
}
