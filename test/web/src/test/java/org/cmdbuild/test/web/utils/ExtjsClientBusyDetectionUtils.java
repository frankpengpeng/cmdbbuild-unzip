package org.cmdbuild.test.web.utils;

import org.openqa.selenium.JavascriptExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import static org.cmdbuild.utils.lang.CmExecutorUtils.sleepSafe;
  
@Deprecated
public class ExtjsClientBusyDetectionUtils {

    private final static Logger LOGGER = LoggerFactory.getLogger(ExtjsClientBusyDetectionUtils.class);

    private static final long clientExecutionTimeLoggingThreshold = 25;

    private static final long defaultTimeout = 10000;
    private static final long defaultWaitTimeBetweenChecks = 20;
    private static final long defaultMaxWaitBeforeBusy = 1200;
    private static final long defaultMaxWaitAfterBusy = 1200;
    private static final long defaultClientScriptExecutionTimeThresholdTriggeringClientBusy = 250; //very sensitive parameter,
    // perhaps needs to be set higher when running on a slow, virtualized environment (Jenkins...)
    private static final boolean defaultRaiseRuntimeExceptionIfNeverBusy = false;

//    public static boolean safeWaitUntilClientIsReady() {
//
//        try {
//            // waitUntilClientIsReady();
//            return true;
//        } catch (WebUICMDBuidTestException e) {
//            LOGGER.warn("waitUntilClientIsReady() raised an exception: {}", e.toString());
//            return false;
//        }
//    }
    @Nullable
    public static Boolean waitUntilClientIsReady(JavascriptExecutor jsExecutor, Optional<ArtificialTestDelayListener> artificialDelayListener, @Nullable DetectionStrategy strategy) {

        return waitUntilClientIsReady(jsExecutor, artificialDelayListener,
                strategy.timeout, strategy.maxWaitBeforeBusy, strategy.maxWaitAfterBusy, strategy.waitTimeBetweenChecks,
                strategy.clientScriptExecutionTimeThresholdTriggeringClientBusy, strategy.raiseRuntimeExceptionIfNeverBusy);
    }

    @Nullable
    public static Boolean waitUntilClientIsReady(JavascriptExecutor jsExecutor) {
        DetectionStrategy strategy = DetectionStrategy.defaultStrategy();
        return waitUntilClientIsReady(jsExecutor, Optional.empty(),
                strategy.timeout, strategy.maxWaitBeforeBusy, strategy.maxWaitAfterBusy, strategy.waitTimeBetweenChecks,
                strategy.clientScriptExecutionTimeThresholdTriggeringClientBusy, strategy.raiseRuntimeExceptionIfNeverBusy);
    }

    @Nullable
    public static Boolean waitUntilClientIsReady(JavascriptExecutor jsExecutor, @Nullable ArtificialTestDelayListener listener, @Nullable DetectionStrategy strategy) {
        if (strategy == null) {
            strategy = DetectionStrategy.defaultStrategy();
        }

        return waitUntilClientIsReady(jsExecutor, Optional.ofNullable(listener),
                strategy.timeout, strategy.maxWaitBeforeBusy, strategy.maxWaitAfterBusy, strategy.waitTimeBetweenChecks,
                strategy.clientScriptExecutionTimeThresholdTriggeringClientBusy, strategy.raiseRuntimeExceptionIfNeverBusy);
    }

    //returns Boolean.True if at least one ajax request was triggered
    //null if no client request was detected but at least one time client was considered busy because script execution took long
    //false if no request nor client busy were detected
    @Nullable
    public static Boolean waitUntilClientIsReady(JavascriptExecutor jsExecutor,
            Optional<ArtificialTestDelayListener> artificialDelayListener,
            @Nullable Long timeout,
            @Nullable Long maxWaitBeforeBusy,
            @Nullable Long maxWaitAfterBusy,
            @Nullable Long waitTimeBetweenChecks,
            @Nullable Long clientScriptExecutionTimeThresholdTriggeringClientBusy,
            @Nullable Boolean raiseRuntimeExceptionIfNeverBusy//, raise exception if never triggered busy
    ) {

        //setup defaults
        if (timeout == null) {
            timeout = defaultTimeout;
        }
        if (maxWaitAfterBusy == null) {
            maxWaitAfterBusy = defaultMaxWaitAfterBusy;
        }
        if (maxWaitBeforeBusy == null) {
            maxWaitBeforeBusy = defaultMaxWaitBeforeBusy;
        }
        if (waitTimeBetweenChecks == null) {
            waitTimeBetweenChecks = defaultWaitTimeBetweenChecks;
        }
        if (clientScriptExecutionTimeThresholdTriggeringClientBusy == null) {
            clientScriptExecutionTimeThresholdTriggeringClientBusy = defaultClientScriptExecutionTimeThresholdTriggeringClientBusy;
        }
        if (raiseRuntimeExceptionIfNeverBusy == null) {
            raiseRuntimeExceptionIfNeverBusy = defaultRaiseRuntimeExceptionIfNeverBusy;
        }

        Instant start = Instant.now();
        Instant deadLine = start.plusMillis(maxWaitAfterBusy);
        Boolean triggered = false;
//        long artificialTime = 0;
        //BEFORE BUSY DETECTION
        do {
            Instant scriptStart = Instant.now();
            Number res = (Number) jsExecutor.executeScript("return CMDBuildUI.util.Ajax.currentPending();");
            long scriptExecutionTime = ChronoUnit.MILLIS.between(scriptStart, Instant.now());
            if (res.intValue() > 0) {
                triggered = Boolean.TRUE;
                LOGGER.info("Detected client busy with {} ajax requests pending", res.intValue());
            } else {
                if (scriptExecutionTime > clientScriptExecutionTimeThresholdTriggeringClientBusy) {
                    triggered = null;
                    LOGGER.info("Client allegedly busy because it took {} ms to execute busy detection script", scriptExecutionTime);
                }
            }
            if (res.intValue() > 0 || scriptExecutionTime > clientExecutionTimeLoggingThreshold) {
                LOGGER.info("currentPendingCount: {}  Client script took {} ms", res.intValue(), scriptExecutionTime);//demote to debug when finished with developement
            }
            sleepSafe(res.intValue() == 0 ? waitTimeBetweenChecks : 3 * waitTimeBetweenChecks);

        } while (Boolean.FALSE.equals(triggered) && Instant.now().isBefore(deadLine));
        
        if (Boolean.FALSE.equals(triggered)) {
            if (artificialDelayListener.isPresent()) {
                artificialDelayListener.get().notifyArtificialDelayTime(ChronoUnit.MILLIS.between(start, Instant.now()));
            }
            return false;
        }

        //AFTER BUSY DETECTION
        deadLine = Instant.now().plusMillis(maxWaitAfterBusy);
        Instant timeoutInstant = start.plusMillis(timeout);
        Instant lastTriggered = Instant.now();
        LOGGER.info("After busy client detection started...");
        do {
            Instant scriptStart = Instant.now();
            Number res = (Number) jsExecutor.executeScript("return CMDBuildUI.util.Ajax.currentPending();");
            long scriptExecutionTime = ChronoUnit.MILLIS.between(scriptStart, Instant.now());
            if (res.intValue() > 0) {
                lastTriggered = Instant.now();
                triggered = Boolean.TRUE;
                deadLine = deadLine.plusMillis(maxWaitAfterBusy);
                LOGGER.info("Detected client busy with {} ajax requests pending", res.intValue());
            } else {
                if (scriptExecutionTime > clientScriptExecutionTimeThresholdTriggeringClientBusy) {
                    lastTriggered = Instant.now();
                    deadLine = deadLine.plusMillis(maxWaitAfterBusy);
                    if (Boolean.FALSE.equals(triggered)) {
                        triggered = null;
                    }
                    LOGGER.info("Client allegedly busy because it took {} ms to execute busy detection script", scriptExecutionTime);
                }
            }

            sleepSafe(res.intValue() == 0 ? waitTimeBetweenChecks : 3 * waitTimeBetweenChecks);

        } while (Instant.now().isBefore(deadLine) && Instant.now().isBefore(timeoutInstant));

        if (artificialDelayListener.isPresent()) {
            artificialDelayListener.get().notifyArtificialDelayTime(ChronoUnit.MILLIS.between(lastTriggered, Instant.now()));
        }

        if (Boolean.TRUE.equals(raiseRuntimeExceptionIfNeverBusy)) {
            throw new RuntimeException("WaitUntilClientIsReady did never detected client busy");
        }

        return triggered;
    }

    public static class DetectionStrategy {

        //all times in milliseconds
        public long waitTimeBetweenChecks = defaultWaitTimeBetweenChecks;
        public long timeout = defaultTimeout;
        public long maxWaitBeforeBusy = defaultMaxWaitBeforeBusy;
        public long maxWaitAfterBusy = defaultMaxWaitAfterBusy;
        public long clientScriptExecutionTimeThresholdTriggeringClientBusy = defaultClientScriptExecutionTimeThresholdTriggeringClientBusy; //very dangerous and execution context dependent
        public boolean raiseRuntimeExceptionIfNeverBusy = defaultRaiseRuntimeExceptionIfNeverBusy;

        private DetectionStrategy() {
        }

        public Boolean waitUntilClientIsReady(JavascriptExecutor jsExecutorOrDriver, @Nullable ArtificialTestDelayListener artificialDelayListener) {
            return ExtjsClientBusyDetectionUtils.waitUntilClientIsReady(jsExecutorOrDriver, Optional.ofNullable(artificialDelayListener), this);
        }

        public DetectionStrategy withTimeout(long timeout) {
            this.timeout = timeout;
            return this;
        }

        public DetectionStrategy withWaitTimeBetweenChecks(long waitTimeBetweenChecks) {
            this.waitTimeBetweenChecks = waitTimeBetweenChecks;
            return this;
        }

        public DetectionStrategy withMaxWaitBeforeBusy(long maxWaitBeforeBusy) {
            this.maxWaitBeforeBusy = maxWaitBeforeBusy;
            return this;
        }

        public DetectionStrategy withMaxWaitAfterBusy(long maxWaitAfterBusy) {
            this.maxWaitAfterBusy = maxWaitAfterBusy;
            return this;
        }

        public DetectionStrategy withClientScriptExecutionTimeThresholdTriggeringClientBusy(long clientScriptExecutionTimeThresholdTriggeringClientBusy) {
            this.clientScriptExecutionTimeThresholdTriggeringClientBusy = clientScriptExecutionTimeThresholdTriggeringClientBusy;
            return this;
        }

        public DetectionStrategy raiseRuntimeExceptionIfNeverBusy(boolean raiseRuntimeExceptionIfNeverBusy) {
            this.raiseRuntimeExceptionIfNeverBusy = raiseRuntimeExceptionIfNeverBusy;
            return this;
        }

        public static DetectionStrategy defaultStrategy() {
            return new ExtjsClientBusyDetectionUtils.DetectionStrategy();
        }

        public static DetectionStrategy longWaitStrategy() {
            return defaultStrategy().withMaxWaitAfterBusy(2000).withMaxWaitBeforeBusy(2000);
        }

        public static DetectionStrategy shortWaitStrategy() {
            return defaultStrategy().withMaxWaitAfterBusy(850).withMaxWaitBeforeBusy(1000);
        }

        public static DetectionStrategy shortBeforeBusyLongAfterBusy() {
            return defaultStrategy().withMaxWaitAfterBusy(2000).withMaxWaitBeforeBusy(850);
        }
    }

}
