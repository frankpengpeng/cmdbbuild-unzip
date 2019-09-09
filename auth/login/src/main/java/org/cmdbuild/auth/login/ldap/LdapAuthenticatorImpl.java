package org.cmdbuild.auth.login.ldap;

import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import static java.lang.String.format;
import static java.util.Collections.list;

import java.util.Hashtable;
import java.util.List;
import javax.annotation.Nullable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.auth.login.AuthenticationException;
import org.cmdbuild.auth.login.LoginUserIdentity;
import org.cmdbuild.auth.login.PasswordAuthenticator;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
public class LdapAuthenticatorImpl implements PasswordAuthenticator {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final LdapAuthenticatorConfiguration configuration;

    public LdapAuthenticatorImpl(LdapAuthenticatorConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String getName() {
        return "LdapAuthenticator";
    }

    @Override
    public boolean isPasswordValid(LoginUserIdentity login, String password) {
        try {
            logger.debug("trying to authenticate login = {} with ldap server = {}", login, configuration.getLdapUrl());
            String ldapUser = getLdapUserFullBindingName(login.getValue());
            if (isBlank(ldapUser)) {
                logger.debug("ldap user not found for login = {}", login);
                return false;
            } else {
                logger.debug("found ldap user =< {} > for login = {}", ldapUser, login);
                boolean isPasswordValid = isLdapUserPasswordValid(ldapUser, password);
                if (isPasswordValid) {
                    logger.debug("successfully validated ldap user credentials for login = {}", login);
                    return true;
                } else {
                    logger.debug("unable to validate ldap user credentials for login = {}", login);
                    return false;
                }
            }
        } catch (Exception e) {
            logger.warn("ldap authentication error for login = {}", login, e);
            return false;
        }
    }

    @Nullable
    private String getLdapUserFullBindingName(String login) throws NamingException {
        DirContext ctx = new InitialDirContext(new Hashtable<>(getBaseCtxEnvParams().with(map().skipNullValues().with(
                Context.SECURITY_AUTHENTICATION, emptyToNull(configuration.getLdapAuthenticationMethod()),
                Context.SECURITY_PRINCIPAL, emptyToNull(configuration.getLdapPrincipal()),
                Context.SECURITY_CREDENTIALS, emptyToNull(configuration.getLdapPrincipalCredentials())))));
        String searchFilter = null;
        try {
            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

            searchFilter = buildSearchFilterForUser(login);

            List<SearchResult> results = list(ctx.search(configuration.getLdapBaseDN(), searchFilter, searchControls));

            if (results.isEmpty()) {
                return null;
            } else {
                return getOnlyElement(results).getNameInNamespace();
            }
        } catch (Exception ex) {
            throw new AuthenticationException(ex, "error searching for user = %s with base ldn = %s and search filter = %s", login, configuration.getLdapBaseDN(), searchFilter);
        } finally {
            closeQuietly(ctx);
        }
    }

    private boolean isLdapUserPasswordValid(String ldapUserFullBindingName, String password) throws NamingException {
        DirContext ctx = new InitialDirContext(new Hashtable<>(getBaseCtxEnvParams().with(
                Context.SECURITY_AUTHENTICATION, "simple",
                Context.SECURITY_PRINCIPAL, ldapUserFullBindingName,
                Context.SECURITY_CREDENTIALS, password)));
        try {
            try {
                ctx.getAttributes("", null);
                return true;
            } catch (Exception e) {
                logger.debug("unable to validate ldap user psw for user = {}", ldapUserFullBindingName, e);
                return false;
            }
        } finally {
            closeQuietly(ctx);
        }
    }

    private String buildSearchFilterForUser(String login) {
        String bindAttr = checkNotBlank(configuration.getLdapBindAttribute(), "ldap bind attribute is null");
        if (isNotBlank(configuration.getLdapSearchFilter())) {
            return format("(&%s(%s=%s))", configuration.getLdapSearchFilter(), bindAttr, login);
        } else {
            return format("(%s=%s)", bindAttr, login);
        }
    }

    private FluentMap<String, String> getBaseCtxEnvParams() {
        return map(System.getProperties()).with(
                Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory",
                Context.PROVIDER_URL, checkNotBlank(configuration.getLdapUrl(), "missing ldap url config"),
                Context.REFERRAL, "follow");
    }

    private void closeQuietly(DirContext ctx) {
        try {
            ctx.close();
        } catch (Exception ex) {
            logger.debug("error closing ldap context", ex);
        }
    }

}
