package org.cmdbuild.gis.geoserver;

import java.io.InputStream;
import java.util.List;
import org.cmdbuild.gis.GeoserverLayer;

public interface GeoserverService {

	List<GeoServerStore> getStores();

	List<GeoserverLayerInfo> getLayers();

	String createStoreAndLayer(GeoserverLayer layerMetadata, InputStream data);

	void modifyStoreData(GeoserverLayer layerMetadata, InputStream data);

	void deleteStoreAndLayers(GeoserverLayer layer);
}
