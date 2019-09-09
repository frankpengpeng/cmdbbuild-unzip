package org.cmdbuild.gis.geoserver;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Pattern;
import static org.cmdbuild.common.error.ErrorAndWarningCollectorService.marker;

import org.cmdbuild.config.GisConfiguration;
import org.cmdbuild.exception.NotFoundException;
import org.cmdbuild.gis.GeoserverLayer;
import org.cmdbuild.gis.geoserver.GeoServerStore.StoreDataType;
import org.cmdbuild.gis.geoserver.commands.CreateModifyDataStore;
import org.cmdbuild.gis.geoserver.commands.DeleteFeatureTypeOrCoverage;
import org.cmdbuild.gis.geoserver.commands.DeleteLayer;
import org.cmdbuild.gis.geoserver.commands.DeleteStore;
import org.cmdbuild.gis.geoserver.commands.ListLayers;
import org.cmdbuild.gis.geoserver.commands.ListStores;
import org.cmdbuild.services.MinionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.services.MinionComponent;
import static org.cmdbuild.services.MinionStatus.MS_READY;
import static org.cmdbuild.services.MinionStatus.MS_DISABLED;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;

@Component
@MinionComponent(name = "GIS GeoServer client", config = "org.cmdbuild.gis.geoserver")//TODO config const
public class GeoserverServiceImpl implements GeoserverService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final GisConfiguration configuration;

    public GeoserverServiceImpl(GisConfiguration configuration) {
        this.configuration = checkNotNull(configuration);
    }

    public MinionStatus getServiceStatus() {
        if (!configuration.isGeoServerEnabled()) {
            return MS_DISABLED;
        } else {
            return MS_READY; //TODO check status
        }
    }

    @Override
    public List<GeoServerStore> getStores() {
        return ListStores.exec(configuration);
    }

    @Override
    public List<GeoserverLayerInfo> getLayers() {
        return ListLayers.exec(configuration);
    }

    @Override
    public String createStoreAndLayer(GeoserverLayer layerMetadata, InputStream data) {
        checkArgument(layerMetadata.getLayerName().matches("^\\S+$"), "invalid layer name =< %s >", layerMetadata.getLayerName());

        GeoServerStore s = new GeoServerStore(layerMetadata.getLayerName(), parseEnum(layerMetadata.getType(), StoreDataType.class));
        return checkNotNull(CreateModifyDataStore.exec(configuration, s, data), "Geoserver has not created the layer");
    }

    @Override
    public void modifyStoreData(GeoserverLayer layerMetadata, InputStream data) {
        GeoServerStore s = new GeoServerStore(layerMetadata.getLayerName(), parseEnum(layerMetadata.getType(), StoreDataType.class));
        CreateModifyDataStore.exec(configuration, s, data);
    }

    @Override
    public void deleteStoreAndLayers(GeoserverLayer layer) {
        GeoServerStore store = new GeoServerStore(layer.getLayerName(), parseEnum(layer.getType(), StoreDataType.class));

        try {
            // Delete the layer first because the store
            // must be empty to be deleted
            List<GeoserverLayerInfo> storeLayers = ListLayers.exec(configuration, store.getName());
            storeLayers.forEach(geoServerLayer -> {
                DeleteLayer.exec(configuration, geoServerLayer);
                DeleteFeatureTypeOrCoverage.exec(configuration, geoServerLayer, store);
            });
        } catch (NotFoundException e) {
            logger.warn(marker(), "GeoServer layer not found for name =< %s >", layer.getLayerName());
        }

        DeleteStore.exec(configuration, store);
    }

}
