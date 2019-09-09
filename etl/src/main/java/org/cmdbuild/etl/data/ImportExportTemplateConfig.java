/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import static com.google.common.base.Objects.equal;
import org.cmdbuild.etl.ImportExportFileFormat;
import java.util.List;
import javax.annotation.Nullable;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.etl.ImportExportColumnConfig;
import org.cmdbuild.etl.ImportExportMergeMode;
import org.cmdbuild.etl.ImportExportTemplateTarget;
import static org.cmdbuild.etl.ImportExportTemplateTarget.IET_CLASS;
import org.cmdbuild.etl.ImportExportTemplateType;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import org.cmdbuild.utils.lang.JsonBean;

@JsonBean(ImportExportTemplateConfigImpl.class)
public interface ImportExportTemplateConfig {

    ImportExportTemplateTarget getTargetType();

    String getTargetName();

    List<ImportExportColumnConfig> getColumns();

    ImportExportMergeMode getMergeMode();

    ImportExportTemplateType getType();

    ImportExportFileFormat getFileFormat();

    @Nullable
    String getAttributeNameForUpdateAttrOnMissing();

    @Nullable
    String getAttributeValueForUpdateAttrOnMissing();

    @Nullable
    String getExportFilter();

    @Nullable
    String getCsvSeparator();

    @Nullable
    String getImportKeyAttribute();

    boolean getUseHeader();

    boolean getIgnoreColumnOrder();

    @Nullable
    Integer getHeaderRow();

    @Nullable
    Integer getDataRow();

    @Nullable
    Integer getFirstCol();

    @JsonIgnore
    default boolean isExportTemplate() {
        switch (getType()) {
            case IETT_EXPORT:
            case IETT_IMPORT_EXPORT:
                return true;
            default:
                return false;
        }
    }

    @JsonIgnore
    default boolean isImportTemplate() {
        switch (getType()) {
            case IETT_IMPORT:
            case IETT_IMPORT_EXPORT:
                return true;
            default:
                return false;
        }
    }

    @JsonIgnore
    default boolean getSkipUnknownColumns() {
        return getIgnoreColumnOrder();
    }

    default ImportExportColumnConfig getColumnByAttrName(String name) {
        return getColumns().stream().filter(c -> equal(c.getAttributeName(), name)).collect(onlyElement("column not found for attr name = %s", name));
    }

    default boolean hasMergeMode(ImportExportMergeMode mode) {
        return equal(mode, getMergeMode());
    }

    default boolean hasTarget(Classe classe) {
        return equal(getTargetType(), IET_CLASS) && equal(getTargetName(), classe.getName());
    }
}
