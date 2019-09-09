/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.session;

import static com.google.common.base.Objects.equal;
import org.cmdbuild.auth.session.inner.CurrentSessionHolder;
import org.cmdbuild.auth.session.inner.SessionDataService;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.Subscribe;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import org.cmdbuild.audit.RequestEventService;
import org.cmdbuild.audit.RequestEventService.RequestCompleteEvent;
import org.cmdbuild.auth.user.OperationUserStore;
import org.cmdbuild.auth.user.OperationUser;
import static org.cmdbuild.auth.user.OperationUserImpl.anonymousOperationUser;
import org.cmdbuild.auth.user.OperationUserStack;
import org.cmdbuild.auth.login.LoginDataImpl;
import org.springframework.stereotype.Component;
import static org.cmdbuild.auth.session.model.SessionImpl.builder;
import static org.cmdbuild.auth.session.model.SessionImpl.copyOf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.auth.session.model.Session;
import org.cmdbuild.auth.session.model.SessionData;
import org.cmdbuild.auth.login.LoginData;
import static org.cmdbuild.utils.random.CmRandomUtils.DEFAULT_RANDOM_ID_SIZE;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import org.cmdbuild.auth.session.dao.SessionRepository;
import org.cmdbuild.auth.login.AuthenticationService;
import static org.cmdbuild.auth.role.RolePrivilege.RP_IMPERSONATE_ALL;
import static org.cmdbuild.auth.session.SessionExpirationStrategy.ES_DEFAULT;
import org.cmdbuild.auth.session.model.SessionImpl;
import static org.cmdbuild.utils.date.CmDateUtils.now;

@Component
public class SessionServiceImpl implements SessionService {

    public static final int SESSION_TOKEN_SIZE = DEFAULT_RANDOM_ID_SIZE;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CurrentSessionHolder currentSessionIdHolder;
    private final SessionRepository sessionRepository;
    private final OperationUserStore userStore;
    private final AuthenticationService authenticationService;
    private final SessionDataService sessionDataService;

    public SessionServiceImpl(CurrentSessionHolder currentSessionHolder, CurrentSessionHolder currentSessionIdHolder, SessionRepository sessionStore, OperationUserStore userStore, AuthenticationService authenticationLogic, RequestEventService requestEventService, SessionDataService sessionDataService) {
        this.sessionRepository = checkNotNull(sessionStore);
        this.userStore = checkNotNull(userStore);
        this.authenticationService = checkNotNull(authenticationLogic);
        this.sessionDataService = checkNotNull(sessionDataService);
        this.currentSessionIdHolder = checkNotNull(currentSessionIdHolder);
        requestEventService.getEventBus().register(new Object() {
            @Subscribe
            public void handleRequestCompleteEvent(RequestCompleteEvent event) {
                Session session = getCurrentSessionOrNull();
                if (session != null) {
                    updateSession(session);
                }
            }
        });

    }

    @Override
    public void validateSessionId(String sessionId) {
        getSessionById(sessionId);//TODO replace with more efficent query
    }

    @Override
    public Session getSessionById(String sessionId) {
        return checkNotNull(getSessionByIdOrNull(sessionId), "cannot find session for id = %s", sessionId);
    }

    @Override
    public List<Session> getAllSessions() {
        return sessionRepository.getAllSessions();
    }

    @Override
    public @Nullable
    Session getSessionByIdOrNull(String sessionId) {
        return sessionRepository.getSessionByIdOrNull(sessionId);
    }

    @Override
    public int getActiveSessionCount() {
        return sessionRepository.getActiveSessionCount();
    }

    @Override
    public String create(LoginData login) {
        OperationUser user = authenticationService.validateCredentialsAndCreateOperationUser(login);
        String sessionId = createSessionToken();
        updateSession(sessionId, user);
        return sessionId;
    }

    @Override
    public void update(String sessionId, LoginData login) {
        Session session = getSessionById(sessionId);
        OperationUser user = authenticationService.updateOperationUser(login, session.getOperationUser());
        session = updateSession(sessionId, user);
        if (equal(sessionId, getCurrentSessionIdOrNull())) {
            userStore.setUser(session.getOperationUser());
        }
    }

    @Override
    public boolean exists(String id) {
        try {
            getSessionById(id); //TODO more efficient query; do not rely on exception for control flow
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void deleteSession(String sessionId) {
        sessionRepository.deleteSession(sessionId);
        //TODO release all session lock (use event trigger)
    }

    @Override
    public void deleteAll() {
        sessionRepository.deleteAll();
    }

    @Override
    public void impersonate(String username, @Nullable String group) {
        Session session = getCurrentSession();
        OperationUserStack operationUserStack = session.getOperationUser();
        checkArgument(canImpersonateOtherUsers(operationUserStack.getRootOperationUser()), "cannot impersonate from current user = %s", operationUserStack);
        OperationUser imp = authenticationService.validateCredentialsAndCreateOperationUser(LoginDataImpl.builder().withLoginString(username).withNoPasswordRequired().withServiceUsersAllowed(true).withGroupName(group).build());
        session = sessionRepository.updateSession(copyOf(session).impersonate(imp).build());
        userStore.setUser(session.getOperationUser());
    }

    @Override
    public void deimpersonate() {
        Session session = sessionRepository.updateSession(copyOf(getCurrentSession()).deImpersonate().build());
        userStore.setUser(session.getOperationUser());
    }

    @Override
    public @Nullable
    String getCurrentSessionIdOrNull() {
        return currentSessionIdHolder.getOrNull();
    }

    @Override
    public void setCurrent(String id) {
        currentSessionIdHolder.set(id);
        userStore.setUser((id == null) ? null : getUserOrAnonymousWhenMissing(id));
    }

    @Override
    public Session getCurrentSession() {
        return getSessionById(checkNotNull(getCurrentSessionIdOrNull(), "current session not set"));
    }

    @Override
    public @Nullable
    Session getCurrentSessionOrNull() {
        String sessionId = getCurrentSessionIdOrNull();
        return sessionId == null ? null : getSessionByIdOrNull(sessionId);
    }

    @Override
    public void updateCurrentSession(Function<Session, Session> fun) {
        Session session = getCurrentSessionOrNull();
        if (session == null) {
            logger.warn("cannot update current session: current session is not available");
        } else {
            session = fun.apply(session);
            updateSession(session);
        }
    }

    @Override
    public boolean sessionExistsAndHasDefaultGroup(String sessionId) {
        return (sessionId == null) ? false : getUserOrAnonymousWhenMissing(sessionId).hasDefaultGroup();
    }

    @Override
    public OperationUser getUser(String sessionId) {
        return getSessionById(sessionId).getOperationUser();
    }

    @Override
    public void setUser(String sessionId, OperationUser user) {
        sessionRepository.updateSession(copyOf(getSessionById(sessionId)).impersonate(user).build());
    }

    @Override
    public void updateSession(Session session) {
        logger.debug("update session = {}", session);
        sessionRepository.updateSession(SessionImpl.copyOf(session).withLastActiveDate(now()).build());
        if (equal(session.getSessionId(), getCurrentSessionIdOrNull())) {
            userStore.setUser(session.getOperationUser());
        }
    }

    @Override
    public SessionData getCurrentSessionDataSafe() {
        return sessionDataService.getCurrentSessionDataSafe();
    }

    private Session updateSession(String sessionId, OperationUser user) {
//        Map<String, String> userConfig = userConfigService.getByUsername(user.getUsername());//TODO check this
//        return sessionRepository.updateSession(builder().withSessionId(sessionId).withOperationUser(user).withSessionData((Map) userConfig).build());
        return sessionRepository.updateSession(builder().withSessionId(sessionId).withOperationUser(user).withExpirationStrategy(ES_DEFAULT).build());
    }

    private String createSessionToken() {
        return randomId(SESSION_TOKEN_SIZE);
    }

    private boolean canImpersonateOtherUsers(OperationUser user) {
        return user.hasPrivileges(RP_IMPERSONATE_ALL);//TODO check this
    }

    private OperationUser getUserOrAnonymousWhenMissing(String sessionId) {
        Session session = getSessionByIdOrNull(sessionId);
        return session == null ? anonymousOperationUser() : session.getOperationUser();
    }

}
