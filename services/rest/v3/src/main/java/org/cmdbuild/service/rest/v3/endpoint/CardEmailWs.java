package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collection;
import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.math.NumberUtils.isNumber;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.core.q3.DaoService;

import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailService;
import org.cmdbuild.email.EmailStatus;
import static org.cmdbuild.email.EmailStatus.ES_DRAFT;
import org.cmdbuild.email.beans.EmailImpl;
import org.cmdbuild.email.beans.EmailImpl.EmailImplBuilder;
import static org.cmdbuild.email.utils.EmailUtils.parseEmailStatus;
import static org.cmdbuild.email.utils.EmailUtils.serializeEmailStatus;
import org.cmdbuild.service.rest.v3.endpoint.CardWs.WsCardData;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CARD_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CLASS_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.EMAIL_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DETAILED;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@Path("{a:classes|processes}/{" + CLASS_ID + "}/{b:cards|instances}/{" + CARD_ID + "}/emails")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class CardEmailWs {

    private final DaoService dao;
    private final EmailService emailService;

    public CardEmailWs(DaoService dao, EmailService emailService) {
        this.dao = checkNotNull(dao);
        this.emailService = checkNotNull(emailService);
    }

    @GET
    @Path(EMPTY)
    public Object readAll(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, @QueryParam(LIMIT) Integer limit, @QueryParam(START) Integer offset, @QueryParam(DETAILED) @DefaultValue(FALSE) Boolean detailed) {
        Card card = dao.getCard(classId, cardId); //TODO check user permissions
        Collection<Email> list = emailService.getAllForCard(card.getId());
        return response(paged(list, detailed ? CardEmailWs::serializeDetailedEmail : CardEmailWs::serializeBasicEmail, offset, limit));
    }

    @GET
    @Path("{" + EMAIL_ID + "}/")
    public Object read(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, @PathParam(EMAIL_ID) Long emailId) {
        Card card = dao.getCard(classId, cardId); //TODO check user permissions
        Email email = emailService.getOne(emailId);
        return response(serializeDetailedEmail(email));//TODO check email id
    }

    @POST
    @Path(EMPTY)
    public Object create(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) String cardId, WsEmailData emailData, @QueryParam("apply_template") @DefaultValue(FALSE) Boolean applyTemplate, @QueryParam("template_only") @DefaultValue(FALSE) Boolean templateOnly) {
        Card card;
        if (equal(classId, "_ANY") && equal(cardId, "_ANY")) {
            card = null;
        } else {
            card = dao.getCard(classId, toLong(cardId)); //TODO check user permissions
        }
        Email email = emailData.toEmail().withReference(card == null ? null : card.getId()).build();
        applyTemplate = applyTemplate || templateOnly;
        Boolean expr;
        if (applyTemplate && emailData.hasExpr()) {
            applyTemplate = expr = handleTemplateExpr(email.getTemplate(), emailData, card);
        } else {
            expr = null;
        }
        email = handleTemplate(email, emailData, card, applyTemplate);
        if (!templateOnly) {
            email = emailService.create(email);
        }
        return response(serializeDetailedEmail(email, expr));
    }

    @PUT
    @Path("{" + EMAIL_ID + "}/")
    public Object update(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, @PathParam(EMAIL_ID) Long emailId, WsEmailData emailData, @QueryParam("apply_template") @DefaultValue(FALSE) Boolean applyTemplate, @QueryParam("template_only") @DefaultValue(FALSE) Boolean templateOnly) {
        Card card = dao.getCard(classId, cardId); //TODO check user permissions
        Email current = emailService.getOne(emailId);
        checkArgument(equal(ES_DRAFT, current.getStatus()), "cannot update email with status = %s", current.getStatus());
        Email email = emailData.toEmail().withReference(card.getId()).withId(emailId).build();
        applyTemplate = applyTemplate || templateOnly;
        Boolean expr;
        if (applyTemplate && emailData.hasExpr()) {
            applyTemplate = expr = handleTemplateExpr(email.getTemplate(), emailData, card);
        } else {
            expr = null;
        }
        email = handleTemplate(email, emailData, card, applyTemplate);
        if (!templateOnly) {
            email = emailService.update(email);
        }
        return response(serializeDetailedEmail(email, expr));
    }

    @DELETE
    @Path("{" + EMAIL_ID + "}/")
    public Object delete(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, @PathParam(EMAIL_ID) Long emailId) {
        Card card = dao.getCard(classId, cardId); //TODO check user permissions
        emailService.delete(emailService.getOne(emailId));//TODO check email id
        return success();
    }

    private Email handleTemplate(Email email, WsEmailData emailData, @Nullable Card card, Boolean applyTemplate) {
        if (applyTemplate) {
            if (emailData.hasCardData()) {
                card = CardImpl.copyOf(card).withAttributes(emailData.getCardData().getValues()).build();
            }
            if (isNotBlank(emailData.systemTemplate)) {
                return emailService.applySysTemplate(email, emailData.systemTemplate);
            } else {
                return emailService.applyTemplate(email, card);
            }
        } else {
            return email;
        }
    }

    private boolean handleTemplateExpr(long template, WsEmailData emailData, Card card) {
        if (emailData.hasCardData()) {
            card = CardImpl.copyOf(card).withAttributes(emailData.getCardData().getValues()).build();
        }
        return toBooleanOrDefault(emailService.applyTemplateExpr(template, card, emailData.getExpr()), false);
    }

    public static FluentMap<String, Object> serializeBasicEmail(Email email) {
        return map(
                "_id", email.getId(),
                "from", email.getFromAddress(),
                "replyTo", email.getReplyTo(),
                "to", email.getToAddresses(),
                "cc", email.getCcAddresses(),
                "bcc", email.getBccAddresses(),
                "subject", email.getSubject(),
                "body", email.getContent(),
                "contentType", email.getContentType(),
                "_content_plain", email.getContentPlaintext(),
                "date", toIsoDateTime(email.getDate()),
                "status", serializeEmailStatus(email.getStatus())
        );
    }

    public static FluentMap<String, Object> serializeDetailedEmail(Email email) {
        return serializeDetailedEmail(email, null);
    }

    public static FluentMap<String, Object> serializeDetailedEmail(Email email, @Nullable Boolean expr) {
        return serializeBasicEmail(email).with(
                "account", email.getAccount(),
                "delay", email.getDelay(),
                "template", email.getTemplate(),
                "autoReplyTemplate", email.getAutoReplyTemplate(),
                "keepSynchronization", email.getKeepSynchronization(),
                "noSubjectPrefix", email.getNoSubjectPrefix(),
                "promptSynchronization", email.getPromptSynchronization(),
                "_content_html", readToString(email.getContentHtmlOrWrappedPlaintext()),
                "card", email.getReference()
        ).skipNullValues().with("_expr", expr);
    }

    public static class WsEmailData {

        private final Long delay, account, template, autoReplyTemplate;
        private final String from, to, cc, bcc, subject, body, expr, replyTo, contentType, systemTemplate;
        private final Boolean keepSynchronization, noSubjectPrefix, promptSynchronization;
        private final EmailStatus status;
        private final WsCardData card;

        public WsEmailData(
                @JsonProperty("delay") Long delay,
                @JsonProperty("from") String from,
                @JsonProperty("replyTo") String replyTo,
                @JsonProperty("to") String to,
                @JsonProperty("cc") String cc,
                @JsonProperty("bcc") String bcc,
                @JsonProperty("subject") String subject,
                @JsonProperty("body") String body,
                @JsonProperty("contentType") String contentType,
                @JsonProperty("account") Long account,
                @JsonProperty("template") String template,
                @JsonProperty("autoReplyTemplate") Long autoReplyTemplate,
                @JsonProperty("keepSynchronization") Boolean keepSynchronization,
                @JsonProperty("noSubjectPrefix") Boolean noSubjectPrefix,
                @JsonProperty("promptSynchronization") Boolean promptSynchronization,
                @JsonProperty("status") String status,
                @JsonProperty("_expr") String expr,
                @JsonProperty("_card") WsCardData card) {
            this.delay = delay;
            this.from = from;
            this.replyTo = replyTo;
            this.to = to;
            this.cc = cc;
            this.bcc = bcc;
            this.subject = subject;
            this.body = body;
            this.account = account;
            this.contentType = contentType;
            if (isNumber(template)) {
                this.template = toLong(template);
                this.systemTemplate = null;
            } else {
                this.template = null;
                this.systemTemplate = template;
            }
            this.autoReplyTemplate = autoReplyTemplate;
            this.keepSynchronization = keepSynchronization;
            this.noSubjectPrefix = noSubjectPrefix;
            this.promptSynchronization = promptSynchronization;
            this.status = firstNonNull(parseEmailStatus(status), ES_DRAFT);
            this.expr = expr;
            this.card = card;
        }

        public EmailImplBuilder toEmail() {
            return EmailImpl.builder()
                    .withReplyTo(replyTo)
                    .withDelay(delay)
                    .withAccount(account)
                    .withBccAddresses(bcc)
                    .withCcAddresses(cc)
                    .withContent(body)
                    .withContentType(contentType)
                    .withDelay(delay)
                    .withFromAddress(from)
                    .withKeepSynchronization(keepSynchronization)
                    .withNoSubjectPrefix(noSubjectPrefix)
                    .withPromptSynchronization(promptSynchronization)
                    .withStatus(status)
                    .withSubject(subject)
                    .withTemplate(template)
                    .withAutoReplyTemplate(autoReplyTemplate)
                    .withToAddresses(to);
        }

        public boolean hasCardData() {
            return card != null;
        }

        public WsCardData getCardData() {
            return checkNotNull(card);
        }

        public boolean hasExpr() {
            return isNotBlank(expr);
        }

        public String getExpr() {
            return checkNotBlank(expr);
        }
    }

}
