package org.cmdbuild.cardfilter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import javax.annotation.Nullable;
import static org.cmdbuild.cardfilter.CardFilterConst.CLASS_ID;
import static org.cmdbuild.cardfilter.CardFilterConst.FILTER;
import static org.cmdbuild.cardfilter.CardFilterConst.SHARED;
import static org.cmdbuild.cardfilter.CardFilterConst.USER_ID;
import static org.cmdbuild.cardfilter.StoredFilterImpl.FILTER_CLASS_NAME;
import org.cmdbuild.cardfilter.StoredFilterImpl.StoredFilterImplBuilder;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@CardMapping(FILTER_CLASS_NAME)
@JsonDeserialize(builder = StoredFilterImplBuilder.class)
public class StoredFilterImpl implements StoredFilter {

    public final static String FILTER_CLASS_NAME = "_Filter";

    private final Long id;
    private final String name;
    private final String description;
    private final String configuration;
    private final String className;
    private final boolean shared;
    private final Long userId;

    private StoredFilterImpl(StoredFilterImplBuilder builder) {
        this.id = builder.id;
        this.name = checkNotBlank(builder.name, "filter name is null");
        this.description = nullToEmpty(builder.description);
        this.configuration = checkNotBlank(builder.configuration, "filter configuration is null");
        this.className = checkNotBlank(builder.className, "filter class is null");
        this.shared = builder.shared;
        if (shared) {
            this.userId = null;
        } else {
            this.userId = checkNotNull(builder.userId, "shared filters must have a owner user id");
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
    @CardAttr(FILTER)
    public String getConfiguration() {
        return configuration;
    }

    @Override
    @CardAttr(CLASS_ID)
    public String getClassName() {
        return className;
    }

    @Override
    @CardAttr(SHARED)
    public boolean isShared() {
        return shared;
    }

    @Override
    @CardAttr(USER_ID)
    public Long getUserId() {
        return userId;
    }

    public static StoredFilterImplBuilder builder() {
        return new StoredFilterImplBuilder();
    }

    public static StoredFilterImplBuilder copyOf(StoredFilter source) {
        return new StoredFilterImplBuilder()
                .withId(source.getId())
                .withName(source.getName())
                .withDescription(source.getDescription())
                .withConfiguration(source.getConfiguration())
                .withClassName(source.getClassName())
                .withShared(source.isShared())
                .withUserId(source.getUserId());
    }

    public static class StoredFilterImplBuilder implements Builder<StoredFilterImpl, StoredFilterImplBuilder> {

        private Long id;
        private String name;
        private String description;
        private String configuration;
        private String className;
        private Boolean shared;
        private Long userId;

        public StoredFilterImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public StoredFilterImplBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public StoredFilterImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public StoredFilterImplBuilder withConfiguration(String configuration) {
            this.configuration = configuration;
            return this;
        }

        public StoredFilterImplBuilder withClassName(String className) {
            this.className = className;
            return this;
        }

        public StoredFilterImplBuilder withShared(Boolean shared) {
            this.shared = shared;
            return this;
        }

        public StoredFilterImplBuilder withUserId(Long userId) {
            this.userId = userId;
            return this;
        }

        @Override
        public StoredFilterImpl build() {
            return new StoredFilterImpl(this);
        }

    }
}
