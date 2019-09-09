
package org.cmdbuild.auth.grant;

import com.google.common.eventbus.EventBus;

public interface GrantEventBusService {

    /**
     * send events of type {@link GrantDataUpdatedEvent}
     */
    EventBus getEventBus();
    
    enum GrantDataUpdatedEvent {
        INSTANCE
    }
}
