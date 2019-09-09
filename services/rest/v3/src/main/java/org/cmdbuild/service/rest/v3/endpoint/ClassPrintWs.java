package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import javax.activation.DataHandler;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import org.apache.commons.io.FilenameUtils;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import static org.cmdbuild.report.utils.ReportExtUtils.reportExtFromString;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CLASS_ID;
import org.cmdbuild.report.SysReportService;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.EXTENSION;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.SORT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.v3.endpoint.CardWs.mapAttrNames;
import static org.cmdbuild.utils.json.CmJsonUtils.LIST_OF_STRINGS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;

@Path("{a:classes}/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class ClassPrintWs {

    private final DaoService dao;
    private final SysReportService reportService;

    public ClassPrintWs(DaoService dao, SysReportService reportService) {
        this.dao = checkNotNull(dao);
        this.reportService = checkNotNull(reportService);
    }

    @GET
    @Path("{" + CLASS_ID + "}/print/{file: [^/]+}")
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler printClassReport(@PathParam(CLASS_ID) String classId,
            @QueryParam(FILTER) String filterStr,
            @QueryParam(SORT) String sort,
            @QueryParam(LIMIT) Long limit,
            @QueryParam(START) Long offset,
            @QueryParam(EXTENSION) String extension,
            @QueryParam("attributes") String attributes) {
        //TODO permission check 
        DaoQueryOptions queryOptions = DaoQueryOptionsImpl.builder()
                .withFilter(filterStr)
                .withSorter(sort)
                .withPaging(offset, limit)
                .withAttrs(isBlank(attributes) ? null : mapAttrNames(fromJson(attributes, LIST_OF_STRINGS)))
                .build();
        return reportService.executeClassReport(dao.getClasse(classId), reportExtFromString(extension), queryOptions);
    }

    @GET
    @Path("{" + CLASS_ID + "}/print_schema/{file: [^/]+}")
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler printClassSchemaReport(@PathParam(CLASS_ID) String classId, @PathParam("file") String fileName, @QueryParam(EXTENSION) String extension) {
        //TODO permission check 
        return reportService.executeClassSchemaReport(dao.getClasse(classId), reportExtFromString(firstNotBlank(extension, FilenameUtils.getExtension(fileName))));
    }

    @GET
    @Path("print_schema/{file: [^/]+}")
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler printSchemaReport(@PathParam(CLASS_ID) String classId, @PathParam("file") String fileName, @QueryParam(EXTENSION) String extension) {
        //TODO permission check 
        return reportService.executeSchemaReport(reportExtFromString(firstNotBlank(extension, FilenameUtils.getExtension(fileName))));
    }
}
