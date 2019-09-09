package org.cmdbuild.service.rest.v2.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.transform;
import java.util.List;
import java.util.function.Function;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.report.ReportData;
import org.cmdbuild.report.ReportInfo;
import org.cmdbuild.report.ReportService;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.EXTENSION;
import org.cmdbuild.service.rest.v2.serializationhelpers.AttributeTypeConversionServicev2;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.common.utils.PagedElements.paged;
import org.cmdbuild.report.ReportFormat;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@Path("reports/")
@Produces(APPLICATION_JSON)
public class ReportsWsV2 {

    private final ReportService reportService;
    private final AttributeTypeConversionServicev2 toAttributeDetail;

    public ReportsWsV2(ReportService reportService, AttributeTypeConversionServicev2 toAttributeDetail) {
        this.reportService = checkNotNull(reportService);
        this.toAttributeDetail = checkNotNull(toAttributeDetail);
    }

    @GET
    @Path(EMPTY)
    public Object readMany() {
        List<ReportInfo> list = reportService.getAll();

        Function<ReportInfo, Object> serializer = this::serializeMinimalReport;
        return map("data", list(transform(list, serializer::apply)));
    }

    @GET
    @Path("{reportId}/")
    public Object readOne(@PathParam("reportId") String reportId) {
        ReportInfo report = reportService.getByIdOrCode(reportId);
        return map("data", serializeDetailedReport(report));
    }

    @GET
    @Path("{reportId}/attributes/")
    public Object readAllAttributes(@PathParam("reportId") String reportId, @QueryParam(LIMIT) Integer limit, @QueryParam(START) Integer offset) {
        Iterable<Attribute> elements = reportService.getParamsById(reportService.getByIdOrCode(reportId).getId());
        return map("data", paged(elements, toAttributeDetail::serializeAttributeType, offset, limit));
    }

    @GET
    @Path("{reportId}/{file: [^/]+}")
    @Produces(APPLICATION_OCTET_STREAM)
    public Object download(@PathParam("reportId") String reportId, @QueryParam(EXTENSION) String extension
    //@QueryParam(PARAMETERS) JsonValues parameters TODO
    ) {
        return map("data", reportService.executeReportAndDownload(reportId, parseEnum(extension, ReportFormat.class)));
    }

    private Object serializeDetailedReport(ReportInfo report) {
        ReportData reportData = report instanceof ReportData ? ((ReportData) report) : reportService.getReportData(report.getId());
        return serializeMinimalReport(report).with(
                "title", report.getCode(),
                "query", reportData.getQuery()
        );
    }

    private FluentMap<String, Object> serializeMinimalReport(ReportInfo report) {
        return map(
                "title", report.getCode(),
                "description", report.getDescription(),
                "_id", report.getId()
        );
    }

}
