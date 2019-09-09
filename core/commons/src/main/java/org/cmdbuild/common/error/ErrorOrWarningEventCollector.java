/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.common.error;

import com.google.common.base.Joiner;
import static com.google.common.base.Predicates.equalTo;
import static java.util.Collections.emptyList;
import java.util.List;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.common.error.ErrorOrWarningEvent.ErrorEventLevel.ERROR;

/**
 * an object that collect error or warning events that may happen in a request
 * and should be reported to user
 *
 * @author davide
 */
public interface ErrorOrWarningEventCollector {

    void enableFullLogCollection();

    boolean isFullLogCollectionEnabled();

    void addEvent(ErrorOrWarningEvent event);

    void addLogs(String logs);

    List<ErrorOrWarningEvent> getCollectedEvents();

    String getLogs();

    default void addInfo(Exception exception) {
        addEvent(new ErrorOrWarningEventImpl(null, ErrorOrWarningEvent.ErrorEventLevel.INFO, exception));
    }

    default void addWarning(Exception exception) {
        addEvent(new ErrorOrWarningEventImpl(null, ErrorOrWarningEvent.ErrorEventLevel.WARNING, exception));
    }

    default void addError(Exception exception) {
        addEvent(new ErrorOrWarningEventImpl(null, ErrorOrWarningEvent.ErrorEventLevel.ERROR, exception));
    }

    default ErrorOrWarningEventCollector withError(Exception exception) {
        addError(exception);
        return this;
    }

    default boolean hasEvents() {
        return !getCollectedEvents().isEmpty();
    }

    default void addEventsFrom(ErrorOrWarningEventCollector inner) {
        inner.getCollectedEvents().forEach(this::addEvent);
    }

    /**
     * return an aggregated user-readable message built from all collected
     * events
     *
     * @return
     */
    default String getMessage() {
        return Joiner.on("; ").join(getCollectedEvents().stream().map((event) -> event.getLevel().name() + ": " + event.getMessage()).collect(toList()));
    }

    static ErrorOrWarningEventCollector dummyErrorOrWarningEventCollector() {
        return new ErrorOrWarningEventCollector() {
            @Override
            public void addEvent(ErrorOrWarningEvent event) {
                //quietly ignore
            }

            @Override
            public List<ErrorOrWarningEvent> getCollectedEvents() {
                return emptyList();
            }

            @Override
            public void enableFullLogCollection() {
                //do nothing
            }

            @Override
            public boolean isFullLogCollectionEnabled() {
                return false;
            }

            @Override
            public void addLogs(String logs) {
                //do nothing
            }

            @Override
            public String getLogs() {
                return "";
            }

        };
    }

    default boolean hasErrors() {
        return hasEvents() && getCollectedEvents().stream().map(ErrorOrWarningEvent::getLevel).anyMatch(equalTo(ERROR));
    }

    default void copyErrorsFrom(ErrorOrWarningEventCollector otherCollector) {
        if (otherCollector.isFullLogCollectionEnabled()) {
            this.enableFullLogCollection();
            this.addLogs(otherCollector.getLogs());
        }
        otherCollector.getCollectedEvents().forEach(this::addEvent);
    }

}
