package org.cmdbuild.menu;

import java.util.List;
import javax.annotation.Nullable;

public interface MenuTreeNode {

	String getCode();

	MenuItemType getType();

	String getDescription();

	@Nullable
	String getTarget();

	List<MenuTreeNode> getChildren();

}
