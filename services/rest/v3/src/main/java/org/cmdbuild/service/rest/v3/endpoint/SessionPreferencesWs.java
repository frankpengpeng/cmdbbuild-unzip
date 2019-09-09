package org.cmdbuild.service.rest.v3.endpoint;

//import com.google.common.base.Optional;
import org.cmdbuild.service.rest.v3.helpers.SessionWsCommons;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.auth.session.model.Session;
import org.cmdbuild.auth.user.OperationUser;
import org.cmdbuild.config.CoreConfiguration;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.ID;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.userconfig.UserConfigService;

@Path("sessions/{" + ID + "}/preferences")//it is actually user preferences
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class SessionPreferencesWs extends SessionWsCommons {

    private final UserConfigService userPreferencesStore;
    private final CoreConfiguration coreConfiguration;

    public SessionPreferencesWs(SessionService sessionService, UserConfigService userPreferencesStore, CoreConfiguration coreConfiguration) {
        super(sessionService);
        this.userPreferencesStore = checkNotNull(userPreferencesStore);
        this.coreConfiguration = checkNotNull(coreConfiguration);
    }

    @GET
    @Path(EMPTY)
    public Object read(@PathParam(ID) String sessionId) {
        logger.debug("read preferences for session = {}", sessionId);
        sessionId = sessionIdOrCurrent(sessionId);
        //TODO validate sessionId = current session id OR isAdmin()
        Session session = sessionService.getSessionById(sessionId);
        Map<String, Object> data = getUserConfig(session.getOperationUser());
        return response(data);
    }

    @GET
    @Path("{key}")
    public String getUserConfig(@PathParam(ID) String sessionId, @PathParam("key") String key) {
        sessionId = sessionIdOrCurrent(sessionId);
        //TODO validate sessionId = current session id OR isAdmin()
        Session session = sessionService.getSessionById(sessionId);
        return userPreferencesStore.getByUsernameOrNull(session.getOperationUser().getLoginUser().getUsername(), key);
    }

    @PUT
    @Path("{key}")
    @Consumes(TEXT_PLAIN)
    public Object updateUserConfigValue(@PathParam(ID) String sessionId, @PathParam("key") String key, String value) {
        checkNonSystem(key);
        sessionId = sessionIdOrCurrent(sessionId);
        //TODO validate sessionId = current session id OR isAdmin()
        Session session = sessionService.getSessionById(sessionId);
        userPreferencesStore.setByUsername(session.getOperationUser().getLoginUser().getUsername(), key, value);
        return success();
    }

    @POST
    @Path("")
    public Object updateUserConfigValues(@PathParam(ID) String sessionId, Map<String, String> data) {
        logger.info("update user preferences with data = {}", data);
        sessionId = sessionIdOrCurrent(sessionId);
        //TODO validate sessionId = current session id OR isAdmin()
        Session session = sessionService.getSessionById(sessionId);
        userPreferencesStore.setByUsername(session.getOperationUser().getUsername(), data);
        return response(getUserConfig(session.getOperationUser()));
    }

    @DELETE
    @Path("{key}")
    public Object deleteSystemConfigValue(@PathParam(ID) String sessionId, @PathParam("key") String key) {
        checkNonSystem(key);
        sessionId = sessionIdOrCurrent(sessionId);
        //TODO validate sessionId = current session id OR isAdmin()
        Session session = sessionService.getSessionById(sessionId);
        userPreferencesStore.deleteByUsername(session.getOperationUser().getLoginUser().getUsername(), key);
        return success();
    }

    private void checkNonSystem(String key) {
        checkArgument(!key.startsWith("cm_ui_"), "cannot set system preferences");//TODO move this to user pref service
    }

    private Map<String, Object> getUserConfig(OperationUser operationUser) {
        Map<String, String> userConfig = userPreferencesStore.getByUsername(operationUser.getUsername());//TODO merge lang and starting class from here
        String initialPage = userConfig.get("cm_user_initialPage");
        if (initialPage == null && operationUser.hasDefaultGroup() && operationUser.getDefaultGroup().hasStartingClass()) {
            initialPage = operationUser.getDefaultGroup().getStartingClass();
        }
        if (initialPage == null) {
            initialPage = coreConfiguration.getStartingClassName();
        }
        return (Map) map()
                .with("cm_ui_startingClass", initialPage)
                .accept((m) -> {
                    if (operationUser.hasDefaultGroup()) {
                        m.put(
                                //								"cm_ui_disabledModules", configuration.getDisabledModules(),
                                //								"cm_ui_disabledCardTabs", configuration.getDisabledCardTabs(),
                                //								"cm_ui_disabledProcessTabs", configuration.getDisabledProcessTabs(),
                                //								"cm_ui_hideSidePanel", configuration.isHideSidePanel(),
                                //								"cm_ui_fullScreenMode", configuration.isFullScreenMode(),
                                //								"cm_ui_simpleHistoryModeForCard", configuration.isSimpleHistoryModeForCard(),
                                //								"cm_ui_simpleHistoryModeForProcess", configuration.isSimpleHistoryModeForProcess(),
                                "cm_ui_processWidgetAlwaysEnabled", operationUser.getDefaultGroup().getConfig().getProcessWidgetAlwaysEnabled());
                    }
                })
                .with(userConfig);
    }
}
