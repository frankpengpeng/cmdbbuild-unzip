/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl;

import static java.lang.String.format;
import java.util.List;
import static java.util.stream.Collectors.joining;

public interface ImportExportOperationResult {

    long getCreatedRecordCount();

    long getModifiedRecordCount();

    long getUnmodifiedRecordCount();

    long getDeletedRecordCount();

    long getProcessedRecordCount();

    List<ImportExportOperationResultError> getErrors();

    default boolean hasErrors() {
        return !getErrors().isEmpty();
    }

    default String getErrorsDescription() {
        return getErrors().stream().map(e -> format("error at record %s : %s", e.getRecordIndex(), e.getTechErrorMessage())).collect(joining("; "));
    }

    default String getResultDescription() {
        return format("processed %s records, created: %s, modified: %s, deleted: %s, unmodified: %s", getProcessedRecordCount(), getCreatedRecordCount(), getModifiedRecordCount(), getDeletedRecordCount(), getUnmodifiedRecordCount());
    }

}
