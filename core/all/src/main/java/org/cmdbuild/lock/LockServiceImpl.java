package org.cmdbuild.lock;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Stopwatch;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.auth.session.model.Session;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.lock.LockRepository.ItemLockAquireResponse;
import org.cmdbuild.requestcontext.RequestContextService;
import static org.cmdbuild.utils.hash.CmHashUtils.hashIfLongerThan;
import static org.cmdbuild.utils.lang.CmExecutorUtils.sleepSafe;

@Component
public class LockServiceImpl implements LockService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final LockRepository lockStore;
    private final SessionService sessionService;
    private final RequestContextService requestContextService;

    public LockServiceImpl(LockRepository lockStore, SessionService sessionService, RequestContextService requestContextService) {
        this.lockStore = checkNotNull(lockStore);
        this.sessionService = checkNotNull(sessionService);
        this.requestContextService = checkNotNull(requestContextService);
    }

    @Override
    public LockResponse aquireLock(String itemId, LockScope lockScope) {
        logger.debug("aquire lock for item id = {}", itemId);
        itemId = checkItemIdAndShrinkIfNecessary(itemId);
        Session session = sessionService.getCurrentSession();
        String requestId = requestContextService.getRequestContextId();
        ItemLockAquireResponse response = lockStore.aquireLock(ItemLockImpl.builder()
                .withItemId(itemId)
                .withScope(lockScope)
                .withSessionId(session.getSessionId())
                .withRequestId(requestId)
                .build());
        if (response.isAquired()) {
            return new SuccessfulLockResponse(response.getLock());
        } else {
            return new UnsuccessfullLockResponse();
        }
    }

    @Override
    public LockResponse aquireLock(String itemId, LockScope lockScope, long waitForMillis) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        LockResponse response = aquireLock(itemId, lockScope);
        while (!response.isAquired() && stopwatch.elapsed(TimeUnit.MILLISECONDS) < waitForMillis) {
            sleepSafe(1000);
            response = aquireLock(itemId, lockScope);
        }
        return response;
    }

    @Override
    @Nullable
    public ItemLock getLockOrNull(String itemId) {
        logger.trace("get lock for item id = {}", itemId);
        itemId = checkItemIdAndShrinkIfNecessary(itemId);
        return lockStore.getLockByItemIdOrNull(itemId);
    }

    @Override
    public void releaseLock(ItemLock lock) {
        logger.debug("release lock = {}", lock);
        ItemLock currentLock = getNotLockedByOther(lock.getItemId());
        if (currentLock != null) {
            lockStore.removeLock(currentLock);
        }
    }

    @Override
    public void deleteLock(String lockId) {
        lockStore.removeLock(getLock(lockId));
    }

    private @Nullable
    ItemLock getNotLockedByOther(String itemId) {
        return checkNotLockedByOthers(getLockOrNull(itemId));
    }

    private @Nullable
    ItemLock checkNotLockedByOthers(@Nullable ItemLock lock) {
        if (lock != null) {
            checkArgument(!isLockedByOther(sessionService.getCurrentSession(), lock), "item = %s is locked by another session", lock.getItemId());
        }
        return lock;
    }

    private boolean isLockedByOther(Session session, ItemLock lock) {
        return !equal(lock.getSessionId(), session.getSessionId());
    }

    @Override
    public void releaseAllLocks() {
        logger.info("release all locks");
        lockStore.removeAllLocks();
    }

    @Override
    public List<ItemLock> getAllLocks() {
        logger.debug("get all locks");
        return lockStore.getAllLocks();
    }

    @Override
    public void requireNotLockedByOthers(String itemId) {
        logger.trace("requireNotLockedByOthers for item id = {}", itemId);
        itemId = checkItemIdAndShrinkIfNecessary(itemId);
        getNotLockedByOther(itemId);
    }

    @Override
    public void requireLockedByCurrent(String itemId) {
        logger.trace("requireLockedByCurrent for item id = {}", itemId);
        itemId = checkItemIdAndShrinkIfNecessary(itemId);
        aquireLockOrFail(itemId);
    }

    private String checkItemIdAndShrinkIfNecessary(String itemId) {
        return hashIfLongerThan(checkNotBlank(itemId), 50);
    }

    private static class UnsuccessfullLockResponse implements LockResponse {

        @Override
        public boolean isAquired() {
            return false;
        }

        @Override
        public ItemLock aquired() {
            throw new IllegalStateException("lock not aquired");
        }

    }

    private static class SuccessfulLockResponse implements LockResponse {

        private final ItemLock itemLock;

        public SuccessfulLockResponse(ItemLock itemLock) {
            this.itemLock = checkNotNull(itemLock);
        }

        @Override
        public boolean isAquired() {
            return true;
        }

        @Override
        public ItemLock aquired() {
            return itemLock;
        }

    }

}
