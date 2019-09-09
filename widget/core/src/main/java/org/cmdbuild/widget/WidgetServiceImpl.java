package org.cmdbuild.widget;

import org.cmdbuild.widget.model.Widget;
import org.cmdbuild.widget.model.WidgetData;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.Map;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.widget.dao.WidgetRepository;
import org.springframework.stereotype.Component;
import org.cmdbuild.cache.CmCache;

@Component
public class WidgetServiceImpl implements WidgetService {

	private final WidgetRepository repository;
	private final WidgetFactoryService factoryService;
	private final WidgetActionService actionService;
	private final CmCache<List<WidgetData>> widgetDataCache;

	public WidgetServiceImpl(WidgetRepository repository, WidgetFactoryService factoryService, WidgetActionService actionService, CacheService cacheService) {
		this.repository = checkNotNull(repository);
		this.factoryService = checkNotNull(factoryService);
		this.actionService = checkNotNull(actionService);
		widgetDataCache = cacheService.newCache("widget_data_by_class");
	}

	@Override
	public List<WidgetData> getAllWidgetsForClass(Classe classe) {
		return widgetDataCache.get(classe.getName(), () -> repository.getAllWidgetsForClass(classe.getName()));
	}

	@Override
	public Widget widgetDataToWidget(WidgetData data, Map<String, Object> context) {
		return factoryService.createWidget(data, context);
	}

	@Override
	public boolean hasWidgetAction(String widgetType, String actionId) {
		return actionService.hasWidgetAction(widgetType, actionId);
	}

	@Override
	public Map<String, Object> executeWidgetAction(Widget widget, String actionId) {
		return actionService.executeWidgetAction(widget, actionId);
	}

	@Override
	public void updateWidgetsForClass(Classe classe, List<WidgetData> widgets) {
		repository.updateForClass(classe.getName(), widgets);
		widgetDataCache.invalidate(classe.getName());
	}

	@Override
	public void deleteForClass(Classe classe) {
		repository.deleteForClass(classe.getName());
		widgetDataCache.invalidate(classe.getName());
	}

}
