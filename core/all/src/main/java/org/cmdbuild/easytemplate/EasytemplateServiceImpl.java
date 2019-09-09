/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.easytemplate;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import javax.annotation.Nullable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.auth.user.OperationUser;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmNullableUtils.getClassOfNullable;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static org.cmdbuild.utils.lang.CmConvertUtils.isNotDecimal;

@Component
public class EasytemplateServiceImpl implements EasytemplateService {

    private final static String JS_CONTEXT_UTILS = readToString(EasytemplateServiceImpl.class.getResourceAsStream("/org/cmdbuild/easytemplate/easytemplate_context_utils.js"));

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");

    private final OperationUserSupplier userSupplier;

    public EasytemplateServiceImpl(OperationUserSupplier userSupplier) {
        this.userSupplier = checkNotNull(userSupplier);
    }

    @Override
    public EasytemplateProcessor getDefaultProcessorWithJsContext(String jsContext) {
        return new EasytemplateProcessorHelper(jsContext).getEasytemplateProcessor();
    }

    @Override
    @Nullable
    public Object evalJavascriptCode(String expr) {
        logger.debug("execute js script = \n\n{}\n", expr);
        try {
            Object value = engine.eval(expr);
            logger.trace("raw script output = {} ({})", value, getClassOfNullable(value).getName());
            value = jsToSystem(value);
            logger.trace("return converted script output = {} ({})", value, getClassOfNullable(value).getName());
            return value;
        } catch (Exception ex) {
            throw runtime(ex, "error processing js script = '%s'", abbreviate(expr));
        }
    }

    private @Nullable
    Object jsToSystem(@Nullable Object value) {
        if (value == null || value instanceof Integer || value instanceof Long || value instanceof String) {
            return value;
        } else if (value instanceof Number && isNotDecimal((Number) value)) {
            return toLong(value);
        } else {
            return value;
        }
    }

    private class EasytemplateProcessorHelper {

        private final String jsContext;

        public EasytemplateProcessorHelper(String jsContext) {
            this.jsContext = jsContext;
        }

        private EasytemplateProcessor getEasytemplateProcessor() {
            OperationUser user = userSupplier.getUser();
            return EasytemplateProcessorImpl.builder()
                    .withResolver("client", this::evalClient)
                    .withResolver("server", this::evalServer)
                    .withResolver("user", (key) -> {
                        switch (key.toLowerCase()) {
                            case "id":
                                return user.getLoginUser().getId();
                            case "name":
                                return user.getLoginUser().getUsername();
                            default:
                                throw unsupported("unsupported 'user:' key = %s", key);
                        }
                    })
                    .withResolver("group", (key) -> {
                        switch (key.toLowerCase()) {
                            case "id":
                                return user.hasDefaultGroup() ? user.getDefaultGroup().getId() : null;
                            case "name":
                                return user.hasDefaultGroup() ? user.getDefaultGroup().getName() : null;
                            default:
                                throw unsupported("unsupported 'group:' key = %s", key);
                        }
                    })
                    .build();
        }

        private Object evalClient(String expr) {
            return evalJsSubcontext("client", expr);
        }

        private Object evalServer(String expr) {
            return evalJsSubcontext("server", expr);
        }

        private Object evalJsSubcontext(String subContext, String expr) {
            return evalJavascriptCode(format("%s\n\nvar context = %s;\n\nevalJsSubcontext(context, \"%s\", \"%s\");", JS_CONTEXT_UTILS, jsContext, subContext, expr));
        }

    }

}
