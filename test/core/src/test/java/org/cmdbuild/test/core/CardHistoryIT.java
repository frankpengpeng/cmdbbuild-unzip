/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.core;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.ClassDefinitionImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.postgres.history.CardHistoryService;
import org.cmdbuild.test.framework.CmTestRunner;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.prepareTuid;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.tuid;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CmTestRunner.class)
public class CardHistoryIT {

    private final DaoService dao;
    private final CardHistoryService historyService;

    public CardHistoryIT(DaoService dao, CardHistoryService historyService) {
        this.dao = checkNotNull(dao);
        this.historyService = checkNotNull(historyService);
    }

    @Before
    public void init() {
        prepareTuid();
    }

    @Test
    public void testCardHistory() {
        Classe one = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("HistoryTestClass")).build());
        Card cardOne = dao.create(CardImpl.buildCard(one, map("Description", "testCard")));
        dao.delete(one.getName(), cardOne.getId());
        Card cardOneHistory = historyService.getHistoryRecord(one.getName(), cardOne.getId());

        assertEquals(cardOne.getId(), cardOneHistory.getId());
        assertEquals(cardOne.getCode(), cardOneHistory.getCode());
        assertEquals(cardOne.getDescription(), cardOneHistory.getDescription());
        assertEquals(cardOneHistory.getCardStatus().toString(), "N");
    }
}
