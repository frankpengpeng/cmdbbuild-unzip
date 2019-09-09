/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.contextmenu;

import java.util.List;

public interface ContextMenuRepository {

	List<ContextMenuItemData> getContextMenuItems(String className);

	void updateContextMenuItems(String className, List<ContextMenuItemData> items);

	void deleteForClass(String className);
}
