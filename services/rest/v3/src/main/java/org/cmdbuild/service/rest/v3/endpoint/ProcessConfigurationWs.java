package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.service.rest.common.helpers.ProcessStatusHelper;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;

import org.cmdbuild.service.rest.common.utils.ProcessStatus;

@Path("configuration/processes/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class ProcessConfigurationWs {

	private final ProcessStatusHelper processStatusHelper;

	public ProcessConfigurationWs(ProcessStatusHelper processStatusHelper) {
		this.processStatusHelper = checkNotNull(processStatusHelper);
	}

	@GET
	@Path("statuses/")
	public Object readStatuses() {
		Iterable<ProcessStatus> elements = processStatusHelper.allValues();
		return response(elements);
	}

}
