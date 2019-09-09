/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.login.cas;

public interface CasAuthenticatorConfiguration {

	String getCasServerUrl();

	String getCasLoginPage();

	String getCasTicketParam();

	String getCasServiceParam();

}
