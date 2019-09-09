package org.cmdbuild.auth.login;

import static com.google.common.base.Preconditions.checkArgument;
import org.cmdbuild.auth.user.UnencryptedPasswordSupplier;
import org.cmdbuild.auth.user.UserRepository;
import org.cmdbuild.auth.role.RoleRepository;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.isNull;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Maps.uniqueIndex;

import java.util.Collection;
import java.util.List;

import org.cmdbuild.auth.user.OperationUser;

import static java.util.Collections.emptyList;
import java.util.Map;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.auth.config.AuthenticationServiceConfiguration;
import org.cmdbuild.auth.grant.UserPrivileges;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.auth.role.Role;
import org.cmdbuild.auth.role.RoleInfo;
import org.cmdbuild.auth.user.LoginUser;
import static org.cmdbuild.auth.login.GodUserUtils.getGodDummyGroup;
import static org.cmdbuild.auth.login.GodUserUtils.getGodLoginUser;
import static org.cmdbuild.auth.login.GodUserUtils.getGodPrivilegeContext;
import static org.cmdbuild.auth.login.GodUserUtils.isGodDummyGroup;
import static org.cmdbuild.auth.login.GodUserUtils.isGodUser;
import org.cmdbuild.auth.multitenant.api.MultitenantService;
import org.cmdbuild.auth.multitenant.api.UserTenantContext;
import static org.cmdbuild.auth.role.RolePrivilege.RP_DATA_ALL_TENANT;
import org.cmdbuild.auth.user.LoginUserImpl;
import static org.cmdbuild.auth.user.OperationUserImpl.builder;
import org.cmdbuild.auth.user.UserPrivilegesImpl;
import static org.cmdbuild.common.error.ErrorAndWarningCollectorService.marker;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@Component
public class AuthenticationServiceImpl implements AuthenticationService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final RoleRepository groupRepository;
    private final MultitenantService multitenantService;
    private final Map<String, PasswordAuthenticator> passwordAuthenticatorsByName;
    private final Map<String, ClientRequestAuthenticator> clientRequestAuthenticatorsByName;
    private final UserRepository userRepository;
    private final AuthenticationServiceConfiguration conf;
    private final UnencryptedPasswordSupplier passwordService;

    public AuthenticationServiceImpl(UnencryptedPasswordSupplier passwordService, RoleRepository groupRepository, MultitenantService multitenantService, List<PasswordAuthenticator> passwordAuthenticators, List<ClientRequestAuthenticator> clientRequestAuthenticators, UserRepository userRepository, AuthenticationServiceConfiguration conf) {
        this.groupRepository = checkNotNull(groupRepository);
        this.multitenantService = checkNotNull(multitenantService);
        this.passwordAuthenticatorsByName = uniqueIndex(passwordAuthenticators, PasswordAuthenticator::getName);
        this.clientRequestAuthenticatorsByName = uniqueIndex(clientRequestAuthenticators, ClientRequestAuthenticator::getName);
        this.userRepository = checkNotNull(userRepository);
        this.conf = checkNotNull(conf);
        this.passwordService = checkNotNull(passwordService);
    }

    @Override
    public LoginUser checkPasswordAndGetUser(LoginUserIdentity login, String password) {
        for (PasswordAuthenticator passwordAuthenticator : getActivePasswordAuthenticators()) {
            try {
                logger.debug("try to validate password for user = {} with authenticator = {}", login, passwordAuthenticator.getName());
                boolean isUserAuthenticated = passwordAuthenticator.isPasswordValid(login, password);
                if (isUserAuthenticated) {
                    logger.debug("successfully authenticated user = {} with authenticator = {}", login, passwordAuthenticator.getName());
                    return getUser(login);
                }
            } catch (Exception ex) {
                logger.error(marker(), "authentication error for user = {}", login, ex);
            }
        }
        throw new AuthenticationException("invalid login credentials or authentication error for user = %s", login);
    }

    @Override
    public ClientAuthenticatorResponse validateCredentialsAndCreateAuthResponse(AuthRequestInfo request) {
        for (ClientRequestAuthenticator clientRequestAuthenticator : getActiveClientRequestAuthenticators()) {
            logger.debug("try to validate request with authenticator = {}", clientRequestAuthenticator.getName());
            RequesthAuthenticatorResponse response = clientRequestAuthenticator.authenticate(request);
            if (response != null) {
                LoginUser authUser = null;
                if (response.getLogin() != null) {
                    authUser = getUserOrNull(response.getLogin());
                }
                return new ClientAuthenticatorResponseImpl(authUser, response.getRedirectUrl());
            }
        }
        return new ClientAuthenticatorResponseImpl(null, null);
    }

    @Nullable
    @Override
    public LoginUser getUserOrNull(LoginUserIdentity identity) {
        if (isGodUser(identity)) {
            return getGodLoginUser();
        } else {
            return userRepository.getUserOrNull(identity);
        }
    }

    @Override
    @Nullable
    public String getUnencryptedPasswordOrNull(LoginUserIdentity login) {
        return passwordService.getUnencryptedPasswordOrNull(login);
    }

    @Override
    @Nullable
    public LoginUser getUserByIdOrNull(Long userId) {
        return userRepository.getUserByIdOrNull(userId);
    }

    @Override
    public Collection<Role> getAllGroups() {
        return groupRepository.getAllGroups();
    }

    @Override
    public Role fetchGroupWithId(Long groupId) {
        return groupRepository.getByIdOrNull(groupId);
    }

    @Override
    public Role getGroupWithNameOrNull(String groupName) {
        return groupRepository.getGroupWithNameOrNull(groupName);
    }

    @Override
    public OperationUser validateCredentialsAndCreateOperationUser(LoginData loginData) {
        logger.debug("try to login user = {} with group = {} and full info = {}", loginData.getLoginString(), loginData.getLoginGroupName(), loginData);
        LoginUser authUser;
        LoginUserIdentity identity = LoginUserIdentity.build(loginData.getLoginString());
        if (loginData.isPasswordRequired()) {
            authUser = checkPasswordAndGetUser(identity, loginData.getPassword());
        } else {
            authUser = getUser(identity);
        }

        if (!loginData.isServiceUsersAllowed() && authUser.isService()) {
            logger.warn("login not allowed for user = %s: user is service and service user login is not allowed", authUser.getUsername());
            throw new AuthenticationException("login failed");
        }

        return buildOperationUser(loginData, authUser);
    }

    @Override
    public OperationUser updateOperationUser(LoginData loginData, OperationUser operationUser) {
        return buildOperationUser(loginData, operationUser.getLoginUser());
    }

    @Override
    public RoleInfo getGroupInfoForGroup(String groupName) {
        return groupRepository.getGroupWithName(groupName);
    }

    @Override
    public Collection<String> getGroupNamesForUserWithId(Long userId) {
        LoginUser user = getUserByIdOrNull(userId);
        return user == null ? emptyList() : user.getGroupNames();
    }

    @Override
    public Collection<String> getGroupNamesForUserWithUsername(String loginString) {
        LoginUser user = getUserByUsernameOrNull(loginString);
        return user == null ? emptyList() : user.getGroupNames();
    }

    @Override
    public LoginUser getUserWithId(Long userId) {
        return getUserByIdOrNull(userId);
    }

    @Override
    public Role getGroupWithId(Long groupId) {
        return fetchGroupWithId(groupId);
    }

    @Override
    public Role getGroupWithName(String groupName) {
        return checkNotNull(getGroupWithNameOrNull(groupName), "group not found for name = %s", groupName);
    }

    private List<PasswordAuthenticator> getActivePasswordAuthenticators() {
        return conf.getActiveAuthenticators().stream().map(passwordAuthenticatorsByName::get).filter(not(isNull())).collect(toImmutableList());
    }

    private List<ClientRequestAuthenticator> getActiveClientRequestAuthenticators() {
        return conf.getActiveAuthenticators().stream().map(clientRequestAuthenticatorsByName::get).filter(not(isNull())).collect(toImmutableList());
    }

    private OperationUser buildOperationUser(LoginData loginData, LoginUser loginUser) {
        String groupName = loginData.getLoginGroupName();
        UserPrivileges privilegeCtx;
        Role selectedGroup;
        if (isGodUser(loginUser)) {
            Role godRole = getGodDummyGroup();
            List<RoleInfo> groups = list(godRole);
            if (isNotBlank(groupName) && !isGodDummyGroup(groupName)) {
                selectedGroup = groupRepository.getGroupWithName(groupName);
                groups.add(selectedGroup);
            } else {
                selectedGroup = godRole;
            }
            privilegeCtx = getGodPrivilegeContext();
            loginUser = LoginUserImpl.copyOf(loginUser)
                    .withGroups(groups)
                    .withAvailableTenantContext(multitenantService.getAdminAvailableTenantContext()).build();
        } else {
            if (isNotBlank(groupName)) {
                checkArgument(loginUser.getGroupNames().contains(groupName), "user has not group = %s", groupName);
                selectedGroup = groupRepository.getGroupWithName(groupName);
            } else {
                Role guessedGroup = guessPreferredGroup(loginUser);
                if (guessedGroup == null) {
                    logger.debug("created not-valid session (user = {} does not have a default group and belongs to multiple groups)", loginUser.getUsername());
                    return builder().withAuthenticatedUser(loginUser).withUserTenantContext(multitenantService.buildUserTenantContext(loginUser, loginData)).build();
                } else {
                    selectedGroup = guessedGroup;
                }
            }
            if (loginUser.hasMultigroupEnabled() && loginUser.getGroupNames().size() > 1) {
                privilegeCtx = buildPrivilegeContext(list(transform(loginUser.getGroupNames(), groupRepository::getGroupWithName)));
            } else {
                privilegeCtx = buildPrivilegeContext(selectedGroup);
            }
            if (privilegeCtx.hasPrivileges(RP_DATA_ALL_TENANT)) {
                loginUser = LoginUserImpl.copyOf(loginUser).withAvailableTenantContext(multitenantService.getAdminAvailableTenantContext()).build();
            }
        }
        UserTenantContext userTenantContext = multitenantService.buildUserTenantContext(loginUser, loginData);
        return builder().withAuthenticatedUser(loginUser).withPrivilegeContext(privilegeCtx).withDefaultGroup(selectedGroup).withUserTenantContext(userTenantContext).build();
    }

    private UserPrivileges buildPrivilegeContext(Role... groups) {
        return UserPrivilegesImpl.builder().withGroups(groups).build();
    }

    private UserPrivileges buildPrivilegeContext(Iterable<Role> groups) {
        return UserPrivilegesImpl.builder().withGroups(groups).build();
    }

    /**
     * Gets the default group (if any) or the only one. If no default group has
     * been found and more than one group is present, {@code null} is returned.
     */
    @Nullable
    private Role guessPreferredGroup(LoginUser user) {
        if (user.hasDefaultGroup()) {
            return groupRepository.getGroupWithName(user.getDefaultGroupName());
        } else if (user.getGroupNames().size() == 1) {
            return groupRepository.getGroupWithName(getOnlyElement(user.getGroupNames()));
        } else {
            return null;
        }
    }

    private static class ClientAuthenticatorResponseImpl implements ClientAuthenticatorResponse {

        final LoginUser user;
        final String redirectUrl;

        public ClientAuthenticatorResponseImpl(@Nullable LoginUser user, @Nullable String redirectUrl) {
            this.user = user;
            this.redirectUrl = redirectUrl;
        }

        @Nullable
        @Override
        public LoginUser getUserOrNull() {
            return user;
        }

        @Nullable
        @Override
        public String getRedirectUrlOrNull() {
            return redirectUrl;
        }
    }

}
