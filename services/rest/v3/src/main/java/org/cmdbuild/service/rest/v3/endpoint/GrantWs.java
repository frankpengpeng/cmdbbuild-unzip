package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import static java.lang.String.format;
import java.util.Collection;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.cmdbuild.auth.grant.PrivilegedObjectType;

import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.auth.grant.GrantData;
import org.cmdbuild.auth.grant.GrantDataImpl;
import org.cmdbuild.auth.grant.GrantDataRepository;
import org.cmdbuild.auth.grant.GrantMode;
import static org.cmdbuild.auth.grant.GrantPrivilege.GP_CLONE;
import static org.cmdbuild.auth.grant.GrantPrivilege.GP_CREATE;
import static org.cmdbuild.auth.grant.GrantPrivilege.GP_DELETE;
import static org.cmdbuild.auth.grant.GrantPrivilege.GP_UPDATE;
import org.cmdbuild.auth.grant.GrantService;
import org.cmdbuild.auth.grant.GrantUtils;
import static org.cmdbuild.auth.grant.GrantUtils.serializeGrantPrivilege;
import static org.cmdbuild.auth.grant.PrivilegedObjectType.POT_CLASS;
import static org.cmdbuild.auth.grant.PrivilegedObjectType.POT_IMPORT_EXPORT_TEMPLATE;
import static org.cmdbuild.auth.login.AuthorityConst.HAS_ADMIN_ACCESS_AUTHORITY;
import org.cmdbuild.auth.role.Role;
import org.cmdbuild.auth.role.RoleRepository;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.data.filter.AttributeFilterProcessor;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.FilterType;
import org.cmdbuild.data.filter.utils.CmdbFilterUtils;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmMapUtils.toImmutableMap;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import org.springframework.security.access.prepost.PreAuthorize;

@Path("roles/{roleId}/grants/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
public class GrantWs {

    private static final BiMap<GrantMode, String> GRANT_MODE_TO_STRING = ImmutableBiMap.copyOf(map(GrantMode.GM_READ, "r",
            GrantMode.GM_WRITE, "w",
            GrantMode.GM_NONE, "-"
    ));
    private static final BiMap<PrivilegedObjectType, String> GRANT_TYPE_TO_STRING = ImmutableBiMap.copyOf(map(PrivilegedObjectType.POT_CLASS, "class",
            PrivilegedObjectType.POT_CUSTOMPAGE, "custompage",
            PrivilegedObjectType.POT_REPORT, "report",
            PrivilegedObjectType.POT_FILTER, "filter",
            PrivilegedObjectType.POT_VIEW, "view",
            POT_IMPORT_EXPORT_TEMPLATE, "ietemplate"
    ));

    private final DaoService dao;
    private final GrantDataRepository repository;
    private final GrantService grantService;
    private final RoleRepository roleRepository;

    public GrantWs(DaoService dao, GrantDataRepository repository, GrantService grantService, RoleRepository roleRepository) {
        this.dao = checkNotNull(dao);
        this.repository = checkNotNull(repository);
        this.grantService = checkNotNull(grantService);
        this.roleRepository = checkNotNull(roleRepository);
    }

    @GET
    @Path(EMPTY)
    public Object readMany(@PathParam("roleId") String roleId, @QueryParam(FILTER) String filterStr, @QueryParam(LIMIT) Long limit, @QueryParam(START) Long offset, @QueryParam("includeObjectDescription") @DefaultValue(FALSE) Boolean includeObjectDescription, @QueryParam("includeRecordsWithoutGrant") @DefaultValue(FALSE) Boolean includeRecordsWithoutGrant) {
        Role role = roleRepository.getByNameOrId(roleId);
        CmdbFilter filter = CmdbFilterUtils.parseFilter(filterStr);
        List<GrantData> grants;
        if (includeRecordsWithoutGrant) {
            grants = grantService.getGrantsForRoleIncludeRecordsWithoutGrant(role.getId());
        } else {
            grants = repository.getGrantsForRole(role.getId());
        }
        if (!filter.isNoop()) {
            filter.checkHasOnlySupportedFilterTypes(FilterType.ATTRIBUTE);
            grants = AttributeFilterProcessor.<GrantData>builder()
                    .withKeyToValueFunction((key, grant) -> {
                        switch (key) {
                            case "objectType":
                                return serializeType(grant.getType());
                            default:
                                throw new IllegalArgumentException("unsupported filter key = " + key);
                        }
                    }).withFilter(filter.getAttributeFilter())
                    .filter(grants);
        }
        return response(paged(grants, offset, limit).map((g) -> serializeGrant(g, includeObjectDescription)));
    }

    @GET
    @Path("/by-target/{objectType}/{objectTypeName}")
    public Object readOneByObject(@PathParam("roleId") String roleId, @PathParam("objectType") String objectTypeStr, @PathParam("objectTypeName") String objectTypeName) {
        Role role = roleRepository.getByNameOrId(roleId);
        PrivilegedObjectType objectType = parseEnum(objectTypeStr, PrivilegedObjectType.class);
        GrantData grant = grantService.getGrantDataByRoleAndTypeAndName(role.getId(), objectType, objectTypeName);
        return response(serializeGrant(grant));
    }

    @PUT//TODO change to POST
    @Path("_ANY")
    public Object update(@PathParam("roleId") String roleId, List<WsGrantData> data) {
        Role role = roleRepository.getByNameOrId(roleId);
        Collection<GrantData> grants = repository.setGrantsForRole(role.getId(), data.stream().map((d) -> GrantDataImpl.builder()
                .withAttributePrivileges(d.attributePrivileges)
                .withCustomPrivileges(d.customPrivileges)
                .withMode(d.mode)
                .withObjectIdOrClassName(d.classNameOrObjectId)
                .withPrivilegeFilter(d.filter)
                .withType(d.type)
                .withRoleId(role.getId())
                .build()).collect(toList()));
        return response(grants.stream().map(this::serializeGrant).collect(toList()));
    }

    private Object serializeGrant(GrantData grant) {
        return serializeGrant(grant, false);
    }

    private Object serializeGrant(GrantData grant, boolean includeObjectDescription) {
        return map(
                "_id", grant.getId(),
                "role", grant.getRoleId(),
                "mode", serializeMode(grant.getMode()),
                "objectType", serializeType(grant.getType()),
                "objectTypeName", grant.getObjectIdOrClassName(),
                "filter", grant.getPrivilegeFilter(),
                "attributePrivileges", grant.getAttributePrivileges()
        ).accept((m) -> {
            if (equal(grant.getType(), POT_CLASS)) {
                m.put("_is_process", dao.getClasse(grant.getClassName()).isProcess());
                list(GP_CREATE, GP_UPDATE, GP_DELETE, GP_CLONE).map(GrantUtils::serializeGrantPrivilege).with("relation", "print").forEach((p) -> {
                    m.put(format("_card_%s_disabled", p), !toBooleanOrDefault(firstNonNull(grant.getCustomPrivileges(), emptyMap()).get(p), true));
                });
            }
            if (includeObjectDescription) {
                m.put("_object_description", grantService.getGrantObjectDescription(grant));
            }
        });
    }

    private static String serializeMode(GrantMode grantMode) {
        return checkNotNull(GRANT_MODE_TO_STRING.get(grantMode), "unsupported grant mode = %s", grantMode);
    }

    private static String serializeType(PrivilegedObjectType grantType) {
        return checkNotNull(GRANT_TYPE_TO_STRING.get(grantType), "unsupported grant type = %s", grantType);
    }

    public static class WsGrantData {

        private final GrantMode mode;
        private final PrivilegedObjectType type;
        private final Object classNameOrObjectId;
        private final Map<String, String> attributePrivileges;
        private final Map<String, Object> customPrivileges;
        private final String filter;

        public WsGrantData(
                @JsonProperty("mode") String mode,
                @JsonProperty("objectType") String type,
                @JsonProperty("objectTypeName") Object classNameOrObjectId,
                @JsonProperty("filter") String filter,
                @JsonProperty("attributePrivileges") Map<String, String> attributePrivileges,
                @JsonProperty("_card_create_disabled") Boolean cardCreateDisabled,
                @JsonProperty("_card_update_disabled") Boolean cardUpdateDisabled,
                @JsonProperty("_card_delete_disabled") Boolean cardDeleteDisabled,
                @JsonProperty("_card_clone_disabled") Boolean cardCloneDisabled,
                @JsonProperty("_card_relation_disabled") Boolean cardRelationDisabled,
                @JsonProperty("_card_print_disabled") Boolean cardPrintDisabled) {
            this.mode = checkNotNull(GRANT_MODE_TO_STRING.inverse().get(mode.toLowerCase()), "unsupported grant mode = %s", mode);
            this.type = checkNotNull(GRANT_TYPE_TO_STRING.inverse().get(type.toLowerCase()), "unsupported grant type = %s", type);
            this.classNameOrObjectId = checkNotNull(classNameOrObjectId);
            this.filter = trimToNull(filter);
            this.attributePrivileges = attributePrivileges;
            if (equal(this.type, POT_CLASS)) {
                this.customPrivileges = (Map) map(
                        serializeGrantPrivilege(GP_CREATE), !firstNonNull(cardCreateDisabled, false),
                        serializeGrantPrivilege(GP_UPDATE), !firstNonNull(cardUpdateDisabled, false),
                        serializeGrantPrivilege(GP_DELETE), !firstNonNull(cardDeleteDisabled, false),
                        serializeGrantPrivilege(GP_CLONE), !firstNonNull(cardCloneDisabled, false),
                        "relation", !firstNonNull(cardRelationDisabled, false),
                        "print", !firstNonNull(cardPrintDisabled, false)
                ).entrySet().stream().filter((e) -> ((Boolean) e.getValue()) == false).collect(toImmutableMap(Entry::getKey, Entry::getValue));
            } else {
                this.customPrivileges = emptyMap();
            }
        }
    }
}
