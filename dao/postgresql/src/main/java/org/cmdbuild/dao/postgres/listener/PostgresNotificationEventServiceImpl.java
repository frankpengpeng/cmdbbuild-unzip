/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.listener;

import com.google.common.eventbus.EventBus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class PostgresNotificationEventServiceImpl implements PostgresNotificationEventService {

	private final EventBus eventBus = new EventBus();

	@Override
	public EventBus getEventBus() {
		return eventBus;
	}

}
