package org.cmdbuild.gis;

import java.util.List;
import javax.annotation.Nullable;

public interface GisAttributeRepository {

	GisAttribute createLayer(GisAttribute layer);

	GisAttribute getLayer(String classId, String name);

	GisAttribute updateLayer(GisAttribute changes);

	void deleteLayer(String classId, String name);

	List<GisAttribute> getAllLayers();

	List<GisAttribute> getLayersByOwnerClass(String classId);

	List<GisAttribute> getVisibleLayersForClass(String classId);

	List<GisAttribute> getLayersByOwnerClassAndLayerName(String classId, @Nullable Iterable<String> layerNames);

	GisAttribute getLayer(long attrId);

}
