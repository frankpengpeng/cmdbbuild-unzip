package org.cmdbuild.services.soap.security;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;

import org.apache.wss4j.common.ext.WSPasswordCallback;
import org.cmdbuild.auth.grant.AuthorizationException;
import org.cmdbuild.auth.login.LoginUserIdentity;
import org.cmdbuild.auth.login.AuthenticationService;
import org.cmdbuild.auth.user.LoginUser;
import org.cmdbuild.auth.login.AuthenticationConfiguration;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component("cmdbuildPasswordCallback")
public class PasswordHandler implements CallbackHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final AuthenticationService authenticationService;
    private final AuthenticationConfiguration authConfiguration;

    public PasswordHandler(AuthenticationService authenticationService, AuthenticationConfiguration authConfiguration) {
        this.authenticationService = checkNotNull(authenticationService);
        this.authConfiguration = checkNotNull(authConfiguration);
    }

    @Bean("cmdbuildPasswordCallbackInterceptor")
    public WSS4JInInterceptor createCmdbuildPasswordCallbackInterceptor() {
        return new WSS4JInInterceptor(map(
                "action", "UsernameToken",
                "passwordCallbackRef", this
        ));
    }

    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (Callback callback : callbacks) {
            if (callback instanceof WSPasswordCallback) {
                WSPasswordCallback wssPasswordCallback = (WSPasswordCallback) callback;
                LoginUserIdentity login = new AuthenticationStringHelper(wssPasswordCallback.getIdentifier()).getAuthenticationLogin().getLogin();
                LoginUser user;
                logger.trace("processing wss password callback with usage = {}", wssPasswordCallback.getUsage());
                switch (wssPasswordCallback.getUsage()) {
                    case WSPasswordCallback.USERNAME_TOKEN:
                        logger.warn("processing soap request with `PasswordDigest` auth: `PasswordDigest` is not recommended since it requires clear text password on server side, and will fail if password are properly secured with a one-way function server-side");
                        wssPasswordCallback.setPassword(authenticationService.getUnencryptedPassword(login));
                        user = authenticationService.getUser(login);
                        break;
                    default:
                        if (authConfiguration.getForceWSPasswordDigest()) {
                            throw new UnsupportedCallbackException(wssPasswordCallback, "Unsupported authentication method");
                        }
                        user = authenticationService.checkPasswordAndGetUser(login, wssPasswordCallback.getPassword());
                }
                if (user == null) {
                    throw new UnsupportedCallbackException(wssPasswordCallback);
                }
            }
        }
    }

    public static class AuthenticationStringHelper {

        private static final String PATTERN = "([^@#!]+(@[^\\.]+\\.[^@#]+)?)((#|!)([^@]+(@[^\\.]+\\.[^@]+)?))?(@([^@\\.]+))?";

        private final LoginAndGroup authenticationLogin;
        private final LoginAndGroup impersonationLogin;
        private final boolean impersonateForcibly;

        public AuthenticationStringHelper(String username) {
            Pattern pattern = Pattern.compile(PATTERN);
            Matcher matcher = pattern.matcher(username);
            if (!matcher.find()) {
                // FIXME
                throw new AuthorizationException("login failed");
            }
            String userOrServiceUser = matcher.group(1);
            String impersonate = matcher.group(4);
            String impersonatedUser = matcher.group(5);
            String group = matcher.group(8);

            authenticationLogin = LoginAndGroup.newInstance(LoginUserIdentity.builder() //
                    .withValue(userOrServiceUser) //
                    .build(), group);
            impersonateForcibly = defaultIfBlank(impersonate, "#").equals("!");
            if (isNotEmpty(impersonatedUser)) {
                impersonationLogin = LoginAndGroup.newInstance(LoginUserIdentity.builder() //
                        .withValue(impersonatedUser) //
                        .build(), group);
            } else {
                impersonationLogin = null;
            }
        }

        public LoginAndGroup getAuthenticationLogin() {
            return authenticationLogin;
        }

        public LoginAndGroup getImpersonationLogin() {
            return impersonationLogin;
        }

        public boolean shouldImpersonate() {
            return impersonationLogin != null;
        }

        public boolean impersonateForcibly() {
            return impersonateForcibly;
        }

        @Override
        public String toString() {
            return reflectionToString(this, SHORT_PREFIX_STYLE);
        }

    }
}
