/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.clustering;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.collect.Ordering;
import com.google.common.eventbus.EventBus;
import java.util.List;
import static org.cmdbuild.utils.hash.CmHashUtils.toIntHash;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface ClusterService {

    void sendMessage(ClusterMessage clusterMessage);

    /**
     * an eventbus to listen to {@link ClusterMessageReceivedEvent} events 
     */
    EventBus getEventBus();

    boolean isRunning();

    List<ClusterNode> getClusterNodes();

    default boolean isSingleActiveNode() {
        return !isRunning() || getClusterNodes().size() <= 1;
    }

    default ClusterNode selectSingleNodeForKey(String key) {
        checkNotBlank(key);
        checkArgument(isRunning());
        List<ClusterNode> clusterNodes = Ordering.natural().onResultOf(ClusterNode::getNodeId).sortedCopy(getClusterNodes());
        int i = toIntHash(key) % clusterNodes.size();
        return clusterNodes.get(i);
    }

}
