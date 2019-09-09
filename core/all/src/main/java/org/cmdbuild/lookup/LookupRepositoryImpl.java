package org.cmdbuild.lookup;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Maps.uniqueIndex;
import static com.google.common.collect.MoreCollectors.toOptional;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.Holder;
import org.cmdbuild.dao.postgres.LookupDescriptionService;
import org.cmdbuild.dao.orm.CardMapperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.cache.CmCache;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;

@Component
public class LookupRepositoryImpl implements LookupRepository {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final static String LOOKUP_TYPE_CODE = "org.cmdbuild.LOOKUPTYPE";

	private final LookupDescriptionService descriptionService;

	private final DaoService dao;

	private final CmCache<Optional<Lookup>> lookupCacheById;
	private final CmCache<List<Lookup>> lookupCacheByType;
	private final Holder<Collection<Lookup>> allLookupRecords, allLookups;
	private final Holder<List<LookupType>> allLookupTypes;

	public LookupRepositoryImpl(CardMapperService mapper, DaoService dataView, CacheService cacheService, DaoService dao, LookupDescriptionService descriptionService) {
		this.dao = checkNotNull(dao);
		this.descriptionService = checkNotNull(descriptionService);
		lookupCacheById = cacheService.newCache("lookup_store_cache_by_id");
		lookupCacheByType = cacheService.newCache("lookup_store_cache_by_type");
		allLookupRecords = cacheService.newHolder("lookup_store_all_lookup_records");
		allLookups = cacheService.newHolder("lookup_store_all_lookups");
		allLookupTypes = cacheService.newHolder("lookup_store_all_lookup_types");
	}

	private void invalidateCache() {
		lookupCacheById.invalidateAll();
		lookupCacheByType.invalidateAll();
		allLookupRecords.invalidate();
		allLookups.invalidate();
		allLookupTypes.invalidate();
		descriptionService.invalidateCache();//TODO use event bus for this
	}

	@Override
	@Nullable
	public Lookup getByIdOrNull(long lookupId) {
		return lookupCacheById.get(lookupId, () -> doGetById(lookupId)).orElse(null);
	}

	private Optional<Lookup> doGetById(long lookupId) {
		return getAll().stream().filter((l) -> equal(l.getId(), lookupId)).findFirst();
	}

	@Override
	public Collection<Lookup> getAll() {
		return allLookups.get(this::doGetAll);
	}

	private Collection<Lookup> doGetAll() {
		List<Lookup> rawLookups = getAllRecords().stream().filter((l) -> !equal(LOOKUP_TYPE_CODE, l.getCode())).collect(toList());
		Map<Long, Lookup> lookupsById = uniqueIndex(rawLookups, Lookup::getId);
		return rawLookups.stream().map((l) -> {
			if (l.getParentId() != null) {
				Lookup parent = checkNotNull(lookupsById.get(l.getParentId()), "lookup not found for id = %s", l.getParentId());
				l = LookupImpl.copyOf(l).withParent(parent).build();
			}
			return l;
		}).collect(toList());
	}

	@Override
	public List<Lookup> getByType(String type, CmdbFilter filter) {
		checkType(type);
		checkNotNull(filter);
		if (filter.isNoop()) {
			return lookupCacheByType.get(type, () -> doReadAll(type));
		} else {
			return dao.selectAll().from(LookupImpl.class).where("Type", EQ, type).where(filter).asList();
		}
	}

	private List<Lookup> doReadAll(String type) {
		return getAll().stream().filter((l) -> equal(l.getType().getName(), type)).collect(toList());
	}

	@Override
	public Collection<LookupType> getAllTypes() {
		return allLookupTypes.get(() -> getAllRecords().stream().filter((l) -> equal(LOOKUP_TYPE_CODE, l.getCode())).map(Lookup::getType).collect(toList()));
	}

	@Override
	public LookupType getTypeByName(String lookupTypeName) {
		return checkNotNull(getTypeByNameOrNull(lookupTypeName), "lookup type not found for name = '%s'", lookupTypeName);
	}

	private @Nullable
	LookupType getTypeByNameOrNull(String lookupTypeName) {
		return getOnlyElement(getAllTypes().stream().filter((l) -> equal(l.getName(), lookupTypeName)).collect(toList()), null);
	}

	@Override
	public LookupType createLookupType(LookupType lookupType) {
		Lookup lookup = LookupImpl.builder()
				.withCode(LOOKUP_TYPE_CODE)
				.withType(lookupType)
				.build();
		lookup = dao.create(lookup);
		invalidateCache();
		return lookup.getType();
	}

	@Override
	public LookupType updateLookupType(String lookupTypeId, LookupType newType) {
		logger.info("updateLookupType = {} to {}", lookupTypeId, newType);
		LookupType curType = getTypeByName(lookupTypeId);
		checkArgument(equal(newType.getParentOrNull(), curType.getParentOrNull()), "it is not permitted to change lookup type parent");
		if (equal(newType.getName(), lookupTypeId)) {
			return curType;//nothing to do
		} else {
			//TODO check lookup reference in class attributes, etc
			checkArgument(getTypeByNameOrNull(newType.getName()) == null, "lookup type already present with name = '%s'", newType.getName());
			dao.getJdbcTemplate().update("UPDATE \"LookUp\" SET \"Type\" = ? WHERE \"Type\" = ? AND \"Status\" = 'A'", newType.getName(), curType.getName());
			invalidateCache();
			return getTypeByName(newType.getName());
		}
	}

	@Override
	@Transactional //TODO check that annotation works!
	public void deleteLookupType(String lookupTypeId) {
		checkType(lookupTypeId);
		//TODO check lookup reference in class attributes, etc
		logger.info("delete lookup type for id = {}", lookupTypeId);
		getAllRecords().stream().filter((l) -> equal(l.getType().getName(), lookupTypeId)).map(Lookup::getId).forEach(this::deleteLookupRecord);
	}

	@Override
	@Nullable
	public Lookup getOneByTypeAndCodeOrNull(String type, String code) {
		return getAllByType(LookupTypeImpl.builder().withName(type).build()).stream().filter((l) -> equal(l.getCode(), code)).collect(toOptional()).orElse(null);
	}

	@Override
	public Lookup getOneByTypeAndDescriptionOrNull(String type, String description) {
		List<Lookup> list = getAllByType(LookupTypeImpl.builder().withName(type).build()).stream().filter((l) -> equal(l.getDescription(), description)).collect(toList());
		if (list.isEmpty()) {
			return null;
		} else if (list.size() == 1) {
			return getOnlyElement(list);
		} else {
			logger.warn("found more than one lookup for type = %s description = %s", type, description);
			return null;
		}
	}

	@Override
	public Lookup createOrUpdate(Lookup lookup) {
		lookup = LookupImpl.copyOf(lookup).build();
		if (lookup.hasParentId() && !lookup.hasParent()) {
			Lookup parent = getById(lookup.getParentId());
			lookup = LookupImpl.copyOf(lookup).withParent(parent).build();
		}
		if (lookup.hasId()) {
			Lookup orig = dao.getById(LookupImpl.class, lookup.getId()).toModel();
			checkArgument(orig.getParentTypeOrNull() == null || lookup.getParentTypeOrNull() == null || equal(orig.getParentTypeOrNull(), lookup.getParentTypeOrNull()), "parenty type mismatch: update not allowed");
			checkArgument(equal(orig.getType().getName(), lookup.getType().getName()), "cannot change lookup type: operation not allowed");
			lookup = dao.update(LookupImpl.copyOf(lookup).withId(orig.getId()).build());
		} else {
			lookup = dao.create(lookup);
		}
		//TODO fix types
		logger.info("create/update lookup record = {}", lookup);
		invalidateCache();
		return getById(lookup.getId());
	}

	@Override
	public void deleteLookupRecord(long lookupValueId) {
		logger.info("delete lookup record for id = {}", lookupValueId);
		//TODO check for records that inherit from this value !!
		dao.delete(LookupImpl.class, lookupValueId);
		invalidateCache();
	}

	private Collection<Lookup> getAllRecords() {
		return allLookupRecords.get(() -> doGetAllRecords());
	}

	private Collection<Lookup> doGetAllRecords() {
		return dao.selectAll().from(LookupImpl.class).asList();
	}

	private void checkType(String lookupTypeId) {
		getTypeByName(lookupTypeId);
	}

}
