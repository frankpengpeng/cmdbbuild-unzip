/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.quick.test.cp1;

import static junit.framework.Assert.assertFalse;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MyComponentWithStartupEvent {

    public boolean receivedContextRefreshEvent = false;

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        assertFalse(receivedContextRefreshEvent);
        receivedContextRefreshEvent = true;
    }
}
