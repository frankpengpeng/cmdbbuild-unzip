/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.data;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import javax.annotation.Nullable;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@CardMapping("_ImportExportTemplate")
public class ImportExportTemplateDataImpl implements ImportExportTemplateData {

    private final Long id, errorEmailTemplateId, errorEmailAccountId;
    private final String code, description;
    private final ImportExportTemplateConfig config;
    private final boolean isActive;

    private ImportExportTemplateDataImpl(ImportExportTemplateDataImplBuilder builder) {
        this.id = builder.id;
        this.code = checkNotBlank(builder.code);
        this.description = nullToEmpty(builder.description);
        this.config = checkNotNull(builder.config);
        this.errorEmailTemplateId = builder.errorEmailTemplateId;
        this.errorEmailAccountId = builder.errorEmailAccountId;
        this.isActive = firstNotNull(builder.isActive, true);
    }

    @Override
    @Nullable
    @CardAttr(ATTR_ID)
    public Long getId() {
        return id;
    }

    @CardAttr(ATTR_CODE)
    @Override
    public String getCode() {
        return code;
    }

    @CardAttr(ATTR_DESCRIPTION)
    @Override
    public String getDescription() {
        return description;
    }

    @CardAttr("Config")
    @Override
    public ImportExportTemplateConfig getConfig() {
        return config;
    }

    @CardAttr("Template")
    @Override
    @Nullable
    public Long getErrorEmailTemplateId() {
        return errorEmailTemplateId;
    }

    @CardAttr("Account")
    @Override
    @Nullable
    public Long getErrorEmailAccountId() {
        return errorEmailAccountId;
    }

    @Override
    @CardAttr("Active")
    public boolean isActive() {
        return isActive;
    }

    public static ImportExportTemplateDataImplBuilder builder() {
        return new ImportExportTemplateDataImplBuilder();
    }

    public static ImportExportTemplateDataImplBuilder copyOf(ImportExportTemplateData source) {
        return new ImportExportTemplateDataImplBuilder()
                .withId(source.getId())
                .withCode(source.getCode())
                .withDescription(source.getDescription())
                .withConfig(source.getConfig())
                .withErrorEmailTemplateId(source.getErrorEmailTemplateId())
                .withErrorEmailAccountId(source.getErrorEmailAccountId())
                .withActive(source.isActive());
    }

    public static class ImportExportTemplateDataImplBuilder implements Builder<ImportExportTemplateDataImpl, ImportExportTemplateDataImplBuilder> {

        private Long id, errorEmailTemplateId, errorEmailAccountId;
        private String code;
        private String description;
        private ImportExportTemplateConfig config;
        private Boolean isActive;

        public ImportExportTemplateDataImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public ImportExportTemplateDataImplBuilder withCode(String code) {
            this.code = code;
            return this;
        }

        public ImportExportTemplateDataImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public ImportExportTemplateDataImplBuilder withConfig(ImportExportTemplateConfig config) {
            this.config = config;
            return this;
        }

        public ImportExportTemplateDataImplBuilder withErrorEmailTemplateId(Long errorEmailTemplateId) {
            this.errorEmailTemplateId = errorEmailTemplateId;
            return this;
        }

        public ImportExportTemplateDataImplBuilder withErrorEmailAccountId(Long errorEmailAccountId) {
            this.errorEmailAccountId = errorEmailAccountId;
            return this;
        }

        public ImportExportTemplateDataImplBuilder withActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        @Override
        public ImportExportTemplateDataImpl build() {
            return new ImportExportTemplateDataImpl(this);
        }

    }
}
