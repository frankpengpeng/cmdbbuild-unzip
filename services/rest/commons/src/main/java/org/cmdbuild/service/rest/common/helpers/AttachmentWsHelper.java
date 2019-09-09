package org.cmdbuild.service.rest.common.helpers;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.activation.DataHandler;

import static com.google.common.base.Preconditions.checkNotNull;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;
import javax.ws.rs.PathParam;
import org.apache.commons.codec.binary.Base64;
import org.cmdbuild.dao.core.q3.DaoService;

import org.cmdbuild.dms.inner.DocumentInfoAndDetail;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import org.cmdbuild.dms.DmsService;
import org.cmdbuild.dms.DocumentDataImpl;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import org.cmdbuild.translation.TranslationService;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlankOrEmpty;
import org.springframework.stereotype.Component;

@Component
public class AttachmentWsHelper {

    private final DaoService dao;
    private final DmsService documentService;
    private final TranslationService translationService;

    public AttachmentWsHelper(DaoService dao, DmsService documentService, TranslationService translationService) {
        this.dao = checkNotNull(dao);
        this.documentService = checkNotNull(documentService);
        this.translationService = checkNotNull(translationService);
    }

    public Object create(String classId, Long cardId, @Nullable WsAttachmentData attachment, DataHandler dataHandler) throws IOException {
        checkCanRead(classId, cardId);
        DocumentInfoAndDetail document = documentService.create(classId, cardId, DocumentDataImpl.builder()
                .withData(dataHandler.getInputStream())
                .accept((b) -> {
                    if (attachment != null) {
                        b.withCategory(attachment.getCategory()).withDescription(attachment.getDescription()).withFilename(firstNotBlankOrEmpty(attachment.getFileName(), dataHandler.getName()));
                    } else {
                        b.withDescription("").withFilename(dataHandler.getName());
                    }
                })
                .withMajorVersion(true)
                .build());
        return response(serializeAttachment(classId, document));
    }

    public Object copyFrom(String classId, Long cardId, @Nullable WsAttachmentData attachment, @Nullable String sourceClassId, @Nullable Long sourceCardId, @Nullable String sourceAttachmentId) throws IOException {
        checkCanRead(classId, cardId);
        checkCanRead(sourceClassId, sourceCardId);
        DocumentInfoAndDetail source = documentService.getCardAttachmentById(sourceClassId, sourceCardId, sourceAttachmentId);
        byte[] data = toByteArray(documentService.download(source.getDocumentId()));
        DocumentInfoAndDetail document = documentService.create(classId, cardId, DocumentDataImpl.builder() //TODO duplicate code 
                .withData(data)
                .withFilename(source.getFileName())
                .accept((b) -> {
                    if (attachment != null) {
                        b.withCategory(attachment.getCategory()).withDescription(attachment.getDescription());
                    } else {
                        b.withDescription("");
                    }
                })
                .withMajorVersion(true)
                .build());
        return response(serializeAttachment(classId, document));
    }

    public Object read(String classId, Long cardId) {
        checkCanRead(classId, cardId);
        List<DocumentInfoAndDetail> list = documentService.getCardAttachments(classId, cardId);
        return response(list.stream().map(d -> serializeAttachment(classId, d)).collect(toList()));
    }

    public Object read(String classId, Long cardId, String attachmentId) {
        checkCanRead(classId, cardId, attachmentId);
        DocumentInfoAndDetail document = documentService.getCardAttachmentById(classId, cardId, attachmentId);
        return response(serializeAttachment(classId, document));
    }

    public DataHandler download(String classId, Long cardId, String attachmentId) {
        checkCanRead(classId, cardId);
        return documentService.download(attachmentId);//TODO permission check
    }

    public Object preview(String classId, Long cardId, String attachmentId) {
        checkCanRead(classId, cardId);
        Optional<DataHandler> preview = documentService.preview(attachmentId);//TODO permission check
        return map("success", true, "data", map().accept((map) -> {
            map.put("hasPreview", preview.isPresent());
            if (preview.isPresent()) {
                map.put("dataUrl", toDataUrl(preview.get()));
            }
        }));
    }

    private String toDataUrl(DataHandler dataHandler) {
        try {
            StringBuilder dataUrl = new StringBuilder("data:");
            String mediaType = dataHandler.getContentType();
            dataUrl.append(mediaType);
            dataUrl.append(";base64,");
            String base64payload = Base64.encodeBase64String(toByteArray(dataHandler.getInputStream()));
            dataUrl.append(base64payload);
            return dataUrl.toString();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Object update(String classId, Long cardId, String attachmentId, @Nullable WsAttachmentData attachment, @Nullable DataHandler dataHandler) {
        checkCanRead(classId, cardId);

        DocumentInfoAndDetail document = documentService.updateDocumentWithAttachmentId(classId, cardId, attachmentId, DocumentDataImpl.builder()
                .accept((b) -> {
                    if (attachment != null) {
                        b.withCategory(attachment.getCategory()).withDescription(attachment.getDescription());
                    } else {
                        b.withDescription("");
                    }
                    if (attachment.getFileName() == null) {
                        b.withFilename(documentService.getCardAttachmentById(classId, cardId, attachmentId).getFileName());
                        b.withData(documentService.download(attachmentId));
                    } else {
                        b.withFilename(attachment.getFileName());
                        b.withData(dataHandler);
                    }
                })
                .withMajorVersion(true)
                .build());
        return response(serializeAttachment(classId, document));
    }

    public Object delete(String classId, Long cardId, String attachmentId) {
        checkCanRead(classId, cardId);
        documentService.delete(attachmentId);//TODO permission check
        return success();
    }

    public Object getAttachmentHistory(String classId, Long cardId, String attachmentId) {
        checkCanRead(classId, cardId, attachmentId);
        List<DocumentInfoAndDetail> versions = documentService.getCardAttachmentVersions(classId, cardId, attachmentId);
        return response(versions.stream().map(d -> serializeAttachment(classId, d)).collect(toList()));
    }

    public DataHandler downloadPreviousVersion(String classId, Long cardId, String attachmentId, @PathParam("version") String versionId) {
        checkCanRead(classId, cardId, attachmentId);
        return documentService.download(attachmentId, versionId);//TODO permission check
    }

    private Object serializeAttachment(String classId, DocumentInfoAndDetail input) {
        return map(
                "_id", input.getDocumentId(),
                "name", input.getFileName(),
                "category", input.getCategory(),
                "_category_description", input.hasCategory() ? documentService.getCategoryLookupForAttachment(dao.getClasse(classId), input).getDescription() : null,
                "_category_description_translation", input.hasCategory() ? translationService.translateLookupDescription(documentService.getCategoryLookupForAttachment(dao.getClasse(classId), input).getType().getName(), documentService.getCategoryLookupForAttachment(dao.getClasse(classId), input).getCode(), documentService.getCategoryLookupForAttachment(dao.getClasse(classId), input).getDescription()) : null,
                "description", input.getDescription(),
                "version", input.getVersion(),
                "author", input.getAuthor(),
                "created", toIsoDateTime(input.getCreated()),
                "modified", toIsoDateTime(input.getModified()));
    }

    private void checkCanRead(String classId, Long cardId) {//TODO
        //TODO handle special case for 'Email' classes
//		Classe targetClass = dataAccessLogic.findClass(classId);
//		if (targetClass == null) {
//			errorHandler.classNotFound(classId);
//		}
//		try {
//			dataAccessLogic.fetchCMCard(classId, cardId);
//		} catch (NoSuchElementException e) {
//			errorHandler.cardNotFound(cardId);
//		}
    }

    private void checkCanRead(String classId, Long cardId, String attachmentId) {//TODO
        //TODO handle special case for 'Email' classes
//		Classe targetClass = dataAccessLogic.findClass(classId);
//		if (targetClass == null) {
//			errorHandler.classNotFound(classId);
//		}
//		try {
//			dataAccessLogic.fetchCMCard(classId, cardId);
//		} catch (NoSuchElementException e) {
//			errorHandler.cardNotFound(cardId);
//		}
//		if (isBlank(attachmentId)) {
//			errorHandler.missingAttachmentId();
//		}
    }

    public static class WsAttachmentData {

        private final String category, description, fileName;

        public WsAttachmentData(@JsonProperty("category") String category, @JsonProperty("description") String description, @JsonProperty("file") String fileName) {
            this.category = category;
            this.description = description;
            this.fileName = fileName;
        }

        public String getCategory() {
            return category;
        }

        public String getDescription() {
            return description;
        }

        public String getFileName() {
            return fileName;
        }

    }

}
