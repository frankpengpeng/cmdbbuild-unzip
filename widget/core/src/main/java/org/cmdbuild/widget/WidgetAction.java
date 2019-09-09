/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.widget;

import java.util.Map;
import org.cmdbuild.widget.model.Widget;

public interface WidgetAction {

	String getType();

	String getActionId();

	Map<String, Object> executeAction(Widget widget);

}
