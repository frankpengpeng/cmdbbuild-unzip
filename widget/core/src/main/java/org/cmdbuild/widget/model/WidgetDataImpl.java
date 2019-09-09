/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.widget.model;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class WidgetDataImpl implements WidgetData {

    private final String type, label, id;
    private final boolean isActive;
    private final Map<String, Object> data;

    private WidgetDataImpl(WidgetDataImplBuilder builder) {
        this.type = checkNotBlank(builder.type, "widget type is null");
        this.label = checkNotBlank(builder.label, "widget label is null");
        this.id = checkNotBlank(builder.id, "widget id is null");
        this.isActive = builder.isActive;
        this.data = (Map) map(checkNotNull(builder.data)).immutable();
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public Map<String, Object> getData() {
        return data;
    }

    public static WidgetDataImplBuilder builder() {
        return new WidgetDataImplBuilder();
    }

    public static WidgetDataImplBuilder copyOf(WidgetInfo source) {
        return new WidgetDataImplBuilder()
                .withType(source.getType())
                .withLabel(source.getLabel())
                .withId(source.getId())
                .withIsActive(source.isActive());
    }

    public static WidgetDataImplBuilder copyOf(WidgetData source) {
        return copyOf((WidgetInfo) source)
                .withData(source.getData());
    }

    public static class WidgetDataImplBuilder implements Builder<WidgetDataImpl, WidgetDataImplBuilder> {

        private String type;
        private String label;
        private String id;
        private boolean isActive;
        private Map<String, ?> data;

        public WidgetDataImplBuilder withType(String type) {
            this.type = type;
            return this;
        }

        public WidgetDataImplBuilder withLabel(String label) {
            this.label = label;
            return this;
        }

        public WidgetDataImplBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public WidgetDataImplBuilder withIsActive(boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public WidgetDataImplBuilder withData(Map<String, ?> data) {
            this.data = data;
            return this;
        }

        @Override
        public WidgetDataImpl build() {
            return new WidgetDataImpl(this);
        }

    }
}
