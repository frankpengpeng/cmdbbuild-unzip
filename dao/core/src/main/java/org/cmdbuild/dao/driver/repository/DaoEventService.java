/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.driver.repository;

import com.google.common.eventbus.EventBus;
import org.cmdbuild.dao.event.DaoEvent;

public interface DaoEventService {

	EventBus getEventBus();

	void post(DaoEvent event);
}
