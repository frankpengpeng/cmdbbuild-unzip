/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.report;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Strings.nullToEmpty;
import javax.annotation.Nullable;

import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class ReportInfoImpl implements ReportInfo {

	private final Long id;
	private final String code, description;
	private final boolean isActive;

	private ReportInfoImpl(ReportInfoImplBuilder builder) {
		this.id = builder.id;
		this.code = checkNotBlank(builder.code, "report code cannot be null");
		this.description = nullToEmpty(builder.description);
		this.isActive = firstNonNull(builder.active, true);
	}

	@Override
	@Nullable
	public Long getId() {
		return id;
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public boolean isActive() {
		return isActive;
	}

	@Override
	public String toString() {
		return "ReportInfoImpl{" + "id=" + id + ", code=" + code + '}';
	}

	public static ReportInfoImplBuilder builder() {
		return new ReportInfoImplBuilder();
	}

	public static ReportInfoImplBuilder copyOf(ReportInfo source) {
		return new ReportInfoImplBuilder()
				.withId(source.getId())
				.withCode(source.getCode())
				.withDescription(source.getDescription())
				.withActive(source.isActive());
	}

	public static class ReportInfoImplBuilder implements Builder<ReportInfoImpl, ReportInfoImplBuilder> {

		private Long id;
		private String code;
		private String description;
		private Boolean active = true;

		public ReportInfoImplBuilder withId(Long id) {
			this.id = id;
			return this;
		}

		public ReportInfoImplBuilder withCode(String code) {
			this.code = code;
			return this;
		}

		public ReportInfoImplBuilder withDescription(String description) {
			this.description = description;
			return this;
		}

		public ReportInfoImplBuilder withActive(Boolean active) {
			this.active = active;
			return this;
		}

		@Override
		public ReportInfoImpl build() {
			return new ReportInfoImpl(this);
		}

	}
}
