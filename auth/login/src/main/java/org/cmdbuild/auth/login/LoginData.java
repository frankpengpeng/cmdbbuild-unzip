/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.login;

import org.cmdbuild.auth.multitenant.api.TenantLoginData;

public interface LoginData extends TenantLoginData {

	String getLoginString();

	String getPassword();

	String getLoginGroupName();

	boolean isPasswordRequired();

	boolean isServiceUsersAllowed();
}
