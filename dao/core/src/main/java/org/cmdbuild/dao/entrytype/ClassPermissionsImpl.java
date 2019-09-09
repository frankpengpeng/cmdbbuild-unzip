/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.entrytype;

import java.util.Map;
import java.util.Set;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import static com.google.common.collect.Maps.transformValues;
import static java.util.Collections.emptySet;
import static java.util.Collections.singletonMap;
import java.util.EnumSet;
import static org.cmdbuild.dao.entrytype.DaoPermissionUtils.intersectClassPermissions;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.dao.entrytype.DaoPermissionUtils.mergeClassPermissions;
import static org.cmdbuild.dao.entrytype.DaoPermissionUtils.removeClassPermissions;
import static org.cmdbuild.dao.entrytype.PermissionScope.PS_CORE;
import static org.cmdbuild.dao.entrytype.PermissionScope.PS_SERVICE;
import static org.cmdbuild.dao.entrytype.PermissionScope.PS_UI;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

public class ClassPermissionsImpl implements ClassPermissions {

	private final static ClassPermissions ALL = new ClassPermissionsImplBuilder().withPermissions(map(PS_CORE, EnumSet.allOf(ClassPermission.class), PS_SERVICE, EnumSet.allOf(ClassPermission.class), PS_UI, EnumSet.allOf(ClassPermission.class))).build(),
			NONE = new ClassPermissionsImplBuilder().withPermissions(map(PS_CORE, emptySet(), PS_SERVICE, emptySet(), PS_UI, emptySet())).build();

	private final Map<PermissionScope, Set<ClassPermission>> permissions;

	private ClassPermissionsImpl(ClassPermissionsImplBuilder builder) {
		this.permissions = ImmutableMap.copyOf(transformValues(checkNotNull(builder.permissions), ImmutableSet::copyOf));
	}

	@Override
	public Map<PermissionScope, Set<ClassPermission>> getPermissionsMap() {
		return permissions;
	}

	public static ClassPermissions all() {
		return ALL;
	}

	public static ClassPermissions none() {
		return NONE;
	}

	public static ClassPermissionsImplBuilder builder() {
		return new ClassPermissionsImplBuilder();
	}

	public static ClassPermissionsImplBuilder copyOf(ClassPermissions source) {
		return new ClassPermissionsImplBuilder().withPermissions(source.getPermissionsMap());
	}

	public static class ClassPermissionsImplBuilder implements Builder<ClassPermissionsImpl, ClassPermissionsImplBuilder> {

		private Map<PermissionScope, Set<ClassPermission>> permissions;

		public ClassPermissionsImplBuilder withPermissions(Map<PermissionScope, Set<ClassPermission>> permissions) {
			this.permissions = permissions;
			return this;
		}

		@Override
		public ClassPermissionsImpl build() {
			return new ClassPermissionsImpl(this);
		}

		public ClassPermissionsImplBuilder addPermissions(ClassPermissions toAdd) {
			this.permissions = mergeClassPermissions(permissions, toAdd.getPermissionsMap());
			return this;
		}

		public ClassPermissionsImplBuilder addPermissions(PermissionScope scope, Set<ClassPermission> toAdd) {
			this.permissions = mergeClassPermissions(permissions, map(none().getPermissionsMap()).with(scope, toAdd));
			return this;
		}

		public ClassPermissionsImplBuilder removePermissions(PermissionScope scope, Set<ClassPermission> toRemove) {
			this.permissions = removeClassPermissions(permissions, singletonMap(scope, toRemove));
			return this;
		}

		public ClassPermissionsImplBuilder intersectPermissions(PermissionScope scope, Set<ClassPermission> toIntersect) {
			return this.intersectPermissions(singletonMap(scope, toIntersect));
		}

		public ClassPermissionsImplBuilder intersectPermissions(ClassPermissions toIntersect) {
			return this.intersectPermissions(toIntersect.getPermissionsMap());
		}

		public ClassPermissionsImplBuilder intersectPermissions(Map<PermissionScope, Set<ClassPermission>> toIntersect) {
			this.permissions = intersectClassPermissions(permissions, toIntersect);
			return this;
		}

	}
}
