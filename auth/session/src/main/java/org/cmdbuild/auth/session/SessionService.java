package org.cmdbuild.auth.session;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.cmdbuild.auth.session.inner.SessionDataService;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.auth.session.model.Session;
import org.cmdbuild.auth.user.OperationUser;
import org.cmdbuild.auth.login.LoginData;
import org.cmdbuild.auth.session.model.SessionImpl;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface SessionService extends SessionDataService {

    default String createAndSet(LoginData login) {
        String session = create(login);
        setCurrent(session);
        return session;
    }

    String create(LoginData login);

    boolean exists(String id);

    void update(String id, LoginData login);

    void deleteSession(String sessionId);

    void deleteAll();

    void impersonate(String username, @Nullable String group);

    default void impersonate(String username) {
        impersonate(username, null);
    }

    void deimpersonate();

    @Nullable
    String getCurrentSessionIdOrNull();

    default String getCurrentSessionId() {
        return checkNotBlank(getCurrentSessionIdOrNull());
    }

    void setCurrent(String id);

    boolean sessionExistsAndHasDefaultGroup(String id);

    OperationUser getUser(String id);

    /**
     * mostly the same as impersonate
     *
     * @param id
     * @param user
     */
    void setUser(String id, OperationUser user);

    /**
     *
     * @param sessionId
     * @throws RuntimeException if session is not valid //TODO: throw different
     * exceptions for not found-expired
     */
    void validateSessionId(String sessionId);

    List<Session> getAllSessions();

    /**
     *
     * @param sessionId
     * @return session data
     * @throws RuntimeException if session not found for id
     */
    Session getSessionById(String sessionId);

    /**
     *
     * @param sessionId
     * @return session data, or null if not found
     */
    @Nullable
    Session getSessionByIdOrNull(String sessionId);

    /**
     * called to perform cleanup/persistence operation after processing a
     * request that may have changed session data; also mark session as active
     *
     * @param session
     */
    void updateSession(Session session);

    void updateCurrentSession(Function<Session, Session> fun);

    default void updateCurrentSessionData(Function<Map, Map> fun) {
        updateCurrentSession((s) -> SessionImpl.copyOf(s).withSessionData(fun.apply(s.getSessionData())).build());
    }

    /**
     * get current session; throw exception if no session available
     *
     * @return current session
     * @throws RuntimeException if no valid session is available
     */
    Session getCurrentSession();

    /**
     * return current session, or null if no valid session exists
     *
     * @return current session or null
     */
    @Nullable
    Session getCurrentSessionOrNull();

    int getActiveSessionCount();

    /**
     *
     * @return true if session existed and was deleted, false otherwise
     */
    default boolean deleteCurrentSessionIfExists() {
        String sessionId = getCurrentSessionIdOrNull();
        if (isNotBlank(sessionId)) {
            deleteSession(sessionId);
            return true;
        } else {
            return false;
        }
    }

}
