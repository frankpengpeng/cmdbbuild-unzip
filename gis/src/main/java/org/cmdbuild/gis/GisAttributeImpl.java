package org.cmdbuild.gis;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collection;
import javax.annotation.Nullable;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import static org.cmdbuild.gis.GisConst.ATTR_OWNER;
import static org.cmdbuild.gis.GisConst.GIS_ATTRIBUTE_TABLE_NAME;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@CardMapping(GIS_ATTRIBUTE_TABLE_NAME)
public class GisAttributeImpl implements GisAttribute {

    private final String layerName, description, mapStyle, type;
    private final int index, minimumZoom, defaultZoom, maximumzoom;
    private final String baseClassId;
    private final boolean active;
    private final Long id;
    private final Set<String> visibility;

    private GisAttributeImpl(GisAttributeImplBuilder builder) {
        this.layerName = checkNotNull(builder.layerName, "layer name cannot be null");
        this.description = checkNotNull(builder.description, "description cannot be null");
        this.active = builder.active;
        this.mapStyle = checkNotNull(builder.mapStyle, "map style cannot be null");
        this.type = checkNotBlank(builder.type, "attr type cannot be null");
        this.index = checkNotNull(builder.index, "index cannot be null");
        this.minimumZoom = checkNotNull(builder.minimumZoom, "min zoom cannot be null");
        this.defaultZoom = checkNotNull(builder.defaultZoom, "def zoom cannot be null");
        this.maximumzoom = checkNotNull(builder.maximumZoom, "max zoom cannot be null");
        this.visibility = set(checkNotNull(builder.visibility, "visibility cannot be null")).immutable();
        this.baseClassId = checkNotBlank(builder.baseClassId, "base class id cannot be null");
        this.id = builder.id;
    }

    @Override
    @CardAttr(ATTR_ID)
    @Nullable
    public Long getId() {
        return id;
    }

    @Override
    @CardAttr(ATTR_CODE)
    public String getLayerName() {
        return layerName;
    }

    @Override
    @CardAttr(ATTR_OWNER)
    public String getOwnerClassName() {
        return baseClassId;
    }

    @Override
    @CardAttr("Active")
    public boolean getActive() {
        return active;
    }

    @Override
    @CardAttr
    public String getDescription() {
        return description;
    }

    @Override
    @CardAttr
    public String getMapStyle() {
        return mapStyle;
    }

    @Override
    @CardAttr
    public String getType() {
        return type;
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
        return maximumzoom;
    }

    @Override
    @CardAttr
    public Set<String> getVisibility() {
        return visibility;
    }

    @Override
    public String toString() {
        return "GisAttribute{" + "layerName=" + layerName + ", baseClassId=" + baseClassId + '}';
    }

    public static GisAttributeImplBuilder builder() {
        return new GisAttributeImplBuilder();
    }

    public static GisAttributeImplBuilder copyOf(GisAttribute source) {
        return new GisAttributeImplBuilder()
                .withId(source.getId())
                .withOwnerClassName(source.getOwnerClassName())
                .withActive(source.getActive())
                .withLayerName(source.getLayerName())
                .withDescription(source.getDescription())
                .withMapStyle(source.getMapStyle())
                .withType(source.getType())
                .withIndex(source.getIndex())
                .withMinimumZoom(source.getMinimumZoom())
                .withDefaultZoom(source.getDefaultZoom())
                .withMaximumZoom(source.getMaximumZoom())
                .withVisibility(source.getVisibility());
    }

    public static class GisAttributeImplBuilder implements Builder<GisAttributeImpl, GisAttributeImplBuilder> {

        private String layerName;
        private Long id;
        private boolean active;
        private String baseClassId, description;
        private String mapStyle;
        private String type;
        private Integer index;
        private Integer minimumZoom, defaultZoom;
        private Integer maximumZoom;
        private Collection<String> visibility;

        public GisAttributeImplBuilder withLayerName(String name) {
            this.layerName = name;
            return this;
        }

        public GisAttributeImplBuilder withOwnerClassName(String classId) {
            this.baseClassId = classId;
            return this;
        }

        public GisAttributeImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public GisAttributeImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public GisAttributeImplBuilder withActive(boolean active) {
            this.active = active;
            return this;
        }

        public GisAttributeImplBuilder withMapStyle(String mapStyle) {
            this.mapStyle = mapStyle;
            return this;
        }

        public GisAttributeImplBuilder withType(String type) {
            this.type = type;
            return this;
        }

        public GisAttributeImplBuilder withIndex(Integer index) {
            this.index = index;
            return this;
        }

        public GisAttributeImplBuilder withMinimumZoom(Integer minimumZoom) {
            this.minimumZoom = minimumZoom;
            return this;
        }

        public GisAttributeImplBuilder withDefaultZoom(Integer defaultZoom) {
            this.defaultZoom = defaultZoom;
            return this;
        }

        public GisAttributeImplBuilder withMaximumZoom(Integer maximumzoom) {
            this.maximumZoom = maximumzoom;
            return this;
        }

        public GisAttributeImplBuilder withVisibility(Collection<String> visibility) {
            this.visibility = visibility;
            return this;
        }

        @Override
        public GisAttributeImpl build() {
            return new GisAttributeImpl(this);
        }

    }
}
