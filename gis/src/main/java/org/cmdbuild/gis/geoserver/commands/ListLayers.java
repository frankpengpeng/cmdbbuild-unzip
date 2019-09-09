package org.cmdbuild.gis.geoserver.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.cmdbuild.config.GisConfiguration;
import org.cmdbuild.utils.Command;
import org.dom4j.Node;
import org.cmdbuild.gis.geoserver.GeoserverLayerInfo;

public class ListLayers extends AbstractGeoCommand implements Command<List<GeoserverLayerInfo>> {

	/**
	 * if there is a store name, the command return only the layers of that
	 * store
	 */
	private final String storeName;

	private ListLayers(GisConfiguration configuration) {
		this(configuration, null);
	}

	private ListLayers(GisConfiguration configuration, String storeName) {
		super(configuration);
		this.storeName = storeName;
	}

	@Override
	public List<GeoserverLayerInfo> run() {
		List<GeoserverLayerInfo> layers = new ArrayList<>();

		String url = String.format("%s/rest/layers", getGeoServerURL());

		List<?> layerList = get(url).selectNodes("//layers/layer");
		for (Iterator<?> iter = layerList.iterator(); iter.hasNext();) {
			String layerName = ((Node) iter.next()).valueOf("name");
			GeoserverLayerInfo layerInfo = GetLayer.exec(configuration, layerName);
			if (this.storeName != null && this.storeName.equals(layerInfo.getStoreName())) {
				layers.add(layerInfo);
			}
		}

		return layers;
	}

	public static List<GeoserverLayerInfo> exec(GisConfiguration configuration) {
		return new ListLayers(configuration).run();
	}

	public static List<GeoserverLayerInfo> exec(GisConfiguration configuration, String storeName) {
		return new ListLayers(configuration, storeName).run();
	}

}
