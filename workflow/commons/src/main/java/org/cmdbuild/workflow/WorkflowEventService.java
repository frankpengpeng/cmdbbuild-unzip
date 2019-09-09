/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cmdbuild.workflow;

import com.google.common.eventbus.EventBus;

public interface WorkflowEventService {
	
	/**
	* register to this event bus to receive {@link FlowUpdatedEvent} events
	*/
	EventBus getEventBus();

}
