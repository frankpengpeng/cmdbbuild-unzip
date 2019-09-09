/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.jobs;

import java.time.ZonedDateTime;
import static java.util.Collections.emptyList;
import java.util.List;
import javax.annotation.Nullable;
import org.cmdbuild.audit.ErrorMessageData;
import org.cmdbuild.audit.ErrorMessagesData;

public interface JobRun {

    @Nullable
    Long getId();

    String getJobCode();

    JobRunStatus getJobStatus();

    ZonedDateTime getTimestamp();

    boolean isCompleted();

    boolean hasErrors();

    @Nullable
    Long getElapsedTime();

    @Nullable
    ErrorMessagesData getErrorMessageData();

    @Nullable
    String getLogs();

    @Nullable
    String getNodeId();

    default List<ErrorMessageData> getErrorOrWarningEvents() {
        return getErrorMessageData() == null ? emptyList() : (List) getErrorMessageData().getData();
    }

}
