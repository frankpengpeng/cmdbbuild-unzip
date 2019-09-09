/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.jobs;

import static java.lang.String.format;

public interface JobSessionService {

	void createJobSessionContext(String user, String reqCtxId);

	void destroyJobSessionContext();

	default void createJobSessionContext(String user, String reqCtxId, Object... params) {
		JobSessionService.this.createJobSessionContext(user, format(reqCtxId, params));
	}
}
