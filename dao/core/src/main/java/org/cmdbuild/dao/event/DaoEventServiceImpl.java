/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.event;

import com.google.common.eventbus.EventBus;
import org.cmdbuild.dao.driver.repository.DaoEventService;
import org.springframework.stereotype.Component;

@Component
public class DaoEventServiceImpl implements DaoEventService {

	private final EventBus eventBus = new EventBus();

	@Override
	public EventBus getEventBus() {
		return eventBus;
	}

	@Override
	public void post(DaoEvent event) {
		eventBus.post(event);
	}
}
