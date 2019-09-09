package org.cmdbuild.service.rest.v3.endpoint;

import org.cmdbuild.service.rest.common.helpers.AttachmentWsHelper;
import javax.activation.DataHandler;

import static com.google.common.base.Preconditions.checkNotNull;
import java.io.IOException;
import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.cmdbuild.service.rest.common.helpers.AttachmentWsHelper.WsAttachmentData;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CARD_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CLASS_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.ATTACHMENT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.ATTACHMENT_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILE;

@Path("{a:processes|classes}/{" + CLASS_ID + "}/{b:instances|cards}/{" + CARD_ID + "}/attachments/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class AttachmentWs {

    private final AttachmentWsHelper service;

    public AttachmentWs(AttachmentWsHelper attachmentWs) {
        this.service = checkNotNull(attachmentWs);
    }

    @POST
    @Path(EMPTY)
    @Consumes(MULTIPART_FORM_DATA)
    @Produces(APPLICATION_JSON)
    public Object create(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, @Multipart(value = ATTACHMENT, required = false) @Nullable WsAttachmentData attachment, @Multipart(FILE) DataHandler dataHandler) throws IOException {
        return service.create(classId, cardId, attachment, dataHandler);
    }

    @POST
    @Path(EMPTY)
    @Produces(APPLICATION_JSON)
    public Object copyFrom(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, @Nullable WsAttachmentData attachment, @QueryParam("copyFrom_class") String sourceClassId, @QueryParam("copyFrom_card") Long sourceCardId, @QueryParam("copyFrom_id") String sourceAttachmentId) throws IOException {
        return service.copyFrom(classId, cardId, attachment, sourceClassId, sourceCardId, sourceAttachmentId);
    }

    @GET
    @Path(EMPTY)
    public Object read(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId) {
        return service.read(classId, cardId);
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/")
    public Object read(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, @PathParam(ATTACHMENT_ID) String attachmentId) {
        return service.read(classId, cardId, attachmentId);
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/{file: [^/]+}")
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler download(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, @PathParam(ATTACHMENT_ID) String attachmentId) {
        return service.download(classId, cardId, attachmentId);
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/preview")
    public Object preview(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, @PathParam(ATTACHMENT_ID) String attachmentId) {
        return service.preview(classId, cardId, attachmentId);
    }

    @PUT
    @Path("{" + ATTACHMENT_ID + "}/")
    @Consumes(MULTIPART_FORM_DATA)
    @Produces(APPLICATION_JSON)
    public Object update(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, @PathParam(ATTACHMENT_ID) String attachmentId, @Nullable @Multipart(value = ATTACHMENT, required = false) WsAttachmentData attachment, @Nullable @Multipart(value = FILE, required = false) DataHandler dataHandler) {
        return service.update(classId, cardId, attachmentId, attachment, dataHandler);
    }

    @DELETE
    @Path("{" + ATTACHMENT_ID + "}/")
    public Object delete(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, @PathParam(ATTACHMENT_ID) String attachmentId) {
        return service.delete(classId, cardId, attachmentId);
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/history")
    public Object getAttachmentHistory(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, @PathParam(ATTACHMENT_ID) String attachmentId) {
        return service.getAttachmentHistory(classId, cardId, attachmentId);
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/history/{version}/{file: [^/]+}")
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler downloadPreviousVersion(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, @PathParam(ATTACHMENT_ID) String attachmentId, @PathParam("version") String versionId) {
        return service.downloadPreviousVersion(classId, cardId, attachmentId, versionId);
    }

}
