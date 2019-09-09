package org.cmdbuild.config;

import java.util.List;

import java.util.Set;
import javax.annotation.Nullable;
import static org.cmdbuild.common.http.HttpConst.WFY_PASSTOKEN_DEFAULT;
import org.springframework.stereotype.Component;
import org.cmdbuild.config.api.ConfigValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.config.api.ConfigComponent;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import static org.cmdbuild.utils.lang.CmNullableUtils.ltEqZeroToNull;

@Component("coreConfig")
@ConfigComponent("org.cmdbuild.core")
public class CoreConfigurationImpl implements CoreConfiguration {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String STARTING_CLASS = "startingclass";
    private static final String RELATION_LIMIT = "relationlimit";
    private static final String LANGUAGE = "language";
//    private static final String POPUP_PERCENTAGE_HEIGHT = "popuppercentageheight";
//    private static final String POPUP_PERCENTAGE_WIDTH = "popuppercentagewidth";
//    private static final String GRID_CARD_RATIO = "grid_card_ratio";
//    private static final String ROW_LIMIT = "rowlimit";
    private static final String LANGUAGE_PROMPT = "languageprompt";
    private static final String SESSION_TIMEOUT = "session.timeout";
    private static final String INSTANCE_NAME = "instance_name";
    private static final String TABS_POSITION = "card_tab_position";
    private static final String LOCK_CARD = "lockcardenabled";
    private static final String LOCKER_CARD_USER_VISIBLE = "lockcarduservisible";
    private static final String LOCK_CARD_TIME_OUT = "lockcardtimeout";
    private static final String ENABLED_LANGUAGES = "enabled_languages";
    private static final String LOGIN_LANGUAGES = "login_languages";
    private static final String LOGOUT_REDIRECT = "logout.redirect";
    private static final String IMPORT_CSV_ONE_BY_ONE = "import_csv.one_by_one";
    private static final String DEMO_MODE_ADMIN = "demomode";

    @ConfigValue(key = "housekeeping.at_startup.enabled", description = "enable housekeeping run at startup", defaultValue = TRUE)
    private boolean runDatabaseHousekeepingFunctionAtStartup;

    @ConfigValue(key = "wfy.enabled", description = "enable working-for-you mode (redirect all requests to wfy page unless the request attach valid 'CMDbuild-WFYpasstoken', as header or cookie)", defaultValue = FALSE)
    private boolean wfyEnabled;

    @ConfigValue(key = "wfy.passtoken", description = "wfy pass token, to be used when in wfy model; YOU SHOULD CHANGE THIS VALUE while running wfy mode on a production node", defaultValue = WFY_PASSTOKEN_DEFAULT)
    private String wfyPasstoken;
//
//    @ConfigValue(key = "referencecombolimit", description = "reference combo limit", defaultValue = "500")
//    private String referenceComboLimit;

    @ConfigValue(key = STARTING_CLASS, description = "", defaultValue = "")
    private String startingClass;

    @ConfigValue(key = RELATION_LIMIT, description = "", defaultValue = "20")
    private int relationLimit;

    @ConfigValue(key = LANGUAGE, description = "", defaultValue = "en")
    private String language;

//    @ConfigValue(key = POPUP_PERCENTAGE_HEIGHT, description = "", defaultValue = "80")
//    private String popupPercentageHeight;
//
//    @ConfigValue(key = POPUP_PERCENTAGE_WIDTH, description = "", defaultValue = "80")
//    private String popupPercentageWidth;
//    @ConfigValue(key = GRID_CARD_RATIO, description = "", defaultValue = "50")
//    private String gridCardRation;
//
//    @ConfigValue(key = ROW_LIMIT, description = "", defaultValue = "20")
//    private String rowLimit;
    @ConfigValue(key = INSTANCE_NAME, description = "", defaultValue = "")
    private String instanceName;

    @ConfigValue(key = TABS_POSITION, description = "", defaultValue = "bottom")
    private String tabsPosition;

    @ConfigValue(key = LOGOUT_REDIRECT, description = "", defaultValue = "")
    private String logoutRedirect;

    @ConfigValue(key = DEMO_MODE_ADMIN, description = "", defaultValue = "")
    private String demoModeAdmin;

    @ConfigValue(key = LANGUAGE_PROMPT, description = "", defaultValue = TRUE)
    private boolean languagePropmpt;

    @ConfigValue(key = LOCK_CARD, description = "", defaultValue = FALSE)
    private boolean lockCard;

    @ConfigValue(key = LOCKER_CARD_USER_VISIBLE, description = "", defaultValue = TRUE)
    private boolean lockerCardUserVisible;

    @ConfigValue(key = IMPORT_CSV_ONE_BY_ONE, description = "", defaultValue = FALSE)
    private boolean importCsvOneByOne;

    @ConfigValue(key = SESSION_TIMEOUT, description = "session timeout in seconds", defaultValue = "3600")
    private int sessionTimeout;

    @ConfigValue(key = "session.persist.delay", description = "max delay from last activity before updating session last active date on db (note: this apply only when no session data was modified, but only lastActive date needs to be updated)", defaultValue = "600")
    private int sessionPersistDelay;

    @ConfigValue(key = "session.activeSessionPeriodForStatistics", description = "amount of time in seconds for which a session is considered 'active' (only for the purpose of statistics and error collection); useful if session.timeout is a big value (days or more)", defaultValue = "1200")
    private int sessionActivePeriodForStatistics;

    @ConfigValue(key = LOCK_CARD_TIME_OUT, description = "", defaultValue = "300")
    private int lockCardTimeout;

//    @ConfigValue(key = "noteInline", description = "", defaultValue = FALSE)
//    private boolean noteInline;//TODO use this
//
//    @ConfigValue(key = "noteInlineClosed", description = "", defaultValue = FALSE)
//    private boolean noteInlineClosed;//TODO use this
//
//    @ConfigValue(key = "gridAutorefresh", description = "", defaultValue = FALSE)
//    private boolean gridAutorefresh;//TODO use this
//
//    @ConfigValue(key = "gridAutorefreshFrequency", description = "", defaultValue = "300")
//    private int gridAutorefreshFrequency;//TODO use this
    @ConfigValue(key = "enableConfigUpdate", description = "enable system config update via rest ws and interface (config update will still be possible on db or via config files)", defaultValue = TRUE)
    private boolean enableConfigUpdate;

    @ConfigValue(key = "trustedKeys", description = "trusted RSA public keys (list of keys in openssl format); these keys may be used to perform login with any username", defaultValue = "")
    private List<String> trustedKeys;

    @ConfigValue(key = ENABLED_LANGUAGES, description = "languages enabled for CM objects translation", defaultValue = "")
    private Set<String> enabledLanguages;

    @ConfigValue(key = LOGIN_LANGUAGES, description = "languages enabled for user login", defaultValue = "")
    private Set<String> loginLanguages;

    @ConfigValue(key = "disableReplayAttackCheck", description = "disable reply attack check (useful for soap ws debug)", defaultValue = TRUE)//TODO this var is not refreshed after config is ready, so only the default is used; fix this somehow
    private boolean disableReplayAttackCheck;

    @ConfigValue(key = "enableMultigrupByDefault", description = "enable user multigroup by default", defaultValue = FALSE)
    private boolean enableMultigrupByDefault;

    @ConfigValue(key = "showInfoAndWarningMessages", description = "show info and warning messages", defaultValue = TRUE)
    private boolean showInfoAndWarningMessages;

    @ConfigValue(key = "companyLogo", description = "company logo (uploads id)", defaultValue = "")
    private Long companyLogo;

    @ConfigValue(key = "cardlock.enabled", description = "", defaultValue = FALSE)
    private Boolean cardlockEnabled;

    @ConfigValue(key = "cardlock.showuser", description = "", defaultValue = FALSE)
    private Boolean cardlockShowUser;

    @Override
    public int getRelationLimit() {
        return relationLimit;
    }

    @Override
    public boolean getCardlockEnabled() {
        return cardlockEnabled;
    }

    @Override
    public boolean getCardlockShowUser() {
        return cardlockShowUser;
    }

//TODO value validation
//		checkArgument(getSessionTimeoutOrDefault() > getSessionPersistDelay() * 2, "session persist delay should be (quite) less than session timeout");
//		checkArgument(getLockCardTimeOut() > getLockCardPersistDelay() * 2, "lock card persist delay should be (quite) less than lock timeout");
    @Override
    public boolean runDatabaseHousekeepingFunctionAtStartup() {
        return runDatabaseHousekeepingFunctionAtStartup;
    }

    @Override
    public boolean showInfoAndWarningMessages() {
        return showInfoAndWarningMessages;
    }

    @Override
    public int getSessionPersistDelay() {
        return sessionPersistDelay;
    }

    @Override
    public boolean enableMultigrupByDefault() {
        return enableMultigrupByDefault;
    }

    @Override
    public boolean disableReplayAttackCheck() {
        return disableReplayAttackCheck;
    }

    @Override
    public boolean isWorkingForYouModeEnabled() {
        return wfyEnabled;
    }

    @Override
    public String getWorkingForYouModePasstoken() {
        return wfyPasstoken;
    }

    @Override
    public boolean allowConfigUpdateViaWs() {
        return enableConfigUpdate;
    }

    @Override
    public String getDefaultLanguage() {
        return language;
    }

    @Override
    public boolean useLanguagePrompt() {
        return languagePropmpt;
    }

    @Override
    public String getStartingClassName() {
        return startingClass;
    }

    @Override
    public String getDemoModeAdmin() {
        return demoModeAdmin;
    }

    @Override
    public String getInstanceName() {
        return instanceName;
    }

    @Override
    public String getTabsPosition() {
        return tabsPosition;
    }

    @Override
    public int getSessionTimeoutOrDefault() {
        return sessionTimeout;
    }

    @Override
    public boolean getLockCard() {
        return lockCard;
    }

    @Override
    public boolean getLockCardUserVisible() {
        return lockerCardUserVisible;
    }

    @Override
    public int getLockCardTimeOut() {
        return lockCardTimeout;
    }

    @Override
    public Set<String> getEnabledLanguages() {
        return enabledLanguages;
    }

    @Override
    public Set<String> getLoginLanguages() {
        return loginLanguages;
    }

    @Override
    public String getRedirectOnLogout() {
        return logoutRedirect;
    }

    @Override
    public boolean isImportCsvOneByOne() {
        return importCsvOneByOne;
    }

    @Override
    public int getSessionActivePeriodForStatistics() {
        return sessionActivePeriodForStatistics;
    }

    @Override
    public List<String> getTrustedKeys() {
        return trustedKeys;
    }

    @Override
    @Nullable
    public Long getCompanyLogoUploadsId() {
        return ltEqZeroToNull(companyLogo);
    }

}
