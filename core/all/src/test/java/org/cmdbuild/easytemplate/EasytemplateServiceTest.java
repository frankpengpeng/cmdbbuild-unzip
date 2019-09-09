/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.easytemplate;

import org.cmdbuild.auth.user.OperationUser;
import static org.cmdbuild.auth.user.OperationUserImpl.anonymousOperationUser;
import org.cmdbuild.auth.user.OperationUserSupplier;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class EasytemplateServiceTest {

    private String jsContext;

    private EasytemplateService easytemplateService;
    private EasytemplateProcessor processor;

    @Before
    public void init() {
        jsContext = toJson(map("client", map("one", "valOne", "two", map("inner", "innerValue"))));

        OperationUser dummyUser = anonymousOperationUser();
        OperationUserSupplier operationUserSupplier = () -> dummyUser;
        easytemplateService = new EasytemplateServiceImpl(operationUserSupplier);
        processor = easytemplateService.getDefaultProcessorWithJsContext(jsContext);
    }

    @Test
    public void testSimpleJsContext() {
        assertEquals("valOne", processor.processExpression("{client:one}"));
    }

    @Test
    public void testInnerJsContext() {
        assertEquals("innerValue", processor.processExpression("{client:two.inner}"));
    }

    @Test
    public void testJsContextResolveToNull() {
        assertEquals("", processor.processExpression("{client:not.existing}"));
    }
//    @Test
//    public void testIdAndDescVarProcessing() {
//        assertEquals("myDesc", processor.processExpression("{server:}"));
//    }

}
