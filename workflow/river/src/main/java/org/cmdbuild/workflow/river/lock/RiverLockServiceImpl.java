/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.lock;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.lock.LockService;
import org.cmdbuild.lock.LockScope;
import org.cmdbuild.workflow.river.engine.lock.AquiredLock;
import org.cmdbuild.workflow.river.engine.lock.LockResponse;
import org.cmdbuild.workflow.river.engine.lock.RiverLockService;
import static org.cmdbuild.workflow.river.engine.lock.AquiredLockImpl.aquiredLock;
import static org.cmdbuild.workflow.river.engine.lock.NotAquiredLockResponseImpl.notAquired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RiverLockServiceImpl implements RiverLockService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final LockService lockService;

	public RiverLockServiceImpl(LockService lockService) {
		this.lockService = checkNotNull(lockService);
	}

	@Override
	public LockResponse aquireLock(String flowId) {
		LockService.LockResponse response = lockService.aquireLock("flow_" + flowId, LockScope.LS_REQUEST);
		if (response.isAquired()) {
			return aquiredLock(flowId, response.aquired().getItemId(), this);
		} else {
			return notAquired();
		}
	}

	@Override
	public void releaseLock(AquiredLock lock) {
		lockService.releaseLock(lock.getLockId());
	}

}
