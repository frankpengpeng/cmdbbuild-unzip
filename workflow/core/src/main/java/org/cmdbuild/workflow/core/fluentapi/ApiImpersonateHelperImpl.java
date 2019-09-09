/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.core.fluentapi;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import java.lang.reflect.Method;
import java.util.Stack;
import javax.annotation.Nullable;
import javax.inject.Provider;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.auth.user.OperationUser;
import static org.cmdbuild.utils.lang.CmConvertUtils.isPrimitiveOrWrapper;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmReflectionUtils.wrapProxy;
import org.cmdbuild.utils.lang.ProxyWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ApiImpersonateHelperImpl implements ApiImpersonateHelper {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Provider<ExtendedApi> extendedApiProvider;
    private final SessionService sessionService;

    public ApiImpersonateHelperImpl(Provider<ExtendedApi> extendedApi, SessionService sessionService) {
        this.extendedApiProvider = checkNotNull(extendedApi);
        this.sessionService = checkNotNull(sessionService);
    }

    @Override
    public ExtendedApi buildImpersonateApiWrapper(String username, @Nullable String group) {
        logger.debug("build impersonate api wrapper for user = {} group = {}", username, group);
        checkNotBlank(username, "username is null");
        ExtendedApi inner = checkNotNull(extendedApiProvider.get());
        return wrapImpersonateProxy(ExtendedApi.class, inner, username, group);
    }

    private <T> T wrapImpersonateProxy(Class<T> type, T inner, String username, @Nullable String group) {
        return wrapProxy(type, inner, new ProxyWrapper() {

            private final Stack<Runnable> afterAction = new Stack();

            @Override
            public void beforeMethodInvocation(Method method, Object[] params) {
                OperationUser currentUser = sessionService.getCurrentSession().getOperationUser();
                if (equal(currentUser.getUsername(), username) && (isBlank(group) || equal(currentUser.getDefaultGroupNameOrNull(), group))) {
                    logger.debug("no need to impersonate user = {} with role = {} before invoking method = {}, already running with correct user", username, group, method);
                    afterAction.push(() -> {
                        logger.debug("no need to deimpersonate after invoking method = {}, already running with correct user", method);
                    });
                } else {
                    logger.debug("impersonate user = {} with role = {} before invoking method = {}", username, group, method);
                    sessionService.impersonate(username, group);
                    afterAction.push(() -> {
                        logger.debug("deimpersonate after invoking method = {}", method);
                        sessionService.deimpersonate();
                    });
                }
            }

            @Override
            public Object afterSuccessfullMethodInvocation(Method method, Object[] params, Object response) {
                if (response != null) {
                    Class resType = method.getReturnType();
                    if (!isPrimitiveOrWrapper(resType)) {
                        response = wrapImpersonateProxy(resType, (T) response, username, group);
                    }
                }
                return response;
            }

            @Override
            public void afterMethodInvocation(Method method, Object[] params) {
                afterAction.pop().run();
            }
        });
    }
}
