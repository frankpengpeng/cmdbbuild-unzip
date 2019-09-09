/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.widget;

import java.util.Map;
import org.cmdbuild.widget.model.Widget;
import org.cmdbuild.widget.model.WidgetData;

public interface WidgetFactoryService {

	Widget createWidget(WidgetData data, Map<String, Object> context);
}
