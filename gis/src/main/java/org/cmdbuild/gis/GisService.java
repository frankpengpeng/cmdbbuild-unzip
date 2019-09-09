package org.cmdbuild.gis;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.MoreCollectors.toOptional;
import static java.lang.Long.parseLong;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.activation.DataHandler;
import javax.annotation.Nullable;

import static org.apache.commons.lang3.math.NumberUtils.isNumber;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.data.filter.CmdbFilter;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.navtree.NavTreeNode;

public interface GisService {

    boolean isGisEnabled();

    GisAttribute createGeoAttribute(GisAttribute layerMetaData);

    void deleteGeoAttribute(String masterTableName, String attributeName);

    Pair<List<GisValue>, List<GisNavTreeNode>> getGeoValuesAndNavTree(Collection<Long> attrs, String bbox, CmdbFilter filter);

    GeoserverLayer createGeoServerLayer(GeoserverLayer layerMetaData, DataHandler file);

    GeoserverLayer updateGeoserverLayer(GeoserverLayer geoserverLayer);

    void deleteGeoServerLayer(long id);

    List<GeoserverLayer> getGeoServerLayers();

    List<GeoserverLayer> getGeoServerLayersForCard(String classId, Long cardId);

    List<GeoserverLayer> updateGeoserverLayersOrder(List<Long> layerIdsInOrder);

    List<GisAttribute> getAllLayers();

    void removeGisTreeNavigation();

    NavTreeNode getGisTreeNavigation();

    GisAttribute getLayerByClassAndName(String classId, String attributeId);

    GisAttribute getLayerById(long attributeId);

    GisAttribute updateGeoAttribute(GisAttribute layer);

    List<GisAttribute> getGeoAttributeByOwnerClass(String classId);

    List<GisAttribute> getGeoAttributesVisibleFromClass(String classId);

    List<GeoserverLayer> getGeoLayersVisibleFromClass(String classId);

    List<GeoserverLayer> getGeoLayersOwnedByClass(String classId);

    List<GisValue> getValues(String classId, Long cardId);

    List<GisValue> getGeoValues(Collection<Long> attrs, String bbox, CmdbFilter filter);

    @Deprecated
    void updateValues(String classId, Long cardId, Map<String, Object> attributes);

    GisValue setValue(GisValue value);

    void deleteValue(String classId, long cardId, String attrId);

    void updateGeoAttributesVisibilityForClass(String classId, Collection<Long> geoAttributes);

    List<GisAttribute> updateGeoAttributesOrder(List<Long> attrIdsInOrder);

    @Nullable
    Area getAreaForValues(Collection<Long> attrs, CmdbFilter filter);

    default GisAttribute getLayerByClassAndNameOrId(String classId, String attributeNameOrLayerId) {
        if (isNumber(attributeNameOrLayerId)) {
            GisAttribute gisAttribute = getLayerById(parseLong(attributeNameOrLayerId));
            checkArgument(equal(gisAttribute.getOwnerClassName(), classId));
            return gisAttribute;
        } else {
            return getLayerByClassAndName(classId, attributeNameOrLayerId);
        }
    }

    @Nullable
    default GisValue getValueOrNull(String classId, Long cardId, String attrId) {
        checkNotBlank(attrId);
        return getValues(classId, cardId).stream().filter((v) -> equal(v.getLayerName(), attrId)).collect(toOptional()).orElse(null);
    }

    default GisValue getValue(String classId, Long cardId, String attrId) {
        return checkNotNull(getValueOrNull(classId, cardId, attrId), "geo value not found for classId = %s cardId = %s attrId = %s", classId, cardId, attrId);
    }

}
