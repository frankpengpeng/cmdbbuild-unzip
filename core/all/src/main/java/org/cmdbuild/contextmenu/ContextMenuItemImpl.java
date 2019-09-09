/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.contextmenu;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class ContextMenuItemImpl implements ContextMenuItem {

	private final String label, jsScript, componentId, config;
	private final ContextMenuType type;
	private final boolean active;
	private final ContextMenuVisibility visibility;

	private ContextMenuItemImpl(ContextMenuItemImplBuilder builder) {
		this.type = checkNotNull(builder.type);
		this.active = checkNotNull(builder.active);
		switch (type) {
			case COMPONENT:
			case CUSTOM:
				this.label = checkNotBlank(builder.label);
				this.visibility = checkNotNull(builder.visibility);
				break;
			case SEPARATOR:
				this.label = "";
				this.visibility = ContextMenuVisibility.ALL;
				break;
			default:
				throw new UnsupportedOperationException("unsupported type = " + type);
		}
		switch (type) {
			case COMPONENT:
				componentId = checkNotBlank(builder.componentId, "component id is required for component-based menu itel");
				config = nullToEmpty(builder.config);
				jsScript = null;
				break;
			case CUSTOM:
				componentId = null;
				config = null;
				jsScript = nullToEmpty(builder.jsScript);
				break;
			case SEPARATOR:
				componentId = null;
				config = null;
				jsScript = null;
				break;
			default:
				throw new UnsupportedOperationException("unsupported type = " + type);
		}
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public String getJsScript() {
		return jsScript;
	}

	@Override
	public String getComponentId() {
		return componentId;
	}

	@Override
	public String getConfig() {
		return config;
	}

	@Override
	public ContextMenuType getType() {
		return type;
	}

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public ContextMenuVisibility getVisibility() {
		return visibility;
	}

	public static ContextMenuItemImplBuilder builder() {
		return new ContextMenuItemImplBuilder();
	}

	public static ContextMenuItemImplBuilder copyOf(ContextMenuItem source) {
		return new ContextMenuItemImplBuilder()
				.withLabel(source.getLabel())
				.withJsScript(source.getJsScript())
				.withComponentId(source.getComponentId())
				.withConfig(source.getConfig())
				.withType(source.getType())
				.withActive(source.isActive())
				.withVisibility(source.getVisibility());
	}

	public static class ContextMenuItemImplBuilder implements Builder<ContextMenuItemImpl, ContextMenuItemImplBuilder> {

		private String label;
		private String jsScript;
		private String componentId;
		private String config;
		private ContextMenuType type;
		private boolean active;
		private ContextMenuVisibility visibility;

		public ContextMenuItemImplBuilder withLabel(String label) {
			this.label = label;
			return this;
		}

		public ContextMenuItemImplBuilder withJsScript(String jsScript) {
			this.jsScript = jsScript;
			return this;
		}

		public ContextMenuItemImplBuilder withComponentId(String componentId) {
			this.componentId = componentId;
			return this;
		}

		public ContextMenuItemImplBuilder withConfig(String config) {
			this.config = config;
			return this;
		}

		public ContextMenuItemImplBuilder withType(ContextMenuType type) {
			this.type = type;
			return this;
		}

		public ContextMenuItemImplBuilder withActive(boolean active) {
			this.active = active;
			return this;
		}

		public ContextMenuItemImplBuilder withVisibility(ContextMenuVisibility visibility) {
			this.visibility = visibility;
			return this;
		}

		@Override
		public ContextMenuItemImpl build() {
			return new ContextMenuItemImpl(this);
		}

	}
}
