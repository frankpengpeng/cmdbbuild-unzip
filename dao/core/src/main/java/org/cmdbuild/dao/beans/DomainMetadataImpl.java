/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.beans;

import com.google.common.base.Joiner;
import static com.google.common.base.Predicates.not;
import com.google.common.base.Splitter;
import static com.google.common.base.Strings.nullToEmpty;
import com.google.common.collect.ImmutableSet;
import static com.google.common.collect.Maps.filterKeys;
import java.util.Collection;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import org.cmdbuild.dao.entrytype.ClassPermissionMode;
import static org.cmdbuild.dao.entrytype.DaoPermissionUtils.serializeClassPermissionMode;
import static org.cmdbuild.dao.entrytype.Domain.DEFAULT_INDEX_VALUE;
import org.cmdbuild.dao.entrytype.DomainCardinality;
import static org.cmdbuild.dao.entrytype.DomainCardinality.MANY_TO_MANY;
import org.cmdbuild.dao.entrytype.DomainCardinalityUtils;
import static org.cmdbuild.dao.entrytype.DomainCardinalityUtils.serializeDomainCardinality;
import org.cmdbuild.dao.entrytype.DomainMetadata;
import static org.cmdbuild.dao.entrytype.DomainMetadata.CARDINALITY;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.toIntegerOrDefault;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.blankToNull;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

public class DomainMetadataImpl extends EntryTypeMetadataImpl implements DomainMetadata {

    private final static Set<String> DOMAIN_METADATA_ATTRS = set(DESCRIPTION_1, DESCRIPTION_2, CARDINALITY, MASTERDETAIL, MASTERDETAIL_DESCRIPTION, MASTERDETAIL_FILTER, DISABLED_1, DISABLED_2, INDEX_1, INDEX_2, INLINE, DEFAULT_CLOSED).immutable();

    private final String description1, description2, masterDetailDescription, masterDetailFilter;
    private final boolean isMasterDetail, isInline, isDefaultClosed;
    private final Set<String> disabled1, disabled2;
    private final int index1, index2;
    private final DomainCardinality cardinality;

    public DomainMetadataImpl(Map<String, String> map) {
        super(map, filterKeys(map, not(DOMAIN_METADATA_ATTRS::contains)));
        description1 = map.get(DESCRIPTION_1);
        description2 = map.get(DESCRIPTION_2);
        cardinality = Optional.ofNullable(blankToNull(map.get(CARDINALITY))).map(DomainCardinalityUtils::parseDomainCardinality).orElse(MANY_TO_MANY);
        isMasterDetail = toBooleanOrDefault(map.get(MASTERDETAIL), false);
        masterDetailDescription = map.get(MASTERDETAIL_DESCRIPTION);
        masterDetailFilter = map.get(MASTERDETAIL_FILTER);
        disabled1 = toDisabled(map.get(DISABLED_1));
        disabled2 = toDisabled(map.get(DISABLED_2));
        index1 = toIntegerOrDefault(map.get(INDEX_1), DEFAULT_INDEX_VALUE);
        index2 = toIntegerOrDefault(map.get(INDEX_2), DEFAULT_INDEX_VALUE);
        isInline = toBooleanOrDefault(map.get(INLINE), false);
        isDefaultClosed = toBooleanOrDefault(map.get(DEFAULT_CLOSED), true);
    }

    public DomainMetadataImpl() {
        this(emptyMap());
    }

    private static Set<String> toDisabled(@Nullable String value) {
        return ImmutableSet.copyOf(Splitter.on(",").trimResults().omitEmptyStrings().splitToList(nullToEmpty(value)));
    }

    @Override
    public boolean isInline() {
        return isInline;
    }

    @Override
    public boolean isDefaultClosed() {
        return isDefaultClosed;
    }

    @Override
    public String getDirectDescription() {
        return description1;
    }

    @Override
    public String getInverseDescription() {
        return description2;
    }

    @Override
    public String getCardinality() {
        return serializeDomainCardinality(cardinality);
    }

    @Override
    public String getMasterDetailDescription() {
        return masterDetailDescription;
    }

    @Override
    public String getMasterDetailFilter() {
        return masterDetailFilter;
    }

    @Override
    public boolean isMasterDetail() {
        return isMasterDetail;
    }

    @Override
    public Collection<String> getDisabledSourceDescendants() {
        return disabled1;
    }

    @Override
    public Collection<String> getDisabledTargetDescendants() {
        return disabled2;
    }

    @Override
    public int getIndexForSource() {
        return index1;
    }

    @Override
    public int getIndexForTarget() {
        return index2;
    }

    public static DomainMetadataImplBuilder builder() {
        return new DomainMetadataImplBuilder();
    }

    public static DomainMetadataImplBuilder copyOf(DomainMetadata source) {
        return builder().with(source);
    }

    public static class DomainMetadataImplBuilder implements Builder<DomainMetadataImpl, DomainMetadataImplBuilder> {

        private final Map<String, String> metadata = map();

        public DomainMetadataImplBuilder with(String key, @Nullable Object value) {
            metadata.put(key, toStringOrNull(value));
            return this;
        }

        public DomainMetadataImplBuilder with(DomainMetadata source) {
            metadata.putAll(source.getAll());
            return this;
        }

        public DomainMetadataImplBuilder withCardinality(String value) {
            return this.with(CARDINALITY, value);
        }

        public DomainMetadataImplBuilder withCardinality(DomainCardinality domainCardinality) {
            return withCardinality(serializeDomainCardinality(domainCardinality));
        }

        public DomainMetadataImplBuilder withDescription(String value) {
            return this.with(DESCRIPTION, value);
        }

        public DomainMetadataImplBuilder withDirectDescription(String value) {
            return this.with(DESCRIPTION_1, value);
        }

        public DomainMetadataImplBuilder withInverseDescription(String value) {
            return this.with(DESCRIPTION_2, value);
        }

        public DomainMetadataImplBuilder withIsActive(Boolean value) {
            return this.with(ACTIVE, value);
        }

        public DomainMetadataImplBuilder withIsMasterDetail(Boolean value) {
            return this.with(MASTERDETAIL, value);
        }

        public DomainMetadataImplBuilder withInline(Boolean value) {
            return this.with(INLINE, value);
        }

        public DomainMetadataImplBuilder withDefaultClosed(Boolean value) {
            return this.with(DEFAULT_CLOSED, value);
        }

        public DomainMetadataImplBuilder withDisabledSourceDescendants(Collection<String> value) {
            return this.with(DISABLED_1, Joiner.on(",").join(firstNonNull(value, emptyList())));
        }

        public DomainMetadataImplBuilder withDisabledTargetDescendants(Collection<String> value) {
            return this.with(DISABLED_2, Joiner.on(",").join(firstNonNull(value, emptyList())));
        }

        public DomainMetadataImplBuilder withMasterDetailDescription(String value) {
            return this.with(MASTERDETAIL_DESCRIPTION, value);
        }

        public DomainMetadataImplBuilder withMasterDetailFilter(String value) {
            return this.with(MASTERDETAIL_FILTER, value);
        }

        public DomainMetadataImplBuilder withSourceIndex(Number value) {
            return this.with(INDEX_1, value);
        }

        public DomainMetadataImplBuilder withTargetIndex(Number value) {
            return this.with(INDEX_2, value);
        }

        public DomainMetadataImplBuilder withMode(ClassPermissionMode mode) {
            return this.with(ENTRY_TYPE_MODE, serializeClassPermissionMode(mode));
        }

        @Override
        public DomainMetadataImpl build() {
            return new DomainMetadataImpl(metadata);
        }

    }
}
