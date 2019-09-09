/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.session.inner;

import org.cmdbuild.auth.session.model.SessionData;

public interface SessionDataService {

	/**
	 * return a session data instance useful to store data in current session;
	 * if there is not a valid session, the returned object will discard any
	 * write operation, and always return null on read
	 *
	 * @return current session data
	 */
	SessionData getCurrentSessionDataSafe();
}
