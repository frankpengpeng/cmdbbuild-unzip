/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.entrytype;

import java.util.Map;
import java.util.Set;

public interface EntryTypeMetadata extends AbstractMetadata {

	static final String ENTRY_TYPE_MODE = "cm_mode";
	static final String PERMISSIONS = "cm_permissions";

	ClassPermissionMode getMode();

	Map<PermissionScope, Set<ClassPermission>> getPermissions();

}
