/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.grant;

import com.google.common.eventbus.EventBus;
import org.springframework.stereotype.Component;

@Component
public class GrantEventBusServiceImpl implements GrantEventBusService {

    private final EventBus eventBus = new EventBus();

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }
}
