/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email.job;

import java.util.Map;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.jobs.JobData;

public interface EmailReaderConfig {

    JobData getJob();

    @Nullable
    String getAccountName();

    @Nullable
    String getActionAttachmentsCategory();

    @Nullable
    String getNotificationTemplate();

    @Nullable
    String getActionWorkflowAttachmentsCategory();

    @Nullable
    String getActionWorkflowClassName();

    @Nullable
    String getActionWorkflowPerformerUsername();

    Map<String, String> getActionWorkflowFieldsMapping();

    @Nullable
    String getFilterFunctionName();

    @Nullable
    String getFilterRegexpFrom();

    @Nullable
    String getFilterRegexpSubject();

    EmailReaderFilterType getFilterType();

    String getFolderIncoming();

    String getFolderProcessed();

    @Nullable
    String getFolderRejected();

    MapperConfig getMapperConfig();

    boolean isActionAttachmentsActive();

    boolean isActionNotificationActive();

    boolean isActionWorkflowActive();

    boolean isActionWorkflowAdvance();

    boolean isActionWorkflowAttachmentsSave();

    boolean moveToRejectedOnError();

    default boolean hasFilterRegexpFrom() {
        return isNotBlank(getFilterRegexpFrom());
    }

    default boolean hasFilterRegexpSubject() {
        return isNotBlank(getFilterRegexpSubject());
    }

    default boolean hasNotificationTemplate() {
        return isNotBlank(getNotificationTemplate());
    }

    default boolean hasAccount() {
        return isNotBlank(getAccountName());
    }

    default boolean isAggressiveInReplyToMatchingEnabled() {
        return true;
    }

    enum EmailReaderFilterType {
        FT_ISREPLY, FT_ISNOTREPLY, FT_REGEX, FT_FUNCTION, FT_NONE
    }

}
