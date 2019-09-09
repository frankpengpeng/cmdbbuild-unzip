/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.role;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import static com.google.common.base.MoreObjects.firstNonNull;
import javax.annotation.Nullable;
import org.cmdbuild.auth.role.GroupConfigImpl.GroupConfigImplBuilder;
import org.cmdbuild.utils.lang.Builder;

@JsonDeserialize(builder = GroupConfigImplBuilder.class)
public class GroupConfigImpl implements GroupConfig {

	private final boolean processWidgetAlwaysEnabled;
	private final String startingClass;

	private GroupConfigImpl(GroupConfigImplBuilder builder) {
		this.processWidgetAlwaysEnabled = firstNonNull(builder.processWidgetAlwaysEnabled, false);
		this.startingClass = builder.startingClass;
	}

	@Override
	public boolean getProcessWidgetAlwaysEnabled() {
		return processWidgetAlwaysEnabled;
	}

	@Override
	public @Nullable
	String getStartingClass() {
		return startingClass;
	}

	public static GroupConfigImplBuilder builder() {
		return new GroupConfigImplBuilder();
	}

	public static GroupConfigImplBuilder copyOf(GroupConfig source) {
		return new GroupConfigImplBuilder()
				.withProcessWidgetAlwaysEnabled(source.getProcessWidgetAlwaysEnabled())
				.withStartingClass(source.getStartingClass());
	}

	public static class GroupConfigImplBuilder implements Builder<GroupConfigImpl, GroupConfigImplBuilder> {

		private Boolean processWidgetAlwaysEnabled;
		private String startingClass;

		public GroupConfigImplBuilder withProcessWidgetAlwaysEnabled(Boolean processWidgetAlwaysEnabled) {
			this.processWidgetAlwaysEnabled = processWidgetAlwaysEnabled;
			return this;
		}

		public GroupConfigImplBuilder withStartingClass(String startingClass) {
			this.startingClass = startingClass;
			return this;
		}

		@Override
		public GroupConfigImpl build() {
			return new GroupConfigImpl(this);
		}

	}
}
