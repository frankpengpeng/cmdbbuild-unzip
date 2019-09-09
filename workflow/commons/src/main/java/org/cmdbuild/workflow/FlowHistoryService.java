/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow;

import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.workflow.model.Flow;

public interface FlowHistoryService {

    PagedElements<Flow> getHistory(String classId, long cardId, DaoQueryOptions queryOptions);

    Flow getHistoryRecord(String classId, long recordId);
}
