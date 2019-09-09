package org.cmdbuild.extcomponents.custompage;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.extcomponents.custompage.CustomPageData.CUSTOM_PAGE_TABLE_NAME;

@CardMapping(CUSTOM_PAGE_TABLE_NAME)
public class CustomPageDataImpl implements CustomPageData {

    private final Long id;
    private final String name, description;
    private final byte[] data;
    private final boolean isActive;

    private CustomPageDataImpl(CustomPageImplBuilder builder) {
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
        return "CustomPageImpl{" + "id=" + id + ", name=" + name + '}';
    }

    public static CustomPageImplBuilder builder() {
        return new CustomPageImplBuilder();
    }

    public static CustomPageImplBuilder copyOf(CustomPageData source) {
        return new CustomPageImplBuilder()
                .withId(source.getId())
                .withName(source.getName())
                .withDescription(source.getDescription())
                .withActive(source.getActive())
                .withData(source.getData());
    }

    public static class CustomPageImplBuilder implements Builder<CustomPageDataImpl, CustomPageImplBuilder> {

        private Long id;
        private String name;
        private String description;
        private byte[] data;
        private Boolean isActive;

        public CustomPageImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public CustomPageImplBuilder withActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public CustomPageImplBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public CustomPageImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public CustomPageImplBuilder withData(byte[] data) {
            this.data = data;
            return this;
        }

        @Override
        public CustomPageDataImpl build() {
            return new CustomPageDataImpl(this);
        }

    }
}
