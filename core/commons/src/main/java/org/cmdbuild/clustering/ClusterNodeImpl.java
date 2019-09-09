/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.clustering;

import org.cmdbuild.utils.lang.CmPreconditions;

public class ClusterNodeImpl implements ClusterNode {

    private final String address;
    private final String nodeId;
    private final boolean isThisNode;

    public ClusterNodeImpl(String nodeId, String address, boolean isThisNode) {
        this.address = CmPreconditions.checkNotBlank(address);
        this.nodeId = CmPreconditions.checkNotBlank(nodeId);
        this.isThisNode = isThisNode;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public String getNodeId() {
        return nodeId;
    }

    @Override
    public boolean isThisNode() {
        return isThisNode;
    }

    @Override
    public String toString() {
        return "ClusterNodeImpl{" + "address=" + address + ", nodeId=" + nodeId + ", thisNode=" + isThisNode + '}';
    }

}
