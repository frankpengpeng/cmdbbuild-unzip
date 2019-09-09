/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.contextmenu;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@Component
public class ContextMenuRepositoryImpl implements ContextMenuRepository {

	private final DaoService dao;

	public ContextMenuRepositoryImpl(DaoService dao) {
		this.dao = checkNotNull(dao);
	}

	@Override
	public List<ContextMenuItemData> getContextMenuItems(String className) {
		return dao.selectAll().from(ContextMenuItemDataImpl.class).where("Owner", EQ, checkNotBlank(className)).asList();
	}

	@Override
	public void updateContextMenuItems(String className, List<ContextMenuItemData> items) { //TODO currently implemented as delete-then-create, refactor as update
		deleteForClass(className);
		items.forEach(dao::create);
	}

	@Override
	public void deleteForClass(String className) {
		getContextMenuItems(className).forEach(dao::delete);
	}

}
