/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.webapp.filters;

import org.cmdbuild.webapp.services.FilterHelperService;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.cmdbuild.auth.login.custom.CustomLoginService;
import org.cmdbuild.auth.session.SessionService;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import org.cmdbuild.webapp.beans.AuthRequestInfoImpl;
import static org.cmdbuild.webapp.utils.FilterUtils.buildCmdbuildSessionCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
public class CustomLoginFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final String errorMessageTemplate = readToString(getClass().getResourceAsStream("/org/cmdbuild/webapp/custom_login_error.html"));

    private final CustomLoginService service;
    private final FilterHelperService helper;
    private final SessionService sessionService;

    public CustomLoginFilter(CustomLoginService service, FilterHelperService helper, SessionService sessionService) {
        this.service = checkNotNull(service);
        this.helper = checkNotNull(helper);
        this.sessionService = checkNotNull(sessionService);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            logger.debug("trying to authenticate custom login request");
            service.handleCustomLoginRequestAndCreateSession(new AuthRequestInfoImpl(request));
            logger.debug("set session token, redirect to login page");
            String sessionToken = sessionService.getCurrentSessionId();
            response.addCookie(buildCmdbuildSessionCookie(request.getContextPath(), sessionToken));
            response.sendRedirect(helper.getLoginRedirectUrl(request));
        } catch (Exception ex) {
            logger.error("custom login filter auth error", ex);
            response.setStatus(401);
            response.setContentType("text/html");
            response.getWriter().write(errorMessageTemplate);
        }
    }

}
