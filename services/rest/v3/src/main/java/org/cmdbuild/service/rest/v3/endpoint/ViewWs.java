package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.activation.DataHandler;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
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
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.view.View;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import org.cmdbuild.translation.ObjectTranslationService;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.view.ViewImpl;
import org.cmdbuild.view.ViewImpl.ViewImplBuilder;
import org.cmdbuild.view.ViewType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;

import static org.cmdbuild.auth.login.AuthorityConst.HAS_ADMIN_ACCESS_AUTHORITY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.report.SysReportService;
import static org.cmdbuild.report.utils.ReportExtUtils.reportExtFromString;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DETAILED;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.EXTENSION;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.SORT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;
import static org.cmdbuild.utils.json.CmJsonUtils.LIST_OF_STRINGS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import org.cmdbuild.view.ViewDefinitionService;

@Path("views/")
@Produces(APPLICATION_JSON)
public class ViewWs {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ViewDefinitionService viewService;
    private final ObjectTranslationService translationService;
    private final SysReportService reportService;

    public ViewWs(ViewDefinitionService viewService, ObjectTranslationService translationService, SysReportService reportService) {
        this.viewService = checkNotNull(viewService);
        this.translationService = checkNotNull(translationService);
        this.reportService = checkNotNull(reportService);
    }

    @GET
    @Path(EMPTY)
    public Object list(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @QueryParam(LIMIT) Long limit, @QueryParam(START) Long offset, @QueryParam(DETAILED) @DefaultValue(FALSE) Boolean detailed) {
        logger.debug("list all views");
        List<View> views = isAdminViewMode(viewMode) ? viewService.getViewsForCurrentUser() : viewService.getActiveViewsForCurrentUser();
        logger.trace("processing views = {}", views);
        return response(views.stream().map(this::serializeView).collect(toList()));
    }

    @GET
    @Path("{viewId}")
    public Object getOne(@PathParam("viewId") String viewId) {
        View view = viewService.getForCurrentUserByIdOrName(viewId);
        return response(serializeView(view));
    }

    @POST
    @Path("")
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object create(WsViewData data) {
        View view = viewService.create(data.toView().build());
        return response(serializeView(view));
    }

    @PUT
    @Path("{viewId}")
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object update(@PathParam("viewId") String viewId, WsViewData data) {
        View view = viewService.update(data.toView().withName(viewId).build());
        return response(serializeView(view));
    }

    @DELETE
    @Path("{viewId}")
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object delete(@PathParam("viewId") String viewId) {
        viewService.delete(viewService.getForCurrentUserByName(viewId).getId());
        return success();
    }

    @GET
    @Path("{viewId}/print/{file: [^/]+}")
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler printView(@PathParam("viewId") String viewId,
            @QueryParam(FILTER) String filterStr,
            @QueryParam(SORT) String sort,
            @QueryParam(LIMIT) Long limit,
            @QueryParam(START) Long offset,
            @QueryParam(EXTENSION) String extension,
            @QueryParam("attributes") String attributes) {

        DaoQueryOptions queryOptions = DaoQueryOptionsImpl.builder()
                .withFilter(filterStr)
                .withSorter(sort)
                .withPaging(offset, limit)
                .withAttrs(isBlank(attributes) ? null : fromJson(attributes, LIST_OF_STRINGS))
                .build();
        return reportService.executeViewReport(viewService.getForCurrentUserByName(viewId), reportExtFromString(extension), queryOptions);
    }

    private Object serializeView(View view) {
        return map(
                "_id", view.getName(),
                "name", view.getName(),
                "type", view.getType().name(),
                "description", view.getDescription(),
                "_description_translation", translationService.translateViewDesciption(view.getName(), view.getDescription()),
                "filter", view.getFilter(),
                "sourceClassName", view.getSourceClass(),
                "sourceFunction", view.getSourceFunction(),
                "active", view.isActive()
        );
    }

    public static class WsViewData {

        private final String name;
        private final String description;
        private final String sourceClassName;
        private final String sourceFunction;
        private final String filter;
        private final ViewType type;
        private final Boolean isActive;

        public WsViewData(
                @JsonProperty("name") String name,
                @JsonProperty("description") String description,
                @JsonProperty("sourceClassName") String sourceClassName,
                @JsonProperty("sourceFunction") String sourceFunction,
                @JsonProperty("filter") String filter,
                @JsonProperty("active") Boolean isActive,
                @JsonProperty("type") String type) {
            this.name = checkNotBlank(name);
            this.description = description;
            this.sourceClassName = sourceClassName;
            this.sourceFunction = sourceFunction;
            this.filter = filter;
            this.isActive = isActive;
            this.type = ViewType.valueOf(checkNotBlank(type).toUpperCase());
        }

        public ViewImplBuilder toView() {
            return ViewImpl.builder()
                    .withName(name)
                    .withDescription(description)
                    .withFilter(filter)
                    .withSourceClass(sourceClassName)
                    .withSourceFunction(sourceFunction)
                    .withType(type)
                    .withActive(isActive);
        }

    }
}
