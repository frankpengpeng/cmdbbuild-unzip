/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.gis;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import java.util.Collection;
import java.util.Set;
import javax.annotation.Nullable;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import static org.cmdbuild.gis.GisConst.ATTR_OWNER_CARD;
import static org.cmdbuild.gis.GisConst.ATTR_OWNER_CLASS;
import static org.cmdbuild.gis.GisConst.GIS_LAYER_TABLE_NAME;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@CardMapping(GIS_LAYER_TABLE_NAME)
public class GeoserverLayerImpl implements GeoserverLayer {

    private final Long id;
    private final String ownerClassId, layerName, description, geoserverName, type;
    private final int index, minimumZoom, defaultZoom, maximumZoom;
    private final boolean active;
    private final Set<String> visibility;
    private final long ownerCardId;

    private GeoserverLayerImpl(GeoserverLayerImplBuilder builder) {
        this.id = builder.id;
        this.layerName = checkNotBlank(builder.layerName);
        this.active = builder.active;
        this.description = nullToEmpty(builder.description);
        this.geoserverName = checkNotBlank(builder.geoserverName);
        this.type = checkNotBlank(builder.type);
        this.index = checkNotNull(builder.index);
        this.minimumZoom = checkNotNull(builder.minimumZoom);
        this.defaultZoom = checkNotNull(builder.defaultZoom, "def zoom cannot be null");
        this.maximumZoom = checkNotNull(builder.maximumZoom, "max zoom cannot be null");
        this.visibility = set(checkNotNull(builder.visibility, "visibility cannot be null")).immutable();
        this.ownerClassId = builder.ownerClassId;
        this.ownerCardId = builder.ownerCardId;
    }

    @Nullable
    @CardAttr(ATTR_ID)
    @Override
    public Long getId() {
        return id;
    }

    @Override
    @CardAttr(ATTR_OWNER_CLASS)
    public String getOwnerClassId() {
        return ownerClassId;
    }

    @Override
    @CardAttr(ATTR_OWNER_CARD)
    public long getOwnerCardId() {
        return ownerCardId;
    }

    @Override
    @CardAttr(ATTR_CODE)
    public String getLayerName() {
        return layerName;
    }
    
    @Override
    @CardAttr("Active")
    public boolean getActive() {
        return active;
    }

    @Override
    @CardAttr
    public String getType() {
        return type;
    }

    @Override
    @CardAttr
    public String getDescription() {
        return description;
    }

    @Override
    @CardAttr
    public String getGeoserverName() {
        return geoserverName;
    }

    @Override
    @CardAttr
    public int getIndex() {
        return index;
    }

    @Override
    @CardAttr
    public int getMinimumZoom() {
        return minimumZoom;
    }

    @Override
    @CardAttr
    public int getDefaultZoom() {
        return defaultZoom;
    }

    @Override
    @CardAttr
    public int getMaximumZoom() {
        return maximumZoom;
    }

    @Override
    @CardAttr
    public Set<String> getVisibility() {
        return visibility;
    }

    public static GeoserverLayerImplBuilder builder() {
        return new GeoserverLayerImplBuilder();
    }

    public static GeoserverLayerImplBuilder copyOf(GeoserverLayer source) {
        return new GeoserverLayerImplBuilder()
                .withLayerName(source.getLayerName())
                .withId(source.getId())
                .withActive(source.getActive())
                .withDescription(source.getDescription())
                .withType(source.getType())
                .withGeoserverName(source.getGeoserverName())
                .withIndex(source.getIndex())
                .withMinimumZoom(source.getMinimumZoom())
                .withDefaultZoom(source.getDefaultZoom())
                .withMaximumZoom(source.getMaximumZoom())
                .withVisibility(source.getVisibility())
                .withOwnerClassId(source.getOwnerClassId())
                .withOwnerCardId(source.getOwnerCardId());
    }

    public static class GeoserverLayerImplBuilder implements Builder<GeoserverLayerImpl, GeoserverLayerImplBuilder> {

        private String layerName;
        private String description;
        private boolean active;
        private String ownerClassId, geoserverName;
        private String type;
        private Integer maximumZoom, minimumZoom, defaultZoom, index;
        private Collection<String> visibility;
        private Long ownerCardId, id;

        public GeoserverLayerImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }
        
        public GeoserverLayerImplBuilder withActive(boolean active) {
            this.active = active;
            return this;
        }

        public GeoserverLayerImplBuilder withType(String type) {
            this.type = type;
            return this;
        }

        public GeoserverLayerImplBuilder withLayerName(String layerName) {
            this.layerName = layerName;
            return this;
        }

        public GeoserverLayerImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public GeoserverLayerImplBuilder withGeoserverName(String geoserverName) {
            this.geoserverName = geoserverName;
            return this;
        }

        public GeoserverLayerImplBuilder withIndex(Integer index) {
            this.index = index;
            return this;
        }

        public GeoserverLayerImplBuilder withMinimumZoom(Integer minimumZoom) {
            this.minimumZoom = minimumZoom;
            return this;
        }

        public GeoserverLayerImplBuilder withDefaultZoom(Integer defaultZoom) {
            this.defaultZoom = defaultZoom;
            return this;
        }

        public GeoserverLayerImplBuilder withMaximumZoom(Integer maximumZoom) {
            this.maximumZoom = maximumZoom;
            return this;
        }

        public GeoserverLayerImplBuilder withOwnerClassId(String ownerClassId) {
            this.ownerClassId = ownerClassId;
            return this;
        }

        public GeoserverLayerImplBuilder withOwnerCardId(Long ownerCardId) {
            this.ownerCardId = ownerCardId;
            return this;
        }

        public GeoserverLayerImplBuilder withVisibility(Collection<String> visibility) {
            this.visibility = visibility;
            return this;
        }

        @Override
        public GeoserverLayerImpl build() {
            return new GeoserverLayerImpl(this);
        }

    }
}
