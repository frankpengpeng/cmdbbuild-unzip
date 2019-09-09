/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.easytemplate;

import com.google.common.base.Function;
import static java.util.Collections.emptyMap;
import java.util.Map;

public class DummyEasytemplateProcessor implements EasytemplateProcessor {

    private final static DummyEasytemplateProcessor INSTANCE = new DummyEasytemplateProcessor();

    public static DummyEasytemplateProcessor getInstance() {
        return INSTANCE;
    }

    @Override
    public String processExpression(String expression, ExprProcessingMode mode) {
        return expression;
    }

    @Override
    public Map<String, Function<String, Object>> getResolvers() {
        return emptyMap();
    }

}
