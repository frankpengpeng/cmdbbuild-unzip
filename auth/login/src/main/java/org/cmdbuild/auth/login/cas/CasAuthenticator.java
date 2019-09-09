package org.cmdbuild.auth.login.cas;

import org.apache.commons.lang3.Validate;
import org.cmdbuild.auth.login.AuthRequestInfo;
import org.cmdbuild.auth.login.ClientRequestAuthenticator;
import org.cmdbuild.auth.login.LoginUserIdentity;
import org.cmdbuild.auth.login.RequesthAuthenticatorResponse;
import static org.cmdbuild.auth.login.RequesthAuthenticatorResponseImpl.newLoginResponse;
import static org.cmdbuild.auth.login.RequesthAuthenticatorResponseImpl.newRedirectResponse;
import org.jasig.cas.client.util.CommonUtils;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.jasig.cas.client.validation.TicketValidationException;
import org.jasig.cas.client.validation.TicketValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * CAS Single Sign-On authenticator
 */
@Component
public class CasAuthenticator implements ClientRequestAuthenticator {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final String SKIP_SSO_PARAM = "skipsso";

    private final CasService casService;

    public CasAuthenticator(CasAuthenticatorConfiguration conf) {
        this.casService = new CasService(conf);
    }

    @Override
    public String getName() {
        return "CasAuthenticator";
    }

    @Override
    public RequesthAuthenticatorResponse authenticate(AuthRequestInfo request) {
        AuthRequestInfo skipSsoRequest = new SkipSsoClientRequest(request);

        String userFromTicket = casService.getUsernameFromTicket(skipSsoRequest);
        if (userFromTicket != null) {
            LoginUserIdentity login = LoginUserIdentity.builder().withValue(userFromTicket).build();
            logger.trace("authenticated as '{}'", userFromTicket);
            return newLoginResponse(login);
        }

        if (skipAuthentication(request)) {
            return null;
        } else {
            final String redirectUrl = casService.getRedirectUrl(skipSsoRequest);
            logger.trace("redirecting to '{}'", redirectUrl);
            return newRedirectResponse(redirectUrl);
        }
    }

    private boolean skipAuthentication(AuthRequestInfo request) {
        return request.getParameter(SKIP_SSO_PARAM) != null;
    }

    private class CasService {

        private static final boolean CAS_RENEW = false;
        private static final boolean CAS_GATEWAY = false;
        private final CasAuthenticatorConfiguration conf;

        public CasService(CasAuthenticatorConfiguration conf) {
            Validate.notNull(conf);
            this.conf = conf;
        }

        public String getRedirectUrl(AuthRequestInfo request) {
            return CommonUtils.constructRedirectUrl(conf.getCasServerUrl() + conf.getCasLoginPage(),
                    conf.getCasServiceParam(), request.getRequestUrl(), CAS_RENEW, CAS_GATEWAY);
        }

        public String getUsernameFromTicket(AuthRequestInfo request) {
            String ticket = request.getParameter(conf.getCasTicketParam());
            if (ticket != null) {
                return validateTicket(ticket, request.getRequestUrl());
            } else {
                return null;
            }
        }

        private String validateTicket(String ticket, String service) {
            try {
                TicketValidator ticketValidator = new Cas20ServiceTicketValidator(conf.getCasServerUrl());
                Assertion assertion = ticketValidator.validate(ticket, service);
                return assertion.getPrincipal().getName();
            } catch (TicketValidationException ex) {
                logger.warn("ticket validation exception", ex);
                return null;
            }
        }
    }

    /**
     * Wraps ClientRequest to add the SKIP_SSO parameter to the URL
     */
    private static class SkipSsoClientRequest implements AuthRequestInfo {

        private final AuthRequestInfo request;

        private SkipSsoClientRequest(AuthRequestInfo request) {
            this.request = request;
        }

        /*
		 * The request URL does never contain parameters
         */
        @Override
        public String getRequestUrl() {
            return String.format("%s?%s", request.getRequestUrl(), SKIP_SSO_PARAM);
        }

        @Override
        public String getHeader(String name) {
            return request.getHeader(name);
        }

        @Override
        public String getParameter(String name) {
            return request.getParameter(name);
        }

        @Override
        public String getRequestPath() {
            return request.getRequestPath();
        }

        @Override
        public byte[] getMultipartParameter(String key) {
            return request.getMultipartParameter(key);
        }

    }
}
