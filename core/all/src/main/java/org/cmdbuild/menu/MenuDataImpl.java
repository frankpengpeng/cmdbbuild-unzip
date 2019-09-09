/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.menu;

import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.springframework.context.annotation.Primary;

import static com.google.common.base.Preconditions.checkNotNull;
import javax.annotation.Nullable;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@Primary
@CardMapping("_Menu")
public class MenuDataImpl implements MenuData {

	private final Long id;
	private final String groupName;
	private final MenuJsonRootNode menuRootNode;

	private MenuDataImpl(MenuDataImplBuilder builder) {
		this.id = builder.id;
		this.groupName = checkNotBlank(builder.groupName);
		this.menuRootNode = checkNotNull(builder.menuRootNode);
	}

	@Override
	@Nullable
	@CardAttr(ATTR_ID)
	public Long getId() {
		return id;
	}

	@Override
	@CardAttr("GroupName")
	public String getGroupName() {
		return groupName;
	}

	@Override
	@CardAttr("Data")
	public MenuJsonRootNode getMenuRootNode() {
		return menuRootNode;
	}

	public static MenuDataImplBuilder builder() {
		return new MenuDataImplBuilder();
	}

	public static MenuDataImplBuilder copyOf(MenuData source) {
		return new MenuDataImplBuilder()
				.withId(source.getId())
				.withGroupName(source.getGroupName())
				.withMenuRootNode(source.getMenuRootNode());
	}

	public static class MenuDataImplBuilder implements Builder<MenuDataImpl, MenuDataImplBuilder> {

		private Long id;
		private String groupName;
		private MenuJsonRootNode menuRootNode;

		public MenuDataImplBuilder withId(Long id) {
			this.id = id;
			return this;
		}

		public MenuDataImplBuilder withGroupName(String groupName) {
			this.groupName = groupName;
			return this;
		}

		public MenuDataImplBuilder withMenuRootNode(MenuJsonRootNode menuRootNode) {
			this.menuRootNode = menuRootNode;
			return this;
		}

		@Override
		public MenuDataImpl build() {
			return new MenuDataImpl(this);
		}

	}
}
