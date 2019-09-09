package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableMap;
import static com.google.common.collect.MoreCollectors.toOptional;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static java.util.stream.Collectors.toList;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.cmdbuild.auth.login.AuthorityConst.HAS_SYSTEM_ACCESS_AUTHORITY;
import org.cmdbuild.auth.userrole.UserRole;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.auth.user.UserData;
import org.cmdbuild.auth.user.UserDataImpl;
import org.cmdbuild.auth.user.UserRepository;
import org.cmdbuild.auth.multitenant.api.MultitenantService;
import org.cmdbuild.auth.multitenant.api.UserAvailableTenantContext;
import org.cmdbuild.auth.multitenant.api.UserAvailableTenantContext.TenantActivationPrivileges;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.auth.session.model.Session;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;

import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.SORT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.utils.CmdbFilterUtils;
import org.cmdbuild.data.filter.utils.CmdbSorterUtils;
import org.cmdbuild.data.filter.CmdbSorter;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DETAILED;
import static org.cmdbuild.userconfig.UserConfigConst.USER_CONFIG_KEY_MULTIGROUP;
import static org.cmdbuild.userconfig.UserConfigConst.USER_CONFIG_MULTITENANT_ACTIVATION_PRIVILEGES;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.cmdbuild.userconfig.UserConfigService;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import org.springframework.security.access.prepost.PreAuthorize;

@Path("users/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class UserWs {

    private final static Map<String, String> USER_TABLE_ATTR_NAME_MAPPING = ImmutableMap.of(
            "username", "Username",
            "description", ATTR_DESCRIPTION,
            "email", "Email",
            "active", "Active"
    );

    private final UserRepository repository;
    private final MultitenantService multitenantService;
    private final RoleRepository groupRepository;
    private final UserConfigService userPreferencesStore;
    private final SessionService sessionService;

    public UserWs(SessionService sessionService, UserRepository repository, MultitenantService multitenantService, RoleRepository groupRepository, UserConfigService userPreferencesStore) {
        this.repository = checkNotNull(repository);
        this.multitenantService = checkNotNull(multitenantService);
        this.groupRepository = checkNotNull(groupRepository);
        this.userPreferencesStore = checkNotNull(userPreferencesStore);
        this.sessionService = checkNotNull(sessionService);
    }

    @GET
    @Path(EMPTY)
    @PreAuthorize(HAS_SYSTEM_ACCESS_AUTHORITY)
    public Object readMany(@QueryParam(FILTER) String filterStr, @QueryParam(SORT) String sort, @QueryParam(LIMIT) Long limit, @QueryParam(START) Long offset, @QueryParam(DETAILED) Boolean detailed) {
        CmdbFilter filter = CmdbFilterUtils.parseFilter(filterStr);
        CmdbSorter sorter = CmdbSorterUtils.parseSorter(sort).mapAttributeNames(USER_TABLE_ATTR_NAME_MAPPING);
        return response(repository.getMany(filter, sorter, offset, limit).map(defaultIfNull(detailed, false) ? this::serializeDetailedUser : this::serializeUser));
    }

    @GET
    @Path("{userId}/")
    @PreAuthorize(HAS_SYSTEM_ACCESS_AUTHORITY)
    public Object readOne(@PathParam("userId") Long id) {
        UserData user = repository.get(id);
        return response(serializeDetailedUser(user));
    }

    @POST
    @Path(EMPTY)
    @PreAuthorize(HAS_SYSTEM_ACCESS_AUTHORITY)
    public Object create(WsUserData data) {
        UserData user = data.toUserData().build();
        user = repository.create(user);
        updatePrefs(user.getUsername(), data);
        updateRoles(user, data);
        updateTenants(user, data);
        user = repository.get(user.getId());
        return response(serializeDetailedUser(user));
    }

    @PUT
    @Path("{userId}/")
    @PreAuthorize(HAS_SYSTEM_ACCESS_AUTHORITY)
    public Object update(@PathParam("userId") Long id, WsUserData data) {
        UserData user = data.toUserData().withId(id).build();
        user = repository.update(user);
        updatePrefs(user.getUsername(), data);
        updateRoles(user, data);
        updateTenants(user, data);
        user = repository.get(id);
        return response(serializeDetailedUser(user));
    }

    @PUT
    @Path("current/password")
    public Object changePassword(WsUserPswData data) {
        Session session = sessionService.getCurrentSession();
        UserData user = repository.get(session.getOperationUser().getId());
        //TODO validate new password (len, characters, etc
        repository.update(UserDataImpl.copyOf(user).withClearPassword(data.getPassword()).build());
        return success();
    }

    private void updatePrefs(String username, WsUserData data) {
        userPreferencesStore.setByUsernameDeleteIfNull(username, "cm_user_initialPage", trimToNull(data.initialPage));
        userPreferencesStore.setByUsernameDeleteIfNull(username, USER_CONFIG_KEY_MULTIGROUP, toStringOrNull(data.multiGroup));
        userPreferencesStore.setByUsernameDeleteIfNull(username, USER_CONFIG_MULTITENANT_ACTIVATION_PRIVILEGES, serializeEnum(data.multiTenantActivationPrivileges));
    }

    private void updateRoles(UserData user, WsUserData data) {
        groupRepository.setUserGroups(user.getId(), data.userGroups.stream().map(WsRoleOrTenantData::getId).collect(toList()), data.defaultRole);
    }

    private void updateTenants(UserData user, WsUserData data) {
        if (multitenantService.isEnabled() && multitenantService.isUserTenantUpdateEnabled()) {
            multitenantService.setUserTenants(user.getId(), data.userTenants.stream().map(WsRoleOrTenantData::getId).collect(toList()));
        }
    }

    public static FluentMap<String, Object> serializeMinimalUser(UserData user) {
        return map(
                "_id", user.getId(),
                "username", user.getUsername(),
                "description", user.getDescription()
        );
    }

    private FluentMap<String, Object> serializeUser(UserData user) {
        return serializeMinimalUser(user).with(
                "email", user.getEmail(),
                "active", user.isActive(),
                "service", user.isService(),
                "passwordExpiration", toIsoDateTime(user.getPasswordExpiration()),
                "lastPasswordChange", toIsoDateTime(user.getLastPasswordChange()),
                "lastExpiringNotification", toIsoDateTime(user.getLastExpiringNotification())
        );
    }

    private Object serializeDetailedUser(UserData user) {
        UserAvailableTenantContext tenantContext = multitenantService.getAvailableTenantContextForUser(user.getId());
        List<UserRole> userGroups = groupRepository.getUserGroups(user.getId());
        List roles = userGroups.stream().map(UserRole::getRole).map((r) -> map(
                "_id", r.getId(),
                "name", r.getName(),
                "description", r.getDescription(),
                "_description_translation", r.getDescription()//TODO
        )).collect(toList());
        UserRole defaultGroup = userGroups.stream().filter(UserRole::isDefault).collect(toOptional()).orElse(null);
        List tenants = multitenantService.getAvailableUserTenants(tenantContext).stream().map((t) -> map(
                "_id", t.getId(),
                "name", t.getDescription(),
                "description", t.getDescription(),
                "_description_translation", t.getDescription()//TODO
        )).collect(toList());
        Map<String, String> prefs = userPreferencesStore.getByUsername(user.getUsername());
        return serializeUser(user).with(
                "userTenants", tenants,
                "defaultUserTenant", tenantContext.getDefaultTenantId(),
                "userGroups", roles,
                "defaultUserGroup", Optional.ofNullable(defaultGroup).map(UserRole::getId).orElse(null),
                "_defaultUserGroup_description", Optional.ofNullable(defaultGroup).map(UserRole::getDescription).orElse(null),
                "language", prefs.get("cm_user_language"),
                "initialPage", prefs.get("cm_user_initialPage"),
                "multiGroup", toBooleanOrDefault(prefs.get(USER_CONFIG_KEY_MULTIGROUP), false),
                "multiTenantActivationPrivileges", prefs.get(USER_CONFIG_MULTITENANT_ACTIVATION_PRIVILEGES));
    }

    public static class WsRoleOrTenantData {

        private final long id;

        public WsRoleOrTenantData(@JsonProperty("_id") Long id) {
            this.id = id;
        }

        public long getId() {
            return id;
        }

    }

    public static class WsUserPswData {

        private final String password;

        public WsUserPswData(@JsonProperty("password") String password) {
            this.password = checkNotBlank(password, "missing 'password' param");
        }

        public String getPassword() {
            return password;
        }

    }

    public static class WsUserData {

        private final Long id, defaultRole;
        private final String username, description, email, password, initialPage;
        private final ZonedDateTime passwordExpiration, lastPasswordChange, lastExpiringNotification;
        private final Boolean isActive, isService, multiTenant, multiGroup;
        private final List<WsRoleOrTenantData> userTenants, userGroups;
        private final TenantActivationPrivileges multiTenantActivationPrivileges;

        public WsUserData(@JsonProperty("_id") Long id,
                @JsonProperty("username") String username,
                @JsonProperty("description") String description,
                @JsonProperty("email") String email,
                @JsonProperty("password") String password,
                @JsonProperty("initialPage") String initialPage,
                @JsonProperty("passwordExpiration") ZonedDateTime passwordExpiration,
                @JsonProperty("lastPasswordChange") ZonedDateTime lastPasswordChange,
                @JsonProperty("lastExpiringNotification") ZonedDateTime lastExpiringNotification,
                @JsonProperty("active") Boolean isActive,
                @JsonProperty("service") Boolean isService,
                @JsonProperty("multiGroup") Boolean multiGroup,
                @JsonProperty("multiTenant") Boolean multiTenant,
                @JsonProperty("multiTenantActivationPrivileges") String multiTenantActivationPrivileges,
                @JsonProperty("defaultUserGroup") Long defaultRole,
                @JsonProperty("userTenants") List<WsRoleOrTenantData> userTenants,
                @JsonProperty("userGroups") List<WsRoleOrTenantData> userGroups) {
            this.id = id;
            this.username = checkNotBlank(username, "'username' is null");
            this.password = password;
            this.description = description;
            this.email = email;
            this.passwordExpiration = passwordExpiration;
            this.lastPasswordChange = lastPasswordChange;
            this.lastExpiringNotification = lastExpiringNotification;
            this.isActive = isActive;
            this.isService = isService;
            this.initialPage = initialPage;
            this.multiGroup = multiGroup;
            this.multiTenant = multiTenant;
            this.userTenants = userTenants;
            this.userGroups = checkNotNull(userGroups, "'userGroups' is null");
            this.defaultRole = defaultRole;
            this.multiTenantActivationPrivileges = parseEnumOrNull(multiTenantActivationPrivileges, TenantActivationPrivileges.class);
        }

        private UserDataImpl.UserDataImplBuilder toUserData() {
            return UserDataImpl.builder()
                    .withId(id)
                    .withUsername(username)
                    .withDescription(description)
                    .withEmail(email)
                    .withClearPassword(password)
                    .withPasswordExpiration(passwordExpiration)
                    .withLastPasswordChange(lastPasswordChange)
                    .withLastExpiringNotification(lastExpiringNotification)
                    .withActive(isActive)
                    .withService(isService);
        }

    }

}
