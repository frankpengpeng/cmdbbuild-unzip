/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.gis.geoserver;

import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class GeoserverLayerInfoImpl implements GeoserverLayerInfo {

	private final String layerName, storeName;

	public GeoserverLayerInfoImpl(String layerName, String storeName) {
		this.layerName = checkNotBlank(layerName);
		this.storeName = checkNotBlank(storeName);
	}

	@Override
	public String getLayerName() {
		return layerName;
	}

	@Override
	public String getStoreName() {
		return storeName;
	}

}
