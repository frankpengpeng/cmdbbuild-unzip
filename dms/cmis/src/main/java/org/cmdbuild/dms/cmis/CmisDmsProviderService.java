package org.cmdbuild.dms.cmis;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.apache.chemistry.opencmis.commons.PropertyIds.NAME;
import static org.apache.chemistry.opencmis.commons.PropertyIds.OBJECT_TYPE_ID;
import static org.apache.chemistry.opencmis.commons.PropertyIds.SECONDARY_OBJECT_TYPE_IDS;
import static org.apache.chemistry.opencmis.commons.SessionParameter.ATOMPUB_URL;
import static org.apache.chemistry.opencmis.commons.SessionParameter.AUTH_HTTP_BASIC;
import static org.apache.chemistry.opencmis.commons.SessionParameter.BINDING_TYPE;
import static org.apache.chemistry.opencmis.commons.SessionParameter.CONNECT_TIMEOUT;
import static org.apache.chemistry.opencmis.commons.SessionParameter.PASSWORD;
import static org.apache.chemistry.opencmis.commons.SessionParameter.READ_TIMEOUT;
import static org.apache.chemistry.opencmis.commons.SessionParameter.USER;
import static org.apache.chemistry.opencmis.commons.enums.CmisVersion.CMIS_1_0;
import static org.apache.chemistry.opencmis.commons.enums.VersioningState.MAJOR;
import static org.apache.chemistry.opencmis.commons.enums.VersioningState.MINOR;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.activation.DataHandler;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.CmisVersion;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import static com.google.common.collect.Lists.transform;
import static com.google.common.collect.MoreCollectors.toOptional;
import java.io.ByteArrayInputStream;
import java.io.File;
import static java.lang.Math.toIntExact;
import javax.annotation.Nullable;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Rendition;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.tika.Tika;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.Holder;
import static org.cmdbuild.common.error.ErrorAndWarningCollectorService.marker;
import org.cmdbuild.config.api.ConfigListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static org.cmdbuild.utils.lang.CmCollectionUtils.isNullOrEmpty;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dms.DocumentData;
import org.cmdbuild.dms.inner.DmsProviderService;
import org.cmdbuild.dms.inner.DocumentInfoAndDetail;
import org.cmdbuild.dms.inner.DocumentInfoAndDetailImpl;
import static org.cmdbuild.dms.inner.DocumentPathUtils.buildDocumentPath;
import org.cmdbuild.services.MinionStatus;
import static org.cmdbuild.utils.date.CmDateUtils.toDateTime;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.blankToNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableString;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import static org.cmdbuild.utils.lang.CmNullableUtils.ltEqZeroToNull;
import org.cmdbuild.services.MinionComponent;
import org.cmdbuild.services.MinionConfig;
import static org.cmdbuild.services.MinionConfig.MC_DISABLED;
import static org.cmdbuild.services.MinionConfig.MC_ENABLED;
import static org.cmdbuild.services.MinionStatus.MS_READY;
import static org.cmdbuild.services.MinionStatus.MS_ERROR;
import static org.cmdbuild.services.MinionStatus.MS_DISABLED;
import static org.cmdbuild.utils.encode.CmEncodeUtils.decodeString;
import static org.cmdbuild.utils.encode.CmEncodeUtils.encodeString;
import org.cmdbuild.utils.io.BigByteArray;
import static org.cmdbuild.utils.io.CmIoUtils.newDataHandler;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@Component
@MinionComponent(name = "Cmis DMS client", config = "org.cmdbuild.dms.cmis")//TODO config ns const
public class CmisDmsProviderService implements DmsProviderService {

    private static final String CMIS_DOCUMENT = "cmis:document",
            CMIS_FOLDER = "cmis:folder",
            CMIS_PROPERTY_AUTHOR = "cm:author",
            CMIS_PROPERTY_DESCRIPTION = PropertyIds.DESCRIPTION,
            CMIS_PROPERTY_CATEGORY = "cmdbuild:classification";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Tika tika = new Tika();

    private final DaoService dao;
    private final CmisDmsConfiguration configuration;

    private final Holder<Repository> repository;

    public CmisDmsProviderService(DaoService dao, CmisDmsConfiguration configuration, CacheService cacheService) {
        this.dao = checkNotNull(dao);
        this.configuration = checkNotNull(configuration);
        this.repository = cacheService.newHolder("dms_cmis_repository");
    }

    private void doClearAllCache() {
        repository.invalidate();
    }

    public MinionStatus getServiceStatus() {
        if (!isEnabled()) {
            return MS_DISABLED;
        } else if (isOk()) {
            return MS_READY;
        } else {
            return MS_ERROR;
        }
    }

    public MinionConfig getMinionConfig() {
        return isEnabled() ? MC_ENABLED : MC_DISABLED;
    }

    @Override
    public String getDmsProviderServiceName() {
        return DMS_PROVIDER_CMIS;
    }

    @Override
    public BigByteArray exportAllDocumentsAsZipFile() {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public List<DocumentInfoAndDetail> getDocuments(String classId, long cardId) {
        logger.debug("get all attachments for classId = %s cardId = %s", classId, cardId);
        Session session = createSession();
        Folder folder = getFolderOrNull(session, buildCmisPath(classId, cardId));
        if (folder == null) {
            return emptyList();
        } else {
            return list(folder.getChildren()).stream().filter(Document.class::isInstance).map(Document.class::cast).map(this::toDocumentInfoAndDetail).collect(toList());
        }
    }

    @Override
    public List<DocumentInfoAndDetail> getDocumentVersions(String documentId) {
        logger.debug("get all attachment versions for documentId = %s", documentId);
        Session session = createSession();
        Document document = (Document) session.getObject(decodeString(checkNotBlank(documentId)));
        if (document == null || !document.isVersionable()) {
            return emptyList();
        } else {
            return document.getAllVersions().stream().map(this::toDocumentInfoAndDetail).collect(toList());
        }
    }

    @Override
    public DataHandler download(String documentId, @Nullable String version) {
        logger.debug("download document id = %s version = %s", documentId, version);
        Document document = (Document) createSession().getObject(decodeString(checkNotBlank(documentId)));
        if (isNotBlank(version)) {
            document = checkNotNull(document.getAllVersions().stream().filter(v -> equal(v.getVersionLabel(), version)).collect(toOptional()).orElse(null), "document not found for version = %s", version);
        }
        return newDataHandler(toByteArray(document.getContentStream().getStream()), document.getContentStreamMimeType(), document.getContentStreamFileName());
    }

    @Override
    public Optional<DataHandler> preview(String documentId) {
        logger.debug("get document preview for doc id = %s", documentId);
        Session session = createSession();
        OperationContext context = session.createOperationContext();
        context.setRenditionFilterString("cmis:thumbnail");
        Document cmisDocument = (Document) session.getObject(decodeString(checkNotBlank(documentId)));
        List<Rendition> list = cmisDocument.getRenditions();
        if (isNullOrEmpty(list)) {
            logger.warn("no preview available for document = {}", documentId);
            return Optional.empty();
        } else {
            logger.debug("available previews = {}", list);
            Rendition rendition = list.iterator().next(); //should contain only one record, as per rendition filter before
            logger.debug("selected preview = {}", rendition);
            return Optional.of(newDataHandler(toByteArray(rendition.getContentStream().getStream()), rendition.getContentStream().getMimeType(), rendition.getContentStream().getFileName()));
        }
    }
//

    @Override
    public DocumentInfoAndDetail getDocument(String documentId) {
        logger.debug("get document with id = %s", documentId);
        Session session = createSession();
        Document document = (Document) session.getObject(decodeString(checkNotBlank(documentId)));
        return toDocumentInfoAndDetail(document);
    }

    @Override
    public DocumentInfoAndDetail create(String classId, long cardId, DocumentData data) {
        logger.debug("create document for classId = %s cardId = %s fileName = %s", data.getFilename());
        Session session = createSession();
        Folder folder = getFolderCreateIfNotExists(session, buildCmisPath(classId, cardId));
        byte[] bytes = checkNotNull(data.getData());
        String mimeType = tika.detect(bytes);
        Document document = folder.createDocument(getProperties(session, data, null),
                session.getObjectFactory().createContentStream(data.getFilename(), bytes.length, mimeType, new ByteArrayInputStream(bytes)),
                data.isMajorVersion() ? MAJOR : MINOR);
        return toDocumentInfoAndDetail(document);
    }

    @Override
    public DocumentInfoAndDetail update(String documentId, DocumentData data) {
        logger.debug("update document with id = %s fileName = %s", documentId, data.getFilename());
        Session session = createSession();
        Document document = (Document) session.getObject(decodeString(checkNotBlank(documentId)));
        if (data.hasData()) {
            document = (Document) session.getObject(document.checkOut());
            try {
                byte[] bytes = checkNotNull(data.getData());
                String mimeType = tika.detect(bytes);
                document.checkIn(data.isMajorVersion(), getProperties(session, data, document), session.getObjectFactory().createContentStream(data.getFilename(), bytes.length, mimeType, new ByteArrayInputStream(bytes)), "");
            } catch (Exception e) {
                document.cancelCheckOut();
                throw runtime(e);
            }
        } else {
            document.updateProperties(getProperties(session, data, document));
        }
        return getDocument(documentId);
    }

    @Override
    public void delete(String documentId) {
        logger.debug("delete document with id = %s", documentId);
        ((Document) createSession().getObject(decodeString(checkNotBlank(documentId)))).delete();
    }

    @ConfigListener(CmisDmsConfiguration.class)
    public void reload() {
        doClearAllCache();
        if (isEnabled()) {
            try {
                checkNotNull(getRepository());
                logger.info("dms service ready");
            } catch (Exception ex) {
                logger.error(marker(), "error starting dms service", ex);
            }
        }
    }

    private boolean isOk() {
        try {
            checkNotNull(getRepository());
            return true;
        } catch (Exception ex) {
            logger.warn("cmis dms service is NOT OK : {}", ex.toString());
            return false;
        }
    }

    private boolean isEnabled() {
        return configuration.isEnabled(getDmsProviderServiceName());
    }

    private String buildCmisPath(String classId, long cardId) {
        return toCmisPath(buildDocumentPath(dao.getClasse(classId), cardId));
    }

    private DocumentInfoAndDetail toDocumentInfoAndDetail(Document document) {
        String category = Optional.ofNullable(document.getProperty(CMIS_PROPERTY_CATEGORY)).map(p -> toStringOrNull(p.getValue())).orElse(null);
        String author = Optional.ofNullable(document.getProperty(CMIS_PROPERTY_AUTHOR)).map(p -> toStringOrNull(p.getValue())).orElse(null);
        return DocumentInfoAndDetailImpl.builder()
                .withDocumentId(encodeString(document.getId()))
                .withAuthor(author)
                .withCategory(category)
                .withCreated(toDateTime(document.getCreationDate()))
                .withDescription(document.getDescription())
                .withFileName(document.getName())
                .withFileSize(ltEqZeroToNull((Integer) toIntExact(document.getContentStreamLength())))
                .withMimeType(firstNonNull(document.getContentStreamMimeType(), "application/octet-stream"))
                .withModified(toDateTime(document.getLastModificationDate()))
                .withVersion(document.getVersionLabel())
                .build();
    }

    private Session createSession() {
        return getRepository().createSession();
    }

    private @Nullable
    Folder getFolderOrNull(Session session, String path) {
        try {
            return (Folder) session.getObjectByPath(path);
        } catch (CmisObjectNotFoundException e) {
            return null;
        }
    }

    private String toCmisPath(String path) {
        return new File(configuration.getCmisPath(), path).getPath();
    }

    private Folder getFolderCreateIfNotExists(Session session, String path) {
        try {
            return (Folder) session.getObjectByPath(path);
        } catch (CmisObjectNotFoundException e) {
            String parentPath = new File(path).getParentFile().getPath(),//TODO check file usage
                    name = new File(path).getName();
            Folder parentFolder = (Folder) getFolderCreateIfNotExists(session, parentPath);
            logger.debug("create cmis folder = {}", path);
            parentFolder.createFolder(map(
                    OBJECT_TYPE_ID, CMIS_FOLDER,
                    NAME, name
            ));
            return (Folder) session.getObjectByPath(path);
        }
    }

    private Map<String, Object> getProperties(Session session, DocumentData document, @Nullable Document cmisDocument) {
        Map<String, Object> map = map();

        CmisVersion version = session.getRepositoryInfo().getCmisVersion();
        if (version.compareTo(CMIS_1_0) <= 0) {
            logger.warn("secondary types not supported by this protocol version ({})", version);
        } else {
            map.putAll((Map) map(
                    CMIS_PROPERTY_AUTHOR, toStringOrNull(document.getAuthor()),
                    SECONDARY_OBJECT_TYPE_IDS, list(set("P:cm:author", "P:cm:titled", "P:cmdbuild:classifiable").accept(s -> {
                        if (cmisDocument != null) {
                            cmisDocument.getSecondaryTypes().stream().map(ObjectType::getId).forEach(s::add);
                        }
                    }))
            ).skipNullValues().with(
                    CMIS_PROPERTY_DESCRIPTION, document.getDescription(),
                    CMIS_PROPERTY_CATEGORY, blankToNull(document.getCategory())
            ));
        }

        if (cmisDocument == null) {
            map.put(OBJECT_TYPE_ID, CMIS_DOCUMENT);
            map.put(NAME, document.getFilename());
        }

        logger.debug("built cmis document properties: \n{}\n", mapToLoggableString(map));

        return map;
    }

    private Repository getRepository() {
        return repository.get(this::doGetRepository);
    }

    private Repository doGetRepository() {
        logger.info("init dms (cmis) repository");
        SessionFactory sessionFactory = SessionFactoryImpl.newInstance();
        Map<String, String> parameters = map(
                ATOMPUB_URL, configuration.getCmisUrl(),
                BINDING_TYPE, BindingType.ATOMPUB.value(),
                AUTH_HTTP_BASIC, "true",
                USER, configuration.getCmisUser(),
                PASSWORD, configuration.getCmisPassword(),
                CONNECT_TIMEOUT, Integer.toString(10000),
                READ_TIMEOUT, Integer.toString(30000));
        logger.debug("parameters for dms repository : \n\n{}\n", mapToLoggableString(parameters));
        List<Repository> repositories = sessionFactory.getRepositories(parameters);
        logger.debug("got repository list = {}", transform(repositories, Repository::getId));
        Repository firstRepo = repositories.get(0);
        logger.debug("selected repository = {}", firstRepo.getId());
        return firstRepo;
    }

}
