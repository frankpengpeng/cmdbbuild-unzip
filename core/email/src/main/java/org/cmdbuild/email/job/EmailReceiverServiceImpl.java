/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email.job;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Lists.reverse;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.auth.session.SessionService;
import static org.cmdbuild.common.error.ErrorAndWarningCollectorService.marker;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.BOOLEAN;
import org.cmdbuild.dao.function.StoredFunction;
import org.cmdbuild.dao.function.StoredFunctionOutputParameter;
import org.cmdbuild.dms.DmsService;
import org.cmdbuild.dms.DocumentDataImpl;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.email.EmailAccountService;
import org.cmdbuild.email.EmailAttachment;
import org.cmdbuild.email.EmailException;
import org.cmdbuild.email.EmailService;
import static org.cmdbuild.email.EmailStatus.ES_OUTGOING;
import org.cmdbuild.email.EmailTemplate;
import org.cmdbuild.email.EmailTemplateService;
import org.cmdbuild.email.beans.EmailImpl;
import static org.cmdbuild.email.beans.EmailImpl.EMAIL_CLASS_NAME;
import org.cmdbuild.email.mta.EmailMtaService;
import org.cmdbuild.email.template.EmailTemplateProcessorService;
import static org.cmdbuild.email.utils.EmailUtils.parseCardIdFromEmailSubject;
import static org.cmdbuild.email.utils.EmailUtils.parseSubjectDescrFromEmailSubject;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import org.cmdbuild.workflow.WorkflowService;
import org.cmdbuild.workflow.model.Flow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@Component
public class EmailReceiverServiceImpl implements EmailReceiverService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String FUNCTION_PARAM_CONTENT = "content", FUNCTION_PARAM_CC_ADDRESSES = "ccAddresses", FUNCTION_PARAM_FROM_ADDRESS = "fromAddress", FUNCTION_PARAM_SUBJECT = "subject", FUNCTION_PARAM_TO_ADDRESSES = "toAddresses";

    private final DaoService dao;
    private final WorkflowService workflowService;
    private final DmsService dmsService;
    private final EmailService emailService;
    private final EmailMtaService mtaService;
    private final EmailAccountService accountService;
    private final EmailTemplateService templateService;
    private final EmailTemplateProcessorService templateProcessor;
    private final SessionService sessionService;

    public EmailReceiverServiceImpl(SessionService sessionService, WorkflowService workflowService, DmsService dmsService, DaoService dao, EmailService emailService, EmailMtaService mtaService, EmailAccountService accountService, EmailTemplateService templateService, EmailTemplateProcessorService templateProcessor) {
        this.dao = checkNotNull(dao);
        this.emailService = checkNotNull(emailService);
        this.mtaService = checkNotNull(mtaService);
        this.accountService = checkNotNull(accountService);
        this.templateService = checkNotNull(templateService);
        this.templateProcessor = checkNotNull(templateProcessor);
        this.dmsService = checkNotNull(dmsService);
        this.workflowService = checkNotNull(workflowService);
        this.sessionService = checkNotNull(sessionService);
    }

    @Override
    public void receiveEmailsWithConfig(EmailReaderConfig config) {
        try {
            new EmailsReaderHelper(config).readEmails();
        } catch (Exception ex) {
            throw new EmailException(ex, "error reading emails for config = %s", config);
        }
    }

    private class EmailsReaderHelper {

        private final EmailReaderConfig config;
        private final EmailAccount account;

        public EmailsReaderHelper(EmailReaderConfig config) {
            this.config = checkNotNull(config);
            account = accountService.getAccount(config.getAccountName());
        }

        public void readEmails() {
            logger.info("reading emails from account = {} for job = {}", account, config.getJob());
            mtaService.receive(account, config.getFolderIncoming(), config.getFolderProcessed(), config.moveToRejectedOnError() ? config.getFolderRejected() : null, this::handleReceivedEmail);
        }

        private void handleReceivedEmail(Email email) {
            new EmailReaderHelper(email).handleReceivedEmail();
        }

        private class EmailReaderHelper {

            private Email email;
            private Email inReplyTo;

            public EmailReaderHelper(Email email) {
                this.email = checkNotNull(email);
            }

            public void handleReceivedEmail() {
                try {
                    logger.debug("processing mail = {} for job = {}", email, config.getJob());
                    safe(this::handleReferencedEmail);
                    if (emailMatchesFilter()) {
                        email = emailService.create(email);
                        safe(this::handleNotification);
                        safe(this::handleAttachments);
                        safe(this::startWorkflow);
                    } else {
                        logger.debug("email does not match filter, ignoring");
                    }
                    logger.info(marker(), "processed email = {}", email);
                } catch (Exception ex) {
                    throw new EmailException(ex, "error processing email = {} for job = {}", email, config.getJob());
                }
            }

            private boolean emailMatchesFilter() {
                switch (config.getFilterType()) {
                    case FT_NONE:
                        return true;
                    case FT_ISREPLY:
                        return inReplyTo != null;
                    case FT_ISNOTREPLY:
                        return inReplyTo == null;
                    case FT_REGEX:
                        return emailMatchesRegexpFilter();
                    case FT_FUNCTION:
                        return emailMatchesFunctionFilter();
                    default:
                        throw new IllegalArgumentException("unsupported email filter type = " + config.getFilterType());
                }
            }

            private void handleReferencedEmail() {
                List<String> references = list(email.getInReplyTo()).with(reverse(email.getReferences())).stream().filter(StringUtils::isNotBlank).distinct().collect(toList());
                logger.debug("search previous email for email = {} with references = {}", email, references);
                inReplyTo = references.stream().map(emailService::getLastWithReferenceByMessageIdOrNull).filter(Objects::nonNull).findFirst().orElse(null);
                String subject = email.getSubject();
                if (inReplyTo == null) {
                    Long referencedEmailId = parseCardIdFromEmailSubject(subject);
                    if (referencedEmailId != null) {
                        inReplyTo = emailService.getOneOrNull(referencedEmailId);
                        if (inReplyTo == null) {
                            logger.warn(marker(), "unable to find previous email from subject = <{}>: email not found for id = {}", subject, referencedEmailId);
                        }
                        subject = parseSubjectDescrFromEmailSubject(subject);
                    }
                }
                if (inReplyTo == null && config.isAggressiveInReplyToMatchingEnabled()) {
                    String from = email.getFirstFromAddressOrNull();
                    if (isNotBlank(from)) {
                        inReplyTo = emailService.getLastWithReferenceBySenderAndSubjectFuzzyMatchingOrNull(from, subject);
                    }
                }
                if (inReplyTo != null) {
                    logger.info("email match previous email = {} with reference card = {}", inReplyTo, inReplyTo.getReference());
                    email = EmailImpl.copyOf(email)
                            .withSubject(subject)
                            .withAutoReplyTemplate(inReplyTo.getAutoReplyTemplate())
                            .withReference(inReplyTo.getReference())
                            .build();
                } else {
                    logger.debug("email does not match any previous email");
                }
            }

            private void handleNotification() {
                if (config.isActionNotificationActive()) {
                    logger.debug("send notification for email = {} job = {}", email, config.getJob());
                    EmailTemplate template;
                    if (!config.hasNotificationTemplate() && email.getAutoReplyTemplate() == null) {
                        logger.warn(marker(), "unable to send notification for email = {}, template config not found in neither job nor email", email);
                    } else {
                        if (config.hasNotificationTemplate()) {
                            template = templateService.getByName(config.getNotificationTemplate());
                        } else {
                            template = templateService.getOne(email.getAutoReplyTemplate());
                        }
                        EmailAccount account;
                        if (template.hasAccount()) {
                            account = accountService.getAccount(template.getAccount());
                        } else if (config.hasAccount()) {
                            account = accountService.getAccount(config.getAccountName());
                        } else {
                            account = null;
                        }
                        Card card = dao.getCard(email.getReference());
                        if (card.isProcess()) {
                            card = workflowService.getFlowCard(card.getClassName(), card.getId());
                        }
                        Email notificationEmail = EmailImpl.builder()
                                .withStatus(ES_OUTGOING)
                                .withTemplate(template.getId())
                                .withReference(email.getReference())
                                .build();
                        notificationEmail = templateProcessor.applyEmailTemplate(notificationEmail, template, card, email);
                        if (email.getFromRawAddressList().stream().anyMatch(notificationEmail::hasToAddress)) {
                            notificationEmail = EmailImpl.copyOf(notificationEmail)
                                    .withReplyTo(email.getMessageId())
                                    .withReferences(list(email.getReferences()).with(email.getMessageId()))
                                    .build();
                        }
                        if (account != null) {
                            notificationEmail = EmailImpl.copyOf(notificationEmail).withAccount(account.getId()).build();
                        }
                        notificationEmail = emailService.create(notificationEmail);
                        logger.info(marker(), "sent notification email = {} for email = {}", notificationEmail, email);
                    }
                }
            }

            private void handleAttachments() {
                if (config.isActionAttachmentsActive()) {
                    logger.debug("handle attachments for email = {} job = {}", email, config.getJob());
                    email.getAttachments().stream().forEach(this::storeEmailAttachment);
                }
            }

            private void startWorkflow() {
                if (config.isActionWorkflowActive()) {
                    logger.debug("start workflow = {} for email = {} job = {}", config.getActionWorkflowClassName(), email, config.getJob());
                    String username = firstNotBlank(config.getActionWorkflowPerformerUsername(), "workflow");//TODO global config default wf username
                    sessionService.impersonate(username);
                    Flow flow;
                    try {
                        Map<String, Object> flowData = templateProcessor.applyEmailTemplate(config.getActionWorkflowFieldsMapping(), email, config.getMapperConfig());
                        logger.debug("flow data from email = \n\n{}\n", mapToLoggableStringLazy(flowData));
                        flow = workflowService.startProcess(config.getActionWorkflowClassName(), flowData, false).getFlowCard();
                        email = emailService.update(EmailImpl.copyOf(email).withReference(flow.getId()).build());
                        if (config.isActionWorkflowAttachmentsSave()) {
                            email.getAttachments().stream().forEach(a -> storeWorkflowAttachment(flow, a));
                        }
                        if (config.isActionWorkflowAdvance()) {
                            workflowService.updateProcessWithOnlyTask(flow.getClassName(), flow.getId(), flowData, true);
                        }
                    } finally {
                        sessionService.deimpersonate();
                    }
                    logger.info(marker(), "started flow = {} for email = {}", flow, email);
                }
            }

            private void storeEmailAttachment(EmailAttachment attachment) {
                dmsService.create(EMAIL_CLASS_NAME, email.getId(), DocumentDataImpl.builder()
                        .withData(attachment.getData())
                        .withFilename(attachment.getFileName())
                        .withCategory(config.getActionAttachmentsCategory())
                        .build());
            }

            private void storeWorkflowAttachment(Flow flow, EmailAttachment attachment) {
                dmsService.create(flow, DocumentDataImpl.builder()
                        .withData(attachment.getData())
                        .withFilename(attachment.getFileName())
                        .withCategory(config.getActionWorkflowAttachmentsCategory())
                        .build());
            }

            private void safe(Runnable action) {
                try {
                    action.run();
                } catch (Exception ex) {
                    logger.error(marker(), "error while processing email = {} job = {} (safe mode is enabled; ignore error and continue email processing)", email, config.getJob(), ex);
                }
            }

            private boolean emailMatchesFunctionFilter() {
                StoredFunction function = dao.getFunctionByName(config.getFilterFunctionName());
                checkArgument(function.hasOnlyOneOutputParameter() && function.getOnlyOutputParameter().getType().isOfType(BOOLEAN), "invalid output type for filter function = %s: required single boolean)", function);
                Set<String> params = set(FUNCTION_PARAM_FROM_ADDRESS, FUNCTION_PARAM_TO_ADDRESSES, FUNCTION_PARAM_CC_ADDRESSES, FUNCTION_PARAM_SUBJECT, FUNCTION_PARAM_CONTENT);
                checkArgument(equal(function.getOutputParameters().stream().map(StoredFunctionOutputParameter::getName).collect(toSet()), params), "invalid params for filter function = %s: requires exactly these params = %s", function, params);
                boolean matches = dao.selectFunction(function, map(
                        FUNCTION_PARAM_FROM_ADDRESS, email.getFromAddress(),
                        FUNCTION_PARAM_TO_ADDRESSES, email.getToAddresses(),
                        FUNCTION_PARAM_CC_ADDRESSES, email.getCcAddresses(),
                        FUNCTION_PARAM_SUBJECT, email.getSubject(),
                        FUNCTION_PARAM_CONTENT, email.getContent()
                )).getSingleFunctionOutput(function);
                logger.debug("email {} function filter = {}", matches ? "matches" : "does not match", function);
                return matches;
            }

            private boolean emailMatchesRegexpFilter() {
                boolean matches = (config.hasFilterRegexpFrom() ? email.getFromEmailAddressList().stream().anyMatch(a -> Pattern.compile(config.getFilterRegexpFrom(), Pattern.CASE_INSENSITIVE).matcher(a).matches()) : true)
                        && (config.hasFilterRegexpSubject() ? nullToEmpty(email.getSubject()).matches(config.getFilterRegexpSubject()) : true);
                logger.debug("email {} regex filter for subject = {} and/or from address = {}", matches ? "matches" : "does not match", config.getFilterRegexpSubject(), config.getFilterRegexpFrom());
                return matches;
            }

        }
    }
}
