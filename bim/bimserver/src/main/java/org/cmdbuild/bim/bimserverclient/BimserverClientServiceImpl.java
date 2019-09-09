package org.cmdbuild.bim.bimserverclient;

import static com.google.common.base.Objects.equal;
import org.cmdbuild.bim.BimException;
import org.cmdbuild.config.BimserverConfiguration;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.util.List;

import javax.activation.DataHandler;

import org.apache.commons.lang3.StringUtils;
import org.bimserver.client.BimServerClient;
import org.bimserver.client.ClientIfcModel;
import org.bimserver.client.json.JsonBimServerClientFactory;
import org.bimserver.emf.IdEObject;
import org.bimserver.emf.IfcModelInterface;
import org.bimserver.interfaces.objects.SDeserializerPluginConfiguration;
import org.bimserver.interfaces.objects.SProject;
import org.bimserver.interfaces.objects.SRevision;
import org.bimserver.shared.UsernamePasswordAuthenticationInfo;
import org.cmdbuild.bim.legacy.model.Entity;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.slf4j.Logger;

import static java.lang.Long.parseLong;
import static java.util.Collections.emptyList;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;
import org.apache.commons.io.FileUtils;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.bimserver.interfaces.objects.SObjectState.ACTIVE;
import static org.cmdbuild.bim.utils.BimConstants.IFC_CONTENT_TYPE;
import org.cmdbuild.cache.CacheService;
import static org.cmdbuild.common.error.ErrorAndWarningCollectorService.marker;
import org.cmdbuild.services.MinionStatus;
import org.cmdbuild.services.PostStartup;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.cache.CmCache;
import org.cmdbuild.config.api.ConfigListener;
import org.cmdbuild.services.PreShutdown;
import org.cmdbuild.services.MinionComponent;
import static org.cmdbuild.services.MinionStatus.MS_READY;
import static org.cmdbuild.services.MinionStatus.MS_ERROR;
import static org.cmdbuild.services.MinionStatus.MS_DISABLED;
import static org.cmdbuild.services.MinionStatus.MS_NOTRUNNING;
import static org.cmdbuild.utils.io.CmIoUtils.countBytes;
import static org.cmdbuild.utils.io.CmIoUtils.newDataHandler;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;

@Component
@MinionComponent(name = "BIM Bimserver client", configBean = BimserverConfiguration.class)
public final class BimserverClientServiceImpl implements BimserverClientService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final BimserverConfiguration configuration;
    private BimServerClient client;
    private JsonBimServerClientFactory clientFactory;
    private final CmCache<IfcModelInterface> models;
    private final AtomicReference<MinionStatus> status = new AtomicReference<>(MS_NOTRUNNING);

    public BimserverClientServiceImpl(BimserverConfiguration configuration, CacheService cacheService) {
        this.configuration = checkNotNull(configuration);
        models = cacheService.newCache("bimserver_models");
    }

    @ConfigListener(BimserverConfiguration.class)
    public void reloadConfig() {
        disconnect();
        connectSafeIfEnabled();
    }

    public MinionStatus getServiceStatus() {
        return status.get();
    }

    @PostStartup
    public void init() {
        connectSafeIfEnabled();
    }

    @PreShutdown
    public void cleanup() {
        disconnect();
    }

    @Override
    public void uploadIfc(String projectId, DataHandler data, @Nullable String ifcFormat) {
        try {
            long size = countBytes(data);
            String fileName = data.getName();
            logger.info("upload ifc file = {} ({}) for bim project id = {} with suggested ifc format =< {} >", fileName, FileUtils.byteCountToDisplaySize(size), projectId, ifcFormat);
            Long poid = parseLong(projectId);
            SDeserializerPluginConfiguration deserializer;
            requireConnection();
            if (isBlank(ifcFormat)) {
                deserializer = client.getServiceInterface().getSuggestedDeserializerForExtension("ifc", poid);
            } else {
                String deserializerName = IfcVersions.valueOf(ifcFormat).getDeserializerName();
                deserializer = client.getServiceInterface().getDeserializerByName(deserializerName);
            }
            logger.debug("begin upload of ifc file = {} for bim poid = {} with actual ifc format = {}", fileName, poid, deserializer.getName());
            client.getServiceInterface().checkinSync(poid, "", deserializer.getOid(), size, fileName, data, false);
            logger.debug("completed upload of ifc file = {} for bim poid = {}", fileName, poid);
        } catch (Throwable e) {
            throw new BimException(e);
        }
    }

    @Override
    public BimserverProject createProject(String projectName, String description, String ifcVersion) {
        try {
            requireConnection();
            SProject project = client.getServiceInterface().addProject(projectName, ifcVersion);
            project.setDescription(description);
            client.getServiceInterface().updateProject(project);
            return toBimserverProject(project);
        } catch (Exception ex) {
            throw new BimException(ex);
        }
    }

    @Override
    public void disableProject(String projectId) {
        try {
            requireConnection();
            Long poid = new Long(projectId);
            client.getServiceInterface().deleteProject(poid);
        } catch (Exception e) {
            throw new BimException(e);
        }
    }

    @Override
    public void enableProject(String projectId) {
        try {
            requireConnection();
            Long poid = new Long(projectId);
            client.getServiceInterface().undeleteProject(poid);
        } catch (Exception e) {
            throw new BimException(e);
        }
    }

    @Override
    public DataHandler downloadIfc(String roid, String ifcVersion) {
        try {
            requireConnection();
            String serializerName = IfcVersions.valueOf(ifcVersion).getSerializerName();
            Serializer serializer = new BimserverSerializer(client.getServiceInterface().getSerializerByName(serializerName));

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            client.download(parseLong(roid), serializer.getOid(), outputStream);

            return newDataHandler(outputStream.toByteArray(), IFC_CONTENT_TYPE, roid + ".ifc");
        } catch (Exception e) {
            throw new BimException(e);
        }
    }

    @Override
    public Iterable<Entity> getEntitiesByType(String type, String revisionId) {
        try {
            requireConnection();
            SRevision revision = client.getServiceInterface().getRevision(new Long(revisionId));
            SProject project = client.getServiceInterface().getProjectByPoid(revision.getProjectId());
            IfcVersions ifcVersion = IfcVersions.valueOf(project.getSchema());

            EPackage einstance = ifcVersion.getPackage();
            String methodName = "get" + type;
            Method method = einstance.getClass().getDeclaredMethod(methodName);
            EClass response = (EClass) method.invoke(einstance, (Object[]) null);

            List<IdEObject> entitiesResponse = firstNotNull(getModelByRevId(revisionId).getAllWithSubTypes(response), emptyList());

            List<Entity> entities = list();
            entitiesResponse.forEach((object) -> {
                if (ifcVersion.equals(IfcVersions.ifc2x3tc1) && object instanceof org.bimserver.models.ifc2x3tc1.IfcRoot) {
                    Entity entity = new org.cmdbuild.bim.bimserverclient.BimserverEntity(org.bimserver.models.ifc2x3tc1.IfcRoot.class.cast(object));
                    entities.add(entity);
                } else if (ifcVersion.equals(IfcVersions.ifc4) && object instanceof org.bimserver.models.ifc4.IfcRoot) {
                    Entity entity = new org.cmdbuild.bim.bimserverclient.BimserverEntity(org.bimserver.models.ifc4.IfcRoot.class.cast(object));
                    entities.add(entity);
                }
            });
            return entities;
        } catch (Exception e) {
            throw new BimException(e);
        }
    }

    @Override
    public Entity getEntityByOid(String revisionId, String objectId) {
        requireConnection();
        IfcModelInterface model = getModelByRevId(revisionId);
        return new BimserverEntity(model.get(parseLong(objectId)));
    }

    @Override
    public String getLastRevisionOfProject(String projectId) {
        try {
            requireConnection();
            SProject project = client.getServiceInterface().getProjectByPoid(parseLong(projectId));
            return String.valueOf(project.getLastRevisionId());
        } catch (Exception e) {
            throw new BimException(e);
        }
    }

    @Override
    public BimserverProject getProjectByPoid(String projectId) {
        try {
            requireConnection();
            SProject project = client.getServiceInterface().getProjectByPoid(parseLong(projectId));
            return toBimserverProject(project);
        } catch (Exception e) {
            throw new BimException(e);
        }
    }

    @Override
    public Entity getReferencedEntity(ReferenceAttribute reference, String revisionId) {
        requireConnection();
        Entity entity;
        if (!StringUtils.isBlank(reference.getGlobalId())) {
            String guid = reference.getGlobalId();
            entity = getEntityByGuid(revisionId, guid, null);
        } else {
            Long oid = reference.getOid();
            entity = getEntityByOid(revisionId, String.valueOf(oid));
        }
        return entity;
    }

    @Override
    public BimserverProject updateProject(BimserverProject project) {
        try {
            requireConnection();
            Long poid = new Long(project.getProjectId());
            SProject currentProject = client.getServiceInterface().getProjectByPoid(poid);
            currentProject.setDescription(project.getDescription());
            client.getServiceInterface().updateProject(currentProject);
            return project;
        } catch (Exception e) {
            throw new BimException(e);
        }
    }

    private synchronized void connectSafeIfEnabled() {
        try {
            if (configuration.isEnabled()) {
                if (!isConnected()) {
                    logger.info("connecting to bim server");
                    clientFactory = new JsonBimServerClientFactory(configuration.getUrl().replaceFirst("/$", ""));
                    client = clientFactory.create(new UsernamePasswordAuthenticationInfo(configuration.getUsername(), configuration.getPassword()));
                    checkArgument(isConnected());
                    logger.info("Bimserver connection established");
                }
                status.set(MS_READY);
            } else {
                status.set(MS_DISABLED);
            }
        } catch (Exception e) {
            status.set(MS_ERROR);
            logger.error(marker(), "Bimserver connection failed (bimserver url = {})", configuration.getUrl(), e);
        }
    }

    private synchronized void disconnect() {
        if (client != null) {
            logger.info("close bim server connection");
            try {
                client.disconnect();
                client.close();
            } catch (Exception ex) {
                logger.warn("error closing bim server client", ex);
            }
            client = null;
        }
        if (clientFactory != null) {
            try {
                clientFactory.close();
            } catch (Exception ex) {
                logger.warn("error closing bim server client factory", ex);
            }
            clientFactory = null;
        }
        status.set(MS_NOTRUNNING);
    }

    private void requireConnection() {
        checkArgument(configuration.isEnabled(), "bimserver not enabled");
        connectSafeIfEnabled();
        checkArgument(isConnected(), "client not connected");
    }

    private BimserverProject toBimserverProject(SProject project) {
        return BimserverProjectImpl.builder()
                .withDescription(project.getDescription())
                .withName(project.getName())
                .withIfcFormat(project.getSchema())
                .withIsActive(equal(project.getState(), ACTIVE))
                .withProjectId(Long.toString(project.getOid()))
                .build();
    }

    private boolean isConnected() {
        if (client == null) {
            return false;
        } else {
            try {
                return client.getBimServerAuthInterface().isLoggedIn();
            } catch (Exception t) {
                status.set(MS_ERROR);
                logger.error("Unable to check login state", t);
                return false;
            }
        }
    }

    private Entity getEntityByGuid(String revisionId, String guid, Iterable<String> candidateTypes) {
        IfcModelInterface model = getModelByRevId(revisionId);
        return new BimserverEntity(model.getByGuid(guid));
    }

    private BimRevision getRevision(String identifier) {
        Long roid = new Long(identifier);
        BimRevision revision = BimRevision.NULL_REVISION;
        try {
            if (roid != -1) {
                requireConnection();
                revision = new BimserverRevision(client.getServiceInterface().getRevision(roid));
            }
            return revision;
        } catch (Exception e) {
            throw new BimException(e);
        }
    }

    private IfcModelInterface getModelByRevId(String revisionId) {
        return models.get(revisionId, () -> doGetModelByRevId(revisionId));
    }

    private IfcModelInterface doGetModelByRevId(String revisionId) {
        requireConnection();
        String projectId = getRevision(revisionId).getProjectId();
        try {
            SProject project = client.getServiceInterface().getProjectByPoid(new Long(projectId));
            ClientIfcModel model = client.getModel(project, new Long(revisionId), true, false);
            return model;
        } catch (Exception e) {
            throw new BimException(e, "error loading model with rev id = %s", revisionId);
        }
    }

    private enum IfcVersions {

        ifc2x3tc1 {
            @Override
            public String getDeserializerName() {
                return "Ifc2x3tc1 (Streaming)";
            }

            @Override
            public String getSerializerName() {
                return "Ifc2x3tc1";
            }

            @Override
            public EPackage getPackage() {
                return org.bimserver.models.ifc2x3tc1.Ifc2x3tc1Package.eINSTANCE;
            }

        },
        ifc4 {
            @Override
            public String getDeserializerName() {
                return "Ifc4 (Streaming)";
            }

            @Override
            public String getSerializerName() {
                return "Ifc4";
            }

            @Override
            public EPackage getPackage() {
                return org.bimserver.models.ifc4.Ifc4Package.eINSTANCE;
            }

        };

        public abstract String getDeserializerName();

        public abstract String getSerializerName();

        public abstract EPackage getPackage();
    }
}
