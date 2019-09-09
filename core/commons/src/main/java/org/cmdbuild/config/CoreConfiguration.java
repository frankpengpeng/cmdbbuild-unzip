package org.cmdbuild.config;

import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import org.cmdbuild.common.localization.LanguageConfiguration;

public interface CoreConfiguration extends LanguageConfiguration {

    boolean runDatabaseHousekeepingFunctionAtStartup();

    boolean isWorkingForYouModeEnabled();

    String getWorkingForYouModePasstoken();

    boolean allowConfigUpdateViaWs();

    int getSessionPersistDelay();

    boolean disableReplayAttackCheck();

    boolean useLanguagePrompt();

    String getStartingClassName();

    String getDemoModeAdmin();

    String getInstanceName();

    String getTabsPosition();

    int getSessionTimeoutOrDefault();

    boolean getLockCard();

    boolean getLockCardUserVisible();

    boolean enableMultigrupByDefault();

    int getLockCardTimeOut();

    int getRelationLimit();

    /**
     * persist only locks with last persist date older than this (unless lock
     * data has changed). This is to avoid multiple write on db for repeated
     * lock acquisitions within the same method or method sequence; only the
     * first write will be committed, others will be discarded for
     * {@link #getLockCardPersistDelay()} seconds.
     *
     * @return
     */
    default int getLockCardPersistDelay() {
        return 10; //seconds
    }

    Set<String> getEnabledLanguages();

    Set<String> getLoginLanguages();

    String getRedirectOnLogout();

    boolean isImportCsvOneByOne();

    int getSessionActivePeriodForStatistics();

    List<String> getTrustedKeys();

    boolean showInfoAndWarningMessages();

    @Nullable
    Long getCompanyLogoUploadsId();

    boolean getCardlockEnabled();

    boolean getCardlockShowUser();
}
