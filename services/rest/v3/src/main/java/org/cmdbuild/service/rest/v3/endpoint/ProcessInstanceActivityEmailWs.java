package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collection;
import static java.util.stream.Collectors.toList;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import org.cmdbuild.dao.core.q3.DaoService;

import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailService;
import org.cmdbuild.service.rest.v3.endpoint.ProcessInstancesWs.WsFlowData;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;

@Path("processes/{processId}/instances/{instanceId}/activities/{activityId}/emails")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class ProcessInstanceActivityEmailWs {

	private final DaoService dao;
	private final EmailService emailService;

	public ProcessInstanceActivityEmailWs(DaoService dao, EmailService emailService) {
		this.dao = checkNotNull(dao);
		this.emailService = checkNotNull(emailService);
	}

	@POST
	@Path("sync")
	public Object updateEmailWithCardData(@PathParam("processId") String processId, @PathParam("instanceId") Long flowId, WsFlowData flowData) {
		//TODO check user permissions
		//TODO auto update email data from template, with current flow data (and trigger email widget hooks)
		Collection<Email> emails = emailService.getAllForCard(flowId);
		return response(emails.stream().map(CardEmailWs::serializeBasicEmail).collect(toList()));
	}

}
