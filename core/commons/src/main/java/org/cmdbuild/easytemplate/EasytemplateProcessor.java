package org.cmdbuild.easytemplate;

import com.google.common.base.Function;
import java.util.Map;
import javax.annotation.Nullable;
import static org.cmdbuild.easytemplate.EasytemplateProcessor.ExprProcessingMode.EPM_DEFAULT;

public interface EasytemplateProcessor {

    @Nullable
    String processExpression(@Nullable String expression, ExprProcessingMode mode);

    Map<String, Function<String, Object>> getResolvers();

    @Nullable
    default String processExpression(@Nullable String expression) {
        return processExpression(expression, EPM_DEFAULT);
    }

    enum ExprProcessingMode {
        EPM_JAVASCRIPT, EPM_DEFAULT
    }
}
