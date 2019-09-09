/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.jobs;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.auth.login.LoginDataImpl;
import org.cmdbuild.auth.session.SessionService;
import static org.cmdbuild.jobs.JobExecutorService.JOBUSER_NOBODY;
import static org.cmdbuild.jobs.JobExecutorService.JOBUSER_SYSTEM;
import org.cmdbuild.requestcontext.RequestContextService;
import static org.cmdbuild.utils.hash.CmHashUtils.compact;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.springframework.stereotype.Component;

@Component
public class JobSessionServiceImpl implements JobSessionService {

    private final RequestContextService requestContextService;
    private final SessionService sessionService;

    public JobSessionServiceImpl(RequestContextService requestContextService, SessionService sessionService) {
        this.requestContextService = checkNotNull(requestContextService);
        this.sessionService = checkNotNull(sessionService);
    }

    @Override
    public void createJobSessionContext(String user, String reqCtxId) {
        requestContextService.initCurrentRequestContext(compact(reqCtxId, 50));
        createSession(user);
    }

    @Override
    public void destroyJobSessionContext() {
        sessionService.deleteCurrentSessionIfExists();
        requestContextService.destroyCurrentRequestContext();//TODO destroy current session, if any
    }

    private void createSession(String user) {
        switch (checkNotBlank(user)) {
            case JOBUSER_NOBODY:
                break;
            case JOBUSER_SYSTEM:
                //TODO do not open a new session each time - use singleton session
                sessionService.createAndSet(LoginDataImpl.builder().withLoginString("cmgod").withNoPasswordRequired().build()); //TODO use other fake system user - not cmgod
                break;
            default:
                sessionService.createAndSet(LoginDataImpl.builder().withLoginString(user).withNoPasswordRequired().build());
        }
    }

}
