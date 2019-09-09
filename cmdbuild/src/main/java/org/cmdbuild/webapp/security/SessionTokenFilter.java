/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.webapp.security;

import org.cmdbuild.webapp.beans.AuthRequestInfoImpl;
import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Optional.fromNullable;
import java.io.IOException;
import static java.util.Arrays.stream;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.cmdbuild.auth.login.AuthenticationService;
import org.cmdbuild.auth.login.ClientAuthenticatorResponse;
import org.cmdbuild.auth.login.LoginDataImpl;
import static org.cmdbuild.common.error.ErrorAndWarningCollectorService.marker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.auth.session.model.Session;
import static org.cmdbuild.common.http.HttpConst.CMDBUILD_AUTHORIZATION_COOKIE;
import static org.cmdbuild.common.http.HttpConst.CMDBUILD_AUTHORIZATION_HEADER;
import org.cmdbuild.webapp.services.FilterHelperService;
import static org.cmdbuild.webapp.utils.FilterUtils.buildCmdbuildSessionCookie;

@Component
@Primary
public class SessionTokenFilter extends OncePerRequestFilter {

    private static final String CMDBUILD_AUTHORIZATION = CMDBUILD_AUTHORIZATION_HEADER;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private FilterHelperService helper;
    @Autowired
    private SessionService sessionService;
    @Autowired
    private AuthenticationService authenticationService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        logger.trace("doFilterInternal BEGIN");

        boolean hasValidSessionToken = false, redirectToLoginPageForIncompleteSession = false;

        try {
            String sessionToken = getSessionTokenFromRequest(request);
            hasValidSessionToken = handleSessionToken(sessionToken);
        } catch (Exception ex) {
            logger.error("session token filter error", ex);
        }

        try {
            if (!hasValidSessionToken) {
                ClientAuthenticatorResponse authenticatorResponse = authenticationService.validateCredentialsAndCreateAuthResponse(new AuthRequestInfoImpl(request));
                if (authenticatorResponse.isAuthenticated()) {
                    String sessionToken = sessionService.create(LoginDataImpl.builder().withNoPasswordRequired().withUser(authenticatorResponse.getUserOrNull()).build());
                    Session session = sessionService.getSessionById(sessionToken);
                    response.addCookie(buildCmdbuildSessionCookie(request.getContextPath(), sessionToken));
                    if (!session.getOperationUser().hasDefaultGroup() && enableRedirectToLoginForIncompleteSession()) {
                        redirectToLoginPageForIncompleteSession = true;
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("request auth error", ex);
        }

        logger.trace("doFilterInternal END");

        if (redirectToLoginPageForIncompleteSession) {
            response.sendRedirect(helper.getLoginRedirectUrl(request));
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private boolean handleSessionToken(String sessionToken) {

        logger.trace("session token from request = {}", sessionToken);
        if (isBlank(sessionToken)) {
            logger.trace("no session token from request, skipping");
        } else {
            Session session = sessionService.getSessionByIdOrNull(sessionToken);
            boolean sessionExists = session != null, sessionHasGroup = session != null && session.getOperationUser().hasDefaultGroup();
            if (!sessionExists) {
                logger.warn(marker(), "session not found for token = {}", sessionToken);
            } else if (!sessionHasGroup && !allowSessionsWithoutGroup()) {
                logger.warn(marker(), "invalid session for token = {}", sessionToken);
            } else {
                Authentication auth = new AuthenticationToken(sessionToken); //TODO get session id from token (current session token is equal to session id; would be better to hide session id and use a custom session token)
                logger.trace("set auth = {} from session token = {}", auth, sessionToken);
                SecurityContextHolder.getContext().setAuthentication(auth);
                return true;
            }
        }
        return false;
    }

    public static String getSessionTokenFromRequest(HttpServletRequest httpRequest) {
        return fromNullable(trimToNull(httpRequest.getHeader(CMDBUILD_AUTHORIZATION_HEADER)))
                .or(fromNullable(trimToNull(httpRequest.getParameter(CMDBUILD_AUTHORIZATION))))
                .or(fromNullable(stream(firstNonNull(httpRequest.getCookies(), new Cookie[]{})).filter(input -> input.getName().equals(CMDBUILD_AUTHORIZATION_COOKIE)).findFirst().map(input -> input.getValue()).orElse(null)))
                .orNull();
    }

    protected boolean allowSessionsWithoutGroup() {
        return false;
    }

    protected boolean enableRedirectToLoginForIncompleteSession() {
        return false;
    }

}
