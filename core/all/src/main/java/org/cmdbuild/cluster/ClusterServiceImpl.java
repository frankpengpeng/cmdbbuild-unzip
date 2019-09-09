package org.cmdbuild.cluster;

import org.cmdbuild.clustering.ClusterNodeImpl;
import com.google.common.base.Joiner;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ComparisonChain;
import com.google.common.eventbus.EventBus;
import java.io.ByteArrayInputStream;
import static java.util.Collections.emptyList;
import static java.lang.String.format;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import org.cmdbuild.clustering.ClusterMessage;
import static org.cmdbuild.clustering.ClusterMessage.THIS_INSTANCE_ID;
import org.cmdbuild.clustering.ClusterMessageImpl;
import org.cmdbuild.clustering.ClusterMessageReceivedEvent;
import org.cmdbuild.clustering.ClusterNode;
import static org.cmdbuild.common.error.ErrorAndWarningCollectorService.marker;
import org.cmdbuild.config.api.ConfigListener;
import org.cmdbuild.services.PostStartup;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.Message.Flag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.services.MinionStatus;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.jgroups.Address;
import org.jgroups.Event;
import org.jgroups.PhysicalAddress;
import org.jgroups.Receiver;
import org.jgroups.View;
import org.jgroups.logging.CustomLogFactory;
import org.jgroups.logging.Log;
import org.jgroups.logging.LogFactory;
import org.jgroups.logging.Slf4jLogImpl;
import org.cmdbuild.services.PreShutdown;
import org.cmdbuild.services.MinionComponent;
import static org.cmdbuild.services.MinionStatus.MS_READY;
import static org.cmdbuild.services.MinionStatus.MS_DISABLED;
import static org.cmdbuild.services.MinionStatus.MS_NOTRUNNING;
import org.cmdbuild.clustering.ClusterService;
import static org.cmdbuild.utils.date.CmDateUtils.systemZoneOffset;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_OBJECTS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;

@Component
@MinionComponent(name = "Clustering", configBean = ClusterConfiguration.class)
public class ClusterServiceImpl implements ClusterService {

    private static final String TIMESTAMP_ATTR = "timestamp",
            SOURCE_ID_ATTR = "sourceId",
            MESSAGE_TYPE_ATTR = "type",
            MESSAGE_ID_ATTR = "messageId",
            DATA_ATTR = "data";
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final EventBus eventBus = new EventBus();

    private final ClusterConfiguration clusterConfig;

    private JChannel channel;

    public ClusterServiceImpl(ClusterConfiguration clusterConfig) {
        this.clusterConfig = checkNotNull(clusterConfig);
    }

    public MinionStatus getServiceStatus() {
        if (!isEnabled()) {
            return MS_DISABLED;
        } else if (isRunning()) {
            return MS_READY;
        } else {
            return MS_NOTRUNNING;
        }
    }

    @Override
    public List<ClusterNode> getClusterNodes() {
        checkArgument(isEnabled(), "clustering is not enabled");
        try {
            View view = channel.getView();
            return getClusterNodes(view);
        } catch (Exception ex) {
            logger.error(marker(), "error retrieving cluster members", ex);
            return emptyList();//TODO return this node
        }
    }

    @PostStartup
    public synchronized void startIfEnabled() {
        if (isEnabled()) {
            start();
        } else {
            logger.info("clustering is disabled");
        }
    }

    @PreShutdown
    public synchronized void stopSafe() {
        if (channel != null) {
            logger.info("stopping clustering service");
            try {
                channel.close();
            } catch (Exception ex) {
                logger.warn("error closing channel", ex);
            } finally {
                channel = null;
            }
            logger.info("clustering service stopped");
        }
    }

    @ConfigListener(ClusterConfiguration.class)
    public void reload() {
        stopSafe();
        startIfEnabled();
    }

    @Override
    public boolean isRunning() {
        return channel != null && !channel.isClosed();
    }

    @Override
    public void sendMessage(ClusterMessage clusterMessage) {
        if (isRunning()) {
            try {
                doSendMessage(clusterMessage);
            } catch (Exception ex) {
                logger.warn(marker(), "error sending cluster message", ex);
            }
        }
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    private boolean isEnabled() {
        return clusterConfig.isClusterEnabled();
    }

    private List<ClusterNode> getClusterNodes(View view) {
        Address thisAddress = channel.getAddress();
        return view.getMembers().stream().map((n) -> {
            String nodeId = n.toString();
            String address = getPhisicalAddressSafe(n);
            return new ClusterNodeImpl(nodeId, address, equal(n, thisAddress));
        }).sorted((a, b) -> ComparisonChain.start().compareTrueFirst(a.isThisNode(), b.isThisNode()).compare(a.getNodeId(), b.getNodeId()).result()).collect(toList());
    }

    private String getPhisicalAddressSafe(Address address) {
        try {
            PhysicalAddress physicalAddress = (PhysicalAddress) channel.down(new Event(Event.GET_PHYSICAL_ADDRESS, address));
            return physicalAddress.printIpAddress();
        } catch (Exception ex) {
            logger.debug("error retrieving phisical addres from addr = {}", address, ex);
            return "<unknown address>";
        }
    }

    private synchronized void start() {
        try {
            startUnsafe();
        } catch (Exception ex) {
            logger.error(marker(), "unable to start clustering service", ex);
        }

    }

    private synchronized void startUnsafe() throws Exception {
        stopSafe();
        logger.info("starting clustering service");
        logger.debug("create new jgroups channel");
        try {
            LogFactory.setCustomLogFactory(new CustomLogFactory() {
                @Override
                public Log getLog(Class clazz) {
                    return getLog(clazz.getName());
                }

                @Override
                public Log getLog(String category) {
                    return new Slf4jLogImpl(format("org.cmdbuild.clustering.jgroups.%s", category));
                }
            });
            String config = readToString(getClass().getResourceAsStream("/org/cmdbuild/clustering/jgroups/jgroups.xml"));
            config = config.replaceAll(Pattern.quote("${tcp.port}"), String.valueOf(clusterConfig.getTcpPort())); //TODO use some template engine
            config = config.replaceAll(Pattern.quote("${tcpping.initial_hosts}"), Joiner.on(",").skipNulls().join(clusterConfig.getClusterNodes()));//TODO use some template engine
            logger.debug("jgroups config = \n\n{}\n", config);
            channel = new JChannel(new ByteArrayInputStream(config.getBytes()));
            channel.setName(clusterConfig.getClusterNodeId());
            channel.setDiscardOwnMessages(true);
            channel.setReceiver(new MyReceiver());
            channel.connect(clusterConfig.getClusterName());
            logger.debug("jgroups channel configuring");
        } catch (Exception ex) {
            stopSafe();
            throw ex;
        }
    }

    private void doSendMessage(ClusterMessage clusterMessage) throws Exception {
        checkNotNull(channel, "jgroups channel is null (jgroups startup failed)");
        logger.debug("send cluster message = {}", clusterMessage);
        checkArgument(equal(clusterMessage.getSourceInstanceId(), THIS_INSTANCE_ID));

        String payload = toJson(map(
                SOURCE_ID_ATTR, clusterConfig.getClusterNodeId(),
                TIMESTAMP_ATTR, now().toInstant().toEpochMilli(),
                MESSAGE_ID_ATTR, clusterMessage.getMessageId(),
                MESSAGE_TYPE_ATTR, clusterMessage.getMessageType(),
                DATA_ATTR, map(clusterMessage.getMessageData())));

        Message message = new Message(null, payload.getBytes(StandardCharsets.UTF_8));
        if (clusterMessage.requireRsvp()) {
            message.setFlag(Flag.RSVP);
        }
        logger.trace("send jgroups message = {} with payload = {}", message, payload);
        channel.send(message);
    }

    private class MyReceiver implements Receiver {

        @Override
        public void receive(Message message) {
            try {
                logger.trace("received jgroups message = {}", message);
                String payload = new String(message.getBuffer(), StandardCharsets.UTF_8);
                logger.trace("jgroups message payload = {}", payload);
                Map<String, Object> map = fromJson(payload, MAP_OF_OBJECTS);
                ClusterMessage clusterMessage = ClusterMessageImpl.builder()
                        .withTimestamp(Instant.ofEpochMilli(toLong(map.get(TIMESTAMP_ATTR))).atZone(systemZoneOffset()))
                        .withSourceInstanceId((String) map.get(SOURCE_ID_ATTR))
                        .withMessageType((String) map.get(MESSAGE_TYPE_ATTR))
                        .withMessageId((String) map.get(MESSAGE_ID_ATTR))
                        .withMessageData((Map<String, Object>) map.get(DATA_ATTR))
                        .build();
                logger.debug("received cluster message = {}", clusterMessage);
                checkArgument(!equal(clusterMessage.getSourceInstanceId(), clusterConfig.getClusterNodeId()), "received cluster message with source node id = this node id");
                eventBus.post(new ClusterMessageReceivedEventImpl(clusterMessage));
            } catch (Exception ex) {
                logger.error("error processing jgroups message = {}", message, ex);
            }
        }

        @Override
        public void viewAccepted(View view) {
            try {
                logger.info("new cluster view received = \n\n{}\n", getClusterNodes(view).stream().map(n -> format("\t%s  %-10s    %s", n.isThisNode() ? "(this node)" : "           ", n.getNodeId(), n.getAddress())).collect(joining("\n")));
            } catch (Exception ex) {
                logger.error("error processing jgroups view = {}", view, ex);
            }
        }

    }

    private class ClusterMessageReceivedEventImpl implements ClusterMessageReceivedEvent {

        final ClusterMessage clusterMessage;

        public ClusterMessageReceivedEventImpl(ClusterMessage clusterMessage) {
            this.clusterMessage = checkNotNull(clusterMessage);
        }

        @Override
        public ClusterMessage getClusterMessage() {
            return clusterMessage;
        }

    }

}
