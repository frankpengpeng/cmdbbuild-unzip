package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import javax.activation.DataHandler;
import javax.ws.rs.Consumes;
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
import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILE;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import org.springframework.security.access.prepost.PreAuthorize;
import org.cmdbuild.extcomponents.commons.ExtComponentInfoImpl;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.auth.login.AuthorityConst.HAS_ADMIN_ACCESS_AUTHORITY;
import org.cmdbuild.extcomponents.contextmenu.ContextMenuComponentService;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;
import org.cmdbuild.extcomponents.commons.ExtComponentInfo;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.EXTENSION;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.PARAMETERS;

@Path("components/contextmenu")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class ContextMenuComponentWs {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ContextMenuComponentService service;

    public ContextMenuComponentWs(ContextMenuComponentService service) {
        this.service = checkNotNull(service);
    }

    @GET
    @Path(EMPTY)
    public Object list(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode) {
        logger.debug("list all Context menu components for current user");
        List<ExtComponentInfo> list = isAdminViewMode(viewMode) ? service.getForCurrentUser() : service.getActiveForCurrentUser();
        return response(list.stream().map(this::serializeInfo));
    }

    @GET
    @Path("{id}")
    public Object get(@PathParam("id") Long id) {
        ExtComponentInfo customMenuComponent = service.get(id);
        return toResponse(customMenuComponent);
    }

    @DELETE
    @Path("{id}")
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object delete(@PathParam("id") Long id) {
        service.delete(id);
        return success();
    }

    @GET
    @Path("{id}/{file: [^/]+}")
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler download(@PathParam("id") Long id, @QueryParam(EXTENSION) String extension, @QueryParam(PARAMETERS) String parametersStr) {
        ExtComponentInfo customMenuComponent = service.get(id);
        return service.getContextMenuData(customMenuComponent.getName());
    }

    @POST
    @Path(EMPTY)
    @Consumes(MULTIPART_FORM_DATA)
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object create(@Multipart(FILE) DataHandler dataHandler, @QueryParam("merge") boolean merge, @Multipart("data") WsContextMenuComponentData data) {
        ExtComponentInfo info;
        if (merge) {
            info = service.createOrUpdate(toByteArray(dataHandler));
        } else {
            info = service.create(toByteArray(dataHandler));
        }
        info = ExtComponentInfoImpl.copyOf(info).accept(b -> {
            if (data != null) {
                b.withDescription(data.description).withActive(data.isActive);
            }
        }).build();
        return toResponse(service.update(info));
    }

    @PUT
    @Path("{id}")
    @Consumes(MULTIPART_FORM_DATA)
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object update(@PathParam("id") Long id, @Multipart(FILE) DataHandler dataHandler) {
        return toResponse(service.update(id, toByteArray(dataHandler)));
    }

    @PUT
    @Path("{id}")
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object update(@PathParam("id") Long id, WsContextMenuComponentData data) {
        ExtComponentInfo contextMenuComponent = service.get(id);
        contextMenuComponent = ExtComponentInfoImpl.copyOf(contextMenuComponent).withDescription(data.description).withActive(data.isActive).build();
        return toResponse(service.update(contextMenuComponent));
    }

    private Object toResponse(ExtComponentInfo customPage) {
        return response(serializeInfo(customPage));
    }

    private Object serializeInfo(ExtComponentInfo customPage) {
        return map(
                "_id", customPage.getId(),
                "active", customPage.getActive(),
                "name", customPage.getName(),
                "description", customPage.getDescription(),
                "alias", customPage.getExtjsAlias(),
                "componentId", customPage.getExtjsComponentId());
    }

    public static class WsContextMenuComponentData {

        private final String description;
        private final Boolean isActive;

        public WsContextMenuComponentData(
                @JsonProperty("description") String description,
                @JsonProperty("active") Boolean isActive
        ) {
            this.description = description;
            this.isActive = isActive;
        }

    }
}
