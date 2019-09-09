/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.quick.scanner;

import java.util.List;
import org.cmdbuild.utils.quick.QuickEventListener;

public interface QuickItemConfig {

    String getItemId();

    String getClassName();

    int getConstructorIndex();

    List<QuickItemBindingConfig> getConstructorBindings();

    List<QuickLifecycleTrigger> getTriggers();

    List<QuickEventListener> getEventListeners();

}
