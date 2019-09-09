/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.services;

import static com.google.common.base.Objects.equal;
import static org.cmdbuild.services.MinionStatus.MS_ERROR;
import static org.cmdbuild.services.MinionStatus.MS_NOTRUNNING;
import static org.cmdbuild.services.MinionStatus.MS_READY;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;

public interface Minion {

	default String getId() {
		return getName().toLowerCase().replaceAll("[^a-z]", "");
	}

	String getName();

	MinionStatus getStatus();

	boolean isEnabled();

	void startService();

	void stopService();

	default boolean canStart() {
		return set(MS_ERROR, MS_NOTRUNNING).contains(getStatus());
	}

	default boolean canStop() {
		return equal(MS_READY, getStatus());
	}
}
