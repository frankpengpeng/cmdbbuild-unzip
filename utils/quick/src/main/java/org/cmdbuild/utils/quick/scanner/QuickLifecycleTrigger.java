/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.quick.scanner;

import static com.google.common.base.Objects.equal;
import org.cmdbuild.utils.quick.QuickItemLifecycleEvent;

public interface QuickLifecycleTrigger {

    String getMethodName();

    QuickItemLifecycleEvent getEvent();

    default boolean matchesEvent(QuickItemLifecycleEvent event) {
        return equal(getEvent(), event);
    }
}
