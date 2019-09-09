/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.ImmutableList.toImmutableList;
import com.google.common.collect.Streams;
import org.cmdbuild.client.rest.core.RestWsClient;
import org.cmdbuild.client.rest.core.AbstractServiceClientImpl;
import static com.google.common.collect.Streams.stream;
import com.google.common.net.UrlEscapers;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.String.format;
import java.net.URI;
import java.time.ZonedDateTime;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import static org.apache.http.entity.ContentType.APPLICATION_FORM_URLENCODED;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.cmdbuild.audit.ErrorMessageDataImpl;
import org.cmdbuild.audit.ErrorMessagesData;
import org.cmdbuild.config.api.ConfigDefinition;
import org.cmdbuild.config.api.ConfigDefinitionImpl;
import org.cmdbuild.services.SystemStatus;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.trimAndCheckNotBlank;
import org.cmdbuild.client.rest.api.SystemApi;
import org.cmdbuild.clustering.ClusterNode;
import org.cmdbuild.clustering.ClusterNodeImpl;
import org.cmdbuild.common.error.ErrorOrWarningEvent;
import static org.cmdbuild.common.http.HttpConst.CMDBUILD_AUTHORIZATION_HEADER;
import org.cmdbuild.config.api.ConfigCategory;
import org.cmdbuild.config.api.ConfigLocation;
import org.cmdbuild.debuginfo.DebugInfoImpl;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.debuginfo.BugReportInfo;
import org.cmdbuild.jobs.JobRun;
import org.cmdbuild.jobs.JobRunStatus;
import org.cmdbuild.jobs.beans.JobRunImpl;
import org.cmdbuild.log.LogService.LogLevel;
import org.cmdbuild.services.MinionStatus;
import org.cmdbuild.utils.date.CmDateUtils;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_OBJECTS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.glassfish.tyrus.client.ClientManager;
import static org.cmdbuild.services.SystemServiceStatusUtils.parseMinionStatus;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_STRINGS;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;

public class SystemApiImpl extends AbstractServiceClientImpl implements SystemApi {

    public SystemApiImpl(RestWsClient restClient) {
        super(restClient);
    }

    private Boolean requireSession;

    @Override
    protected boolean isSessionTokenRequired() {
        return firstNonNull(requireSession, true);
    }

    @Override
    public JobRun getJobRun(String jobId, long runId) {
        JsonElement element = get(format("jobs/%s/runs/%s", encodeUrlPath(jobId), runId)).asJson();
        JsonObject data = element.getAsJsonObject().getAsJsonObject("data");
        return JobRunImpl.builder()
                .withId(toLong(data.get("_id")))
                .withJobCode(toString(data.get("jobCode")))
                .withJobStatus(parseEnum(toString(data.get("status")), JobRunStatus.class))
                .withCompleted(toBoolean(data.get("completed")))
                .withTimestamp(toDateTime(data.get("timestamp")))
                .withElapsedTime(toLong(data.get("elapsedMillis")))
                .withErrorMessageData(new ErrorMessagesData(stream(data.get("errors").getAsJsonArray()).map(JsonElement::getAsJsonObject) //TODO duplicate code, fix
                        .map((e) -> new ErrorMessageDataImpl(ErrorOrWarningEvent.ErrorEventLevel.valueOf(toString(e.get("level"))), toString(e.get("message")), toString(e.get("exception")))).collect(toList())))
                .build();
    }

    @Override
    public List<JobRun> getLastJobRuns(String jobId, long limit) {
        return parseResponse(get(format("jobs/%s/runs?limit=%s", encodeUrlPath(jobId), limit)).asJson());
    }

    @Override
    public List<JobRun> getLastJobErrors(String jobId, long limit) {
        return parseResponse(get(format("jobs/%s/errors?limit=%s", encodeUrlPath(jobId), limit)).asJson());
    }

    @Override
    public List<JobRun> getLastJobRuns(long limit) {
        return parseResponse(get(format("jobs/_ANY/runs?limit=%s", limit)).asJson());
    }

    @Override
    public List<JobRun> getLastJobErrors(long limit) {
        return parseResponse(get(format("jobs/_ANY/errors?limit=%s", limit)).asJson());
    }

    private List<JobRun> parseResponse(JsonElement reqResponse) {
        JsonArray requests = reqResponse.getAsJsonObject().getAsJsonArray("data");
        return Streams.stream(requests).map((JsonElement::getAsJsonObject)).map((data) -> {
            return JobRunImpl.builder()
                    .withId(toLong(data.get("_id")))
                    .withJobCode(toString(data.get("jobCode")))
                    .withJobStatus(parseEnum(toString(data.get("status")), JobRunStatus.class))
                    .withCompleted(toBoolean(data.get("completed")))
                    .withTimestamp(toDateTime(data.get("timestamp")))
                    .withElapsedTime(toLong(data.get("elapsedMillis")))
                    .withErrorMessages(emptyList())
                    .build();

        }).collect(toList());
    }

    @Override
    public SystemStatus getStatus() {
        try {
            requireSession = false; //this is not great. TODO: refactor this with aspectj or something
            logger.debug("getStatus");
            JsonElement response = get("boot/status").asJson();
            String systemStatusValue = trimAndCheckNotBlank(response.getAsJsonObject().get("status").getAsString(), "cannot find status code in response");
            return parseEnum(systemStatusValue, SystemStatus.class);
        } finally {
            requireSession = null;
        }
    }

    @Override
    public Map<String, String> getSystemInfo() {
        return fromJson(get("system/status").asJackson().get("data"), MAP_OF_STRINGS);
    }

    @Override
    public List<LoggerInfo> getLoggers() {
        logger.debug("getLoggers");
        return stream(get("system/loggers").asJson().getAsJsonObject().getAsJsonArray("data"))
                .map(JsonElement::getAsJsonObject)
                .map((record) -> new SimpleLoggerInfo(record.getAsJsonPrimitive("category").getAsString(), record.getAsJsonPrimitive("level").getAsString()))
                .collect(toList());
    }

    @Override
    public void setLogger(LoggerInfo loggerToUpdate) {
        logger.debug("updateLogger = {}", loggerToUpdate);
        post("system/loggers/" + trimAndCheckNotBlank(loggerToUpdate.getCategory()), trimAndCheckNotBlank(loggerToUpdate.getLevel()));
    }

    @Override
    public void deleteLogger(String category) {
        logger.debug("deleteLogger = {}", category);
        delete("system/loggers/" + trimAndCheckNotBlank(category));
    }

    @Override
    public Future<Void> streamLogMessages(Consumer<LogMessage> listener) {
        try {
            String sessionId = restClient().getSessionToken();
            ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().configurator(new ClientEndpointConfig.Configurator() {
                @Override
                public void beforeRequest(Map<String, List<String>> headers) {
                    headers.put(CMDBUILD_AUTHORIZATION_HEADER, singletonList(sessionId));
                }

            }).build();
            ClientManager client = ClientManager.createClient();
            CompletableFuture connectionReadyFuture = new CompletableFuture(), connectionClosedFuture = new CompletableFuture();
            client.connectToServer(new Endpoint() {

                @Override
                public void onOpen(Session session, EndpointConfig config) {
                    try {
                        logger.debug("bimserver session opened = {}", session.getId());
                    } catch (Exception ex) {
                        logger.error("error processing bimserver open session event", ex);
                    }

                    session.addMessageHandler(new MessageHandler.Whole<String>() {

                        @Override
                        public void onMessage(String msg) {
                            Map<String, Object> payload = fromJson(msg, MAP_OF_OBJECTS);
                            switch (nullToEmpty(toStringOrNull(payload.get("_event")))) {
                                case "socket.session.ok":
                                    connectionReadyFuture.complete(true);
                                    break;
                                case "log.message":
                                    LogMessage logMessage = new LogMessageImpl(payload);
                                    listener.accept(logMessage);
                                    break;
                                case "socket.error":
                                    ((CompletableFuture) connectionReadyFuture).completeExceptionally(runtime("error opening socket connection: %s", msg));
                                    //TODO close connection (?)
                                    break;
                            }
                        }

                    });
                    try {
                        session.getBasicRemote().sendText(toJson(map("_action", "socket.session.login", "token", sessionId)));
                    } catch (IOException ex) {
                        throw runtime(ex);
                    }
                }

                @Override
                public void onClose(Session session, CloseReason closeReason) {
                    logger.debug("websocket session closed, session = {}, reason = {}", session.getId(), closeReason);
                    connectionClosedFuture.complete(true);
                }

            }, cec, new URI(restClient().getServerUrl().replaceFirst("http", "ws") + "services/websocket/v1/main"));
            connectionReadyFuture.get(5, TimeUnit.SECONDS);
            post("system/loggers/stream", "");
            return connectionClosedFuture;
        } catch (Exception ex) {
            throw runtime(ex);
        }
    }

    @Override
    public Map<String, String> getConfig() {
        logger.debug("getConfig");
        Map<String, String> map = map();
        get("system/config").asJson().getAsJsonObject().getAsJsonObject("data").entrySet().forEach((entry) -> {
            map.put(entry.getKey(), toString(entry.getValue()));
        });
        return map;
    }

    @Override
    public Map<String, ConfigDefinition> getConfigDefinitions() {
        logger.debug("getConfigDefinitions");
        Map<String, ConfigDefinition> map = map();
        get("system/config?detailed=true").asJson().getAsJsonObject().getAsJsonObject("data").entrySet().forEach((entry) -> {
            JsonObject definition = entry.getValue().getAsJsonObject();
            if (definition.getAsJsonPrimitive("hasDefinition").getAsBoolean()) {
                map.put(entry.getKey(), ConfigDefinitionImpl.builder()
                        .withKey(entry.getKey())
                        .withDescription(toString(definition.get("description")))
                        .withDefaultValue(toString(definition.get("default")))
                        .withCategory(parseEnumOrNull(toString(definition.get("category")), ConfigCategory.class))
                        .withLocation(parseEnumOrNull(toString(definition.get("location")), ConfigLocation.class))
                        .withDefaultValue(toString(definition.get("default")))
                        .build());
            }
        });
        return map;
    }

    @Override
    public void upgradeWebapp(InputStream warFileData) {
        checkNotNull(warFileData, "data param cannot be null");
        HttpEntity multipart = MultipartEntityBuilder.create().addBinaryBody("file", listenUpload("war file upload", warFileData), ContentType.APPLICATION_OCTET_STREAM, "file.war").build();
        post("system/upgrade", multipart);
    }

    @Override
    public void recreateDatabase(InputStream dumpFileData, boolean freezesessions) {
        checkNotNull(dumpFileData, "data param cannot be null");
        HttpEntity multipart = MultipartEntityBuilder.create().addBinaryBody("file", listenUpload("dump file upload", dumpFileData), ContentType.APPLICATION_OCTET_STREAM, "file.dump").build();
        post("system/database/import?freezesessions=" + freezesessions, multipart);
    }

    @Override
    public String getConfig(String key) {
        logger.debug("getConfig for key = {}", key);
        return toString(get("system/config/" + trimAndCheckNotBlank(key)).asJson().getAsJsonObject().getAsJsonPrimitive("data"));
    }

    @Override
    @Nullable
    public Object eval(String script) {
        return toString(post("system/eval", new StringEntity(format("script=%s", UrlEscapers.urlFormParameterEscaper().escape(script)), APPLICATION_FORM_URLENCODED))
                .asJson().getAsJsonObject().getAsJsonObject("data").get("output"));
    }

    @Override
    public SystemApi setConfig(String key, String value) {
        logger.debug("setConfig for key = {} to value = {}", key, value);
        put("system/config/" + trimAndCheckNotBlank(key), value);
        return this;
    }

    @Override
    public SystemApi setConfigs(Map<String, String> data) {
        logger.debug("setConfig with data = {}", data);
        put("system/config/_MANY", data);
        return this;
    }

    @Override
    public SystemApi deleteConfig(String key) {
        logger.debug("deleteConfig for key = {}", key);
        delete("system/config/" + trimAndCheckNotBlank(key));
        return this;
    }

    @Override
    public void sendBroadcast(String message) {
        checkNotBlank(message);
        post("system/messages/broadcast?message=" + encodeUrlQuery(message), "");
    }

    @Override
    public List<PatchInfo> getPatches() {
        try {
            requireSession = false; //this is not great. TODO: refactor this with aspectj or something
            logger.debug("get patches");
            return stream(get("boot/patches").asJson().getAsJsonObject().getAsJsonArray("data")).map(JsonElement::getAsJsonObject).map((patch) -> {
                return new SimplePatchInfo(toString(patch.get("name")), toString(patch.get("description")), toString(patch.get("category")));
            }).collect(toList());
        } finally {
            requireSession = null;
        }
    }

    @Override
    public List<ServiceStatusInfo> getServicesStatus() {
        return stream(get("system_services").asJson().getAsJsonObject().getAsJsonArray("data")).map(JsonElement::getAsJsonObject).map((patch) -> {
            String name = checkNotBlank(toString(patch.get("name")));
            MinionStatus status = parseMinionStatus(toString(patch.get("status")));
            return new ServiceStatusInfo() {
                @Override
                public String getServiceName() {
                    return name;
                }

                @Override
                public MinionStatus getServiceStatus() {
                    return status;
                }

            };
        }).collect(toList());
    }

    @Override
    public ClusterStatus getClusterStatus() {
        JsonObject data = get("system/cluster/status").asJson().getAsJsonObject().getAsJsonObject("data");
        boolean isRunning = data.getAsJsonPrimitive("running").getAsBoolean();
        List<ClusterNode> nodes;
        if (isRunning) {
            nodes = stream(data.getAsJsonArray("nodes")).map(n -> new ClusterNodeImpl(n.getAsJsonObject().get("nodeId").getAsString(),
                    n.getAsJsonObject().get("address").getAsString(),
                    n.getAsJsonObject().get("thisNode").getAsBoolean())).collect(toImmutableList());
        } else {
            nodes = emptyList();
        }
        return new ClusterStatus() {
            @Override
            public boolean isRunning() {
                return isRunning;
            }

            @Override
            public List<ClusterNode> getNodes() {
                return nodes;
            }
        };
    }

    @Override
    public void applyPatches() {
        try {
            requireSession = false; //this is not great. TODO: refactor this with aspectj or something
            logger.debug("apply patches");
            post("boot/patches/apply", "");
        } finally {
            requireSession = null;
        }
    }

    @Override
    public void importFromDms() {
        logger.debug("importFromDms");
        post("system/dms/import", "");
    }

    @Override
    public void reloadConfig() {
        logger.debug("reloadConfig");
        post("system/config/reload", "");
    }

    @Override
    public void dropAllCaches() {
        logger.debug("drop all caches");
        post("system/cache/drop", "");
    }

    @Override
    public void reload() {
        logger.debug("reload system");
        post("system/reload", "");
    }

    @Override
    public void stop() {
        post("system/stop", "");
    }

    @Override
    public void restart() {
        post("system/restart", "");
    }

    @Override
    public void dropCache(String cacheId) {
        checkNotBlank(cacheId);
        logger.debug("drop cache = %s", cacheId);
        post(format("system/cache/%s/drop", encodeUrlPath(cacheId)), "");
    }

    @Override
    public void reconfigureDatabase(Map<String, String> config) {
        //TODO validate config
        post("system/database/reconfigure", map(config));
    }

    @Override
    public byte[] dumpDatabase() {
        return getBytes("system/database/dump");
    }

    @Override
    public byte[] downloadDebugInfo() {
        return getBytes("system/debuginfo/download");
    }

    @Override
    public BugReportInfo sendBugReport(@Nullable String message) {
        String url = "system/debuginfo/send";
        if (isNotBlank(message)) {
            url += "?message=" + encodeUrlQuery(message);
        }
        JsonObject data = post(url, "").asJson().getAsJsonObject().getAsJsonObject("data");
        return new DebugInfoImpl(toString(data.get("fileName")));
    }

    private static class LogMessageImpl implements LogMessage {

        private final LogLevel level;
        private final String message, stacktrace, line;
        private final ZonedDateTime timestamp;

        public LogMessageImpl(Map<String, Object> payload) {
            level = checkNotNull(convert(payload.get("level"), LogLevel.class));
            message = checkNotBlank(toStringOrNull(payload.get("message")));
            line = checkNotBlank(toStringOrNull(payload.get("line")));
            stacktrace = emptyToNull(toStringOrNull(payload.get("stacktrace")));
            timestamp = checkNotNull(CmDateUtils.toDateTime(payload.get("timestamp")));
        }

        @Override
        public LogLevel getLevel() {
            return level;
        }

        @Override
        public String getMessage() {
            return message;
        }

        @Override
        @Nullable
        public String getStacktrace() {
            return stacktrace;
        }

        @Override
        public ZonedDateTime getTimestamp() {
            return timestamp;
        }

        @Override
        public String getLine() {
            return line;
        }

    }

}
