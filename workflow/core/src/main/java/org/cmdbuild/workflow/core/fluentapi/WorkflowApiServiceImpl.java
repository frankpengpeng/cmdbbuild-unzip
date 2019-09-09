/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.core.fluentapi;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import java.lang.reflect.Method;
import static java.util.Arrays.asList;
import java.util.Collections;
import java.util.Map;
import javax.inject.Provider;
import org.cmdbuild.api.fluent.CmApiService;
import static org.cmdbuild.common.error.ErrorAndWarningCollectorService.marker;
import static org.cmdbuild.utils.lang.CmReflectionUtils.wrapProxy;
import org.cmdbuild.utils.lang.ProxyWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WorkflowApiServiceImpl implements WorkflowApiService, CmApiService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Provider<ExtendedApi> workflowApi;

    public WorkflowApiServiceImpl(Provider<ExtendedApi> workflowApi) {
        this.workflowApi = checkNotNull(workflowApi);
    }

    @Override
    public Map<String, Object> getWorkflowApiAsDataMap() {
        return Collections.singletonMap("cmdb", getWorkflowApi());
    }

    @Override
    public Object getCmApi() {
        return getWorkflowApi();
    }

    @Override
    public ExtendedApi getWorkflowApi() {
        return wrapProxy(ExtendedApi.class, checkNotNull(workflowApi.get()), new LoggerProxyWrapper());
    }

    private class LoggerProxyWrapper extends ProxyWrapper {

        @Override
        public void afterFailedMethodInvocation(Method method, Object[] params, Throwable error) {
            logger.error(marker(), format("error invoking method = %s of cmdbuild api with params = %s", method.getName(), asList(params)), error);
        }

    }
}
