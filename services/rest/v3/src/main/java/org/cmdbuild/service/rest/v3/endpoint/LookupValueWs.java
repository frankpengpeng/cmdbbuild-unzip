package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.MoreCollectors.onlyElement;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.utils.CmdbFilterUtils;
import org.cmdbuild.lookup.Lookup;
import org.cmdbuild.lookup.LookupImpl;
import org.cmdbuild.lookup.LookupImpl.LookupBuilder;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.lookup.LookupType;
import static org.cmdbuild.service.rest.v3.endpoint.LookupTypeWs.decodeIfHex;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LOOKUP_TYPE_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LOOKUP_VALUE_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import org.cmdbuild.translation.ObjectTranslationService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.springframework.security.access.prepost.PreAuthorize;

import static org.cmdbuild.auth.login.AuthorityConst.HAS_ADMIN_ACCESS_AUTHORITY;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@Path("lookup_types/{" + LOOKUP_TYPE_ID + "}/values/")
@Produces(APPLICATION_JSON)
public class LookupValueWs {

    private final LookupService lookupService;
    private final ObjectTranslationService translationService;

    public LookupValueWs(LookupService lookupLogic, ObjectTranslationService translationService) {
        this.lookupService = checkNotNull(lookupLogic);
        this.translationService = checkNotNull(translationService);
    }

    @GET
    @Path("{" + LOOKUP_VALUE_ID + "}/")
    public Object read(@PathParam(LOOKUP_TYPE_ID) String lookupTypeId, @PathParam(LOOKUP_VALUE_ID) Long lookupValueId) {
        Lookup lookup = lookupService.getLookup(lookupValueId);
        return response(toResponse(lookup));
    }

    @GET
    @Path(EMPTY)
    public Object readAll(@PathParam(LOOKUP_TYPE_ID) String lookupTypeId, @QueryParam(LIMIT) Integer limit, @QueryParam(START) Integer offset, @QueryParam(FILTER) String filterStr, @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode) {
        CmdbFilter filter = CmdbFilterUtils.parseFilter(filterStr);
        PagedElements<Lookup> lookups = isAdminViewMode(viewMode) ? lookupService.getAllLookup(decodeIfHex(lookupTypeId), offset, limit, filter) : lookupService.getActiveLookup(decodeIfHex(lookupTypeId), offset, limit, filter);
        return response(lookups.stream().map(this::toResponse).collect(toList()), lookups.totalSize());
    }

    @POST
    @Path("")
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object create(@PathParam(LOOKUP_TYPE_ID) String lookupTypeId, WsLookupValue wsLookupValue) {
        LookupType lookupType = lookupService.getLookupType(decodeIfHex(lookupTypeId));
        Lookup lookup = lookupService.createOrUpdateLookup(wsLookupValue.buildLookup().withType(lookupType).build());
        return response(toResponse(lookup));
    }

    @PUT
    @Path("{lookupValueId}")
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object update(@PathParam(LOOKUP_TYPE_ID) String lookupTypeId, @PathParam("lookupValueId") Long lookupId, WsLookupValue wsLookupValue) {
        LookupType lookupType = lookupService.getLookupType(decodeIfHex(lookupTypeId));
        Lookup lookup = lookupService.createOrUpdateLookup(wsLookupValue.buildLookup().withType(lookupType).withId(checkNotNull(lookupId)).build());
        return response(toResponse(lookup));
    }

    @DELETE
    @Path("{lookupValueId}")
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object delete(@PathParam(LOOKUP_TYPE_ID) String lookupTypeId, @PathParam("lookupValueId") Long lookupId) {
        lookupService.deleteLookupValue(decodeIfHex(lookupTypeId), lookupId);
        return success();
    }

    @POST
    @Path("order")
    public Object reorder(@PathParam(LOOKUP_TYPE_ID) String lookupTypeId, List<Long> lookupValueIds, @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode) {
        lookupTypeId = decodeIfHex(lookupTypeId);
        checkNotNull(lookupValueIds);
        checkArgument(set(lookupValueIds).size() == lookupValueIds.size());
        checkArgument(lookupValueIds.stream().allMatch(notNull()));

        List<Lookup> lookups = list(lookupService.getAllLookup(lookupTypeId));

        List<Lookup> lookupsToSave = list();

        for (int i = 0; i < lookupValueIds.size(); i++) {
            Long lookupId = lookupValueIds.get(i);
            Lookup lookup = lookups.stream().filter((l) -> equal(l.getId(), lookupId)).collect(onlyElement());
            int newIndex = i + 1;
            if (newIndex != lookup.getIndex()) {
                lookupsToSave.add(LookupImpl.copyOf(lookup).withIndex(newIndex).build());
            }
        }

        lookupsToSave.forEach(lookupService::createOrUpdateLookup);

        return readAll(lookupTypeId, null, null, null, viewMode);
    }

    private Object toResponse(Lookup lookup) {
        return map(
                "_id", lookup.getId(),
                "_type", lookup.getType().getName(),
                "code", lookup.getCode(),
                "description", lookup.getDescription(),
                "_description_translation", translationService.translateLookupDescriptionSafe(lookup.getType().getName(), lookup.getCode(), lookup.getDescription()),
                "index", lookup.getIndex(),
                "active", lookup.isActive(),
                "parent_id", lookup.getParentId(),
                "parent_type", lookup.getParentTypeOrNull(),
                "default", lookup.isDefault(),
                "note", lookup.getNotes(),
                "text_color", lookup.getTextColor(),
                "icon_type", lookup.getIconType().name().toLowerCase(),
                "icon_image", lookup.getIconImage(),
                "icon_font", lookup.getIconFont(),
                "icon_color", lookup.getIconColor());
    }

    public static class WsLookupValue {

        private final Long parentId;
        private final Integer index;
        private final boolean isDefault, active;
        private final String code, description, iconType, iconImage, iconFont, iconColor, textColor, notes;

        public WsLookupValue(
                @JsonProperty("parent_id") Long parentId,
                @JsonProperty("index") Integer index,
                @JsonProperty("default") Boolean isDefault,
                @JsonProperty("active") Boolean active,
                @JsonProperty("code") String code,
                @JsonProperty("description") String description,
                @JsonProperty("icon_type") String iconType,
                @JsonProperty("icon_image") String iconImage,
                @JsonProperty("icon_font") String iconFont,
                @JsonProperty("icon_color") String iconColor,
                @JsonProperty("text_color") String textColor,
                @JsonProperty("note") String notes) {
            this.parentId = parentId;
            this.index = index;
            this.isDefault = isDefault;
            this.code = checkNotBlank(code);
            this.description = description;
            this.iconType = checkNotBlank(iconType);
            this.iconImage = iconImage;
            this.iconFont = iconFont;
            this.iconColor = iconColor;
            this.textColor = textColor;
            this.notes = notes;
            this.active = active;
        }

        private LookupBuilder buildLookup() {
            return LookupImpl.builder()
                    .withCode(code)
                    .withIconColor(iconColor)
                    .withTextColor(textColor)
                    .withDefault(isDefault)
                    .withDescription(description)
                    .withIconFont(iconFont)
                    .withIconImage(iconImage)
                    .withIconTypeAsString(iconType)
                    .withNotes(notes)
                    .withIndex(index)
                    .withActive(active)
                    .withParentId(parentId);
        }

    }
}
