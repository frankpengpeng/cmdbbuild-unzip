/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.dao.repos;

import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.cmdbuild.utils.lang.Builder;

@CardMapping("_Plan")
public class PlanDataImpl implements PlanData {

	public final static String ATTR_DATA = "Data", ATTR_CLASSE_ID = "ClassId";

	private final String planId, data, classId;

	private PlanDataImpl(SimplePlanDataBuilder builder) {
		this.planId = checkNotBlank(builder.planId);
		this.data = checkNotBlank(builder.data);
		this.classId = checkNotBlank(builder.classId);
	}

	@CardAttr(ATTR_CODE)
	@Override
	public String getPlanId() {
		return planId;
	}

	@CardAttr(ATTR_DATA)
	@Override
	public String getData() {
		return data;
	}

	@CardAttr(ATTR_CLASSE_ID)
	@Override
	public String getClasseId() {
		return classId;
	}

	public static SimplePlanDataBuilder builder() {
		return new SimplePlanDataBuilder();
	}

	public static SimplePlanDataBuilder copyOf(PlanDataImpl source) {
		return new SimplePlanDataBuilder()
				.withPlanId(source.getPlanId())
				.withData(source.getData())
				.withClasseId(source.getClasseId());
	}

	public static class SimplePlanDataBuilder implements Builder<PlanDataImpl, SimplePlanDataBuilder> {

		private String planId;
		private String data;
		private String classId;

		public SimplePlanDataBuilder withPlanId(String planId) {
			this.planId = planId;
			return this;
		}

		public SimplePlanDataBuilder withData(String data) {
			this.data = data;
			return this;
		}

		public SimplePlanDataBuilder withClasseId(String classId) {
			this.classId = classId;
			return this;
		}

		@Override
		public PlanDataImpl build() {
			return new PlanDataImpl(this);
		}

	}
}
