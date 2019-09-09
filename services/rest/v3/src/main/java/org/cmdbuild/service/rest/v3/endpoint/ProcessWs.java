package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.Ordering;
import java.util.Collection;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.function.Consumer;
import static java.util.stream.Collectors.toList;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.ws.rs.Consumes;
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
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.cmdbuild.classe.ExtendedClass;
import org.cmdbuild.service.rest.common.serializationhelpers.ClassSerializationHelper;
import org.cmdbuild.service.rest.common.serializationhelpers.ClassSerializationHelper.WsClassData;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import org.cmdbuild.utils.date.CmDateUtils;
import org.cmdbuild.workflow.WorkflowService;

import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import org.cmdbuild.workflow.model.XpdlInfo;
import static org.cmdbuild.utils.io.CmIoUtils.toDataSource;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import org.cmdbuild.workflow.model.Process;
import org.springframework.security.access.prepost.PreAuthorize;
import org.cmdbuild.classe.access.UserClassService;
import static org.cmdbuild.auth.login.AuthorityConst.HAS_ADMIN_ACCESS_AUTHORITY;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DETAILED;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;

@Path("{a:processes}/")
@Produces(APPLICATION_JSON)
public class ProcessWs {

    private final WorkflowService workflowService;//TODO replace with user wf service
    private final UserClassService classService;
    private final ClassSerializationHelper helper;

    public ProcessWs(WorkflowService workflowService, UserClassService classService, ClassSerializationHelper helper) {
        this.workflowService = checkNotNull(workflowService);
        this.classService = checkNotNull(classService);
        this.helper = checkNotNull(helper);
    }

    @GET
    @Path(EMPTY)
    public Object readAll(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @QueryParam(LIMIT) Long limit, @QueryParam(START) Long offset, @QueryParam(DETAILED) @DefaultValue(FALSE) Boolean detailed) {
        Collection<Process> all = isAdminViewMode(viewMode) ? workflowService.getAllProcessClasses() : workflowService.getActiveProcessClasses();
        List<Process> ordered = Ordering.natural().onResultOf(Process::getName).sortedCopy(all);
        PagedElements<Process> paged = paged(ordered, offset, limit);
        return response(paged.map(detailed ? this::detailedResponse : this::minimalResponse));
    }

    @GET
    @Path("{processId}/")
    public Object read(@PathParam("processId") String processId) {
        Process classe = workflowService.getProcess(processId);
        return response(detailedResponse(classe));
    }

    @POST
    @Path(EMPTY)
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object create(WsClassData data) {
        ExtendedClass classe = classService.createClass(helper.extendedClassDefinitionForNewClass(data));
        return read(classe.getClasse().getName());
    }

    @PUT
    @Path("{processId}/")
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object update(@PathParam("processId") String classId, WsClassData data) {
        ExtendedClass classe = classService.updateClass(helper.extendedClassDefinitionForExistingClass(classId, data));
        return read(classe.getClasse().getName());
    }

    @DELETE
    @Path("{processId}/")
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object delete(@PathParam("processId") String classId) {
        classService.deleteClass(classId);
        return success();
    }

    @POST
    @Path("{processId}/versions")
    @Consumes(MULTIPART_FORM_DATA)
    @Produces(APPLICATION_JSON)
    public Object uploadNewXpdlVersion(@PathParam("processId") String processId, @Multipart(FILE) DataHandler dataHandler) {
        XpdlInfo xpdlInfo = workflowService.addXpdl(processId, toDataSource(dataHandler));
        //cacheService.invalidateAll(); //TODO invalidate only needed caches
        return response(xpdlInfoToResponse(xpdlInfo));
    }

    @POST
    @Path("{processId}/migration")
    @Consumes(MULTIPART_FORM_DATA)
    @Produces(APPLICATION_JSON)
    public Object uploadXpdlVersionAndMigrateProcessToNewProvider(@PathParam("processId") String processId, @Multipart(FILE) DataHandler dataHandler) {
        workflowService.migrateFlowInstancesToNewProvider(processId, toDataSource(dataHandler));
        return success();
    }

    @POST
    @Path("{processId}/migration")
    @Produces(APPLICATION_JSON)
    public Object uploadMigrateProcessToNewProvider(@PathParam("processId") String processId) {
        workflowService.migrateFlowInstancesToNewProviderWithExistingXpdl(processId);
        return success();
    }

    @GET
    @Path("{processId}/versions")
    public Object getAllXpdlVersions(@PathParam("processId") String processId) {
        List<XpdlInfo> versions;
        if (workflowService.isWorkflowEnabled()) {
            versions = workflowService.getXpdlInfosOrderByVersionDesc(processId);
        } else {
            versions = emptyList();
        }
        return response(versions.stream().map(ProcessWs::xpdlInfoToResponse).collect(toList()));
    }

    @GET
    @Path("{processId}/versions/{planId}/file")
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler getXpdlVersionFile(@PathParam("processId") String processId, @PathParam("planId") String planId) {
        DataSource dataSource = workflowService.getXpdlByClasseIdAndPlanId(processId, planId);
        return new DataHandler(dataSource);
    }

    @GET
    @Path("{processId}/template")
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler getXpdlTemplateFile(@PathParam("processId") String processId) {
        DataSource dataSource = workflowService.getXpdlTemplate(processId);
        return new DataHandler(dataSource);
    }

    private static Object xpdlInfoToResponse(XpdlInfo version) {
        return map("_id", version.getPlanId(),
                "provider", version.getProvider(),
                "version", version.getVersion(),
                "planId", version.getPlanId(),
                "default", version.isDefault(),
                "lastUpdate", CmDateUtils.toIsoDateTime(version.getLastUpdate()));
    }

    private FluentMap<String, Object> minimalResponse(Process p) {
        return helper.buildBasicResponse(classService.getUserClass(p.getName())).accept(processSpecificDataMapConsumer(p)); //TODO avoind new user service query
    }

    private Consumer<FluentMap<String, Object>> processSpecificDataMapConsumer(Process p) {
        return (m) -> m.put(
                "flowStatusAttr", p.getFlowStatusLookup(),
                "messageAttr", p.getMetadata().getMessageAttr(),
                "enableSaveButton", p.isFlowSaveButtonEnabled(),
                "stoppableByUser", p.getMetadata().isUserStoppable(),//TODO add user permissions here ??
                "engine", firstNotBlank(p.getProviderOrNull(), workflowService.getDefaultProvider())
        );
    }

    private FluentMap<String, Object> detailedResponse(Process process) {
        return helper.buildFullDetailExtendedResponse(classService.getExtendedUserClass(process.getName())).accept(processSpecificDataMapConsumer(process));//TODO avoind new user service query

    }

}
