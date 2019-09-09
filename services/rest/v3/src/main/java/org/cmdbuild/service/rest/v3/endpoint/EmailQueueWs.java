package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Ordering;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.WILDCARD;
import static org.cmdbuild.auth.login.AuthorityConst.HAS_SYSTEM_ACCESS_AUTHORITY;
import org.cmdbuild.email.Email;

import org.cmdbuild.email.EmailService;
import org.cmdbuild.email.queue.EmailQueueService;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import org.springframework.security.access.prepost.PreAuthorize;

@Path("email/queue/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@PreAuthorize(HAS_SYSTEM_ACCESS_AUTHORITY)
public class EmailQueueWs {

    private final EmailService emailService;
    private final EmailQueueService queueService;

    public EmailQueueWs(EmailService emailService, EmailQueueService queueService) {
        this.emailService = checkNotNull(emailService);
        this.queueService = checkNotNull(queueService);
    }

    @POST
    @Path("trigger")
    @Consumes(WILDCARD)
    public Object triggerQueue() {
        queueService.triggerEmailQueue();
        return success();
    }

    @GET
    @Path("outgoing/")
    public Object getOutgoingEmails() {
        return response(emailService.getAllForOutgoingProcessing().stream().sorted(Ordering.natural().onResultOf(Email::getDate).reversed()).map(CardEmailWs::serializeBasicEmail));
    }

    @POST
    @Path("outgoing/{emailId}/trigger")
    @Consumes(WILDCARD)
    public Object sendSingleEmail(@PathParam("emailId") Long emailId) {
        queueService.sendSingleEmail(emailId);
        return success();
    }

}
