package org.cmdbuild.menu;

import static com.google.common.base.Objects.equal;
import java.util.List;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.cmdbuild.menu.MenuItemType.ROOT;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class MenuTreeNodeImpl implements MenuTreeNode {

	private final MenuItemType type;
	private final String description, target, code;
	private final List<MenuTreeNode> children;

	private MenuTreeNodeImpl(MenuTreeNodeImplBuilder builder) {
		this.type = checkNotNull(builder.type);
		this.description = checkNotBlank(builder.description);
		this.target = trimToNull(builder.target);
		if (equal(type, ROOT)) {
			this.code = "ROOT";
		} else {
			this.code = checkNotBlank(builder.code);
		}
		this.children = ImmutableList.copyOf(checkNotNull(builder.children));
	}

	@Override
	public MenuItemType getType() {
		return type;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getTarget() {
		return target;
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public List<MenuTreeNode> getChildren() {
		return children;
	}

	@Override
	public String toString() {
		return "MenuTreeNodeImpl{" + ", type=" + type + ", description=" + description + ", objectType=" + target + ", code=" + code + '}';
	}

	public static MenuTreeNodeImplBuilder builder() {
		return new MenuTreeNodeImplBuilder();
	}

	public static MenuTreeNodeImpl buildRoot(List<MenuTreeNode> children) {
		return new MenuTreeNodeImplBuilder()
				.withType(ROOT)
				.withDescription("ROOT")
				.withChildren(children)
				.build();
	}

	public static MenuTreeNodeImplBuilder copyOf(MenuTreeNode source) {
		return new MenuTreeNodeImplBuilder()
				.withType(source.getType())
				.withDescription(source.getDescription())
				.withTarget(source.getTarget())
				.withCode(source.getCode())
				.withChildren(source.getChildren());
	}

	public static class MenuTreeNodeImplBuilder implements Builder<MenuTreeNodeImpl, MenuTreeNodeImplBuilder> {

		private MenuItemType type;
		private String description;
		private String target;
		private String code;
		private List<MenuTreeNode> children = list();

		public MenuTreeNodeImplBuilder withType(MenuItemType type) {
			this.type = type;
			return this;
		}

		public MenuTreeNodeImplBuilder withDescription(String description) {
			this.description = description;
			return this;
		}

		public MenuTreeNodeImplBuilder withTarget(String objectType) {
			this.target = objectType;
			return this;
		}

		public MenuTreeNodeImplBuilder withCode(String uniqueIdentifier) {
			this.code = uniqueIdentifier;
			return this;
		}

		public MenuTreeNodeImplBuilder withChildren(List<MenuTreeNode> children) {
			this.children = children;
			return this;
		}

		public MenuTreeNodeImplBuilder addChild(MenuTreeNode child) {
			children.add(child);
			return this;
		}

		@Override
		public MenuTreeNodeImpl build() {
			return new MenuTreeNodeImpl(this);
		}

	}
}
