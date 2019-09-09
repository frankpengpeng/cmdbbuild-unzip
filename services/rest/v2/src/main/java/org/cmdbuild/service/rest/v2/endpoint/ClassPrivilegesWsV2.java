package org.cmdbuild.service.rest.v2.endpoint;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("roles/{roleId}/classes_privileges/")
@Produces(APPLICATION_JSON)
public class ClassPrivilegesWsV2 {

    @GET
    @Path(EMPTY)
    public Object readMany(@PathParam("roleId") String roleId) {
        return null;
    }

    @GET
    @Path("{classId}/")
    public Object readOne(@PathParam("roleId") String roleId, @PathParam("classId") String classId) {
        return null;
    }

}
