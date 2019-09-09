package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.trimToNull;

import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.auth.role.GroupConfig;
import org.cmdbuild.auth.role.GroupConfigImpl;
import org.cmdbuild.auth.role.Role;
import org.cmdbuild.auth.role.RoleImpl;
import org.cmdbuild.auth.user.UserData;
import org.cmdbuild.auth.user.UserRepository;
import org.cmdbuild.auth.role.RoleType;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.common.utils.PagedElements.paged;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;
import org.cmdbuild.data.filter.utils.CmdbFilterUtils;
import org.cmdbuild.data.filter.utils.CmdbSorterUtils;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.SORT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import org.cmdbuild.utils.json.CmJsonUtils;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_OBJECTS;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBoolean;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import static org.cmdbuild.auth.login.AuthorityConst.HAS_ADMIN_ACCESS_AUTHORITY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import org.cmdbuild.auth.userrole.UserRoleRepository;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;

@Path("roles/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class RoleWs {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleService;

    public RoleWs(UserRepository userRepository, RoleRepository roleRepository, UserRoleRepository userRoleService) {
        this.userRepository = checkNotNull(userRepository);
        this.roleRepository = checkNotNull(roleRepository);
        this.userRoleService = checkNotNull(userRoleService);
    }

    @GET
    @Path("{roleId}/")
    public Object readOne(@PathParam("roleId") String roleId) {
        Role role = roleRepository.getByNameOrId(roleId);
        return response(serializeDetailedRole(role));
    }

    @GET
    @Path(EMPTY)
    public Object readMany(@QueryParam(LIMIT) Long limit, @QueryParam(START) Long offset, @QueryParam("detailed") @DefaultValue(FALSE) boolean detailed) {
        List<Role> groups = roleRepository.getAllGroups();
        return response(paged(groups, offset, limit).map(detailed ? this::serializeDetailedRole : RoleWs::serializeBasicRole));
    }

    @GET
    @Path("{roleId}/users")
    public Object readRoleUsers(@PathParam("roleId") String roleId, @QueryParam(FILTER) String filterStr, @QueryParam(SORT) String sort, @QueryParam(LIMIT) Long limit, @QueryParam(START) Long offset, @QueryParam("assigned") Boolean assigned) {
        CmdbFilter filter = CmdbFilterUtils.parseFilter(filterStr);
        CmdbSorter sorter = CmdbSorterUtils.parseSorter(sort);
        Role role = roleRepository.getByNameOrId(roleId);
        PagedElements<UserData> users;
        if (firstNotNull(assigned, true) == true) {
            users = userRepository.getAllWithRole(role.getId(), filter, sorter, offset, limit);
        } else {
            users = userRepository.getAllWithoutRole(role.getId(), filter, sorter, offset, limit);
        }
        return response(users.stream().map(UserWs::serializeMinimalUser), users.totalSize());
    }

    @Deprecated//TODO move to POST
    @PUT
    @Path("{roleId}/users")
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object updateUsersPut(@PathParam("roleId") String roleId, WsRoleUsers users) {
        return updateUsersPost(roleId, users);
    }

    @POST
    @Path("{roleId}/users")
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object updateUsersPost(@PathParam("roleId") String roleId, WsRoleUsers users) {
        Role role = roleRepository.getByNameOrId(roleId);
        users.usersToAdd.forEach((userId) -> userRoleService.addRoleToUser(userId, role.getId()));
        users.usersToRemove.forEach((userId) -> userRoleService.removeRoleFromUser(userId, role.getId()));
        return success();
    }

    @POST
    @Path(EMPTY)
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object create(String jsonData) {
        Role role = toRole(jsonData).build();
        role = roleRepository.create(role);
        return response(serializeDetailedRole(role));
    }

    @PUT
    @Path("{roleId}/")
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object update(@PathParam("roleId") String roleId, String jsonData) {
        Role role = roleRepository.getByNameOrId(roleId);
        role = toRole(jsonData).withId(role.getId()).build();
        role = roleRepository.update(role);
        return response(serializeDetailedRole(role));
    }

    public static FluentMap<String, Object> serializeBasicRole(Role role) {
        return map(
                "_id", role.getId(),
                "type", role.getType().name().toLowerCase(),
                "name", role.getName(),
                "description", role.getDescription(),
                "email", role.getEmail(),
                "active", role.isActive());
    }

    private Object serializeDetailedRole(Role role) {
        GroupConfig config = role.getConfig();
        return serializeBasicRole(role).with(
                "processWidgetAlwaysEnabled", config.getProcessWidgetAlwaysEnabled(),
                "startingClass", config.getStartingClass()
        ).accept((m) -> role.getRolePrivilegesAsMap().forEach((k, v) -> m.put(format("_%s", k.name().toLowerCase()), v)));
    }

    private RoleImpl.RoleImplBuilder toRole(String jsonData) {
        WsRoleData data = fromJson(jsonData, WsRoleData.class);
        Map<String, Boolean> customPermissions = CmJsonUtils.<Map<String, Object>>fromJson(jsonData, MAP_OF_OBJECTS).entrySet().stream()
                .filter((e) -> e.getKey().startsWith("_rp_"))
                .collect(toMap((e) -> e.getKey().replaceFirst("^_rp_", ""), (e) -> toBoolean(e.getValue())));
        return data.toRole().withCustomPrivileges(customPermissions);
    }

    public static class WsRoleUsers {

        private final List<Long> usersToAdd, usersToRemove;

        public WsRoleUsers(@JsonProperty("add") List<Long> usersToAdd, @JsonProperty("remove") List<Long> usersToRemove) {
            this.usersToAdd = checkNotNull(usersToAdd);
            this.usersToRemove = checkNotNull(usersToRemove);
        }

    }

    public static class WsRoleData {

        private final Long id;
        private final String name, description, email, startingClass;
        private final boolean isActive;
        private final RoleType type;
        private final Boolean processWidgetAlwaysEnabled;

        public WsRoleData(
                @JsonProperty("_id") Long id,
                @JsonProperty("type") String type,
                @JsonProperty("name") String name,
                @JsonProperty("description") String description,
                @JsonProperty("email") String email,
                @JsonProperty("startingClass") String startingClass,
                @JsonProperty("active") Boolean isActive,
                @JsonProperty("processWidgetAlwaysEnabled") Boolean processWidgetAlwaysEnabled) {
            this.id = id;
            this.name = checkNotBlank(name);
            this.description = description;
            this.email = email;
            this.startingClass = trimToNull(startingClass);
            this.isActive = firstNotNull(isActive, true);
            this.type = parseEnumOrDefault(type, RoleType.DEFAULT);
            this.processWidgetAlwaysEnabled = processWidgetAlwaysEnabled;
        }

        private RoleImpl.RoleImplBuilder toRole() {
            return RoleImpl.builder()
                    .withId(id)
                    .withActive(isActive)
                    .withType(type)
                    .withDescription(description)
                    .withEmail(email)
                    .withName(name)
                    .withConfig(GroupConfigImpl.builder()
                            .withStartingClass(startingClass)
                            .withProcessWidgetAlwaysEnabled(processWidgetAlwaysEnabled)
                            .build());
        }

    }

}
