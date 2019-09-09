/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v3.test.cxf;

import static com.google.common.collect.Iterables.getOnlyElement;
import java.util.List;
import java.util.Map;
import org.cmdbuild.common.error.ErrorOrWarningEvent;
import org.cmdbuild.common.error.ErrorOrWarningEventImpl;
import static org.cmdbuild.service.rest.v3.providers.ExceptionHandlerService.errorToMessage;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class WsErrorUtilsTest {

    @Test
    public void testErrorToMessage1() {
        List<Object> messages = errorToMessage(new ErrorOrWarningEventImpl(null, ErrorOrWarningEvent.ErrorEventLevel.ERROR, new NullPointerException("attribute value is null or missing for key = cm_filter_mark_ResponsabileMarketing+role:Direzione within entry = CardImpl{id=6230503, code=AD01 - AperturaPortale, type=AccountDemoCMDBuild}")));

        assertEquals(1, messages.size());

        Map<String, String> element = (Map) getOnlyElement(messages);

        assertEquals(false, element.get("show_user"));
        assertEquals("ERROR", element.get("level"));
        assertEquals("java.lang.NullPointerException: attribute value is null or missing for key = cm_filter_mark_ResponsabileMarketing+role:Direzione within entry = CardImpl{id=6230503, code=AD01 - AperturaPortale, type=AccountDemoCMDBuild}", element.get("message"));
    }

    @Test
    public void testErrorToMessage2() {
        List<Object> messages = errorToMessage(new ErrorOrWarningEventImpl(null, ErrorOrWarningEvent.ErrorEventLevel.ERROR, new NullPointerException("CM: attribute value is null or missing for key = cm_filter_mark_ResponsabileMarketing+role:Direzione within entry = CardImpl{id=6230503, code=AD01 - AperturaPortale, type=AccountDemoCMDBuild}")));

        assertEquals(2, messages.size());

        {
            Map<String, String> element = messages.stream().filter(m -> (boolean) ((Map) m).get("show_user") == true).collect(onlyElement());

            assertEquals(true, element.get("show_user"));
            assertEquals("ERROR", element.get("level"));
            assertEquals("attribute value is null or missing for key = cm_filter_mark_ResponsabileMarketing+role:Direzione within entry = CardImpl{id=6230503, code=AD01 - AperturaPortale, type=AccountDemoCMDBuild}", element.get("message"));
        }

        {
            Map<String, String> element = messages.stream().filter(m -> (boolean) ((Map) m).get("show_user") == false).collect(onlyElement());

            assertEquals(false, element.get("show_user"));
            assertEquals("ERROR", element.get("level"));
            assertEquals("java.lang.NullPointerException: CM: attribute value is null or missing for key = cm_filter_mark_ResponsabileMarketing+role:Direzione within entry = CardImpl{id=6230503, code=AD01 - AperturaPortale, type=AccountDemoCMDBuild}", element.get("message"));
        }
    }

}
