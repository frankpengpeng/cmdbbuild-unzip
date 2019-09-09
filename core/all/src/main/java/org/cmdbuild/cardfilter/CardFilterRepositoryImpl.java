package org.cmdbuild.cardfilter;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;
import org.cmdbuild.cache.CacheService;
import static org.cmdbuild.cardfilter.CardFilterConst.CLASS_ID;
import static org.cmdbuild.cardfilter.CardFilterConst.DB_REPO;
import static org.cmdbuild.cardfilter.CardFilterConst.SHARED;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static org.cmdbuild.cardfilter.CardFilterConst.USER_ID;
import static org.cmdbuild.cardfilter.StoredFilterImpl.FILTER_CLASS_NAME;
import static org.cmdbuild.common.Constants.ROLE_CLASS_NAME;
import static org.cmdbuild.dao.beans.CardIdAndClassNameImpl.card;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import org.springframework.context.annotation.Primary;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.WhereOperator.EQ;
import static org.cmdbuild.data.filter.SorterElement.SorterElementDirection.ASC;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.cache.CmCache;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDOBJ1;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDOBJ2;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;

@Primary
@Component(DB_REPO)
public class CardFilterRepositoryImpl implements CardFilterRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final CmCache<StoredFilter> filtersById;

    public CardFilterRepositoryImpl(DaoService dao, CacheService cacheService) {
        this.dao = checkNotNull(dao);
        filtersById = cacheService.newCache("card_filters_by_id");
    }

    @Override
    @Nullable
    public StoredFilter readOrNull(Long filterId) {
        return filtersById.get(filterId.toString(), () -> dao.getById(StoredFilterImpl.class, filterId).toModel());
    }

    @Override
    public List<StoredFilter> readNonSharedFilters(String className, long userId) {
        return dao.selectAll().from(StoredFilterImpl.class)
                .where(SHARED, EQ, false)
                .where(USER_ID, EQ, userId)
                .where(CLASS_ID, EQ, checkNotBlank(className))
                .orderBy(ATTR_CODE, ASC)
                .asList();
    }

    @Override
    public List<StoredFilter> readSharedFilters(String className) {
        return dao.selectAll().from(StoredFilterImpl.class)
                .where(SHARED, EQ, true)
                .where(CLASS_ID, EQ, checkNotBlank(className))
                .orderBy(ATTR_CODE, ASC)
                .asList();
    }

    @Override
    public StoredFilter create(StoredFilter filter) {
        return dao.create(StoredFilterImpl.copyOf(filter).build());
    }

    @Override
    public StoredFilter update(StoredFilter filter) {
        filtersById.invalidate(filter.getId());
        return dao.update(StoredFilterImpl.copyOf(filter).build());
    }

    @Override
    public void delete(long filterId) {
        filtersById.invalidate(filterId);
        dao.delete(StoredFilterImpl.class, filterId);
    }

    @Override
    public List<StoredFilter> getAllSharedFilters() {
        return dao.selectAll().from(StoredFilterImpl.class).where(SHARED, EQ, true).orderBy(ATTR_CODE, ASC).asList();
    }

    @Override
    public List<CardFilterAsDefaultForClass> getFiltersForRole(long roleId) {
        return dao.selectAll().fromDomain("FilterRole").where(ATTR_IDOBJ2, EQ, roleId).getRelations().stream()
                .map(r -> new CardFilterAsDefaultForClassImpl(getOne(r.getSourceId()), dao.getClasse(r.getString("DefaultFor")), r.getTargetId())).collect(toList());
    }

    @Override
    public List<CardFilterAsDefaultForClass> getDefaultFiltersForFilter(long filterId) {
        return dao.selectAll().fromDomain("FilterRole").where(ATTR_IDOBJ1, EQ, filterId).getRelations().stream()
                .map(r -> new CardFilterAsDefaultForClassImpl(getOne(r.getSourceId()), dao.getClasse(r.getString("DefaultFor")), r.getTargetId())).collect(toList());
    }

    @Override
    public void setFiltersForRole(long roleId, Collection<CardFilterAsDefaultForClass> filters) {
        filters.stream().forEach(f -> checkArgument(roleId == f.getDefaultForRole()));
        Map<String, CardFilterAsDefaultForClass> newFilters = map(filters, this::keyFor),
                currentFilters = map(getFiltersForRole(roleId), this::keyFor);
        Map<String, CardFilterAsDefaultForClass> toAdd = map(newFilters).withoutKeys(currentFilters.keySet()),
                toRemove = map(currentFilters).withoutKeys(newFilters.keySet());
        toRemove.values().forEach(f -> dao.getJdbcTemplate().update("UPDATE \"Map_FilterRole\" SET \"Status\" = 'N' WHERE \"Status\" = 'A' AND \"IdObj1\" = ? AND \"IdObj2\" = ? AND \"DefaultFor\" = _cm3_utils_name_to_regclass(?)",
                f.getFilter().getId(), f.getDefaultForRole(), f.getDefaultForClass().getName()));
        toAdd.values().forEach((f) -> dao.createRelation("FilterRole", card(FILTER_CLASS_NAME, f.getFilter().getId()), card(ROLE_CLASS_NAME, f.getDefaultForRole()), "DefaultFor", f.getDefaultForClass()));
    }

    @Override
    public void setDefaultFiltersForFilterWithMatchingClass(long filterId, Collection<CardFilterAsDefaultForClass> filters) {
        filters.stream().forEach(f -> checkArgument(equal(f.getDefaultForClass().getName(), f.getFilter().getClassName()) && filterId == f.getFilter().getId()));
        Map<String, CardFilterAsDefaultForClass> newFilters = map(filters, this::keyFor),
                currentFilters = getDefaultFiltersForFilter(filterId).stream().filter(f -> equal(f.getDefaultForClass().getName(), f.getFilter().getClassName())).collect(toMap(this::keyFor, identity()));
        Map<String, CardFilterAsDefaultForClass> toAdd = map(newFilters).withoutKeys(currentFilters.keySet()),
                toRemove = map(currentFilters).withoutKeys(newFilters.keySet());
        toRemove.values().forEach(f -> dao.getJdbcTemplate().update("UPDATE \"Map_FilterRole\" SET \"Status\" = 'N' WHERE \"Status\" = 'A' AND \"IdObj1\" = ? AND \"IdObj2\" = ? AND \"DefaultFor\" = _cm3_utils_name_to_regclass(?)",
                f.getFilter().getId(), f.getDefaultForRole(), f.getDefaultForClass().getName()));
        toAdd.values().forEach((f) -> dao.createRelation("FilterRole", card(FILTER_CLASS_NAME, f.getFilter().getId()), card(ROLE_CLASS_NAME, f.getDefaultForRole()), "DefaultFor", f.getDefaultForClass()));
    }

    private String keyFor(CardFilterAsDefaultForClass f) {
        return key(f.getFilter().getId(), f.getDefaultForClass().getName(), f.getDefaultForRole());
    }

}
