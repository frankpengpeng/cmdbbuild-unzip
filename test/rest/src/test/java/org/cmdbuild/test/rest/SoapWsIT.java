package org.cmdbuild.test.rest;

import static java.util.Collections.emptyList;
import static org.cmdbuild.test.rest.TestContextProviders.TC_EMPTY;

import org.cmdbuild.services.soap.client.CmdbuildSoapClient;
import static org.cmdbuild.services.soap.client.CmdbuildSoapClient.PasswordType.DIGEST;
import static org.cmdbuild.services.soap.client.CmdbuildSoapClient.PasswordType.TEXT;
import static org.cmdbuild.services.soap.client.CmdbuildSoapClient.token;
import static org.cmdbuild.services.soap.client.CmdbuildSoapClient.usernameAndPassword;
import org.cmdbuild.services.soap.client.beans.CardExt;
import org.cmdbuild.services.soap.client.beans.Private;
import org.cmdbuild.services.soap.client.beans.UserInfo;

import org.cmdbuild.test.framework.CmTestRunner;
import org.cmdbuild.test.framework.Context;
import org.cmdbuild.test.rest.utils.AbstractWsIT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CmTestRunner.class)
@Context(TC_EMPTY)
public class SoapWsIT extends AbstractWsIT {

    @Test
    public void testSoapWsWithTokenAuth() {
        Private soap = CmdbuildSoapClient.<Private>aSoapClient()
                .withUrl(buildSoapWsUrl())
                .withAuthentication(token(this::getSessionToken))
                .build().getProxy();

        UserInfo userInfo = soap.getUserInfo();

        assertNotNull(userInfo);
        assertEquals("admin", userInfo.getUsername());//TODO improve this
    }

    @Test
    public void testSoapWsWithDigestAuth() {
        Private soap = CmdbuildSoapClient.<Private>aSoapClient()
                .withUrl(buildSoapWsUrl())
                .withAuthentication(usernameAndPassword(DIGEST, "admin", "admin"))//TODO improve this
                .build().getProxy();

        UserInfo userInfo = soap.getUserInfo();

        assertNotNull(userInfo);
        assertEquals("admin", userInfo.getUsername());//TODO improve this
    }

    @Test
    public void testSoapWsWithTextAuth() {
        Private soap = CmdbuildSoapClient.<Private>aSoapClient()
                .withUrl(buildSoapWsUrl())
                .withAuthentication(usernameAndPassword(TEXT, "admin", "admin"))//TODO improve this
                .build().getProxy();

        UserInfo userInfo = soap.getUserInfo();

        assertNotNull(userInfo);
        assertEquals("admin", userInfo.getUsername());//TODO improve this
    }

    @Test(expected = Exception.class)
    public void testSoapWsFailWithTokenAuth() {
        Private soap = CmdbuildSoapClient.<Private>aSoapClient()
                .withUrl(buildSoapWsUrl())
                .withAuthentication(token(() -> "invalidtoken"))
                .build().getProxy();
        soap.getUserInfo();
        fail();
    }

    @Test(expected = Exception.class)
    public void testSoapWsFailWithDigestAuth() {
        Private soap = CmdbuildSoapClient.<Private>aSoapClient()
                .withUrl(buildSoapWsUrl())
                .withAuthentication(usernameAndPassword(DIGEST, "admin", "invalidPassword"))//TODO improve this
                .build().getProxy();
        soap.getUserInfo();
        fail();
    }

    @Test(expected = Exception.class)
    public void testSoapWsWFailithTextAuth() {
        Private soap = CmdbuildSoapClient.<Private>aSoapClient()
                .withUrl(buildSoapWsUrl())
                .withAuthentication(usernameAndPassword(TEXT, "admin", "invalidPassword"))//TODO improve this
                .build().getProxy();
        soap.getUserInfo();
        fail();
    }

//    @Test TODO test with user and password new algo
//    public void testSoapWs3() {
//        getRestClient().system().setConfig("org.cmdbuild.auth.preferredPasswordAlgorythm", "cm3");
//        getRestClient().users().createUser("");
//                
//        Private soap = CmdbuildSoapClient.<Private>aSoapClient()
//                .withUrl(buildSoapWsUrl())
//                .withAuthentication(usernameAndPassword(DIGEST, "admin", "admin"))//TODO improve this
//                .build().getProxy();
//
//        UserInfo userInfo = soap.getUserInfo();
//
//        assertNotNull(userInfo);
//        assertEquals("admin", userInfo.getUsername());//TODO improve this
//    }
    @Test
    @Ignore("TODO verify this")
    public void testGetUnexistingCard() {
        Private soap = CmdbuildSoapClient.<Private>aSoapClient()
                .withUrl(buildSoapWsUrl())
                //                .withAuthentication(token(this::getSessionToken)) 
                .withAuthentication(usernameAndPassword(DIGEST, "admin", "admin"))//TODO improve this
                .build().getProxy();

        UserInfo userInfo = soap.getUserInfo();

        CardExt card = soap.getCardWithLongDateFormat("Class", -1l, emptyList());
//        CardExt card = soap.getCardWithLongDateFormat("Class", -1l, listOf(Attribute.class).accept(l -> {
//            Attribute a = new Attribute();
//            a.setName(ATTR_ID);
//            l.add(a);
//        }));

        assertNotNull(card);
        assertEquals(-1, card.getId());
    }

}
