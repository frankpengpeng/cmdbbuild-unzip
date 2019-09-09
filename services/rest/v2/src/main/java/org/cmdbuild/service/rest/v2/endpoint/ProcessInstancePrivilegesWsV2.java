package org.cmdbuild.service.rest.v2.endpoint;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("processes/{processId}/instances/{processInstanceId}/privileges/")
@Produces(APPLICATION_JSON)
public class ProcessInstancePrivilegesWsV2 {

    @GET
    @Path(EMPTY)
    public Object readMany(@PathParam("processId") String processId, @PathParam("processInstanceId") Long processInstanceId) {
        return null;
    }

}
