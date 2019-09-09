package org.cmdbuild.workflow;

import com.google.common.base.Stopwatch;
import com.google.common.base.Supplier;
import com.google.gson.Gson;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.services.soap.client.CmdbuildSoapClient.token;
import static org.cmdbuild.services.soap.client.CmdbuildSoapClient.usernameAndPassword;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.Builder;
import org.cmdbuild.services.soap.client.beans.Private;
import org.cmdbuild.services.soap.client.CmdbuildSoapClient;
import static org.cmdbuild.services.soap.client.CmdbuildSoapClient.PasswordType.TEXT;
import org.cmdbuild.services.soap.client.SoapClient;
import static org.cmdbuild.utils.lang.CmReflectionUtils.wrapProxy;
import org.cmdbuild.utils.lang.ProxyWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.workflow.api.CmdbuildClientConfig;

public class CmdbuildClientBuilder implements Builder<Private> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String URL_SEPARATOR = "/";
    private static final String URL_SUFFIX = "services/soap/Private";

    private static final String USER_SEPARATOR = "#";
    private static final String USER_SEPARATOR_2 = "!";
    private static final String GROUP_SEPARATOR = "@";

    private final CmdbuildClientConfig configuration;
    private String username;
    private String group;
    private boolean forciblyImpersonate;

    public CmdbuildClientBuilder(CmdbuildClientConfig configuration) {
        this.configuration = configuration;
        this.username = EMPTY;
        this.group = EMPTY;
    }

    public CmdbuildClientBuilder withUsername(String username) {
        this.username = username;
        return this;
    }

    public CmdbuildClientBuilder withGroup(String group) {
        this.group = group;
        return this;
    }

    public CmdbuildClientBuilder forciblyImpersonate(boolean forciblyImpersonate) {
        this.forciblyImpersonate = forciblyImpersonate;
        return this;
    }

    @Override
    public Private build() {
        Validate.notNull(username);
        Validate.notNull(group);

        String url = completeUrl(configuration.getUrl());
        String fullUsername = completeUsername(configuration.getUsername(), username, group);
        String password = configuration.getPassword();

        String modeDescription;
        Private proxy;

        logger.info("creating SOAP client for url '{}' and username '{}'", url, fullUsername);
        modeDescription = "SOAP";
        SoapClient<Private> soapClient;
        if (configuration.isTokenEnabled()) {
            Supplier<String> tokenSupplier = () -> CmdbuildSoapClient.<Private>aSoapClient().forClass(Private.class).withUrl(url)
                    .withAuthentication(usernameAndPassword(TEXT, fullUsername, password))
                    .build().getProxy().createSession();
            soapClient = CmdbuildSoapClient.<Private>aSoapClient().forClass(Private.class).withUrl(url)
                    .withAuthentication(token(tokenSupplier))
                    .build();
        } else {
            soapClient = CmdbuildSoapClient.<Private>aSoapClient().forClass(Private.class).withUrl(url)
                    .withAuthentication(usernameAndPassword(TEXT, fullUsername, password))
                    .build();
        }
        proxy = soapClient.getProxy();

        proxy = wrapProxy(Private.class, proxy, new ProxyWrapper() {

            private final Gson gson = new Gson();
            private Stopwatch stopwatch;

            @Override
            public void beforeMethodInvocation(Method method, Object[] params) {
                logger.info("invoke cmdbuild {} client method {}", modeDescription, method.getName());
//				if (logger.isTraceEnabled()) {
                logger.info("method params = {}", gson.toJson(params));
//				}
                stopwatch = Stopwatch.createStarted();
            }

            @Override
            public void afterFailedMethodInvocation(Method method, Object[] params, Throwable error) {
                logger.error("error invoking cmdbuild {} client method {}", modeDescription, method.getName(), error);
            }

            @Override
            public void afterMethodInvocation(Method method, Object[] params) {
                logger.info("completed cmdbuild {} client method {}, elapsed time is {}ms", modeDescription, method.getName(), stopwatch.elapsed(TimeUnit.MILLISECONDS));
                stopwatch = null;
            }

            @Override
            public Object afterSuccessfullMethodInvocation(Method method, Object[] params, Object response) {
//				if (logger.isTraceEnabled()) {
                stopwatch.stop();
                logger.info("method return = {}", gson.toJson(response));
//				}
                return response;
            }

        });

        return proxy;
    }

    private String completeUrl(String baseUrl) {
        return new StringBuilder(baseUrl)
                .append(baseUrl.endsWith(URL_SEPARATOR) ? EMPTY : URL_SEPARATOR)
                .append(URL_SUFFIX)
                .toString();
    }

    private String completeUsername(String wsUsername, String currentUser, String currentGroup) {
        String userSeparator = forciblyImpersonate ? USER_SEPARATOR_2 : USER_SEPARATOR;
        return new StringBuilder(wsUsername)
                .append(isNotBlank(currentUser) ? userSeparator + currentUser : EMPTY)
                .append(isNotBlank(currentGroup) ? GROUP_SEPARATOR + currentGroup : EMPTY)
                .toString();
    }

}
