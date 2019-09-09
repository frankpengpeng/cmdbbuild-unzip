package org.cmdbuild.test.web.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import static com.google.common.base.MoreObjects.firstNonNull;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientLog {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final List<LogEntry> entries;

    public ClientLog(@Nullable List<LogEntry> entries) {
        this.entries = firstNonNull(entries, new ArrayList<>());
    }

    public ClientLog(@Nullable LogEntries entries) {
        if (entries == null) {
            this.entries = new ArrayList<>(null);
        } else {
            this.entries = new ArrayList<>(entries.getAll());
        }
    }

    public ClientLog(WebDriver driver) {
        this(driver, LogType.CLIENT);
    }

    public ClientLog(WebDriver driver, String logType) {
        this.entries = driver.manage().logs().get(logType).getAll();
    }

    public ClientLog supress(List<String> keysToSuppress) {
        return fromEntries(entries.stream().filter(le -> keysToSuppress.stream().noneMatch(le.toString()::contains))
                .collect(Collectors.toList()));
    }

    public ClientLog supress(String... keysToSuppress) {
        return supress(Arrays.asList(keysToSuppress));
    }

    public ClientLog keep(List<String> keysToKeep) {
        if (keysToKeep == null || keysToKeep.isEmpty()) {
            return fromEntries(entries);
        } else {
            return fromEntries(entries.stream().filter(le -> keysToKeep.stream().anyMatch(le.toString()::contains))
                    .collect(Collectors.toList()));
            //use version below for debug
//			List<LogEntry> kept = new ArrayList<>();
//			for(LogEntry le : entries) {
//				for (String k2k : keysToKeep) {
//					if (le.toString().contains(k2k)) {
//						kept.add(le);
//						break;
//					}
//				}
//			}
//			return fromEntries(kept);
        }
    }

    public ClientLog minSeverity(Level minLevel) {
        return fromEntries(entries.stream().filter(le -> le.getLevel().intValue() >= minLevel.intValue()).collect(Collectors.toList()));
    }

    public boolean success() {
        return entries.isEmpty();
    }

    public boolean error() {
        return !success();
    }

    public int errors() {
        return entries.size();
    }

//    public ClientLog printLogs(@Nullable Logger logger, @Nullable String title) {
//        try {
//            Logger actualLogger = firstNonNull(logger, this.logger);
//            title = firstNonNull(title, "Reporting Browser logs: ");
//            actualLogger.info(title);
//            entries.forEach(le -> actualLogger.warn("@: {} Severity: {} Message: {}", le.getTimestamp(), le.getLevel().getName(), le.getMessage()));
//            return this;
//        } catch (Exception e) {
//            this.logger.error("PrintLogs failed because of: {}", e.toString());
//            return this;
//        }
//
//    }

    public List<LogEntry> getEntries() {
        return entries;
    }

    public String getErrorsForLogs() {
        return entries.stream().map(e -> format("%s: %s", e.getLevel(), e.getMessage())).collect((joining(", ")));
    }

    //Factory
    public static ClientLog fromEntries(@Nullable List<LogEntry> entries) {
        return new ClientLog(entries);
    }

    public static ClientLog fromLogEntries(@Nullable LogEntries entries) {
        return new ClientLog(entries);
    }

    public static ClientLog fetchFromWebDriver(WebDriver driver) {
        return fetchFromWebDriver(driver, LogType.BROWSER);
    }

    public static ClientLog fetchFromWebDriver(WebDriver driver, String logType) {
        return fromEntries(driver.manage().logs().get(logType).getAll());
    }

}
