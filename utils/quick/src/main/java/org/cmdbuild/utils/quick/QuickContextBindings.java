/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.quick;

import java.util.List;
import org.cmdbuild.utils.quick.scanner.QuickItemConfig;

public interface QuickContextBindings {

    List<QuickItemConfig> getItemConfigsInLoadingOrder();

}
