/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl;

import java.util.List;

import static com.google.common.base.Strings.nullToEmpty;
import javax.annotation.Nullable;
import org.cmdbuild.dao.entrytype.EntryType;
import static org.cmdbuild.etl.ImportExportTemplateTarget.IET_CLASS;
import static org.cmdbuild.etl.ImportExportTemplateTarget.IET_DOMAIN;
import org.cmdbuild.etl.data.ImportExportTemplateConfig;
import org.cmdbuild.etl.data.ImportExportTemplateConfigImpl;
import org.cmdbuild.etl.data.ImportExportTemplateConfigImpl.ImportExportTemplateConfigImplBuilder;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class ImportExportTemplateImpl implements ImportExportTemplate {

    private final Long id, errorEmailTemplateId, errorEmailAccountId;
    private final String code, description;
    private final ImportExportTemplateConfig config;
    private final boolean isActive;

    private ImportExportTemplateImpl(ImportExportTemplateImplBuilder builder) {
        this.id = builder.id;
        this.errorEmailTemplateId = builder.errorEmailTemplateId;
        this.errorEmailAccountId = builder.errorEmailAccountId;
        this.code = checkNotBlank(builder.code, "template code is null");
        this.description = nullToEmpty(builder.description);
        this.isActive = firstNotNull(builder.active, true);
        this.config = builder.config.build();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    @Nullable
    public Long getErrorEmailTemplateId() {
        return errorEmailTemplateId;
    }

    @Override
    @Nullable
    public Long getErrorEmailAccountId() {
        return errorEmailAccountId;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getTargetName() {
        return config.getTargetName();
    }

    @Override
    public ImportExportTemplateType getType() {
        return config.getType();
    }

    @Override
    public ImportExportFileFormat getFileFormat() {
        return config.getFileFormat();
    }

    @Override
    @Nullable
    public String getAttributeNameForUpdateAttrOnMissing() {
        return config.getAttributeNameForUpdateAttrOnMissing();
    }

    @Override
    @Nullable
    public String getAttributeValueForUpdateAttrOnMissing() {
        return config.getAttributeValueForUpdateAttrOnMissing();
    }

    @Override
    @Nullable
    public String getExportFilter() {
        return config.getExportFilter();
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public ImportExportTemplateTarget getTargetType() {
        return config.getTargetType();
    }

    @Override
    public List<ImportExportColumnConfig> getColumns() {
        return config.getColumns();
    }

    @Override
    public ImportExportMergeMode getMergeMode() {
        return config.getMergeMode();
    }

    @Override
    @Nullable
    public String getCsvSeparator() {
        return config.getCsvSeparator();
    }

    @Override
    @Nullable
    public String getImportKeyAttribute() {
        return config.getImportKeyAttribute();
    }

    @Override
    public boolean getUseHeader() {
        return config.getUseHeader();
    }

    @Override
    public boolean getIgnoreColumnOrder() {
        return config.getIgnoreColumnOrder();
    }

    @Override
    @Nullable
    public Integer getHeaderRow() {
        return config.getHeaderRow();
    }

    @Override
    @Nullable
    public Integer getDataRow() {
        return config.getDataRow();
    }

    @Override
    @Nullable
    public Integer getFirstCol() {
        return config.getFirstCol();
    }

    @Override
    public String toString() {
        return "ImportExportTemplateImpl{" + "id=" + id + ", code=" + code + ", target=" + getTargetName() + ", format=" + serializeEnum(config.getFileFormat()) + '}';
    }

    public static ImportExportTemplateImplBuilder builder() {
        return new ImportExportTemplateImplBuilder();
    }

    public static ImportExportTemplateImplBuilder copyOf(ImportExportTemplate source) {
        return copyOf((ImportExportTemplateConfig) source)
                .withId(source.getId())
                .withErrorEmailTemplateId(source.getErrorEmailTemplateId())
                .withErrorEmailAccountId(source.getErrorEmailAccountId())
                .withCode(source.getCode())
                .withDescription(source.getDescription())
                .withActive(source.isActive());
    }

    public static ImportExportTemplateImplBuilder copyOf(ImportExportTemplateConfig source) {
        return new ImportExportTemplateImplBuilder()
                .withTargetName(source.getTargetName())
                .withAttributeNameForUpdateAttrOnMissing(source.getAttributeNameForUpdateAttrOnMissing())
                .withAttributeValueForUpdateAttrOnMissing(source.getAttributeValueForUpdateAttrOnMissing())
                .withExportFilter(source.getExportFilter())
                .withTargetType(source.getTargetType())
                .withColumns(source.getColumns())
                .withType(source.getType())
                .withFileFormat(source.getFileFormat())
                .withMergeMode(source.getMergeMode())
                .withImportKeyAttribute(source.getImportKeyAttribute())
                .withCsvSeparator(source.getCsvSeparator())
                .withUseHeader(source.getUseHeader())
                .withIgnoreColumnOrder(source.getIgnoreColumnOrder())
                .withHeaderRow(source.getHeaderRow())
                .withDataRow(source.getDataRow())
                .withFirstCol(source.getFirstCol());
    }

    public static class ImportExportTemplateImplBuilder implements Builder<ImportExportTemplateImpl, ImportExportTemplateImplBuilder> {

        private Long id;
        private Long errorEmailTemplateId;
        private Long errorEmailAccountId;
        private String code;
        private String description;
        private Boolean active;
        private final ImportExportTemplateConfigImplBuilder config = ImportExportTemplateConfigImpl.builder();

        public ImportExportTemplateImplBuilder withUseHeader(Boolean useHeader) {
            config.withUseHeader(useHeader);
            return this;
        }

        public ImportExportTemplateImplBuilder withIgnoreColumnOrder(Boolean ignoreColumnOrder) {
            config.withIgnoreColumnOrder(ignoreColumnOrder);
            return this;
        }

        public ImportExportTemplateImplBuilder withHeaderRow(Integer headerRow) {
            config.withHeaderRow(headerRow);
            return this;
        }

        public ImportExportTemplateImplBuilder withDataRow(Integer dataRow) {
            config.withDataRow(dataRow);
            return this;
        }

        public ImportExportTemplateImplBuilder withFirstCol(Integer firstCol) {
            config.withFirstCol(firstCol);
            return this;
        }

        public ImportExportTemplateImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public ImportExportTemplateImplBuilder withErrorEmailTemplateId(Long errorEmailTemplateId) {
            this.errorEmailTemplateId = errorEmailTemplateId;
            return this;
        }

        public ImportExportTemplateImplBuilder withErrorEmailAccountId(Long errorEmailAccountId) {
            this.errorEmailAccountId = errorEmailAccountId;
            return this;
        }

        public ImportExportTemplateImplBuilder withCode(String code) {
            this.code = code;
            return this;
        }

        public ImportExportTemplateImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public ImportExportTemplateImplBuilder withTargetName(String targetName) {
            config.withTargetName(targetName);
            return this;
        }

        public ImportExportTemplateImplBuilder withTarget(EntryType entryType) {
            switch (entryType.getEtType()) {
                case ET_CLASS:
                    return this.withTargetType(IET_CLASS).withTargetName(entryType.getName());
                case ET_DOMAIN:
                    return this.withTargetType(IET_DOMAIN).withTargetName(entryType.getName());
                default:
                    throw new EtlException("invalid target = %s", entryType);
            }
        }

        public ImportExportTemplateImplBuilder withAttributeNameForUpdateAttrOnMissing(String attributeNameForUpdateAttrOnMissing) {
            config.withAttributeNameForUpdateAttrOnMissing(attributeNameForUpdateAttrOnMissing);
            return this;
        }

        public ImportExportTemplateImplBuilder withAttributeValueForUpdateAttrOnMissing(String attributeValueForUpdateAttrOnMissing) {
            config.withAttributeValueForUpdateAttrOnMissing(attributeValueForUpdateAttrOnMissing);
            return this;
        }

        public ImportExportTemplateImplBuilder withExportFilter(String exportFilter) {
            config.withExportFilter(exportFilter);
            return this;
        }

        public ImportExportTemplateImplBuilder withActive(Boolean active) {
            this.active = active;
            return this;
        }

        public ImportExportTemplateImplBuilder withTargetType(ImportExportTemplateTarget targetType) {
            config.withTargetType(targetType);
            return this;
        }

        public ImportExportTemplateImplBuilder withType(ImportExportTemplateType type) {
            config.withType(type);
            return this;
        }

        public ImportExportTemplateImplBuilder withFileFormat(ImportExportFileFormat format) {
            config.withFileFormat(format);
            return this;
        }

        public ImportExportTemplateImplBuilder withColumns(List<ImportExportColumnConfig> columns) {
            config.withColumns(columns);
            return this;
        }

        public ImportExportTemplateImplBuilder withMergeMode(ImportExportMergeMode mergeMode) {
            config.withMergeMode(mergeMode);
            return this;
        }

        public ImportExportTemplateImplBuilder withCsvSeparator(String csvSeparator) {
            config.withCsvSeparator(csvSeparator);
            return this;
        }

        public ImportExportTemplateImplBuilder withImportKeyAttribute(String importKeyAttribute) {
            config.withImportKeyAttribute(importKeyAttribute);
            return this;
        }

        @Override
        public ImportExportTemplateImpl build() {
            return new ImportExportTemplateImpl(this);
        }

    }
}
