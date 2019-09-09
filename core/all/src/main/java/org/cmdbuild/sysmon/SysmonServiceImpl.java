/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.sysmon;

import com.google.common.base.Joiner;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import static java.lang.Math.toIntExact;
import static java.lang.String.format;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.nio.charset.Charset;
import java.util.List;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.cluster.NodeIdProvider;
import static org.cmdbuild.common.error.ErrorAndWarningCollectorService.marker;
import org.cmdbuild.scheduler.ScheduledJob;
import org.cmdbuild.sysmon.SystemStatusLogImpl.SystemStatusRecordImplBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.config.api.DirectoryService;
import org.cmdbuild.debuginfo.BuildInfoService;
import org.cmdbuild.services.PostStartup;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@Component
public class SysmonServiceImpl implements SysmonService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final SysmonRepository sysmonRepository;
    private final DirectoryService directoryService;
    private final NodeIdProvider nodeIdProvider;
    private final SessionService sessionService;
    private final BuildInfoService buildInfoService;

    public SysmonServiceImpl(SysmonRepository sysmonRepository, DirectoryService directoryService, NodeIdProvider nodeIdProvider, SessionService sessionService, BuildInfoService buildInfoService) {
        this.sysmonRepository = checkNotNull(sysmonRepository);
        this.directoryService = checkNotNull(directoryService);
        this.nodeIdProvider = checkNotNull(nodeIdProvider);
        this.sessionService = checkNotNull(sessionService);
        this.buildInfoService = checkNotNull(buildInfoService);
    }

    @ScheduledJob("0 * * * * ?")//run every minute; TODO configure frequency
    public void logSystemStatus() {
        sysmonRepository.store(getSystemRuntimeStatus());
    }

    @Override
    public SystemStatusLog getSystemRuntimeStatus() {
        return new SystemStatusChecker().gatherSystemStatus();
    }

    @PostStartup
    public void logSystemInfo() {
        logger.info("default charset = {}", Charset.defaultCharset().name());
    }

    private class SystemStatusChecker {

        private final SystemStatusRecordImplBuilder builder = SystemStatusLogImpl.builder();
        private final List<String> warnings = list();

        public SystemStatusLog gatherSystemStatus() {
            checkJavaMem();
            checkOsMemAndCpu();
            checkFilesystem();
            checkSessions();
            attachWarnings();
            return builder.build();
        }

        private void checkJavaMem() {
            int javaMemAvailable = toIntExact((Runtime.getRuntime().maxMemory() / 1000 / 1000)),
                    javaMemUsed = toIntExact(((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000 / 1000));

            logger.debug("java memory used = {}/{} MB", javaMemUsed, javaMemAvailable);

            builder.withJavaMemoryUsed(javaMemUsed)
                    .withJavaMemoryAvailable(javaMemAvailable);

            int javaPid;
            String hostname;

            {
                javaPid = Integer.valueOf(ManagementFactory.getRuntimeMXBean().getName().replaceAll("^([0-9]+)@.*", "$1"));//TODO for java 9 this can be replaced with something better
                hostname = ManagementFactory.getRuntimeMXBean().getName().replaceAll("^[0-9]+@(.*)", "$1");
            }

            builder.withJavaPid(javaPid)
                    .withHostname(hostname)
                    .withNodeId(nodeIdProvider.getClusterNodeId());

            if (buildInfoService.hasBuildInfo()) {
                builder.withBuildInfo(buildInfoService.getBuildInfo().getCommitInfo());
            }

            int percUsed = javaMemUsed * 100 / javaMemAvailable;
            if (percUsed > 85) {
                addWarning("WARNING: java memory almost exausted, %s%% used (%s/%s MB)", percUsed, javaMemUsed, javaMemAvailable);
            }
        }

        private void checkOsMemAndCpu() {

            Integer systemMemAvailable = null, systemMemFree, systemMemUsed = null;
            try {
                File file = new File("/proc/meminfo");
                if (file.canRead()) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        systemMemAvailable = toIntExact((Long.valueOf(reader.readLine().replaceFirst("MemTotal:[^0-9]+([0-9]+).*", "$1")) / 1000));
                        reader.readLine();
                        systemMemFree = toIntExact((Long.valueOf(reader.readLine().replaceFirst("MemAvailable:[^0-9]+([0-9]+).*", "$1")) / 1000));
                    }
                    systemMemUsed = systemMemAvailable - systemMemFree;

                    int percUsed = systemMemUsed * 100 / systemMemAvailable;
                    if (percUsed > 85) {
                        addWarning("WARNING: system memory almost exausted, %s%% used (%s/%s GB)", percUsed, systemMemUsed / 1000d, systemMemAvailable / 1000d);
                    }
                }
            } catch (Exception ex) {
                logger.debug("error reading system memory info", ex);
            }

            logger.debug("system memory used = {}/{} MB", systemMemUsed, systemMemAvailable);

            OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();

            double systemLoadAvg = operatingSystemMXBean.getSystemLoadAverage();
            int procNum = operatingSystemMXBean.getAvailableProcessors();

            logger.debug("system load avg = {} (on {} cpu)", systemLoadAvg, procNum);

            builder.withSystemMemoryAvailable(systemMemAvailable)
                    .withSystemMemoryUsed(systemMemUsed)
                    .withLoadAvg(systemLoadAvg);

            double loadAvgPerCpu = systemLoadAvg / procNum;
            if (loadAvgPerCpu > 1) {
                addWarning("WARNING: high cpu load, loadavg = %s, loadavg/cpu = %s", systemLoadAvg, loadAvgPerCpu);
            } else if (loadAvgPerCpu > 0.5 || systemLoadAvg > 1) {
                logger.debug("moderate cpu load detected, loadavg = {}, loadavg/cpu = {}", systemLoadAvg, loadAvgPerCpu);
            }
        }

        private void checkFilesystem() { //TODO check all filesystems ??
            Integer filesystemMemTotal, filesystemMemFree, filesystemMemUsed;
            int percUsed;

            if (directoryService.hasContainerDirectory()) {
                filesystemMemTotal = toIntExact((directoryService.getContainerDirectory().getTotalSpace() / 1000 / 1000));
                filesystemMemFree = toIntExact((directoryService.getContainerDirectory().getUsableSpace() / 1000 / 1000));
                filesystemMemUsed = filesystemMemTotal - filesystemMemFree;
                logger.debug("filesystem usage = {}/{} GB", filesystemMemUsed / 1000d, filesystemMemTotal / 1000d);
                percUsed = filesystemMemUsed * 100 / filesystemMemTotal;
                if (percUsed > 95) {
                    addWarning("CRITICAL: filesystem space almost exausted on %s, %s%% used (%s/%s GB)", directoryService.getWebappDirectory(), percUsed, filesystemMemUsed / 1000d, filesystemMemTotal / 1000d);
                } else if (percUsed > 85) {
                    addWarning("WARNING: filesystem space almost exausted on %s, %s%% used (%s/%s GB)", directoryService.getWebappDirectory(), percUsed, filesystemMemUsed / 1000d, filesystemMemTotal / 1000d);
                }
            } else {
                percUsed = filesystemMemFree = filesystemMemTotal = filesystemMemUsed = 0;
            }

            builder.withFilesystemMemoryAvailable(filesystemMemTotal);
            builder.withFilesystemMemoryUsed(filesystemMemUsed);

        }

        private void checkSessions() {

            int activeSessions = sessionService.getActiveSessionCount();

            logger.debug("active session count = {}", activeSessions);

            builder.withActiveSessionCount(activeSessions);
        }

        private void attachWarnings() {
            if (!warnings.isEmpty()) {
                builder.withWarnings(Joiner.on("; ").join(warnings));
            }
        }

        private void addWarning(String format, Object... args) {
            String message = format(format, args);
            logger.warn(marker(), message);
            warnings.add(message);

        }
    }

}
