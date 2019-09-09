/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.shark;

import static com.google.common.base.Preconditions.checkNotNull;
import org.enhydra.shark.api.client.wfmc.wapi.WMSessionHandle;

public class SharkTransactionContext {

	private final WMSessionHandle handle;
	private final SharkEventCollector eventCollector;

	public SharkTransactionContext(WMSessionHandle handle, SharkEventCollector eventCollector) {
		this.handle = checkNotNull(handle);
		this.eventCollector = checkNotNull(eventCollector);
	}

	public WMSessionHandle getHandle() {
		return handle;
	}

	public SharkEventCollector getEventCollector() {
		return eventCollector;
	}

}
