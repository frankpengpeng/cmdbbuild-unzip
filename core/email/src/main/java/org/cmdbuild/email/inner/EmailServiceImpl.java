/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email.inner;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.EventBus;
import static java.util.Collections.emptyList;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dms.DmsService;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailAttachment;
import org.cmdbuild.email.beans.EmailAttachmentImpl;
import org.cmdbuild.email.EmailService;
import static org.cmdbuild.email.EmailStatus.ES_OUTGOING;
import org.cmdbuild.email.EmailTemplate;
import org.cmdbuild.email.EmailTemplateService;
import org.cmdbuild.email.beans.EmailImpl;
import static org.cmdbuild.email.beans.EmailImpl.EMAIL_CLASS_NAME;
import org.cmdbuild.email.data.EmailRepository;
import org.cmdbuild.email.template.EmailTemplateProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmailServiceImpl implements EmailService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final EventBus eventBus = new EventBus();
    private final EmailRepository repository;
    private final EmailTemplateService templateRepository;
    private final EmailTemplateProcessorService templateProcessorService;
    private final DmsService dmsService;

    public EmailServiceImpl(EmailRepository repository, EmailTemplateService templateRepository, EmailTemplateProcessorService templateProcessorService, DmsService dmsService) {
        this.repository = checkNotNull(repository);
        this.templateRepository = checkNotNull(templateRepository);
        this.templateProcessorService = checkNotNull(templateProcessorService);
        this.dmsService = checkNotNull(dmsService);
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public List<Email> getAllForCard(long reference) {
        return repository.getAllForCard(reference);
    }

    @Override
    @Nullable
    public Email getOneOrNull(long emailId) {
        return repository.getOneOrNull(emailId);
    }

    @Override
    public List<Email> getAllForTemplate(long templateId) {
        return repository.getAllForTemplate(templateId);
    }

    @Override
    public List<Email> getByMessageId(String messageId) {
        return repository.getByMessageId(messageId);
    }

    @Override
    @Nullable
    public Email getLastWithReferenceBySenderAndSubjectFuzzyMatchingOrNull(String from, String subject) {
        return repository.getLastWithReferenceBySenderAndSubjectFuzzyMatchingOrNull(from, subject);
    }

    @Override
    public Email create(Email email) {
        logger.debug("create email = {}", email);
        email = repository.create(email);
        checkOutgoing(email);
        return email;
    }

    @Override
    public Email update(Email email) {
        logger.debug("update email = {}", email);
        email = repository.update(email);
        checkOutgoing(email);
        return email;
    }

    @Override
    public void delete(Email email) {
        logger.debug("delete email = {}", email);
        repository.delete(email);
    }

    @Override
    public Email applyTemplate(Email email, Card cardData) {
        logger.debug("sync template for email = {}", email);
        checkArgument(email.hasTemplate(), "unable to sync email without template");
        EmailTemplate template = templateRepository.getOne(email.getTemplate());
        return templateProcessorService.applyEmailTemplate(email, template, cardData);
//        return doApplyTemplate(email, cardData, null);
    }

    @Override
    public String applyTemplateExpr(Long templateId, Card cardData, String expr) {
        EmailTemplate template = templateRepository.getOne(checkNotNull(templateId, "email template id cannot be null"));
        return templateProcessorService.applyEmailTemplateExpr(expr, template, cardData);
    }

    @Override
    public Email applySysTemplate(Email email, String sysTemplateId) {
        EmailTemplate template = templateRepository.getSystemTemplate(sysTemplateId);
        return templateProcessorService.applyEmailTemplate(email, template);
    }

//    private Email doApplyTemplate(Email email, Card cardData, @Nullable Email replyTo) {
//        logger.debug("sync template for email = {}", email);
//        checkArgument(email.hasTemplate(), "unable to sync email without template");
//        EmailTemplate template = templateRepository.getOne(email.getTemplate());
//        if (replyTo == null) {
//            return templateProcessorService.applyEmailTemplate(email, template, cardData);
//        } else {
//            return templateProcessorService.applyEmailTemplate(email, template, cardData, replyTo);
//        }
//    }
    @Override
    public List<Email> getAllForOutgoingProcessing() {
        return repository.getAllForOutgoingProcessing();
    }

    @Override
    public List<EmailAttachment> getEmailAttachments(long emailId) {
        if (dmsService.isEnabled()) {
            return dmsService.getCardAttachments(EMAIL_CLASS_NAME, emailId).stream().map(a
                    -> EmailAttachmentImpl.builder()
                            .withFileName(a.getFileName())
                            .withMimeType(a.getMimeType())
                            .withData(dmsService.getDocumentContent(a.getDocumentId()))
                            .build()).collect(toList());
        } else {
            return emptyList();
        }
    }

    @Override
    public Email loadAttachments(Email email) {
        if (email.getId() == null) {
            return email;
        } else {
            return EmailImpl.copyOf(email).withAttachments(getEmailAttachments(email.getId())).build();
        }
    }

    private void checkOutgoing(Email email) {
        if (equal(email.getStatus(), ES_OUTGOING)) {
            logger.debug("outgoing email processed, trigger email queue (email = {})", email);
            eventBus.post(NewOutgoingEmailEvent.INSTANCE);
        }
    }

}
