/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.audit;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import javax.annotation.PreDestroy;
import static org.apache.commons.lang3.StringUtils.abbreviate;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.audit.RequestInfo.NO_SESSION_USER;
import org.cmdbuild.scheduler.ScheduledJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.config.RequestTrackingConfiguration;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.cluster.ClusterConfiguration;
import org.cmdbuild.requestcontext.RequestContextService;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.services.SystemService;
import static org.cmdbuild.utils.lang.CmExecutorUtils.namedThreadFactory;
import static org.cmdbuild.utils.lang.CmExecutorUtils.shutdownQuietly;

@Component
public class RequestTrackingServiceImpl implements RequestTrackingService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final RequestTrackingConfiguration config;
    private final RequestTrackingWritableRepository store;
    private final SessionService sessionService;
    private final ClusterConfiguration clusterConfiguration;

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(namedThreadFactory(getClass())); // if we use a single thread executor we can avoid a bunch of synchronization later. Also, this act as a performance control.
    private final Cache<String, OngoingRequestStatus> ongoingRequests = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.DAYS).build();

    private final SystemService systemService;

    public RequestTrackingServiceImpl(ClusterConfiguration clusterConfiguration, RequestTrackingConfiguration config, RequestTrackingWritableRepository store, SessionService sessionService, RequestContextService contextService, SystemService bootService) {
        logger.debug("init");
        this.config = checkNotNull(config);
        this.store = checkNotNull(store);
        this.sessionService = checkNotNull(sessionService);
        this.systemService = checkNotNull(bootService);
        this.clusterConfiguration = checkNotNull(clusterConfiguration);
        scheduledExecutorService.submit(() -> {
            contextService.initCurrentRequestContext("request tracking background job");
            //TODO set admin user
        });
    }

    @PreDestroy
    public void cleanup() {
        logger.info("cleanup");
        shutdownQuietly(scheduledExecutorService);
    }

    @ScheduledJob("0 0 * * * ?") //run every hour
    public void doCleanupRequestTable() {
        cleanupRequestTable();
    }

    @Override
    public void cleanupRequestTable() {
        Integer maxRecordAgeToKeepSeconds = config.getMaxRecordAgeToKeepSeconds();
        if (maxRecordAgeToKeepSeconds != null && maxRecordAgeToKeepSeconds > 0) {
            store.cleanupRequestTableForMaxAge(maxRecordAgeToKeepSeconds);
        }
        Integer maxRecordsToKeep = config.getMaxRecordsToKeep();
        if (maxRecordsToKeep != null && maxRecordsToKeep > 0) {
            store.cleanupRequestTableForMaxRecords(maxRecordsToKeep);
        }
    }

    @Override
    public void dropAllData() {
        store.dropAll();
        //note: this may cause inconsistencies with ongoingRequestsNotYetPersisted and possible pending requests: use with care!
    }

    @Override
    public void requestBegin(RequestData data) {
        RequestData thisData = processRequestData(data);
        logger.debug("request begin = {}", thisData);
        if (isDbPersistEnabled()) {
            scheduledExecutorService.submit(() -> {
                ScheduledFuture persistOngoingRequestJob = scheduledExecutorService.schedule(() -> {
                    try {
                        if (isDbPersistEnabled()) {
                            ongoingRequests.put(thisData.getRequestId(), new OngoingRequestStatus(thisData.getRequestId(), true, null));
                            store.create(thisData);
                        }
                    } catch (Exception ex) {
                        logger.error("error processing request begin, request = {}", thisData);
                        logger.error("error processing request begin", ex);
                    }
                }, 10, TimeUnit.SECONDS);
                ongoingRequests.put(thisData.getRequestId(), new OngoingRequestStatus(thisData.getRequestId(), false, persistOngoingRequestJob));
            });
        } else {
            logger.trace("db persist disabled, skipping...");
        }
    }

    @Override
    public void requestComplete(RequestData data) {
        try {
            RequestData thisData = processRequestData(data);
            logger.debug("request complete = {}", thisData);
            checkArgument(thisData.isCompleted());
            if (isDbPersistEnabled()) {
                scheduledExecutorService.submit(() -> {
                    try {
                        OngoingRequestStatus ongoingRequestStatus = ongoingRequests.getIfPresent(data.getRequestId());
                        if (ongoingRequestStatus == null) {
                            store.create(thisData);
                        } else {
                            if (ongoingRequestStatus.persistOngoingRequestJob != null) {
                                ongoingRequestStatus.persistOngoingRequestJob.cancel(true);
                            }
                            if (ongoingRequestStatus.alreadyPersisted) {
                                store.update(thisData);
                            } else {
                                store.create(thisData);
                            }
                            ongoingRequests.invalidate(data.getRequestId());
                        }
                    } catch (Exception ex) {
                        logger.error("error processing request completion, request = {}", thisData);
                        logger.error("error processing request completion", ex);
                    }
                });
            } else {
                logger.trace("db persist disabled, skipping...");
            }
        } catch (Exception ex) {
            throw runtime(ex);
        }
    }

    private boolean isDbPersistEnabled() {
        return (config.getMaxRecordsToKeep() == null || config.getMaxRecordsToKeep() != 0) && (config.getMaxRecordAgeToKeepSeconds() == null || config.getMaxRecordAgeToKeepSeconds() != 0) && systemService.isSystemReady();
    }

    private RequestData processRequestData(RequestData data) {
        if (data.hasPayload() && !data.hasSession()) {
            Matcher matcher = Pattern.compile("CMDBuild-Authorization[>]([^<]+)[<]/CMDBuild-Authorization").matcher(data.getPayload());
            if (matcher.find()) {
                RequestDataImpl.copyOf(data).withSessionId(matcher.group(1)).build();
            }
        }
        String username = NO_SESSION_USER;
        try {
            if (data.hasSession()) {
                username = sessionService.getSessionById(data.getSessionId()).getOperationUser().getLoginUser().getUsername();
            }
        } catch (Exception ex) {
            logger.debug("unable to retrieve username for session = " + data.getSessionId(), ex);
        }
        return RequestDataImpl.copyOf(data).withUser(username).withNodeId(clusterConfiguration.getClusterNodeId()).accept((b) -> {
            if (data.hasPayload()) {
                b.withPayload(trimPayload(data.getPayload()));
            }
            if (isNotBlank(data.getResponse())) {
                b.withResponse(trimPayload(data.getResponse()));
            }
        }).build();
    }

    private String trimPayload(String payload) {
        if (config.getMaxPayloadLength() > 0 && payload.length() > config.getMaxPayloadLength()) {
            String suffix = " PAYLOAD_TRIMMED_TO_" + config.getMaxPayloadLength() + "_BYTES";
            int maxLenMinusSuffix = config.getMaxPayloadLength() - suffix.length();
            return maxLenMinusSuffix > 0
                    ? (abbreviate(payload, maxLenMinusSuffix) + suffix)
                    : abbreviate(payload, config.getMaxPayloadLength());
        } else {
            return payload;
        }
    }

    private static class OngoingRequestStatus {

        public final String trackingId;
        public final boolean alreadyPersisted;
        public final ScheduledFuture persistOngoingRequestJob;

        public OngoingRequestStatus(String trackingId, boolean alreadyPersisted, @Nullable ScheduledFuture persistOngoingRequestJob) {
            this.trackingId = checkNotBlank(trackingId);
            this.alreadyPersisted = alreadyPersisted;
            this.persistOngoingRequestJob = persistOngoingRequestJob;
        }

    }

}
