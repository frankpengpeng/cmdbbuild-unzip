/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dms.dao;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.IOException;
import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.activation.DataHandler;
import javax.annotation.Nullable;
import static org.apache.commons.lang.StringUtils.isBlank;
import org.apache.tika.Tika;
import org.cmdbuild.dms.DmsConfiguration;
import org.cmdbuild.dms.DocumentData;
import org.springframework.stereotype.Component;
import org.cmdbuild.dms.inner.DmsProviderService;
import org.cmdbuild.dms.inner.DocumentInfoAndDetail;
import org.cmdbuild.services.MinionStatus;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import static org.cmdbuild.utils.hash.CmHashUtils.hash;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import org.cmdbuild.services.MinionComponent;
import org.cmdbuild.services.MinionConfig;
import static org.cmdbuild.services.MinionConfig.MC_DISABLED;
import static org.cmdbuild.services.MinionConfig.MC_ENABLED;
import static org.cmdbuild.services.MinionStatus.MS_READY;
import static org.cmdbuild.services.MinionStatus.MS_DISABLED;
import org.cmdbuild.utils.io.BigByteArray;
import org.cmdbuild.utils.io.BigByteArrayOutputStream;
import org.springframework.transaction.annotation.Transactional;
import static org.cmdbuild.utils.io.CmIoUtils.newDataHandler;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowConsumer;

@Component
@MinionComponent(name = "Postgres DMS Service", config = "org.cmdbuild.dms.postgres")
public class PgDmsProviderServiceImpl implements DmsProviderService {

    private final Tika tika = new Tika();

    private final DocumentInfoRepository repository;
    private final DocumentDataRepository dataRepository;
    private final DmsConfiguration configuration;

    public PgDmsProviderServiceImpl(DocumentInfoRepository repository, DocumentDataRepository dataRepository, DmsConfiguration dmsConfiguration) {
        this.repository = checkNotNull(repository);
        this.dataRepository = checkNotNull(dataRepository);
        this.configuration = checkNotNull(dmsConfiguration);
    }

    @Override
    public String getDmsProviderServiceName() {
        return DMS_PROVIDER_POSTGRES;
    }

    public MinionStatus getServiceStatus() {
        if (!isEnabled()) {
            return MS_DISABLED;
        } else {
            return MS_READY;
        }
    }

    public MinionConfig getMinionConfig() {
        return isEnabled() ? MC_ENABLED : MC_DISABLED;
    }

    private boolean isEnabled() {
        return configuration.isEnabled(getDmsProviderServiceName());
    }

    @Override
    public BigByteArray exportAllDocumentsAsZipFile() {
        BigByteArrayOutputStream out = new BigByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(out)) {
            repository.getAll().stream().forEach(rethrowConsumer(d -> {
                ZipEntry entry = new ZipEntry(format("Class/%s/%s", d.getCardId(), d.getFileName()));
                zip.putNextEntry(entry);
                zip.write(dataRepository.getDocumentData(d.getId()));
                zip.closeEntry();
            }));
        } catch (IOException ex) {
            throw runtime(ex);
        }
        return out.toBigByteArray();
    }

    @Override
    public List<DocumentInfoAndDetail> getDocuments(String classId, long cardId) {
        return (List) repository.getAllByCardId(cardId);
    }

    @Override
    public DocumentInfoAndDetail getDocument(String documentId) {
        return repository.getById(toLong(documentId));
    }

    @Override
    public List<DocumentInfoAndDetail> getDocumentVersions(String documentId) {
        return (List) repository.getAllVersions(toLong(documentId));
    }

    @Override
    @Transactional
    public DocumentInfoAndDetail create(String className, long cardId, DocumentData document) {
        byte[] data = checkNotNull(document.getData(), "cannot create document with null data");
        PgDocument documentInfo = PgDocumentImpl.builder()
                .withAuthor(document.getAuthor())
                .withCardId(cardId)
                .withCategory(document.getCategory())
                .withCreated(now())
                .withDescription(document.getDescription())
                .withFileName(document.getFilename())
                .withFileSize(data.length)
                .withHash(hash(data))
                .withMimeType(tika.detect(data))
                .withModified(now())
                .withVersion(nextVersion(null, true))
                .build();
        documentInfo = repository.create(documentInfo);
        dataRepository.createDocumentData(documentInfo.getId(), data);
        return documentInfo;
    }

    @Override
    @Transactional
    public DocumentInfoAndDetail update(String documentId, DocumentData document) {
        PgDocument currentDocument = repository.getById(toLong(documentId));
        byte[] data = document.getData();
        PgDocument documentInfo = PgDocumentImpl.copyOf(currentDocument)
                .withAuthor(document.getAuthor())
                .withCategory(document.getCategory())
                .withDescription(document.getDescription())
                .accept((b) -> {
                    if (data != null) {
                        b.withFileName(document.getFilename())
                                .withFileSize(data.length)
                                .withHash(hash(data))
                                .withMimeType(tika.detect(data))
                                .withModified(now())
                                .withVersion(nextVersion(currentDocument.getVersion(), document.isMajorVersion()));
                    }
                })
                .build();
        documentInfo = repository.update(documentInfo);
        if (data != null) {
            dataRepository.updateDocumentData(documentInfo.getId(), data);
        }
        return documentInfo;
    }

    @Override
    public DataHandler download(String attachmentId, @Nullable String version) {
        PgDocument document;
        if (isBlank(version)) {
            document = repository.getById(toLong(attachmentId));
        } else {
            document = repository.getByIdAndVersion(toLong(attachmentId), version);
        }
        byte[] data = dataRepository.getDocumentData(document.getId());
        return newDataHandler(data, document.getMimeType(), document.getFileName());
    }

    @Override
    public void delete(String documentId) {
        PgDocument document = repository.getById(toLong(documentId));
        repository.delete(document);
    }

    private static String nextVersion(@Nullable String currentVersion, boolean major) {
        if (isBlank(currentVersion)) {
            return "1.0";
        } else {
            Matcher matcher = Pattern.compile("^([0-9]+)[.]([0-9]+)$").matcher(currentVersion);
            checkArgument(matcher.find(), "invalid version syntax for value = '%s'", currentVersion);
            int first = parseInt(matcher.group(1)), last = parseInt(matcher.group(2));
            if (major) {
                first++;
                last = 0;
            } else {
                last++;
            }
            return format("%s.%s", first, last);
        }
    }

}
