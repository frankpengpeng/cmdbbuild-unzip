/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.model;

import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.workflow.inner.PlanUpdatedEvent;

public class SimplePlanUpdatedEvent implements PlanUpdatedEvent {

	final String classId;
	private final String planId;

	private SimplePlanUpdatedEvent(SimplePlanUpdatedEventBuilder builder) {
		this.classId = checkNotBlank(builder.classId);
		this.planId = checkNotBlank(builder.planId);
	}

	@Override
	public String getClassId() {
		return classId;
	}

	@Override
	public String getPlanId() {
		return planId;
	}

	@Override
	public String toString() {
		return "SimplePlanUpdatedEvent{" + "classId=" + classId + ", planId=" + planId + '}';
	}

	public static SimplePlanUpdatedEventBuilder builder() {
		return new SimplePlanUpdatedEventBuilder();
	}

	public static SimplePlanUpdatedEventBuilder copyOf(PlanUpdatedEvent source) {
		return new SimplePlanUpdatedEventBuilder()
				.withClassId(source.getClassId())
				.withPlanId(source.getPlanId());
	}

	public static class SimplePlanUpdatedEventBuilder implements Builder<SimplePlanUpdatedEvent, SimplePlanUpdatedEventBuilder> {

		private String classId;
		private String planId;

		public SimplePlanUpdatedEventBuilder withClassId(String classId) {
			this.classId = classId;
			return this;
		}

		public SimplePlanUpdatedEventBuilder withPlanId(String planId) {
			this.planId = planId;
			return this;
		}

		@Override
		public SimplePlanUpdatedEvent build() {
			return new SimplePlanUpdatedEvent(this);
		}

	}
}
