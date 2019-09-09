/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl;

import static com.google.common.base.Objects.equal;
import javax.annotation.Nullable;
import static org.cmdbuild.etl.ImportExportColumnMode.IECM_IGNORE;

public interface ImportExportColumnConfig {

    String getAttributeName();

    String getColumnName();

    ImportExportColumnMode getMode();

    @Nullable
    String getDefault();

    default boolean ignoreColumn() {
        return equal(getMode(), IECM_IGNORE);
    }

    default boolean doNotIgnoreColumn() {
        return !ignoreColumn();
    }

}
