package org.cmdbuild.gis.geoserver.commands;

import org.cmdbuild.config.GisConfiguration;
import org.cmdbuild.utils.Command;
import org.cmdbuild.gis.geoserver.GeoserverLayerInfo;

public class DeleteLayer extends AbstractGeoCommand implements Command<Void> {

	private final GeoserverLayerInfo layer;

	public static Void exec(GisConfiguration configuration, GeoserverLayerInfo layer) {
		return new DeleteLayer(configuration, layer).run();
	}

	private DeleteLayer(GisConfiguration configuration, GeoserverLayerInfo layer) {
		super(configuration);
		this.layer = layer;
	}

	@Override
	public Void run() {
		String url = String.format("%s/rest/layers/%s", getGeoServerURL(), layer.getLayerName());
		delete(url);
		return null;
	}
}
