package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import static com.google.common.collect.Maps.newLinkedHashMap;
import com.google.common.collect.Ordering;
import java.io.File;
import static java.lang.String.format;
import java.lang.management.ManagementFactory;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.MediaType.WILDCARD;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.io.FileUtils;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.cmdbuild.auth.multitenant.api.MultitenantService;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.common.log.LoggerConfigImpl;
import org.cmdbuild.audit.RequestTrackingService;
import org.cmdbuild.cache.CmCacheStats;
import static org.cmdbuild.common.error.ErrorAndWarningCollectorService.marker;
import org.cmdbuild.common.log.LoggerConfig;
import org.cmdbuild.config.RequestTrackingConfiguration;
import org.cmdbuild.dao.ConfigurableDataSource;
import org.cmdbuild.scheduler.SchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.dao.postgres.DumpService;
import org.cmdbuild.lang.scriptexecutors.BeanshellScriptExecutor;
import org.cmdbuild.scheduler.beans.ScheduledJobInfo;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationUtils.serializePatchInfo;
import org.cmdbuild.utils.date.CmDateUtils;
import static org.cmdbuild.utils.io.CmIoUtils.tempFile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.cmdbuild.debuginfo.BugReportService;
import org.cmdbuild.debuginfo.BugReportInfo;
import static org.cmdbuild.debuginfo.SystemStatusUtils.getIpAddr;
import static org.cmdbuild.utils.date.CmDateUtils.systemDate;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.auth.login.AuthorityConst.HAS_SYSTEM_ACCESS_AUTHORITY;
import org.cmdbuild.config.api.GlobalConfigService;
import static org.cmdbuild.dao.config.DatabaseConfiguration.DATABASE_CONFIG_NAMESPACE;
import org.cmdbuild.dao.config.inner.DatabaseCreator;
import org.cmdbuild.dao.config.inner.DatabaseCreatorConfig;
import org.cmdbuild.dao.config.inner.DatabaseCreatorConfigImpl;
import org.cmdbuild.event.EventService;
import org.cmdbuild.platform.PlatformService;
import org.cmdbuild.service.rest.v3.helpers.LogMessageStreamHelper;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILE;
import org.cmdbuild.sysmon.SysmonService;
import org.cmdbuild.utils.io.CmIoUtils;
import static org.cmdbuild.utils.io.CmNetUtils.getHostname;
import org.cmdbuild.services.SystemService;
import static org.cmdbuild.services.SystemStatusUtils.serializeSystemStatus;
import static org.cmdbuild.utils.io.CmIoUtils.copy;
import org.cmdbuild.clustering.ClusterService;
import org.cmdbuild.platform.UpgradeHelperService;
import org.cmdbuild.workflow.core.fluentapi.WorkflowApiService;
import org.cmdbuild.dao.config.inner.PatchService;
import org.cmdbuild.dao.postgres.PostgresDateService;
import org.cmdbuild.debuginfo.BuildInfoService;
import static org.cmdbuild.utils.encode.CmEncodeUtils.decodeString;
import static org.cmdbuild.utils.encode.CmEncodeUtils.encodeString;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.sysmon.SystemStatusLog;
import org.cmdbuild.common.log.LoggersConfigService;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.cmdbuild.config.api.DirectoryService;
import org.cmdbuild.dao.config.inner.ConfigImportStrategy;
import org.cmdbuild.dms.DmsService;
import static org.cmdbuild.utils.date.CmDateUtils.dateTimeFileSuffix;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDuration;
import org.cmdbuild.utils.io.BigByteArrayDataSource;
import static org.cmdbuild.utils.io.CmIoUtils.toBigByteArray;

@Path("system/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@PreAuthorize(HAS_SYSTEM_ACCESS_AUTHORITY)
public class SystemWs {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final GlobalConfigService configService;
    private final LogMessageStreamHelper logMessageStreamHelper;
    private final ConfigurableDataSource dataSource;
    private final CacheService cacheService;
    private final RequestTrackingService requestTrackingService;
    private final PatchService patchManager;
    private final SchedulerService schedulerService;
    private final MultitenantService multitenantService;
    private final LoggersConfigService loggerConfigurationService;
    private final BugReportService bugreportService;
    private final DumpService dumpService;
    private final ClusterService clusteringService;
    private final PlatformService platformService;
    private final SysmonService sysmonService;
    private final EventService eventService;
    private final SystemService bootService;
    private final UpgradeHelperService upgradeService;
    private final WorkflowApiService apiService;
    private final BuildInfoService buildInfoService;
    private final PostgresDateService postgresDateService;
    private final DirectoryService directoryService;
    private final DmsService documentService;

    public SystemWs(GlobalConfigService configService, LogMessageStreamHelper logMessageStreamHelper, ConfigurableDataSource dataSource, CacheService cacheService, RequestTrackingService requestTrackingService, PatchService patchManager, SchedulerService schedulerService, MultitenantService multitenantService, LoggersConfigService loggerConfigurationService, BugReportService bugreportService, DumpService dumpService, ClusterService clusteringService, PlatformService platformService, SysmonService sysmonService, EventService eventService, SystemService bootService, UpgradeHelperService upgradeService, WorkflowApiService apiService, BuildInfoService buildInfoService, PostgresDateService postgresDateService, DirectoryService directoryService, DmsService documentService) {
        this.configService = checkNotNull(configService);
        this.logMessageStreamHelper = checkNotNull(logMessageStreamHelper);
        this.dataSource = checkNotNull(dataSource);
        this.cacheService = checkNotNull(cacheService);
        this.requestTrackingService = checkNotNull(requestTrackingService);
        this.patchManager = checkNotNull(patchManager);
        this.schedulerService = checkNotNull(schedulerService);
        this.multitenantService = checkNotNull(multitenantService);
        this.loggerConfigurationService = checkNotNull(loggerConfigurationService);
        this.bugreportService = checkNotNull(bugreportService);
        this.dumpService = checkNotNull(dumpService);
        this.clusteringService = checkNotNull(clusteringService);
        this.platformService = checkNotNull(platformService);
        this.sysmonService = checkNotNull(sysmonService);
        this.eventService = checkNotNull(eventService);
        this.bootService = checkNotNull(bootService);
        this.upgradeService = checkNotNull(upgradeService);
        this.apiService = checkNotNull(apiService);
        this.buildInfoService = checkNotNull(buildInfoService);
        this.postgresDateService = checkNotNull(postgresDateService);
        this.directoryService = checkNotNull(directoryService);
        this.documentService = checkNotNull(documentService);
    }

    @GET
    @Path("status")
    public Object status() {
        SystemStatusLog runtimeStatus = sysmonService.getSystemRuntimeStatus();
        return response(map(
                "hostname", getHostname(),
                "hostaddress", getIpAddr(),
                "build_info", buildInfoService.getCommitInfoOrUnknownIfNotAvailable(),
                "version", buildInfoService.getVersionNumberOrUnknownIfNotAvailable(),
                "runtime", Runtime.getRuntime().toString(),
                "uptime", toIsoDuration(ManagementFactory.getRuntimeMXBean().getUptime()),
                "server_time", toIsoDateTime(systemDate()),
                "db_timezone", postgresDateService.getTimezone(),
                "db_timezone_offset", postgresDateService.getOffset(),
                "disk_used", runtimeStatus.getFilesystemMemoryUsed(),
                "disk_free", runtimeStatus.getFilesystemMemoryAvailable(),
                "java_memory_used", runtimeStatus.getJavaMemoryUsed(),
                "java_memory_free", runtimeStatus.getJavaMemoryAvailable(),
                "system_memory_used", runtimeStatus.getSystemMemoryUsed(),
                "system_memory_free", runtimeStatus.getSystemMemoryAvailable(),
                "process_pid", runtimeStatus.getJavaPid(),
                "system_load", runtimeStatus.getLoadAvg())
                .accept((m) -> {
                    if (runtimeStatus.hasWarnings()) {
                        m.put("warning", runtimeStatus.getWarnings());
                    }
                    try {
                        BasicDataSource basicDataSource = dataSource.getInner();
                        m.put("datasource_active_connections", String.valueOf(basicDataSource.getNumActive()));
                        m.put("datasource_idle_connections", String.valueOf(basicDataSource.getNumIdle()));
                        m.put("datasource_max_active_connections", String.valueOf(basicDataSource.getMaxTotal()));
                        m.put("datasource_max_idle_connections", String.valueOf(basicDataSource.getMaxIdle()));
                    } catch (Exception ex) {
                        logger.warn(marker(), "error retrieving datasource info", ex);
                    }
                })
        );
    }

    @GET
    @Path("cluster/status")
    public Object getClusterStatus() {
        return response(map(
                "running", clusteringService.isRunning(),
                "nodes", clusteringService.isRunning() ? clusteringService.getClusterNodes().stream().map(n -> map("address", n.getAddress(), "nodeId", n.getNodeId(), "thisNode", n.isThisNode())).collect(toList()) : emptyList())
        );
    }

    @POST
    @Path("cache/drop")
    @Consumes(WILDCARD)
    public Object dropCache() {
        logger.info("drop system cache");
        cacheService.invalidateAll();
        return success();
    }

    @POST
    @Path("cache/{cacheId}/drop")
    @Consumes(WILDCARD)
    public Object dropCache(@PathParam("cacheId") String cacheId) {
        logger.info("drop cache = {}", cacheId);
        cacheService.invalidate(cacheId);
        return success();
    }

    @GET
    @Path("cache/stats")
    public Object getCacheStats() {
        Map<String, CmCacheStats> stats = cacheService.getStats();
        return success().with("data", list().accept((l) -> {
            stats.forEach((key, value) -> {
                l.add(map("name", key, "objectsCount", value.getSize(), "objectsSize", value.getEstimateMemSize(), "_objectsSize_description", FileUtils.byteCountToDisplaySize(value.getEstimateMemSize())));
            });
        }));
    }

    @POST
    @Path("stop")
    @Consumes(WILDCARD)
    public Object stopSystem() {
        logger.info("stop cmdbuild");
        platformService.stopContainer();
        return success();
    }

    @POST
    @Path("reload")
    @Consumes(WILDCARD)
    public Object reloadSystem() {
        logger.info("reload cmdbuild");
        configService.reload();
        cacheService.invalidateAll();
        return success();
    }

    @POST
    @Path("restart")
    @Consumes(WILDCARD)
    public Object restartSystem() {
        logger.info("restart cmdbuild");
        platformService.restartContainer();
        return success();
    }

    @POST
    @Path("upgrade")
    @Consumes(MULTIPART_FORM_DATA)
    public Object upgradeSystem(@Multipart(FILE) DataHandler dataHandler) {
        logger.info("upgrade cmdbuild");
        upgradeService.upgradeWebapp(CmIoUtils.toByteArray(dataHandler));
        return success();
    }

    /**
     * drop all data collected by audit process (request tracking)
     *
     * this is mostly useful for debug/devel, or to clear data after we've
     * disabled tracking
     *
     */
    @POST
    @Path("audit/drop")
    @Consumes(WILDCARD)
    public void dropAudit() {
        logger.info("drop audit data");
        requestTrackingService.dropAllData();
    }

    /**
     * run cleanup process for data collected by audit process (request
     * tracking); cleanup process will run as configured in
     * {@link RequestTrackingConfiguration}
     *
     */
    @POST
    @Path("audit/cleanup")
    @Consumes(WILDCARD)
    public void cleanupAudit() {
        logger.info("cleanup audit data");
        requestTrackingService.cleanupRequestTable();
    }

    @GET
    @Path("patches")
    public Object getAllPatches() {
        return ImmutableMap.of("patches", patchManager.getAllPatches().stream()
                .sorted((a, b) -> ComparisonChain.start().compareFalseFirst(a.isApplied(), b.isApplied()).compare(firstNonNull(a.getApplyDate(), 0), firstNonNull(b.getApplyDate(), 0)).compare(b.getComparableVersion(), a.getComparableVersion()).result())
                .map((patch) -> serializePatchInfo(patch).accept((map) -> {
            map.put("applied", patch.isApplied());
            if (patch.isApplied()) {
                map.put("appliedOnDate", CmDateUtils.toIsoDateTime(patch.getApplyDate()));
            }
            if (!isBlank(patch.getHash())) {
                map.put("hash", patch.getHash());
            }
            List<String> warnings = Lists.newArrayList();
            if (patch.hashMismatch()) {
                warnings.add("hash mismatch: the hash on db does not match the hash on file");
            }
            if (patch.isApplied() && !patch.hasPatchOnFile()) {
                warnings.add("orphan patch: this patch does not exisit on file");
            }
            if (!warnings.isEmpty()) {
                map.put("warning", warnings);
            }
        })).collect(toList()));
    }

    /**
     * return all active tenants
     *
     * @return
     */
    @GET
    @Path("tenants")
    public List<Object> getAllTenants() {
        return multitenantService.getAllActiveTenants().stream().map((tenant) -> {
            Map map = newLinkedHashMap();
            map.put("id", tenant.getId());
            map.put("description", tenant.getDescription());
            return map;
        }).collect(toList());
    }

    @GET
    @Path("scheduler/jobs")
    public Object getSchedulerJobs() {
        return map("success", true, "data", schedulerService.getConfiguredJobs().stream()
                .sorted(Ordering.natural().onResultOf(ScheduledJobInfo::getKey))
                .map((job) -> map(
                "_id", encodeString(job.getKey()),
                "group", job.getGroup(),
                "name", job.getName(),
                "trigger", job.getTrigger()
        )).collect(toList()));
    }

    @POST
    @Path("scheduler/jobs/{jobId}/trigger")
    public Object triggerJobNow(@PathParam("jobId") String jobId) {
        schedulerService.triggerJobImmediately(decodeString(jobId));
        return success();
    }

    @GET
    @Path("loggers")
    public Object getAllLoggers() {
        return response(loggerConfigurationService.getAllLoggerConfig().stream()
                .sorted(Ordering.natural().onResultOf(LoggerConfig::getCategory))
                .map((item) -> map("category", item.getCategory(), "level", item.getLevel())).collect(toList()));
    }

    @POST
    @Path("loggers/{key}")
    @Consumes(TEXT_PLAIN)
    public void updateLoggerLevel(@PathParam("key") String loggerCategory, String loggerLevel) {
        if ("default".equalsIgnoreCase(loggerLevel)) {
            loggerConfigurationService.removeLoggerConfig(loggerCategory);
        } else {
            loggerConfigurationService.setLoggerConfig(new LoggerConfigImpl(loggerCategory, loggerLevel));
        }
    }

    @PUT
    @Path("loggers/{key}")
    @Consumes(TEXT_PLAIN)
    public void addLoggerLevel(@PathParam("key") String loggerCategory, String loggerLevel) {
        loggerConfigurationService.setLoggerConfig(new LoggerConfigImpl(loggerCategory, loggerLevel));
    }

    @DELETE
    @Path("loggers/{key}")
    public void deleteLoggerLevel(@PathParam("key") String loggerCategory) {
        loggerConfigurationService.removeLoggerConfig(loggerCategory);
    }

    @POST
    @Consumes(WILDCARD)
    @Path("loggers/stream")
    public Object receiveLogMessages() {
        logMessageStreamHelper.startReceivingLogMessages();
        return success();
    }

    @DELETE
    @Consumes(WILDCARD)
    @Path("loggers/stream")
    public Object stopReceivingLogMessages() {
        logMessageStreamHelper.stopReceivingLogMessages();
        return success();
    }

    @POST
    @Path("eval")
    @Consumes(APPLICATION_FORM_URLENCODED)
    public Object eval(@FormParam("script") String script) {
        Map<String, Object> input = apiService.getWorkflowApiAsDataMap();//TODO data params ?
        Object value = new BeanshellScriptExecutor(script).execute(input).get("output");
        return response(map("output", value));
    }

    @GET
    @Path("database/dump")
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler dumpDatabase() {
        File tempFile = tempFile();
        dumpService.dumpDatabaseToFile(tempFile);
        DataSource dumpFileDataSource;
        if (tempFile.length() < 1024 * 1024 * 1024 * 2) {
            dumpFileDataSource = new BigByteArrayDataSource(toBigByteArray(tempFile), APPLICATION_OCTET_STREAM, format("cmdbuild_%s.dump", dateTimeFileSuffix()));
            deleteQuietly(tempFile);
        } else {
            dumpFileDataSource = new FileDataSource(tempFile);
        }
        return new DataHandler(dumpFileDataSource);
    }

    @POST
    @Path("database/reconfigure")
    public Object reconfigureDatabase(Map<String, String> dbConfig) {
        Map<String, String> currentConfig = configService.getConfig(DATABASE_CONFIG_NAMESPACE).getAsMap(), newConfig = map(currentConfig).with(dbConfig);
        if (equal(currentConfig, newConfig)) {
            logger.info(marker(), "database config already up to date, skip reconfigure");
        } else {
            DatabaseCreatorConfig config = DatabaseCreatorConfigImpl.builder().withConfig(newConfig).build();
            config.checkConfig();
            bootService.stopSystem();
            configService.putStrings(DATABASE_CONFIG_NAMESPACE, config.getConfig());
            cacheService.invalidateAll();
            bootService.startSystem();
        }
        return success().with("status", serializeSystemStatus(bootService.getSystemStatus()));
    }

    @POST
    @Path("database/import")
    @Consumes(MULTIPART_FORM_DATA)
    public Object importDatabaseFromDump(@Multipart(FILE) DataHandler dataHandler, @QueryParam("freezesessions") @DefaultValue(FALSE) Boolean freezesessions) {//TODO optional backup-before and restore-on-failure options; optional dump and restore config table
        logger.info("recreate cmdbuild database");
        File tempFile = tempFile(null, "dump");
        copy(dataHandler, tempFile);
        DatabaseCreator databaseCreator = new DatabaseCreator(DatabaseCreatorConfigImpl.builder()
                .withConfig(configService.getConfig(DATABASE_CONFIG_NAMESPACE).getAsMap())
                .withUseSharkSchema(true)//TODO auto check from dump 
                .withSqlPath(new File(directoryService.getWebappDirectory(), "WEB-INF/sql").getAbsolutePath())
                .withConfigImportStrategy(ConfigImportStrategy.CIS_DATA_ONLY)
                .withKeepLocalConfig(true)
                .withSource(tempFile.getAbsolutePath()).build());
        Map<String, String> savedConfig = map(configService.getConfigAsMap());
        try {
            bootService.stopSystem();
            dataSource.closeInner();
            databaseCreator.dropDatabase();
        } catch (Exception ex) {
            logger.error("error dropping database; restarting system");
            dataSource.reloadInner();
            bootService.startSystem();
            throw ex;
        }
        try {
            databaseCreator.configureDatabase();
            try {
                databaseCreator.applyPatches();
            } finally {
                databaseCreator.adjustConfigs(savedConfig);
            }
            if (freezesessions) {
                databaseCreator.freezeSessions();
            }
        } finally {
            cacheService.invalidateAll();
            configService.reloadFromFilesSkipNotify();
            dataSource.reloadInner();
            bootService.startSystem();
            deleteQuietly(tempFile);
        }
        return success();
    }

    @GET
    @Path("debuginfo/download")
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler generateDebugInfo() {
        return new DataHandler(bugreportService.generateBugReport());
    }

    @POST
    @Consumes(WILDCARD)
    @Path("debuginfo/send")
    public Object sendBugReport(@QueryParam("message") String message) {
        BugReportInfo debugInfo = bugreportService.sendBugReport(message);
        return response(map("fileName", debugInfo.getFileName()));
    }

    @POST
    @Consumes(WILDCARD)
    @Path("messages/broadcast")
    public Object sendBroadcastMessage(@QueryParam("message") String message) {
        eventService.sendBroadcastAlert(message);
        return success();
    }

    @GET
    @Path("dms/export")
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler exportAllDocuments() {
        return documentService.exportAllDocuments();
    }
}
