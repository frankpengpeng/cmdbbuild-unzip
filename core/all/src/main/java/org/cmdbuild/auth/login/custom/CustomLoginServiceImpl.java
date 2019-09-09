/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.login.custom;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import java.util.Map;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.api.fluent.CmApiService;
import org.cmdbuild.auth.login.AuthRequestInfo;
import org.cmdbuild.auth.login.LoginDataImpl;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.lang.scriptexecutors.BeanshellScriptExecutor;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.customclassloader.CustomClassloaderServiceImpl;
import static org.cmdbuild.utils.encode.CmPackUtils.unpackStringIfPackedOrBase64;

@Component
public class CustomLoginServiceImpl implements CustomLoginService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CmApiService apiService;
    private final SessionService sessionService;
    private final CustomLoginConfiguration config;
    private final CustomClassloaderServiceImpl customClassloaderService;

    public CustomLoginServiceImpl(CmApiService apiService, SessionService sessionService, CustomLoginConfiguration config, CustomClassloaderServiceImpl customClassloaderService) {
        this.apiService = checkNotNull(apiService);
        this.sessionService = checkNotNull(sessionService);
        this.config = checkNotNull(config);
        this.customClassloaderService = checkNotNull(customClassloaderService);
    }

    @Override
    public void handleCustomLoginRequestAndCreateSession(AuthRequestInfo authRequestInfo) {

        checkArgument(config.isCustomLoginEnabled(), "custom login is not enabled");

        String script = checkNotBlank(unpackStringIfPackedOrBase64(config.getCustomLoginHandlerScript()), "custom login handler script config is null"),
                classLoaderPath = config.getCustomLoginHandlerScriptClasspath();

        logger.trace("execute custom login handler script = \n\n{}\n", script);

        Map<String, Object> result = new BeanshellScriptExecutor(script, isBlank(classLoaderPath) ? null : customClassloaderService.getCustomClassLoader(classLoaderPath)).execute(map(
                "request", authRequestInfo,
                "cmdb", apiService.getCmApi(),
                "logger", LoggerFactory.getLogger(format("%s.HANDLER_SCRIPT", getClass().getName()))));

        String username = checkNotBlank(toStringOrNull(result.get("username")), "script fail to set 'username' var"), group = toStringOrNull(result.get("group"));

        logger.debug("custom login handler returned username =< {} > group =< {} >", username, group);

        sessionService.createAndSet(LoginDataImpl.builder().withLoginString(username).withGroupName(group).withNoPasswordRequired().build());
    }

}
