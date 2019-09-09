package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import javax.activation.DataHandler;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.transform;
import static com.google.common.collect.MoreCollectors.onlyElement;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import static org.cmdbuild.auth.login.AuthorityConst.HAS_ADMIN_ACCESS_AUTHORITY;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.common.utils.PagedElements.isPaged;
import static org.cmdbuild.common.utils.PagedElements.paged;

import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.report.ReportFormat;
import org.cmdbuild.report.ReportData;
import org.cmdbuild.report.ReportInfo;
import org.cmdbuild.report.ReportInfoImpl;
import org.cmdbuild.report.ReportService;
import static org.cmdbuild.report.utils.ReportExtUtils.reportExtFromString;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.EXTENSION;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.PARAMETERS;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.REPORT_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DETAILED;
import org.cmdbuild.translation.ObjectTranslationService;
import org.cmdbuild.utils.io.CmIoUtils;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_OBJECTS;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.springframework.security.access.prepost.PreAuthorize;
import org.cmdbuild.data.filter.AttributeFilterProcessor;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.FilterType;
import org.cmdbuild.data.filter.utils.CmdbFilterUtils;
import org.cmdbuild.report.utils.ReportFilesUtils;
import org.cmdbuild.service.rest.common.serializationhelpers.AttributeTypeConversionService;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@Path("reports/")
@Produces(APPLICATION_JSON)
public class ReportWs {

    private final ReportService service;
    private final AttributeTypeConversionService toAttributeDetail;
    private final ObjectTranslationService translation;

    public ReportWs(ReportService reportService, AttributeTypeConversionService toAttributeDetail, ObjectTranslationService translation) {
        this.service = checkNotNull(reportService);
        this.toAttributeDetail = checkNotNull(toAttributeDetail);
        this.translation = checkNotNull(translation);
    }

    @GET
    @Path(EMPTY)
    public Object readAll(@QueryParam(FILTER) String filterStr, @QueryParam(LIMIT) Integer limit, @QueryParam(START) Integer offset, @QueryParam(DETAILED) Boolean detailed) {
        List<ReportInfo> list = service.getForCurrentUser();
        CmdbFilter filter = CmdbFilterUtils.parseFilter(filterStr);
        if (filter.hasFilter()) {
            filter.checkHasOnlySupportedFilterTypes(FilterType.ATTRIBUTE);
            list = AttributeFilterProcessor.<ReportInfo>builder()
                    .withKeyToValueFunction((key, report) -> {
                        switch (checkNotBlank(key)) {
                            case "title":
                            case "code":
                                return report.getCode();
                            case "description":
                                return report.getDescription();
                            default:
                                throw new IllegalArgumentException("invalid attribute filter key = " + key);
                        }
                    })
                    .withFilter(filter.getAttributeFilter()).build().filter(list);
        }
        Function<ReportInfo, Object> serializer = defaultIfNull(detailed, false) ? this::serializeDetailedReport : this::serializeMinimalReport;
        if (isPaged(offset, limit)) {
            PagedElements paged = paged(list, offset, limit).map(serializer);
            return response(paged);
        } else {
            return response(list(transform(list, serializer::apply)));
        }
    }

    @GET
    @Path("{" + REPORT_ID + "}/")
    public Object read(@PathParam(REPORT_ID) String reportId) {
        ReportInfo report = service.getByIdOrCode(reportId);
        return response(serializeDetailedReport(report));
    }

    @GET
    @Path("{" + REPORT_ID + "}/attributes/")
    public Object readAllAttributes(@PathParam(REPORT_ID) String reportId, @QueryParam(LIMIT) Integer limit, @QueryParam(START) Integer offset) {
        ReportInfo report = service.getByIdOrCode(reportId);
        Iterable<Attribute> elements = service.getParamsById(report.getId());
        return response(paged(elements, toAttributeDetail::serializeAttributeType, offset, limit));
    }

    @GET
    @Path("{" + REPORT_ID + "}/{file: [^/]+}")
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler download(@PathParam(REPORT_ID) String reportId, @QueryParam(EXTENSION) String extension, @QueryParam(PARAMETERS) String parametersStr) {
        ReportInfo report = service.getByIdOrCode(reportId);
        Map<String, Object> parameters = isBlank(parametersStr) ? emptyMap() : fromJson(parametersStr, MAP_OF_OBJECTS);
        return service.executeReportAndDownload(report.getId(), reportExtFromString(extension), parameters);//TODO handle special report codes
    }

    @POST
    @Path("")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object createReport(List<Attachment> attachments) {
        checkNotNull(attachments);
        WsReportData data = getData(attachments);
        Map<String, byte[]> files = getFiles(attachments);
        ReportData reportData = service.createReport(data.toReportInfo().build(), files);
        return response(serializeDetailedReport(reportData));
    }

    @PUT
    @Path("{" + REPORT_ID + "}/")
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Object updateReport(@PathParam(REPORT_ID) String reportId, List<Attachment> attachments) {
        WsReportData wsData = getData(attachments);
        Map<String, byte[]> files = getFiles(attachments);
        ReportInfo info = wsData.toReportInfo().withId(service.getByIdOrCode(reportId).getId()).build();
        ReportData reportData;
        if (files.isEmpty()) {
            reportData = service.updateReportInfo(info);
        } else {
            reportData = service.updateReport(info, files);
        }
        return response(serializeDetailedReport(reportData));
    }

    @PUT
    @Path("{" + REPORT_ID + "}/template")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object updateReportTemplate(@PathParam(REPORT_ID) String reportId, List<Attachment> attachments) {
        checkNotNull(attachments);
        Map<String, byte[]> files = getFiles(attachments);
        ReportData reportData = service.updateReportTemplate(service.getByIdOrCode(reportId).getId(), files);
        return response(serializeDetailedReport(reportData));
    }

    @GET
    @Path("{" + REPORT_ID + "}/template{x:/?[^/]*}")
    @Produces(APPLICATION_OCTET_STREAM)
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public DataHandler downloadTemplateFiles(@PathParam(REPORT_ID) Long reportId) {
        return service.executeReportAndDownload(reportId.toString(), ReportFormat.ZIP);
    }

    @DELETE
    @Path("{" + REPORT_ID + "}/")
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object deleteReport(@PathParam(REPORT_ID) Long reportId) {
        service.deleteReport(reportId);
        return success();
    }

    private FluentMap<String, Object> serializeMinimalReport(ReportInfo report) {
        return map(
                "_id", report.getId(),
                "code", report.getCode(),
                "description", report.getDescription(),
                "_description_translation", translation.translateReportDesciption(report.getCode(), report.getDescription()),
                "active", report.isActive()
        );
    }

    private Object serializeDetailedReport(ReportInfo report) {
        ReportData reportData = report instanceof ReportData ? ((ReportData) report) : service.getReportData(report.getId());
        return serializeMinimalReport(report).with(
                "title", report.getCode(),
                "query", reportData.getQuery()
        //				"groups", reportData.getGroups().stream().sorted(Ordering.natural()).collect(toList())
        //				"type", report.getType()
        );
    }

    private static WsReportData getData(List<Attachment> attachments) {
        return fromJson(readToString(attachments.stream().filter((a) -> a.getContentType().isCompatible(MediaType.APPLICATION_JSON_TYPE)).collect(onlyElement()).getDataHandler()), WsReportData.class);
    }

    private static Map<String, byte[]> getFiles(List<Attachment> attachments) {
        return ReportFilesUtils.unpackReportFiles(attachments.stream()
                .filter((a) -> !a.getContentType().isCompatible(MediaType.APPLICATION_JSON_TYPE))
                .map(Attachment::getDataHandler).collect(toMap(DataHandler::getName, CmIoUtils::toByteArray)));
    }

    public static class WsReportData {

        private final String description, code;
        private final boolean isActive;

        public WsReportData(@JsonProperty("description") String description,
                @JsonProperty("code") String code,
                @JsonProperty("active") Boolean isActive) {
            this.description = description;
            this.code = checkNotBlank(code, "missing code param");
            this.isActive = isActive;
        }

        public ReportInfoImpl.ReportInfoImplBuilder toReportInfo() {
            return ReportInfoImpl.builder()
                    .withActive(isActive)
                    .withCode(code)
                    .withDescription(description);
        }
    }
}
