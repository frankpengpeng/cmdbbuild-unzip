/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.grant;

import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.beans.CmdbFilterImpl;
import org.cmdbuild.data.filter.utils.CmdbFilterUtils;
import static org.cmdbuild.data.filter.utils.CmdbFilterUtils.parseFilter;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class GroupOfPrivilegesImpl implements GroupOfPrivileges {

    private final Set<GrantPrivilege> privileges;
    private final Map<String, Set<GrantAttributePrivilege>> attributePrivileges;
    private final String source;
    private final CmdbFilter filter;

    private GroupOfPrivilegesImpl(GroupOfPrivilegesImplBuilder builder) {
        this.source = checkNotBlank(builder.source);
        this.privileges = checkNotNull(builder.privileges);
        this.attributePrivileges = builder.attributePrivileges;
        this.filter = isBlank(builder.filter) ? CmdbFilterImpl.noopFilter() : parseFilter(builder.filter);
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public Set<GrantPrivilege> getPrivileges() {
        return privileges;
    }

    @Override
    @Nullable
    public Map<String, Set<GrantAttributePrivilege>> getAttributePrivileges() {
        return attributePrivileges;
    }

    @Override
    public CmdbFilter getFilter() {
        return filter;
    }

    public static GroupOfPrivilegesImplBuilder builder() {
        return new GroupOfPrivilegesImplBuilder();
    }

    public static GroupOfPrivilegesImplBuilder copyOf(GroupOfPrivileges source) {
        return new GroupOfPrivilegesImplBuilder()
                .withPrivileges(source.getPrivileges())
                .withAttributePrivileges(source.getAttributePrivileges())
                .withSource(source.getSource())
                .withFilter(source.getFilter());
    }

    public static class GroupOfPrivilegesImplBuilder implements Builder<GroupOfPrivilegesImpl, GroupOfPrivilegesImplBuilder> {

        private String source;
        private Set<GrantPrivilege> privileges;
        private Map<String, Set<GrantAttributePrivilege>> attributePrivileges;
        private String filter;

        public GroupOfPrivilegesImplBuilder withSource(String source) {
            this.source = source;
            return this;
        }

        public GroupOfPrivilegesImplBuilder withPrivileges(Set<GrantPrivilege> privileges) {
            this.privileges = privileges;
            return this;
        }

        public GroupOfPrivilegesImplBuilder withAttributePrivileges(Map<String, Set<GrantAttributePrivilege>> attributePrivileges) {
            this.attributePrivileges = attributePrivileges;
            return this;
        }

        public GroupOfPrivilegesImplBuilder withFilter(@Nullable String filter) {
            this.filter = filter;
            return this;
        }

        public GroupOfPrivilegesImplBuilder withFilter(@Nullable CmdbFilter filter) {
            this.filter = filter == null ? null : CmdbFilterUtils.serializeFilter(filter);
            return this;
        }

        @Override
        public GroupOfPrivilegesImpl build() {
            return new GroupOfPrivilegesImpl(this);
        }

    }
}
