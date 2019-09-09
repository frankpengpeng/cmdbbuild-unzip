package org.cmdbuild.config;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import org.springframework.stereotype.Component;
import org.cmdbuild.auth.login.AuthenticationConfiguration;
import org.cmdbuild.auth.login.PasswordAlgo;
import static org.cmdbuild.config.api.ConfigCategory.CC_ENV;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@Component
@ConfigComponent("org.cmdbuild.auth")
public final class AuthConfigurationImpl implements AuthenticationConfiguration {

    @ConfigValue(key = "force.ws.password.digest", description = "", defaultValue = TRUE)
    private boolean forceWsPasswordDigest;

    @ConfigValue(key = "case.insensitive", description = "", defaultValue = TRUE)
    private boolean authCaseInsensitive;

    @ConfigValue(key = "methods", description = "auth methods (valid values are 'DBAuthenticator', 'LdapAuthenticator', "
            + "etc; 'rsa' and 'file' are always enabled, you can disable them by adding '-rsa' and '-file'; "
            + "order of auth methods is meaningful", defaultValue = "DBAuthenticator")
    private List<String> authMethods;

    @ConfigValue(key = "header.attribute.name", description = "", defaultValue = "username")
    private String headerAttributeName;

    @ConfigValue(key = "cas.server.url", description = "", defaultValue = "", category = CC_ENV)
    private String casServerUrl;

    @ConfigValue(key = "cas.login.page", description = "", defaultValue = "/login", category = CC_ENV)
    private String casLoginPage;

    @ConfigValue(key = "cas.ticket.param", description = "", defaultValue = "ticket", category = CC_ENV)
    private String casTicketParam;

    @ConfigValue(key = "cas.service.param", description = "", defaultValue = "service", category = CC_ENV)
    private String casServiceParam;

    @ConfigValue(key = "ldap.basedn", description = "ldap base dn for user query (such as dc=example,dc=com)", defaultValue = "", category = CC_ENV)
    private String ldapBaseDn;

    @ConfigValue(key = "ldap.server.address", description = "ldap server host address", defaultValue = "localhost", category = CC_ENV)
    private String ldapServerAddress;

    @ConfigValue(key = "ldap.server.port", description = "ldap server port", defaultValue = "389", category = CC_ENV)
    private String ldapServerPort;

    @ConfigValue(key = "ldap.bind.attribute", description = "ldap user bind attribute (used for searching users on ldap directory)", defaultValue = "cn", category = CC_ENV)
    private String ldapBindAttribute;

    @ConfigValue(key = "ldap.search.filter", description = "ldap search filter (used in addition to bind attribute, to further refine user query", defaultValue = "", category = CC_ENV)
    private String ldapSearchFilter;

    @ConfigValue(key = "ldap.search.auth.method", description = "ldaph auth method (optional, one of 'none', 'simple' or 'strong')", defaultValue = "simple", category = CC_ENV)
    private String ldapAuthenticationMethod;

    @ConfigValue(key = "ldap.search.auth.principal", description = "ldap auth principal, such as uid=admin,ou=system", defaultValue = "", category = CC_ENV)
    private String ldapAuthenticationPrincipal;

    @ConfigValue(key = "ldap.search.auth.password", description = "ldap auth password (or other credentials)", defaultValue = "", category = CC_ENV)
    private String ldapAuthenticationPassword;

    @ConfigValue(key = "ldap.use.ssl", description = "", defaultValue = FALSE, category = CC_ENV)
    private boolean ldapUseSsl;

    @ConfigValue(key = "customlogin.enabled", description = "enable custom login (custom login request processing, to integrate with proprietary ssl/login frameworks)", defaultValue = FALSE)
    private boolean customLoginEnabled;

    @ConfigValue(key = "customlogin.handler", description = "custom login handler script (beanshell script, optionally encoded with base 64 or PACK)")
    private String customLoginHandlerScript;

    @ConfigValue(key = "customlogin.classpath", description = "custom login handler script classpath")
    private String customLoginHandlerScriptClasspath;

    @ConfigValue(key = "preferredPasswordAlgorythm", description = "preferred password algorythm, one of `legacy` (legacy algo), `cm3easy` (modern but symmetric encryption algo, AES), `cm3` (state-of-the-art, secure algo, PBKDF2)", defaultValue = "legacy")
    private PasswordAlgo preferredPasswordAlgorythm;

    @ConfigValue(key = "logoutRedirect", description = "logount redirect url (es: `http://my.sso/some/path`); if set, after logout browser will redirect here")
    private String logoutRedirectUrl;

    @Override
    @Nullable
    public String getLogoutRedirectUrl() {
        return logoutRedirectUrl;
    }

    public boolean isLdapConfigured() {
        return !(isBlank(getLdapBindAttribute()) || isBlank(getLdapBaseDN()) || isBlank(getLdapServerAddress()));
    }

    public boolean isHeaderConfigured() {
        return !(isBlank(getHeaderAttributeName()));
    }

    public boolean isCasConfigured() {
        return !(isBlank(getCasServerUrl()));
    }

    @Override
    public boolean isCaseInsensitive() {
        return authCaseInsensitive;
    }

    @Override
    public boolean getForceWSPasswordDigest() {
        return forceWsPasswordDigest;
    }

    @Override
    public String getHeaderAttributeName() {
        return headerAttributeName;
    }

    @Override
    public String getCasServerUrl() {
        return casServerUrl;
    }

    @Override
    public String getCasLoginPage() {
        return casLoginPage;
    }

    @Override
    public String getCasTicketParam() {
        return casTicketParam;
    }

    @Override
    public String getCasServiceParam() {
        return casServiceParam;
    }

    @Override
    public String getLdapUrl() {
        return String.format("%s://%s:%s", getLdapProtocol(), getLdapServerAddress(), getLdapServerPort());
    }

    private String getLdapServerAddress() {
        return ldapServerAddress;
    }

    private String getLdapServerPort() {
        return ldapServerPort;
    }

    private String getLdapProtocol() {
        return getLdapUseSsl() ? "ldaps" : "ldap";
    }

    private boolean getLdapUseSsl() {
        return ldapUseSsl;
    }

    @Override
    public String getLdapBaseDN() {
        return ldapBaseDn;
    }

    @Override
    public String getLdapBindAttribute() {
        return ldapBindAttribute;
    }

    @Override
    public String getLdapSearchFilter() {
        return ldapSearchFilter;
    }

    @Override
    public String getLdapAuthenticationMethod() {
        return ldapAuthenticationMethod;
    }

    @Override
    public String getLdapPrincipal() {
        return ldapAuthenticationPrincipal;
    }

    @Override
    public String getLdapPrincipalCredentials() {
        return ldapAuthenticationPassword;
    }

    @Override
    public Collection<String> getActiveAuthenticators() {
        List<String> list = list("rsa", "file");
        authMethods.forEach(a -> {
            if (a.startsWith("-")) {
                list.remove(a.replaceFirst("^-", ""));
            } else {
                list.add(a);
            }
        });
        return list;
    }

    @Override
    public boolean isCustomLoginEnabled() {
        return customLoginEnabled;
    }

    @Override
    public String getCustomLoginHandlerScript() {
        return customLoginHandlerScript;
    }

    @Override
    @Nullable
    public String getCustomLoginHandlerScriptClasspath() {
        return customLoginHandlerScriptClasspath;
    }

    @Override
    public PasswordAlgo getPreferredPasswordAlgorythm() {
        return checkNotNull(preferredPasswordAlgorythm);
    }

}
