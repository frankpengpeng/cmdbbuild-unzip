/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.quick.scanner;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.utils.quick.QuickItemLifecycleEvent;

public class QuickLifecycleTriggerImpl implements QuickLifecycleTrigger {

    private final String methodName;
    private final QuickItemLifecycleEvent event;

    public QuickLifecycleTriggerImpl(String methodName, QuickItemLifecycleEvent event) {
        this.methodName = checkNotBlank(methodName);
        this.event = checkNotNull(event);
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public QuickItemLifecycleEvent getEvent() {
        return event;
    }

}
