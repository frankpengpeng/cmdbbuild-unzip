package org.cmdbuild.gis;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.notNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.cmdbuild.config.GisConfiguration;
import org.json.JSONObject;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import javax.activation.DataHandler;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.apache.commons.lang3.tuple.Pair;
import static org.cmdbuild.common.error.ErrorAndWarningCollectorService.marker;
import org.cmdbuild.config.api.ConfigListener;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.services.PostStartup;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.gis.geoserver.GeoserverService;
import static org.cmdbuild.gis.utils.GisUtils.cmGeometryToPostgisSql;
import org.cmdbuild.services.MinionStatus;
import org.cmdbuild.services.MinionComponent;
import static org.cmdbuild.services.MinionStatus.MS_READY;
import static org.cmdbuild.services.MinionStatus.MS_ERROR;
import static org.cmdbuild.services.MinionStatus.MS_DISABLED;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import org.cmdbuild.navtree.NavTreeNode;
import org.cmdbuild.navtree.NavTreeService;
import static org.cmdbuild.utils.io.CmIoUtils.toDataSource;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@Component("gisService")
@MinionComponent(name = "GIS Service", configBean = GisConfiguration.class)
public class GisServiceImpl implements GisService {

    private static final String DOMAIN_TREE_TYPE = "gisnavigation";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final GisAttributeRepository gisAttributeRepository;
    private final GisValueRepository gisValueRepository;
    private final NavTreeService domainTreeStore;
    private final GisConfiguration configuration;
    private final GeoserverService geoServerService;
    private final GeoserverLayerRepository layerRepository;

    public GisServiceImpl(NavTreeService domainTreeRepository, GeoserverLayerRepository layerRepository, DaoService dao, GisValueRepository geoFeatureStore, GisConfiguration configuration, GeoserverService geoServerService, GisAttributeRepository layerStore) {
        this.dao = checkNotNull(dao);
        this.gisAttributeRepository = checkNotNull(layerStore);
        this.domainTreeStore = checkNotNull(domainTreeRepository);
        this.gisValueRepository = checkNotNull(geoFeatureStore);
        this.configuration = checkNotNull(configuration);
        this.geoServerService = checkNotNull(geoServerService);
        this.layerRepository = checkNotNull(layerRepository);
    }

    public MinionStatus getServiceStatus() {
        if (!isGisEnabled()) {
            return MS_DISABLED;
        } else {
            if (gisValueRepository.isGisSchemaOk()) {
                return MS_READY;
            } else {
                return MS_ERROR;
            }
        }
    }

    @PostStartup
    public void init() {
        checkGisConfiguration();
    }

    @Override
    public boolean isGisEnabled() {
        return configuration.isEnabled();
    }

    @ConfigListener(GisConfiguration.class)
    public void checkGisConfiguration() {
        if (isGisEnabled()) {
            logger.debug("checkGisSchema");
            try {
                gisValueRepository.checkGisSchemaAndCreateIfMissing();
            } catch (Exception ex) {
                logger.error(marker(), "error checking gis schema", ex);
            }
        }
    }

    @Override
    @Transactional
    public GisAttribute createGeoAttribute(GisAttribute layer) {
        checkGisEnabled();
        gisValueRepository.createGisTable(layer);
        return gisAttributeRepository.createLayer(layer);
    }

    @Override
    public GisAttribute updateGeoAttribute(GisAttribute layer) {
        checkGisEnabled();
        return gisAttributeRepository.updateLayer(layer);
    }

    @Override
    @Transactional
    public void deleteGeoAttribute(String masterTableName, String attributeName) {
        checkGisEnabled();
        checkNotBlank(masterTableName);
        checkNotBlank(attributeName);
        gisValueRepository.deleteGisTable(masterTableName, attributeName);
        gisAttributeRepository.deleteLayer(masterTableName, attributeName);
    }

    @Override
    public List<GisValue> getValues(String classId, Long cardId) {
        checkGisEnabled();
        Classe classe = dao.getClasse(classId);
        return gisAttributeRepository.getLayersByOwnerClass(classe.getName()).stream().map((l) -> gisValueRepository.getGisValueOrNull(l, cardId)).filter(notNull()).collect(toList());//TODO avoid n*m query
    }

    @Override
    public List<GisValue> getGeoValues(Collection<Long> layers, String bbox, CmdbFilter filter) {
        checkGisEnabled();
        List<GisValue> values = gisValueRepository.getGisValues(layers, bbox);
        values = filterGisValues(values, filter);
        return values;
    }

    @Override
    public Pair<List<GisValue>, List<GisNavTreeNode>> getGeoValuesAndNavTree(Collection<Long> attrs, String bbox, CmdbFilter filter) {
        checkGisEnabled();
        NavTreeNode navTreeDomains = getGisTreeNavigation();
        Pair<List<GisValue>, List<GisNavTreeNode>> res = gisValueRepository.getGeoValuesAndNavTree(attrs, bbox, navTreeDomains);
        res = Pair.of(filterGisValues(res.getLeft(), filter), filterNavTree(res.getRight(), filter));
        return res;
    }

    @Override
    @Nullable
    public Area getAreaForValues(Collection<Long> attrs, CmdbFilter filter) {
        checkGisEnabled();
        checkArgument(filter.isNoop(), "filter not supported yet");
        return gisValueRepository.getAreaForValues(attrs);
    }

    private List<GisValue> filterGisValues(List<GisValue> values, CmdbFilter filter) {
        if (filter.isNoop()) {
            return values;
        } else {
            return list(values).without(v -> dao.selectAll().from(v.getOwnerClassId()).where(ATTR_ID, EQ, v.getOwnerCardId()).where(filter).getCardOrNull() == null);//TODO improve performance
        }
    }

    private List<GisNavTreeNode> filterNavTree(List<GisNavTreeNode> values, CmdbFilter filter) {//TOTO check this
        if (filter.isNoop()) {
            return values;
        } else {
            return list(values).without(v -> dao.selectAll().from(v.getClassId()).where(ATTR_ID, EQ, v.getCardId()).where(filter).getCardOrNull() == null);//TODO improve performance
        }
    }

    @Override
    @Deprecated
    public void updateValues(String classId, Long cardId, Map<String, Object> attributes) {

        checkGisEnabled();

        String geoAttributesJsonString = (String) attributes.get("geoAttributes");
        if (geoAttributesJsonString != null) {
            try {
                JSONObject geoAttributesObject = new JSONObject(geoAttributesJsonString);
                String[] geoAttributesName = JSONObject.getNames(geoAttributesObject);
                Classe masterTable = dao.getClasse(classId);

                if (geoAttributesName != null) {
                    for (String name : geoAttributesName) {
                        GisAttribute layerMetaData = gisAttributeRepository.getLayer(masterTable.getName(), name);
                        String value = geoAttributesObject.getString(name);

                        if (isBlank(value)) {
                            gisValueRepository.deleteGisValue(layerMetaData, cardId);
                        } else {
                            gisValueRepository.setGisValue(layerMetaData, value, cardId);
                        }

//                        GisValue geoFeature = gisValueRepository.getGeoFeatureOrNull(layerMetaData, cardId);
//
//                        if (geoFeature == null) {
//                            // the feature does not exists
//                            // create it
//                            if (value != null && !value.trim().isEmpty()) {
//                                gisValueRepository.setGeoValue(layerMetaData, value, cardId);
//                            }
//                        } else {
//                            if (value != null && !value.trim().isEmpty()) {
//                                // there is a non empty value
//                                // update the geometry
//                                gisValueRepository.setGeoValue(layerMetaData, value, cardId);
//                            } else {
//                                // the new value is blank, so delete the feature
//                                gisValueRepository.deleteGeoValue(layerMetaData, cardId);
//                            }
//                        }
                    }
                }
            } catch (JSONException ex) {
                throw new GisException(ex);
            }
        }
    }

    @Override
    public GisValue setValue(GisValue value) {
        GisAttribute attribute = gisAttributeRepository.getLayer(value.getOwnerClassId(), value.getLayerName());
        String rawGeometryValue = cmGeometryToPostgisSql(value.getGeometry());
//        boolean hasValue = getValueOrNull(value.getOwnerClassId(), value.getOwnerCardId(), value.getLayerName()) != null;
//        if (hasValue) {
        gisValueRepository.setGisValue(attribute, rawGeometryValue, value.getOwnerCardId());
//        } else {
//            gisValueRepository.setGeoValue(attribute, rawGeometryValue, value.getOwnerCardId());
//        }
        return getValue(value.getOwnerClassId(), value.getOwnerCardId(), value.getLayerName());
    }

    @Override
    public void deleteValue(String classId, long cardId, String attrId) {
        GisAttribute attribute = gisAttributeRepository.getLayer(classId, attrId);
        gisValueRepository.deleteGisValue(attribute, cardId);
    }

    @Override
    @Transactional
    public void updateGeoAttributesVisibilityForClass(String classId, Collection<Long> newVis) {
        Classe classe = dao.getClasse(classId);
        newVis = set(checkNotNull(newVis));
        Set<Long> currentVis = getGeoAttributesVisibleFromClass(classId).stream().map(GisAttribute::getId).collect(toSet());
        Set<Long> toAdd = set(newVis).without(currentVis),
                toRemove = set(currentVis).without(newVis);
        toAdd.forEach((id) -> {
            GisAttribute attr = gisAttributeRepository.getLayer(id);
            attr = GisAttributeImpl.copyOf(attr).withVisibility(set(attr.getVisibility()).with(classe.getName())).build();
            gisAttributeRepository.updateLayer(attr);
        });
        toRemove.forEach((id) -> {
            GisAttribute attr = gisAttributeRepository.getLayer(id);
            attr = GisAttributeImpl.copyOf(attr).withVisibility(set(attr.getVisibility()).without(classe.getName())).build();
            gisAttributeRepository.updateLayer(attr);
        });
    }

    @Override
    public List<GisAttribute> updateGeoAttributesOrder(List<Long> attrIdsInOrder) {
        checkArgument(set(attrIdsInOrder).size() == attrIdsInOrder.size(), "invalid attr id list: list contains duplicates");
        AtomicInteger index = new AtomicInteger(0);
        return attrIdsInOrder.stream().map(gisAttributeRepository::getLayer).map(l -> GisAttributeImpl.copyOf(l).withIndex(index.getAndIncrement()).build()).map(gisAttributeRepository::updateLayer).collect(toList());
    }

    @Override
    public GeoserverLayer createGeoServerLayer(GeoserverLayer layer, DataHandler file) {
        try {
            checkGisEnabled();
            checkGeoServerIsEnabled();

            String geoServerLayerName = geoServerService.createStoreAndLayer(layer, toDataSource(file).getInputStream());

//			String fullName = String.format(GEO_TABLE_NAME_FORMAT, GEOSERVER, layer.getLayerName());
//			layerRepository
            layer = GeoserverLayerImpl.copyOf(layer)
                    //					.withTableName(fullName)
                    .withGeoserverName(geoServerLayerName)
                    .build();

            return layerRepository.create(layer);
        } catch (IOException ex) {
            throw new GisException(ex);
        }
    }

    @Override
    public GeoserverLayer updateGeoserverLayer(GeoserverLayer geoserverLayer) {
        checkGisEnabled();
        checkGeoServerIsEnabled();
        GeoserverLayer current = layerRepository.get(geoserverLayer.getId());
        geoserverLayer = GeoserverLayerImpl.copyOf(geoserverLayer)
                .withId(current.getId())
                .withLayerName(current.getLayerName())
                .build();
        return layerRepository.update(geoserverLayer);
    }

//	@Override
//	@Transactional
//	public void modifyGeoServerLayer(Layer layer, FileItem file) {
////		String name, String description, int maximumZoom, int minimumZoom Set<String> cardBinding
//
//		ensureGisIsEnabled();
//		ensureGeoServerIsEnabled();
//
//		Layer layerMetadata = layerMetadataStore.modifyLayerMetadata(GEOSERVER, name, description, minimumZoom, maximumZoom, null, cardBinding);
//
//		if (file != null && file.getSize() > 0) {
//			try {
//				geoServerService.modifyStoreData(layerMetadata, file.getInputStream());
//			} catch (IOException ex) {
//				throw new GisException(ex);
//			}
//		}
//	}
    @Override
    @Transactional
    public void deleteGeoServerLayer(long id) {
        checkGisEnabled();
        checkGeoServerIsEnabled();

//		String fullName = String.format(GEO_TABLE_NAME_FORMAT, GEOSERVER, name);
//		GisAttribute layer = gisAttributeRepository.getLayer(fullName);
        GeoserverLayer layer = layerRepository.get(id);
        geoServerService.deleteStoreAndLayers(layer);
        layerRepository.delete(id);
    }

    @Override
    @Transactional
    public List<GeoserverLayer> getGeoServerLayers() {
        checkGisEnabled();
        return layerRepository.getAll();
//		return gisAttributeRepository.getLayersByOwnerClass(GEOSERVER);
    }

    @Override
    public List<GeoserverLayer> getGeoServerLayersForCard(String classId, Long cardId) {
        checkGisEnabled();
        return layerRepository.getForCard(dao.getClasse(classId), cardId);
    }

    @Override
    public List<GeoserverLayer> updateGeoserverLayersOrder(List<Long> layerIdsInOrder) {
        AtomicInteger index = new AtomicInteger(0);
        checkArgument(set(layerIdsInOrder).size() == layerIdsInOrder.size(), "invalid layer id order list: duplicate id found in list");
        return layerIdsInOrder.stream().map(layerRepository::get).map(l -> GeoserverLayerImpl.copyOf(l).withIndex(index.getAndIncrement()).build()).map(layerRepository::update).collect(toList());
    }

    @Override
    public List<GeoserverLayer> getGeoLayersVisibleFromClass(String classId) {
        checkGisEnabled();
        return layerRepository.getVisibleFromClass(dao.getClasse(classId));
    }

    @Override
    public List<GeoserverLayer> getGeoLayersOwnedByClass(String classId) {
        checkGisEnabled();
        return layerRepository.getOwnedByClass(dao.getClasse(classId));
    }

    @Override
    public List<GisAttribute> getAllLayers() {
        checkGisEnabled();
        return gisAttributeRepository.getAllLayers();
    }

    @Override
    public GisAttribute getLayerByClassAndName(String classId, String attributeId) {
        checkGisEnabled();
        Classe classe = dao.getClasse(classId);
        return gisAttributeRepository.getLayer(classe.getName(), attributeId);
    }

    @Override
    public GisAttribute getLayerById(long attributeId) {
        checkGisEnabled();
        return gisAttributeRepository.getLayer(attributeId);
    }

    @Override
    public List<GisAttribute> getGeoAttributeByOwnerClass(String classId) {
        checkGisEnabled();
        Classe classe = dao.getClasse(classId);
        return gisAttributeRepository.getLayersByOwnerClass(classe.getName());
    }

    @Override
    public List<GisAttribute> getGeoAttributesVisibleFromClass(String classId) {
        checkGisEnabled();
        return gisAttributeRepository.getVisibleLayersForClass(classId);
    }

    @Override
    public void removeGisTreeNavigation() {
        domainTreeStore.removeTree(DOMAIN_TREE_TYPE);
    }

    @Override
    public NavTreeNode getGisTreeNavigation() {
        return domainTreeStore.getTree(DOMAIN_TREE_TYPE).getData();
    }

    private void checkGisEnabled() {
        if (!isGisEnabled()) {
            throw new GisException("GIS Module is non enabled");
        }
    }

    private void checkGeoServerIsEnabled() {
        if (!configuration.isGeoServerEnabled()) {
            throw new GisException("GEOServer is non enabled");
        }
    }
}
