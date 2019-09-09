/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine.lock;

import org.cmdbuild.workflow.river.engine.RiverFlow;

public interface RiverLockService {

	default LockResponse aquireLock(RiverFlow flow) {
		return aquireLock(flow.getId());
	}

	LockResponse aquireLock(String walkId);

	void releaseLock(AquiredLock lock);

}
