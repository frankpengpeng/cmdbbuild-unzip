/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.grant;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableMap;
import static org.cmdbuild.auth.grant.GrantConstants.GRANT_CLASS_NAME;
import org.cmdbuild.dao.orm.annotations.CardMapping;

import static java.util.Collections.emptyMap;
import java.util.EnumSet;
import java.util.Map;
import javax.annotation.Nullable;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.isNullOrEmpty;
import static org.cmdbuild.auth.grant.GrantConstants.GRANT_ATTR_PRIVILEGED_CLASS_ID;
import static org.cmdbuild.auth.grant.GrantConstants.GRANT_ATTR_PRIVILEGED_OBJECT_ID;
import static org.cmdbuild.auth.grant.GrantConstants.GRANT_ATTR_MODE;
import static org.cmdbuild.auth.grant.GrantConstants.GRANT_ATTR_PRIVILEGE_FILTER;
import static org.cmdbuild.auth.grant.GrantConstants.GRANT_ATTR_UI_CARD_EDIT_MODE;
import static org.cmdbuild.auth.grant.GrantConstants.GRANT_ATTR_ATTRIBUTES_PRIVILEGES;
import static org.cmdbuild.auth.grant.GrantConstants.GRANT_ATTR_TYPE;
import static org.cmdbuild.auth.grant.GrantConstants.GRANT_ATTR_ROLE_ID;
import static org.cmdbuild.auth.grant.GrantModeUtils.parseGrantMode;
import static org.cmdbuild.auth.grant.GrantModeUtils.serializeGrantMode;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLongOrNull;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNullOrNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import static org.cmdbuild.auth.grant.GrantUtils.serializePrivilegedObjectType;
import static org.cmdbuild.auth.grant.GrantUtils.parsePrivilegedObjectType;
import org.cmdbuild.utils.lang.JsonBean;

@CardMapping(GRANT_CLASS_NAME)
public class GrantDataImpl implements GrantData {

    private final Long id, objectId;
    private final long roleId;
    private final String className;
    private final GrantMode mode;
    private final String privilegeFilter;
    private final Map<String, String> attrPrivileges;
    private final Map<String, Object> cardEditMode;
    private final PrivilegedObjectType type;

    private GrantDataImpl(GrantDataImplBuilder builder) {
        this.id = builder.id;
        this.roleId = builder.roleId;
        this.type = checkNotNull(builder.type);
        this.mode = checkNotNull(builder.mode);
        switch (type) {
            case POT_CLASS:
                this.className = checkNotBlank(toStringOrNull(firstNotNullOrNull(builder.className, builder.objectIdOrClassName)), "class name cannot be null");
                this.objectId = null;
                this.privilegeFilter = builder.privilegeFilter;
                this.cardEditMode = isNullOrEmpty(builder.cardEditMode) ? emptyMap() : ImmutableMap.copyOf(builder.cardEditMode);
                this.attrPrivileges = isNullOrEmpty(builder.attrPrivileges) ? emptyMap() : ImmutableMap.copyOf(builder.attrPrivileges);
                break;
            case POT_VIEW:
                this.className = null;
                this.objectId = checkNotNull(toLongOrNull(firstNotNullOrNull(builder.objectId, builder.objectIdOrClassName)), "object id cannot be null");
                this.privilegeFilter = builder.privilegeFilter;
                this.cardEditMode = isNullOrEmpty(builder.cardEditMode) ? emptyMap() : ImmutableMap.copyOf(builder.cardEditMode);
                this.attrPrivileges = isNullOrEmpty(builder.attrPrivileges) ? emptyMap() : ImmutableMap.copyOf(builder.attrPrivileges);
                break;
            case POT_FILTER:
                this.className = null;
                this.objectId = checkNotNull(toLongOrNull(firstNotNullOrNull(builder.objectId, builder.objectIdOrClassName)), "object id cannot be null");
                this.privilegeFilter = null;
                this.cardEditMode = null;
                this.attrPrivileges = null;
                break;
            case POT_REPORT:
                checkArgument(EnumSet.of(GrantMode.GM_NONE, GrantMode.GM_READ).contains(mode), "invalid mode for report grant = %s", mode);
                this.className = null;
                this.objectId = checkNotNull(toLongOrNull(firstNotNullOrNull(builder.objectId, builder.objectIdOrClassName)), "object id (report id) cannot be null");
                this.privilegeFilter = null;
                this.cardEditMode = null;
                this.attrPrivileges = null;
                break;
            case POT_CUSTOMPAGE:
                checkArgument(EnumSet.of(GrantMode.GM_NONE, GrantMode.GM_READ).contains(mode), "invalid mode for custom page grant = %s", mode);
                this.className = null;
                this.objectId = checkNotNull(toLongOrNull(firstNotNullOrNull(builder.objectId, builder.objectIdOrClassName)), "object id (custom page id) cannot be null");
                this.privilegeFilter = null;
                this.cardEditMode = null;
                this.attrPrivileges = null;
                break;
            case POT_IMPORT_EXPORT_TEMPLATE:
                checkArgument(EnumSet.of(GrantMode.GM_NONE, GrantMode.GM_READ).contains(mode), "invalid mode for import export template grant = %s", mode);
                this.className = null;
                this.objectId = checkNotNull(toLongOrNull(firstNotNullOrNull(builder.objectId, builder.objectIdOrClassName)), "object id cannot be null");
                this.privilegeFilter = null;
                this.cardEditMode = null;
                this.attrPrivileges = null;
                break;
            default:
                throw unsupported("unsupported grant type = %s", type);
        }
    }

    @Override
    @Nullable
    @CardAttr(ATTR_ID)
    public Long getId() {
        return id;
    }

    @Override
    @CardAttr(GRANT_ATTR_ROLE_ID)
    public long getRoleId() {
        return roleId;
    }

    @Override
    @Nullable
    @CardAttr(GRANT_ATTR_PRIVILEGED_CLASS_ID)
    public String getClassName() {
        return className;
    }

    @Override
    @Nullable
    @CardAttr(GRANT_ATTR_PRIVILEGED_OBJECT_ID)
    public Long getObjectId() {
        return objectId;
    }

    @Override
    public GrantMode getMode() {
        return mode;
    }

    @CardAttr(GRANT_ATTR_MODE)
    public String getModeAsString() {
        return serializeGrantMode(mode);
    }

    @Override
    @Nullable
    @CardAttr(GRANT_ATTR_PRIVILEGE_FILTER)
    public String getPrivilegeFilter() {
        return privilegeFilter;
    }

    @Override
    @Nullable
    @CardAttr(GRANT_ATTR_UI_CARD_EDIT_MODE)
    @JsonBean
    public Map<String, Object> getCustomPrivileges() {
        return cardEditMode;
    }

    @Override
    @CardAttr(GRANT_ATTR_ATTRIBUTES_PRIVILEGES)
    @JsonBean
    public Map<String, String> getAttributePrivileges() {
        return attrPrivileges;
    }

    @Override
    public PrivilegedObjectType getType() {
        return type;
    }

    @CardAttr(GRANT_ATTR_TYPE)
    public String getTypeAsString() {
        return serializePrivilegedObjectType(type);
    }

    @Override
    public String toString() {
        return "GrantData{" + "id=" + id + ", subject=" + getObjectIdOrClassName() + ", roleId=" + roleId + ", mode=" + mode + ", type=" + type + '}';
    }

    public static GrantDataImplBuilder builder() {
        return new GrantDataImplBuilder();
    }

    public static GrantDataImplBuilder copyOf(GrantData source) {
        return new GrantDataImplBuilder()
                .withId(source.getId())
                .withType(source.getType())
                .withRoleId(source.getRoleId())
                .withClassName(source.getClassName())
                .withObjectId(source.getObjectId())
                .withMode(source.getMode())
                .withPrivilegeFilter(source.getPrivilegeFilter())
                .withCustomPrivileges(source.getCustomPrivileges())
                .withAttributePrivileges(source.getAttributePrivileges());
    }

    public static class GrantDataImplBuilder implements Builder<GrantDataImpl, GrantDataImplBuilder> {

        private Long id, roleId;
        private Long objectId;
        private Object objectIdOrClassName;
        private GrantMode mode;
        private String className, privilegeFilter;
        private Map<String, Object> cardEditMode;
        private Map<String, String> attrPrivileges;
        private PrivilegedObjectType type;

        public GrantDataImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public GrantDataImplBuilder withRoleId(Long roleId) {
            this.roleId = roleId;
            return this;
        }

        public GrantDataImplBuilder withClassName(String classOid) {
            this.className = classOid;
            return this;
        }

        public GrantDataImplBuilder withObjectIdOrClassName(Object objectIdOrClassName) {
            this.objectIdOrClassName = objectIdOrClassName;
            return this;
        }

        public GrantDataImplBuilder withObjectId(Long objectId) {
            this.objectId = objectId;
            return this;
        }

        public GrantDataImplBuilder withMode(GrantMode mode) {
            this.mode = mode;
            return this;
        }

        public GrantDataImplBuilder withModeAsString(String mode) {
            this.mode = parseGrantMode(mode);
            return this;
        }

        public GrantDataImplBuilder withType(PrivilegedObjectType type) {
            this.type = type;
            return this;
        }

        public GrantDataImplBuilder withTypeAsString(String type) {
            this.type = parsePrivilegedObjectType(type);
            return this;
        }

        public GrantDataImplBuilder withPrivilegeFilter(String privilegeFilter) {
            this.privilegeFilter = privilegeFilter;
            return this;
        }

        public GrantDataImplBuilder withCustomPrivileges(Map<String, Object> cardEditMode) {
            this.cardEditMode = cardEditMode;
            return this;
        }

        public GrantDataImplBuilder withAttributePrivileges(Map<String, String> attrPrivileges) {
            this.attrPrivileges = attrPrivileges;
            return this;
        }

        @Override
        public GrantDataImpl build() {
            return new GrantDataImpl(this);
        }

    }
}
