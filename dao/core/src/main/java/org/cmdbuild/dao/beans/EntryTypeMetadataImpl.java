/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.beans;

import static com.google.common.base.Predicates.not;
import com.google.common.collect.ImmutableSet;
import static com.google.common.collect.Maps.filterKeys;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.cmdbuild.dao.entrytype.ClassPermission;
import org.cmdbuild.dao.entrytype.ClassPermissionMode;
import org.cmdbuild.dao.entrytype.ClassPermissions;
import org.cmdbuild.dao.entrytype.ClassPermissionsImpl;
import org.cmdbuild.dao.entrytype.EntryTypeMetadata;
import static org.cmdbuild.dao.entrytype.EntryTypeMetadata.ENTRY_TYPE_MODE;
import org.cmdbuild.dao.entrytype.PermissionScope;
import static org.cmdbuild.dao.entrytype.DaoPermissionUtils.getDefaultPermissions;
import static org.cmdbuild.dao.entrytype.DaoPermissionUtils.parseClassPermissions;
import static org.cmdbuild.dao.entrytype.ClassPermissionMode.CPM_ALL;
import org.cmdbuild.dao.entrytype.DaoPermissionUtils;

public abstract class EntryTypeMetadataImpl extends AbstractMetadataImpl implements EntryTypeMetadata {

	private static final Set<String> ENTRY_TYPE_METADATA_KEYS = ImmutableSet.of(ENTRY_TYPE_MODE, PERMISSIONS);

	private final ClassPermissionMode mode;
	private final ClassPermissions permissions;

	protected EntryTypeMetadataImpl(Map<String, String> allAttrs, Map<String, String> customAttrs) {
		super(allAttrs, filterKeys(customAttrs, not(ENTRY_TYPE_METADATA_KEYS::contains)));
		mode = Optional.ofNullable(trimToNull(allAttrs.get(ENTRY_TYPE_MODE))).map(DaoPermissionUtils::parseClassPermissionMode).orElse(CPM_ALL);
		if (isNotBlank(allAttrs.get(PERMISSIONS))) {
			permissions = ClassPermissionsImpl
					.copyOf(getDefaultPermissions(mode))
					.addPermissions(parseClassPermissions(allAttrs.get(PERMISSIONS)))
					.build();
		} else {
			permissions = getDefaultPermissions(mode);
		}

	}

	@Override
	public ClassPermissionMode getMode() {
		return mode;
	}

	@Override
	public Map<PermissionScope, Set<ClassPermission>> getPermissions() {
		return permissions.getPermissionsMap();
	}

}
