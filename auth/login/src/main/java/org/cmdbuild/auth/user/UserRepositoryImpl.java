package org.cmdbuild.auth.user;

import org.cmdbuild.auth.role.RoleRepository;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.MoreCollectors.toOptional;
import static java.lang.Math.toIntExact;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.auth.multitenant.api.MultitenantService;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;

import org.cmdbuild.utils.crypto.CmLegacyPasswordUtils;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import org.cmdbuild.auth.config.UserRepositoryConfig;
import org.cmdbuild.auth.login.LoginUserIdentity;
import org.cmdbuild.auth.login.PasswordAuthenticator;
import org.cmdbuild.auth.multitenant.UserAvailableTenantContextImpl;
import org.cmdbuild.auth.multitenant.api.UserAvailableTenantContext;
import org.cmdbuild.auth.multitenant.api.UserAvailableTenantContext.TenantActivationPrivileges;
import static org.cmdbuild.dao.core.q3.WhereOperator.EQ_CASE_INSENSITIVE;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.common.utils.PagedElements.isPaged;
import static org.cmdbuild.common.utils.PagedElements.paged;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;
import org.cmdbuild.auth.role.Role;
import org.cmdbuild.auth.userrole.UserRole;
import org.cmdbuild.config.CoreConfiguration;
import static org.cmdbuild.userconfig.UserConfigConst.USER_CONFIG_KEY_MULTIGROUP;
import static org.cmdbuild.userconfig.UserConfigConst.USER_CONFIG_MULTITENANT_ACTIVATION_PRIVILEGES;
import org.cmdbuild.userconfig.UserConfigService;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.springframework.context.annotation.Primary;

@Component
@Primary
public class UserRepositoryImpl implements UserRepository, PasswordAuthenticator, UnencryptedPasswordSupplier {

    private final UserRepositoryConfig configuration;
    private final CoreConfiguration coreConfig;
    private final DaoService dao;
    private final MultitenantService multitenantService;
    private final RoleRepository groupRepository;
    private final UserConfigService userConfigService;
    private final UserPasswordService passwordService;

    public UserRepositoryImpl(UserRepositoryConfig configuration, CoreConfiguration coreConfig, DaoService dao, MultitenantService multitenantService, RoleRepository groupRepository, UserConfigService userConfigService, UserPasswordService passwordService) {
        this.configuration = checkNotNull(configuration);
        this.coreConfig = checkNotNull(coreConfig);
        this.dao = checkNotNull(dao);
        this.multitenantService = checkNotNull(multitenantService);
        this.groupRepository = checkNotNull(groupRepository);
        this.userConfigService = checkNotNull(userConfigService);
        this.passwordService = checkNotNull(passwordService);
    }

    @Override
    public LoginUser getUserOrNull(LoginUserIdentity login) {
        UserData userCard = getUserDataOrNull(login);
        return userCard == null ? null : buildUserFromCard(userCard);
    }

    @Override
    public LoginUser getUserByIdOrNull(Long userId) {
        UserData user = dao.selectAll().from(UserData.class).where(ATTR_ID, EQ, userId).getOne();
        return buildUserFromCard(user);
    }

    @Override
    public PagedElements<UserData> getMany(CmdbFilter filter, CmdbSorter sorter, @Nullable Long offset, @Nullable Long limit) {
        List<UserData> list = dao.selectAll().from(UserData.class).where(filter).orderBy(sorter).paginate(offset, limit).asList();
        if (isPaged(offset, limit)) {
            long count = dao.selectCount().from(UserData.class).where(filter).getCount();
            return paged(list, toIntExact(count));//TODO change count to long
        } else {
            return paged(list);
        }
    }

    @Override
    public PagedElements<UserData> getAllWithoutRole(long roleId, CmdbFilter filter, CmdbSorter sorter, @Nullable Long offset, @Nullable Long limit) {
        return getAllWithRole(roleId, filter, sorter, offset, limit, false);
    }

    @Override
    public PagedElements<UserData> getAllWithRole(long roleId, CmdbFilter filter, CmdbSorter sorter, @Nullable Long offset, @Nullable Long limit) {
        return getAllWithRole(roleId, filter, sorter, offset, limit, true);
    }

    @Override
    public UserData get(long id) {
        return dao.getById(UserData.class, id).toModel();
    }

    @Override
    public UserData create(UserData user) {
        return dao.create(user);
    }

    @Override
    public UserData update(UserData user) {
        if (isBlank(user.getPassword())) {
            UserData current = get(user.getId());
            user = UserDataImpl.copyOf(user)
                    .withPassword(current.getPassword())
                    .build();
        }
        return dao.update(user);
    }

    private PagedElements<UserData> getAllWithRole(long roleId, CmdbFilter filter, CmdbSorter sorter, @Nullable Long offset, @Nullable Long limit, boolean assigned) {
        String query = "EXISTS (SELECT * FROM \"Map_UserRole\" _mur WHERE _mur.\"IdObj1\" = _user.\"Id\" AND _mur.\"IdObj2\" = ? AND _mur.\"Status\" = 'A')";
        if (assigned == false) {
            query = "NOT " + query;
        }
        List<UserData> list = dao.selectAll().from(UserData.class).where(filter)
                .whereExpr(query, roleId)
                .orderBy(sorter).paginate(offset, limit).asList();
        if (isPaged(offset, limit)) {
            long count = dao.selectCount().from(UserData.class).where(filter)
                    .whereExpr(query, roleId)
                    .getCount();
            return paged(list, toIntExact(count));//TODO change count to long
        } else {
            return paged(list);
        }
    }

    private LoginUser buildUserFromCard(UserData user) {
        List<UserRole> groups = groupRepository.getUserGroups(user.getId());
        String defaultGroupName = groups.stream().filter(UserRole::isDefault).collect(toOptional()).map(UserRole::getRole).map(Role::getName).orElse(null);
        Map<String, String> userConfig = userConfigService.getByUsername(user.getUsername());
        UserAvailableTenantContext userAvailableTenantContext = multitenantService.getAvailableTenantContextForUser(user.getId());
        if (isNotBlank(userConfig.get(USER_CONFIG_MULTITENANT_ACTIVATION_PRIVILEGES))) {
            userAvailableTenantContext = UserAvailableTenantContextImpl.copyOf(userAvailableTenantContext).withTenantActivationPrivileges(parseEnum(userConfig.get(USER_CONFIG_MULTITENANT_ACTIVATION_PRIVILEGES), TenantActivationPrivileges.class)).build();
        }
        return LoginUserImpl.builder()
                .withId(user.getId())
                .withUsername(user.getUsername())
                .withEmail(defaultString(user.getEmail()))
                .withDescription(defaultString(user.getDescription()))
                .withDefaultGroupName(defaultGroupName)
                .withActiveStatus(user.isActive())
                .withServiceStatus(user.isService())
                .withpasswordExpirationTimestamp(user.getPasswordExpiration())
                .withLastPasswordChange(user.getLastPasswordChange())
                .withLastExpiringNotification(user.getLastExpiringNotification())
                .withMultigroupEnabled(toBooleanOrDefault(userConfig.get(USER_CONFIG_KEY_MULTIGROUP), coreConfig.enableMultigrupByDefault()))
                .withAvailableTenantContext(userAvailableTenantContext)
                .accept(b -> {
                    groups.stream().map(UserRole::getRole).forEach(b::addGroup);
                })
                .build();
    }

    @Nullable
    private UserData getUserDataOrNull(LoginUserIdentity login) throws NoSuchElementException {
        String attribute;
        switch (login.getType()) {
            case EMAIL:
                attribute = "Email";
                break;
            case USERNAME:
                attribute = "Username";
                break;
            default:
                throw unsupported("unsupported login type = %s", login.getType());
        }
        return dao.selectAll().from(UserData.class)
                .where("Active", EQ, true)
                .where(attribute, configuration.isCaseInsensitive() ? EQ_CASE_INSENSITIVE : EQ, login.getValue())
                .getOneOrNull();
    }

    private UserData getUserData(LoginUserIdentity login) {
        return checkNotNull(getUserDataOrNull(login), "user not found for login = %s", login);
    }

    @Override
    public String getName() {
        return "DBAuthenticator";
    }

    @Override
    public boolean isPasswordValid(LoginUserIdentity login, String password) {
        if (isBlank(password)) {
            return false;
        } else {
            UserData userData = getUserDataOrNull(login);
            if (userData == null) {
                return false;
            } else {
                return passwordService.verifyPassword(password, userData.getPassword());
            }
        }
    }

    @Override
    @Nullable
    public String getUnencryptedPasswordOrNull(LoginUserIdentity login) {
        return passwordService.decryptPasswordIfPossible(getUserData(login).getPassword());
    }

}
