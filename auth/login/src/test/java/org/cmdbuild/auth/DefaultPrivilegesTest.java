package org.cmdbuild.auth;

public class DefaultPrivilegesTest {

//	@Test
//	public void simplePrivilegesAreIndependent() {
//		assertFalse(new SimplePrivilege().implies(new SimplePrivilege()));
//	}
//
//	@Test
//	public void readOrWritePrivilegeImpliesItself() {
//		assertTrue(AuthorizationConst.WRITE.implies(AuthorizationConst.WRITE));
//		assertTrue(AuthorizationConst.READ.implies(AuthorizationConst.READ));
//	}
//
//	@Test
//	public void writeImpliesReadPrivilege() {
//		assertTrue(AuthorizationConst.WRITE.implies(AuthorizationConst.READ));
//		assertTrue(AuthorizationConst.WRITE.implies(AuthorizationConst.WRITE));
//		assertFalse(AuthorizationConst.READ.implies(AuthorizationConst.WRITE));
//	}
//
//	@Test
//	public void godImpliesEveryPrivilege() {
//		assertTrue(AuthorizationConst.GOD.implies(AuthorizationConst.READ));
//		assertTrue(AuthorizationConst.GOD.implies(AuthorizationConst.WRITE));
//		assertTrue(AuthorizationConst.GOD.implies(new SimplePrivilege()));
//	}
//
//	@Test
//	public void databaseDesignerIsIndependent() {
//		assertTrue(AuthorizationConst.DATABASE_DESIGNER.implies(AuthorizationConst.DATABASE_DESIGNER));
//	}
//
//	@Test
//	public void administratorIsIndependent() {
//		assertTrue(AuthorizationConst.ADMINISTRATOR.implies(AuthorizationConst.ADMINISTRATOR));
//		assertFalse(AuthorizationConst.ADMINISTRATOR.implies(AuthorizationConst.DATABASE_DESIGNER));
//		assertFalse(AuthorizationConst.ADMINISTRATOR.implies(AuthorizationConst.READ));
//		assertFalse(AuthorizationConst.ADMINISTRATOR.implies(AuthorizationConst.WRITE));
//	}
}
