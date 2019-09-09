/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.gis;

import java.util.Set;
import javax.annotation.Nullable;

public interface GisLayer {

	@Nullable
	Long getId();

	String getLayerName();

	int getIndex();

	int getMinimumZoom();

	int getDefaultZoom();

	int getMaximumZoom();

	Set<String> getVisibility();

	default boolean isVisible(String tableName) {
		return getVisibility().contains(tableName);
	}
}
