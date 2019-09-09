/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.api;

import static com.google.common.base.Preconditions.checkNotNull;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.client.rest.core.RestServiceClient;
import org.cmdbuild.clustering.ClusterNode;
import org.cmdbuild.config.api.ConfigDefinition;
import org.cmdbuild.services.SystemStatus;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.trimAndCheckNotBlank;
import org.cmdbuild.debuginfo.BugReportInfo;
import org.cmdbuild.jobs.JobRun;
import org.cmdbuild.log.LogService.LogLevel;
import org.cmdbuild.services.MinionStatus;

public interface SystemApi extends RestServiceClient {

    JobRun getJobRun(String jobId, long runId);

    default JobRun getJobRun(long runId) {
        return getJobRun("_ANY", runId);
    }

    List<JobRun> getLastJobRuns(String jobId, long limit);

    List<JobRun> getLastJobErrors(String jobId, long limit);

    List<JobRun> getLastJobRuns(long limit);

    List<JobRun> getLastJobErrors(long limit);

    SystemStatus getStatus();

    Map<String, String> getSystemInfo();

    List<ServiceStatusInfo> getServicesStatus();

    ClusterStatus getClusterStatus();

    void sendBroadcast(String message);

    interface ServiceStatusInfo {

        String getServiceName();

        MinionStatus getServiceStatus();
    }

    List<LoggerInfo> getLoggers();

    List<PatchInfo> getPatches();

    void upgradeWebapp(InputStream warFileData);

    void recreateDatabase(InputStream dumpFileData, boolean freezesessions);

    default void recreateDatabase(InputStream dumpFileData) {
        recreateDatabase(dumpFileData, false);
    }

    void stop();

    void restart();

    void applyPatches();

    void importFromDms();

    void setLogger(LoggerInfo logger);

    default void setLogger(String category, String level) {
        setLogger(new SimpleLoggerInfo(category, level));
    }

    void deleteLogger(String category);

    Future<Void> streamLogMessages(Consumer<LogMessage> listener);

    void reloadConfig();

    void dropAllCaches();

    void reload();

    void dropCache(String cacheId);

    byte[] dumpDatabase();

    void reconfigureDatabase(Map<String, String> config);

    byte[] downloadDebugInfo();

    default BugReportInfo sendBugReport() {
        return sendBugReport(null);
    }

    BugReportInfo sendBugReport(@Nullable String message);

    interface LoggerInfo {

        String getCategory();

        String getLevel();
    }

    @Nullable
    Object eval(String script);

    interface PatchInfo {

        String getCategory();

        String getName();

        String getDescription();
    }

    interface LogMessage {

        LogLevel getLevel();

        String getMessage();

        String getLine();

        ZonedDateTime getTimestamp();

        @Nullable
        String getStacktrace();

        default boolean hasStacktrace() {
            return isNotBlank(getStacktrace());
        }
    }

    Map<String, String> getConfig();

    Map<String, ConfigDefinition> getConfigDefinitions();

    String getConfig(String key);

    SystemApi setConfig(String key, String value);

    SystemApi setConfigs(Map<String, String> data);

    default SystemApi setConfigs(Object... items) {
        return setConfigs(map(items));
    }

    SystemApi deleteConfig(String key);

    interface ClusterStatus {

        boolean isRunning();

        List<ClusterNode> getNodes();
    }

    static class SimpleLoggerInfo implements LoggerInfo {

        private final String category, level;

        public SimpleLoggerInfo(String category, String level) {
            this.category = trimAndCheckNotBlank(category);
            this.level = trimAndCheckNotBlank(level);
        }

        @Override
        public String getCategory() {
            return category;
        }

        @Override
        public String getLevel() {
            return level;
        }

        @Override
        public String toString() {
            return "SimpleLogger{" + "category=" + category + ", level=" + level + '}';
        }

    }

    static class SimplePatchInfo implements PatchInfo {

        private final String name, description, category;

        public SimplePatchInfo(String name, String description, String category) {
            this.name = trimAndCheckNotBlank(name);
            this.description = checkNotNull(description);
            this.category = trimAndCheckNotBlank(category);
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public String getCategory() {
            return category;
        }

        @Override
        public String toString() {
            return "SimplePatchInfo{" + "name=" + name + ", description=" + description + ", category=" + category + '}';
        }

    }
}
