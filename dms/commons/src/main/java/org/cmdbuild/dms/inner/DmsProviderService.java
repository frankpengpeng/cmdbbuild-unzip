package org.cmdbuild.dms.inner;

import java.util.List;
import java.util.Optional;

import javax.activation.DataHandler;
import javax.annotation.Nullable;
import org.cmdbuild.dms.DocumentData;
import org.cmdbuild.utils.io.BigByteArray;

public interface DmsProviderService {

    static final String DMS_PROVIDER_CMIS = "cmis", DMS_PROVIDER_POSTGRES = "postgres";

    String getDmsProviderServiceName();

    DocumentInfoAndDetail getDocument(String documentId);

    List<DocumentInfoAndDetail> getDocuments(String classId, long cardId);

    List<DocumentInfoAndDetail> getDocumentVersions(String documentId);

    DocumentInfoAndDetail create(String classId, long cardId, DocumentData document);

    DocumentInfoAndDetail update(String documentId, DocumentData document);

    DataHandler download(String documentId, @Nullable String version);

    default Optional<DataHandler> preview(String documentId) {
        return Optional.empty();
    }

    void delete(String documentId);

    BigByteArray exportAllDocumentsAsZipFile();

}
