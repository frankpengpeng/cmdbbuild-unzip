package org.cmdbuild.service.rest.v3.endpoint;

import org.cmdbuild.service.rest.v3.helpers.SessionWsCommons;
import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import static com.google.common.collect.Maps.transformValues;
import com.google.common.collect.Ordering;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map.Entry;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.cmdbuild.auth.grant.GroupOfPrivileges;

import org.cmdbuild.auth.multitenant.api.MultitenantService;
import org.cmdbuild.auth.user.OperationUser;
import org.cmdbuild.auth.login.LoginDataImpl;
import org.cmdbuild.auth.session.SessionScope;

import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.auth.session.model.Session;
import org.cmdbuild.config.CoreConfiguration;
import static org.cmdbuild.service.rest.v3.helpers.SessionWsCommons.CURRENT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.EXT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.ID;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.emptyToNull;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlankOrNull;
import org.springframework.security.access.prepost.PreAuthorize;
import static org.cmdbuild.auth.login.AuthorityConst.HAS_ADMIN_ACCESS_AUTHORITY;
import static org.cmdbuild.auth.login.AuthorityConst.HAS_SYSTEM_ACCESS_AUTHORITY;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.data.filter.utils.CmdbFilterUtils.serializeFilter;

@Path("sessions/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class SessionsWs extends SessionWsCommons {

    private final MultitenantService multitenantService;
    private final CoreConfiguration configuration;

    public SessionsWs(SessionService sessionService, MultitenantService multitenantService, CoreConfiguration configuration) {
        super(sessionService);
        this.multitenantService = checkNotNull(multitenantService);
        this.configuration = checkNotNull(configuration);
    }

    /**
     * create a new session (ie: login)
     *
     * @param sessionData require only {@link Session#username} and
     * {@link Session#password} params to be set
     * @param includeExtendedData if <i>true</i> attach extended data to
     * response (which includes descriptions for roles and tenants).
     * @return
     */
    @POST
    @Path(EMPTY)
    public Object create(WsSessionData sessionData, @QueryParam(EXT) @Nullable Boolean includeExtendedData, @QueryParam("scope") String scopeStr) {
        checkNotBlank(sessionData.getUsername(), "'username' param cannot be null");
        checkNotNull(sessionData.getPassword(), "'password' param cannot be null");

        SessionScope scope = checkNotNull(convert(firstNotBlankOrNull(scopeStr, sessionData.scope), SessionScope.class), "must set 'scope' param (valid values = %s)", list(SessionScope.values()).stream().map(SessionScope::name).map(String::toLowerCase).collect(joining(",")));
        boolean serviceUsersAllowed;
        switch (scope) {
            case SERVICE:
                serviceUsersAllowed = true;
                break;
            case UI:
                serviceUsersAllowed = false;
                break;
            default:
                throw unsupported("unsupported session scope = %s", scope);
        }

        String sessionId = sessionService.create(LoginDataImpl.builder()
                .withLoginString(sessionData.getUsername())
                .withPassword(sessionData.getPassword())
                .withGroupName(sessionData.getRole())
                .withServiceUsersAllowed(serviceUsersAllowed)
                .withIgnoreTenantPolicies(sessionData.ignoreTenants)
                .build());

        return response(serializeSession(sessionService.getSessionById(sessionId), includeExtendedData));
    }

    private Object serializeSession(Session session) {
        return serializeSession(session, false);
    }

    private FluentMap serializeSession(Session session, @Nullable Boolean includeExtendedData) {
        OperationUser user = session.getOperationUser();
        return map(
                "_id", session.getSessionId(),
                "username", user.getLoginUser().getUsername(),
                "userDescription", user.getLoginUser().getDescription(),
                "role", user.getDefaultGroupNameOrNull(),
                "availableRoles", user.getLoginUser().getGroupNames(),
                "multigroup", user.getLoginUser().hasMultigroupEnabled(),
                "rolePrivileges", user.getRolePrivilegesAsMap().entrySet().stream().filter((e) -> e.getValue() == true).collect(toMap((e) -> e.getKey().name().toLowerCase().replaceFirst("^rp_", ""), Entry::getValue)),
                "beginDate", toIsoDateTime(session.getBeginDate()),
                "lastActive", toIsoDateTime(session.getLastActiveDate())
        ).accept((m) -> {
            if (multitenantService.isEnabled()) {
                m.put("availableTenants", user.getLoginUser().getAvailableTenantContext().getAvailableTenantIds(),
                        "tenant", user.getUserTenantContext().getDefaultTenantId(),
                        "activeTenants", user.getUserTenantContext().getActiveTenantIds(),
                        "canIgnoreTenants", user.getLoginUser().getAvailableTenantContext().ignoreTenantPolicies(),
                        "ignoreTenants", user.getUserTenantContext().ignoreTenantPolicies(),
                        "multiTenantActivationPrivileges", serializeEnum(user.getLoginUser().getAvailableTenantContext().getTenantActivationPrivileges())
                );
            }
            if (firstNonNull(includeExtendedData, false)) {
                m.put(
                        "availableRolesExtendedData", user.getLoginUser().getRoleInfos().stream().map((g) -> map("code", g.getName(), "description", g.getDescription())).collect(toList()),
                        "availableTenantsExtendedData", multitenantService.getTenantDescriptions(user.getLoginUser().getAvailableTenantContext().getAvailableTenantIds()).entrySet().stream()
                                .map((e) -> map("code", e.getKey(), "description", firstNonNull(trimToNull(e.getValue()), format("tenant #%s", e.getKey())))).collect(toList()));
            }
        });
    }

    /**
     * read session
     *
     * @param sessionId session id (or {@link CURRENT} for current session)
     * @param includeExtendedData if <i>true</i> attach extended data to
     * response (which includes descriptions for roles and tenants).
     * @return
     */
    @GET
    @Path("{" + ID + "}/")
    public Object read(@PathParam(ID) String sessionId, @QueryParam(EXT) Boolean includeExtendedData) {
        sessionId = sessionIdOrCurrent(sessionId);
        checkArgument(sessionService.exists(sessionId), "session not found for id = %s", sessionId);
        return response(serializeSession(sessionService.getSessionById(sessionId), includeExtendedData));
    }

    @GET
    @Path("{sessionId}/privileges")
    public Object read(@PathParam("sessionId") String sessionId) {
        sessionId = sessionIdOrCurrent(sessionId);
        checkArgument(sessionService.exists(sessionId), "session not found for id = %s", sessionId);
        OperationUser user = sessionService.getUser(sessionId);
        return response(user.getPrivilegeContext().getAllPrivileges().entrySet().stream().sorted(Ordering.natural().onResultOf(Entry::getKey)).map((p) -> map(
                "subject", p.getKey())
                .with(serializeGroupOfPrivileges(p.getValue().getMinPrivilegesForAllRecords()))
                .accept((m) -> {
                    if (p.getValue().hasPrivilegesWithFilter()) {
                        m.put("hasPrivilegesWithFilter", true,
                                "privilegesWithFilter", p.getValue().getPrivilegeGroupsWithFilter().stream().map((pf) -> map("filter", serializeFilter(pf.getFilter())).with(serializeGroupOfPrivileges(pf))).collect(toList()));
                    }
                })).collect(toList()));
    }

    @GET
    @Path("")
    @PreAuthorize(HAS_SYSTEM_ACCESS_AUTHORITY)
    public Object readAll() {
        List<Session> sessions = sessionService.getAllSessions();
        return response(sessions.stream().sorted(Ordering.natural().onResultOf(Session::getLastActiveDate).reversed()).map(this::serializeSession).collect(toList()));
    }

    private static FluentMap<String, Object> serializeGroupOfPrivileges(GroupOfPrivileges privileges) {
        return (FluentMap) map(
                "source", privileges.getSource(),
                "privileges", privileges.getPrivileges().stream().sorted(Ordering.natural()).map((p) -> p.name().toLowerCase()).collect(toList())
        ).skipNullValues().with(
                "attributePrivileges", emptyToNull(map(transformValues(privileges.getAttributePrivileges(), (v) -> v.stream().sorted(Ordering.natural()).map((p) -> p.name().toLowerCase())))));
    }

    /**
     *
     * @param sessionId session id (or {@link CURRENT} for current session)
     * @param sessionData require only {@link Session#role} and optionally
     * {@link Session#tenant} and/or {@link Session#activeTenants}
     * @param includeExtendedData if <i>true</i> attach extended data to
     * response (which includes descriptions for roles and tenants).
     * @return
     */
    @PUT
    @Path("{" + ID + "}/")
    public Object update(@PathParam(ID) String sessionId, WsSessionData sessionData, @QueryParam(EXT) Boolean includeExtendedData) {
        sessionId = sessionIdOrCurrent(sessionId);
        checkArgument(sessionService.exists(sessionId), "session not found for id = %s", sessionId);
        checkArgument(!isBlank(sessionData.getRole()), "'group' param cannot be null");

        OperationUser currentOperationUser = sessionService.getUser(sessionId);
        sessionService.update(sessionId,
                LoginDataImpl.builder()
                        .withLoginString(currentOperationUser.getLoginUser().getUsername())
                        .withGroupName(sessionData.getRole())
                        .withDefaultTenant(sessionData.getDefaultTenant())
                        .withActiveTenants(sessionData.getActiveTenants())
                        .withIgnoreTenantPolicies(sessionData.ignoreTenants)
                        .withServiceUsersAllowed(true)//TODO use scope
                        .build());

        return response(serializeSession(sessionService.getSessionById(sessionId), includeExtendedData));
    }

    /**
     * delete session (logout)
     *
     * @param sessionId (or {@link CURRENT} for current session)
     */
    @DELETE
    @Path("{" + ID + "}/")
    public Object delete(@PathParam(ID) String sessionId) {
        sessionId = sessionIdOrCurrent(sessionId);
        checkArgument(sessionService.exists(sessionId), "session not found for id = %s", sessionId);
        sessionService.deleteSession(sessionId);
        return success();
    }

    /**
     * delete all sessions
     *
     */
    @DELETE
    @Path("all")
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object deleteAll() {//TODO authorize admin only !
        logger.info("delete all sessions");
        sessionService.deleteAll();
        return success();
    }

    /**
     * send a keepalive request. Currently you can request keepalive only for
     * current session (by id or with {@link CURRENT} keyword).
     *
     * @param sessionId session id (or {@link CURRENT} for current session)
     * @return an informative object with recommended keepalive interval
     */
    @POST
    @Path("{" + ID + "}/keepalive")
    public Object keepalive(@PathParam(ID) String sessionId) {
        checkArgument(CURRENT.equalsIgnoreCase(sessionId) || equal(sessionId, sessionService.getCurrentSessionIdOrNull()), "cannot request keepalive for session %s while logged with session %s", sessionId, sessionService.getCurrentSessionIdOrNull());
        //no explicit operation is required, all requests will automatically extend session ttl
        return response(map("_id", sessionService.getCurrentSessionIdOrNull(), "timeToLiveSeconds", configuration.getSessionTimeoutOrDefault(), "recommendedKeepaliveIntervalSeconds", configuration.getSessionTimeoutOrDefault() / 3));
    }

    public static class WsSessionData {

        public final String username, password, role, scope;
        public final Long defaultTenant;
        public final boolean ignoreTenants;
        public final List<Long> activeTenants;

        public WsSessionData(@JsonProperty("username") String username,
                @JsonProperty("password") String password,
                @JsonProperty("role") String role,
                @JsonProperty("scope") String scope,
                @JsonProperty("tenant") Long defaultTenant,
                @JsonProperty("ignoreTenants") Boolean ignoreTenants,
                @JsonProperty("activeTenants") List<Long> activeTenants) {
            this.username = username;
            this.password = password;
            this.role = role;
            this.scope = scope;
            this.defaultTenant = defaultTenant;
            this.activeTenants = activeTenants == null ? emptyList() : ImmutableList.copyOf(activeTenants);
            this.ignoreTenants = firstNonNull(ignoreTenants, false);
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getRole() {
            return role;
        }

        public Long getDefaultTenant() {
            return defaultTenant;
        }

        public List<Long> getActiveTenants() {
            return activeTenants;
        }

    }
}
