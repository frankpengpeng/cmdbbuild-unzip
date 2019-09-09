/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.contextmenu;

import javax.annotation.Nullable;

public interface ContextMenuItem {

	String getLabel();

	ContextMenuType getType();

	@Nullable
	String getComponentId();

	@Nullable
	String getJsScript();

	@Nullable
	String getConfig();

	boolean isActive();

	ContextMenuVisibility getVisibility();

}
