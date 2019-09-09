/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.lock;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.not;
import java.time.ZonedDateTime;
import static java.time.temporal.ChronoUnit.SECONDS;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.scheduler.ScheduledJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.config.CoreConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.WhereOperator.EQ;
import org.cmdbuild.cache.CmCache;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNullOrLtEqZero;
import static org.cmdbuild.lock.LockScopeUtils.serializeLockScope;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBoolean;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;

/**
 *
 */
@Component
public class LockRepositoryImpl implements LockRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final JdbcTemplate jdbcTemplate;
    private final CoreConfiguration configuration;

    private final CmCache<Optional<ItemLockHolder>> itemLocksByItemId;

    public LockRepositoryImpl(DaoService dao, CoreConfiguration configuration, CacheService cacheService) {
        logger.info("init");
        this.dao = checkNotNull(dao);
        this.jdbcTemplate = dao.getJdbcTemplate();
        this.configuration = checkNotNull(configuration);
        itemLocksByItemId = cacheService.newCache("item_lock_cache");
    }

    @ScheduledJob("0 10 * * * ?")//once per hour
    public void cleanupLockTable() {
        int res = jdbcTemplate.update("DELETE FROM \"_Lock\" WHERE \"LastActiveDate\" + format('%s seconds', \"TimeToLive\")::interval < now()");
        logger.debug("deleteted {} expired locks", res);
    }

    @Override
    public ItemLockAquireResponse aquireLock(ItemLock itemLock) {
        ItemLockHolder currentHolder = getLockHolderByItemIdOrNull(itemLock.getItemId());
        if (currentHolder == null || currentHolder.getLastPersistedDate().until(now(), SECONDS) > configuration.getLockCardPersistDelay() || !currentHolder.getItemLock().isCompatibleWith(itemLock)) {
            logger.debug("current holder is missing or expired or incompatible, try to aquire from db");
            return aquireLockOnDb(itemLock);
        } else {
            return new ItemLockAquireResponseImpl(true, currentHolder.getItemLock());
        }
    }

    private ItemLockAquireResponse aquireLockOnDb(ItemLock itemLock) {
        if (isNullOrLtEqZero(itemLock.getTimeToLiveSeconds())) {
            itemLock = ItemLockImpl.copyOf(itemLock).withTimeToLiveSeconds(configuration.getLockCardTimeOut()).build();
        }
        return jdbcTemplate.queryForObject("SELECT * FROM _cm3_lock_aquire_try(?,?,?,?,?)", (r, i) -> {
            boolean aquired = toBoolean(r.getObject("is_aquired"));
            long lockId = toLong(r.getObject("lock_id"));
            ItemLock actualLock = dao.getById(ItemLock.class, lockId).toModel();
            itemLocksByItemId.put(actualLock.getItemId(), Optional.of(new ItemLockHolder(actualLock, actualLock.getLastActiveDate())));
            return new ItemLockAquireResponseImpl(aquired, actualLock);
        }, itemLock.getItemId(), itemLock.getSessionId(), itemLock.getRequestId(), serializeLockScope(itemLock.getScope()), itemLock.getTimeToLiveSeconds());
    }

    @Override
    @Nullable
    public ItemLock getLockByItemIdOrNull(String itemId) {
        logger.trace("get lock for item id = {}", itemId);
        return Optional.ofNullable(getLockHolderByItemIdOrNull(itemId)).map(ItemLockHolder::getItemLock).orElse(null);
    }

    @Override
    public List<ItemLock> getAllLocks() {
        logger.debug("get all locks");
        return dao.selectAll().from(ItemLock.class).asList();
    }

    @Override
    public void removeAllLocks() {
        logger.debug("remove all locks");
        jdbcTemplate.update("DELETE FROM \"_Lock\"");
        itemLocksByItemId.invalidateAll();
    }

    @Override
    public void removeLock(ItemLock lock) {
        logger.debug("remove lock = {}", lock);
        jdbcTemplate.update("DELETE FROM \"_Lock\" WHERE \"ItemId\" = ?", lock.getItemId());
        itemLocksByItemId.invalidate(lock.getItemId());
    }

    @Nullable
    private ItemLockHolder getLockHolderByItemIdOrNull(String itemId) {
        return itemLocksByItemId.get(itemId, () -> Optional.ofNullable(doGetLockHolderByItemIdOrNull(itemId))).filter(not(ItemLockHolder::isExpired)).orElse(null);
    }

    @Nullable
    private ItemLockHolder doGetLockHolderByItemIdOrNull(String itemId) {
        ItemLock itemLock = doGetLockByItemId(itemId);
        return itemLock == null ? null : new ItemLockHolder(itemLock, itemLock.getLastActiveDate());
    }

    @Nullable
    private ItemLock doGetLockByItemId(String itemId) {
        return dao.selectAll().from(ItemLock.class).where("ItemId", EQ, itemId).getOneOrNull();
    }

    private class ItemLockHolder {

        private final ItemLock itemLock;
        private final ZonedDateTime lastPersistedDate;

        public ItemLockHolder(ItemLock itemLock, ZonedDateTime lastPersistedDate) {
            this.itemLock = checkNotNull(itemLock);
            this.lastPersistedDate = checkNotNull(lastPersistedDate);
        }

        public ItemLock getItemLock() {
            return itemLock;
        }

        public ZonedDateTime getLastPersistedDate() {
            return lastPersistedDate;
        }

        public boolean isExpired() {
            return itemLock.getLastActiveDate().plusSeconds(itemLock.getTimeToLiveSeconds()).isBefore(now());
        }

    }

    private static class ItemLockAquireResponseImpl implements ItemLockAquireResponse {

        private final boolean isAquired;
        private final ItemLock itemLock;

        public ItemLockAquireResponseImpl(boolean isAquired, ItemLock itemLock) {
            this.isAquired = isAquired;
            this.itemLock = checkNotNull(itemLock);
        }

        @Override
        public boolean isAquired() {
            return isAquired;
        }

        @Override
        public ItemLock getLock() {
            return itemLock;
        }
    }

}
