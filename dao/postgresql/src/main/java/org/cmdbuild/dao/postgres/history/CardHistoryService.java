/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.history;

import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.beans.Card;

public interface CardHistoryService {

	PagedElements<Card> getHistory(String classId, long cardId, DaoQueryOptions queryOptions);

	Card getHistoryRecord(String classId, long recordId);

}
