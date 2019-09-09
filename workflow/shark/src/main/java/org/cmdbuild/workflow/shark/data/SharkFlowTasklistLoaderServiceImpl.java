/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.shark.data;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.workflow.core.model.FlowImpl;
import org.cmdbuild.workflow.core.LookupHelper;
import org.springframework.stereotype.Component;
import org.cmdbuild.workflow.core.dao.data.CardToFlowCardWrapperServiceDelegate;
import org.springframework.beans.factory.annotation.Qualifier;
import static org.cmdbuild.workflow.WorkflowCommonConst.SHARK;
import org.cmdbuild.workflow.model.WorkflowException;
import org.cmdbuild.dao.beans.Card;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_PLAN_INFO;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.model.Process;
import org.cmdbuild.workflow.inner.ProcessRepository;

@Component
@Qualifier(SHARK)
public class SharkFlowTasklistLoaderServiceImpl implements CardToFlowCardWrapperServiceDelegate {

    private final LookupHelper lookupHelper;
    private final ProcessRepository planClasseRepository;

    public SharkFlowTasklistLoaderServiceImpl(ProcessRepository planClasseRepository, LookupHelper lookupHelper) {
        this.lookupHelper = checkNotNull(lookupHelper);
        this.planClasseRepository = checkNotNull(planClasseRepository);
    }

    @Override
    public String getName() {
        return SHARK;
    }

    @Override
    public Flow cardToFlowCard(Card card) {
        try {
            String planId = checkNotBlank(card.get(ATTR_PLAN_INFO, String.class));
            Process planClasse = planClasseRepository.getPlanClasseByClassAndPlanId(card.getClassName(), planId);
            return FlowImpl.builder()
                    .withCard(card)
                    .withFlowStatus(lookupHelper.getFlowStatus(card))
                    .withPlan(planClasse)
                    .build();
        } catch (Exception ex) {
            throw new WorkflowException(ex, "error processing flow card = %s", card);
        }
    }

}
