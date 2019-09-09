package org.cmdbuild.cardfilter;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collection;
import java.util.Optional;

import java.util.List;
import javax.annotation.Nullable;

public interface CardFilterService {

    StoredFilter create(StoredFilter filter);

    default Optional<StoredFilter> read(StoredFilter filter) {
        StoredFilter res = readOrNull(filter.getId());
        return Optional.ofNullable(res);
    }

    @Nullable
    StoredFilter readOrNull(long filterId);

    default StoredFilter getById(long filterId) {
        return checkNotNull(readOrNull(filterId), "filter not found for id = %s", filterId);
    }

    StoredFilter update(StoredFilter filter);

    default void delete(StoredFilter filter) {
        delete(filter.getId());
    }

    void delete(long filterId);

    List<StoredFilter> readAllForCurrentUser(String className);

    List<StoredFilter> readAllSharedFilters();

    List<StoredFilter> readSharedForCurrentUser(String className);

    List<CardFilterAsDefaultForClass> getDefaultFiltersForRole(long roleId);

    List<CardFilterAsDefaultForClass> getDefaultFiltersForFilter(long filterId);

    void setDefaultFiltersForRole(long roleId, Collection<CardFilterAsDefaultForClass> newFilters);
 
    List<CardFilterAsDefaultForClass> getAllDefaultFiltersForCurrentUser();

    List<CardFilterAsDefaultForClass> setDefaultFiltersForFilterWithMatchingClass(long filterId, List<CardFilterAsDefaultForClass> filtersUpdate);

}
