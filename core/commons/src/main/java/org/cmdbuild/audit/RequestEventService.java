/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.audit;

import com.google.common.eventbus.EventBus;

public interface RequestEventService {

	EventBus getEventBus();

	interface RequestBeginEvent {

	}

	interface RequestCompleteEvent {
	}
}
