/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.jobs.beans;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import java.util.Map;
import javax.annotation.Nullable;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.cmdbuild.jobs.JobData;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.utils.lang.JsonBean;

@CardMapping("_Job")
public class JobDataImpl implements JobData {

    private final Long id;
    private final String code, description, type;
    private final boolean isEnabled;
    private final Map<String, Object> config;

    private JobDataImpl(JobDataImplBuilder builder) {
        this.id = builder.id;
        this.code = checkNotBlank(builder.code);
        this.description = nullToEmpty(builder.description);
        this.type = checkNotBlank(builder.type);
        this.isEnabled = builder.isEnabled;
        this.config = map(checkNotNull(builder.config)).immutable();
    }

    @CardAttr(ATTR_ID)
    @Nullable
    @Override
    public Long getId() {
        return id;
    }

    @CardAttr(ATTR_CODE)
    @Override
    public String getCode() {
        return code;
    }

    @CardAttr(ATTR_DESCRIPTION)
    @Override
    public String getDescription() {
        return description;
    }

    @Override
    @CardAttr
    public String getType() {
        return type;
    }

    @CardAttr
    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @CardAttr
    @JsonBean
    @Override
    public Map<String, Object> getConfig() {
        return config;
    }

    @Override
    public String toString() {
        return "JobData{" + "id=" + id + ", code=" + code + ", type=" + type + ", isEnabled=" + isEnabled + '}';
    }

    public static JobDataImplBuilder builder() {
        return new JobDataImplBuilder();
    }

    public static JobDataImplBuilder copyOf(JobData source) {
        return new JobDataImplBuilder()
                .withId(source.getId())
                .withCode(source.getCode())
                .withDescription(source.getDescription())
                .withType(source.getType())
                .withEnabled(source.isEnabled())
                .withConfig(source.getConfig());
    }

    public static class JobDataImplBuilder implements Builder<JobDataImpl, JobDataImplBuilder> {

        private Long id;
        private String code;
        private String description;
        private String type;
        private Boolean isEnabled;
        private Map<String, Object> config;

        public JobDataImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public JobDataImplBuilder withCode(String code) {
            this.code = code;
            return this;
        }

        public JobDataImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public JobDataImplBuilder withType(String type) {
            this.type = type;
            return this;
        }

        public JobDataImplBuilder withEnabled(Boolean isEnabled) {
            this.isEnabled = isEnabled;
            return this;
        }

        public JobDataImplBuilder withConfig(Map<String, Object> config) {
            this.config = config;
            return this;
        }

        @Override
        public JobDataImpl build() {
            return new JobDataImpl(this);
        }

    }
}
