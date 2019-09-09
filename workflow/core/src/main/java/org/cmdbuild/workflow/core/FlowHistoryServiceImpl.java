/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.core;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.postgres.history.CardHistoryService;
import org.cmdbuild.workflow.FlowHistoryService;
import org.cmdbuild.workflow.core.dao.data.CardToFlowCardWrapperService;
import org.cmdbuild.workflow.model.Flow;
import org.springframework.stereotype.Component;

@Component
public class FlowHistoryServiceImpl implements FlowHistoryService {

    private final CardHistoryService historyService;
    private final CardToFlowCardWrapperService wrapperService;

    public FlowHistoryServiceImpl(CardHistoryService historyService, CardToFlowCardWrapperService wrapperService) {
        this.historyService = checkNotNull(historyService);
        this.wrapperService = checkNotNull(wrapperService);
    }

    @Override
    public PagedElements<Flow> getHistory(String classId, long cardId, DaoQueryOptions queryOptions) {
        return historyService.getHistory(classId, cardId, queryOptions).map(wrapperService::cardToFlowCard);
    }

    @Override
    public Flow getHistoryRecord(String classId, long recordId) {
        return wrapperService.cardToFlowCard(historyService.getHistoryRecord(classId, recordId));
    }

}
