package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Functions.compose;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.Map;
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
import static org.cmdbuild.auth.login.AuthorityConst.HAS_ADMIN_ACCESS_AUTHORITY;
import org.cmdbuild.classe.ExtendedClass;
import org.cmdbuild.classe.access.UserClassService;
import static org.cmdbuild.common.utils.PagedElements.paged;
import org.cmdbuild.data.filter.AttributeFilterProcessor;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.FilterType;
import org.cmdbuild.data.filter.utils.CmdbFilterUtils;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DETAILED;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.cmdbuild.service.rest.common.serializationhelpers.ClassSerializationHelper;
import org.cmdbuild.service.rest.common.serializationhelpers.ClassSerializationHelper.WsClassData;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

@Path("{a:classes}/")
@Produces(APPLICATION_JSON)
public class ClassWs {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final UserClassService classService;
    private final ClassSerializationHelper helper;

    public ClassWs(UserClassService classService, ClassSerializationHelper helper) {
        this.classService = checkNotNull(classService);
        this.helper = checkNotNull(helper);
    }

    @GET
    @Path(EMPTY)
    public Object readAll(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @QueryParam(DETAILED) boolean detailed, @QueryParam(LIMIT) Integer limit, @QueryParam(START) Integer offset, @QueryParam(FILTER) String filterStr) {
        List list = (isAdminViewMode(viewMode) ? classService.getAllUserClasses() : classService.getActiveUserClasses()).stream()
                .map(detailed ? compose(helper::buildFullDetailExtendedResponse, classService::getExtendedClass) : helper::buildBasicResponse).collect(toList());
        CmdbFilter filter = CmdbFilterUtils.parseFilter(filterStr);
        filter.checkHasOnlySupportedFilterTypes(FilterType.ATTRIBUTE);
        if (filter.hasAttributeFilter()) {
            list = AttributeFilterProcessor.<Map<String, Object>>builder().withKeyToValueFunction((k, m) -> toStringOrNull(m.get(k))).withFilter(filter.getAttributeFilter()).filter(list);
        }
        return response(paged(list, offset, limit));
    }

    @GET
    @Path("{classId}/")
    public Object read(@PathParam("classId") String classId) {
        ExtendedClass classe = classService.getExtendedUserClass(classId);
        return buildResponse(classe);
    }

    @POST
    @Path(EMPTY)
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object create(WsClassData data) {
        logger.debug("create classe with data = {}", data);
        ExtendedClass classe = classService.createClass(helper.extendedClassDefinitionForNewClass(data));
        return buildResponse(classe);
    }

    @PUT
    @Path("{classId}/")
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object update(@PathParam("classId") String classId, WsClassData data) {
        logger.debug("update classe = {} with data = {}", classId, data);
        ExtendedClass classe = classService.updateClass(helper.extendedClassDefinitionForExistingClass(classId, data));
        return buildResponse(classe);
    }

    @DELETE
    @Path("{classId}/")
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object delete(@PathParam("classId") String classId) {
        classService.deleteClass(classId);
        return success();
    }

    private Object buildResponse(ExtendedClass classe) {
        return response(helper.buildFullDetailExtendedResponse(classe));
    }

}
