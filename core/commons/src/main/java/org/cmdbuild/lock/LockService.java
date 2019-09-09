package org.cmdbuild.lock;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import javax.annotation.Nullable;

/**
 * lock service
 *
 * itemId is an unique key for item on which to aquire lock; it may be used
 * as-is, or hashed/replaced (so the actual lock itemId is the one returned by {@link ItemLock#getItemId()
 * }, which may or may not be equal to the supplied itemId (but is guaranteed to
 * be unique and generated in a repeatable way)).
 *
 * @author davide
 */
public interface LockService {

    /**
     * aquire a lock on a previously unlocked item
     *
     * @param itemId
     * @return
     */
    default ItemLock aquireLockOrFail(String itemId) {
        return aquireLockOrFail(itemId, LockScope.LS_SESSION);
    }

    default ItemLock aquireLockOrFail(String itemId, LockScope lockScope) {
        return aquireLock(itemId, lockScope).aquired();
    }

    default LockResponse aquireLock(String itemId) {
        return aquireLock(itemId, LockScope.LS_SESSION);
    }

    LockResponse aquireLock(String itemId, LockScope lockScope);

    LockResponse aquireLock(String itemId, LockScope lockScope, long waitForMillis);

    default LockResponse aquireLockWaitALittle(String itemId, LockScope lockScope) {
        return aquireLock(itemId, lockScope, 30000);
    }

    @Nullable
    ItemLock getLockOrNull(String itemId);

    default ItemLock getLock(String itemId) {
        return checkNotNull(getLockOrNull(itemId), "lock not found for id = %s", itemId);
    }

    /**
     * renew an existing lock on item (currently synonim for aquireLock)
     *
     * @param itemId
     * @return
     */
    default ItemLock renewLock(String itemId) {
        return aquireLockOrFail(itemId);
    }

    /**
     * release lock (current user must be the owner of the lock, or admin).
     *
     * itemLock object must have been obtained from this service (you shouldn't
     * build your own itemLock instance and pass it to this methid)
     *
     * @param itemLock
     */
    void releaseLock(ItemLock itemLock);

    /**
     * release lock (current user must be the owner of the lock, or admin)
     *
     * @param itemId
     */
    default void releaseLock(String itemId) {
        ItemLock lock = getLockOrNull(itemId);
        if (lock != null) {
            releaseLock(lock);
        }
    }

    void deleteLock(String lockId);

    void releaseAllLocks();

    /**
     * return all locks (for admin ui only)
     *
     * @return list of all locks
     */
    List<ItemLock> getAllLocks();

    /**
     * throw exception if item is locked by someone else
     *
     * @param itemId
     */
    void requireNotLockedByOthers(String itemId);

    /**
     * throw exception if item is not locked by current user
     *
     * @param itemId
     */
    void requireLockedByCurrent(String itemId);

    static String itemIdFromCardId(Long cardId) {
        return "card_" + cardId.toString();
    }

    static String itemIdFromCardIdAndActivityId(Long cardId, String activityId) {
        return "activity_" + activityId + "_" + cardId.toString();
    }

    interface LockResponse {

        boolean isAquired();

        ItemLock aquired();
    }

}
