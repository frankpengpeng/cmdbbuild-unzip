package org.cmdbuild.auth.grant;

import static com.google.common.base.Preconditions.checkArgument;
import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import static java.util.Collections.emptyMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import static org.cmdbuild.auth.grant.GrantPrivilege.GP_ALL;
import static org.cmdbuild.auth.grant.GrantPrivilege.GP_READ;
import static org.cmdbuild.auth.grant.GrantPrivilege.GP_WRITE;
import org.cmdbuild.utils.lang.Builder;

public class GrantImpl implements Grant {
 
	private final PrivilegedObjectType objectType;
	private final Set<GrantPrivilege> privileges;
	private final PrivilegeSubjectWithInfo object;
	private final String privilegeFilter;
	private final Map<String, GrantAttributePrivilege> attributePrivileges;

	private GrantImpl(PrivilegePairImplBuilder builder) {
		this.objectType = checkNotNull(builder.objectType);
		this.privileges = ImmutableSet.copyOf(checkNotNull(builder.privileges));
		this.object = checkNotNull(builder.object);
		this.privilegeFilter = builder.privilegeFilter;
		switch (objectType) {
			case POT_CLASS:
				this.attributePrivileges = ImmutableMap.copyOf(checkNotNull(builder.attributePrivileges));
				break;
			default:
				Set<GrantPrivilege> invalidPrivileges = Sets.difference(privileges, EnumSet.of(GP_ALL, GP_READ, GP_WRITE));
				checkArgument(invalidPrivileges.isEmpty(), "invalid grant privileges for type = %s, privileges = %s", objectType, invalidPrivileges);
				this.attributePrivileges = emptyMap();

		}
	}

	@Override
	public PrivilegedObjectType getObjectType() {
		return objectType;
	}

	@Override
	public Set<GrantPrivilege> getPrivileges() {
		return privileges;
	}

	@Override
	public PrivilegeSubjectWithInfo getSubject() {
		return object;
	}

	@Override
	@Nullable
	public String getFilterOrNull() {
		return privilegeFilter;
	}

	@Override
	public Map<String, GrantAttributePrivilege> getAttributePrivileges() {
		return attributePrivileges;
	}

	public static PrivilegePairImplBuilder builder() {
		return new PrivilegePairImplBuilder();
	}

	public static PrivilegePairImplBuilder copyOf(Grant source) {
		return new PrivilegePairImplBuilder()
				.withObjectType(source.getObjectType())
				.withPrivileges(source.getPrivileges())
				.withObject(source.getSubject())
				.withPrivilegeFilter(source.getFilterOrNull())
				.withAttributePrivileges(source.getAttributePrivileges());
	}

	public static class PrivilegePairImplBuilder implements Builder<GrantImpl, PrivilegePairImplBuilder> {

		private PrivilegedObjectType objectType;
		private Set<GrantPrivilege> privileges;
		private PrivilegeSubjectWithInfo object;
		private String privilegeFilter;
		private Map<String, GrantAttributePrivilege> attributePrivileges;

		public PrivilegePairImplBuilder withObjectType(PrivilegedObjectType objectType) {
			this.objectType = objectType;
			return this;
		}

		public PrivilegePairImplBuilder withPrivileges(Set<GrantPrivilege> privileges) {
			this.privileges = privileges;
			return this;
		}

		public PrivilegePairImplBuilder withObject(PrivilegeSubjectWithInfo object) {
			this.object = object;
			return this;
		}

		public PrivilegePairImplBuilder withPrivilegeFilter(String privilegeFilter) {
			this.privilegeFilter = privilegeFilter;
			return this;
		}

		public PrivilegePairImplBuilder withAttributePrivileges(Map<String, GrantAttributePrivilege> attributePrivileges) {
			this.attributePrivileges = attributePrivileges;
			return this;
		}

		@Override
		public GrantImpl build() {
			return new GrantImpl(this);
		}

	}
}
