/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.lock;

import static com.google.common.base.Objects.equal;
import java.time.ZonedDateTime;
import static org.cmdbuild.lock.LockScope.LS_SESSION;

public interface ItemLock {

    String getItemId();

    String getSessionId();

    String getRequestId();

    ZonedDateTime getBeginDate();

    ZonedDateTime getLastActiveDate();

    int getTimeToLiveSeconds();

    LockScope getScope();

    default boolean isSessionScope() {
        return equal(LS_SESSION, getScope());
    }

    default boolean isCompatibleWith(ItemLock itemLock) {
        return equal(this.getScope(), itemLock.getScope()) && equal(this.getSessionId(), itemLock.getSessionId()) && (this.isSessionScope() || equal(this.getRequestId(), itemLock.getRequestId()));
    }

}
