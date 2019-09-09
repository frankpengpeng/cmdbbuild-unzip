/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.core.dao.data;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.uniqueIndex;
import java.util.List;
import java.util.Map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.workflow.WorkflowCommonConst.RIVER;
import static org.cmdbuild.workflow.WorkflowCommonConst.SHARK;
import org.cmdbuild.workflow.model.WorkflowException;
import org.cmdbuild.workflow.utils.PlanIdUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.beans.Card;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_PLAN_INFO;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.utils.PlanIdUtils.PackageIdAndVersionAndDefinitionId;

@Component
@Primary
public class CardToFlowCardWrapperServiceImpl implements CardToFlowCardWrapperService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final Map<String, CardToFlowCardWrapperServiceDelegate> delegates;

	public CardToFlowCardWrapperServiceImpl(List<CardToFlowCardWrapperServiceDelegate> delegates) {
		this.delegates = uniqueIndex(checkNotNull(delegates), CardToFlowCardWrapperServiceDelegate::getName);
	}

	private CardToFlowCardWrapperService getDelegate(String delegateToUse) {
		return checkNotNull(delegates.get(delegateToUse), "delegate not found for key = %s", delegateToUse);
	}

	@Override
	public Flow cardToFlowCard(Card card) {
		try {
			String delegateToUse = getFlowProvider(card);
			logger.debug("selected delegate = {} for card = {}", delegateToUse, card);
			return getDelegate(delegateToUse).cardToFlowCard(card);
		} catch (Exception ex) {
			throw new WorkflowException(ex, "error processing flow card = %s", card);
		}
	}

	private String getFlowProvider(Card card) {
		String value = checkNotBlank(card.get(ATTR_PLAN_INFO, String.class), "missing plan info (plan id) from card data");
		PlanIdUtils.PackageIdAndVersionAndDefinitionId packageIdAndVersionAndDefinitionId = PlanIdUtils.readPlanId(value);
		logger.trace("selecting flow provider using info = {}", packageIdAndVersionAndDefinitionId);
		if (isRiver(packageIdAndVersionAndDefinitionId)) {
			return RIVER;
		} else {
			return SHARK;
		}
	}

	public static boolean isRiver(PackageIdAndVersionAndDefinitionId info) {
		return info.getPackageId().startsWith("river");
	}

}
