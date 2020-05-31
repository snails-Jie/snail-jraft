package zhangjie.raft.core;

import zhangjie.raft.core.entity.NodeId;

public interface Node extends Lifecycle{
    /**
     * Get current node id.
     */
    NodeId getNodeId();
}
