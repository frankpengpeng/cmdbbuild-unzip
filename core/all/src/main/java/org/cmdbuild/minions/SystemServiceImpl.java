/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.minions;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import org.cmdbuild.services.SystemStatus;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Ordering;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import static java.lang.String.format;
import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static java.util.stream.Collectors.joining;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import static org.cmdbuild.common.error.ErrorAndWarningCollectorService.marker;
import org.cmdbuild.dao.config.DatabaseConfiguration;
import org.cmdbuild.dao.config.inner.AllPatchesAppliedEvent;
import org.cmdbuild.debuginfo.BuildInfoService;
import org.cmdbuild.requestcontext.RequestContextService;
import org.cmdbuild.system.SystemEventService;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.cmdbuild.services.SystemService;
import org.cmdbuild.services.Minion;
import static org.cmdbuild.services.MinionStatus.MS_READY;
import static org.cmdbuild.services.MinionStatus.MS_ERROR;
import org.cmdbuild.services.MinionService;
import org.cmdbuild.services.AppContextReadyEvent;
import org.cmdbuild.services.SystemReadyEvent;
import org.cmdbuild.services.SystemStartingServicesEvent;
import org.cmdbuild.services.SystemStoppingServicesEvent;
import static org.cmdbuild.services.SystemServiceStatusUtils.serializeMinionStatus;
import org.cmdbuild.services.SystemStartedServicesEvent;
import static org.cmdbuild.services.SystemStatus.SYST_BEFORE_DATABASE_CHECK;
import static org.cmdbuild.services.SystemStatus.SYST_BEFORE_READY;
import static org.cmdbuild.services.SystemStatus.SYST_BEFORE_STARTING_SERVICES;
import static org.cmdbuild.services.SystemStatus.SYST_BEFORE_STOPPING_SERVICES;
import static org.cmdbuild.services.SystemStatus.SYST_CHECKING_DATABASE;
import static org.cmdbuild.services.SystemStatus.SYST_PREPARING_SERVICES;
import static org.cmdbuild.services.SystemStatus.SYST_STARTING_SERVICES;
import static org.cmdbuild.services.SystemStatus.SYST_STOPPING_SERVICES;
import static org.cmdbuild.services.SystemStatus.SYST_READY;
import static org.cmdbuild.services.SystemStatus.SYST_WAITING_FOR_APP_CONTEXT;
import static org.cmdbuild.services.SystemStatusUtils.serializeSystemStatus;
import static org.cmdbuild.utils.lang.CmExecutorUtils.namedThreadFactory;
import static org.cmdbuild.utils.lang.CmExecutorUtils.shutdownQuietly;
import org.cmdbuild.dao.config.inner.PatchService;
import org.cmdbuild.dao.postgres.listener.PostgresNotificationEvent;
import org.cmdbuild.dao.postgres.listener.PostgresNotificationEventService;
import static org.cmdbuild.platform.PlatformUtils.checkOsUser;
import org.cmdbuild.services.SystemLoadingConfigEvent;
import org.cmdbuild.services.SystemLoadingConfigFilesEvent;
import static org.cmdbuild.services.SystemStatus.SYST_LOADING_CONFIG;
import static org.cmdbuild.services.SystemStatus.SYST_BEFORE_LOADING_CONFIG;
import static org.cmdbuild.services.SystemStatus.SYST_LOADING_CONFIG_FILES;
import static org.cmdbuild.services.SystemStatus.SYST_NOT_RUNNING;
import static org.cmdbuild.utils.date.CmDateUtils.toUserDuration;
import org.slf4j.MDC;

/**
 *
 * this spring service run all required checks at startup. Cmdbuild startup
 * works like this:<ul>
 * <li>spring application context startup (all spring beans should NOT access
 * db, start stuff or do anything heavy in their constructor and PostConstruct
 * methods; nothing important should happen before the whole spring app context
 * is ready)</li>
 * <li>once spring is ready, we'll have a boot rest ws responding (minimal ws
 * set that may be used to handle configuration, patch, monitor startup process
 * etc); all other ws should be blocked</li>
 * <li>this bean will run boot check (check db, check config, check patch,
 * etc)</li>
 * <li>if the system requires user input (for example, the database is not
 * configured, or there are patches to run) it will hold the spring
 * initialization process while waiting for user input (via boot services)</li>
 * <li>after all check pass (with or without user intervention) the service will
 * go on starting other services (cluster, scheduler, etc); note: config load
 * from db will be triggered before, once patch manager completes its
 * operation</li>
 * </ul>
 *
 * @author davide
 */
@Component
public class SystemServiceImpl implements SystemService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ExecutorService executor = Executors.newSingleThreadExecutor(namedThreadFactory(getClass()));

    private final DatabaseConfiguration databaseConfiguration;
    private final PatchService patchService;
    private final RequestContextService requestContextService;
    private final EventBus eventBus;
    private final BuildInfoService buildInfoService;
    private final MinionService minionService;
    private final PostgresNotificationEventService pgEventService;

    private SystemStatus systemStatus = SYST_WAITING_FOR_APP_CONTEXT;

    public SystemServiceImpl(PostgresNotificationEventService pgEventService, MinionService servicesStatusService, BuildInfoService buildInfoService, DatabaseConfiguration databaseConfiguration, PatchService patchManager, RequestContextService requestContextService, SystemEventService systemEventService) {
        this.patchService = checkNotNull(patchManager);
        this.requestContextService = checkNotNull(requestContextService);
        this.databaseConfiguration = checkNotNull(databaseConfiguration);
        this.buildInfoService = checkNotNull(buildInfoService);
        this.minionService = checkNotNull(servicesStatusService);
        this.pgEventService = checkNotNull(pgEventService);
        eventBus = systemEventService.getEventBus();
    }

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        logger.info("spring context is ready");
        checkStartupConditions();
        executor.submit(() -> {
            MDC.put("cm_type", "sys");
            MDC.put("cm_id", "sys:boot");
            requestContextService.initCurrentRequestContext("system startup");
            //TODO set user
            logger.info("start system");
            runNextStep();
        });
    }

    @Override
    public SystemStatus getSystemStatus() {
        return systemStatus;
    }

    @Override
    public void startSystem() {
        checkArgument(equal(SYST_NOT_RUNNING, systemStatus), "cannot start services, invalid system status = %s (system must be in status %s)", serializeSystemStatus(systemStatus), serializeSystemStatus(SYST_NOT_RUNNING));
        runNextStep(SYST_BEFORE_DATABASE_CHECK);
    }

    @Override
    public void stopSystem() {
        if (!equal(SYST_READY, systemStatus)) {
            logger.warn(marker(), "cannot stop services, current system status = {} (system must be in status READY)", serializeSystemStatus(systemStatus));
        } else {
            runNextStep(SYST_BEFORE_STOPPING_SERVICES);
        }
    }

    @PostConstruct
    public void test() {
        logger.info("received application context PostConstruct event");
    }

    @PreDestroy
    public void cleanup() {
        shutdownQuietly(executor);
    }

    private final static Map<SystemStatus, Object> TRANSITION_EVENTS = ImmutableMap.copyOf(map(
            SYST_PREPARING_SERVICES, AppContextReadyEvent.INSTANCE,
            SYST_LOADING_CONFIG_FILES, SystemLoadingConfigFilesEvent.INSTANCE,
            SYST_LOADING_CONFIG, SystemLoadingConfigEvent.INSTANCE,
            SYST_STARTING_SERVICES, SystemStartingServicesEvent.INSTANCE,
            SYST_STOPPING_SERVICES, SystemStoppingServicesEvent.INSTANCE,
            SYST_BEFORE_READY, SystemStartedServicesEvent.INSTANCE,
            SYST_READY, SystemReadyEvent.INSTANCE
    ));

    private final Map<SystemStatus, Object> TRANSITION_FLOW = ImmutableMap.copyOf(map(SYST_WAITING_FOR_APP_CONTEXT, SYST_PREPARING_SERVICES,
            SYST_PREPARING_SERVICES, SYST_LOADING_CONFIG_FILES,
            SYST_LOADING_CONFIG_FILES, SYST_BEFORE_DATABASE_CHECK,
            SYST_BEFORE_DATABASE_CHECK, SYST_CHECKING_DATABASE,
            SYST_CHECKING_DATABASE, (Runnable) this::checkDatabase,
            SYST_BEFORE_LOADING_CONFIG, SYST_LOADING_CONFIG,
            SYST_LOADING_CONFIG, SYST_BEFORE_STARTING_SERVICES,
            SYST_BEFORE_STARTING_SERVICES, SYST_STARTING_SERVICES,
            SYST_STARTING_SERVICES, SYST_BEFORE_READY,
            SYST_BEFORE_READY, SYST_READY,
            SYST_BEFORE_STOPPING_SERVICES, SYST_STOPPING_SERVICES,
            SYST_STOPPING_SERVICES, SYST_NOT_RUNNING
    ));

    private void runNextStep(SystemStatus systemStatus) {
        setSystemStatus(systemStatus);
        runNextStep();
    }

    private void runNextStep() {
        try {
            Object nextStep = TRANSITION_FLOW.get(systemStatus);
            if (nextStep == null) {
                //do nothing
            } else if (nextStep instanceof SystemStatus) {
                setSystemStatus((SystemStatus) nextStep);
                runNextStep();
            } else if (nextStep instanceof Runnable) {
                ((Runnable) nextStep).run();
            } else {
                throw new UnsupportedOperationException("unsupported transition = " + nextStep);
            }
        } catch (Exception ex) {
            logger.error("system startup error", ex);
            setSystemStatus(SystemStatus.SYST_ERROR);
        }
    }

    private void setSystemStatus(SystemStatus systemStatus) {
        this.systemStatus = checkNotNull(systemStatus);
        logger.debug("system status set to = {}", this.systemStatus);
        switch (this.systemStatus) {
            case SYST_READY:
                //TODO dynamic version
                //			
                //   ___ __  __ ___  ___      _ _    _ 
                //  / __|  \/  |   \| _ )_  _(_) |__| |
                // | (__| |\/| | |) | _ \ || | | / _` |
                //  \___|_|  |_|___/|___/\_,_|_|_\__,_|
                //                
                String asciiArtPattern = "\t   ___ __  __ ___  ___      _ _    _ \n\t  / __|  \\/  |   \\| _ )_  _(_) |__| |\n\t | (__| |\\/| | |) | _ \\ || | | / _` |\n\t  \\___|_|  |_|___/|___/\\_,_|_|_\\__,_|\n\t                     v%s READY",
                 asciiArtBanner = format(asciiArtPattern, buildInfoService.getVersionNumberOrUnknownIfNotAvailable()),
                 servicesStatus = buildServicesStatusInfoMessage(),
                 sourceCodeVersionInfo = buildInfoService.hasBuildInfo() ? buildInfoService.getCommitInfo() : "<build info not available>";
                logger.info("\n\n\n\n{}\n\n\n\n{}\n\n\n\trunning source code rev {}\n\tstartup (uptime) {}\n\n", asciiArtBanner, servicesStatus, sourceCodeVersionInfo, toUserDuration(ManagementFactory.getRuntimeMXBean().getUptime()));
                break;
            case SYST_CHECKING_DATABASE:
            case SYST_LOADING_CONFIG:
            case SYST_WAITING_FOR_USER:
            case SYST_STARTING_SERVICES:
            case SYST_STOPPING_SERVICES:
            case SYST_NOT_RUNNING:
            case SYST_ERROR:
                logger.info("\n\n\n\n\tsystem is {}\n\n\n", serializeSystemStatus(this.systemStatus));
        }
        Optional.ofNullable(TRANSITION_EVENTS.get(this.systemStatus)).ifPresent(eventBus::post);
    }

    private String buildServicesStatusInfoMessage() {
        return minionService.getMinions().stream().sorted(Ordering.natural().onResultOf(Minion::getName))
                .map(s -> format("\t%-24s\t%s   %s", s.getName(), map(MS_READY, "*", MS_ERROR, "E").getOrDefault(s.getStatus(), " "), serializeMinionStatus(s.getStatus()))).collect(joining("\n"));
    }

    private void checkDatabase() {
        logger.info("check patch manager");

        if (databaseConfiguration.enableAutoPatch() && patchService.hasPendingPatchesOrFunctions()) {
            try {
                patchService.applyPendingPatchesAndFunctions();
            } catch (Exception ex) {
                logger.error("error during auto-patching", ex);
            }
        }

        if (patchService.hasPendingPatchesOrFunctions()) {
            setSystemStatus(SystemStatus.SYST_WAITING_FOR_USER);
            logger.info("patch manager is not ready, waiting for user input");
            Object listener = new Object() {

                @Subscribe
                public void handleAllPatchAppliedAndDatabaseReadyEvent(AllPatchesAppliedEvent event) {
                    handleEvent();
                }

                @Subscribe
                public void handlePostgresNotificationEvent(PostgresNotificationEvent event) {
                    if (event.isEvent("org.cmdbuild.dao.config.AllPatchesApplied")) {
                        handleEvent();
                    }
                }

                private void handleEvent() {
                    patchService.getEventBus().unregister(this);
                    pgEventService.getEventBus().unregister(this);
                    executor.submit(() -> runNextStep(SYST_BEFORE_LOADING_CONFIG));
                }

            };
            patchService.getEventBus().register(listener);
            pgEventService.getEventBus().register(listener);
        } else {
            runNextStep(SYST_BEFORE_LOADING_CONFIG);
        }
    }

    private void checkStartupConditions() {
        checkOsUser();
        //TODO check system mem/resources, etc
    }

}
