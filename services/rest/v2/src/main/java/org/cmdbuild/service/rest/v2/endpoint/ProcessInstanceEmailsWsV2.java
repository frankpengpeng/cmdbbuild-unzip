package org.cmdbuild.service.rest.v2.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collection;
import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailService;
import org.cmdbuild.email.EmailStatus;
import static org.cmdbuild.email.EmailStatus.ES_DRAFT;
import org.cmdbuild.email.beans.EmailAccountImpl;
import org.cmdbuild.email.beans.EmailImpl;
import static org.cmdbuild.email.utils.EmailUtils.parseEmailStatus;
import static org.cmdbuild.email.utils.EmailUtils.serializeEmailStatus;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.workflow.WorkflowService;

@Path("processes/{processId}/instances/{processInstanceId}/emails/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class ProcessInstanceEmailsWsV2 {

    private final DaoService dao;
    private final EmailService emailService;
    private final WorkflowService workflowService;

    public ProcessInstanceEmailsWsV2(DaoService dao, EmailService emailService, WorkflowService workflowService) {
        this.dao = checkNotNull(dao);
        this.emailService = checkNotNull(emailService);
        this.workflowService = checkNotNull(workflowService);
    }

    @GET
    @Path("statuses/")
    public Object statuses() {
        return null;
    }

    @POST
    @Path(EMPTY)
    public Object create(@PathParam("processId") String processId, @PathParam("processInstanceId") Long processInstanceId, WsEmailData emailData) {
        Card card;
        if (equal(processId, "_ANY") && equal(processInstanceId, "_ANY")) {
            card = null;
        } else {
            card = workflowService.getFlowCard(processId, processInstanceId);
        }
        Email email = emailData.toEmail().withReference(card == null ? null : card.getId()).build();

        email = emailService.create(email);
        return map("data", serializeDetailedEmail(email));
    }

    @GET
    @Path(EMPTY)
    public Object readMany(@PathParam("processId") String processId, @PathParam("processInstanceId") Long processInstanceId, @QueryParam(LIMIT) Integer limit, @QueryParam(START) Integer offset) {
        Collection<Email> list = emailService.getAllForCard(workflowService.getFlowCard(processId, processInstanceId).getCardId());
        return map("data", list.stream().map(this::serializeDetailedEmail).collect(toList()), "meta", map("total", list.size()));
    }

    @GET
    @Path("{emailId}/")
    public Object readOne(@PathParam("processId") String processId, @PathParam("processInstanceId") Long processInstanceId, @PathParam("emailId") Long emailId) {
        Email mail = emailService.getOne(emailId);
        return map("data", serializeDetailedEmail(mail));
    }

    @PUT
    @Path("{emailId}/")
    public Object update(@PathParam("processId") String processId, @PathParam("processInstanceId") Long processInstanceId, @PathParam("emailId") Long emailId, WsEmailData emailData) {
        Card card = workflowService.getFlowCard(processId, processInstanceId);
        Email current = emailService.getOne(emailId);
        checkArgument(equal(ES_DRAFT, current.getStatus()), "cannot update email with status = %s", current.getStatus());
        Email email = emailData.toEmail().withReference(card.getId()).withId(emailId).build();
        email = emailService.update(email);
        return map("data", serializeDetailedEmail(email));
    }

    @DELETE
    @Path("{emailId}/")
    public Object delete(@PathParam("processId") String processId, @PathParam("processInstanceId") Long processInstanceId, @PathParam("emailId") Long emailId) {
        Card card = workflowService.getFlowCard(processId, processInstanceId);
        emailService.delete(emailService.getOne(emailId));
        return success();
    }

    public CmMapUtils.FluentMap<String, Object> serializeBasicEmail(Email email) {
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

    public CmMapUtils.FluentMap<String, Object> serializeDetailedEmail(Email email) {
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
        ).skipNullValues();
    }

    public static class WsEmailData {

        private final Long delay, account, template, autoReplyTemplate;
        private final String from, to, cc, bcc, subject, body, replyTo, contentType;
        private final Boolean keepSynchronization, noSubjectPrefix, promptSynchronization;
        private final EmailStatus status;
        private final CardEmailsWsV2.WsCardData card;

        public WsEmailData(
                @JsonProperty("delay") Long delay,
                @JsonProperty("from") String from,
                @JsonProperty("replyTo") String replyTo,
                @JsonProperty("to") String to,
                @JsonProperty("cc") String cc,
                @JsonProperty("bcc") String bcc,
                @JsonProperty("subject") String subject,
                @JsonProperty("contentType") String contentType,
                @JsonProperty("body") String body,
                @JsonProperty("account") Long account,
                @JsonProperty("template") Long template,
                @JsonProperty("autoReplyTemplate") Long autoReplyTemplate,
                @JsonProperty("keepSynchronization") Boolean keepSynchronization,
                @JsonProperty("noSubjectPrefix") Boolean noSubjectPrefix,
                @JsonProperty("promptSynchronization") Boolean promptSynchronization,
                @JsonProperty("status") String status,
                @JsonProperty("_card") CardEmailsWsV2.WsCardData card) {
            this.delay = delay;
            this.from = from;
            this.replyTo = replyTo;
            this.to = to;
            this.cc = cc;
            this.bcc = bcc;
            this.subject = subject;
            this.contentType = contentType;
            this.body = body;
            this.account = account;
            this.template = template;
            this.autoReplyTemplate = autoReplyTemplate;
            this.keepSynchronization = keepSynchronization;
            this.noSubjectPrefix = noSubjectPrefix;
            this.promptSynchronization = promptSynchronization;
            this.status = firstNonNull(parseEmailStatus(status), ES_DRAFT);
            this.card = card;
        }

        public EmailImpl.EmailImplBuilder toEmail() {
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

        public CardEmailsWsV2.WsCardData getCardData() {
            return checkNotNull(card);
        }
    }

}
