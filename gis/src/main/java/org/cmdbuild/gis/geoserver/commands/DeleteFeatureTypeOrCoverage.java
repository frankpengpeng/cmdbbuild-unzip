package org.cmdbuild.gis.geoserver.commands;

import org.cmdbuild.config.GisConfiguration;
import org.cmdbuild.gis.geoserver.GeoServerStore;
import org.cmdbuild.utils.Command;
import org.cmdbuild.gis.geoserver.GeoserverLayerInfo;

public class DeleteFeatureTypeOrCoverage extends AbstractGeoCommand implements Command<Void> {

	private final GeoserverLayerInfo layer;
	private final GeoServerStore store;

	public static Void exec(GisConfiguration configuration, GeoserverLayerInfo layer, GeoServerStore store) {
		return new DeleteFeatureTypeOrCoverage(configuration, layer, store).run();
	}

	private DeleteFeatureTypeOrCoverage(GisConfiguration configuration, GeoserverLayerInfo layer, GeoServerStore store) {
		super(configuration);
		this.layer = layer;
		this.store = store;
	}

	@Override
	public Void run() {
		String url = String.format("%s/rest/workspaces/%s/%ss/%s/%ss/%s", getGeoServerURL(),
				getGeoServerWorkspace(), store.getStoreType().toLowerCase(), store.getName(), store.getStoreSubtype()
				.toLowerCase(), layer.getLayerName());
		delete(url);
		return null;
	}
}
