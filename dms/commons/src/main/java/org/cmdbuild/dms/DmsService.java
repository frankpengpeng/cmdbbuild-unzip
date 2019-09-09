package org.cmdbuild.dms;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.Optional;

import javax.activation.DataHandler;

import javax.annotation.Nullable;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dms.inner.DocumentInfoAndDetail;
import org.cmdbuild.lookup.Lookup;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;

public interface DmsService {

    List<DocumentInfoAndDetail> getCardAttachments(String className, long cardId);

    @Nullable
    DocumentInfoAndDetail getCardAttachmentOrNull(String className, long cardId, String fileName);

    default DocumentInfoAndDetail getCardAttachmentWithFilename(String className, long cardId, String fileName) {
        return checkNotNull(getCardAttachmentOrNull(className, cardId, fileName), "card attachment not found for class = %s card = %s fileName = %s", className, cardId, fileName);
    }

    DocumentInfoAndDetail getCardAttachmentById(String className, long cardId, String documentId);

    List<DocumentInfoAndDetail> getCardAttachmentVersions(String className, long cardId, String filename);

    DocumentInfoAndDetail create(String className, long cardId, DocumentData document);

    DocumentInfoAndDetail updateDocumentWithAttachmentId(String className, long cardId, String attachmentId, DocumentData documentData);

    DataHandler download(String documentId, @Nullable String version);

    Optional<DataHandler> preview(String documentId);

    void delete(String documentId);

    Lookup getCategoryLookupForAttachment(Classe classe, DocumentInfoAndDetail document);

    default DocumentInfoAndDetail create(Card card, DocumentData document) {
        return create(card.getClassName(), card.getId(), document);
    }

    default DataHandler download(String className, long cardId, String filename, @Nullable String version) {
        DocumentInfoAndDetail document = getCardAttachmentWithFilename(className, cardId, filename);
        return download(document.getDocumentId(), version);
    }

    default DataHandler download(String documentId) {
        return download(documentId, null);
    }

    default byte[] getDocumentContent(String documentId) {
        return toByteArray(download(documentId));
    }

    default void delete(String className, long cardId, String filename) {
        DocumentInfoAndDetail document = getCardAttachmentWithFilename(className, cardId, filename);
        delete(document.getDocumentId());
    }

    default DocumentInfoAndDetail updateDocumentWithFilename(String className, long cardId, String filename, DocumentData documentData) {
        DocumentInfoAndDetail document = getCardAttachmentWithFilename(className, cardId, filename);
        return updateDocumentWithAttachmentId(className, cardId, document.getDocumentId(), documentData);
    }

    DataHandler exportAllDocuments();

    boolean isEnabled();

}
