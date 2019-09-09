package org.cmdbuild.extcomponents.contextmenu;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import static org.cmdbuild.extcomponents.contextmenu.ContextMenuComponentData.CONTEXT_MENU_COMPONENT_TABLE_NAME;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@CardMapping(CONTEXT_MENU_COMPONENT_TABLE_NAME)
public class ContextMenuComponentDataImpl implements ContextMenuComponentData {

    private final Long id;
    private final String name, description;
    private final byte[] data;
    private final boolean isActive;

    private ContextMenuComponentDataImpl(ContextMenuComponentDataImplBuilder builder) {
        this.id = (builder.id);
        this.name = checkNotBlank(builder.name);
        this.description = nullToEmpty(builder.description);
        this.data = checkNotNull(builder.data);
        this.isActive = firstNotNull(builder.isActive, true);
    }

    @Override
    @CardAttr
    public Long getId() {
        return id;
    }

    @Override
    @CardAttr("Active")
    public boolean getActive() {
        return isActive;
    }

    @Override
    @CardAttr(ATTR_CODE)
    public String getName() {
        return name;
    }

    @Override
    @CardAttr
    public String getDescription() {
        return description;
    }

    @Override
    @CardAttr
    public byte[] getData() {
        return data;
    }

    @Override
    public String toString() {
        return "ContextMenuComponentData{" + "id=" + id + ", name=" + name + '}';
    }

    public static ContextMenuComponentDataImplBuilder builder() {
        return new ContextMenuComponentDataImplBuilder();
    }

    public static ContextMenuComponentDataImplBuilder copyOf(ContextMenuComponentData source) {
        return new ContextMenuComponentDataImplBuilder()
                .withId(source.getId())
                .withName(source.getName())
                .withDescription(source.getDescription())
                .withActive(source.getActive())
                .withData(source.getData());
    }

    public static class ContextMenuComponentDataImplBuilder implements Builder<ContextMenuComponentDataImpl, ContextMenuComponentDataImplBuilder> {

        private Long id;
        private String name;
        private String description;
        private byte[] data;
        private Boolean isActive;

        public ContextMenuComponentDataImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public ContextMenuComponentDataImplBuilder withActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public ContextMenuComponentDataImplBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public ContextMenuComponentDataImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public ContextMenuComponentDataImplBuilder withData(byte[] data) {
            this.data = data;
            return this;
        }

        @Override
        public ContextMenuComponentDataImpl build() {
            return new ContextMenuComponentDataImpl(this);
        }

    }
}
