/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Strings.nullToEmpty;
import javax.annotation.Nullable;
import org.cmdbuild.etl.ImportExportColumnConfigImpl.ImportExportColumnConfigImplBuilder;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.etl.ImportExportColumnMode.IECM_DEFAULT;
import static org.cmdbuild.etl.ImportExportColumnMode.IECM_IGNORE;

@JsonDeserialize(builder = ImportExportColumnConfigImplBuilder.class)
public class ImportExportColumnConfigImpl implements ImportExportColumnConfig {

    private final String attributeName, columnName;
    private final ImportExportColumnMode mode;
    private final String defaultValue;

    private ImportExportColumnConfigImpl(ImportExportColumnConfigImplBuilder builder) {
        this.mode = firstNotNull(builder.mode, IECM_DEFAULT);
        this.columnName = nullToEmpty(builder.columnName);
        if (equal(mode, IECM_IGNORE)) {
            this.attributeName = "ignore";
            this.defaultValue = null;
        } else {
            this.attributeName = checkNotBlank(builder.attributeName);
            this.defaultValue = builder.defaultValue;
        }
    }

    @Override
    public String getAttributeName() {
        return attributeName;
    }

    @Override
    public String getColumnName() {
        return columnName;
    }

    @Override
    @JsonIgnore
    public ImportExportColumnMode getMode() {
        return mode;
    }

    @JsonProperty("mode")
    public String getModeAsString() {
        return serializeEnum(mode);
    }

    @Override
    @Nullable
    public String getDefault() {
        return defaultValue;
    }

    @Override
    public String toString() {
        return "ImportExportColumnConfig{" + "attributeName=" + attributeName + ", columnName=" + columnName + '}';
    }

    public static ImportExportColumnConfigImplBuilder builder() {
        return new ImportExportColumnConfigImplBuilder();
    }

    public static ImportExportColumnConfigImpl build(String attrName) {
        return builder().withAttributeName(attrName).build();
    }

    public static ImportExportColumnConfigImplBuilder copyOf(ImportExportColumnConfig source) {
        return new ImportExportColumnConfigImplBuilder()
                .withAttributeName(source.getAttributeName())
                .withColumnName(source.getColumnName())
                .withMode(source.getMode())
                .withDefault(source.getDefault());
    }

    public static class ImportExportColumnConfigImplBuilder implements Builder<ImportExportColumnConfigImpl, ImportExportColumnConfigImplBuilder> {

        private String attributeName;
        private String columnName;
        private ImportExportColumnMode mode;
        private String defaultValue;

        public ImportExportColumnConfigImplBuilder withAttributeName(String attributeName) {
            this.attributeName = attributeName;
            return this;
        }

        public ImportExportColumnConfigImplBuilder withColumnName(String columnName) {
            this.columnName = columnName;
            return this;
        }

        public ImportExportColumnConfigImplBuilder withMode(ImportExportColumnMode mode) {
            this.mode = mode;
            return this;
        }

        public ImportExportColumnConfigImplBuilder withMode(String mode) {
            this.mode = parseEnum(mode, ImportExportColumnMode.class);
            return this;
        }

        public ImportExportColumnConfigImplBuilder withDefault(String defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        @Override
        public ImportExportColumnConfigImpl build() {
            return new ImportExportColumnConfigImpl(this);
        }

    }
}
