/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.data;

import org.cmdbuild.etl.ImportExportFileFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import static com.google.common.base.Preconditions.checkArgument;
import java.util.List;
import org.cmdbuild.etl.ImportExportColumnConfig;
import org.cmdbuild.etl.ImportExportMergeMode;
import org.cmdbuild.etl.ImportExportTemplateTarget;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.equalTo;
import com.google.common.collect.ImmutableList;
import static com.google.common.collect.Maps.uniqueIndex;
import static java.util.Collections.emptyList;
import java.util.Set;
import javax.annotation.Nullable;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDOBJ1;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDOBJ2;
import org.cmdbuild.etl.EtlException;
import org.cmdbuild.etl.ImportExportColumnConfigImpl;
import static org.cmdbuild.etl.ImportExportMergeMode.IEM_LEAVE_MISSING;
import static org.cmdbuild.etl.ImportExportTemplateTarget.IET_DOMAIN;
import org.cmdbuild.etl.ImportExportTemplateType;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmNullableUtils.ltEqZeroToNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@JsonDeserialize(builder = ImportExportTemplateConfigImpl.ImportExportTemplateConfigImplBuilder.class)
public class ImportExportTemplateConfigImpl implements ImportExportTemplateConfig {

    private final ImportExportTemplateTarget targetType;
    private final String targetName;
    private final List<ImportExportColumnConfig> columns;
    private final ImportExportMergeMode mergeMode;
    private final ImportExportTemplateType type;
    private final ImportExportFileFormat format;
    private final String attributeNameForUpdateAttrOnMissing, attributeValueForUpdateAttrOnMissing, exportFilter, csvSeparator, importKeyAttribute;
    private final boolean useHeader, ignoreColumnOrder;
    private final Integer headerRow, dataRow, firstCol;

    private ImportExportTemplateConfigImpl(ImportExportTemplateConfigImplBuilder builder) {
        this.targetType = checkNotNull(builder.targetType, "target type is null");
        this.type = checkNotNull(builder.type, "template type is null");
        this.format = checkNotNull(builder.format, "file format is null");
        this.targetName = checkNotBlank(builder.targetName, "target name is null");
        this.columns = ImmutableList.copyOf(firstNotNull(builder.columns, emptyList()));
        Set<String> attrs = uniqueIndex(columns, ImportExportColumnConfig::getAttributeName).keySet();
        this.csvSeparator = builder.csvSeparator;
        if (isImportTemplate()) {
            switch (targetType) {
                case IET_DOMAIN:
                    checkArgument(attrs.contains(ATTR_IDOBJ1), "missing required attr = %s for domain template", ATTR_IDOBJ1);
                    checkArgument(attrs.contains(ATTR_IDOBJ2), "missing required attr = %s for domain template", ATTR_IDOBJ2);
                    this.importKeyAttribute = null;
                    break;
                case IET_CLASS:
                    this.importKeyAttribute = checkNotBlank(builder.importKeyAttribute, "import key attr is null");
                    checkArgument(columns.stream().map(ImportExportColumnConfig::getAttributeName).anyMatch(equalTo(importKeyAttribute)), "invalid key attr = %s", importKeyAttribute);
                    break;
                default:
                    throw new EtlException("unsupported target type = %s", targetType);
            }
            this.mergeMode = firstNotNull(builder.mergeMode, IEM_LEAVE_MISSING);
            switch (mergeMode) {
                case IEM_UPDATE_ATTR_ON_MISSING:
                    attributeNameForUpdateAttrOnMissing = checkNotBlank(builder.attributeNameForUpdateAttrOnMissing);
                    attributeValueForUpdateAttrOnMissing = checkNotNull(builder.attributeValueForUpdateAttrOnMissing);
                    break;
                default:
                    attributeNameForUpdateAttrOnMissing = null;
                    attributeValueForUpdateAttrOnMissing = null;
            }
        } else {
            this.importKeyAttribute = null;
            this.mergeMode = IEM_LEAVE_MISSING;
            attributeNameForUpdateAttrOnMissing = null;
            attributeValueForUpdateAttrOnMissing = null;
        }
        if (isExportTemplate()) {
            this.exportFilter = builder.exportFilter;
        } else {
            this.exportFilter = null;
        }
        this.headerRow = ltEqZeroToNull(builder.headerRow);
        this.dataRow = ltEqZeroToNull(builder.dataRow);
        this.firstCol = ltEqZeroToNull(builder.firstCol);
        this.useHeader = firstNotNull(builder.useHeader, columns.isEmpty() || columns.stream().anyMatch(c -> isNotBlank(c.getColumnName())));
        this.ignoreColumnOrder = firstNotNull(builder.ignoreColumnOrder, false);
        checkArgument(ignoreColumnOrder == false || (useHeader == true && columns.stream().allMatch(c -> isNotBlank(c.getColumnName()))), "invalid param ignoreColumnOrder with incomplete/missing header config");
    }

    @Override
    public boolean getUseHeader() {
        return useHeader;
    }

    @Override
    public boolean getIgnoreColumnOrder() {
        return ignoreColumnOrder;
    }

    @Override
    @Nullable
    public Integer getHeaderRow() {
        return headerRow;
    }

    @Override
    @Nullable
    public Integer getDataRow() {
        return dataRow;
    }

    @Override
    @Nullable
    public Integer getFirstCol() {
        return firstCol;
    }

    @Override
    @JsonIgnore
    public ImportExportTemplateTarget getTargetType() {
        return targetType;
    }

    @JsonProperty("targetType")
    public String getTargetTypeAsString() {
        return serializeEnum(targetType);
    }

    @Override
    @JsonIgnore
    public ImportExportFileFormat getFileFormat() {
        return format;
    }

    @JsonProperty("format")
    public String getFileFormatAsString() {
        return serializeEnum(format);
    }

    @Override
    public String getTargetName() {
        return targetName;
    }

    @Override
    public List<ImportExportColumnConfig> getColumns() {
        return columns;
    }

    @Override
    @JsonIgnore
    public ImportExportMergeMode getMergeMode() {
        return mergeMode;
    }

    @JsonProperty("mergeMode")
    public String getMergeModeAsString() {
        return serializeEnum(mergeMode);
    }

    @Override
    @Nullable
    public String getAttributeNameForUpdateAttrOnMissing() {
        return attributeNameForUpdateAttrOnMissing;
    }

    @Override
    @Nullable
    public String getAttributeValueForUpdateAttrOnMissing() {
        return attributeValueForUpdateAttrOnMissing;
    }

    @Override
    @Nullable
    public String getExportFilter() {
        return exportFilter;
    }

    @Override
    @JsonIgnore
    public ImportExportTemplateType getType() {
        return type;
    }

    @JsonProperty("type")
    public String getTypeAsString() {
        return serializeEnum(type);
    }

    @Override
    @Nullable
    public String getCsvSeparator() {
        return csvSeparator;
    }

    @Override
    @Nullable
    public String getImportKeyAttribute() {
        return importKeyAttribute;
    }

    public static ImportExportTemplateConfigImplBuilder builder() {
        return new ImportExportTemplateConfigImplBuilder();
    }

    public static ImportExportTemplateConfigImplBuilder copyOf(ImportExportTemplateConfig source) {
        return new ImportExportTemplateConfigImplBuilder()
                .withTargetType(source.getTargetType())
                .withTargetName(source.getTargetName())
                .withColumns(source.getColumns())
                .withMergeMode(source.getMergeMode())
                .withAttributeNameForUpdateAttrOnMissing(source.getAttributeNameForUpdateAttrOnMissing())
                .withAttributeValueForUpdateAttrOnMissing(source.getAttributeValueForUpdateAttrOnMissing())
                .withExportFilter(source.getExportFilter())
                .withFileFormat(source.getFileFormat())
                .withType(source.getType())
                .withCsvSeparator(source.getCsvSeparator())
                .withImportKeyAttribute(source.getImportKeyAttribute())
                .withUseHeader(source.getUseHeader())
                .withIgnoreColumnOrder(source.getIgnoreColumnOrder())
                .withHeaderRow(source.getHeaderRow())
                .withDataRow(source.getDataRow())
                .withFirstCol(source.getFirstCol());
    }

    public static class ImportExportTemplateConfigImplBuilder implements Builder<ImportExportTemplateConfigImpl, ImportExportTemplateConfigImplBuilder> {

        private ImportExportTemplateTarget targetType;
        private String targetName;
        private List<? extends ImportExportColumnConfig> columns;
        private ImportExportMergeMode mergeMode;
        private String attributeNameForUpdateAttrOnMissing;
        private String attributeValueForUpdateAttrOnMissing;
        private String exportFilter, csvSeparator, importKeyAttribute;
        private ImportExportTemplateType type;
        private ImportExportFileFormat format;
        private Boolean useHeader, ignoreColumnOrder;
        private Integer headerRow, dataRow, firstCol;

        public ImportExportTemplateConfigImplBuilder withTargetType(ImportExportTemplateTarget targetType) {
            this.targetType = targetType;
            return this;
        }

        public ImportExportTemplateConfigImplBuilder withUseHeader(Boolean useHeader) {
            this.useHeader = useHeader;
            return this;
        }

        public ImportExportTemplateConfigImplBuilder withIgnoreColumnOrder(Boolean ignoreColumnOrder) {
            this.ignoreColumnOrder = ignoreColumnOrder;
            return this;
        }

        public ImportExportTemplateConfigImplBuilder withHeaderRow(Integer headerRow) {
            this.headerRow = headerRow;
            return this;
        }

        public ImportExportTemplateConfigImplBuilder withDataRow(Integer dataRow) {
            this.dataRow = dataRow;
            return this;
        }

        public ImportExportTemplateConfigImplBuilder withFirstCol(Integer firstCol) {
            this.firstCol = firstCol;
            return this;
        }

        public ImportExportTemplateConfigImplBuilder withType(ImportExportTemplateType type) {
            this.type = type;
            return this;
        }

        public ImportExportTemplateConfigImplBuilder withFileFormat(ImportExportFileFormat format) {
            this.format = format;
            return this;
        }

        public ImportExportTemplateConfigImplBuilder withTargetName(String targetName) {
            this.targetName = targetName;
            return this;
        }

        public ImportExportTemplateConfigImplBuilder withCsvSeparator(String csvSeparator) {
            this.csvSeparator = csvSeparator;
            return this;
        }

        public ImportExportTemplateConfigImplBuilder withImportKeyAttribute(String importKeyAttribute) {
            this.importKeyAttribute = importKeyAttribute;
            return this;
        }

        @JsonDeserialize(contentAs = ImportExportColumnConfigImpl.class)
        public ImportExportTemplateConfigImplBuilder withColumns(List<? extends ImportExportColumnConfig> columns) {
            this.columns = columns;
            return this;
        }

        public ImportExportTemplateConfigImplBuilder withMergeMode(ImportExportMergeMode mergeMode) {
            this.mergeMode = mergeMode;
            return this;
        }

        @JsonSetter("mergeMode")
        public ImportExportTemplateConfigImplBuilder withMergeModeAsString(String mergeMode) {
            return this.withMergeMode(parseEnum(mergeMode, ImportExportMergeMode.class));
        }

        @JsonSetter("type")
        public ImportExportTemplateConfigImplBuilder withTypeAsString(String type) {
            return this.withType(parseEnum(type, ImportExportTemplateType.class));
        }

        @JsonSetter("targetType")
        public ImportExportTemplateConfigImplBuilder withTargetTypeAsString(String targetType) {
            return this.withTargetType(parseEnum(targetType, ImportExportTemplateTarget.class));
        }

        @JsonSetter("format")
        public ImportExportTemplateConfigImplBuilder withFileFormatAsString(String format) {
            return this.withFileFormat(parseEnum(format, ImportExportFileFormat.class));
        }

        public ImportExportTemplateConfigImplBuilder withAttributeNameForUpdateAttrOnMissing(String attributeNameForUpdateAttrOnMissing) {
            this.attributeNameForUpdateAttrOnMissing = attributeNameForUpdateAttrOnMissing;
            return this;
        }

        public ImportExportTemplateConfigImplBuilder withAttributeValueForUpdateAttrOnMissing(String attributeValueForUpdateAttrOnMissing) {
            this.attributeValueForUpdateAttrOnMissing = attributeValueForUpdateAttrOnMissing;
            return this;
        }

        public ImportExportTemplateConfigImplBuilder withExportFilter(String exportFilter) {
            this.exportFilter = exportFilter;
            return this;
        }

        @Override
        public ImportExportTemplateConfigImpl build() {
            return new ImportExportTemplateConfigImpl(this);
        }

    }
}
