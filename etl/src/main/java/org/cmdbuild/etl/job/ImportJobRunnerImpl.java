/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.job;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import java.io.File;
import static java.lang.String.format;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;
import org.apache.commons.io.FileUtils;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.common.error.ErrorAndWarningCollectorService.marker;
import org.cmdbuild.config.api.DirectoryService;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.email.EmailAccountService;
import org.cmdbuild.email.EmailService;
import static org.cmdbuild.email.EmailStatus.ES_OUTGOING;
import org.cmdbuild.email.EmailTemplate;
import org.cmdbuild.email.EmailTemplateService;
import org.cmdbuild.email.beans.EmailImpl;
import org.cmdbuild.email.template.EmailTemplateProcessorService;
import org.cmdbuild.etl.EtlException;
import org.cmdbuild.etl.ImportExportOperationResult;
import org.cmdbuild.etl.ImportExportService;
import org.cmdbuild.etl.ImportExportTemplate;
import static org.cmdbuild.etl.job.ExportJobRunnerImpl.ExportNotificationMode.EN_NEVER;
import static org.cmdbuild.etl.job.ImportJobRunnerImpl.ImportJobSource.IS_FILE;
import static org.cmdbuild.etl.job.ImportJobRunnerImpl.ImportNotificationMode.IN_ALWAYS;
import static org.cmdbuild.etl.job.ImportJobRunnerImpl.ImportNotificationMode.IN_NEVER;
import static org.cmdbuild.etl.job.ImportJobRunnerImpl.ImportNotificationMode.IN_ON_ERRORS;
import static org.cmdbuild.etl.job.ImportJobRunnerImpl.PostImportAction.PIA_DO_NOTHING;
import org.cmdbuild.jobs.JobData;
import org.cmdbuild.jobs.JobRunContext;
import org.cmdbuild.jobs.JobRunner;
import org.cmdbuild.utils.date.CmDateUtils;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmExceptionUtils.exceptionToUserMessage;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ImportJobRunnerImpl implements JobRunner {

    public static final String IMPORT_JOB_TYPE = "import_file";

    private final static String PROCESSED_FILE_EXT = "_processed";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ImportExportService importExportService;
    private final EmailTemplateService emailTemplateService;
    private final EmailAccountService emailAccountService;
    private final EmailTemplateProcessorService emailTemplateProcessorService;
    private final EmailService emailService;
    private final DirectoryService directoryService;

    public ImportJobRunnerImpl(ImportExportService importExportService, EmailTemplateService emailTemplateService, EmailAccountService emailAccountService, EmailTemplateProcessorService emailTemplateProcessorService, EmailService emailService, DirectoryService directoryService) {
        this.importExportService = checkNotNull(importExportService);
        this.emailTemplateService = checkNotNull(emailTemplateService);
        this.emailAccountService = checkNotNull(emailAccountService);
        this.emailTemplateProcessorService = checkNotNull(emailTemplateProcessorService);
        this.emailService = checkNotNull(emailService);
        this.directoryService = checkNotNull(directoryService);
    }

    @Override
    public String getName() {
        return IMPORT_JOB_TYPE;
    }

    @Override
    public void runJob(JobData jobData, JobRunContext jobContext) {
        try {
            logger.debug("preparing import job = {}", jobData);
            new ImportJobHelper(jobData, jobContext).runImportJob();
        } catch (Exception ex) {
            throw new EtlException(ex, "error executing import job = %s", jobData);
        }
    }

    @Override
    public void vaildateJob(JobData jobData) {
        new ImportJobConfig(jobData);
    }

    private class ImportJobConfig {

        private final JobData jobData;
        private final ImportExportTemplate template;
        private final EmailTemplate emailTemplate;
        private final EmailAccount emailAccount;
        private final String directory, url, filePattern, targetDirectory;
        private final ImportJobSource source;
        private final PostImportAction postImportAction;
        private final ImportNotificationMode notificationMode;

        public ImportJobConfig(JobData jobData) {
            this.jobData = checkNotNull(jobData);
            this.template = importExportService.getTemplateByName(jobData.getConfigNotBlank("template"));
            this.source = parseEnum(jobData.getConfigNotBlank("source"), ImportJobSource.class);
            switch (source) {
                case IS_FILE:
                    directory = jobData.getConfigNotBlank("directory");
                    filePattern = toStringOrNull(jobData.getConfig().get("filePattern"));
                    url = null;
                    postImportAction = parseEnum(jobData.getConfigNotBlank("postImportAction"), PostImportAction.class);
                    switch (postImportAction) {
                        case PIA_MOVE_FILES:
                            targetDirectory = jobData.getConfigNotBlank("targetDirectory");
                            break;
                        default:
                            targetDirectory = null;
                    }
                    break;
                case IS_URL:
                    directory = null;
                    filePattern = null;
                    url = jobData.getConfigNotBlank("url");
                    postImportAction = PIA_DO_NOTHING;
                    targetDirectory = null;
                    break;
                default:
                    throw new EtlException("unsupported source = %s", source);
            }
            String emailTemplateCode = toStringOrNull(jobData.getConfig().get("errorEmailTemplate")),
                    emailAccountCode = toStringOrNull(jobData.getConfig().get("errorEmailAccount"));
            emailTemplate = isBlank(emailTemplateCode) ? null : emailTemplateService.getByName(emailTemplateCode);
            emailAccount = isBlank(emailAccountCode) ? null : emailAccountService.getAccount(emailAccountCode);
            notificationMode = parseEnumOrDefault((String) jobData.getConfig().get("notificationMode"), (ImportNotificationMode) (emailTemplate == null ? IN_NEVER : IN_ON_ERRORS));
            checkArgument(emailTemplate != null || equal(notificationMode, IN_NEVER));
        }

        public boolean failForMissingFile() {
            return true;
        }

        public boolean failForMissingDir() {
            return true;
        }

    }

    private class ImportJobHelper {

        private final ImportJobConfig config;
        private final JobRunContext context;

        public ImportJobHelper(JobData jobData, JobRunContext jobContext) {
            this.config = new ImportJobConfig(jobData);
            this.context = checkNotNull(jobContext);
        }

        public void runImportJob() {
            logger.info("executing import job = {}", config.jobData);
            byte[] data = loadDataForImport();
            if (data == null) {
                logger.info(marker(), "skip import operation: no data available for import");
            } else {
                try {
                    ImportExportOperationResult operationResult = importExportService.importDataWithTemplate(newDataSource(data, "application/octet-stream"), config.template);
                    handlePostImportAction();
                    sendMailIfConfigured(map(
                            "cm_import_failed", false,
                            "cm_import_result", operationResult,//TODO data for template
                            "cm_import_result_description", operationResult.getResultDescription(),
                            "cm_import_errors_description", operationResult.getErrorsDescription(),
                            "cm_import_processed_count", operationResult.getProcessedRecordCount(),
                            "cm_import_created_count", operationResult.getCreatedRecordCount(),
                            "cm_import_modified_count", operationResult.getModifiedRecordCount(),
                            "cm_import_deleted_count", operationResult.getDeletedRecordCount(),
                            "cm_import_unmodified_count", operationResult.getUnmodifiedRecordCount(),
                            "cm_import_errors_count", operationResult.getErrors().size(),
                            "cm_import_errors", operationResult.getErrors().stream().map(e -> map(
                            "index", e.getRecordIndex(),
                            "row", e.getRecordLineNumber(),
                            "record", e.getRecordData(),
                            "message", e.getUserErrorMessage()
                    )).collect(toList())), operationResult.hasErrors());
                } catch (Exception ex) {
                    logger.error(marker(), "import operation failed with error", ex);
                    sendMailIfConfigured(map(
                            "cm_import_failed", true,
                            "cm_import_error", ex,
                            "cm_import_error_desc", exceptionToUserMessage(ex)
                    ), true);
                    throw new EtlException(ex);
                }
            }
        }

        private void sendMailIfConfigured(Map<String, Object> mailData, boolean hasError) {
            if (equal(config.notificationMode, IN_ALWAYS) || (hasError && equal(config.notificationMode, IN_ON_ERRORS))) {
                sendMail(mailData);
            }
        }

        private void sendMail(Map<String, Object> mailData) {
            Email email = emailTemplateProcessorService.createEmailFromTemplate(config.emailTemplate, (Map) map(
                    "cm_job_run", context.getJobRunId(),
                    "cm_import_source", getSourceDebugInfoStr()
            ).with(mailData));
            email = EmailImpl.copyOf(email).withStatus(ES_OUTGOING).accept(e -> {
                if (config.emailAccount != null) {
                    e.withAccount(config.emailAccount.getId());
                }
            }).build();
            email = emailService.create(email);
            logger.debug("sent notification email = %s", email);
        }

        @Nullable
        private String getSourceDebugInfoStr() {
            switch (config.source) {
                case IS_FILE:
                    return Optional.ofNullable(getFileForImport()).map(File::getAbsolutePath).orElse(null);
                case IS_URL:
                    return config.url;
                default:
                    throw new EtlException("unsupported source = %s", config.source);
            }
        }

        @Nullable
        private byte[] loadDataForImport() {
            switch (config.source) {
                case IS_FILE:
                    return loadFileDataForImport();
                case IS_URL:
                    return loadUrlDataForImport();
                default:
                    throw new EtlException("unsupported source = %s", config.source);
            }
        }

        private byte[] loadUrlDataForImport() {
            try {
                logger.debug("load data from url = {}", config.url);
                URI uri = URI.create(config.url);
                byte[] data = toByteArray(uri.toURL().openStream());
                logger.debug("processing {} bytes from url = {}", FileUtils.byteCountToDisplaySize(data.length), config.url);
                return data;
            } catch (Exception ex) {
                throw new EtlException(ex, "error loading data from url = %s", config.url);
            }
        }

        @Nullable
        private byte[] loadFileDataForImport() {
            File fileToImport = getFileForImport();
            if (fileToImport == null) {
                return null;
            } else {
                logger.debug("found file for import = {}", fileToImport.getAbsolutePath());
                byte[] data = toByteArray(fileToImport);
                logger.debug("processing {} bytes from file = {}", FileUtils.byteCountToDisplaySize(data.length), fileToImport.getAbsolutePath());
                return data;
            }
        }

        @Nullable
        private File getFileForImport() {
            File dir = directoryService.getFileRelativeToContainerDirectoryIfAvailableAndNotAbsolute(new File(config.directory));
            if (!dir.exists()) {
                if (config.failForMissingDir()) {
                    throw new EtlException("CM: invalid source dir =< %s >", config.directory);
                } else {
                    logger.warn(marker(), "CM: invalid source dir = {}", dir.getAbsolutePath());
                    return null;
                }
            }
            List<File> files = list(dir.listFiles());
            if (isNotBlank(config.filePattern)) {
                files = files.stream().filter(f -> Pattern.compile(config.filePattern).matcher(f.getName()).find()).collect(toList());
            }
            files = files.stream().filter(f -> !f.getName().endsWith(PROCESSED_FILE_EXT)).collect(toList());
            if (files.isEmpty()) {
                if (config.failForMissingFile()) {
                    throw new EtlException("CM: no file found in dir =< %s > with pattern =< %s >", config.directory, firstNotBlank(config.filePattern, ".*"));
                } else {
                    logger.warn(marker(), "CM: no file found in dir = {} with pattern =< {} >", dir.getAbsolutePath(), firstNotBlank(config.filePattern, ".*"));
                    return null;
                }
            }
            checkArgument(files.size() == 1, "expected only one file for import job, but found many = %s", files.stream().map(File::getAbsolutePath).collect(joining(", ")));
            return getOnlyElement(files);
        }

        private void handlePostImportAction() {
            if (equal(config.source, IS_FILE)) {
                File file = checkNotNull(getFileForImport());
                switch (config.postImportAction) {
                    case PIA_DELETE_FILES:
                        deleteQuietly(file);
                        logger.debug("delete processed file = %s", file.getAbsolutePath());
                        break;
                    case PIA_DO_NOTHING:
                        break;
                    case PIA_DISABLE_FILES:
                        File targetForRename = new File(file.getParentFile(), format("%s_%s%s", file.getName(), CmDateUtils.dateTimeFileSuffix(), PROCESSED_FILE_EXT));
                        logger.debug("move processed file to %s", targetForRename.getAbsolutePath());
                        file.renameTo(targetForRename);
                        break;
                    case PIA_MOVE_FILES:
                        File targetDir = directoryService.getFileRelativeToContainerDirectoryIfAvailableAndNotAbsolute(new File(checkNotBlank(config.targetDirectory)));
                        targetDir.mkdirs();
                        checkArgument(targetDir.isDirectory(), "invalid target dir = %s", targetDir.getAbsolutePath());
                        File targetForMove = new File(targetDir, file.getName());
                        logger.debug("move processed file to %s", targetForMove.getAbsolutePath());
                        file.renameTo(targetForMove);
                        break;
                    default:
                        throw new EtlException("unsupported port import action = %s", config.postImportAction);
                }
            }
        }

    }

    public static enum ImportJobSource {
        IS_FILE, IS_URL
    }

    enum ImportNotificationMode {
        IN_ON_ERRORS, IN_ALWAYS, IN_NEVER
    }

    public static enum PostImportAction {
        PIA_DELETE_FILES, PIA_DISABLE_FILES, PIA_MOVE_FILES, PIA_DO_NOTHING
    }
}
