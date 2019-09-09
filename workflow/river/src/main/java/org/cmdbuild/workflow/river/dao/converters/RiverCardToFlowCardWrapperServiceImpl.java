/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.dao.converters;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.workflow.core.model.FlowImpl;
import org.cmdbuild.workflow.core.LookupHelper;
import static org.cmdbuild.workflow.WorkflowCommonConst.RIVER;
import org.cmdbuild.workflow.core.dao.data.CardToFlowCardWrapperServiceDelegate;
import org.cmdbuild.workflow.model.PlanInfo;
import org.cmdbuild.workflow.model.PlanInfoImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.beans.Card;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_PLAN_INFO;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.model.Process;
import org.cmdbuild.workflow.inner.ProcessRepository;
import static org.cmdbuild.workflow.river.WfRiverUtils.RIVER_FAKE_PACKAGE;

@Component
@Qualifier(RIVER)
public class RiverCardToFlowCardWrapperServiceImpl implements CardToFlowCardWrapperServiceDelegate {

	private final LookupHelper lookupHelper;
	private final ProcessRepository planClasseRepository;

	public RiverCardToFlowCardWrapperServiceImpl(ProcessRepository planClasseRepository, LookupHelper lookupHelper) {
		this.lookupHelper = checkNotNull(lookupHelper);
		this.planClasseRepository = checkNotNull(planClasseRepository);
	}

	@Override
	public String getName() {
		return RIVER;
	}

	@Override
	public Flow cardToFlowCard(Card card) {
		String planId = getPlanId(card);
		Process planClasse = planClasseRepository.getPlanClasseByClassAndPlanId(card.getClassName(), planId);
		return FlowImpl.builder()
				.withCard(card)
				.withFlowStatus(lookupHelper.getFlowStatus(card))
				.withPlan(planClasse)
				.build();
	}

	public static String getPlanId(Card card) {
		String value = checkNotBlank(card.get(ATTR_PLAN_INFO, String.class));
		PlanInfo planInfo = PlanInfoImpl.deserialize(value);
		checkArgument(planInfo.getPackageId().equals(RIVER_FAKE_PACKAGE));
		return planInfo.getDefinitionId();
	}

}
