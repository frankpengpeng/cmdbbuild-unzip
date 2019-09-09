package org.cmdbuild.email;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Ordering;
import com.google.common.eventbus.EventBus;
import java.util.List;
import javax.annotation.Nullable;
import org.cmdbuild.dao.beans.Card;

public interface EmailService {

    Email create(Email email);

    List<Email> getAllForCard(long reference);

    @Nullable
    Email getOneOrNull(long emailId);

    Email update(Email email);

    void delete(Email email);

    Email applyTemplate(Email email, Card cardData);

    String applyTemplateExpr(Long templateId, Card cardData, String expr);

    Email applySysTemplate(Email email, String sysTemplateId);

    EventBus getEventBus();

    List<Email> getAllForOutgoingProcessing();

    List<EmailAttachment> getEmailAttachments(long emailId);

    Email loadAttachments(Email email);

    List<Email> getByMessageId(String messageId);

    List<Email> getAllForTemplate(long templateId);

    default Email getOne(long emailId) {
        return checkNotNull(getOneOrNull(emailId), "email not found for id = %s", emailId);
    }

    @Nullable
    default Email getLastWithReferenceByMessageIdOrNull(String messageId) {
        return getByMessageId(messageId).stream().filter(Email::hasReference).sorted(Ordering.natural().onResultOf(Email::getDate).reversed()).findFirst().orElse(null);
    }

    @Nullable
    default Email getLastByMessageIdOrNull(String messageId) {
        return getByMessageId(messageId).stream().sorted(Ordering.natural().onResultOf(Email::getDate).reversed()).findFirst().orElse(null);
    }

    @Nullable
    Email getLastWithReferenceBySenderAndSubjectFuzzyMatchingOrNull(String from, String subject);

    enum NewOutgoingEmailEvent {
        INSTANCE
    }
}
