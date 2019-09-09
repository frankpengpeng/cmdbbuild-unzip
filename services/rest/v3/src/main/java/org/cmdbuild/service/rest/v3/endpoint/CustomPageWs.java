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
import org.cmdbuild.extcomponents.custompage.CustomPageService;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILE;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import org.springframework.security.access.prepost.PreAuthorize;
import org.cmdbuild.extcomponents.commons.ExtComponentInfoImpl;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.auth.login.AuthorityConst.HAS_ADMIN_ACCESS_AUTHORITY;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;
import org.cmdbuild.extcomponents.commons.ExtComponentInfo;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.EXTENSION;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.PARAMETERS;

@Path("custompages/")
@Produces(APPLICATION_JSON)
public class CustomPageWs {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CustomPageService customPageService;

    public CustomPageWs(CustomPageService customPageService) {
        this.customPageService = checkNotNull(customPageService);
    }

    @GET
    @Path(EMPTY)
    public Object list(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode) {
        logger.debug("list all custom pages for current user");
        List<ExtComponentInfo> list = isAdminViewMode(viewMode) ? customPageService.getForCurrentUser() : customPageService.getActiveForCurrentUser();
        return response(list.stream().map(this::serializeCustomPage));
    }

    @GET
    @Path("{id}")
    public Object get(@PathParam("id") Long id) {
        ExtComponentInfo customPage = customPageService.get(id);
        return customPageResponse(customPage);
    }

    @DELETE
    @Path("{id}")
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object delete(@PathParam("id") Long id) {
        customPageService.delete(id);
        return success();
    }

    @POST
    @Path(EMPTY)
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object create(@Multipart(FILE) DataHandler dataHandler, @QueryParam("merge") boolean merge, @Multipart("data") WsCustomPageData data) {
        ExtComponentInfo customPage;
        if (merge) {
            customPage = customPageService.createOrUpdate(toByteArray(dataHandler));
        } else {
            customPage = customPageService.create(toByteArray(dataHandler));
        }
        customPage = ExtComponentInfoImpl.copyOf(customPage).accept(b -> {
            if (data != null) {
                b.withDescription(data.description).withActive(data.active);
            }
        }).build();
        return customPageResponse(customPageService.update(customPage));
    }

    @PUT
    @Path("{id}")
    @Consumes(MULTIPART_FORM_DATA)
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object update(@PathParam("id") Long id, @Multipart(FILE) DataHandler dataHandler) {
        return customPageResponse(customPageService.update(id, toByteArray(dataHandler)));
    }

    @PUT
    @Path("{id}")
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object update(@PathParam("id") Long id, WsCustomPageData data) {
        ExtComponentInfo customPage = customPageService.get(id);
        customPage = ExtComponentInfoImpl.copyOf(customPage).withDescription(data.description).withActive(data.active).build();
        return customPageResponse(customPageService.update(customPage));
    }

    @GET
    @Path("{id}/{file: [^/]+}")
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler download(@PathParam("id") Long id, @QueryParam(EXTENSION) String extension, @QueryParam(PARAMETERS) String parametersStr) {
        ExtComponentInfo customPage = customPageService.get(id);
        return customPageService.getCustomPageData(customPage.getName());
    }

    private Object customPageResponse(ExtComponentInfo customPage) {
        return response(serializeCustomPage(customPage));
    }

    private Object serializeCustomPage(ExtComponentInfo customPage) {
        return map(
                "_id", customPage.getId(),
                "active", customPage.getActive(),
                "name", customPage.getName(),
                "description", customPage.getDescription(),
                "alias", customPage.getExtjsAlias(),
                "componentId", customPage.getExtjsComponentId());
    }

    public static class WsCustomPageData {

        private final String description;
        private final boolean active;

        public WsCustomPageData(
                @JsonProperty("description") String description,
                @JsonProperty("active") boolean active) {
            this.description = description;
            this.active = active;
        }

    }
}
