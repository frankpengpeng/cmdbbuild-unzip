/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.contextmenu;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Ordering;
import java.util.List;
import static java.util.stream.Collectors.toList;
import java.util.stream.IntStream;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.dao.entrytype.Classe;
import org.springframework.stereotype.Component;
import org.cmdbuild.cache.CmCache;

@Component
public class ContextMenuServiceImpl implements ContextMenuService {

	private final ContextMenuRepository contextMenuRepository;
	private final CmCache<List<ContextMenuItem>> cache;

	public ContextMenuServiceImpl(ContextMenuRepository contextMenuRepository, CacheService cacheService) {
		this.contextMenuRepository = checkNotNull(contextMenuRepository);
		cache = cacheService.newCache("context_menu_items_by_class_oid");
	}

	@Override
	public List<ContextMenuItem> getContextMenuItems(Classe classe) {
		return cache.get(classe.getName(), () -> contextMenuRepository.getContextMenuItems(classe.getName()).stream()
				.sorted(Ordering.natural().onResultOf(ContextMenuItemData::getIndex))
				.map(this::toContextMenuItem)
				.collect(toList()));
	}

	@Override
	public void updateContextMenuItems(Classe classe, List<ContextMenuItem> items) {
		contextMenuRepository.updateContextMenuItems(classe.getName(), IntStream.range(0, items.size()).mapToObj((i) -> {
			ContextMenuItem item = items.get(i);
			return ContextMenuItemDataImpl.builder()
					.withIndex(i)
					.withClassId(classe.getName())
					.withLabel(item.getLabel())
					.withJsScript(item.getJsScript())
					.withActive(item.isActive())
					.withComponentId(item.getComponentId())
					.withConfig(item.getConfig())
					.withType(item.getType().name())
					.withVisibility(item.getVisibility().name())
					.build();
		}).collect(toList()));
		cache.invalidate(classe.getName());
	}

	private ContextMenuItem toContextMenuItem(ContextMenuItemData data) {
		return ContextMenuItemImpl.builder()
				.withActive(data.isActive())
				.withComponentId(data.getComponentId())
				.withConfig(data.getConfig())
				.withJsScript(data.getJsScript())
				.withLabel(data.getLabel())
				.withType(ContextMenuType.valueOf(data.getType()))
				.withVisibility(ContextMenuVisibility.valueOf(data.getVisibility()))
				.build();
	}

	@Override
	public void deleteForClass(Classe classe) {
		contextMenuRepository.deleteForClass(classe.getName());
		cache.invalidate(classe.getName());
	}

}
