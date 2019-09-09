package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map.Entry;
import static java.util.stream.Collectors.toList;
import javax.activation.DataHandler;
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
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import static org.cmdbuild.auth.login.AuthorityConst.HAS_ADMIN_ACCESS_AUTHORITY;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.cmdbuild.etl.ImportExportColumnConfig;
import org.cmdbuild.etl.ImportExportColumnConfigImpl;
import org.cmdbuild.etl.ImportExportColumnMode;
import org.cmdbuild.etl.ImportExportFileFormat;
import org.cmdbuild.etl.ImportExportMergeMode;
import org.cmdbuild.etl.ImportExportOperationResult;
import org.cmdbuild.etl.ImportExportService;
import org.cmdbuild.etl.ImportExportTemplate;
import org.cmdbuild.etl.ImportExportTemplateImpl;
import org.cmdbuild.etl.ImportExportTemplateImpl.ImportExportTemplateImplBuilder;
import org.cmdbuild.etl.ImportExportTemplateTarget;
import static org.cmdbuild.etl.ImportExportTemplateTarget.IET_CLASS;
import org.cmdbuild.etl.ImportExportTemplateType;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DETAILED;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;
import static org.cmdbuild.utils.io.CmIoUtils.toDataSource;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import org.springframework.security.access.prepost.PreAuthorize;

@Path("etl/templates/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class ImportExportTemplateWs {

    private final ImportExportService service;

    public ImportExportTemplateWs(ImportExportService service) {
        this.service = checkNotNull(service);
    }

    @GET
    @Path(EMPTY)
    public Object readAll(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @QueryParam(LIMIT) Long limit, @QueryParam(START) Long offset, @QueryParam(DETAILED) @DefaultValue(FALSE) boolean detailed) {
        List<ImportExportTemplate> templates = service.getAllForUser();
        if (!isAdminViewMode(viewMode)) {
            templates = templates.stream().filter(ImportExportTemplate::isActive).collect(toList());
        }
        return response(paged(templates, offset, limit).map(detailed ? this::serializeDetailedTemplate : this::serializeBasicTemplate));
    }

    @GET
    @Path("by-class/{classId}")
    public Object readAllForClass(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @PathParam("classId") String classId, @QueryParam(LIMIT) Long limit, @QueryParam(START) Long offset,
            @QueryParam(DETAILED) @DefaultValue(FALSE) boolean detailed, @QueryParam("include_related_domains") @DefaultValue(FALSE) boolean includeRelatedDomains) {
        List<ImportExportTemplate> templates;
        if (includeRelatedDomains) {
            templates = service.getForUserForTargetClassAndRelatedDomains(classId);
        } else {
            templates = service.getForUserForTarget(IET_CLASS, classId);
        }
        if (!isAdminViewMode(viewMode)) {
            templates = templates.stream().filter(ImportExportTemplate::isActive).collect(toList());
        }
        return response(paged(templates, offset, limit).map(detailed ? this::serializeDetailedTemplate : this::serializeBasicTemplate));
    }

    @GET
    @Path("{templateId}/")
    public Object read(@PathParam("templateId") Long id) {
        ImportExportTemplate template = service.getOne(id);
        return response(serializeDetailedTemplate(template));
    }

    @GET
    @Path("{templateId}/export{x:(/.*)?}")
    public DataHandler executeExportTemplate(@PathParam("templateId") Long id) {
        return new DataHandler(service.exportDataWithTemplate(id));
    }

    @POST
    @Path("{templateId}/import")
    @Consumes(MULTIPART_FORM_DATA)
    public Object executeImportTemplate(@PathParam("templateId") Long id, @Multipart(value = FILE, required = true) DataHandler dataHandler) {
        ImportExportOperationResult result = service.importDataWithTemplate(toDataSource(dataHandler), id);
        return response(map(
                "hasErrors", result.hasErrors(),
                "created", result.getCreatedRecordCount(),
                "modified", result.getModifiedRecordCount(),
                "unmodified", result.getUnmodifiedRecordCount(),
                "deleted", result.getDeletedRecordCount(),
                "processed", result.getProcessedRecordCount(),
                "errors", result.getErrors().stream().map(e -> map(
                "recordNumber", e.getRecordIndex(),
                "lineNumber", e.getRecordLineNumber(),
                "record", e.getRecordData().stream().map(Entry::getValue).collect(toList()),
                "message", e.getUserErrorMessage(),
                "techMessage", e.getTechErrorMessage()
        )).collect(toList())));
    }

    @POST
    @Path(EMPTY)
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object create(WsImportExportTemplateData data) {
        return response(serializeDetailedTemplate(service.create(data.toImportExportTemplate().build())));
    }

    @PUT
    @Path("{templateId}/")
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object update(@PathParam("templateId") Long templateId, WsImportExportTemplateData data) {
        return response(serializeDetailedTemplate(service.update(data.toImportExportTemplate().withId(templateId).build())));
    }

    @DELETE
    @Path("{templateId}/")
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object delete(@PathParam("templateId") Long templateId) {
        service.delete(templateId);
        return success();
    }

    private FluentMap serializeBasicTemplate(ImportExportTemplate template) {
        return map(
                "_id", template.getId(),
                "code", template.getCode(),
                "description", template.getDescription(),
                "targetType", serializeEnum(template.getTargetType()),
                "targetName", template.getTargetName(),
                "active", template.isActive(),
                "type", serializeEnum(template.getType()),
                "_export", template.isExportTemplate(),
                "_import", template.isImportTemplate()
        );
    }

    private FluentMap serializeDetailedTemplate(ImportExportTemplate template) {
        return serializeBasicTemplate(template).with(
                "fileFormat", serializeEnum(template.getFileFormat()),
                "errorEmailTemplate", template.getErrorEmailTemplateId(),
                "errorEmailAccount", template.getErrorEmailAccountId(),
                "exportFilter", template.getExportFilter(),
                "mergeMode", serializeEnum(template.getMergeMode()),
                "mergeMode_when_missing_update_attr", template.getAttributeNameForUpdateAttrOnMissing(),
                "mergeMode_when_missing_update_value", template.getAttributeValueForUpdateAttrOnMissing(),
                "csv_separator", template.getCsvSeparator(),
                "importKeyAttribute", template.getImportKeyAttribute(),
                "useHeader", template.getUseHeader(),
                "ignoreColumnOrder", template.getIgnoreColumnOrder(),
                "headerRow", template.getHeaderRow(),
                "dataRow", template.getDataRow(),
                "firstCol", template.getFirstCol(),
                "columns", template.getColumns().stream().map(c -> map(
                "attribute", c.getAttributeName(),
                "columnName", c.getColumnName(),
                "default", c.getDefault(),
                "mode", serializeEnum(c.getMode())
        )).collect(toList()));
    }

    public static class WsImportExportTemplateData {

        private final Long errorEmailTemplate, errorEmailAccount;
        private final String code, description, targetName, exportFilter, mergeModeUpdateAttr, mergeModeUpdateValue, csvSeparator, importKeyAttribute;
        private final Boolean active;
        private final ImportExportTemplateType type;
        private final ImportExportMergeMode mergeMode;
        private final ImportExportTemplateTarget targetType;
        private final ImportExportFileFormat fileFormat;
        private final List<WsImportExportTemplateColumnData> columns;
        private final Boolean useHeader, ignoreColumnOrder;
        private final Integer headerRow, dataRow, firstCol;

        public WsImportExportTemplateData(
                @JsonProperty("errorEmailTemplate") Long errorEmailTemplate,
                @JsonProperty("errorEmailAccount") Long errorEmailAccount,
                @JsonProperty("fileFormat") String fileFormat,
                @JsonProperty("code") String code,
                @JsonProperty("description") String description,
                @JsonProperty("targetName") String targetName,
                @JsonProperty("targetType") String targetType,
                @JsonProperty("exportFilter") String exportFilter,
                @JsonProperty("mergeMode") String mergeMode,
                @JsonProperty("mergeMode_when_missing_update_attr") String mergeModeUpdateAttr,
                @JsonProperty("mergeMode_when_missing_update_value") String mergeModeUpdateValue,
                @JsonProperty("active") Boolean active,
                @JsonProperty("type") String type,
                @JsonProperty("useHeader") Boolean useHeader,
                @JsonProperty("ignoreColumnOrder") Boolean ignoreColumnOrder,
                @JsonProperty("headerRow") Integer headerRow,
                @JsonProperty("dataRow") Integer dataRow,
                @JsonProperty("firstCol") Integer firstCol,
                @JsonProperty("csv_separator") String csvSeparator,
                @JsonProperty("importKeyAttribute") String importKeyAttribute,
                @JsonProperty("columns") List<WsImportExportTemplateColumnData> columns) {
            this.errorEmailTemplate = errorEmailTemplate;
            this.errorEmailAccount = errorEmailAccount;
            this.code = code;
            this.description = description;
            this.targetName = targetName;
            this.targetType = parseEnum(targetType, ImportExportTemplateTarget.class);
            this.exportFilter = exportFilter;
            this.mergeModeUpdateAttr = mergeModeUpdateAttr;
            this.mergeModeUpdateValue = mergeModeUpdateValue;
            this.active = active;
            this.type = parseEnum(type, ImportExportTemplateType.class);
            this.mergeMode = parseEnumOrNull(mergeMode, ImportExportMergeMode.class);
            this.columns = firstNotNull(columns, emptyList());
            this.fileFormat = parseEnum(fileFormat, ImportExportFileFormat.class);
            this.csvSeparator = csvSeparator;
            this.importKeyAttribute = importKeyAttribute;
            this.useHeader = useHeader;
            this.headerRow = headerRow;
            this.dataRow = dataRow;
            this.firstCol = firstCol;
            this.ignoreColumnOrder = ignoreColumnOrder;
        }

        public ImportExportTemplateImplBuilder toImportExportTemplate() {
            return ImportExportTemplateImpl.builder()
                    .withCode(code)
                    .withDescription(description)
                    .withActive(active)
                    .withAttributeNameForUpdateAttrOnMissing(mergeModeUpdateAttr)
                    .withAttributeValueForUpdateAttrOnMissing(mergeModeUpdateValue)
                    .withErrorEmailAccountId(errorEmailAccount)
                    .withErrorEmailTemplateId(errorEmailTemplate)
                    .withExportFilter(exportFilter)
                    .withMergeMode(mergeMode)
                    .withTargetName(targetName)
                    .withTargetType(targetType)
                    .withType(type)
                    .withFileFormat(fileFormat)
                    .withCsvSeparator(csvSeparator)
                    .withImportKeyAttribute(importKeyAttribute)
                    .withUseHeader(useHeader)
                    .withIgnoreColumnOrder(ignoreColumnOrder)
                    .withHeaderRow(headerRow)
                    .withDataRow(dataRow)
                    .withFirstCol(firstCol)
                    .withColumns(columns.stream().map(WsImportExportTemplateColumnData::toColumnConfig).collect(toImmutableList()));
        }
    }

    public static class WsImportExportTemplateColumnData {

        private final String attribute, columnName, defaultValue;
        private final ImportExportColumnMode mode;

        public WsImportExportTemplateColumnData(
                @JsonProperty("attribute") String attribute,
                @JsonProperty("columnName") String columnName,
                @JsonProperty("default") String defaultValue,
                @JsonProperty("mode") String mode) {
            this.attribute = attribute;
            this.columnName = columnName;
            this.defaultValue = defaultValue;
            this.mode = parseEnumOrNull(mode, ImportExportColumnMode.class);
        }

        public ImportExportColumnConfig toColumnConfig() {
            return ImportExportColumnConfigImpl.builder()
                    .withAttributeName(attribute)
                    .withColumnName(columnName)
                    .withDefault(defaultValue)
                    .withMode(mode)
                    .build();
        }
    }
}
