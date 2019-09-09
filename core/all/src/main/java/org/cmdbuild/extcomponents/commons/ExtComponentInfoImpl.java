/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.extcomponents.commons;

import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;

public class ExtComponentInfoImpl implements ExtComponentInfo {

    private final long id;
    private final String name, description, extjsComponentId, extjsAlias;
    private final boolean isActive;

    private ExtComponentInfoImpl(ExtComponentInfoImplBuilder builder) {
        this.id = builder.id;
        this.isActive = builder.isActive;
        this.name = checkNotBlank(builder.name);
        this.description = firstNotBlank(builder.description, name);
        this.extjsComponentId = checkNotBlank(builder.extjsComponentId);
        this.extjsAlias = checkNotBlank(builder.extjsAlias);
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public boolean getActive() {
        return isActive;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getExtjsComponentId() {
        return extjsComponentId;
    }

    @Override
    public String getExtjsAlias() {
        return extjsAlias;
    }

    @Override
    public String toString() {
        return "ExtComponentInfo{" + "id=" + id + ", name=" + name + '}';
    }

    public static ExtComponentInfoImplBuilder builder() {
        return new ExtComponentInfoImplBuilder();
    }

    public static ExtComponentInfoImplBuilder copyOf(ExtComponentInfo source) {
        return new ExtComponentInfoImplBuilder()
                .withId(source.getId())
                .withActive(source.getActive())
                .withName(source.getName())
                .withDescription(source.getDescription())
                .withExtjsComponentId(source.getExtjsComponentId())
                .withExtjsAlias(source.getExtjsAlias());
    }

    public static class ExtComponentInfoImplBuilder implements Builder<ExtComponentInfoImpl, ExtComponentInfoImplBuilder> {

        private long id;
        private Boolean isActive;
        private String name;
        private String description;
        private String extjsComponentId;
        private String extjsAlias;

        public ExtComponentInfoImplBuilder withId(long id) {
            this.id = id;
            return this;
        }

        public ExtComponentInfoImplBuilder withActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public ExtComponentInfoImplBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public ExtComponentInfoImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public ExtComponentInfoImplBuilder withExtjsComponentId(String extjsComponentId) {
            this.extjsComponentId = extjsComponentId;
            return this;
        }

        public ExtComponentInfoImplBuilder withExtjsAlias(String extjsAlias) {
            this.extjsAlias = extjsAlias;
            return this;
        }

        @Override
        public ExtComponentInfoImpl build() {
            return new ExtComponentInfoImpl(this);
        }

    }
}
