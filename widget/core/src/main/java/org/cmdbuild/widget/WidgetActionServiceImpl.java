/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.widget;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;
import org.cmdbuild.widget.model.Widget;
import org.springframework.stereotype.Component;

@Component
public class WidgetActionServiceImpl implements WidgetActionService {

	private final Map<String, WidgetAction> widgetActionsByWidgetTypeActionId;

	public WidgetActionServiceImpl(List<WidgetAction> actions) {
		checkNotNull(actions);
		widgetActionsByWidgetTypeActionId = Maps.uniqueIndex(actions, (a) -> key(a.getType(), a.getActionId()));
	}

	@Override
	public Map<String, Object> executeWidgetAction(Widget widget, String actionId) {
		WidgetAction action = checkNotNull(widgetActionsByWidgetTypeActionId.get(key(widget.getType(), actionId)), "action not found for widget = %s actionId = %s", widget, actionId);
		Map<String, Object> res = action.executeAction(widget);
		return res;
	}

	@Override
	public boolean hasWidgetAction(String widgetType, String actionId) {
		return widgetActionsByWidgetTypeActionId.containsKey(key(widgetType, actionId));
	}

}
