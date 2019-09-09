package org.cmdbuild.lookup;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.MoreCollectors.toOptional;
import org.cmdbuild.common.utils.PagedElements;

import javax.annotation.Nullable;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.utils.CmdbFilterUtils;

public interface LookupService {

    PagedElements<LookupType> getAllTypes(@Nullable Integer offset, @Nullable Integer limit, @Nullable String filter);

    PagedElements<Lookup> getAllLookup(String type, @Nullable Integer offset, @Nullable Integer limit, CmdbFilter filter);

    PagedElements<Lookup> getActiveLookup(String type, @Nullable Integer offset, @Nullable Integer limit, CmdbFilter filter);

    @Nullable
    Lookup getLookupByTypeAndCodeOrNull(String type, String code);

    Iterable<Lookup> getAllLookupOfParent(LookupType type);

    @Nullable
    Lookup getLookupOrNull(Long id);

    Lookup getLookup(Long id);

    Lookup createOrUpdateLookup(Lookup lookup);

    Lookup createOrUpdateLookup(LookupType lookupType, String code);

    LookupType getLookupType(String lookupTypeId);

    LookupType createLookupType(LookupType lookupType);

    LookupType updateLookupType(String lookupTypeId, LookupType lookupType);

    void deleteLookupValue(String lookupTypeId, long lookupId);

    void deleteLookupType(String lookupTypeId);

    default PagedElements<LookupType> getAllTypes() {
        return getAllTypes(null, null, null);
    }

    default PagedElements<Lookup> getAllLookup(String type) {
        return getAllLookup(type, null, null);
    }

    default PagedElements<Lookup> getAllLookup(LookupType type) {
        return getAllLookup(type, null, null);
    }

    default PagedElements<Lookup> getAllLookup(String type, @Nullable Integer offset, @Nullable Integer limit) {
        return getAllLookup(type, offset, limit, CmdbFilterUtils.noopFilter());
    }

    default PagedElements<Lookup> getAllLookup(LookupType type, @Nullable Integer offset, @Nullable Integer limit) {
        return getAllLookup(type.getName(), offset, limit);
    }

    default Lookup getLookupByTypeAndCode(String type, String code) {
        return checkNotNull(getLookupByTypeAndCodeOrNull(type, code), "lookup not found for type =< %s > and code =< %s >", type, code);
    }

    default LookupType createLookupType(String lookupType) {
        return createLookupType(LookupTypeImpl.builder().withName(lookupType).build());
    }

    @Nullable
    default Lookup getLookupByTypeAndDescriptionOrNull(String lookupType, String description) {
        return getAllLookup(lookupType).stream().filter(l -> equal(l.getDescription(), description)).collect(toOptional()).orElse(null);
    }

    default Lookup getLookupByTypeAndDescription(String lookupType, String description) {
        return checkNotNull(getLookupByTypeAndDescriptionOrNull(lookupType, description), "lookup not found for type =< %s > description =< %s >", lookupType, description);
    }

}
