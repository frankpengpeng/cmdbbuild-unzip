package org.cmdbuild.gis.geoserver.commands;

import java.io.InputStream;
import java.util.List;

import org.cmdbuild.config.GisConfiguration;
import org.cmdbuild.gis.geoserver.GeoServerStore;
import org.cmdbuild.gis.geoserver.GeoServerStore.StoreDataType;
import org.cmdbuild.utils.Command;
import org.cmdbuild.gis.geoserver.GeoserverLayerInfo;

public class CreateModifyDataStore extends AbstractGeoCommand implements Command<String> {

	private final GeoServerStore store;
	private final InputStream data;

	public static String exec(final GisConfiguration configuration, final GeoServerStore store, final InputStream data) {
		return new CreateModifyDataStore(configuration, store, data).run();
	}

	private CreateModifyDataStore(final GisConfiguration configuration, final GeoServerStore store,
			final InputStream data) {
		super(configuration);
		this.store = store;
		this.data = data;
	}

	@Override
	public String run() {
		final StoreDataType type = store.getDataType();
		final String url = String.format("%s/rest/workspaces/%s/%ss/%s/file.%s", getGeoServerURL(),
				getGeoServerWorkspace(), type.getStoreTypeName().toLowerCase(), store.getName(),
				type.getUploadFileExtension());
		put(data, url, type.getMime());
		final List<GeoserverLayerInfo> storeLayers = ListLayers.exec(configuration, store.getName());

		if (storeLayers.size() > 0) {
			final GeoserverLayerInfo l = storeLayers.get(0);
			return l.getLayerName();
		}

		return null;
	}
}
