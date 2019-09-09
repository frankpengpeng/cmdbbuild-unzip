/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.common.error;

import static com.google.common.collect.Lists.newArrayList;
import java.util.List;
import org.cmdbuild.requestcontext.RequestContextHolder;
import org.cmdbuild.requestcontext.RequestContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ErrorAndWarningCollectorServiceImpl implements ErrorAndWarningCollectorService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final RequestContextHolder<EventCollectionImpl> holder;

    public ErrorAndWarningCollectorServiceImpl(RequestContextService requestContextService) {
        holder = requestContextService.createRequestContextHolder(EventCollectionImpl::new);
    }

    @Override
    public ErrorOrWarningEventCollector getCurrentRequestEventCollector() {
        return holder.get();
    }

    private class EventCollectionImpl implements ErrorOrWarningEventCollector {

        private final StringBuilder logs = new StringBuilder();
        private final List<ErrorOrWarningEvent> collectedEvents = newArrayList();
        private boolean enableFullLogCollection = false;

        @Override
        public synchronized void addEvent(ErrorOrWarningEvent event) {
            logger.trace("collect error event = {}", event);
            this.collectedEvents.add(event);
        }

        @Override
        public List<ErrorOrWarningEvent> getCollectedEvents() {
            return collectedEvents;
        }

        @Override
        public void enableFullLogCollection() {
            enableFullLogCollection = true;
        }

        @Override
        public boolean isFullLogCollectionEnabled() {
            return enableFullLogCollection;
        }

        @Override
        public void addLogs(String logs) {
            this.logs.append(logs);
        }

        @Override
        public String getLogs() {
            return logs.toString();
        }

    }

}
