package org.cmdbuild.lookup;

import org.cmdbuild.common.utils.PagedElements;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.alwaysTrue;
import com.google.common.collect.Ordering;
import java.util.Collection;
import static java.util.Collections.emptyList;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.data.filter.CmdbFilter;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNullOrLtEqZero;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@Component("lookupService")
public class LookupServiceImpl implements LookupService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final LookupRepository repository;

    public LookupServiceImpl(LookupRepository repository) {
        this.repository = checkNotNull(repository);
    }

    @Override
    public PagedElements<LookupType> getAllTypes(@Nullable Integer offset, @Nullable Integer limit, @Nullable String filter) {
        logger.trace("getting all lookup types");
        Collection<LookupType> allTypes = repository.getAllTypes();
        List<LookupType> list = allTypes.stream()
                .filter(isBlank(filter) ? alwaysTrue() : (l) -> matchFilter(filter, l.getName()))
                .sorted(Ordering.natural().onResultOf(LookupType::getName))
                .skip(firstNonNull(offset, 0))
                .limit(firstNonNull(limit, Integer.MAX_VALUE))
                .collect(toList());
        return new PagedElements<>(list, allTypes.size());
    }

    @Override
    public PagedElements<Lookup> getAllLookup(String type, @Nullable Integer offset, @Nullable Integer limit, CmdbFilter filter) {
        return getAllLookup(type, offset, limit, filter, false);
    }

    @Override
    public PagedElements<Lookup> getActiveLookup(String type, Integer offset, Integer limit, CmdbFilter filter) {
        return getAllLookup(type, offset, limit, filter, true);
    }

    private PagedElements<Lookup> getAllLookup(String type, @Nullable Integer offset, @Nullable Integer limit, CmdbFilter filter, boolean activeOnly) {
        Collection<Lookup> lookups = repository.getByType(type, filter);
        if (activeOnly) {
            lookups = lookups.stream().filter(Lookup::isActive).collect(toList());
        }
        int count = lookups.size();
        lookups = lookups.stream()
                .sorted(Ordering.natural().onResultOf(Lookup::getIndex))
                .skip(firstNonNull(offset, 0))
                .limit(firstNonNull(limit, Integer.MAX_VALUE))
                .collect(toList());
        return new PagedElements<>(lookups, count);
    }

    @Override
    @Nullable
    public Lookup getLookupByTypeAndCodeOrNull(String type, String code) {
        return repository.getOneByTypeAndCodeOrNull(type, code);
    }

    @Override
    public Iterable<Lookup> getAllLookupOfParent(LookupType type) {
        logger.debug("getting all lookups for the parent of type '{}'", type);
        if (type.hasParent()) {
            return repository.getAllByType(type.getParentOrNull());
        } else {
            return emptyList();
        }
    }

    @Override
    public Lookup getLookup(Long id) {
        return repository.getById(id);
    }

    @Override
    public Lookup getLookupOrNull(Long id) {
        return repository.getByIdOrNull(id);
    }

    @Override
    public LookupType getLookupType(String lookupTypeId) {
        return repository.getTypeByName(lookupTypeId);
    }

    @Override
    public Lookup createOrUpdateLookup(Lookup lookup) {
        logger.info("creating or updating lookup '{}'", lookup);
        checkNotNull(lookup);
        if (isNullOrLtEqZero(lookup.getIndex())) {
            int nextIndex = repository.getAllByType(lookup.getType()).stream().map(Lookup::getIndex).reduce(Integer::max).orElse(-1) + 1;
            lookup = LookupImpl.copyOf(lookup).withIndex(nextIndex).build();
        } else {
            lookup = LookupImpl.copyOf(lookup).build();
        }
        return repository.createOrUpdate(lookup);
    }

    @Override
    public Lookup createOrUpdateLookup(LookupType lookupType, String code) {
        return createOrUpdateLookup(LookupImpl.builder().withType(lookupType).withCode(code).build());
    }

    @Override
    public LookupType createLookupType(LookupType lookupType) {
        return repository.createLookupType(lookupType);
    }

    @Override
    public LookupType updateLookupType(String lookupTypeId, LookupType lookupType) {
        return repository.updateLookupType(lookupTypeId, lookupType);
    }

    @Override
    public void deleteLookupValue(String lookupTypeId, long lookupValueId) {
        repository.deleteLookupRecord(lookupValueId);
    }

    @Override
    public void deleteLookupType(String lookupTypeId) {
        repository.deleteLookupType(lookupTypeId);
    }

    private static boolean matchFilter(String filter, String name) {
        checkNotBlank(filter);
        checkNotBlank(name);
        return name.toLowerCase().contains(filter.trim().toLowerCase());
    }
}
