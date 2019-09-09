package org.cmdbuild.service.rest.v2.endpoint;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("sessions/{sessionId}/impersonate/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class ImpersonateWsV2 {

	@PUT
	@Path("{username}/")
	public Object start(@PathParam("sessionId") String id, @PathParam("username") String username ){
            return null;
        }

	@DELETE
	@Path(EMPTY)
	public Object stop(@PathParam("sessionId") String id ){
            return null;
        }

}
