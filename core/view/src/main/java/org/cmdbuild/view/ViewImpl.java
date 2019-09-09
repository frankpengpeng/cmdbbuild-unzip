package org.cmdbuild.view;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import javax.annotation.Nullable;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.cmdbuild.data.filter.utils.CmdbFilterUtils;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.view.ViewImpl.VIEW_CLASS_NAME;

@CardMapping(VIEW_CLASS_NAME)
public class ViewImpl implements View {

    public static final String VIEW_CLASS_NAME = "_View";

    private final Long id;
    private final String name;
    private final String description;
    private final String sourceClassName;
    private final String sourceFunction;
    private final String filter;
    private final ViewType type;
    private final boolean isActive;

    private ViewImpl(ViewImplBuilder builder) {
        this.id = builder.id;
        this.name = checkNotBlank(builder.name);
        this.description = nullToEmpty(builder.description);
        this.type = checkNotNull(builder.type);
        this.isActive = firstNotNull(builder.isActive, true);
        switch (type) {
            case FILTER:
                this.sourceClassName = checkNotBlank(builder.sourceClass);
                this.sourceFunction = null;
                this.filter = checkNotBlank(builder.filter);
                CmdbFilterUtils.checkFilterSyntax(filter);
                break;
            case SQL:
                this.sourceFunction = checkNotBlank(builder.sourceFunction);
                this.sourceClassName = null;
                this.filter = null;
                break;
            default:
                throw unsupported("unsupported view type = %s", type);
        }
    }

    @Override
    @Nullable
    @CardAttr(ATTR_ID)
    public Long getId() {
        return id;
    }

    @Override
    @CardAttr(ATTR_CODE)
    public String getName() {
        return name;
    }

    @Override
    @CardAttr(ATTR_DESCRIPTION)
    public String getDescription() {
        return description;
    }

    @Override
    @Nullable
    @CardAttr("IdSourceClass")
    public String getSourceClass() {
        return sourceClassName;
    }

    @Override
    @Nullable
    @CardAttr
    public String getSourceFunction() {
        return sourceFunction;
    }

    @Override
    @Nullable
    @CardAttr
    public String getFilter() {
        return filter;
    }

    @Override
    @CardAttr
    public ViewType getType() {
        return type;
    }

    @Override
    @CardAttr("Active")
    public boolean isActive() {
        return isActive;
    }

    @Override
    public String getPrivilegeId() {
        return String.format("View:%d", getId());
    }

    @Override
    public String toString() {
        return "View{" + "id=" + id + ", name=" + name + ", type=" + type + '}';
    }

    public static ViewImplBuilder builder() {
        return new ViewImplBuilder();
    }

    public static ViewImplBuilder copyOf(View source) {
        return new ViewImplBuilder()
                .withId(source.getId())
                .withName(source.getName())
                .withDescription(source.getDescription())
                .withSourceClass(source.getSourceClass())
                .withSourceFunction(source.getSourceFunction())
                .withFilter(source.getFilter())
                .withActive(source.isActive())
                .withType(source.getType());
    }

    public static class ViewImplBuilder implements Builder<ViewImpl, ViewImplBuilder> {

        private Long id;
        private String name;
        private String description;
        private String sourceClass;
        private String sourceFunction;
        private String filter;
        private ViewType type;
        private Boolean isActive;

        public ViewImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public ViewImplBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public ViewImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public ViewImplBuilder withSourceClass(String sourceClass) {
            this.sourceClass = sourceClass;
            return this;
        }

        public ViewImplBuilder withSourceFunction(String sourceFunction) {
            this.sourceFunction = sourceFunction;
            return this;
        }

        public ViewImplBuilder withFilter(String filter) {
            this.filter = filter;
            return this;
        }

        public ViewImplBuilder withType(ViewType type) {
            this.type = type;
            return this;
        }

        public ViewImplBuilder withActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        @Override
        public ViewImpl build() {
            return new ViewImpl(this);
        }

    }
}
