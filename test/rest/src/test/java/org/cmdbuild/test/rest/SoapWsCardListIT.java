/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.rest;

import org.cmdbuild.services.soap.client.CmdbuildSoapClient;
import static org.cmdbuild.services.soap.client.CmdbuildSoapClient.token;
import org.cmdbuild.services.soap.client.beans.CardList;
import org.cmdbuild.services.soap.client.beans.Private;
import org.cmdbuild.test.framework.CmTestRunner;
import org.cmdbuild.test.framework.Context;
import static org.cmdbuild.test.rest.TestContextProviders.TC_R2U;
import org.cmdbuild.test.rest.utils.AbstractWsIT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CmTestRunner.class)
@Context(TC_R2U)
public class SoapWsCardListIT extends AbstractWsIT {

    private Private soap;

    @Before
    public void init() {
        soap = CmdbuildSoapClient.<Private>aSoapClient()
                .withUrl(buildSoapWsUrl())
                .withAuthentication(token(this::getSessionToken))
                .build().getProxy();
    }

    @Test
    public void testSoapGetCardList1() {
        CardList cardList = soap.getCardList("Employee", null, null, null, 10l, null, null, null);
        assertNotNull(cardList);

        CardList cardListWithOffset = soap.getCardList("Employee", null, null, null, 10l, 1l, null, null);
        assertNotNull(cardListWithOffset);

        assertEquals(cardList.getCards().get(1).getId(), cardListWithOffset.getCards().get(0).getId());
        assertEquals(cardList.getCards().get(2).getId(), cardListWithOffset.getCards().get(1).getId());
        assertEquals(cardList.getCards().get(3).getId(), cardListWithOffset.getCards().get(2).getId());
    }

    @Test
    public void testSoapGetCardList2() {
        CardList cardListAll = soap.getCardList("Employee", null, null, null, null, null, null, null);

        CardList cardListPag1 = soap.getCardList("Employee", null, null, null, 3l, null, null, null);
        CardList cardListPag2 = soap.getCardList("Employee", null, null, null, 3l, 3l, null, null);
        CardList cardListPag3 = soap.getCardList("Employee", null, null, null, null, 6l, null, null);

        assertEquals(cardListAll.getTotalRows(), cardListPag1.getTotalRows());
        assertEquals(cardListAll.getTotalRows(), cardListPag2.getTotalRows());
        assertEquals(cardListAll.getTotalRows(), cardListPag3.getTotalRows());

        assertEquals(cardListPag1.getCards().size() + cardListPag2.getCards().size() + cardListPag3.getCards().size(), cardListAll.getTotalRows());
        assertEquals(cardListPag1.getCards().size() + cardListPag2.getCards().size() + cardListPag3.getCards().size(), cardListAll.getCards().size());
    }
}
