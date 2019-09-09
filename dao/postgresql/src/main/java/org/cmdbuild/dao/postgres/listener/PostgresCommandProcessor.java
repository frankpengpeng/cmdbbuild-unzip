/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.listener;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Ordering;
import com.google.common.eventbus.Subscribe;
import java.util.List;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.clustering.ClusterNode;
import org.cmdbuild.clustering.ClusterService;
import org.cmdbuild.config.api.GlobalConfigService;
import org.cmdbuild.platform.PlatformService;
import org.cmdbuild.syscommand.SysCommand;
import org.cmdbuild.syscommand.SysCommandBus;
import org.cmdbuild.syscommand.SysCommandImpl;
import static org.cmdbuild.utils.hash.CmHashUtils.toIntHash;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PostgresCommandProcessor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final GlobalConfigService configService;
    private final CacheService cacheService;
    private final PostgresNotificationService notificationService;
    private final PlatformService platformService;
    private final SysCommandBus sysCommandBus;
    private final ClusterService clusterService;

    public PostgresCommandProcessor(ClusterService clusterService, PlatformService platformService, GlobalConfigService configService, CacheService cacheService, PostgresNotificationEventService eventService, PostgresNotificationService notificationService, SysCommandBus sysCommandBus) {
        this.configService = checkNotNull(configService);
        this.cacheService = checkNotNull(cacheService);
        this.notificationService = checkNotNull(notificationService);
        this.platformService = checkNotNull(platformService);
        this.sysCommandBus = checkNotNull(sysCommandBus);
        this.clusterService = checkNotNull(clusterService);
        eventService.getEventBus().register(new Object() {

            @Subscribe
            public void handlePostgresNotificationEvent(PostgresNotificationEvent event) {
                new CommandProcessor(event).handleCommand();
            }

        });
    }

    private class CommandProcessor {

        private final PostgresNotificationEvent event;
        private String action;

        public CommandProcessor(PostgresNotificationEvent event) {
            this.event = checkNotNull(event);
        }

        public void handleCommand() {
            try {
                if (equal(event.getChannel(), "cmevents")) {
                    if (event.isCommand()) {
                        action = event.getAction();
                        doHandleCommand();
                    }
                }
            } catch (Exception ex) {
                logger.error("error processing postgres notification event = {}", event, ex);
                notifyError("error processing message: " + ex);
            }
        }

        private void doHandleCommand() {
            switch (action.toLowerCase()) {
                case "reload":
                    logger.info("system reload requested from postgres process");
                    configService.reload();
                    cacheService.invalidateAll();
                    notifySuccess("system reload completed");
                    break;
                case "restart":
                    logger.info("system restart requested from postgres process");
                    notifySuccess("system restart requested");
                    platformService.restartContainer();
                    break;
                case "shutdown":
                    logger.info("system shutdown requested from postgres process");
                    notifySuccess("system shutdown requested");
                    platformService.stopContainer();
                    break;
                case "test":
                    logger.info("received TEST command from postres process = {} via channel = {}; raw payload = {}", event.getServerPid(), event.getChannel(), abbreviate(event.getPayload()));
                    notifySuccess("test OK");
                    break;
                default:
                    sendSyscommandEvent(new SysCommandImpl(action, event.getPayloadAsMap()));
            }
        }

        private void notifySuccess(String message) {
            notificationService.sendInfo("CMDBuild %s command: %s", action, checkNotBlank(message));
        }

        private void notifyError(String message) {
            notificationService.sendInfo("CMDBUild command error: %s", checkNotBlank(message));
        }

        private void sendSyscommandEvent(SysCommand command) {
            if (command.runOnAllClusterNodes() || clusterService.isSingleActiveNode()) {
                sysCommandBus.postCommand(command);
            } else {
                ClusterNode selectedNode = clusterService.selectSingleNodeForKey(command.getId());
                logger.info("selected cluster node for command = {} node = {}", command, selectedNode);
                if (selectedNode.isThisNode()) {
                    sysCommandBus.postCommand(command);
                }
            }

        }
    }

}
