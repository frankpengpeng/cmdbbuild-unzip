package org.cmdbuild.auth.login.header;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.auth.login.AuthRequestInfo;
import org.cmdbuild.auth.login.ClientRequestAuthenticator;
import org.cmdbuild.auth.login.LoginUserIdentity;
import org.cmdbuild.auth.login.RequesthAuthenticatorResponse;
import static org.cmdbuild.auth.login.RequesthAuthenticatorResponseImpl.newLoginResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Authenticates a user based on the presence of a header parameter. It can be
 * used when a Single Sign-On proxy adds the header.
 */
@Component
public class HeaderAuthenticator implements ClientRequestAuthenticator {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final HeaderAuthenticatorConfiguration conf;

    public HeaderAuthenticator(HeaderAuthenticatorConfiguration conf) {
        this.conf = checkNotNull(conf);
    }

    @Override
    public String getName() {
        return "HeaderAuthenticator";
    }

    @Override
    public RequesthAuthenticatorResponse authenticate(AuthRequestInfo request) {
        String headerAttr = conf.getHeaderAttributeName(),
                loginString = request.getHeader(headerAttr);
        logger.trace("using header attr = {}", headerAttr);
        if (isNotBlank(loginString)) {
            LoginUserIdentity login = LoginUserIdentity.build(loginString);
            logger.debug("Authenticated user = {}", loginString);
            return newLoginResponse(login);
        } else {
            return null;
        }
    }

}
