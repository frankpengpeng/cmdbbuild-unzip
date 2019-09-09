package org.cmdbuild.service.rest.v2.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import java.io.IOException;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import javax.activation.DataHandler;
import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.cmdbuild.service.rest.common.helpers.AttachmentWsHelper;
import org.cmdbuild.service.rest.common.helpers.AttachmentWsHelper.WsAttachmentData;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.ATTACHMENT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILE;

@Path("processes/{processId}/instances/{processInstanceId}/attachments/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class ProcessInstanceAttachmentsWsV2 {

    private final AttachmentWsHelper service;

    public ProcessInstanceAttachmentsWsV2(AttachmentWsHelper attachmentWs) {
        this.service = checkNotNull(attachmentWs);
    }

    @POST
    @Path(EMPTY)
    @Consumes(MULTIPART_FORM_DATA)
    @Produces(APPLICATION_JSON)
    public Object create(
            @PathParam("processId") String processId,
            @PathParam("processInstanceId") Long instanceId,
            @Multipart(value = ATTACHMENT, required = false)
            @Nullable WsAttachmentData attachment, @Multipart(FILE) DataHandler dataHandler) throws IOException {
        return service.create(processId, instanceId, attachment, dataHandler);
    }

    @GET
    @Path(EMPTY)
    public Object read(@PathParam("processId") String processId, @PathParam("processInstanceId") Long processInstanceId) {
        return service.read(processId, processInstanceId);
    }

    @GET
    @Path("{attachmentId}/")
    public Object read(@PathParam("processId") String processId, @PathParam("processInstanceId") Long processInstanceId, @PathParam("attachmentId") String attachmentId) {
        return service.read(processId, processInstanceId, attachmentId);
    }

    @GET
    @Path("{attachmentId}/{file: [^/]+}")
    @Produces(APPLICATION_OCTET_STREAM)
    public Object download(
            @PathParam("processId") String processId,
            @PathParam("processInstanceId") Long processInstanceId,
            @PathParam("attachmentId") String attachmentId
    ) {
        return service.download(processId, processInstanceId, attachmentId);
    }

    @PUT
    @Path("{attachmentId}/")
    @Consumes(MULTIPART_FORM_DATA)
    @Produces(APPLICATION_JSON)
    public Object update(
            @PathParam("processId") String processId,
            @PathParam("processInstanceId") Long instanceId,
            @PathParam("attachmentId") String attachmentId,
            @Multipart(value = ATTACHMENT, required = false) WsAttachmentData attachment,
            @Multipart(value = FILE, required = false) DataHandler dataHandler
    ) {
        return service.update(processId, instanceId, attachmentId, attachment, dataHandler);
    }

    @DELETE
    @Path("{attachmentId}/")
    public Object delete(@PathParam("processId") String processId, @PathParam("processInstanceId") Long processInstanceId, @PathParam("attachmentId") String attachmentId) {
        return service.delete(processId, processInstanceId, attachmentId);
    }
}
