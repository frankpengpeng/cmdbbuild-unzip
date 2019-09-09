/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email.job;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;
import javax.annotation.Nullable;
import static org.cmdbuild.email.job.EmailReaderConfig.EmailReaderFilterType.FT_NONE;
import static org.cmdbuild.email.utils.EmailUtils.parseWorkflowMappingParam;
import org.cmdbuild.jobs.JobData;
import org.cmdbuild.jobs.JobRunContext;
import org.cmdbuild.jobs.JobRunner;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmailJobRunnerImpl implements JobRunner {

    public static final String EMAIL_JOB_TYPE = "emailService";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final EmailReceiverService emailReaderService;

    public EmailJobRunnerImpl(EmailReceiverService emailReaderService) {
        this.emailReaderService = checkNotNull(emailReaderService);
    }

    @Override
    public String getName() {
        return EMAIL_JOB_TYPE;
    }

    @Override
    public void runJob(JobData jobData, JobRunContext jobContext) {
        EmailReaderConfig config = new EmailReaderConfigImpl(jobData);
        emailReaderService.receiveEmailsWithConfig(config);
    }

    private class EmailReaderConfigImpl implements EmailReaderConfig {

        private final EmailReaderFilterType filterType;
        private final String accountName, folderIncoming, folderRejected, folderProcessed,
                filterRegexpFrom, filterRegexpSubject, filterFunctionName,
                actionWorkflowClassName, actionAttachmentsCategory, actionNotificationTemplate,
                actionWorkflowAttachmentsCategory, actionWorkflowPerformerUsername;
        private final JobData job;
        private final boolean filterReject,
                actionWorkflowActive, actionWorkflowAdvance, actionAttachmentsActive, actionNotificationActive,
                actionWorkflowAttachmentsSave;
        private final Map<String, String> actionWorkflowFieldsMapping;
        private final MapperConfig mapperConfig;

        public EmailReaderConfigImpl(JobData jobData) {
            checkArgument(jobData.isOfType(EMAIL_JOB_TYPE), "invalid type of job data = %s", jobData);
            job = checkNotNull(jobData);
            Map<String, String> config = (Map) jobData.getConfig();
            filterType = parseEnumOrDefault(config.get("filter_type"), FT_NONE);
            accountName = config.get("account_name");
            filterReject = toBooleanOrDefault(config.get("filter_reject"), false);
            folderIncoming = checkNotBlank(config.get("folder_incoming"));
            folderRejected = config.get("folder_rejected");
            folderProcessed = checkNotBlank(config.get("folder_processed"));
            filterRegexpFrom = config.get("filter_regex_from");
            filterFunctionName = config.get("filter_function_name");
            filterRegexpSubject = config.get("filter_regex_subject");
            actionWorkflowActive = toBooleanOrDefault(config.get("action_workflow_active"), false);
            actionWorkflowAdvance = toBooleanOrDefault(config.get("action_workflow_advance"), false);
            actionAttachmentsActive = toBooleanOrDefault(config.get("action_attachments_active"), false);
            actionNotificationActive = toBooleanOrDefault(config.get("action_notification_active"), false);
            actionWorkflowAttachmentsSave = toBooleanOrDefault(config.get("action_workflow_attachmentssave"), false);
            actionWorkflowClassName = config.get("action_workflow_class_name");
            actionWorkflowPerformerUsername = config.get("action_workflow_performer_username");
            actionAttachmentsCategory = config.get("action_attachments_category");
            actionNotificationTemplate = config.get("action_notification_template");
            actionWorkflowFieldsMapping = parseWorkflowMappingParam(config.get("action_workflow_fields_mapping"));
            actionWorkflowAttachmentsCategory = config.get("action_workflow_attachmentscategory");
            mapperConfig = new MapperConfigImpl(config);
        }

        @Override
        public JobData getJob() {
            return job;
        }

        @Override
        public MapperConfig getMapperConfig() {
            return mapperConfig;
        }

        @Override
        public EmailReaderFilterType getFilterType() {
            return filterType;
        }

        @Override
        @Nullable
        public String getAccountName() {
            return accountName;
        }

        @Override
        public String getFolderIncoming() {
            return folderIncoming;
        }

        @Nullable
        @Override
        public String getFolderRejected() {
            return folderRejected;
        }

        @Override
        public String getFolderProcessed() {
            return folderProcessed;
        }

        @Nullable
        @Override
        public String getFilterRegexpFrom() {
            return filterRegexpFrom;
        }

        @Nullable
        @Override
        public String getFilterRegexpSubject() {
            return filterRegexpSubject;
        }

        @Nullable
        @Override
        public String getFilterFunctionName() {
            return filterFunctionName;
        }

        @Nullable
        @Override
        public String getActionWorkflowClassName() {
            return actionWorkflowClassName;
        }

        @Nullable
        @Override
        public String getActionWorkflowPerformerUsername() {
            return actionWorkflowPerformerUsername;
        }

        @Nullable
        @Override
        public String getActionAttachmentsCategory() {
            return actionAttachmentsCategory;
        }

        @Nullable
        @Override
        public String getNotificationTemplate() {
            return actionNotificationTemplate;
        }

        @Override
        public Map<String, String> getActionWorkflowFieldsMapping() {
            return actionWorkflowFieldsMapping;
        }

        @Nullable
        @Override
        public String getActionWorkflowAttachmentsCategory() {
            return actionWorkflowAttachmentsCategory;
        }

        @Override
        public boolean moveToRejectedOnError() {
            return filterReject;
        }

        @Override
        public boolean isActionWorkflowActive() {
            return actionWorkflowActive;
        }

        @Override
        public boolean isActionWorkflowAdvance() {
            return actionWorkflowAdvance;
        }

        @Override
        public boolean isActionAttachmentsActive() {
            return actionAttachmentsActive;
        }

        @Override
        public boolean isActionNotificationActive() {
            return actionNotificationActive;
        }

        @Override
        public boolean isActionWorkflowAttachmentsSave() {
            return actionWorkflowAttachmentsSave;
        }

        @Override
        public String toString() {
            return "EmailReaderConfigImpl{" + "account=" + accountName + ", job=" + job + '}';
        }

    }

}
