/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.login;

public class AuthorityConst {

	public static final String ADMIN_ACCESS_AUTHORITY = "ADMIN_ACCESS";
	public static final String SYSTEM_ACCESS_AUTHORITY = "SYSTEM_ACCESS";
//	public static final String REPORTS_MODIFY_AUTHORITY = "REPORTS_MODIFY";
//	public static final String REPORTS_VIEW_AUTHORITY = "REPORTS_VIEW";

	public static final String HAS_ADMIN_ACCESS_AUTHORITY = "hasAuthority('" + ADMIN_ACCESS_AUTHORITY + "')";
	public static final String HAS_SYSTEM_ACCESS_AUTHORITY = "hasAuthority('" + SYSTEM_ACCESS_AUTHORITY + "')";
//	public static final String HAS_REPORTS_MODIFY_AUTHORITY = "hasAuthority('" + REPORTS_MODIFY_AUTHORITY + "')";
//	public static final String HAS_REPORTS_VIEW_AUTHORITY = "hasAuthority('" + REPORTS_VIEW_AUTHORITY + "')";

}
