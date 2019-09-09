/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email.beans;

import com.google.common.base.Joiner;
import static com.google.common.base.Preconditions.checkArgument;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailStatus;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import com.google.common.collect.ImmutableList;
import java.time.ZonedDateTime;
import static java.util.Collections.emptyList;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.StringUtils.abbreviate;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_BEGINDATE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.cmdbuild.email.EmailAttachment;
import static org.cmdbuild.email.EmailContentUtils.getContentTypeOrAutoDetect;
import static org.cmdbuild.email.beans.EmailImpl.EMAIL_CLASS_NAME;
import static org.cmdbuild.email.utils.EmailUtils.parseEmailStatus;
import static org.cmdbuild.email.utils.EmailUtils.serializeEmailStatus;
import org.cmdbuild.utils.date.CmDateUtils;
import org.cmdbuild.utils.lang.Builder;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;

@CardMapping(EMAIL_CLASS_NAME)
public class EmailImpl implements Email {

    public static final String EMAIL_CLASS_NAME = "Email";

    private final Long id, reference, delay, account, template, autoReplyTemplate;
    private final String fromAddress, toAddresses, ccAddresses, bccAddresses, subject, content, contentType, messageId, inReplyTo, replyTo;
    private final List<String> references;
    private final EmailStatus status;
    private final boolean noSubjectPrefix, keepSynchronization, promptSynchronization;
    private final ZonedDateTime sentOrReceivedDate, beginDate;
    private final int errorCount;
    private final List<EmailAttachment> attachments;

    private EmailImpl(EmailImplBuilder builder) {
        this.id = builder.id;
        this.reference = builder.reference;
        this.fromAddress = trimToEmpty(builder.fromAddress);
        this.toAddresses = trimToEmpty(builder.toAddresses);
        this.ccAddresses = trimToEmpty(builder.ccAddresses);
        this.bccAddresses = trimToEmpty(builder.bccAddresses);
        this.subject = nullToEmpty(builder.subject);
        this.content = nullToEmpty(builder.content);
        this.account = builder.account;
        this.template = builder.template;
        this.autoReplyTemplate = builder.autoReplyTemplate;
        this.delay = builder.delay;
        this.status = checkNotNull(builder.status);
        this.noSubjectPrefix = firstNotNull(builder.noSubjectPrefix, false);
        this.keepSynchronization = firstNotNull(builder.keepSynchronization, false);
        this.promptSynchronization = firstNotNull(builder.promptSynchronization, false);
        this.attachments = ImmutableList.copyOf(firstNotNull(builder.attachments, emptyList()));
        this.errorCount = firstNotNull(builder.errorCount, 0);
        this.messageId = builder.messageId;
        this.replyTo = builder.replyTo;
        this.inReplyTo = builder.inReplyTo;
        this.references = ImmutableList.copyOf(firstNotNull(builder.references, emptyList()));
        checkArgument(!references.stream().anyMatch(StringUtils::isBlank));
        this.contentType = getContentTypeOrAutoDetect(builder.contentType, content);
        this.beginDate = firstNotNull(builder.beginDate, CmDateUtils.now());
        switch (status) {
            case ES_RECEIVED:
            case ES_SENT:
                this.sentOrReceivedDate = checkNotNull(builder.sentOrReceivedDate, "missing email date");
                break;
            default:
                this.sentOrReceivedDate = null;
        }

    }

    @CardAttr("ContentType")
    @Override
    public String getContentType() {
        return contentType;
    }

    @CardAttr("MessageId")
    @Override
    @Nullable
    public String getMessageId() {
        return messageId;
    }

    @CardAttr("ReplyTo")
    @Override
    @Nullable
    public String getReplyTo() {
        return replyTo;
    }

    @CardAttr("InReplyTo")
    @Override
    @Nullable
    public String getInReplyTo() {
        return inReplyTo;
    }

    @CardAttr("References")
    @Override
    public List<String> getReferences() {
        return references;
    }

    @CardAttr(ATTR_ID)
    @Override
    @Nullable
    public Long getId() {
        return id;
    }

    @Nullable
    @Override
    @CardAttr("Card")
    public Long getReference() {
        return reference;
    }

    @Nullable
    @Override
    @CardAttr
    public String getFromAddress() {
        return fromAddress;
    }

    @Nullable
    @Override
    @CardAttr
    public String getToAddresses() {
        return toAddresses;
    }

    @Nullable
    @Override
    @CardAttr
    public String getCcAddresses() {
        return ccAddresses;
    }

    @Nullable
    @Override
    @CardAttr
    public String getBccAddresses() {
        return bccAddresses;
    }

    @Nullable
    @Override
    @CardAttr
    public String getSubject() {
        return subject;
    }

    @Override
    @CardAttr
    public String getContent() {
        return content;
    }

    @Nullable
    @Override
    @CardAttr
    public Long getAccount() {
        return account;
    }

    @Nullable
    @Override
    @CardAttr
    public Long getTemplate() {
        return template;
    }

    @Nullable
    @Override
    @CardAttr("NotifyWith")
    public Long getAutoReplyTemplate() {
        return autoReplyTemplate;
    }

    @Nullable
    @Override
    @CardAttr
    public Long getDelay() {
        return delay;
    }

    @Override
    public EmailStatus getStatus() {
        return status;
    }

    @CardAttr("EmailStatus")
    public String getStatusAsString() {
        return serializeEmailStatus(getStatus());
    }

    @Override
    @CardAttr
    public boolean getNoSubjectPrefix() {
        return noSubjectPrefix;
    }

    @Override
    @CardAttr
    public boolean getKeepSynchronization() {
        return keepSynchronization;
    }

    @Override
    @CardAttr
    public boolean getPromptSynchronization() {
        return promptSynchronization;
    }

    @Override
    @CardAttr("EmailDate")
    public ZonedDateTime getSentOrReceivedDate() {
        return sentOrReceivedDate;
    }

    @Override
    @CardAttr(value = ATTR_BEGINDATE, writeToDb = false)
    @Nullable
    public ZonedDateTime getBeginDate() {
        return beginDate;
    }

    @Override
    @CardAttr
    public int getErrorCount() {
        return errorCount;
    }

    @Override
    public List<EmailAttachment> getAttachments() {
        return attachments;
    }

    @Override
    public String toString() {
        return "Email{" + "id=" + id + ", reference=" + reference + ", subject=<" + abbreviate(subject, 40) + ">, status=" + serializeEmailStatus(status) + '}';
    }

    public static EmailImplBuilder builder() {
        return new EmailImplBuilder();
    }

    public static EmailImplBuilder copyOf(Email source) {
        return new EmailImplBuilder()
                .withId(source.getId())
                .withReference(source.getReference())
                .withFromAddress(source.getFromAddress())
                .withToAddresses(source.getToAddresses())
                .withCcAddresses(source.getCcAddresses())
                .withBccAddresses(source.getBccAddresses())
                .withSubject(source.getSubject())
                .withContent(source.getContent())
                .withAccount(source.getAccount())
                .withTemplate(source.getTemplate())
                .withAutoReplyTemplate(source.getAutoReplyTemplate())
                .withDelay(source.getDelay())
                .withSentOrReceivedDate(source.getSentOrReceivedDate())
                .withBeginDate(source.getBeginDate())
                .withStatus(source.getStatus())
                .withNoSubjectPrefix(source.getNoSubjectPrefix())
                .withKeepSynchronization(source.getKeepSynchronization())
                .withPromptSynchronization(source.getPromptSynchronization())
                .withErrorCount(source.getErrorCount())
                .withMessageId(source.getMessageId())
                .withInReplyTo(source.getInReplyTo())
                .withReplyTo(source.getReplyTo())
                .withReferences(source.getReferences())
                .withContentType(source.getContentType())
                .withAttachments(source.getAttachments());
    }

    public static class EmailImplBuilder implements Builder<EmailImpl, EmailImplBuilder> {

        private Long id;
        private Long reference, account, template, autoReplyTemplate;
        private String fromAddress, contentType, messageId, inReplyTo, replyTo;
        private String toAddresses;
        private String ccAddresses;
        private String bccAddresses;
        private String subject;
        private String content;
        private Long delay;
        private EmailStatus status;
        private Boolean noSubjectPrefix;
        private Boolean keepSynchronization;
        private Boolean promptSynchronization;
        private ZonedDateTime sentOrReceivedDate, beginDate;
        private Integer errorCount;
        private final List<EmailAttachment> attachments = list();
        private List<String> references;

        public EmailImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public EmailImplBuilder withReference(Long reference) {
            this.reference = reference;
            return this;
        }

        public EmailImplBuilder withContentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public EmailImplBuilder withMessageId(String messageId) {
            this.messageId = messageId;
            return this;
        }

        public EmailImplBuilder withInReplyTo(String inReplyTo) {
            this.inReplyTo = inReplyTo;
            return this;
        }

        public EmailImplBuilder withReplyTo(String replyTo) {
            this.replyTo = replyTo;
            return this;
        }

        public EmailImplBuilder withReferences(List<String> references) {
            this.references = references;
            return this;
        }

        public EmailImplBuilder withFromAddress(String fromAddress) {
            this.fromAddress = fromAddress;
            return this;
        }

        public EmailImplBuilder withToAddresses(String toAddresses) {
            this.toAddresses = toAddresses;
            return this;
        }

        public EmailImplBuilder withToAddresses(List<String> toAddresses) {
            this.toAddresses = Joiner.on(" ").join(CmCollectionUtils.nullToEmpty(toAddresses));//TODO check this
            return this;
        }

        public EmailImplBuilder withCcAddresses(String ccAddresses) {
            this.ccAddresses = ccAddresses;
            return this;
        }

        public EmailImplBuilder withCcAddresses(List<String> ccAddresses) {
            this.ccAddresses = Joiner.on(" ").join(CmCollectionUtils.nullToEmpty(ccAddresses));//TODO check this
            return this;
        }

        public EmailImplBuilder withBccAddresses(String bccAddresses) {
            this.bccAddresses = bccAddresses;
            return this;
        }

        public EmailImplBuilder withBccAddresses(List<String> bccAddresses) {
            this.bccAddresses = Joiner.on(" ").join(CmCollectionUtils.nullToEmpty(bccAddresses));//TODO check this
            return this;
        }

        public EmailImplBuilder withSubject(String subject) {
            this.subject = subject;
            return this;
        }

        public EmailImplBuilder withContent(String content) {
            this.content = content;
            return this;
        }

        public EmailImplBuilder withAccount(Long account) {
            this.account = account;
            return this;
        }

        public EmailImplBuilder withTemplate(Long template) {
            this.template = template;
            return this;
        }

        public EmailImplBuilder withAutoReplyTemplate(Long autoReplyTemplate) {
            this.autoReplyTemplate = autoReplyTemplate;
            return this;
        }

        public EmailImplBuilder withDelay(Long delay) {
            this.delay = delay;
            return this;
        }

        public EmailImplBuilder withSentOrReceivedDate(ZonedDateTime date) {
            this.sentOrReceivedDate = date;
            return this;
        }

        public EmailImplBuilder withBeginDate(ZonedDateTime date) {
            this.beginDate = date;
            return this;
        }

        public EmailImplBuilder withStatus(EmailStatus status) {
            this.status = status;
            return this;
        }

        public EmailImplBuilder withErrorCount(Integer errorCount) {
            this.errorCount = errorCount;
            return this;
        }

        public EmailImplBuilder withStatusAsString(String status) {
            return this.withStatus(parseEmailStatus(status));
        }

        public EmailImplBuilder withNoSubjectPrefix(Boolean noSubjectPrefix) {
            this.noSubjectPrefix = noSubjectPrefix;
            return this;
        }

        public EmailImplBuilder withKeepSynchronization(Boolean keepSynchronization) {
            this.keepSynchronization = keepSynchronization;
            return this;
        }

        public EmailImplBuilder withPromptSynchronization(Boolean promptSynchronization) {
            this.promptSynchronization = promptSynchronization;
            return this;
        }

        public EmailImplBuilder withAttachments(List<EmailAttachment> attachments) {
            this.attachments.clear();
            this.attachments.addAll(attachments);
            return this;
        }

        public EmailImplBuilder addAttachment(EmailAttachment attachment) {
            this.attachments.add(attachment);
            return this;
        }

        @Override
        public EmailImpl build() {
            return new EmailImpl(this);
        }

    }
}
