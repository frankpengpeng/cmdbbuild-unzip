/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.test;

import java.util.Map;
import java.util.Set;
import org.cmdbuild.dao.entrytype.AttributePermission;
import static org.cmdbuild.dao.entrytype.AttributePermission.AP_CREATE;
import static org.cmdbuild.dao.entrytype.AttributePermission.AP_LIST;
import static org.cmdbuild.dao.entrytype.AttributePermission.AP_MODIFY;
import static org.cmdbuild.dao.entrytype.AttributePermission.AP_READ;
import org.cmdbuild.dao.entrytype.ClassPermission;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_READ;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_WRITE;
import static org.cmdbuild.dao.entrytype.DaoPermissionUtils.intersectAttributePermissions;
import static org.cmdbuild.dao.entrytype.DaoPermissionUtils.intersectClassPermissions;
import static org.cmdbuild.dao.entrytype.DaoPermissionUtils.parseAttributePermissions;
import static org.cmdbuild.dao.entrytype.DaoPermissionUtils.parseClassPermissions;
import org.cmdbuild.dao.entrytype.PermissionScope;
import static org.cmdbuild.dao.entrytype.PermissionScope.PS_SERVICE;
import static org.cmdbuild.dao.entrytype.PermissionScope.PS_UI;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class PermissionProcessingTest {

	@Test
	public void testPermissionIntersection() {
		Map<PermissionScope, Set<ClassPermission>> source = parseClassPermissions("A|A|A").getPermissionsMap();
		Map<PermissionScope, Set<ClassPermission>> toIntersect = parseClassPermissions("A|A|A").getPermissionsMap();

		Map<PermissionScope, Set<ClassPermission>> res = intersectClassPermissions(source, toIntersect);
		assertEquals(res.keySet(), source.keySet());
		res.keySet().forEach((k) -> assertEquals(source.get(k), res.get(k)));
	}

	@Test
	public void testPermissionIntersection2() {
		Map<PermissionScope, Set<ClassPermission>> source = parseClassPermissions("A|A|A").getPermissionsMap();
		Map<PermissionScope, Set<ClassPermission>> toIntersect = map(PS_SERVICE, set(CP_WRITE));

		Map<PermissionScope, Set<ClassPermission>> res = intersectClassPermissions(source, toIntersect);
		assertEquals(res.keySet(), source.keySet());
		assertTrue(res.get(PS_UI).contains(CP_WRITE));
		assertTrue(res.get(PS_UI).contains(CP_READ));
	}

	@Test
	public void testPermissionIntersection3() {
		Map<PermissionScope, Set<ClassPermission>> source = parseClassPermissions("A|A|A").getPermissionsMap();
		Map<PermissionScope, Set<ClassPermission>> toIntersect = map(PS_SERVICE, set(CP_READ));

		Map<PermissionScope, Set<ClassPermission>> res = intersectClassPermissions(source, toIntersect);
		assertEquals(res.keySet(), source.keySet());
		assertFalse(res.get(PS_UI).contains(CP_WRITE));
		assertTrue(res.get(PS_UI).contains(CP_READ));
	}

	@Test
	public void testPermissionIntersection4() {
		Map<PermissionScope, Set<AttributePermission>> source = parseAttributePermissions("A|A|A").getPermissionMap();

		Map<PermissionScope, Set<AttributePermission>> res = intersectAttributePermissions(source, set(AP_LIST, AP_MODIFY), map(PS_SERVICE, set(AP_LIST)));

		assertEquals(res.keySet(), source.keySet());
		assertFalse(res.get(PS_SERVICE).contains(AP_MODIFY));
		assertTrue(res.get(PS_SERVICE).contains(AP_LIST));
		assertTrue(res.get(PS_SERVICE).contains(AP_READ));
		assertTrue(res.get(PS_SERVICE).contains(AP_CREATE));
	}

}
