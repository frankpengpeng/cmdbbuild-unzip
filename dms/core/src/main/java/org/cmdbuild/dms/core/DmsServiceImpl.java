package org.cmdbuild.dms.core;

import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.transform;
import com.google.common.collect.Maps;
import static com.google.common.collect.MoreCollectors.toOptional;
import static java.lang.String.format;
import java.util.Optional;
import javax.annotation.Nullable;
import static org.apache.commons.lang.NumberUtils.isNumber;
import static org.apache.commons.lang.StringUtils.isBlank;
import org.cmdbuild.auth.user.OperationUserSupplier;
import static org.cmdbuild.common.error.ErrorAndWarningCollectorService.marker;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.Classe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.dms.DmsConfiguration;
import org.cmdbuild.dms.inner.DocumentInfoAndDetail;
import org.cmdbuild.dms.DmsService;
import org.cmdbuild.dms.DocumentData;
import org.cmdbuild.dms.DocumentDataImpl;
import org.cmdbuild.dms.inner.DmsProviderService;
import org.cmdbuild.dms.inner.DocumentInfoAndDetailImpl;
import org.cmdbuild.lookup.Lookup;
import org.cmdbuild.lookup.LookupRepository;
import org.cmdbuild.services.MinionStatus;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLongOrNull;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNullOrLtEqZero;
import org.cmdbuild.services.MinionComponent;
import static org.cmdbuild.services.MinionStatus.MS_READY;
import static org.cmdbuild.services.MinionStatus.MS_ERROR;
import static org.cmdbuild.services.MinionStatus.MS_DISABLED;
import org.cmdbuild.utils.date.CmDateUtils;
import static org.cmdbuild.utils.io.CmIoUtils.newDataHandler;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@Component
@MinionComponent(name = "DMS Service", configBean = DmsConfiguration.class)
public class DmsServiceImpl implements DmsService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DmsConfiguration configuration;
    private final Map<String, DmsProviderService> services;
    private final LookupRepository lookupStore;
    private final DaoService dao;
    private final OperationUserSupplier userSupplier;

    public DmsServiceImpl(List<DmsProviderService> services, DmsConfiguration configuration, LookupRepository lookupStore, DaoService dao, OperationUserSupplier userSupplier) {
        this.configuration = checkNotNull(configuration);
        this.services = Maps.uniqueIndex(checkNotNull(services), DmsProviderService::getDmsProviderServiceName);
        this.lookupStore = checkNotNull(lookupStore);
        this.dao = checkNotNull(dao);
        this.userSupplier = checkNotNull(userSupplier);
    }

    public MinionStatus getServiceStatus() {
        if (!configuration.isEnabled()) {
            return MS_DISABLED;
        } else if (isDmsServiceOk()) {
            return MS_READY;
        } else {
            return MS_ERROR;
        }
    }

    private boolean isDmsServiceOk() {
        try {
            checkNotNull(getService());
            return true;
        } catch (Exception ex) {
            logger.warn("dms service is NOT OK : {}", ex.toString());
            return false;
        }
    }

    private DmsProviderService getService() {
        return checkNotNull(services.get(configuration.getService()), "dms service not found for name = %s", configuration.getService());
    }

    @Override
    public boolean isEnabled() {
        return configuration.isEnabled();
    }

    @Override
    public DocumentInfoAndDetail getCardAttachmentById(String className, long cardId, String documentId) {
        return cmisCategoryToCmdbuildCategory(dao.getClasse(className), getService().getDocument(documentId));//TODO validate card id, class id
    }

    @Override
    public List<DocumentInfoAndDetail> getCardAttachments(String className, long cardId) {
        logger.debug("search all documents for className = {} and cardId = {}", className, cardId);
        return cmisCategoryToCmdbuildCategory(dao.getClasse(className), getService().getDocuments(className, cardId));
    }

    @Override
    @Nullable
    public DocumentInfoAndDetail getCardAttachmentOrNull(String className, long cardId, String fileName) {
        return getCardAttachments(className, cardId).stream().filter((input) -> input.getDocumentId().equals(fileName)).collect(toOptional()).orElse(null);//TODO replace with more efficent query
    }

    @Override
    public List<DocumentInfoAndDetail> getCardAttachmentVersions(String className, long cardId, String filename) {
        DocumentInfoAndDetail document = getCardAttachmentWithFilename(className, cardId, filename);
        return cmisCategoryToCmdbuildCategory(dao.getClasse(className), getService().getDocumentVersions(document.getDocumentId()));
    }

    @Override
    public DocumentInfoAndDetail create(String className, long cardId, DocumentData documentData) {
        Classe classe = dao.getClasse(className);
        documentData = cmdbuildCategoryToCmisCategory(classe, documentData);
        documentData = DocumentDataImpl.copyOf(documentData).withAuthor(userSupplier.getUsername()).build();
        checkArgument(getCardAttachmentOrNull(className, cardId, documentData.getFilename()) == null, "document already present for className = %s cardId = %s fileName = %s", className, cardId, documentData.getFilename());
        DocumentInfoAndDetail document = getService().create(className, cardId, documentData);
        return cmisCategoryToCmdbuildCategory(classe, document);
    }

    @Override
    public DocumentInfoAndDetail updateDocumentWithAttachmentId(String className, long cardId, String attachmentId, DocumentData documentData) {
        Classe classe = dao.getClasse(className);
        documentData = cmdbuildCategoryToCmisCategory(classe, documentData);
        documentData = DocumentDataImpl.copyOf(documentData).withAuthor(userSupplier.getUsername()).build();
        DocumentInfoAndDetail document = getService().update(attachmentId, documentData);//TODO validate card id, class id
        return cmisCategoryToCmdbuildCategory(classe, document);
    }

    @Override
    public DataHandler download(String attachmentId, @Nullable String version) {
        return getService().download(attachmentId, version);
    }

    @Override
    public Optional<DataHandler> preview(String attachmentId) {
        return getService().preview(attachmentId);
    }

    @Override
    public void delete(String documentId) {
        getService().delete(documentId);
    }

    @Override
    public Lookup getCategoryLookupForAttachment(Classe classe, DocumentInfoAndDetail document) {
        return lookupStore.getOneByTypeAndId(firstNotBlank(classe.getAttachmentTypeLookupTypeOrNull(), configuration.getDefaultDocumentCategoryLookup()), toLong(document.getCategory()));
    }

    private List<DocumentInfoAndDetail> cmisCategoryToCmdbuildCategory(Classe classe, List<DocumentInfoAndDetail> list) {
        return list(transform(list, d -> cmisCategoryToCmdbuildCategory(classe, d)));
    }

    private DocumentInfoAndDetail cmisCategoryToCmdbuildCategory(Classe classe, DocumentInfoAndDetail document) {
        if (isBlank(document.getCategory())) {
            return document;
        } else {
            return DocumentInfoAndDetailImpl.copyOf(document).withCategory(categoryFromCmis(classe.getAttachmentTypeLookupTypeOrNull(), document.getCategory())).build();
        }
    }

    private DocumentData cmdbuildCategoryToCmisCategory(Classe classe, DocumentData documentData) {
        if (isBlank(documentData.getCategory())) {
            return documentData;
        } else {
            return DocumentDataImpl.copyOf(documentData).withCategory(categoryToCmis(classe.getAttachmentTypeLookupTypeOrNull(), documentData.getCategory())).build();
        }
    }

    private @Nullable
    String categoryToCmis(@Nullable String categoryLookup, @Nullable String value) {
        if (isBlank(value)) {
            return null;
        } else if (isNumber(value)) {
            Long id = toLongOrNull(value);
            if (isNullOrLtEqZero(id)) {
                return null;
            } else {
                return lookupStore.getOneByTypeAndId(firstNotBlank(categoryLookup, configuration.getDefaultDocumentCategoryLookup()), id).getCode();
            }
        } else {
            return lookupStore.getOneByTypeAndCodeOrDescription(firstNotBlank(categoryLookup, configuration.getDefaultDocumentCategoryLookup()), value).getCode();
        }
    }

    private @Nullable
    String categoryFromCmis(@Nullable String categoryLookup, @Nullable Object value) {
        if (isBlank(toStringOrNull(value))) {
            return null;
        } else {
            try {
                return lookupStore.getOneByTypeAndCodeOrDescription(firstNotBlank(categoryLookup, configuration.getDefaultDocumentCategoryLookup()), toStringNotBlank(value)).getId().toString();
            } catch (Exception ex) {
                logger.warn(marker(), "received invalid category code from cmis = %s", value, ex);
                return null;
            }
        }
    }

    @Override
    public DataHandler exportAllDocuments() {
        return newDataHandler(getService().exportAllDocumentsAsZipFile(), "application/zip", format("dms_export_%s.zip", CmDateUtils.dateTimeFileSuffix()));
    }
}
