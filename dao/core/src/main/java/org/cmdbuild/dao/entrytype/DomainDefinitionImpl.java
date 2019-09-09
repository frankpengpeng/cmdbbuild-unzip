/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.entrytype;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.cmdbuild.dao.beans.DomainMetadataImpl;
import org.cmdbuild.dao.beans.DomainMetadataImpl.DomainMetadataImplBuilder;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;

public class DomainDefinitionImpl implements DomainDefinition {

    private final Long oid;
    private final String name;
    private final Classe sourceClass, targetClass;
    private final DomainMetadata metadata;

    private DomainDefinitionImpl(DomainDefinitionImplBuilder builder) {
        this.oid = builder.oid;
        this.sourceClass = checkNotNull(builder.sourceClass, "source class is null");
        this.targetClass = checkNotNull(builder.targetClass, "target class is null");
        this.name = firstNotBlank(builder.name, format("%s%s", sourceClass.getName(), targetClass.getName()));//TODO check size limit
        this.metadata = checkNotNull(builder.metadata, "domain metadata is null");
    }

    @Nullable
    @Override
    public Long getOid() {
        return oid;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Classe getSourceClass() {
        return sourceClass;
    }

    @Override
    public Classe getTargetClass() {
        return targetClass;
    }

    @Override
    public DomainMetadata getMetadata() {
        return metadata;
    }

    public static DomainDefinitionImplBuilder builder() {
        return new DomainDefinitionImplBuilder();
    }

    public static DomainDefinitionImplBuilder copyOf(DomainDefinition source) {
        return new DomainDefinitionImplBuilder()
                .withOid(source.getOid())
                .withName(source.getName())
                .withSourceClass(source.getSourceClass())
                .withTargetClass(source.getTargetClass())
                .withMetadata(source.getMetadata());
    }

    public static class DomainDefinitionImplBuilder implements Builder<DomainDefinitionImpl, DomainDefinitionImplBuilder> {

        private Long oid;
        private String name;
        private Classe sourceClass;
        private Classe targetClass;
        private DomainMetadata metadata = new DomainMetadataImpl();

        public DomainDefinitionImplBuilder withOid(Long oid) {
            this.oid = oid;
            return this;
        }

        public DomainDefinitionImplBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public DomainDefinitionImplBuilder withSourceClass(Classe sourceClass) {
            this.sourceClass = sourceClass;
            return this;
        }

        public DomainDefinitionImplBuilder withTargetClass(Classe targetClass) {
            this.targetClass = targetClass;
            return this;
        }

        public DomainDefinitionImplBuilder withMetadata(DomainMetadata metadata) {
            this.metadata = metadata;
            return this;
        }

        public DomainDefinitionImplBuilder withMetadata(Consumer<DomainMetadataImplBuilder> metadata) {
            this.metadata = DomainMetadataImpl.builder().accept(metadata).build();
            return this;
        }

        public DomainDefinitionImplBuilder withCardinality(DomainCardinality cardinality) {
            this.metadata = DomainMetadataImpl.copyOf(metadata).withCardinality(cardinality).build();
            return this;
        }

        @Override
        public DomainDefinitionImpl build() {
            return new DomainDefinitionImpl(this);
        }

    }
}
